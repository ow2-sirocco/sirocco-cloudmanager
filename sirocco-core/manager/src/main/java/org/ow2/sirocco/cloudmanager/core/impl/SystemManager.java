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
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteSystemManager;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ServiceUnavailableException;
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
import org.ow2.sirocco.cloudmanager.model.cimi.System;
import org.ow2.sirocco.cloudmanager.model.cimi.System.State;
import org.ow2.sirocco.cloudmanager.model.cimi.SystemCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.SystemTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@Stateless
@Remote(IRemoteSystemManager.class)
@Local(ISystemManager.class)
@SuppressWarnings("unused")
public class SystemManager implements ISystemManager {

    private static Logger logger = Logger.getLogger(SystemManager.class
            .getName());

    private static String CREATE_ACTION = "system creation";
    private static String ADD_VOLUME_ACTION = "addVolumeToSystem";
    private static String START_ACTION = "system start";
    private static String STOP_ACTION = "system stop";
    private static String DELETE_ACTION = "system delete";
    private static String REMOVE_MACHINE_ACTION = "removeMachineFromSystem";
    private static String ADD_CREDENTIAL_ACTION = "addCredentialToSystem";
    private static String REMOVE_CREDENTIAL_ACTION = "removeCredentialFromSystem";
    private static String ADD_MACHINE_ACTION = "addMachineToSystem";
    private static String REMOVE_SYSTEM_ACTION = "removeSystemFromSystem";
    private static String ADD_SYSTEM_ACTION = "addSystemToSystem";
    private static String REMOVE_VOLUME_ACTION = "removeVolumeFromSystem";

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
    public Job createSystem(final SystemCreate systemCreate)
            throws CloudProviderException {

        // this.checkQuota(userManager.getUserByUsername(this.user), system);

        ICloudProviderConnector connector = this.getCloudProviderConnector();
        if (connector == null) {
            throw new CloudProviderException("no connector found");
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
        this.em.persist(system);
        this.em.flush();

        // creation of main system job
        Job parentJob = this.createJob(CREATE_ACTION, system);
        this.em.persist(parentJob);
        this.em.flush();

        // implementation when System is not supported by underlying connector
        Set<ComponentDescriptor> componentDescriptors = systemCreate
                .getSystemTemplate().getComponentDescriptors();

        // iterating through descriptors
        Iterator<ComponentDescriptor> iter = componentDescriptors.iterator();
        while (iter.hasNext()) {
            ComponentDescriptor cd = iter.next();

            if (cd.getComponentType() == ComponentType.MACHINE) {
                // creating new machines
                for (int i = 0; i < cd.getComponentQuantity(); i++) {
                    MachineCreate mc = new MachineCreate();
                    if (cd.getComponentQuantity() > 1) {
                        mc.setName(cd.getComponentName()
                                + new Integer(i).toString());
                    }

                    MachineTemplate mt = machineManager
                            .getMachineTemplateById(cd.getComponentTemplate());
                    mc.setMachineTemplate(mt);
                    mc.setDescription(cd.getComponentDescription());
                    mc.setProperties(cd.getProperties());

                    Job j = machineManager.createMachine(mc);
                    j.setParentJob(parentJob);
                }

            }

            if (cd.getComponentType() == ComponentType.VOLUME) {
                // creating new volumes
                for (int i = 0; i < cd.getComponentQuantity(); i++) {
                    VolumeCreate vc = new VolumeCreate();
                    if (cd.getComponentQuantity() > 1) {
                        vc.setName(cd.getComponentName()
                                + new Integer(i).toString());
                    }
                    VolumeTemplate vt = volumeManager.getVolumeTemplateById(cd
                            .getComponentTemplate());
                    vc.setVolumeTemplate(vt);
                    vc.setDescription(cd.getComponentDescription());
                    vc.setProperties(cd.getProperties());

                    Job j = volumeManager.createVolume(vc);
                    j.setParentJob(parentJob);
                }
            }
            if (cd.getComponentType() == ComponentType.SYSTEM) {
                // creating new systems
                for (int i = 0; i < cd.getComponentQuantity(); i++) {
                    SystemCreate sc = new SystemCreate();
                    if (cd.getComponentQuantity() > 1) {
                        sc.setName(cd.getComponentName()
                                + new Integer(i).toString());
                    }
                    SystemTemplate st = this.getSystemTemplateById(cd
                            .getComponentTemplate());
                    sc.setSystemTemplate(st);
                    sc.setDescription(cd.getComponentDescription());
                    sc.setProperties(cd.getProperties());

                    Job j = this.createSystem(sc);
                    j.setParentJob(parentJob);
                }
            }
            if (cd.getComponentType() == ComponentType.NETWORK) {
                // creating new networks
                for (int i = 0; i < cd.getComponentQuantity(); i++) {
                    NetworkCreate nc = new NetworkCreate();
                    if (cd.getComponentQuantity() > 1) {
                        nc.setName(cd.getComponentName()
                                + new Integer(i).toString());
                    }
                    NetworkTemplate nt = networkManager
                            .getNetworkTemplateById(cd.getComponentTemplate());
                    nc.setNetworkTemplate(nt);
                    nc.setDescription(cd.getComponentDescription());
                    nc.setProperties(cd.getProperties());

                    Job j = networkManager.createNetwork(nc);
                    j.setParentJob(parentJob);
                }
            }
            if (cd.getComponentType() == ComponentType.CREDENTIALS) {
                // creating new credentials
                for (int i = 0; i < cd.getComponentQuantity(); i++) {
                    CredentialsCreate cc = new CredentialsCreate();
                    if (cd.getComponentQuantity() > 1) {
                        cc.setName(cd.getComponentName()
                                + new Integer(i).toString());
                    }
                    CredentialsTemplate ct = credentialsManager
                            .getCredentialsTemplateById(cd
                                    .getComponentTemplate());
                    cc.setCredentialTemplate(ct);
                    cc.setDescription(cd.getComponentDescription());
                    cc.setProperties(cd.getProperties());

                    // no job for credentials!
                    Credentials c = credentialsManager.createCredentials(cc);
                    system.getCredentials().add(c);

                }
            }

        }

        /*
         * // sending command to selected connector Job job = null; try { job =
         * connector.getSystemService().createSystem(systemCreate); } catch
         * (ConnectorException e) { throw new
         * CloudProviderException("system creation failed"); }
         * 
         * 
         * 
         * // persist this.em.persist(system); this.em.flush();// useless?
         * job.setTargetEntity(system);
         * 
         * // Ask for connector to notify when job completes try {
         * connector.setNotificationOnJobCompletion
         * (job.getProviderAssignedId()); } catch (Exception e) { throw new
         * ServiceUnavailableException(e.getMessage()); }
         * 
         * this.relConnector(system, connector);
         * 
         * return job;
         */

        return parentJob;

    }

    @Override
    public SystemTemplate createSystemTemplate(final SystemTemplate systemT)
            throws CloudProviderException {

        systemT.setUser(this.getUser());
        systemT.setCreated(new Date());

        this.em.persist(systemT);
        this.em.flush();
        return systemT;
    }

    @Override
    public System getSystemById(final String systemId)
            throws CloudProviderException {
        System result = this.em.find(System.class, new Integer(systemId));
        return result;
    }

    @Override
    public SystemTemplate getSystemTemplateById(final String systemTemplateId)
            throws CloudProviderException {
        SystemTemplate result = this.em.find(SystemTemplate.class, new Integer(
                systemTemplateId));
        return result;
    }

    private ComponentDescriptor getComponentDescriptorById(
            final String componentDescriptorId) throws CloudProviderException {
        ComponentDescriptor result = this.em.find(ComponentDescriptor.class,
                new Integer(componentDescriptorId));
        return result;
    }

    @Override
    public Job addVolumeToSystem(final Volume volume, final String systemId)
            throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<Volume> vols = s.getVolumes();
        vols.add(volume);

        // for system not supported by underlying connector
        Job job = this.createJob(ADD_VOLUME_ACTION, s);
        job.setStatus(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    @Override
    public Job removeVolumeFromSystem(final String volumeId,
            final String systemId) throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<Volume> vols = s.getVolumes();
        Volume v = this.volumeManager.getVolumeById(volumeId);

        for (Volume vol : vols) {
            if (vol.getId().equals(v.getId())) {
                vols.remove(vol);
            }
        }

        // for system not supported by underlying connector
        Job job = this.createJob(REMOVE_VOLUME_ACTION, s);
        job.setStatus(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    @Override
    public Job addSystemToSystem(final System system, final String systemId)
            throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<System> syss = s.getSystems();
        syss.add(system);

        // for system not supported by underlying connector
        Job job = this.createJob(ADD_SYSTEM_ACTION, s);
        job.setStatus(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    @Override
    public Job removeSystemFromSystem(final String systemToRemoveId,
            final String systemId) throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<System> syss = s.getSystems();
        System sRemove = this.getSystemById(systemToRemoveId);

        for (System sys : syss) {
            if (sys.getId().equals(sRemove.getId())) {
                syss.remove(sys);
            }
        }

        // for system not supported by underlying connector
        Job job = this.createJob(REMOVE_SYSTEM_ACTION, s);
        job.setStatus(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    @Override
    public Job addMachineToSystem(final Machine machine, final String systemId)
            throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<Machine> machines = s.getMachines();
        machines.add(machine);

        // for system not supported by underlying connector
        Job job = this.createJob(ADD_MACHINE_ACTION, s);
        job.setStatus(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    @Override
    public Job removeMachineFromSystem(final String machineId,
            final String systemId) throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<Machine> machines = s.getMachines();
        Machine m = this.machineManager.getMachineById(machineId);

        for (Machine mach : machines) {
            if (mach.getId().equals(m.getId())) {
                machines.remove(mach);
            }
        }

        // for system not supported by underlying connector
        Job job = this.createJob(REMOVE_MACHINE_ACTION, s);
        job.setStatus(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    @Override
    public Job addCredentialToSystem(final Credentials credential,
            final String systemId) throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<Credentials> credentials = s.getCredentials();
        credentials.add(credential);

        // for system not supported by underlying connector
        Job job = this.createJob(ADD_CREDENTIAL_ACTION, s);
        job.setStatus(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    @Override
    public Job removeCredentialFromSystem(final String credentialId,
            final String systemId) throws CloudProviderException {
        System s = this.getSystemById(systemId);
        List<Credentials> credentials = s.getCredentials();
        Credentials c = this.credentialsManager
                .getCredentialsById(credentialId);

        for (Credentials cred : credentials) {
            if (cred.getId().equals(c.getId())) {
                credentials.remove(cred);
            }
        }

        // for system not supported by underlying connector
        Job job = this.createJob(REMOVE_CREDENTIAL_ACTION, s);
        job.setStatus(Status.SUCCESS);// no call to connector
        this.em.persist(job);

        return job;
    }

    @Override
    public boolean addComponentDescriptorToSystemTemplate(
            final ComponentDescriptor componentDescriptor,
            final String systemTemplateId) throws CloudProviderException {
        SystemTemplate s = this.getSystemTemplateById(systemTemplateId);
        Set<ComponentDescriptor> descrs = s.getComponentDescriptors();

        descrs.add(componentDescriptor);

        s.setComponentDescriptors(descrs);

        this.em.persist(s);

        return true;
    }

    @Override
    public boolean removeComponentDescriptorFromSystemTemplate(
            final String componentDescriptorId, final String systemTemplateId)
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
    public System updateComponentDescriptor(final String id,
            final Map<String, Object> updatedAttributes)
            throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public System updateSystem(final String id,
            final Map<String, Object> updatedAttributes)
            throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SystemTemplate updateSystemTemplate(final String id,
            final Map<String, Object> updatedAttributes)
            throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job startSystem(final String systemId) throws CloudProviderException {
        // Job j = this.doService(systemId, "start");

        // implementation for system not supported by underlying connector
        System s = this.getSystemById(systemId);
        s.setState(State.STARTING);
        // creation of main system job
        Job parentJob = this.createJob(START_ACTION, s);
        this.em.persist(parentJob);
        this.em.flush();

        for (Machine m : s.getMachines()) {
            Job j = machineManager.startMachine(m.getId().toString());
            j.setParentJob(parentJob);
        }
        for (Volume v : s.getVolumes()) {
            // Job j=volumeManager..startVolume(v.getId().toString());
            // j.setParentJob(parentJob);
        }
        for (System sy : s.getSystems()) {
            Job j = this.startSystem(sy.getId().toString());
            j.setParentJob(parentJob);
        }
        for (Network n : s.getNetworks()) {
            Job j = networkManager.startNetwork(n.getId().toString());
            j.setParentJob(parentJob);
        }
        for (Credentials c : s.getCredentials()) {
            // Job j=volumeManager..startVolume(v.getId().toString());
            // j.setParentJob(parentJob);
        }

        return parentJob;
    }

    @Override
    public Job stopSystem(final String systemId) throws CloudProviderException {
        // Job j = this.doService(systemId, "stop");

        // implementation for system not supported by underlying connector
        System s = this.getSystemById(systemId);
        s.setState(State.STOPPING);
        // creation of main system job
        Job parentJob = this.createJob(STOP_ACTION, s);
        this.em.persist(parentJob);
        this.em.flush();

        for (Machine m : s.getMachines()) {
            Job j = machineManager.stopMachine(m.getId().toString());
            j.setParentJob(parentJob);
        }
        for (Volume v : s.getVolumes()) {
            // Job j=volumeManager..startVolume(v.getId().toString());
            // j.setParentJob(parentJob);
        }
        for (System sy : s.getSystems()) {
            Job j = this.stopSystem(sy.getId().toString());
            j.setParentJob(parentJob);
        }
        for (Network n : s.getNetworks()) {
            Job j = networkManager.stopNetwork(n.getId().toString());
            j.setParentJob(parentJob);
        }
        for (Credentials c : s.getCredentials()) {
            // Job j=volumeManager..startVolume(v.getId().toString());
            // j.setParentJob(parentJob);
        }

        return parentJob;
    }

    @Override
    public Job deleteSystem(final String systemId)
            throws CloudProviderException {
        // implementation for system not supported by underlying connector
        System s = this.getSystemById(systemId);
        s.setState(State.DELETING);
        // creation of main system job
        Job parentJob = this.createJob(DELETE_ACTION, s);
        this.em.persist(parentJob);
        this.em.flush();

        for (Machine m : s.getMachines()) {
            Job j = machineManager.deleteMachine(m.getId().toString());
            j.setParentJob(parentJob);
        }
        for (Volume v : s.getVolumes()) {
            Job j = volumeManager.deleteVolume(v.getId().toString());
            j.setParentJob(parentJob);
        }
        for (System sy : s.getSystems()) {
            Job j = this.deleteSystem(sy.getId().toString());
            j.setParentJob(parentJob);
        }
        for (Network n : s.getNetworks()) {
            Job j = networkManager.deleteNetwork(n.getId().toString());
            j.setParentJob(parentJob);
        }
        for (Credentials c : s.getCredentials()) {
            credentialsManager.deleteCredentials(c.getId().toString());
            // j.setParentJob(parentJob);
        }

        return parentJob;
    }

    // private methods

    private Job doService(final String systemId, final String action)
            throws CloudProviderException {

        System s = this.getSystemById(systemId);

        // implementation for system not supported by underlying connector

        /*
         * ICloudProviderConnector connector = this.getConnector(s); if
         * (connector == null) { throw new
         * CloudProviderException("no connector found"); } Job j;
         * 
         * try { if (action.equals("start")) { j =
         * connector.getSystemService().startSystem( s.getProviderAssignedId());
         * s.setState(System.State.STARTING); } else if (action.equals("stop"))
         * { j = connector.getSystemService().stopSystem(
         * s.getProviderAssignedId()); s.setState(System.State.STOPPING); } else
         * { throw new ServiceUnavailableException(
         * "Unsupported operation action " + action + " on system id " +
         * s.getProviderAssignedId() + " " + s.getId()); } } catch
         * (ConnectorException e) { throw new
         * ServiceUnavailableException(e.getMessage() + " action " + action +
         * " system id " + s.getProviderAssignedId() + " " + s.getId());
         * 
         * }
         * 
         * j.setTargetEntity(s); j.setUser(this.getUser());
         * 
         * this.em.persist(j); this.em.flush();
         * 
         * if (j.getStatus() == Job.Status.RUNNING) { try {
         * connector.setNotificationOnJobCompletion(j .getProviderAssignedId());
         * } catch (Exception e) { throw new
         * ServiceUnavailableException(e.getMessage() + "  system " + action); }
         * }
         * 
         * // Ask for connector to notify when job completes try {
         * connector.setNotificationOnJobCompletion(j.getProviderAssignedId());
         * } catch (Exception e) { throw new
         * ServiceUnavailableException(e.getMessage()); }
         * 
         * this.relConnector(s, connector);
         */

        return null;

    }

    private boolean checkQuota(final User u, final System sys) {
        /**
         * TODO Check current quota
         */
        return true;
    }

    private void relConnector(final System ce,
            final ICloudProviderConnector connector)
            throws CloudProviderException {
        String cpType = ce.getCloudProviderAccount().getCloudProvider()
                .getCloudProviderType();
        ICloudProviderConnectorFactory cFactory = null;
        try {
            cFactory = this.cloudProviderConnectorFactoryFinder
                    .getCloudProviderConnectorFactory(cpType);
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
            final CloudProvider provider) {
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

        CloudProvider cloudProvider = this.selectCloudProvider();
        if (cloudProvider == null) {
            throw new CloudProviderException("no provider found");
        }
        CloudProviderAccount cloudProviderAccount = this
                .selectCloudProviderAccount(cloudProvider);
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
            final CloudProviderAccount cloudProviderAccount)
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

    private Job createJob(final String action, final CloudResource targetEntity)
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
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean jobCompletionHandler(final String notification_id) {

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
            job=jobManager.getJobById(job.getId().toString());
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
                    SystemManager.logger.info(" SystemHandler updating successful job " + job.getId()+" for main job "+job.getId().toString());
                    System s = (System) job.getTargetEntity();

                    if (j.getTargetEntity() instanceof Machine) {

                        if (job.getAction().equals(SystemManager.CREATE_ACTION)) {
                            s.getMachines().add((Machine) j.getTargetEntity());
                        }

                        job.getProperties().put(j.getId().toString(),
                                HANDLED_JOB);
                    }
                    if (j.getTargetEntity() instanceof Volume) {
                        
                        if (job.getAction().equals(SystemManager.CREATE_ACTION)) {
                            s.getVolumes().add((Volume) j.getTargetEntity());
                        }
                        
                        job.getProperties().put(j.getId().toString(),
                                HANDLED_JOB);
                    }
                    if (j.getTargetEntity() instanceof System) {
                        
                        if (job.getAction().equals(SystemManager.CREATE_ACTION)) {
                            s.getSystems().add((System) j.getTargetEntity());
                        }
                        
                        job.getProperties().put(j.getId().toString(),
                                HANDLED_JOB);
                    }
                    if (j.getTargetEntity() instanceof Network) {
                        
                        if (job.getAction().equals(SystemManager.CREATE_ACTION)) {
                            s.getNetworks().add((Network) j.getTargetEntity());
                        }
                        
                        job.getProperties().put(j.getId().toString(),
                                HANDLED_JOB);
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
            SystemManager.logger.info(" SystemHandler one or more jobs are failed "+job.getId().toString());
            return true;
        }
        if (cancelled) {
            // one or more jobs are cancelled, so all is cancelled
            job.setStatus(Status.CANCELLED);
            System s = (System) job.getTargetEntity();
            s.setState(State.ERROR);
            SystemManager.logger.info(" SystemHandler one or more jobs are cancelled "+job.getId().toString());
            return true;
        }
        if (running) {
            // one or more jobs are running, so all is running
            job.setStatus(Status.RUNNING);
            SystemManager.logger.info(" SystemHandler one or more jobs are running "+job.getId().toString());
            return true;
        }

        // job success
        job.setStatus(Status.SUCCESS);
        System s = (System) job.getTargetEntity();
        SystemManager.logger.info(" SystemHandler all jobs are successful "+job.getId().toString());
        
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
            this.em.remove(s);
        }


        // Find the system by providerAssignedId (or the job as well)
        /*
         * String jid = job.getProviderAssignedId().toString(); //
         * providerAssignedSystemId String pasid =
         * job.getTargetEntity().getId().toString(); Job jpersisted = null;
         * 
         * try { jpersisted = (Job) this.em
         * .createQuery("FROM Job j WHERE j.providerAssignedId=:jid")
         * .setParameter("jid", jid).getSingleResult(); } catch
         * (NoResultException e) { // ignore for now
         * SystemManager.logger.info("Cannot find job for system" + pasid);
         * return false; } catch (NonUniqueResultException e) {
         * SystemManager.logger.info("No single job for system !!" + pasid);
         * return false; } catch (Exception e) { SystemManager.logger
         * .info("Internal error in finding job for system" + pasid); return
         * false; } System sPersisted = null;
         * 
         * try { if (jpersisted == null) { //** // * find the system from its
         * providerAssignedId in fact there // * could be more than one machine
         * with same same // * providerAssignedId? // * sPersisted = (System)
         * this.em .createQuery(
         * "FROM System s WHERE s.providerAssignedId=:pamid")
         * .setParameter("pamid", pasid).getSingleResult();
         * 
         * } else { // find the machine from its id Integer mid =
         * Integer.valueOf(jpersisted.getTargetEntity() .getId().toString());
         * sPersisted = this.em.find(System.class, mid); }
         * 
         * } catch (NoResultException e) {
         * SystemManager.logger.info("Could not find the system or job for " +
         * pasid); return false; } catch (NonUniqueResultException e) {
         * SystemManager.logger.info("Multiple system found for " + pasid);
         * return false; } catch (Exception e) { SystemManager.logger
         * .info("Unknown error : Could not find the system or job for " +
         * pasid); return false; }
         * 
         * // update the system by invoking the connector CloudProviderAccount
         * cpa = sPersisted.getCloudProviderAccount(); ICloudProviderConnector
         * connector; try { connector = this.getCloudProviderConnector(cpa); }
         * catch (CloudProviderException e) { // no point to return false?
         * SystemManager.logger.info("Could not get cloud connector " +
         * e.getMessage()); return false; }
         * 
         * // TODO: update system in the database...
         * 
         * this.em.flush();
         */
        return true;
    }

}
