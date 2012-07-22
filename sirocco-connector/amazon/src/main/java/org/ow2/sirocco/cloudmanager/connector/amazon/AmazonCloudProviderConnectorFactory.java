package org.ow2.sirocco.cloudmanager.connector.amazon;

import java.util.ArrayList;
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
import org.jclouds.aws.ec2.AWSEC2AsyncClient;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.services.AWSKeyPairClient;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.ec2.compute.domain.EC2HardwareBuilder;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.InstanceStateChange;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.services.ElasticBlockStoreClient;
import org.jclouds.location.suppliers.derived.ZoneIdToURIFromJoinOnRegionIdToURI;
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
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress;
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
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Module;

@Component(public_factory = false)
@Provides
public class AmazonCloudProviderConnectorFactory implements ICloudProviderConnectorFactory {
    private static Log logger = LogFactory.getLog(AmazonCloudProviderConnectorFactory.class);

    public static final String CLOUD_PROVIDER_TYPE = "amazon";

    private static int DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS = 180;

    private static final int THREADPOOL_SIZE = 10;

    private static final Map<CloudProviderLocation, String> locationMap = new HashMap<CloudProviderLocation, String>();

    static {
        AmazonCloudProviderConnectorFactory.locationMap
            .put(new CloudProviderLocation("IE", null, "Ireland", null), "eu-west-1");
        AmazonCloudProviderConnectorFactory.locationMap.put(new CloudProviderLocation("US", "US-VA", "United States",
            "Virginia"), "us-east-1");
        AmazonCloudProviderConnectorFactory.locationMap.put(new CloudProviderLocation("US", "US-CA", "United States",
            "California"), "us-west-1");
        AmazonCloudProviderConnectorFactory.locationMap.put(new CloudProviderLocation("SG", null, "Singapore", null),
            "ap-southeast-1");
        AmazonCloudProviderConnectorFactory.locationMap.put(new CloudProviderLocation("JP", "JP-13", "Japan", "Tokyo"),
            "ap-northeast-1");
        AmazonCloudProviderConnectorFactory.locationMap.put(new CloudProviderLocation("BR", "BR-SP", "Brazil", "Sao Paulo"),
            "sa-east-1");
    }

    private static final Map<String, Hardware> AWSEC2_HARDWARE_MAP = Collections
        .unmodifiableMap(new HashMap<String, Hardware>() {
            {
                this.put(InstanceType.C1_MEDIUM, EC2HardwareBuilder.c1_medium().build());
                this.put(InstanceType.C1_XLARGE, EC2HardwareBuilder.c1_xlarge().build());
                this.put(InstanceType.CC1_4XLARGE, EC2HardwareBuilder.cc1_4xlarge().build());
                this.put(InstanceType.CC2_8XLARGE, EC2HardwareBuilder.cc2_8xlarge().build());
                this.put(InstanceType.CG1_4XLARGE, EC2HardwareBuilder.cg1_4xlarge().build());
                this.put(InstanceType.M1_LARGE, EC2HardwareBuilder.m1_large().build());
                this.put(InstanceType.M1_MEDIUM, EC2HardwareBuilder.m1_medium().build());
                this.put(InstanceType.M1_SMALL, EC2HardwareBuilder.m1_small().build());
                this.put(InstanceType.M1_XLARGE, EC2HardwareBuilder.m1_xlarge().build());
                this.put(InstanceType.M2_2XLARGE, EC2HardwareBuilder.m2_2xlarge().build());
                this.put(InstanceType.M2_4XLARGE, EC2HardwareBuilder.m2_4xlarge().build());
                this.put(InstanceType.M2_XLARGE, EC2HardwareBuilder.m2_xlarge().build());
                this.put(InstanceType.T1_MICRO, EC2HardwareBuilder.t1_micro().build());
            }
        });

    @ServiceProperty(name = ICloudProviderConnectorFactory.CLOUD_PROVIDER_TYPE_PROPERTY, value = AmazonCloudProviderConnectorFactory.CLOUD_PROVIDER_TYPE)
    private String cloudProviderType;

    @Requires
    private IJobManager jobManager;

    public AmazonCloudProviderConnectorFactory() {

    }

    public AmazonCloudProviderConnectorFactory(final IJobManager jobManager) {
        this.jobManager = jobManager;
    }

    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors
        .newFixedThreadPool(AmazonCloudProviderConnectorFactory.THREADPOOL_SIZE));

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
            AmazonCloudProviderConnectorFactory.logger.info("Disposing Amazon connector account.login="
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
        AmazonCloudProviderConnectorFactory.logger.info("Adding new Amazon connector account.login="
            + cloudProviderAccount.getLogin() + " location=" + cloudProviderLocation);
        result = new AmazonCloudProviderConnector(cloudProviderAccount, cloudProviderLocation);
        this.cloudProvidersInUse.add(result);
        return result;
    }

    @Override
    public Set<CloudProviderLocation> listCloudProviderLocations() {
        return AmazonCloudProviderConnectorFactory.locationMap.keySet();
    }

    private class AmazonCloudProviderConnector implements ICloudProviderConnector, IComputeService, IVolumeService {

        private final String cloudProviderId;

        private CloudProviderAccount cloudProviderAccount;

        private CloudProviderLocation cloudProviderLocation;

        private String amazonRegionCode;

        private String defaultAvailabilityZone;

        final String accessKeyId;

        final String secretKeyId;

        private AWSEC2Client syncClient;

        private AWSEC2AsyncClient asyncClient;

        private Network cimiPrivateNetwork, cimiPublicNetwork;

        public AmazonCloudProviderConnector(final CloudProviderAccount cloudProviderAccount,
            final CloudProviderLocation cloudProviderLocation) {
            this.cloudProviderId = UUID.randomUUID().toString();
            this.cloudProviderLocation = cloudProviderLocation;
            this.cloudProviderAccount = cloudProviderAccount;
            this.accessKeyId = cloudProviderAccount.getLogin();
            this.secretKeyId = cloudProviderAccount.getPassword();
            this.amazonRegionCode = AmazonCloudProviderConnectorFactory.locationMap.get(cloudProviderLocation);

            Properties overrides = new Properties();

            String httpProxyHost = System.getProperty("http.proxyHost");
            String httpProxyPort = System.getProperty("http.proxyPort");
            if (httpProxyHost != null) {
                overrides.setProperty(Constants.PROPERTY_PROXY_HOST, httpProxyHost);
            }
            if (httpProxyPort != null) {
                overrides.setProperty(Constants.PROPERTY_PROXY_PORT, httpProxyPort);
            }
            overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
            overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");

            Class zz = ZoneIdToURIFromJoinOnRegionIdToURI.class;

            ComputeServiceContext computeServiceContext = new ComputeServiceContextFactory().createContext("aws-ec2",
                this.accessKeyId, this.secretKeyId, ImmutableSet.<Module> of(), overrides);
            ComputeService computeService = computeServiceContext.getComputeService();
            this.syncClient = (AWSEC2Client) computeServiceContext.getProviderSpecificContext().getApi();
            this.asyncClient = (AWSEC2AsyncClient) computeServiceContext.getProviderSpecificContext().getAsyncApi();

            this.defaultAvailabilityZone = this.syncClient.getAvailabilityZoneAndRegionServices()
                .describeAvailabilityZonesInRegion(this.amazonRegionCode).iterator().next().getZone();

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

        private String findSuitableInstanceType(final MachineConfiguration machineConfig) {
            long memoryInMBytes = machineConfig.getMemory() / 1024;

            for (Hardware hardware : AmazonCloudProviderConnectorFactory.AWSEC2_HARDWARE_MAP.values()) {
                if (memoryInMBytes == hardware.getRam()) {
                    if (machineConfig.getCpu() == hardware.getProcessors().size()) {
                        // special test for micro instance with no disk
                        if (hardware.getVolumes().size() == 0 && machineConfig.getDiskTemplates().size() == 1
                            && machineConfig.getDiskTemplates().get(0).getCapacity() == 0) {
                            return hardware.getProviderId();
                        }
                        if (machineConfig.getDiskTemplates().size() == hardware.getVolumes().size()) {
                            // XXX we assume that disks are ordered the same way
                            int i = 0;
                            for (; i < machineConfig.getDiskTemplates().size(); i++) {
                                long diskSizeInGigaBytes = machineConfig.getDiskTemplates().get(i).getCapacity()
                                    / (1000 * 1000);
                                long hardwareDiskSizeInGigaBytes = hardware.getVolumes().get(i).getSize().longValue();
                                if (diskSizeInGigaBytes != hardwareDiskSizeInGigaBytes) {
                                    break;
                                }
                            }
                            if (i >= machineConfig.getDiskTemplates().size()) {
                                return hardware.getProviderId();
                            }
                        }
                    }
                }
            }
            return null;
        }

        private Machine.State fromInstanceStateToMachineState(final InstanceState state) {
            switch (state) {
            case PENDING:
                return Machine.State.CREATING;
            case RUNNING:
                return Machine.State.STARTED;
            case SHUTTING_DOWN:
                return Machine.State.STOPPING;
            case STOPPED:
                return Machine.State.STOPPED;
            case STOPPING:
                return Machine.State.STOPPING;
            case TERMINATED:
                return Machine.State.DELETED;
            case UNRECOGNIZED:
                return Machine.State.ERROR;
            default:
                return Machine.State.ERROR;
            }
        }

        private void fromAWSRunningInstanceToMachine(final AWSRunningInstance runningInstance, final Machine machine) {
            machine.setProviderAssignedId(runningInstance.getId());
            machine.setState(this.fromInstanceStateToMachineState(runningInstance.getInstanceState()));

            List<MachineNetworkInterface> nics = new ArrayList<MachineNetworkInterface>();
            machine.setNetworkInterfaces(nics);

            if (runningInstance.getPrivateIpAddress() != null) {
                org.ow2.sirocco.cloudmanager.model.cimi.Address cimiAddress = new org.ow2.sirocco.cloudmanager.model.cimi.Address();
                cimiAddress = new org.ow2.sirocco.cloudmanager.model.cimi.Address();
                cimiAddress.setIp(runningInstance.getPrivateIpAddress());
                cimiAddress.setNetwork(this.cimiPrivateNetwork);
                cimiAddress.setAllocation("dynamic");
                cimiAddress.setProtocol("IPv4");
                cimiAddress.setHostName(runningInstance.getPrivateDnsName());
                cimiAddress.setResource(this.cimiPrivateNetwork);

                List<org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress> cimiAddresses = new ArrayList<org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress>();
                MachineNetworkInterfaceAddress entry = new MachineNetworkInterfaceAddress();
                entry.setAddress(cimiAddress);

                cimiAddresses.add(entry);
                MachineNetworkInterface privateNic = new MachineNetworkInterface();
                privateNic.setAddresses(cimiAddresses);
                privateNic.setNetworkType(Network.Type.PRIVATE);
                nics.add(privateNic);
            }

            if (runningInstance.getIpAddress() != null) {
                org.ow2.sirocco.cloudmanager.model.cimi.Address cimiAddress = new org.ow2.sirocco.cloudmanager.model.cimi.Address();
                cimiAddress.setIp(runningInstance.getIpAddress());
                cimiAddress.setNetwork(this.cimiPublicNetwork);
                cimiAddress.setAllocation("dynamic");
                cimiAddress.setProtocol("IPv4");
                cimiAddress.setHostName(runningInstance.getDnsName());
                cimiAddress.setResource(this.cimiPublicNetwork);

                List<MachineNetworkInterfaceAddress> cimiAddresses = new ArrayList<MachineNetworkInterfaceAddress>();
                MachineNetworkInterfaceAddress entry = new MachineNetworkInterfaceAddress();
                entry.setAddress(cimiAddress);
                cimiAddresses.add(entry);
                MachineNetworkInterface publicNic = new MachineNetworkInterface();
                publicNic.setAddresses(cimiAddresses);
                publicNic.setNetworkType(Network.Type.PUBLIC);
                nics.add(publicNic);
            }

            Hardware hardware = AmazonCloudProviderConnectorFactory.AWSEC2_HARDWARE_MAP.get(runningInstance.getInstanceType());
            machine.setCpu(hardware.getProcessors().size());
            machine.setMemory(hardware.getRam() * 1024);
            List<MachineDisk> machineDisks = new ArrayList<MachineDisk>();
            if (hardware.getVolumes().size() == 0) {
                MachineDisk machineDisk = new MachineDisk();
                machineDisk.setInitialLocation("");
                machineDisk.setCapacity(0);
                machineDisks.add(machineDisk);
            } else {
                for (org.jclouds.compute.domain.Volume volume : hardware.getVolumes()) {
                    MachineDisk machineDisk = new MachineDisk();
                    machineDisk.setInitialLocation(volume.getDevice());
                    machineDisk.setCapacity((int) (volume.getSize() * 1000 * 1000));
                    machineDisks.add(machineDisk);
                }
            }
            machine.setDisks(machineDisks);
        }

        private KeyPair findOrInstallKeyPair(final String publicKey) {
            AWSKeyPairClient keyPairClient = AmazonCloudProviderConnector.this.syncClient.getKeyPairServices();

            String keyPairName = "keyPair-" + UUID.randomUUID();
            KeyPair kp = AmazonCloudProviderConnector.this.syncClient.getKeyPairServices().importKeyPairInRegion(
                AmazonCloudProviderConnector.this.amazonRegionCode, keyPairName, publicKey);

            Set<KeyPair> keyPairs = keyPairClient.describeKeyPairsInRegion(this.amazonRegionCode);
            KeyPair result = null;
            for (KeyPair keyPair : keyPairs) {
                if (keyPair.getSha1OfPrivateKey().equals(kp.getSha1OfPrivateKey())
                    && !keyPair.getKeyName().equals(kp.getKeyName())) {
                    result = keyPair;
                    break;
                }
            }
            if (result != null) {
                keyPairClient.deleteKeyPairInRegion(this.amazonRegionCode, kp.getKeyName());
            } else {
                result = kp;
            }

            return result;
        }

        @Override
        public Job createMachine(final MachineCreate machineCreate) throws ConnectorException {
            final String instanceType = this.findSuitableInstanceType(machineCreate.getMachineTemplate()
                .getMachineConfiguration());
            if (instanceType == null) {
                throw new ConnectorException("Not suitable instance type found");
            }
            KeyPair keyPair = null;
            if (machineCreate.getMachineTemplate().getCredentials() != null) {
                keyPair = this.findOrInstallKeyPair(new String(machineCreate.getMachineTemplate().getCredentials()
                    .getPublicKey()));
            }
            RunInstancesOptions options = RunInstancesOptions.Builder.asType(instanceType);
            if (keyPair != null) {
                options = options.withKeyName(keyPair.getKeyName());
            }
            String userData = machineCreate.getMachineTemplate().getUserData();
            if (userData != null) {
                options.withUserData(userData.getBytes());
            }

            String imageIdKey = "amazon/" + this.amazonRegionCode;
            String imageId = machineCreate.getMachineTemplate().getMachineImage().getProperties().get(imageIdKey);
            if (imageId == null) {
                throw new ConnectorException("Cannot find imageId for key " + imageIdKey);
            }

            Reservation<? extends AWSRunningInstance> reservation = AmazonCloudProviderConnector.this.syncClient
                .getInstanceServices().runInstancesInRegion(AmazonCloudProviderConnector.this.amazonRegionCode,
                    this.defaultAvailabilityZone, imageId, 1, 1, options);

            final String instanceId = reservation.iterator().next().getId();

            final Machine machine = new Machine();
            AmazonCloudProviderConnector.this.fromAWSRunningInstanceToMachine(Iterables.getFirst(reservation, null), machine);

            final Callable<Machine> createTask = new Callable<Machine>() {
                @Override
                public Machine call() throws Exception {
                    int waitTimeInSeconds = AmazonCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                    do {
                        Reservation<? extends AWSRunningInstance> reservation = Iterables.getFirst(
                            AmazonCloudProviderConnector.this.syncClient.getInstanceServices().describeInstancesInRegion(
                                AmazonCloudProviderConnector.this.amazonRegionCode, instanceId), null);
                        AWSRunningInstance instance = Iterables.getFirst(reservation, null);
                        if (instance.getInstanceState() != InstanceState.PENDING) {
                            System.out.println("Machine created status=" + instance.getInstanceState());
                            AmazonCloudProviderConnector.this.fromAWSRunningInstanceToMachine(instance, machine);
                            break;
                        }
                        Thread.sleep(1000);
                    } while (waitTimeInSeconds-- > 0);
                    return machine;
                }
            };
            ListenableFuture<Machine> result = AmazonCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return AmazonCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "add", result);
        }

        @Override
        public Job startMachine(final String machineId) throws ConnectorException {
            final Machine machine = this.getMachine(machineId);

            try {
                Set<? extends InstanceStateChange> startResult = this.syncClient.getInstanceServices().startInstancesInRegion(
                    this.amazonRegionCode, machineId);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }

            final Callable<Void> startTask = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int waitTimeInSeconds = AmazonCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                    do {
                        Reservation<? extends AWSRunningInstance> reservation = Iterables.getFirst(
                            AmazonCloudProviderConnector.this.syncClient.getInstanceServices().describeInstancesInRegion(
                                AmazonCloudProviderConnector.this.amazonRegionCode, machineId), null);
                        AWSRunningInstance instance = Iterables.getFirst(reservation, null);
                        if (instance.getInstanceState() != InstanceState.PENDING) {
                            AmazonCloudProviderConnector.this.fromAWSRunningInstanceToMachine(instance, machine);
                            break;
                        }
                        Thread.sleep(1000);
                    } while (waitTimeInSeconds-- > 0);
                    return null;
                }
            };
            ListenableFuture<Void> result = AmazonCloudProviderConnectorFactory.this.executorService.submit(startTask);
            return AmazonCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "start", result);
        }

        @Override
        public Job stopMachine(final String machineId, final boolean force) throws ConnectorException {
            final Machine machine = this.getMachine(machineId);

            try {
                // XXX set force to true
                Set<? extends InstanceStateChange> stopResult = this.syncClient.getInstanceServices().stopInstancesInRegion(
                    this.amazonRegionCode, true, machineId);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }

            final Callable<Void> stoptTask = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int waitTimeInSeconds = AmazonCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                    do {
                        Reservation<? extends AWSRunningInstance> reservation = Iterables.getFirst(
                            AmazonCloudProviderConnector.this.syncClient.getInstanceServices().describeInstancesInRegion(
                                AmazonCloudProviderConnector.this.amazonRegionCode, machineId), null);
                        AWSRunningInstance instance = Iterables.getFirst(reservation, null);
                        if (instance.getInstanceState() != InstanceState.PENDING
                            && instance.getInstanceState() != InstanceState.STOPPING) {
                            AmazonCloudProviderConnector.this.fromAWSRunningInstanceToMachine(instance, machine);
                            break;
                        }
                        Thread.sleep(1000);
                    } while (waitTimeInSeconds-- > 0);
                    return null;
                }
            };
            ListenableFuture<Void> result = AmazonCloudProviderConnectorFactory.this.executorService.submit(stoptTask);
            return AmazonCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "stop", result);
        }

        @Override
        public Job suspendMachine(final String machineId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job restartMachine(final String machineId, final boolean force) throws ConnectorException {
            final Machine machine = this.getMachine(machineId);

            try {
                this.syncClient.getInstanceServices().rebootInstancesInRegion(this.amazonRegionCode, machineId);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
            final Callable<Void> task = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int waitTimeInSeconds = AmazonCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                    do {
                        Reservation<? extends AWSRunningInstance> reservation = Iterables.getFirst(
                            AmazonCloudProviderConnector.this.syncClient.getInstanceServices().describeInstancesInRegion(
                                AmazonCloudProviderConnector.this.amazonRegionCode, machineId), null);
                        AWSRunningInstance instance = Iterables.getFirst(reservation, null);
                        if (instance.getInstanceState() != InstanceState.PENDING
                            && instance.getInstanceState() != InstanceState.SHUTTING_DOWN
                            && instance.getInstanceState() != InstanceState.STOPPING) {
                            AmazonCloudProviderConnector.this.fromAWSRunningInstanceToMachine(instance, machine);
                            break;
                        }
                        Thread.sleep(1000);
                    } while (waitTimeInSeconds-- > 0);
                    return null;
                }
            };
            ListenableFuture<Void> result = AmazonCloudProviderConnectorFactory.this.executorService.submit(task);
            return AmazonCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "restart", result);
        }

        @Override
        public Job pauseMachine(final String machineId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job deleteMachine(final String machineId) throws ConnectorException {
            final Machine machine = this.getMachine(machineId);

            try {
                Set<? extends InstanceStateChange> deleteResult = this.syncClient.getInstanceServices()
                    .terminateInstancesInRegion(this.amazonRegionCode, machineId);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }

            final Callable<Void> task = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int waitTimeInSeconds = AmazonCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                    do {
                        Reservation<? extends AWSRunningInstance> reservation = Iterables.getFirst(
                            AmazonCloudProviderConnector.this.syncClient.getInstanceServices().describeInstancesInRegion(
                                AmazonCloudProviderConnector.this.amazonRegionCode, machineId), null);
                        AWSRunningInstance instance = Iterables.getFirst(reservation, null);
                        if (instance.getInstanceState() == InstanceState.TERMINATED
                            || (instance.getInstanceState() != InstanceState.PENDING
                                && instance.getInstanceState() != InstanceState.SHUTTING_DOWN && instance.getInstanceState() != InstanceState.STOPPING)) {
                            AmazonCloudProviderConnector.this.fromAWSRunningInstanceToMachine(instance, machine);
                            break;
                        }
                        Thread.sleep(1000);
                    } while (waitTimeInSeconds-- > 0);
                    return null;
                }
            };
            ListenableFuture<Void> result = AmazonCloudProviderConnectorFactory.this.executorService.submit(task);

            return AmazonCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "delete", result);
        }

        @Override
        public org.ow2.sirocco.cloudmanager.model.cimi.Machine.State getMachineState(final String machineId)
            throws ConnectorException {
            try {
                Reservation<? extends AWSRunningInstance> reservation = Iterables.getFirst(this.syncClient
                    .getInstanceServices().describeInstancesInRegion(this.amazonRegionCode, machineId), null);
                return this.fromInstanceStateToMachineState(Iterables.getFirst(reservation, null).getInstanceState());
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        @Override
        public Machine getMachine(final String machineId) throws ConnectorException {
            try {
                Reservation<? extends AWSRunningInstance> reservation = Iterables.getFirst(this.syncClient
                    .getInstanceServices().describeInstancesInRegion(this.amazonRegionCode, machineId), null);
                if (reservation.isEmpty()) {
                    throw new ConnectorException("Machine with id " + machineId + " does not exist");
                }
                Machine machine = new Machine();
                this.fromAWSRunningInstanceToMachine(Iterables.getFirst(reservation, null), machine);
                return machine;
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        @Override
        public Job addVolumeToMachine(final String machineId, final MachineVolume machineVolume) throws ConnectorException {
            final String volumeId = machineVolume.getVolume().getProviderAssignedId();
            final Volume volume = this.getVolume(volumeId);
            Machine machine = this.getMachine(machineId);
            String device = machineVolume.getInitialLocation();
            if (device == null) {
                throw new ConnectorException("device not specified");
            }
            try {
                final ElasticBlockStoreClient ebsClient = this.syncClient.getElasticBlockStoreServices();
                Attachment attachment = ebsClient.attachVolumeInRegion(this.amazonRegionCode, volumeId, machineId, device);

                final Callable<Volume> task = new Callable<Volume>() {
                    @Override
                    public Volume call() throws Exception {
                        int waitTimeInSeconds = AmazonCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                        do {
                            Set<org.jclouds.ec2.domain.Volume> ebsVolumes = ebsClient.describeVolumesInRegion(
                                AmazonCloudProviderConnector.this.amazonRegionCode, volumeId);
                            if (ebsVolumes.isEmpty()) {
                                throw new Exception("Volume does not exist");
                            }
                            org.jclouds.ec2.domain.Volume ebsVolume = ebsVolumes.iterator().next();
                            if (ebsVolume.getStatus() == org.jclouds.ec2.domain.Volume.Status.IN_USE
                                || ebsVolume.getStatus() == org.jclouds.ec2.domain.Volume.Status.ERROR) {
                                AmazonCloudProviderConnector.this.fromEbsVolumetToCimiVolume(ebsVolume, volume);
                                break;
                            }
                            Thread.sleep(1000);
                        } while (waitTimeInSeconds-- > 0);
                        return volume;
                    }
                };

                ListenableFuture<Volume> result = AmazonCloudProviderConnectorFactory.this.executorService.submit(task);
                return AmazonCloudProviderConnectorFactory.this.jobManager.newJob(machine, volume, "add", result);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        @Override
        public Job removeVolumeFromMachine(final String machineId, final MachineVolume machineVolume) throws ConnectorException {
            final String volumeId = machineVolume.getVolume().getProviderAssignedId();
            final Volume volume = this.getVolume(volumeId);
            Machine machine = this.getMachine(machineId);

            try {
                final ElasticBlockStoreClient ebsClient = this.syncClient.getElasticBlockStoreServices();
                // XXX set force to true
                ebsClient.detachVolumeInRegion(this.amazonRegionCode, volumeId, true);

                final Callable<Volume> task = new Callable<Volume>() {
                    @Override
                    public Volume call() throws Exception {
                        int waitTimeInSeconds = AmazonCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                        do {
                            Set<org.jclouds.ec2.domain.Volume> ebsVolumes = ebsClient.describeVolumesInRegion(
                                AmazonCloudProviderConnector.this.amazonRegionCode, volumeId);
                            if (ebsVolumes.isEmpty()) {
                                throw new Exception("Volume does not exist");
                            }
                            org.jclouds.ec2.domain.Volume ebsVolume = ebsVolumes.iterator().next();
                            if (ebsVolume.getStatus() != org.jclouds.ec2.domain.Volume.Status.IN_USE
                                || ebsVolume.getStatus() == org.jclouds.ec2.domain.Volume.Status.ERROR) {
                                AmazonCloudProviderConnector.this.fromEbsVolumetToCimiVolume(ebsVolume, volume);
                                break;
                            }
                            Thread.sleep(1000);
                        } while (waitTimeInSeconds-- > 0);
                        return volume;
                    }
                };

                ListenableFuture<Volume> result = AmazonCloudProviderConnectorFactory.this.executorService.submit(task);
                return AmazonCloudProviderConnectorFactory.this.jobManager.newJob(machine, volume, "delete", result);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }

        }

        //
        // Volume Service
        //

        private Volume.State fromEbsVolumeStatusToCimiVolumeState(final org.jclouds.ec2.domain.Volume.Status status) {
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

        private void fromEbsVolumetToCimiVolume(final org.jclouds.ec2.domain.Volume ebsVolume, final Volume cimiVolume) {
            cimiVolume.setProviderAssignedId(ebsVolume.getId());
            cimiVolume.setCapacity(ebsVolume.getSize() * 1000 * 1000);
            cimiVolume.setState(this.fromEbsVolumeStatusToCimiVolumeState(ebsVolume.getStatus()));
        }

        @Override
        public Job createVolume(final VolumeCreate volumeCreate) throws ConnectorException {
            VolumeConfiguration volumeConfig = volumeCreate.getVolumeTemplate().getVolumeConfig();
            int sizeInGB = volumeConfig.getCapacity() / (1000 * 1000);

            try {
                final ElasticBlockStoreClient ebsClient = this.syncClient.getElasticBlockStoreServices();

                org.jclouds.ec2.domain.Volume ebsVolume = ebsClient.createVolumeInAvailabilityZone(
                    this.defaultAvailabilityZone, sizeInGB);
                final String ebsVolumeId = ebsVolume.getId();
                final Volume cimiVolume = new Volume();
                AmazonCloudProviderConnector.this.fromEbsVolumetToCimiVolume(ebsVolume, cimiVolume);

                final Callable<Volume> createTask = new Callable<Volume>() {
                    @Override
                    public Volume call() throws Exception {
                        int waitTimeInSeconds = AmazonCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                        do {
                            Set<org.jclouds.ec2.domain.Volume> ebsVolumes = ebsClient.describeVolumesInRegion(
                                AmazonCloudProviderConnector.this.amazonRegionCode, ebsVolumeId);
                            if (ebsVolumes.isEmpty()) {
                                throw new Exception("Volume does not exist");
                            }
                            org.jclouds.ec2.domain.Volume ebsVolume = ebsVolumes.iterator().next();
                            if (ebsVolume.getStatus() != org.jclouds.ec2.domain.Volume.Status.CREATING) {
                                AmazonCloudProviderConnector.this.fromEbsVolumetToCimiVolume(ebsVolume, cimiVolume);
                                break;
                            }
                            Thread.sleep(1000);
                        } while (waitTimeInSeconds-- > 0);
                        return cimiVolume;
                    }
                };
                ListenableFuture<Volume> result = AmazonCloudProviderConnectorFactory.this.executorService.submit(createTask);
                return AmazonCloudProviderConnectorFactory.this.jobManager.newJob(cimiVolume, null, "add", result);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        @Override
        public Job deleteVolume(final String volumeId) throws ConnectorException {
            final Volume volume = this.getVolume(volumeId);

            try {
                final ElasticBlockStoreClient ebsClient = this.syncClient.getElasticBlockStoreServices();

                ebsClient.deleteVolumeInRegion(this.amazonRegionCode, volumeId);

                final Callable<Void> task = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        int waitTimeInSeconds = AmazonCloudProviderConnectorFactory.DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                        do {
                            try {
                                Set<org.jclouds.ec2.domain.Volume> ebsVolumes = ebsClient.describeVolumesInRegion(
                                    AmazonCloudProviderConnector.this.amazonRegionCode, volumeId);
                                if (ebsVolumes.isEmpty()) {
                                    break;
                                }
                            } catch (Exception e) {
                                // if volume does not exist an exception is
                                // raised by jclouds
                                break;
                            }
                            Thread.sleep(1000);
                        } while (waitTimeInSeconds-- > 0);
                        return null;
                    }
                };
                ListenableFuture<Void> result = AmazonCloudProviderConnectorFactory.this.executorService.submit(task);
                return AmazonCloudProviderConnectorFactory.this.jobManager.newJob(volume, null, "delete", result);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        @Override
        public State getVolumeState(final String volumeId) throws ConnectorException {
            try {
                Set<org.jclouds.ec2.domain.Volume> ebsVolumes = this.syncClient.getElasticBlockStoreServices()
                    .describeVolumesInRegion(AmazonCloudProviderConnector.this.amazonRegionCode, volumeId);
                if (ebsVolumes.isEmpty()) {
                    throw new Exception("Volume does not exist");
                }
                org.jclouds.ec2.domain.Volume ebsVolume = ebsVolumes.iterator().next();
                return this.fromEbsVolumeStatusToCimiVolumeState(ebsVolume.getStatus());
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        @Override
        public Volume getVolume(final String volumeId) throws ConnectorException {
            try {
                Set<org.jclouds.ec2.domain.Volume> ebsVolumes = this.syncClient.getElasticBlockStoreServices()
                    .describeVolumesInRegion(AmazonCloudProviderConnector.this.amazonRegionCode, volumeId);
                if (ebsVolumes.isEmpty()) {
                    return null;
                }
                org.jclouds.ec2.domain.Volume ebsVolume = ebsVolumes.iterator().next();
                Volume cimiVolume = new Volume();
                AmazonCloudProviderConnector.this.fromEbsVolumetToCimiVolume(ebsVolume, cimiVolume);
                return cimiVolume;
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
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
