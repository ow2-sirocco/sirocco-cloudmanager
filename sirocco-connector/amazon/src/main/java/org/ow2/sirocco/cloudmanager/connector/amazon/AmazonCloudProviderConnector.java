package org.ow2.sirocco.cloudmanager.connector.amazon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.AWSEC2AsyncClient;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.options.AWSDescribeImagesOptions;
import org.jclouds.aws.ec2.services.AWSKeyPairClient;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.ec2.compute.domain.EC2HardwareBuilder;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.InstanceStateChange;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.services.ElasticBlockStoreClient;
import org.jclouds.rest.RestContext;
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
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.Type;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume.State;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.ProviderMapping;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.inject.Module;

public class AmazonCloudProviderConnector implements ICloudProviderConnector, IComputeService, IVolumeService, INetworkService,
    IImageService {
    private static Logger logger = LoggerFactory.getLogger(AmazonCloudProviderConnector.class);

    public static final String CLOUD_PROVIDER_TYPE = "amazon";

    private static int DEFAULT_RESOURCE_STATE_CHANGE_WAIT_TIME_IN_SECONDS = 180;

    private static final int THREADPOOL_SIZE = 10;

    private static final Map<CloudProviderLocation, String> locationMap = new HashMap<CloudProviderLocation, String>();

    static {
        AmazonCloudProviderConnector.locationMap.put(new CloudProviderLocation("IE", null, "Ireland", null), "eu-west-1");
        AmazonCloudProviderConnector.locationMap.put(new CloudProviderLocation("US", "US-VA", "United States", "Virginia"),
            "us-east-1");
        AmazonCloudProviderConnector.locationMap.put(new CloudProviderLocation("US", "US-CA", "United States", "California"),
            "us-west-1");
        AmazonCloudProviderConnector.locationMap.put(new CloudProviderLocation("US", "US-OR", "United States", "Oregon"),
            "us-west-2");
        AmazonCloudProviderConnector.locationMap
            .put(new CloudProviderLocation("SG", null, "Singapore", null), "ap-southeast-1");
        AmazonCloudProviderConnector.locationMap.put(new CloudProviderLocation("AU", "AU-NSW", "Australia", "Sydney"),
            "ap-southeast-2");
        AmazonCloudProviderConnector.locationMap.put(new CloudProviderLocation("JP", "JP-13", "Japan", "Tokyo"),
            "ap-northeast-1");
        AmazonCloudProviderConnector.locationMap.put(new CloudProviderLocation("BR", "BR-SP", "Brazil", "Sao Paulo"),
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

    private List<AmazonProvider> providers = new ArrayList<AmazonProvider>();

    private synchronized AmazonProvider getProvider(final ProviderTarget target) {
        for (AmazonProvider provider : this.providers) {
            if (provider.cloudProviderAccount.equals(target.getAccount())) {
                // location can be null?
                if (provider.cloudProviderLocation != target.getLocation()) {
                    if (target.getLocation() != null) {
                        if (provider.cloudProviderLocation.getId().equals(target.getLocation().getId())) {
                            return provider;
                        }
                    }
                } else {
                    return provider;
                }
            }
        }

        AmazonProvider provider = new AmazonProvider(target.getAccount(), target.getLocation());
        this.providers.add(provider);
        return provider;
    }

    @Override
    public Set<CloudProviderLocation> getLocations() {
        return AmazonCloudProviderConnector.locationMap.keySet();
    }

    @Override
    public IComputeService getComputeService() throws ConnectorException {
        return this;
    }

    @Override
    public ISystemService getSystemService() throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public IVolumeService getVolumeService() throws ConnectorException {
        return this;
    }

    @Override
    public IImageService getImageService() throws ConnectorException {
        return this;
    }

    @Override
    public INetworkService getNetworkService() throws ConnectorException {
        return this;
    }

    @Override
    public Network createNetwork(final NetworkCreate networkCreate, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public Network getNetwork(final String networkId, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public Network.State getNetworkState(final String networkId, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public List<Network> getNetworks(final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getNetworks();
    }

    @Override
    public void deleteNetwork(final String networkId, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void startNetwork(final String networkId, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void stopNetwork(final String networkId, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public NetworkPort createNetworkPort(final NetworkPortCreate networkPortCreate, final ProviderTarget target)
        throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public NetworkPort getNetworkPort(final String networkPortId, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void deleteNetworkPort(final String networkPortId, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void startNetworkPort(final String networkPortId, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void stopNetworkPort(final String networkPortId, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public ForwardingGroup createForwardingGroup(final ForwardingGroupCreate forwardingGroupCreate, final ProviderTarget target)
        throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public ForwardingGroup getForwardingGroup(final String forwardingGroupId, final ProviderTarget target)
        throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void deleteForwardingGroup(final ForwardingGroup forwardingGroup, final ProviderTarget target)
        throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void addNetworkToForwardingGroup(final String forwardingGroupId, final ForwardingGroupNetwork fgNetwork,
        final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public void removeNetworkFromForwardingGroup(final String forwardingGroupId, final String networkId,
        final ProviderTarget target) throws ConnectorException {
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
    public Volume createVolume(final VolumeCreate volumeCreate, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).createVolume(volumeCreate);
    }

    @Override
    public void deleteVolume(final String volumeId, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).deleteVolume(volumeId);
    }

    @Override
    public Volume.State getVolumeState(final String volumeId, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getVolumeState(volumeId);
    }

    @Override
    public Volume getVolume(final String volumeId, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getVolume(volumeId);
    }

    @Override
    public VolumeImage createVolumeImage(final VolumeImage volumeImage, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public VolumeImage createVolumeSnapshot(final String volumeId, final VolumeImage volumeImage, final ProviderTarget target)
        throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public VolumeImage getVolumeImage(final String volumeImageId, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getVolumeImage(volumeImageId);
    }

    @Override
    public void deleteVolumeImage(final String volumeImageId, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).deleteVolumeImage(volumeImageId);
    }

    @Override
    public Machine createMachine(final MachineCreate machineCreate, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).createMachine(machineCreate);
    }

    @Override
    public void startMachine(final String machineId, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).startMachine(machineId);
    }

    @Override
    public void stopMachine(final String machineId, final boolean force, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).stopMachine(machineId, force);
    }

    @Override
    public void suspendMachine(final String machineId, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).suspendMachine(machineId);
    }

    @Override
    public void restartMachine(final String machineId, final boolean force, final ProviderTarget target)
        throws ConnectorException {
        this.getProvider(target).restartMachine(machineId, force);
    }

    @Override
    public void pauseMachine(final String machineId, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).pauseMachine(machineId);
    }

    @Override
    public void deleteMachine(final String machineId, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).deleteMachine(machineId);
    }

    @Override
    public MachineImage captureMachine(final String machineId, final MachineImage machineImage, final ProviderTarget target)
        throws ConnectorException {
        return this.getProvider(target).captureMachine(machineId, machineImage);
    }

    @Override
    public Machine.State getMachineState(final String machineId, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getMachineState(machineId);
    }

    @Override
    public Machine getMachine(final String machineId, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getMachine(machineId);
    }

    @Override
    public void addVolumeToMachine(final String machineId, final MachineVolume machineVolume, final ProviderTarget target)
        throws ConnectorException {
        this.getProvider(target).addVolumeToMachine(machineId, machineVolume);
    }

    @Override
    public void removeVolumeFromMachine(final String machineId, final MachineVolume machineVolume, final ProviderTarget target)
        throws ConnectorException {
        this.getProvider(target).removeVolumeFromMachine(machineId, machineVolume);
    }

    @Override
    public void deleteMachineImage(final String imageId, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public MachineImage getMachineImage(final String machineImageId, final ProviderTarget target) throws ConnectorException {
        throw new ConnectorException("unsupported operation");
    }

    @Override
    public List<MachineConfiguration> getMachineConfigs(final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getMachineConfigs();
    }

    @Override
    public List<MachineImage> getMachineImages(final boolean returnPublicImages, final Map<String, String> searchCriteria,
        final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getMachineImages(returnPublicImages, searchCriteria);
    }

    private static class AmazonProvider {

        private final String cloudProviderId;

        private CloudProviderAccount cloudProviderAccount;

        private CloudProviderLocation cloudProviderLocation;

        private String amazonRegionCode;

        private String defaultAvailabilityZone;

        final String accessKeyId;

        final String secretKeyId;

        private AWSEC2Client syncClient;

        private AWSEC2AsyncClient asyncClient;

        private Network cimiPublicNetwork;

        public AmazonProvider(final CloudProviderAccount cloudProviderAccount, final CloudProviderLocation cloudProviderLocation) {
            this.cloudProviderId = UUID.randomUUID().toString();
            this.cloudProviderLocation = cloudProviderLocation;
            this.cloudProviderAccount = cloudProviderAccount;
            this.accessKeyId = cloudProviderAccount.getLogin();
            this.secretKeyId = cloudProviderAccount.getPassword();
            this.amazonRegionCode = AmazonCloudProviderConnector.locationMap.get(cloudProviderLocation);

            Properties overrides = new Properties();

            String httpProxyHost = java.lang.System.getProperty("http.proxyHost");
            String httpProxyPort = java.lang.System.getProperty("http.proxyPort");
            if (httpProxyHost != null) {
                overrides.setProperty(Constants.PROPERTY_PROXY_HOST, httpProxyHost);
            }
            if (httpProxyPort != null) {
                overrides.setProperty(Constants.PROPERTY_PROXY_PORT, httpProxyPort);
            }
            overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
            overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");

            ContextBuilder builder = ContextBuilder.newBuilder("aws-ec2").credentials(this.accessKeyId, this.secretKeyId)
                .modules(ImmutableSet.<Module> of()).overrides(overrides);
            RestContext<AWSEC2Client, AWSEC2AsyncClient> context = builder.buildView(ComputeServiceContext.class).unwrap();

            this.syncClient = context.getApi();
            this.asyncClient = context.getAsyncApi();

            this.defaultAvailabilityZone = this.syncClient.getAvailabilityZoneAndRegionServices()
                .describeAvailabilityZonesInRegion(this.amazonRegionCode).iterator().next().getZone();

            this.cimiPublicNetwork = new Network();
            this.cimiPublicNetwork.setName("Amazon default public network");
            this.cimiPublicNetwork.setProviderAssignedId("public" + cloudProviderAccount.getLogin());
            this.cimiPublicNetwork.setState(Network.State.STARTED);
            this.cimiPublicNetwork.setNetworkType(Network.Type.PUBLIC);
            this.cimiPublicNetwork.setCloudProviderAccount(cloudProviderAccount);
            this.cimiPublicNetwork.setLocation(cloudProviderLocation);
        }

        //
        // Compute Service
        //

        private String findSuitableInstanceType(final MachineConfiguration machineConfig) {
            long memoryInMBytes = machineConfig.getMemory() / 1024;

            for (Hardware hardware : AmazonCloudProviderConnector.AWSEC2_HARDWARE_MAP.values()) {
                if (memoryInMBytes == hardware.getRam()) {
                    if (machineConfig.getCpu() == hardware.getProcessors().size()) {
                        // special test for micro instance with no disk
                        if (hardware.getVolumes().size() == 0 && machineConfig.getDisks().size() == 1
                            && machineConfig.getDisks().get(0).getCapacity() == 0) {
                            return hardware.getProviderId();
                        }
                        if (machineConfig.getDisks().size() == hardware.getVolumes().size()) {
                            // XXX we assume that disks are ordered the same way
                            int i = 0;
                            for (; i < machineConfig.getDisks().size(); i++) {
                                long diskSizeInGigaBytes = machineConfig.getDisks().get(i).getCapacity() / (1000 * 1000);
                                long hardwareDiskSizeInGigaBytes = hardware.getVolumes().get(i).getSize().longValue();
                                if (diskSizeInGigaBytes != hardwareDiskSizeInGigaBytes) {
                                    break;
                                }
                            }
                            if (i >= machineConfig.getDisks().size()) {
                                return hardware.getProviderId();
                            }
                        }
                    }
                }
            }
            return null;
        }

        public List<MachineConfiguration> getMachineConfigs() {
            List<MachineConfiguration> result = new ArrayList<>();
            for (Hardware hardware : AmazonCloudProviderConnector.AWSEC2_HARDWARE_MAP.values()) {
                MachineConfiguration machineConfig = new MachineConfiguration();
                machineConfig.setName(hardware.getId());
                machineConfig.setCpu(hardware.getProcessors().size());
                machineConfig.setMemory(hardware.getRam() * 1024);
                List<DiskTemplate> disks = new ArrayList<>();
                if (hardware.getVolumes().size() == 0) {
                    DiskTemplate disk = new DiskTemplate();
                    disk.setCapacity(0);
                    disks.add(disk);
                } else {
                    for (org.jclouds.compute.domain.Volume volume : hardware.getVolumes()) {
                        DiskTemplate disk = new DiskTemplate();
                        disk.setCapacity(volume.getSize().intValue() * 1000 * 1000);
                        disks.add(disk);
                    }
                }
                machineConfig.setDisks(disks);

                ProviderMapping providerMapping = new ProviderMapping();
                providerMapping.setProviderAssignedId(hardware.getProviderId());
                providerMapping.setProviderAccount(this.cloudProviderAccount);
                machineConfig.setProviderMappings(Collections.singletonList(providerMapping));
                result.add(machineConfig);
            }
            return result;
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

            if (runningInstance.getPrivateIpAddress() != null || runningInstance.getIpAddress() != null) {
                List<MachineNetworkInterfaceAddress> cimiAddresses = new ArrayList<MachineNetworkInterfaceAddress>();

                MachineNetworkInterface nic = new MachineNetworkInterface();
                nic.setNetwork(this.cimiPublicNetwork);
                nic.setAddresses(cimiAddresses);
                nic.setState(MachineNetworkInterface.InterfaceState.ACTIVE);
                nics.add(nic);

                if (runningInstance.getPrivateIpAddress() != null) {
                    MachineNetworkInterfaceAddress entry = new MachineNetworkInterfaceAddress();
                    org.ow2.sirocco.cloudmanager.model.cimi.Address cimiAddress = new org.ow2.sirocco.cloudmanager.model.cimi.Address();
                    cimiAddress = new org.ow2.sirocco.cloudmanager.model.cimi.Address();
                    cimiAddress.setIp(runningInstance.getPrivateIpAddress());
                    cimiAddress.setAllocation("dynamic");
                    cimiAddress.setProtocol("IPv4");
                    cimiAddress.setHostName(runningInstance.getPrivateDnsName());
                    entry.setAddress(cimiAddress);
                    cimiAddresses.add(entry);
                }

                if (runningInstance.getIpAddress() != null) {
                    org.ow2.sirocco.cloudmanager.model.cimi.Address cimiAddress = new org.ow2.sirocco.cloudmanager.model.cimi.Address();
                    cimiAddress.setIp(runningInstance.getIpAddress());
                    cimiAddress.setAllocation("dynamic");
                    cimiAddress.setProtocol("IPv4");
                    cimiAddress.setHostName(runningInstance.getDnsName());
                    MachineNetworkInterfaceAddress entry = new MachineNetworkInterfaceAddress();
                    entry.setAddress(cimiAddress);
                    cimiAddresses.add(entry);
                }
            }

            Hardware hardware = AmazonCloudProviderConnector.AWSEC2_HARDWARE_MAP.get(runningInstance.getInstanceType());
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

            // volumes

            List<MachineVolume> volumeAttachments = new ArrayList<MachineVolume>();
            machine.setVolumes(volumeAttachments);

            for (Map.Entry<String, BlockDevice> entry : runningInstance.getEbsBlockDevices().entrySet()) {
                BlockDevice blockDevice = entry.getValue();
                if (blockDevice.isDeleteOnTermination()) {
                    continue;
                }
                MachineVolume attachment = new MachineVolume();
                Volume volume = new Volume();
                volume.setProviderAssignedId(blockDevice.getVolumeId());
                attachment.setVolume(volume);
                MachineVolume.State attachmentState = null;
                switch (blockDevice.getAttachmentStatus()) {
                case ATTACHED:
                    attachmentState = MachineVolume.State.ATTACHED;
                    break;
                case ATTACHING:
                    attachmentState = MachineVolume.State.ATTACHING;
                    break;
                case DETACHED:
                    attachmentState = MachineVolume.State.DELETED;
                    break;
                case DETACHING:
                    attachmentState = MachineVolume.State.DETACHING;
                    break;
                case BUSY:
                case UNRECOGNIZED:
                    attachmentState = MachineVolume.State.PENDING;
                    break;
                }
                attachment.setState(attachmentState);
                volumeAttachments.add(attachment);
            }

        }

        private KeyPair findOrInstallKeyPair(final String publicKey) {
            AWSKeyPairClient keyPairClient = this.syncClient.getKeyPairServices();

            String keyPairName = "keyPair-" + UUID.randomUUID();
            KeyPair kp = this.syncClient.getKeyPairServices().importKeyPairInRegion(this.amazonRegionCode, keyPairName,
                publicKey);

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

        public Machine createMachine(final MachineCreate machineCreate) throws ConnectorException {
            final String instanceType = this.findSuitableInstanceType(machineCreate.getMachineTemplate().getMachineConfig());
            if (instanceType == null) {
                throw new ConnectorException("Not suitable instance type found");
            }
            KeyPair keyPair = null;
            if (machineCreate.getMachineTemplate().getCredential() != null) {
                keyPair = this.findOrInstallKeyPair(new String(machineCreate.getMachineTemplate().getCredential()
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

            ProviderMapping mapping = ProviderMapping.find(machineCreate.getMachineTemplate().getMachineImage(),
                this.cloudProviderAccount, this.cloudProviderLocation);
            if (mapping == null) {
                throw new ConnectorException("Cannot find imageId for image "
                    + machineCreate.getMachineTemplate().getMachineImage().getName());
            }
            String imageId = mapping.getProviderAssignedId();

            Reservation<? extends AWSRunningInstance> reservation = this.syncClient.getInstanceServices().runInstancesInRegion(
                this.amazonRegionCode, this.defaultAvailabilityZone, imageId, 1, 1, options);

            final Machine machine = new Machine();
            this.fromAWSRunningInstanceToMachine(Iterables.getFirst(reservation, null), machine);

            return machine;
        }

        public void startMachine(final String machineId) throws ConnectorException {

            try {
                Set<? extends InstanceStateChange> startResult = this.syncClient.getInstanceServices().startInstancesInRegion(
                    this.amazonRegionCode, machineId);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        public void stopMachine(final String machineId, final boolean force) throws ConnectorException {
            try {
                // XXX set force to true
                Set<? extends InstanceStateChange> stopResult = this.syncClient.getInstanceServices().stopInstancesInRegion(
                    this.amazonRegionCode, true, machineId);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        public void suspendMachine(final String machineId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        public void restartMachine(final String machineId, final boolean force) throws ConnectorException {
            try {
                this.syncClient.getInstanceServices().rebootInstancesInRegion(this.amazonRegionCode, machineId);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        public void pauseMachine(final String machineId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        public MachineImage captureMachine(final String machineId, final MachineImage machineImage) throws ConnectorException {
            // TODO
            throw new ConnectorException("unsupported operation");
        }

        public void deleteMachine(final String machineId) throws ConnectorException {
            try {
                Set<? extends InstanceStateChange> deleteResult = this.syncClient.getInstanceServices()
                    .terminateInstancesInRegion(this.amazonRegionCode, machineId);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

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

        public Machine getMachine(final String machineId) throws ConnectorException {
            try {
                Reservation<? extends AWSRunningInstance> reservation = Iterables.getFirst(this.syncClient
                    .getInstanceServices().describeInstancesInRegion(this.amazonRegionCode, machineId), null);
                if (!reservation.isEmpty()) {
                    Machine machine = new Machine();
                    this.fromAWSRunningInstanceToMachine(Iterables.getFirst(reservation, null), machine);
                    return machine;
                }
            } catch (org.jclouds.rest.ResourceNotFoundException e) {

            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
            throw new ResourceNotFoundException("Machine with id " + machineId + " does not exist");
        }

        public void addVolumeToMachine(final String machineId, final MachineVolume machineVolume) throws ConnectorException {
            final String volumeId = machineVolume.getVolume().getProviderAssignedId();
            String device = machineVolume.getInitialLocation();
            if (device == null) {
                throw new ConnectorException("device not specified");
            }
            try {
                final ElasticBlockStoreClient ebsClient = this.syncClient.getElasticBlockStoreServices();
                Attachment attachment = ebsClient.attachVolumeInRegion(this.amazonRegionCode, volumeId, machineId, device);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        public void removeVolumeFromMachine(final String machineId, final MachineVolume machineVolume)
            throws ConnectorException {
            final String volumeId = machineVolume.getVolume().getProviderAssignedId();
            try {
                final ElasticBlockStoreClient ebsClient = this.syncClient.getElasticBlockStoreServices();
                // XXX set force to true
                ebsClient.detachVolumeInRegion(this.amazonRegionCode, volumeId, true);
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

        public Volume createVolume(final VolumeCreate volumeCreate) throws ConnectorException {
            VolumeConfiguration volumeConfig = volumeCreate.getVolumeTemplate().getVolumeConfig();
            int sizeInGB = volumeConfig.getCapacity() / (1000 * 1000);

            try {
                final ElasticBlockStoreClient ebsClient = this.syncClient.getElasticBlockStoreServices();
                AmazonCloudProviderConnector.logger.info("zone=" + this.defaultAvailabilityZone);
                org.jclouds.ec2.domain.Volume ebsVolume = ebsClient.createVolumeInAvailabilityZone(
                    this.defaultAvailabilityZone, sizeInGB);
                final Volume cimiVolume = new Volume();
                this.fromEbsVolumetToCimiVolume(ebsVolume, cimiVolume);
                return cimiVolume;
            } catch (Exception ex) {
                AmazonCloudProviderConnector.logger.error("Failed to create volume", ex);
                throw new ConnectorException(ex.getMessage());
            }
        }

        public void deleteVolume(final String volumeId) throws ConnectorException {
            try {
                final ElasticBlockStoreClient ebsClient = this.syncClient.getElasticBlockStoreServices();
                ebsClient.deleteVolumeInRegion(this.amazonRegionCode, volumeId);
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        public State getVolumeState(final String volumeId) throws ConnectorException {
            try {
                Set<org.jclouds.ec2.domain.Volume> ebsVolumes = this.syncClient.getElasticBlockStoreServices()
                    .describeVolumesInRegion(this.amazonRegionCode, volumeId);
                if (ebsVolumes.isEmpty()) {
                    throw new Exception("Volume does not exist");
                }
                org.jclouds.ec2.domain.Volume ebsVolume = ebsVolumes.iterator().next();
                return this.fromEbsVolumeStatusToCimiVolumeState(ebsVolume.getStatus());
            } catch (Exception ex) {
                throw new ConnectorException(ex.getMessage());
            }
        }

        public Volume getVolume(final String volumeId) throws ConnectorException {
            try {
                Set<org.jclouds.ec2.domain.Volume> ebsVolumes = this.syncClient.getElasticBlockStoreServices()
                    .describeVolumesInRegion(this.amazonRegionCode, volumeId);
                if (!ebsVolumes.isEmpty()) {
                    org.jclouds.ec2.domain.Volume ebsVolume = ebsVolumes.iterator().next();
                    Volume cimiVolume = new Volume();
                    this.fromEbsVolumetToCimiVolume(ebsVolume, cimiVolume);
                    return cimiVolume;
                }
            } catch (org.jclouds.rest.ResourceNotFoundException e) {

            } catch (Exception ex) {
                AmazonCloudProviderConnector.logger.error("getVolume", ex);
                throw new ConnectorException(ex.getMessage());
            }
            throw new ResourceNotFoundException("volume with id " + volumeId + " not found");
        }

        public void createVolumeImage(final VolumeImage volumeImage) throws ConnectorException {
            // TODO Auto-generated method stub
            throw new ConnectorException("unsupported operation");
        }

        public void createVolumeSnapshot(final String volumeId, final VolumeImage volumeImage) throws ConnectorException {
            // TODO Auto-generated method stub
            throw new ConnectorException("unsupported operation");
        }

        public VolumeImage getVolumeImage(final String volumeImageId) throws ConnectorException {
            // TODO Auto-generated method stub
            throw new ConnectorException("unsupported operation");
        }

        public void deleteVolumeImage(final String volumeImageId) throws ConnectorException {
            // TODO Auto-generated method stub
            throw new ConnectorException("unsupported operation");
        }

        public List<MachineImage> getMachineImages(final boolean returnAccountImagesOnly,
            final Map<String, String> searchCriteria) throws ConnectorException {
            List<MachineImage> result = new ArrayList<>();
            AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
            if (returnAccountImagesOnly) {
                options.ownedBy("self");
            }
            Multimap<String, String> filters = ArrayListMultimap.create();
            // filters.put("name", "ubuntu/images/ebs*2013*");
            filters.put("name", "debian-*2013????");
            filters.put("root-device-type", "ebs");
            options.filters(filters);
            for (Image image : this.syncClient.getAMIServices().describeImagesInRegion(this.amazonRegionCode, options)) {
                MachineImage machineImage = new MachineImage();
                machineImage.setName(image.getName());
                System.out.println(image.getName());
                machineImage.setType(Type.IMAGE);
                switch (image.getImageState()) {
                case AVAILABLE:
                    machineImage.setState(MachineImage.State.AVAILABLE);
                    break;
                case DEREGISTERED:
                    // ??
                    continue;
                case UNRECOGNIZED:
                    // ??
                    continue;
                default:
                    break;

                }
                ProviderMapping providerMapping = new ProviderMapping();
                providerMapping.setProviderAssignedId(image.getId());
                providerMapping.setProviderAccount(this.cloudProviderAccount);
                providerMapping.setProviderLocation(this.cloudProviderLocation);
                machineImage.setProviderMappings(Collections.singletonList(providerMapping));
                result.add(machineImage);
            }
            return result;
        }

        public List<Network> getNetworks() {
            return Collections.singletonList(this.cimiPublicNetwork);
        }
    }

}
