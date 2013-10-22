/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
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

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFinder;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.connector.api.INetworkService;
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IResourceWatcher;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.impl.command.MachineActionCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.MachineCreateCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.MachineDeleteCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.NetworkCreateCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.NetworkDeleteCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.ResourceCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.VolumeAttachCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.VolumeCreateCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.VolumeDeleteCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.VolumeDetachCommand;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/RequestQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = "MyID"),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "MySub")})
public class RequestDispatcher implements MessageListener {
    private static Logger logger = LoggerFactory.getLogger(RequestDispatcher.class.getName());

    @Resource
    public MessageDrivenContext mdc;

    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @EJB
    private ICloudProviderConnectorFinder connectorFinder;

    @EJB
    private IResourceWatcher resourceWatcher;

    @EJB
    private IMachineManager machineManager;

    @EJB
    private IVolumeManager volumeManager;

    @EJB
    private INetworkManager networkManager;

    @Override
    public void onMessage(final Message inMessage) {

        ResourceCommand command = null;
        try {
            command = inMessage.getBody(ResourceCommand.class);
            switch (command.getCommandType()) {
            case MachineCreateCommand.MACHINE_CREATE:
                this.createMachine((MachineCreateCommand) command);
                break;
            case MachineDeleteCommand.MACHINE_DELETE:
                this.deleteMachine(command);
                break;
            case MachineActionCommand.MACHINE_ACTION:
                this.actionMachine((MachineActionCommand) command);
                break;
            case VolumeCreateCommand.VOLUME_CREATE:
                this.createVolume((VolumeCreateCommand) command);
                break;
            case VolumeDeleteCommand.VOLUME_DELETE:
                this.deleteVolume(command);
                break;
            case VolumeAttachCommand.VOLUME_ATTACH:
                this.attachVolume((VolumeAttachCommand) command);
                break;
            case VolumeDetachCommand.VOLUME_DETACH:
                this.detachVolume((VolumeDetachCommand) command);
                break;
            case NetworkCreateCommand.NETWORK_CREATE:
                this.createNetwork((NetworkCreateCommand) command);
                break;
            case NetworkDeleteCommand.NETWORK_DELETE:
                this.deleteNetwork(command);
                break;
            }
        } catch (JMSException e) {
            RequestDispatcher.logger.error("RequestDispatcher.onMessage", e);
            // this.mdc.setRollbackOnly();
        } catch (CloudProviderException | ConnectorException e) {
            RequestDispatcher.logger.error("RequestDispatcher.onMessage", e);
            try {
                this.setErrorStatus(command, e);
            } catch (CloudProviderException e1) {
            }
        } catch (Exception e) {
            RequestDispatcher.logger.error("RequestDispatcher.onMessage", e);
        }
    }

    private void setErrorStatus(final ResourceCommand command, final Exception e) throws CloudProviderException {
        Job job = this.em.find(Job.class, new Integer(command.getJob().getId()));
        job.setState(Job.Status.FAILED);
        job.setStatusMessage(e.getMessage());
        switch (command.getCommandType()) {
        case MachineCreateCommand.MACHINE_CREATE:
        case MachineDeleteCommand.MACHINE_DELETE:
        case MachineActionCommand.MACHINE_ACTION:
            this.machineManager.updateMachineState(command.getResourceId(), Machine.State.ERROR);
            break;
        case VolumeCreateCommand.VOLUME_CREATE:
        case VolumeDeleteCommand.VOLUME_DELETE:
            this.volumeManager.updateVolumeState(command.getResourceId(), Volume.State.ERROR);
            break;
        case VolumeAttachCommand.VOLUME_ATTACH:
            this.machineManager.updateMachineVolumeState(((VolumeAttachCommand) command).getVolumeAttachment().getId()
                .toString(), MachineVolume.State.ERROR);
            break;
        case VolumeDetachCommand.VOLUME_DETACH:
            this.machineManager.updateMachineVolumeState(((VolumeDetachCommand) command).getVolumeAttachment().getId()
                .toString(), MachineVolume.State.ERROR);
            break;
        case NetworkCreateCommand.NETWORK_CREATE:
        case NetworkDeleteCommand.NETWORK_DELETE:
            this.networkManager.updateNetworkState(command.getResourceId(), Network.State.ERROR);
            break;
        }
    }

    private void createMachine(final MachineCreateCommand command) throws CloudProviderException, ConnectorException {
        ICloudProviderConnector connector = this.findCloudProviderConnector(command.getAccount());

        IComputeService computeService = connector.getComputeService();
        Machine newMachine = computeService.createMachine(command.getMachineCreate(),
            new ProviderTarget().account(command.getAccount()).location(command.getLocation()));

        Machine machine = this.em.find(Machine.class, Integer.valueOf(command.getResourceId()));
        machine.setProviderAssignedId(newMachine.getProviderAssignedId());

        this.resourceWatcher.watchMachine(machine, command.getJob(), Machine.State.STARTED, Machine.State.STOPPED);
    }

    private void deleteMachine(final ResourceCommand command) throws CloudProviderException, ConnectorException {
        Machine machine = this.em.find(Machine.class, Integer.valueOf(command.getResourceId()));
        ICloudProviderConnector connector = this.findCloudProviderConnector(machine.getCloudProviderAccount());
        IComputeService computeService = connector.getComputeService();
        computeService.deleteMachine(machine.getProviderAssignedId(),
            new ProviderTarget().account(machine.getCloudProviderAccount()).location(machine.getLocation()));

        this.resourceWatcher.watchMachine(machine, command.getJob(), Machine.State.DELETED);
    }

    private void actionMachine(final MachineActionCommand command) throws CloudProviderException, ConnectorException {
        Machine machine = this.em.find(Machine.class, Integer.valueOf(command.getResourceId()));
        ICloudProviderConnector connector = this.findCloudProviderConnector(machine.getCloudProviderAccount());
        IComputeService computeService = connector.getComputeService();
        ProviderTarget target = new ProviderTarget().account(machine.getCloudProviderAccount()).location(machine.getLocation());
        Machine.State expectedState = null;
        switch (command.getAction()) {
        case "start":
            computeService.startMachine(machine.getProviderAssignedId(), target);
            expectedState = State.STARTED;
            break;
        case "stop":
            computeService.stopMachine(machine.getProviderAssignedId(), command.isForce(), target);
            expectedState = State.STOPPED;
            break;
        case "suspend":
            computeService.deleteMachine(machine.getProviderAssignedId(), target);
            expectedState = State.SUSPENDED;
            break;
        case "pause":
            computeService.deleteMachine(machine.getProviderAssignedId(), target);
            expectedState = State.PAUSED;
            break;
        case "restart":
            computeService.restartMachine(machine.getProviderAssignedId(), command.isForce(), target);
            expectedState = State.STARTED;
            break;
        }

        this.resourceWatcher.watchMachine(machine, command.getJob(), expectedState);
    }

    private void createVolume(final VolumeCreateCommand command) throws CloudProviderException, ConnectorException {
        ICloudProviderConnector connector = this.findCloudProviderConnector(command.getAccount());

        IVolumeService volumeService = connector.getVolumeService();
        Volume newVolume = volumeService.createVolume(command.getVolumeCreate(),
            new ProviderTarget().account(command.getAccount()).location(command.getLocation()));

        Volume volume = this.em.find(Volume.class, Integer.valueOf(command.getResourceId()));
        volume.setProviderAssignedId(newVolume.getProviderAssignedId());

        this.resourceWatcher.watchVolume(volume, command.getJob());
    }

    private void deleteVolume(final ResourceCommand command) throws CloudProviderException, ConnectorException {
        Volume volume = this.em.find(Volume.class, Integer.valueOf(command.getResourceId()));
        ICloudProviderConnector connector = this.findCloudProviderConnector(volume.getCloudProviderAccount());
        IVolumeService volumeService = connector.getVolumeService();
        volumeService.deleteVolume(volume.getProviderAssignedId(),
            new ProviderTarget().account(volume.getCloudProviderAccount()).location(volume.getLocation()));

        this.resourceWatcher.watchVolume(volume, command.getJob());
    }

    private void attachVolume(final VolumeAttachCommand command) throws CloudProviderException, ConnectorException {
        Machine machine = this.em.find(Machine.class, Integer.valueOf(command.getResourceId()));
        ICloudProviderConnector connector = this.findCloudProviderConnector(machine.getCloudProviderAccount());
        IComputeService computeService = connector.getComputeService();
        ProviderTarget target = new ProviderTarget().account(machine.getCloudProviderAccount()).location(machine.getLocation());
        computeService.addVolumeToMachine(machine.getProviderAssignedId(), command.getVolumeAttachment(), target);

        this.resourceWatcher.watchVolumeAttachment(machine, command.getVolumeAttachment(), command.getJob());
    }

    private void detachVolume(final VolumeDetachCommand command) throws CloudProviderException, ConnectorException {
        Machine machine = this.em.find(Machine.class, Integer.valueOf(command.getResourceId()));
        ICloudProviderConnector connector = this.findCloudProviderConnector(machine.getCloudProviderAccount());
        IComputeService computeService = connector.getComputeService();
        ProviderTarget target = new ProviderTarget().account(machine.getCloudProviderAccount()).location(machine.getLocation());
        computeService.removeVolumeFromMachine(machine.getProviderAssignedId(), command.getVolumeAttachment(), target);

        this.resourceWatcher.watchVolumeAttachment(machine, command.getVolumeAttachment(), command.getJob());
    }

    private void createNetwork(final NetworkCreateCommand command) throws CloudProviderException, ConnectorException {
        ICloudProviderConnector connector = this.findCloudProviderConnector(command.getAccount());

        INetworkService networkService = connector.getNetworkService();
        Network newNetwork = networkService.createNetwork(command.getNetworkCreate(),
            new ProviderTarget().account(command.getAccount()).location(command.getLocation()));

        Network network = this.em.find(Network.class, Integer.valueOf(command.getResourceId()));
        network.setProviderAssignedId(newNetwork.getProviderAssignedId());

        this.resourceWatcher.watchNetwork(network, command.getJob(), Network.State.STARTED, Network.State.STOPPED);
    }

    private void deleteNetwork(final ResourceCommand command) throws CloudProviderException, ConnectorException {
        Network network = this.em.find(Network.class, Integer.valueOf(command.getResourceId()));
        ICloudProviderConnector connector = this.findCloudProviderConnector(network.getCloudProviderAccount());
        INetworkService networkService = connector.getNetworkService();
        networkService.deleteNetwork(network.getProviderAssignedId(),
            new ProviderTarget().account(network.getCloudProviderAccount()).location(network.getLocation()));

        this.resourceWatcher.watchNetwork(network, command.getJob(), Network.State.DELETED);
    }

    private ICloudProviderConnector findCloudProviderConnector(final CloudProviderAccount cloudProviderAccount)
        throws CloudProviderException {
        ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(cloudProviderAccount
            .getCloudProvider().getCloudProviderType());
        if (connector == null) {
            RequestDispatcher.logger.error("Cannot find connector for cloud provider type "
                + cloudProviderAccount.getCloudProvider().getCloudProviderType());
            throw new CloudProviderException("Cannot find connector for cloud provider type "
                + cloudProviderAccount.getCloudProvider().getCloudProviderType());
        }
        return connector;
    }

}
