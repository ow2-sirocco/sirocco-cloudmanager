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

package org.ow2.sirocco.cloudmanager.connector.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.ow2.sirocco.cloudmanager.connector.api.BadStateException;
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
import org.ow2.sirocco.cloudmanager.model.cimi.CloudCollectionItem;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.Type;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface.InterfaceState;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkNetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Resource;
import org.ow2.sirocco.cloudmanager.model.cimi.Subnet;
import org.ow2.sirocco.cloudmanager.model.cimi.SubnetConfig;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.ProviderMapping;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupRule;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Visibility;
import org.ow2.sirocco.cloudmanager.model.cimi.system.ComponentDescriptor;
import org.ow2.sirocco.cloudmanager.model.cimi.system.ComponentDescriptor.ComponentType;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCredentials;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemMachine;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemSystem;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemVolume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class MockCloudProviderConnector implements ICloudProviderConnector, IComputeService, ISystemService, IVolumeService,
    INetworkService, IImageService {

    private static Logger logger = LoggerFactory.getLogger(MockCloudProviderConnector.class);

    private static final int ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS = 1;

    private List<MockProvider> mockProviders = new ArrayList<MockProvider>();

    private synchronized MockProvider getProvider(final ProviderTarget target) {
        for (MockProvider provider : this.mockProviders) {
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

        MockProvider provider = new MockProvider();
        provider.cloudProviderAccount = target.getAccount();
        provider.cloudProviderLocation = target.getLocation();
        this.mockProviders.add(provider);
        return provider;
    }

    @Override
    public Set<CloudProviderLocation> getLocations() {
        CloudProviderLocation mockLocation = new CloudProviderLocation();
        mockLocation.setCountryName("France");
        return Collections.singleton(mockLocation);
    }

    @Override
    public IComputeService getComputeService() throws ConnectorException {
        return this;
    }

    @Override
    public ISystemService getSystemService() throws ConnectorException {
        return this;
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
        this.getProvider(target).startNetwork(networkId);
    }

    @Override
    public void stopNetwork(final String networkId, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).stopNetwork(networkId);
    }

    @Override
    public NetworkPort createNetworkPort(final NetworkPortCreate networkPortCreate, final ProviderTarget target)
        throws ConnectorException {
        return this.getProvider(target).createNetworkPort(networkPortCreate);
    }

    @Override
    public NetworkPort getNetworkPort(final String networkPortId, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getNetworkPort(networkPortId);
    }

    @Override
    public void deleteNetworkPort(final String networkPortId, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).deleteNetworkPort(networkPortId);
    }

    @Override
    public void startNetworkPort(final String networkPortId, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).startNetworkPort(networkPortId);
    }

    @Override
    public void stopNetworkPort(final String networkPortId, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).stopNetworkPort(networkPortId);
    }

    @Override
    public ForwardingGroup createForwardingGroup(final ForwardingGroupCreate forwardingGroupCreate, final ProviderTarget target)
        throws ConnectorException {
        return this.getProvider(target).createForwardingGroup(forwardingGroupCreate);
    }

    @Override
    public ForwardingGroup getForwardingGroup(final String forwardingGroupId, final ProviderTarget target)
        throws ConnectorException {
        return this.getProvider(target).getForwardingGroup(forwardingGroupId);
    }

    @Override
    public void deleteForwardingGroup(final ForwardingGroup forwardingGroup, final ProviderTarget target)
        throws ConnectorException {
        this.getProvider(target).deleteForwardingGroup(forwardingGroup);
    }

    @Override
    public void addNetworkToForwardingGroup(final String forwardingGroupId, final ForwardingGroupNetwork fgNetwork,
        final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).addNetworkToForwardingGroup(forwardingGroupId, fgNetwork);
    }

    @Override
    public void removeNetworkFromForwardingGroup(final String forwardingGroupId, final String networkId,
        final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).removeNetworkFromForwardingGroup(forwardingGroupId, networkId);
    }

    @Override
    public String createSecurityGroup(final SecurityGroupCreate create, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).createSecurityGroup(create);
    }

    @Override
    public SecurityGroup getSecurityGroup(final String groupId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        return this.getProvider(target).getSecurityGroup(groupId);
    }

    @Override
    public List<SecurityGroup> getSecurityGroups(final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getSecurityGroups();
    }

    @Override
    public void deleteRuleFromSecurityGroup(final String groupId, final SecurityGroupRule rule, final ProviderTarget target)
        throws ConnectorException {
        this.getProvider(target).deleteRuleFromSecurityGroup(groupId, rule);
    }

    @Override
    public String addRuleToSecurityGroup(final String groupId, final SecurityGroupRule rule, final ProviderTarget target)
        throws ConnectorException {
        return this.getProvider(target).addRuleToSecurityGroup(groupId, rule);
    }

    @Override
    public void deleteSecurityGroup(final String groupId, final ProviderTarget target) throws ResourceNotFoundException,
        ConnectorException {
        this.getProvider(target).deleteSecurityGroup(groupId);
    }

    @Override
    public List<Address> getAddresses(final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getAddresses();
    }

    @Override
    public Address allocateAddress(final Map<String, String> properties, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).allocateAddress(properties);
    }

    @Override
    public void deallocateAddress(final Address address, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).deleteAddress(address);
    }

    @Override
    public void addAddressToMachine(final String machineId, final Address address, final ProviderTarget target)
        throws ConnectorException {
        this.getProvider(target).addAddressToMachine(machineId, address);
    }

    @Override
    public void removeAddressFromMachine(final String machineId, final Address address, final ProviderTarget target)
        throws ConnectorException {
        this.getProvider(target).removeAddressFromMachine(machineId, address);
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
        return this.getProvider(target).createVolumeImage(volumeImage);
    }

    @Override
    public VolumeImage createVolumeSnapshot(final String volumeId, final VolumeImage volumeImage, final ProviderTarget target)
        throws ConnectorException {
        return this.getProvider(target).createVolumeSnapshot(volumeId, volumeImage);
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
    public System createSystem(final SystemCreate systemCreate, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).createSystem(systemCreate);
    }

    @Override
    public void deleteSystem(final String systemId, final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).deleteSystem(systemId);
    }

    @Override
    public void startSystem(final String systemId, final Map<String, String> properties, final ProviderTarget target)
        throws ConnectorException {
        this.getProvider(target).startSystem(systemId, properties);
    }

    @Override
    public void stopSystem(final String systemId, final boolean force, final Map<String, String> properties,
        final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).stopSystem(systemId, force, properties);
    }

    @Override
    public void restartSystem(final String systemId, final boolean force, final Map<String, String> properties,
        final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).restartSystem(systemId, force, properties);
    }

    @Override
    public void pauseSystem(final String systemId, final Map<String, String> properties, final ProviderTarget target)
        throws ConnectorException {
        this.getProvider(target).pauseSystem(systemId, properties);
    }

    @Override
    public void suspendSystem(final String systemId, final Map<String, String> properties, final ProviderTarget target)
        throws ConnectorException {
        this.getProvider(target).suspendSystem(systemId, properties);
    }

    @Override
    public System getSystem(final String systemId, final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getSystem(systemId);
    }

    @Override
    public org.ow2.sirocco.cloudmanager.model.cimi.system.System.State getSystemState(final String systemId,
        final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getSystemState(systemId);
    }

    @Override
    public List<? extends CloudCollectionItem> getEntityListFromSystem(final String systemId, final String entityType,
        final ProviderTarget target) throws ConnectorException {
        return this.getProvider(target).getEntityListFromSystem(systemId, entityType);
    }

    @Override
    public void deleteEntityInSystem(final String systemId, final String entityId, final String entityType,
        final ProviderTarget target) throws ConnectorException {
        this.getProvider(target).deleteEntityInSystem(systemId, entityId, entityType);
    }

    @Override
    public void removeEntityFromSystem(final String systemId, final String entityId, final ProviderTarget target)
        throws ConnectorException {
        this.getProvider(target).removeEntityFromSystem(systemId, entityId);
    }

    @Override
    public void addEntityToSystem(final String systemId, final String entityId, final ProviderTarget target)
        throws ConnectorException {
        this.getProvider(target).addEntityToSystem(systemId, entityId);
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
    public State getMachineState(final String machineId, final ProviderTarget target) throws ConnectorException {
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
    public List<MachineImage> getMachineImages(final boolean returnPublicImages, final Map<String, String> searchCriteria,
        final ProviderTarget target) {
        return this.getProvider(target).getMachineImages(returnPublicImages, searchCriteria);
    }

    @Override
    public List<MachineConfiguration> getMachineConfigs(final ProviderTarget provider) {
        return this.getProvider(provider).getMachineConfigs();
    }

    private static class MockProvider {
        private final static String ADDRESS_PREFIX = "81.200.35.";

        private final static int ADDRESS_NUMBER = 20;

        private CloudProviderAccount cloudProviderAccount;

        private CloudProviderLocation cloudProviderLocation;

        private Map<String, Volume> volumes = new ConcurrentHashMap<String, Volume>();

        private Map<String, VolumeImage> volumeImages = new ConcurrentHashMap<String, VolumeImage>();

        private Map<String, Machine> machines = new ConcurrentHashMap<String, Machine>();

        private Map<String, MachineImage> machineImages = new ConcurrentHashMap<String, MachineImage>();

        private Map<String, System> systems = new ConcurrentHashMap<String, System>();

        private Map<String, Network> networks = new ConcurrentHashMap<String, Network>();

        private Map<String, NetworkPort> networkPorts = new ConcurrentHashMap<String, NetworkPort>();

        private Map<String, ForwardingGroup> forwardingGroups = new ConcurrentHashMap<String, ForwardingGroup>();

        private Map<String, SecurityGroup> securityGroups = new ConcurrentHashMap<String, SecurityGroup>();

        private Address addressPool[];

        private Map<String, Address> allocatedAddresses = new ConcurrentHashMap<String, Address>();

        private Random random = new Random();

        MockProvider() {
            Network publicNetwork = new Network();
            publicNetwork.setNetworkType(Network.Type.PUBLIC);
            publicNetwork.setName("Mock public network");
            publicNetwork.setState(Network.State.STARTED);
            publicNetwork.setProviderAssignedId("publicNetwork" + UUID.randomUUID().toString());
            publicNetwork.setCloudProviderAccount(this.cloudProviderAccount);
            publicNetwork.setLocation(this.cloudProviderLocation);
            Subnet subnet = new Subnet();
            subnet.setCidr("192.168.200.0/24");
            subnet.setProviderAssignedId(UUID.randomUUID().toString());
            subnet.setProtocol("IPv4");
            subnet.setState(Subnet.State.AVAILABLE);
            publicNetwork.setSubnets(Collections.singletonList(subnet));
            this.networks.put(publicNetwork.getProviderAssignedId(), publicNetwork);
            this.addressPool = new Address[MockProvider.ADDRESS_NUMBER];
            for (int address_suffix = 1; address_suffix <= MockProvider.ADDRESS_NUMBER; address_suffix++) {
                Address address = new Address();
                address.setProviderAssignedId(UUID.randomUUID().toString());
                address.setIp(MockProvider.ADDRESS_PREFIX + address_suffix);
                address.setState(Address.State.DELETED);
                this.addressPool[address_suffix - 1] = address;
            }
        }

        private boolean actionDone(final Resource resource) {
            if (resource.getUpdated() == null) {
                return false;
            }
            Date now = new Date();
            return (now.getTime() - resource.getUpdated().getTime()) > MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS;
        }

        public synchronized Volume createVolume(final VolumeCreate volumeCreate) throws ConnectorException {
            final String volumeProviderAssignedId = UUID.randomUUID().toString();
            final Volume volume = new Volume();
            volume.setName(volumeCreate.getName());
            volume.setProviderAssignedId(volumeProviderAssignedId);
            volume.setCapacity(volumeCreate.getVolumeTemplate().getVolumeConfig().getCapacity());
            this.volumes.put(volumeProviderAssignedId, volume);
            volume.setState(Volume.State.CREATING);
            volume.setUpdated(new Date());
            MockCloudProviderConnector.logger.info("Created Volume with id " + volume.getProviderAssignedId());
            return volume;
        }

        public synchronized void deleteVolume(final String volumeId) throws ConnectorException {
            Volume volume = this.volumes.get(volumeId);
            if (volume == null) {
                throw new ResourceNotFoundException("Volume " + volumeId + " doesn't exist");
            }
            volume.setState(Volume.State.DELETING);
            volume.setUpdated(new Date());
        }

        public synchronized org.ow2.sirocco.cloudmanager.model.cimi.Volume.State getVolumeState(final String volumeId)
            throws ConnectorException {
            return this.getVolume(volumeId).getState();
        }

        public synchronized Volume getVolume(final String volumeId) throws ConnectorException {
            Volume volume = this.volumes.get(volumeId);
            if (volume == null) {
                MockCloudProviderConnector.logger.info("Volume with id " + volumeId + " not found");
                throw new ResourceNotFoundException("Volume " + volumeId + " does not exist");
            }
            if (this.actionDone(volume)) {
                if (volume.getState() == Volume.State.CREATING) {
                    volume.setState(Volume.State.AVAILABLE);
                    volume.setUpdated(new Date());
                } else if (volume.getState() == Volume.State.DELETING) {
                    this.volumes.remove(volume.getProviderAssignedId());
                    volume.setState(Volume.State.DELETED);
                    volume.setUpdated(new Date());
                }
            }
            return volume;
        }

        public synchronized Machine createMachine(final MachineCreate machineCreate) throws ConnectorException {
            final String machineProviderAssignedId = UUID.randomUUID().toString();
            final Machine machine = new Machine();
            machine.setProviderAssignedId(machineProviderAssignedId);
            this.machines.put(machineProviderAssignedId, machine);
            MockCloudProviderConnector.logger.info("Creating machine with providerAssignedId " + machineProviderAssignedId);
            machine.setName(machineCreate.getName());
            machine.setDescription(machineCreate.getDescription());
            if (machineCreate.getProperties() != null) {
                machine.setProperties(new HashMap<String, String>(machineCreate.getProperties()));
            }
            machine.setState(Machine.State.CREATING);
            machine.setCpu(machineCreate.getMachineTemplate().getMachineConfig().getCpu());
            machine.setMemory(machineCreate.getMachineTemplate().getMachineConfig().getMemory());
            List<MachineDisk> disks = new ArrayList<MachineDisk>();
            if (machineCreate.getMachineTemplate().getMachineConfig().getDisks() != null) {
                for (DiskTemplate diskTemplate : machineCreate.getMachineTemplate().getMachineConfig().getDisks()) {
                    MachineDisk mdisk = new MachineDisk();
                    mdisk.setCapacity(diskTemplate.getCapacity());
                    mdisk.setInitialLocation(diskTemplate.getInitialLocation());
                    disks.add(mdisk);
                }
            }

            machine.setDisks(disks);

            if (machineCreate.getMachineTemplate().getNetworkInterfaces() != null) {
                for (MachineTemplateNetworkInterface networkInterface : machineCreate.getMachineTemplate()
                    .getNetworkInterfaces()) {
                    MachineNetworkInterface nic = new MachineNetworkInterface();
                    List<MachineNetworkInterfaceAddress> addrs = new ArrayList<MachineNetworkInterfaceAddress>();
                    MachineNetworkInterfaceAddress entry = new MachineNetworkInterfaceAddress();
                    Address ip = new Address();
                    ip.setIp("192.168.200." + this.random.nextInt(253) + 2);
                    ip.setAllocation("dynamic");
                    ip.setProtocol("IPv4");
                    ip.setNetwork(networkInterface.getNetwork());
                    entry.setAddress(ip);
                    addrs.add(entry);
                    nic.setAddresses(addrs);

                    nic.setState(InterfaceState.ACTIVE);
                    nic.setNetwork(networkInterface.getNetwork());
                    nic.setNetworkType(networkInterface.getNetwork().getNetworkType());
                    machine.addNetworkInterface(nic);
                }
            } else {
                machine.setNetworkInterfaces(Lists.<MachineNetworkInterface> newArrayList());
            }

            // TODO create and attach volumes

            machine.setVolumes(new ArrayList<MachineVolume>());
            machine.setUpdated(new Date());
            return machine;
        }

        public synchronized void startMachine(final String machineId) throws ConnectorException {
            MockCloudProviderConnector.logger.info("Starting machine with providerAssignedId " + machineId);
            final Machine machine = this.machines.get(machineId);
            if (machine == null) {
                throw new ResourceNotFoundException("Machine " + machineId + " doesn't exist");
            }
            if (machine.getState() == State.CREATING || machine.getState() == State.STARTED
                || machine.getState() == State.DELETING) {
                throw new BadStateException("Illegal operation");
            }
            machine.setState(State.STARTING);
            machine.setUpdated(new Date());
        }

        public synchronized void stopMachine(final String machineId, final boolean force) throws ConnectorException {
            MockCloudProviderConnector.logger.info("Stopping machine with providerAssignedId " + machineId);
            final Machine machine = this.machines.get(machineId);
            if (machine == null) {
                throw new ResourceNotFoundException("Machine " + machineId + " doesn't exist");
            }
            if (machine.getState() == State.CREATING || machine.getState() == State.STOPPED
                || machine.getState() == State.PAUSING || machine.getState() == State.PAUSED
                || machine.getState() == State.SUSPENDING || machine.getState() == State.SUSPENDED
                || machine.getState() == State.DELETING) {
                throw new BadStateException("Illegal operation");
            }
            machine.setState(State.STOPPING);
            machine.setUpdated(new Date());
        }

        public synchronized void suspendMachine(final String machineId) throws ConnectorException {
            final Machine machine = this.machines.get(machineId);
            if (machine == null) {
                throw new ResourceNotFoundException("Machine " + machineId + " doesn't exist");
            }
            if (machine.getState() == State.CREATING || machine.getState() == State.STARTING
                || machine.getState() == State.STOPPING || machine.getState() == State.STOPPED
                || machine.getState() == State.PAUSING || machine.getState() == State.PAUSED
                || machine.getState() == State.SUSPENDING || machine.getState() == State.SUSPENDED
                || machine.getState() == State.DELETING) {
                throw new BadStateException("Illegal operation");
            }
            machine.setState(State.SUSPENDING);
            machine.setUpdated(new Date());
        }

        public synchronized void restartMachine(final String machineId, final boolean force) throws ConnectorException {
            final Machine machine = this.machines.get(machineId);
            if (machine == null) {
                throw new ResourceNotFoundException("Machine " + machineId + " doesn't exist");
            }
        }

        public synchronized void pauseMachine(final String machineId) throws ConnectorException {
            final Machine machine = this.machines.get(machineId);
            if (machine == null) {
                throw new ResourceNotFoundException("Machine " + machineId + " doesn't exist");
            }
            if (machine.getState() == State.CREATING || machine.getState() == State.STARTING
                || machine.getState() == State.STOPPING || machine.getState() == State.STOPPED
                || machine.getState() == State.PAUSING || machine.getState() == State.PAUSED
                || machine.getState() == State.SUSPENDING || machine.getState() == State.SUSPENDED
                || machine.getState() == State.DELETING) {
                throw new BadStateException("Illegal operation");
            }
            machine.setState(State.PAUSING);
            machine.setUpdated(new Date());
        }

        public MachineImage captureMachine(final String machineId, final MachineImage machineImage) throws ConnectorException {
            final Machine machine = this.machines.get(machineId);
            if (machine == null) {
                throw new ResourceNotFoundException("Machine " + machineId + " doesn't exist");
            }
            final MachineImage capturedMachineImage = new MachineImage();
            capturedMachineImage.setProviderAssignedId(UUID.randomUUID().toString());
            capturedMachineImage.setName(machineImage.getName());
            capturedMachineImage.setDescription(machineImage.getDescription());
            capturedMachineImage.setType(Type.IMAGE);
            capturedMachineImage.setState(MachineImage.State.CREATING);
            Map<String, String> props = new HashMap<String, String>();
            props.put("mock", "1234");
            capturedMachineImage.setProperties(props);
            capturedMachineImage.setUpdated(new Date());
            this.machineImages.put(capturedMachineImage.getProviderAssignedId(), capturedMachineImage);
            return capturedMachineImage;
        }

        public MachineImage getMachineImage(final String machineImageId) throws ConnectorException {
            MachineImage machineImage = this.machineImages.get(machineImageId);
            if (machineImage == null) {
                throw new ResourceNotFoundException("MachineImage " + machineImageId + " does not exist");
            }
            if (this.actionDone(machineImage)) {
                if (machineImage.getState() == MachineImage.State.CREATING) {
                    machineImage.setState(MachineImage.State.AVAILABLE);
                    machineImage.setUpdated(new Date());
                } else if (machineImage.getState() == MachineImage.State.DELETING) {
                    machineImage.setState(MachineImage.State.DELETED);
                    machineImage.setUpdated(new Date());
                    this.machineImages.remove(machineImage.getProviderAssignedId());
                }
            }
            return machineImage;
        }

        public List<MachineImage> getMachineImages(final boolean returnPublicImages, final Map<String, String> searchCriteria) {
            List<MachineImage> result = new ArrayList<MachineImage>(this.machineImages.values());
            MachineImage mockPublicMachineImage = new MachineImage();
            mockPublicMachineImage.setName("Mock Image");
            mockPublicMachineImage.setDescription("Mock image");
            mockPublicMachineImage.setVisibility(Visibility.PUBLIC);
            ProviderMapping providerMapping = new ProviderMapping();
            providerMapping.setProviderAssignedId("MockMachineImage");
            providerMapping.setProviderAccount(this.cloudProviderAccount);
            providerMapping.setProviderLocation(this.cloudProviderLocation);
            mockPublicMachineImage.setProviderMappings(Collections.singletonList(providerMapping));
            result.add(mockPublicMachineImage);
            return result;
        }

        public void deleteMachineImage(final String machineImageId) throws ConnectorException {
            MachineImage machineImage = this.machineImages.get(machineImageId);
            if (machineImage == null) {
                throw new ResourceNotFoundException("MachineImage " + machineImageId + " does not exist");
            }
            machineImage.setState(MachineImage.State.DELETING);
            machineImage.setUpdated(new Date());
        }

        public synchronized void deleteMachine(final String machineId) throws ConnectorException {
            MockCloudProviderConnector.logger.info("Deleting machine with providerAssignedId " + machineId);
            Machine machine = this.machines.get(machineId);
            if (machine == null) {
                throw new ResourceNotFoundException("Machine " + machineId + " doesn't exist");
            }
            machine.setState(Machine.State.DELETING);
            machine.setUpdated(new Date());
        }

        public synchronized State getMachineState(final String machineId) throws ConnectorException {
            return this.getMachine(machineId).getState();
        }

        public synchronized Machine getMachine(final String machineId) throws ConnectorException {
            Machine machine = this.machines.get(machineId);
            if (machine == null) {
                throw new ResourceNotFoundException("Machine " + machineId + " does not exist");
            }
            if (this.actionDone(machine)) {
                if (machine.getState() == Machine.State.CREATING) {
                    if (machine.getNetworkInterfaces() != null) {
                        for (MachineNetworkInterface networkInterface : machine.getNetworkInterfaces()) {
                            networkInterface.setState(InterfaceState.ACTIVE);
                        }
                    }
                    machine.setState(Machine.State.STARTED);
                    machine.setUpdated(new Date());
                } else if (machine.getState() == Machine.State.DELETING) {
                    this.machines.remove(machine.getProviderAssignedId());
                    machine.setState(Machine.State.DELETED);
                    machine.setUpdated(new Date());
                } else if (machine.getState() == Machine.State.PAUSING) {
                    machine.setState(Machine.State.PAUSED);
                    machine.setUpdated(new Date());
                } else if (machine.getState() == Machine.State.STARTING) {
                    machine.setState(Machine.State.STARTED);
                    machine.setUpdated(new Date());
                } else if (machine.getState() == Machine.State.STOPPING) {
                    machine.setState(Machine.State.STOPPED);
                    machine.setUpdated(new Date());
                } else if (machine.getState() == Machine.State.SUSPENDING) {
                    machine.setState(Machine.State.SUSPENDED);
                    machine.setUpdated(new Date());
                }
            }
            Iterator<MachineVolume> it = machine.getVolumes().iterator();
            while (it.hasNext()) {
                MachineVolume mv = it.next();
                if (this.actionDone(mv)) {
                    if (mv.getState() == MachineVolume.State.ATTACHING) {
                        mv.setState(MachineVolume.State.ATTACHED);
                        mv.setUpdated(new Date());
                    } else if (mv.getState() == MachineVolume.State.DETACHING) {
                        it.remove();
                    }
                }
            }
            return machine;
        }

        public List<MachineConfiguration> getMachineConfigs() {
            MachineConfiguration mockMachineConfiguration = new MachineConfiguration();
            mockMachineConfiguration.setCpu(1);
            mockMachineConfiguration.setMemory(1024);
            DiskTemplate disk = new DiskTemplate();
            disk.setCapacity(1000 * 1000 * 2);
            mockMachineConfiguration.setDisks(Collections.singletonList(disk));
            mockMachineConfiguration.setName("MockMachineConfig");
            ProviderMapping providerMapping = new ProviderMapping();
            providerMapping.setProviderAssignedId("MockMachineConfig");
            providerMapping.setProviderAccount(this.cloudProviderAccount);
            providerMapping.setProviderLocation(this.cloudProviderLocation);
            mockMachineConfiguration.setProviderMappings(Collections.singletonList(providerMapping));
            return Collections.singletonList(mockMachineConfiguration);
        }

        public synchronized System.State getSystemState(final String systemId) throws ConnectorException {
            return this.getSystem(systemId).getState();
        }

        public synchronized System getSystem(final String systemId) throws ConnectorException {
            System system = this.systems.get(systemId);
            if (system == null) {
                throw new ResourceNotFoundException("System " + systemId + " does not exist");
            }
            if (this.actionDone(system)) {
                if (system.getState() == System.State.CREATING) {
                    system.setState(System.State.STARTED);
                    system.setUpdated(new Date());
                } else if (system.getState() == System.State.DELETING) {
                    this.systems.remove(system.getProviderAssignedId());
                    system.setState(System.State.DELETED);
                    system.setUpdated(new Date());
                } else if (system.getState() == System.State.PAUSING) {
                    system.setState(System.State.PAUSED);
                    system.setUpdated(new Date());
                } else if (system.getState() == System.State.STARTING) {
                    system.setState(System.State.STARTED);
                    system.setUpdated(new Date());
                } else if (system.getState() == System.State.STOPPING) {
                    system.setState(System.State.STOPPED);
                    system.setUpdated(new Date());
                } else if (system.getState() == System.State.SUSPENDING) {
                    system.setState(System.State.SUSPENDED);
                    system.setUpdated(new Date());
                }
            }
            return system;
        }

        public synchronized List<? extends CloudCollectionItem> getEntityListFromSystem(final String systemId,
            final String entityType) throws ConnectorException {
            System system = this.systems.get(systemId);
            if (system == null) {
                throw new ResourceNotFoundException("System " + systemId + " does not exist");
            }
            if (entityType.equals(SystemMachine.class.getName())) {
                return new ArrayList<SystemMachine>(system.getMachines());
            } else if (entityType.equals(SystemVolume.class.getName())) {
                return new ArrayList<SystemVolume>(system.getVolumes());
            } else if (entityType.equals(SystemSystem.class.getName())) {
                return new ArrayList<SystemSystem>(system.getSystems());
            } else if (entityType.equals(SystemNetwork.class.getName())) {
                return new ArrayList<SystemNetwork>(system.getNetworks());
            } else if (entityType.equals(SystemCredentials.class.getName())) {
                return new ArrayList<SystemCredentials>(system.getCredentials());
            } else {
                throw new ConnectorException("object type not owned by a system");
            }
        }

        private Machine.State waitForMachineState(final String machineId, final Machine.State... expectedStates)
            throws ConnectorException {
            int tries = 2 + MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS / 10;
            while (tries-- > 0) {
                Machine.State machineState = this.getMachineState(machineId);
                for (Machine.State expectedFinalState : expectedStates) {
                    if (machineState == expectedFinalState) {
                        return machineState;
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
            return null;
        }

        private Volume.State waitForVolumeState(final String volumeId, final Volume.State... expectedStates)
            throws ConnectorException {
            int tries = 2 + MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS / 10;
            while (tries-- > 0) {
                Volume.State volumeState = this.getVolumeState(volumeId);
                for (Volume.State expectedFinalState : expectedStates) {
                    if (volumeState == expectedFinalState) {
                        return volumeState;
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
            return null;
        }

        private Network.State waitForNetworkState(final String networkId, final Network.State... expectedStates)
            throws ConnectorException {
            int tries = 2 + MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS / 10;
            while (tries-- > 0) {
                Network.State networkState = this.getNetworkState(networkId);
                for (Network.State expectedFinalState : expectedStates) {
                    if (networkState == expectedFinalState) {
                        return networkState;
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
            return null;
        }

        private System.State waitForSystemState(final String systemId, final System.State... expectedStates)
            throws ConnectorException {
            int tries = 2 + MockCloudProviderConnector.ENTITY_LIFECYCLE_OPERATION_TIME_IN_MILLISECONDS / 10;
            while (tries-- > 0) {
                System.State systemState = this.getSystemState(systemId);
                for (System.State expectedFinalState : expectedStates) {
                    if (systemState == expectedFinalState) {
                        return systemState;
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
            return null;
        }

        public System createSystem(final SystemCreate systemCreate) throws ConnectorException {

            // assign a random provider id
            final String systemProviderAssignedId = UUID.randomUUID().toString();
            final System system = new System();
            system.setProviderAssignedId(systemProviderAssignedId);

            system.setState(System.State.CREATING);
            MockCloudProviderConnector.logger.info("Creating system with providerAssignedId " + systemProviderAssignedId);

            // attributes
            system.setDescription(systemCreate.getDescription());
            system.setName(systemCreate.getName());
            system.setMachines(new ArrayList<SystemMachine>());
            system.setVolumes(new ArrayList<SystemVolume>());
            system.setSystems(new ArrayList<SystemSystem>());
            system.setNetworks(new ArrayList<SystemNetwork>());
            system.setCredentials(new ArrayList<SystemCredentials>());
            system.setProperties(new HashMap<String, String>());

            Set<ComponentDescriptor> componentDescriptors = systemCreate.getSystemTemplate().getComponentDescriptors();

            // iterating through descriptors

            boolean failedCancelled = false;

            // create Volumes and Networks first
            Iterator<ComponentDescriptor> iter = componentDescriptors.iterator();
            while (iter.hasNext()) {
                ComponentDescriptor cd = iter.next();

                if (cd.getComponentType() == ComponentType.VOLUME) {
                    // creating new volumes
                    for (int i = 0; i < cd.getComponentQuantity(); i++) {
                        VolumeCreate vc = new VolumeCreate();
                        if (cd.getComponentQuantity() > 1) {
                            String name = cd.getName() == null ? "" : cd.getName();
                            vc.setName(name + new Integer(i + 1).toString());
                        } else {
                            vc.setName(cd.getName());
                        }
                        VolumeTemplate vt = (VolumeTemplate) cd.getComponentTemplate();
                        vc.setVolumeTemplate(vt);
                        vc.setDescription(cd.getDescription());
                        vc.setProperties(cd.getProperties());

                        Volume vol = this.createVolume(vc);
                        this.waitForVolumeState(vol.getProviderAssignedId(), Volume.State.AVAILABLE);
                        SystemVolume sv = new SystemVolume();
                        sv.setState(SystemVolume.State.AVAILABLE);
                        sv.setResource(vol);
                        system.getVolumes().add(sv);
                    }
                }

                if (cd.getComponentType() == ComponentType.NETWORK) {
                    // creating new networks
                    for (int i = 0; i < cd.getComponentQuantity(); i++) {
                        NetworkCreate nc = new NetworkCreate();
                        if (cd.getComponentQuantity() > 1) {
                            String name = cd.getName() == null ? "" : cd.getName();
                            nc.setName(name + new Integer(i + 1).toString());
                        } else {
                            nc.setName(cd.getName());
                        }
                        NetworkTemplate nt = (NetworkTemplate) cd.getComponentTemplate();
                        nc.setNetworkTemplate(nt);
                        nc.setDescription(cd.getDescription());
                        nc.setProperties(cd.getProperties());

                        Network net = this.createNetwork(nc);
                        this.waitForNetworkState(net.getProviderAssignedId(), Network.State.STARTED, Network.State.STOPPED);
                        net.setState(Network.State.STARTED);
                        SystemNetwork sn = new SystemNetwork();
                        sn.setState(SystemNetwork.State.AVAILABLE);
                        sn.setResource(net);
                        system.getNetworks().add(sn);
                    }
                }
            }

            // resolve volume component references if any
            for (ComponentDescriptor component : systemCreate.getSystemTemplate().getComponentDescriptors()) {
                if (component.getComponentType() == ComponentType.MACHINE) {
                    MachineTemplate machineTemplate = (MachineTemplate) component.getComponentTemplate();
                    if (machineTemplate.getVolumes() != null) {
                        for (MachineVolume machineVolume : machineTemplate.getVolumes()) {
                            if (machineVolume.getSystemVolumeName() != null) {
                                MockCloudProviderConnector.logger.info("Resolving volume ref #"
                                    + machineVolume.getSystemVolumeName());
                                for (SystemVolume sysVolume : system.getVolumes()) {
                                    Volume vol = sysVolume.getVolume();
                                    if (vol.getName() != null && vol.getName().equals(machineVolume.getSystemVolumeName())) {
                                        machineVolume.setVolume(vol);
                                        MockCloudProviderConnector.logger.info("Volume ref #"
                                            + machineVolume.getSystemVolumeName() + " resolved");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // resolve network component references if any
            for (ComponentDescriptor component : systemCreate.getSystemTemplate().getComponentDescriptors()) {
                if (component.getComponentType() == ComponentType.MACHINE) {
                    MachineTemplate machineTemplate = (MachineTemplate) component.getComponentTemplate();
                    for (MachineTemplateNetworkInterface nic : machineTemplate.getNetworkInterfaces()) {
                        if (nic.getSystemNetworkName() != null) {
                            MockCloudProviderConnector.logger.info("Resolving network ref #" + nic.getSystemNetworkName());
                            for (SystemNetwork sysNetwork : system.getNetworks()) {
                                Network net = sysNetwork.getNetwork();
                                if (net.getName() != null && net.getName().equals(nic.getSystemNetworkName())) {
                                    nic.setNetwork(net);
                                    MockCloudProviderConnector.logger.info("Network ref #" + nic.getSystemNetworkName()
                                        + " resolved");
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // create Machines and Systems
            iter = componentDescriptors.iterator();
            while (iter.hasNext()) {
                ComponentDescriptor cd = iter.next();

                if (cd.getComponentType() == ComponentType.MACHINE) {
                    // creating new machines
                    for (int i = 0; i < cd.getComponentQuantity(); i++) {
                        MachineCreate mc = new MachineCreate();
                        if (cd.getComponentQuantity() > 1) {
                            String name = cd.getName() == null ? "" : cd.getName();
                            mc.setName(name + new Integer(i + 1).toString());
                        } else {
                            mc.setName(cd.getName());
                        }

                        MachineTemplate mt = (MachineTemplate) cd.getComponentTemplate();
                        mc.setMachineTemplate(mt);
                        mc.setDescription(cd.getDescription());
                        mc.setProperties(cd.getProperties());

                        Machine machine = this.createMachine(mc);
                        this.waitForMachineState(machine.getProviderAssignedId(), Machine.State.STARTED, Machine.State.STOPPED);
                        machine.setState(Machine.State.STARTED);
                        // attach volumes
                        if (mt.getVolumes() != null) {
                            for (MachineVolume machineVolume : mt.getVolumes()) {
                                if (machineVolume.getVolume() == null) {
                                    MockCloudProviderConnector.logger.error("Unresolved MachineVolume ref="
                                        + machineVolume.getSystemVolumeName());
                                    continue;
                                }
                                MachineVolume machineVolume2 = new MachineVolume();
                                machineVolume2.setInitialLocation(machineVolume.getInitialLocation());
                                machineVolume2.setVolume(machineVolume.getVolume());
                                machine.getVolumes().add(machineVolume2);
                            }
                        }
                        // TODO create and attach volumes
                        SystemMachine sm = new SystemMachine();
                        sm.setState(SystemMachine.State.AVAILABLE);
                        sm.setResource(machine);
                        system.getMachines().add(sm);
                    }
                }

                if (cd.getComponentType() == ComponentType.SYSTEM) {
                    // creating new systems
                    for (int i = 0; i < cd.getComponentQuantity(); i++) {
                        SystemCreate sc = new SystemCreate();
                        if (cd.getComponentQuantity() > 1) {
                            String name = cd.getName() == null ? "" : cd.getName();
                            sc.setName(name + new Integer(i + 1).toString());
                        } else {
                            sc.setName(cd.getName());
                        }
                        SystemTemplate st = (SystemTemplate) cd.getComponentTemplate();
                        sc.setSystemTemplate(st);
                        sc.setDescription(cd.getDescription());
                        sc.setProperties(cd.getProperties());

                        System subSystem = this.createSystem(sc);
                        this.waitForSystemState(subSystem.getProviderAssignedId(), System.State.STARTED, System.State.STOPPED);
                        SystemSystem ss = new SystemSystem();
                        ss.setState(SystemSystem.State.AVAILABLE);
                        ss.setResource(subSystem);
                        system.getSystems().add(ss);
                    }
                }

            }

            if (failedCancelled) {
                system.setState(System.State.ERROR);
            } else {
                system.setState(System.State.CREATING);
            }
            this.systems.put(system.getProviderAssignedId(), system);
            system.setUpdated(new Date());

            return system;
        }

        public void deleteEntityInSystem(final String systemId, final String entityId, final String entityType)
            throws ConnectorException {
            // TODO Auto-generated method stub
        }

        /**
         * remove an entity (systemMachine, systemVolume etc) from a system<br>
         * <b><font color=red>Warning:the entityId is the provider id of <i>the
         * underlying resource</i>, not the systemXXX one</font></b>
         */
        public void removeEntityFromSystem(final String systemId, final String entityId) throws ConnectorException {

            System s = this.systems.get(systemId);

            boolean entityFound = false;

            for (SystemMachine sm : s.getMachines()) {
                if (sm.getResource().getProviderAssignedId().equals(entityId)) {
                    entityFound = true;
                    sm.setState(CloudCollectionItem.State.DELETED);
                    s.getMachines().remove(sm);
                    break;
                }
            }
            for (SystemVolume sm : s.getVolumes()) {
                if (sm.getResource().getProviderAssignedId().equals(entityId)) {
                    entityFound = true;
                    sm.setState(CloudCollectionItem.State.DELETED);
                    s.getVolumes().remove(sm);
                }
            }
            for (SystemSystem sm : s.getSystems()) {
                if (sm.getResource().getProviderAssignedId().equals(entityId)) {
                    entityFound = true;
                    sm.setState(CloudCollectionItem.State.DELETED);
                    s.getSystems().remove(sm);
                }
            }
            for (SystemNetwork sm : s.getNetworks()) {
                if (sm.getResource().getProviderAssignedId().equals(entityId)) {
                    entityFound = true;
                    sm.setState(CloudCollectionItem.State.DELETED);
                    s.getNetworks().remove(sm);
                }
            }

            if (!entityFound) {
                throw new ConnectorException("entity given " + entityId + " not found in system" + systemId);
            }

        }

        public void addEntityToSystem(final String systemId, final String entityId) throws ConnectorException {
            // TODO Auto-generated method stub
        }

        // private utility methods for System services (start,stop,etc)

        private boolean serviceSystem(final List<? extends CloudCollectionItem> l, final SystemAction action,
            final boolean force, final Map<String, String> properties) throws ConnectorException {
            boolean failedCancelled = false;
            for (CloudCollectionItem m : l) {
                this.callSystemService(m.getResource(), action, m.getResource().getProviderAssignedId().toString(), force,
                    properties);
            }
            return failedCancelled;
        }

        private void callSystemService(final CloudResource ce, final SystemAction action, final String providerId,
            final boolean force, final Map<String, String> properties) throws ConnectorException {
            if (ce.getClass().equals(Machine.class)) {
                switch (action) {
                case START:
                    this.startMachine(providerId);
                    this.waitForMachineState(providerId, Machine.State.STARTED);
                    break;
                case STOP:
                    this.stopMachine(providerId, force);
                    this.waitForMachineState(providerId, Machine.State.STOPPED);
                    break;
                case SUSPEND:
                    this.suspendMachine(providerId);
                    this.waitForMachineState(providerId, Machine.State.SUSPENDED);
                    break;
                case PAUSE:
                    this.pauseMachine(providerId);
                    this.waitForMachineState(providerId, Machine.State.PAUSED);
                    break;
                case RESTART:
                    this.restartMachine(providerId, force);
                    break;
                }
            } else if (ce.getClass().equals(System.class)) {
                switch (action) {
                case START:
                    this.startSystem(providerId, properties);
                    this.waitForSystemState(providerId, System.State.STARTED);
                    break;
                case STOP:
                    this.stopSystem(providerId, force, properties);
                    this.waitForSystemState(providerId, System.State.STOPPED);
                    break;
                case SUSPEND:
                    this.suspendSystem(providerId, properties);
                    this.waitForSystemState(providerId, System.State.SUSPENDED);
                    break;
                case PAUSE:
                    this.pauseSystem(providerId, properties);
                    this.waitForSystemState(providerId, System.State.PAUSED);
                    break;
                case RESTART:
                    this.restartSystem(providerId, force, properties);
                    break;
                }
            } else if (ce.getClass().equals(Network.class)) {
                switch (action) {
                case START:
                    this.startNetwork(providerId);
                    this.waitForNetworkState(providerId, Network.State.STARTED);
                    break;
                case STOP:
                    this.stopNetwork(providerId);
                    this.waitForNetworkState(providerId, Network.State.STOPPED);
                    break;
                }
            }
        }

        private static enum SystemAction {
            START, STOP, PAUSE, SUSPEND, RESTART, ADD, DELETE, ENTITY_REMOVE
        }

        private static final List<System.State> forbiddenSystemStartActions = Arrays.asList(System.State.CREATING,
            System.State.STARTED, System.State.DELETING);

        private static final List<System.State> forbiddenSystemStopActions = Arrays.asList(System.State.CREATING,
            System.State.STOPPED, System.State.PAUSING, System.State.PAUSED, System.State.SUSPENDING, System.State.SUSPENDED,
            System.State.DELETING);

        private static final List<System.State> forbiddenSystemPauseActions = Arrays.asList(System.State.CREATING,
            System.State.STARTING, System.State.STOPPING, System.State.STOPPED, System.State.PAUSING, System.State.PAUSED,
            System.State.SUSPENDING, System.State.SUSPENDED, System.State.DELETING);

        private static final List<System.State> forbiddenSystemSuspendActions = Arrays.asList(System.State.CREATING,
            System.State.STARTING, System.State.STOPPING, System.State.STOPPED, System.State.PAUSING, System.State.PAUSED,
            System.State.SUSPENDING, System.State.SUSPENDED, System.State.DELETING);

        private static final List<System.State> forbiddenSystemRestartActions = Arrays.asList();

        private void doSystemService(final String systemId, final System.State temporaryState, final SystemAction action,
            final List<System.State> forbiddenStates, final boolean force, final Map<String, String> properties)
            throws ConnectorException {
            MockCloudProviderConnector.logger.info(action + " system with providerAssignedId " + systemId);
            final System system = this.systems.get(systemId);
            if (system == null) {
                throw new ResourceNotFoundException("System " + systemId + " doesn't exist");
            }
            if (forbiddenStates.contains(system.getState())) {
                throw new BadStateException("Illegal operation");
            }

            system.setState(temporaryState);
            system.setUpdated(new Date());

            boolean failedCancelled = false;

            failedCancelled |= this.serviceSystem(system.getMachines(), action, force, properties);
            failedCancelled |= this.serviceSystem(system.getSystems(), action, force, properties);
            failedCancelled |= this.serviceSystem(system.getNetworks(), action, force, properties);

            if (failedCancelled) {
                // one or more jobs are failed or cancelled, so all is in error
                system.setState(System.State.ERROR);
            }
        }

        public void startSystem(final String systemId, final Map<String, String> properties) throws ConnectorException {
            this.doSystemService(systemId, System.State.STARTING, SystemAction.START, MockProvider.forbiddenSystemStartActions,
                false, properties);
        }

        public void stopSystem(final String systemId, final boolean force, final Map<String, String> properties)
            throws ConnectorException {
            this.doSystemService(systemId, System.State.STOPPING, SystemAction.STOP, MockProvider.forbiddenSystemStopActions,
                force, properties);
        }

        public void restartSystem(final String systemId, final boolean force, final Map<String, String> properties)
            throws ConnectorException {
            this.doSystemService(systemId, System.State.STARTING, SystemAction.RESTART,
                MockProvider.forbiddenSystemRestartActions, force, properties);
        }

        public void pauseSystem(final String systemId, final Map<String, String> properties) throws ConnectorException {
            this.doSystemService(systemId, System.State.PAUSING, SystemAction.PAUSE, MockProvider.forbiddenSystemPauseActions,
                false, properties);
        }

        public void suspendSystem(final String systemId, final Map<String, String> properties) throws ConnectorException {
            this.doSystemService(systemId, System.State.SUSPENDING, SystemAction.SUSPEND,
                MockProvider.forbiddenSystemSuspendActions, false, properties);
        }

        public void deleteSystem(final String systemId) throws ConnectorException {
            MockCloudProviderConnector.logger.info("deleting system with providerAssignedId " + systemId);

            final System system = this.systems.get(systemId);
            if (system == null) {
                throw new ResourceNotFoundException("System " + systemId + " doesn't exist");
            }

            boolean failedCancelled = false;

            for (SystemMachine m : system.getMachines()) {
                this.deleteMachine(m.getResource().getProviderAssignedId().toString());

            }
            for (SystemSystem m : system.getSystems()) {
                this.deleteSystem(m.getResource().getProviderAssignedId().toString());
            }
            for (SystemVolume m : system.getVolumes()) {
                this.deleteVolume(m.getResource().getProviderAssignedId().toString());
            }
            for (SystemNetwork m : system.getNetworks()) {
                this.deleteNetwork(m.getResource().getProviderAssignedId().toString());
            }
            system.setState(System.State.DELETING);
            system.setUpdated(new Date());
        }

        public void addVolumeToMachine(final String machineId, final MachineVolume machineVolume) throws ConnectorException {
            final Machine machine = this.machines.get(machineId);
            if (machine == null) {
                throw new ResourceNotFoundException("Machine " + machineId + " doesn't exist");
            }
            machineVolume.setState(MachineVolume.State.ATTACHING);
            machineVolume.setUpdated(new Date());
            machine.getVolumes().add(machineVolume);
        }

        public void removeVolumeFromMachine(final String machineId, final MachineVolume machineVolume)
            throws ConnectorException {
            final Machine machine = this.machines.get(machineId);
            if (machine == null) {
                throw new ResourceNotFoundException("Machine " + machineId + " doesn't exist");
            }
            MachineVolume targetMachineVolume = null;
            for (MachineVolume mv : machine.getVolumes()) {
                if (mv.getVolume() != null
                    && mv.getVolume().getProviderAssignedId().equals(machineVolume.getVolume().getProviderAssignedId())) {
                    targetMachineVolume = mv;
                    break;
                }
            }
            targetMachineVolume.setState(MachineVolume.State.DETACHING);
            targetMachineVolume.setUpdated(new Date());
        }

        public VolumeImage createVolumeImage(final VolumeImage from) throws ConnectorException {
            final String volumeImageProviderAssignedId = UUID.randomUUID().toString();
            final VolumeImage volumeImage = new VolumeImage();
            volumeImage.setBootable(from.getBootable());
            volumeImage.setImageLocation(from.getImageLocation());
            volumeImage.setProviderAssignedId(volumeImageProviderAssignedId);
            this.volumeImages.put(volumeImageProviderAssignedId, volumeImage);
            volumeImage.setState(VolumeImage.State.CREATING);
            volumeImage.setUpdated(new Date());
            return volumeImage;
        }

        public VolumeImage createVolumeSnapshot(final String volumeId, final VolumeImage from) throws ConnectorException {
            final Volume volume = this.volumes.get(volumeId);
            if (volume == null) {
                throw new ResourceNotFoundException("Volume " + volumeId + " doesn't exist");
            }
            final String volumeImageProviderAssignedId = UUID.randomUUID().toString();
            final VolumeImage volumeImage = new VolumeImage();
            volumeImage.setBootable(from.getBootable());
            volumeImage.setImageLocation(from.getImageLocation());
            volumeImage.setProviderAssignedId(volumeImageProviderAssignedId);
            this.volumeImages.put(volumeImageProviderAssignedId, volumeImage);
            volumeImage.setState(VolumeImage.State.CREATING);
            volumeImage.setUpdated(new Date());
            return volumeImage;
        }

        public VolumeImage getVolumeImage(final String volumeImageId) throws ConnectorException {
            final VolumeImage volumeImage = this.volumeImages.get(volumeImageId);
            if (volumeImage == null) {
                throw new ResourceNotFoundException("VolumeImage " + volumeImageId + " doesn't exist");
            }
            if (this.actionDone(volumeImage)) {
                if (volumeImage.getState() == VolumeImage.State.CREATING) {
                    volumeImage.setState(VolumeImage.State.AVAILABLE);
                    volumeImage.setUpdated(new Date());
                } else if (volumeImage.getState() == VolumeImage.State.DELETING) {
                    this.volumeImages.remove(volumeImage.getProviderAssignedId());
                    volumeImage.setState(VolumeImage.State.DELETED);
                    volumeImage.setUpdated(new Date());
                }
            }
            return volumeImage;
        }

        public void deleteVolumeImage(final String volumeImageId) throws ConnectorException {
            VolumeImage volumeImage = this.volumeImages.get(volumeImageId);
            if (volumeImage == null) {
                throw new ResourceNotFoundException("VolumeImage " + volumeImageId + " doesn't exist");
            }
            volumeImage.setState(VolumeImage.State.DELETING);
            volumeImage.setUpdated(new Date());
        }

        private static class SubnetConfig2MockSubnet implements Function<SubnetConfig, Subnet> {
            @Override
            public Subnet apply(final SubnetConfig config) {
                Subnet subnet = new Subnet();
                subnet.setCidr(config.getCidr());
                subnet.setProtocol(config.getProtocol());
                subnet.setEnableDhcp(config.isEnableDhcp());
                subnet.setState(Subnet.State.AVAILABLE);
                subnet.setProviderAssignedId(UUID.randomUUID().toString());
                return subnet;
            }
        }

        public Network createNetwork(final NetworkCreate networkCreate) throws ConnectorException {
            final String networkProviderAssignedId = UUID.randomUUID().toString();
            final Network network = new Network();
            network.setName(networkCreate.getName());
            network.setNetworkType(networkCreate.getNetworkTemplate().getNetworkConfig().getNetworkType());
            network.setSubnets(Lists.transform(networkCreate.getNetworkTemplate().getNetworkConfig().getSubnets(),
                new SubnetConfig2MockSubnet()));
            network.setClassOfService(networkCreate.getNetworkTemplate().getNetworkConfig().getClassOfService());
            network.setMtu(networkCreate.getNetworkTemplate().getNetworkConfig().getMtu());
            network.setProviderAssignedId(networkProviderAssignedId);
            network.setNetworkPorts(new ArrayList<NetworkNetworkPort>());
            this.networks.put(networkProviderAssignedId, network);
            network.setState(Network.State.CREATING);
            network.setUpdated(new Date());
            return network;
        }

        public Network.State getNetworkState(final String networkId) throws ConnectorException {
            return this.getNetwork(networkId).getState();
        }

        public Network getNetwork(final String networkId) throws ConnectorException {
            final Network network = this.networks.get(networkId);
            if (network == null) {
                throw new ResourceNotFoundException("Network " + networkId + " doesn't exist");
            }
            if (this.actionDone(network)) {
                if (network.getState() == Network.State.CREATING) {
                    network.setState(Network.State.STARTED);
                    network.setUpdated(new Date());
                } else if (network.getState() == Network.State.STARTING) {
                    network.setState(Network.State.STARTED);
                    network.setUpdated(new Date());
                } else if (network.getState() == Network.State.STOPPING) {
                    network.setState(Network.State.STOPPED);
                    network.setUpdated(new Date());
                } else if (network.getState() == Network.State.DELETING) {
                    network.setState(Network.State.DELETED);
                    network.setUpdated(new Date());
                    this.networks.remove(network.getProviderAssignedId());
                }
            }
            return network;
        }

        public List<Network> getNetworks() throws ConnectorException {
            List<Network> result = new ArrayList<Network>();
            for (Network net : this.networks.values()) {
                result.add(this.getNetwork(net.getProviderAssignedId()));
            }
            return result;
        }

        public void deleteNetwork(final String networkId) throws ConnectorException {
            MockCloudProviderConnector.logger.info("Deleting network with providerAssignedId " + networkId);
            Network network = this.networks.get(networkId);
            if (network == null) {
                throw new ResourceNotFoundException("Network " + networkId + " doesn't exist");
            }
            network.setState(Network.State.DELETING);
            network.setUpdated(new Date());
        }

        public void startNetwork(final String networkId) throws ConnectorException {
            MockCloudProviderConnector.logger.info("Starting network with providerAssignedId " + networkId);
            final Network network = this.networks.get(networkId);
            if (network == null) {
                throw new ResourceNotFoundException("Network " + networkId + " doesn't exist");
            }
            if (network.getState() != Network.State.STOPPED) {
                throw new ConnectorException("Illegal operation");
            }
            network.setState(Network.State.STARTING);
            network.setUpdated(new Date());
        }

        public void stopNetwork(final String networkId) throws ConnectorException {
            MockCloudProviderConnector.logger.info("Stopping network with providerAssignedId " + networkId);
            final Network network = this.networks.get(networkId);
            if (network == null) {
                throw new ResourceNotFoundException("Network " + networkId + " doesn't exist");
            }
            if (network.getState() != Network.State.STARTED) {
                throw new ConnectorException("Illegal operation");
            }
            network.setState(Network.State.STOPPING);
            network.setUpdated(new Date());
        }

        public NetworkPort createNetworkPort(final NetworkPortCreate networkPortCreate) throws ConnectorException {
            if (networkPortCreate.getNetworkPortTemplate().getNetwork() == null) {
                throw new ResourceNotFoundException("Wrong network port template: null network");
            }
            final Network network;
            String networkId = networkPortCreate.getNetworkPortTemplate().getNetwork().getProviderAssignedId();
            network = this.networks.get(networkId);
            if (network == null) {
                throw new ResourceNotFoundException("Unknown network with id=" + networkId);
            }
            final String networkPortProviderAssignedId = UUID.randomUUID().toString();
            final NetworkPort networkPort = new NetworkPort();
            networkPort
                .setClassOfService(networkPortCreate.getNetworkPortTemplate().getNetworkPortConfig().getClassOfService());
            networkPort.setPortType(networkPortCreate.getNetworkPortTemplate().getNetworkPortConfig().getPortType());
            networkPort.setProviderAssignedId(networkPortProviderAssignedId);
            this.networkPorts.put(networkPortProviderAssignedId, networkPort);
            networkPort.setState(NetworkPort.State.CREATING);
            networkPort.setNetwork(network);
            networkPort.setUpdated(new Date());
            return networkPort;
        }

        public NetworkPort getNetworkPort(final String networkPortId) throws ConnectorException {
            final NetworkPort networkPort = this.networkPorts.get(networkPortId);
            if (networkPort == null) {
                throw new ResourceNotFoundException("NetworkPort " + networkPortId + " doesn't exist");
            }
            if (this.actionDone(networkPort)) {
                if (networkPort.getState() == NetworkPort.State.CREATING) {
                    networkPort.setState(NetworkPort.State.STARTED);
                    NetworkNetworkPort netNetworkPort = new NetworkNetworkPort();
                    netNetworkPort.setState(NetworkNetworkPort.State.AVAILABLE);
                    netNetworkPort.setNetworkPort(networkPort);
                    networkPort.getNetwork().getNetworkPorts().add(netNetworkPort);
                    networkPort.setUpdated(new Date());
                } else if (networkPort.getState() == NetworkPort.State.STARTING) {
                    networkPort.setState(NetworkPort.State.STARTED);
                    networkPort.setUpdated(new Date());
                } else if (networkPort.getState() == NetworkPort.State.STOPPING) {
                    networkPort.setState(NetworkPort.State.STOPPED);
                    networkPort.setUpdated(new Date());
                } else if (networkPort.getState() == NetworkPort.State.DELETING) {
                    networkPort.setState(NetworkPort.State.DELETED);
                    networkPort.setUpdated(new Date());
                    this.networkPorts.remove(networkPort.getProviderAssignedId());
                }
            }
            return networkPort;
        }

        public void deleteNetworkPort(final String networkPortId) throws ConnectorException {
            MockCloudProviderConnector.logger.info("Deleting network port with providerAssignedId " + networkPortId);
            NetworkPort networkPort = this.networkPorts.get(networkPortId);
            if (networkPort == null) {
                throw new ResourceNotFoundException("NetworkPort " + networkPortId + " doesn't exist");
            }
            networkPort.setState(NetworkPort.State.DELETING);
            networkPort.setUpdated(new Date());
        }

        public void startNetworkPort(final String networkPortId) throws ConnectorException {
            MockCloudProviderConnector.logger.info("Starting network port with providerAssignedId " + networkPortId);
            final NetworkPort networkPort = this.networkPorts.get(networkPortId);
            if (networkPort == null) {
                throw new ResourceNotFoundException("Network " + networkPortId + " doesn't exist");
            }
            if (networkPort.getState() != NetworkPort.State.STOPPED) {
                throw new ConnectorException("Illegal operation");
            }
            networkPort.setState(NetworkPort.State.STARTING);
            networkPort.setUpdated(new Date());
        }

        public void stopNetworkPort(final String networkPortId) throws ConnectorException {
            MockCloudProviderConnector.logger.info("Stopping network port with providerAssignedId " + networkPortId);
            final NetworkPort networkPort = this.networkPorts.get(networkPortId);
            if (networkPort == null) {
                throw new ResourceNotFoundException("Network " + networkPortId + " doesn't exist");
            }
            if (networkPort.getState() != NetworkPort.State.STARTED) {
                throw new ConnectorException("Illegal operation");
            }
            networkPort.setState(NetworkPort.State.STOPPING);
            networkPort.setUpdated(new Date());
        }

        public ForwardingGroup createForwardingGroup(final ForwardingGroupCreate forwardingGroupCreate)
            throws ConnectorException {
            final List<ForwardingGroupNetwork> networksToAdd = new ArrayList<ForwardingGroupNetwork>();
            if (forwardingGroupCreate.getForwardingGroupTemplate().getNetworks() != null) {
                for (Network net : forwardingGroupCreate.getForwardingGroupTemplate().getNetworks()) {
                    String netId = net.getProviderAssignedId();
                    Network providerNetwork = this.networks.get(netId);
                    if (providerNetwork == null) {
                        throw new ResourceNotFoundException("Unknown network with id " + netId);
                    }
                    ForwardingGroupNetwork fgNetwork = new ForwardingGroupNetwork();
                    fgNetwork.setNetwork(providerNetwork);
                    fgNetwork.setState(ForwardingGroupNetwork.State.AVAILABLE);
                    networksToAdd.add(fgNetwork);
                }
            }
            final String forwardingGroupProviderAssignedId = UUID.randomUUID().toString();
            final ForwardingGroup forwardingGroup = new ForwardingGroup();
            forwardingGroup.setProviderAssignedId(forwardingGroupProviderAssignedId);
            forwardingGroup.setNetworks(new ArrayList<ForwardingGroupNetwork>());
            this.forwardingGroups.put(forwardingGroupProviderAssignedId, forwardingGroup);
            forwardingGroup.setState(ForwardingGroup.State.AVAILABLE);
            forwardingGroup.setNetworks(networksToAdd);
            forwardingGroup.setUpdated(new Date());
            return forwardingGroup;
        }

        public ForwardingGroup getForwardingGroup(final String forwardingGroupId) throws ConnectorException {
            final ForwardingGroup forwardingGroup = this.forwardingGroups.get(forwardingGroupId);
            if (forwardingGroup == null) {
                throw new ResourceNotFoundException("ForwardingGroup " + forwardingGroupId + " doesn't exist");
            }
            if (this.actionDone(forwardingGroup)) {
                if (forwardingGroup.getState() == ForwardingGroup.State.CREATING) {
                    forwardingGroup.setState(ForwardingGroup.State.AVAILABLE);
                    forwardingGroup.setUpdated(new Date());
                } else if (forwardingGroup.getState() == ForwardingGroup.State.DELETING) {
                    forwardingGroup.setState(ForwardingGroup.State.DELETED);
                    forwardingGroup.setUpdated(new Date());
                    this.forwardingGroups.remove(forwardingGroup.getProviderAssignedId());
                }
            }
            Iterator<ForwardingGroupNetwork> it = forwardingGroup.getNetworks().iterator();
            while (it.hasNext()) {
                ForwardingGroupNetwork fgNetwork = it.next();
                if (this.actionDone(fgNetwork)) {
                    if (fgNetwork.getState() == ForwardingGroupNetwork.State.ATTACHING) {
                        fgNetwork.setState(ForwardingGroupNetwork.State.AVAILABLE);
                        fgNetwork.setUpdated(new Date());
                    } else if (fgNetwork.getState() == ForwardingGroupNetwork.State.DETACHING) {
                        it.remove();
                    }
                }
            }
            return forwardingGroup;
        }

        public void deleteForwardingGroup(final ForwardingGroup forwardingGroup) throws ConnectorException {
            MockCloudProviderConnector.logger.info("Deleting forwarding group with providerAssignedId "
                + forwardingGroup.getProviderAssignedId());
            ForwardingGroup mockForwardingGroup = this.forwardingGroups.get(forwardingGroup.getProviderAssignedId());
            if (mockForwardingGroup == null) {
                throw new ResourceNotFoundException("ForwardingGroup " + forwardingGroup.getProviderAssignedId()
                    + " doesn't exist");
            }
            this.forwardingGroups.remove(mockForwardingGroup.getProviderAssignedId());
            mockForwardingGroup.setState(ForwardingGroup.State.DELETED);
            mockForwardingGroup.setUpdated(new Date());
        }

        public void addNetworkToForwardingGroup(final String forwardingGroupId, final ForwardingGroupNetwork fgNetwork)
            throws ConnectorException {
            final ForwardingGroup forwardingGroup = this.forwardingGroups.get(forwardingGroupId);
            if (forwardingGroup == null) {
                throw new ResourceNotFoundException("NetworkPort " + forwardingGroupId + " doesn't exist");
            }
            final Network network = this.networks.get(fgNetwork.getNetwork().getProviderAssignedId());
            if (network == null) {
                throw new ResourceNotFoundException("Unknown network with id=" + fgNetwork.getNetwork().getProviderAssignedId());
            }
            fgNetwork.setState(ForwardingGroupNetwork.State.ATTACHING);
            fgNetwork.setUpdated(new Date());
            forwardingGroup.getNetworks().add(fgNetwork);
        }

        public void removeNetworkFromForwardingGroup(final String forwardingGroupId, final String networkId)
            throws ConnectorException {
            final ForwardingGroup forwardingGroup = this.forwardingGroups.get(forwardingGroupId);
            if (forwardingGroup == null) {
                throw new ResourceNotFoundException("NetworkPort " + forwardingGroupId + " doesn't exist");
            }
            final Network network = this.networks.get(networkId);
            if (network == null) {
                throw new ResourceNotFoundException("Unknown network with id=" + networkId);
            }
            ForwardingGroupNetwork fgNetwork = null;
            for (ForwardingGroupNetwork net : forwardingGroup.getNetworks()) {
                if (net.getNetwork().getProviderAssignedId().equals(networkId)) {
                    fgNetwork = net;
                    break;
                }
            }
            if (fgNetwork == null) {
                throw new ConnectorException("Network with id=" + networkId + " is not a member of forwarding group with id="
                    + forwardingGroupId);
            }
            fgNetwork.setState(ForwardingGroupNetwork.State.DETACHING);
            fgNetwork.setUpdated(new Date());
        }

        public String createSecurityGroup(final SecurityGroupCreate create) {
            SecurityGroup secGroup = new SecurityGroup();
            secGroup.setName(create.getName());
            secGroup.setDescription(create.getDescription());
            secGroup.setProviderAssignedId(UUID.randomUUID().toString());
            secGroup.setRules(new ArrayList<SecurityGroupRule>());
            secGroup.setState(SecurityGroup.State.AVAILABLE);
            secGroup.setCreated(new Date());
            this.securityGroups.put(secGroup.getProviderAssignedId(), secGroup);
            return secGroup.getProviderAssignedId();
        }

        public SecurityGroup getSecurityGroup(final String groupId) throws ConnectorException {
            SecurityGroup secGroup = this.securityGroups.get(groupId);
            if (secGroup == null) {
                throw new ResourceNotFoundException();
            }
            return secGroup;
        }

        public List<SecurityGroup> getSecurityGroups() {
            List<SecurityGroup> result = new ArrayList<SecurityGroup>();
            for (SecurityGroup group : this.securityGroups.values()) {
                result.add(group);
            }
            return result;
        }

        public void deleteSecurityGroup(final String groupId) throws ConnectorException {
            SecurityGroup secGroup = this.securityGroups.get(groupId);
            if (secGroup == null) {
                throw new ResourceNotFoundException();
            }
            this.securityGroups.remove(groupId);
        }

        public void deleteRuleFromSecurityGroup(final String groupId, final SecurityGroupRule rule) throws ConnectorException {
            SecurityGroup secGroup = this.securityGroups.get(groupId);
            if (secGroup == null) {
                throw new ResourceNotFoundException();
            }
            for (Iterator<SecurityGroupRule> it = secGroup.getRules().iterator(); it.hasNext();) {
                SecurityGroupRule groupRule = it.next();
                if (groupRule.getProviderAssignedId().equals(rule.getProviderAssignedId())) {
                    it.remove();
                    return;
                }
            }
            throw new ResourceNotFoundException("rule with id " + rule.getProviderAssignedId() + " not found");
        }

        public String addRuleToSecurityGroup(final String groupId, final SecurityGroupRule rule) throws ConnectorException {
            SecurityGroup secGroup = this.securityGroups.get(groupId);
            if (secGroup == null) {
                throw new ResourceNotFoundException();
            }
            SecurityGroupRule newRule = new SecurityGroupRule();
            newRule.setParentGroup(secGroup);
            newRule.setIpProtocol(rule.getIpProtocol());
            newRule.setFromPort(rule.getFromPort());
            newRule.setToPort(rule.getToPort());
            newRule.setSourceIpRange(rule.getSourceIpRange());
            if (rule.getSourceGroup() != null) {
                SecurityGroup sourceSecGroup = this.securityGroups.get(rule.getSourceGroup().getProviderAssignedId());
                if (sourceSecGroup == null) {
                    throw new ResourceNotFoundException();
                }
                newRule.setSourceGroup(sourceSecGroup);
            }
            newRule.setProviderAssignedId(UUID.randomUUID().toString());
            secGroup.getRules().add(newRule);
            return newRule.getProviderAssignedId();
        }

        public void removeAddressFromMachine(final String machineId, final Address address) throws ConnectorException {
            final Machine machine = this.machines.get(machineId);
            if (machine == null) {
                throw new ResourceNotFoundException("Machine " + machineId + " doesn't exist");
            }
            Address addr = this.allocatedAddresses.get(address.getIp());
            if (addr == null) {
                throw new ConnectorException("Address " + address.getIp() + " not found");
            }
            if (addr.getResource() == null || !addr.getResource().getProviderAssignedId().equals(machineId)) {
                throw new ConnectorException("Address " + addr.getIp() + " not associated with machine " + machineId);
            }
            addr.setResource(null);
            // remove address from machine first nic
            this.deleteAddressFromMachine(addr.getIp(), machine);
        }

        private void deleteAddressFromMachine(final String ip, final Machine machine) {
            for (Iterator<MachineNetworkInterfaceAddress> it = machine.getNetworkInterfaces().get(0).getAddresses().iterator(); it
                .hasNext();) {
                MachineNetworkInterfaceAddress nicAddr = it.next();
                if (nicAddr.getAddress().getIp().equals(ip)) {
                    it.remove();
                    break;
                }
            }
        }

        public void addAddressToMachine(final String machineId, final Address address) throws ConnectorException {
            final Machine machine = this.machines.get(machineId);
            if (machine == null) {
                throw new ResourceNotFoundException("Machine " + machineId + " doesn't exist");
            }
            Address addr = this.allocatedAddresses.get(address.getIp());
            if (addr == null) {
                throw new ConnectorException("Address " + address.getIp() + " not found");
            }
            if (addr.getResource() != null) {
                throw new ConnectorException("Address " + addr.getIp() + " already associated");
            }
            if (machine.getNetworkInterfaces().isEmpty()) {
                throw new ConnectorException("No network");
            }

            addr.setResource(machine);

            // add address to machine first nic
            MachineNetworkInterface nic = machine.getNetworkInterfaces().get(0);
            MachineNetworkInterfaceAddress entry = new MachineNetworkInterfaceAddress();
            Address ip = new Address();
            ip.setIp(addr.getIp());
            ip.setAllocation("dynamic");
            ip.setProtocol("IPv4");
            ip.setNetwork(nic.getNetwork());
            entry.setAddress(ip);
            nic.getAddresses().add(entry);
        }

        public synchronized void deleteAddress(final Address address) throws ConnectorException {
            Address addr = this.allocatedAddresses.get(address.getIp());
            if (addr == null) {
                throw new ConnectorException("Address " + address.getIp() + " not found");
            }
            addr.setState(Address.State.DELETED);
            if (addr.getResource() != null) {
                this.deleteAddressFromMachine(addr.getIp(), (Machine) addr.getResource());
                addr.setResource(null);
            }
            this.allocatedAddresses.remove(address.getIp());
        }

        public synchronized Address allocateAddress(final Map<String, String> properties) throws ConnectorException {
            for (Address addr : this.addressPool) {
                if (addr.getState() == Address.State.DELETED) {
                    addr.setState(Address.State.CREATED);
                    this.allocatedAddresses.put(addr.getIp(), addr);
                    return addr;
                }
            }
            throw new ConnectorException("No address available");
        }

        public List<Address> getAddresses() {
            return new ArrayList<Address>(this.allocatedAddresses.values());
        }

    }

}
