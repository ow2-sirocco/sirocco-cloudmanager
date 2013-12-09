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
        for (OpenStackCloudProvider provider : this.openstackCPs) {
            /*
             * if
             * (provider.getCloudProviderAccount().equals(target.getAccount())
             * &&
             * provider.getCloudProviderLocation().equals(target.getLocation()))
             * { return provider; }
             */
            if (provider.getCloudProviderAccount().getId().equals(target.getAccount().getId())) {
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
     * TODO Mix 
     * - connector cache 
     * - REST call trace (On/Off) 
     * - woorea exception handling 
     * - availability_zone (API for zone) 
     * 
     * Compute 
     * - reboot: not supported by woorea 
     * - CIMI address allocation mode : dynamic / fixed 
     * - captureMachine 
     * 
     * Network 
     * - createNetwork/deleteNetwork: woorea bugs : see fixme 
     * - Forwarding groups / BagPipe
     * 
     * Image
     * - deleteMachineImage
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
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void stopMachine(final String machineId, final boolean force, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
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
    public ForwardingGroup createForwardingGroup(final ForwardingGroupCreate forwardingGroupCreate, final ProviderTarget target)
        throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ForwardingGroup getForwardingGroup(final String forwardingGroupId, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteForwardingGroup(final ForwardingGroup forwardingGroup, final ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        // TODO Auto-generated method stub

    }

    @Override
    public void addNetworkToForwardingGroup(final String forwardingGroupId, final ForwardingGroupNetwork fgNetwork,
        final ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeNetworkFromForwardingGroup(final String forwardingGroupId, final String networkId,
        final ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        // TODO Auto-generated method stub

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
        throw new ConnectorException("unsupported operation");

    }

}
