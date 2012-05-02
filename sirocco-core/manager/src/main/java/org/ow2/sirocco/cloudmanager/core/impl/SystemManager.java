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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactory;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactoryFinder;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.connector.api.ISystemService;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IJobListener;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteSystemManager;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ServiceUnavailableException;
import org.ow2.sirocco.cloudmanager.core.utils.PasswordValidator;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.*;
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
import org.ow2.sirocco.cloudmanager.model.cimi.System;
import org.ow2.sirocco.cloudmanager.model.cimi.System.State;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@Stateless(name = ISystemManager.EJB_JNDI_NAME, mappedName = ISystemManager.EJB_JNDI_NAME)
@Remote(IRemoteSystemManager.class)
@Local(ISystemManager.class)
@SuppressWarnings("unused")
public class SystemManager implements ISystemManager {

    private static Logger logger = Logger.getLogger(MachineImageManager.class
            .getName());
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
    
    private User getUser() throws CloudProviderException{
        String username = this.ctx.getCallerPrincipal().getName();
        return this.userManager.getUserByUsername(username);
    }

    @Override
    public Job createSystem(SystemCreate systemCreate)
            throws CloudProviderException {

        // this.checkQuota(userManager.getUserByUsername(this.user), system);

        ICloudProviderConnector connector = getCloudProviderConnector();
        if (connector == null) {
            throw new CloudProviderException("no connector found");
        }

        // sending command to connector selected
        Job job = null;
        try {
            job = connector.getSystemService().createSystem(systemCreate);
        } catch (ConnectorException e) {
            throw new CloudProviderException("system creation failed");
        }

        // creation of entities in the base
        System system = new System();
        system.setCreated(new Date());
        system.setDescription(systemCreate.getDescription());
        system.setLocation(null);
        system.setName(systemCreate.getName());
        system.setProperties(systemCreate.getProperties());
        system.setState(State.CREATING);
        system.setUser(this.getUser());

        // persist
        this.em.persist(system);
        this.em.flush();// useless?
        job.setTargetEntity(system);
        
        // Ask for connector to notify when job completes
        try {
            connector.setNotificationOnJobCompletion(job.getProviderAssignedId());
        } catch (Exception e) {
            throw new ServiceUnavailableException(e.getMessage());
        }
        
        this.relConnector(system, connector);

        return job;
    }

    @Override
    public SystemTemplate createSystemTemplate(SystemTemplate systemT)
            throws CloudProviderException {
        
        systemT.setUser(this.getUser());
        systemT.setCreated(new Date());
        
        
        this.em.persist(systemT);
        this.em.flush();
        return systemT;
    }

    @Override
    public System getSystemById(String systemId) throws CloudProviderException {
        System result = this.em.find(System.class, new Integer(systemId));
        return result;
    }

    @Override
    public SystemTemplate getSystemTemplateById(String systemTemplateId)
            throws CloudProviderException {
        SystemTemplate result = this.em.find(SystemTemplate.class, new Integer(
                systemTemplateId));
        return result;
    }

    private ComponentDescriptor getComponentDescriptorById(
            String componentDescriptorId) throws CloudProviderException {
        ComponentDescriptor result = this.em.find(ComponentDescriptor.class,
                new Integer(componentDescriptorId));
        return result;
    }

    @Override
    public Job addVolumeToSystem(Volume volume, String systemId)
            throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<Volume> vols = s.getVolumeColl().getVolumes();
        vols.add(volume);
        s.getVolumeColl().setVolumes(vols);
        this.em.persist(s);

        // TODO: Jobs...

        Job job = createJob("addVolumeToSystem", s);

        this.em.persist(job);

        return job;
    }

    @Override
    public Job removeVolumeFromSystem(String volumeId, String systemId)
            throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<Volume> vols = s.getVolumeColl().getVolumes();
        Volume v = this.volumeManager.getVolumeById(volumeId);

        for (Volume vol : vols) {
            if (vol.getId().equals(v.getId())) {
                vols.remove(vol);
            }
        }

        s.getVolumeColl().setVolumes(vols);
        this.em.persist(s);

        // TODO: Jobs...

        Job job = createJob("removeVolumeFromSystem", s);

        this.em.persist(job);

        return job;
    }

    @Override
    public Job addSystemToSystem(System system, String systemId)
            throws CloudProviderException {
        System s = this.getSystemById(systemId);
        Set<System> syss = s.getSystemColl().getItems();
        syss.add(system);
        s.getSystemColl().setItems(syss);
        this.em.persist(s);

        // TODO: Jobs...

        Job job = createJob("addSystemToSystem", s);

        this.em.persist(job);

        return job;
    }

    @Override
    public Job removeSystemFromSystem(String systemToRemoveId, String systemId)
            throws CloudProviderException {
        System s = this.getSystemById(systemId);
        Set<System> syss = s.getSystemColl().getItems();
        System sRemove = this.getSystemById(systemToRemoveId);

        for (System sys : syss) {
            if (sys.getId().equals(sRemove.getId())) {
                syss.remove(sys);
            }
        }

        s.getSystemColl().setItems(syss);
        this.em.persist(s);

        // TODO: Jobs...

        Job job = createJob("removeSystemFromSystem", s);

        this.em.persist(job);

        return job;
    }

    @Override
    public Job addMachineToSystem(Machine machine, String systemId)
            throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<Machine> machines = s.getMachineColl().getMachines();
        machines.add(machine);
        s.getMachineColl().setMachines(machines);
        this.em.persist(s);

        // TODO: Jobs...

        Job job = createJob("addMachineToSystem", s);

        this.em.persist(job);

        return job;
    }

    @Override
    public Job removeMachineFromSystem(String machineId, String systemId)
            throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<Machine> machines = s.getMachineColl().getMachines();
        Machine m = this.machineManager.getMachineById(machineId);

        for (Machine mach : machines) {
            if (mach.getId().equals(m.getId())) {
                machines.remove(mach);
            }
        }

        s.getMachineColl().setMachines(machines);
        this.em.persist(s);

        // TODO: Jobs...

        Job job = createJob("removeMachineFromSystem", s);

        this.em.persist(job);

        return job;
    }

    @Override
    public Job addCredentialToSystem(Credentials credential, String systemId)
            throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<Credentials> credentials = s.getCredentialColl().getCredentials();
        credentials.add(credential);
        s.getCredentialColl().setCredentials(credentials);
        this.em.persist(s);

        // TODO: Jobs...

        Job job = createJob("addCredentialToSystem", s);

        this.em.persist(job);

        return job;
    }

    @Override
    public Job removeCredentialFromSystem(String credentialId, String systemId)
            throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<Credentials> credentials = s.getCredentialColl().getCredentials();
        Credentials c = this.credentialsManager
                .getCredentialsById(credentialId);

        for (Credentials cred : credentials) {
            if (cred.getId().equals(c.getId())) {
                credentials.remove(cred);
            }
        }

        s.getCredentialColl().setCredentials(credentials);
        this.em.persist(s);

        // TODO: Jobs...

        Job job = createJob("removeMachineFromSystem", s);

        this.em.persist(job);

        return job;
    }

    @Override
    public boolean addComponentDescriptorToSystemTemplate(
            ComponentDescriptor componentDescriptor, String systemTemplateId)
            throws CloudProviderException {
        SystemTemplate s = this.getSystemTemplateById(systemTemplateId);
        Set<ComponentDescriptor> descrs = s.getComponentDescriptors();

        descrs.add(componentDescriptor);

        s.setComponentDescriptors(descrs);

        this.em.persist(s);

        return true;
    }

    @Override
    public boolean removeComponentDescriptorFromSystemTemplate(
            String componentDescriptorId, String systemTemplateId)
            throws CloudProviderException {
        SystemTemplate s = this.getSystemTemplateById(systemTemplateId);
        Set<ComponentDescriptor> descrs = s.getComponentDescriptors();
        ComponentDescriptor cd = this
                .getComponentDescriptorById(componentDescriptorId);

        for (ComponentDescriptor cdesc : descrs) {
            if (cdesc.getId().equals(cd.getId())) {
                descrs.remove(cdesc);
            }
        }

        s.setComponentDescriptors(descrs);

        this.em.persist(s);

        return true;
    }

    @Override
    public System updateComponentDescriptor(String id,
            Map<String, Object> updatedAttributes)
            throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public System updateSystem(String id, Map<String, Object> updatedAttributes)
            throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SystemTemplate updateSystemTemplate(String id,
            Map<String, Object> updatedAttributes)
            throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job startSystem(String systemId) throws CloudProviderException {
        Job j = this.doService(systemId, "start");
        return j;
    }

    @Override
    public Job stopSystem(String systemId) throws CloudProviderException {
        Job j = this.doService(systemId, "stop");
        return j;
    }

    @Override
    public Job deleteSystem(String systemId) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    // private methods

    private Job doService(final String systemId, final String action)
            throws CloudProviderException {

        System s = this.getSystemById(systemId);
        ICloudProviderConnector connector = getConnector(s);
        if (connector == null) {
            throw new CloudProviderException("no connector found");
        }
        Job j;

        try {
            if (action.equals("start")) {
                j = connector.getSystemService().startSystem(
                        s.getProviderAssignedId());
                s.setState(System.State.STARTING);
            } else if (action.equals("stop")) {
                j = connector.getSystemService().stopSystem(
                        s.getProviderAssignedId());
                s.setState(System.State.STOPPING);
            } else {
                throw new ServiceUnavailableException(
                        "Unsupported operation action " + action
                                + " on system id " + s.getProviderAssignedId()
                                + " " + s.getId());
            }
        } catch (ConnectorException e) {
            throw new ServiceUnavailableException(e.getMessage() + " action "
                    + action + " system id " + s.getProviderAssignedId() + " "
                    + s.getId());

        }
        
        j.setTargetEntity(s);
        j.setUser(this.getUser());
        
        this.em.persist(j);
        this.em.flush();
        
        if (j.getStatus() == Job.Status.RUNNING) {
            try {
                connector.setNotificationOnJobCompletion(j.getProviderAssignedId());
            } catch (Exception e) {
                throw new ServiceUnavailableException(e.getMessage() + "  system " +action);
            }
        }
        
        
        // Ask for connector to notify when job completes
        try {
            connector.setNotificationOnJobCompletion(j.getProviderAssignedId());
        } catch (Exception e) {
            throw new ServiceUnavailableException(e.getMessage());
        }
        
        this.relConnector(s, connector);

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
        Query q = this.em
                .createQuery("FROM CloudProvider c WHERE c.cloudProviderType=:type");
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

    private CloudProviderAccount selectCloudProviderAccount(
            CloudProvider provider) {
        Set<CloudProviderAccount> accounts = provider
                .getCloudProviderAccounts();
        if (accounts.isEmpty() == false) {
            return accounts.iterator().next();
        }
        return null;
    }

    private ICloudProviderConnector getConnector(final System s)
            throws CloudProviderException {

        ICloudProviderConnector connector = null;

        connector = this.getCloudProviderConnector(s.getCloudProviderAccount());
        return connector;
    }

    private ICloudProviderConnector getCloudProviderConnector()
            throws CloudProviderException {

        CloudProvider cloudProvider = selectCloudProvider();
        if (cloudProvider == null) {
            throw new CloudProviderException("no provider found");
        }
        CloudProviderAccount cloudProviderAccount = selectCloudProviderAccount(cloudProvider);
        if (cloudProviderAccount == null) {
            throw new CloudProviderException("no provider account found");
        }

        if (cloudProviderAccount == null) {
            throw new CloudProviderException("Cloud provider account ");
        }
        ICloudProviderConnectorFactory connectorFactory = this.cloudProviderConnectorFactoryFinder
                .getCloudProviderConnectorFactory(cloudProviderAccount
                        .getCloudProvider().getCloudProviderType());
        if (connectorFactory == null) {
            throw new CloudProviderException(
                    " Internal error in connector factory ");
        }

        CloudProviderLocation location = null;

        return connectorFactory.getCloudProviderConnector(cloudProviderAccount,
                location);
    }

    private ICloudProviderConnector getCloudProviderConnector(
            CloudProviderAccount cloudProviderAccount)
            throws CloudProviderException {

        CloudProvider cloudProvider = cloudProviderAccount.getCloudProvider();

        if (cloudProviderAccount == null) {
            throw new CloudProviderException("Cloud provider account ");
        }
        ICloudProviderConnectorFactory connectorFactory = this.cloudProviderConnectorFactoryFinder
                .getCloudProviderConnectorFactory(cloudProviderAccount
                        .getCloudProvider().getCloudProviderType());
        if (connectorFactory == null) {
            throw new CloudProviderException(
                    " Internal error in connector factory ");
        }

        CloudProviderLocation location = null;

        return connectorFactory.getCloudProviderConnector(cloudProviderAccount,
                location);
    }

    private Job createJob(String action, CloudEntity targetEntity)
            throws CloudProviderException {

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
    public boolean completionHandler(Job job) {
        /**
         * Find the system by providerAssignedId (or the job as well)
         */
        String jid = job.getProviderAssignedId().toString();
        /** providerAssignedSystemId */
        String pasid = job.getTargetEntity().getId().toString();
        Job jpersisted = null;
        SystemManager.logger.info(" Notification for job " + job.getProviderAssignedId() + " " + pasid);
        try {
            jpersisted = (Job) this.em.createQuery("FROM Job j WHERE j.providerAssignedId=:jid").setParameter("jid", jid)
                .getSingleResult();
        } catch (NoResultException e) {
            /** ignore for now */
            SystemManager.logger.info("Cannot find job for system" + pasid);
            return false;
        } catch (NonUniqueResultException e) {
            SystemManager.logger.info("No single job for system !!" + pasid);
            return false;
        } catch (Exception e) {
            SystemManager.logger.info("Internal error in finding job for system" + pasid);
            return false;
        }
        System sPersisted = null;

        try {
            if (jpersisted == null) {
                /**
                 * find the system from its providerAssignedId in fact there
                 * could be more than one machine with same same
                 * providerAssignedId?
                 */
                sPersisted = (System) this.em.createQuery("FROM System s WHERE s.providerAssignedId=:pamid")
                    .setParameter("pamid", pasid).getSingleResult();

            } else {
                /** find the machine from its id */
                Integer mid = Integer.valueOf(jpersisted.getTargetEntity().getId().toString());
                sPersisted = this.em.find(System.class, mid);
            }

        } catch (NoResultException e) {
            SystemManager.logger.info("Could not find the system or job for " + pasid);
            return false;
        } catch (NonUniqueResultException e) {
            SystemManager.logger.info("Multiple system found for " + pasid);
            return false;
        } catch (Exception e) {
            SystemManager.logger.info("Unknown error : Could not find the system or job for " + pasid);
            return false;
        }

        /** update the system by invoking the connector */
        CloudProviderAccount cpa = sPersisted.getCloudProviderAccount();
        ICloudProviderConnector connector;
        try {
            connector = this.getCloudProviderConnector(cpa);
        } catch (CloudProviderException e) {
            /** no point to return false? */
            SystemManager.logger.info("Could not get cloud connector " + e.getMessage());
            return false;
        }
        
        //TODO: update system in the database...

        this.em.flush();
        return true;
    }
    
    

}
