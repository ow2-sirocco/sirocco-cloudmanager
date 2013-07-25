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

package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.glassfish.osgicdi.OSGiService;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFinder;
import org.ow2.sirocco.cloudmanager.connector.api.ISystemService;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager.Placement;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteSystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IResourceWatcher;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ServiceUnavailableException;
import org.ow2.sirocco.cloudmanager.core.utils.QueryHelper;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudCollectionItem;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.Network.Type;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.SiroccoConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;
import org.ow2.sirocco.cloudmanager.model.cimi.system.ComponentDescriptor;
import org.ow2.sirocco.cloudmanager.model.cimi.system.ComponentDescriptor.ComponentType;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System.State;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCredentials;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemMachine;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemSystem;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemVolume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Remote(IRemoteSystemManager.class)
@Local(ISystemManager.class)
@SuppressWarnings("unused")
@IdentityInterceptorBinding
public class SystemManager implements ISystemManager {

    private static Logger logger = LoggerFactory.getLogger(SystemManager.class.getName());

    // private static String CREATE_ACTION = "system creation";
    //
    // private static String START_ACTION = "system start";
    //
    // private static String STOP_ACTION = "system stop";
    //
    // private static String SUSPEND_ACTION = "system suspend";
    //
    // private static String PAUSE_ACTION = "system pause";
    //
    // private static String RESTART_ACTION = "system restart";
    //
    // private static String DELETE_ACTION = "system delete";

    private static String REMOVE_ENTITY_ACTION = "removeEntityFromSystem";

    private static String ADD_MACHINE_ACTION = "addMachineToSystem";

    private static String REMOVE_MACHINE_ACTION = "removeMachineFromSystem";

    private static String UPDATE_MACHINE_ACTION = "updateMachineFromSystem";

    private static String ADD_CREDENTIAL_ACTION = "addCredentialToSystem";

    private static String REMOVE_CREDENTIAL_ACTION = "removeCredentialFromSystem";

    private static String UPDATE_CREDENTIAL_ACTION = "updateCredentialFromSystem";

    private static String ADD_SYSTEM_ACTION = "addSystemToSystem";

    private static String REMOVE_SYSTEM_ACTION = "removeSystemFromSystem";

    private static String UPDATE_SYSTEM_ACTION = "updateSystemFromSystem";

    private static String ADD_VOLUME_ACTION = "addVolumeToSystem";

    private static String REMOVE_VOLUME_ACTION = "removeVolumeFromSystem";

    private static String UPDATE_VOLUME_ACTION = "updateVolumeFromSystem";

    private static String ADD_NETWORK_ACTION = "addNetworkToSystem";

    private static String REMOVE_NETWORK_ACTION = "removeNetworkFromSystem";

    private static String UPDATE_NETWORK_ACTION = "updateNetworkFromSystem";

    private static String HANDLED_JOB = "handled";

    private static String PROP_SYSTEM_SUPPORTED_IN_CONNECTOR = "_SystemSupportedInConnector";

    private static String PROP_JOB_DETAILED_ACTION = "_JobDetailedAction";

    private static String PROP_JOB_DETAILED_SUB_ACTION = "_JobDetailedSubAction";

    private static String PROP_JOB_COLLECTION_ID = "_JobCollectionId";

    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Inject
    private IdentityContext identityContext;

    @Resource
    private EJBContext ctx;

    @EJB
    private ITenantManager tenantManager;

    @EJB
    private IMachineManager machineManager;

    @EJB
    private IVolumeManager volumeManager;

    @EJB
    private ICredentialsManager credentialsManager;

    @EJB
    private INetworkManager networkManager;

    @EJB
    private IJobManager jobManager;

    @EJB
    private ICloudProviderManager cloudProviderManager;

    @EJB
    private IResourceWatcher resourceWatcher;

    @Inject
    @OSGiService(dynamic = true)
    private ICloudProviderConnectorFinder connectorFinder;

    private Tenant getTenant() throws CloudProviderException {
        return this.tenantManager.getTenant(this.identityContext);
    }

    private boolean isSystemSupportedInConnector(final ICloudProviderConnector connector) {
        boolean isSystemSupportedInConnector = false;

        if (connector.getClass().getName().equals("org.ow2.sirocco.cloudmanager.connector.mock.MockCloudProviderConnector")) {
            try {
                if ((Boolean) this.getConfiguration("mockConnectorImplementsSystem")) {
                    isSystemSupportedInConnector = true;
                } else {
                    isSystemSupportedInConnector = false;
                }
            } catch (CloudProviderException e) {
                SystemManager.logger.warn("no parameter found for mockConnectorImplementsSystem");
                isSystemSupportedInConnector = false;
            }
        } else {
            try {
                ISystemService sysServ = connector.getSystemService();
                isSystemSupportedInConnector = true;
            } catch (ConnectorException e1) {
                isSystemSupportedInConnector = false;
            }
        }

        return isSystemSupportedInConnector;

    }

    @Override
    public void setConfiguration(final String paramName, final Object paramValue) throws CloudProviderException {

        SiroccoConfiguration config = null;
        try {
            config = (SiroccoConfiguration) this.em.createQuery("SELECT s FROM SiroccoConfiguration s").getSingleResult();
        } catch (NoResultException e) {
            config = null;
        }

        if (config == null) {
            config = new SiroccoConfiguration();
            this.em.persist(config);
            // this.em.flush();
        }
        if (paramName.equals("mockConnectorImplementsSystem") && paramValue instanceof Boolean) {
            config.setMockConnectorImplementsSystem((Boolean) paramValue);
        } else {
            throw new CloudProviderException("no parameter found for " + paramName);
        }

    }

    @Override
    public Object getConfiguration(final String paramName) throws CloudProviderException {

        SiroccoConfiguration config = null;
        try {
            config = (SiroccoConfiguration) this.em.createQuery("SELECT s FROM SiroccoConfiguration s").getSingleResult();
        } catch (NoResultException e) {
            config = null;
        }

        if (config == null) {
            config = new SiroccoConfiguration();
            this.em.persist(config);
            // this.em.flush();
        }
        if (paramName.equals("mockConnectorImplementsSystem")) {
            return config.isMockConnectorImplementsSystem();
        } else {
            throw new CloudProviderException("no parameter found for " + paramName);
        }

    }

    private CloudCollectionItem createCollection(final Class<? extends CloudCollectionItem> entityType,
        final CloudResource resource, final org.ow2.sirocco.cloudmanager.model.cimi.CloudCollectionItem.State state)
        throws CloudProviderException {

        CloudCollectionItem sc = null;
        try {
            sc = entityType.newInstance();
        } catch (InstantiationException e) {
            new CloudProviderException("InstantiationException in createCollection for type " + entityType);
        } catch (IllegalAccessException e) {
            new CloudProviderException("IllegalAccessException in createCollection for type " + entityType);
        }
        sc.setResource(resource);
        sc.setState(state);
        sc.setCreated(new Date());
        sc.setProperties(new HashMap<String, String>());
        return sc;
    }

    private CloudCollectionItem updateCollectionFromProvider(final CloudCollectionItem providerEntity,
        final CloudResource resource, final org.ow2.sirocco.cloudmanager.model.cimi.CloudCollectionItem.State state)
        throws CloudProviderException {

        CloudCollectionItem sc = providerEntity;

        sc.setResource(resource);
        sc.setState(state);
        sc.setCreated(new Date());
        sc.setProperties(new HashMap<String, String>());
        return sc;
    }

    private ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount cloudProviderAccount)
        throws CloudProviderException {
        ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(cloudProviderAccount
            .getCloudProvider().getCloudProviderType());
        if (connector == null) {
            SystemManager.logger.error("Cannot find connector for cloud provider type "
                + cloudProviderAccount.getCloudProvider().getCloudProviderType());
            return null;
        }
        return connector;
    }

    @Override
    public Job createSystem(final SystemCreate systemCreate) throws CloudProviderException {
        SystemManager.logger.info("Creating System " + systemCreate.getName());

        Tenant tenant = this.getTenant();

        Placement placement = this.cloudProviderManager.placeResource(tenant.getId().toString(), systemCreate.getProperties());
        ICloudProviderConnector connector = this.getCloudProviderConnector(placement.getAccount());
        if (connector == null) {
            throw new CloudProviderException("Cannot retrieve cloud provider connector "
                + placement.getAccount().getCloudProvider().getCloudProviderType());
        }

        // creating credentials if necessary
        // iterating through descriptors
        Set<ComponentDescriptor> componentDescriptorsCred = systemCreate.getSystemTemplate().getComponentDescriptors();

        Iterator<ComponentDescriptor> iterCred = componentDescriptorsCred.iterator();
        List<SystemCredentials> creds = new ArrayList<SystemCredentials>();

        while (iterCred.hasNext()) {
            ComponentDescriptor cd = iterCred.next();
            if (cd.getComponentType() == ComponentType.CREDENTIALS) {
                // creating new credentials

                for (int i = 0; i < cd.getComponentQuantity(); i++) {
                    CredentialsCreate cc = new CredentialsCreate();
                    if (cd.getComponentQuantity() > 1) {
                        String name = cd.getName() == null ? "" : cd.getName();
                        cc.setName(name + new Integer(i + 1).toString());
                    } else {
                        cc.setName(cd.getName());
                    }
                    CredentialsTemplate ct = (CredentialsTemplate) cd.getComponentTemplate();
                    cc.setCredentialTemplate(ct);
                    cc.setDescription(cd.getDescription());
                    cc.setProperties(cd.getProperties() == null ? new HashMap<String, String>() : new HashMap<String, String>(
                        cd.getProperties()));

                    // no job for credentials!
                    Credentials c = this.credentialsManager.createCredentials(cc);
                    SystemCredentials sc = (SystemCredentials) this.createCollection(SystemCredentials.class, c,
                        SystemCredentials.State.AVAILABLE);
                    this.em.persist(sc);
                    creds.add(sc);
                }
            }
        }

        // resolve MachineTemplate credential component references if any
        for (ComponentDescriptor component : systemCreate.getSystemTemplate().getComponentDescriptors()) {
            if (component.getComponentType() == ComponentType.MACHINE) {
                MachineTemplate machineTemplate = (MachineTemplate) component.getComponentTemplate();
                if (machineTemplate.getSystemCredentialName() != null) {
                    for (SystemCredentials sysCred : creds) {
                        Credentials cred = (Credentials) sysCred.getResource();
                        if (cred.getName() != null && cred.getName().equals(machineTemplate.getSystemCredentialName())) {
                            machineTemplate.setCredential(cred);
                            break;
                        }
                    }
                }
            }
        }

        System systemFromProvider;

        try {
            ISystemService systemService = connector.getSystemService();
            systemFromProvider = systemService.createSystem(systemCreate, new ProviderTarget().account(placement.getAccount())
                .location(placement.getLocation()));
        } catch (ConnectorException e) {
            SystemManager.logger.error("Failed to create system: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        System system = new System();
        system.setProviderAssignedId(systemFromProvider.getProviderAssignedId());
        system.setName(systemCreate.getName());
        system.setDescription(systemCreate.getDescription());
        system.setProperties(systemCreate.getProperties() == null ? new HashMap<String, String>()
            : new HashMap<String, String>(systemCreate.getProperties()));
        system.setState(State.CREATING);
        system.setTenant(this.getTenant());
        system.setCloudProviderAccount(placement.getAccount());
        system.setLocation(placement.getLocation());

        system.setCredentials(creds);

        this.em.persist(system);

        Job job = new Job();
        job.setTenant(tenant);
        job.setTargetResource(system);
        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(system);
        job.setAffectedResources(affectedResources);
        job.setCreated(new Date());
        job.setDescription("System creation");
        job.setState(Job.Status.RUNNING);
        job.setAction("add");// TODO: normalize!!
        job.setTimeOfStatusChange(new Date());// now
        this.em.persist(job);
        this.em.flush();

        this.resourceWatcher.watchSystem(system, job);

        return job;

    }

    @Override
    public void syncSystem(final String systemId, final System updatedSystem, final String jobId) throws CloudProviderException {
        System system = this.em.find(System.class, Integer.valueOf(systemId));
        Job job = this.em.find(Job.class, Integer.valueOf(jobId));
        if (updatedSystem == null) {
            system.setState(System.State.DELETED);
            this.updateSystemContentState(updatedSystem, "delete");
        } else {
            system.setState(updatedSystem.getState());
            if (system.getCreated() == null) {
                system.setCreated(new Date());
                this.persistSystemContent(updatedSystem, system.getTenant(), system.getCloudProviderAccount(),
                    system.getLocation());

                system.setMachines(updatedSystem.getMachines());
                system.setNetworks(updatedSystem.getNetworks());
                system.setSystems(updatedSystem.getSystems());
                system.setVolumes(updatedSystem.getVolumes());
            } else {
                this.updateSystemContentState(updatedSystem, job.getAction());
            }
            system.setUpdated(new Date());
        }
        job.setState(Job.Status.SUCCESS);
    }

    private void setJobProperty(final Job j, final String key, final String value) {
        Map<String, String> prop = j.getProperties();
        prop.put(key, value);
        j.setProperties(prop);
    }

    private String getJobProperty(final Job j, final String key) {
        Map<String, String> prop = j.getProperties();
        return prop.get(key);
    }

    private boolean findComponentDescriptor(final SystemTemplate systemTemplate, final String name, final ComponentType type) {
        for (ComponentDescriptor cd : systemTemplate.getComponentDescriptors()) {
            if (cd.getComponentType() == type && cd.getName() != null && cd.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void validateSystemTemplate(final SystemTemplate systemTemplate) throws InvalidRequestException,
        CloudProviderException {
        for (ComponentDescriptor cd : systemTemplate.getComponentDescriptors()) {
            if (cd.getId() == null) {
                CloudTemplate ct = cd.getComponentTemplate();
                if (ct instanceof SystemTemplate) {
                    this.validateSystemTemplate((SystemTemplate) ct);
                } else if (ct instanceof MachineTemplate) {
                    MachineTemplate machineTemplate = (MachineTemplate) ct;
                    if (machineTemplate.getNetworkInterfaces() != null) {
                        for (MachineTemplateNetworkInterface nic : machineTemplate.getNetworkInterfaces()) {
                            if (nic.getSystemNetworkName() != null) {
                                if (!this.findComponentDescriptor(systemTemplate, nic.getSystemNetworkName(),
                                    ComponentType.NETWORK)) {
                                    throw new InvalidRequestException("Invalid network interface: component #"
                                        + nic.getSystemNetworkName() + " not found");
                                }
                            } else if (nic.getNetworkType() == null && nic.getNetwork() == null) {
                                throw new InvalidRequestException("Invalid network interface: missing network");
                            }

                        }
                    }
                    if (machineTemplate.getVolumes() != null) {
                        for (MachineVolume machineVolume : machineTemplate.getVolumes()) {
                            if (machineVolume.getSystemVolumeName() != null
                                && !this.findComponentDescriptor(systemTemplate, machineVolume.getSystemVolumeName(),
                                    ComponentType.VOLUME)) {
                                throw new InvalidRequestException("Invalid volume: component #"
                                    + machineVolume.getSystemVolumeName() + " not found");
                            }
                        }
                    }
                    if (machineTemplate.getSystemCredentialName() != null
                        && !this.findComponentDescriptor(systemTemplate, machineTemplate.getSystemCredentialName(),
                            ComponentType.CREDENTIALS)) {
                        throw new InvalidRequestException("Invalid credential: component #"
                            + machineTemplate.getSystemCredentialName() + " not found");
                    }
                }
            }
        }
    }

    @Override
    public SystemTemplate createSystemTemplate(final SystemTemplate systemT) throws InvalidRequestException,
        CloudProviderException {
        SystemManager.logger.info("Creating SystemTemplate name=" + systemT.getName());
        this.validateSystemTemplate(systemT);
        systemT.setTenant(this.getTenant());
        systemT.setCreated(new Date());

        for (ComponentDescriptor cd : systemT.getComponentDescriptors()) {
            if (cd.getId() == null) {
                // no id, will be persisted as jpa entity
                cd.setTenant(this.getTenant());
                cd.setCreated(new Date());
                CloudTemplate ct = cd.getComponentTemplate();

                if (ct.getId() == null) {
                    // no id, the template is new: calling manager
                    // createTemplate for each one

                    ct.setIsEmbeddedInSystemTemplate(true);

                    if (ct instanceof SystemTemplate) {
                        // recursive calls
                        this.createSystemTemplate((SystemTemplate) ct);
                    }
                    if (ct instanceof VolumeTemplate) {
                        this.volumeManager.createVolumeTemplate((VolumeTemplate) ct);
                    }
                    if (ct instanceof NetworkTemplate) {
                        this.networkManager.createNetworkTemplate((NetworkTemplate) ct);
                    }
                    if (ct instanceof CredentialsTemplate) {
                        this.credentialsManager.createCredentialsTemplate((CredentialsTemplate) ct);
                    }
                    if (ct instanceof MachineTemplate) {
                        this.machineManager.createMachineTemplate((MachineTemplate) ct);
                    }

                }
            }
        }

        this.em.persist(systemT);
        this.em.flush();
        return systemT;
    }

    @Override
    public void deleteSystemTemplate(final String systemTemplateId) throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<System> getSystems() throws CloudProviderException {
        return QueryHelper.getEntityList("System", this.em, this.getTenant().getId(), System.State.DELETED, false);
    }

    @Override
    public QueryResult<System> getSystems(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("System", System.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .stateToIgnore(System.State.DELETED));
    }

    @Override
    public System getSystemById(final String systemId) throws CloudProviderException {
        if (systemId == null) {
            throw new InvalidRequestException(" null system id");
        }
        System result = this.em.find(System.class, new Integer(systemId));

        if (result == null || result.getState() == System.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid system id " + systemId);
        }
        result.getMachines().size();
        result.getNetworks().size();
        result.getSystems().size();
        result.getVolumes().size();
        result.getCredentials().size();
        result.getProperties().size();
        return result;
    }

    @Override
    public System getSystemAttributes(final String systemId, final List<String> attributes) throws CloudProviderException {
        System sys = this.getSystemById(systemId);
        return UtilsForManagers.fillResourceAttributes(sys, attributes);
    }

    @Override
    public SystemTemplate getSystemTemplateById(final String systemTemplateId) throws CloudProviderException {
        SystemTemplate result = this.em.find(SystemTemplate.class, new Integer(systemTemplateId));
        if (result == null) {
            throw new ResourceNotFoundException("Invalid SystemTemplate id: " + systemTemplateId);
        }
        return result;
    }

    @Override
    public SystemTemplate getSystemTemplateAttributes(final String systemTemplateId, final List<String> attributes)
        throws CloudProviderException {
        SystemTemplate sysTemplate = this.getSystemTemplateById(systemTemplateId);
        return UtilsForManagers.fillResourceAttributes(sysTemplate, attributes);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SystemTemplate> getSystemTemplates() throws CloudProviderException {
        return QueryHelper.getEntityList("SystemTemplate", this.em, this.getTenant().getId(), null, true);
    }

    @Override
    public QueryResult<SystemTemplate> getSystemTemplates(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("SystemTemplate", SystemTemplate.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .filterEmbbededTemplate().returnPublicEntities());
    }

    private ComponentDescriptor getComponentDescriptorById(final String componentDescriptorId) throws CloudProviderException {
        ComponentDescriptor result = this.em.find(ComponentDescriptor.class, new Integer(componentDescriptorId));
        return result;
    }

    @Override
    public Job addEntityToSystem(final String systemId, final CloudCollectionItem entity) throws CloudProviderException {

        System s = this.getSystemById(systemId);

        if (entity == null || s == null) {
            throw new CloudProviderException("bad id given in parameter");
        }
        if (entity.getResource() == null) {
            throw new CloudProviderException("no resource linked to this entity");
        }
        if (QueryHelper.getCloudResourceById(this.em, entity.getResource().getId().toString()) == null) {
            throw new CloudProviderException("nonexisting resource linked to this SystemEntity");
        }

        // persist only systemXXX
        if (entity.getId() == null) {
            if (entity.getResource().getId() == null) {
                throw new CloudProviderException("CloudCollectionItem must own an existing resource");
            } else {
                CloudResource resTmp = this.em.merge(entity.getResource());
                entity.setResource(null);
                this.em.persist(entity);
                // this.em.flush();
                entity.setResource(resTmp);
            }
        } else {
            throw new CloudProviderException("CloudCollectionItem must be new");
        }

        String jobAction = null;
        if (entity instanceof SystemMachine) {
            s.getMachines().add((SystemMachine) entity);
            jobAction = SystemManager.ADD_MACHINE_ACTION;
        } else if (entity instanceof SystemVolume) {
            s.getVolumes().add((SystemVolume) entity);
            jobAction = SystemManager.ADD_VOLUME_ACTION;
        } else if (entity instanceof SystemSystem) {
            s.getSystems().add((SystemSystem) entity);
            jobAction = SystemManager.ADD_SYSTEM_ACTION;
        } else if (entity instanceof SystemNetwork) {
            s.getNetworks().add((SystemNetwork) entity);
            jobAction = SystemManager.ADD_NETWORK_ACTION;
        } else if (entity instanceof SystemCredentials) {
            s.getCredentials().add((SystemCredentials) entity);
            jobAction = SystemManager.ADD_CREDENTIAL_ACTION;
        } else {
            throw new CloudProviderException("object type can't be owned by a system");
        }

        // for system not supported by underlying connector
        Job job = this.createJob("add", s);
        this.setJobProperty(job, SystemManager.PROP_JOB_DETAILED_ACTION, jobAction);
        job.setState(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    @Override
    public Job removeEntityFromSystem(final String systemId, final String entityId) throws CloudProviderException {

        System s = this.getSystemById(systemId);
        CloudCollectionItem ce = QueryHelper.getCloudCollectionById(this.em, entityId);
        if (ce == null || s == null) {
            throw new CloudProviderException("bad id given in parameter");
        }

        // parent job
        Job parentJob = this.createJob("delete", s);
        this.setJobProperty(parentJob, SystemManager.PROP_JOB_DETAILED_ACTION, SystemManager.REMOVE_ENTITY_ACTION);
        this.setJobProperty(parentJob, SystemManager.PROP_JOB_COLLECTION_ID, entityId);
        this.em.persist(parentJob);

        String jobAction = null;
        if (ce instanceof SystemMachine) {
            jobAction = SystemManager.REMOVE_MACHINE_ACTION;
        } else if (ce instanceof SystemVolume) {
            jobAction = SystemManager.REMOVE_VOLUME_ACTION;
        } else if (ce instanceof SystemSystem) {
            jobAction = SystemManager.REMOVE_SYSTEM_ACTION;
        } else if (ce instanceof SystemNetwork) {
            jobAction = SystemManager.REMOVE_NETWORK_ACTION;
        } else if (ce instanceof SystemCredentials) {
            jobAction = SystemManager.REMOVE_CREDENTIAL_ACTION;
        } else {
            throw new CloudProviderException("object type can't be owned by a system");
        }
        this.setJobProperty(parentJob, SystemManager.PROP_JOB_DETAILED_SUB_ACTION, jobAction);

        // TODO:workflowICloudProviderConnector connector =
        // this.getConnector(s);
        ICloudProviderConnector connector = null;

        // connector or not connector? that is the question!
        // TODO:workflow
        // if (this.isSystemSupportedInConnector(connector)) {
        // Job j;
        // try {
        // j =
        // connector.getSystemService().removeEntityFromSystem(s.getProviderAssignedId(),
        // ce.getResource().getProviderAssignedId());
        // } catch (ConnectorException e) {
        // throw new ServiceUnavailableException(e.getMessage() + " action " +
        // jobAction + " system id "
        // + s.getProviderAssignedId() + " " + s.getId());
        // }
        //
        // parentJob.addNestedJob(j);
        // j.setTenant(this.getTenant());
        // this.em.persist(j);
        //
        // } else {
        // // no child job=> doing all immediately
        // this.removeEntityFromSystem_Final(parentJob, s);
        // parentJob.setState(Job.Status.SUCCESS);
        //
        // }

        return parentJob;
    }

    private void removeEntityFromSystem_Final(final Job job, final System s) throws CloudProviderException {
        String entityId = this.getJobProperty(job, SystemManager.PROP_JOB_COLLECTION_ID);
        CloudCollectionItem ce = QueryHelper.getCloudCollectionById(this.em, entityId);
        ce.setState(CloudCollectionItem.State.DELETED);
        this.removeItemFromSystemCollection(s, ce);
        // deleting SystemXXX
        // this.em.remove(ce);
    }

    private void removeItemFromSystemCollection(final System s, final CloudCollectionItem ce) {

        if (ce instanceof SystemMachine) {
            s.getMachines().remove(ce);
        } else if (ce instanceof SystemVolume) {
            s.getVolumes().remove(ce);
        } else if (ce instanceof SystemNetwork) {
            s.getNetworks().remove(ce);
        } else if (ce instanceof SystemCredentials) {
            s.getCredentials().remove(ce);
        } else if (ce instanceof SystemSystem) {
            s.getSystems().remove(ce);
        }
    }

    /**
     * only used to update common attributes of SystemXXX<br>
     * no update of linked resource
     */
    @Override
    public Job updateEntityInSystem(final String systemId, final CloudCollectionItem entity) throws CloudProviderException {

        System s = this.getSystemById(systemId);

        if (entity == null || s == null) {
            throw new CloudProviderException("bad id given in parameter");
        }
        if (QueryHelper.getCloudResourceById(this.em, entity.getResource().getId().toString()) == null) {
            throw new CloudProviderException("nonexisting resource linked to this SystemEntity");
        }

        String jobAction = null;
        if (entity instanceof SystemMachine) {
            jobAction = SystemManager.UPDATE_MACHINE_ACTION;
        } else if (entity instanceof SystemVolume) {
            jobAction = SystemManager.UPDATE_VOLUME_ACTION;
        } else if (entity instanceof SystemSystem) {
            jobAction = SystemManager.UPDATE_SYSTEM_ACTION;
        } else if (entity instanceof SystemNetwork) {
            jobAction = SystemManager.UPDATE_NETWORK_ACTION;
        } else if (entity instanceof SystemCredentials) {
            jobAction = SystemManager.UPDATE_CREDENTIAL_ACTION;
        } else {
            throw new CloudProviderException("object type can't be owned by a system");
        }
        this.em.merge(entity);

        Job job = this.createJob("edit", s);
        this.setJobProperty(job, SystemManager.PROP_JOB_DETAILED_ACTION, jobAction);
        job.setState(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    public CloudCollectionItem getEntityFromSystem(final String systemId, final String entityId) throws CloudProviderException {
        CloudCollectionItem ce = QueryHelper.getCloudCollectionById(this.em, entityId);
        if (ce == null) {
            throw new CloudProviderException("bad id given in parameter");
        }
        return ce;
    }

    @Override
    public List<? extends CloudCollectionItem> getEntityListFromSystem(final String systemId,
        final Class<? extends CloudCollectionItem> entityType) throws CloudProviderException {

        System s = this.getSystemById(systemId);
        if (s == null || entityType == null) {
            throw new CloudProviderException("bad id given in parameter");
        }

        if (entityType.equals(SystemMachine.class)) {
            return s.getMachines();
        } else if (entityType.equals(SystemVolume.class)) {
            return s.getVolumes();
        } else if (entityType.equals(SystemSystem.class)) {
            return s.getSystems();
        } else if (entityType.equals(SystemNetwork.class)) {
            return s.getNetworks();
        } else if (entityType.equals(SystemCredentials.class)) {
            return s.getCredentials();
        } else {
            throw new CloudProviderException("object type not owned by a system");
        }
    }

    @Override
    public QueryResult<CloudCollectionItem> getEntityListFromSystem(final String systemId,
        final Class<? extends CloudCollectionItem> entityType, final int first, final int last, final List<String> filters,
        final List<String> attributes) throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new CloudProviderException("action not implemented");
        // return null;
    }

    @Override
    public Job updateEntityAttributesInSystem(final String systemId, final String entityType, final String entityId,
        final Map<String, Object> updatedAttributes) throws InvalidRequestException, ResourceNotFoundException,
        CloudProviderException {
        // TODO Auto-generated method stub
        throw new CloudProviderException("action not implemented");
        // return null;
    }

    @Override
    public boolean addComponentDescriptorToSystemTemplate(final ComponentDescriptor componentDescriptor,
        final String systemTemplateId) throws CloudProviderException {
        SystemTemplate s = this.getSystemTemplateById(systemTemplateId);
        if (s == null) {
            throw new CloudProviderException("bad systemTemplateId given (" + systemTemplateId + ")");
        }

        if (componentDescriptor.getId() != null) {
            throw new CloudProviderException("ComponentDescriptor must be new");
        } else {
            // new
            if (componentDescriptor.getComponentTemplate().getId() == null) {
                // template must exist
                throw new CloudProviderException("ComponentDescriptor owned template must exist");
            } else {
                // existing template
                Integer templateId = componentDescriptor.getComponentTemplate().getId();
                componentDescriptor.setComponentTemplate(this.em.find(CloudTemplate.class, templateId));
                componentDescriptor.setTenant(this.getTenant());
                // validate componentDescriptor before persisting
                if (componentDescriptor.getName() == null || "".equals(componentDescriptor.getName())) {
                    throw new CloudProviderException("ComponentDescriptor name should not be void");
                }
                if (componentDescriptor.getComponentQuantity() == null) {
                    throw new CloudProviderException("ComponentDescriptor quantity should not be void");
                } else {
                    if (componentDescriptor.getComponentQuantity() < 1) {
                        throw new CloudProviderException("ComponentDescriptor quantity should be greater than 0");
                    }
                }
                if (componentDescriptor.getProperties() == null) {
                    componentDescriptor.setProperties(new HashMap<String, String>());
                }

                // all is ok
                this.em.persist(componentDescriptor);
            }
        }

        // updating system list of descriptors
        s.getComponentDescriptors().add(componentDescriptor);

        return true;
    }

    @Override
    public boolean removeComponentDescriptorFromSystemTemplate(final String componentDescriptorId, final String systemTemplateId)
        throws CloudProviderException {
        SystemTemplate s = this.getSystemTemplateById(systemTemplateId);
        Set<ComponentDescriptor> descrs = s.getComponentDescriptors();
        ComponentDescriptor cd = this.getComponentDescriptorById(componentDescriptorId);

        for (ComponentDescriptor cdesc : descrs) {
            if (cdesc.getId().equals(cd.getId())) {
                descrs.remove(cdesc);
                break;
            }
        }
        return true;
    }

    @Override
    public System updateComponentDescriptor(final String id, final Map<String, Object> updatedAttributes)
        throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new CloudProviderException("action not implemented");
        // return null;
    }

    @Override
    public System updateAttributesInSystem(final String id, final Map<String, Object> updatedAttributes)
        throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new CloudProviderException("action not implemented");
        // return null;
    }

    @Override
    public SystemTemplate updateAttributesInSystemTemplate(final String id, final Map<String, Object> updatedAttributes)
        throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new CloudProviderException("action not implemented");
        // return null;
    }

    @Override
    public System updateSystem(final System system) throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new CloudProviderException("action not implemented");
    }

    @Override
    public SystemTemplate updateSystemTemplate(final SystemTemplate systemTemplate) throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new CloudProviderException("action not implemented");
    }

    @Override
    public Job startSystem(final String systemId, final Map<String, String> properties) throws CloudProviderException {
        Job parentJob = null;
        return this.doService(systemId, "start", properties);
    }

    @Override
    public Job stopSystem(final String systemId, final boolean force, final Map<String, String> properties)
        throws CloudProviderException {
        return this.doService(systemId, "stop", properties);
    }

    @Override
    public Job suspendSystem(final String systemId, final Map<String, String> properties) throws CloudProviderException {
        return this.doService(systemId, "suspend", properties);
    }

    @Override
    public Job pauseSystem(final String systemId, final Map<String, String> properties) throws CloudProviderException {
        return this.doService(systemId, "pause", properties);
    }

    @Override
    public Job restartSystem(final String systemId, final boolean force, final Map<String, String> properties)
        throws CloudProviderException {
        return this.doService(systemId, "restart", properties);
    }

    @Override
    public Job deleteSystem(final String systemId) throws CloudProviderException {
        return this.doService(systemId, "delete", null);
    }

    @Override
    public Job startSystem(final String systemId) throws CloudProviderException {
        return this.startSystem(systemId, null);
    }

    @Override
    public Job stopSystem(final String systemId, final boolean force) throws CloudProviderException {
        return this.stopSystem(systemId, force, null);
    }

    @Override
    public Job stopSystem(final String systemId) throws CloudProviderException {
        return this.stopSystem(systemId, false, null);
    }

    @Override
    public Job restartSystem(final String systemId, final boolean force) throws CloudProviderException {
        return this.restartSystem(systemId, force, null);
    }

    @Override
    public Job pauseSystem(final String systemId) throws CloudProviderException {
        return this.pauseSystem(systemId, null);
    }

    @Override
    public Job suspendSystem(final String systemId) throws CloudProviderException {
        return this.suspendSystem(systemId, null);
    }

    // private methods

    private Job doService(final String systemId, final String action, final Map<String, String> properties,
        final Object... params) throws CloudProviderException {
        System system = this.getSystemById(systemId);

        ICloudProviderConnector connector = this.getCloudProviderConnector(system.getCloudProviderAccount());

        try {
            ISystemService systemService = connector.getSystemService();
            ProviderTarget target = new ProviderTarget().account(system.getCloudProviderAccount()).location(
                system.getLocation());
            if (action.equals("start")) {
                systemService.startSystem(system.getProviderAssignedId(), properties, target);
            } else if (action.equals("stop")) {
                boolean force = (params.length > 0 && params[0] instanceof Boolean) ? ((Boolean) params[0]) : false;
                systemService.stopSystem(system.getProviderAssignedId(), force, properties, target);
            } else if (action.equals("suspend")) {
                systemService.suspendSystem(system.getProviderAssignedId(), properties, target);
            } else if (action.equals("pause")) {
                systemService.pauseSystem(system.getProviderAssignedId(), properties, target);
            } else if (action.equals("delete")) {
                systemService.deleteSystem(system.getProviderAssignedId(), target);
            }
        } catch (ConnectorException e) {
            SystemManager.logger.error("Failed to " + action + " system: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        // choosing the right action
        if (action.equals("start")) {
            system.setState(System.State.STARTING);
        } else if (action.equals("stop")) {
            system.setState(System.State.STOPPING);
        } else if (action.equals("suspend")) {
            system.setState(System.State.SUSPENDING);
        } else if (action.equals("pause")) {
            system.setState(System.State.PAUSING);
        } else if (action.equals("delete")) {
            system.setState(System.State.DELETING);
        }
        this.em.merge(system);

        Tenant tenant = this.getTenant();

        // creating Job
        Job job = new Job();
        job.setTenant(tenant);
        job.setTargetResource(system);
        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(system);
        job.setAffectedResources(affectedResources);
        job.setCreated(new Date());
        job.setDescription("System " + action);
        job.setState(Job.Status.RUNNING);
        job.setAction(action);// TODO: normalize!!
        job.setTimeOfStatusChange(new Date());// now
        this.em.persist(job);
        this.em.flush();

        this.resourceWatcher.watchSystem(system, job);

        return job;
    }

    private boolean checkQuota(final Tenant tenant, final System sys) {
        /**
         * TODO Check current quota
         */
        return true;
    }

    private Job createJob(final String action, final CloudResource targetEntity) throws CloudProviderException {

        Job job = new Job();
        job.setAction(action);
        job.setCreated(new Date());
        job.setIsCancellable(false);
        job.setName("job " + action);
        job.setParentJob(null);
        job.setState(Status.RUNNING);
        job.setTargetResource(targetEntity);
        job.setTenant(this.getTenant());
        job.setProperties(new HashMap<String, String>());

        return job;
    }

    /**
     * used to recursively persist a full system content (with subobjects,
     * subsub...), but not system object itself
     * 
     * @param connector
     * @param providerSystem
     * @throws CloudProviderException
     */
    private void persistSystemContent(final System providerSystem, final Tenant tenant, final CloudProviderAccount account,
        final CloudProviderLocation location) throws CloudProviderException {
        // getting system owned object lists from connector
        System s = providerSystem;
        List<SystemMachine> machines = providerSystem.getMachines() == null ? new ArrayList<SystemMachine>() : providerSystem
            .getMachines();
        List<SystemVolume> volumes = providerSystem.getVolumes() == null ? new ArrayList<SystemVolume>() : providerSystem
            .getVolumes();
        List<SystemSystem> systems = providerSystem.getSystems() == null ? new ArrayList<SystemSystem>() : providerSystem
            .getSystems();
        List<SystemNetwork> networks = providerSystem.getNetworks() == null ? new ArrayList<SystemNetwork>() : providerSystem
            .getNetworks();
        Map<String, Network> networkMap = new HashMap<String, Network>();
        Map<String, Volume> volumeMap = new HashMap<String, Volume>();

        // creating and adding objects
        for (SystemNetwork sn : networks) {
            sn.getResource().setId(null);
            sn.getNetwork().setTenant(tenant);
            sn.getNetwork().setCloudProviderAccount(account);
            sn.getNetwork().setLocation(location);
            // TODO: replace with dedicated method from related manager!
            Network network = sn.getNetwork();
            this.em.persist(network);
            networkMap.put(network.getProviderAssignedId(), network);
            this.updateCollectionFromProvider(sn, sn.getResource(), SystemNetwork.State.AVAILABLE);
            this.em.persist(sn);
        }
        // this.em.flush();

        for (SystemVolume sv : volumes) {
            sv.getResource().setId(null);
            sv.getVolume().setTenant(tenant);
            sv.getVolume().setCloudProviderAccount(account);
            sv.getVolume().setLocation(location);
            // TODO: replace with dedicated method from related manager!
            this.em.persist(sv.getResource());
            volumeMap.put(sv.getVolume().getProviderAssignedId(), sv.getVolume());
            this.updateCollectionFromProvider(sv, sv.getResource(), SystemVolume.State.AVAILABLE);
            this.em.persist(sv);
        }
        // this.em.flush();
        if (machines != null) {
            for (SystemMachine sm : machines) {
                Machine mach = sm.getMachine();
                mach.setTenant(tenant);
                mach.setCloudProviderAccount(account);
                mach.setLocation(location);
                mach.setCreated(new Date());

                if (mach.getNetworkInterfaces() != null) {
                    for (MachineNetworkInterface nic : mach.getNetworkInterfaces()) {
                        if (nic.getNetwork() != null) {
                            if (nic.getNetwork().getNetworkType() == Type.PRIVATE) {
                                Network persistedNetwork = networkMap.get(nic.getNetwork().getProviderAssignedId());
                                nic.setNetwork(persistedNetwork);
                            } else {
                                nic.setNetwork(this.networkManager.getPublicNetwork());
                            }
                        }
                    }
                }
                if (mach.getVolumes() != null) {
                    for (MachineVolume machineVol : mach.getVolumes()) {
                        if (machineVol.getVolume() != null) {
                            machineVol.setVolume(volumeMap.get(machineVol.getVolume().getProviderAssignedId()));
                        }
                    }
                }

                this.machineManager.persistMachineInSystem(mach);
                this.updateCollectionFromProvider(sm, sm.getResource(), SystemMachine.State.AVAILABLE);
                this.em.persist(sm);
            }
        }
        // this.em.flush();

        for (SystemSystem ss : systems) {
            ss.getResource().setId(null);
            ss.setId(null);
            ss.getSystem().setCloudProviderAccount(account);
            ss.getSystem().setLocation(location);
            ss.getSystem().setTenant(tenant);
            this.persistSystemContent((System) ss.getResource(), tenant, account, location);
            this.em.persist(ss.getResource());
            this.updateCollectionFromProvider(ss, ss.getResource(), SystemSystem.State.AVAILABLE);
            this.em.persist(ss);
            this.em.flush();
        }
        // this.em.flush();
    }

    /**
     * used to recursively update system content state from full provider
     * system, but not system state itself
     * 
     * @param connector
     * @param providerSystem
     * @param jobAction
     * @throws CloudProviderException
     */
    private void updateSystemContentState(final System providerSystem, final String jobAction) throws CloudProviderException {
        // getting system owned object lists from connector
        System s = providerSystem;

        List<SystemMachine> machines = new ArrayList<SystemMachine>(
            providerSystem.getMachines() == null ? new ArrayList<SystemMachine>() : providerSystem.getMachines());
        List<SystemVolume> volumes = new ArrayList<SystemVolume>(
            providerSystem.getVolumes() == null ? new ArrayList<SystemVolume>() : providerSystem.getVolumes());
        List<SystemSystem> systems = new ArrayList<SystemSystem>(
            providerSystem.getSystems() == null ? new ArrayList<SystemSystem>() : providerSystem.getSystems());
        List<SystemNetwork> networks = new ArrayList<SystemNetwork>(
            providerSystem.getNetworks() == null ? new ArrayList<SystemNetwork>() : providerSystem.getNetworks());

        // syncing objects status

        for (SystemMachine sn : machines) {
            Machine lmanaged = (Machine) QueryHelper.getResourceFromProviderId(this.em, sn.getResource()
                .getProviderAssignedId());
            if (jobAction.equals("delete")) {
                // lmanaged.setState(Machine.State.DELETED);
                this.machineManager.deleteMachineInSystem(lmanaged);
            } else {
                lmanaged.setState(((Machine) sn.getResource()).getState());
            }
        }

        for (SystemVolume sn : volumes) {
            Volume lmanaged = (Volume) QueryHelper.getResourceFromProviderId(this.em, sn.getResource().getProviderAssignedId());
            if (jobAction.equals("delete")) {
                lmanaged.setState(Volume.State.DELETED);
            } else {
                lmanaged.setState(((Volume) sn.getResource()).getState());
            }
        }

        for (SystemNetwork sn : networks) {
            Network lmanaged = (Network) QueryHelper.getResourceFromProviderId(this.em, sn.getResource()
                .getProviderAssignedId());
            if (jobAction.equals("delete")) {
                lmanaged.setState(Network.State.DELETED);
            } else {
                lmanaged.setState(((Network) sn.getResource()).getState());
            }
        }

        for (SystemSystem sn : systems) {
            // recursion rules!
            this.updateSystemContentState((System) sn.getResource(), jobAction);
            System lmanaged = (System) QueryHelper.getResourceFromProviderId(this.em, sn.getResource().getProviderAssignedId());
            if (jobAction.equals("delete")) {
                lmanaged.setState(System.State.DELETED);
                sn.setState(SystemSystem.State.DELETED);
                this.removeItemFromSystemCollection(s, sn);
            } else {
                lmanaged.setState(((System) sn.getResource()).getState());
            }
        }
    }

    @Override
    public void handleEntityStateChange(final Class<? extends CloudResource> entityType, final String entityId,
        final boolean deletion) {

        // getting system attachement if any
        SystemManager.logger.info("updating system state - " + entityType.getName() + " - " + entityId);
        CloudCollectionItem obj = null;
        try {
            obj = (CloudCollectionItem) this.em
                .createQuery("SELECT v FROM CloudCollectionItem v WHERE v.resource.id=:resourceId")
                .setParameter("resourceId", new Integer(entityId)).getSingleResult();
        } catch (NoResultException e) {
            obj = null;
        }
        // object not attached to any systemXXX collection, stopping all
        if (obj == null) {
            SystemManager.logger.info(" object not in any system - " + entityId);
            return;
        }
        // object in a system collection
        System sys = null;
        try {
            String collection = null;
            if (obj instanceof SystemMachine) {
                collection = "v.machines";
            } else if (obj instanceof SystemSystem) {
                collection = "v.systems";
            } else if (obj instanceof SystemVolume) {
                collection = "v.volumes";
            } else if (obj instanceof SystemNetwork) {
                collection = "v.networks";
            }
            sys = (System) this.em.createQuery("SELECT v FROM System v WHERE :resource member " + collection)
                .setParameter("resource", obj).getSingleResult();
        } catch (NoResultException e) {
            sys = null;
        }
        // bug:collection not attached to any system collection, stopping all
        if (sys == null) {
            SystemManager.logger.warn(" collection not in any system - " + obj.getId());
            return;
        }

        // delete systemXXX if deleted event
        if (deletion) {
            obj.setState(CloudCollectionItem.State.DELETED);
            this.removeItemFromSystemCollection(sys, obj);
            // this.em.remove(obj);
            return;
        }

        // updating system status
        try {
            this.updateSystemStatus(sys.getId().toString());
        } catch (CloudProviderException e) {
            SystemManager.logger.warn(" system status update failed for system " + sys.getId());
        }

    }

    /**
     * update system status by cycling through it's child machines
     * 
     * @param systemId
     * @throws CloudProviderException
     */
    private boolean updateSystemStatus(final String systemId) throws CloudProviderException {
        System s = this.getSystemById(systemId);

        Machine.State firstState = null;
        boolean mixed = false;

        if (s.getMachines().size() > 0) {
            firstState = ((Machine) s.getMachines().get(0).getResource()).getState();
        } else {
            // no machine => mixed
            s.setState(State.MIXED);
            return true;
        }
        // update subsystems recursively...enjoy ;)
        for (SystemSystem ss : s.getSystems()) {
            this.updateSystemStatus(ss.getResource().getId().toString());
        }

        for (SystemMachine sn : s.getMachines()) {
            Machine.State state = ((Machine) sn.getResource()).getState();
            if (!state.equals(firstState)) {
                mixed = true;
                break;
            }
        }

        if (mixed) {
            s.setState(State.MIXED);
            return true;
        } else {
            // translating machine state into system state
            System.State sysState = null;
            switch (firstState) {
            case STARTED:
                sysState = System.State.STARTED;
                break;
            case STARTING:
                sysState = System.State.STARTING;
                break;
            case STOPPED:
                sysState = System.State.STOPPED;
                break;
            case STOPPING:
                sysState = System.State.STOPPING;
                break;
            case SUSPENDED:
                sysState = System.State.SUSPENDED;
                break;
            case SUSPENDING:
                sysState = System.State.SUSPENDING;
                break;
            case PAUSED:
                sysState = System.State.PAUSED;
                break;
            case PAUSING:
                sysState = System.State.PAUSING;
                break;
            }

            if (sysState != null) {
                // updating system state
                s.setState(sysState);
                return true;
            } else {
                // not a useful machine state (creating, deleting, etc.)
                // do nothing
                return false;
            }
        }
    }

    @Override
    public Job exportSystem(final String systemId, final String format, final String destination,
        final Map<String, String> properties) throws CloudProviderException {
        throw new ServiceUnavailableException("Unsupported operation");
    }

    @Override
    public Job importSystem(final String source, final Map<String, String> properties) throws CloudProviderException {
        throw new ServiceUnavailableException("Unsupported operation");
    }

    @Override
    public Job exportSystemTemplate(final String systemTemplateId, final String format, final String destination,
        final Map<String, String> properties) throws CloudProviderException {
        throw new ServiceUnavailableException("Unsupported operation");
    }

    @Override
    public Job importSystemTemplate(final String source, final Map<String, String> properties) throws CloudProviderException {
        throw new ServiceUnavailableException("Unsupported operation");
    }

}
