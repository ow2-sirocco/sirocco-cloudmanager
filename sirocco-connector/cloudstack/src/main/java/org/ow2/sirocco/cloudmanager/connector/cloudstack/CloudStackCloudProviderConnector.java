package org.ow2.sirocco.cloudmanager.connector.cloudstack;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.NIC;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.SshKeyPair;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.Template.Status;
import org.jclouds.cloudstack.domain.TemplateFilter;
import org.jclouds.cloudstack.domain.TrafficType;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.features.NetworkClient;
import org.jclouds.cloudstack.features.OfferingClient;
import org.jclouds.cloudstack.features.SSHKeyPairClient;
import org.jclouds.cloudstack.features.TemplateClient;
import org.jclouds.cloudstack.features.VirtualMachineClient;
import org.jclouds.cloudstack.features.ZoneClient;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.jclouds.cloudstack.options.ListTemplatesOptions;
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
import org.ow2.sirocco.cloudmanager.model.cimi.Subnet;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume.State;
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

import com.google.common.collect.ImmutableSet;
import com.google.common.io.BaseEncoding;
import com.google.inject.Module;

public class CloudStackCloudProviderConnector implements ICloudProviderConnector, IComputeService, IVolumeService,
    INetworkService, IImageService {
    private static Logger logger = LoggerFactory.getLogger(CloudStackCloudProviderConnector.class);

    private List<CloudStackProvider> providers = new ArrayList<CloudStackProvider>();

    private synchronized CloudStackProvider getProvider(final ProviderTarget target) {
        for (CloudStackProvider provider : this.providers) {
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

        CloudStackProvider provider = new CloudStackProvider(target.getAccount(), target.getLocation());
        this.providers.add(provider);
        return provider;
    }

    @Override
    public Set<CloudProviderLocation> getLocations() {
        return Collections.emptySet();
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
        return this.getProvider(target).createNetwork(networkCreate);
    }

    @Override
    public Network getNetwork(final String networkId, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getNetwork(networkId);
    }

    @Override
    public Network.State getNetworkState(final String networkId, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getNetworkState(networkId);
    }

    @Override
    public List<Network> getNetworks(final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getNetworks();
    }

    @Override
    public void deleteNetwork(final String networkId, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).deleteNetwork(networkId);
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
    public void addMachineToSecurityGroup(final String machineId, final String groupId, final ProviderTarget target)
        throws ConnectorException {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeMachineFromSecurityGroup(final String machineId, final String groupId, final ProviderTarget target)
        throws ConnectorException {
        // TODO Auto-generated method stub

    }

    @Override
    public Address allocateAddress(final Map<String, String> properties, final ProviderTarget target) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deallocateAddress(final Address address, final ProviderTarget target) throws ConnectorException {
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
        this.getProvider(target).deleteMachineImage(imageId);
    }

    @Override
    public MachineImage getMachineImage(final String machineImageId, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getMachineImage(machineImageId);
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

    private static class CloudStackProvider {

        private CloudProviderAccount cloudProviderAccount;

        private CloudProviderLocation cloudProviderLocation;

        private String apiKey;

        private String secretKey;

        private VirtualMachineClient vmClient;

        private NetworkClient networkClient;

        private OfferingClient offeringClient;

        private TemplateClient templateClient;

        private SSHKeyPairClient keyPairClient;

        private String defaultZoneId;

        private Map<String, String> keyPairMap = new HashMap<String, String>();

        public CloudStackProvider(final CloudProviderAccount cloudProviderAccount,
            final CloudProviderLocation cloudProviderLocation) {
            this.cloudProviderLocation = cloudProviderLocation;
            this.cloudProviderAccount = cloudProviderAccount;
            this.apiKey = cloudProviderAccount.getLogin();
            this.secretKey = cloudProviderAccount.getPassword();
            Properties overrides = new Properties();

            // String httpProxyHost = java.lang.System.getProperty("http.proxyHost");
            // String httpProxyPort = java.lang.System.getProperty("http.proxyPort");
            // if (httpProxyHost != null) {
            // overrides.setProperty(Constants.PROPERTY_PROXY_HOST, httpProxyHost);
            // }
            // if (httpProxyPort != null) {
            // overrides.setProperty(Constants.PROPERTY_PROXY_PORT, httpProxyPort);
            // }
            overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
            overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");

            CloudStackContext context = ContextBuilder.newBuilder("cloudstack")
                .endpoint(cloudProviderAccount.getCloudProvider().getEndpoint()).credentials(this.apiKey, this.secretKey)
                .modules(ImmutableSet.<Module> of()).overrides(overrides).buildApi(CloudStackContext.class);

            this.vmClient = context.getApi().getVirtualMachineClient();
            this.networkClient = context.getApi().getNetworkClient();
            this.offeringClient = context.getApi().getOfferingClient();
            this.templateClient = context.getApi().getTemplateClient();
            this.keyPairClient = context.getApi().getSSHKeyPairClient();

            ZoneClient zoneClient = context.getApi().getZoneClient();

            this.defaultZoneId = zoneClient.listZones().iterator().next().getId();
        }

        //
        // Compute Service
        //

        public List<MachineConfiguration> getMachineConfigs() {
            List<MachineConfiguration> result = new ArrayList<>();
            for (ServiceOffering offering : this.offeringClient.listServiceOfferings()) {
                MachineConfiguration machineConfig = new MachineConfiguration();
                machineConfig.setName(offering.getName());
                machineConfig.setCpu(offering.getCpuNumber());
                machineConfig.setMemory(offering.getMemory() * 1024);

                // Add disk of 0 size because VM disk size is determined by the image size
                List<DiskTemplate> disks = new ArrayList<>();
                DiskTemplate disk = new DiskTemplate();
                disk.setCapacity(0);
                disks.add(disk);
                machineConfig.setDisks(disks);

                ProviderMapping providerMapping = new ProviderMapping();
                providerMapping.setProviderAssignedId(offering.getId());
                providerMapping.setProviderAccount(this.cloudProviderAccount);
                machineConfig.setProviderMappings(Collections.singletonList(providerMapping));
                result.add(machineConfig);
            }
            return result;
        }

        public Machine createMachine(final MachineCreate machineCreate) throws ConnectorException {
            String keyPairName = null;
            if (machineCreate.getMachineTemplate().getCredential() != null) {
                keyPairName = this.findOrInstallKeyPair(new String(machineCreate.getMachineTemplate().getCredential()
                    .getPublicKey()));
            }
            DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
            if (keyPairName != null) {
                options = options.keyPair(keyPairName);
            }
            String userData = machineCreate.getMachineTemplate().getUserData();
            if (userData != null) {
                options.userData(userData.getBytes());
            }

            ProviderMapping mapping = ProviderMapping.find(machineCreate.getMachineTemplate().getMachineImage(),
                this.cloudProviderAccount, this.cloudProviderLocation);
            if (mapping == null) {
                throw new ConnectorException("Cannot find imageId for image "
                    + machineCreate.getMachineTemplate().getMachineImage().getName());
            }
            String templateId = mapping.getProviderAssignedId();

            mapping = ProviderMapping.find(machineCreate.getMachineTemplate().getMachineConfig(), this.cloudProviderAccount,
                this.cloudProviderLocation);
            if (mapping == null) {
                throw new ConnectorException("Cannot find CloudStack id for MachineConfig "
                    + machineCreate.getMachineTemplate().getMachineConfig().getName());
            }
            String serviceOfferingId = mapping.getProviderAssignedId();

            options = options.displayName(machineCreate.getName());
            AsyncCreateResponse response = this.vmClient.deployVirtualMachineInZone(this.defaultZoneId, serviceOfferingId,
                templateId, options);
            final Machine machine = new Machine();
            machine.setProviderAssignedId(response.getId());

            return machine;
        }

        private String findOrInstallKeyPair(final String publicKey) {
            String keyPairName = this.keyPairMap.get(publicKey);
            if (keyPairName != null) {
                return keyPairName;
            }

            String publicKeyFingerPrint = CloudStackCloudProviderConnector.getPublicKeyFingerprint(publicKey);
            for (SshKeyPair keyPair : this.keyPairClient.listSSHKeyPairs()) {
                if (keyPair.getFingerprint().equals(publicKeyFingerPrint)) {
                    this.keyPairMap.put(publicKey, keyPair.getName());
                    return keyPair.getName();
                }
            }

            SshKeyPair newKeyPair = this.keyPairClient.registerSSHKeyPair("keypair-" + UUID.randomUUID().toString(), publicKey);
            this.keyPairMap.put(publicKey, newKeyPair.getName());
            return newKeyPair.getName();
        }

        public void startMachine(final String machineId) throws ConnectorException {
            try {
                this.vmClient.startVirtualMachine(machineId);
            } catch (Exception e) {
                throw new ConnectorException(e.getMessage());
            }
        }

        public void stopMachine(final String machineId, final boolean force) throws ConnectorException {
            try {
                this.vmClient.stopVirtualMachine(machineId);
            } catch (Exception e) {
                throw new ConnectorException(e.getMessage());
            }
        }

        public void suspendMachine(final String machineId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        public void restartMachine(final String machineId, final boolean force) throws ConnectorException {
            try {
                this.vmClient.rebootVirtualMachine(machineId);
            } catch (Exception e) {
                throw new ConnectorException(e.getMessage());
            }
        }

        public void pauseMachine(final String machineId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        public MachineImage captureMachine(final String machineId, final MachineImage machineImage) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        public void deleteMachine(final String machineId) throws ConnectorException {
            try {
                this.vmClient.destroyVirtualMachine(machineId);
            } catch (Exception e) {
                throw new ConnectorException(e.getMessage());
            }
        }

        private void fromCloudStackVMToMachine(final VirtualMachine vm, final Machine machine) throws ConnectorException {
            machine.setProviderAssignedId(vm.getId());
            machine.setState(this.fromVMStateToMachineState(vm.getState()));

            List<MachineNetworkInterface> nics = new ArrayList<MachineNetworkInterface>();
            machine.setNetworkInterfaces(nics);

            for (NIC vmNic : vm.getNICs()) {
                MachineNetworkInterface nic = new MachineNetworkInterface();
                List<MachineNetworkInterfaceAddress> cimiAddresses = new ArrayList<MachineNetworkInterfaceAddress>();

                Network cimiNetwork = new Network();
                this.fromCloudStackNetworkToCimiNetwork(vmNic.getNetworkId(), cimiNetwork);
                nic.setNetwork(cimiNetwork);
                nic.setAddresses(cimiAddresses);
                nic.setState(MachineNetworkInterface.InterfaceState.ACTIVE);

                MachineNetworkInterfaceAddress entry = new MachineNetworkInterfaceAddress();
                org.ow2.sirocco.cloudmanager.model.cimi.Address cimiAddress = new org.ow2.sirocco.cloudmanager.model.cimi.Address();
                cimiAddress = new org.ow2.sirocco.cloudmanager.model.cimi.Address();
                cimiAddress.setIp(vmNic.getIPAddress());
                cimiAddress.setAllocation("dynamic");
                cimiAddress.setProtocol("IPv4");
                entry.setAddress(cimiAddress);
                cimiAddresses.add(entry);

                nics.add(nic);
            }

            machine.setCpu((int) vm.getCpuCount());
            machine.setMemory((int) vm.getMemory() * 1024);
            // TODO set disk size to template size
            List<MachineDisk> machineDisks = new ArrayList<MachineDisk>();
            MachineDisk machineDisk = new MachineDisk();
            machineDisk.setInitialLocation("");
            machineDisk.setCapacity(0);
            machineDisks.add(machineDisk);
            machine.setDisks(machineDisks);

            // TODO volumes

        }

        private org.ow2.sirocco.cloudmanager.model.cimi.Machine.State fromVMStateToMachineState(
            final org.jclouds.cloudstack.domain.VirtualMachine.State state) {
            switch (state) {
            case DESTROYED:
                return org.ow2.sirocco.cloudmanager.model.cimi.Machine.State.DELETED;
            case ERROR:
                return org.ow2.sirocco.cloudmanager.model.cimi.Machine.State.ERROR;
            case EXPUNGING:
                return org.ow2.sirocco.cloudmanager.model.cimi.Machine.State.UNKNOWN;
            case MIGRATING:
                return org.ow2.sirocco.cloudmanager.model.cimi.Machine.State.UNKNOWN;
            case RUNNING:
                return org.ow2.sirocco.cloudmanager.model.cimi.Machine.State.STARTED;
            case SHUTDOWNED:
                return org.ow2.sirocco.cloudmanager.model.cimi.Machine.State.STOPPED;
            case STARTING:
                return org.ow2.sirocco.cloudmanager.model.cimi.Machine.State.STARTING;
            case STOPPED:
                return org.ow2.sirocco.cloudmanager.model.cimi.Machine.State.STOPPED;
            case STOPPING:
                return org.ow2.sirocco.cloudmanager.model.cimi.Machine.State.STOPPING;
            case UNKNOWN:
                return org.ow2.sirocco.cloudmanager.model.cimi.Machine.State.UNKNOWN;
            case UNRECOGNIZED:
                return org.ow2.sirocco.cloudmanager.model.cimi.Machine.State.UNKNOWN;
            default:
                return org.ow2.sirocco.cloudmanager.model.cimi.Machine.State.UNKNOWN;
            }
        }

        public org.ow2.sirocco.cloudmanager.model.cimi.Machine.State getMachineState(final String machineId)
            throws ConnectorException {
            VirtualMachine vm = this.vmClient.getVirtualMachine(machineId);
            if (vm == null) {
                throw new ResourceNotFoundException("CloudStack VM with id " + machineId + " not found");
            }
            return this.fromVMStateToMachineState(vm.getState());
        }

        public Machine getMachine(final String machineId) throws ConnectorException {
            VirtualMachine vm = this.vmClient.getVirtualMachine(machineId);
            if (vm == null) {
                throw new ResourceNotFoundException("CloudStack VM with id " + machineId + " not found");
            }
            Machine machine = new Machine();
            this.fromCloudStackVMToMachine(vm, machine);
            return machine;
        }

        public void addVolumeToMachine(final String machineId, final MachineVolume machineVolume) throws ConnectorException {
        }

        public void removeVolumeFromMachine(final String machineId, final MachineVolume machineVolume) {
        }

        //
        // Volume Service
        //

        public Volume createVolume(final VolumeCreate volumeCreate) throws ConnectorException {
            // TODO
            throw new ConnectorException("unsupported operation");
        }

        public void deleteVolume(final String volumeId) throws ConnectorException {
        }

        public State getVolumeState(final String volumeId) throws ConnectorException {
            // TODO
            throw new ConnectorException("unsupported operation");
        }

        public Volume getVolume(final String volumeId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        public VolumeImage getVolumeImage(final String volumeImageId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        public void deleteVolumeImage(final String volumeImageId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        //
        // Image Service
        //

        private MachineImage fromTemplateToCimiMachineImage(final Template template) {
            MachineImage machineImage = new MachineImage();
            machineImage.setName(template.getName());
            machineImage.setState(this.fromTemplateStatusToCimiMachineImageState(template.getStatus()));
            machineImage.setType(Type.IMAGE);
            machineImage.setOsType(template.getOSType());
            if (template.getSize() != null) {
                machineImage.setCapacity(template.getSize().intValue());
            }
            ProviderMapping providerMapping = new ProviderMapping();
            providerMapping.setProviderAssignedId(template.getId());
            providerMapping.setProviderAccount(this.cloudProviderAccount);
            providerMapping.setProviderLocation(this.cloudProviderLocation);
            machineImage.setProviderMappings(Collections.singletonList(providerMapping));
            return machineImage;
        }

        public List<MachineImage> getMachineImages(final boolean returnAccountImagesOnly,
            final Map<String, String> searchCriteria) throws ConnectorException {
            List<MachineImage> result = new ArrayList<>();
            ListTemplatesOptions options = ListTemplatesOptions.Builder.zoneId(this.defaultZoneId);
            if (returnAccountImagesOnly) {
                options = options.filter(TemplateFilter.SELF_EXECUTABLE);
            } else {
                options = options.filter(TemplateFilter.EXECUTABLE);
            }
            for (Template template : this.templateClient.listTemplates(options)) {
                result.add(this.fromTemplateToCimiMachineImage(template));
            }
            return result;
        }

        private org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State fromTemplateStatusToCimiMachineImageState(
            final Status status) {
            if (status == null) {
                return org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State.AVAILABLE;
            }
            switch (status) {
            case ABANDONED:
                return org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State.ERROR;
            case DOWNLOADED:
                return org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State.AVAILABLE;
            case DOWNLOAD_ERROR:
                return org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State.ERROR;
            case DOWNLOAD_IN_PROGRESS:
                return org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State.CREATING;
            case NOT_DOWNLOADED:
                return org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State.UNKNOWN;
            case NOT_UPLOADED:
                return org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State.UNKNOWN;
            case UNKNOWN:
                return org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State.UNKNOWN;
            case UNRECOGNIZED:
                return org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State.UNKNOWN;
            case UPLOADED:
                return org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State.AVAILABLE;
            case UPLOAD_ERROR:
                return org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State.ERROR;
            case UPLOAD_IN_PROGRESS:
                return org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State.CREATING;
            default:
                return org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State.ERROR;
            }
        }

        public void deleteMachineImage(final String imageId) {
            // TODO Auto-generated method stub

        }

        public MachineImage getMachineImage(final String machineImageId) throws ConnectorException {
            Template template = this.templateClient.getTemplateInZone(machineImageId, this.defaultZoneId);
            if (template == null) {
                throw new ResourceNotFoundException("CloudStack template with id " + machineImageId + " not found");
            }
            return this.fromTemplateToCimiMachineImage(template);
        }

        //
        // Network Service
        //

        public void deleteNetwork(final String networkId) {
            // TODO Auto-generated method stub

        }

        private Network.State fromCloudStackNetworkStateToCimiNetworkState(final String state) {
            switch (state) {
            case "Allocated":
                return Network.State.CREATING;
            case "Setup":
                return Network.State.STARTED;
            case "Implementing":
                return Network.State.CREATING;
            case "Implemented":
                return Network.State.STARTED;
            case "Shutdown":
                return Network.State.DELETING;
            case "Destroy":
                return Network.State.DELETED;
            default:
                CloudStackCloudProviderConnector.logger.error("Unknown Network state: " + state);
                return Network.State.ERROR;
            }
        }

        private void fromCloudStackNetworkToCimiNetwork(final String networkId, final Network cimiNetwork)
            throws ConnectorException {
            org.jclouds.cloudstack.domain.Network cloudStackNetwork = this.networkClient.getNetwork(networkId);
            if (cloudStackNetwork == null) {
                throw new ResourceNotFoundException("CloudStack Network with id " + networkId + " does not exist");
            }
            this.fromCloudStackNetworkToCimiNetwork(cloudStackNetwork, cimiNetwork);
        }

        private void fromCloudStackNetworkToCimiNetwork(final org.jclouds.cloudstack.domain.Network cloudStackNetwork,
            final Network cimiNetwork) {
            cimiNetwork.setName(cloudStackNetwork.getName());
            cimiNetwork.setProviderAssignedId(cloudStackNetwork.getId());
            cimiNetwork.setState(this.fromCloudStackNetworkStateToCimiNetworkState(cloudStackNetwork.getState()));
            if (cloudStackNetwork.getTrafficType() == TrafficType.PUBLIC) {
                cimiNetwork.setNetworkType(Network.Type.PUBLIC);
            } else {
                cimiNetwork.setNetworkType(Network.Type.PRIVATE);
            }

            List<Subnet> subnets = new ArrayList<Subnet>();
            cimiNetwork.setSubnets(subnets);

            // TODO add subnet if network has valid startIP and endIP
        }

        public org.ow2.sirocco.cloudmanager.model.cimi.Network.State getNetworkState(final String networkId)
            throws ConnectorException {
            org.jclouds.cloudstack.domain.Network cloudStackNetwork = this.networkClient.getNetwork(networkId);
            if (cloudStackNetwork == null) {
                throw new ResourceNotFoundException("CloudStack Network with id " + networkId + " does not exist");
            }
            return this.fromCloudStackNetworkStateToCimiNetworkState(cloudStackNetwork.getState());
        }

        public Network getNetwork(final String networkId) throws ConnectorException {
            Network network = new Network();
            this.fromCloudStackNetworkToCimiNetwork(networkId, network);
            return network;
        }

        public Network createNetwork(final NetworkCreate networkCreate) {
            // TODO Auto-generated method stub
            return null;
        }

        public List<Network> getNetworks() {
            List<Network> networks = new ArrayList<Network>();
            for (org.jclouds.cloudstack.domain.Network net : this.networkClient.listNetworks()) {
                Network cimiNetwork = new Network();
                this.fromCloudStackNetworkToCimiNetwork(net, cimiNetwork);
                networks.add(cimiNetwork);
            }
            return networks;
        }
    }

    private static String getPublicKeyFingerprint(final String publicKey) {
        String key[] = publicKey.split(" ");
        if (key.length < 2) {
            throw new RuntimeException("Incorrect public key is passed in");
        }
        byte[] keyBytes = BaseEncoding.base64().decode(key[1]);

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String sumString = CloudStackCloudProviderConnector.toHexString(md5.digest(keyBytes));
        String rString = "";

        for (int i = 2; i <= sumString.length(); i += 2) {
            rString += sumString.substring(i - 2, i);
            if (i != sumString.length()) {
                rString += ":";
            }
        }

        return rString;
    }

    private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String toHexString(final byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (byte element : b) {
            sb.append(CloudStackCloudProviderConnector.hexChars[(element >> 4) & 0x0f]);
            sb.append(CloudStackCloudProviderConnector.hexChars[(element) & 0x0f]);
        }
        return sb.toString();
    }

}
