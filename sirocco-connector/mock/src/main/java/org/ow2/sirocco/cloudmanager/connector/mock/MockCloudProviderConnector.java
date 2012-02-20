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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.connector.api.IImageService;
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.google.common.util.concurrent.ListenableFuture;

public class MockCloudProviderConnector implements ICloudProviderConnector, IComputeService, IVolumeService, IImageService {

    private static Log logger = LogFactory.getLog(MockCloudProviderConnector.class);

    private static final int VOLUME_CREATION_TIME_IN_SECONDS = 10;

    private static final int VOLUME_DELETION_TIME_IN_SECONDS = 10;

    private final String cloudProviderId;

    private final CloudProviderAccount cloudProviderAccount;

    private final CloudProviderLocation cloudProviderLocation;

    private final MockCloudProviderConnectorFactory mockCloudProviderConnectorFactory;

    private Map<String, Volume> volumes = new HashMap<String, Volume>();

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
    public Job createVolume(final VolumeCreate volumeCreate) throws ConnectorException {
        final String volumeProviderAssignedId = UUID.randomUUID().toString();
        final Volume volume = new Volume();
        volume.setProviderAssignedId(volumeProviderAssignedId);
        this.volumes.put(volumeProviderAssignedId, volume);
        volume.setState(Volume.State.CREATING);

        final Callable<Volume> createTask = new Callable<Volume>() {
            @Override
            public Volume call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.VOLUME_CREATION_TIME_IN_SECONDS * 1000);
                volume.setState(Volume.State.AVAILABLE);
                return volume;
            }
        };

        ListenableFuture<Volume> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(createTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(volumeProviderAssignedId, "volume.create", result);
    }

    @Override
    public Job deleteVolume(final String volumeId) throws ConnectorException {
        Volume volume = this.volumes.get(volumeId);
        if (volume == null) {
            throw new ConnectorException("Volume " + volumeId + " doesn't exist");
        }
        volume.setState(Volume.State.DELETING);
        final Callable<Void> deleteTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(MockCloudProviderConnector.VOLUME_DELETION_TIME_IN_SECONDS * 1000);
                MockCloudProviderConnector.this.volumes.remove(volumeId);
                return null;
            }
        };

        ListenableFuture<Void> result = this.mockCloudProviderConnectorFactory.getExecutorService().submit(deleteTask);
        return this.mockCloudProviderConnectorFactory.getJobManager().newJob(volumeId, "volume.delete", result);

    }

    @Override
    public org.ow2.sirocco.cloudmanager.model.cimi.Volume.State getVolumeState(final String volumeId) throws ConnectorException {
        Volume volume = this.volumes.get(volumeId);
        if (volume == null) {
            throw new ConnectorException("Volume " + volumeId + " doesn't exist");
        }
        return volume.getState();
    }

    @Override
    public Job createMachine(final MachineCreate machineCreate) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job startMachine(final String machineId) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job stopMachine(final String machineId) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job suspendMachine(final String machineId) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job restartMachine(final String machineId) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job pauseMachine(final String machineId) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job deleteMachine(final String machineId) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public State getMachineState(final String machineId) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MachineConfiguration getMachineConfiguration(final String machineId) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<NetworkInterface> getMachineNetworkInterfaces(final String machineId) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
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
