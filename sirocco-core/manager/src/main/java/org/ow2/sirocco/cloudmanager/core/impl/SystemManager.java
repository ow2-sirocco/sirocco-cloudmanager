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
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactory;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactoryFinder;
import org.ow2.sirocco.cloudmanager.connector.api.ISystemService;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager.Placement;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteSystemManager;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.BadStateException;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ServiceUnavailableException;
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
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.SiroccoConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;
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

@Stateless
@Remote(IRemoteSystemManager.class)
@Local(ISystemManager.class)
@SuppressWarnings("unused")
public class SystemManager implements ISystemManager {

    private static Logger logger = Logger.getLogger(SystemManager.class.getName());

    private static String CREATE_ACTION = "system creation";

    private static String START_ACTION = "system start";

    private static String STOP_ACTION = "system stop";

    private static String SUSPEND_ACTION = "system suspend";

    private static String PAUSE_ACTION = "system pause";

    private static String RESTART_ACTION = "system restart";

    private static String DELETE_ACTION = "system delete";

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

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Resource
    private EJBContext ctx;

    @OSGiResource
    private ICloudProviderConnectorFactoryFinder cloudProviderConnectorFactoryFinder;

    @EJB
    private IUserManager userManager;

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

    private User getUser() throws CloudProviderException {
        String username = this.ctx.getCallerPrincipal().getName();
        return this.userManager.getUserByUsername(username);
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
            config = (SiroccoConfiguration) this.em.createQuery("FROM " + SiroccoConfiguration.class.getName())
                .getSingleResult();
        } catch (NoResultException e) {
            config = null;
        }

        if (config == null) {
            config = new SiroccoConfiguration();
            this.em.persist(config);
            this.em.flush();
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
            config = (SiroccoConfiguration) this.em.createQuery("FROM " + SiroccoConfiguration.class.getName())
                .getSingleResult();
        } catch (NoResultException e) {
            config = null;
        }

        if (config == null) {
            config = new SiroccoConfiguration();
            this.em.persist(config);
            this.em.flush();
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
        sc.setUser(this.getUser());
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
        sc.setUser(this.getUser());
        sc.setProperties(new HashMap<String, String>());
        return sc;
    }

    @Override
    public Job createSystem(final SystemCreate systemCreate) throws CloudProviderException {

        // this.checkQuota(userManager.getUserByUsername(this.user), system);

        // creation of entities in the base
        System system = new System();
        system.setCreated(new Date());
        system.setDescription(systemCreate.getDescription());
        system.setLocation(null);
        system.setName(systemCreate.getName());
        system.setProperties(systemCreate.getProperties() == null ? new HashMap<String, String>()
            : new HashMap<String, String>(systemCreate.getProperties()));
        system.setState(State.CREATING);
        system.setUser(this.getUser());
        this.em.persist(system);
        this.em.flush();

        // creation of main system job
        Job parentJob = this.createJob("add", system);
        this.setJobProperty(parentJob, SystemManager.PROP_JOB_DETAILED_ACTION, SystemManager.CREATE_ACTION);
        parentJob.setTargetEntity(system);
        this.em.persist(parentJob);
        this.em.flush();

        // creating credentials if necessary
        // iterating through descriptors
        Set<ComponentDescriptor> componentDescriptorsCred = systemCreate.getSystemTemplate().getComponentDescriptors();

        Iterator<ComponentDescriptor> iterCred = componentDescriptorsCred.iterator();
        while (iterCred.hasNext()) {
            ComponentDescriptor cd = iterCred.next();
            if (cd.getComponentType() == ComponentType.CREDENTIALS) {
                // creating new credentials
                for (int i = 0; i < cd.getComponentQuantity(); i++) {
                    CredentialsCreate cc = new CredentialsCreate();
                    if (cd.getComponentQuantity() > 1) {
                        String name = cd.getName() == null ? "" : cd.getName();
                        cc.setName(name + new Integer(i).toString());
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
                    system.getCredentials().add(sc);
                }
            }
        }

        // ICloudProviderConnector connector = this.getCloudProviderConnector();
        // if (connector == null) {
        // throw new CloudProviderException("no connector found");
        // }
        // CloudProviderAccount cpa =
        // this.selectCloudProviderAccount(this.selectCloudProvider());

        Placement placement = this.cloudProviderManager.placeResource(systemCreate.getProperties());
        ICloudProviderConnector connector = this.getCloudProviderConnector(placement.getAccount(), placement.getLocation());
        if (connector == null) {
            throw new CloudProviderException("Cannot retrieve cloud provider connector "
                + placement.getAccount().getCloudProvider().getCloudProviderType());
        }

        SystemManager.logger.info("cpa id: " + placement.getAccount().getId());
        system.setCloudProviderAccount(placement.getAccount());
        system.setLocation(placement.getLocation());

        this.em.flush();

        if (this.isSystemSupportedInConnector(connector)) {

            // sending command to selected connector
            Job job = null;
            try {
                job = connector.getSystemService().createSystem(systemCreate);
            } catch (ConnectorException e) {
                SystemManager.logger.info("system creation failed", e);
                throw new CloudProviderException("system creation failed");
            }
            // job returned by connector is a copy of the real connector job
            // so we can directly persist it
            job.setDescription("System creation");
            job.setUser(this.getUser());
            this.em.persist(job);

            system.setProviderAssignedId(job.getTargetEntity().getProviderAssignedId());

            job.setTargetEntity(system);
            job.setParentJob(parentJob);

            this.setJobProperty(parentJob, SystemManager.PROP_SYSTEM_SUPPORTED_IN_CONNECTOR, "ok");

            // Ask for connector to notify when job completes
            try {
                UtilsForManagers.emitJobListenerMessage(job.getProviderAssignedId(), this.ctx);
            } catch (Exception e) {
                throw new ServiceUnavailableException(e.getMessage());
            }
            this.relConnector(system.getCloudProviderAccount(), connector);

        } else {
            // implementation when System is not supported by underlying
            // connector
            Set<ComponentDescriptor> componentDescriptors = systemCreate.getSystemTemplate().getComponentDescriptors();

            // iterating through descriptors
            Iterator<ComponentDescriptor> iter = componentDescriptors.iterator();
            while (iter.hasNext()) {
                ComponentDescriptor cd = iter.next();

                if (cd.getComponentType() == ComponentType.MACHINE) {
                    // creating new machines
                    for (int i = 0; i < cd.getComponentQuantity(); i++) {
                        MachineCreate mc = new MachineCreate();
                        if (cd.getComponentQuantity() > 1) {
                            String name = cd.getName() == null ? "" : cd.getName();
                            mc.setName(name + new Integer(i).toString());
                        } else {
                            mc.setName(cd.getName());
                        }
                        MachineTemplate mt = (MachineTemplate) cd.getComponentTemplate();
                        mc.setMachineTemplate(mt);
                        mc.setDescription(cd.getDescription());
                        Map<String, String> props = cd.getProperties() == null ? new HashMap<String, String>()
                            : new HashMap<String, String>(cd.getProperties());

                        props.put("provider", placement.getAccount().getCloudProvider().getCloudProviderType());
                        if (placement.getLocation() != null) {
                            props.put("location", placement.getLocation().getCountryName());
                        }
                        mc.setProperties(props);

                        Job j = this.machineManager.createMachine(mc);
                        j.setParentJob(parentJob);

                        SystemMachine sc = (SystemMachine) this.createCollection(SystemMachine.class, j.getTargetEntity(),
                            SystemMachine.State.NOT_AVAILABLE);
                        this.em.persist(sc);
                    }
                }
                if (cd.getComponentType() == ComponentType.VOLUME) {
                    // creating new volumes
                    for (int i = 0; i < cd.getComponentQuantity(); i++) {
                        VolumeCreate vc = new VolumeCreate();
                        if (cd.getComponentQuantity() > 1) {
                            String name = cd.getName() == null ? "" : cd.getName();
                            vc.setName(name + new Integer(i).toString());
                        } else {
                            vc.setName(cd.getName());
                        }
                        VolumeTemplate vt = (VolumeTemplate) cd.getComponentTemplate();
                        vc.setVolumeTemplate(vt);
                        vc.setDescription(cd.getDescription());
                        Map<String, String> props = cd.getProperties() == null ? new HashMap<String, String>()
                            : new HashMap<String, String>(cd.getProperties());

                        props.put("provider", placement.getAccount().getCloudProvider().getCloudProviderType());
                        if (placement.getLocation() != null) {
                            props.put("location", placement.getLocation().getCountryName());
                        }
                        vc.setProperties(props);

                        Job j = this.volumeManager.createVolume(vc);
                        j.setParentJob(parentJob);

                        SystemVolume sc = (SystemVolume) this.createCollection(SystemVolume.class, j.getTargetEntity(),
                            SystemVolume.State.NOT_AVAILABLE);
                        this.em.persist(sc);
                    }
                }
                if (cd.getComponentType() == ComponentType.SYSTEM) {
                    // creating new systems
                    for (int i = 0; i < cd.getComponentQuantity(); i++) {
                        SystemCreate sc = new SystemCreate();
                        if (cd.getComponentQuantity() > 1) {
                            String name = cd.getName() == null ? "" : cd.getName();
                            sc.setName(name + new Integer(i).toString());
                        } else {
                            sc.setName(cd.getName());
                        }
                        SystemTemplate st = (SystemTemplate) cd.getComponentTemplate();
                        sc.setSystemTemplate(st);
                        sc.setDescription(cd.getDescription());
                        Map<String, String> props = cd.getProperties() == null ? new HashMap<String, String>()
                            : new HashMap<String, String>(cd.getProperties());

                        props.put("provider", placement.getAccount().getCloudProvider().getCloudProviderType());
                        if (placement.getLocation() != null) {
                            props.put("location", placement.getLocation().getCountryName());
                        }
                        sc.setProperties(props);

                        Job j = this.createSystem(sc);
                        j.setParentJob(parentJob);

                        SystemSystem ss = (SystemSystem) this.createCollection(SystemSystem.class, j.getTargetEntity(),
                            SystemSystem.State.NOT_AVAILABLE);
                        this.em.persist(ss);

                        // special case: created system is in mixed state,
                        // because it has no childs
                        // =>parent system must be in mixed state as well
                        if (((System) ss.getResource()).getState().equals(System.State.MIXED)) {
                            system.setState(System.State.MIXED);
                        }
                    }
                }
                if (cd.getComponentType() == ComponentType.NETWORK) {
                    // creating new networks
                    for (int i = 0; i < cd.getComponentQuantity(); i++) {
                        NetworkCreate nc = new NetworkCreate();
                        if (cd.getComponentQuantity() > 1) {
                            String name = cd.getName() == null ? "" : cd.getName();
                            nc.setName(name + new Integer(i).toString());
                        } else {
                            nc.setName(cd.getName());
                        }
                        NetworkTemplate nt = (NetworkTemplate) cd.getComponentTemplate();
                        nc.setNetworkTemplate(nt);
                        nc.setDescription(cd.getDescription());
                        Map<String, String> props = cd.getProperties() == null ? new HashMap<String, String>()
                            : new HashMap<String, String>(cd.getProperties());

                        props.put("provider", placement.getAccount().getCloudProvider().getCloudProviderType());
                        if (placement.getLocation() != null) {
                            props.put("location", placement.getLocation().getCountryName());
                        }
                        nc.setProperties(props);

                        Job j = this.networkManager.createNetwork(nc);
                        j.setParentJob(parentJob);

                        SystemNetwork sc = (SystemNetwork) this.createCollection(SystemNetwork.class, j.getTargetEntity(),
                            SystemNetwork.State.NOT_AVAILABLE);
                        this.em.persist(sc);
                    }
                }
            }
            // has this system any running job?
            List<Job> nestedJobs = parentJob.getNestedJobs();
            if (nestedJobs != null && nestedJobs.size() == 0) {
                // no job handling, job finished instantly and system in mixed
                // state
                parentJob.setStatus(Status.SUCCESS);
                system.setState(State.MIXED);
            }

        }

        return parentJob;

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

    @Override
    public SystemTemplate createSystemTemplate(final SystemTemplate systemT) throws CloudProviderException {

        systemT.setUser(this.getUser());
        systemT.setCreated(new Date());

        for (ComponentDescriptor cd : systemT.getComponentDescriptors()) {
            if (cd.getId() == null) {
                // no id, will be persisted as jpa entity
                cd.setUser(this.getUser());
                cd.setCreated(new Date());
                CloudTemplate ct = cd.getComponentTemplate();

                if (ct.getId() == null) {
                    // no id, the template is new: calling manager
                    // createTemplate for each one

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
                    if (ct instanceof MachineTemplate) {
                        this.machineManager.createMachineTemplate((MachineTemplate) ct);
                    }
                    if (ct instanceof CredentialsTemplate) {
                        this.credentialsManager.createCredentialsTemplate((CredentialsTemplate) ct);
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
        return UtilsForManagers.getEntityList("System", this.em, this.getUser().getUsername());
    }

    @Override
    public QueryResult<System> getSystems(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        return UtilsForManagers.getEntityList("System", this.em, this.getUser().getUsername(), first, last, filters,
            attributes, true);
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
    public SystemTemplate getSystemTemplateById(final String systemTemplateId) throws CloudProviderException {
        SystemTemplate result = this.em.find(SystemTemplate.class, new Integer(systemTemplateId));
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SystemTemplate> getSystemTemplates() throws CloudProviderException {
        return UtilsForManagers.getEntityList("SystemTemplate", this.em, this.getUser().getUsername(), false);
    }

    @Override
    public QueryResult<SystemTemplate> getSystemTemplates(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        return UtilsForManagers.getEntityList("SystemTemplate", this.em, this.getUser().getUsername(), first, last, filters,
            attributes, false);
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
        if (UtilsForManagers.getCloudResourceById(this.em, entity.getResource().getId().toString()) == null) {
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
                this.em.flush();
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
        job.setStatus(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    @Override
    public Job removeEntityFromSystem(final String systemId, final String entityId) throws CloudProviderException {

        System s = this.getSystemById(systemId);
        CloudCollectionItem ce = UtilsForManagers.getCloudCollectionById(this.em, entityId);
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

        ICloudProviderConnector connector = this.getConnector(s);

        // connector or not connector? that is the question!
        if (this.isSystemSupportedInConnector(connector)) {
            Job j;
            try {
                j = connector.getSystemService().removeEntityFromSystem(s.getProviderAssignedId(),
                    ce.getResource().getProviderAssignedId());
            } catch (ConnectorException e) {
                throw new ServiceUnavailableException(e.getMessage() + " action " + jobAction + " system id "
                    + s.getProviderAssignedId() + " " + s.getId());
            }

            j.setParentJob(parentJob);
            j.setUser(this.getUser());
            this.em.persist(j);

        } else {
            // no child job=> doing all immediately
            this.removeEntityFromSystem_Final(parentJob, s);
            parentJob.setStatus(Job.Status.SUCCESS);

        }

        return parentJob;
    }

    private void removeEntityFromSystem_Final(final Job job, final System s) throws CloudProviderException {
        String entityId = this.getJobProperty(job, SystemManager.PROP_JOB_COLLECTION_ID);
        CloudCollectionItem ce = UtilsForManagers.getCloudCollectionById(this.em, entityId);
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
        if (UtilsForManagers.getCloudResourceById(this.em, entity.getResource().getId().toString()) == null) {
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
        job.setStatus(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    public CloudCollectionItem getEntityFromSystem(final String systemId, final String entityId) throws CloudProviderException {
        CloudCollectionItem ce = UtilsForManagers.getCloudCollectionById(this.em, entityId);
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
                componentDescriptor.setUser(this.getUser());
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

        System s = this.getSystemById(systemId);

        ICloudProviderConnector connector = this.getConnector(s);

        if (this.isSystemSupportedInConnector(connector)) {
            parentJob = this.doService(systemId, "start", SystemManager.START_ACTION, properties);
        } else {
            // implementation for system not supported by underlying connector
            s.setState(State.STARTING);
            // creation of main system job
            parentJob = this.createJob("start", s);
            this.setJobProperty(parentJob, SystemManager.PROP_JOB_DETAILED_ACTION, SystemManager.START_ACTION);
            this.em.persist(parentJob);
            this.em.flush();

            for (SystemMachine m : s.getMachines()) {
                try {
                    Job j = this.machineManager.startMachine(m.getResource().getId().toString(), properties);
                    j.setParentJob(parentJob);
                } catch (BadStateException e) {
                    SystemManager.logger.debug("bad state exception:" + e.getMessage());
                }
            }
            for (SystemSystem sy : s.getSystems()) {
                Job j = this.startSystem(sy.getResource().getId().toString(), properties);
                j.setParentJob(parentJob);
            }
        }
        return parentJob;
    }

    @Override
    public Job stopSystem(final String systemId, final boolean force, final Map<String, String> properties)
        throws CloudProviderException {
        Job parentJob = null;

        System s = this.getSystemById(systemId);

        ICloudProviderConnector connector = this.getConnector(s);

        if (this.isSystemSupportedInConnector(connector)) {
            parentJob = this.doService(systemId, "stop", SystemManager.STOP_ACTION, properties);
        } else {
            // implementation for system not supported by underlying connector
            s.setState(State.STOPPING);
            // creation of main system job
            parentJob = this.createJob("stop", s);
            this.setJobProperty(parentJob, SystemManager.PROP_JOB_DETAILED_ACTION, SystemManager.STOP_ACTION);
            this.em.persist(parentJob);
            this.em.flush();

            for (SystemMachine m : s.getMachines()) {
                try {
                    Job j = this.machineManager.stopMachine(m.getResource().getId().toString(), force, properties);
                    j.setParentJob(parentJob);
                } catch (BadStateException e) {
                    SystemManager.logger.info("bad state exception:" + e.getMessage());
                }
            }
            for (SystemSystem sy : s.getSystems()) {
                Job j = this.stopSystem(sy.getResource().getId().toString(), force, properties);
                j.setParentJob(parentJob);
            }
        }
        return parentJob;
    }

    @Override
    public Job suspendSystem(final String systemId, final Map<String, String> properties) throws CloudProviderException {
        Job parentJob = null;

        System s = this.getSystemById(systemId);

        ICloudProviderConnector connector = this.getConnector(s);

        if (this.isSystemSupportedInConnector(connector)) {
            parentJob = this.doService(systemId, "suspend", SystemManager.SUSPEND_ACTION, properties);
        } else {
            // implementation for system not supported by underlying connector
            s.setState(State.SUSPENDING);
            // creation of main system job
            parentJob = this.createJob("suspend", s);
            this.setJobProperty(parentJob, SystemManager.PROP_JOB_DETAILED_ACTION, SystemManager.SUSPEND_ACTION);
            this.em.persist(parentJob);
            this.em.flush();

            for (SystemMachine m : s.getMachines()) {
                try {
                    Job j = this.machineManager.suspendMachine(m.getResource().getId().toString(), properties);
                    j.setParentJob(parentJob);
                } catch (BadStateException e) {
                    SystemManager.logger.debug("bad state exception:" + e.getMessage());
                }
            }
            for (SystemSystem sy : s.getSystems()) {
                Job j = this.suspendSystem(sy.getResource().getId().toString(), properties);
                j.setParentJob(parentJob);
            }
        }
        return parentJob;
    }

    @Override
    public Job pauseSystem(final String systemId, final Map<String, String> properties) throws CloudProviderException {
        Job parentJob = null;

        System s = this.getSystemById(systemId);

        ICloudProviderConnector connector = this.getConnector(s);

        if (this.isSystemSupportedInConnector(connector)) {
            parentJob = this.doService(systemId, "pause", SystemManager.PAUSE_ACTION, properties);
        } else {
            // implementation for system not supported by underlying connector
            s.setState(State.PAUSING);
            // creation of main system job
            parentJob = this.createJob("pause", s);
            this.setJobProperty(parentJob, SystemManager.PROP_JOB_DETAILED_ACTION, SystemManager.PAUSE_ACTION);
            this.em.persist(parentJob);
            this.em.flush();

            for (SystemMachine m : s.getMachines()) {
                try {
                    Job j = this.machineManager.pauseMachine(m.getResource().getId().toString(), properties);
                    j.setParentJob(parentJob);
                } catch (BadStateException e) {
                    SystemManager.logger.debug("bad state exception:" + e.getMessage());
                }
            }
            for (SystemSystem sy : s.getSystems()) {
                Job j = this.pauseSystem(sy.getResource().getId().toString(), properties);
                j.setParentJob(parentJob);
            }
        }
        return parentJob;
    }

    @Override
    public Job restartSystem(final String systemId, final boolean force, final Map<String, String> properties)
        throws CloudProviderException {
        Job parentJob = null;

        System s = this.getSystemById(systemId);

        ICloudProviderConnector connector = this.getConnector(s);

        if (this.isSystemSupportedInConnector(connector)) {
            parentJob = this.doService(systemId, "restart", SystemManager.RESTART_ACTION, properties);
        } else {
            // implementation for system not supported by underlying connector
            s.setState(State.STARTING);
            // creation of main system job
            parentJob = this.createJob("restart", s);
            this.setJobProperty(parentJob, SystemManager.PROP_JOB_DETAILED_ACTION, SystemManager.RESTART_ACTION);
            this.em.persist(parentJob);
            this.em.flush();

            for (SystemMachine m : s.getMachines()) {
                try {
                    Job j = this.machineManager.restartMachine(m.getResource().getId().toString(), force, properties);
                    j.setParentJob(parentJob);
                } catch (BadStateException e) {
                    SystemManager.logger.debug("bad state exception:" + e.getMessage());
                }
            }
            for (SystemSystem sy : s.getSystems()) {
                Job j = this.restartSystem(sy.getResource().getId().toString(), force, properties);
                j.setParentJob(parentJob);
            }
        }
        return parentJob;
    }

    @Override
    public Job deleteSystem(final String systemId) throws CloudProviderException {
        Job parentJob = null;

        System s = this.getSystemById(systemId);

        ICloudProviderConnector connector = this.getConnector(s);

        if (this.isSystemSupportedInConnector(connector)) {
            parentJob = this.doService(systemId, "delete", SystemManager.DELETE_ACTION, null);
        } else {
            // implementation for system not supported by underlying connector
            s.setState(State.DELETING);
            // creation of main system job
            parentJob = this.createJob("delete", s);
            this.setJobProperty(parentJob, SystemManager.PROP_JOB_DETAILED_ACTION, SystemManager.DELETE_ACTION);
            this.em.persist(parentJob);
            this.em.flush();

            // CASCADE remove is enabled for System so all SystemXXX will be
            // deleted automatically. But SystemXXX has no cascade for
            // Machine,etc because we must call related managers to remove them
            // in a clean way
            for (SystemMachine m : s.getMachines()) {
                Job j = this.machineManager.deleteMachine(m.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemVolume v : s.getVolumes()) {
                Job j = this.volumeManager.deleteVolume(v.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemSystem sy : s.getSystems()) {
                Job j = this.deleteSystem(sy.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemNetwork n : s.getNetworks()) {
                Job j = this.networkManager.deleteNetwork(n.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemCredentials c : s.getCredentials()) {
                this.credentialsManager.deleteCredentials(c.getResource().getId().toString());
                // j.setParentJob(parentJob);
            }
        }
        return parentJob;
    }

    // private methods

    private Job doService(final String systemId, final String basicAction, final String detailedAction,
        final Map<String, String> properties, final Object... params) throws CloudProviderException {

        System s = this.getSystemById(systemId);

        // implementation for system supported by underlying connector

        ICloudProviderConnector connector = this.getConnector(s);
        if (connector == null) {
            throw new CloudProviderException("no connector found");
        }

        Job parentJob = this.createJob(basicAction, s);
        this.setJobProperty(parentJob, SystemManager.PROP_JOB_DETAILED_ACTION, detailedAction);
        this.setJobProperty(parentJob, SystemManager.PROP_SYSTEM_SUPPORTED_IN_CONNECTOR, "ok");
        this.em.persist(parentJob);
        this.em.flush();

        Job j;
        try {
            if (detailedAction.equals(SystemManager.START_ACTION)) {
                j = connector.getSystemService().startSystem(s.getProviderAssignedId(), properties);
                s.setState(System.State.STARTING);
                j.setParentJob(parentJob);
            } else if (detailedAction.equals(SystemManager.STOP_ACTION)) {
                boolean force = (params.length > 0 && params[0] instanceof Boolean) ? ((Boolean) params[0]) : false;
                j = connector.getSystemService().stopSystem(s.getProviderAssignedId(), force, properties);
                s.setState(System.State.STOPPING);
                j.setParentJob(parentJob);
            } else if (detailedAction.equals(SystemManager.SUSPEND_ACTION)) {
                j = connector.getSystemService().suspendSystem(s.getProviderAssignedId(), properties);
                s.setState(System.State.SUSPENDING);
                j.setParentJob(parentJob);
            } else if (detailedAction.equals(SystemManager.PAUSE_ACTION)) {
                j = connector.getSystemService().pauseSystem(s.getProviderAssignedId(), properties);
                s.setState(System.State.PAUSING);
                j.setParentJob(parentJob);
            } else if (detailedAction.equals(SystemManager.RESTART_ACTION)) {
                boolean force = (params.length > 0 && params[0] instanceof Boolean) ? ((Boolean) params[0]) : false;
                j = connector.getSystemService().restartSystem(s.getProviderAssignedId(), force, properties);
                s.setState(System.State.STARTING);
                j.setParentJob(parentJob);
            } else if (detailedAction.equals(SystemManager.DELETE_ACTION)) {
                j = connector.getSystemService().deleteSystem(s.getProviderAssignedId());
                s.setState(System.State.DELETING);
                j.setParentJob(parentJob);
            } else {
                throw new ServiceUnavailableException("Unsupported operation action " + detailedAction + " on system id "
                    + s.getProviderAssignedId() + " " + s.getId());
            }
        } catch (ConnectorException e) {
            throw new ServiceUnavailableException(e.getMessage() + " action " + detailedAction + " system id "
                + s.getProviderAssignedId() + " " + s.getId());
        }

        j.setTargetEntity(s);
        j.setUser(this.getUser());
        this.em.persist(j);
        this.em.flush();

        // Ask for connector to notify when job completes
        try {
            UtilsForManagers.emitJobListenerMessage(j.getProviderAssignedId(), this.ctx);
        } catch (Exception e) {
            throw new ServiceUnavailableException(e.getMessage());
        }

        return parentJob;

    }

    private boolean checkQuota(final User u, final System sys) {
        /**
         * TODO Check current quota
         */
        return true;
    }

    private void relConnector(final CloudProviderAccount cpa, final ICloudProviderConnector connector)
        throws CloudProviderException {
        String cpType = cpa.getCloudProvider().getCloudProviderType();
        ICloudProviderConnectorFactory cFactory = null;
        try {
            cFactory = this.cloudProviderConnectorFactoryFinder.getCloudProviderConnectorFactory(cpType);
            String connectorId = connector.getCloudProviderId();
            cFactory.disposeCloudProviderConnector(connectorId);
        } catch (ConnectorException e) {
            throw new CloudProviderException(e.getMessage());
        }
    }

    private CloudProvider selectCloudProvider() {
        Query q = this.em.createQuery("FROM CloudProvider c WHERE c.cloudProviderType=:type");
        q.setParameter("type", "mock");

        q.setMaxResults(1);

        CloudProvider cp = null;
        @SuppressWarnings("unchecked")
        List<CloudProvider> l = q.getResultList();
        if (l.size() > 0) {
            return l.get(0);
        }
        return null;
    }

    private CloudProviderAccount selectCloudProviderAccount(final CloudProvider provider) {
        Set<CloudProviderAccount> accounts = provider.getCloudProviderAccounts();
        if (accounts.isEmpty() == false) {
            return accounts.iterator().next();
        }
        return null;
    }

    private ICloudProviderConnector getConnector(final System s) throws CloudProviderException {

        ICloudProviderConnector connector = null;

        connector = this.getCloudProviderConnector(s.getCloudProviderAccount(), s.getLocation());
        return connector;
    }

    private ICloudProviderConnector getCloudProviderConnector() throws CloudProviderException {

        CloudProvider cloudProvider = this.selectCloudProvider();
        if (cloudProvider == null) {
            throw new CloudProviderException("no provider found");
        }
        CloudProviderAccount cloudProviderAccount = this.selectCloudProviderAccount(cloudProvider);
        if (cloudProviderAccount == null) {
            throw new CloudProviderException("no provider account found");
        }

        if (cloudProviderAccount == null) {
            throw new CloudProviderException("Cloud provider account ");
        }
        ICloudProviderConnectorFactory connectorFactory = this.cloudProviderConnectorFactoryFinder
            .getCloudProviderConnectorFactory(cloudProviderAccount.getCloudProvider().getCloudProviderType());
        if (connectorFactory == null) {
            throw new CloudProviderException(" Internal error in connector factory ");
        }

        CloudProviderLocation location = null;

        SystemManager.logger.info("getCloudProviderConnector: cloudProviderAccount has id " + cloudProviderAccount.getId());

        try {
            return connectorFactory.getCloudProviderConnector(cloudProviderAccount, location);
        } catch (ConnectorException ex) {
            throw new CloudProviderException(ex.getMessage());
        }
    }

    private ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount cloudProviderAccount)
        throws CloudProviderException {

        CloudProvider cloudProvider = cloudProviderAccount.getCloudProvider();

        if (cloudProviderAccount == null) {
            throw new CloudProviderException("Cloud provider account ");
        }
        ICloudProviderConnectorFactory connectorFactory = this.cloudProviderConnectorFactoryFinder
            .getCloudProviderConnectorFactory(cloudProviderAccount.getCloudProvider().getCloudProviderType());
        if (connectorFactory == null) {
            throw new CloudProviderException(" Internal error in connector factory ");
        }

        CloudProviderLocation location = null;

        try {
            return connectorFactory.getCloudProviderConnector(cloudProviderAccount, location);
        } catch (ConnectorException e) {
            throw new CloudProviderException(e.getMessage());
        }
    }

    private ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount cloudProviderAccount,
        final CloudProviderLocation location) throws CloudProviderException {
        ICloudProviderConnectorFactory connectorFactory = this.cloudProviderConnectorFactoryFinder
            .getCloudProviderConnectorFactory(cloudProviderAccount.getCloudProvider().getCloudProviderType());
        if (connectorFactory == null) {
            throw new CloudProviderException(" Internal error in connector factory ");
        }
        try {
            return connectorFactory.getCloudProviderConnector(cloudProviderAccount, location);
        } catch (ConnectorException e) {
            SystemManager.logger.info("Connector error", e);
            throw new CloudProviderException(e.getMessage());
        }
    }

    private Job createJob(final String action, final CloudResource targetEntity) throws CloudProviderException {

        Job job = new Job();
        job.setAction(action);
        job.setCreated(new Date());
        job.setIsCancellable(false);
        job.setName("job " + action);
        job.setParentJob(null);
        job.setStatus(Status.RUNNING);
        job.setTargetEntity(targetEntity);
        job.setUser(this.getUser());
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
    private void persistSystemContent(final System providerSystem, final User user, final CloudProviderAccount account,
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

        // creating and adding objects
        for (SystemNetwork sn : networks) {
            sn.getNetwork().setUser(user);
            sn.getNetwork().setCloudProviderAccount(account);
            sn.getNetwork().setLocation(location);
            // TODO: replace with dedicated method from related manager!
            this.em.persist(sn.getResource());
            this.updateCollectionFromProvider(sn, sn.getResource(), SystemNetwork.State.AVAILABLE);
            this.em.persist(sn);
        }
        this.em.flush();

        for (SystemVolume sv : volumes) {
            sv.getVolume().setUser(user);
            sv.getVolume().setCloudProviderAccount(account);
            sv.getVolume().setLocation(location);
            // TODO: replace with dedicated method from related manager!
            this.em.persist(sv.getResource());
            this.updateCollectionFromProvider(sv, sv.getResource(), SystemVolume.State.AVAILABLE);
            this.em.persist(sv);
        }
        this.em.flush();
        if (machines != null) {
            for (SystemMachine sm : machines) {
                Machine mach = sm.getMachine();
                mach.setUser(user);
                mach.setCloudProviderAccount(account);
                mach.setLocation(location);
                mach.setCreated(new Date());

                this.machineManager.persistMachineInSystem(mach);
                this.updateCollectionFromProvider(sm, sm.getResource(), SystemMachine.State.AVAILABLE);
                this.em.persist(sm);
            }
        }
        this.em.flush();

        for (SystemSystem ss : systems) {
            ss.getSystem().setCloudProviderAccount(account);
            ss.getSystem().setLocation(location);
            ss.getSystem().setUser(user);
            this.persistSystemContent((System) ss.getResource(), user, account, location);
            this.em.persist(ss.getResource());
            this.updateCollectionFromProvider(ss, ss.getResource(), SystemSystem.State.AVAILABLE);
            this.em.persist(ss);
            this.em.flush();
        }
        this.em.flush();
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
    private void updateSystemContentState(final ICloudProviderConnector connector, final System providerSystem,
        final String jobAction) throws CloudProviderException {
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
            Machine lmanaged = (Machine) UtilsForManagers.getResourceFromProviderId(this.em, sn.getResource()
                .getProviderAssignedId());
            if (jobAction.equals(SystemManager.DELETE_ACTION)) {
                // lmanaged.setState(Machine.State.DELETED);
                this.machineManager.deleteMachineInSystem(lmanaged);
            } else {
                lmanaged.setState(((Machine) sn.getResource()).getState());
            }
        }

        for (SystemVolume sn : volumes) {
            Volume lmanaged = (Volume) UtilsForManagers.getResourceFromProviderId(this.em, sn.getResource()
                .getProviderAssignedId());
            if (jobAction.equals(SystemManager.DELETE_ACTION)) {
                lmanaged.setState(Volume.State.DELETED);
            } else {
                lmanaged.setState(((Volume) sn.getResource()).getState());
            }
        }

        for (SystemNetwork sn : networks) {
            Network lmanaged = (Network) UtilsForManagers.getResourceFromProviderId(this.em, sn.getResource()
                .getProviderAssignedId());
            if (jobAction.equals(SystemManager.DELETE_ACTION)) {
                lmanaged.setState(Network.State.DELETED);
            } else {
                lmanaged.setState(((Network) sn.getResource()).getState());
            }
        }

        for (SystemSystem sn : systems) {
            // recursion rules!
            this.updateSystemContentState(connector, (System) sn.getResource(), jobAction);
            System lmanaged = (System) UtilsForManagers.getResourceFromProviderId(this.em, sn.getResource()
                .getProviderAssignedId());
            if (jobAction.equals(SystemManager.DELETE_ACTION)) {
                lmanaged.setState(System.State.DELETED);
                sn.setState(SystemSystem.State.DELETED);
                this.removeItemFromSystemCollection(s, sn);
            } else {
                lmanaged.setState(((System) sn.getResource()).getState());
            }
        }
    }

    @Override
    public boolean jobCompletionHandler(final String notification_id) throws CloudProviderException {

        Job job;
        try {
            job = this.jobManager.getJobById(notification_id);
        } catch (ResourceNotFoundException e1) {
            SystemManager.logger.error("Could not find job " + notification_id);
            throw new CloudProviderException("Could not find job " + notification_id);
        } catch (CloudProviderException e1) {
            SystemManager.logger.error("unable to get job " + notification_id);
            throw new CloudProviderException("unable to get job " + notification_id);
        }

        SystemManager.logger.info(" System Notification for job " + job.getId());

        if (job.getNestedJobs().size() == 0) {
            // job is a connector job, waiting parent call
            SystemManager.logger.info(" connector job " + job.getId() + ", do nothing...");
            return true;
        }

        // system supported connector mode?
        String connectorMode = this.getJobProperty(job, SystemManager.PROP_SYSTEM_SUPPORTED_IN_CONNECTOR);

        if (connectorMode != null) {
            // connector supports systems
            Job connectorJob = job.getNestedJobs().get(0);

            if (connectorJob.getStatus().equals(Status.SUCCESS)) {
                // success!
                job.setStatus(Status.SUCCESS);

                // getting system owned object lists from connector
                System s = null;
                System managedSystem = (System) job.getTargetEntity();
                CloudProviderAccount cpa = managedSystem.getCloudProviderAccount();
                CloudProviderLocation location = managedSystem.getLocation();
                // querying connector
                ICloudProviderConnector connector = this.getCloudProviderConnector(cpa, location);
                if (connector == null) {
                    throw new CloudProviderException("no connector found");
                }

                // which action?
                String jobDetailedAction = this.getJobProperty(job, SystemManager.PROP_JOB_DETAILED_ACTION);

                if (!jobDetailedAction.equals(SystemManager.DELETE_ACTION)) {
                    try {
                        s = connector.getSystemService().getSystem(job.getTargetEntity().getProviderAssignedId().toString());
                    } catch (ConnectorException e) {
                        throw new CloudProviderException("unable to get system from provider");
                    }
                }

                if (jobDetailedAction.equals(SystemManager.CREATE_ACTION)) {
                    this.persistSystemContent(s, managedSystem.getUser(), cpa, location);

                    managedSystem.setMachines(s.getMachines());
                    managedSystem.setNetworks(s.getNetworks());
                    managedSystem.setState(s.getState());
                    managedSystem.setSystems(s.getSystems());
                    managedSystem.setVolumes(s.getVolumes());

                } else if (jobDetailedAction.equals(SystemManager.START_ACTION)
                    || jobDetailedAction.equals(SystemManager.STOP_ACTION)) {

                    this.updateSystemContentState(connector, s, jobDetailedAction);
                    // updating parent system state
                    ((System) job.getTargetEntity()).setState(s.getState());

                } else if (jobDetailedAction.equals(SystemManager.DELETE_ACTION)) {

                    this.updateSystemContentState(connector, (System) job.getTargetEntity(), jobDetailedAction);
                    ((System) job.getTargetEntity()).setState(System.State.DELETED);

                } else if (jobDetailedAction.equals(SystemManager.REMOVE_ENTITY_ACTION)) {
                    // removing entity
                    this.removeEntityFromSystem_Final(job, s);
                }

                if (!jobDetailedAction.equals(SystemManager.DELETE_ACTION)) {
                    this.updateSystemStatus(managedSystem.getId().toString());
                }
                ;

                this.relConnector(cpa, connector);
            } else {
                // error
                job.setStatus(connectorJob.getStatus());
                System s = (System) job.getTargetEntity();
                s.setState(State.ERROR);
                SystemManager.logger.error(" SystemHandler - connector job failed " + job.getId().toString());
                throw new CloudProviderException(" SystemHandler - connector job failed " + job.getId().toString());
            }
        } else {
            // connector doesn't support systems
            Status state = Status.SUCCESS;

            boolean failed = false;
            boolean cancelled = false;
            boolean running = false;

            try {
                job = this.jobManager.getJobById(job.getId().toString());
            } catch (ResourceNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (CloudProviderException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String jobDetailedAction = this.getJobProperty(job, SystemManager.PROP_JOB_DETAILED_ACTION);
            SystemManager.logger.info("jobDetailedAction: " + jobDetailedAction);

            // looking at child jobs status
            for (Job j : job.getNestedJobs()) {
                if (j.getStatus().equals(Status.FAILED)) {
                    failed = true;
                    SystemManager.logger.info(j.getId().toString() + " failed");
                }
                if (j.getStatus().equals(Status.CANCELLED)) {
                    cancelled = true;
                    SystemManager.logger.info(j.getId().toString() + " cancelled");
                }
                if (j.getStatus().equals(Status.RUNNING)) {
                    running = true;
                    SystemManager.logger.info(j.getId().toString() + " running");
                }
                if (j.getStatus().equals(Status.SUCCESS)) {
                    // update System in database if not already done
                    SystemManager.logger.info(job.getId().toString() + " containsKey " + j.getId().toString() + ":  "
                        + job.getProperties().containsKey(j.getId().toString()));
                    if (!(job.getProperties().containsKey(j.getId().toString()))) {
                        SystemManager.logger.info(" SystemHandler updating successful job " + j.getId() + " for main job "
                            + job.getId().toString());
                        System s = (System) job.getTargetEntity();

                        if (j.getTargetEntity() instanceof Machine) {
                            if (jobDetailedAction.equals(SystemManager.CREATE_ACTION)) {
                                SystemMachine sc = (SystemMachine) UtilsForManagers.getCloudCollectionFromCloudResource(
                                    this.em, j.getTargetEntity());
                                sc.setState(SystemMachine.State.AVAILABLE);
                                s.getMachines().add(sc);
                            }
                            job.getProperties().put(j.getId().toString(), SystemManager.HANDLED_JOB);

                        }
                        if (j.getTargetEntity() instanceof Volume) {
                            if (jobDetailedAction.equals(SystemManager.CREATE_ACTION)) {
                                SystemVolume sc = (SystemVolume) UtilsForManagers.getCloudCollectionFromCloudResource(this.em,
                                    j.getTargetEntity());
                                sc.setState(SystemVolume.State.AVAILABLE);
                                s.getVolumes().add(sc);
                            }
                            job.getProperties().put(j.getId().toString(), SystemManager.HANDLED_JOB);
                        }
                        if (j.getTargetEntity() instanceof System) {
                            if (jobDetailedAction.equals(SystemManager.CREATE_ACTION)) {
                                SystemSystem sc = (SystemSystem) UtilsForManagers.getCloudCollectionFromCloudResource(this.em,
                                    j.getTargetEntity());
                                sc.setState(SystemSystem.State.AVAILABLE);
                                s.getSystems().add(sc);
                            }
                            job.getProperties().put(j.getId().toString(), SystemManager.HANDLED_JOB);
                        }
                        if (j.getTargetEntity() instanceof Network) {
                            if (jobDetailedAction.equals(SystemManager.CREATE_ACTION)) {
                                SystemNetwork sc = (SystemNetwork) UtilsForManagers.getCloudCollectionFromCloudResource(
                                    this.em, j.getTargetEntity());
                                sc.setState(SystemNetwork.State.AVAILABLE);
                                s.getNetworks().add(sc);
                            }
                            job.getProperties().put(j.getId().toString(), SystemManager.HANDLED_JOB);
                        }

                        SystemManager.logger.info(job.getId().toString() + " contains2Key " + j.getId().toString() + ":  "
                            + job.getProperties().containsKey(j.getId().toString()));
                    }
                }
            }

            if (failed) {
                // one or more jobs are failed, so all is failed
                job.setStatus(Status.FAILED);
                System s = (System) job.getTargetEntity();
                s.setState(State.ERROR);
                SystemManager.logger.info(" SystemHandler one or more jobs are failed " + job.getId().toString());
                return true;
            }
            if (cancelled) {
                // one or more jobs are cancelled, so all is cancelled
                job.setStatus(Status.CANCELLED);
                System s = (System) job.getTargetEntity();
                s.setState(State.ERROR);
                SystemManager.logger.info(" SystemHandler one or more jobs are cancelled " + job.getId().toString());
                return true;
            }
            if (running) {
                // one or more jobs are running, so all is running
                job.setStatus(Status.RUNNING);
                SystemManager.logger.info(" SystemHandler one or more jobs are running " + job.getId().toString());
                return true;
            }

            // job success
            job.setStatus(Status.SUCCESS);
            System s = (System) job.getTargetEntity();
            SystemManager.logger.info(" SystemHandler all jobs are successful " + job.getId().toString());

            if (jobDetailedAction.equals(SystemManager.REMOVE_ENTITY_ACTION)) {
                // removing entity
                this.removeEntityFromSystem_Final(job, s);
            }

            if (jobDetailedAction.equals(SystemManager.DELETE_ACTION)) {
                s.setState(System.State.DELETED);
                this.handleEntityStateChange(System.class, s.getId().toString(), true);
            } else {
                if (!this.updateSystemStatus(s.getId().toString())) {
                    // no update, setting generic system state
                    if (jobDetailedAction.equals(SystemManager.CREATE_ACTION)) {
                        s.setState(State.STOPPED);
                    }
                    if (jobDetailedAction.equals(SystemManager.START_ACTION)) {
                        s.setState(State.STARTED);
                    }
                    if (jobDetailedAction.equals(SystemManager.STOP_ACTION)) {
                        s.setState(State.STOPPED);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void handleEntityStateChange(final Class<? extends CloudResource> entityType, final String entityId,
        final boolean deletion) {

        // getting system attachement if any
        SystemManager.logger.info("updating system state - " + entityType.getName() + " - " + entityId);
        CloudCollectionItem obj = null;
        try {
            obj = (CloudCollectionItem) this.em
                .createQuery("FROM " + CloudCollectionItem.class.getName() + " v WHERE v.resource.id=:resourceId")
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
            sys = (System) this.em
                .createQuery(
                    "FROM "
                        + System.class.getName()
                        + " v WHERE :resource member of v.machines or :resource member of v.systems or :resource member of v.volumes or :resource member of v.networks")
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job importSystem(final String source, final Map<String, String> properties) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job exportSystemTemplate(final String systemTemplateId, final String format, final String destination,
        final Map<String, String> properties) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job importSystemTemplate(final String source, final Map<String, String> properties) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

}
