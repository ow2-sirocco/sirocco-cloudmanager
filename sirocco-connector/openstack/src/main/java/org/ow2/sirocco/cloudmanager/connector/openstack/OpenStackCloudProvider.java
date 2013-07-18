package org.ow2.sirocco.cloudmanager.connector.openstack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.connector.api.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.woorea.openstack.base.client.OpenStackResponseException;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;
import com.woorea.openstack.keystone.utils.KeystoneUtils;
import com.woorea.openstack.nova.Nova;
import com.woorea.openstack.nova.model.Flavor;
import com.woorea.openstack.nova.model.FloatingIp;
import com.woorea.openstack.nova.model.KeyPair;
import com.woorea.openstack.nova.model.Server;
import com.woorea.openstack.nova.model.Server.Addresses.Address;
import com.woorea.openstack.nova.model.ServerForCreate;
import com.woorea.openstack.nova.model.VolumeAttachment;
import com.woorea.openstack.nova.model.VolumeAttachments;
import com.woorea.openstack.nova.model.VolumeForCreate;

public class OpenStackCloudProvider {
    private static Logger logger = LoggerFactory.getLogger(OpenStackCloudProvider.class);

    private static int DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS = 240;

    private CloudProviderAccount cloudProviderAccount;

	private CloudProviderLocation cloudProviderLocation;
	
    private Map<String, String> keyPairMap = new HashMap<String, String>();

    private String tenantName;
	
    //private String novaEndPointName;
    
    private Nova novaClient;

    private Network cimiPrivateNetwork, cimiPublicNetwork;

	public OpenStackCloudProvider(final ProviderTarget target) throws ConnectorException {
		this.cloudProviderAccount = target.getAccount();
		this.cloudProviderLocation = target.getLocation();

        Map<String, String> properties = cloudProviderAccount.getCloudProvider().getProperties();
        if (properties == null || properties.get("tenantName") == null) {
            throw new ConnectorException("No access to properties: tenantName");
        }
        this.tenantName = properties.get("tenantName");
        logger.info("connect: " + cloudProviderAccount.getLogin() + ":" + cloudProviderAccount.getPassword() 
        		+ " to tenant=" + this.tenantName 
        		+ ", KEYSTONE_AUTH_URL=" + cloudProviderAccount.getCloudProvider().getEndpoint());		
        
        //
		Keystone keystone = new Keystone(cloudProviderAccount.getCloudProvider().getEndpoint());
		Access access = keystone.tokens().authenticate(new UsernamePassword(cloudProviderAccount.getLogin(), cloudProviderAccount.getPassword()))
				.withTenantName(this.tenantName)
				.execute();
				
		//use the token in the following requests
		keystone.token(access.getToken().getId());
		
		//java.lang.System.out.println("1=" + KeystoneUtils.findEndpointURL(access.getServiceCatalog(), "compute", null, "public"));
		
		//this.novaClient = new Nova("http://10.192.133.101:8774/v2".concat("/").concat(access.getToken().getTenant().getId())); /// tmp 
		this.novaClient = new Nova(KeystoneUtils.findEndpointURL(access.getServiceCatalog(), "compute", null, "public"));  
		this.novaClient.token(access.getToken().getId());
				
		//novaClient.enableLogging(Logger.getLogger("nova"), 100 * 1024); // check how to trace REST call (On/Off)
		
		/*Flavors flavors = novaClient.flavors().list(true).execute();
		for(Flavor flavor : flavors) {
			System.out.println(flavor);
		}*/
		
		/*Images images = novaClient.images().list(true).execute();
		for(Image image : images) {
			System.out.println(image);
		}*/

        // FIXME without Quantum: mapping to a backend network
		this.cimiPrivateNetwork = new Network();
        this.cimiPrivateNetwork.setProviderAssignedId("0");
        this.cimiPrivateNetwork.setState(Network.State.STARTED);
        this.cimiPrivateNetwork.setNetworkType(Network.Type.PRIVATE);

        this.cimiPublicNetwork = new Network();
        this.cimiPublicNetwork.setProviderAssignedId("1");
        this.cimiPublicNetwork.setState(Network.State.STARTED);
        this.cimiPublicNetwork.setNetworkType(Network.Type.PUBLIC);

	}

    public CloudProviderAccount getCloudProviderAccount() {
		return cloudProviderAccount;
	}

	public CloudProviderLocation getCloudProviderLocation() {
		return cloudProviderLocation;
	}

    //
    // Compute Service
    //

	public Machine.State fromServerStatusToMachineState(final String novaStatus) {
    	if (novaStatus.equalsIgnoreCase("ACTIVE")){
            return Machine.State.STARTED;
    	} else if (novaStatus.equalsIgnoreCase("BUILD")){
            return Machine.State.CREATING;
    	} else if (novaStatus.equalsIgnoreCase("DELETED")){
            return Machine.State.DELETED;
    	} else if (novaStatus.equalsIgnoreCase("HARD_REBOOT")){
            return Machine.State.STARTED;
    	} else if (novaStatus.equalsIgnoreCase("PASSWORD")){
            return Machine.State.STARTED;
    	} else if (novaStatus.equalsIgnoreCase("REBOOT")){
            return Machine.State.STARTED;
    	} else if (novaStatus.equalsIgnoreCase("SUSPENDED")){
            return Machine.State.STOPPED;
    	} else {
            return Machine.State.ERROR; // CIMI mapping!
    	}
	}

    private void fromServerToMachine(final String serverId, final Machine machine) {
        Server server = novaClient.servers().show(serverId).execute(); // get a fresh server
    	
    	machine.setProviderAssignedId(serverId);
    	machine.setName(server.getName());
        machine.setState(this.fromServerStatusToMachineState(server.getStatus())); 

        // HW
        //Flavor flavor = server.getFlavor(); // doesn't work (check if woorea support a lazy instantiation mode (of the object of the model) 
        Flavor flavor = novaClient.flavors().show(server.getFlavor().getId()).execute();
        /*logger.info("flavor: " + flavor);*/		

        machine.setCpu(new Integer(flavor.getVcpus()));
        machine.setMemory(flavor.getRam() * 1024);
        List<MachineDisk> machineDisks = new ArrayList<MachineDisk>();
        MachineDisk machineDisk = new MachineDisk();
        machineDisk.setCapacity(new Integer(flavor.getDisk()) * 1000);  
        machineDisks.add(machineDisk);
        if (flavor.getEphemeral() > 0){
            MachineDisk machineEphemeralDisk = new MachineDisk();
            machineEphemeralDisk.setCapacity(flavor.getEphemeral() * 1000);  
            machineDisks.add(machineEphemeralDisk);
        	
        }
        machine.setDisks(machineDisks);

        // Network (without Quantum)
        // assumption: first IP address is private, next addresses are public (floating IPs)
        List<MachineNetworkInterface> nics = new ArrayList<MachineNetworkInterface>();
        machine.setNetworkInterfaces(nics);
        MachineNetworkInterface privateNic = new MachineNetworkInterface();
        privateNic.setAddresses(new ArrayList<MachineNetworkInterfaceAddress>());
        privateNic.setNetwork(this.cimiPrivateNetwork);
        //privateNic.setNetworkType(Network.Type.PRIVATE);
        privateNic.setState(MachineNetworkInterface.InterfaceState.ACTIVE);
        MachineNetworkInterface publicNic = new MachineNetworkInterface();
        publicNic.setAddresses(new ArrayList<MachineNetworkInterfaceAddress>());
        publicNic.setNetwork(this.cimiPublicNetwork);
        //publicNic.setNetworkType(Network.Type.PUBLIC);
        publicNic.setState(MachineNetworkInterface.InterfaceState.ACTIVE);
         for (String networkType : server.getAddresses().getAddresses().keySet()) {
            Collection<Address> addresses = server.getAddresses().getAddresses().get(networkType);
            logger.info("-- " + addresses);
            Iterator<Address> iterator = addresses.iterator();
            if (iterator.hasNext()) {
                this.addAddress(iterator.next(), this.cimiPrivateNetwork, privateNic);
            }
            while (iterator.hasNext()) {
                this.addAddress(iterator.next(), this.cimiPublicNetwork, publicNic);
            }
        }
        if (privateNic.getAddresses().size() > 0) {
            nics.add(privateNic);
        }
        if (publicNic.getAddresses().size() > 0) {
            nics.add(publicNic);
        }
        
        // Volume
        List<MachineVolume> machineVolumes = new ArrayList<MachineVolume>();
        machine.setVolumes(machineVolumes);
        VolumeAttachments volumeAttachments = novaClient.servers().listVolumeAttachments(serverId).execute();
        Iterator<VolumeAttachment> iterator = volumeAttachments.iterator();
        while (iterator.hasNext()) {
        	VolumeAttachment volumeAttachment = iterator.next();
            MachineVolume machineVolume = new MachineVolume(); 
            Volume volume = this.getVolume(volumeAttachment.getVolumeId()); 
            machineVolume.setVolume(volume);
            machineVolume.setProviderAssignedId(volumeAttachment.getId());
            machineVolume.setState(MachineVolume.State.ATTACHED);
            machineVolume.setInitialLocation(volumeAttachment.getDevice());
            machineVolumes.add(machineVolume);
        }
        
        /* FIXME 
         * - Network with Quantum
         * */        
    }

	public Machine createMachine(MachineCreate machineCreate) throws ConnectorException, InterruptedException {
        logger.info("creating Machine for " + cloudProviderAccount.getLogin());

        ServerForCreate serverForCreate = new ServerForCreate();

        String serverName = null;
        if (machineCreate.getName() != null) {
            serverName = machineCreate.getName() + "-" + UUID.randomUUID();
        } else {
            serverName = "sirocco-" + UUID.randomUUID();
        }
        serverForCreate.setName(serverName);
        
        String flavorId = this.findSuitableFlavor(machineCreate.getMachineTemplate().getMachineConfig());
        if (flavorId == null) {
            throw new ConnectorException("Cannot find Nova flavor matching machineConfig");
        }
        serverForCreate.setFlavorRef(flavorId);

        String imageIdKey = "openstack";
        String imageId = machineCreate.getMachineTemplate().getMachineImage().getProperties().get(imageIdKey);
        if (imageId == null) {
            throw new ConnectorException("Cannot find imageId for key " + imageIdKey);
        }
        serverForCreate.setImageRef(imageId);
        
        String keyPairName = null;
        if (machineCreate.getMachineTemplate().getCredential() != null) {
            //String publicKey = new String(machineCreate.getMachineTemplate().getCredential().getPublicKey());
            String publicKey = machineCreate.getMachineTemplate().getCredential().getPublicKey();
            keyPairName = this.getKeyPair(publicKey);
        }
        if (keyPairName != null) {
        	serverForCreate.setKeyName(keyPairName);
        }
        
        serverForCreate.getSecurityGroups()
            .add(new ServerForCreate.SecurityGroup("default")); // default security group

        String userData = machineCreate.getMachineTemplate().getUserData();
        if (userData != null) {
        	byte[] encoded = Base64.encodeBase64(userData.getBytes());
        	userData = new String(encoded);
        	serverForCreate.setUserData(userData);
        }
        
        Server server = novaClient.servers().boot(serverForCreate).execute(); // to get the server id
        //logger.info(server);
        server = novaClient.servers().show(server.getId()).execute(); // to get detailed information about the server 
        //logger.info(server);

        // public IP
        boolean allocateFloatingIp = false;
        if (machineCreate.getMachineTemplate().getNetworkInterfaces() != null) {
            for (MachineTemplateNetworkInterface nic : machineCreate.getMachineTemplate().getNetworkInterfaces()) {
                // NB: nic template could refer either to a Network resource xor a SystemNetworkName
            	// In practice templates (generated by Sirocco) should refer to a Network resource when using an OpenStack connector 
                if (nic.getNetwork().getNetworkType() == Network.Type.PUBLIC) {
                    allocateFloatingIp = true;
                    break;
                }
            }
        }
        if (allocateFloatingIp) {
            addFloatingIPToMachine(server.getId());
        }

        final Machine machine = new Machine();
        fromServerToMachine(server.getId(), machine); 
        return machine;
        
        /* FIXME 
         * - Network with Quantum & conventions without Quantum
         * */        
	}

	public Machine getMachine(String machineId)  {
        final Machine machine = new Machine();
        fromServerToMachine(machineId, machine); 
        return machine;
	} 

	public Machine.State getMachineState(String machineId) {
        //Server server = getServer(machineId);
        Server server = novaClient.servers().show(machineId).execute(); 
		return fromServerStatusToMachineState(server.getStatus());
	}

	public void deleteMachine(String machineId) {
		this.freeFloatingIpsFromServer(machineId);
        novaClient.servers().delete(machineId).execute();  
	}	

	public void restartMachine(String machineId, boolean force) throws ConnectorException {
		//novaClient.servers().reboot(machineId, << force ? Reboot HARD : Reboot SOFT >>).execute(); // TODO when supported by woorea (RebootAction, RebootType)
        throw new ConnectorException("unsupported operation");
	}
	
	public void addVolumeToMachine(String machineId, MachineVolume machineVolume) throws ConnectorException {
        String device = machineVolume.getInitialLocation();  
        if (device == null) {
            throw new ConnectorException("device not specified");
        }
		novaClient.servers().attachVolume(machineId, machineVolume.getVolume().getProviderAssignedId(), device).execute();
	}
	
	public void removeVolumeFromMachine(String machineId, MachineVolume machineVolume) {
		novaClient.servers().detachVolume(machineId, machineVolume.getProviderAssignedId());
	}
    
    private void addAddress(final Address address, final Network cimiNetwork, final MachineNetworkInterface nic) {
        org.ow2.sirocco.cloudmanager.model.cimi.Address cimiAddress = new org.ow2.sirocco.cloudmanager.model.cimi.Address();
        cimiAddress.setIp(address.getAddr());
        cimiAddress.setNetwork(cimiNetwork);
        cimiAddress.setAllocation("dynamic");
        cimiAddress.setProtocol("IPv4");
        cimiAddress.setResource(cimiNetwork);
        MachineNetworkInterfaceAddress entry = new MachineNetworkInterfaceAddress();
        entry.setAddress(cimiAddress);
        nic.getAddresses().add(entry);
    }
	
	/*public Server getServer(String machineId) throws ConnectorException {
		try {
			return novaClient.servers().show(machineId).execute();
		} catch (OpenStackResponseException e) {
	        System.out.println("- " + e.getMessage() 
	        		+ ", " + e.getStatus()
	        		+ ", " + e.getLocalizedMessage()
	        		+ ", " + e.getCause()
	        		);
	        if (e.getStatus() == 404){
				throw new ResourceNotFoundException(e);	        	
	        }
	        else{
				throw new ConnectorException(e);	        	
	        }
		}
	}*/

    private String findSuitableFlavor(final MachineConfiguration machineConfig) {
        for (Flavor flavor : novaClient.flavors().list(true).execute()) {
            long memoryInKBytes = machineConfig.getMemory();
            long flavorMemoryInKBytes = flavor.getRam() * 1024;
            /*System.out.println(
            		"memoryInKBytes=" + memoryInKBytes 
            		+ ", flavorMemoryInKBytes=" + flavorMemoryInKBytes
            		);*/
            if (memoryInKBytes == flavorMemoryInKBytes) {
            	Integer flavorCpu = new Integer(flavor.getVcpus());
                //if (machineConfig.getCpu() == flavor.getVcpus()) {
            	/*System.out.println(
                		"Cpu()=" + machineConfig.getCpu() 
                		+ ", flavorCpu=" + flavorCpu
                		);*/
                if (machineConfig.getCpu().intValue() == flavorCpu.intValue()) {
                	/*System.out.println(
                    		"machineConfig.getDisks().size()=" + machineConfig.getDisks().size()
                    		);*/
                	/*if (machineConfig.getDisks().size() == 0) { 
                		return flavor.getId();
                	}
                	else */
                	if (machineConfig.getDisks().size() == 1 && flavor.getEphemeral() == 0) {
                        long diskSizeInKBytes = machineConfig.getDisks().get(0).getCapacity();
                        long flavorDiskSizeInKBytes = Long.parseLong(flavor.getDisk()) * 1000 * 1000;
                        if (diskSizeInKBytes == flavorDiskSizeInKBytes) {
                            return flavor.getId();
                        }
                    } else if (machineConfig.getDisks().size() == 2 && flavor.getEphemeral() > 0) {
                        long diskSizeInKBytes = machineConfig.getDisks().get(0).getCapacity();
                        long flavorDiskSizeInKBytes = Long.parseLong(flavor.getDisk()) * 1000 * 1000;
                        if (diskSizeInKBytes == flavorDiskSizeInKBytes) {
                            diskSizeInKBytes = machineConfig.getDisks().get(1).getCapacity();
                            flavorDiskSizeInKBytes = flavor.getEphemeral().longValue() * 1000 * 1000;
                            if (diskSizeInKBytes == flavorDiskSizeInKBytes) {
                                return flavor.getId();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getKeyPair(final String publicKey) {
        String keyPairName = OpenStackCloudProvider.this.keyPairMap.get(publicKey);
        if (keyPairName != null) {
            return keyPairName;
        }

        for (KeyPair keyPair : novaClient.keyPairs().list().execute()) {
            if (keyPair.getPublicKey().equals(publicKey)) {
            	OpenStackCloudProvider.this.keyPairMap.put(publicKey, keyPair.getName());
                return keyPair.getName();
            }
        }

        KeyPair newKeyPair = novaClient.keyPairs().create("keypair-" + UUID.randomUUID().toString(), publicKey).execute();
        OpenStackCloudProvider.this.keyPairMap.put(publicKey, newKeyPair.getName());
        return newKeyPair.getName();
    }
    
    private String addFloatingIPToMachine(final String serverId) throws InterruptedException {
    	int waitTimeInSeconds = DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;    	
        do {
            Server server = novaClient.servers().show(serverId).execute();  
            if (!server.getStatus().equalsIgnoreCase("BUILD")) {
                break;
            }
            Thread.sleep(1000);
        } while (waitTimeInSeconds-- > 0);
        
    	FloatingIp floatingIp = novaClient.floatingIps().allocate(null).execute();
        logger.info("Allocating floating IP " + floatingIp.getIp());
        novaClient.servers().associateFloatingIp(serverId, floatingIp.getIp()).execute();
        
        // Check if it is safe not to wait that the floating IP shows up in the server detail
        /*do {
            Server server = novaClient.servers().show(serverId).execute();  
            if (this.findIpAddressOnServer(server, floatingIp.getIp())) {
            	logger.info("Floating IP " + floatingIp.getIp() + " attached to server " + serverId);
                break;
            }
            Thread.sleep(1000);
        } while (waitTimeInSeconds-- > 0);*/
        
        return floatingIp.getIp();
    }

    private boolean findIpAddressOnServer(final Server server, final String ip) {
        for (String networkType : server.getAddresses().getAddresses().keySet()) {
            Collection<Address> addresses = server.getAddresses().getAddresses().get(networkType);
            for (Address address : addresses) {
                if (address.getAddr().equals(ip)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void freeFloatingIpsFromServer(final String serverId) {
        for (FloatingIp floatingIp : novaClient.floatingIps().list().execute()) {
            if (floatingIp.getInstanceId() != null && floatingIp.getInstanceId().equals(serverId)) {
                logger.info("Releasing floating IP " + floatingIp.getIp()
                    + " from server " + serverId);
                novaClient.servers().disassociateFloatingIp(serverId, floatingIp.getIp()).execute();
                novaClient.floatingIps().deallocate(floatingIp.getId()).execute();
            }
        }
    }

    //
    // Volume
    //

    private Volume.State fromNovaVolumeStatusToCimiVolumeState(final String novaStatus) {
    	if (novaStatus.equalsIgnoreCase("AVAILABLE")){
            return Volume.State.AVAILABLE;
    	} else if (novaStatus.equalsIgnoreCase("CREATING")){
            return Volume.State.CREATING;
    	} else if (novaStatus.equalsIgnoreCase("DELETING")){
            return Volume.State.DELETING;
    	} else if (novaStatus.equalsIgnoreCase("IN-USE")){ 
            return Volume.State.AVAILABLE; 
    	} else {
            return Volume.State.ERROR; // CIMI mapping!
    	}
    }

    private void fromNovaVolumeToCimiVolume(final String volumeId, final Volume cimiVolume) { 
		com.woorea.openstack.nova.model.Volume novaVolume = novaClient.volumes().show(volumeId).execute();

		cimiVolume.setName(novaVolume.getName());
		cimiVolume.setDescription(novaVolume.getDescription());
		cimiVolume.setProviderAssignedId(novaVolume.getId());
        //cimiVolume.setState(this.getVolumeState(volumeId));
        cimiVolume.setState(fromNovaVolumeStatusToCimiVolumeState(novaVolume.getStatus()));
        // GB to KB
        cimiVolume.setCapacity(novaVolume.getSize() * 1000 * 1000);
    }
    
	public Volume createVolume(VolumeCreate volumeCreate) throws ConnectorException{
        logger.info("creating Machine for " + cloudProviderAccount.getLogin());

        VolumeForCreate volumeForCreate = new VolumeForCreate();
        
        String volumeName = null;
        if (volumeCreate.getName() != null) {
        	volumeName = volumeCreate.getName() + "-" + UUID.randomUUID();
        } else {
        	volumeName = "sirocco-" + UUID.randomUUID();
        }
        volumeForCreate.setName(volumeName);
        volumeForCreate.setDescription(volumeCreate.getDescription()); 

        VolumeConfiguration volumeConfig = volumeCreate.getVolumeTemplate().getVolumeConfig();
        int sizeInGB = volumeConfig.getCapacity() / (1000 * 1000);
        volumeForCreate.setSize(new Integer(sizeInGB));
        
        com.woorea.openstack.nova.model.Volume novaVolume = novaClient.volumes().create(volumeForCreate).execute(); 
		
        final Volume cimiVolume = new Volume();
        fromNovaVolumeToCimiVolume(novaVolume.getId(), cimiVolume); 
        return cimiVolume;
	}
	
	public Volume getVolume(String volumeId) {
        final Volume volume = new Volume();
        fromNovaVolumeToCimiVolume(volumeId, volume); 
        return volume;
	}

	public Volume.State getVolumeState(final String volumeId) { 
		com.woorea.openstack.nova.model.Volume novaVolume = novaClient.volumes().show(volumeId).execute();
		return fromNovaVolumeStatusToCimiVolumeState(novaVolume.getStatus());
	}
	
	public void deleteVolume(String volumeId) {
        novaClient.volumes().delete(volumeId).execute(); 
	}

}
