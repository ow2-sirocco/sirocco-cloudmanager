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

package org.ow2.sirocco.cloudmanager.connector.openstack;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
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
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.Network.State;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.woorea.openstack.base.client.OpenStackResponseException;

public class OpenStackCloudProviderConnector implements ICloudProviderConnector, IComputeService, IVolumeService,
    INetworkService, IImageService {
    private static Logger logger = LoggerFactory.getLogger(OpenStackCloudProviderConnector.class);

    private List<OpenStackCloudProvider> openstackCPs = new ArrayList<OpenStackCloudProvider>();

    private synchronized OpenStackCloudProvider getProvider(final ProviderTarget target) throws ConnectorException {
        if (target.getAccount() == null || target.getLocation() == null) {
            throw new ConnectorException("target.account or target.location is null");
        }
        for (Iterator<OpenStackCloudProvider> it = this.openstackCPs.iterator(); it.hasNext();) {
            OpenStackCloudProvider provider = it.next();

            Calendar now = Calendar.getInstance();

            if (provider.getExpirationDate().before(now)) {
                OpenStackCloudProviderConnector.logger.info("OpenStackCloudProvider "
                    + provider.getCloudProviderAccount().getCloudProvider().getDescription() + " token expired");
                it.remove();
                // TODO close provider
                continue;
            }
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
        OpenStackCloudProvider provider = new OpenStackCloudProvider(target);
        this.openstackCPs.add(provider);
        return provider;
    }

    /*
     * TODO 
     * 
     * Mix 
     * - connector cache 
     * - REST call trace (On/Off) 
     * - woorea exception handling 
     * - availability_zone (API for zone) 
     * 
     */

    //
    // ICloudProviderConnector
    //

    @Override
    public IComputeService getComputeService() throws ConnectorException {
        return this;
    }

    @Override
    public IVolumeService getVolumeService() throws ConnectorException {
        return this;
    }

    @Override
    public INetworkService getNetworkService() throws ConnectorException {
        return this;
    }

    @Override
    public Set<CloudProviderLocation> getLocations() {
        return null;
    }

    @Override
    public ISystemService getSystemService() throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public IImageService getImageService() throws ConnectorException {
        return this;
    }

    //
    // Compute Service
    //

    @Override
    public Machine createMachine(final MachineCreate machineCreate, final ProviderTarget target) throws ConnectorException {
        try {
            return this.getProvider(target).createMachine(machineCreate);
        } catch (OpenStackResponseException e) {
            throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new ConnectorException("message=" + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMachine(final String machineId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        try {
            this.getProvider(target).deleteMachine(machineId);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public Machine getMachine(final String machineId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        try {
            return this.getProvider(target).getMachine(machineId);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public Machine.State getMachineState(final String machineId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        try {
            return this.getProvider(target).getMachineState(machineId);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public void restartMachine(final String machineId, final boolean force, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        try {
            this.getProvider(target).restartMachine(machineId, force);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public MachineImage captureMachine(final String machineId, final MachineImage machineImage, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void addVolumeToMachine(final String machineId, final MachineVolume machineVolume, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        try {
            this.getProvider(target).addVolumeToMachine(machineId, machineVolume);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public void removeVolumeFromMachine(final String machineId, final MachineVolume machineVolume, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        try {
            this.getProvider(target).removeVolumeFromMachine(machineId, machineVolume);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public void startMachine(final String machineId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        try {
            this.getProvider(target).startMachine(machineId);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public void stopMachine(final String machineId, final boolean force, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        try {
            this.getProvider(target).stopMachine(machineId, force);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public void pauseMachine(final String machineId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void suspendMachine(final String machineId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public List<MachineConfiguration> getMachineConfigs(final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getMachineConfigs();
    }

    //
    // Volume Service
    //

    @Override
    public Volume createVolume(final VolumeCreate volumeCreate, final ProviderTarget target) throws ConnectorException {
        try {
            return this.getProvider(target).createVolume(volumeCreate);
        } catch (OpenStackResponseException e) {
            throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
        }
    }

    @Override
    public void deleteVolume(final String volumeId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        try {
            this.getProvider(target).deleteVolume(volumeId);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public Volume.State getVolumeState(final String volumeId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        try {
            return this.getProvider(target).getVolumeState(volumeId);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public Volume getVolume(final String volumeId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        try {
            return this.getProvider(target).getVolume(volumeId);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public VolumeImage createVolumeImage(final VolumeImage volumeImage, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public VolumeImage createVolumeSnapshot(final String volumeId, final VolumeImage volumeImage, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public VolumeImage getVolumeImage(final String volumeImageId, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void deleteVolumeImage(final String volumeImageId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    //
    // Network Service
    //

    @Override
    public Network createNetwork(final NetworkCreate networkCreate, final ProviderTarget target) throws ConnectorException {
        try {
            return this.getProvider(target).createNetwork(networkCreate);
        } catch (OpenStackResponseException e) {
            throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new ConnectorException("message=" + e.getMessage(), e);
        }
    }

    @Override
    public Network getNetwork(final String networkId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        try {
            return this.getProvider(target).getNetwork(networkId);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public State getNetworkState(final String networkId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        try {
            return this.getProvider(target).getNetworkState(networkId);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public List<Network> getNetworks(final ProviderTarget target) throws ConnectorException {
        try {
            return this.getProvider(target).getNetworks();
        } catch (OpenStackResponseException e) {
            throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
        }
    }

    @Override
    public void deleteNetwork(final String networkId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        try {
            this.getProvider(target).deleteNetwork(networkId);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
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

    //
    // Network : Security Group
    //

    @Override
    public String createSecurityGroup(final SecurityGroupCreate create, final ProviderTarget target) throws ConnectorException {
        try {
            return this.getProvider(target).createSecurityGroup(create);
        } catch (OpenStackResponseException e) {
            throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
        }
    }

    @Override
    public void deleteSecurityGroup(final String groupId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        try {
            this.getProvider(target).deleteSecurityGroup(groupId);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public SecurityGroup getSecurityGroup(final String groupId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        try {
            return this.getProvider(target).getSecurityGroup(groupId);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public List<SecurityGroup> getSecurityGroups(final ProviderTarget target) throws ConnectorException {
        try {
            return this.getProvider(target).getSecurityGroups();
        } catch (OpenStackResponseException e) {
            throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
        }
    }

    @Override
    public void deleteRuleFromSecurityGroup(final String groupId, final SecurityGroupRule rule, final ProviderTarget target)
        throws ConnectorException {
        // TODO
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public String addRuleToSecurityGroup(final String groupId, final SecurityGroupRule rule, final ProviderTarget target)
        throws ConnectorException {
        // TODO
        throw new ConnectorException("unsupported operation");
    }

    //
    // Network : (floating IP) Address
    //

    @Override
    public List<Address> getAddresses(final ProviderTarget target) throws ConnectorException {
        try {
            return this.getProvider(target).getAddresses();
        } catch (OpenStackResponseException e) {
            throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
        }
    }

    @Override
    public Address allocateAddress(final Map<String, String> properties, final ProviderTarget target) throws ConnectorException {
        try {
            return this.getProvider(target).allocateAddress(properties);
        } catch (OpenStackResponseException e) {
            throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
        }
    }

    @Override
    public void deallocateAddress(final Address address, final ProviderTarget target) throws ConnectorException {
        try {
            this.getProvider(target).deallocateAddress(address);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public void addAddressToMachine(final String machineId, final Address address, final ProviderTarget target)
        throws ConnectorException {
        try {
            this.getProvider(target).addAddressToMachine(machineId, address);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public void removeAddressFromMachine(final String machineId, final Address address, final ProviderTarget target)
        throws ConnectorException {
        try {
            this.getProvider(target).removeAddressFromMachine(machineId, address);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404) {
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            } else {
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
            }
        }
    }

    //
    // Network : Forwarding Group
    //

    @Override
    public ForwardingGroup createForwardingGroup(final ForwardingGroupCreate forwardingGroupCreate, final ProviderTarget target)
        throws ConnectorException {
        try {
            return this.getProvider(target).createForwardingGroup(forwardingGroupCreate);
        } catch (OpenStackResponseException e) {
            throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
        }
    }

    @Override
    public ForwardingGroup getForwardingGroup(final String forwardingGroupId, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void deleteForwardingGroup(final ForwardingGroup forwardingGroup, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        try {
            this.getProvider(target).deleteForwardingGroup(forwardingGroup);
        } catch (OpenStackResponseException e) {
            throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
        }
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
    // Network : Port
    //

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

    //
    // Image service
    //

    @Override
    public MachineImage getMachineImage(final String machineImageId, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        return this.getProvider(target).getMachineImage(machineImageId);
    }

    @Override
    public List<MachineImage> getMachineImages(final boolean returnPublicImages, final Map<String, String> searchCriteria,
        final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getMachineImages(returnPublicImages, searchCriteria);
    }

    @Override
    public void deleteMachineImage(final String imageId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        this.getProvider(target).deleteMachineImage(imageId);

    }

}
