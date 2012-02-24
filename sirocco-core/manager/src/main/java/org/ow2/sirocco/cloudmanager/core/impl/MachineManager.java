/**
 *
 * SIROCCO
 * Copyright (C) 2012 France Telecom
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
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

import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactory;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactoryFinder;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.exception.ServiceUnavailableException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.User;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;

@Stateless(name = IMachineManager.EJB_JNDI_NAME, mappedName = IMachineManager.EJB_JNDI_NAME)
@Remote(IRemoteMachineManager.class)
@Local(IMachineManager.class)
public class MachineManager implements IMachineManager, IRemoteMachineManager {

    static final String EJB_JNDI_NAME = "MachineManager";

    private static Logger logger = Logger.getLogger(MachineManager.class.getName());

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @EJB
    private IUserManager userManager;

    @OSGiResource
    private ICloudProviderConnectorFactoryFinder cloudProviderConnectorFactoryFinder;

    @Resource
    private SessionContext ctx;

    private User user;

    @Resource
    public void setSessionContext(final SessionContext ctx) {
        this.ctx = ctx;
    }

    private void setUser() throws CloudProviderException {
        String username = this.ctx.getCallerPrincipal().getName();
        this.user = this.userManager.getUserByUsername(username);
    }


    private List<CloudProvider> selectCloudProviders(final MachineTemplate mt) {
    	// TODO selection cloud provider
    	
    	List<CloudProvider> l = new ArrayList<CloudProvider>();
    	
    	Query q = this.em.createQuery("FROM CloudProvider c WHERE c.cloudProviderType=:type");
    	q.setParameter("type", "mock");
    	
    	q.setMaxResults(1);
    	List<CloudProvider> cp = (List<CloudProvider>)q.getResultList();
    	if (cp.size() == 0) {
    		return l;
    	}
    	List<CloudProviderLocation> cpll = cp.get(0).getCloudProviderLocations();
    	if (cpll.size() == 0) {
    		return l;
    	}
    	
    	l.add(cp.get(0));
    	return l;
    }

    /**
     * Operations on MachineCollection
     */
    private CloudProviderAccount selectCloudProviderAccount(final CloudProvider provider, final User u,
        final MachineTemplate template) {
        List<CloudProviderAccount> accounts = provider.getCloudProviderAccounts();
        CloudProviderAccount a = null;

        /**
         * TODO Choose a provider account who can access the image
         */
        if (accounts.isEmpty() == false) {
            a = accounts.get(0);
        }
        return a;
    }

    private boolean checkQuota(final User u, final MachineConfiguration mc) {
        /**
         * TODO Check current quota
         */
        return true;
    }

    private ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount account,
        final CloudProviderLocation location) throws CloudProviderException {

        ICloudProviderConnectorFactory connectorFactory = this.cloudProviderConnectorFactoryFinder
            .getCloudProviderConnectorFactory(account.getCloudProvider().getCloudProviderType());
        return connectorFactory.getCloudProviderConnector(account, location);
    }

    /**
     * User could have passed by value or by reference. Validation is expected
     * to be done by REST layer
     */
    private void checkVolumes(MachineTemplate mt, User u) throws InvalidRequestException {
    	List<MachineVolume> volumes = mt.getVolumes();
    	
    	if (volumes == null) {
    		throw new InvalidRequestException("Volumes array null");
    	}
    	if (volumes.size() == 0) {
    		return;
    	}
    	
    	for (MachineVolume v : volumes) {
            Volume volume = v.getVolume();
            /**
             * Volume should not be passed by value. Check that the volume id
             * exists.
             */
            if (volume.getId() == null) {
            	throw new InvalidRequestException("No volume id ");
            }
            Volume vvv = this.em.find(Volume.class, volume.getId());
            if (vvv == null) {
                throw new InvalidRequestException("Volume " + volume.getId() + " of name " + volume.getName()
                    + " does not exist ");
            }
        }
    	
    	List<MachineVolumeTemplate> volumeTemplates = mt.getVolumeTemplates();
    	if (volumeTemplates == null) {
    		throw new InvalidRequestException("VolumeTemplates array null");
    	}
    	if (volumeTemplates.size() == 0) {
    		return;
    	}
    	// TODO CHECK VOLUME TEMPLATES
    }
    
    // TODO
    private void validateMachineImage(final MachineTemplate mt, final User u) throws InvalidRequestException {
    	MachineImage mi = mt.getMachineImage();
    	if ((mi == null) || (mi.getId() == null)){
    		throw new InvalidRequestException(" MachineImage should be set");
    	}
    	// check that machine image is known
    	// use MachineImageManager
    	/**
         * POLICY ABOUT IMAGE AND CLOUD PROVIDER!
         */
    	MachineImage mimage = this.em.find(MachineImage.class, mi.getId());
    	if (mimage == null) {
    		throw new InvalidRequestException("Unknown machine image in request ");
    	}
    }
    
    private void validateCreationParameters(final MachineTemplate mt, final User u) throws CloudProviderException {
        // TODO check all references
    	this.checkVolumes(mt, u);
        this.validateMachineConfiguration(mt.getMachineConfiguration());
        this.validateMachineImage(mt, u);

        this.validateCredentials(mt.getCredentials());
        this.validateNetworkInterface(mt.getNetworkInterfaces());

    }

    public Job createMachine(final MachineCreate machineCreate) throws CloudProviderException {

        this.setUser();

        MachineTemplate mt = machineCreate.getMachineTemplate();

        this.validateCreationParameters(mt, this.user);

        /**
         * TODO Check quota
         */
        if (this.checkQuota(this.user, mt.getMachineConfiguration()) == false) {
            throw new CloudProviderException("User exceeded quota ");
        }

        /**
         * Obtain list of matching provider
         */
        List<CloudProvider> providers = this.selectCloudProviders(mt);
        if (providers.size() == 0) {
            throw new ServiceUnavailableException("Could not find a suitable cloud provider  ");
        }
        CloudProviderAccount account = null;
        CloudProvider myprovider = null;
        for (CloudProvider cp : providers) {
            /**
             * Select provider account to use
             */
            account = this.selectCloudProviderAccount(cp, this.user, mt);
            if (account != null) {
                myprovider = cp;
                break;
            }
        }

        if (account == null) {
            throw new CloudProviderException("Could not find a cloud provider account ");
        }
        /** there must be at least one location if we are here */
        CloudProviderLocation mylocation = myprovider.getCloudProviderLocations().get(0);
        ICloudProviderConnector connector = this.getCloudProviderConnector(account, mylocation);
        if (connector == null) {
            throw new CloudProviderException("Could not obtain connector to provider "
                + account.getCloudProvider().getCloudProviderType());
        }
        String connectorid = connector.getCloudProviderId();

        Job creationJob = null;
        IComputeService computeService = null;

        /**
         * Convention: The entity Ids refer to sirocco given ids. The provider
         * id is stored in providerAssignedId. The connector layer will use
         * providerAssignedId in its communication with the provider.
         */
        try {
            computeService = connector.getComputeService();
            creationJob = computeService.createMachine(machineCreate);
        } catch (Exception e) {
            MachineManager.logger.info("Failed to create machine ");
            throw new CloudProviderException(e.getMessage());
        }

        // TODO Should the creation request be logged?
        if (creationJob.getStatus() == Job.Status.FAILED) {
            throw new ServiceUnavailableException("Machine creation failed ");
        }

        Machine m = new Machine();

        m.setName(machineCreate.getName());
        m.setDescription(machineCreate.getDescription());
        m.setProperties(machineCreate.getProperties());

        m.setState(Machine.State.CREATING);
        m.setUser(this.user);
        m.setCpu(mt.getMachineConfiguration().getCpu());
        m.setMemory(mt.getMachineConfiguration().getMemory());
        
        m.setCloudProviderAccount(account);
        m.setProviderAssignedId(creationJob.getTargetEntity());
        /** set cloud provider location */
        m.setLocation(mylocation);
        
        m.setCreated(new Date());
        
        /**
         * TODO: move the following to machineCreationCompletionEvent
         */
        List<Disk> disks = new ArrayList<Disk>();
        List<DiskTemplate> dts = mt.getMachineConfiguration().getDiskTemplates();

        for (DiskTemplate dt : dts) {
            DiskTemplate d = new DiskTemplate();
            d.setQuantity(dt.getQuantity());
            d.setUnit(dt.getUnit());
        }
        m.setVolumes(mt.getVolumes());
        m.setDisks(disks);
        
        List<MachineVolume> volumes = mt.getVolumes();
        m.setVolumes(volumes);

        /**
         * Persist machine and job according to status of Job returned by
         * connector
         */

        Job j = new Job();
        // Should use JobManager to create job
        j.setName(creationJob.getName());
        j.setDescription("Machine creation ");
        j.setProperties(new HashMap<String, String>());
        
        j.setCreated(new Date());
        j.setProviderAssignedId(creationJob.getId().toString());
        j.setAction("create");
        j.setStatus(creationJob.getStatus());
        j.setReturnCode(creationJob.getReturnCode());
        j.setIsCancellable(false);
        j.setParentJob(null);
        j.setNestedJobs(new ArrayList<Job>());
        j.setUser(user);
        
        // TODO Cancelled status
        
        if (creationJob.getStatus() == Job.Status.SUCCESS) {
        	try {
        		m.setState(computeService.getMachineState(creationJob.getTargetEntity()));
        	} catch (ConnectorException ce) {
        		throw new ServiceUnavailableException(ce.getMessage());
        	}
        	this.em.persist(m);
        	j.setTargetEntity(m.getId().toString());
        	this.em.persist(j);
        	this.em.flush();

        } else {
        	/** Job is RUNNING. Will be notified when creation completes */

        	this.em.persist(m);
        	j.setTargetEntity(m.getId().toString());
        	this.em.persist(j);
        	this.em.flush();

        	// Ask for connector to notify when job completes
        	try {
        		connector.setNotificationOnJobCompletion(creationJob.getProviderAssignedId());
        	} catch (Exception e) {
        		throw new ServiceUnavailableException(e.getMessage());
        	}
        }

        return j;
    }

    public List<Machine> getMachines(final int first, final int last, final List<String> attributes)
        throws CloudProviderException {

        this.setUser();
        if ((first < 0) || (last < 0) || (last < first)) {
        	throw new InvalidRequestException(" Illegal array index " +first +" " +last);
        }
        
        Query query = this.em
            .createNamedQuery("FROM Machine v WHERE v.user.username=:userName AND v.state<>'DELETED' ORDER BY v.id");
        query.setParameter("userName", this.user.getUsername());
        query.setMaxResults(last - first + 1);
        query.setFirstResult(first);
        List<Machine> machines = query.setFirstResult(first).setMaxResults(last - first + 1).getResultList();
        for (Machine machine : machines) {

            if (attributes.contains("volumes")) {
                machine.getVolumes().size();
            }
            if (attributes.contains("disks")) {
                machine.getDisks().size();
            }

            if (attributes.contains("networkInterfaces")) {
                machine.getNetworkInterfaces().size();
            }
            // TODO 
            machine.initFSM();
        }
        return machines;
    }

    // TODO
    public List<Machine> getMachinesAttributes(final List<String> attributes, final String queryExpression)
        throws CloudProviderException {
        List<Machine> machines = new ArrayList<Machine>();

        return machines;
    }

    /**
     * Operations on Machine
     */
    private Machine checkOps(final String machineId, final String action) throws CloudProviderException {
        Machine m = null;
        if (machineId == null) {
        	throw new InvalidRequestException("Null machine id ");
        }
        try {
            m = this.em.find(Machine.class, Integer.valueOf(machineId));
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }

        m.initFSM();
        Set<String> actions = m.getOperations();
        if (actions.contains(action) == false) {
            throw new InvalidRequestException(" Cannot " + action + "  machine at state " + m.getState());
        }
        return m;
    }

    private void relConnector(final Machine m, final ICloudProviderConnector connector) throws CloudProviderException {
        String cpType = m.getCloudProviderAccount().getCloudProvider().getCloudProviderType();
        ICloudProviderConnectorFactory cFactory = null;
        try {
            cFactory = this.cloudProviderConnectorFactoryFinder.getCloudProviderConnectorFactory(cpType);
            String connectorId = connector.getCloudProviderId();
            cFactory.disposeCloudProviderConnector(connectorId);
        } catch (ConnectorException e) {
            throw new CloudProviderException(e.getMessage());
        }
    }

    private ICloudProviderConnector getConnector(final Machine m) throws CloudProviderException {

        ICloudProviderConnector connector = null;
        
        connector = this.getCloudProviderConnector(m.getCloudProviderAccount(), m.getLocation());
        return connector;
    }

    // TODO with JobManager
    
    private Job initJobToPersist(final Job j, final Machine m, final String action) {

        Job persistedJob = new Job();
        persistedJob.setStatus(j.getStatus());
        persistedJob.setAction(action);
        
        persistedJob.setParentJob(null);
        // TODO machine create may be dispatched as a set of jobs
        persistedJob.setNestedJobs(null);
        persistedJob.setStatusMessage(j.getStatusMessage());
        persistedJob.setTargetEntity(m.getId().toString());
        persistedJob.setProviderAssignedId(j.getId().toString());
        persistedJob.setCreated(new Date());
        persistedJob.setLocation(m.getLocation());
        persistedJob.setUser(m.getUser());
        persistedJob.setReturnCode(j.getReturnCode());
        return persistedJob;
    }

    private Job doService(final String machineId, final String action) throws CloudProviderException {

        Job j;
        Machine m = this.checkOps(machineId, action);
        ICloudProviderConnector connector = this.getConnector(m);
        IComputeService computeService;
        try {
            computeService = connector.getComputeService();
        } catch (ConnectorException e) {
            throw new ServiceUnavailableException(e.getMessage());
        }

        try {
            if (action.equals("start")) {
                j = computeService.startMachine(m.getProviderAssignedId());
                m.setState(Machine.State.STARTING);
            } else if (action.equals("stop")) {
                j = computeService.stopMachine(m.getProviderAssignedId());
                m.setState(Machine.State.STOPPING);
            } else {
            	// TODO capabilities
                throw new ServiceUnavailableException("Unsupported operation ");
            }
        } catch (ConnectorException e) {
            throw new ServiceUnavailableException(e.getMessage());
        }

        if ((j.getStatus() == Job.Status.FAILED) || (j.getStatus() == Job.Status.CANCELLED)
            || (j.getStatus() == Job.Status.SUCCESS)) {
            /**
             * what to do ? should we immediately obtain the machine status and
             * update it without creating a job entity?
             */
            Machine.State s = m.getState();
            try {
                s = computeService.getMachineState(m.getProviderAssignedId());
            } catch (ConnectorException e) {
                /** what to do ? */
            }
            m.setState(s);
        }
        Job persistedJob = this.initJobToPersist(j, m, action);
        this.em.persist(persistedJob);
        this.em.flush();
        /** Ask connector for notification */
        
        if (j.getStatus() == Job.Status.RUNNING) {
        	try {
        		connector.setNotificationOnJobCompletion(persistedJob.getProviderAssignedId());
        	} catch (Exception e) {
        		throw new ServiceUnavailableException(e.getMessage());
        	}
        }
        /** Tell connector that we are done with it */
        this.relConnector(m, connector);
        return persistedJob;
    }

    public Job startMachine(final String machineId) throws CloudProviderException {

        Job persistedJob = this.doService(machineId, "start");
        return persistedJob;
    }

    public Job stopMachine(final String machineId) throws CloudProviderException {

        return this.doService(machineId, "start");
    }

    private void deleteMachineFromDb(Machine m) {
    	MachineManager.logger.info("Removing deleted machine ");
    	
    	m.setCloudProviderAccount(null);
    	m.setUser(null);
    	m.setVolumes(null);
    	this.em.remove(m);
    }
    
    // TODO 
    // Delete may be done in any state of the machine
    public Job deleteMachine(final String machineId) throws CloudProviderException {
        Job j = null;
        if (machineId == null) {
        	throw new InvalidRequestException(" Null machine id");
        }
        Machine m = this.em.find(Machine.class, Integer.valueOf(machineId));
        if (m == null) {
        	throw new ResourceNotFoundException(" Invalid machine id " +machineId);
        }
        
        ICloudProviderConnector connector = this.getConnector(m);
        IComputeService computeService;
 
        try {
            computeService = connector.getComputeService();
        } catch (ConnectorException e) {
            throw new ServiceUnavailableException(e.getMessage());
        }
        try {
        	j = computeService.deleteMachine(m.getProviderAssignedId());
        } catch (ConnectorException e) {
            throw new ServiceUnavailableException(e.getMessage());
        }
        if ((j.getStatus() == Job.Status.FAILED) || (j.getStatus() == Job.Status.CANCELLED)
                || (j.getStatus() == Job.Status.SUCCESS)) {
                /**
                 * what to do ? should we immediately obtain the machine status and
                 * update it without creating a job entity?
                 */
                Machine.State s = m.getState();
                try {
                    s = computeService.getMachineState(m.getProviderAssignedId());
                } catch (ConnectorException e) {
                    /** what to do ? */
                }
                m.setState(s);
        }
        
        Job persistedJob = this.initJobToPersist(j, m, "delete");
        this.em.persist(persistedJob);
        this.em.flush();
        /** Ask connector for notification when job completes*/
        boolean deletedone = false;
        if (j.getStatus() == Job.Status.RUNNING) {
        	try {
        		connector.setNotificationOnJobCompletion(persistedJob.getProviderAssignedId());
        	} catch (Exception e) {
        		throw new ServiceUnavailableException(e.getMessage());
        	}
        } else if (j.getStatus() == Job.Status.SUCCESS) {
        	/** machine is deleted */
        	deletedone = true;
        }
        /** Tell connector that we are done with it */
        this.relConnector(m, connector);
        if (deletedone == true) {
        	
        	// deleteMachineFromDb(m);
        }
        this.em.persist(persistedJob);
        this.em.flush();
        return persistedJob;
    }

    public Machine getMachineById(final String machineId) throws ResourceNotFoundException, CloudProviderException {
        if (machineId == null) {
        	throw new InvalidRequestException(" null machine id");
        }
    	Machine m = this.em.find(Machine.class, Integer.valueOf(machineId));
        if (m == null) {
        	throw new ResourceNotFoundException(" Invalid machine id " +machineId);
        }
        m.getVolumes().size();
        m.getNetworkInterfaces().size();
        m.getDisks().size();
        return m;
    }

    public Machine getMachineAttributes(final String machineId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        Machine m = null;
        if (machineId == null) {
        	throw new InvalidRequestException(" null machine id");
        }
        try {
            m = this.em.find(Machine.class, Integer.valueOf(machineId));
        } catch (Exception e) {
        	throw new CloudProviderException(" Internal failure ");
        }
        if (m == null) {
            throw new ResourceNotFoundException("Machine " + machineId + " cannot be found");
        }
        if (attributes.contains("volumes")) {
            m.getVolumes().size();
        }
        if (attributes.contains("disks")) {
            m.getDisks().size();
        }
        if (attributes.contains("networkInterfaces")) {
            m.getNetworkInterfaces().size();
        }
        return m;
    }

    /**
     * for each update operation change the local state of machine only after
     * having received the new state from server.
     */
    /** TEMP: filter out updates not accepted */
    private Map<String, Object> filterUpdates(final Map<String, Object> requested) {
        Map<String, Object> s = new HashMap<String, Object>();
        if (requested.containsKey("cpu")) {
            s.put("cpu", requested.get("cpu"));
        }
        if (requested.containsKey("memory")) {
            s.put("memory", requested.get("memory"));
        }
        if (requested.containsKey("properties")) {
            s.put("properties", requested.get("properties"));
        }
        return s;
    }
    // TODO
    public Job updateMachine(final String machineId, final Map<String, Object> attributes) throws ResourceNotFoundException,
        CloudProviderException {

        Job j = null;
        if (machineId == null) {
        	throw new InvalidRequestException(" null machine id");
        }
        Machine m = this.em.find(Machine.class, Integer.valueOf(machineId));
        if (m == null) {
            throw new ResourceNotFoundException("Machine " + machineId + " cannot be found");
        }
        // REMOVE
        if (attributes.size() > 0) {
        	throw new ServiceUnavailableException(" Come back later for update ");
        }
        if (attributes.size() > 1) {
            throw new InvalidRequestException("May update only one by one ");
        }
        Map<String, Object> allowedUpdates = this.filterUpdates(attributes);

        /** invoke connector */
        ICloudProviderConnector connector = this.getConnector(m);
        IComputeService computeService;
        try {
            computeService = connector.getComputeService();
        } catch (ConnectorException e) {
            throw new ServiceUnavailableException(e.getMessage());
        }

        // j = computeService.updateMachine(m, allowedUpdates);

        // this is getting quite complicated :(
        return j;
    }

    /**
     * Operations on MachineCollection
     */
    public MachineCollection getMachineCollection() throws CloudProviderException {

        this.setUser();
        Integer userid = this.user.getId();
        Query query = this.em.createQuery("FROM Machine m WHERE m.user.id=:userid").setParameter("userid", userid);
        List<Machine> machines = (List<Machine>) query.getResultList();
        MachineCollection collection = (MachineCollection) this.em
            .createQuery("FROM MachineCollection m WHERE m.user.id=:userid").setParameter("userid", userid).getSingleResult();
        collection.setMachines(machines);
        return collection;
    }

    /** TODO */
    /** only name, description and properties */
    public void updateMachineCollection(final Map<String, Object> attributes) throws CloudProviderException {

    }

    /**
     * Operations on MachineConfiguration
     */
    public MachineConfiguration getMachineConfiguration(final String mcId) throws CloudProviderException {
    	if (mcId == null) {
        	throw new InvalidRequestException(" null machine configuration id");
        }
    	MachineConfiguration mc = this.em.find(MachineConfiguration.class, Integer.valueOf(mcId));
        if (mc == null) {
        	throw new ResourceNotFoundException("Unknown machine configuration " +mcId);
        }
        mc.getDiskTemplates().size();
        return mc;
    }

    public void updateMachineConfiguration(final String mcId, final Map<String, Object> attributes)
        throws CloudProviderException {
    	if ((mcId == null) || (attributes == null)) {
        	throw new InvalidRequestException(" null machine configuration id");
        }
        MachineConfiguration mc = this.em.find(MachineConfiguration.class, Integer.valueOf(mcId));
        if (mc == null) {
        	throw new ResourceNotFoundException("Unknown machine configuration " +mcId);
        }
        if (attributes.containsKey("cpu")) {
            Cpu cpu = (Cpu) attributes.get("cpu");
            mc.setCpu(cpu);
        }
        if (attributes.containsKey("memory")) {
            Memory mem = (Memory) attributes.get("memory");
            mc.setMemory(mem);
        }

        if (attributes.containsKey("diskTemplates")) {
            List<DiskTemplate> dts = (List<DiskTemplate>) attributes.get("diskTemplates");
            mc.setDiskTemplates(dts);
        }

        this.em.flush();
    }

    public void deleteMachineConfiguration(final String mcId) throws CloudProviderException {

        List<MachineTemplate> mts = null;
        Integer mcid = Integer.valueOf(mcId);
        MachineConfiguration config = null;
        if (mcId == null) {
        	throw new InvalidRequestException(" null machine configuration id");
        }
        try {
            config = this.em.find(MachineConfiguration.class, mcid);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
		if (config == null) {
			throw new ResourceNotFoundException(
					"Unknown machine configuration " + mcid);
		}
        try {
            /**
             * Do not allow delete if any machine template refers to this
             * configuration
             */
            mts = (List<MachineTemplate>) this.em.createQuery("FROM MachineTemplate m WHERE m.machineConfiguration.id=:mcid")
                .setParameter("mcid", mcid).getResultList();
        } catch (Exception e) {
            return;
        }
        if ((mts != null) && (mts.size() > 0)) {
            throw new ResourceConflictException("MachineTemplates " + mts.get(0).getId() + " uses the configuration " + mcid);
        }
        // CHECK that disktemplates are cascaded deleted
        config.setUser(null);
        // 
        this.em.remove(config);
    }

    /**
     * Operations on MachineConfigurationCollection
     */
    public MachineConfigurationCollection getMachineConfigurationCollection() throws CloudProviderException {
        this.setUser();
        Integer userid = this.user.getId();
        Query query = this.em.createQuery("FROM MachineConfiguration c WHERE c.user.id=:userid");
        List<MachineConfiguration> configs = query.setParameter("userid", userid).getResultList();
        // There should be only one collection
        MachineConfigurationCollection collection = (MachineConfigurationCollection) this.em
            .createQuery("FROM MachineConfigurationCollection m WHERE m.user.id=:userid").setParameter("userid", userid)
            .getSingleResult();
        collection.setMachineConfigurations(configs);
        return collection;
    }

    public MachineConfiguration createMachineConfiguration(final MachineConfiguration machineConfig)
        throws CloudProviderException {

        this.setUser();
        Integer userid = this.user.getId();
        this.validateMachineConfiguration(machineConfig);
        boolean exists = true;
        try {
            MachineConfiguration mc = (MachineConfiguration) this.em
                .createQuery("FROM MachineConfiguration m WHERE m.user.id=:userid AND m.name=:name")
                .setParameter("userid", userid).setParameter("name", machineConfig.getName()).getSingleResult();
        } catch (NoResultException e) {
            exists = false;
        }
        if (exists == true) {
            throw new CloudProviderException("MachineConfiguration by name already exists " + machineConfig.getName());
        }
        machineConfig.setUser(this.user);
        machineConfig.setCreated(new Date());
        this.em.persist(machineConfig);
        this.em.flush();
        return machineConfig;
    }

    /**
     * Operations on MachineTemplate
     */
    public MachineTemplate getMachineTemplate(final String mtId) throws CloudProviderException {
    	if (mtId == null) {
        	throw new InvalidRequestException(" null machine template id");
        }
    	MachineTemplate mt = this.em.find(MachineTemplate.class, Integer.valueOf(mtId));
        if (mt == null) {
        	throw new ResourceNotFoundException(" Could not find machine template" +mtId);
        }
        mt.getVolumes().size();
        mt.getVolumeTemplates().size();
        mt.getNetworkInterfaces().size();

        return mt;
    }

    public MachineTemplate updateMachineTemplate(final String mtId, final Map<String, Object> attributes)
        throws CloudProviderException {
        MachineTemplate mt = null;
        if ((mtId == null) || (attributes == null)) {
        	throw new InvalidRequestException(" null machine template id");
        }
        mt = this.em.find(MachineTemplate.class, Integer.valueOf(mtId));
        if (mt == null) {
        	throw new ResourceNotFoundException(" Could not find machine template" +mtId);
        }
        try {
            
            if (attributes.containsKey("name")) {
                mt.setName((String) attributes.get("name"));
            }

            if (attributes.containsKey("description")) {
                mt.setDescription((String) attributes.get("description"));
            }

            if (attributes.containsKey("properties")) {
                mt.setProperties((Map<String, String>) attributes.get("properties"));
            }
            // Cannot change attributes of original machineConfig
            // only reference is changed
            if (attributes.containsKey("machineConfiguration")) {

                String mc = (String) attributes.get("machineConfiguration");

                MachineConfiguration config = this.em.find(MachineConfiguration.class, Integer.valueOf(mc));
                if (config == null) {
                	throw new InvalidRequestException(" Could not find machine configuration" +mc);
                }
                mt.setMachineConfiguration(config);
            }
            if (attributes.containsKey("machineImage")) {
                String mi = (String) attributes.get("machineImage");

                MachineImage image = this.em.find(MachineImage.class, Integer.valueOf(mi));
                if (image == null) {
                	throw new InvalidRequestException(" Could not find machine image" +mi);
                }
                mt.setMachineImage(image);
            }
            if (attributes.containsKey("credentials")) {
                String credentials = (String) attributes.get("credentials");

                Credentials cred = this.em.find(Credentials.class, Integer.valueOf(credentials));
                if (cred == null) {
                	throw new InvalidRequestException(" Could not find credentials" +credentials);
                }
                mt.setCredentials(cred);
            }
            if (attributes.containsKey("volumes")) {
                List<MachineVolume> volumes = (List<MachineVolume>) attributes.get("volumes");
                if (volumes == null) {
                	// should this be allowed?
                	throw new InvalidRequestException(" setting null volume ");
                } 
				/** check that the volume exists */
				for (MachineVolume volume : volumes) {
					if (volume.getVolume() == null) {
						throw new InvalidRequestException(" setting null volume ");
					}
					if (volume.getAttachmentPoint() == null) {
						throw new InvalidRequestException(" setting empty attachment point ");
					}
					try {
						this.em.getReference(Volume.class, volume.getVolume());
					} catch (Exception e) {
						throw new InvalidRequestException(e.getMessage());
					}
				}
                // CHECK replace originally referenced volumes
                mt.setVolumes(volumes);
            }
            if (attributes.containsKey("volumeTemplates")) {
                List<MachineVolumeTemplate> vts = (List<MachineVolumeTemplate>) attributes.get("volumes");
                if (vts == null) {
                	// should this be allowed?
                	throw new InvalidRequestException(" setting null volume templates ");
                } 
                /** check that each volume exists */
                for (MachineVolumeTemplate vt : vts) {
                    try {
                        this.em.getReference(VolumeTemplate.class, /*
                                                                    * vt.
                                                                    * getVolumeTemplate
                                                                    * ()
                                                                    */1);
                    } catch (Exception e) {
                        throw new CloudProviderException(e.getMessage());
                    }
                }
                mt.setVolumeTemplates(vts);
            }

            if (attributes.containsKey("networkInterfaces")) {
                List<NetworkInterface> list = (List<NetworkInterface>) (attributes.get("networkInterfaces"));
               
                /** validate(list); */
                mt.setNetworkInterfaces(list);
            }
            mt.setUpdated(new Date());
            this.em.merge(mt);
            this.em.flush();
        } catch (Exception e) {
            throw new CloudProviderException(e.getMessage());
        }
        return mt;
    }

    private void deleteMachineTemplateFromDb(MachineTemplate mt) {
    	 mt.setUser(null);
         mt.setVolumes(null);
         mt.setVolumeTemplates(null);
         this.em.remove(mt);
    }
    
    public void deleteMachineTemplate(final String mtId) throws CloudProviderException {
        this.setUser();

        MachineTemplate mt = this.em.find(MachineTemplate.class, Integer.valueOf(mtId));
        if (mt == null) {
        	throw new ResourceNotFoundException("Cannot find machine template " +mtId);
        }
        if (mt.getUser().equals(this.user) == false) {
            throw new CloudProviderException("Not owner, cannot delete machine template ");
        }
        
        deleteMachineTemplateFromDb(mt);
        this.em.flush();
    }

    /**
     * All checks done in CIMI REST layer: REST Layer has validated that
     * referenced MachineConfiguration etc do really exist.
     */
    public MachineTemplate createMachineTemplate(final MachineTemplate mt) throws CloudProviderException {

        this.setUser();
        Integer userid = this.user.getId();
        boolean exists = true;
        try {
            MachineTemplate mtemplate = (MachineTemplate) this.em
                .createQuery("FROM MachineTemplate m WHERE m.user.id=:userid AND m.name=:name").setParameter("userid", userid)
                .setParameter("name", mt.getName()).getSingleResult();
        } catch (NoResultException e) {
            exists = false;
        }
        if (exists == true) {
            throw new InvalidRequestException("MachineTemplate by name already exists " + mt.getName());
        }
        MachineConfiguration mc = mt.getMachineConfiguration();
        if (mc == null) {
        	throw new InvalidRequestException("No machineconfiguration ");
        }
        MachineConfiguration mc1 = this.em.find(MachineConfiguration.class, Integer.valueOf(mc.getId()));
        if (mc1 == null) {
        	throw new InvalidRequestException("Invalid reference to machine configuraiton " +mc.getId());
        }
        this.validateMachineConfiguration(mt.getMachineConfiguration());
        /**
         * Check that volumes, machineimage, credentials are known
         */
        List<MachineVolume> volumes = mt.getVolumes();
        if (volumes != null) {
        	for (MachineVolume mv : volumes) {
            	Volume vv = null;
            	if ((mv.getAttachmentPoint() == null ) || (mv.getVolume() == null) || (mv.getVolume().getId() == null)) {
            		throw new InvalidRequestException(" Attachment point empty  " );
            	}
            	try {
            		vv = this.em.find(Volume.class, mv.getVolume().getId());
            	} catch (Exception e) {
            		throw new CloudProviderException(" Internal error ");
            	}
            	if (vv == null) {
            		throw new InvalidRequestException(" Cannot find volume " +mv.getVolume().getId());
            	}
            }
        }

        this.validateNetworkInterface(mt.getNetworkInterfaces());
        mt.setUser(this.user);
        mt.setCreated(new Date());
        this.em.persist(mt);
        this.em.flush();
        return mt;
    }

    public MachineTemplateCollection getMachineTemplateCollection() throws CloudProviderException {
        this.setUser();
        Integer userid = this.user.getId();
        Query query = this.em.createQuery("SELECT c FROM MachineTemplate c WHERE c.user.id=:userid");
        List<MachineTemplate> templates = query.setParameter("userid", userid).getResultList();
        MachineTemplateCollection collection = (MachineTemplateCollection) this.em
            .createQuery("FROM MachineTemplateCollection m WHERE m.user.id=:userid").setParameter("userid", userid)
            .getSingleResult();
        collection.setMachineTemplates(templates);
        return collection;
    }

    public void updateMachineTemplateCollection(final Map<String, Object> attributes) throws CloudProviderException {

        this.setUser();
        Integer userid = this.user.getId();

        MachineTemplateCollection collection = (MachineTemplateCollection) this.em
            .createQuery("FROM MachineTemplateCollection m WHERE m.user.id=:userid").setParameter("userid", userid)
            .getSingleResult();

        if (attributes.containsKey("name")) {
            collection.setName((String) attributes.get("name"));
        }
        if (attributes.containsKey("properties")) {
            collection.setProperties((Map<String, String>) attributes.get("properties"));
        }
        if (attributes.containsKey("description")) {
            collection.setDescription((String) attributes.get("description"));
        }
        this.em.flush();
    }

    /**
     * To complete:
     */
    private void validateMachineConfiguration(final MachineConfiguration mc) throws CloudProviderException {

    	if (mc.getCpu() == null) {
    		throw new InvalidRequestException(" Cpu attribute should be set");
    	}
        /** cpu values */
        if (mc.getCpu().getNumberCpu() < 0) {
            throw new InvalidRequestException("Incorrect MachineConfiguration ");
        }
        if (mc.getCpu().getQuantity() < 0) {
            throw new InvalidRequestException("Incorrect MachineConfiguration ");
        }
        
        if (mc.getMemory() == null) {
        	throw new InvalidRequestException(" Memory attribute should be set");
        }
        /** memory values */
        if (mc.getMemory().getQuantity() < 0) {
            throw new InvalidRequestException("Incorrect MachineConfiguration ");
        }
        /** disk */
        List<DiskTemplate> disks = mc.getDiskTemplates();
        if (disks == null)   {
        	return;
        }
        if (disks.size() == 0) {
        	return;
        }
        for (DiskTemplate d : disks) {
            if (d.getQuantity() < 0) {
                throw new InvalidRequestException("Incorrect MachineConfiguration ");
            }
        }
    }

    private void removeMachine(final Machine deleted) {
        /**
         * We should not need to explicitly remove references?
         */
        User u = deleted.getUser();
        u.getMachines().remove(deleted);
        
        // deleted.setCloudProviderAccount(null);
        // deleted.setUser(null);
        deleted.setVolumes(null);
        
        this.em.remove(deleted);
    }

    /**
     * Handler job completions
     */

    public boolean machineCompletionHandler(final Job notification) {

        /**
         * Find the machine by providerAssignedId (or the job as well)
         */
        String jid = notification.getId().toString();
        /** providerAssignedMachineId */
        String pamid = notification.getTargetEntity();
        Job jpersisted = null;
        try {
            jpersisted = (Job) this.em.createQuery("FROM Job j WHERE j.providerAssignedId=:jid").setParameter("jid", jid)
                .getSingleResult();
        } catch (NoResultException e) {
            /** ignore for now */
            MachineManager.logger.info("Cannot find job for machine" + pamid);
            return false;
        } catch (NonUniqueResultException e) {
            MachineManager.logger.info("No single job for machine !!" + pamid);
            return false;
        } catch (Exception e) {
            MachineManager.logger.info("Internal error in finding job for machine" + pamid);
            return false;
        }
        Machine mpersisted = null;

        try {
            if (jpersisted == null) {
                /**
                 * find the machine from its providerAssignedId in fact there
                 * could be more than one machine with same same
                 * providerAssignedId?
                 */
                mpersisted = (Machine) this.em.createQuery("FROM Machine m WHERE m.providerAssignedId=:pamid")
                    .setParameter("pamid", pamid).getSingleResult();

            } else {
                /** find the machine from its id */
                Integer mid = Integer.valueOf(jpersisted.getTargetEntity());
                mpersisted = this.em.find(Machine.class, mid);
            }
        } catch (NoResultException e) {
            MachineManager.logger.info("Could not find the machine or job for " + pamid);
            return false;
        } catch (NonUniqueResultException e) {
            MachineManager.logger.info("Multiple machines found for " + pamid);
            return false;
        } catch (Exception e) {
            MachineManager.logger.info("Unknown error : Could not find the machine or job for " + pamid);
            return false;
        }

        /** update the machine by invoking the connector */
        CloudProviderAccount cpa = mpersisted.getCloudProviderAccount();
        CloudProviderLocation loc = mpersisted.getLocation();
        ICloudProviderConnector connector;
        try {
            connector = this.getCloudProviderConnector(cpa, loc);
        } catch (CloudProviderException e) {
            /** no point to return false? */
            MachineManager.logger.info("Could not get cloud connector " + e.getMessage());
            return false;
        }
        String connectorid = connector.getCloudProviderId();
        IComputeService computeService = null;
        Machine updated = null;
        try {
            computeService = connector.getComputeService();
            updated = computeService.getMachine(mpersisted.getProviderAssignedId());
        } catch (ConnectorException e) {
            MachineManager.logger.info(" Could not get compute service " + e.getMessage());
            return false;
        }
        
        if (updated == null) {
        	// TODO : what to do?
        }
        if ((notification.getAction().equals("machine.create"))) {
            mpersisted.setCreated(new Date());
        } else if ((notification.getAction().equals("machine.delete"))) {
            /**
             * delete the machine locally if the delete had correctly completed
             */
            // removeMachine(mpersisted);
            /** job will get removed at retention timeout */
        	
        } else {
            mpersisted.setUpdated(new Date());
        }
        if (updated != null) { // paranoia
        	mpersisted.setDisks(updated.getDisks());
        	mpersisted.setCpu(updated.getCpu());
        	mpersisted.setNetworkInterfaces(updated.getNetworkInterfaces());
        	mpersisted.setState(updated.getState());
        	mpersisted.setVolumes(updated.getVolumes());
        }
        /**
         * Update the job
         */
        jpersisted.setStatus(notification.getStatus());
        jpersisted.setReturnCode(notification.getReturnCode());
        jpersisted.setStatusMessage(notification.getStatusMessage());
        jpersisted.setTimeOfStatusChange(notification.getTimeOfStatusChange());

        try {
            this.relConnector(mpersisted, connector);
        } catch (CloudProviderException e) {
            MachineManager.logger.info("Error in releasing connector " + e.getMessage());
        }
        // TODO JobManager (connector side) should delete job
        this.em.flush();
        return true;
    }

    public void machineUpdateCompletionHandler(final Job notification) {
        // SHOULD NOT BE INVOKED!
    }

    private void validateCredentials(final Credentials cred) throws CloudProviderException {

        if (cred.getKey().length == 0) {
            throw new CloudProviderException("Incorrect credentials key length ");
        }

    }

    private void validateNetworkInterface(final List<NetworkInterface> nics) throws CloudProviderException {

    }

    private void validateVolumeTemplates(final List<VolumeTemplate> volumeTemplates) throws CloudProviderException {

    }

}
