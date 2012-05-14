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
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.connector.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.connector.api.IImageService;
import org.ow2.sirocco.cloudmanager.connector.api.ISystemService;
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDiskCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterface.InterfaceState;
import org.ow2.sirocco.cloudmanager.model.cimi.SystemCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.google.common.util.concurrent.ListenableFuture;

public class MockCloudProviderConnector implements ICloudProviderConnector, IComputeService, ISystemService, IVolumeService,
    IImageService {

    private static Log logger = LogFactory.getLog(MockCloudProviderConnector.class);

    private static final int ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS = 200;

    private final String cloudProviderId;

    private final CloudProviderAccount cloudProviderAccount;

    private final CloudProviderLocation cloudProviderLocation;

    private final MockCloudProviderConnectorFactory mockCloudProviderConnectorFactory;

    private Map<String, Volume> volumes = new ConcurrentHashMap<String, Volume>();

    private Map<String, VolumeImage> volumeImages = new ConcurrentHashMap<String, VolumeImage>();

    private Map<String, Machine> machines = new ConcurrentHashMap<String, Machine>();

    public MockCloudProviderConnector(final MockCloudProviderConnectorFactory mockCloudProviderConnectorFactory,
        final CloudProviderAccount cloudProviderAccount, final CloudProviderLocation cloudProviderLocation) {
        this.mockCloudProviderConnectorFactory = mockCloudProviderConnectorFactory;
        this.cloudProviderId = UUID.randomUUID().toString();
        this.cloudProviderLocation = cloudProviderLocation;
        this.cloudProviderAccount = cloudProviderAccount;
    }

    @Override
    public String getCloudProviderId() {
        return this.cloudProviderId;
    }

    @Override
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    @Override
    public CloudProviderLocation getCloudProviderLocation() {
        return this.cloudProviderLocation;
    }

    @Override
    public IComputeService getComputeService() throws ConnectorException {
        return this;
    }

    @Override
    public ISystemService getSystemService() throws ConnectorException {
        return this;
    }

    @Override
    public IVolumeService getVolumeService() throws ConnectorException {
        return this;
    }

    @Override
    public IImageService getImageService() throws ConnectorException {
        return this;
    }

    @Override
    public void setNotificationOnJobCompletion(final String jobId) throws ConnectorException {
        try {
            this.mockCloudProviderConnectorFactory.getJobManager().setNotificationOnJobCompletion(jobId);
        } catch (Exception e) {
            throw new ConnectorException(e.getMessage());
        }
    }

    @Override
    public synchronized Job createVolume(final VolumeCreate volumeCreate) throws ConnectorException {
        final String volumeProviderAssignedId = UUID.randomUUID().toString();
        final Volume volume = new Volume();
        volume.setProviderAssignedId(volumeProviderAssignedId);
        this.volumes.put(volumeProviderAssignedId, volume);
        volume.setState(Volume.State.CREATING);

        final Callable<Volume> createTask = new Callable<Volume>() {
            @Override
            public Volume call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                volume.setState(Volume.State.AVAILABLE);
                return volume;
            }
        };

        ListenableFuture<Volume> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(createTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(volume, null, "add", result);
    }

    @Override
    public synchronized Job deleteVolume(final String volumeId) throws ConnectorException {
        Volume volume = this.volumes.get(volumeId);
        if (volume == null) {
            throw new ConnectorException("Volume " + volumeId + " doesn't exist");
        }
        volume.setState(Volume.State.DELETING);
        final Callable<Void> deleteTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                MockCloudProviderConnector.this.volumes.remove(volumeId);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(deleteTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(volume, null, "delete", result);

    }

    @Override
    public synchronized org.ow2.sirocco.cloudmanager.model.cimi.Volume.State getVolumeState(final String volumeId)
        throws ConnectorException {
        return this.getVolume(volumeId).getState();
    }

    @Override
    public synchronized Volume getVolume(final String volumeId) throws ConnectorException {
        Volume volume = this.volumes.get(volumeId);
        if (volume == null) {
            throw new ConnectorException("Volume " + volumeId + " doesn't exist");
        }
        return volume;
    }

    @Override
    public synchronized Job createMachine(final MachineCreate machineCreate) throws ConnectorException {
        final String machineProviderAssignedId = UUID.randomUUID().toString();
        final Machine machine = new Machine();
        machine.setProviderAssignedId(machineProviderAssignedId);
        this.machines.put(machineProviderAssignedId, machine);
        MockCloudProviderConnector.logger.info("Creating machine with providerAssignedId " + machineProviderAssignedId);
        machine.setState(Machine.State.CREATING);
        machine.setCpu(new Cpu(machineCreate.getMachineTemplate().getMachineConfiguration().getCpu()));
        machine.setMemory(machineCreate.getMachineTemplate().getMachineConfiguration().getMemory());
        List<MachineDisk> disks = new ArrayList<MachineDisk>();
        if (machineCreate.getMachineTemplate().getMachineConfiguration().getDiskTemplates() != null) {
            for (DiskTemplate diskTemplate : machineCreate.getMachineTemplate().getMachineConfiguration().getDiskTemplates()) {
                MachineDisk mdisk = new MachineDisk();
                // TODO
                // disk.setDiskUnit(diskTemplate.getDiskUnit());
                mdisk.setDisk(new Disk());
                mdisk.getDisk().setQuantity(diskTemplate.getQuantity());
                mdisk.setInitialLocation(diskTemplate.getInitialLocation());
                disks.add(mdisk);
            }
        }
        MachineDiskCollection diskCollection = new MachineDiskCollection();
        diskCollection.setItems(disks);
        machine.setDisks(diskCollection);

        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        if (machineCreate.getMachineTemplate().getNetworkInterfaces() != null) {
            for (NetworkInterface networkInterface : machineCreate.getMachineTemplate().getNetworkInterfaces()) {
                NetworkInterface newNetIntf = new NetworkInterface();
                newNetIntf.setAddress(networkInterface.getAddress());
                newNetIntf.setAllocation(networkInterface.getAllocation());
                newNetIntf.setDefaultGateway(networkInterface.getDefaultGateway());
                newNetIntf.setHostname(networkInterface.getHostname());
                newNetIntf.setMacAddress(networkInterface.getMacAddress());
                newNetIntf.setProtocol(networkInterface.getProtocol());
                newNetIntf.setState(InterfaceState.STANDBY);
                networkInterfaces.add(newNetIntf);
            }
        }
        machine.setNetworkInterfaces(networkInterfaces);

        final Callable<Machine> createTask = new Callable<Machine>() {
            @Override
            public Machine call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                for (NetworkInterface networkInterface : machine.getNetworkInterfaces()) {
                    networkInterface.setState(InterfaceState.ACTIVE);
                }
                machine.setState(Machine.State.STOPPED);
                return machine;
            }
        };
        // TODO create and attach volumes
        MachineVolumeCollection volumeCollection = new MachineVolumeCollection();
        volumeCollection.setItems(Collections.<MachineVolume> emptyList());
        machine.setVolumes(volumeCollection);
        ListenableFuture<Machine> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(createTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machine, null, "add", result);

    }

    @Override
    public synchronized Job startMachine(final String machineId) throws ConnectorException {
        MockCloudProviderConnector.logger.info("Starting machine with providerAssignedId " + machineId);
        final Machine machine = this.machines.get(machineId);
        if (machine == null) {
            throw new ConnectorException("Machine " + machineId + " doesn't exist");
        }
        if (machine.getState() == State.CREATING || machine.getState() == State.STARTED || machine.getState() == State.DELETING) {
            throw new ConnectorException("Illegal operation");
        }
        machine.setState(State.STARTING);

        final Callable<Void> startTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                machine.setState(Machine.State.STARTED);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(startTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machine, null, "start", result);
    }

    @Override
    public synchronized Job stopMachine(final String machineId) throws ConnectorException {
        MockCloudProviderConnector.logger.info("Stopping machine with providerAssignedId " + machineId);
        final Machine machine = this.machines.get(machineId);
        if (machine == null) {
            throw new ConnectorException("Machine " + machineId + " doesn't exist");
        }
        if (machine.getState() == State.CREATING || machine.getState() == State.STOPPED || machine.getState() == State.PAUSING
            || machine.getState() == State.PAUSED || machine.getState() == State.SUSPENDING
            || machine.getState() == State.SUSPENDED || machine.getState() == State.DELETING) {
            throw new ConnectorException("Illegal operation");
        }
        machine.setState(State.STOPPING);

        final Callable<Void> stopTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                machine.setState(Machine.State.STOPPED);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(stopTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machine, null, "stop", result);
    }

    @Override
    public synchronized Job suspendMachine(final String machineId) throws ConnectorException {
        final Machine machine = this.machines.get(machineId);
        if (machine == null) {
            throw new ConnectorException("Machine " + machineId + " doesn't exist");
        }
        if (machine.getState() == State.CREATING || machine.getState() == State.STARTING
            || machine.getState() == State.STOPPING || machine.getState() == State.STOPPED
            || machine.getState() == State.PAUSING || machine.getState() == State.PAUSED
            || machine.getState() == State.SUSPENDING || machine.getState() == State.SUSPENDED
            || machine.getState() == State.DELETING) {
            throw new ConnectorException("Illegal operation");
        }
        machine.setState(State.SUSPENDING);

        final Callable<Void> suspendTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                machine.setState(Machine.State.SUSPENDED);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(suspendTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machine, null, "suspend", result);
    }

    @Override
    public synchronized Job restartMachine(final String machineId) throws ConnectorException {
        final Machine machine = this.machines.get(machineId);
        if (machine == null) {
            throw new ConnectorException("Machine " + machineId + " doesn't exist");
        }
        final Callable<Void> restartTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(restartTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machine, null, "restart", result);
    }

    @Override
    public synchronized Job pauseMachine(final String machineId) throws ConnectorException {
        final Machine machine = this.machines.get(machineId);
        if (machine == null) {
            throw new ConnectorException("Machine " + machineId + " doesn't exist");
        }
        if (machine.getState() == State.CREATING || machine.getState() == State.STARTING
            || machine.getState() == State.STOPPING || machine.getState() == State.STOPPED
            || machine.getState() == State.PAUSING || machine.getState() == State.PAUSED
            || machine.getState() == State.SUSPENDING || machine.getState() == State.SUSPENDED
            || machine.getState() == State.DELETING) {
            throw new ConnectorException("Illegal operation");
        }
        machine.setState(State.PAUSING);

        final Callable<Void> pauseTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                machine.setState(Machine.State.PAUSED);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(pauseTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machine, null, "pause", result);
    }

    @Override
    public synchronized Job deleteMachine(final String machineId) throws ConnectorException {
        MockCloudProviderConnector.logger.info("Deleting machine with providerAssignedId " + machineId);
        Machine machine = this.machines.get(machineId);
        if (machine == null) {
            throw new ConnectorException("Machine " + machineId + " doesn't exist");
        }
        machine.setState(Machine.State.DELETING);
        final Callable<Void> deleteTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                MockCloudProviderConnector.this.machines.remove(machineId);
                MockCloudProviderConnector.logger.info("Machine " + machineId + " deleted");
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(deleteTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machine, null, "delete", result);
    }

    @Override
    public synchronized State getMachineState(final String machineId) throws ConnectorException {
        return this.getMachine(machineId).getState();
    }

    @Override
    public synchronized Machine getMachine(final String machineId) throws ConnectorException {
        Machine machine = this.machines.get(machineId);
        return machine;
    }

    @Override
    public Job destroyImage(final String imageId) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job uploadImage(final MachineImage imageUpload) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job createSystem(final SystemCreate systemCreate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Job startSystem(final String systemId) throws ConnectorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Job stopSystem(final String systemId) throws ConnectorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Job restartSystem(final String systemId) throws ConnectorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Job pauseSystem(final String systemId) throws ConnectorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Job suspendSystem(final String systemId) throws ConnectorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Job addVolumeToMachine(final String machineId, final MachineVolume machineVolume) throws ConnectorException {
        final Machine machine = this.machines.get(machineId);
        if (machine == null) {
            throw new ConnectorException("Machine " + machineId + " doesn't exist");
        }

        final Callable<Void> attachTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                machine.getVolumes().getItems().add(machineVolume);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(attachTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machine, machineVolume.getVolume(), "add", result);
    }

    @Override
    public Job removeVolumeFromMachine(final String machineId, final MachineVolume machineVolume) throws ConnectorException {
        final Machine machine = this.machines.get(machineId);
        if (machine == null) {
            throw new ConnectorException("Machine " + machineId + " doesn't exist");
        }

        final Callable<Void> detachTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                machine.getVolumes().getItems().remove(machineVolume);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(detachTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machine, machineVolume.getVolume(), "delete",
            result);
    }

    @Override
    public Job createVolumeImage(final VolumeImage from) throws ConnectorException {
        final String volumeImageProviderAssignedId = UUID.randomUUID().toString();
        final VolumeImage volumeImage = new VolumeImage();
        volumeImage.setBootable(from.getBootable());
        volumeImage.setImageLocation(from.getImageLocation());
        volumeImage.setOwner(null);
        volumeImage.setProviderAssignedId(volumeImageProviderAssignedId);
        this.volumeImages.put(volumeImageProviderAssignedId, volumeImage);
        volumeImage.setState(VolumeImage.State.CREATING);

        final Callable<VolumeImage> createTask = new Callable<VolumeImage>() {
            @Override
            public VolumeImage call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                volumeImage.setState(VolumeImage.State.AVAILABLE);
                return volumeImage;
            }
        };

        ListenableFuture<VolumeImage> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(createTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(volumeImage, null, "add", result);
    }

    @Override
    public Job createVolumeSnapshot(final String volumeId, final VolumeImage from) throws ConnectorException {
        final Volume volume = this.volumes.get(volumeId);
        if (volume == null) {
            throw new ConnectorException("Volume " + volumeId + " doesn't exist");
        }
        final String volumeImageProviderAssignedId = UUID.randomUUID().toString();
        final VolumeImage volumeImage = new VolumeImage();
        volumeImage.setBootable(from.getBootable());
        volumeImage.setImageLocation(from.getImageLocation());
        volumeImage.setOwner(volume);
        volumeImage.setProviderAssignedId(volumeImageProviderAssignedId);
        this.volumeImages.put(volumeImageProviderAssignedId, volumeImage);
        volumeImage.setState(VolumeImage.State.CREATING);

        final Callable<VolumeImage> createTask = new Callable<VolumeImage>() {
            @Override
            public VolumeImage call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                volumeImage.setState(VolumeImage.State.AVAILABLE);
                volume.getImages().add(volumeImage);
                return volumeImage;
            }
        };

        ListenableFuture<VolumeImage> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(createTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(volumeImage, null, "add", result);
    }

    @Override
    public VolumeImage getVolumeImage(final String volumeImageId) throws ConnectorException {
        final VolumeImage volumeImage = this.volumeImages.get(volumeImageId);
        if (volumeImage == null) {
            throw new ConnectorException("VolumeImage " + volumeImageId + " doesn't exist");
        }
        return volumeImage;
    }

    @Override
    public Job deleteVolumeImage(final String volumeImageId) throws ConnectorException {
        VolumeImage volumeImage = this.volumeImages.get(volumeImageId);
        if (volumeImage == null) {
            throw new ConnectorException("VolumeImage " + volumeImageId + " doesn't exist");
        }
        volumeImage.setState(VolumeImage.State.DELETING);
        final Callable<Void> deleteTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                MockCloudProviderConnector.this.volumeImages.remove(volumeImageId);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(deleteTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(volumeImage, null, "delete", result);
    }

}
