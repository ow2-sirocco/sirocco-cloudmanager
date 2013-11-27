/**
 *
 * SIROCCO
 * Copyright (C) 2013 Orange
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
 */
package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFinder;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.connector.api.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IResourceWatcher;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.remote.IRemoteResourceWatcher;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume.State;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Local(IResourceWatcher.class)
@Remote(IRemoteResourceWatcher.class)
public class ResourceWatcher implements IResourceWatcher {
    private static Logger logger = LoggerFactory.getLogger(ResourceWatcher.class.getName());

    private static final int SLEEP_BETWEEN_POLL_IN_SECONDS = 10;

    private static final int MAX_WAIT_TIME_IN_SECONDS = 10 * 60;

    @Resource
    SessionContext context;

    @EJB
    IMachineManager machineManager;

    @EJB
    INetworkManager networkManager;

    @EJB
    IVolumeManager volumeManager;

    @EJB
    ISystemManager systemManager;

    @EJB
    private ICloudProviderConnectorFinder connectorFinder;

    private ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount cloudProviderAccount)
        throws CloudProviderException {
        ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(cloudProviderAccount
            .getCloudProvider().getCloudProviderType());
        if (connector == null) {
            ResourceWatcher.logger.error("Cannot find connector for cloud provider type "
                + cloudProviderAccount.getCloudProvider().getCloudProviderType());
            return null;
        }
        return connector;
    }

    @Override
    @Asynchronous
    public Future<Void> watchMachine(final Machine machine, final Job job, final Machine.State... expectedStates)
        throws CloudProviderException {
        ICloudProviderConnector connector = this.getCloudProviderConnector(machine.getCloudProviderAccount());
        ProviderTarget target = new ProviderTarget().account(machine.getCloudProviderAccount()).location(machine.getLocation());

        int tries = ResourceWatcher.MAX_WAIT_TIME_IN_SECONDS / ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS;
        mainloop: while (tries-- > 0) {
            try {
                Machine updatedMachine = connector.getComputeService().getMachine(machine.getProviderAssignedId(), target);

                for (Machine.State expectedFinalState : expectedStates) {
                    if (updatedMachine.getState() == expectedFinalState) {
                        this.machineManager.syncMachine(machine.getId(), updatedMachine, job.getId());
                        break mainloop;
                    }
                }
            } catch (ResourceNotFoundException e) {
                this.machineManager.syncMachine(machine.getId(), null, job.getId());
                break;
            } catch (ConnectorException e) {
                ResourceWatcher.logger.error("Failed to poll machine state: ", e);
            }
            try {
                Thread.sleep(1000 * ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS);
            } catch (InterruptedException e) {
                break;
            }
            if (this.context.wasCancelCalled()) {
                ResourceWatcher.logger.info("Machine watcher cancelled for machine " + machine.getId());
                break;
            }
        }
        return new AsyncResult<Void>(null);
    }

    @Override
    @Asynchronous
    public Future<Void> watchNetwork(final Network network, final Job job, final Network.State... expectedStates)
        throws CloudProviderException {
        ICloudProviderConnector connector = this.getCloudProviderConnector(network.getCloudProviderAccount());
        ProviderTarget target = new ProviderTarget().account(network.getCloudProviderAccount()).location(network.getLocation());

        int tries = ResourceWatcher.MAX_WAIT_TIME_IN_SECONDS / ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS;
        mainloop: while (tries-- > 0) {
            try {
                Network updatedNetwork = connector.getNetworkService().getNetwork(network.getProviderAssignedId(), target);
                for (Network.State expectedFinalState : expectedStates) {
                    if (updatedNetwork.getState() == expectedFinalState) {
                        this.networkManager.syncNetwork(network.getId(), updatedNetwork, job.getId());
                        break mainloop;
                    }
                }
            } catch (ResourceNotFoundException e) {
                this.networkManager.syncNetwork(network.getId(), null, job.getId());
                break;
            } catch (ConnectorException e) {
                ResourceWatcher.logger.error("Failed to poll network state: ", e);
                break;
            }
            try {
                Thread.sleep(1000 * ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS);
            } catch (InterruptedException e) {
                break;
            }
            if (this.context.wasCancelCalled()) {
                break;
            }
        }
        return new AsyncResult<Void>(null);
    }

    @Override
    @Asynchronous
    public Future<Void> watchVolume(final Volume volume, final Job job, final Volume.State... expectedStates)
        throws CloudProviderException {
        ICloudProviderConnector connector = this.getCloudProviderConnector(volume.getCloudProviderAccount());
        ProviderTarget target = new ProviderTarget().account(volume.getCloudProviderAccount()).location(volume.getLocation());

        int tries = ResourceWatcher.MAX_WAIT_TIME_IN_SECONDS / ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS;
        mainloop: while (tries-- > 0) {
            try {
                Volume updatedVolume = connector.getVolumeService().getVolume(volume.getProviderAssignedId(), target);
                for (Volume.State expectedFinalState : expectedStates) {
                    if (updatedVolume.getState() == expectedFinalState) {
                        this.volumeManager.syncVolume(volume.getId(), updatedVolume, job.getId());
                        break mainloop;
                    }
                }

            } catch (ResourceNotFoundException e) {
                this.volumeManager.syncVolume(volume.getId(), null, job.getId());
                break;
            } catch (ConnectorException e) {
                ResourceWatcher.logger.error("Failed to poll volume state: ", e);
                break;
            }
            try {
                Thread.sleep(1000 * ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS);
            } catch (InterruptedException e) {
                break;
            }
            if (this.context.wasCancelCalled()) {
                break;
            }
        }
        return new AsyncResult<Void>(null);
    }

    @Override
    @Asynchronous
    public Future<Void> watchVolumeAttachment(final Machine machine, final MachineVolume volumeAttachment, final Job job,
        final MachineVolume.State... expectedStates) throws CloudProviderException {
        ICloudProviderConnector connector = this.getCloudProviderConnector(machine.getCloudProviderAccount());
        ProviderTarget target = new ProviderTarget().account(machine.getCloudProviderAccount()).location(machine.getLocation());

        int tries = ResourceWatcher.MAX_WAIT_TIME_IN_SECONDS / ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS;
        String volumeId = volumeAttachment.getVolume().getProviderAssignedId();
        mainloop: while (tries-- > 0) {
            try {
                Machine updatedMachine = connector.getComputeService().getMachine(machine.getProviderAssignedId(), target);
                if (updatedMachine.getVolumes() != null) {
                    boolean volumeAttachmentFound = false;
                    for (MachineVolume mv : updatedMachine.getVolumes()) {
                        if (mv.getVolume().getProviderAssignedId().equals(volumeId)) {
                            volumeAttachmentFound = true;
                            for (MachineVolume.State expectedFinalState : expectedStates) {
                                if (mv.getState() == expectedFinalState) {
                                    this.machineManager.syncVolumeAttachment(machine.getId(), mv, job.getId());
                                    break mainloop;
                                }
                            }
                            break;
                        }
                    }
                    if (!volumeAttachmentFound) {
                        volumeAttachment.setState(State.DELETED);
                        this.machineManager.syncVolumeAttachment(machine.getId(), volumeAttachment, job.getId());
                        break;
                    }
                }
            } catch (ResourceNotFoundException e) {
                this.machineManager.syncMachine(machine.getId(), null, job.getId());
                break;
            } catch (ConnectorException e) {
                ResourceWatcher.logger.error("Failed to poll machine state: ", e);
                break;
            }
            try {
                Thread.sleep(1000 * ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS);
            } catch (InterruptedException e) {
                break;
            }
            if (this.context.wasCancelCalled()) {
                break;
            }
        }
        return new AsyncResult<Void>(null);
    }

    @Asynchronous
    @Override
    public Future<Void> watchSystem(final System system, final Job job, final System.State... expectedStates)
        throws CloudProviderException {
        ICloudProviderConnector connector = this.getCloudProviderConnector(system.getCloudProviderAccount());
        ProviderTarget target = new ProviderTarget().account(system.getCloudProviderAccount()).location(system.getLocation());

        int tries = ResourceWatcher.MAX_WAIT_TIME_IN_SECONDS / ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS;
        mainloop: while (tries-- > 0) {
            try {
                System updatedSystem = connector.getSystemService().getSystem(system.getProviderAssignedId(), target);
                for (System.State expectedFinalState : expectedStates) {
                    if (updatedSystem.getState() == expectedFinalState) {
                        this.systemManager.syncSystem(system.getId(), updatedSystem, job.getId());
                        break mainloop;
                    }
                }
            } catch (ResourceNotFoundException e) {
                this.systemManager.syncSystem(system.getId(), null, job.getId());
                break;
            } catch (ConnectorException e) {
                ResourceWatcher.logger.error("Failed to poll system state: ", e);
                break;
            }
            try {
                Thread.sleep(1000 * ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS);
            } catch (InterruptedException e) {
                break;
            }
            if (this.context.wasCancelCalled()) {
                break;
            }
        }
        return new AsyncResult<Void>(null);
    }

}
