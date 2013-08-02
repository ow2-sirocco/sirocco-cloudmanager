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
import java.util.Set;

import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.connector.api.IImageService;
import org.ow2.sirocco.cloudmanager.connector.api.INetworkService;
import org.ow2.sirocco.cloudmanager.connector.api.IProviderCapability;
import org.ow2.sirocco.cloudmanager.connector.api.ISystemService;
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.connector.api.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
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

public class OpenStackCloudProviderConnector implements ICloudProviderConnector, IComputeService, IVolumeService, INetworkService {
    private static Logger logger = LoggerFactory.getLogger(OpenStackCloudProviderConnector.class);
    
    private List<OpenStackCloudProvider> openstackCPs = new ArrayList<OpenStackCloudProvider>();

    private synchronized OpenStackCloudProvider getProvider(final ProviderTarget target) throws ConnectorException { 
    	if (target.getAccount() == null || target.getLocation() == null){
            throw new ConnectorException("target.account or target.location is null");
    	}
        for (OpenStackCloudProvider provider : this.openstackCPs) {
            /*if (provider.getCloudProviderAccount().equals(target.getAccount())
                && provider.getCloudProviderLocation().equals(target.getLocation())) {
                return provider;
            }*/
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
    
    /* TODO
     * Network
     * - Quantum
     * 
     * Compute
     * - reboot: when supported by woorea 
     * - Network with/without Quantum
     * 
     * Mix
     * - connector cache 
     * - code format
     * - REST call trace (On/Off)
     * - woorea exception handling
     * - availability_zone (API for zone)
     * - CIMI status mapping : server, volume, network
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
	public IProviderCapability getProviderCapability()
			throws ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public ISystemService getSystemService() throws ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public IImageService getImageService() throws ConnectorException {
        throw new ConnectorException("unsupported operation");
	}


    //
    // Compute Service
    //

	@Override
	public Machine createMachine(MachineCreate machineCreate,
			ProviderTarget target) throws ConnectorException {
        try {
			return this.getProvider(target).createMachine(machineCreate); 
		} catch (OpenStackResponseException e) {
			throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
		} catch (InterruptedException e) {
			throw new ConnectorException("message=" + e.getMessage(), e);
		}
	}

	@Override
	public void deleteMachine(String machineId, ProviderTarget target)
			throws ResourceNotFoundException, ConnectorException {
        try {
			this.getProvider(target).deleteMachine(machineId);
		} catch (OpenStackResponseException e) {
	        if (e.getStatus() == 404){
				throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
	        else{
				throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
		}
	}

	@Override
	public Machine getMachine(String machineId, ProviderTarget target)
			 throws ResourceNotFoundException, ConnectorException {
        try {
			return this.getProvider(target).getMachine(machineId);
		} catch (OpenStackResponseException e) {
	        if (e.getStatus() == 404){
				throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
	        else{
				throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
		}
	}

	@Override
	public Machine.State getMachineState(String machineId, ProviderTarget target)
			throws ResourceNotFoundException, ConnectorException {
        try {
			return this.getProvider(target).getMachineState(machineId);
		} catch (OpenStackResponseException e) {
	        if (e.getStatus() == 404){
				throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
	        else{
				throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
		}
	}

	@Override
	public void restartMachine(String machineId, boolean force,
			ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        try {
			this.getProvider(target).restartMachine(machineId, force);
		} catch (OpenStackResponseException e) {
	        if (e.getStatus() == 404){
				throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
	        else{
				throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
		}
	}

	@Override
	public MachineImage captureMachine(String machineId,
			MachineImage machineImage, ProviderTarget target)
					throws ResourceNotFoundException, ConnectorException {
		// TODO
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public void addVolumeToMachine(final String machineId, final MachineVolume machineVolume, ProviderTarget target) 
			throws ResourceNotFoundException, ConnectorException {
        try {
			this.getProvider(target).addVolumeToMachine(machineId, machineVolume);
		} catch (OpenStackResponseException e) {
	        if (e.getStatus() == 404){
				throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
	        else{
				throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
		}
	}

	@Override
	public void removeVolumeFromMachine(String machineId,
			MachineVolume machineVolume, ProviderTarget target)
					throws ResourceNotFoundException, ConnectorException {
        try {
			this.getProvider(target).removeVolumeFromMachine(machineId, machineVolume);
		} catch (OpenStackResponseException e) {
	        if (e.getStatus() == 404){
				throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
	        else{
				throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
		}
	}

	@Override
	public void startMachine(String machineId, ProviderTarget target)
			throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public void stopMachine(String machineId, boolean force,
			ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public void pauseMachine(String machineId, ProviderTarget target)
			throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public void suspendMachine(String machineId, ProviderTarget target)
			throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

    //
    // Volume Service
    //

	@Override
	public Volume createVolume(VolumeCreate volumeCreate, ProviderTarget target)
			throws ConnectorException {
        try {
			return this.getProvider(target).createVolume(volumeCreate); 
		} catch (OpenStackResponseException e) {
			throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);
		}
	}

	@Override
	public void deleteVolume(String volumeId, ProviderTarget target)
			throws ResourceNotFoundException, ConnectorException {
        try {
			this.getProvider(target).deleteVolume(volumeId);
		} catch (OpenStackResponseException e) {
	        if (e.getStatus() == 404){
				throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
	        else{
				throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
		}
	}

	@Override
	public Volume.State getVolumeState(String volumeId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        try {
			return this.getProvider(target).getVolumeState(volumeId);
		} catch (OpenStackResponseException e) {
	        if (e.getStatus() == 404){
				throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
	        else{
				throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
		}
	}

	@Override
	public Volume getVolume(String volumeId, ProviderTarget target)
			throws ResourceNotFoundException, ConnectorException {
        try { 
			return this.getProvider(target).getVolume(volumeId);
		} catch (OpenStackResponseException e) {
	        if (e.getStatus() == 404){
				throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
	        else{
				throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);	        	
	        }
		}
	}

	@Override
	public VolumeImage createVolumeImage(VolumeImage volumeImage,
			ProviderTarget target) throws ResourceNotFoundException,
			ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public VolumeImage createVolumeSnapshot(String volumeId,
			VolumeImage volumeImage, ProviderTarget target)
			throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public VolumeImage getVolumeImage(String volumeImageId,
			ProviderTarget target) throws ResourceNotFoundException,
			ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public void deleteVolumeImage(String volumeImageId, ProviderTarget target)
			throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

    //
    // Network Service
    //

    @Override
    public Network createNetwork(NetworkCreate networkCreate, ProviderTarget target) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Network getNetwork(String networkId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public State getNetworkState(String networkId, ProviderTarget target) throws ConnectorException {
        try {
            return this.getProvider(target).getNetworkState(networkId);
        } catch (OpenStackResponseException e) {
            if (e.getStatus() == 404){
                throw new ResourceNotFoundException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);               
            }
            else{
                throw new ConnectorException("cause=" + e.getStatus() + ", message=" + e.getMessage(), e);              
            }
        }
    }

    @Override
    public List<Network> getNetworks(ProviderTarget target) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteNetwork(String networkId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ForwardingGroup createForwardingGroup(ForwardingGroupCreate forwardingGroupCreate, ProviderTarget target)
        throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ForwardingGroup getForwardingGroup(String forwardingGroupId, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteForwardingGroup(String forwardingGroupId, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addNetworkToForwardingGroup(String forwardingGroupId, ForwardingGroupNetwork fgNetwork, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeNetworkFromForwardingGroup(String forwardingGroupId, String networkId, ProviderTarget target)
        throws ResourceNotFoundException, ConnectorException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startNetwork(String networkId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void stopNetwork(String networkId, ProviderTarget target) throws ResourceNotFoundException, ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public NetworkPort createNetworkPort(NetworkPortCreate networkPortCreate, ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public NetworkPort getNetworkPort(String networkPortId, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void deleteNetworkPort(String networkPortId, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void startNetworkPort(String networkPortId, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void stopNetworkPort(String networkPortId, ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        throw new ConnectorException("unsupported operation");
    }
}
