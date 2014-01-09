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

package org.ow2.sirocco.cloudmanager.connector.vcd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.connector.api.IImageService;
import org.ow2.sirocco.cloudmanager.connector.api.INetworkService;
import org.ow2.sirocco.cloudmanager.connector.api.ISystemService;
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.connector.api.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudCollectionItem;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupRule;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCreate;

public class VcdCloudProviderConnector implements ICloudProviderConnector, IComputeService, ISystemService, INetworkService,
    IImageService {

    private List<VcdCloudProvider> vcdCPs = new ArrayList<VcdCloudProvider>();

    private synchronized VcdCloudProvider getProvider(final ProviderTarget target) throws ConnectorException {
        if (target.getAccount() == null || target.getLocation() == null) {
            throw new ConnectorException("target.account or target.location is null");
        }
        for (VcdCloudProvider provider : this.vcdCPs) {
            /*
             * if
             * (provider.getCloudProviderAccount().equals(target.getAccount())
             * &&
             * provider.getCloudProviderLocation().equals(target.getLocation()))
             * { return provider; }
             */
            if (provider.getCloudProviderAccount().equals(target.getAccount())) {
                // location can be null?
                if (provider.getCloudProviderLocation() != target.getLocation()) {
                    if (target.getLocation() != null) {
                        if (provider.getCloudProviderLocation().getId().equals(target.getLocation().getId())) {
                            return provider;
                        }
                    }
                } else {
                    return provider;
                }
            }
        }
        VcdCloudProvider provider = new VcdCloudProvider(target);
        this.vcdCPs.add(provider);
        return provider;
    }

    /*
     * TODO - getMachine : CIMI address management aligned with openstack -
     * explicit subnet ; conflict between cidr - CIMI address allocation mode :
     * dynamic / fixed
     */

    //
    // ICloudProviderConnector
    //

    @Override
    public IComputeService getComputeService() throws ConnectorException {
        return this;
    }

    @Override
    public ISystemService getSystemService() throws ConnectorException {
        return this;
    }

    @Override
    public INetworkService getNetworkService() throws ConnectorException {
        // return this;
        return this;
    }

    @Override
    public IVolumeService getVolumeService() throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public Set<CloudProviderLocation> getLocations() {
        return null;
    }

    @Override
    public IImageService getImageService() throws ConnectorException {
        return this;
    }

    //
    // System Service
    //

    @Override
    public System createSystem(final SystemCreate systemCreate, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).createSystem(systemCreate);
    }

    @Override
    public void deleteSystem(final String systemId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        this.getProvider(target).deleteSystem(systemId);

    }

    @Override
    public void startSystem(final String systemId, final Map<String, String> properties, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        this.getProvider(target).startSystem(systemId, properties);

    }

    @Override
    public void stopSystem(final String systemId, final boolean force, final Map<String, String> properties,
        final ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        this.getProvider(target).stopSystem(systemId, force, properties);

    }

    @Override
    public void restartSystem(final String systemId, final boolean force, final Map<String, String> properties,
        final ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        this.getProvider(target).restartSystem(systemId, force, properties);

    }

    @Override
    public void pauseSystem(final String systemId, final Map<String, String> properties, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void suspendSystem(final String systemId, final Map<String, String> properties, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        this.getProvider(target).suspendSystem(systemId, properties);

    }

    @Override
    public System getSystem(final String systemId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        return this.getProvider(target).getSystem(systemId);
    }

    @Override
    public System.State getSystemState(final String systemId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        return this.getProvider(target).getSystemState(systemId);
    }

    @Override
    public List<? extends CloudCollectionItem> getEntityListFromSystem(final String systemId, final String entityType,
        final ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        return this.getProvider(target).getEntityListFromSystem(systemId, entityType);
    }

    @Override
    public void deleteEntityInSystem(final String systemId, final String entityId, final String entityType,
        final ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        this.getProvider(target).deleteEntityInSystem(systemId, entityId, entityType);

    }

    @Override
    public void removeEntityFromSystem(final String systemId, final String entityId, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");

    }

    @Override
    public void addEntityToSystem(final String systemId, final String entityId, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");

    }

    //
    // Compute Service
    //

    @Override
    public Machine createMachine(final MachineCreate machineCreate, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).createMachine(machineCreate);
    }

    @Override
    public void deleteMachine(final String machineId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        this.getProvider(target).deleteMachine(machineId);
    }

    @Override
    public void startMachine(final String machineId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        this.getProvider(target).startMachine(machineId);
    }

    @Override
    public void stopMachine(final String machineId, final boolean force, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        this.getProvider(target).stopMachine(machineId, force);
    }

    @Override
    public void suspendMachine(final String machineId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        this.getProvider(target).suspendMachine(machineId);
    }

    @Override
    public void restartMachine(final String machineId, final boolean force, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        this.getProvider(target).restartMachine(machineId, force);
    }

    @Override
    public State getMachineState(final String machineId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        return this.getProvider(target).getMachineState(machineId);
    }

    @Override
    public Machine getMachine(final String machineId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        return this.getProvider(target).getMachine(machineId);
    }

    @Override
    public void pauseMachine(final String machineId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public MachineImage captureMachine(final String machineId, final MachineImage machineImage, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void addVolumeToMachine(final String machineId, final MachineVolume machineVolume, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void removeVolumeFromMachine(final String machineId, final MachineVolume machineVolume, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public List<MachineConfiguration> getMachineConfigs(final ProviderTarget provider) throws ConnectorException {
        return Collections.emptyList();
    }

    //
    // Network Service
    //

    @Override
    public List<Network> getNetworks(final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getNetworks();
    }

    @Override
    public Network getNetwork(final String networkId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        return this.getProvider(target).getNetwork(networkId);
    }

    @Override
    public org.ow2.sirocco.cloudmanager.model.cimi.Network.State getNetworkState(final String networkId,
        final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public Network createNetwork(final NetworkCreate networkCreate, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void deleteNetwork(final String networkId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void startNetwork(final String networkId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void stopNetwork(final String networkId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public String createSecurityGroup(final SecurityGroupCreate create, final ProviderTarget target) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SecurityGroup getSecurityGroup(final String groupId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SecurityGroup> getSecurityGroups(final ProviderTarget target) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteRuleFromSecurityGroup(final String groupId, final SecurityGroupRule rule, final ProviderTarget target)
        throws ConnectorException {
        // TODO Auto-generated method stub

    }

    @Override
    public String addRuleToSecurityGroup(final String groupId, final SecurityGroupRule rule, final ProviderTarget target)
        throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteSecurityGroup(final String groupId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        // TODO Auto-generated method stub

    }

    @Override
    public Address allocateAddress(final Map<String, String> properties, final ProviderTarget target) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAddress(final Address address, final ProviderTarget target) throws ConnectorException {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Address> getAddresses(final ProviderTarget target) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addAddressToMachine(final String machineId, final Address address, final ProviderTarget target)
        throws ConnectorException {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAddressFromMachine(final String machineId, final Address address, final ProviderTarget target)
        throws ConnectorException {
        // TODO Auto-generated method stub

    }

    @Override
    public NetworkPort createNetworkPort(final NetworkPortCreate networkPortCreate, final ProviderTarget target)
        throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public NetworkPort getNetworkPort(final String networkPortId, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void deleteNetworkPort(final String networkPortId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void startNetworkPort(final String networkPortId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void stopNetworkPort(final String networkPortId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public ForwardingGroup createForwardingGroup(final ForwardingGroupCreate forwardingGroupCreate, final ProviderTarget target)
        throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public ForwardingGroup getForwardingGroup(final String forwardingGroupId, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void deleteForwardingGroup(final ForwardingGroup forwardingGroup, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void addNetworkToForwardingGroup(final String forwardingGroupId, final ForwardingGroupNetwork fgNetwork,
        final ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void removeNetworkFromForwardingGroup(final String forwardingGroupId, final String networkId,
        final ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    //
    // Image service
    //

    @Override
    public void deleteMachineImage(final String imageId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");

    }

    @Override
    public MachineImage getMachineImage(final String machineImageId, final ProviderTarget target) throws ConnectorException {
        // TODO
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public List<MachineImage> getMachineImages(final boolean returnPublicImages, final Map<String, String> searchCriteria,
        final ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        // TODO
        return Collections.emptyList();
    }

}
