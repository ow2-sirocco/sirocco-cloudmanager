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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.xml.namespace.QName;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
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
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
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
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.vmware.vcloud.api.rest.schema.ComposeVAppParamsType;
import com.vmware.vcloud.api.rest.schema.InstantiateVAppTemplateParamsType;
import com.vmware.vcloud.api.rest.schema.InstantiationParamsType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigSectionType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.NetworkConnectionSectionType;
import com.vmware.vcloud.api.rest.schema.NetworkConnectionType;
import com.vmware.vcloud.api.rest.schema.ObjectFactory;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.SourcedCompositionItemParamType;
import com.vmware.vcloud.api.rest.schema.VAppNetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.VAppType;
import com.vmware.vcloud.api.rest.schema.VmType;
import com.vmware.vcloud.api.rest.schema.ovf.CimString;
import com.vmware.vcloud.api.rest.schema.ovf.CimUnsignedLong;
import com.vmware.vcloud.api.rest.schema.ovf.MsgType;
import com.vmware.vcloud.api.rest.schema.ovf.ProductSectionProperty;
import com.vmware.vcloud.api.rest.schema.ovf.ProductSectionType;
import com.vmware.vcloud.api.rest.schema.ovf.RASDType;
import com.vmware.vcloud.api.rest.schema.ovf.ResourceType;
import com.vmware.vcloud.api.rest.schema.ovf.SectionType;
import com.vmware.vcloud.api.rest.schema.ovf.VirtualHardwareSectionType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VappTemplate;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.VirtualCpu;
import com.vmware.vcloud.sdk.VirtualDisk;
import com.vmware.vcloud.sdk.VirtualMemory;
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

    public static final String CLOUD_PROVIDER_TYPE = "vcd";

    private static int DEFAULT_WAIT_TIME_IN_MILLISECONDS = 600000;

    private static final int THREADPOOL_SIZE = 10;

    private static final Map<CloudProviderLocation, String> locationMap = new HashMap<CloudProviderLocation, String>();

    @Requires
    private IJobManager jobManager;

    @ServiceProperty(name = ICloudProviderConnectorFactory.CLOUD_PROVIDER_TYPE_PROPERTY, value = VcdCloudProviderConnectorFactory.CLOUD_PROVIDER_TYPE)
    private String cloudProviderType;

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

        boolean version1 = true;

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
                return System.State.ERROR; // FIXME: CIMI mapping
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
            system.setVolumes(new ArrayList<SystemVolume>());
            system.setNetworks(new ArrayList<SystemNetwork>());
            system.setSystems(new ArrayList<SystemSystem>());
            system.setCredentials(new ArrayList<SystemCredentials>());
        }

        @Override
        public Job createSystem(final SystemCreate systemCreate) throws ConnectorException {
            if (this.version1) {
                return this.createSystem_v1(systemCreate);
            } else {
                return this.createSystem_v2(systemCreate);
            }
        }

        public Job createSystem_v1(final SystemCreate systemCreate) throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("creating system " + systemCreate.getName());
            final int waitTimeInMilliSeconds = VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
            final ReferenceType vAppTemplateRef = new ReferenceType();
            vAppTemplateRef.setHref(systemCreate.getSystemTemplate().getProviderAssignedId());

            // Instantiating the vAppTemplate
            final Vapp vapp;
            try {
                VappTemplate vappTemplate = VappTemplate.getVappTemplateByReference(this.vcloudClient, vAppTemplateRef);
                VcdCloudProviderConnectorFactory.logger.info("Instantiating vAppTemplate: "
                    + vappTemplate.getReference().getName());
                vapp = this.instantiateVappTemplate(vAppTemplateRef, this.vdc, systemCreate);
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
                        if (tasks.size() > 0) { // FIXME wait for all tasks
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

        public Job createSystem_v2(final SystemCreate systemCreate) throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("creating system ");
            final int waitTimeInMilliSeconds = VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
            final System system = new System();
            system.setState(System.State.CREATING);

            final Callable<System> createTask = new Callable<System>() {
                @Override
                public System call() throws Exception {
                    try {
                        // VcdCloudProviderConnectorFactory.logger.info("Creating vApp");
                        Vapp vapp = VcdCloudProviderConnector.this.createVapp(VcdCloudProviderConnector.this.vdc, systemCreate);

                        // Deploying the Instantiated vApp
                        VcdCloudProviderConnectorFactory.logger.info("Deploying " + vapp.getResource().getName());
                        vapp.deploy(false, 1000000, false).waitForTask(waitTimeInMilliSeconds);

                        // refresh the vapp (otherwise no childrenVms is
                        // visible! - TBC)
                        vapp = Vapp.getVappByReference(VcdCloudProviderConnector.this.vcloudClient, vapp.getReference());
                        VcdCloudProviderConnector.this.fromvAppToSystem(vapp, system);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return system;
                }
            };
            ListenableFuture<System> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(system, null, "add", result);
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
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job addEntityToSystem(final String systemId, final String entityId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
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
                return Machine.State.ERROR; // FIXME: CIMI mapping
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
                machine.setCpu(vm.getCpu().getNoOfCpus());
                machine.setMemory(vm.getMemory().getMemorySize().intValue() * 1024); // CIMI:
                                                                                     // kibibytes
                List<MachineDisk> machineDisks = new ArrayList<MachineDisk>();
                machine.setDisks(machineDisks);
                for (VirtualDisk disk : vm.getDisks()) {
                    if (disk.isHardDisk()) {
                        MachineDisk machineDisk = new MachineDisk();
                        machineDisk.setInitialLocation("");
                        machineDisk.setCapacity(disk.getHardDiskSize().intValue() * 1000); // CIMI:
                                                                                           // kilobytes
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
            if (this.version1) {
                throw new ConnectorException("unsupported operation");
            } else {
                return this.createMachine_v2(machineCreate);
            }
        }

        public Job createMachine_v2(final MachineCreate machineCreate) throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("creating machine ");
            final int waitTimeInMilliSeconds = VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
            final Machine machine = new Machine();
            machine.setState(Machine.State.CREATING);

            final Callable<Machine> createTask = new Callable<Machine>() {
                @Override
                public Machine call() throws Exception {
                    try {
                        // VcdCloudProviderConnectorFactory.logger.info("Creating vApp");
                        Vapp vapp = VcdCloudProviderConnector.this
                            .createVapp(VcdCloudProviderConnector.this.vdc, machineCreate);

                        // Deploying the Instantiated vApp
                        VcdCloudProviderConnectorFactory.logger.info("Deploying " + vapp.getResource().getName());
                        vapp.deploy(false, 1000000, false).waitForTask(waitTimeInMilliSeconds);

                        // refresh the vapp (otherwise no childrenVms is
                        // visible! - TBC)
                        vapp = Vapp.getVappByReference(VcdCloudProviderConnector.this.vcloudClient, vapp.getReference());
                        if (vapp.getChildrenVms().size() != 1) {
                            throw new ConnectorException("only one vm is expected!");
                        }
                        VcdCloudProviderConnector.this.fromVmToMachine(vapp.getChildrenVms().get(0), machine);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return machine;
                }
            };
            ListenableFuture<Machine> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "add", result);
        }

        private Vapp createVapp(final Vdc vdc, final MachineCreate machineCreate) throws VCloudException, ConnectorException,
            TimeoutException {
            if (machineCreate.getName() == null || machineCreate.getName().equals("")) {
                throw new ConnectorException("validation error on field 'machineCreate.name': may not be empty");
            }

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
            systemTemplate.setName("System-" + machineCreate.getName());
            systemTemplate.setDescription(machineCreate.getDescription());
            systemTemplate.setProperties(new HashMap<String, String>());
            systemTemplate.setComponentDescriptors(componentDescriptors);

            SystemCreate systemCreate = new SystemCreate();
            systemCreate.setName("System-" + machineCreate.getName());
            systemCreate.setDescription(machineCreate.getDescription());
            systemCreate.setProperties(new HashMap<String, String>());
            systemCreate.setSystemTemplate(systemTemplate);

            return this.createVapp(vdc, systemCreate);
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

        //
        // VCD
        //

        private Vapp getVappByProviderAssignedId(final String id) throws ConnectorException {
            try {
                ReferenceType vAppRef = new ReferenceType();
                vAppRef.setHref(id);
                return Vapp.getVappByReference(this.vcloudClient, vAppRef);
            } catch (VCloudException e) {
                throw new ConnectorException(e);
            }
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

        private Vapp createVapp(final Vdc vdc, final SystemCreate systemCreate) throws VCloudException, ConnectorException,
            TimeoutException {

            // create the request body (ComposeVAppParamsType)
            ComposeVAppParamsType composeVAppParamsType = new ComposeVAppParamsType();
            composeVAppParamsType.setDeploy(false);
            if (systemCreate.getName() == null || systemCreate.getName().equals("")) {
                throw new ConnectorException("validation error on field 'systemCreate.name': may not be empty");
            }
            composeVAppParamsType.setName(systemCreate.getName() + "-" + UUID.randomUUID());
            composeVAppParamsType.setDescription(systemCreate.getDescription());

            // set default vApp instantiation parameters
            InstantiationParamsType instantiationParamsType = this.createDefaultVappInstantiationParamsType(this.vdc);
            composeVAppParamsType.setInstantiationParams(instantiationParamsType);

            // TODO: set vApp network, startup, lease... sections (vs CIMI?)

            // source items (VMs)
            Set<ComponentDescriptor> machineComponentDescriptors = this.getComponentDescriptorsOfType(systemCreate,
                ComponentType.MACHINE);
            Map<String, MachineTemplate> machineTemplateMap = new HashMap<String, MachineTemplate>();
            for (ComponentDescriptor mcd : machineComponentDescriptors) {
                MachineTemplate mt = (MachineTemplate) mcd.getComponentTemplate();
                for (int i = 0; i < mcd.getComponentQuantity(); i++) {

                    // create sourceItem body (SourcedCompositionItemParamType)
                    SourcedCompositionItemParamType item = new SourcedCompositionItemParamType();
                    ReferenceType source = new ReferenceType();
                    String name = mcd.getName() == null ? "siroccoMachine" : mcd.getName();
                    if (mcd.getComponentQuantity() > 1) {
                        name += new Integer(i).toString();
                    }
                    name += "-" + UUID.randomUUID();
                    source.setName(name);
                    machineTemplateMap.put(name, mt);

                    // set Href
                    String idKey = "vcd";
                    String vmTmplRef = mt.getMachineImage().getProperties().get(idKey);
                    if (vmTmplRef == null) {
                        throw new ConnectorException("Cannot find vAppTemplate/vm Id for key " + idKey);
                    }
                    source.setHref(vmTmplRef);
                    item.setSource(source);

                    composeVAppParamsType.getSourcedItem().add(item);
                }
            }

            // make the composition request, and get a vApp in return
            Vapp vapp = vdc.composeVapp(composeVAppParamsType);
            List<Task> tasks = vapp.getTasks();
            if (tasks.size() > 0) { // TODO wait for all tasks
                tasks.get(0).waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
            }

            // configure vms
            VcdCloudProviderConnector.this.configureVmSections(vapp, machineTemplateMap);

            return vapp;
        }

        private Set<ComponentDescriptor> getComponentDescriptorsOfType(final SystemCreate systemCreate,
            final ComponentType cType) {
            Set<ComponentDescriptor> componentDescriptors = new HashSet<ComponentDescriptor>();
            for (ComponentDescriptor cd : systemCreate.getSystemTemplate().getComponentDescriptors()) {
                if (cd.getComponentType() == cType) {
                    componentDescriptors.add(cd);
                }
            }
            return componentDescriptors;
        }

        private InstantiationParamsType createDefaultVappInstantiationParamsType(final Vdc vdc) throws ConnectorException {

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
            // networkConfigurationType.setFenceMode(FenceModeValuesType.NATROUTED.value());
            // networkConfigurationType.setFenceMode(FenceModeValuesType.ISOLATED.value());

            VAppNetworkConfigurationType vAppNetworkConfigurationType = new VAppNetworkConfigurationType();
            vAppNetworkConfigurationType.setConfiguration(networkConfigurationType);
            // Default configuration
            vAppNetworkConfigurationType.setNetworkName(vdc.getAvailableNetworkRefs().iterator().next().getName());

            // fill in the NetworkConfigSection
            NetworkConfigSectionType networkConfigSectionType = new NetworkConfigSectionType();
            MsgType networkInfo = new MsgType();
            networkConfigSectionType.setInfo(networkInfo);
            List<VAppNetworkConfigurationType> vAppNetworkConfigs = networkConfigSectionType.getNetworkConfig();
            vAppNetworkConfigs.add(vAppNetworkConfigurationType);

            // fill in InstantititonParams
            InstantiationParamsType instantiationParamsType = new InstantiationParamsType();
            List<JAXBElement<? extends SectionType>> sections = instantiationParamsType.getSection();
            sections.add(new ObjectFactory().createNetworkConfigSection(networkConfigSectionType));

            return instantiationParamsType;
        }

        private void configureVmSections(Vapp vapp, final Map<String, MachineTemplate> machineTemplateMap)
            throws VCloudException, TimeoutException {
            // refresh the vapp (TBC)
            vapp = Vapp.getVappByReference(this.vcloudClient, vapp.getReference());

            // set virtual hardware
            VcdCloudProviderConnectorFactory.logger.info("Configuring virtual hardware");
            VcdCloudProviderConnector.this.configureVirtualHardware(vapp, machineTemplateMap);

            // set IPs
            VcdCloudProviderConnectorFactory.logger.info("Configuring VM Ip Addressing Mode");
            VcdCloudProviderConnector.this.configureVMsDefaultIPAddressingMode(vapp);

            // set user data
            VcdCloudProviderConnectorFactory.logger.info("Configuring user data");
            VcdCloudProviderConnector.this.configureUserData(vapp, machineTemplateMap);

            // TODO: set guestCustomization.. sections (vs CIMI?)
        }

        private void configureVirtualHardware(final Vapp vapp, final Map<String, MachineTemplate> machineTemplateMap)
            throws VCloudException, TimeoutException {
            for (VM childVm : vapp.getChildrenVms()) {
                MachineTemplate mt = machineTemplateMap.get(childVm.getResource().getName());
                MachineConfiguration mc = mt.getMachineConfiguration();
                if (mc == null) {
                    continue;
                }

                VirtualHardwareSectionType virtualHardwareSectionType = childVm.getVirtualHardwareSection();
                if (virtualHardwareSectionType == null) {
                    virtualHardwareSectionType = new VirtualHardwareSectionType();
                }

                for (RASDType item : virtualHardwareSectionType.getItem()) {
                    int type = Integer.parseInt(item.getResourceType().getValue());
                    /*VcdCloudProviderConnectorFactory.logger.info("- virtualHardwareItemType: " + type + ", "
                        + item.getDescription().getValue());*/
                }

                // CPU
                VcdCloudProviderConnectorFactory.logger.info("  Number of Virtual CPUs: " + childVm.getCpu().getNoOfCpus());
                VirtualCpu virtualCpuItem = childVm.getCpu();
                if (mc.getCpu() > 0) {
                    if (virtualCpuItem.getNoOfCpus() != mc.getCpu()) {
                        VcdCloudProviderConnectorFactory.logger.info("  -> updating: " + mc.getCpu() + " Virtual CPUs");
                        virtualCpuItem.setNoOfCpus(mc.getCpu());
                        childVm.updateCpu(virtualCpuItem).waitForTask(
                            VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                    }
                }

                // RAM
                if (mc.getMemory() > 0) {
                    VcdCloudProviderConnectorFactory.logger
                        .info("  Memory Size: " + childVm.getMemory().getMemorySize() + "MB");
                    long memoryInMBytes = mc.getMemory() / 1024; // kibibytes
                                                                 // CIMI
                    VirtualMemory virtualMemoryItemInMBytes = childVm.getMemory();
                    if (virtualMemoryItemInMBytes.getMemorySize().longValue() != memoryInMBytes) {
                        VcdCloudProviderConnectorFactory.logger.info("  -> updating: " + memoryInMBytes + " MB");
                        virtualMemoryItemInMBytes.setMemorySize(BigInteger.valueOf(memoryInMBytes));
                        childVm.updateMemory(virtualMemoryItemInMBytes).waitForTask(
                            VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                    }
                }

                // DISK
                // Add virtual disk if needed
                List<VirtualDisk> disks = childVm.getDisks();
                boolean diskSectionHasChanged = false;
                for (DiskTemplate disk : mc.getDiskTemplates()) {
                    long diskInMBytes = disk.getCapacity() / 1000; // kilobytes
                                                                   // CIMI
                    if (diskInMBytes < 1) {
                        diskInMBytes = 1;
                    }
                    VcdCloudProviderConnectorFactory.logger.info("  Add New Disk: " + diskInMBytes + " MB, LsiLogic");
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
                    childVm.updateDisks(disks).waitForTask(VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                }
            }
        }

        private void configureVMsDefaultIPAddressingMode(final Vapp vapp) throws VCloudException, TimeoutException {
            List<VM> childVms = vapp.getChildrenVms();
            for (VM childVm : childVms) {
                NetworkConnectionSectionType networkConnectionSectionType = childVm.getNetworkConnectionSection();
                List<NetworkConnectionType> networkConnections = networkConnectionSectionType.getNetworkConnection();
                for (NetworkConnectionType networkConnection : networkConnections) {
                    networkConnection.setIpAddressAllocationMode(IpAddressAllocationModeType.POOL.value());
                    networkConnection.setNetwork(this.vdc.getAvailableNetworkRefs().iterator().next().getName());
                }
                childVm.updateSection(networkConnectionSectionType).waitForTask(
                    VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
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

                childVm.updateProductSections(productSections).waitForTask(
                    VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
            }
        }

        //
        // TMP
        //

        // Instantiating the vAppTemplate
        private Vapp instantiateVappTemplate(final ReferenceType vAppTemplateReference, final Vdc vdc,
            final SystemCreate systemCreate) throws VCloudException, ConnectorException {

            InstantiationParamsType instantiationParamsType = this.createDefaultVappInstantiationParamsType(this.vdc);

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
                childVm.updateSection(networkConnectionSectionType).waitForTask(
                    VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
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

                childVm.updateProductSections(productSections).waitForTask(
                    VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
            }
        }

        public Job createSystem_v3(final SystemCreate systemCreate) throws ConnectorException {
            final int waitTimeInMilliSeconds = VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
            final ReferenceType vAppTemplateRef = new ReferenceType();
            vAppTemplateRef.setHref(systemCreate.getSystemTemplate().getProviderAssignedId());

            // Instantiating the vAppTemplate
            final Vapp vapp;
            try {
                VappTemplate vappTemplate = VappTemplate.getVappTemplateByReference(this.vcloudClient, vAppTemplateRef);
                VcdCloudProviderConnectorFactory.logger.info("Instantiating vAppTemplate: "
                    + vappTemplate.getReference().getName());
                vapp = this.composeVappTemplate(vAppTemplateRef, this.vdc, systemCreate);

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

                        /* VcdCloudProviderConnectorFactory.logger.info("Configuring VM Ip Addressing Mode");
                           VcdCloudProviderConnector.this.configureVMsDefaultIPAddressingMode(vapp.getReference());
                        */
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

        // composing the vAppTemplate
        private Vapp composeVappTemplate(final ReferenceType vAppTemplateReference, final Vdc vdc,
            final SystemCreate systemCreate) throws VCloudException, ConnectorException {

            InstantiationParamsType instantiationParamsType = this.createDefaultVappInstantiationParamsType(this.vdc);

            // create the request body (ComposeVAppParamsType)
            ComposeVAppParamsType composeVAppParamsType = new ComposeVAppParamsType();
            composeVAppParamsType.setDeploy(false);
            composeVAppParamsType.setInstantiationParams(instantiationParamsType);
            composeVAppParamsType.setName("ComposedVapp-EE" + "-" + UUID.randomUUID());
            List<SourcedCompositionItemParamType> items = composeVAppParamsType.getSourcedItem();

            // getting the vapptemplates first vm.
            VappTemplate vappTemplate = VappTemplate.getVappTemplateByReference(this.vcloudClient, vAppTemplateReference);
            VappTemplate vm = vappTemplate.getChildren().get(0);
            String vmHref = vm.getReference().getHref();
            VcdCloudProviderConnectorFactory.logger.info("----VM template=" + vmHref);

            // adding vm items with different names.
            for (int i = 0; i < 1; i++) {
                SourcedCompositionItemParamType vappTemplateItem = new SourcedCompositionItemParamType();
                ReferenceType vappTemplateVMRef = new ReferenceType();
                vappTemplateVMRef.setHref(vmHref);
                vappTemplateVMRef.setName(i + "-" + vAppTemplateReference.getName() + "-" + UUID.randomUUID());
                vappTemplateItem.setSource(vappTemplateVMRef);

                // Configuring vms addressing mode
                NetworkConnectionSectionType networkConnectionSectionType = new NetworkConnectionSectionType();
                networkConnectionSectionType.setInfo(new MsgType());

                NetworkConnectionType networkConnectionType = new NetworkConnectionType();
                networkConnectionType.setNetwork(this.vdc.getAvailableNetworkRefs().iterator().next().getName());
                networkConnectionType.setIpAddressAllocationMode(IpAddressAllocationModeType.POOL.value());
                networkConnectionSectionType.getNetworkConnection().add(networkConnectionType);

                InstantiationParamsType vmInstantiationParamsType = new InstantiationParamsType();
                List<JAXBElement<? extends SectionType>> vmSections = vmInstantiationParamsType.getSection();
                vmSections.add(new ObjectFactory().createNetworkConnectionSection(networkConnectionSectionType));
                vappTemplateItem.setInstantiationParams(vmInstantiationParamsType);

                //
                items.add(vappTemplateItem);
            }

            // make the request, and get a vApp in return
            return vdc.composeVapp(composeVAppParamsType);
        }

        public Job createMachine_v3(final MachineCreate machineCreate) throws ConnectorException {
            final int waitTimeInMilliSeconds = VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS;

            final Vapp vapp;
            try {
                vapp = this.createVapp(this.vdc, machineCreate);
            } catch (Exception ex) {
                throw new ConnectorException(ex);
            }

            final Machine machine = new Machine();
            // this.fromVmToMachine(vapp.getChildrenVms().get(0), machine);
            machine.setState(Machine.State.CREATING);

            final Callable<Machine> createTask = new Callable<Machine>() {
                @Override
                public Machine call() throws Exception {
                    try {
                        List<Task> tasks = vapp.getTasks();
                        if (tasks.size() > 0) {
                            tasks.get(0).waitForTask(waitTimeInMilliSeconds);
                        }

                        // Deploying the Instantiated vApp
                        VcdCloudProviderConnectorFactory.logger.info("Deploying " + vapp.getResource().getName());
                        vapp.deploy(false, 1000000, false).waitForTask(waitTimeInMilliSeconds);

                        // refresh the vapp
                        Vapp vapp2 = Vapp.getVappByReference(VcdCloudProviderConnector.this.vcloudClient, vapp.getReference());

                        VcdCloudProviderConnectorFactory.logger.info("nbr de vms: " + vapp2.getChildrenVms().size());
                        List<VM> childVms = vapp2.getChildrenVms();
                        for (VM childVm : childVms) {
                            VcdCloudProviderConnectorFactory.logger.info("  vm: " + childVm.getResource().getName());
                        }
                        VcdCloudProviderConnector.this.fromVmToMachine(vapp2.getChildrenVms().get(0), machine);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return machine;
                }
            };
            ListenableFuture<Machine> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "add", result);
        }

        private void configureVirtualHardware_Back2(final Vapp vapp, final Map<String, MachineTemplate> machineTemplateMap)
            throws VCloudException, TimeoutException {
            for (VM childVm : vapp.getChildrenVms()) {
                MachineTemplate mt = machineTemplateMap.get(childVm.getResource().getName());
                MachineConfiguration mc = mt.getMachineConfiguration();
                if (mc == null) {
                    continue;
                }

                VirtualHardwareSectionType virtualHardwareSectionType = childVm.getVirtualHardwareSection();
                if (virtualHardwareSectionType == null) {
                    virtualHardwareSectionType = new VirtualHardwareSectionType();
                }

                for (RASDType item : virtualHardwareSectionType.getItem()) {
                    int type = Integer.parseInt(item.getResourceType().getValue());
                    VcdCloudProviderConnectorFactory.logger.info("- virtualHardwareItemType: " + type + ", "
                        + item.getDescription().getValue());

                    switch (type) {
                    case Constants.RASD_RESOURCETYPE_CPU: {
                        VcdCloudProviderConnectorFactory.logger.info("  CPU: " + item.getVirtualQuantity().getValue() + ", "
                            + childVm.getCpu().getNoOfCpus() + ", " + mc.getCpu());
                        VirtualCpu virtualCpuItem = childVm.getCpu();
                        if (mc.getCpu() > 0) {
                            if (virtualCpuItem.getNoOfCpus() != mc.getCpu()) {
                                VcdCloudProviderConnectorFactory.logger.info("  CPU updated");
                                virtualCpuItem.setNoOfCpus(mc.getCpu());
                                childVm.updateCpu(virtualCpuItem).waitForTask(
                                    VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                            }
                        }
                        break;
                    }
                    case Constants.RASD_RESOURCETYPE_RAM: {
                        if (mc.getMemory() > 0) {
                            VcdCloudProviderConnectorFactory.logger.info("  RAM: " + item.getVirtualQuantity().getValue()
                                + "MB, " + childVm.getMemory().getMemorySize() + "MB, " + mc.getMemory() + "KB");
                            long memoryInMBytes = mc.getMemory() / 1024;
                            VirtualMemory virtualMemoryItemInMBytes = childVm.getMemory();
                            if (virtualMemoryItemInMBytes.getMemorySize().longValue() != memoryInMBytes) {
                                VcdCloudProviderConnectorFactory.logger.info("  RAM updated");
                                virtualMemoryItemInMBytes.setMemorySize(BigInteger.valueOf(memoryInMBytes));
                                childVm.updateMemory(virtualMemoryItemInMBytes).waitForTask(
                                    VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                            }
                        }
                        break;
                    }
                    default:
                        // Not handled
                    }

                    // TODO: disks
                    //
                    // Add virtual disk if needed
                    //
                    /*for (DiskTemplate disk : mc.getDiskTemplates()) {

                    }*/

                    /*if (sectionHasChanged) {
                        childVm.updateSection(virtualHardwareSectionType).waitForTask(
                            VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
                    }*/
                }
            }
        }

        private void configureVirtualHardware_Back(final Vapp vapp, final Map<String, MachineTemplate> machineTemplateMap)
            throws VCloudException, TimeoutException {
            List<VM> childVms = vapp.getChildrenVms();
            for (VM childVm : childVms) {
                MachineTemplate mt = machineTemplateMap.get(childVm.getResource().getName());
                MachineConfiguration mc = mt.getMachineConfiguration();
                if (mc == null) {
                    continue;
                }

                boolean sectionHasChanged = false;
                VirtualHardwareSectionType virtualHardwareSectionType = childVm.getVirtualHardwareSection();
                if (virtualHardwareSectionType == null) {
                    virtualHardwareSectionType = new VirtualHardwareSectionType();
                }

                for (RASDType item : virtualHardwareSectionType.getItem()) {
                    int type = Integer.parseInt(item.getResourceType().getValue());
                    VcdCloudProviderConnectorFactory.logger.info("- virtualHardwareItemType: " + type + ", "
                        + item.getDescription().getValue());

                    switch (type) {
                    case Constants.RASD_RESOURCETYPE_CPU: {
                        VcdCloudProviderConnectorFactory.logger.info("  CPU: " + item.getVirtualQuantity().getValue() + ", "
                            + childVm.getCpu().getNoOfCpus() + ", " + mc.getCpu());
                        if (mc.getCpu() > 0) {
                            if (childVm.getCpu().getNoOfCpus() != mc.getCpu()) {
                                CimUnsignedLong newValue = new CimUnsignedLong();
                                newValue.setValue(BigInteger.valueOf(mc.getCpu()));
                                item.setVirtualQuantity(newValue);
                                sectionHasChanged = true;
                            }
                        }
                        break;
                    }
                    case Constants.RASD_RESOURCETYPE_RAM: {
                        if (mc.getMemory() > 0) {
                            VcdCloudProviderConnectorFactory.logger.info("  RAM: " + item.getVirtualQuantity().getValue()
                                + "MB, " + childVm.getMemory().getMemorySize() + "MB, " + mc.getMemory() + "KB");
                            long memoryInKBytes = mc.getMemory();
                            long vAppMemoryInKBytes = childVm.getMemory().getMemorySize().longValue() * 1024;
                            if (vAppMemoryInKBytes != memoryInKBytes) {
                                VcdCloudProviderConnectorFactory.logger.info("  RAM updated");
                                CimUnsignedLong newValue = new CimUnsignedLong();
                                newValue.setValue(BigInteger.valueOf(memoryInKBytes));
                                CimString unit = new CimString();
                                unit.setValue(Constants.RASD_ALLOCATION_UNIT_KILOBYTE);
                                item.setVirtualQuantity(newValue);
                                item.setAllocationUnits(unit);
                                sectionHasChanged = true;
                            }
                        }
                        break;
                    }
                    default:
                        // Not handled
                    }

                    // TODO: disks
                    //
                    // Add virtual disk if needed
                    //
                    /*for (DiskTemplate disk : mc.getDiskTemplates()) {

                    }*/

                    if (sectionHasChanged) {
                        childVm.updateSection(virtualHardwareSectionType).waitForTask(0);
                    }
                }
            }
        }
    }
}
