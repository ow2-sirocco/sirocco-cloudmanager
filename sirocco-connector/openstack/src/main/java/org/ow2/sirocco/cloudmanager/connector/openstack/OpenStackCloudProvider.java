package org.ow2.sirocco.cloudmanager.connector.openstack;

import java.util.Map;
import java.util.UUID;

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
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenStackCloudProvider {
    private static Logger logger = LoggerFactory.getLogger(OpenStackCloudProvider.class);

    private CloudProviderAccount cloudProviderAccount;

	private CloudProviderLocation cloudProviderLocation;
	
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
			
		//NovaClient novaClient = new NovaClient(KeystoneUtils.findEndpointURL(access.getServiceCatalog(), "compute", null, "public"), access.getToken().getId());
		//this.novaClient = new Nova(this.novaEndPointName.concat("/").concat(access.getToken().getTenant().getId()));
		this.novaClient = new Nova(KeystoneUtils.findEndpointURL(access.getServiceCatalog(), "compute", null, "public"));
		this.novaClient.token(access.getToken().getId());
		//novaClient.enableLogging(Logger.getLogger("nova"), 100 * 1024);
		
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

    private String findSuitableFlavor(final MachineConfiguration machineConfig) {
        for (Flavor flavor : novaClient.flavors().list(true).execute()) {
            long memoryInKBytes = machineConfig.getMemory();
            long flavorMemoryInKBytes = flavor.getRam() * 1024;
            if (memoryInKBytes == flavorMemoryInKBytes) {
            	Integer flavorCpu = new Integer(flavor.getVcpus());
                //if (machineConfig.getCpu() == flavor.getVcpus()) {
                if (machineConfig.getCpu() == flavorCpu) {
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
        
        /* FIXME 
         * 
         * jcloud /woorea ? 
         * - ApiForZone
         * - keypair, 
         * - security group, 
         * - user data
         * 
         * check if flavorId, imageId exist in OpenStack instance
         * */        
        KeyPairs keysPairs = novaClient.keyPairs().list().execute(); // tmp
        

        ServerForCreate serverForCreate = new ServerForCreate();
        serverForCreate.setName(serverName);
        serverForCreate.setFlavorRef(flavorId);
        serverForCreate.setImageRef(imageId);
        serverForCreate.setKeyName(keysPairs.getList().get(0).getName());
        serverForCreate.getSecurityGroups()
            .add(new ServerForCreate.SecurityGroup("default"));
        // serverForCreate.getSecurityGroups().add(new
        // ServerForCreate.SecurityGroup(securityGroup.getName()));
        
        Server server = novaClient.servers().boot(serverForCreate).execute();
        System.out.println(server);

        final Machine machine = new Machine();
        //fromServerToMachine(server, machine); // TODO
        return machine;
	}	
}
