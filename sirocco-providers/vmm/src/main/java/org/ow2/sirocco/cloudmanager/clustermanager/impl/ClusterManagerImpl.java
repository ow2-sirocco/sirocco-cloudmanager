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

package org.ow2.sirocco.cloudmanager.clustermanager.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.ow2.sirocco.cloudmanager.clustermanager.api.AllocationMode;
import org.ow2.sirocco.cloudmanager.clustermanager.api.Domain;
import org.ow2.sirocco.cloudmanager.clustermanager.api.Host;
import org.ow2.sirocco.cloudmanager.clustermanager.api.IClusterManager;
import org.ow2.sirocco.cloudmanager.clustermanager.api.ResourcePartitionInfo;
import org.ow2.sirocco.cloudmanager.clustermanager.api.ServerPool;
import org.ow2.sirocco.cloudmanager.clustermanager.api.VirtualMachineCreationSpec;
import org.ow2.sirocco.cloudmanager.clustermanager.api.VolumeSpec;
import org.ow2.sirocco.vmm.api.DiskOperation;
import org.ow2.sirocco.vmm.api.DomainMXBean;
import org.ow2.sirocco.vmm.api.GuestInfo;
import org.ow2.sirocco.vmm.api.HostMXBean;
import org.ow2.sirocco.vmm.api.ResourcePartitionMXBean;
import org.ow2.sirocco.vmm.api.ServerPoolMXBean;
import org.ow2.sirocco.vmm.api.StoragePoolMXBean;
import org.ow2.sirocco.vmm.api.VMMAgentMXBean;
import org.ow2.sirocco.vmm.api.VMMException;
import org.ow2.sirocco.vmm.api.VNICSpec;
import org.ow2.sirocco.vmm.api.VNICSpec.MacAddressAssignement;
import org.ow2.sirocco.vmm.api.VirtualCdrom;
import org.ow2.sirocco.vmm.api.VirtualCdromSpec;
import org.ow2.sirocco.vmm.api.VirtualDisk;
import org.ow2.sirocco.vmm.api.VirtualDiskSpec;
import org.ow2.sirocco.vmm.api.VirtualMachineConfigSpec;
import org.ow2.sirocco.vmm.api.VirtualMachineImageMXBean;
import org.ow2.sirocco.vmm.api.VirtualMachineImageStoreMXBean;
import org.ow2.sirocco.vmm.api.VirtualMachineMXBean;
import org.ow2.sirocco.vmm.api.Volume;
import org.ow2.sirocco.vmm.api.monitoring.MonitorableMXBean.ConsolidationFunction;
import org.ow2.sirocco.vmm.api.monitoring.PerfMetric;
import org.ow2.sirocco.vmm.api.monitoring.PerfMetricInfo;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

@SuppressWarnings({"deprecation", "unused"})
public class ClusterManagerImpl implements IClusterManager, ManagedService {

    private static Log logger = LogFactory.getLog(ClusterManagerImpl.class);

    private static final String RESOURCE_PARTITION_USAGE_PROPERTY = "usage";

    private static final String RESOURCE_PARTITION_ON_DEMAND_USAGE = "OD";

    private static final String RESOURCE_PARTITION_ADVANCED_RESERVATION_USAGE = "AR";

    private static final String TOPIC_CONNECTION_FACTORY_NAME = "JTCF";

    private static final String TOPIC_NAME = "ClusterManager";

    private static final String DEFAULT_AGENT_URL = "service:jmx:rmi:///jndi/rmi://localhost:9999/server";

    private static final int OBTAIN_GUEST_IP_ADDRESS_DEFAULT_WAITING_TIME_IN_SECONDS = 90;

    private static final int SLEEPING_TIME_IN_MILLISECONDS_15000 = 15000;

    private static final long RESOLUTION_IN_MILLISECONDS_10 = 10;

    private MBeanServerConnection mbsc;

    private JMXConnector jmxc;

    private boolean isConnected;

    private String jmxURL;

    private DomainMXBean rootDomainMBean;

    private Domain rootDomain;

    private Map<String, HostMXBean> hostMBeanTable = new Hashtable<String, HostMXBean>();

    private Map<String, VirtualMachineMXBean> vmMBeanTable = new Hashtable<String, VirtualMachineMXBean>();

    private Map<String, ServerPoolMXBean> poolMBeanTable = new Hashtable<String, ServerPoolMXBean>();

    private Map<String, StoragePoolMXBean> storagePoolMBeanTable = new Hashtable<String, StoragePoolMXBean>();

    private List<ResourcePartitionInfo> onDemandResourcePartitions = new ArrayList<ResourcePartitionInfo>();

    private List<ResourcePartitionInfo> advancedReservationResourcePartitions = new ArrayList<ResourcePartitionInfo>();

    private List<String> locations;

    private class VolumeInfo {

        private StoragePoolMXBean storagePool;

        private String volumeKey;

        private Volume volume;

        VolumeInfo(final String providerId) throws VMMException {
            int i = providerId.indexOf(' ');
            if (i <= 0 || providerId.length() < i + 2) {
                throw new VMMException("Wrong providerId");
            }
            String storagePoolName = providerId.substring(0, i);
            this.storagePool = ClusterManagerImpl.this.getStoragePoolMXBean(storagePoolName);
            if (this.storagePool == null) {
                throw new VMMException("Cannot find storage pool " + storagePoolName);
            }
            this.volumeKey = providerId.substring(i + 1);
            this.volume = this.storagePool.getVolumeByKey(this.volumeKey);
            if (this.volume == null) {
                throw new VMMException("Cannot find volume " + this.volumeKey + " in storage pool " + storagePoolName);
            }
        }

    }

    public ClusterManagerImpl() {

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void updated(final Dictionary properties) throws ConfigurationException {
        if (properties == null) {
            ClusterManagerImpl.logger.error("No config file !");
        } else {
            this.jmxURL = (String) properties.get("agent.url");
            if (this.jmxURL == null) {
                this.jmxURL = ClusterManagerImpl.DEFAULT_AGENT_URL;
            }
            ClusterManagerImpl.logger.info("Agent URL set to " + this.jmxURL);
        }
    }

    public synchronized void start() {
        ClusterManagerImpl.logger.debug("ClusterManagerImpl started");
    }

    public synchronized void shutdown() {
        ClusterManagerImpl.logger.debug("ClusterManagerImpl shutting down");
        if (this.isConnected) {
            this.disconnect();
        }
    }

    private synchronized void checkConnection() {
        if (!this.isConnected) {
            this.connect();
        }
    }

    private void connect() {
        try {
            ClusterManagerImpl.logger.info("Connecting to: " + this.jmxURL);

            if (this.jmxURL == null) {
                this.jmxURL = ClusterManagerImpl.DEFAULT_AGENT_URL;
            } // no "else" needed.

            JMXServiceURL url = new JMXServiceURL(this.jmxURL);
            this.jmxc = JMXConnectorFactory.connect(url, null);
            String connectionId = this.jmxc.getConnectionId();
            this.mbsc = this.jmxc.getMBeanServerConnection();
            Set<ObjectName> s = this.mbsc.queryNames(new ObjectName("org.ow2.sirocco.vmm.api:type=Agent"), null);
            ObjectName bridgeObjectName = s.toArray(new ObjectName[1])[0];
            VMMAgentMXBean bridgeMBean = JMX.newMXBeanProxy(this.mbsc, bridgeObjectName, VMMAgentMXBean.class);
            this.rootDomainMBean = bridgeMBean.getRootDomain();
            for (ResourcePartitionInfo rpInfo : this.onDemandResourcePartitions) {
                rpInfo.setMbean(JMX.newMXBeanProxy(this.mbsc, rpInfo.getObjectName(), ResourcePartitionMXBean.class));
            }
            for (ResourcePartitionInfo rpInfo : this.advancedReservationResourcePartitions) {
                rpInfo.setMbean(JMX.newMXBeanProxy(this.mbsc, rpInfo.getObjectName(), ResourcePartitionMXBean.class));
            }

            this.isConnected = true;
            ClusterManagerImpl.logger.info("Connected to: " + this.jmxURL);
        } catch (MalformedURLException e) {
            ClusterManagerImpl.logger.error("ERROR : impossible to connect to JMX bridge: " + e.getMessage());
        } catch (IOException e) {
            ClusterManagerImpl.logger.error("ERROR : impossible to connect to JMX bridge: " + e.getMessage());
        } catch (MalformedObjectNameException e) {
            ClusterManagerImpl.logger.error("ERROR : impossible to connect to JMX bridge: " + e.getMessage());
        } catch (NullPointerException e) {
            ClusterManagerImpl.logger.error("ERROR : impossible to connect to JMX bridge: " + e.getMessage());
        }
    }

    private void disconnect() {
        // XXX should remove all notif listeners
        try {
            ClusterManagerImpl.logger.info("called");
            this.jmxc.close();
            this.hostMBeanTable.clear();
            this.vmMBeanTable.clear();
        } catch (Exception ex) {
            ClusterManagerImpl.logger.error("Disconnect error:", ex);
        }
    }

    public VirtualMachineMXBean getVirtualMachineMBean(final String providerId) {
        this.checkConnection();
        VirtualMachineMXBean vmMBean = this.vmMBeanTable.get(providerId);
        if (vmMBean == null) {
            synchronized (this) {
                try {

                    Set<ObjectName> s = this.mbsc.queryNames(new ObjectName("org.ow2.sirocco.vmm.api:type=VirtualMachine,uuid="
                        + providerId + ",*"), null);
                    ObjectName vmMBeanName = s.toArray(new ObjectName[1])[0];

                    if (s.isEmpty()) {
                        return null;
                    }
                    vmMBean = JMX.newMXBeanProxy(this.mbsc, vmMBeanName, VirtualMachineMXBean.class, true);
                } catch (Exception ex) {
                    ClusterManagerImpl.logger.info("Error : ", ex);
                    return null;
                }
                this.vmMBeanTable.put(providerId, vmMBean);
            }
        }
        return vmMBean;
    }

    private StoragePoolMXBean getStoragePoolMXBean(final String poolName) {
        this.checkConnection();
        StoragePoolMXBean storagePool = this.storagePoolMBeanTable.get(poolName);
        if (storagePool == null) {
            synchronized (this) {
                try {

                    Set<ObjectName> s = this.mbsc.queryNames(new ObjectName("org.ow2.sirocco.vmm.api:type=StoragePool,name="
                        + poolName + ",*"), null);
                    ObjectName objectName = s.toArray(new ObjectName[1])[0];

                    if (s.isEmpty()) {
                        return null;
                    }
                    storagePool = JMX.newMXBeanProxy(this.mbsc, objectName, StoragePoolMXBean.class, true);
                } catch (Exception ex) {
                    ClusterManagerImpl.logger.info("Error : ", ex);
                    return null;
                }
                this.storagePoolMBeanTable.put(poolName, storagePool);
            }
        }
        return storagePool;
    }

    private synchronized void addHostMBean(final HostMXBean hostMBean, final ObjectName objectName) {
        this.hostMBeanTable.put(objectName.getKeyProperty("name"), hostMBean);
    }

    private HostMXBean getHostMBean(final String hostId) {
        HostMXBean hostMBean = this.hostMBeanTable.get(hostId);
        if (hostMBean == null) {
            this.checkConnection();
            try {
                Set<ObjectName> s = this.mbsc.queryNames(new ObjectName("org.ow2.sirocco.vmm.api:type=Host,name=" + hostId
                    + ",*"), null);
                ObjectName objectName = s.toArray(new ObjectName[1])[0];
                HostMXBean host = JMX.newMXBeanProxy(this.mbsc, objectName, HostMXBean.class, true);
                this.addHostMBean(hostMBean, objectName);
                return host;
            } catch (MalformedObjectNameException e) {
                ClusterManagerImpl.logger.info(e);
                return null;
            } catch (NullPointerException e) {
                ClusterManagerImpl.logger.info(e);
                return null;
            } catch (IOException e) {
                ClusterManagerImpl.logger.info(e);
                return null;
            }
        } else {
            return hostMBean;
        }
    }

    private ServerPoolMXBean getServerPoolMBean(final String objectNameString) {
        this.checkConnection();
        ServerPoolMXBean poolMBean = this.poolMBeanTable.get(objectNameString);
        ClusterManagerImpl.logger.debug("\n\n\n" + objectNameString);
        if (poolMBean == null) {
            try {
                poolMBean = JMX.newMXBeanProxy(this.mbsc, new ObjectName(objectNameString), ServerPoolMXBean.class);
                this.poolMBeanTable.put(objectNameString, poolMBean);
            } catch (Exception ex) {
                ClusterManagerImpl.logger.error("Failed to retrieve ServerPoolMXBean proxy for " + objectNameString, ex);
            }
        }
        return poolMBean;
    }

    private ServerPoolMXBean getServerPoolMBean2(final String serverPoolName) {
        if (serverPoolName == null) {
            return null;
        }
        this.checkConnection();
        try {

            // ServerPoolMXBean sp =
            // this.poolMBeanTable.get("org.ow2.sirocco.vmm.api:type=ServerPool,name="
            // + serverPoolName);
            //
            // ClusterManagerImpl.logger.info("org.ow2.sirocco.vmm.api:type=ServerPool,name="
            // + serverPoolName);
            // ClusterManagerImpl.logger.info(sp.getObjectName());
            // if (sp != null) {
            // sp = JMX.newMXBeanProxy(this.mbsc, sp.getObjectName(),
            // ServerPoolMXBean.class, true);
            // }
            Set<ObjectName> s = this.mbsc.queryNames(new ObjectName("org.ow2.sirocco.vmm.api:type=ServerPool,name="
                + serverPoolName + ",*"), null);
            ObjectName oname = s.toArray(new ObjectName[1])[0];
            return JMX.newMXBeanProxy(this.mbsc, oname, ServerPoolMXBean.class, true);
        } catch (NullPointerException e) {
            ClusterManagerImpl.logger.info(e);
            return null;
        } catch (MalformedObjectNameException e) {
            ClusterManagerImpl.logger.info(e);
            return null;
        } catch (IOException e) {
            ClusterManagerImpl.logger.info(e);
            return null;
        }
    }

    @Override
    public String createVirtualMachine(final VirtualMachineCreationSpec creationSpec, final Map<String, String> constraints,
        final AllocationMode allocationMode, final boolean startVM) throws VMMException {
        this.checkConnection();

        final ResourcePartitionMXBean resourcePartition;
        resourcePartition = this.getResourcePartition(allocationMode, constraints.get("hypervisor"),
            constraints.get("location"));
        if (resourcePartition == null) {
            String errorMessage = "Cannot find resource partition for " + allocationMode + " , hypervisor="
                + constraints.get("hypervisor") + " location=" + constraints.get("location");
            ClusterManagerImpl.logger.error(errorMessage);
            throw new VMMException(errorMessage);
        }

        final VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
        configSpec.setName(creationSpec.getName());
        configSpec.setNumVCPUs(creationSpec.getNumVCPUs());
        configSpec.setMemoryMB(creationSpec.getMemorySizeMB());

        VirtualDiskSpec diskSpec = new VirtualDiskSpec();
        diskSpec.setCapacityMB(creationSpec.getDiskCapacityMB());
        diskSpec.setDiskOp(DiskOperation.CREATE_FROM);
        diskSpec.setCopyOnWrite(true);
        // imageId is built as: <storage pool name> <volume key>
        int i = creationSpec.getVmImageId().indexOf(" ");
        if (i <= 0 || creationSpec.getVmImageId().length() < i + 2) {
            throw new VMMException("Wrong providerId");
        }
        String storagePoolName = creationSpec.getVmImageId().substring(0, i);
        String volumeKey = creationSpec.getVmImageId().substring(i + 1);

        StoragePoolMXBean vmImageStoragePool = this.getStoragePoolMXBean(storagePoolName);
        if (vmImageStoragePool == null) {
            throw new VMMException("Cannot find storage pool " + storagePoolName + " referenced by VM Image");
        }
        Volume vmImageVolume = vmImageStoragePool.getVolumeByKey(volumeKey);
        if (vmImageVolume == null) {
            // throw new VMMException("Cannot find volume with key " + volumeKey
            // + " referenced by VM Image");
            ClusterManagerImpl.logger.warn("Cannot find volume with key " + volumeKey + " referenced by VM Image");
            for (Volume volume : vmImageStoragePool.getVolumes()) {
                ClusterManagerImpl.logger.info("" + volume);
                if (volume.getKey().startsWith(volumeKey)) {
                    vmImageVolume = volume;
                    break;
                }// no "else" needed.
            }
        }
        diskSpec.setVolume(vmImageVolume);

        // find the default storage pool of the server pool which will host the
        // VM
        ServerPoolMXBean serverPool = resourcePartition.getRootServerPool();
        StoragePoolMXBean targetStoragePool = null;
        for (StoragePoolMXBean storagePool : serverPool.getStoragePools()) {
            Map<String, String> props = storagePool.getAttributes();
            if (props != null) {
                String isDefault = props.get("default");
                targetStoragePool = storagePool;
                break;
            }
        }
        if (targetStoragePool == null) {
            targetStoragePool = serverPool.getStoragePools().get(0);
            ClusterManagerImpl.logger.warn("No default storage pool for server pool " + serverPool.getName()
                + ", defaulting to storage pool " + targetStoragePool.getName());
        }
        diskSpec.setStoragePool(targetStoragePool);
        configSpec.setDiskSpecs(Collections.singletonList(diskSpec));

        // add default empty cdrom drive
        VirtualCdromSpec cdromSpec = new VirtualCdromSpec();
        cdromSpec.setIsoVolume(null);
        configSpec.setCdromSpecs(Collections.singletonList(cdromSpec));

        VNICSpec vnicSpec = new VNICSpec();
        vnicSpec.setAddressType(MacAddressAssignement.GENERATED);
        vnicSpec.setNetworkName("default");
        configSpec.setVnicSpecs(Collections.singletonList(vnicSpec));

        Map<String, String> vmProps = new HashMap<String, String>();
        vmProps.put("bootDevice", "disk");
        configSpec.setProperties(vmProps);

        //

        ClusterManagerImpl.logger.info("Creating VM " + creationSpec.getName());
        VirtualMachineMXBean vmMBean = null;

        vmMBean = resourcePartition.createVirtualMachine(configSpec, creationSpec.getCustomizationSpec(), startVM, true);
        if (startVM) {
            ClusterManagerImpl.logger.info("Starting VM " + creationSpec.getName());
        } // no "else" needed.

        String uuid = vmMBean.getUuid();
        ClusterManagerImpl.this.vmMBeanTable.put(uuid, vmMBean);
        String hostName = vmMBean.getHostMBean().getHostName();
        if (startVM) {
            ClusterManagerImpl.logger.info("VM " + creationSpec.getName() + " created and started on host " + hostName);
            // retrieve guest OS IP address
            this.waitForVmIPAddress(vmMBean);
        } else {
            ClusterManagerImpl.logger.info("VM " + creationSpec.getName() + " created on host " + hostName);
        }

        return uuid;

    }

    private void waitForVmIPAddress(final VirtualMachineMXBean vmMBean) throws VMMException {
        int waitingTimeInSec = 0;
        GuestInfo guestInfo = null;
        while (waitingTimeInSec < ClusterManagerImpl.OBTAIN_GUEST_IP_ADDRESS_DEFAULT_WAITING_TIME_IN_SECONDS) {
            guestInfo = vmMBean.getGuestInfo();
            if (guestInfo != null && guestInfo.getIpAddresses() != null && guestInfo.getIpAddresses().size() > 0) {
                break;
            }
            try {
                Thread.sleep(ClusterManagerImpl.SLEEPING_TIME_IN_MILLISECONDS_15000);
            } catch (InterruptedException e) {
            }
            waitingTimeInSec += ClusterManagerImpl.SLEEPING_TIME_IN_MILLISECONDS_15000 / 1000;
        }
        if (guestInfo == null || guestInfo.getIpAddresses() == null || guestInfo.getIpAddresses().size() == 0) {
            ClusterManagerImpl.logger.warn("Failed to retrieve IP address of VM " + vmMBean.getNameLabel() + " after "
                + ClusterManagerImpl.OBTAIN_GUEST_IP_ADDRESS_DEFAULT_WAITING_TIME_IN_SECONDS + " seconds");
        }
    }

    @Override
    public synchronized void destroyVirtualMachine(final String providerId) throws VMMException {
        this.checkConnection();
        VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean != null) {
            vmMBean.destroy();

            this.vmMBeanTable.remove(providerId);

        } else {
            ClusterManagerImpl.logger.error("cannot destroy VM with unknow providerId=" + providerId);
        }
    }

    @Override
    public String createImageFromVirtualMachine(final String providerId, final String name, final String description)
        throws VMMException {
        this.checkConnection();
        final VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean == null) {
            ClusterManagerImpl.logger.info("cannot find VM with given providerId");
            throw new VMMException("Cannot find VM with providerId=" + providerId);
        }

        StoragePoolMXBean storagePool = vmMBean.getHostMBean().getServerPool().getStoragePools().get(0);
        Volume volume = vmMBean.makeImage(name, storagePool);

        return storagePool.getPath() + " " + volume.getKey();
    }

    public void destroyVMImage(final String providerId) throws VMMException {
        this.checkConnection();
        this.destroyVMImage(providerId, this.rootDomainMBean);
    }

    private StoragePoolMXBean findStoragePoolMatchingFormat(final String format) throws VMMException {
        for (ResourcePartitionInfo rp : this.onDemandResourcePartitions) {
            for (StoragePoolMXBean pool : rp.getMbean().getRootServerPool().getStoragePools()) {
                if (pool.getAttributes().get("default") == null) {
                    for (String poolSupportFormat : pool.getSupportedDiskImageFormats()) {
                        if (poolSupportFormat.equalsIgnoreCase(format)) {
                            return pool;
                        }
                    }
                }
            }
        }
        return null;
    }

    private StoragePoolMXBean findStoragePool(final String hypervisor, final String location) throws VMMException {
        for (ResourcePartitionInfo rp : this.onDemandResourcePartitions) {
            if (location == null || (rp.getLocation() != null && rp.getLocation().equals(location))) {
                ServerPoolMXBean serverPool = rp.getMbean().getRootServerPool();
                if (hypervisor == null || serverPool.getHypervisor().equals(hypervisor)) {
                    for (StoragePoolMXBean pool : serverPool.getStoragePools()) {
                        String prop = pool.getAttributes().get("default");
                        if (prop != null && prop.equalsIgnoreCase("true")) {
                            return pool;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String uploadVMImage(final String url, final String format, final String name, final String description,
        final String checkSum) throws VMMException {
        this.checkConnection();
        this.getResourceTree();
        final StoragePoolMXBean storagePool = this.findStoragePoolMatchingFormat(format);
        if (storagePool == null) {
            throw new VMMException("Cannot find storage pool supporting " + format + " format");
        }

        // TODO move the following code to "non event mode".

        Volume volume = storagePool.uploadVolume(url, format, name, description, checkSum);
        String providerId = storagePool.getPath() + " " + volume.getKey();
        // ClusterManagerImpl.this.emitMessage(new
        // VMImageUploadCompletionEvent(url, providerId));
        return providerId;
    }

    private boolean destroyVMImage(final String providerId, final DomainMXBean domain) throws VMMException {

        for (DomainMXBean subDomain : domain.getSubDomains()) {
            if (this.destroyVMImage(providerId, subDomain)) {
                break;
            }
        }
        for (ServerPoolMXBean sp : domain.getServerPools()) {
            VirtualMachineImageStoreMXBean dStore = sp.getVMImageStore();
            VirtualMachineImageMXBean image;

            try {
                image = dStore.lookUpByUUID(providerId);
            } catch (Exception ex) {
                continue;
            }

            if (image != null) {
                dStore.removeVMImageTemplate(image);
                return true;
            }
        }
        return false;
    }

    @Override
    public String createVolume(final VolumeSpec spec) throws VMMException {
        // This is the volume's providerId.
        String result = null;

        this.checkConnection();
        this.getResourceTree();

        String hypervisor = spec.getConstraints() != null ? spec.getConstraints().get("hypervisor") : null;
        String location = spec.getConstraints() != null ? spec.getConstraints().get("location") : null;

        StoragePoolMXBean storagePool = this.findStoragePool(hypervisor, location);
        if (storagePool == null) {
            throw new VMMException("Cannot find storage pool matching hypervisor=" + hypervisor + " location=" + location);
        }
        String format = null;
        for (String f : storagePool.getSupportedDiskImageFormats()) {
            if (!f.equalsIgnoreCase("iso")) {
                format = f;
                break;
            }
        }
        Volume vol = storagePool.createVolume(spec.getName(), spec.getCapacityInMB(), format);
        String providerId = storagePool.getPath() + " " + vol.getKey();
        return providerId;
    }

    @Override
    public void destroyVolume(final String volProviderId) throws VMMException {
        this.checkConnection();
        VolumeInfo volInfo = new VolumeInfo(volProviderId);
        volInfo.storagePool.destroyVolume(volInfo.volumeKey);
    }

    @Override
    public void attachVolumeToVM(final String vmProviderId, final String volumeId) throws VMMException {
        this.checkConnection();
        VolumeInfo volInfo = new VolumeInfo(volumeId);
        VirtualMachineMXBean vm = this.getVirtualMachineMBean(vmProviderId);
        if (vm == null) {
            throw new VMMException("Cannot find VM " + vmProviderId);
        }
        if (volInfo.volume.getFormat().equals("iso")) {
            List<VirtualCdrom> cdroms = vm.getVirtualCdroms();
            if (cdroms == null || cdroms.size() == 0) {
                throw new VMMException("Cannot find CD-ROM drive in VM " + vm.getNameLabel());
            }
            vm.changeCdromMedia(cdroms.get(0).getDevice(), volInfo.volume);

        } else {
            VirtualDiskSpec diskSpec = new VirtualDiskSpec();
            diskSpec.setDiskOp(DiskOperation.ATTACH);
            diskSpec.setVolume(volInfo.volume);
            vm.attachVolume(diskSpec);
        }
    }

    @Override
    public void detachVolumeFromVM(final String vmProviderId, final String volumeId) throws VMMException {
        this.checkConnection();
        VolumeInfo volInfo = new VolumeInfo(volumeId);
        VirtualMachineMXBean vm = this.getVirtualMachineMBean(vmProviderId);
        if (vm == null) {
            throw new VMMException("Cannot find VM " + vmProviderId);
        }
        if (volInfo.volume.getFormat().equals("iso")) {
            List<VirtualCdrom> cdroms = vm.getVirtualCdroms();
            if (cdroms == null || cdroms.size() == 0) {
                throw new VMMException("Cannot find CD-ROM drive in VM " + vm.getNameLabel());
            }
            vm.changeCdromMedia(cdroms.get(0).getDevice(), null);
        } else {
            vm.detachVolume(volInfo.volumeKey);
        }
    }

    @Override
    public List<String> getVMVolumes(final String vmProviderId) throws VMMException {
        VirtualMachineMXBean vm = this.getVirtualMachineMBean(vmProviderId);
        if (vm == null) {
            throw new VMMException("Cannot find VM " + vmProviderId);
        }
        List<VirtualDisk> disks = vm.getVirtualDisks();
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < disks.size(); i++) {
            Volume vol = disks.get(i).getVolume();
            result.add(vol.getStoragePool().getPath() + " " + vol.getKey());
        }
        return result;
    }

    @Override
    public List<Host> getHostRefFromServerPool(final String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResourcePartitionMXBean getResourcePartition(final AllocationMode allocationMode, final String hypervisorType,
        final String location) {
        this.checkConnection();
        this.getResourceTree();
        List<ResourcePartitionInfo> rpInfos = allocationMode == AllocationMode.ON_DEMAND ? this.onDemandResourcePartitions
            : this.advancedReservationResourcePartitions;
        for (ResourcePartitionInfo info : rpInfos) {
            if (!info.getHypervisor().equalsIgnoreCase(hypervisorType)) {
                continue;
            }
            if (location == null || location.equals(info.getLocation())) {
                return info.getMbean();
            }
        }
        return null;
    }

    @Override
    public List<ResourcePartitionInfo> getResourcePartitions() {
        this.checkConnection();
        this.getResourceTree();
        List<ResourcePartitionInfo> result = new ArrayList<ResourcePartitionInfo>(this.onDemandResourcePartitions);
        result.addAll(this.advancedReservationResourcePartitions);
        return result;
    }

    @Override
    public List<String> getLocations() {
        this.checkConnection();
        if (this.locations == null) {
            this.locations = new ArrayList<String>();
            this.collectLocations(this.rootDomainMBean, this.locations);
        }
        return this.locations;
    }

    private void collectLocations(final DomainMXBean domain, final List<String> locs) {
        String loc = domain.getAttributes().get("location");
        if (loc != null && !loc.equals("")) {
            locs.add(loc);
        }
        for (DomainMXBean subDomain : domain.getSubDomains()) {
            this.collectLocations(subDomain, locs);
        }
    }

    private ObjectName hostPathToObjectName(final String path) throws MalformedObjectNameException {
        String hostName = path.substring(path.lastIndexOf('/') + 1);
        return new ObjectName("org.ow2.sirocco.vmm.api:type=Host,name=" + path + ",hostname=" + hostName);
    }

    private Host buildHost(final HostMXBean hostMBean) {
        Host hostVO = new Host();
        hostVO.setId(hostMBean.getObjectName().getKeyProperty("name"));

        try {
            hostVO.setHostName(hostMBean.getHostName());
            hostVO.setNumCPUs(hostMBean.getNumCPU());
            hostVO.setCpuInfo(hostMBean.getCPUInfo());
            hostVO.setHypervisorInfo(hostMBean.getHypervisorInfo());
            int cpuSpeedMHz = Integer.parseInt(hostVO.getCpuInfo().get("speedMHz"));
            hostVO.setCpuCapacityMHz(cpuSpeedMHz * hostVO.getNumCPUs());
            hostVO.setCpuCoreCapacity(hostVO.getNumCPUs());
            hostVO.setMemoryCapacityMB((int) hostMBean.getTotalMemoryMB());
            hostVO.setStorageCapacityMB((int) hostMBean.getServerPool().getStorageCapacityMB());
        } catch (VMMException ex) {
            ClusterManagerImpl.logger.error("Cannot init HostVO", ex);
        }

        return hostVO;
    }

    private void walkResourcePartition(final ResourcePartitionMXBean partition, final String hypervisor, final String location) {
        String path = partition.getPath();
        if (ClusterManagerImpl.RESOURCE_PARTITION_ON_DEMAND_USAGE.equals(partition.getProperties().get(
            ClusterManagerImpl.RESOURCE_PARTITION_USAGE_PROPERTY))) {
            ResourcePartitionInfo rpInfo = new ResourcePartitionInfo(AllocationMode.ON_DEMAND, partition, path, hypervisor,
                location);
            this.onDemandResourcePartitions.add(rpInfo);
            ClusterManagerImpl.logger.info("Adding on-demand " + rpInfo);
        }
        if (ClusterManagerImpl.RESOURCE_PARTITION_ADVANCED_RESERVATION_USAGE.equals(partition.getProperties().get(
            ClusterManagerImpl.RESOURCE_PARTITION_USAGE_PROPERTY))) {
            ResourcePartitionInfo rpInfo = new ResourcePartitionInfo(AllocationMode.ADVANCED_RESERVATION, partition, path,
                hypervisor, location);
            this.advancedReservationResourcePartitions.add(rpInfo);
            ClusterManagerImpl.logger.info("Adding advanced reservation " + rpInfo);
        }
        for (ResourcePartitionMXBean child : partition.getSubResourcePartitions()) {
            this.walkResourcePartition(child, hypervisor, location);
        }
    }

    @Override
    public Map<String, ServerPoolMXBean> getPoolMBeanTable() {
        return this.poolMBeanTable;
    }

    private ServerPool buildServerPool(final ServerPoolMXBean poolMBean, final String location) {
        this.poolMBeanTable.put(poolMBean.getObjectName().toString(), poolMBean);
        ServerPool poolVO = new ServerPool();
        poolVO.setId(poolMBean.getObjectName().toString());
        poolVO.setName(poolMBean.getName());
        for (HostMXBean host : poolMBean.getManagedHosts()) {
            Host hostVO = this.buildHost(host);
            poolVO.addHost(hostVO);
            this.addHostMBean(host, host.getObjectName());
            poolVO.setCpuCapacityMHz(poolVO.getCpuCapacityMHz() + hostVO.getCpuCapacityMHz());
            poolVO.setCpuCoreCapacity(poolVO.getCpuCoreCapacity() + hostVO.getCpuCoreCapacity());
            poolVO.setMemoryCapacityMB(poolVO.getMemoryCapacityMB() + hostVO.getMemoryCapacityMB());
        }
        String hypervisor = poolMBean.getHypervisor();
        this.walkResourcePartition(poolMBean, hypervisor, location);
        poolVO.setStorageCapacityMB((int) poolMBean.getStorageCapacityMB());
        return poolVO;
    }

    private Domain buildDomain(final DomainMXBean domainMBean) {
        Domain domain = new Domain();
        domain.setId(domainMBean.getObjectName().toString());
        domain.setName(domainMBean.getName());
        String location = domainMBean.getAttributes().get("location");
        for (DomainMXBean subDomain : domainMBean.getSubDomains()) {
            Domain subDomainVO = this.buildDomain(subDomain);
            domain.addSubDomain(subDomainVO);
            domain.setCpuCapacityMHz(domain.getCpuCapacityMHz() + subDomainVO.getCpuCapacityMHz());
            domain.setCpuCoreCapacity(domain.getCpuCoreCapacity() + subDomainVO.getCpuCoreCapacity());
            domain.setMemoryCapacityMB(domain.getMemoryCapacityMB() + subDomainVO.getMemoryCapacityMB());
            domain.setStorageCapacityMB(domain.getStorageCapacityMB() + subDomainVO.getStorageCapacityMB());
        }
        for (ServerPoolMXBean pool : domainMBean.getServerPools()) {
            ServerPool poolVO = this.buildServerPool(pool, location);
            domain.addServerPool(poolVO);
            domain.setCpuCapacityMHz(domain.getCpuCapacityMHz() + poolVO.getCpuCapacityMHz());
            domain.setCpuCoreCapacity(domain.getCpuCoreCapacity() + poolVO.getCpuCoreCapacity());
            domain.setMemoryCapacityMB(domain.getMemoryCapacityMB() + poolVO.getMemoryCapacityMB());
            domain.setStorageCapacityMB(domain.getStorageCapacityMB() + poolVO.getStorageCapacityMB());
        }
        return domain;
    }

    private void updateHostResourceUsage(final Host host) {
        try {
            HostMXBean hostMBean = this.getHostMBean(host.getId());
            if (hostMBean != null) {
                int numCpuCoreAllocated = 0;
                for (VirtualMachineMXBean vm : hostMBean.getResidentVMs()) {
                    numCpuCoreAllocated += vm.getNumVCPUs();
                }
                host.setCpuCoreAllocated(numCpuCoreAllocated);
                host.setCpuAllocatedMHz(numCpuCoreAllocated * Integer.parseInt(host.getCpuInfo().get("speedMHz")));
                host.setMemoryAllocatedMB((int) (hostMBean.getTotalMemoryMB() - hostMBean.getFreeMemoryMB()));
                host.setStorageAllocatedMB((int) (hostMBean.getServerPool().getStorageCapacityMB() - hostMBean.getServerPool()
                    .getFreeStorageMBCapacity()));
            }
        } catch (Exception ex) {
            ClusterManagerImpl.logger.error("Cannot update HostVO resource usage", ex);
        }
    }

    private void updateServerPoolResourceUsage(final ServerPool pool) {
        int usedCpuCore = 0;
        int usedCpuMHz = 0;
        int usedStorageMB = 0;
        int usedMemoryMB = 0;
        for (Host host : pool.getHosts()) {
            this.updateHostResourceUsage(host);
            usedCpuCore += host.getCpuCoreAllocated();
            usedCpuMHz += host.getCpuAllocatedMHz();
            usedMemoryMB += host.getMemoryAllocatedMB();
        }
        ServerPoolMXBean poolMBean = this.getServerPoolMBean(pool.getId());
        if (poolMBean != null) {
            usedStorageMB = (int) (poolMBean.getStorageCapacityMB() - poolMBean.getFreeStorageMBCapacity());
        }
        pool.setCpuAllocatedMHz(usedCpuMHz);
        pool.setCpuCoreAllocated(usedCpuCore);
        pool.setMemoryAllocatedMB(usedMemoryMB);
        pool.setStorageAllocatedMB(usedStorageMB);
    }

    private void updateDomainResourceUsage(final Domain domain) {
        int usedCpuCore = 0;
        int usedCpuMHz = 0;
        int usedStorageMB = 0;
        int usedMemoryMB = 0;
        for (Domain subDomain : domain.getSubDomains()) {
            this.updateDomainResourceUsage(subDomain);
            usedCpuCore += subDomain.getCpuCoreAllocated();
            usedCpuMHz += subDomain.getCpuAllocatedMHz();
            usedMemoryMB += subDomain.getMemoryAllocatedMB();
            usedStorageMB += subDomain.getStorageAllocatedMB();
        }
        for (ServerPool pool : domain.getServerPools()) {
            this.updateServerPoolResourceUsage(pool);
            usedCpuCore += pool.getCpuCoreAllocated();
            usedCpuMHz += pool.getCpuAllocatedMHz();
            usedMemoryMB += pool.getMemoryAllocatedMB();
            usedStorageMB += pool.getStorageAllocatedMB();
        }
        domain.setCpuAllocatedMHz(usedCpuMHz);
        domain.setCpuCoreAllocated(usedCpuCore);
        domain.setMemoryAllocatedMB(usedMemoryMB);
        domain.setStorageAllocatedMB(usedStorageMB);
    }

    private synchronized void updateDataCenterResourceUsage() {
        this.checkConnection();
        if (this.rootDomain != null) {
            this.updateDomainResourceUsage(this.rootDomain);
            // this.emitMessage(new ResourceUsageEvent(this.rootDomain));
        }
    }

    @Override
    public synchronized Domain getResourceTree() {
        if (this.rootDomain == null) {
            this.checkConnection();
            this.rootDomain = this.buildDomain(this.rootDomainMBean);
            this.updateDomainResourceUsage(this.rootDomain);
            ClusterManagerImpl.logger.info("DataCenter tree: \n" + this.rootDomain);
        }
        return this.rootDomain;
    }

    @Override
    public List<ServerPool> getServerPools() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getVirtualMachineCPULoad(final String providerId) {
        this.checkConnection();
        VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean != null) {
            try {
                return vmMBean.getCPULoad();
            } catch (VMMException ex) {
                ClusterManagerImpl.logger.error("Cannot retrieve CPU load", ex);
            }
        }
        return 0;
    }

    @Override
    public String getVirtualMachineConsole(final String providerId) throws VMMException {
        this.checkConnection();
        VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean == null) {
            ClusterManagerImpl.logger.info("getVirtualMachineConsole: cannot find VM with given providerId");
            return null;
        }
        return vmMBean.getConsole();
    }

    @Override
    public long getVirtualMachineMemoryUsedMB(final String providerId) {
        this.checkConnection();
        VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean != null) {
            try {
                return vmMBean.getMemoryUsedMB();
            } catch (VMMException ex) {
                ClusterManagerImpl.logger.error("getMemoryUsedMB failed", ex);
            }
        }
        return 0;
    }

    @Override
    public List<String> getVirtualMachineRefByHost(final String hostId) {
        List<String> result = new ArrayList<String>();
        this.checkConnection();
        HostMXBean host = this.getHostMBean(hostId);
        if (host == null) {
            ClusterManagerImpl.logger.error("Cannot retrieve MXBean for host " + hostId);
        } else {
            try {
                List<VirtualMachineMXBean> vms = host.getResidentVMs();
                for (int i = 0; i < vms.size(); i++) {
                    result.add(vms.get(i).getUuid());
                }
            } catch (VMMException ex) {
                ClusterManagerImpl.logger.error("Failed to retrieve VM from host" + hostId, ex);
            }
        }
        return result;
    }

    @Override
    public Map<String, Long> getVirtualMachineSchedulingParams(final String providerId) {
        HashMap<String, Long> result = new HashMap<String, Long>();
        VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean != null) {
            try {
                result.put("cap", Long.valueOf(vmMBean.getSchedulingCap()));
                result.put("weight", Long.valueOf(vmMBean.getSchedulingCap()));
            } catch (Exception ex) {
            }
        }
        return result;
    }

    @Override
    public void migrateVirtualMachine(final String providerId, final String destinationHostId) throws VMMException {
        this.checkConnection();
        VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean == null) {
            ClusterManagerImpl.logger.info("migrateVM: cannot find VM with given providerId");
            throw new VMMException("VM not found");
        }
        HostMXBean targetHost = this.getHostMBean(destinationHostId);
        if (targetHost == null) {
            ClusterManagerImpl.logger.error("Cannot retrieve MXBean for host " + destinationHostId);
            throw new VMMException("Host not found");
        }
        vmMBean.migrate(targetHost, true);
    }

    @Override
    public void pauseVirtualMachine(final String providerId) throws VMMException {
        this.checkConnection();
        VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean != null) {
            vmMBean.pause();
        } else {
            throw new VMMException("VM not found");
        }
    }

    @Override
    public void rebootVirtualMachine(final String providerId) throws VMMException {
        this.checkConnection();
        VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean != null) {
            vmMBean.reboot();
        } else {
            throw new VMMException("VM not found");
        }
    }

    @Override
    public void startVirtualMachine(final String providerId) throws VMMException {
        this.checkConnection();
        VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean != null) {
            vmMBean.start();
        } else {
            throw new VMMException("VM not found");
        }
    }

    @Override
    public void stopVirtualMachine(final String providerId) throws VMMException {
        this.checkConnection();
        VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean != null) {
            vmMBean.shutdown();
        } else {
            throw new VMMException("VM not found");
        }
    }

    @Override
    public void unpauseVirtualMachine(final String providerId) throws VMMException {
        this.checkConnection();
        VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean != null) {
            vmMBean.unpause();
        } else {
            throw new VMMException("VM not found");
        }
    }

    @Override
    public void suspendVirtualMachine(final String providerId) throws VMMException {
        this.checkConnection();
        VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean != null) {
            vmMBean.suspend();
        } else {
            throw new VMMException("VM not found");
        }
    }

    @Override
    public void resumeVirtualMachine(final String providerId) throws VMMException {
        this.checkConnection();
        VirtualMachineMXBean vmMBean = this.getVirtualMachineMBean(providerId);
        if (vmMBean != null) {
            vmMBean.resume();
        } else {
            throw new VMMException("VM not found");
        }
    }

    @Override
    public PerfMetricInfo[] getHostAvailablePerfMetrics(final String hostId) throws VMMException {
        HostMXBean host = this.getHostMBean(hostId);
        return host.listPerfMetricInfos();
    }

    @Override
    public PerfMetricInfo[] getVirtualMachineAvailablePerfMetrics(final String vmRef) throws VMMException {
        this.checkConnection();
        VirtualMachineMXBean vm = this.getVirtualMachineMBean(vmRef);
        return vm.listPerfMetricInfos();
    }

    @Override
    public PerfMetric[] getHostPerfMetrics(final String hostPath, final String metricName, final Date startTime,
        final Date endTime) throws Exception {
        this.checkConnection();
        if (hostPath == null) {
            throw new IllegalArgumentException("hostPath argument is null");
        }
        HostMXBean host = this.getHostMBean(hostPath);
        try {
            if (host != null) {
                return host.getPerfMetrics(metricName, startTime, endTime, ClusterManagerImpl.RESOLUTION_IN_MILLISECONDS_10,
                    ConsolidationFunction.AVERAGE);
            }
        } catch (Exception e) {
            ClusterManagerImpl.logger.error("Failed to retrieve host perf metrics :", e);
            return null;
        }
        return null;
    }

    @Override
    public PerfMetric[] getVirtualMachinePerfMetrics(final String vmRef, final String metricName, final Date startTime,
        final Date endTime) throws Exception {
        this.checkConnection();
        VirtualMachineMXBean vm = this.getVirtualMachineMBean(vmRef);
        if (vm != null) {
            return vm.getPerfMetrics(metricName, startTime, endTime, ClusterManagerImpl.RESOLUTION_IN_MILLISECONDS_10,
                ConsolidationFunction.AVERAGE);
        } else {
            throw new Exception("vm not found");
        }
    }

    @Override
    public PerfMetric[] getServerPoolPerfMetrics(final String id, final String metricName, final Date startTime,
        final Date endTime) throws Exception {
        ClusterManagerImpl.logger.info("called");
        this.checkConnection();
        if (id == null) {
            ClusterManagerImpl.logger.error("Server pool doesn't exist (id=null)");
            return null;
        }
        try {
            ServerPoolMXBean sp = this.getServerPoolMBean(id);

            if (sp != null) {
                return sp.getPerfMetrics(metricName, startTime, endTime, ClusterManagerImpl.RESOLUTION_IN_MILLISECONDS_10,
                    ConsolidationFunction.AVERAGE);
            }
        } catch (Exception e) {
            ClusterManagerImpl.logger.error("Server pool doesn't exist :", e);
            return null;
        }
        return null;
    }

}
