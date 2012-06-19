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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.connector.api.IImageService;
import org.ow2.sirocco.cloudmanager.connector.api.INetworkService;
import org.ow2.sirocco.cloudmanager.connector.api.ISystemService;
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDiskCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterface.InterfaceState;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterfaceMachine;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;
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
    INetworkService, IImageService {

    private static Log logger = LogFactory.getLog(MockCloudProviderConnector.class);

    private static final int ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS = 0;

    private final String cloudProviderId;

    private final CloudProviderAccount cloudProviderAccount;

    private final CloudProviderLocation cloudProviderLocation;

    private final MockCloudProviderConnectorFactory mockCloudProviderConnectorFactory;

    private Map<String, Volume> volumes = new ConcurrentHashMap<String, Volume>();

    private Map<String, VolumeImage> volumeImages = new ConcurrentHashMap<String, VolumeImage>();

    private Map<String, Machine> machines = new ConcurrentHashMap<String, Machine>();

    private Map<String, Network> networks = new ConcurrentHashMap<String, Network>();

    private Map<String, NetworkPort> networkPorts = new ConcurrentHashMap<String, NetworkPort>();

    private Map<String, ForwardingGroup> forwardingGroups = new ConcurrentHashMap<String, ForwardingGroup>();

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
    public INetworkService getNetworkService() throws ConnectorException {
        return this;
    }

    @Override
    public synchronized Job createVolume(final VolumeCreate volumeCreate) throws ConnectorException {
        final String volumeProviderAssignedId = UUID.randomUUID().toString();
        final Volume volume = new Volume();
        volume.setProviderAssignedId(volumeProviderAssignedId);
        Disk capacity = new Disk();
        capacity.setQuantity(volumeCreate.getVolumeTemplate().getVolumeConfig().getCapacity().getQuantity());
        capacity.setUnit(volumeCreate.getVolumeTemplate().getVolumeConfig().getCapacity().getUnits());
        volume.setCapacity(capacity);
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

        if (machineCreate.getMachineTemplate().getNetworkInterfaces() != null) {
            for (NetworkInterface networkInterface : machineCreate.getMachineTemplate().getNetworkInterfaces()) {
                NetworkInterfaceMachine newNetIntf = new NetworkInterfaceMachine();
                // TODO
                newNetIntf.setAddresses(networkInterface.getAddresses());

                newNetIntf.setMacAddress("00:11:22:33:44:55");

                newNetIntf.setState(InterfaceState.STANDBY);

                newNetIntf.setNetworkType(networkInterface.getNetworkType());

                machine.addNetworkInterface(newNetIntf);
            }
        }

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
        volumeCollection.setItems(new ArrayList<MachineVolume>());
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

    @Override
    public Job createNetwork(final NetworkCreate networkCreate) throws ConnectorException {
        final ForwardingGroup fg;
        if (networkCreate.getNetworkTemplate().getForwardingGroup() != null) {
            String forwardingGroupId = networkCreate.getNetworkTemplate().getForwardingGroup().getProviderAssignedId();
            fg = this.forwardingGroups.get(forwardingGroupId);
            if (fg == null) {
                throw new ConnectorException("Unknown forwarding group with id=" + forwardingGroupId);
            }
        } else {
            fg = null;
        }
        final String networkProviderAssignedId = UUID.randomUUID().toString();
        final Network network = new Network();
        network.setClassOfService(networkCreate.getNetworkTemplate().getNetworkConfig().getClassOfService());
        network.setMtu(networkCreate.getNetworkTemplate().getNetworkConfig().getMtu());
        network.setProviderAssignedId(networkProviderAssignedId);
        network.setForwardingGroup(fg);
        network.setNetworkPorts(new ArrayList<NetworkPort>());
        this.networks.put(networkProviderAssignedId, network);
        network.setState(Network.State.CREATING);

        final Callable<Network> createTask = new Callable<Network>() {
            @Override
            public Network call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                network.setState(Network.State.STARTED);
                if (fg != null) {
                    fg.getNetworks().add(network);
                }
                return network;
            }
        };

        ListenableFuture<Network> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(createTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(network, null, "add", result);
    }

    @Override
    public Network getNetwork(final String networkId) throws ConnectorException {
        return this.networks.get(networkId);
    }

    @Override
    public Job deleteNetwork(final String networkId) throws ConnectorException {
        MockCloudProviderConnector.logger.info("Deleting network with providerAssignedId " + networkId);
        Network network = this.networks.get(networkId);
        if (network == null) {
            throw new ConnectorException("Network " + networkId + " doesn't exist");
        }
        network.setState(Network.State.DELETING);
        final Callable<Void> deleteTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                MockCloudProviderConnector.this.networks.remove(networkId);
                MockCloudProviderConnector.logger.info("Network " + networkId + " deleted");
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(deleteTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(network, null, "delete", result);
    }

    @Override
    public Job startNetwork(final String networkId) throws ConnectorException {
        MockCloudProviderConnector.logger.info("Starting network with providerAssignedId " + networkId);
        final Network network = this.networks.get(networkId);
        if (network == null) {
            throw new ConnectorException("Network " + networkId + " doesn't exist");
        }
        if (network.getState() != Network.State.STOPPED) {
            throw new ConnectorException("Illegal operation");
        }
        network.setState(Network.State.STARTING);
        final Callable<Void> deleteTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                network.setState(Network.State.STARTED);
                MockCloudProviderConnector.logger.info("Network " + networkId + " started");
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(deleteTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(network, null, "start", result);
    }

    @Override
    public Job stopNetwork(final String networkId) throws ConnectorException {
        MockCloudProviderConnector.logger.info("Stopping network with providerAssignedId " + networkId);
        final Network network = this.networks.get(networkId);
        if (network == null) {
            throw new ConnectorException("Network " + networkId + " doesn't exist");
        }
        if (network.getState() != Network.State.STARTED) {
            throw new ConnectorException("Illegal operation");
        }
        network.setState(Network.State.STOPPING);
        final Callable<Void> deleteTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                network.setState(Network.State.STOPPED);
                MockCloudProviderConnector.logger.info("Network " + networkId + " stopped");
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(deleteTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(network, null, "stop", result);
    }

    @Override
    public Job createNetworkPort(final NetworkPortCreate networkPortCreate) throws ConnectorException {
        if (networkPortCreate.getNetworkPortTemplate().getNetwork() == null) {
            throw new ConnectorException("Wrong network port template: null network");
        }
        final Network network;
        String networkId = networkPortCreate.getNetworkPortTemplate().getNetwork().getProviderAssignedId();
        network = this.networks.get(networkId);
        if (network == null) {
            throw new ConnectorException("Unknown network with id=" + networkId);
        }
        final String networkPortProviderAssignedId = UUID.randomUUID().toString();
        final NetworkPort networkPort = new NetworkPort();
        networkPort.setClassOfService(networkPortCreate.getNetworkPortTemplate().getNetworkPortConfig().getClassOfService());
        networkPort.setPortType(networkPortCreate.getNetworkPortTemplate().getNetworkPortConfig().getPortType());
        networkPort.setProviderAssignedId(networkPortProviderAssignedId);
        this.networkPorts.put(networkPortProviderAssignedId, networkPort);
        networkPort.setState(NetworkPort.State.CREATING);

        final Callable<NetworkPort> createTask = new Callable<NetworkPort>() {
            @Override
            public NetworkPort call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                networkPort.setState(NetworkPort.State.STARTED);
                networkPort.setNetwork(network);
                network.getNetworkPorts().add(networkPort);
                return networkPort;
            }
        };

        ListenableFuture<NetworkPort> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(createTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(networkPort, null, "add", result);
    }

    @Override
    public NetworkPort getNetworkPort(final String networkPortId) throws ConnectorException {
        return this.networkPorts.get(networkPortId);
    }

    @Override
    public Job deleteNetworkPort(final String networkPortId) throws ConnectorException {
        MockCloudProviderConnector.logger.info("Deleting network port with providerAssignedId " + networkPortId);
        NetworkPort networkPort = this.networkPorts.get(networkPortId);
        if (networkPort == null) {
            throw new ConnectorException("NetworkPort " + networkPortId + " doesn't exist");
        }
        networkPort.setState(NetworkPort.State.DELETING);
        final Callable<Void> deleteTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                MockCloudProviderConnector.this.networkPorts.remove(networkPortId);
                MockCloudProviderConnector.logger.info("NetworkPort " + networkPortId + " deleted");
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(deleteTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(networkPort, null, "delete", result);
    }

    @Override
    public Job startNetworkPort(final String networkPortId) throws ConnectorException {
        MockCloudProviderConnector.logger.info("Starting network port with providerAssignedId " + networkPortId);
        final NetworkPort networkPort = this.networkPorts.get(networkPortId);
        if (networkPort == null) {
            throw new ConnectorException("Network " + networkPortId + " doesn't exist");
        }
        if (networkPort.getState() != NetworkPort.State.STOPPED) {
            throw new ConnectorException("Illegal operation");
        }
        networkPort.setState(NetworkPort.State.STARTING);
        final Callable<Void> deleteTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                networkPort.setState(NetworkPort.State.STARTED);
                MockCloudProviderConnector.logger.info("NetworkPort " + networkPortId + " started");
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(deleteTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(networkPort, null, "start", result);
    }

    @Override
    public Job stopNetworkPort(final String networkPortId) throws ConnectorException {
        MockCloudProviderConnector.logger.info("Stopping network port with providerAssignedId " + networkPortId);
        final NetworkPort networkPort = this.networkPorts.get(networkPortId);
        if (networkPort == null) {
            throw new ConnectorException("Network " + networkPortId + " doesn't exist");
        }
        if (networkPort.getState() != NetworkPort.State.STARTED) {
            throw new ConnectorException("Illegal operation");
        }
        networkPort.setState(NetworkPort.State.STOPPING);
        final Callable<Void> deleteTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                networkPort.setState(NetworkPort.State.STOPPED);
                MockCloudProviderConnector.logger.info("NetworkPort " + networkPortId + " stopped");
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(deleteTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(networkPort, null, "stop", result);
    }

    @Override
    public Job createForwardingGroup(final ForwardingGroupCreate forwardingGroupCreate) throws ConnectorException {
        final List<Network> networksToAdd = new ArrayList<Network>();
        if (forwardingGroupCreate.getForwardingGroupTemplate().getNetworks() != null) {
            for (Network net : forwardingGroupCreate.getForwardingGroupTemplate().getNetworks()) {
                String netId = net.getProviderAssignedId();
                Network providerNetwork = this.networks.get(netId);
                if (providerNetwork == null) {
                    throw new ConnectorException("Unknown network with id " + netId);
                }
                networksToAdd.add(providerNetwork);
            }
        }
        final String forwardingGroupProviderAssignedId = UUID.randomUUID().toString();
        final ForwardingGroup forwardingGroup = new ForwardingGroup();
        forwardingGroup.setProviderAssignedId(forwardingGroupProviderAssignedId);
        forwardingGroup.setNetworks(new ArrayList<Network>());
        this.forwardingGroups.put(forwardingGroupProviderAssignedId, forwardingGroup);
        forwardingGroup.setState(ForwardingGroup.State.CREATING);

        final Callable<ForwardingGroup> createTask = new Callable<ForwardingGroup>() {
            @Override
            public ForwardingGroup call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                forwardingGroup.getNetworks().addAll(networksToAdd);
                forwardingGroup.setState(ForwardingGroup.State.AVAILABLE);
                return forwardingGroup;
            }
        };

        ListenableFuture<ForwardingGroup> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(
            createTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(forwardingGroup, null, "add", result);
    }

    @Override
    public ForwardingGroup getForwardingGroup(final String forwardingGroupId) throws ConnectorException {
        return this.forwardingGroups.get(forwardingGroupId);
    }

    @Override
    public Job deleteForwardingGroup(final String forwardingGroupId) throws ConnectorException {
        MockCloudProviderConnector.logger.info("Deleting forwarding group with providerAssignedId " + forwardingGroupId);
        ForwardingGroup forwardingGroup = this.forwardingGroups.get(forwardingGroupId);
        if (forwardingGroup == null) {
            throw new ConnectorException("NetworkPort " + forwardingGroupId + " doesn't exist");
        }
        forwardingGroup.setState(ForwardingGroup.State.DELETING);
        final Callable<Void> deleteTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                MockCloudProviderConnector.this.forwardingGroups.remove(forwardingGroupId);
                MockCloudProviderConnector.logger.info("ForwardingGroup " + forwardingGroupId + " deleted");
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(deleteTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(forwardingGroup, null, "delete", result);
    }

    @Override
    public Job addNetworkToForwardingGroup(final String forwardingGroupId, final String networkId) throws ConnectorException {
        final ForwardingGroup forwardingGroup = this.forwardingGroups.get(forwardingGroupId);
        if (forwardingGroup == null) {
            throw new ConnectorException("NetworkPort " + forwardingGroupId + " doesn't exist");
        }
        final Network network = this.networks.get(networkId);
        if (network == null) {
            throw new ConnectorException("Unknown network with id=" + networkId);
        }
        final Callable<Void> attachTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                forwardingGroup.getNetworks().add(network);
                MockCloudProviderConnector.logger.info("Added network to ForwardingGroup " + forwardingGroupId);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(attachTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(forwardingGroup, network, "add", result);
    }

    @Override
    public Job removeNetworkFromForwardingGroup(final String forwardingGroupId, final String networkId)
        throws ConnectorException {
        final ForwardingGroup forwardingGroup = this.forwardingGroups.get(forwardingGroupId);
        if (forwardingGroup == null) {
            throw new ConnectorException("NetworkPort " + forwardingGroupId + " doesn't exist");
        }
        final Network network = this.networks.get(networkId);
        if (network == null) {
            throw new ConnectorException("Unknown network with id=" + networkId);
        }
        if (!forwardingGroup.getNetworks().contains(network)) {
            throw new ConnectorException("Network with id=" + networkId + " is not a member of forwarding group with id="
                + forwardingGroupId);
        }
        final Callable<Void> attachTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                forwardingGroup.getNetworks().remove(network);
                MockCloudProviderConnector.logger.info("Removed Network from ForwardingGroup " + forwardingGroupId);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(attachTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(forwardingGroup, network, "delete", result);
    }

}
