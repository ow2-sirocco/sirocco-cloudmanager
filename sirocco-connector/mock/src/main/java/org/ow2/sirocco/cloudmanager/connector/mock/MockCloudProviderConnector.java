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
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterface.InterfaceState;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.google.common.util.concurrent.ListenableFuture;

public class MockCloudProviderConnector implements ICloudProviderConnector, IComputeService, IVolumeService, IImageService {

    private static Log logger = LogFactory.getLog(MockCloudProviderConnector.class);

    private static final int ENTITY_LIFECYLCE_OPERATION_TIME_IN_SECONDS = 1;

    private final String cloudProviderId;

    private final CloudProviderAccount cloudProviderAccount;

    private final CloudProviderLocation cloudProviderLocation;

    private final MockCloudProviderConnectorFactory mockCloudProviderConnectorFactory;

    private Map<String, Volume> volumes = new ConcurrentHashMap<String, Volume>();

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
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYLCE_OPERATION_TIME_IN_SECONDS * 1000);
                volume.setState(Volume.State.AVAILABLE);
                return volume;
            }
        };

        ListenableFuture<Volume> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(createTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(volumeProviderAssignedId, "volume.create", result);
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
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYLCE_OPERATION_TIME_IN_SECONDS * 1000);
                MockCloudProviderConnector.this.volumes.remove(volumeId);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(deleteTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(volumeId, "volume.delete", result);

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
        machine.setState(Machine.State.CREATING);
        machine.setCpu(new Cpu(machineCreate.getMachineTemplate().getMachineConfiguration().getCpu()));
        machine.setMemory(machineCreate.getMachineTemplate().getMachineConfiguration().getMemory());
        List<Disk> disks = new ArrayList<Disk>();
        for (DiskTemplate diskTemplate : machineCreate.getMachineTemplate().getMachineConfiguration().getDiskTemplates()) {
            Disk disk = new Disk();
            // TODO
            // disk.setDiskUnit(diskTemplate.getDiskUnit());
            disk.setQuantity(diskTemplate.getQuantity());
            disks.add(disk);
        }
        machine.setDisks(disks);
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
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
        machine.setNetworkInterfaces(networkInterfaces);

        final Callable<Machine> createTask = new Callable<Machine>() {
            @Override
            public Machine call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYLCE_OPERATION_TIME_IN_SECONDS * 1000);
                for (NetworkInterface networkInterface : machine.getNetworkInterfaces()) {
                    networkInterface.setState(InterfaceState.ACTIVE);
                }
                machine.setState(Machine.State.STOPPED);
                return machine;
            }
        };

        ListenableFuture<Machine> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(createTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machineProviderAssignedId, "machine.create",
            result);

    }

    @Override
    public synchronized Job startMachine(final String machineId) throws ConnectorException {
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
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYLCE_OPERATION_TIME_IN_SECONDS * 1000);
                machine.setState(Machine.State.STARTED);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(startTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machineId, "machine.start", result);
    }

    @Override
    public synchronized Job stopMachine(final String machineId) throws ConnectorException {
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
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYLCE_OPERATION_TIME_IN_SECONDS * 1000);
                machine.setState(Machine.State.STOPPED);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(stopTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machineId, "machine.stop", result);
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
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYLCE_OPERATION_TIME_IN_SECONDS * 1000);
                machine.setState(Machine.State.SUSPENDED);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(suspendTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machineId, "machine.suspend", result);
    }

    @Override
    public synchronized Job restartMachine(final String machineId) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
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
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYLCE_OPERATION_TIME_IN_SECONDS * 1000);
                machine.setState(Machine.State.PAUSED);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(pauseTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machineId, "machine.paused", result);
    }

    @Override
    public synchronized Job deleteMachine(final String machineId) throws ConnectorException {
        Machine machine = this.machines.get(machineId);
        if (machine == null) {
            throw new ConnectorException("Volume " + machineId + " doesn't exist");
        }
        machine.setState(Machine.State.DELETING);
        final Callable<Void> deleteTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.ENTITY_LIFECYLCE_OPERATION_TIME_IN_SECONDS * 1000);
                MockCloudProviderConnector.this.machines.remove(machineId);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(deleteTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(machineId, "machine.delete", result);
    }

    @Override
    public synchronized State getMachineState(final String machineId) throws ConnectorException {
        return this.getMachine(machineId).getState();
    }

    @Override
    public synchronized Machine getMachine(final String machineId) throws ConnectorException {
        Machine machine = this.machines.get(machineId);
        if (machine == null) {
            throw new ConnectorException("Machine " + machineId + " doesn't exist");
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

}
