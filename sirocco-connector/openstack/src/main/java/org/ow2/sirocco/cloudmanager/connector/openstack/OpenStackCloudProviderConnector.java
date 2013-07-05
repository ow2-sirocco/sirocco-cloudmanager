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
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.woorea.openstack.base.client.OpenStackResponseException;

public class OpenStackCloudProviderConnector implements ICloudProviderConnector, IComputeService {
    private static Logger logger = LoggerFactory.getLogger(OpenStackCloudProviderConnector.class);
    
    private List<OpenStackCloudProvider> openstackCPs = new ArrayList<OpenStackCloudProvider>();

    private synchronized OpenStackCloudProvider getProvider(final ProviderTarget target) throws ConnectorException { // FIXME exception
    	if (target.getAccount() == null || target.getLocation() == null){
            throw new ConnectorException("target.account or target.location is null");
    	}
        for (OpenStackCloudProvider provider : this.openstackCPs) {
            if (provider.getCloudProviderAccount().equals(target.getAccount())
                && provider.getCloudProviderLocation().equals(target.getLocation())) {
                return provider;
            }
        }
        OpenStackCloudProvider provider = new OpenStackCloudProvider(target);
        this.openstackCPs.add(provider);
        return provider;
    }
    
    /* TODO
     * MIX
     * - connector cache
     * - code format
     * - REST call trace (On/Off)
     * - woorea exception handling
     * - availability_zone (API for zone)
     * 
     * COMPUTE, VOLUME, NETWORK
     * - disk / ephemeral 
     * - network : simple mode convention (name...)
     * - Network : Quantum
     * - volume
     */

    
    //
    // ICloudProviderConnector
    //
    
	@Override
	public IComputeService getComputeService() throws ConnectorException {
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
	public IVolumeService getVolumeService() throws ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public INetworkService getNetworkService() throws ConnectorException {
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
			throws ConnectorException {
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
			 throws ConnectorException {
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
	public State getMachineState(String machineId, ProviderTarget target)
			throws ConnectorException {
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
			ProviderTarget target) throws ConnectorException {
		// TODO
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public MachineImage captureMachine(String machineId,
			MachineImage machineImage, ProviderTarget target)
			throws ConnectorException {
		// TODO
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public void addVolumeToMachine(String arg0, MachineVolume arg1,
			ProviderTarget arg2) throws ConnectorException {
		// TODO
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public void removeVolumeFromMachine(String machineId,
			MachineVolume machineVolume, ProviderTarget target)
			throws ConnectorException {
		// TODO
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public void startMachine(String machineId, ProviderTarget target)
			throws ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public void stopMachine(String machineId, boolean force,
			ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public void pauseMachine(String machineId, ProviderTarget target)
			throws ConnectorException {
        throw new ConnectorException("unsupported operation");
	}

	@Override
	public void suspendMachine(String machineId, ProviderTarget target)
			throws ConnectorException {
        throw new ConnectorException("unsupported operation");
	}
}
