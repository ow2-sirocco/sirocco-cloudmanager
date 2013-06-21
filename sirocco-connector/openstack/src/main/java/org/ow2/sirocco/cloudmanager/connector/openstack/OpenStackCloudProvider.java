package org.ow2.sirocco.cloudmanager.connector.openstack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.openstack.base.client.OpenStackResponseException;
import org.openstack.keystone.Keystone;
import org.openstack.keystone.model.Access;
import org.openstack.keystone.model.authentication.UsernamePassword;
import org.openstack.keystone.utils.KeystoneUtils;
import org.openstack.nova.Nova;
import org.openstack.nova.model.Flavor;
import org.openstack.nova.model.Image;
import org.openstack.nova.model.Images;
import org.openstack.nova.model.KeyPairs;
import org.openstack.nova.model.Server;
import org.openstack.nova.model.ServerForCreate;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.connector.api.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenStackCloudProvider {
    private static Logger logger = LoggerFactory.getLogger(OpenStackCloudProvider.class);

    private CloudProviderAccount cloudProviderAccount;

	private CloudProviderLocation cloudProviderLocation;
	
    private Map<String, String> keyPairMap = new HashMap<String, String>();

    private String tenantName;
	
    //private String novaEndPointName;
    
    private Nova novaClient;

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

	public Machine createMachine(MachineCreate machineCreate) throws ConnectorException {
        logger.info("creating Machine for " + cloudProviderAccount.getLogin());
        
        String flavorId = this.findSuitableFlavor(machineCreate.getMachineTemplate().getMachineConfig());
        if (flavorId == null) {
            throw new ConnectorException("Cannot find Nova flavor matching machineConfig");
        }

        String imageIdKey = "openstack";
        String imageId = machineCreate.getMachineTemplate().getMachineImage().getProperties().get(imageIdKey);
        if (imageId == null) {
            throw new ConnectorException("Cannot find imageId for key " + imageIdKey);
        }

        String serverName = null;
        if (machineCreate.getName() != null) {
            serverName = machineCreate.getName() + "-" + UUID.randomUUID();
        } else {
            serverName = "sirocco-" + UUID.randomUUID();
        }
        
        KeyPairs keysPairs = novaClient.keyPairs().list().execute(); // tmp        

        ServerForCreate serverForCreate = new ServerForCreate();
        serverForCreate.setName(serverName);
        serverForCreate.setFlavorRef(flavorId);
        serverForCreate.setImageRef(imageId);
        
        /*serverForCreate.setKeyName(keysPairs.getList().get(0).getName());*/
        serverForCreate.getSecurityGroups()
            .add(new ServerForCreate.SecurityGroup("default"));
        // serverForCreate.getSecurityGroups().add(new
        // ServerForCreate.SecurityGroup(securityGroup.getName()));

        String userData = machineCreate.getMachineTemplate().getUserData();
        if (userData != null) {
        	byte[] encoded = Base64.encodeBase64(userData.getBytes());
        	userData = new String(encoded);
        	serverForCreate.setUserData(userData);
        }
        
        Server server = novaClient.servers().boot(serverForCreate).execute();
        //System.out.println(server);

        final Machine machine = new Machine();
        fromServerToMachine(server, machine); 
        return machine;
        
        /* FIXME 
         * jcloud /woorea ? 
         * - ApiForZone
         * - keypair, 
         * - security group, 
         * - user data
         * 
         * check if flavorId, imageId exist in OpenStack instance
         * */        
	}


	public Machine getMachine(String machineId)  {
        
        //Server server = getServer(machineId);
        Server server = novaClient.servers().show(machineId).execute();
        //System.out.println(server);

        final Machine machine = new Machine();
        fromServerToMachine(server, machine); 
        return machine;
	} 

	public State getMachineState(String machineId) {
        //Server server = getServer(machineId);
        Server server = novaClient.servers().show(machineId).execute();
		return this.fromServerStatusToMachineState(server);
	}

	public void deleteMachine(String machineId) {
        novaClient.servers().delete(machineId).execute(); 
	}	

    //
    // mix
    //

    private Machine.State fromServerStatusToMachineState(final Server server) {
    	String status = server.getStatus();
    	
    	if (status.equalsIgnoreCase("ACTIVE")){
            return Machine.State.STARTED;
    	} else if (status.equalsIgnoreCase("BUILD")){
            return Machine.State.CREATING;
    	} else if (status.equalsIgnoreCase("DELETED")){
            return Machine.State.DELETED;
    	} else if (status.equalsIgnoreCase("HARD_REBOOT")){
            return Machine.State.STARTED;
    	} else if (status.equalsIgnoreCase("PASSWORD")){
            return Machine.State.STARTED;
    	} else if (status.equalsIgnoreCase("REBOOT")){
            return Machine.State.STARTED;
    	} else if (status.equalsIgnoreCase("SUSPENDED")){
            return Machine.State.STOPPED;
    	} else {
            return Machine.State.ERROR; // CIMI mapping!
    	}
    }

    private void fromServerToMachine(final Server server, final Machine machine) {
        /*logger.info("fromServerToMachine: id=" + server.getId() 
        		+ ", name=" + server.getName() 
        		+ ", state=" + server.getStatus() 
        		+ ", flavor id=" + server.getFlavor().getId() 
        		+ ", cpu=" + server.getFlavor().getVcpus() 
        		+ ", mem=" + server.getFlavor().getRam()
        		+ ", disk=" + server.getFlavor().getDisk()
        		+ ", ephemeral=" + server.getFlavor().getEphemeral()
        		);*/
        /*logger.info("server: " + server);*/		

    	
    	machine.setProviderAssignedId(server.getId());        
        machine.setState(this.fromServerStatusToMachineState(server)); 

        // HW
        //Flavor flavor = server.getFlavor(); // WARNING: doesn't work (check if woorea support a lazy instantiation of the object of the model) 
        Flavor flavor = novaClient.flavors().show(server.getFlavor().getId()).execute();
        //System.out.println(flavor);
        /*logger.info("flavor: " + flavor);*/		

        machine.setCpu(new Integer(flavor.getVcpus()));
        machine.setMemory(flavor.getRam() * 1024);
        List<MachineDisk> machineDisks = new ArrayList<MachineDisk>();
        MachineDisk machineDisk = new MachineDisk();
        machineDisk.setCapacity(new Integer(flavor.getDisk()) * 1000); // FIXME ephemeral 
        machineDisks.add(machineDisk);
        machine.setDisks(machineDisks);

        // TODO Network
        /* List<MachineNetworkInterface> nics = new ArrayList<MachineNetworkInterface>();
        machine.setNetworkInterfaces(nics);
        MachineNetworkInterface privateNic = new MachineNetworkInterface();
        privateNic.setAddresses(new ArrayList<MachineNetworkInterfaceAddress>());
        privateNic.setNetworkType(Network.Type.PRIVATE);
        privateNic.setState(MachineNetworkInterface.InterfaceState.ACTIVE);
        MachineNetworkInterface publicNic = new MachineNetworkInterface();
        publicNic.setAddresses(new ArrayList<MachineNetworkInterfaceAddress>());
        publicNic.setNetworkType(Network.Type.PUBLIC);
        publicNic.setState(MachineNetworkInterface.InterfaceState.ACTIVE);

        // TODO
        // assumption: first IP address is private, next addresses are
        // public (floating IPs)
        for (String networkType : server.getAddresses().keySet()) {
            Collection<Address> addresses = server.getAddresses().get(networkType);
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
        }*/
        
        /* FIXME 
         * - disk / ephemeral 
         * - volume
         * - ApiForZone
         * */        
    }
	
	public Server getServer(String machineId) throws ConnectorException {
		try {
			return novaClient.servers().show(machineId).execute();
		} catch (OpenStackResponseException e) {
	        /*System.out.println("- " + e.getMessage() 
	        		+ ", " + e.getStatus()
	        		+ ", " + e.getLocalizedMessage()
	        		+ ", " + e.getCause()
	        		);*/
	        if (e.getStatus() == 404){
				throw new ResourceNotFoundException(e);	        	
	        }
	        else{
				throw new ConnectorException(e);	        	
	        }
		}
	}

    private String findSuitableFlavor(final MachineConfiguration machineConfig) {
        for (Flavor flavor : novaClient.flavors().list(true).execute()) {
            long memoryInKBytes = machineConfig.getMemory();
            long flavorMemoryInKBytes = flavor.getRam() * 1024;
            System.out.println(
            		"memoryInKBytes=" + memoryInKBytes 
            		+ ", flavorMemoryInKBytes=" + flavorMemoryInKBytes
            		);
            if (memoryInKBytes == flavorMemoryInKBytes) {
            	Integer flavorCpu = new Integer(flavor.getVcpus());
                //if (machineConfig.getCpu() == flavor.getVcpus()) {
            	System.out.println(
                		"Cpu()=" + machineConfig.getCpu() 
                		+ ", flavorCpu=" + flavorCpu
                		);
                if (machineConfig.getCpu().intValue() == flavorCpu.intValue()) {
                	System.out.println(
                    		"machineConfig.getDisks().size()=" + machineConfig.getDisks().size()
                    		);
                	if (machineConfig.getDisks().size() == 0) { // FIXME tmp
                		return flavor.getId();
                	}
                	else if (machineConfig.getDisks().size() == 1 && flavor.getEphemeral() == 0) {
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
}
