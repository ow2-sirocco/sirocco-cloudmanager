/**
 *
 * SIROCCO
 * Copyright (C) 2011 France Telecom
 * Contact: sirocco@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * $Id$
 *  
 */

package org.ow2.sirocco.cloudmanager.provider.vmm;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.ow2.sirocco.cloudmanager.clustermanager.api.AllocationMode;
import org.ow2.sirocco.cloudmanager.clustermanager.api.IClusterManager;
import org.ow2.sirocco.cloudmanager.clustermanager.api.VirtualMachineCreationSpec;
import org.ow2.sirocco.cloudmanager.clustermanager.api.VolumeSpec;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Job;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.entity.NetworkInterface;
import org.ow2.sirocco.cloudmanager.provider.api.entity.PerfMetric;
import org.ow2.sirocco.cloudmanager.provider.api.entity.PerfMetricInfo;
import org.ow2.sirocco.cloudmanager.provider.api.entity.PerfMetricInfo.Unit;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Volume;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.service.IComputeService;
import org.ow2.sirocco.cloudmanager.provider.api.service.IImageService;
import org.ow2.sirocco.cloudmanager.provider.api.service.IMonitoringService;
import org.ow2.sirocco.cloudmanager.provider.api.service.IPhysicalInfrastructureManagement;
import org.ow2.sirocco.cloudmanager.provider.api.service.IVolumeService;
import org.ow2.sirocco.cloudmanager.provider.api.service.ImageCreate;
import org.ow2.sirocco.cloudmanager.provider.api.service.ImageUpload;
import org.ow2.sirocco.cloudmanager.provider.api.service.MachineCreate;
import org.ow2.sirocco.cloudmanager.provider.api.service.VolumeAttachment;
import org.ow2.sirocco.cloudmanager.provider.api.service.VolumeCreate;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobManager;
import org.ow2.sirocco.cloudmanager.provider.util.vncproxy.api.VNCProxy;
import org.ow2.sirocco.vmm.api.CustomizationSpec;
import org.ow2.sirocco.vmm.api.GuestInfo;
import org.ow2.sirocco.vmm.api.ServerPoolMXBean;
import org.ow2.sirocco.vmm.api.VMMException;
import org.ow2.sirocco.vmm.api.VirtualDisk;
import org.ow2.sirocco.vmm.api.VirtualMachineMXBean;
import org.ow2.sirocco.vmm.api.VirtualMachineMXBean.PowerState;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class VMMComputeService implements IComputeService, IImageService, IVolumeService, IMonitoringService,
    IPhysicalInfrastructureManagement {
    private static Log logger = LogFactory.getLog(VMMComputeService.class);

    private static final int THREADPOOL_SIZE = 10;

    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors
        .newFixedThreadPool(VMMComputeService.THREADPOOL_SIZE));

    private final IClusterManager clusterManager;

    private final CloudProviderLocation cloudProviderLocation;

    private final JobManager jobManager;

    private final VNCProxy webSocketProxyManager;

    public VMMComputeService(final CloudProviderLocation cloudProviderLocation, final IClusterManager clusterManager,
        final JobManager jobManager, final VNCProxy webSocketProxyManager) throws CloudProviderException {
        this.cloudProviderLocation = cloudProviderLocation;
        this.clusterManager = clusterManager;
        this.jobManager = jobManager;
        this.webSocketProxyManager = webSocketProxyManager;
    }

    public void start() {
    }

    public void shutdown() {
        // TODO let submitted tasks complete ?
        this.executorService.shutdownNow();
    }

    @Override
    public Job<Machine> createMachine(final MachineCreate machineCreate) throws CloudProviderException {

        // Check machineTemplate
        if (machineCreate.getMachineTemplate() == null) {
            throw new CloudProviderException("The given machine template cannot be null");
        }

        // Check machineConfig.
        if (machineCreate.getMachineTemplate().getMachineConfig() == null) {
            throw new CloudProviderException("The given machineConfig cannot be null");
        }

        // Check vmImageConfig.
        if (machineCreate.getMachineTemplate().getMachineImage() == null) {
            throw new CloudProviderException("The given image cannot be null");
        }

        String hypervisorType = null;

        this.clusterManager.getResourceTree();
        Map<String, ServerPoolMXBean> poolMBeanTable = this.clusterManager.getPoolMBeanTable();
        for (ServerPoolMXBean serverPoolMXBean : poolMBeanTable.values()) {
            // We assume that all the VMM's serverPoolMXBean(s) have the same
            // hypervisor.
            hypervisorType = serverPoolMXBean.getHypervisor();
            break;
        }

        // Check hypervisorType value.
        if (hypervisorType == null) {
            throw new CloudProviderException("The given hypervisorType can NOT be: " + hypervisorType + ".");
        }

        final VirtualMachineCreationSpec creationSpec = new VirtualMachineCreationSpec();
        String vmName = machineCreate.getName();
        if (vmName == null) {
            vmName = "vm";
        }
        creationSpec.setName(vmName + "-" + UUID.randomUUID());
        creationSpec.setVmImageId(machineCreate.getMachineTemplate().getMachineImage().getProviderAssignedId());
        creationSpec.setNumVCPUs(machineCreate.getMachineTemplate().getMachineConfig().getNumCPUs());
        creationSpec.setMemorySizeMB((int) machineCreate.getMachineTemplate().getMachineConfig().getMemorySizeMB());
        creationSpec.setDiskCapacityMB((int) machineCreate.getMachineTemplate().getMachineConfig().getDiskSizeMB());

        CustomizationSpec customizationSpec = new CustomizationSpec();
        creationSpec.setCustomizationSpec(customizationSpec);
        Map<String, String> guestOSCustomizationParams = new HashMap<String, String>();
        // if (machineCreate.getOptions().getTags() != null) {
        // guestOSCustomizationParams.putAll(machineCreate.getOptions().getTags());
        // }
        if (machineCreate.getMachineTemplate().getMachineAdmin() != null) {
            guestOSCustomizationParams.put("sshkey", machineCreate.getMachineTemplate().getMachineAdmin().getPublicKey());
        }
        if (!guestOSCustomizationParams.isEmpty()) {
            customizationSpec.setGuestOsCustomizationParams(guestOSCustomizationParams);
        }
        customizationSpec.setGuestOsHostName(machineCreate.getMachineTemplate().getHostname());

        final Map<String, String> constraints = new LinkedHashMap<String, String>();
        constraints.put("hypervisor", hypervisorType);

        constraints.put("location", this.cloudProviderLocation.getLocationId());

        final AllocationMode allocationMode = AllocationMode.ON_DEMAND;

        final boolean startVM = false;

        final Callable<Machine> createMachineTask = new Callable<Machine>() {
            @Override
            public Machine call() throws Exception {
                String providerId = VMMComputeService.this.clusterManager.createVirtualMachine(creationSpec, constraints,
                    allocationMode, startVM);
                Machine result = new Machine();
                result.setProviderAssignedId(providerId);
                result.setState(VMMComputeService.this.getMachineState(providerId));
                result.setNetworkInterfaces(VMMComputeService.this.getMachineNetworkInterfaces(providerId));
                return result;
            }
        };
        ListenableFuture<Machine> result = this.executorService.submit(createMachineTask);
        return this.jobManager.newJob("", "machine.create", result);
    }

    @Override
    public Job<Machine.State> startMachine(final String machineId) throws CloudProviderException {
        final Callable<Machine.State> startMachineTask = new Callable<Machine.State>() {
            @Override
            public Machine.State call() throws Exception {
                VMMComputeService.this.clusterManager.startVirtualMachine(machineId);
                Machine.State status;
                int waitTimeInSeconds = 20;
                do {
                    status = VMMComputeService.this.getMachineState(machineId);
                    if (status == Machine.State.STARTED) {
                        break;
                    }
                    Thread.sleep(1000);
                } while (waitTimeInSeconds-- > 0);
                return status;
            }
        };
        ListenableFuture<Machine.State> result = this.executorService.submit(startMachineTask);
        return this.jobManager.newJob(machineId, "machine.start", result);
    }

    @Override
    public Job<Machine.State> stopMachine(final String machineId) throws CloudProviderException {
        final Callable<Machine.State> stopMachineTask = new Callable<Machine.State>() {
            @Override
            public Machine.State call() throws Exception {
                VMMComputeService.this.clusterManager.stopVirtualMachine(machineId);
                Machine.State status;
                int waitTimeInSeconds = 20;
                do {
                    status = VMMComputeService.this.getMachineState(machineId);
                    if (status == Machine.State.STOPPED) {
                        break;
                    }
                    Thread.sleep(1000);
                } while (waitTimeInSeconds-- > 0);
                return status;
            }
        };
        ListenableFuture<Machine.State> result = this.executorService.submit(stopMachineTask);
        return this.jobManager.newJob(machineId, "machine.stop", result);
    }

    @Override
    public Job<Machine.State> pauseMachine(final String machineId) throws CloudProviderException {
        final Callable<Machine.State> pauseMachineTask = new Callable<Machine.State>() {
            @Override
            public Machine.State call() throws Exception {
                VMMComputeService.this.clusterManager.pauseVirtualMachine(machineId);
                Machine.State status;
                int waitTimeInSeconds = 20;
                do {
                    status = VMMComputeService.this.getMachineState(machineId);
                    if (status == Machine.State.PAUSED) {
                        break;
                    }
                    Thread.sleep(1000);
                } while (waitTimeInSeconds-- > 0);
                return status;
            }
        };
        ListenableFuture<Machine.State> result = this.executorService.submit(pauseMachineTask);
        return this.jobManager.newJob(machineId, "machine.pause", result);
    }

    @Override
    public Job<Machine.State> unpauseMachine(final String machineId) throws CloudProviderException {
        final Callable<Machine.State> unpauseMachineTask = new Callable<Machine.State>() {
            @Override
            public Machine.State call() throws Exception {
                VMMComputeService.this.clusterManager.unpauseVirtualMachine(machineId);
                Machine.State status;
                int waitTimeInSeconds = 20;
                do {
                    status = VMMComputeService.this.getMachineState(machineId);
                    if (status == Machine.State.STARTED) {
                        break;
                    }
                    Thread.sleep(1000);
                } while (waitTimeInSeconds-- > 0);
                return status;
            }
        };
        ListenableFuture<Machine.State> result = this.executorService.submit(unpauseMachineTask);
        return this.jobManager.newJob(machineId, "machine.unpause", result);
    }

    @Override
    public Job<Machine.State> suspendMachine(final String machineId) throws CloudProviderException {
        final Callable<Machine.State> suspendMachineTask = new Callable<Machine.State>() {
            @Override
            public Machine.State call() throws Exception {
                VMMComputeService.this.clusterManager.suspendVirtualMachine(machineId);
                Machine.State status;
                int waitTimeInSeconds = 20;
                do {
                    status = VMMComputeService.this.getMachineState(machineId);
                    if (status == Machine.State.SUSPENDED) {
                        break;
                    }
                    Thread.sleep(1000);
                } while (waitTimeInSeconds-- > 0);
                return status;
            }
        };
        ListenableFuture<Machine.State> result = this.executorService.submit(suspendMachineTask);
        return this.jobManager.newJob(machineId, "machine.suspend", result);
    }

    @Override
    public Job<Machine.State> resumeMachine(final String machineId) throws CloudProviderException {
        final Callable<Machine.State> resumeMachineTask = new Callable<Machine.State>() {
            @Override
            public Machine.State call() throws Exception {
                VMMComputeService.this.clusterManager.resumeVirtualMachine(machineId);
                Machine.State status;
                int waitTimeInSeconds = 20;
                do {
                    status = VMMComputeService.this.getMachineState(machineId);
                    if (status == Machine.State.STARTED) {
                        break;
                    }
                    Thread.sleep(1000);
                } while (waitTimeInSeconds-- > 0);
                return status;
            }
        };
        ListenableFuture<Machine.State> result = this.executorService.submit(resumeMachineTask);
        return this.jobManager.newJob(machineId, "machine.resume", result);
    }

    @Override
    public Job<Void> rebootMachine(final String machineId) throws CloudProviderException {
        final Callable<Void> rebootMachineTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                VMMComputeService.this.clusterManager.rebootVirtualMachine(machineId);
                return null;
            }
        };
        ListenableFuture<Void> result = this.executorService.submit(rebootMachineTask);
        return this.jobManager.newJob(machineId, "machine.reboot", result);
    }

    @Override
    public Job<Void> destroyMachine(final String machineId) throws CloudProviderException {
        final Callable<Void> destroyMachineTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    String vncUrl = VMMComputeService.this.clusterManager.getVirtualMachineConsole(machineId);
                    URI vncURI = new URI(vncUrl);
                    String host = vncURI.getHost();
                    int port = vncURI.getPort();
                    VMMComputeService.this.webSocketProxyManager.destroyWebSocketProxy(host, port);
                } catch (Exception ex) {
                    // ignore
                }
                VMMComputeService.this.clusterManager.destroyVirtualMachine(machineId);
                return null;
            }
        };
        ListenableFuture<Void> result = this.executorService.submit(destroyMachineTask);
        return this.jobManager.newJob(machineId, "machine.destroy", result);
    }

    @Override
    public Machine.State getMachineState(final String machineId) throws CloudProviderException {
        try {
            PowerState powerState = this.clusterManager.getVirtualMachineMBean(machineId).getState();
            if (PowerState.HALTED.equals(powerState)) {
                return Machine.State.STOPPED;
            } else if (PowerState.PAUSED.equals(powerState)) {
                return Machine.State.PAUSED;
            } else if (PowerState.RUNNING.equals(powerState)) {
                return Machine.State.STARTED;
            } else if (PowerState.SUSPENDED.equals(powerState)) {
                return Machine.State.SUSPENDED;
            } else if (PowerState.UNKNOWN.equals(powerState)) {
                return Machine.State.ERROR;
            } else {
                throw new CloudProviderException("The current virtual machine's state: " + powerState
                    + " is NOT known by the system.");
            }
        } catch (VMMException e) {
            VMMComputeService.logger.error(e.getMessage(), (Object[]) e.getStackTrace());
            throw new CloudProviderException(e.getMessage());
        }
    }

    @Override
    public MachineConfiguration getMachineConfiguration(final String machineId) throws CloudProviderException {
        try {
            VirtualMachineMXBean virtualMachineMXBean = this.clusterManager.getVirtualMachineMBean(machineId);
            MachineConfiguration machineConfiguration = new MachineConfiguration();
            machineConfiguration.setNumCPUs(virtualMachineMXBean.getNumVCPUs());
            machineConfiguration.setMemorySizeMB(virtualMachineMXBean.getMemorySizeMB());
            long memoryInMB = virtualMachineMXBean.getMemorySizeMB();
            List<VirtualDisk> disks = virtualMachineMXBean.getVirtualDisks();
            if (!disks.isEmpty()) {
                machineConfiguration.setDiskSizeMB(disks.get(0).getVolume().getCapacityMB());
            }
            return machineConfiguration;
        } catch (VMMException e) {
            VMMComputeService.logger.error(e.getMessage(), (Object[]) e.getStackTrace());
            throw new CloudProviderException(e.getMessage());
        }
    }

    @Override
    public List<NetworkInterface> getMachineNetworkInterfaces(final String machineId) throws CloudProviderException {
        List<NetworkInterface> networkInterfaceInfos = new ArrayList<NetworkInterface>();
        VirtualMachineMXBean virtualMachineMXBean = this.clusterManager.getVirtualMachineMBean(machineId);
        GuestInfo guestInfo = null;

        try {
            guestInfo = virtualMachineMXBean.getGuestInfo();
        } catch (VMMException e) {
            throw new CloudProviderException(e.getMessage());
        }
        if (guestInfo != null && guestInfo.getIpAddresses() != null && guestInfo.getIpAddresses().size() > 0) {
            String vmIpAddress = guestInfo.getIpAddresses().get(0);
            NetworkInterface nic = new NetworkInterface();
            nic.setAddress(vmIpAddress);
            nic.setHostname(guestInfo.getHostName());
            networkInterfaceInfos.add(nic);
        }
        return networkInterfaceInfos;
    }

    @Override
    public String getMachineGraphicalConsoleUrl(final String machineId) throws CloudProviderException {
        try {
            String vncUrl = this.clusterManager.getVirtualMachineConsole(machineId);
            URI vncURI = new URI(vncUrl);
            String host = vncURI.getHost();
            int port = vncURI.getPort();
            return this.webSocketProxyManager.getVncUrl(host, port);
        } catch (Exception e) {
            VMMComputeService.logger.error(e.getMessage(), e);
            throw new CloudProviderException(e.getMessage());
        }
    }

    @Override
    public List<String> listMachines() throws CloudProviderException {
        List<String> vmIds = new ArrayList<String>();
        // We intentionally ignore the return value of
        // this.clusterManager.getResourceTree();
        this.clusterManager.getResourceTree();
        Map<String, ServerPoolMXBean> poolMBeanTable = this.clusterManager.getPoolMBeanTable();
        for (ServerPoolMXBean serverPoolMXBean : poolMBeanTable.values()) {
            try {
                for (VirtualMachineMXBean virtualMachineMXBean : serverPoolMXBean.getVirtualMachines()) {
                    vmIds.add(virtualMachineMXBean.getUuid());
                }
            } catch (VMMException e) {
                VMMComputeService.logger.error(e.getMessage(), e);
                throw new CloudProviderException(e.getMessage());
            }
        }
        return vmIds;
    }

    @Override
    public Job<MachineImage> captureImage(final String machineId, final ImageCreate imageCreate) throws CloudProviderException {
        final Callable<MachineImage> createImageTask = new Callable<MachineImage>() {
            @Override
            public MachineImage call() throws Exception {
                String imageId = VMMComputeService.this.clusterManager.createImageFromVirtualMachine(machineId,
                    imageCreate.getName(), imageCreate.getDescription());
                MachineImage image = new MachineImage();
                image.setProviderAssignedId(imageId);
                return image;
            }
        };
        ListenableFuture<MachineImage> result = this.executorService.submit(createImageTask);
        return this.jobManager.newJob(machineId, "machine.capture", result);
    }

    @Override
    public Job<Void> destroyImage(final String imageId) throws CloudProviderException {
        final Callable<Void> destroyImageTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                VMMComputeService.this.clusterManager.destroyVMImage(imageId);
                return null;
            }
        };
        ListenableFuture<Void> result = this.executorService.submit(destroyImageTask);
        return this.jobManager.newJob(imageId, "image.destroy", result);
    }

    @Override
    public Job<MachineImage> uploadImage(final ImageUpload imageUpload) throws CloudProviderException {
        final Callable<MachineImage> uploadImageTask = new Callable<MachineImage>() {
            @Override
            public MachineImage call() throws Exception {
                String imageId = VMMComputeService.this.clusterManager.uploadVMImage(imageUpload.getUrl(),
                    imageUpload.getFormat(), imageUpload.getName(), imageUpload.getDescription(), null);
                MachineImage image = new MachineImage();
                image.setProviderAssignedId(imageId);
                return image;
            }
        };
        ListenableFuture<MachineImage> result = this.executorService.submit(uploadImageTask);
        return this.jobManager.newJob("", "image.upload", result);
    }

    @Override
    public Job<Void> migrateMachine(final String machineId, final String hostId) {
        final Callable<Void> migrateMachineTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                VMMComputeService.this.clusterManager.migrateVirtualMachine(machineId, hostId);
                return null;
            }
        };
        ListenableFuture<Void> result = this.executorService.submit(migrateMachineTask);
        return this.jobManager.newJob(machineId, "machine.migrate", result);
    }

    @Override
    public Job<Volume> createVolume(final VolumeCreate volumeCreate) throws CloudProviderException {
        final Callable<Volume> createTask = new Callable<Volume>() {
            @Override
            public Volume call() throws Exception {
                VolumeSpec spec = new VolumeSpec();
                spec.setName(volumeCreate.getName());
                spec.setDescription(volumeCreate.getDescription());
                Map<String, String> constraints = new HashMap<String, String>();
                constraints.put("location", VMMComputeService.this.cloudProviderLocation.getLocationId());
                spec.setConstraints(constraints);
                String volumeId = VMMComputeService.this.clusterManager.createVolume(spec);
                Volume volume = new Volume();
                volume.setProviderAssignedId(volumeId);
                return volume;
            }
        };
        ListenableFuture<Volume> result = this.executorService.submit(createTask);
        return this.jobManager.newJob("", "volume.create", result);
    }

    @Override
    public Job<Void> destroyVolume(final String volumeId) throws CloudProviderException {
        final Callable<Void> destroyVolumeTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                VMMComputeService.this.clusterManager.destroyVolume(volumeId);
                return null;
            }
        };
        ListenableFuture<Void> result = this.executorService.submit(destroyVolumeTask);
        return this.jobManager.newJob(volumeId, "volume.destroy", result);
    }

    @Override
    public Job<VolumeAttachment> attachVolume(final String machineId, final VolumeAttachment attachement)
        throws CloudProviderException {
        final Callable<VolumeAttachment> attachVolumeTask = new Callable<VolumeAttachment>() {
            @Override
            public VolumeAttachment call() throws Exception {
                VMMComputeService.this.clusterManager.attachVolumeToVM(machineId, attachement.getVolumeId());
                return attachement;
            }
        };
        ListenableFuture<VolumeAttachment> result = this.executorService.submit(attachVolumeTask);
        return this.jobManager.newJob(machineId, "machine.attachvolume", result);
    }

    @Override
    public Job<String> detachVolume(final String machineId, final String volumeId) throws CloudProviderException {
        final Callable<String> detachVolumeTask = new Callable<String>() {
            @Override
            public String call() throws Exception {
                VMMComputeService.this.clusterManager.detachVolumeFromVM(machineId, volumeId);
                return volumeId;
            }
        };
        ListenableFuture<String> result = this.executorService.submit(detachVolumeTask);
        return this.jobManager.newJob(machineId, "machine.detachvolume", result);
    }

    @Override
    public List<PerfMetricInfo> getAvailableMachinePerfMetrics(final String machineId) throws CloudProviderException {
        try {
            org.ow2.sirocco.vmm.api.monitoring.PerfMetricInfo[] infos = this.clusterManager
                .getVirtualMachineAvailablePerfMetrics(machineId);
            List<PerfMetricInfo> result = new ArrayList<PerfMetricInfo>();
            for (org.ow2.sirocco.vmm.api.monitoring.PerfMetricInfo info : infos) {
                PerfMetricInfo metricInfo = new PerfMetricInfo();
                metricInfo.setName(info.getName());
                metricInfo.setDescription(info.getDescription());
                metricInfo.setStartTime(info.getStartTime());
                metricInfo.setUnit(Unit.valueOf(info.getUnit().toString()));
                result.add(metricInfo);
            }
            return result;
        } catch (VMMException ex) {
            throw new CloudProviderException(ex.getMessage());
        }
    }

    @Override
    public List<PerfMetric> getMachinePerfMetrics(final String machineId, final PerfMetricInfo info, final Date startTime,
        final Date endTime) throws CloudProviderException {
        try {
            org.ow2.sirocco.vmm.api.monitoring.PerfMetric[] metrics = this.clusterManager.getVirtualMachinePerfMetrics(
                machineId, info.getName(), startTime, endTime);
            List<PerfMetric> result = new ArrayList<PerfMetric>();
            for (org.ow2.sirocco.vmm.api.monitoring.PerfMetric from : metrics) {
                PerfMetric metric = new PerfMetric();
                metric.setTime(from.getTime());
                metric.setValue(from.getValue());
                result.add(metric);
            }
            return result;
        } catch (Exception ex) {
            throw new CloudProviderException(ex.getMessage());
        }
    }

}
