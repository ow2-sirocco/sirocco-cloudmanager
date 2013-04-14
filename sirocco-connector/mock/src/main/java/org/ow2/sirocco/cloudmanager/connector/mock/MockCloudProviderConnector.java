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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.ow2.sirocco.cloudmanager.connector.api.BadStateException;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.connector.api.IImageService;
import org.ow2.sirocco.cloudmanager.connector.api.INetworkService;
import org.ow2.sirocco.cloudmanager.connector.api.IProviderCapability;
import org.ow2.sirocco.cloudmanager.connector.api.ISystemService;
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.api.IJobManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudCollectionItem;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.Type;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface.InterfaceState;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkNetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.system.ComponentDescriptor;
import org.ow2.sirocco.cloudmanager.model.cimi.system.ComponentDescriptor.ComponentType;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCredentials;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemMachine;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemSystem;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemVolume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

public class MockCloudProviderConnector implements ICloudProviderConnector, IComputeService, ISystemService, IVolumeService,
    INetworkService, IImageService {

    private static Logger logger = LoggerFactory.getLogger(MockCloudProviderConnector.class);

    private static final int ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS = 0;

    private final String cloudProviderId;

    private CloudProviderAccount cloudProviderAccount;

    private CloudProviderLocation cloudProviderLocation;

    private final MockCloudProviderConnectorFactory mockCloudProviderConnectorFactory;

    private Map<String, Volume> volumes = new ConcurrentHashMap<String, Volume>();

    private Map<String, VolumeImage> volumeImages = new ConcurrentHashMap<String, VolumeImage>();

    private Map<String, Machine> machines = new ConcurrentHashMap<String, Machine>();

    private Map<String, System> systems = new ConcurrentHashMap<String, System>();

    private Map<String, Network> networks = new ConcurrentHashMap<String, Network>();

    private Map<String, NetworkPort> networkPorts = new ConcurrentHashMap<String, NetworkPort>();

    private Map<String, ForwardingGroup> forwardingGroups = new ConcurrentHashMap<String, ForwardingGroup>();

    private IProviderCapability capabilities = new MockCloudProviderCapability();

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
    public void setCloudProviderAccount(final CloudProviderAccount cpa) {
        this.cloudProviderAccount = cpa;
    }

    @Override
    public CloudProviderLocation getCloudProviderLocation() {
        return this.cloudProviderLocation;
    }

    @Override
    public void setCloudProviderLocation(final CloudProviderLocation cpl) {
        this.cloudProviderLocation = cpl;
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
    public IProviderCapability getProviderCapability() throws ConnectorException {
        return this.capabilities;
    }

    @Override
    public synchronized Job createVolume(final VolumeCreate volumeCreate) throws ConnectorException {
        final String volumeProviderAssignedId = UUID.randomUUID().toString();
        final Volume volume = new Volume();
        volume.setName(volumeCreate.getName());
        volume.setProviderAssignedId(volumeProviderAssignedId);
        volume.setCapacity(volumeCreate.getVolumeTemplate().getVolumeConfig().getCapacity());
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
            throw new ConnectorException("Volume " + volumeId + " does not exist");
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
        machine.setName(machineCreate.getName());
        machine.setDescription(machineCreate.getDescription());
        if (machineCreate.getProperties() != null) {
            machine.setProperties(new HashMap<String, String>(machineCreate.getProperties()));
        }
        machine.setState(Machine.State.CREATING);
        machine.setCpu(machineCreate.getMachineTemplate().getMachineConfig().getCpu());
        machine.setMemory(machineCreate.getMachineTemplate().getMachineConfig().getMemory());
        List<MachineDisk> disks = new ArrayList<MachineDisk>();
        if (machineCreate.getMachineTemplate().getMachineConfig().getDisks() != null) {
            for (DiskTemplate diskTemplate : machineCreate.getMachineTemplate().getMachineConfig().getDisks()) {
                MachineDisk mdisk = new MachineDisk();
                mdisk.setCapacity(diskTemplate.getCapacity());
                mdisk.setInitialLocation(diskTemplate.getInitialLocation());
                disks.add(mdisk);
            }
        }

        machine.setDisks(disks);

        if (machineCreate.getMachineTemplate().getNetworkInterfaces() != null) {
            for (MachineTemplateNetworkInterface networkInterface : machineCreate.getMachineTemplate().getNetworkInterfaces()) {
                MachineNetworkInterface newNetIntf = new MachineNetworkInterface();
                // TODO
                if (networkInterface.getAddresses() != null) {
                    List<MachineNetworkInterfaceAddress> addrs = new ArrayList<MachineNetworkInterfaceAddress>();
                    for (Address a : networkInterface.getAddresses()) {
                        MachineNetworkInterfaceAddress entry = new MachineNetworkInterfaceAddress();

                        Address new_a = new Address();
                        a.clone(new_a);
                        new_a.setNetwork(a.getNetwork());
                        entry.setAddress(new_a);
                        addrs.add(entry);
                    }
                    newNetIntf.setAddresses(addrs);

                }

                newNetIntf.setMacAddress("00:11:22:33:44:55");

                newNetIntf.setState(InterfaceState.PASSIVE);

                newNetIntf.setNetworkType(networkInterface.getNetworkType());

                machine.addNetworkInterface(newNetIntf);
            }
        }

        final Callable<Machine> createTask = new Callable<Machine>() {
            @Override
            public Machine call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                if (machine.getNetworkInterfaces() != null) {
                    for (MachineNetworkInterface networkInterface : machine.getNetworkInterfaces()) {
                        networkInterface.setState(InterfaceState.ACTIVE);
                    }
                }
                machine.setState(Machine.State.STOPPED);
                return machine;
            }
        };
        // TODO create and attach volumes

        machine.setVolumes(new ArrayList<MachineVolume>());
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
            throw new BadStateException("Illegal operation");
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
    public synchronized Job stopMachine(final String machineId, final boolean force) throws ConnectorException {
        MockCloudProviderConnector.logger.info("Stopping machine with providerAssignedId " + machineId);
        final Machine machine = this.machines.get(machineId);
        if (machine == null) {
            throw new ConnectorException("Machine " + machineId + " doesn't exist");
        }
        if (machine.getState() == State.CREATING || machine.getState() == State.STOPPED || machine.getState() == State.PAUSING
            || machine.getState() == State.PAUSED || machine.getState() == State.SUSPENDING
            || machine.getState() == State.SUSPENDED || machine.getState() == State.DELETING) {
            throw new BadStateException("Illegal operation");
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
            throw new BadStateException("Illegal operation");
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
    public synchronized Job restartMachine(final String machineId, final boolean force) throws ConnectorException {
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
            throw new BadStateException("Illegal operation");
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
    public Job captureMachine(final String machineId, final MachineImage machineImage) throws ConnectorException {
        final Machine machine = this.machines.get(machineId);
        if (machine == null) {
            throw new ConnectorException("Machine " + machineId + " doesn't exist");
        }
        final MachineImage capturedMachineImage = new MachineImage();
        capturedMachineImage.setName(machineImage.getName());
        capturedMachineImage.setDescription(machineImage.getDescription());
        capturedMachineImage.setType(Type.IMAGE);
        capturedMachineImage.setState(MachineImage.State.CREATING);
        Map<String, String> props = new HashMap<String, String>();
        props.put("mock", "1234");
        capturedMachineImage.setProperties(props);
        final Callable<MachineImage> createTask = new Callable<MachineImage>() {
            @Override
            public MachineImage call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                capturedMachineImage.setState(MachineImage.State.AVAILABLE);
                return machineImage;
            }
        };
        ListenableFuture<MachineImage> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(createTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(capturedMachineImage, machine, "add", result);
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
        if (machine == null) {
            throw new ConnectorException("Machine " + machineId + " does not exist");
        }
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

    private boolean waitForJob(final Job j, final long maxTimeSecond) {
        long time = 0;
        if (j == null) {
            return true;
        }
        while (j.getState().equals(Job.Status.RUNNING)) {
            try {
                time++;
                if (time > maxTimeSecond) {
                    return true;
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        if (j.getState().equals(Status.FAILED) || j.getState().equals(Status.CANCELLED) || j.getState().equals(Status.RUNNING)) {
            return true;
        }
        return false;
    }

    @Override
    public synchronized System getSystem(final String systemId) throws ConnectorException {
        System system = this.systems.get(systemId);
        if (system == null) {
            throw new ConnectorException("System " + systemId + " does not exist");
        }
        return system;
    }

    @Override
    public synchronized List<? extends CloudCollectionItem> getEntityListFromSystem(final String systemId,
        final String entityType) throws ConnectorException {
        System system = this.systems.get(systemId);
        if (system == null) {
            throw new ConnectorException("System " + systemId + " does not exist");
        }
        if (entityType.equals(SystemMachine.class.getName())) {
            return new ArrayList<SystemMachine>(system.getMachines());
        } else if (entityType.equals(SystemVolume.class.getName())) {
            return new ArrayList<SystemVolume>(system.getVolumes());
        } else if (entityType.equals(SystemSystem.class.getName())) {
            return new ArrayList<SystemSystem>(system.getSystems());
        } else if (entityType.equals(SystemNetwork.class.getName())) {
            return new ArrayList<SystemNetwork>(system.getNetworks());
        } else if (entityType.equals(SystemCredentials.class.getName())) {
            return new ArrayList<SystemCredentials>(system.getCredentials());
        } else {
            throw new ConnectorException("object type not owned by a system");
        }
    }

    @Override
    public Job createSystem(final SystemCreate systemCreate) throws ConnectorException {

        // assign a random provider id
        final String systemProviderAssignedId = UUID.randomUUID().toString();
        final System system = new System();
        system.setProviderAssignedId(systemProviderAssignedId);

        system.setState(System.State.CREATING);
        MockCloudProviderConnector.logger.info("Creating system with providerAssignedId " + systemProviderAssignedId);

        IJobManager jobManager = this.mockCloudProviderConnectorFactory.getJobManager();

        // attributes
        system.setDescription(systemCreate.getDescription());
        system.setName(systemCreate.getName());
        system.setMachines(new ArrayList<SystemMachine>());
        system.setVolumes(new ArrayList<SystemVolume>());
        system.setSystems(new ArrayList<SystemSystem>());
        system.setNetworks(new ArrayList<SystemNetwork>());
        system.setCredentials(new ArrayList<SystemCredentials>());
        system.setProperties(new HashMap<String, String>());

        Set<ComponentDescriptor> componentDescriptors = systemCreate.getSystemTemplate().getComponentDescriptors();

        // iterating through descriptors

        boolean failedCancelled = false;

        // create Volumes and Networks first
        Iterator<ComponentDescriptor> iter = componentDescriptors.iterator();
        while (iter.hasNext()) {
            ComponentDescriptor cd = iter.next();

            if (cd.getComponentType() == ComponentType.VOLUME) {
                // creating new volumes
                for (int i = 0; i < cd.getComponentQuantity(); i++) {
                    VolumeCreate vc = new VolumeCreate();
                    if (cd.getComponentQuantity() > 1) {
                        String name = cd.getName() == null ? "" : cd.getName();
                        vc.setName(name + new Integer(i + 1).toString());
                    } else {
                        vc.setName(cd.getName());
                    }
                    VolumeTemplate vt = (VolumeTemplate) cd.getComponentTemplate();
                    vc.setVolumeTemplate(vt);
                    vc.setDescription(cd.getDescription());
                    vc.setProperties(cd.getProperties());

                    // warning:job returned by createXXX is a copy!
                    Job j = jobManager.getJobById(this.createVolume(vc).getProviderAssignedId().toString());
                    failedCancelled = this.waitForJob(j, MockCloudProviderConnector.maxJobTimeInSeconds);
                    if (j.getState().equals(Status.SUCCESS)) {
                        SystemVolume sv = new SystemVolume();
                        sv.setState(SystemVolume.State.AVAILABLE);
                        sv.setResource(j.getTargetResource());
                        system.getVolumes().add(sv);
                    }
                }
            }

            if (cd.getComponentType() == ComponentType.NETWORK) {
                // creating new networks
                for (int i = 0; i < cd.getComponentQuantity(); i++) {
                    NetworkCreate nc = new NetworkCreate();
                    if (cd.getComponentQuantity() > 1) {
                        String name = cd.getName() == null ? "" : cd.getName();
                        nc.setName(name + new Integer(i + 1).toString());
                    } else {
                        nc.setName(cd.getName());
                    }
                    NetworkTemplate nt = (NetworkTemplate) cd.getComponentTemplate();
                    nc.setNetworkTemplate(nt);
                    nc.setDescription(cd.getDescription());
                    nc.setProperties(cd.getProperties());

                    // warning:job returned by createXXX is a copy!
                    Job j = jobManager.getJobById(this.createNetwork(nc).getProviderAssignedId().toString());
                    failedCancelled = this.waitForJob(j, MockCloudProviderConnector.maxJobTimeInSeconds);
                    if (j.getState().equals(Status.SUCCESS)) {
                        SystemNetwork sn = new SystemNetwork();
                        sn.setState(SystemNetwork.State.AVAILABLE);
                        sn.setResource(j.getTargetResource());
                        system.getNetworks().add(sn);
                    }
                }
            }
        }

        // resolve volume component references if any
        for (ComponentDescriptor component : systemCreate.getSystemTemplate().getComponentDescriptors()) {
            if (component.getComponentType() == ComponentType.MACHINE) {
                MachineTemplate machineTemplate = (MachineTemplate) component.getComponentTemplate();
                if (machineTemplate.getVolumes() != null) {
                    for (MachineVolume machineVolume : machineTemplate.getVolumes()) {
                        if (machineVolume.getSystemVolumeName() != null) {
                            MockCloudProviderConnector.logger.info("Resolving volume ref #"
                                + machineVolume.getSystemVolumeName());
                            for (SystemVolume sysVolume : system.getVolumes()) {
                                Volume vol = sysVolume.getVolume();
                                if (vol.getName() != null && vol.getName().equals(machineVolume.getSystemVolumeName())) {
                                    machineVolume.setVolume(vol);
                                    MockCloudProviderConnector.logger.info("Volume ref #" + machineVolume.getSystemVolumeName()
                                        + " resolved");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // resolve network component references if any
        for (ComponentDescriptor component : systemCreate.getSystemTemplate().getComponentDescriptors()) {
            if (component.getComponentType() == ComponentType.MACHINE) {
                MachineTemplate machineTemplate = (MachineTemplate) component.getComponentTemplate();
                for (MachineTemplateNetworkInterface nic : machineTemplate.getNetworkInterfaces()) {
                    if (nic.getSystemNetworkName() != null) {
                        MockCloudProviderConnector.logger.info("Resolving network ref #" + nic.getSystemNetworkName());
                        for (SystemNetwork sysNetwork : system.getNetworks()) {
                            Network net = sysNetwork.getNetwork();
                            if (net.getName() != null && net.getName().equals(nic.getSystemNetworkName())) {
                                nic.setNetwork(net);
                                MockCloudProviderConnector.logger.info("Network ref #" + nic.getSystemNetworkName()
                                    + " resolved");
                                break;
                            }
                        }
                    }
                }
            }
        }

        // create Machines and Systems
        iter = componentDescriptors.iterator();
        while (iter.hasNext()) {
            ComponentDescriptor cd = iter.next();

            if (cd.getComponentType() == ComponentType.MACHINE) {
                // creating new machines
                for (int i = 0; i < cd.getComponentQuantity(); i++) {
                    MachineCreate mc = new MachineCreate();
                    if (cd.getComponentQuantity() > 1) {
                        String name = cd.getName() == null ? "" : cd.getName();
                        mc.setName(name + new Integer(i + 1).toString());
                    } else {
                        mc.setName(cd.getName());
                    }

                    MachineTemplate mt = (MachineTemplate) cd.getComponentTemplate();
                    mc.setMachineTemplate(mt);
                    mc.setDescription(cd.getDescription());
                    mc.setProperties(cd.getProperties());

                    // warning:job returned by createXXX is a copy!
                    Job j = jobManager.getJobById(this.createMachine(mc).getProviderAssignedId().toString());
                    failedCancelled = this.waitForJob(j, MockCloudProviderConnector.maxJobTimeInSeconds);
                    if (j.getState().equals(Status.SUCCESS)) {
                        Machine machine = (Machine) j.getTargetResource();
                        // attach volumes
                        if (mt.getVolumes() != null) {
                            for (MachineVolume machineVolume : mt.getVolumes()) {
                                if (machineVolume.getVolume() == null) {
                                    MockCloudProviderConnector.logger.error("Unresolved MachineVolume ref="
                                        + machineVolume.getSystemVolumeName());
                                    continue;
                                }
                                MachineVolume machineVolume2 = new MachineVolume();
                                machineVolume2.setInitialLocation(machineVolume.getInitialLocation());
                                machineVolume2.setVolume(machineVolume.getVolume());
                                machine.getVolumes().add(machineVolume2);
                            }
                        }
                        // TODO create and attach volumes
                        SystemMachine sm = new SystemMachine();
                        sm.setState(SystemMachine.State.AVAILABLE);
                        sm.setResource(j.getTargetResource());
                        system.getMachines().add(sm);
                    }
                }
            }

            if (cd.getComponentType() == ComponentType.SYSTEM) {
                // creating new systems
                for (int i = 0; i < cd.getComponentQuantity(); i++) {
                    SystemCreate sc = new SystemCreate();
                    if (cd.getComponentQuantity() > 1) {
                        String name = cd.getName() == null ? "" : cd.getName();
                        sc.setName(name + new Integer(i + 1).toString());
                    } else {
                        sc.setName(cd.getName());
                    }
                    SystemTemplate st = (SystemTemplate) cd.getComponentTemplate();
                    sc.setSystemTemplate(st);
                    sc.setDescription(cd.getDescription());
                    sc.setProperties(cd.getProperties());

                    // warning:job returned by createXXX is a copy!
                    Job j = jobManager.getJobById(this.createSystem(sc).getProviderAssignedId().toString());
                    failedCancelled = this.waitForJob(j, MockCloudProviderConnector.maxJobTimeInSeconds);
                    if (j.getState().equals(Status.SUCCESS)) {
                        SystemSystem ss = new SystemSystem();
                        ss.setState(SystemSystem.State.AVAILABLE);
                        ss.setResource(j.getTargetResource());
                        system.getSystems().add(ss);
                    }
                }
            }

        }

        if (failedCancelled) {
            // one or more jobs are failed, so all is failed
            system.setState(System.State.ERROR);
        }

        return this.simulateProviderTask(system, SystemAction.ADD, failedCancelled);
    }

    @Override
    public Job deleteEntityInSystem(final String systemId, final String entityId, final String entityType)
        throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * remove an entity (systemMachine, systemVolume etc) from a system<br>
     * <b><font color=red>Warning:the entityId is the provider id of <i>the
     * underlying resource</i>, not the systemXXX one</font></b>
     */
    @Override
    public Job removeEntityFromSystem(final String systemId, final String entityId) throws ConnectorException {

        System s = this.systems.get(systemId);

        boolean entityFound = false;

        for (SystemMachine sm : s.getMachines()) {
            if (sm.getResource().getProviderAssignedId().equals(entityId)) {
                entityFound = true;
                sm.setState(CloudCollectionItem.State.DELETED);
                s.getMachines().remove(sm);
                break;
            }
        }
        for (SystemVolume sm : s.getVolumes()) {
            if (sm.getResource().getProviderAssignedId().equals(entityId)) {
                entityFound = true;
                sm.setState(CloudCollectionItem.State.DELETED);
                s.getVolumes().remove(sm);
            }
        }
        for (SystemSystem sm : s.getSystems()) {
            if (sm.getResource().getProviderAssignedId().equals(entityId)) {
                entityFound = true;
                sm.setState(CloudCollectionItem.State.DELETED);
                s.getSystems().remove(sm);
            }
        }
        for (SystemNetwork sm : s.getNetworks()) {
            if (sm.getResource().getProviderAssignedId().equals(entityId)) {
                entityFound = true;
                sm.setState(CloudCollectionItem.State.DELETED);
                s.getNetworks().remove(sm);
            }
        }

        if (!entityFound) {
            throw new ConnectorException("entity given " + entityId + " not found in system" + systemId);
        }

        return this.simulateProviderTask(s, SystemAction.ENTITY_REMOVE, false);
    }

    @Override
    public Job addEntityToSystem(final String systemId, final String entityId) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    // private utility methods for System services (start,stop,etc)

    private boolean serviceSystem(final List<? extends CloudCollectionItem> l, final SystemAction action, final boolean force,
        final Map<String, String> properties) throws ConnectorException {
        boolean failedCancelled = false;
        IJobManager jobManager = this.mockCloudProviderConnectorFactory.getJobManager();
        for (CloudCollectionItem m : l) {
            // warning:job returned by createXXX is a copy!
            Job j = jobManager.getJobById(this
                .callSystemService(m.getResource(), action, m.getResource().getProviderAssignedId().toString(), force,
                    properties).getProviderAssignedId().toString());
            failedCancelled = this.waitForJob(j, MockCloudProviderConnector.maxJobTimeInSeconds);
        }
        return failedCancelled;
    }

    private Job callSystemService(final CloudResource ce, final SystemAction action, final String providerId,
        final boolean force, final Map<String, String> properties) throws ConnectorException {
        try {
            if (ce.getClass().equals(Machine.class)) {
                switch (action) {
                case START:
                    return this.startMachine(providerId);
                case STOP:
                    return this.stopMachine(providerId, force);
                case SUSPEND:
                    return this.suspendMachine(providerId);
                case PAUSE:
                    return this.pauseMachine(providerId);
                case RESTART:
                    return this.restartMachine(providerId, force);
                }
            }
            if (ce.getClass().equals(System.class)) {
                switch (action) {
                case START:
                    return this.startSystem(providerId, properties);
                case STOP:
                    return this.stopSystem(providerId, force, properties);
                case SUSPEND:
                    return this.suspendSystem(providerId, properties);
                case PAUSE:
                    return this.pauseSystem(providerId, properties);
                case RESTART:
                    return this.restartSystem(providerId, force, properties);
                }
            }
            if (ce.getClass().equals(Network.class)) {
                switch (action) {
                case START:
                    return this.startNetwork(providerId);
                case STOP:
                    return this.stopNetwork(providerId);
                    // case SUSPEND:
                    // return this.suspendNetwork(providerId);
                    // case PAUSE:
                    // return this.pauseNetwork(providerId);
                    // case RESTART:
                    // return this.restartNetwork(providerId);
                }
            }
        } catch (BadStateException e) {
            // do nothing, return dummy job
            Job j = new Job();
            j.setProviderAssignedId("666");
            j.setName("dummy job");
            return j;
        }
        throw new ConnectorException("Illegal Operation");
    }

    private Job simulateProviderTask(final System ce, final SystemAction action, final boolean failedOrCancelled) {
        // simulating task
        final Callable<CloudResource> createTask = new Callable<CloudResource>() {
            @Override
            public CloudResource call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                switch (action) {
                case START:
                    ce.setState(System.State.STARTED);
                    break;
                case PAUSE:
                    ce.setState(System.State.PAUSED);
                    break;
                case STOP:
                    ce.setState(System.State.STOPPED);
                    break;
                case RESTART:
                    ce.setState(System.State.STARTED);
                    break;
                case SUSPEND:
                    ce.setState(System.State.SUSPENDED);
                    break;
                case ADD:
                    ce.setState(System.State.STOPPED);
                    MockCloudProviderConnector.this.systems.put(ce.getProviderAssignedId(), ce);
                    break;
                case DELETE:
                    ce.setState(System.State.DELETED);
                    MockCloudProviderConnector.this.systems.remove(ce.getProviderAssignedId());
                    break;
                case ENTITY_REMOVE:
                    break;
                default:
                    throw new Exception("action not implemented");
                }
                if (failedOrCancelled) {
                    ce.setState(System.State.ERROR);
                }
                return ce;

            }
        };
        ListenableFuture<CloudResource> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(createTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(ce, null, action.name().toLowerCase(), result);
    }

    private static enum SystemAction {
        START, STOP, PAUSE, SUSPEND, RESTART, ADD, DELETE, ENTITY_REMOVE
    }

    private static final List<System.State> forbiddenSystemStartActions = ImmutableList.of(System.State.CREATING,
        System.State.STARTED, System.State.DELETING);

    private static final List<System.State> forbiddenSystemStopActions = ImmutableList.of(System.State.CREATING,
        System.State.STOPPED, System.State.PAUSING, System.State.PAUSED, System.State.SUSPENDING, System.State.SUSPENDED,
        System.State.DELETING);

    private static final List<System.State> forbiddenSystemPauseActions = ImmutableList.of(System.State.CREATING,
        System.State.STARTING, System.State.STOPPING, System.State.STOPPED, System.State.PAUSING, System.State.PAUSED,
        System.State.SUSPENDING, System.State.SUSPENDED, System.State.DELETING);

    private static final List<System.State> forbiddenSystemSuspendActions = ImmutableList.of(System.State.CREATING,
        System.State.STARTING, System.State.STOPPING, System.State.STOPPED, System.State.PAUSING, System.State.PAUSED,
        System.State.SUSPENDING, System.State.SUSPENDED, System.State.DELETING);

    private static final List<System.State> forbiddenSystemRestartActions = ImmutableList.of();

    private static final long maxJobTimeInSeconds = 600;

    private Job doSystemService(final String systemId, final System.State temporaryState, final SystemAction action,
        final List<System.State> forbiddenStates, final boolean force, final Map<String, String> properties)
        throws ConnectorException {
        MockCloudProviderConnector.logger.info(action + " system with providerAssignedId " + systemId);
        final System system = this.systems.get(systemId);
        if (system == null) {
            throw new ConnectorException("System " + systemId + " doesn't exist");
        }
        if (forbiddenStates.contains(system.getState())) {
            throw new BadStateException("Illegal operation");
        }

        system.setState(temporaryState);

        boolean failedCancelled = false;

        failedCancelled |= this.serviceSystem(system.getMachines(), action, force, properties);
        failedCancelled |= this.serviceSystem(system.getSystems(), action, force, properties);
        failedCancelled |= this.serviceSystem(system.getNetworks(), action, force, properties);

        if (failedCancelled) {
            // one or more jobs are failed or cancelled, so all is in error
            system.setState(System.State.ERROR);
        }
        return this.simulateProviderTask(system, action, failedCancelled);
    }

    @Override
    public Job startSystem(final String systemId, final Map<String, String> properties) throws ConnectorException {
        return this.doSystemService(systemId, System.State.STARTING, SystemAction.START,
            MockCloudProviderConnector.forbiddenSystemStartActions, false, properties);
    }

    @Override
    public Job stopSystem(final String systemId, final boolean force, final Map<String, String> properties)
        throws ConnectorException {
        return this.doSystemService(systemId, System.State.STOPPING, SystemAction.STOP,
            MockCloudProviderConnector.forbiddenSystemStopActions, force, properties);
    }

    @Override
    public Job restartSystem(final String systemId, final boolean force, final Map<String, String> properties)
        throws ConnectorException {
        return this.doSystemService(systemId, System.State.STARTING, SystemAction.RESTART,
            MockCloudProviderConnector.forbiddenSystemRestartActions, force, properties);
    }

    @Override
    public Job pauseSystem(final String systemId, final Map<String, String> properties) throws ConnectorException {
        return this.doSystemService(systemId, System.State.PAUSING, SystemAction.PAUSE,
            MockCloudProviderConnector.forbiddenSystemPauseActions, false, properties);
    }

    @Override
    public Job suspendSystem(final String systemId, final Map<String, String> properties) throws ConnectorException {
        return this.doSystemService(systemId, System.State.SUSPENDING, SystemAction.SUSPEND,
            MockCloudProviderConnector.forbiddenSystemSuspendActions, false, properties);
    }

    @Override
    public Job deleteSystem(final String systemId) throws ConnectorException {
        MockCloudProviderConnector.logger.info("deleting system with providerAssignedId " + systemId);
        IJobManager jobManager = this.mockCloudProviderConnectorFactory.getJobManager();

        final System system = this.systems.get(systemId);
        if (system == null) {
            throw new ConnectorException("System " + systemId + " doesn't exist");
        }

        boolean failedCancelled = false;

        for (SystemMachine m : system.getMachines()) {
            // warning:job returned by createXXX is a copy!
            Job j = jobManager.getJobById(this.deleteMachine(m.getResource().getProviderAssignedId().toString())
                .getProviderAssignedId().toString());
            failedCancelled = this.waitForJob(j, MockCloudProviderConnector.maxJobTimeInSeconds);
        }
        for (SystemSystem m : system.getSystems()) {
            // warning:job returned by createXXX is a copy!
            Job j = jobManager.getJobById(this.deleteSystem(m.getResource().getProviderAssignedId().toString())
                .getProviderAssignedId().toString());
            failedCancelled = this.waitForJob(j, MockCloudProviderConnector.maxJobTimeInSeconds);
        }
        for (SystemVolume m : system.getVolumes()) {
            // warning:job returned by createXXX is a copy!
            Job j = jobManager.getJobById(this.deleteVolume(m.getResource().getProviderAssignedId().toString())
                .getProviderAssignedId().toString());
            failedCancelled = this.waitForJob(j, MockCloudProviderConnector.maxJobTimeInSeconds);
        }
        for (SystemNetwork m : system.getNetworks()) {
            // warning:job returned by createXXX is a copy!
            Job j = jobManager.getJobById(this.deleteNetwork(m.getResource().getProviderAssignedId().toString())
                .getProviderAssignedId().toString());
            failedCancelled = this.waitForJob(j, MockCloudProviderConnector.maxJobTimeInSeconds);
        }
        system.setState(System.State.DELETING);

        return this.simulateProviderTask(system, SystemAction.DELETE, failedCancelled);
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
                machine.getVolumes().add(machineVolume);
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
                machine.getVolumes().remove(machineVolume);
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
        network.setNetworkPorts(new ArrayList<NetworkNetworkPort>());
        this.networks.put(networkProviderAssignedId, network);
        network.setState(Network.State.CREATING);

        final Callable<Network> createTask = new Callable<Network>() {
            @Override
            public Network call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                network.setState(Network.State.STARTED);
                if (fg != null) {
                    ForwardingGroupNetwork fgNetwork = new ForwardingGroupNetwork();
                    fgNetwork.setNetwork(network);
                    fgNetwork.setState(ForwardingGroupNetwork.State.AVAILABLE);
                    fg.getNetworks().add(fgNetwork);
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
    public List<Network> getNetworks() throws ConnectorException {
        return new ArrayList<Network>(this.networks.values());
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
                NetworkNetworkPort netNetworkPort = new NetworkNetworkPort();
                netNetworkPort.setState(NetworkNetworkPort.State.AVAILABLE);
                netNetworkPort.setNetworkPort(networkPort);
                network.getNetworkPorts().add(netNetworkPort);
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
        final Set<ForwardingGroupNetwork> networksToAdd = new HashSet<ForwardingGroupNetwork>();
        if (forwardingGroupCreate.getForwardingGroupTemplate().getNetworks() != null) {
            for (Network net : forwardingGroupCreate.getForwardingGroupTemplate().getNetworks()) {
                String netId = net.getProviderAssignedId();
                Network providerNetwork = this.networks.get(netId);
                if (providerNetwork == null) {
                    throw new ConnectorException("Unknown network with id " + netId);
                }
                ForwardingGroupNetwork fgNetwork = new ForwardingGroupNetwork();
                fgNetwork.setNetwork(providerNetwork);
                fgNetwork.setState(ForwardingGroupNetwork.State.AVAILABLE);
                networksToAdd.add(fgNetwork);
            }
        }
        final String forwardingGroupProviderAssignedId = UUID.randomUUID().toString();
        final ForwardingGroup forwardingGroup = new ForwardingGroup();
        forwardingGroup.setProviderAssignedId(forwardingGroupProviderAssignedId);
        forwardingGroup.setNetworks(new HashSet<ForwardingGroupNetwork>());
        this.forwardingGroups.put(forwardingGroupProviderAssignedId, forwardingGroup);
        forwardingGroup.setState(ForwardingGroup.State.CREATING);

        final Callable<ForwardingGroup> createTask = new Callable<ForwardingGroup>() {
            @Override
            public ForwardingGroup call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                forwardingGroup.setNetworks(networksToAdd);
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
    public Job addNetworkToForwardingGroup(final String forwardingGroupId, final ForwardingGroupNetwork fgNetwork)
        throws ConnectorException {
        final ForwardingGroup forwardingGroup = this.forwardingGroups.get(forwardingGroupId);
        if (forwardingGroup == null) {
            throw new ConnectorException("NetworkPort " + forwardingGroupId + " doesn't exist");
        }
        final Network network = this.networks.get(fgNetwork.getNetwork().getProviderAssignedId());
        if (network == null) {
            throw new ConnectorException("Unknown network with id=" + fgNetwork.getNetwork().getProviderAssignedId());
        }
        final Callable<Void> attachTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                fgNetwork.setState(ForwardingGroupNetwork.State.AVAILABLE);
                forwardingGroup.getNetworks().add(fgNetwork);
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
        ForwardingGroupNetwork fgNetwork = null;
        for (ForwardingGroupNetwork net : forwardingGroup.getNetworks()) {
            if (net.getNetwork().getProviderAssignedId().equals(networkId)) {
                fgNetwork = net;
                break;
            }
        }
        if (fgNetwork == null) {
            throw new ConnectorException("Network with id=" + networkId + " is not a member of forwarding group with id="
                + forwardingGroupId);
        }
        final ForwardingGroupNetwork fgNetworkToRemove = fgNetwork;
        final Callable<Void> attachTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS);
                forwardingGroup.getNetworks().remove(fgNetworkToRemove);
                MockCloudProviderConnector.logger.info("Removed Network from ForwardingGroup " + forwardingGroupId);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(attachTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(forwardingGroup, network, "delete", result);
    }

}
