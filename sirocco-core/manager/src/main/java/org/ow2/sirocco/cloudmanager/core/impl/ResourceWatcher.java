package org.ow2.sirocco.cloudmanager.core.impl;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFinder;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.connector.api.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteResourceWatcher;
import org.ow2.sirocco.cloudmanager.core.api.IResourceWatcher;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
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
    public void watchMachine(final Machine machine, final Job job, final Machine.State... expectedStates)
        throws CloudProviderException {
        ICloudProviderConnector connector = this.getCloudProviderConnector(machine.getCloudProviderAccount());
        ProviderTarget target = new ProviderTarget().account(machine.getCloudProviderAccount()).location(machine.getLocation());

        int tries = ResourceWatcher.MAX_WAIT_TIME_IN_SECONDS / ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS;
        mainloop: while (tries-- > 0) {
            try {
                Machine updatedMachine = connector.getComputeService().getMachine(machine.getProviderAssignedId(), target);

                for (Machine.State expectedFinalState : expectedStates) {
                    ResourceWatcher.logger
                        .info("updatedState=" + updatedMachine.getState() + " expected=" + expectedFinalState);
                    if (updatedMachine.getState() == expectedFinalState) {
                        this.machineManager.syncMachine(machine.getId().toString(), updatedMachine, job.getId().toString());
                        break mainloop;
                    }
                }
            } catch (ResourceNotFoundException e) {
                this.machineManager.syncMachine(machine.getId().toString(), null, job.getId().toString());
                break;
            } catch (ConnectorException e) {
                ResourceWatcher.logger.error("Failed to poll machine state: ", e);
            }
            try {
                Thread.sleep(1000 * ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @Override
    @Asynchronous
    public void watchNetwork(final Network network, final Job job, final Network.State... expectedStates)
        throws CloudProviderException {
        ICloudProviderConnector connector = this.getCloudProviderConnector(network.getCloudProviderAccount());
        ProviderTarget target = new ProviderTarget().account(network.getCloudProviderAccount()).location(network.getLocation());

        int tries = ResourceWatcher.MAX_WAIT_TIME_IN_SECONDS / ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS;
        mainloop: while (tries-- > 0) {
            try {
                Network updatedNetwork = connector.getNetworkService().getNetwork(network.getProviderAssignedId(), target);
                for (Network.State expectedFinalState : expectedStates) {
                    if (updatedNetwork.getState() == expectedFinalState) {
                        this.networkManager.syncNetwork(network.getId().toString(), updatedNetwork, job.getId().toString());
                        break mainloop;
                    }
                }
            } catch (ResourceNotFoundException e) {
                this.networkManager.syncNetwork(network.getId().toString(), null, job.getId().toString());
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
        }
    }

    @Override
    @Asynchronous
    public void watchVolume(final Volume volume, final Job job) throws CloudProviderException {
        ICloudProviderConnector connector = this.getCloudProviderConnector(volume.getCloudProviderAccount());
        ProviderTarget target = new ProviderTarget().account(volume.getCloudProviderAccount()).location(volume.getLocation());

        int tries = ResourceWatcher.MAX_WAIT_TIME_IN_SECONDS / ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS;
        while (tries-- > 0) {
            try {
                Volume updatedVolume = connector.getVolumeService().getVolume(volume.getProviderAssignedId(), target);
                if (!updatedVolume.getState().toString().endsWith("ING")) {
                    this.volumeManager.syncVolume(volume.getId().toString(), updatedVolume, job.getId().toString());
                    break;
                }
            } catch (ResourceNotFoundException e) {
                this.volumeManager.syncVolume(volume.getId().toString(), null, job.getId().toString());
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
        }
    }

    @Override
    @Asynchronous
    public void watchVolumeAttachment(final Machine machine, final MachineVolume volumeAttachment, final Job job)
        throws CloudProviderException {
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
                            if (!mv.getState().toString().endsWith("ING")) {
                                this.machineManager
                                    .syncVolumeAttachment(machine.getId().toString(), mv, job.getId().toString());
                                break mainloop;
                            }
                            break;
                        }
                    }
                    if (!volumeAttachmentFound) {
                        volumeAttachment.setState(State.DELETED);
                        this.machineManager.syncVolumeAttachment(machine.getId().toString(), volumeAttachment, job.getId()
                            .toString());
                        break;
                    }
                }
            } catch (ResourceNotFoundException e) {
                this.machineManager.syncMachine(machine.getId().toString(), null, job.getId().toString());
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
        }
    }

    @Override
    public void watchSystem(final System system, final Job job) throws CloudProviderException {
        ICloudProviderConnector connector = this.getCloudProviderConnector(system.getCloudProviderAccount());
        ProviderTarget target = new ProviderTarget().account(system.getCloudProviderAccount()).location(system.getLocation());

        int tries = ResourceWatcher.MAX_WAIT_TIME_IN_SECONDS / ResourceWatcher.SLEEP_BETWEEN_POLL_IN_SECONDS;
        while (tries-- > 0) {
            try {
                System updatedSystem = connector.getSystemService().getSystem(system.getProviderAssignedId(), target);
                if (!updatedSystem.getState().toString().endsWith("ING")) {
                    this.systemManager.syncSystem(system.getId().toString(), updatedSystem, job.getId().toString());
                    break;
                }
            } catch (ResourceNotFoundException e) {
                this.systemManager.syncSystem(system.getId().toString(), null, job.getId().toString());
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
        }
    }

}
