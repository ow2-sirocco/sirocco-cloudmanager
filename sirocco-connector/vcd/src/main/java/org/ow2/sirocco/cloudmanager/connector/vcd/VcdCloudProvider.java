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

package org.ow2.sirocco.cloudmanager.connector.vcd;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.connector.api.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudCollectionItem;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Subnet;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.ProviderMapping;
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

import com.vmware.vcloud.api.rest.schema.ComposeVAppParamsType;
import com.vmware.vcloud.api.rest.schema.FirewallRuleType;
import com.vmware.vcloud.api.rest.schema.FirewallServiceType;
import com.vmware.vcloud.api.rest.schema.GuestCustomizationSectionType;
import com.vmware.vcloud.api.rest.schema.InstantiationParamsType;
import com.vmware.vcloud.api.rest.schema.IpRangeType;
import com.vmware.vcloud.api.rest.schema.IpRangesType;
import com.vmware.vcloud.api.rest.schema.IpScopeType;
import com.vmware.vcloud.api.rest.schema.IpScopesType;
import com.vmware.vcloud.api.rest.schema.NatRuleType;
import com.vmware.vcloud.api.rest.schema.NatServiceType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigSectionType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.NetworkConnectionSectionType;
import com.vmware.vcloud.api.rest.schema.NetworkConnectionType;
import com.vmware.vcloud.api.rest.schema.NetworkFeaturesType;
import com.vmware.vcloud.api.rest.schema.NetworkServiceType;
import com.vmware.vcloud.api.rest.schema.ObjectFactory;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.SourcedCompositionItemParamType;
import com.vmware.vcloud.api.rest.schema.VAppNetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.VAppType;
import com.vmware.vcloud.api.rest.schema.VmType;
import com.vmware.vcloud.api.rest.schema.ovf.CimString;
import com.vmware.vcloud.api.rest.schema.ovf.MsgType;
import com.vmware.vcloud.api.rest.schema.ovf.ProductSectionProperty;
import com.vmware.vcloud.api.rest.schema.ovf.ProductSectionType;
import com.vmware.vcloud.api.rest.schema.ovf.RASDType;
import com.vmware.vcloud.api.rest.schema.ovf.ResourceType;
import com.vmware.vcloud.api.rest.schema.ovf.SectionType;
import com.vmware.vcloud.sdk.Expression;
import com.vmware.vcloud.sdk.Filter;
import com.vmware.vcloud.sdk.OrgVdcNetwork;
import com.vmware.vcloud.sdk.QueryParams;
import com.vmware.vcloud.sdk.ReferenceResult;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VappNetwork;
import com.vmware.vcloud.sdk.VirtualCpu;
import com.vmware.vcloud.sdk.VirtualDisk;
import com.vmware.vcloud.sdk.VirtualMemory;
import com.vmware.vcloud.sdk.admin.AdminOrgVdcNetwork;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.admin.AdminVdc;
import com.vmware.vcloud.sdk.admin.EdgeGateway;
import com.vmware.vcloud.sdk.constants.FenceModeValuesType;
import com.vmware.vcloud.sdk.constants.IpAddressAllocationModeType;
import com.vmware.vcloud.sdk.constants.NatPolicyType;
import com.vmware.vcloud.sdk.constants.NatTypeType;
import com.vmware.vcloud.sdk.constants.UndeployPowerActionType;
import com.vmware.vcloud.sdk.constants.VMStatus;
import com.vmware.vcloud.sdk.constants.VappStatus;
import com.vmware.vcloud.sdk.constants.query.ExpressionType;
import com.vmware.vcloud.sdk.constants.query.QueryReferenceField;
import com.vmware.vcloud.sdk.constants.query.QueryReferenceType;

public class VcdCloudProvider {
    private static Logger logger = LoggerFactory.getLogger(VcdCloudProvider.class);

    private static int DEFAULT_WAIT_TIME_IN_MILLISECONDS = 600000;

    private final String cloudProviderId; // XXX

    private final CloudProviderAccount cloudProviderAccount;

    private final CloudProviderLocation cloudProviderLocation;

    private final VCloudContext vCloudContext;

    public VcdCloudProvider(final ProviderTarget target) throws ConnectorException {
        this.cloudProviderId = UUID.randomUUID().toString();
        this.cloudProviderAccount = target.getAccount();
        this.cloudProviderLocation = target.getLocation();
        this.vCloudContext = new VCloudContext(this.cloudProviderAccount, VcdCloudProvider.logger);
    }

    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public CloudProviderLocation getCloudProviderLocation() {
        return this.cloudProviderLocation;
    }

    //
    // System Service
    //

    private System.State fromvAppStatusToSystemState(final Vapp vapp) {
        VappStatus state = vapp.getVappStatus();

        if (state == VappStatus.FAILED_CREATION) {
            return System.State.ERROR;
        } else if (state == VappStatus.INCONSISTENT_STATE) {
            return System.State.ERROR;
        } else if (state == VappStatus.MIXED) {
            return System.State.MIXED;
        } else if (state == VappStatus.POWERED_OFF) {
            return System.State.STOPPED;
        } else if (state == VappStatus.POWERED_ON) {
            return System.State.STARTED;
        } else if (state == VappStatus.RESOLVED) {
            return System.State.STOPPED;
        } else if (state == VappStatus.SUSPENDED) {
            return System.State.SUSPENDED;
        } else if (state == VappStatus.UNKNOWN) {
            return System.State.ERROR;
        } else if (state == VappStatus.UNRECOGNIZED) {
            return System.State.ERROR;
        } else if (state == VappStatus.UNRESOLVED) {
            return System.State.STOPPED;
        } else if (state == VappStatus.WAITING_FOR_INPUT) {
            return System.State.ERROR; // CIMI mapping!
        } else {
            return System.State.ERROR;
        }
    }

    private void fromvAppToSystem(final Vapp vapp, final System system) throws ConnectorException {
        VAppType vAppType = vapp.getResource();

        system.setName(vAppType.getName());
        system.setDescription(vAppType.getDescription());
        system.setProviderAssignedId(vAppType.getHref());
        system.setState(this.fromvAppStatusToSystemState(vapp));
        /*VcdCloudProviderConnectorFactory.logger.info("# vApp state=" + vapp.getVappStatus() + ", system state=" + system.getState());*/
        system.setLocation(this.cloudProviderLocation);
        system.setCloudProviderAccount(this.cloudProviderAccount);

        system.setMachines(new ArrayList<SystemMachine>());
        try {
            List<VM> childVms = vapp.getChildrenVms();
            for (VM childVm : childVms) {
                SystemMachine systemMachine = new SystemMachine();
                this.fromVmToSystemMachine(childVm, systemMachine);
                system.getMachines().add(systemMachine);
            }
        } catch (VCloudException e) {
            throw new ConnectorException(e);
        }

        system.setNetworks(new ArrayList<SystemNetwork>());
        try {
            for (ReferenceType vappNetworkReferenceType : vapp.getVappNetworkRefsByName().values()) {
                VappNetwork vappNetwork = VappNetwork.getVappNetworkByReference(this.vCloudContext.getVcloudClient(),
                    vappNetworkReferenceType);
                /* Mapping Rule
                 * Add systemNetwork only for isolated vAppNetwork */
                if (!vappNetwork.getResource().getConfiguration().getFenceMode().equals(FenceModeValuesType.ISOLATED.value())) {
                    continue;
                }
                SystemNetwork systemNetwork = new SystemNetwork();
                this.fromVAppNetworkToSystemNetwork(vappNetwork, systemNetwork);
                system.getNetworks().add(systemNetwork);
            }
        } catch (VCloudException e) {
            throw new ConnectorException(e);
        }

        system.setVolumes(new ArrayList<SystemVolume>());
        system.setSystems(new ArrayList<SystemSystem>());
        system.setCredentials(new ArrayList<SystemCredentials>());
    }

    public System createSystem(final SystemCreate systemCreate) throws ConnectorException {
        VcdCloudProvider.logger.info("creating system ");
        final int waitTimeInMilliSeconds = VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
        final Map<String, MachineTemplate> machineTemplateMap = new HashMap<String, MachineTemplate>();
        final System system = new System();
        Vapp vapp = null;

        try {
            vapp = this.createVapp(systemCreate, machineTemplateMap);
            List<Task> tasks = vapp.getTasks();
            if (tasks.size() > 0) { // wait for all tasks
                tasks.get(0).waitForTask(waitTimeInMilliSeconds);
            }

            // configure vms
            this.configureVmSections(vapp, machineTemplateMap);

            // refresh the vapp (otherwise no childrenVms is visible!
            vapp = Vapp.getVappByReference(this.vCloudContext.getVcloudClient(), vapp.getReference());
            this.startSystem(vapp.getResource().getHref(), new HashMap<String, String>());
            this.fromvAppToSystem(vapp, system);

        } catch (Exception ex) {
            try {
                if (vapp != null) {
                    VcdCloudProvider.logger.info("createSystem failed. Try to delete the created vapp");
                    this.deleteVapp(vapp);
                }
            } catch (VCloudException e) {
            } catch (TimeoutException e) {
            }
            throw new ConnectorException(ex);
        }
        return system;
    }

    public void deleteSystem(final String systemId) throws ConnectorException {
        VcdCloudProvider.logger.info("deleting system with providerAssignedId " + systemId);
        Vapp vapp = this.getVappByProviderAssignedId(systemId);

        try {
            this.deleteVapp(vapp);
        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    private void deleteVapp(final Vapp vapp) throws VCloudException, TimeoutException {
        final int waitTimeInMilliSeconds = VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
        if (vapp.isDeployed()) {
            VcdCloudProvider.logger.info("Undeploying vapp: " + vapp.getResource().getName());
            if (vapp.getVappStatus() == VappStatus.POWERED_ON) {
                vapp.undeploy(UndeployPowerActionType.POWEROFF).waitForTask(waitTimeInMilliSeconds);
            } else {
                vapp.undeploy(UndeployPowerActionType.DEFAULT).waitForTask(waitTimeInMilliSeconds);
            }
        }
        VcdCloudProvider.logger.info("deleting vapp: " + vapp.getResource().getName());
        // vapp.delete().waitForTask(waitTimeInMilliSeconds);
        vapp.delete();
    }

    public void startSystem(final String systemId, final Map<String, String> properties) throws ConnectorException {
        VcdCloudProvider.logger.info("starting system with providerAssignedId " + systemId);
        Vapp vapp = this.getVappByProviderAssignedId(systemId);

        try {
            VcdCloudProvider.logger.info("powerOn " + vapp.getResource().getName());
            // vapp.powerOn().waitForTask(DEFAULT_WAIT_TIME_IN_MILLISECONDS);
            vapp.powerOn();
        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    public void stopSystem(final String systemId, final boolean force, final Map<String, String> properties)
        throws ConnectorException {
        VcdCloudProvider.logger.info("stopping system with providerAssignedId " + systemId);
        Vapp vapp = this.getVappByProviderAssignedId(systemId);

        try {
            if (force) {
                VcdCloudProvider.logger.info("powerOff " + vapp.getResource().getName());
                // vapp.powerOff().waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                vapp.powerOff();
            } else {
                VcdCloudProvider.logger.info("shutdown " + vapp.getResource().getName());
                // vapp.shutdown().waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                vapp.shutdown();
            }
        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    public void restartSystem(final String systemId, final boolean force, final Map<String, String> properties)
        throws ConnectorException {
        VcdCloudProvider.logger.info("restarting system with providerAssignedId " + systemId);
        Vapp vapp = this.getVappByProviderAssignedId(systemId);

        try {
            if (force) {
                VcdCloudProvider.logger.info("reset " + vapp.getResource().getName());
                // vapp.reset().waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                vapp.reset();
            } else {
                VcdCloudProvider.logger.info("reboot " + vapp.getResource().getName());
                // vapp.reboot().waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                vapp.reboot();
            }
        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    public void suspendSystem(final String systemId, final Map<String, String> properties) throws ConnectorException {
        VcdCloudProvider.logger.info("suspending system with providerAssignedId " + systemId);
        Vapp vapp = this.getVappByProviderAssignedId(systemId);

        try {
            VcdCloudProvider.logger.info("suspend " + vapp.getResource().getName());
            // vapp.suspend().waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
            vapp.suspend();
        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    public System.State getSystemState(final String systemId) throws ResourceNotFoundException, ConnectorException {
        Vapp vapp = this.getVappByProviderAssignedId(systemId);
        return this.fromvAppStatusToSystemState(vapp);
    }

    public System getSystem(final String systemId) throws ConnectorException, ResourceNotFoundException {
        final System system = new System();
        this.fromvAppToSystem(this.getVappByProviderAssignedId(systemId), system);
        return system;
    }

    public List<? extends CloudCollectionItem> getEntityListFromSystem(final String systemId, final String entityType)
        throws ConnectorException {
        VcdCloudProvider.logger.info("getEntityListFromSystem with entityType=" + entityType);
        final System system = this.getSystem(systemId);
        if (entityType.equals(SystemMachine.class.getName())) {
            return system.getMachines();
        } else {
            throw new ConnectorException("unsupported entity type: " + entityType);
        }
    }

    public void deleteEntityInSystem(final String systemId, final String entityId, final String entityType)
        throws ConnectorException {
        VcdCloudProvider.logger.info("deleting Entity with providerAssignedId " + entityId + " and type=" + entityType
            + ", in system with providerAssignedId=" + systemId);
        final System system = this.getSystem(systemId);

        if (entityType.equals(SystemMachine.class.getName())) {
            for (SystemMachine sm : system.getMachines()) {
                if (sm.getResource().getProviderAssignedId().equals(entityId)) {
                    this.deleteMachineInSystem(system, (Machine) sm.getResource());
                }
            }
        } else {
            throw new ConnectorException("unsupported entity type: " + entityType);
        }

        throw new ResourceNotFoundException("entity " + entityId + " not found in system " + systemId);
    }

    private void deleteMachineInSystem(final System system, final Machine machine) throws ConnectorException {
        final int waitTimeInMilliSeconds = VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
        VM vm = this.getVmByProviderAssignedId(machine.getProviderAssignedId());

        try {
            if (vm.isDeployed()) {
                VcdCloudProvider.logger.info("Undeploying " + vm.getResource().getName());
                if (vm.getVMStatus() == VMStatus.POWERED_ON) {
                    vm.undeploy(UndeployPowerActionType.POWEROFF).waitForTask(waitTimeInMilliSeconds);
                } else {
                    vm.undeploy(UndeployPowerActionType.DEFAULT).waitForTask(waitTimeInMilliSeconds);
                }
            }
            VcdCloudProvider.logger.info("deleting " + vm.getResource().getName());
            // vm.delete().waitForTask(waitTimeInMilliSeconds);
            vm.delete();
        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    //
    // Compute Service
    //

    private Machine.State fromvVmStatusToMachineState(final VM vm) {
        VMStatus state = vm.getVMStatus();

        if (state == VMStatus.FAILED_CREATION) {
            return Machine.State.ERROR;
        } else if (state == VMStatus.INCONSISTENT_STATE) {
            return Machine.State.ERROR;
        } else if (state == VMStatus.POWERED_OFF) {
            return Machine.State.STOPPED;
        } else if (state == VMStatus.POWERED_ON) {
            return Machine.State.STARTED;
        } else if (state == VMStatus.RESOLVED) {
            return Machine.State.STOPPED;
        } else if (state == VMStatus.SUSPENDED) {
            return Machine.State.SUSPENDED;
        } else if (state == VMStatus.UNKNOWN) {
            return Machine.State.ERROR;
        } else if (state == VMStatus.UNRECOGNIZED) {
            return Machine.State.ERROR;
        } else if (state == VMStatus.UNRESOLVED) {
            return Machine.State.STOPPED;
        } else if (state == VMStatus.WAITING_FOR_INPUT) {
            return Machine.State.ERROR; // CIMI mapping!
        } else {
            return Machine.State.ERROR;
        }
    }

    private void fromVmToSystemMachine(final VM vm, final SystemMachine sm) throws ConnectorException {
        sm.setState(SystemMachine.State.AVAILABLE);
        Machine machine = new Machine();
        this.fromVmToMachine(vm, machine);
        sm.setResource(machine);
    }

    private void fromVmToMachine(final VM vm, final Machine machine) throws ConnectorException {
        VmType vmType = vm.getResource();

        try {
            machine.setName(vmType.getName());
            machine.setDescription(vmType.getDescription());
            machine.setProviderAssignedId(vmType.getHref());
            machine.setState(this.fromvVmStatusToMachineState(vm));
            /*VcdCloudProviderConnectorFactory.logger.info("## vm state=" + vm.getVMStatus() 
                + ", machine state=" + machine.getState() + ", ips=" + vm.getIpAddressesById());*/

            // HW
            machine.setCpu(vm.getCpu().getNoOfCpus());
            machine.setMemory(vm.getMemory().getMemorySize().intValue() * 1024); /*CIMI: kibibytes*/
            List<MachineDisk> machineDisks = new ArrayList<MachineDisk>();
            machine.setDisks(machineDisks);
            for (VirtualDisk disk : vm.getDisks()) {
                if (disk.isHardDisk()) {
                    MachineDisk machineDisk = new MachineDisk();
                    machineDisk.setInitialLocation("");
                    machineDisk.setCapacity(disk.getHardDiskSize().intValue() * 1000); /*CIMI: kilobytes*/
                    machineDisks.add(machineDisk);
                }
            }

            // Network
            List<MachineNetworkInterface> nics = new ArrayList<MachineNetworkInterface>();
            machine.setNetworkInterfaces(nics);
            for (NetworkConnectionType networkConnection : vm.getNetworkConnections()) {
                /*VcdCloudProviderConnectorFactory.logger.info("### vm Ip=" + networkConnection.getIpAddress()
                    + ", vm external Ip=" + networkConnection.getExternalIpAddress() + ", vm allocation mode="
                    + networkConnection.getIpAddressAllocationMode());*/

                String ipAddressAllocationMode = networkConnection.getIpAddressAllocationMode();
                String cimiIpAddressAllocationMode = "";
                if (ipAddressAllocationMode.equalsIgnoreCase("DHCP")) {
                    cimiIpAddressAllocationMode = "dynamic";
                } else if (ipAddressAllocationMode.equalsIgnoreCase("MANUAL")
                    || ipAddressAllocationMode.equalsIgnoreCase("POOL")) {
                    cimiIpAddressAllocationMode = "static";
                }

                Vapp parentVapp = Vapp.getVappByReference(this.vCloudContext.getVcloudClient(), vm.getParentVappReference());
                VappNetwork vappNetwork = this.getVappNetworkByName(parentVapp, networkConnection.getNetwork());
                Network network = new Network();

                /* Mapping Rule
                * If the vAppNetwork is isolated then the mapping is based on the vAppNetwork, 
                * else it is based on the OrgVdcNetwork to which the vAppNetwork is connected */
                if (vappNetwork.getResource().getConfiguration().getFenceMode().equals(FenceModeValuesType.ISOLATED.value())) {
                    this.fromVappNetworkToNetwork(vappNetwork, network);
                } else {
                    /*VAppNetworkConfigurationType vAppNetworkConfigurationType = parentVapp
                        .getVappNetworkConfigurationByName(networkConnection.getNetwork());*/

                    VAppNetworkConfigurationType vAppNetworkConfigurationType = this.getVappNetworkConfigurationByName(
                        parentVapp, networkConnection.getNetwork());
                    OrgVdcNetwork orgVdcNetwork = this.getOrgVdcNetworkByProviderAssignedId(vAppNetworkConfigurationType
                        .getConfiguration().getParentNetwork().getHref());

                    this.fromOrgVdcNetworkToNetwork(orgVdcNetwork, network);
                }

                if (networkConnection.getIpAddress() != null && networkConnection.getIpAddress() != "") {
                    /*Address cimiAddress = new Address();
                    // if CIMI.public && CimiPublicOrgVdcNetworkName && NAT routed 
                    if (network.getNetworkType().equals(Network.Type.PUBLIC)
                        && network.getName().equals(this.vCloudContext.getCimiPublicOrgVdcNetworkName())
                        && this.vCloudContext.isCimiPublicOrgVdcNetworkIsRouted()) {
                        cimiAddress.setIp(this.getNatRoutedIpAddress(networkConnection.getIpAddress()));
                    } else {
                        cimiAddress.setIp(networkConnection.getIpAddress());
                    }
                    cimiAddress.setAllocation(cimiIpAddressAllocationMode);
                    cimiAddress.setProtocol("IPv4");*/

                    List<MachineNetworkInterfaceAddress> cimiAddresses = new ArrayList<MachineNetworkInterfaceAddress>();

                    Address cimiAddress = new Address();
                    cimiAddress.setIp(networkConnection.getIpAddress());
                    cimiAddress.setAllocation(cimiIpAddressAllocationMode);
                    cimiAddress.setProtocol("IPv4");
                    MachineNetworkInterfaceAddress entry = new MachineNetworkInterfaceAddress();
                    entry.setAddress(cimiAddress);
                    cimiAddresses.add(entry);

                    /*if CimiPublicOrgVdcNetworkName && NAT routed*/
                    if (network.getName().equals(this.vCloudContext.getCimiPublicOrgVdcNetworkName())
                        && this.vCloudContext.isCimiPublicOrgVdcNetworkIsRouted()) {
                        Address publicCimiAddress = new Address();
                        publicCimiAddress.setIp(this.getNatRoutedIpAddress(networkConnection.getIpAddress()));
                        publicCimiAddress.setAllocation("dynamic");
                        publicCimiAddress.setProtocol("IPv4");
                        MachineNetworkInterfaceAddress publicEntry = new MachineNetworkInterfaceAddress();
                        publicEntry.setAddress(publicCimiAddress);
                        cimiAddresses.add(publicEntry);
                    }

                    /*if CIMI.public && CimiPublicOrgVdcNetworkName && NAT routed*/
                    /*if (network.getNetworkType().equals(Network.Type.PUBLIC)
                        && network.getName().equals(this.vCloudContext.getCimiPublicOrgVdcNetworkName())
                        && this.vCloudContext.isCimiPublicOrgVdcNetworkIsRouted()) {
                        Address publicCimiAddress = new Address();
                        publicCimiAddress.setIp(this.getNatRoutedIpAddress(networkConnection.getIpAddress()));
                        publicCimiAddress.setAllocation("dynamic");
                        publicCimiAddress.setProtocol("IPv4");
                        MachineNetworkInterfaceAddress publicEntry = new MachineNetworkInterfaceAddress();
                        publicEntry.setAddress(publicCimiAddress);
                        cimiAddresses.add(publicEntry);
                    }*/

                    MachineNetworkInterface privateNic = new MachineNetworkInterface();
                    privateNic.setAddresses(cimiAddresses);
                    privateNic.setState(MachineNetworkInterface.InterfaceState.ACTIVE);
                    privateNic.setNetwork(network);
                    nics.add(privateNic);

                }
            }

        } catch (VCloudException e) {
            throw new ConnectorException(e);
        }
    }

    private String getNatRoutedIpAddress(final String internalIp) throws ConnectorException {
        NatServiceType natService = this.getNatService(); /* FIXME on the fly / initial setup */
        for (NatRuleType natRule : natService.getNatRule()) {
            VcdCloudProvider.logger.info("  NAT rule description " + natRule.getDescription() + "\n  NAT rule getRuleType "
                + natRule.getRuleType() + "\n  NAT rule isIsEnabled " + natRule.isIsEnabled() + "\n  NAT rule getId "
                + natRule.getId());
            if (natRule.getGatewayNatRule() != null && natRule.getRuleType().equalsIgnoreCase("DNAT")) {
                if (internalIp.equals(natRule.getGatewayNatRule().getTranslatedIp())) {
                    VcdCloudProvider.logger.info("GatewayNatRule match:" + natRule.getGatewayNatRule()
                        + "\n   NAT rule getIcmpSubType " + natRule.getGatewayNatRule().getIcmpSubType()
                        + "\n   NAT rule getOriginalIp " + natRule.getGatewayNatRule().getOriginalIp()
                        + "\n   NAT rule getTranslatedIp " + natRule.getGatewayNatRule().getTranslatedIp()
                        + "\n   NAT rule getOriginalPort " + natRule.getGatewayNatRule().getOriginalPort()
                        + "\n   NAT rule getTranslatedPort " + natRule.getGatewayNatRule().getTranslatedPort()
                        + "\n   NAT rule getProtocol " + natRule.getGatewayNatRule().getProtocol()
                        + "\n   NAT rule getInterface " + natRule.getGatewayNatRule().getInterface());
                    return natRule.getGatewayNatRule().getOriginalIp();
                }
            }
        }
        throw new ConnectorException("no GatewayNatRule for: " + internalIp);
    }

    private NatServiceType getNatService() throws ConnectorException {
        for (JAXBElement<? extends NetworkServiceType> jaxbElement : this.vCloudContext.getEdgeGateway().getResource()
            .getConfiguration().getEdgeGatewayServiceConfiguration().getNetworkService()) {
            if (jaxbElement.getValue() instanceof NatServiceType) {
                return (NatServiceType) jaxbElement.getValue();
            }
        }
        throw new ConnectorException("no NatService available");
    }

    public Machine createMachine(final MachineCreate machineCreate) throws ConnectorException {
        VcdCloudProvider.logger.info("creating machine ");
        final int waitTimeInMilliSeconds = VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
        final Map<String, MachineTemplate> machineTemplateMap = new HashMap<String, MachineTemplate>();
        final Machine machine = new Machine();
        Vapp vapp = null;

        try {
            vapp = this.createVapp(machineCreate, machineTemplateMap);

            List<Task> tasks = vapp.getTasks();
            if (tasks.size() > 0) { // wait for all tasks
                tasks.get(0).waitForTask(waitTimeInMilliSeconds);
            }

            // refresh the vapp (otherwise no childrenVms is visible! - TBC)
            vapp = Vapp.getVappByReference(this.vCloudContext.getVcloudClient(), vapp.getReference());
            /*VcdCloudProvider.logger.info("nbr de vms: " + vapp.getChildrenVms().size());
            for (VM childVm : vapp.getChildrenVms()) {
                VcdCloudProvider.logger.info("  vm: " + childVm.getResource().getName());
            }*/
            if (vapp.getChildrenVms().size() != 1) {
                throw new ConnectorException("only one vm is expected!");
            }

            // configure vms
            this.configureVmSections(vapp, machineTemplateMap);

            /*refresh the vapp (otherwise no childrenVms is visible! (TBC if this second refresh is required)*/
            vapp = Vapp.getVappByReference(this.vCloudContext.getVcloudClient(), vapp.getReference());

            if (machineCreate.getMachineTemplate().getInitialState() == null
                || machineCreate.getMachineTemplate().getInitialState() == Machine.State.STARTED) {
                this.startMachine(vapp.getChildrenVms().get(0).getResource().getHref());
            }

            this.fromVmToMachine(vapp.getChildrenVms().get(0), machine);

        } catch (Exception ex) {
            try {
                if (vapp != null) {
                    VcdCloudProvider.logger.info("createMachine failed. Try to delete the created vapp");
                    this.deleteVapp(vapp);
                }
            } catch (VCloudException e) {
            } catch (TimeoutException e) {
            }
            throw new ConnectorException(ex);
        }
        return machine;
    }

    private Vapp createVapp(final MachineCreate machineCreate, final Map<String, MachineTemplate> machineTemplateMap)
        throws VCloudException, ConnectorException, TimeoutException {
        /*if (machineCreate.getName() == null || machineCreate.getName().equals("")) {
            throw new ConnectorException("validation error on field 'machineCreate.name': may not be empty");
        }*/

        ComponentDescriptor component = new ComponentDescriptor();
        component.setComponentQuantity(1);
        component.setComponentType(ComponentType.MACHINE);
        component.setName(machineCreate.getName());
        component.setDescription(machineCreate.getDescription());
        component.setProperties(machineCreate.getProperties());
        component.setComponentTemplate(machineCreate.getMachineTemplate());

        Set<ComponentDescriptor> componentDescriptors = new HashSet<ComponentDescriptor>();
        componentDescriptors.add(component);

        SystemTemplate systemTemplate = new SystemTemplate();
        systemTemplate.setName(machineCreate.getName());
        systemTemplate.setDescription(machineCreate.getDescription());
        systemTemplate.setProperties(new HashMap<String, String>());
        systemTemplate.setComponentDescriptors(componentDescriptors);

        SystemCreate systemCreate = new SystemCreate();
        systemCreate.setName(machineCreate.getName());
        systemCreate.setDescription(machineCreate.getDescription());
        systemCreate.setProperties(new HashMap<String, String>());
        systemCreate.setSystemTemplate(systemTemplate);

        return this.createVapp(systemCreate, machineTemplateMap);
    }

    public void deleteMachine(final String machineId) throws ConnectorException {
        // !!!! This method should only be used with isolated machine !!!!
        final int waitTimeInMilliSeconds = VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
        VcdCloudProvider.logger.info("deleting machine with providerAssignedId " + machineId);
        VM vm = this.getVmByProviderAssignedId(machineId);

        try {
            Vapp vapp = Vapp.getVappByReference(VcdCloudProvider.this.vCloudContext.getVcloudClient(),
                vm.getParentVappReference());
            /*check if the vapp has one and only one vm otherwise it is not an isolated machine!*/
            if (vapp.getChildrenVms().size() != 1) {
                throw new ConnectorException("only one vm is expected!");
            }
            if (vapp.isDeployed()) {
                VcdCloudProvider.logger.info("Undeploying " + vapp.getResource().getName());
                if (vapp.getVappStatus() == VappStatus.POWERED_ON) {
                    vapp.undeploy(UndeployPowerActionType.POWEROFF).waitForTask(waitTimeInMilliSeconds);
                } else {
                    vapp.undeploy(UndeployPowerActionType.DEFAULT).waitForTask(waitTimeInMilliSeconds);
                }
            }
            VcdCloudProvider.logger.info("deleting " + vapp.getResource().getName());
            // vapp.delete().waitForTask(waitTimeInMilliSeconds);
            vapp.delete();
        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    public void startMachine(final String machineId) throws ConnectorException {
        VcdCloudProvider.logger.info("starting machine with providerAssignedId " + machineId);
        VM vm = this.getVmByProviderAssignedId(machineId);

        try {
            VcdCloudProvider.logger.info("powerOn " + vm.getResource().getName());
            // vm.powerOn().waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
            vm.powerOn();
        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    public void stopMachine(final String machineId, final boolean force) throws ConnectorException {
        VcdCloudProvider.logger.info("stopping machine with providerAssignedId " + machineId);
        VM vm = this.getVmByProviderAssignedId(machineId);

        try {
            if (force) {
                VcdCloudProvider.logger.info("powerOff " + vm.getResource().getName());
                // vm.powerOff().waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                vm.powerOff();
            } else {
                VcdCloudProvider.logger.info("shutdown " + vm.getResource().getName());
                // vm.shutdown().waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                vm.shutdown();
            }
        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    public void suspendMachine(final String machineId) throws ConnectorException {
        VcdCloudProvider.logger.info("suspending machine with providerAssignedId " + machineId);
        VM vm = this.getVmByProviderAssignedId(machineId);

        try {
            VcdCloudProvider.logger.info("suspend " + vm.getResource().getName());
            // vm.suspend().waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
            vm.suspend();
        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    public void restartMachine(final String machineId, final boolean force) throws ConnectorException {
        VcdCloudProvider.logger.info("restarting machine with providerAssignedId " + machineId);
        VM vm = this.getVmByProviderAssignedId(machineId);

        try {
            if (force) {
                VcdCloudProvider.logger.info("reset " + vm.getResource().getName());
                // vm.reset().waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                vm.reset();
            } else {
                VcdCloudProvider.logger.info("reboot " + vm.getResource().getName());
                vm.reboot().waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                vm.reboot();
            }
        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    public Machine.State getMachineState(final String machineId) throws ConnectorException {
        VM vm = this.getVmByProviderAssignedId(machineId);
        return this.fromvVmStatusToMachineState(vm);
    }

    public Machine getMachine(final String machineId) throws ConnectorException {
        VM vm = this.getVmByProviderAssignedId(machineId);
        Machine machine = new Machine();
        this.fromVmToMachine(vm, machine);
        return machine;
    }

    //
    // Network Service
    //

    private void fromVAppNetworkToSystemNetwork(final VappNetwork vappNetwork, final SystemNetwork sn) {
        sn.setState(SystemNetwork.State.AVAILABLE);
        Network network = new Network();
        this.fromVappNetworkToNetwork(vappNetwork, network);
        sn.setResource(network);
    }

    private void fromVappNetworkToNetwork(final VappNetwork vappNetwork, final Network n) {
        ReferenceType vappNetworkReferenceType = vappNetwork.getReference();
        n.setName(vappNetworkReferenceType.getName());
        n.setProviderAssignedId(vappNetworkReferenceType.getHref());
        n.setState(Network.State.STARTED);

        // Mapping Rule
        /* If the vAppNetwork is not isolated and its name = the CIMI public network configuration parameter 
         * then the CIMI network is set to PUBLIC else to PRIVATE */
        /* Applying this rule with a vApp created outside Sirocco might not work properly ! 
         * A workaround in this case is to use the OrgVdcNetwork to which the vAppNetwork is connected */
        /*if (!vappNetwork.getResource().getConfiguration().getFenceMode().equals(FenceModeValuesType.ISOLATED.value())
            && vappNetworkReferenceType.getName().equals(this.vCloudContext.getCimiPublicOrgVdcNetworkName())) {
            n.setNetworkType(Network.Type.PUBLIC);
        } else {
            n.setNetworkType(Network.Type.PRIVATE);
        }*/

        // subnet
        List<Subnet> subnets = new ArrayList<Subnet>();
        n.setSubnets(subnets);
        /*SubnetUtils utils = new SubnetUtils(vappNetwork.getResource().getConfiguration().getIpScope().getGateway(), vappNetwork
            .getResource().getConfiguration().getIpScope().getNetmask());*/
        SubnetUtils utils = new SubnetUtils(vappNetwork.getResource().getConfiguration().getIpScopes().getIpScope().get(0)
            .getGateway(), vappNetwork.getResource().getConfiguration().getIpScopes().getIpScope().get(0).getNetmask());
        utils = new SubnetUtils(utils.getInfo().getNetworkAddress(), utils.getInfo().getNetmask());
        utils.setInclusiveHostCount(false);
        SubnetInfo info = utils.getInfo();
        Subnet subnet = new Subnet();
        subnet.setCidr(info.getCidrSignature());
        // subnet.setName(????);
        // subnet.setEnableDhcp(???);
        // subnet.setProviderAssignedId(???);
        subnet.setState(Subnet.State.AVAILABLE);
        subnet.setProtocol("IPv4");
        subnets.add(subnet);
    }

    private void fromOrgVdcNetworkToNetwork(final OrgVdcNetwork orgVdcNetwork, final Network n) {
        ReferenceType orgVdcNetworkReferenceType = orgVdcNetwork.getReference();
        n.setName(orgVdcNetworkReferenceType.getName());
        n.setProviderAssignedId(orgVdcNetworkReferenceType.getHref());
        n.setState(Network.State.STARTED);

        // Mapping Rule
        /* If the orgVdcNetwork name = the CIMI public network configuration parameter 
         * then the CIMI network is set to PUBLIC else to PRIVATE */
        /*if (orgVdcNetworkReferenceType.getName().equals(this.vCloudContext.getCimiPublicOrgVdcNetworkName())) {
            n.setNetworkType(Network.Type.PUBLIC);
        } else {
            n.setNetworkType(Network.Type.PRIVATE);
        }*/

        // subnet
        List<Subnet> subnets = new ArrayList<Subnet>();
        n.setSubnets(subnets);
        /*SubnetUtils utils = new SubnetUtils(orgVdcNetwork.getResource().getConfiguration().getIpScope().getGateway(), orgVdcNetwork
            .getResource().getConfiguration().getIpScope().getNetmask());*/
        SubnetUtils utils = new SubnetUtils(orgVdcNetwork.getResource().getConfiguration().getIpScopes().getIpScope().get(0)
            .getGateway(), orgVdcNetwork.getResource().getConfiguration().getIpScopes().getIpScope().get(0).getNetmask());
        utils = new SubnetUtils(utils.getInfo().getNetworkAddress(), utils.getInfo().getNetmask());
        utils.setInclusiveHostCount(false);
        SubnetInfo info = utils.getInfo();
        Subnet subnet = new Subnet();
        subnet.setCidr(info.getCidrSignature());
        // subnet.setName(????);
        // subnet.setEnableDhcp(???);
        // subnet.setProviderAssignedId(???);
        subnet.setState(Subnet.State.AVAILABLE);
        subnet.setProtocol("IPv4");
        subnets.add(subnet);
    }

    public List<Network> getNetworks() throws ConnectorException {
        ArrayList<Network> networks = new ArrayList<Network>();
        // networks.add(this.vCloudContext.getCimiPublicNetwork());

        for (ReferenceType orgVdcNetworkRefType : this.vCloudContext.getVdc().getAvailableNetworkRefs()) {
            networks.add(this.getNetwork(orgVdcNetworkRefType.getHref()));
        }

        return networks;
    }

    public Network getNetwork(final String networkId) throws ConnectorException, ResourceNotFoundException {
        final Network network = new Network();
        OrgVdcNetwork orgVdcNetwork = this.getOrgVdcNetworkByProviderAssignedId(networkId);
        this.fromOrgVdcNetworkToNetwork(orgVdcNetwork, network);
        return network;
    }

    //
    // VCD
    //

    private Vapp getVappByProviderAssignedId(final String id) throws ConnectorException, ResourceNotFoundException {
        try {
            ReferenceType vAppRef = new ReferenceType();
            vAppRef.setHref(id);
            return Vapp.getVappByReference(this.vCloudContext.getVcloudClient(), vAppRef);
        } catch (VCloudException e) { /*mapping to ResourceNotFoundException!*/
            /*VcdCloudProvider.logger.info("VCloudException: " + e.getVcloudError() + ", "
            + e.getVcloudError().getMajorErrorCode() + ", " + e.getVcloudError().getMinorErrorCode() + ", "
            + e.getVcloudError().getVendorSpecificErrorCode() + ", " + e.getVcloudError().getOtherAttributes());*/
            throw new ResourceNotFoundException(e);
        }
    }

    private VM getVmByProviderAssignedId(final String id) throws ConnectorException, ResourceNotFoundException {
        try {
            ReferenceType vmRef = new ReferenceType();
            vmRef.setHref(id);
            return VM.getVMByReference(this.vCloudContext.getVcloudClient(), vmRef);
        } catch (VCloudException e) { /*mapping to ResourceNotFoundException!*/
            /*VcdCloudProvider.logger.info("VCloudException: " + e.getVcloudError() + ", "
                + e.getVcloudError().getMajorErrorCode() + ", " + e.getVcloudError().getMinorErrorCode() + ", "
                + e.getVcloudError().getVendorSpecificErrorCode() + ", " + e.getVcloudError().getOtherAttributes());*/
            throw new ResourceNotFoundException(e);
        }
    }

    private VappNetwork getVappNetworkByProviderAssignedId(final String id) throws ConnectorException,
        ResourceNotFoundException {
        try {
            ReferenceType vappNetworkRef = new ReferenceType();
            vappNetworkRef.setHref(id);
            return VappNetwork.getVappNetworkByReference(this.vCloudContext.getVcloudClient(), vappNetworkRef);
        } catch (VCloudException e) {
            throw new ResourceNotFoundException(e);
        }
    }

    private VappNetwork getVappNetworkByName(final Vapp vapp, final String vAppNetworkName) throws ConnectorException,
        ResourceNotFoundException {
        try {
            ReferenceType vappNetworkReferenceType = vapp.getVappNetworkRefsByName().get(vAppNetworkName);
            VappNetwork vappNetwork = VappNetwork.getVappNetworkByReference(this.vCloudContext.getVcloudClient(),
                vappNetworkReferenceType);
            return vappNetwork;
        } catch (VCloudException e) {
            throw new ResourceNotFoundException(e);
        }
    }

    private VAppNetworkConfigurationType getVappNetworkConfigurationByName(final Vapp vapp, final String vAppNetworkName)
        throws ResourceNotFoundException {
        try {
            VAppNetworkConfigurationType vAppNetworkConfigurationType = vapp.getVappNetworkConfigurationByName(vAppNetworkName);
            return vAppNetworkConfigurationType;
        } catch (VCloudException e) {
            throw new ResourceNotFoundException(e);
        }
    }

    private OrgVdcNetwork getOrgVdcNetworkByProviderAssignedId(final String id) throws ConnectorException,
        ResourceNotFoundException {
        try {
            ReferenceType orgVdcNetworkRef = new ReferenceType();
            orgVdcNetworkRef.setHref(id);
            return OrgVdcNetwork.getOrgVdcNetworkByReference(this.vCloudContext.getVcloudClient(), orgVdcNetworkRef);
        } catch (VCloudException e) {
            throw new ResourceNotFoundException(e);
        }
    }

    private Vapp createVapp(final SystemCreate systemCreate, final Map<String, MachineTemplate> machineTemplateMap)
        throws VCloudException, ConnectorException, TimeoutException {

        // create the request body (ComposeVAppParamsType)
        ComposeVAppParamsType composeVAppParamsType = new ComposeVAppParamsType();
        composeVAppParamsType.setDeploy(false);
        String systemName = systemCreate.getName();
        if (systemName == null || systemName.equals("")) {
            systemName = "sirocco";
        }
        composeVAppParamsType.setName(systemName);
        composeVAppParamsType.setDescription(systemCreate.getDescription());
        // set default vApp instantiation parameters
        InstantiationParamsType instantiationParamsType = this.createVappInstantiationParamsType(systemCreate);
        composeVAppParamsType.setInstantiationParams(instantiationParamsType);
        // startup, lease... sections (vs CIMI?)

        // source items (VMs)
        Set<ComponentDescriptor> machineComponentDescriptors = this.getComponentDescriptorsOfType(systemCreate,
            ComponentType.MACHINE);
        for (ComponentDescriptor mcd : machineComponentDescriptors) {
            MachineTemplate mt = (MachineTemplate) mcd.getComponentTemplate();
            for (int i = 0; i < mcd.getComponentQuantity(); i++) {
                // create sourceItem body (SourcedCompositionItemParamType)
                SourcedCompositionItemParamType item = new SourcedCompositionItemParamType();
                ReferenceType source = new ReferenceType();
                String name = (mcd.getName() == null || mcd.getName().equals("")) ? "sirocco" : mcd.getName();
                if (mcd.getComponentQuantity() > 1) {
                    name += new Integer(i + 1).toString();
                }
                source.setName(name);
                // associate machine instances with templates
                machineTemplateMap.put(name, mt);
                // set Href
                ProviderMapping mapping = ProviderMapping.find(mt.getMachineImage(), this.cloudProviderAccount,
                    this.cloudProviderLocation);
                if (mapping == null) {
                    throw new ConnectorException("Cannot find imageId for image " + mt.getMachineImage().getName());
                }
                String vmTmplRef = mapping.getProviderAssignedId();

                source.setHref(vmTmplRef);
                item.setSource(source);

                // Configure connection
                /* remove unused CIMI network ? ; set nic index (vs.CIMI ?)*/
                NetworkConnectionSectionType networkConnectionSectionType = new NetworkConnectionSectionType();
                networkConnectionSectionType.setInfo(new MsgType());
                int networkConnectionIndex = 0;
                for (MachineTemplateNetworkInterface nic : mt.getNetworkInterfaces()) {
                    NetworkConnectionType networkConnectionType = new NetworkConnectionType();
                    if (nic.getNetwork() != null) {
                        /*if (nic.getNetwork().getNetworkType() == Network.Type.PUBLIC) {
                            networkConnectionType.setNetwork(this.vCloudContext.getCimiPublicOrgVdcNetworkName());
                        } else {
                            VappNetwork vappNetwork = this.getVappNetworkByProviderAssignedId(nic.getNetwork()
                                .getProviderAssignedId());
                            networkConnectionType.setNetwork(vappNetwork.getResource().getName());
                        }*/
                        OrgVdcNetwork orgVdcNetwork = this.getOrgVdcNetworkByProviderAssignedId(nic.getNetwork()
                            .getProviderAssignedId());
                        networkConnectionType.setNetwork(orgVdcNetwork.getResource().getName());
                    } else if (nic.getSystemNetworkName() != null) {
                        networkConnectionType.setNetwork(nic.getSystemNetworkName());
                    } else {
                        throw new ConnectorException(
                            "validation error on nic template : should refer either to a Network resource xor a SystemNetworkName");
                    }
                    networkConnectionType.setIpAddressAllocationMode(IpAddressAllocationModeType.POOL.value());
                    networkConnectionType.setIsConnected(true);
                    networkConnectionType.setNetworkConnectionIndex(networkConnectionIndex++);
                    networkConnectionSectionType.getNetworkConnection().add(networkConnectionType);
                }
                InstantiationParamsType vmInstantiationParamsType = new InstantiationParamsType();
                List<JAXBElement<? extends SectionType>> vmSections = vmInstantiationParamsType.getSection();
                vmSections.add(new ObjectFactory().createNetworkConnectionSection(networkConnectionSectionType));
                item.setInstantiationParams(vmInstantiationParamsType);

                composeVAppParamsType.getSourcedItem().add(item);
            }
        }

        // make the composition request, and get a vApp in return
        return this.vCloudContext.getVdc().composeVapp(composeVAppParamsType);
    }

    private Set<ComponentDescriptor> getComponentDescriptorsOfType(final SystemCreate systemCreate, final ComponentType cType) {
        Set<ComponentDescriptor> componentDescriptors = new HashSet<ComponentDescriptor>();
        for (ComponentDescriptor cd : systemCreate.getSystemTemplate().getComponentDescriptors()) {
            if (cd.getComponentType() == cType) {
                componentDescriptors.add(cd);
            }
        }
        return componentDescriptors;
    }

    private InstantiationParamsType createVappInstantiationParamsType(final SystemCreate systemCreate)
        throws ConnectorException {
        NetworkConfigSectionType networkConfigSectionType = new NetworkConfigSectionType();
        networkConfigSectionType.setInfo(new MsgType());
        List<VAppNetworkConfigurationType> vAppNetworkConfigs = networkConfigSectionType.getNetworkConfig();

        // add bridged vAppNetworks
        Set<String> bridgedVAppNetworks = new HashSet<String>();
        for (ComponentDescriptor mcd : this.getComponentDescriptorsOfType(systemCreate, ComponentType.MACHINE)) {
            MachineTemplate mt = (MachineTemplate) mcd.getComponentTemplate();
            for (MachineTemplateNetworkInterface nic : mt.getNetworkInterfaces()) {
                if (nic.getNetwork() != null) {
                    OrgVdcNetwork orgVdcNetwork = this.getOrgVdcNetworkByProviderAssignedId(nic.getNetwork()
                        .getProviderAssignedId());
                    String orgVdcNetworkName = orgVdcNetwork.getResource().getName();
                    if (bridgedVAppNetworks.contains(orgVdcNetworkName)) {
                        // do not duplicate
                        continue;
                    }
                    bridgedVAppNetworks.add(orgVdcNetworkName);
                    VAppNetworkConfigurationType vAppNetworkConfigurationType = this
                        .createBridgedVAppNetworkConfigurationType(orgVdcNetwork);
                    // fill in the NetworkConfigSection
                    vAppNetworkConfigs.add(vAppNetworkConfigurationType);
                }
            }

        }

        // add isolated vAppNetworks
        for (ComponentDescriptor ncd : this.getComponentDescriptorsOfType(systemCreate, ComponentType.NETWORK)) {
            NetworkTemplate nt = (NetworkTemplate) ncd.getComponentTemplate();
            if (ncd.getComponentQuantity() != 1) {
                throw new ConnectorException(
                    "validation error on field 'Network componentDescriptor.quantity': should be equal to 1");
            }
            /*if (nt.getNetworkConfig().getNetworkType() != Network.Type.PRIVATE) {
                throw new ConnectorException(
                    "validation error on field 'Network componentDescriptor.networkTemplate.networkConfiguration.networkType': should be equal to Private");
            }*/
            VAppNetworkConfigurationType private_vAppNetworkConfigurationType = this
                .createIsolatedVAppNetworkConfigurationType(ncd.getName(), nt.getNetworkConfig());
            // fill in the NetworkConfigSection
            vAppNetworkConfigs.add(private_vAppNetworkConfigurationType);
        }

        // fill in and return InstantiationParams
        InstantiationParamsType instantiationParamsType = new InstantiationParamsType();
        List<JAXBElement<? extends SectionType>> sections = instantiationParamsType.getSection();
        sections.add(new ObjectFactory().createNetworkConfigSection(networkConfigSectionType));
        return instantiationParamsType;
    }

    VAppNetworkConfigurationType createBridgedVAppNetworkConfigurationType(final OrgVdcNetwork parentOrgVdcNetwork) {
        VAppNetworkConfigurationType vAppNetworkConfigurationType = new VAppNetworkConfigurationType();
        NetworkConfigurationType networkConfigurationType = new NetworkConfigurationType();
        String networkName = parentOrgVdcNetwork.getResource().getName();
        VcdCloudProvider.logger.info("vAppNetworkConfiguration Bridged:" + networkName);
        vAppNetworkConfigurationType.setNetworkName(networkName);
        networkConfigurationType.setParentNetwork(parentOrgVdcNetwork.getReference());
        /*networkConfigurationType.setParentNetwork(this.vCloudContext.getVdc().getAvailableNetworkRefByName(
            networkName));*/
        networkConfigurationType.setFenceMode(FenceModeValuesType.BRIDGED.value());
        networkConfigurationType.setRetainNetInfoAcrossDeployments(true);
        vAppNetworkConfigurationType.setConfiguration(networkConfigurationType);
        return vAppNetworkConfigurationType;
    }

    VAppNetworkConfigurationType createIsolatedVAppNetworkConfigurationType(final String networkName,
        final NetworkConfiguration networkConfiguration) throws ConnectorException {
        VAppNetworkConfigurationType private_vAppNetworkConfigurationType = new VAppNetworkConfigurationType();
        NetworkConfigurationType private_networkConfigurationType = new NetworkConfigurationType();
        VcdCloudProvider.logger.info("vAppNetworkConfiguration Isolated:" + networkName);
        private_vAppNetworkConfigurationType.setNetworkName(networkName);
        // private_networkConfigurationType.setParentNetwork(vdc.getAvailableNetworkRefByName(this.cimiPublicOrgVdcNetworkName));
        private_networkConfigurationType.setFenceMode(FenceModeValuesType.ISOLATED.value());
        private_networkConfigurationType.setRetainNetInfoAcrossDeployments(true);

        // Configure Internal IP Settings
        if (networkConfiguration.getSubnets().size() != 1) {
            throw new ConnectorException("validation error on field 'networkConfiguration.subnets.size': should be equal to 1");
        }
        SubnetUtils utils = new SubnetUtils(networkConfiguration.getSubnets().get(0).getCidr());
        utils.setInclusiveHostCount(false);
        SubnetInfo info = utils.getInfo();
        if (info.getAddressCount() < 2) { /* gateway @ + IP range @ >= 2 */
            throw new ConnectorException("no usable addresses");
        }

        IpScopeType ipScope = new IpScopeType();
        /*ipScope.setNetmask("255.255.255.0");
        ipScope.setGateway("192.168.2.1");*/
        ipScope.setNetmask(info.getNetmask());
        ipScope.setGateway(info.getLowAddress());
        ipScope.setIsEnabled(true);
        ipScope.setIsInherited(false); // ???

        IpRangesType ipRangesType = new IpRangesType();
        IpRangeType ipRangeType = new IpRangeType();
        /*ipRangeType.setStartAddress("192.168.2.100");
        ipRangeType.setEndAddress("192.168.2.199");*/
        ipRangeType.setStartAddress(info.getAllAddresses()[1]);
        ipRangeType.setEndAddress(info.getHighAddress());

        ipRangesType.getIpRange().add(ipRangeType);

        ipScope.setIpRanges(ipRangesType);
        ipScope.setIsEnabled(true);
        IpScopesType ipScopes = new IpScopesType();
        ipScopes.getIpScope().add(ipScope);
        private_networkConfigurationType.setIpScopes(ipScopes);

        private_vAppNetworkConfigurationType.setConfiguration(private_networkConfigurationType);
        return private_vAppNetworkConfigurationType;
    }

    private void configureVmSections(Vapp vapp, final Map<String, MachineTemplate> machineTemplateMap) throws VCloudException,
        TimeoutException, ConnectorException {
        // refresh the vapp (TBC)
        vapp = Vapp.getVappByReference(this.vCloudContext.getVcloudClient(), vapp.getReference());

        // set virtual hardware
        VcdCloudProvider.logger.info("Configuring virtual hardware");
        this.configureVirtualHardware(vapp, machineTemplateMap);

        // set IPs
        /*logger.info("Configuring VM Ip Addressing Mode");
        configureVMsDefaultIPAddressingMode(vapp);*/
        // configureVMsNatIPAddressingMode(vapp);

        // set user data
        VcdCloudProvider.logger.info("Configuring user data");
        this.configureUserData(vapp, machineTemplateMap);

        // set guest customization
        VcdCloudProvider.logger.info("Configuring guest customization");
        this.configureGuestCustomization(vapp);

        // configure other vm sections (vs CIMI?)
    }

    private void configureVirtualHardware(final Vapp vapp, final Map<String, MachineTemplate> machineTemplateMap)
        throws VCloudException, TimeoutException {
        for (VM childVm : vapp.getChildrenVms()) {
            MachineTemplate mt = machineTemplateMap.get(childVm.getResource().getName());
            MachineConfiguration mc = mt.getMachineConfig();
            if (mc == null) {
                continue;
            }

            /*VirtualHardwareSectionType virtualHardwareSectionType = childVm.getVirtualHardwareSection();
            if (virtualHardwareSectionType == null) {
                virtualHardwareSectionType = new VirtualHardwareSectionType();
            }

            for (RASDType item : virtualHardwareSectionType.getItem()) {
                int type = Integer.parseInt(item.getResourceType().getValue());
                VcdCloudProviderConnectorFactory.logger.info("- virtualHardwareItemType: " + type + ", "
                    + item.getDescription().getValue());
            }*/

            // configure CPU if needed
            VcdCloudProvider.logger.info("  Number of Virtual CPUs: " + childVm.getCpu().getNoOfCpus());
            VirtualCpu virtualCpuItem = childVm.getCpu();
            if (mc.getCpu() > 0) {
                if (virtualCpuItem.getNoOfCpus() != mc.getCpu()) {
                    VcdCloudProvider.logger.info("  -> updating: " + mc.getCpu() + " Virtual CPUs");
                    virtualCpuItem.setNoOfCpus(mc.getCpu());
                    childVm.updateCpu(virtualCpuItem).waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                }
            }

            // configure RAM if needed
            if (mc.getMemory() > 0) {
                VcdCloudProvider.logger.info("  Memory Size: " + childVm.getMemory().getMemorySize() + "MB");
                long memoryInMBytes = mc.getMemory() / 1024; /* CIMI: kibibytes*/
                VirtualMemory virtualMemoryItemInMBytes = childVm.getMemory();
                if (virtualMemoryItemInMBytes.getMemorySize().longValue() != memoryInMBytes) {
                    VcdCloudProvider.logger.info("  -> updating: " + memoryInMBytes + " MB");
                    virtualMemoryItemInMBytes.setMemorySize(BigInteger.valueOf(memoryInMBytes));
                    childVm.updateMemory(virtualMemoryItemInMBytes).waitForTask(
                        VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                }
            }

            // Add virtual disk if needed
            // FIXME policy (add/update disks)
            List<VirtualDisk> disks = childVm.getDisks();
            boolean diskSectionHasChanged = false;
            for (DiskTemplate disk : mc.getDisks()) {
                long diskInMBytes = disk.getCapacity() / 1000; /*CIMI: kilobytes*/
                if (diskInMBytes < 1) {
                    diskInMBytes = 1;
                }
                VcdCloudProvider.logger.info("  Add New Disk: " + diskInMBytes + " MB, LsiLogic");
                CimString cimString = new CimString();
                Map<QName, String> cimAttributes = cimString.getOtherAttributes();
                cimAttributes.put(new QName("http://www.vmware.com/vcloud/v1.5", "busSubType", "vcloud"), "lsilogic");
                cimAttributes.put(new QName("http://www.vmware.com/vcloud/v1.5", "busType", "vcloud"), "6");
                cimAttributes.put(new QName("http://www.vmware.com/vcloud/v1.5", "capacity", "vcloud"),
                    String.valueOf(diskInMBytes));

                CimString setElementName = new CimString();
                setElementName.setValue("Hard disk");
                CimString setInstanceID = new CimString();
                setInstanceID.setValue("anything");
                ResourceType setResourceType = new ResourceType();
                setResourceType.setValue(String.valueOf(Constants.RASD_RESOURCETYPE_DISK_DEVICE));

                RASDType diskItemType = new RASDType();
                diskItemType.setElementName(setElementName);
                diskItemType.setInstanceID(setInstanceID);
                diskItemType.setResourceType(setResourceType);
                List<CimString> diskAttributes = diskItemType.getHostResource();
                diskAttributes.add(cimString);

                disks.add(new VirtualDisk(diskItemType));
                diskSectionHasChanged = true;
            }
            if (diskSectionHasChanged) {
                childVm.updateDisks(disks).waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
            }
        }
    }

    private void configureUserData(final Vapp vapp, final Map<String, MachineTemplate> machineTemplateMap)
        throws VCloudException, TimeoutException {

        MsgType msgType = new MsgType();
        msgType.setValue("user Data");

        for (VM childVm : vapp.getChildrenVms()) {
            MachineTemplate mt = machineTemplateMap.get(childVm.getResource().getName());
            String userData = mt.getUserData();
            if (userData == null) {
                continue;
            }

            ProductSectionProperty myProp = new ProductSectionProperty();
            myProp.setUserConfigurable(true);
            myProp.setKey("userData");
            myProp.setValueAttrib(userData);
            myProp.setType("string");
            myProp.setLabel(msgType);
            myProp.setDescription(msgType);

            ProductSectionType ovfEnvSection = new ProductSectionType();
            ovfEnvSection.setInfo(msgType);
            ovfEnvSection.setProduct(msgType);
            ovfEnvSection.setClazz("");
            ovfEnvSection.setRequired(true);
            ovfEnvSection.getCategoryOrProperty().add(myProp);

            List<ProductSectionType> productSections = childVm.getProductSections();
            productSections.add(ovfEnvSection);

            childVm.updateProductSections(productSections).waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
        }
    }

    private void configureGuestCustomization(final Vapp vapp) throws VCloudException, TimeoutException {
        for (VM childVm : vapp.getChildrenVms()) {
            GuestCustomizationSectionType guestCustomizationSection = childVm.getGuestCustomizationSection();
            guestCustomizationSection.setEnabled(true);
            childVm.updateSection(guestCustomizationSection).waitForTask(0);
        }
    }

    //
    // TMP
    //

    private void logAdminOrgVdcNetwork() throws VCloudException, ConnectorException {
        // require orgAdmin role
        ReferenceType adminOrgRef = this.vCloudContext.getVcloudClient().getVcloudAdmin()
            .getAdminOrgRefByName(this.vCloudContext.getOrgName());
        AdminOrganization adminOrg = AdminOrganization
            .getAdminOrgByReference(this.vCloudContext.getVcloudClient(), adminOrgRef);
        AdminOrgVdcNetwork adminOrgVdcNetwork = null;
        // ---- get the OrgVdcNetwork (VDC 1.5)
        /*ReferenceType adminOrgVdcNetworkRef = adminOrg.getAdminOrgNetworkRefByName(this.orgVdcNetworkName);
        adminOrgVdcNetwork = AdminOrgVdcNetwork.getOrgVdcNetworkByReference(this.vcloudClient, adminOrgVdcNetworkRef);*/
        // ---- get the OrgVdcNetwork (query)
        /*QueryParams<QueryReferenceField> params = new QueryParams<QueryReferenceField>();
        Filter filter = new Filter(new Expression(QueryReferenceField.NAME, this.orgVdcNetworkName, ExpressionType.EQUALS));
        params.setFilter(filter);
        ReferenceResult result = this.vcloudClient.getQueryService().queryReferences(QueryReferenceType.ADMINORGNETWORK,
            params);
        if (result.getReferences().size() == 0) {
            throw new ConnectorException("No OrgVdcNetwork : " + this.orgVdcNetworkName);
        }
        adminOrgVdcNetwork = AdminOrgVdcNetwork.getOrgVdcNetworkById(this.vcloudClient, result.getReferences().get(0)
            .getId());*/
        // ---- get the OrgVdcNetwork (VDC 5.1)
        ReferenceType adminVdcRef = adminOrg.getAdminVdcRefByName(this.vCloudContext.getVdcName());
        AdminVdc adminVdc2 = AdminVdc.getAdminVdcByReference(this.vCloudContext.getVcloudClient(), adminVdcRef); // !!!
        for (ReferenceType adminOrgVdcNetworkRef : adminVdc2.getOrgVdcNetworkRefs().getReferences()) {
            AdminOrgVdcNetwork adminOrgVdcNetwork2 = AdminOrgVdcNetwork.getOrgVdcNetworkByReference(
                this.vCloudContext.getVcloudClient(), adminOrgVdcNetworkRef);
            VcdCloudProvider.logger.info("adminOrgVdcNetwork2 name=" + adminOrgVdcNetwork2.getResource().getName());
            if (adminOrgVdcNetwork2.getResource().getName().equals(this.vCloudContext.getCimiPublicOrgVdcNetworkName())) {
                adminOrgVdcNetwork = adminOrgVdcNetwork2;
            }
        }
        if (adminOrgVdcNetwork == null) {
            throw new ConnectorException("No OrgVdcNetwork : " + this.vCloudContext.getCimiPublicOrgVdcNetworkName());
        }

        VcdCloudProvider.logger.info("adminOrgVdcNetwork name=" + adminOrgVdcNetwork.getResource().getName());
        if (adminOrgVdcNetwork.getResource().getConfiguration() != null) {
            VcdCloudProvider.logger.info("adminOrgVdcNetwork has Configuration");
            if (adminOrgVdcNetwork.getResource().getConfiguration().getFeatures() != null) {
                VcdCloudProvider.logger.info("adminOrgVdcNetwork Configuration has features");
                for (JAXBElement<? extends NetworkServiceType> jaxbElement : adminOrgVdcNetwork.getResource()
                    .getConfiguration().getFeatures().getNetworkService()) {
                    this.logNetworkService(jaxbElement);
                }
            }
        }

        // ---
        /*VcloudClient.setLogLevel(Level.INFO);
        ReferenceType adminVdcRef = adminOrg.getAdminVdcRefByName(vdcName);
        AdminVdc adminVdc = AdminVdc.getAdminVdcByReference(this.vcloudClient, adminVdcRef);*/
    }

    private void logEdgeGatewayByQuery() throws VCloudException, ConnectorException {
        QueryParams<QueryReferenceField> params = new QueryParams<QueryReferenceField>();
        Filter filter = new Filter(new Expression(QueryReferenceField.NAME, this.vCloudContext.getEdgeGatewayName(),
            ExpressionType.EQUALS));
        params.setFilter(filter);
        ReferenceResult result = this.vCloudContext.getVcloudClient().getQueryService()
            .queryReferences(QueryReferenceType.EDGEGATEWAY, params);
        if (result.getReferences().size() == 0) {
            throw new ConnectorException("No edgeGateway : " + this.vCloudContext.getEdgeGatewayName());
        }
        EdgeGateway edgeGateway = EdgeGateway.getEdgeGatewayByReference(this.vCloudContext.getVcloudClient(), result
            .getReferences().get(0));
        VcdCloudProvider.logger.info("edgeGateway name=" + edgeGateway.getResource().getName());

        if (edgeGateway.getResource().getConfiguration() != null) {
            VcdCloudProvider.logger.info("edgeGateway has Configuration");
            if (edgeGateway.getResource().getConfiguration().getEdgeGatewayServiceConfiguration() != null) {
                VcdCloudProvider.logger.info("edgeGateway Configuration has features");
                for (JAXBElement<? extends NetworkServiceType> jaxbElement : edgeGateway.getResource().getConfiguration()
                    .getEdgeGatewayServiceConfiguration().getNetworkService()) {
                    this.logNetworkService(jaxbElement);
                }
            }
        }
    }

    private void logEdgeGateway() throws VCloudException, ConnectorException {
        // require orgAdmin role
        ReferenceType adminOrgRef = this.vCloudContext.getVcloudClient().getVcloudAdmin()
            .getAdminOrgRefByName(this.vCloudContext.getOrgName());
        AdminOrganization adminOrg = AdminOrganization
            .getAdminOrgByReference(this.vCloudContext.getVcloudClient(), adminOrgRef);
        ReferenceType adminVdcRef = adminOrg.getAdminVdcRefByName(this.vCloudContext.getVdcName());
        AdminVdc adminVdc2 = AdminVdc.getAdminVdcByReference(this.vCloudContext.getVcloudClient(), adminVdcRef); // !!!
        EdgeGateway edgeGateway = null;
        for (ReferenceType edgeGatewayRef : adminVdc2.getEdgeGatewayRefs().getReferences()) {
            EdgeGateway edgeGateway2 = EdgeGateway.getEdgeGatewayByReference(this.vCloudContext.getVcloudClient(),
                edgeGatewayRef);
            VcdCloudProvider.logger.info("edgeGateway name=" + edgeGateway2.getResource().getName());
            if (edgeGateway2.getResource().getName().equals(this.vCloudContext.getEdgeGatewayName())) {
                edgeGateway = edgeGateway2;
            }
        }
        if (edgeGateway == null) {
            throw new ConnectorException("No edgeGateway : " + this.vCloudContext.getEdgeGatewayName());
        }

        if (edgeGateway.getResource().getConfiguration() != null) {
            VcdCloudProvider.logger.info("edgeGateway has Configuration");
            if (edgeGateway.getResource().getConfiguration().getEdgeGatewayServiceConfiguration() != null) {
                VcdCloudProvider.logger.info("edgeGateway Configuration has features");
                for (JAXBElement<? extends NetworkServiceType> jaxbElement : edgeGateway.getResource().getConfiguration()
                    .getEdgeGatewayServiceConfiguration().getNetworkService()) {
                    this.logNetworkService(jaxbElement);
                }
            }
        }
    }

    private void logNetworkService(final JAXBElement<? extends NetworkServiceType> jaxbElement) {
        if (jaxbElement.getValue() instanceof FirewallServiceType) {
            FirewallServiceType firewallService = (FirewallServiceType) jaxbElement.getValue();
            VcdCloudProvider.logger.info("FW " + jaxbElement.getValue() + "\n isIsEnabled " + firewallService.isIsEnabled()
                + "\n getDefaultAction " + firewallService.getDefaultAction() + "\n isLogDefaultAction "
                + firewallService.isLogDefaultAction() + "\n Number of FW rules=" + firewallService.getFirewallRule().size());
            for (FirewallRuleType firewallRule : firewallService.getFirewallRule()) {
                VcdCloudProvider.logger.info("  FW rule description" + firewallRule.getDescription() + "\n  FW rule policy"
                    + firewallRule.getPolicy() + "\n");
                /*VcdCloudProviderConnectorFactory.logger.info("  FW rule policy" + firewallRule.getPolicy());
                VcdCloudProviderConnectorFactory.logger.info("  ");*/
            }
        }
        if (jaxbElement.getValue() instanceof NatServiceType) {
            NatServiceType natService = (NatServiceType) jaxbElement.getValue();
            VcdCloudProvider.logger.info("NAT " + jaxbElement.getValue() + "\n isIsEnabled " + natService.isIsEnabled()
                + "\n getNatType " + natService.getNatType() + "\n getPolicy " + natService.getPolicy() + "\n getExternalIp "
                + natService.getExternalIp() + "\n Number of NAT rules=" + natService.getNatRule().size());
            for (NatRuleType natRule : natService.getNatRule()) {
                VcdCloudProvider.logger.info("  NAT rule description " + natRule.getDescription() + "\n  NAT rule getRuleType "
                    + natRule.getRuleType() + "\n  NAT rule isIsEnabled " + natRule.isIsEnabled() + "\n  NAT rule getId "
                    + natRule.getId());
                /*VcdCloudProviderConnectorFactory.logger.info("  NAT rule getRuleType " + natRule.getRuleType());
                VcdCloudProviderConnectorFactory.logger.info("  NAT rule isIsEnabled " + natRule.isIsEnabled());
                VcdCloudProviderConnectorFactory.logger.info("  NAT rule getId " + natRule.getId());*/
                if (natRule.getGatewayNatRule() != null) {
                    VcdCloudProvider.logger.info("   NAT rule getGatewayNatRule " + natRule.getGatewayNatRule()
                        + "\n   NAT rule getIcmpSubType " + natRule.getGatewayNatRule().getIcmpSubType()
                        + "\n   NAT rule getOriginalIp " + natRule.getGatewayNatRule().getOriginalIp()
                        + "\n   NAT rule getTranslatedIp " + natRule.getGatewayNatRule().getTranslatedIp()
                        + "\n   NAT rule getOriginalPort " + natRule.getGatewayNatRule().getOriginalPort()
                        + "\n   NAT rule getTranslatedPort " + natRule.getGatewayNatRule().getTranslatedPort()
                        + "\n   NAT rule getProtocol " + natRule.getGatewayNatRule().getProtocol()
                        + "\n   NAT rule getInterface " + natRule.getGatewayNatRule().getInterface());
                }
                VcdCloudProvider.logger.info("  NAT rule getOneToOneBasicRule " + natRule.getOneToOneBasicRule()
                    + "\n  NAT rule getOneToOneVmRule " + natRule.getOneToOneVmRule() + "\n  NAT rule getPortForwardingRule "
                    + natRule.getPortForwardingRule() + "\n  NAT rule getVmRule " + natRule.getVmRule() + "\n");
            }
        }
    }

    private InstantiationParamsType createDefaultVappInstantiationParamsType_old(final String fenceMode)
        throws ConnectorException {

        VAppNetworkConfigurationType vAppNetworkConfigurationType = new VAppNetworkConfigurationType();
        NetworkConfigurationType networkConfigurationType = new NetworkConfigurationType();

        // specify the NetworkConfiguration for the vApp network.
        if (fenceMode.equals(FenceModeValuesType.BRIDGED.value())) {
            VcdCloudProvider.logger.info("vAppNetworkConfiguration Bridged:"
                + this.vCloudContext.getCimiPublicOrgVdcNetworkName());
            vAppNetworkConfigurationType.setNetworkName(this.vCloudContext.getCimiPublicOrgVdcNetworkName());
            networkConfigurationType.setParentNetwork(this.vCloudContext.getVdc().getAvailableNetworkRefByName(
                this.vCloudContext.getCimiPublicOrgVdcNetworkName()));
            networkConfigurationType.setFenceMode(fenceMode);
            networkConfigurationType.setRetainNetInfoAcrossDeployments(true);
        } else if (fenceMode.equals(FenceModeValuesType.NATROUTED.value())) {
            VcdCloudProvider.logger.info("vAppNetworkConfiguration NATROUTED:" + Constants.VAPP_NETWORK_NAME);
            vAppNetworkConfigurationType.setNetworkName(Constants.VAPP_NETWORK_NAME);
            networkConfigurationType.setParentNetwork(this.vCloudContext.getVdc().getAvailableNetworkRefByName(
                this.vCloudContext.getCimiPublicOrgVdcNetworkName()));
            networkConfigurationType.setFenceMode(fenceMode);
            networkConfigurationType.setRetainNetInfoAcrossDeployments(false); // ???

            // Configure Internal IP Settings
            IpScopeType ipScope = new IpScopeType();
            ipScope.setNetmask("255.255.255.0");
            ipScope.setGateway("192.168.2.1");
            ipScope.setIsEnabled(true);
            ipScope.setIsInherited(false); // ???

            IpRangesType ipRangesType = new IpRangesType();
            IpRangeType ipRangeType = new IpRangeType();
            ipRangeType.setStartAddress("192.168.2.100");
            ipRangeType.setEndAddress("192.168.2.199");

            ipRangesType.getIpRange().add(ipRangeType);

            ipScope.setIpRanges(ipRangesType);
            ipScope.setIsEnabled(true);
            IpScopesType ipScopes = new IpScopesType();
            ipScopes.getIpScope().add(ipScope);
            networkConfigurationType.setIpScopes(ipScopes);

            NetworkFeaturesType features = new NetworkFeaturesType();
            NatServiceType natServiceType = new NatServiceType();
            natServiceType.setIsEnabled(true);
            // natServiceType.setPolicy(NatPolicyType.ALLOWTRAFFIC.name().toLowerCase());
            natServiceType.setPolicy(NatPolicyType.ALLOWTRAFFIC.value());
            // natServiceType.setNatType(NatTypeType.IPTRANSLATION.name().toLowerCase());
            natServiceType.setNatType(NatTypeType.IPTRANSLATION.value());
            features.getNetworkService().add(new ObjectFactory().createNatService(natServiceType));
            networkConfigurationType.setFeatures(features);

        } else { // ISOLATED !!!
            throw new ConnectorException("Unsupported fence mode: " + fenceMode);
        }
        vAppNetworkConfigurationType.setConfiguration(networkConfigurationType);

        // fill in the NetworkConfigSection
        NetworkConfigSectionType networkConfigSectionType = new NetworkConfigSectionType();
        MsgType networkInfo = new MsgType();
        networkConfigSectionType.setInfo(networkInfo);
        List<VAppNetworkConfigurationType> vAppNetworkConfigs = networkConfigSectionType.getNetworkConfig();
        vAppNetworkConfigs.add(vAppNetworkConfigurationType);

        // fill in InstantiationParams
        InstantiationParamsType instantiationParamsType = new InstantiationParamsType();
        List<JAXBElement<? extends SectionType>> sections = instantiationParamsType.getSection();
        sections.add(new ObjectFactory().createNetworkConfigSection(networkConfigSectionType));

        return instantiationParamsType;
    }

    private void configureVMsDefaultIPAddressingMode(final Vapp vapp) throws VCloudException, TimeoutException {
        List<VM> childVms = vapp.getChildrenVms();
        for (VM childVm : childVms) {
            NetworkConnectionSectionType networkConnectionSectionType = childVm.getNetworkConnectionSection();
            List<NetworkConnectionType> networkConnections = networkConnectionSectionType.getNetworkConnection();
            for (NetworkConnectionType networkConnection : networkConnections) {
                networkConnection.setIpAddressAllocationMode(IpAddressAllocationModeType.POOL.value());
                networkConnection.setNetwork(this.vCloudContext.getVdc().getAvailableNetworkRefs().iterator().next().getName());
            }
            childVm.updateSection(networkConnectionSectionType).waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
        }
    }

    private void configureVMsNatIPAddressingMode(final Vapp vapp) throws VCloudException, TimeoutException {
        List<VM> childVms = vapp.getChildrenVms();
        for (VM childVm : childVms) {
            NetworkConnectionSectionType networkConnectionSectionType = new NetworkConnectionSectionType();
            networkConnectionSectionType.setInfo(new MsgType());

            NetworkConnectionType networkConnectionType = new NetworkConnectionType();
            networkConnectionType.setNetwork("myNtwk");
            networkConnectionType.setIpAddressAllocationMode(IpAddressAllocationModeType.POOL.value());
            networkConnectionSectionType.getNetworkConnection().add(networkConnectionType);

            childVm.updateSection(networkConnectionSectionType).waitForTask(VcdCloudProvider.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
        }
    }

}
