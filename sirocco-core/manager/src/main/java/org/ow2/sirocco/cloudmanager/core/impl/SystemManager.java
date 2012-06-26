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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactory;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactoryFinder;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IJobListener;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteSystemManager;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ServiceUnavailableException;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.ComponentDescriptor;
import org.ow2.sirocco.cloudmanager.model.cimi.ComponentDescriptor.ComponentType;
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
import org.ow2.sirocco.cloudmanager.model.cimi.CloudCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCredentials;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemMachine;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemNetwork;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemSystem;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System.State;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemVolume;

import com.google.common.collect.ImmutableList;

@Stateless
@Remote(IRemoteSystemManager.class)
@Local(ISystemManager.class)
@SuppressWarnings("unused")
public class SystemManager implements ISystemManager {

    private static Logger logger = Logger.getLogger(SystemManager.class.getName());

    private static String CREATE_ACTION = "system creation";

    private static String START_ACTION = "system start";

    private static String STOP_ACTION = "system stop";

    private static String DELETE_ACTION = "system delete";

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

    private User getUser() throws CloudProviderException {
        String username = this.ctx.getCallerPrincipal().getName();
        return this.userManager.getUserByUsername(username);
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
        system.setProperties(systemCreate.getProperties());
        system.setState(State.CREATING);
        system.setUser(this.getUser());
        this.em.persist(system);
        this.em.flush();

        // creation of main system job
        Job parentJob = this.createJob(CREATE_ACTION, system);
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
                        cc.setName(cd.getComponentName() + new Integer(i).toString());
                    }
                    CredentialsTemplate ct = (CredentialsTemplate) cd.getComponentTemplate();
                    cc.setCredentialTemplate(ct);
                    cc.setDescription(cd.getComponentDescription());
                    cc.setProperties(cd.getProperties());

                    // no job for credentials!
                    Credentials c = credentialsManager.createCredentials(cc);
                    SystemCredentials sc = new SystemCredentials();
                    sc.setResource(c);
                    sc.setState(SystemCredentials.State.AVAILABLE);
                    this.em.persist(sc);
                    system.getCredentials().add(sc);
                }
            }
        }

        if (0 == 0) {

            ICloudProviderConnector connector = this.getCloudProviderConnector();
            if (connector == null) {
                throw new CloudProviderException("no connector found");
            }

            // sending command to selected connector
            Job job = null;
            try {
                job = connector.getSystemService().createSystem(systemCreate);
            } catch (ConnectorException e) {
                throw new CloudProviderException("system creation failed");
            }
            // job returned by connector is a copy of the real connector job
            // so we can directly persist it
            this.em.persist(job);
            job.setTargetEntity(system);
            job.setParentJob(parentJob);

            // Ask for connector to notify when job completes
            try {
                UtilsForManagers.emitJobListenerMessage(job.getProviderAssignedId(), this.ctx);
            } catch (Exception e) {
                throw new ServiceUnavailableException(e.getMessage());
            }
            this.relConnector(system, connector);

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
                            mc.setName(cd.getComponentName() + new Integer(i).toString());
                        }
                        MachineTemplate mt = (MachineTemplate) cd.getComponentTemplate();
                        mc.setMachineTemplate(mt);
                        mc.setDescription(cd.getComponentDescription());
                        mc.setProperties(cd.getProperties());

                        Job j = machineManager.createMachine(mc);
                        j.setParentJob(parentJob);

                        SystemMachine sc = new SystemMachine();
                        sc.setResource(j.getTargetEntity());
                        sc.setState(SystemMachine.State.NOT_AVAILABLE);
                        this.em.persist(sc);
                    }
                }
                if (cd.getComponentType() == ComponentType.VOLUME) {
                    // creating new volumes
                    for (int i = 0; i < cd.getComponentQuantity(); i++) {
                        VolumeCreate vc = new VolumeCreate();
                        if (cd.getComponentQuantity() > 1) {
                            vc.setName(cd.getComponentName() + new Integer(i).toString());
                        }
                        VolumeTemplate vt = (VolumeTemplate) cd.getComponentTemplate();
                        vc.setVolumeTemplate(vt);
                        vc.setDescription(cd.getComponentDescription());
                        vc.setProperties(cd.getProperties());

                        Job j = volumeManager.createVolume(vc);
                        j.setParentJob(parentJob);

                        SystemVolume sc = new SystemVolume();
                        sc.setResource(j.getTargetEntity());
                        sc.setState(SystemVolume.State.NOT_AVAILABLE);
                        this.em.persist(sc);
                    }
                }
                if (cd.getComponentType() == ComponentType.SYSTEM) {
                    // creating new systems
                    for (int i = 0; i < cd.getComponentQuantity(); i++) {
                        SystemCreate sc = new SystemCreate();
                        if (cd.getComponentQuantity() > 1) {
                            sc.setName(cd.getComponentName() + new Integer(i).toString());
                        }
                        SystemTemplate st = (SystemTemplate) cd.getComponentTemplate();
                        sc.setSystemTemplate(st);
                        sc.setDescription(cd.getComponentDescription());
                        sc.setProperties(cd.getProperties());

                        Job j = this.createSystem(sc);
                        j.setParentJob(parentJob);

                        SystemSystem ss = new SystemSystem();
                        ss.setResource(j.getTargetEntity());
                        ss.setState(SystemSystem.State.NOT_AVAILABLE);
                        this.em.persist(ss);
                    }
                }
                if (cd.getComponentType() == ComponentType.NETWORK) {
                    // creating new networks
                    for (int i = 0; i < cd.getComponentQuantity(); i++) {
                        NetworkCreate nc = new NetworkCreate();
                        if (cd.getComponentQuantity() > 1) {
                            nc.setName(cd.getComponentName() + new Integer(i).toString());
                        }
                        NetworkTemplate nt = (NetworkTemplate) cd.getComponentTemplate();
                        nc.setNetworkTemplate(nt);
                        nc.setDescription(cd.getComponentDescription());
                        nc.setProperties(cd.getProperties());

                        Job j = networkManager.createNetwork(nc);
                        j.setParentJob(parentJob);

                        SystemNetwork sc = new SystemNetwork();
                        sc.setResource(j.getTargetEntity());
                        sc.setState(SystemNetwork.State.NOT_AVAILABLE);
                        this.em.persist(sc);
                    }
                }
            }
        }

        return parentJob;

    }

    @Override
    public SystemTemplate createSystemTemplate(final SystemTemplate systemT) throws CloudProviderException {

        systemT.setUser(this.getUser());
        systemT.setCreated(new Date());

        for (ComponentDescriptor cd : systemT.getComponentDescriptors()) {
            if ("".equals(cd.getId()) || cd.getId() == null) {
                // no id, will be persisted as jpa entity
                cd.setUser(this.getUser());
                cd.setCreated(new Date());
            }
        }

        this.em.persist(systemT);
        this.em.flush();
        return systemT;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<System> getSystems() throws CloudProviderException {
        return UtilsForManagers.getEntityList("System", this.em, this.getUser().getUsername());
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
        return UtilsForManagers.getEntityList("SystemTemplate", this.em, this.getUser().getUsername());
    }

    private ComponentDescriptor getComponentDescriptorById(final String componentDescriptorId) throws CloudProviderException {
        ComponentDescriptor result = this.em.find(ComponentDescriptor.class, new Integer(componentDescriptorId));
        return result;
    }

    public List<CloudCollection> getEntityFromSystem(final String systemId, final String collectionType) {

        return null;

    }

    @Override
    public Job addEntityToSystem(final String systemId, final CloudCollection entity) throws CloudProviderException {

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
        this.em.persist(entity);
        String jobAction = null;
        if (entity instanceof SystemMachine) {
            s.getMachines().add((SystemMachine) entity);
            jobAction = ADD_MACHINE_ACTION;
        } else if (entity instanceof SystemVolume) {
            s.getVolumes().add((SystemVolume) entity);
            jobAction = ADD_VOLUME_ACTION;
        } else if (entity instanceof SystemSystem) {
            s.getSystems().add((SystemSystem) entity);
            jobAction = ADD_SYSTEM_ACTION;
        } else if (entity instanceof SystemNetwork) {
            s.getNetworks().add((SystemNetwork) entity);
            jobAction = ADD_NETWORK_ACTION;
        } else if (entity instanceof SystemCredentials) {
            s.getCredentials().add((SystemCredentials) entity);
            jobAction = ADD_CREDENTIAL_ACTION;
        } else {
            throw new CloudProviderException("object type can't be owned by a system");
        }

        // for system not supported by underlying connector
        Job job = this.createJob(jobAction, s);
        job.setStatus(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    @Override
    public Job removeEntityFromSystem(final String systemId, final String entityId) throws CloudProviderException {

        // should we delete linked resource? (Machine,etc)

        System s = this.getSystemById(systemId);
        CloudCollection ce = UtilsForManagers.getCloudCollectionById(this.em, entityId);
        if (ce == null || s == null) {
            throw new CloudProviderException("bad id given in parameter");
        }
        String jobAction = null;
        if (ce instanceof SystemMachine) {
            s.getMachines().remove((SystemMachine) ce);
            jobAction = REMOVE_MACHINE_ACTION;
        } else if (ce instanceof SystemVolume) {
            s.getVolumes().remove((SystemVolume) ce);
            jobAction = REMOVE_VOLUME_ACTION;
        } else if (ce instanceof SystemSystem) {
            s.getSystems().remove((SystemSystem) ce);
            jobAction = REMOVE_SYSTEM_ACTION;
        } else if (ce instanceof SystemNetwork) {
            s.getNetworks().remove((SystemNetwork) ce);
            jobAction = REMOVE_NETWORK_ACTION;
        } else if (ce instanceof SystemCredentials) {
            s.getCredentials().remove((SystemCredentials) ce);
            jobAction = REMOVE_CREDENTIAL_ACTION;
        } else {
            throw new CloudProviderException("object type can't be owned by a system");
        }
        // deleting SystemXXX
        this.em.remove(ce);

        // for system not supported by underlying connector
        Job job = this.createJob(jobAction, s);
        job.setStatus(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    @Override
    public Job updateEntityInSystem(final String systemId, final CloudCollection entity) throws CloudProviderException {

        // should we update the linked resource?
        System s = this.getSystemById(systemId);

        if (entity == null || s == null) {
            throw new CloudProviderException("bad id given in parameter");
        }
        if (UtilsForManagers.getCloudResourceById(this.em, entity.getResource().getId().toString()) == null) {
            throw new CloudProviderException("nonexisting resource linked to this SystemEntity");
        }

        String jobAction = null;
        if (entity instanceof SystemMachine) {
            jobAction = UPDATE_MACHINE_ACTION;
        } else if (entity instanceof SystemVolume) {
            jobAction = UPDATE_VOLUME_ACTION;
        } else if (entity instanceof SystemSystem) {
            jobAction = UPDATE_SYSTEM_ACTION;
        } else if (entity instanceof SystemNetwork) {
            jobAction = UPDATE_NETWORK_ACTION;
        } else if (entity instanceof SystemCredentials) {
            jobAction = UPDATE_CREDENTIAL_ACTION;
        } else {
            throw new CloudProviderException("object type can't be owned by a system");
        }
        this.em.merge(entity);

        // for system not supported by underlying connector
        Job job = this.createJob(jobAction, s);
        job.setStatus(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    @Override
    public boolean addComponentDescriptorToSystemTemplate(final ComponentDescriptor componentDescriptor,
        final String systemTemplateId) throws CloudProviderException {
        SystemTemplate s = this.getSystemTemplateById(systemTemplateId);
        Set<ComponentDescriptor> descrs = s.getComponentDescriptors();

        descrs.add(componentDescriptor);

        s.setComponentDescriptors(descrs);

        this.em.persist(s);

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

        s.setComponentDescriptors(descrs);

        this.em.persist(s);

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
    public System updateSystem(final String id, final Map<String, Object> updatedAttributes) throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new CloudProviderException("action not implemented");
        // return null;
    }

    @Override
    public SystemTemplate updateSystemTemplate(final String id, final Map<String, Object> updatedAttributes)
        throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new CloudProviderException("action not implemented");
        // return null;
    }

    @Override
    public Job startSystem(final String systemId) throws CloudProviderException {

        Job parentJob = null;
        if (0 == 0) {
            parentJob = this.doService(systemId, START_ACTION);
        } else {
            // implementation for system not supported by underlying connector
            System s = this.getSystemById(systemId);
            s.setState(State.STARTING);
            // creation of main system job
            parentJob = this.createJob(START_ACTION, s);
            this.em.persist(parentJob);
            this.em.flush();

            for (SystemMachine m : s.getMachines()) {
                Job j = machineManager.startMachine(m.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemVolume v : s.getVolumes()) {
                // Job j=volumeManager..startVolume(v.getId().toString());
                // j.setParentJob(parentJob);
            }
            for (SystemSystem sy : s.getSystems()) {
                Job j = this.startSystem(sy.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemNetwork n : s.getNetworks()) {
                Job j = networkManager.startNetwork(n.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemCredentials c : s.getCredentials()) {
                // Job j=volumeManager..startVolume(v.getId().toString());
                // j.setParentJob(parentJob);
            }
        }
        return parentJob;
    }

    @Override
    public Job stopSystem(final String systemId) throws CloudProviderException {
        Job parentJob = null;
        if (0 == 0) {
            parentJob = this.doService(systemId, STOP_ACTION);
        } else {
            // implementation for system not supported by underlying connector
            System s = this.getSystemById(systemId);
            s.setState(State.STOPPING);
            // creation of main system job
            parentJob = this.createJob(STOP_ACTION, s);
            this.em.persist(parentJob);
            this.em.flush();

            for (SystemMachine m : s.getMachines()) {
                Job j = machineManager.stopMachine(m.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemVolume v : s.getVolumes()) {
                // Job j=volumeManager..startVolume(v.getId().toString());
                // j.setParentJob(parentJob);
            }
            for (SystemSystem sy : s.getSystems()) {
                Job j = this.stopSystem(sy.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemNetwork n : s.getNetworks()) {
                Job j = networkManager.stopNetwork(n.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemCredentials c : s.getCredentials()) {
                // Job j=volumeManager..startVolume(v.getId().toString());
                // j.setParentJob(parentJob);
            }
        }
        return parentJob;
    }

    @Override
    public Job deleteSystem(final String systemId) throws CloudProviderException {
        Job parentJob = null;
        if (0 == 0) {
            parentJob = this.doService(systemId, DELETE_ACTION);
        } else {
            // implementation for system not supported by underlying connector
            System s = this.getSystemById(systemId);
            s.setState(State.DELETING);
            // creation of main system job
            parentJob = this.createJob(DELETE_ACTION, s);
            this.em.persist(parentJob);
            this.em.flush();

            // CASCADE remove is enabled for System so all SystemXXX will be
            // deleted automatically. But SystemXXX has no cascade for
            // Machine,etc because we must call related managers to remove them
            // in a clean way
            for (SystemMachine m : s.getMachines()) {
                Job j = machineManager.deleteMachine(m.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemVolume v : s.getVolumes()) {
                Job j = volumeManager.deleteVolume(v.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemSystem sy : s.getSystems()) {
                Job j = this.deleteSystem(sy.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemNetwork n : s.getNetworks()) {
                Job j = networkManager.deleteNetwork(n.getResource().getId().toString());
                j.setParentJob(parentJob);
            }
            for (SystemCredentials c : s.getCredentials()) {
                credentialsManager.deleteCredentials(c.getResource().getId().toString());
                // j.setParentJob(parentJob);
            }
        }
        return parentJob;
    }

    // private methods

    private Job doService(final String systemId, final String action) throws CloudProviderException {

        System s = this.getSystemById(systemId);

        // implementation for system not supported by underlying connector

        /*
         * ICloudProviderConnector connector = this.getConnector(s); if
         * (connector == null) { throw new
         * CloudProviderException("no connector found"); } Job j; try { if
         * (action.equals("start")) { j =
         * connector.getSystemService().startSystem( s.getProviderAssignedId());
         * s.setState(System.State.STARTING); } else if (action.equals("stop"))
         * { j = connector.getSystemService().stopSystem(
         * s.getProviderAssignedId()); s.setState(System.State.STOPPING); } else
         * { throw new ServiceUnavailableException(
         * "Unsupported operation action " + action + " on system id " +
         * s.getProviderAssignedId() + " " + s.getId()); } } catch
         * (ConnectorException e) { throw new
         * ServiceUnavailableException(e.getMessage() + " action " + action +
         * " system id " + s.getProviderAssignedId() + " " + s.getId()); }
         * j.setTargetEntity(s); j.setUser(this.getUser()); this.em.persist(j);
         * this.em.flush(); if (j.getStatus() == Job.Status.RUNNING) { try {
         * connector.setNotificationOnJobCompletion(j .getProviderAssignedId());
         * } catch (Exception e) { throw new
         * ServiceUnavailableException(e.getMessage() + "  system " + action); }
         * } // Ask for connector to notify when job completes try {
         * connector.setNotificationOnJobCompletion(j.getProviderAssignedId());
         * } catch (Exception e) { throw new
         * ServiceUnavailableException(e.getMessage()); } this.relConnector(s,
         * connector);
         */

        return null;

    }

    private boolean checkQuota(final User u, final System sys) {
        /**
         * TODO Check current quota
         */
        return true;
    }

    private void relConnector(final System ce, final ICloudProviderConnector connector) throws CloudProviderException {
        String cpType = ce.getCloudProviderAccount().getCloudProvider().getCloudProviderType();
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

        connector = this.getCloudProviderConnector(s.getCloudProviderAccount());
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

        return connectorFactory.getCloudProviderConnector(cloudProviderAccount, location);
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

        return connectorFactory.getCloudProviderConnector(cloudProviderAccount, location);
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

        return job;
    }

    @Override
    public boolean jobCompletionHandler(final String notification_id) throws CloudProviderException {

        Job job;
        try {
            job = jobManager.getJobById(notification_id);
        } catch (ResourceNotFoundException e1) {
            SystemManager.logger.info("Could not find job " + notification_id);
            return false;
        } catch (CloudProviderException e1) {
            SystemManager.logger.info("unable to get job " + notification_id);
            return false;
        }

        SystemManager.logger.info(" System Notification for job " + job.getId());

        Status state = Status.SUCCESS;

        boolean failed = false;
        boolean cancelled = false;
        boolean running = false;

        try {
            job = jobManager.getJobById(job.getId().toString());
        } catch (ResourceNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CloudProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (Job j : job.getNestedJobs()) {
            if (j.getStatus().equals(Status.FAILED)) {
                failed = true;
            }
            if (j.getStatus().equals(Status.CANCELLED)) {
                cancelled = true;
            }
            if (j.getStatus().equals(Status.RUNNING)) {
                running = true;
            }
            if (j.getStatus().equals(Status.SUCCESS)) {
                // update System in database if not already done
                if (!job.getProperties().containsKey(j.getId().toString())) {
                    SystemManager.logger.info(" SystemHandler updating successful job " + job.getId() + " for main job "
                        + job.getId().toString());
                    System s = (System) job.getTargetEntity();

                    if (j.getTargetEntity() instanceof Machine) {
                        if (job.getAction().equals(SystemManager.CREATE_ACTION)) {
                            SystemMachine sc = (SystemMachine) UtilsForManagers.getCloudCollectionFromCloudResource(this.em,
                                j.getTargetEntity());
                            sc.setState(SystemMachine.State.AVAILABLE);
                            s.getMachines().add(sc);
                        }
                        job.getProperties().put(j.getId().toString(), HANDLED_JOB);
                    }
                    if (j.getTargetEntity() instanceof Volume) {
                        if (job.getAction().equals(SystemManager.CREATE_ACTION)) {
                            SystemVolume sc = (SystemVolume) UtilsForManagers.getCloudCollectionFromCloudResource(this.em,
                                j.getTargetEntity());
                            sc.setState(SystemVolume.State.AVAILABLE);
                            s.getVolumes().add(sc);
                        }
                        job.getProperties().put(j.getId().toString(), HANDLED_JOB);
                    }
                    if (j.getTargetEntity() instanceof System) {
                        if (job.getAction().equals(SystemManager.CREATE_ACTION)) {
                            SystemSystem sc = (SystemSystem) UtilsForManagers.getCloudCollectionFromCloudResource(this.em,
                                j.getTargetEntity());
                            sc.setState(SystemSystem.State.AVAILABLE);
                            s.getSystems().add(sc);
                        }
                        job.getProperties().put(j.getId().toString(), HANDLED_JOB);
                    }
                    if (j.getTargetEntity() instanceof Network) {
                        if (job.getAction().equals(SystemManager.CREATE_ACTION)) {
                            SystemNetwork sc = (SystemNetwork) UtilsForManagers.getCloudCollectionFromCloudResource(this.em,
                                j.getTargetEntity());
                            sc.setState(SystemNetwork.State.AVAILABLE);
                            s.getNetworks().add(sc);
                        }
                        job.getProperties().put(j.getId().toString(), HANDLED_JOB);
                    }
                    /*
                     * if (j.getTargetEntity() instanceof Credentials) {
                     * s.getNetworks().add((Network)j.getTargetEntity());
                     * job.getProperties().put(j.getId().toString(),
                     * HANDLED_JOB); }
                     */}
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

        if (job.getAction().equals(SystemManager.CREATE_ACTION)) {
            s.setState(State.CREATED);
        }
        if (job.getAction().equals(SystemManager.START_ACTION)) {
            s.setState(State.STARTED);
        }
        if (job.getAction().equals(SystemManager.STOP_ACTION)) {
            s.setState(State.STOPPED);
        }
        if (job.getAction().equals(SystemManager.DELETE_ACTION)) {
            s.setState(System.State.DELETED);
            // this.em.remove(s);
        }

        // Find the system by providerAssignedId (or the job as well)
        /*
         * String jid = job.getProviderAssignedId().toString(); //
         * providerAssignedSystemId String pasid =
         * job.getTargetEntity().getId().toString(); Job jpersisted = null; try
         * { jpersisted = (Job) this.em
         * .createQuery("FROM Job j WHERE j.providerAssignedId=:jid")
         * .setParameter("jid", jid).getSingleResult(); } catch
         * (NoResultException e) { // ignore for now
         * SystemManager.logger.info("Cannot find job for system" + pasid);
         * return false; } catch (NonUniqueResultException e) {
         * SystemManager.logger.info("No single job for system !!" + pasid);
         * return false; } catch (Exception e) { SystemManager.logger
         * .info("Internal error in finding job for system" + pasid); return
         * false; } System sPersisted = null; try { if (jpersisted == null) {
         * //** // * find the system from its providerAssignedId in fact there
         * // * could be more than one machine with same same // *
         * providerAssignedId? // * sPersisted = (System) this.em .createQuery(
         * "FROM System s WHERE s.providerAssignedId=:pamid")
         * .setParameter("pamid", pasid).getSingleResult(); } else { // find the
         * machine from its id Integer mid =
         * Integer.valueOf(jpersisted.getTargetEntity() .getId().toString());
         * sPersisted = this.em.find(System.class, mid); } } catch
         * (NoResultException e) {
         * SystemManager.logger.info("Could not find the system or job for " +
         * pasid); return false; } catch (NonUniqueResultException e) {
         * SystemManager.logger.info("Multiple system found for " + pasid);
         * return false; } catch (Exception e) { SystemManager.logger
         * .info("Unknown error : Could not find the system or job for " +
         * pasid); return false; } // update the system by invoking the
         * connector CloudProviderAccount cpa =
         * sPersisted.getCloudProviderAccount(); ICloudProviderConnector
         * connector; try { connector = this.getCloudProviderConnector(cpa); }
         * catch (CloudProviderException e) { // no point to return false?
         * SystemManager.logger.info("Could not get cloud connector " +
         * e.getMessage()); return false; } // TODO: update system in the
         * database... this.em.flush();
         */
        return true;
    }

}
