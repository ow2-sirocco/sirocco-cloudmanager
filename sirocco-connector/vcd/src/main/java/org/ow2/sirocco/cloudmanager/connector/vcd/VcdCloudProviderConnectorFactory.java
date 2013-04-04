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
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkTemplate;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
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
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.QueryParams;
import com.vmware.vcloud.sdk.ReferenceResult;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VappNetwork;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
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
import com.vmware.vcloud.sdk.constants.Version;
import com.vmware.vcloud.sdk.constants.query.ExpressionType;
import com.vmware.vcloud.sdk.constants.query.QueryReferenceField;
import com.vmware.vcloud.sdk.constants.query.QueryReferenceType;

@Component(public_factory = false)
@Provides
public class VcdCloudProviderConnectorFactory implements ICloudProviderConnectorFactory {
    /*private static Log logger = LogFactory.getLog(VcdCloudProviderConnectorFactory.class);*/
    private static Logger logger = LoggerFactory.getLogger(VcdCloudProviderConnectorFactory.class);

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

    private class VcdCloudProviderConnector implements ICloudProviderConnector, ISystemService, IComputeService,
        INetworkService {

        // TODO: fix
        // - FakeSSLSocketFactory
        // - CIMI Network: public CIMI network / routed OrgVcdNetwork ; private
        // CIMI network shared between systems
        // - nested vApp?

        private final String cloudProviderId;

        private final CloudProviderAccount cloudProviderAccount;

        private final CloudProviderLocation cloudProviderLocation;

        private VcloudClient vcloudClient;

        private String orgName;

        private Organization org;

        private AdminOrganization adminOrg;

        private String vdcName;

        private Vdc vdc;

        private AdminVdc adminVdc; // FIXME require VCD sysAdmin role

        private String cimiPublicOrgVdcNetworkName;

        private OrgVdcNetwork cimiPublicOrgVdcNetwork;

        private boolean cimiPublicOrgVdcNetworkIsRouted = false;

        public VcdCloudProviderConnector(final CloudProviderAccount cloudProviderAccount,
            final CloudProviderLocation cloudProviderLocation) throws ConnectorException {
            this.cloudProviderId = UUID.randomUUID().toString();
            this.cloudProviderLocation = cloudProviderLocation;
            this.cloudProviderAccount = cloudProviderAccount;

            Map<String, String> properties = cloudProviderAccount.getCloudProvider().getProperties();
            if (properties == null || properties.get("orgName") == null || properties.get("vdcName") == null
                || properties.get("cimiPublicOrgVdcNetworkName") == null) {
                throw new ConnectorException("No access to properties: orgName or vdcName or cimiPublicOrgVdcNetworkName");
            }
            this.orgName = properties.get("orgName");
            this.vdcName = properties.get("vdcName");
            this.cimiPublicOrgVdcNetworkName = properties.get("cimiPublicOrgVdcNetworkName");
            VcdCloudProviderConnectorFactory.logger.info("connect " + cloudProviderAccount.getLogin() + " to Organization="
                + this.orgName + ", VirtualDataCenter=" + this.vdcName + ", cimiPublicOrgVdcNetwork="
                + this.cimiPublicOrgVdcNetworkName);

            try {
                VcloudClient.setLogLevel(Level.OFF);
                // VcloudClient.setLogLevel(Level.INFO);
                this.vcloudClient = new VcloudClient(cloudProviderAccount.getCloudProvider().getEndpoint(), Version.V5_1);
                this.vcloudClient.registerScheme("https", 443, FakeSSLSocketFactory.getInstance());
                String user = cloudProviderAccount.getLogin() + "@" + this.orgName;
                /*String user = "Administrator@System";*/// !!!
                this.vcloudClient.login(user, cloudProviderAccount.getPassword());

                ReferenceType orgRef = this.vcloudClient.getOrgRefByName(this.orgName);
                this.org = Organization.getOrganizationByReference(this.vcloudClient, orgRef);
                ReferenceType adminOrgRef = this.vcloudClient.getVcloudAdmin().getAdminOrgRefByName(this.orgName);
                this.adminOrg = AdminOrganization.getAdminOrgByReference(this.vcloudClient, adminOrgRef);

                ReferenceType vdcRef = this.org.getVdcRefByName(this.vdcName);
                this.vdc = Vdc.getVdcByReference(this.vcloudClient, vdcRef);
                // FIXME require VCD sysAdmin role
                /*ReferenceType adminVdcRef = this.adminOrg.getAdminVdcRefByName(this.vdcName);
                this.adminVdc = AdminVdc.getAdminVdcByReference(this.vcloudClient, adminVdcRef);*/

                VcdCloudProviderConnectorFactory.logger.info("--- " + this.vdc.getAvailableNetworkRefsByName());
                ReferenceType orgVdcNetworkNameRef = this.vdc.getAvailableNetworkRefByName(this.cimiPublicOrgVdcNetworkName);
                this.cimiPublicOrgVdcNetwork = OrgVdcNetwork.getOrgVdcNetworkByReference(this.vcloudClient,
                    orgVdcNetworkNameRef);
                /*VcdCloudProviderConnectorFactory.logger.info("--- publicOrgVdcNetwork=" + this.cimiPublicOrgVdcNetwork.getResource().getName());*/

                // test Bridged/Nated
                if (this.cimiPublicOrgVdcNetwork.getResource().getConfiguration().getFenceMode()
                    .equals(FenceModeValuesType.NATROUTED.value())) {
                    this.cimiPublicOrgVdcNetworkIsRouted = true;
                } else if (this.cimiPublicOrgVdcNetwork.getResource().getConfiguration().getFenceMode()
                    .equals(FenceModeValuesType.BRIDGED.value())) {
                    this.cimiPublicOrgVdcNetworkIsRouted = false;
                } else {
                    throw new ConnectorException("cimiPublicOrgVdcNetwork type="
                        + this.cimiPublicOrgVdcNetwork.getResource().getConfiguration().getFenceMode()
                        + " : should be equal to Direct or Routed");
                }
                VcdCloudProviderConnectorFactory.logger.info("--- publicOrgVdcNetwork="
                    + this.cimiPublicOrgVdcNetwork.getResource().getName() + ", isRouted="
                    + this.cimiPublicOrgVdcNetworkIsRouted);

                // ----
                // this.logAdminOrgVdcNetwork();
                // this.logEdgeGatewayByQuery();
                // this.logEdgeGateway();

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
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public IImageService getImageService() throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public INetworkService getNetworkService() throws ConnectorException {
            return this;
        }

        @Override
        public ISystemService getSystemService() throws ConnectorException {
            return this;
        }

        @Override
        public IProviderCapability getProviderCapability() throws ConnectorException {
            throw new ConnectorException("unsupported operation");
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
                    VappNetwork vappNetwork = VappNetwork
                        .getVappNetworkByReference(this.vcloudClient, vappNetworkReferenceType);
                    SystemNetwork systemNetwork = new SystemNetwork();
                    this.fromVAppNetworkToSystemNetwork(vappNetwork, systemNetwork);
                    if (!systemNetwork.getNetwork().getNetworkType().equals(Network.Type.PRIVATE)) {
                        continue;
                    }
                    system.getNetworks().add(systemNetwork);
                }
            } catch (VCloudException e) {
                throw new ConnectorException(e);
            }

            system.setVolumes(new ArrayList<SystemVolume>());
            system.setSystems(new ArrayList<SystemSystem>());
            system.setCredentials(new ArrayList<SystemCredentials>());
        }

        @Override
        public Job createSystem(final SystemCreate systemCreate) throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("creating system ");
            final int waitTimeInMilliSeconds = VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
            final Map<String, MachineTemplate> machineTemplateMap = new HashMap<String, MachineTemplate>();
            final System system = new System();

            // create the vApp
            final Vapp vapp;
            try {
                // VcloudClient.setLogLevel(Level.CONFIG); // !!!
                vapp = VcdCloudProviderConnector.this.createVapp(systemCreate, machineTemplateMap);
                VcdCloudProviderConnector.this.fromvAppToSystem(vapp, system);
                system.setState(System.State.CREATING);
            } catch (Exception ex) {
                throw new ConnectorException(ex);
            }

            final Callable<System> createTask = new Callable<System>() {
                @Override
                public System call() throws Exception {
                    try {
                        // VcloudClient.setLogLevel(Level.OFF); // !!!
                        VcdCloudProviderConnectorFactory.logger.info("start Job");
                        List<Task> tasks = vapp.getTasks();
                        if (tasks.size() > 0) { // TODO wait for all tasks
                            tasks.get(0).waitForTask(waitTimeInMilliSeconds);
                        }
                        // VcloudClient.setLogLevel(Level.INFO); // !!!

                        // test
                        /*VcdCloudProviderConnectorFactory.logger.info("update NetworkConfigSectionType ");
                        NetworkConfigSectionType n = VcdCloudProviderConnector.this.createDefaultNetworkConfigSectionType(
                            VcdCloudProviderConnector.this.vdc, FenceModeValuesType.NATROUTED.value());
                        vapp.updateSection(n).waitForTask(waitTimeInMilliSeconds);*/

                        // configure vms
                        VcdCloudProviderConnector.this.configureVmSections(vapp, machineTemplateMap);

                        // Deploying the Instantiated vApp
                        /*VcdCloudProviderConnectorFactory.logger.info("Deploying " + vapp.getResource().getName());
                        vapp.deploy(false, 1000000, true).waitForTask(waitTimeInMilliSeconds);*/

                        // refresh the vapp (otherwise no childrenVms is
                        // visible! - TBC)
                        Vapp vappFresh = Vapp.getVappByReference(VcdCloudProviderConnector.this.vcloudClient,
                            vapp.getReference());
                        /*for (VM childVm : vappFresh.getChildrenVms()) {
                            VcdCloudProviderConnectorFactory.logger.info("deploying  vm: " + childVm.getResource().getName());
                            childVm.deploy(false, 1000000, true).waitForTask(waitTimeInMilliSeconds);
                        }*/
                        VcdCloudProviderConnector.this.fromvAppToSystem(vappFresh, system);
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
                        if (vapp.isDeployed()) {
                            VcdCloudProviderConnectorFactory.logger.info("Undeploying " + vapp.getResource().getName());
                            if (vapp.getVappStatus() == VappStatus.POWERED_ON) {
                                vapp.undeploy(UndeployPowerActionType.POWEROFF).waitForTask(waitTimeInMilliSeconds);
                            } else {
                                vapp.undeploy(UndeployPowerActionType.DEFAULT).waitForTask(waitTimeInMilliSeconds);
                            }
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
            // VcloudClient.setLogLevel(Level.INFO); // !!!
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
        public Job deleteEntityInSystem(final String systemId, final String entityId, final String entityType)
            throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("deleting Entity with providerAssignedId " + entityId + " and type="
                + entityType + ", in system with providerAssignedId=" + systemId);
            final System system = this.getSystem(systemId);

            if (entityType.equals(SystemMachine.class.getName())) {
                for (SystemMachine sm : system.getMachines()) {
                    if (sm.getResource().getProviderAssignedId().equals(entityId)) {
                        return this.deleteMachineInSystem(system, (Machine) sm.getResource());
                    }
                }
            } else {
                throw new ConnectorException("unsupported entity type: " + entityType);
            }

            throw new ConnectorException("entity " + entityId + " not found in system " + systemId);
        }

        private Job deleteMachineInSystem(final System system, final Machine machine) throws ConnectorException {
            final int waitTimeInMilliSeconds = VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
            // final Machine machine = this.getMachine(machineId);
            machine.setState(Machine.State.DELETING);
            system.setState(System.State.MIXED);

            final Callable<System> createTask = new Callable<System>() {
                @Override
                public System call() throws Exception {
                    try {
                        VM vm = VcdCloudProviderConnector.this.getVmByProviderAssignedId(machine.getProviderAssignedId());
                        if (vm.isDeployed()) {
                            VcdCloudProviderConnectorFactory.logger.info("Undeploying " + vm.getResource().getName());
                            if (vm.getVMStatus() == VMStatus.POWERED_ON) {
                                vm.undeploy(UndeployPowerActionType.POWEROFF).waitForTask(waitTimeInMilliSeconds);
                            } else {
                                vm.undeploy(UndeployPowerActionType.DEFAULT).waitForTask(waitTimeInMilliSeconds);
                            }
                        }
                        VcdCloudProviderConnectorFactory.logger.info("deleting " + vm.getResource().getName());
                        vm.delete().waitForTask(waitTimeInMilliSeconds);
                        VcdCloudProviderConnector.this.fromvAppToSystem(
                            VcdCloudProviderConnector.this.getVappByProviderAssignedId(system.getProviderAssignedId()), system);
                        machine.setState(Machine.State.DELETED);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return system;
                }
            };
            ListenableFuture<System> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(system, machine, "delete", result);
        }

        @Override
        public Job removeEntityFromSystem(final String systemId, final String entityId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job addEntityToSystem(final String systemId, final String entityId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
            // TODO
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

                    Vapp parentVapp = Vapp.getVappByReference(this.vcloudClient, vm.getParentVappReference());
                    VappNetwork vappNetwork = this.getVappNetworkByName(parentVapp, networkConnection.getNetwork());
                    Network network = new Network();
                    this.fromVappNetworkToNetwork(vappNetwork, network);

                    if (networkConnection.getIpAddress() != null && networkConnection.getIpAddress() != "") {
                        Address cimiAddress = new Address();
                        cimiAddress.setIp(networkConnection.getIpAddress());
                        cimiAddress.setAllocation(cimiIpAddressAllocationMode);
                        cimiAddress.setProtocol("IPv4");
                        cimiAddress.setNetwork(network);
                        // cimiAddress.setHostName(???); // ???
                        cimiAddress.setResource(null);

                        List<MachineNetworkInterfaceAddress> cimiAddresses = new ArrayList<MachineNetworkInterfaceAddress>();
                        MachineNetworkInterfaceAddress entry = new MachineNetworkInterfaceAddress();
                        entry.setAddress(cimiAddress);
                        cimiAddresses.add(entry);
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

        @Override
        public Job createMachine(final MachineCreate machineCreate) throws ConnectorException {
            VcdCloudProviderConnectorFactory.logger.info("creating machine ");
            final int waitTimeInMilliSeconds = VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
            final Map<String, MachineTemplate> machineTemplateMap = new HashMap<String, MachineTemplate>();
            final Machine machine = new Machine();

            // create the vApp
            final Vapp vapp;
            try {
                vapp = VcdCloudProviderConnector.this.createVapp(machineCreate, machineTemplateMap);

                List<Task> tasks = vapp.getTasks();
                if (tasks.size() > 0) { // wait for all tasks
                    tasks.get(0).waitForTask(waitTimeInMilliSeconds);
                }

                // refresh the vapp (otherwise no childrenVms is visible! - TBC)
                Vapp vappFresh = Vapp.getVappByReference(VcdCloudProviderConnector.this.vcloudClient, vapp.getReference());
                /*VcdCloudProviderConnectorFactory.logger.info("nbr de vms: " + vappFresh.getChildrenVms().size());
                for (VM childVm : vappFresh.getChildrenVms()) {
                    VcdCloudProviderConnectorFactory.logger.info("  vm: " + childVm.getResource().getName());
                }*/
                if (vappFresh.getChildrenVms().size() != 1) {
                    throw new ConnectorException("only one vm is expected!");
                }

                VcdCloudProviderConnector.this.fromVmToMachine(vappFresh.getChildrenVms().get(0), machine);
                machine.setState(Machine.State.CREATING);

            } catch (Exception ex) {
                throw new ConnectorException(ex);
            }

            final Callable<Machine> createTask = new Callable<Machine>() {
                @Override
                public Machine call() throws Exception {
                    try {
                        VcdCloudProviderConnectorFactory.logger.info("start Job");

                        // configure vms
                        VcdCloudProviderConnector.this.configureVmSections(vapp, machineTemplateMap);

                        // Deploying the Instantiated vApp
                        /*VcdCloudProviderConnectorFactory.logger.info("Deploying " + vapp.getResource().getName());
                        vapp.deploy(false, 1000000, true).waitForTask(waitTimeInMilliSeconds);*/

                        // refresh the vapp (otherwise no childrenVms is
                        // visible! - TBC)
                        Vapp vappFresh = Vapp.getVappByReference(VcdCloudProviderConnector.this.vcloudClient,
                            vapp.getReference());
                        /*for (VM childVm : vappFresh.getChildrenVms()) {
                            VcdCloudProviderConnectorFactory.logger.info("deploying  vm: " + childVm.getResource().getName());
                            childVm.deploy(false, 1000000, true).waitForTask(waitTimeInMilliSeconds);
                        }*/
                        VcdCloudProviderConnector.this.fromVmToMachine(vappFresh.getChildrenVms().get(0), machine);
                    } catch (Exception ex) {
                        throw new ConnectorException(ex);
                    }
                    return machine;
                }
            };
            ListenableFuture<Machine> result = VcdCloudProviderConnectorFactory.this.executorService.submit(createTask);
            return VcdCloudProviderConnectorFactory.this.jobManager.newJob(machine, null, "add", result);
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

        @Override
        public Job deleteMachine(final String machineId) throws ConnectorException {
            // !!!! This method should only be used with isolated machine !!!!
            final int waitTimeInMilliSeconds = VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS;
            VcdCloudProviderConnectorFactory.logger.info("deleting machine with providerAssignedId " + machineId);
            final Machine machine = this.getMachine(machineId);
            machine.setState(Machine.State.DELETING);

            final Callable<Machine> createTask = new Callable<Machine>() {
                // This method only works with isolated machine!
                @Override
                public Machine call() throws Exception {
                    try {
                        VM vm = VcdCloudProviderConnector.this.getVmByProviderAssignedId(machineId);

                        /*VcdCloudProviderConnectorFactory.logger.info("Undeploying " + vm.getResource().getName());
                        if (vm.getVMStatus() == VMStatus.POWERED_ON) {
                            vm.undeploy(UndeployPowerActionType.POWEROFF).waitForTask(waitTimeInMilliSeconds);
                        } else {
                            vm.undeploy(UndeployPowerActionType.DEFAULT).waitForTask(waitTimeInMilliSeconds);
                        }
                        VcdCloudProviderConnectorFactory.logger.info("deleting " + vm.getResource().getName());
                        vm.delete().waitForTask(waitTimeInMilliSeconds);*/

                        vm.getParentVappReference();
                        Vapp vapp = Vapp.getVappByReference(VcdCloudProviderConnector.this.vcloudClient,
                            vm.getParentVappReference());
                        if (vapp.getChildrenVms().size() != 1) {
                            // check if the vapp has one and only one vm
                            // otherwise it is not an isolated machine!
                            throw new ConnectorException("only one vm is expected!");
                        }
                        if (vapp.isDeployed()) {
                            VcdCloudProviderConnectorFactory.logger.info("Undeploying " + vapp.getResource().getName());
                            if (vapp.getVappStatus() == VappStatus.POWERED_ON) {
                                vapp.undeploy(UndeployPowerActionType.POWEROFF).waitForTask(waitTimeInMilliSeconds);
                            } else {
                                vapp.undeploy(UndeployPowerActionType.DEFAULT).waitForTask(waitTimeInMilliSeconds);
                            }
                        }
                        VcdCloudProviderConnectorFactory.logger.info("deleting " + vapp.getResource().getName());
                        vapp.delete().waitForTask(waitTimeInMilliSeconds);

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
            if (vappNetwork.getResource().getConfiguration().getFenceMode().equals(FenceModeValuesType.ISOLATED.value())) {
                n.setNetworkType(Network.Type.PRIVATE);
            } else {
                n.setNetworkType(Network.Type.PUBLIC);
                /* FIXME consider any other vAppNetwork as a Public CIMI network for the moment */
                /* NB: future release might use vAppNetwork with fence mode=bridged for shared Private CIMI Networks between Systems)*/
            }
        }

        @Override
        public Job createNetwork(final NetworkCreate networkCreate) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Network getNetwork(final String networkId) throws ConnectorException {
            throw new ConnectorException("unsupported operation"); // TODO
        }

        @Override
        public List<Network> getNetworks() throws ConnectorException {
            ArrayList<Network> networks = new ArrayList<Network>();

            Network cimiPublicNetwork = new Network();
            // cimiPublicNetwork.setName(this.cimiPublicOrgVdcNetworkName);
            cimiPublicNetwork.setName(this.cimiPublicOrgVdcNetwork.getResource().getName());
            cimiPublicNetwork.setProviderAssignedId(this.cimiPublicOrgVdcNetwork.getResource().getHref());
            cimiPublicNetwork.setState(Network.State.STARTED);
            cimiPublicNetwork.setNetworkType(Network.Type.PUBLIC);
            networks.add(cimiPublicNetwork);

            // TODO add private networks

            return networks;
        }

        @Override
        public Job deleteNetwork(final String networkId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job startNetwork(final String networkId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job stopNetwork(final String networkId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job createNetworkPort(final NetworkPortCreate networkPortCreate) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public NetworkPort getNetworkPort(final String networkPortId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job deleteNetworkPort(final String networkPortId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job startNetworkPort(final String networkPortId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job stopNetworkPort(final String networkPortId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job createForwardingGroup(final ForwardingGroupCreate forwardingGroupCreate) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public ForwardingGroup getForwardingGroup(final String forwardingGroupId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job deleteForwardingGroup(final String forwardingGroupId) throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job addNetworkToForwardingGroup(final String forwardingGroupId, final ForwardingGroupNetwork fgNetwork)
            throws ConnectorException {
            throw new ConnectorException("unsupported operation");
        }

        @Override
        public Job removeNetworkFromForwardingGroup(final String forwardingGroupId, final String networkId)
            throws ConnectorException {
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

        private VappNetwork getVappNetworkByProviderAssignedId(final String id) throws ConnectorException {
            try {
                ReferenceType vappNetworkRef = new ReferenceType();
                vappNetworkRef.setHref(id);
                return VappNetwork.getVappNetworkByReference(this.vcloudClient, vappNetworkRef);
            } catch (VCloudException e) {
                throw new ConnectorException(e);
            }
        }

        private VappNetwork getVappNetworkByName(final Vapp vapp, final String networkName) throws ConnectorException {
            try {
                ReferenceType vappNetworkReferenceType = vapp.getVappNetworkRefsByName().get(networkName);
                VappNetwork vappNetwork = VappNetwork.getVappNetworkByReference(this.vcloudClient, vappNetworkReferenceType);
                return vappNetwork;
            } catch (VCloudException e) {
                throw new ConnectorException(e);
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
            // TODO: startup, lease... sections (vs CIMI?)

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
                    String idKey = "vcd";
                    String vmTmplRef = mt.getMachineImage().getProperties().get(idKey);
                    if (vmTmplRef == null) {
                        throw new ConnectorException("Cannot find vAppTemplate/vm Id for key " + idKey);
                    }
                    source.setHref(vmTmplRef);
                    item.setSource(source);

                    // Configure connection
                    /*FIXME remove unused CIMI network ? ; set nic index (vs.CIMI ?)*/
                    NetworkConnectionSectionType networkConnectionSectionType = new NetworkConnectionSectionType();
                    networkConnectionSectionType.setInfo(new MsgType());
                    int networkConnectionIndex = 0;
                    for (MachineTemplateNetworkInterface nic : mt.getNetworkInterfaces()) {
                        NetworkConnectionType networkConnectionType = new NetworkConnectionType();
                        /*if (nic.getNetwork() != null) {
                            networkConnectionType.setNetwork(nic.getNetwork().getName()); 
                        } else if (nic.getNetworkTemplateComponentDescriptor() != null) {
                            NetworkTemplate nt = (NetworkTemplate) nic.getNetworkTemplateComponentDescriptor()
                                .getComponentTemplate();
                            networkConnectionType.setNetwork(nt.getName());
                        } else {
                            throw new ConnectorException(
                                "validation error on a nic template: configuration should refer either to a Network ressource xor a NetworkTemplateComponentDescriptor");
                        }*/
                        if (nic.getNetwork() != null) {
                            if (nic.getNetwork().getNetworkType() == Network.Type.PUBLIC) {
                                networkConnectionType.setNetwork(this.cimiPublicOrgVdcNetworkName);
                            } else {
                                VappNetwork vappNetwork = this.getVappNetworkByProviderAssignedId(nic.getNetwork()
                                    .getProviderAssignedId());
                                networkConnectionType.setNetwork(vappNetwork.getResource().getName());
                            }
                        } else if (nic.getSystemNetworkName() != null) {
                            networkConnectionType.setNetwork(nic.getSystemNetworkName());
                        } else {
                            throw new ConnectorException(
                                "validation error on nic template : should refer either to a Network ressource xor a SystemNetworkName");
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
            return this.vdc.composeVapp(composeVAppParamsType);
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

        private InstantiationParamsType createVappInstantiationParamsType(final SystemCreate systemCreate)
            throws ConnectorException {

            // add CIMI Public Network
            VAppNetworkConfigurationType vAppNetworkConfigurationType = new VAppNetworkConfigurationType();
            NetworkConfigurationType networkConfigurationType = new NetworkConfigurationType();
            VcdCloudProviderConnectorFactory.logger
                .info("vAppNetworkConfiguration Bridged:" + this.cimiPublicOrgVdcNetworkName);
            vAppNetworkConfigurationType.setNetworkName(this.cimiPublicOrgVdcNetworkName);
            networkConfigurationType.setParentNetwork(this.vdc.getAvailableNetworkRefByName(this.cimiPublicOrgVdcNetworkName));
            networkConfigurationType.setFenceMode(FenceModeValuesType.BRIDGED.value());
            networkConfigurationType.setRetainNetInfoAcrossDeployments(true);
            vAppNetworkConfigurationType.setConfiguration(networkConfigurationType);
            // fill in the NetworkConfigSection
            NetworkConfigSectionType networkConfigSectionType = new NetworkConfigSectionType();
            MsgType networkInfo = new MsgType();
            networkConfigSectionType.setInfo(networkInfo);
            List<VAppNetworkConfigurationType> vAppNetworkConfigs = networkConfigSectionType.getNetworkConfig();
            vAppNetworkConfigs.add(vAppNetworkConfigurationType);

            // add CIMI Private Network
            Set<ComponentDescriptor> networkComponentDescriptors = this.getComponentDescriptorsOfType(systemCreate,
                ComponentType.NETWORK);
            for (ComponentDescriptor ncd : networkComponentDescriptors) {
                NetworkTemplate nt = (NetworkTemplate) ncd.getComponentTemplate();
                if (ncd.getComponentQuantity() != 1) {
                    throw new ConnectorException(
                        "validation error on field 'Network componentDescriptor.quantity': should be equal to 1");
                }
                // FIXME support only CIMI private (system) network at the
                // moment
                if (nt.getNetworkConfig().getNetworkType() != Network.Type.PRIVATE) {
                    throw new ConnectorException(
                        "validation error on field 'Network componentDescriptor.networkTemplate.networkConfiguration.networkType': should be equal to Private");
                }
                VAppNetworkConfigurationType private_vAppNetworkConfigurationType = new VAppNetworkConfigurationType();
                NetworkConfigurationType private_networkConfigurationType = new NetworkConfigurationType();
                VcdCloudProviderConnectorFactory.logger.info("vAppNetworkConfiguration Isolated:" + ncd.getName());
                private_vAppNetworkConfigurationType.setNetworkName(ncd.getName());
                // private_networkConfigurationType.setParentNetwork(vdc.getAvailableNetworkRefByName(this.cimiPublicOrgVdcNetworkName));
                private_networkConfigurationType.setFenceMode(FenceModeValuesType.ISOLATED.value());
                private_networkConfigurationType.setRetainNetInfoAcrossDeployments(true);

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
                private_networkConfigurationType.setIpScopes(ipScopes);

                private_vAppNetworkConfigurationType.setConfiguration(private_networkConfigurationType);
                vAppNetworkConfigs.add(private_vAppNetworkConfigurationType);
            }

            // fill in InstantiationParams
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
            /*VcdCloudProviderConnectorFactory.logger.info("Configuring VM Ip Addressing Mode");
            VcdCloudProviderConnector.this.configureVMsDefaultIPAddressingMode(vapp);*/
            // VcdCloudProviderConnector.this.configureVMsNatIPAddressingMode(vapp);

            // set user data
            VcdCloudProviderConnectorFactory.logger.info("Configuring user data");
            VcdCloudProviderConnector.this.configureUserData(vapp, machineTemplateMap);

            // set guest customization
            VcdCloudProviderConnectorFactory.logger.info("Configuring guest customization");
            VcdCloudProviderConnector.this.configureGuestCustomization(vapp);

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

                // configure RAM if needed
                if (mc.getMemory() > 0) {
                    VcdCloudProviderConnectorFactory.logger
                        .info("  Memory Size: " + childVm.getMemory().getMemorySize() + "MB");
                    long memoryInMBytes = mc.getMemory() / 1024; /* CIMI: kibibytes*/
                    VirtualMemory virtualMemoryItemInMBytes = childVm.getMemory();
                    if (virtualMemoryItemInMBytes.getMemorySize().longValue() != memoryInMBytes) {
                        VcdCloudProviderConnectorFactory.logger.info("  -> updating: " + memoryInMBytes + " MB");
                        virtualMemoryItemInMBytes.setMemorySize(BigInteger.valueOf(memoryInMBytes));
                        childVm.updateMemory(virtualMemoryItemInMBytes).waitForTask(
                            VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
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

        private void configureGuestCustomization(final Vapp vapp) throws VCloudException, TimeoutException {
            for (VM childVm : vapp.getChildrenVms()) {
                // FIXME: GuestCustomization (v 1.5)
                GuestCustomizationSectionType guestCustomizationSection = childVm.getGuestCustomizationSection();
                guestCustomizationSection.setEnabled(true);
                childVm.updateSection(guestCustomizationSection).waitForTask(0);
            }
        }

        //
        // TMP
        //

        private void logAdminOrgVdcNetwork() throws VCloudException, ConnectorException {
            /*ReferenceType adminOrgRef = this.vcloudClient.getVcloudAdmin().getAdminOrgRefByName(orgName);
            AdminOrganization adminOrg = AdminOrganization.getAdminOrgByReference(this.vcloudClient, adminOrgRef);*/
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
            ReferenceType adminVdcRef = this.adminOrg.getAdminVdcRefByName(this.vdcName);
            AdminVdc adminVdc2 = AdminVdc.getAdminVdcByReference(this.vcloudClient, adminVdcRef); // !!!
            for (ReferenceType adminOrgVdcNetworkRef : adminVdc2.getOrgVdcNetworkRefs().getReferences()) {
                AdminOrgVdcNetwork adminOrgVdcNetwork2 = AdminOrgVdcNetwork.getOrgVdcNetworkByReference(this.vcloudClient,
                    adminOrgVdcNetworkRef);
                VcdCloudProviderConnectorFactory.logger.info("adminOrgVdcNetwork2 name="
                    + adminOrgVdcNetwork2.getResource().getName());
                if (adminOrgVdcNetwork2.getResource().getName().equals(this.cimiPublicOrgVdcNetworkName)) {
                    adminOrgVdcNetwork = adminOrgVdcNetwork2;
                }
            }
            if (adminOrgVdcNetwork == null) {
                throw new ConnectorException("No OrgVdcNetwork : " + this.cimiPublicOrgVdcNetworkName);
            }

            VcdCloudProviderConnectorFactory.logger.info("adminOrgVdcNetwork name="
                + adminOrgVdcNetwork.getResource().getName());
            if (adminOrgVdcNetwork.getResource().getConfiguration() != null) {
                VcdCloudProviderConnectorFactory.logger.info("adminOrgVdcNetwork has Configuration");
                if (adminOrgVdcNetwork.getResource().getConfiguration().getFeatures() != null) {
                    VcdCloudProviderConnectorFactory.logger.info("adminOrgVdcNetwork Configuration has features");
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
            Filter filter = new Filter(new Expression(QueryReferenceField.NAME, "Edge-OPW", ExpressionType.EQUALS));
            params.setFilter(filter);
            ReferenceResult result = this.vcloudClient.getQueryService()
                .queryReferences(QueryReferenceType.EDGEGATEWAY, params);
            if (result.getReferences().size() == 0) {
                throw new ConnectorException("No edgeGateway : " + "Edge-OPW");
            }
            EdgeGateway edgeGateway = EdgeGateway.getEdgeGatewayByReference(this.vcloudClient, result.getReferences().get(0));
            VcdCloudProviderConnectorFactory.logger.info("edgeGateway name=" + edgeGateway.getResource().getName());

            if (edgeGateway.getResource().getConfiguration() != null) {
                VcdCloudProviderConnectorFactory.logger.info("edgeGateway has Configuration");
                if (edgeGateway.getResource().getConfiguration().getEdgeGatewayServiceConfiguration() != null) {
                    VcdCloudProviderConnectorFactory.logger.info("edgeGateway Configuration has features");
                    for (JAXBElement<? extends NetworkServiceType> jaxbElement : edgeGateway.getResource().getConfiguration()
                        .getEdgeGatewayServiceConfiguration().getNetworkService()) {
                        this.logNetworkService(jaxbElement);
                    }
                }
            }
        }

        private void logEdgeGateway() throws VCloudException, ConnectorException {
            /*ReferenceType adminOrgRef = this.vcloudClient.getVcloudAdmin().getAdminOrgRefByName(orgName);
            AdminOrganization adminOrg = AdminOrganization.getAdminOrgByReference(this.vcloudClient, adminOrgRef);*/
            ReferenceType adminVdcRef = this.adminOrg.getAdminVdcRefByName(this.vdcName);
            AdminVdc adminVdc2 = AdminVdc.getAdminVdcByReference(this.vcloudClient, adminVdcRef); // !!!
            EdgeGateway edgeGateway = null;
            for (ReferenceType edgeGatewayRef : adminVdc2.getEdgeGatewayRefs().getReferences()) {
                EdgeGateway edgeGateway2 = EdgeGateway.getEdgeGatewayByReference(this.vcloudClient, edgeGatewayRef);
                VcdCloudProviderConnectorFactory.logger.info("edgeGateway name=" + edgeGateway2.getResource().getName());
                if (edgeGateway2.getResource().getName().equals("EdgeG")) {
                    edgeGateway = edgeGateway2;
                }
            }
            if (edgeGateway == null) {
                throw new ConnectorException("No edgeGateway : " + "EdgeG");
            }

            if (edgeGateway.getResource().getConfiguration() != null) {
                VcdCloudProviderConnectorFactory.logger.info("edgeGateway has Configuration");
                if (edgeGateway.getResource().getConfiguration().getEdgeGatewayServiceConfiguration() != null) {
                    VcdCloudProviderConnectorFactory.logger.info("edgeGateway Configuration has features");
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
                VcdCloudProviderConnectorFactory.logger.info("FW " + jaxbElement.getValue() + "\n isIsEnabled "
                    + firewallService.isIsEnabled() + "\n getDefaultAction " + firewallService.getDefaultAction()
                    + "\n isLogDefaultAction " + firewallService.isLogDefaultAction() + "\n Number of FW rules="
                    + firewallService.getFirewallRule().size());
                for (FirewallRuleType firewallRule : firewallService.getFirewallRule()) {
                    VcdCloudProviderConnectorFactory.logger.info("  FW rule description" + firewallRule.getDescription()
                        + "\n  FW rule policy" + firewallRule.getPolicy() + "\n");
                    /*VcdCloudProviderConnectorFactory.logger.info("  FW rule policy" + firewallRule.getPolicy());
                    VcdCloudProviderConnectorFactory.logger.info("  ");*/
                }
            }
            if (jaxbElement.getValue() instanceof NatServiceType) {
                NatServiceType natService = (NatServiceType) jaxbElement.getValue();
                VcdCloudProviderConnectorFactory.logger.info("NAT " + jaxbElement.getValue() + "\n isIsEnabled "
                    + natService.isIsEnabled() + "\n getNatType " + natService.getNatType() + "\n getPolicy "
                    + natService.getPolicy() + "\n getExternalIp " + natService.getExternalIp() + "\n Number of NAT rules="
                    + natService.getNatRule().size());
                for (NatRuleType natRule : natService.getNatRule()) {
                    VcdCloudProviderConnectorFactory.logger.info("  NAT rule description " + natRule.getDescription()
                        + "\n  NAT rule getRuleType " + natRule.getRuleType() + "\n  NAT rule isIsEnabled "
                        + natRule.isIsEnabled() + "\n  NAT rule getId " + natRule.getId());
                    /*VcdCloudProviderConnectorFactory.logger.info("  NAT rule getRuleType " + natRule.getRuleType());
                    VcdCloudProviderConnectorFactory.logger.info("  NAT rule isIsEnabled " + natRule.isIsEnabled());
                    VcdCloudProviderConnectorFactory.logger.info("  NAT rule getId " + natRule.getId());*/
                    if (natRule.getGatewayNatRule() != null) {
                        VcdCloudProviderConnectorFactory.logger.info("   NAT rule getGatewayNatRule "
                            + natRule.getGatewayNatRule() + "\n   NAT rule getIcmpSubType "
                            + natRule.getGatewayNatRule().getIcmpSubType() + "\n   NAT rule getOriginalIp "
                            + natRule.getGatewayNatRule().getOriginalIp() + "\n   NAT rule getTranslatedIp "
                            + natRule.getGatewayNatRule().getTranslatedIp() + "\n   NAT rule getOriginalPort "
                            + natRule.getGatewayNatRule().getOriginalPort() + "\n   NAT rule getTranslatedPort "
                            + natRule.getGatewayNatRule().getTranslatedPort() + "\n   NAT rule getProtocol "
                            + natRule.getGatewayNatRule().getProtocol() + "\n   NAT rule getInterface "
                            + natRule.getGatewayNatRule().getInterface());
                    }
                    VcdCloudProviderConnectorFactory.logger.info("  NAT rule getOneToOneBasicRule "
                        + natRule.getOneToOneBasicRule() + "\n  NAT rule getOneToOneVmRule " + natRule.getOneToOneVmRule()
                        + "\n  NAT rule getPortForwardingRule " + natRule.getPortForwardingRule() + "\n  NAT rule getVmRule "
                        + natRule.getVmRule() + "\n");
                }
            }
        }

        private InstantiationParamsType createDefaultVappInstantiationParamsType_old(final String fenceMode)
            throws ConnectorException {

            VAppNetworkConfigurationType vAppNetworkConfigurationType = new VAppNetworkConfigurationType();
            NetworkConfigurationType networkConfigurationType = new NetworkConfigurationType();

            // specify the NetworkConfiguration for the vApp network.
            if (fenceMode.equals(FenceModeValuesType.BRIDGED.value())) {
                VcdCloudProviderConnectorFactory.logger.info("vAppNetworkConfiguration Bridged:"
                    + this.cimiPublicOrgVdcNetworkName);
                vAppNetworkConfigurationType.setNetworkName(this.cimiPublicOrgVdcNetworkName);
                networkConfigurationType.setParentNetwork(this.vdc
                    .getAvailableNetworkRefByName(this.cimiPublicOrgVdcNetworkName));
                networkConfigurationType.setFenceMode(fenceMode);
                networkConfigurationType.setRetainNetInfoAcrossDeployments(true);
            } else if (fenceMode.equals(FenceModeValuesType.NATROUTED.value())) {
                VcdCloudProviderConnectorFactory.logger.info("vAppNetworkConfiguration NATROUTED:"
                    + Constants.VAPP_NETWORK_NAME);
                vAppNetworkConfigurationType.setNetworkName(Constants.VAPP_NETWORK_NAME);
                networkConfigurationType.setParentNetwork(this.vdc
                    .getAvailableNetworkRefByName(this.cimiPublicOrgVdcNetworkName));
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

            } else { // TODO ISOLATED
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
                    networkConnection.setNetwork(this.vdc.getAvailableNetworkRefs().iterator().next().getName());
                }
                childVm.updateSection(networkConnectionSectionType).waitForTask(
                    VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
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

                childVm.updateSection(networkConnectionSectionType).waitForTask(
                    VcdCloudProviderConnectorFactory.DEFAULT_WAIT_TIME_IN_MILLISECONDS);
            }
        }
    }
}
