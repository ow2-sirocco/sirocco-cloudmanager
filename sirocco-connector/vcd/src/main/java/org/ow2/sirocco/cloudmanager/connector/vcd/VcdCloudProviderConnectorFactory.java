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
 */

package org.ow2.sirocco.cloudmanager.connector.vcd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import javax.xml.bind.JAXBElement;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
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
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudCollectionItem;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemMachine;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.vmware.vcloud.api.rest.schema.InstantiateVAppTemplateParamsType;
import com.vmware.vcloud.api.rest.schema.InstantiationParamsType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigSectionType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.NetworkConnectionSectionType;
import com.vmware.vcloud.api.rest.schema.NetworkConnectionType;
import com.vmware.vcloud.api.rest.schema.ObjectFactory;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.VAppNetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.VAppType;
import com.vmware.vcloud.api.rest.schema.VmType;
import com.vmware.vcloud.api.rest.schema.ovf.MsgType;
import com.vmware.vcloud.api.rest.schema.ovf.ProductSectionProperty;
import com.vmware.vcloud.api.rest.schema.ovf.ProductSectionType;
import com.vmware.vcloud.api.rest.schema.ovf.SectionType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VappTemplate;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.VirtualDisk;
import com.vmware.vcloud.sdk.constants.FenceModeValuesType;
import com.vmware.vcloud.sdk.constants.IpAddressAllocationModeType;
import com.vmware.vcloud.sdk.constants.UndeployPowerActionType;
import com.vmware.vcloud.sdk.constants.VMStatus;
import com.vmware.vcloud.sdk.constants.VappStatus;
import com.vmware.vcloud.sdk.constants.Version;

@Component(public_factory = false)
@Provides
public class VcdCloudProviderConnectorFactory implements ICloudProviderConnectorFactory {
    private static Log logger = LogFactory.getLog(VcdCloudProviderConnectorFactory.class);

    private static int DEFAULT_WAIT_TIME_IN_MILLISECONDS = 600000;

    private static final int THREADPOOL_SIZE = 10;

    private static final Map<CloudProviderLocation, String> locationMap = new HashMap<CloudProviderLocation, String>();

    @Requires
    private IJobManager jobManager;

    public VcdCloudProviderConnectorFactory() {

    }

    public VcdCloudProviderConnectorFactory(final IJobManager jobManager) {
        this.jobManager = jobManager;
    }

    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors
        .newFixedThreadPool(VcdCloudProviderConnectorFactory.THREADPOOL_SIZE));

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
            VcdCloudProviderConnectorFactory.logger.info("Disposing VCD connector account.login="
                + cloudProviderToBeDeleted.getCloudProviderAccount().getLogin() + " location="
                + cloudProviderToBeDeleted.getCloudProviderLocation());
            this.cloudProvidersInUse.remove(cloudProviderToBeDeleted);
        }
    }

    @Override
    public ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount cloudProviderAccount,
        final CloudProviderLocation cloudProviderLocation) throws ConnectorException {
        ICloudProviderConnector result;
        for (ICloudProviderConnector cloudProvider : this.cloudProvidersInUse) {
            if (cloudProvider.getCloudProviderAccount().getLogin().equals(cloudProviderAccount.getLogin())) {
                if (cloudProviderLocation == null || cloudProvider.getCloudProviderLocation().equals(cloudProviderLocation)) {
                    return cloudProvider;
                }
            }
        }
        VcdCloudProviderConnectorFactory.logger.info("Adding new VCD connector account.login="
            + cloudProviderAccount.getLogin() + " location=" + cloudProviderLocation);
        try {
            result = new VcdCloudProviderConnector(cloudProviderAccount, cloudProviderLocation);
        } catch (ConnectorException e) {
            throw new ConnectorException(e);
        }
        this.cloudProvidersInUse.add(result);
        return result;
    }

    @Override
    public Set<CloudProviderLocation> listCloudProviderLocations() {
        return VcdCloudProviderConnectorFactory.locationMap.keySet();
    }

    private class VcdCloudProviderConnector implements ICloudProviderConnector, ISystemService, IComputeService {

        // TODO: fix
        // - FakeSSLSocketFactory
        // - CIMI Network entities (vs default network settings)
        // - nested vApp ?

        private final String cloudProviderId;

        private final CloudProviderAccount cloudProviderAccount;

        private final CloudProviderLocation cloudProviderLocation;

        private VcloudClient vcloudClient;

        private Organization org;

        private Vdc vdc;

        public VcdCloudProviderConnector(final CloudProviderAccount cloudProviderAccount,
            final CloudProviderLocation cloudProviderLocation) throws ConnectorException {
            this.cloudProviderId = UUID.randomUUID().toString();
            this.cloudProviderLocation = cloudProviderLocation;
            this.cloudProviderAccount = cloudProviderAccount;
            Map<String, String> properties = cloudProviderAccount.getCloudProvider().getProperties();
            if (properties == null || properties.get("orgName") == null || properties.get("vdcName") == null) {
                throw new ConnectorException("No access to cloud provider account properties: orgName and vdcName");
            }
            String orgName = properties.get("orgName");
            String vdcName = properties.get("vdcName");
            VcdCloudProviderConnectorFactory.logger.info("connect " + cloudProviderAccount.getLogin() + " to Organization="
                + orgName + ", VirtualDataCenter=" + vdcName);

            try {
                VcloudClient.setLogLevel(Level.OFF);
                this.vcloudClient = new VcloudClient(cloudProviderAccount.getCloudProvider().getEndpoint(), Version.V1_5);
                this.vcloudClient.registerScheme("https", 443, FakeSSLSocketFactory.getInstance());
                String user = cloudProviderAccount.getLogin() + "@" + orgName;
                this.vcloudClient.login(user, cloudProviderAccount.getPassword());
                ReferenceType orgRef = this.vcloudClient.getOrgRefByName(orgName);
                this.org = Organization.getOrganizationByReference(this.vcloudClient, orgRef);
                ReferenceType vdcRef = this.org.getVdcRefByName(vdcName);
                this.vdc = Vdc.getVdcByReference(this.vcloudClient, vdcRef);
            } catch (Exception ex) {
                throw new ConnectorException(ex);
            }
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
        public CloudProviderLocation getCloudProviderLocation() {
            return this.cloudProviderLocation;
        }

        @Override
        public IComputeService getComputeService() throws ConnectorException {
            return this;
        }

        @Override
        public IVolumeService getVolumeService() throws ConnectorException {
            return null;
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
            return this;
        }

        @Override
        public IProviderCapability getProviderCapability() throws ConnectorException {
            return null;
        }

        @Override
        public void setCloudProviderAccount(final CloudProviderAccount cpa) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public void setCloudProviderLocation(final CloudProviderLocation cpl) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
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
                return System.State.ERROR; // TODO: fix CIMI state
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
            // VcdCloudProviderConnectorFactory.logger.info("# vApp state=" +
            // vapp.getVappStatus() + ", system state="
            // + system.getState());
            system.setLocation(this.cloudProviderLocation);
            system.setCloudProviderAccount(this.cloudProviderAccount);

            try {
                List<SystemMachine> systemMachines = new ArrayList<SystemMachine>();
                system.setMachines(systemMachines);

                List<VM> childVms = vapp.getChildrenVms();
                for (VM childVm : childVms) {
                    SystemMachine systemMachine = new SystemMachine();
                    this.fromVmToSystemMachine(childVm, systemMachine);
                    system.getMachines().add(systemMachine);
                }
            } catch (VCloudException e) {
                throw new ConnectorException(e);
            }
        }

        @Override
        public Job createSystem(final SystemCreate systemCreate) throws ConnectorException {
            final int waitTimeInMilliSeconds = VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
            final ReferenceType vAppTemplateRef = new ReferenceType();
            vAppTemplateRef.setHref(systemCreate.getSystemTemplate().getProviderAssignedId());

            // Instantiating the vAppTemplate
            final Vapp vapp;
            try {
                VappTemplate vappTemplate = VappTemplate.getVappTemplateByReference(this.vcloudClient, vAppTemplateRef);
                VcdCloudProviderConnectorFactory.logger.info("Instantiating vAppTemplate: "
                    + vappTemplate.getReference().getName());
                vapp = this.newvAppFromTemplateDefaultMode(vAppTemplateRef, this.vdc, systemCreate);
            } catch (Exception ex) {
                throw new ConnectorException(ex);
            }

            final System system = new System();
            this.fromvAppToSystem(vapp, system);
            system.setState(System.State.CREATING);

            final Callable<System> createTask = new Callable<System>() {
                @Override
                public System call() throws Exception {
                    try {
                        List<Task> tasks = vapp.getTasks();
                        if (tasks.size() > 0) {
                            tasks.get(0).waitForTask(waitTimeInMilliSeconds);
                        }

                        VcdCloudProviderConnectorFactory.logger.info("Configuring VM Ip Addressing Mode");
                        VcdCloudProviderConnector.this.configureVMsDefaultIPAddressingMode(vapp.getReference());

                        if (systemCreate.getProperties() != null) {
                            String userData = systemCreate.getProperties().get("userData");
                            if (userData != null) {
                                VcdCloudProviderConnectorFactory.logger.info("Configuring user data");
                                VcdCloudProviderConnector.this.configureProductSection(vapp.getReference(), userData);
                            }
                        }

                        // Deploying the Instantiated vApp
                        VcdCloudProviderConnectorFactory.logger.info("Deploying " + vapp.getResource().getName());
                        vapp.deploy(false, 1000000, false).waitForTask(waitTimeInMilliSeconds);
                        VcdCloudProviderConnector.this.fromvAppToSystem(
                            VcdCloudProviderConnector.this.getVappByProviderAssignedId(system.getProviderAssignedId()), system);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return system;
                }
            };
            ListenableFuture<System> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(system, null, "add", result);
        }

        // Instantiating the vAppTemplate
        private Vapp newvAppFromTemplateDefaultMode(final ReferenceType vAppTemplateReference, final Vdc vdc,
            final SystemCreate systemCreate) throws VCloudException, ConnectorException {

            // get the OrgNetwork to which we can connect the vAppnetwork
            NetworkConfigurationType networkConfigurationType = new NetworkConfigurationType();
            if (vdc.getAvailableNetworkRefs().size() == 0) {
                throw new ConnectorException("No Networks in vdc to instantiate the vapp");
            }
            // VcdCloudProviderConnectorFactory.logger.info("available networks= "
            // + vdc.getAvailableNetworkRefs().iterator().next().getName());

            // specify the NetworkConfiguration for the vApp network.
            networkConfigurationType.setParentNetwork(vdc.getAvailableNetworkRefs().iterator().next());
            networkConfigurationType.setFenceMode(FenceModeValuesType.BRIDGED.value());

            VAppNetworkConfigurationType vAppNetworkConfigurationType = new VAppNetworkConfigurationType();
            vAppNetworkConfigurationType.setConfiguration(networkConfigurationType);
            // Default configuration (a vAppNetwork should be used instead)
            vAppNetworkConfigurationType.setNetworkName(vdc.getAvailableNetworkRefs().iterator().next().getName());

            // fill in the NetworkConfigSection
            NetworkConfigSectionType networkConfigSectionType = new NetworkConfigSectionType();
            MsgType networkInfo = new MsgType();
            networkConfigSectionType.setInfo(networkInfo);
            List<VAppNetworkConfigurationType> vAppNetworkConfigs = networkConfigSectionType.getNetworkConfig();
            vAppNetworkConfigs.add(vAppNetworkConfigurationType);

            // fill in remaining InstantititonParams (name, Source)
            InstantiationParamsType instantiationParamsType = new InstantiationParamsType();
            List<JAXBElement<? extends SectionType>> sections = instantiationParamsType.getSection();
            sections.add(new ObjectFactory().createNetworkConfigSection(networkConfigSectionType));

            // create the request body (InstantiateVAppTemplateParams)
            InstantiateVAppTemplateParamsType instVappTemplParamsType = new InstantiateVAppTemplateParamsType();
            if (systemCreate.getSystemTemplate().getName() == null || systemCreate.getSystemTemplate().getName().equals("")) {
                throw new ConnectorException("validation error on field 'systemTemplate.name': may not be empty");
            }
            instVappTemplParamsType.setName(systemCreate.getSystemTemplate().getName() + "-" + UUID.randomUUID());
            instVappTemplParamsType.setDescription(systemCreate.getSystemTemplate().getDescription());
            instVappTemplParamsType.setSource(vAppTemplateReference);
            instVappTemplParamsType.setInstantiationParams(instantiationParamsType);

            // make the request, and get a vApp in return
            return vdc.instantiateVappTemplate(instVappTemplParamsType);
        }

        // Configuring vms addressing mode
        private void configureVMsDefaultIPAddressingMode(final ReferenceType vappRef) throws VCloudException, TimeoutException {
            // Default IP allocation: Pool
            Vapp vapp = Vapp.getVappByReference(this.vcloudClient, vappRef);
            List<VM> childVms = vapp.getChildrenVms();
            for (VM childVm : childVms) {
                NetworkConnectionSectionType networkConnectionSectionType = childVm.getNetworkConnectionSection();
                List<NetworkConnectionType> networkConnections = networkConnectionSectionType.getNetworkConnection();
                for (NetworkConnectionType networkConnection : networkConnections) {
                    networkConnection.setIpAddressAllocationMode(IpAddressAllocationModeType.POOL.value());
                    networkConnection.setNetwork(this.vdc.getAvailableNetworkRefs().iterator().next().getName());
                }
                childVm.updateSection(networkConnectionSectionType).waitForTask(0);
            }
        }

        private void configureProductSection(final ReferenceType vappRef, final String userData) throws VCloudException,
            TimeoutException {
            Vapp vapp = Vapp.getVappByReference(this.vcloudClient, vappRef);
            MsgType msgType = new MsgType();
            msgType.setValue("user Data");

            for (VM childVm : vapp.getChildrenVms()) {
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

                childVm.updateProductSections(productSections).waitForTask(0);
            }
        }

        @Override
        public Job deleteSystem(final String systemId) throws ConnectorException {
            final int waitTimeInMilliSeconds = VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
            VcdCloudProviderConnectorFactory.logger.info("deleting system with providerAssignedId " + systemId);
            final System system = this.getSystem(systemId);
            system.setState(System.State.DELETING);

            final Callable<System> createTask = new Callable<System>() {
                @Override
                public System call() throws Exception {
                    try {
                        Vapp vapp = VcdCloudProviderConnector.this.getVappByProviderAssignedId(systemId);
                        VcdCloudProviderConnectorFactory.logger.info("Undeploying " + vapp.getResource().getName());
                        if (vapp.getVappStatus() == VappStatus.POWERED_ON) {
                            vapp.undeploy(UndeployPowerActionType.POWEROFF).waitForTask(waitTimeInMilliSeconds);
                        } else {
                            vapp.undeploy(UndeployPowerActionType.DEFAULT).waitForTask(waitTimeInMilliSeconds);
                        }
                        VcdCloudProviderConnectorFactory.logger.info("deleting " + vapp.getResource().getName());
                        vapp.delete().waitForTask(waitTimeInMilliSeconds);
                        system.setState(System.State.DELETED);
                        system.setMachines(new ArrayList<SystemMachine>());
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return system;
                }
            };
            ListenableFuture<System> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(system, null, "delete", result);
        }

        @Override
        public Job startSystem(final String systemId, final Map<String, String> properties) throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("starting system with providerAssignedId " + systemId);
            final System system = this.getSystem(systemId);
            system.setState(System.State.STARTING);

            final Callable<System> createTask = new Callable<System>() {
                @Override
                public System call() throws Exception {
                    try {
                        Vapp vapp = VcdCloudProviderConnector.this.getVappByProviderAssignedId(systemId);
                        VcdCloudProviderConnectorFactory.logger.info("powerOn " + vapp.getResource().getName());
                        vapp.powerOn().waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                        VcdCloudProviderConnector.this.fromvAppToSystem(
                            VcdCloudProviderConnector.this.getVappByProviderAssignedId(systemId), system);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return system;
                }
            };
            ListenableFuture<System> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(system, null, "start", result);
        }

        @Override
        public Job stopSystem(final String systemId, final boolean force, final Map<String, String> properties)
            throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("stopping system with providerAssignedId " + systemId);
            final System system = this.getSystem(systemId);
            system.setState(System.State.STOPPING);

            final Callable<System> createTask = new Callable<System>() {
                @Override
                public System call() throws Exception {
                    try {
                        Vapp vapp = VcdCloudProviderConnector.this.getVappByProviderAssignedId(systemId);
                        if (force) {
                            VcdCloudProviderConnectorFactory.logger.info("powerOff " + vapp.getResource().getName());
                            vapp.powerOff().waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                        } else {
                            VcdCloudProviderConnectorFactory.logger.info("shutdown " + vapp.getResource().getName());
                            vapp.shutdown().waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                        }
                        VcdCloudProviderConnector.this.fromvAppToSystem(
                            VcdCloudProviderConnector.this.getVappByProviderAssignedId(systemId), system);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return system;
                }
            };
            ListenableFuture<System> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(system, null, "stop", result);
        }

        @Override
        public Job restartSystem(final String systemId, final boolean force, final Map<String, String> properties)
            throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("restarting system with providerAssignedId " + systemId);
            final System system = this.getSystem(systemId);
            system.setState(System.State.STARTING);

            final Callable<System> createTask = new Callable<System>() {
                @Override
                public System call() throws Exception {
                    try {
                        Vapp vapp = VcdCloudProviderConnector.this.getVappByProviderAssignedId(systemId);
                        if (force) {
                            VcdCloudProviderConnectorFactory.logger.info("reset " + vapp.getResource().getName());
                            vapp.reset().waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                        } else {
                            VcdCloudProviderConnectorFactory.logger.info("reboot " + vapp.getResource().getName());
                            vapp.reboot().waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                        }
                        VcdCloudProviderConnector.this.fromvAppToSystem(
                            VcdCloudProviderConnector.this.getVappByProviderAssignedId(systemId), system);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return system;
                }
            };
            ListenableFuture<System> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(system, null, "restart", result);
        }

        @Override
        public Job pauseSystem(final String systemId, final Map<String, String> properties) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job suspendSystem(final String systemId, final Map<String, String> properties) throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("suspending system with providerAssignedId " + systemId);
            final System system = this.getSystem(systemId);
            system.setState(System.State.SUSPENDING);

            final Callable<System> createTask = new Callable<System>() {
                @Override
                public System call() throws Exception {
                    try {
                        Vapp vapp = VcdCloudProviderConnector.this.getVappByProviderAssignedId(systemId);
                        VcdCloudProviderConnectorFactory.logger.info("suspend " + vapp.getResource().getName());
                        vapp.suspend().waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                        VcdCloudProviderConnector.this.fromvAppToSystem(
                            VcdCloudProviderConnector.this.getVappByProviderAssignedId(systemId), system);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return system;
                }
            };
            ListenableFuture<System> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(system, null, "suspend", result);
        }

        @Override
        public System getSystem(final String systemId) throws ConnectorException {
            final System system = new System();
            this.fromvAppToSystem(this.getVappByProviderAssignedId(systemId), system);
            return system;
        }

        @Override
        public List<? extends CloudCollectionItem> getEntityListFromSystem(final String systemId, final String entityType)
            throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("getEntityListFromSystem with entityType=" + entityType);
            final System system = this.getSystem(systemId);
            if (entityType.equals(SystemMachine.class.getName())) {
                return system.getMachines();
            } else {
                throw new ConnectorException("unsupported entity type: " + entityType);
            }
        }

        @Override
        public Job removeEntityFromSystem(final String systemId, final String entityId) throws ConnectorException {
            // TODO
            throw new ConnectorException("unsupported operation");
        }

        private Vapp getVappByProviderAssignedId(final String id) throws ConnectorException {
            try {
                ReferenceType vAppRef = new ReferenceType();
                vAppRef.setHref(id);
                return Vapp.getVappByReference(this.vcloudClient, vAppRef);
            } catch (VCloudException e) {
                throw new ConnectorException(e);
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
                return Machine.State.ERROR; // TODO: fix CIMI state
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
                // VcdCloudProviderConnectorFactory.logger.info("## vm state=" +
                // vm.getVMStatus() + ", machine state="
                // + machine.getState() + ", ips=" + vm.getIpAddressesById());

                // HW
                // TODO: (check RAM & Disk size unit)
                machine.setCpu(vm.getCpu().getNoOfCpus());
                machine.setMemory(vm.getMemory().getMemorySize().intValue() * 1024);
                List<MachineDisk> machineDisks = new ArrayList<MachineDisk>();
                machine.setDisks(machineDisks);
                for (VirtualDisk disk : vm.getDisks()) {
                    if (disk.isHardDisk()) {
                        MachineDisk machineDisk = new MachineDisk();
                        machineDisk.setInitialLocation("");
                        machineDisk.setCapacity(disk.getHardDiskSize().intValue() * 1024);
                        machineDisks.add(machineDisk);
                    }
                }

                // Network
                // TODO: (fix nic, public/private, Hostname, Network...)
                List<MachineNetworkInterface> nics = new ArrayList<MachineNetworkInterface>();
                machine.setNetworkInterfaces(nics);
                for (NetworkConnectionType networkConnection : vm.getNetworkConnections()) {
                    // VcdCloudProviderConnectorFactory.logger.info("### vm Ip="
                    // + networkConnection.getIpAddress()
                    // + ", vm external Ip=" +
                    // networkConnection.getExternalIpAddress() +
                    // ", vm allocation mode="
                    // + networkConnection.getIpAddressAllocationMode());

                    String ipAddressAllocationMode = networkConnection.getIpAddressAllocationMode();
                    String cimiIpAddressAllocationMode = "";
                    if (ipAddressAllocationMode.equalsIgnoreCase("DHCP")) {
                        cimiIpAddressAllocationMode = "dynamic";
                    } else if (ipAddressAllocationMode.equalsIgnoreCase("MANUAL")
                        || ipAddressAllocationMode.equalsIgnoreCase("POOL")) {
                        cimiIpAddressAllocationMode = "static";
                    }

                    if (networkConnection.getIpAddress() != null && networkConnection.getIpAddress() != "") {
                        Address cimiAddress = new Address();
                        cimiAddress.setIp(networkConnection.getIpAddress());
                        cimiAddress.setAllocation(cimiIpAddressAllocationMode);
                        cimiAddress.setProtocol("IPv4");
                        // cimiAddress.setNetwork(???);
                        // cimiAddress.setHostName(???);
                        cimiAddress.setResource(null);

                        List<MachineNetworkInterfaceAddress> cimiAddresses = new ArrayList<MachineNetworkInterfaceAddress>();
                        MachineNetworkInterfaceAddress entry = new MachineNetworkInterfaceAddress();
                        entry.setAddress(cimiAddress);
                        cimiAddresses.add(entry);
                        MachineNetworkInterface privateNic = new MachineNetworkInterface();
                        privateNic.setAddresses(cimiAddresses);
                        // TODO hack for POC (NetworkType should be
                        // PRIVATE)
                        privateNic.setNetworkType(Network.Type.PUBLIC);
                        nics.add(privateNic);
                    }

                    if (networkConnection.getExternalIpAddress() != null && networkConnection.getIpAddress() != "") {
                        Address cimiAddress = new Address();
                        cimiAddress.setIp(networkConnection.getExternalIpAddress());
                        cimiAddress.setAllocation(cimiIpAddressAllocationMode);
                        cimiAddress.setProtocol("IPv4");
                        // cimiAddress.setNetwork(???);
                        // cimiAddress.setHostName(???);
                        cimiAddress.setResource(null);

                        List<MachineNetworkInterfaceAddress> cimiAddresses = new ArrayList<MachineNetworkInterfaceAddress>();
                        MachineNetworkInterfaceAddress entry = new MachineNetworkInterfaceAddress();
                        entry.setAddress(cimiAddress);
                        cimiAddresses.add(entry);
                        MachineNetworkInterface publicNic = new MachineNetworkInterface();
                        publicNic.setAddresses(cimiAddresses);
                        publicNic.setNetworkType(Network.Type.PUBLIC);
                        nics.add(publicNic);
                    }
                }

            } catch (VCloudException e) {
                throw new ConnectorException(e);
            }
            // set other Machine attributes ?
        }

        @Override
        public Job createMachine(final MachineCreate machineCreate) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job deleteMachine(final String machineId) throws ConnectorException {
            final int waitTimeInMilliSeconds = VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
            VcdCloudProviderConnectorFactory.logger.info("deleting machine with providerAssignedId " + machineId);
            final Machine machine = this.getMachine(machineId);
            machine.setState(Machine.State.DELETING);

            final Callable<Machine> createTask = new Callable<Machine>() {
                @Override
                public Machine call() throws Exception {
                    try {
                        VM vm = VcdCloudProviderConnector.this.getVmByProviderAssignedId(machineId);
                        VcdCloudProviderConnectorFactory.logger.info("Undeploying " + vm.getResource().getName());
                        if (vm.getVMStatus() == VMStatus.POWERED_ON) {
                            vm.undeploy(UndeployPowerActionType.POWEROFF).waitForTask(waitTimeInMilliSeconds);
                        } else {
                            vm.undeploy(UndeployPowerActionType.DEFAULT).waitForTask(waitTimeInMilliSeconds);
                        }
                        VcdCloudProviderConnectorFactory.logger.info("deleting " + vm.getResource().getName());
                        vm.delete().waitForTask(waitTimeInMilliSeconds);
                        machine.setState(Machine.State.DELETED);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return machine;
                }
            };
            ListenableFuture<Machine> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "delete", result);
        }

        @Override
        public Job startMachine(final String machineId) throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("starting machine with providerAssignedId " + machineId);
            final Machine machine = this.getMachine(machineId);
            machine.setState(Machine.State.STARTING);

            final Callable<Machine> createTask = new Callable<Machine>() {
                @Override
                public Machine call() throws Exception {
                    try {
                        VM vm = VcdCloudProviderConnector.this.getVmByProviderAssignedId(machineId);
                        VcdCloudProviderConnectorFactory.logger.info("powerOn " + vm.getResource().getName());
                        vm.powerOn().waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                        VcdCloudProviderConnector.this.fromVmToMachine(
                            VcdCloudProviderConnector.this.getVmByProviderAssignedId(machineId), machine);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return machine;
                }
            };
            ListenableFuture<Machine> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "start", result);
        }

        @Override
        public Job stopMachine(final String machineId, final boolean force) throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("stopping machine with providerAssignedId " + machineId);
            final Machine machine = this.getMachine(machineId);
            machine.setState(Machine.State.STOPPING);

            final Callable<Machine> createTask = new Callable<Machine>() {
                @Override
                public Machine call() throws Exception {
                    try {
                        VM vm = VcdCloudProviderConnector.this.getVmByProviderAssignedId(machineId);
                        if (force) {
                            VcdCloudProviderConnectorFactory.logger.info("powerOff " + vm.getResource().getName());
                            vm.powerOff().waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                        } else {
                            VcdCloudProviderConnectorFactory.logger.info("shutdown " + vm.getResource().getName());
                            vm.shutdown().waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                        }
                        VcdCloudProviderConnector.this.fromVmToMachine(
                            VcdCloudProviderConnector.this.getVmByProviderAssignedId(machineId), machine);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return machine;
                }
            };
            ListenableFuture<Machine> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "stop", result);
        }

        @Override
        public Job suspendMachine(final String machineId) throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("suspending machine with providerAssignedId " + machineId);
            final Machine machine = this.getMachine(machineId);
            machine.setState(Machine.State.SUSPENDING);

            final Callable<Machine> createTask = new Callable<Machine>() {
                @Override
                public Machine call() throws Exception {
                    try {
                        VM vm = VcdCloudProviderConnector.this.getVmByProviderAssignedId(machineId);
                        VcdCloudProviderConnectorFactory.logger.info("suspend " + vm.getResource().getName());
                        vm.suspend().waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                        VcdCloudProviderConnector.this.fromVmToMachine(
                            VcdCloudProviderConnector.this.getVmByProviderAssignedId(machineId), machine);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return machine;
                }
            };
            ListenableFuture<Machine> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "suspend", result);
        }

        @Override
        public Job restartMachine(final String machineId, final boolean force) throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("restarting machine with providerAssignedId " + machineId);
            final Machine machine = this.getMachine(machineId);
            machine.setState(Machine.State.STARTING);

            final Callable<Machine> createTask = new Callable<Machine>() {
                @Override
                public Machine call() throws Exception {
                    try {
                        VM vm = VcdCloudProviderConnector.this.getVmByProviderAssignedId(machineId);
                        if (force) {
                            VcdCloudProviderConnectorFactory.logger.info("reset " + vm.getResource().getName());
                            vm.reset().waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                        } else {
                            VcdCloudProviderConnectorFactory.logger.info("reboot " + vm.getResource().getName());
                            vm.reboot().waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                        }
                        VcdCloudProviderConnector.this.fromVmToMachine(
                            VcdCloudProviderConnector.this.getVmByProviderAssignedId(machineId), machine);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return machine;
                }
            };
            ListenableFuture<Machine> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "restart", result);
        }

        @Override
        public Job pauseMachine(final String machineId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Machine.State getMachineState(final String machineId) throws ConnectorException {
            VM vm = this.getVmByProviderAssignedId(machineId);
            return this.fromvVmStatusToMachineState(vm);
        }

        @Override
        public Machine getMachine(final String machineId) throws ConnectorException {
            VM vm = this.getVmByProviderAssignedId(machineId);
            Machine machine = new Machine();
            this.fromVmToMachine(vm, machine);
            return machine;
        }

        @Override
        public Job addVolumeToMachine(final String machineId, final MachineVolume machineVolume) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job removeVolumeFromMachine(final String machineId, final MachineVolume machineVolume) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        private VM getVmByProviderAssignedId(final String id) throws ConnectorException {
            try {
                ReferenceType vmRef = new ReferenceType();
                vmRef.setHref(id);
                return VM.getVMByReference(this.vcloudClient, vmRef);
            } catch (VCloudException e) {
                throw new ConnectorException(e);
            }
        }
    }
}
