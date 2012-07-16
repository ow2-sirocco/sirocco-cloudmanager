/**
 *
 * SIROCCO
 * Copyright (C) 2011 France Telecom
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
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.connector.openstack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.openstack.nova.v2_0.NovaAsyncClient;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.RebootType;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPClient;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairClient;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeClient;
import org.jclouds.openstack.nova.v2_0.features.ServerClient;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeOptions;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactory;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.connector.api.IImageService;
import org.ow2.sirocco.cloudmanager.connector.api.INetworkService;
import org.ow2.sirocco.cloudmanager.connector.api.IProviderCapability;
import org.ow2.sirocco.cloudmanager.connector.api.ISystemService;
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.api.IJobManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume.State;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Module;

@Component(public_factory = false)
@Provides
public class OpenStackCloudProviderConnectorFactory implements ICloudProviderConnectorFactory {
    private static Log logger = LogFactory.getLog(OpenStackCloudProviderConnectorFactory.class);

    public static final String CLOUD_PROVIDER_TYPE = "openstack";

    private static int DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS = 240;

    private static final int THREADPOOL_SIZE = 10;

    @ServiceProperty(name = ICloudProviderConnectorFactory.CLOUD_PROVIDER_TYPE_PROPERTY, value = OpenStackCloudProviderConnectorFactory.CLOUD_PROVIDER_TYPE)
    private String cloudProviderType;

    @Requires
    private IJobManager jobManager;

    private Map<String, String> keyPairMap = new HashMap<String, String>();

    public OpenStackCloudProviderConnectorFactory() {

    }

    public OpenStackCloudProviderConnectorFactory(final IJobManager jobManager) {
        this.jobManager = jobManager;
    }

    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors
        .newFixedThreadPool(OpenStackCloudProviderConnectorFactory.THREADPOOL_SIZE));

    private Set<ICloudProviderConnector> cloudProvidersInUse = new LinkedHashSet<ICloudProviderConnector>();

    @Override
    public void disposeCloudProviderConnector(final String cloudProviderId) throws ConnectorException {
        ICloudProviderConnector cloudProviderToBeDeleted = null;
        for (ICloudProviderConnector cloudProvider : this.cloudProvidersInUse) {
            if (cloudProvider.getCloudProviderId().equals(cloudProviderId)) {
                cloudProviderToBeDeleted = cloudProvider;
                break;
            }
        }
        if (cloudProviderToBeDeleted == null) {
            throw new ConnectorException("The given cloudProviderId: " + cloudProviderId + " is unknown by the system.");
        } else {
            OpenStackCloudProviderConnectorFactory.logger.info("Disposing Openstack connector account.login="
                + cloudProviderToBeDeleted.getCloudProviderAccount().getLogin() + " location="
                + cloudProviderToBeDeleted.getCloudProviderLocation());
            this.cloudProvidersInUse.remove(cloudProviderToBeDeleted);
        }
    }

    @Override
    public ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount cloudProviderAccount,
        final CloudProviderLocation cloudProviderLocation) {
        ICloudProviderConnector result;
        for (ICloudProviderConnector cloudProvider : this.cloudProvidersInUse) {
            if (cloudProvider.getCloudProviderAccount().getLogin().equals(cloudProviderAccount.getLogin())) {
                if (cloudProviderLocation == null || cloudProvider.getCloudProviderLocation().equals(cloudProviderLocation)) {
                    return cloudProvider;
                }
            }
        }
        OpenStackCloudProviderConnectorFactory.logger.info("Adding new OpenStack connector account.login="
            + cloudProviderAccount.getLogin() + " location=" + cloudProviderLocation);
        result = new OpenStackCloudProviderConnector(cloudProviderAccount, cloudProviderLocation);
        this.cloudProvidersInUse.add(result);
        return result;
    }

    @Override
    public Set<CloudProviderLocation> listCloudProviderLocations() {
        // XXX hardcoded single location
        final CloudProviderLocation location = new CloudProviderLocation("FR", "FR-75", "France", "Paris");
        return Collections.singleton(location);
    }

    private class OpenStackCloudProviderConnector implements ICloudProviderConnector, IComputeService, IVolumeService {

        private final String cloudProviderId;

        private CloudProviderAccount cloudProviderAccount;

        private CloudProviderLocation cloudProviderLocation;

        private NovaClient novaClient;

        private NovaAsyncClient novaAsyncClient;

        private String zone;

        private Set<Flavor> flavors;

        private Network cimiPrivateNetwork, cimiPublicNetwork;

        public OpenStackCloudProviderConnector(final CloudProviderAccount cloudProviderAccount,
            final CloudProviderLocation cloudProviderLocation) {
            this.cloudProviderId = UUID.randomUUID().toString();
            this.cloudProviderLocation = cloudProviderLocation;
            this.cloudProviderAccount = cloudProviderAccount;

            Properties overrides = new Properties();
            overrides.setProperty(Constants.PROPERTY_ENDPOINT, cloudProviderAccount.getCloudProvider().getEndpoint());
            overrides.setProperty(Constants.PROPERTY_API_VERSION, "2.0");
            overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
            overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");

            Iterable<Module> modules = ImmutableSet.<Module> of(); // new
                                                                   // SLF4JLoggingModule());
            String user = this.cloudProviderAccount.getLogin();
            String apiKey = this.cloudProviderAccount.getPassword();

            ComputeServiceContext context = new ComputeServiceContextFactory().createContext("openstack-nova", user, apiKey,
                modules, overrides);
            this.novaClient = NovaClient.class.cast(context.getProviderSpecificContext().getApi());
            this.novaAsyncClient = NovaAsyncClient.class.cast(context.getProviderSpecificContext().getAsyncApi());
            // XXX we pick the first zone and ignore others
            this.zone = this.novaClient.getConfiguredZones().iterator().next();

            this.flavors = this.novaClient.getFlavorClientForZone(this.zone).listFlavorsInDetail();

            this.cimiPrivateNetwork = new Network();
            this.cimiPrivateNetwork.setProviderAssignedId("0");
            this.cimiPrivateNetwork.setState(Network.State.STARTED);
            this.cimiPrivateNetwork.setNetworkType(Network.Type.PRIVATE);

            this.cimiPublicNetwork = new Network();
            this.cimiPublicNetwork.setProviderAssignedId("1");
            this.cimiPublicNetwork.setState(Network.State.STARTED);
            this.cimiPublicNetwork.setNetworkType(Network.Type.PUBLIC);

        }

        @Override
        public String getCloudProviderId() {
            return this.cloudProviderId;
        }

        @Override
        public CloudProviderAccount getCloudProviderAccount() {
            return this.cloudProviderAccount;
        }

        @Override
        public void setCloudProviderAccount(final CloudProviderAccount cpa) {
            this.cloudProviderAccount = cpa;
        }

        @Override
        public CloudProviderLocation getCloudProviderLocation() {
            return this.cloudProviderLocation;
        }

        @Override
        public void setCloudProviderLocation(final CloudProviderLocation cpl) {
            this.cloudProviderLocation = cpl;
        }

        @Override
        public IComputeService getComputeService() throws ConnectorException {
            return this;
        }

        @Override
        public IVolumeService getVolumeService() throws ConnectorException {
            return this;
        }

        @Override
        public IImageService getImageService() throws ConnectorException {
            return null;
        }

        @Override
        public INetworkService getNetworkService() throws ConnectorException {
            return null;
        }

        @Override
        public ISystemService getSystemService() throws ConnectorException {
            throw new ConnectorException("Unsupported");
        }

        @Override
        public IProviderCapability getProviderCapability() throws ConnectorException {
            return null;
        }

        //
        // Compute Service
        //

        private String findSuitableFlavor(final MachineConfiguration machineConfig) {
            for (Flavor flavor : this.flavors) {
                long memoryInKBytes = machineConfig.getMemory();
                long flavorMemoryInKBytes = flavor.getRam() * 1024;
                if (memoryInKBytes == flavorMemoryInKBytes) {
                    if (machineConfig.getCpu() == flavor.getVcpus()) {
                        if (machineConfig.getDiskTemplates().size() == 1) {
                            long diskSizeInKBytes = machineConfig.getDiskTemplates().get(0).getCapacity();
                            long flavorDiskSizeInKBytes = flavor.getDisk() * 1000;
                            if (diskSizeInKBytes == flavorDiskSizeInKBytes) {
                                return flavor.getId();
                            }
                        }
                    }
                }
            }
            return null;
        }

        private Machine.State fromServerStatusToMachineState(final Server.Status serverStatus) {
            switch (serverStatus) {
            case ACTIVE:
                return Machine.State.STARTED;
            case BUILD:
                return Machine.State.CREATING;
            case DELETED:
                return Machine.State.DELETED;
            case HARD_REBOOT:
                return Machine.State.STARTED;
            case PASSWORD:
                return Machine.State.STARTED;
            case REBOOT:
                return Machine.State.STARTED;
            case REBUILD:
                return Machine.State.ERROR;
            case RESIZE:
                return Machine.State.ERROR;
            case SUSPENDED:
                return Machine.State.STOPPED;
            case UNKNOWN:
                return Machine.State.ERROR;
            case UNRECOGNIZED:
                return Machine.State.ERROR;
            case VERIFY_RESIZE:
                return Machine.State.ERROR;
            default:
                return Machine.State.ERROR;
            }
        }

        private void fromServerToMachine(final Server server, final Machine machine) {
            machine.setProviderAssignedId(server.getId());
            machine.setState(OpenStackCloudProviderConnector.this.fromServerStatusToMachineState(server.getStatus()));
            List<MachineNetworkInterface> nics = new ArrayList<MachineNetworkInterface>();
            machine.setNetworkInterfaces(nics);
            MachineNetworkInterface privateNic = new MachineNetworkInterface();
            privateNic.setAddresses(new ArrayList<org.ow2.sirocco.cloudmanager.model.cimi.Address>());
            privateNic.setNetworkType(Network.Type.PRIVATE);
            MachineNetworkInterface publicNic = new MachineNetworkInterface();
            publicNic.setAddresses(new ArrayList<org.ow2.sirocco.cloudmanager.model.cimi.Address>());
            publicNic.setNetworkType(Network.Type.PUBLIC);

            for (String networkType : server.getAddresses().keySet()) {
                Collection<Address> addresses = server.getAddresses().get(networkType);
                Network cimiNetwork = (networkType.equalsIgnoreCase("private") ? OpenStackCloudProviderConnector.this.cimiPrivateNetwork
                    : OpenStackCloudProviderConnector.this.cimiPublicNetwork);
                List<org.ow2.sirocco.cloudmanager.model.cimi.Address> cimiAddresses = null;
                if (cimiNetwork == this.cimiPrivateNetwork) {
                    cimiAddresses = privateNic.getAddresses();
                }
                if (cimiNetwork == this.cimiPublicNetwork) {
                    cimiAddresses = publicNic.getAddresses();
                }
                for (Address address : addresses) {
                    org.ow2.sirocco.cloudmanager.model.cimi.Address cimiAddress = new org.ow2.sirocco.cloudmanager.model.cimi.Address();
                    cimiAddress.setIp(address.getAddr());
                    cimiAddress.setNetwork(cimiNetwork);
                    cimiAddress.setAllocation("dynamic");
                    cimiAddress.setProtocol("IPv4");
                    cimiAddress.setResource(cimiNetwork);
                    cimiAddresses.add(cimiAddress);
                }
            }

            if (privateNic.getAddresses().size() > 0) {
                nics.add(privateNic);
            }
            if (publicNic.getAddresses().size() > 0) {
                nics.add(publicNic);
            }

            Flavor flavor = this.novaClient.getFlavorClientForZone(this.zone).getFlavor(server.getFlavor().getId());
            machine.setCpu(flavor.getVcpus());
            machine.setMemory(flavor.getRam() * 1024);
            List<MachineDisk> machineDisks = new ArrayList<MachineDisk>();
            MachineDisk machineDisk = new MachineDisk();
            machineDisk.setCapacity(flavor.getDisk() * 1000);
            machineDisks.add(machineDisk);
            machine.setDisks(machineDisks);
        }

        private String getKeyPair(final String publicKey) {
            String keyPairName = OpenStackCloudProviderConnectorFactory.this.keyPairMap.get(publicKey);
            if (keyPairName != null) {
                return keyPairName;
            }

            KeyPairClient keyPairClient = this.novaClient.getKeyPairExtensionForZone(this.zone).get();

            for (Map<String, KeyPair> map : keyPairClient.listKeyPairs()) {
                for (Map.Entry<String, KeyPair> entry : map.entrySet()) {
                    if (entry.getValue().getPublicKey().equals(publicKey)) {
                        OpenStackCloudProviderConnectorFactory.this.keyPairMap.put(publicKey, entry.getValue().getName());
                        return entry.getValue().getName();
                    }
                }
            }

            KeyPair newKeyPair = keyPairClient.createKeyPairWithPublicKey("keypair-" + UUID.randomUUID().toString(), publicKey);
            OpenStackCloudProviderConnectorFactory.this.keyPairMap.put(publicKey, newKeyPair.getName());
            return newKeyPair.getName();
        }

        private boolean findIpAddressOnServer(final Server server, final String ip) {
            for (String networkType : server.getAddresses().keySet()) {
                Collection<Address> addresses = server.getAddresses().get(networkType);
                for (Address address : addresses) {
                    if (address.getAddr().equals(ip)) {
                        return true;
                    }
                }
            }
            return false;
        }

        // TODO error handling
        private String addFloatingIPToMachine(final String serverId) throws Exception {
            final FloatingIPClient floatingIPClient = this.novaClient.getFloatingIPExtensionForZone(this.zone).get();
            final ServerClient serverClient = this.novaClient.getServerClientForZone(this.zone);

            FloatingIP floatingIP = floatingIPClient.allocate();
            OpenStackCloudProviderConnectorFactory.logger.info("Allocating floating IP " + floatingIP.getIp());
            floatingIPClient.addFloatingIPToServer(floatingIP.getIp(), serverId);
            int waitTimeInSeconds = OpenStackCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
            do {
                Server server = serverClient.getServer(serverId);
                if (server == null) {
                    throw new Exception("Machine with id " + serverId + " unknown");
                }
                if (this.findIpAddressOnServer(server, floatingIP.getIp())) {
                    OpenStackCloudProviderConnectorFactory.logger.info("Floating IP " + floatingIP.getIp()
                        + " attached to server " + serverId);
                    break;
                }
                Thread.sleep(1000);
            } while (waitTimeInSeconds-- > 0);
            return floatingIP.getIp();
        }

        private void freeFloatingIpsFromServer(final String serverId) {
            final FloatingIPClient floatingIPClient = this.novaClient.getFloatingIPExtensionForZone(this.zone).get();

            for (FloatingIP floatingIP : floatingIPClient.listFloatingIPs()) {
                if (floatingIP.getInstanceId().equals(serverId)) {
                    OpenStackCloudProviderConnectorFactory.logger.info("Releasing floating IP " + floatingIP.getIp()
                        + " from server " + serverId);
                    floatingIPClient.removeFloatingIPFromServer(floatingIP.getIp(), serverId);
                    floatingIPClient.deallocate(floatingIP.getId());
                }
            }

        }

        @Override
        public Job createMachine(final MachineCreate machineCreate) throws ConnectorException {
            String flavorId = this.findSuitableFlavor(machineCreate.getMachineTemplate().getMachineConfiguration());
            if (flavorId == null) {
                throw new ConnectorException("Cannot find Nova flavor matching machineConfig");
            }
            final ServerClient serverClient = this.novaClient.getServerClientForZone(this.zone);

            String keyPairName = null;
            if (machineCreate.getMachineTemplate().getCredentials() != null) {
                String publicKey = new String(machineCreate.getMachineTemplate().getCredentials().getPublicKey());
                keyPairName = this.getKeyPair(publicKey);
            }

            // XXX default security group
            CreateServerOptions options = CreateServerOptions.Builder.securityGroupNames("default");
            if (keyPairName != null) {
                options.keyPairName(keyPairName);
            }

            String userData = machineCreate.getMachineTemplate().getUserData();
            if (userData != null) {
                options.userData(userData.getBytes());
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
            ServerCreated serverCreated = serverClient.createServer(serverName, imageId, flavorId, options);
            final String serverId = serverCreated.getId();
            final Machine machine = new Machine();
            machine.setProviderAssignedId(serverId);

            final Callable<Machine> createTask = new Callable<Machine>() {
                @Override
                public Machine call() throws Exception {
                    Server server;
                    int waitTimeInSeconds = OpenStackCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                    do {
                        server = serverClient.getServer(serverId);
                        if (server == null) {
                            throw new Exception("Machine with id " + serverId + " unknown");
                        }
                        Server.Status status = server.getStatus();
                        if (status != Server.Status.BUILD) {
                            break;
                        }
                        Thread.sleep(1000);
                    } while (waitTimeInSeconds-- > 0);

                    // XXX tentative fix to determine if a public IP needs to be
                    // assigned to the machine
                    boolean allocateFloatingIp = false;
                    if (machineCreate.getMachineTemplate().getNetworkInterfaces() != null) {
                        for (MachineTemplateNetworkInterface nic : machineCreate.getMachineTemplate().getNetworkInterfaces()) {
                            if (nic.getNetworkType() == Network.Type.PUBLIC) {
                                allocateFloatingIp = true;
                                break;
                            }
                        }
                    }
                    if (allocateFloatingIp) {
                        OpenStackCloudProviderConnector.this.addFloatingIPToMachine(serverId);
                    }
                    OpenStackCloudProviderConnector.this.fromServerToMachine(server, machine);
                    return machine;
                }
            };
            ListenableFuture<Machine> result = OpenStackCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return OpenStackCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "add", result);

        }

        @Override
        public Job startMachine(final String machineId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job stopMachine(final String machineId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job suspendMachine(final String machineId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job restartMachine(final String machineId, final boolean force) throws ConnectorException {
            final ServerClient serverClient = this.novaClient.getServerClientForZone(this.zone);
            final Callable<Void> startTask = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    serverClient.rebootServer(machineId, force ? RebootType.HARD : RebootType.SOFT);
                    int waitTimeInSeconds = OpenStackCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                    do {
                        Server server = serverClient.getServer(machineId);
                        Server.Status status = server.getStatus();
                        if (status != Server.Status.REBOOT) {
                            break;
                        }
                        Thread.sleep(1000);
                    } while (waitTimeInSeconds-- > 0);
                    return null;
                }
            };
            Machine machine = new Machine();
            machine.setProviderAssignedId(machineId);
            ListenableFuture<Void> result = OpenStackCloudProviderConnectorFactory.this.executorService.submit(startTask);
            return OpenStackCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "restart", result);
        }

        @Override
        public Job pauseMachine(final String machineId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job deleteMachine(final String machineId) throws ConnectorException {
            final ServerClient serverClient = this.novaClient.getServerClientForZone(this.zone);
            final Callable<Void> startTask = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    // check whether server has floating ip
                    // floating ips must be detached first
                    // see https://bugs.launchpad.net/nova/+bug/997763
                    OpenStackCloudProviderConnector.this.freeFloatingIpsFromServer(machineId);
                    if (!serverClient.deleteServer(machineId)) {
                        throw new Exception("Failed to delete server");
                    }
                    int waitTimeInSeconds = OpenStackCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                    do {
                        Server server = serverClient.getServer(machineId);
                        if (server == null) {
                            break;
                        }
                        Server.Status status = server.getStatus();
                        if (status == Server.Status.DELETED) {
                            break;
                        }
                        Thread.sleep(1000);
                    } while (waitTimeInSeconds-- > 0);
                    return null;
                }
            };
            Machine machine = new Machine();
            machine.setProviderAssignedId(machineId);
            ListenableFuture<Void> result = OpenStackCloudProviderConnectorFactory.this.executorService.submit(startTask);
            return OpenStackCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "delete", result);
        }

        @Override
        public org.ow2.sirocco.cloudmanager.model.cimi.Machine.State getMachineState(final String machineId)
            throws ConnectorException {
            final ServerClient serverClient = this.novaClient.getServerClientForZone(this.zone);
            Server server = serverClient.getServer(machineId);
            if (server == null) {
                throw new ConnectorException("Machine with id " + machineId + " not found");
            }
            Server.Status status = server.getStatus();
            return this.fromServerStatusToMachineState(status);
        }

        @Override
        public Machine getMachine(final String machineId) throws ConnectorException {
            final ServerClient serverClient = this.novaClient.getServerClientForZone(this.zone);
            Server server = serverClient.getServer(machineId);
            if (server == null) {
                throw new ConnectorException("Machine " + machineId + " does not exist");
            }
            Machine machine = new Machine();
            this.fromServerToMachine(server, machine);
            return machine;
        }

        @Override
        public Job addVolumeToMachine(final String machineId, final MachineVolume machineVolume) throws ConnectorException {
            String volumeId = machineVolume.getVolume().getProviderAssignedId();
            Volume volume = this.getVolume(volumeId);
            Machine machine = this.getMachine(machineId);
            String device = machineVolume.getInitialLocation();
            if (device == null) {
                throw new ConnectorException("device not specified");
            }
            try {
                ListenableFuture<VolumeAttachment> attachResult = this.novaAsyncClient.getVolumeExtensionForZone(this.zone)
                    .get().attachVolumeToServerAsDevice(volumeId, machineId, device);
                return OpenStackCloudProviderConnectorFactory.this.jobManager.newJob(machine, volume, "add", attachResult);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        @Override
        public Job removeVolumeFromMachine(final String machineId, final MachineVolume machineVolume) throws ConnectorException {
            String volumeId = machineVolume.getVolume().getProviderAssignedId();
            Volume volume = this.getVolume(volumeId);
            Machine machine = this.getMachine(machineId);
            try {
                ListenableFuture<Boolean> attachResult = this.novaAsyncClient.getVolumeExtensionForZone(this.zone).get()
                    .detachVolumeFromServer(volumeId, machineId);
                return OpenStackCloudProviderConnectorFactory.this.jobManager.newJob(machine, volume, "add", attachResult);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        //
        // Volume Service
        //

        private Volume.State fromNovaVolumeStatusToCimiVolumeState(
            final org.jclouds.openstack.nova.v2_0.domain.Volume.Status status) {
            switch (status) {
            case AVAILABLE:
                return Volume.State.AVAILABLE;
            case CREATING:
                return Volume.State.CREATING;
            case DELETING:
                return Volume.State.DELETING;
            case ERROR:
                return Volume.State.ERROR;
            case IN_USE:
                return Volume.State.AVAILABLE; // XXX
            case UNRECOGNIZED:
                return Volume.State.ERROR; // XXX
            default:
                return Volume.State.ERROR;
            }
        }

        private void fromNovaVolumeToCimiVolume(final org.jclouds.openstack.nova.v2_0.domain.Volume novaVolume,
            final Volume cimiVolume) {
            cimiVolume.setProviderAssignedId(novaVolume.getId());
            // GB to KB
            cimiVolume.setCapacity(novaVolume.getSize() * 1000 * 1000);
            cimiVolume.setState(this.fromNovaVolumeStatusToCimiVolumeState(novaVolume.getStatus()));
        }

        @Override
        public Job createVolume(final VolumeCreate volumeCreate) throws ConnectorException {
            final VolumeClient volumeClient = this.novaClient.getVolumeExtensionForZone(this.zone).get();
            CreateVolumeOptions options = CreateVolumeOptions.Builder.name(volumeCreate.getName()).description(
                volumeCreate.getDescription());
            VolumeConfiguration volumeConfig = volumeCreate.getVolumeTemplate().getVolumeConfig();
            int sizeInGB = volumeConfig.getCapacity() / (1000 * 1000);
            org.jclouds.openstack.nova.v2_0.domain.Volume novaVolume = volumeClient.createVolume(sizeInGB, options);
            final String novaVolumeId = novaVolume.getId();
            final Volume cimiVolume = new Volume();
            this.fromNovaVolumeToCimiVolume(novaVolume, cimiVolume);

            final Callable<Volume> createTask = new Callable<Volume>() {
                @Override
                public Volume call() throws Exception {
                    int waitTimeInSeconds = OpenStackCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                    do {
                        org.jclouds.openstack.nova.v2_0.domain.Volume novaVolume = volumeClient.getVolume(novaVolumeId);
                        if (novaVolume == null) {
                            throw new Exception("Volume does not exist");
                        }
                        if (novaVolume.getStatus() != org.jclouds.openstack.nova.v2_0.domain.Volume.Status.CREATING) {
                            OpenStackCloudProviderConnector.this.fromNovaVolumeToCimiVolume(novaVolume, cimiVolume);
                            break;
                        }
                        Thread.sleep(1000);
                    } while (waitTimeInSeconds-- > 0);
                    return cimiVolume;
                }
            };
            ListenableFuture<Volume> result = OpenStackCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return OpenStackCloudProviderConnectorFactory.this.jobManager.newJob(cimiVolume, null, "add", result);
        }

        @Override
        public Job deleteVolume(final String volumeId) throws ConnectorException {
            Volume volume = this.getVolume(volumeId);
            final VolumeClient volumeClient = this.novaClient.getVolumeExtensionForZone(this.zone).get();

            if (!volumeClient.deleteVolume(volumeId)) {
                throw new ConnectorException("Failed to delete volume " + volumeId);
            }
            final Callable<Void> deleteTask = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int waitTimeInSeconds = OpenStackCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                    do {
                        org.jclouds.openstack.nova.v2_0.domain.Volume novaVolume = volumeClient.getVolume(volumeId);
                        if (novaVolume == null) {
                            break;
                        }
                        Thread.sleep(1000);
                    } while (waitTimeInSeconds-- > 0);
                    return null;
                }
            };
            ListenableFuture<Void> result = OpenStackCloudProviderConnectorFactory.this.executorService.submit(deleteTask);
            return OpenStackCloudProviderConnectorFactory.this.jobManager.newJob(volume, null, "delete", result);
        }

        @Override
        public State getVolumeState(final String volumeId) throws ConnectorException {
            final VolumeClient volumeClient = this.novaClient.getVolumeExtensionForZone(this.zone).get();
            org.jclouds.openstack.nova.v2_0.domain.Volume novaVolume = volumeClient.getVolume(volumeId);
            if (novaVolume == null) {
                throw new ConnectorException("Volume " + volumeId + " does not exist");
            }
            return this.fromNovaVolumeStatusToCimiVolumeState(novaVolume.getStatus());
        }

        @Override
        public Volume getVolume(final String volumeId) throws ConnectorException {
            final VolumeClient volumeClient = this.novaClient.getVolumeExtensionForZone(this.zone).get();
            org.jclouds.openstack.nova.v2_0.domain.Volume novaVolume = volumeClient.getVolume(volumeId);
            if (novaVolume == null) {
                throw new ConnectorException("Volume " + volumeId + " does not exist");
            }
            Volume cimiVolume = new Volume();
            this.fromNovaVolumeToCimiVolume(novaVolume, cimiVolume);
            return cimiVolume;
        }

        @Override
        public Job createVolumeImage(final VolumeImage volumeImage) throws ConnectorException {
            // TODO Auto-generated method stub
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job createVolumeSnapshot(final String volumeId, final VolumeImage volumeImage) throws ConnectorException {
            // TODO Auto-generated method stub
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public VolumeImage getVolumeImage(final String volumeImageId) throws ConnectorException {
            // TODO Auto-generated method stub
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job deleteVolumeImage(final String volumeImageId) throws ConnectorException {
            // TODO Auto-generated method stub
            throw new ConnectorException("unsupported operation");
        }

    }

}
