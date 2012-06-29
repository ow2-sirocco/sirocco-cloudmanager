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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
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
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ServiceUnavailableException;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@Stateless
@Remote(IRemoteMachineManager.class)
@Local(IMachineManager.class)
public class MachineManager implements IMachineManager {

    static final String EJB_JNDI_NAME = "MachineManager";

    private static Logger logger = Logger.getLogger(MachineManager.class.getName());

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @EJB
    private IUserManager userManager;

    @EJB
    private IVolumeManager volumeManager;

    @EJB
    private IJobManager jobManager;

    @OSGiResource
    private ICloudProviderConnectorFactoryFinder cloudProviderConnectorFactoryFinder;

    @Resource
    private SessionContext ctx;

    @Resource
    public void setSessionContext(final SessionContext ctx) {
        this.ctx = ctx;
    }

    private User getUser() throws CloudProviderException {
        String username = this.ctx.getCallerPrincipal().getName();
        return this.userManager.getUserByUsername(username);
    }

    /**
     * Operations on CloudProviderEntryPoint
     */

    @Override
    public CloudEntryPoint getCloudEntryPoint() throws CloudProviderException {
        Integer userid = this.getUser().getId();
        CloudEntryPoint cep = (CloudEntryPoint) this.em.createQuery("FROM CloudEntryPoint c WHERE c.user.id=:userid")
            .setParameter("userid", userid).getSingleResult();
        return cep;
    }

    /**
     * Placement manager
     */
    private CloudProvider selectCloudProvider(final MachineTemplate mt) throws CloudProviderException {

        Query q = this.em.createQuery("FROM CloudProvider c WHERE c.cloudProviderType=:type");
        q.setParameter("type", "mock");

        q.setMaxResults(1);
        List<CloudProvider> cp = q.getResultList();
        if (cp.size() == 0) {
            throw new CloudProviderException(" No matching cloud provider for template " + mt.getId());
        }
        return cp.get(0);
    }

    private CloudProviderAccount getCloudProviderAccount(final CloudProvider provider, final User u,
        final MachineTemplate template) {
        Set<CloudProviderAccount> accounts = provider.getCloudProviderAccounts();
        CloudProviderAccount a = null;

        if (accounts.isEmpty() == false) {
            a = accounts.iterator().next();
        }
        return a;
    }

    private boolean checkQuota(final User u, final MachineConfiguration mc) {
        return true;
    }

    /** helper methods to obtain connector handle */
    private ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount account,
        final CloudProviderLocation location) throws CloudProviderException {

        if (account == null) {
            throw new CloudProviderException("Cloud provider account ");
        }
        ICloudProviderConnectorFactory connectorFactory = this.cloudProviderConnectorFactoryFinder
            .getCloudProviderConnectorFactory(account.getCloudProvider().getCloudProviderType());
        if (connectorFactory == null) {
            throw new CloudProviderException(" Internal error in connector factory ");
        }
        return connectorFactory.getCloudProviderConnector(account, location);
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

    private void relConnector(final String cpType, final ICloudProviderConnector connector) throws CloudProviderException {

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

    /**
     * User could have passed by value or by reference. Validation is expected
     * to be done by REST layer
     */
    private void checkVolumes(final MachineTemplate mt, final User u) throws CloudProviderException {

        List<MachineVolume> volumes = mt.getVolumes();
        if (volumes == null || volumes.size() == 0) {
            return;
        }

        for (MachineVolume mv : volumes) {

            if (mv.getInitialLocation() == null) {
                throw new InvalidRequestException("initialLocation not set for volume ");
            }

            Volume v = mv.getVolume();
            /**
             * Volume should not be passed by value. Check that the volume id
             * exists.
             */
            if ((v == null) || (v.getId() == null)) {
                throw new InvalidRequestException("No volume id ");
            }

            this.volumeManager.getVolumeById(v.getId().toString());
        }

        List<MachineVolumeTemplate> vts = mt.getVolumeTemplates();

        if (vts == null) {
            throw new InvalidRequestException("VolumeTemplates array null");
        }
        if (vts.size() == 0) {
            return;
        }

        for (MachineVolumeTemplate mvt : vts) {
            if (mvt.getInitialLocation() == null) {
                throw new InvalidRequestException("initialLocation not set for volume template");
            }

            VolumeTemplate vt = mvt.getVolumeTemplate();
            if ((vt == null) || (vt.getId() == null)) {
                throw new InvalidRequestException("No volume template id ");
            }

            this.volumeManager.getVolumeTemplateById(vt.getId().toString());
        }
    }

    // TODO
    private void validateMachineImage(final MachineTemplate mt, final User u) throws InvalidRequestException {
        MachineImage mi = mt.getMachineImage();
        if ((mi == null) || (mi.getId() == null)) {
            throw new InvalidRequestException(" MachineImage should be set");
        }
        // check that machine image is known
        // use MachineImageManager

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

    private Job createJob(final CloudResource targetResource, final List<CloudResource> affectedResources, final String action,
        final Job.Status status, final Job parent) throws CloudProviderException {

        Job j = new Job();
        j.setTargetEntity(targetResource);
        j.setAffectedEntities(affectedResources);
        j.setAction(action);
        j.setStatus(status);
        j.setUser(this.getUser());
        j.setCreated(new Date());
        j.setProperties(new HashMap<String, String>());
        j.setParentJob(parent);

        this.em.persist(j);
        return j;
    }

    private void updateJob(final Job job) {
        // this.jobManager.updateJob(job);
        this.em.flush();
    }

    /**
     * Creation of new volumes for a new machine being created.
     */
    private void addVolumes(final Job parent, final Machine m, final List<MachineVolumeTemplate> vts) {

        Job connJob = null;

        for (MachineVolumeTemplate mvt : vts) {
            MachineManager.logger.info(" creating new volume for machine ");

            VolumeCreate volumeCreate = new VolumeCreate();
            // TODO
            volumeCreate.setName("internal");
            volumeCreate.setVolumeTemplate(mvt.getVolumeTemplate());
            try {
                connJob = this.volumeManager.createVolume(volumeCreate);
            } catch (CloudProviderException e) {
                // TODO clean up
                MachineManager.logger.info(" Error in creating volume ");
                return;
            }
            // TODO: lock parent
            connJob.setParentJob(parent);
            this.updateJob(connJob);

            MachineManager.logger.info("addVolumes job " + connJob.getId());

            MachineVolume mv = new MachineVolume();
            mv.setVolume((Volume) connJob.getTargetEntity());
            mv.setInitialLocation(mvt.getInitialLocation());
            mv.setState(MachineVolume.State.PENDING);
            this.em.persist(mv);
            m.addMachineVolume(mv);
        }
    }

    private boolean volumeInUse(final Volume volume) {
        Query q = this.em.createQuery("FROM MachineVolume v WHERE v.volume.id=:vid");
        q.setParameter("vid", volume.getId());
        List<MachineVolume> list = q.getResultList();
        for (MachineVolume mv : list) {
            if ((mv.getState() == MachineVolume.State.ATTACHED) || (mv.getState() == MachineVolume.State.ATTACHING)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create needed machine volumes for new machine. The input list is from
     * machine template used in creation
     */
    private void attachVolumes(final Job j, final Machine m, final List<MachineVolume> volumes) {

        for (MachineVolume mvsrc : volumes) {

            Volume v = mvsrc.getVolume();
            if (this.volumeInUse(v) == true) {
                continue;
            }
            MachineVolume mv = new MachineVolume();
            mv.setVolume(mvsrc.getVolume());
            mv.setInitialLocation(mvsrc.getInitialLocation());
            mv.setState(MachineVolume.State.PENDING);
            this.em.persist(mv);
            m.addMachineVolume(mv);
        }
    }

    public Job createMachine(final MachineCreate machineCreate) throws CloudProviderException {

        MachineTemplate mt = machineCreate.getMachineTemplate();

        this.validateCreationParameters(mt, this.getUser());

        /**
         * TODO Check quota
         */
        if (this.checkQuota(this.getUser(), mt.getMachineConfiguration()) == false) {
            throw new CloudProviderException("User exceeded quota ");
        }
        MachineManager.logger.info(" selectCloudProviders ");
        /**
         * Obtain list of matching provider
         */
        CloudProvider provider = this.selectCloudProvider(mt);
        CloudProviderAccount account = null;

        account = this.getCloudProviderAccount(provider, this.getUser(), mt);
        if (account == null) {
            throw new CloudProviderException("Could not find a cloud provider account ");
        }
        /** there must be at least one location if we are here */
        CloudProviderLocation mylocation = null;// myprovider.getCloudProviderLocations().get(0);
        ICloudProviderConnector connector = this.getCloudProviderConnector(account, mylocation);
        if (connector == null) {
            throw new CloudProviderException("Could not obtain connector to provider "
                + account.getCloudProvider().getCloudProviderType());
        }
        String connectorid = connector.getCloudProviderId();
        MachineManager.logger.info(" got a connector " + connectorid);

        Job jobCreateMachine = null;
        IComputeService computeService = null;

        /**
         * Convention: The entity Ids refer to sirocco given ids. The provider
         * id is stored in providerAssignedId. The connector layer will use
         * providerAssignedId in its communication with the real provider.
         */
        try {
            computeService = connector.getComputeService();
            jobCreateMachine = computeService.createMachine(machineCreate);
        } catch (Exception e) {
            MachineManager.logger.info("Failed to create machine ", e);
            throw new CloudProviderException(e.getMessage());
        }

        if (jobCreateMachine.getStatus() == Job.Status.FAILED) {
            throw new ServiceUnavailableException("Machine creation failed ");
        }

        Machine m = new Machine();

        m.setName(machineCreate.getName());
        m.setDescription(machineCreate.getDescription());
        m.setProperties(machineCreate.getProperties());

        m.setState(Machine.State.CREATING);
        m.setUser(this.getUser());
        m.setCpu(mt.getMachineConfiguration().getCpu());
        m.setMemory(mt.getMachineConfiguration().getMemory());

        m.setCloudProviderAccount(account);
        m.setProviderAssignedId(jobCreateMachine.getTargetEntity().getProviderAssignedId());

        m.setLocation(mylocation);
        m.setCreated(new Date());

        this.initVolumeCollection(m);
        this.initDiskCollection(m);
        this.em.persist(m);

        MachineManager.logger.info("New machine id " + m.getId().toString());

        /**
         * Create root job and link child
         */
        List<CloudResource> affectedEntities = new ArrayList<CloudResource>();
        affectedEntities.add(m);
        Job j = this.createJob(m, affectedEntities, "add", Job.Status.RUNNING, null);
        j.setDescription("Machine Collection add ");
        Map<String, String> map = j.getProperties();
        map.put("parent-machine", "ok");
        j.setProperties(map);

        this.updateJob(j);
        MachineManager.logger.info("Machine create non leaf job id " + j.getId() + " status " + jobCreateMachine.getStatus());
        Job child = this.createJob(m, null, "add", jobCreateMachine.getStatus(), j);

        child.setProviderAssignedId(jobCreateMachine.getProviderAssignedId());
        child.setDescription("Machine creation ");
        this.updateJob(child);

        // Ask for connector to notify when job completes
        try {
            UtilsForManagers.emitJobListenerMessage(jobCreateMachine.getProviderAssignedId(), this.ctx);
        } catch (Exception e) {
            throw new ServiceUnavailableException(e.getMessage());
        }
        MachineManager.logger.info(" Machine create child job " + child.getId());
        /**
         * add volumes to machines and attach them
         */
        List<MachineVolumeTemplate> volTemplates = mt.getVolumeTemplates();

        if (volTemplates != null && volTemplates.size() != 0) {
            this.addVolumes(j, m, volTemplates);
        }
        List<MachineVolume> vol = mt.getVolumes();
        if (vol != null && vol.size() != 0) {
            this.attachVolumes(j, m, vol);
        }
        MachineManager.logger.info("Return Job of new machine creation " + j.getId().toString());

        return j;
    }

    private void readMachineAttributes(final Machine m) {

        // disks
        if (m.getDisks() != null) {
            m.getDisks().size();
        }

        // network interfaces

        if (m.getNetworkInterfaces() != null) {
            m.getNetworkInterfaces().size();
        }

        if (m.getVolumes() != null) {
            m.getVolumes().size();
        }
        m.initFSM();
    }

    public List<Machine> getMachines(final int first, final int last, final List<String> attributes)
        throws CloudProviderException {

        if ((first < 0) || (last < 0) || (last < first)) {
            throw new InvalidRequestException(" Illegal array index " + first + " " + last);
        }

        Query query = this.em
            .createNamedQuery("FROM Machine v WHERE v.user.username=:userName AND v.state<>'DELETED' ORDER BY v.id");
        query.setParameter("userName", this.getUser().getUsername());
        query.setMaxResults(last - first + 1);
        query.setFirstResult(first);
        List<Machine> machines = query.setFirstResult(first).setMaxResults(last - first + 1).getResultList();

        for (Machine machine : machines) {
            this.readMachineAttributes(machine);
        }
        return machines;
    }

    @Override
    public List<Machine> getMachines() throws CloudProviderException {
        return UtilsForManagers.getEntityList("Machine", this.em, this.getUser().getUsername());
    }

    // TODO
    public List<Machine> getMachines(final List<String> attributes, final String queryExpression) throws CloudProviderException {
        List<Machine> machines = new ArrayList<Machine>();
        if (queryExpression != null && !queryExpression.isEmpty()) {
            // TODO
            throw new UnsupportedOperationException();
        }
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
        m = this.em.find(Machine.class, Integer.valueOf(machineId));

        if (m == null) {
            throw new ResourceNotFoundException("Machine " + machineId + " not found");
        }

        m.initFSM();

        Set<String> actions = m.getOperations();

        if (actions.contains(action) == false) {
            throw new InvalidRequestException(" Cannot " + action + "  machine at state " + m.getState());
        }

        return m;
    }

    public Job startMachine(final String machineId) throws CloudProviderException {

        Job persistedJob = this.doService(machineId, "start");
        return persistedJob;
    }

    public Job stopMachine(final String machineId) throws CloudProviderException {

        return this.doService(machineId, "stop");
    }

    private Job doService(final String machineId, final String action) throws CloudProviderException {

        Job j;
        Machine m = this.checkOps(machineId, action);
        ICloudProviderConnector connector = this.getConnector(m);
        IComputeService computeService;
        try {
            computeService = connector.getComputeService();
        } catch (ConnectorException e) {
            String eee = e.getMessage();
            throw new ServiceUnavailableException(" " + eee + " action " + action + " machine " + machineId + " "
                + m.getProviderAssignedId());
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
                throw new ServiceUnavailableException("Unsupported operation action " + action + " on machine id "
                    + m.getProviderAssignedId() + " " + m.getId());
            }
        } catch (ConnectorException e) {
            throw new ServiceUnavailableException(e.getMessage() + " action " + action + " machine id "
                + m.getProviderAssignedId() + " " + m.getId());
        }
        MachineManager.logger.info("operation " + action + " for machine " + m.getId() + " job status " + j.getStatus());

        Job job = this.createJob(m, null, action, j.getStatus(), null);
        job.setProviderAssignedId(j.getProviderAssignedId());
        this.updateJob(job);
        Map<String, String> map = job.getProperties();
        map.put("parent-machine", "ok");
        job.setProperties(map);

        /** Ask connector for notification */

        if (j.getStatus() != Job.Status.FAILED) {
            try {
                UtilsForManagers.emitJobListenerMessage(job.getProviderAssignedId(), this.ctx);
            } catch (Exception e) {
                throw new ServiceUnavailableException(e.getMessage() + "  " + action);
            }
        }
        /** Tell connector that we are done with it */
        MachineManager.logger.info("operation " + action + " requested " + j.getStatus());
        this.relConnector(m, connector);
        return job;
    }

    // Delete may be done in any state of the machine
    public Job deleteMachine(final String machineId) throws CloudProviderException {
        Job j = null;
        MachineManager.logger.info("deleteMachine " + machineId);
        if (machineId == null) {
            throw new InvalidRequestException(" Null machine id");
        }
        Machine m = this.em.find(Machine.class, Integer.valueOf(machineId));
        if (m == null) {
            throw new ResourceNotFoundException(" Invalid machine id " + machineId);
        }

        /** Do not allow delete if there are any attached volumes */
        List<MachineVolume> attachedVolumes = m.getVolumes();
        if (attachedVolumes != null && attachedVolumes.size() >= 0) {
            for (MachineVolume mv : attachedVolumes) {
                if (mv.getState() == MachineVolume.State.ATTACHED) {
                    MachineManager.logger.info(" Detach volumes before deleting the machine " + m.getId()
                        + mv.getInitialLocation());
                    throw new InvalidRequestException(" Cannot delete machine with attached volumes " + m.getId());
                }
            }
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

        if (j.getStatus() != Job.Status.RUNNING) {
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

        Job job = this.createJob(m, null, "delete", j.getStatus(), null);
        job.setProviderAssignedId(j.getProviderAssignedId());
        this.updateJob(job);
        Map<String, String> map = job.getProperties();
        map.put("parent-machine", "ok");
        job.setProperties(map);

        if (j.getStatus() != Job.Status.FAILED) {
            try {
                UtilsForManagers.emitJobListenerMessage(job.getProviderAssignedId(), this.ctx);
            } catch (Exception e) {
                throw new ServiceUnavailableException(e.getMessage());
            }
        }
        /** Tell connector that we are done with it */
        this.relConnector(m, connector);
        return job;
    }

    private Machine getMachineFromId(final String machineId) throws ResourceNotFoundException, CloudProviderException {
        if (machineId == null) {
            throw new InvalidRequestException(" null machine id");
        }
        Machine m = this.em.find(Machine.class, Integer.valueOf(machineId));
        if (m == null || m.getState() == Machine.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid machine id " + machineId);
        }
        return m;
    }

    public Machine getMachineById(final String machineId) throws ResourceNotFoundException, CloudProviderException {
        Machine m = this.getMachineFromId(machineId);
        this.readMachineAttributes(m);
        return m;
    }

    public Machine getMachineAttributes(final String machineId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        Machine m = this.getMachineFromId(machineId);
        this.readMachineAttributes(m);
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
        if (requested.containsKey("name")) {
            s.put("name", requested.get("name"));
        }
        if (requested.containsKey("description")) {
            s.put("description", requested.get("description"));
        }
        return s;
    }

    @Override
    public Job updateMachine(final Machine machine) throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    // TODO
    public Job updateMachineAttributes(final String machineId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, CloudProviderException {

        Job j = null;

        Machine m = this.getMachineFromId(machineId);

        if (attributes.containsKey("name")) {
            m.setName((String) attributes.get("name"));
        }

        if (attributes.containsKey("description")) {
            m.setDescription((String) attributes.get("description"));
        }

        j = new Job();
        j.setTargetEntity(m);
        j.setStatus(Job.Status.SUCCESS);
        j.setAction("update");
        j.setParentJob(null);
        j.setNestedJobs(null);
        j.setReturnCode(0);
        j.setUser(this.getUser());
        this.em.persist(j);
        this.em.flush();
        return j;

    }

    /**
     * Operations on MachineConfiguration
     */
    public MachineConfiguration getMachineConfigurationById(final String mcId) throws CloudProviderException {
        if (mcId == null) {
            throw new InvalidRequestException(" null machine configuration id");
        }
        MachineConfiguration mc = this.em.find(MachineConfiguration.class, Integer.valueOf(mcId));
        if (mc == null) {
            throw new ResourceNotFoundException("Unknown machine configuration " + mcId);
        }
        mc.getDiskTemplates().size();
        return mc;
    }

    @Override
    public void updateMachineConfiguration(final MachineConfiguration machineConfiguration) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    public void updateMachineConfigurationAttributes(final String mcId, final Map<String, Object> attributes)
        throws CloudProviderException {
        if ((mcId == null) || (attributes == null)) {
            throw new InvalidRequestException(" null machine configuration id");
        }
        MachineConfiguration mc = this.em.find(MachineConfiguration.class, Integer.valueOf(mcId));
        if (mc == null) {
            throw new ResourceNotFoundException("Unknown machine configuration " + mcId);
        }
        if (attributes.containsKey("cpu")) {
            mc.setCpu((Integer) attributes.get("cpu"));
        }
        if (attributes.containsKey("memory")) {
            mc.setMemory((Integer) attributes.get("memory"));
        }

        if (attributes.containsKey("disks")) {
            List<DiskTemplate> dts = (List<DiskTemplate>) attributes.get("disks");
            mc.setDiskTemplates(dts);
        }

        this.em.flush();
    }

    public void deleteMachineConfiguration(final String mcId) throws CloudProviderException {

        MachineConfiguration config = (MachineConfiguration) this.getObjectFromEM(MachineConfiguration.class, mcId);

        List<MachineTemplate> mts = null;
        try {
            /**
             * Refuse delete if configuration is being used.
             */
            mts = this.em.createQuery("FROM MachineTemplate m WHERE m.machineConfiguration.id=:mcid")
                .setParameter("mcid", Integer.valueOf(mcId)).getResultList();
        } catch (Exception e) {
            return;
        }
        if ((mts != null) && (mts.size() > 0)) {
            throw new ResourceConflictException("MachineTemplates " + mts.get(0).getId() + " uses the configuration " + mcId);
        }
        config.setUser(null);

        this.em.remove(config);
        this.em.flush();
    }

    @Override
    public List<MachineConfiguration> getMachineConfigurations() throws CloudProviderException {
        List<MachineConfiguration> machineConfigs = this.em
            .createQuery("SELECT c FROM MachineConfiguration c WHERE c.user.id=:userid")
            .setParameter("userid", this.getUser().getId()).getResultList();
        for (MachineConfiguration machineConfig : machineConfigs) {
            machineConfig.getDiskTemplates().size();
        }
        return machineConfigs;
    }

    @Override
    public List<MachineConfiguration> getMachineConfigurations(final int first, final int last, final List<String> attributes)
        throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        Query query = this.em.createQuery("FROM MachineConfiguration v WHERE v.user.username=:username ORDER BY v.id");
        query.setParameter("username", user.getUsername());
        query.setMaxResults(last - first + 1);
        query.setFirstResult(first);
        return query.setFirstResult(first).setMaxResults(last - first + 1).getResultList();
    }

    @Override
    public List<MachineConfiguration> getMachineConfigurations(final List<String> attributes, final String queryExpression)
        throws InvalidRequestException, CloudProviderException {
        if (queryExpression != null && !queryExpression.isEmpty()) {
            // TODO
            throw new UnsupportedOperationException();
        }
        User user = this.getUser();
        return this.em.createQuery("FROM MachineConfiguration v WHERE v.user.username=:username ORDER BY v.id")
            .setParameter("username", user.getUsername()).getResultList();
    }

    public MachineConfiguration createMachineConfiguration(final MachineConfiguration machineConfig)
        throws CloudProviderException {

        Integer userid = this.getUser().getId();
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
        machineConfig.setUser(this.getUser());
        machineConfig.setCreated(new Date());
        this.em.persist(machineConfig);
        this.em.flush();
        return machineConfig;
    }

    /**
     * Operations on MachineTemplate
     */
    public MachineTemplate getMachineTemplateById(final String mtId) throws CloudProviderException {
        if (mtId == null) {
            throw new InvalidRequestException(" null machine template id");
        }
        MachineTemplate mt = this.em.find(MachineTemplate.class, Integer.valueOf(mtId));
        if (mt == null) {
            throw new ResourceNotFoundException(" Could not find machine template" + mtId);
        }
        if (mt.getVolumes() != null) {
            mt.getVolumes().size();
        }
        if (mt.getVolumeTemplates() != null) {
            mt.getVolumeTemplates().size();
        }
        mt.getNetworkInterfaces().size();
        mt.getMachineConfiguration().getDiskTemplates().size();

        return mt;
    }

    @Override
    public void updateMachineTemplate(final MachineTemplate machineTemplate) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    public void updateMachineTemplateAttributes(final String mtId, final Map<String, Object> attributes)
        throws CloudProviderException {
        MachineTemplate mt = null;
        if ((mtId == null) || (attributes == null)) {
            throw new InvalidRequestException(" null machine template id");
        }
        mt = this.em.find(MachineTemplate.class, Integer.valueOf(mtId));
        if (mt == null) {
            throw new ResourceNotFoundException(" Could not find machine template" + mtId);
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
                    throw new InvalidRequestException(" Could not find machine configuration" + mc);
                }
                mt.setMachineConfiguration(config);
            }
            if (attributes.containsKey("machineImage")) {
                String mi = (String) attributes.get("machineImage");

                MachineImage image = this.em.find(MachineImage.class, Integer.valueOf(mi));
                if (image == null) {
                    throw new InvalidRequestException(" Could not find machine image" + mi);
                }
                mt.setMachineImage(image);
            }
            if (attributes.containsKey("credentials")) {
                String credentials = (String) attributes.get("credentials");

                Credentials cred = this.em.find(Credentials.class, Integer.valueOf(credentials));
                if (cred == null) {
                    throw new InvalidRequestException(" Could not find credentials" + credentials);
                }
                mt.setCredentials(cred);
            }

            if (attributes.containsKey("networkInterfaces")) {
                List<MachineTemplateNetworkInterface> list = (List<MachineTemplateNetworkInterface>) (attributes
                    .get("networkInterfaces"));

                /** validate(list); */
                mt.setNetworkInterfaces(list);
            }
            mt.setUpdated(new Date());
            this.em.merge(mt);
            this.em.flush();
        } catch (Exception e) {
            throw new CloudProviderException(e.getMessage());
        }
    }

    private void deleteMachineTemplateFromDb(final MachineTemplate mt) {
        mt.setUser(null);

        List<MachineVolume> vColl = mt.getVolumes();
        mt.setVolumes(null);
        if (vColl != null) {
            for (MachineVolume mv : vColl) {
                mv.setVolume(null);
                this.em.remove(mv);
            }
        }

        List<MachineVolumeTemplate> vtColl = mt.getVolumeTemplates();
        mt.setVolumeTemplates(null);
        if (vtColl != null) {
            for (MachineVolumeTemplate mvt : vtColl) {
                mvt.setVolumeTemplate(null);
                this.em.remove(mvt);
            }
        }

        this.em.remove(mt);
        this.em.flush();
    }

    public void deleteMachineTemplate(final String mtId) throws CloudProviderException {

        MachineTemplate mt = this.em.find(MachineTemplate.class, Integer.valueOf(mtId));
        if (mt == null) {
            throw new ResourceNotFoundException("Cannot find machine template " + mtId);
        }
        if (mt.getUser().equals(this.getUser()) == false) {
            throw new CloudProviderException("Not owner, cannot delete machine template ");
        }

        this.deleteMachineTemplateFromDb(mt);

    }

    /**
     * For initial creation of machine template
     */
    private void createVolumeTemplateCollectionForMt(final MachineTemplate mt) throws CloudProviderException {

        List<MachineVolumeTemplate> volumeTemplates = mt.getVolumeTemplates();

        if (volumeTemplates == null || volumeTemplates.size() == 0) {
            return;
        }

        for (MachineVolumeTemplate mvt : volumeTemplates) {

            if (mvt.getInitialLocation() == null) {
                // ignore this entry
                continue;
            }

            VolumeTemplate vt = mvt.getVolumeTemplate();
            if ((vt == null) || (vt.getId() == null)) {
                // ignore this entry
                continue;
            }

            // TODO volume manager
            VolumeTemplate vtt = null;
            try {
                vtt = (VolumeTemplate) this.getObjectFromEM(VolumeTemplate.class, vt.getId().toString());
            } catch (CloudProviderException e) {
                MachineManager.logger.info(" Incorrect volume template being attached to machine template " + vt.getId()
                    + " ignoring ");
                continue;
            }

            // TODO unidirectional
            mvt.setVolumeTemplate(vtt);
            this.em.persist(mvt);
            mt.addMachineVolumeTemplate(mvt);
        }
    }

    private void createVolumeCollectionForMt(final MachineTemplate mt) throws CloudProviderException {

        List<MachineVolume> volumes = mt.getVolumes();

        if (volumes == null || volumes.size() == 0) {
            return;
        }

        for (MachineVolume mv : volumes) {

            if (mv.getInitialLocation() == null) {
                // ignore this entry
                continue;
            }

            Volume v = mv.getVolume();
            if ((v == null) || (v.getId() == null)) {
                // ignore this entry
                continue;
            }

            // TODO volume manager
            Volume vv = null;
            try {
                vv = this.em.find(Volume.class, v.getId());
            } catch (Exception e) {
                MachineManager.logger.info(" Incorrect volume being attached to machine template " + v.getId() + " ignoring ");
                continue;
            }

            mv.setVolume(vv);
            this.em.persist(mv);
            mt.addMachineVolume(mv);
        }
    }

    /**
     * Persist each networkinterface in the data base TODO: NetworkManager.
     */
    private void createNetworkInterfaces(final MachineTemplate mt) throws CloudProviderException {
        List<MachineTemplateNetworkInterface> list = mt.getNetworkInterfaces();
        for (MachineTemplateNetworkInterface nic : list) {
            this.em.persist(nic);
            mt.addNetworkInterface(nic);
        }
    }

    /**
     * All checks done in CIMI REST layer: REST Layer has validated that
     * referenced MachineConfiguration etc do really exist.
     */
    public MachineTemplate createMachineTemplate(final MachineTemplate mt) throws CloudProviderException {

        Integer userid = this.getUser().getId();
        boolean exists = true;
        try {
            this.em.createQuery("FROM MachineTemplate m WHERE m.user.id=:userid AND m.name=:name")
                .setParameter("userid", userid).setParameter("name", mt.getName()).getSingleResult();
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
            throw new InvalidRequestException("Invalid reference to machine configuraiton " + mc.getId());
        }
        this.validateMachineConfiguration(mt.getMachineConfiguration());

        MachineImage mi = mt.getMachineImage();
        if (mi == null) {
            throw new InvalidRequestException("No machine image ");
        }
        mi = this.em.find(MachineImage.class, Integer.valueOf(mi.getId()));
        if (mi == null || mi.getState() == MachineImage.State.DELETED) {
            throw new InvalidRequestException("Invalid reference to machine image " + mi.getId());
        }

        /**
         * create volume and volume template collection.
         */
        this.createVolumeCollectionForMt(mt);
        this.createVolumeTemplateCollectionForMt(mt);
        this.createNetworkInterfaces(mt);

        mt.setUser(this.getUser());
        mt.setCreated(new Date());
        this.em.persist(mt);
        this.em.flush();
        if (mt.getMachineConfiguration().getDiskTemplates() != null) {
            mt.getMachineConfiguration().getDiskTemplates().size();
        }
        if (mt.getMachineConfiguration().getProperties() != null) {
            mt.getMachineConfiguration().getProperties().size();
        }
        if (mt.getVolumes() != null) {
            mt.getVolumes().size();
        }
        return mt;
    }

    @Override
    public List<MachineTemplate> getMachineTemplates(final int first, final int last, final List<String> attributes)
        throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MachineTemplate> getMachineTemplates() throws CloudProviderException {
        List<MachineTemplate> machineTemplates = this.em.createQuery("SELECT c FROM MachineTemplate c WHERE c.user.id=:userid")
            .setParameter("userid", this.getUser().getId()).getResultList();
        for (MachineTemplate machineTemplate : machineTemplates) {
            machineTemplate.getMachineConfiguration().getDiskTemplates().size();
        }
        return machineTemplates;
    }

    @Override
    public List<MachineTemplate> getMachineTemplates(final List<String> attributes, final String queryExpression)
        throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * To complete:
     */
    private void validateMachineConfiguration(final MachineConfiguration mc) throws CloudProviderException {

        if (mc.getCpu() == null) {
            throw new InvalidRequestException(" Cpu attribute should be set");
        }
        /** cpu values */
        if (mc.getCpu() < 0) {
            throw new InvalidRequestException("Incorrect MachineConfiguration ");
        }
        if (mc.getCpu() < 0) {
            throw new InvalidRequestException("Incorrect MachineConfiguration ");
        }

        if (mc.getMemory() == null) {
            throw new InvalidRequestException(" Memory attribute should be set");
        }
        /** memory values */
        if (mc.getMemory() < 0) {
            throw new InvalidRequestException("Incorrect MachineConfiguration ");
        }

        /** disk */
        List<DiskTemplate> disks = mc.getDiskTemplates();
        if (disks == null) {
            return;
        }
        if (disks.size() == 0) {
            return;
        }
        for (DiskTemplate d : disks) {
            if (d.getCapacity() < 0) {
                throw new InvalidRequestException("Incorrect MachineConfiguration ");
            }
            if (d.getFormat() == null) {
                throw new InvalidRequestException("Incorrect MachineConfiguration format should be set ");
            }
            // TODO initialLocation
        }
    }

    private void removeMachine(final Machine deleted) {
        MachineManager.logger.info(" deleting machine " + deleted.getId());

        deleted.setCloudProviderAccount(null);

        /**
         * TODO: CHECK what to do with the volumes? Should they be deleted?
         */
        List<MachineVolume> volColl = deleted.getVolumes();
        List<MachineDisk> diskColl = deleted.getDisks();

        deleted.setVolumes(null);
        deleted.setDisks(null);

        if (volColl != null) {
            for (MachineVolume mv : volColl) {
                mv.setVolume(null);
                mv.setState(MachineVolume.State.DELETED);
                // this.em.remove(mv);
            }
        }

        if (diskColl != null) {
            for (MachineDisk disk : diskColl) {
                disk.setState(MachineDisk.State.DELETED);
                // this.em.remove(disk);
            }
        }

        deleted.setState(State.DELETED);
        this.em.flush();
    }

    private void initDiskCollection(final Machine m) {
    }

    private void initVolumeCollection(final Machine m) {
    }

    /**
     * Initialize disks for newly created machine
     */
    private void createDisks(final Machine persisted, final Machine created) {
        List<MachineDisk> diskColl = created.getDisks();
        if (diskColl == null) {
            return;
        }
        for (MachineDisk disk : diskColl) {
            this.em.persist(disk);
            persisted.addMachineDisk(disk);
        }

        this.em.flush();
    }

    /**
     * Create network interface entities
     */
    private void createNetworkInterfaces(final Machine persisted, final Machine created) {
        List<MachineNetworkInterface> nics = created.getNetworkInterfaces();
        if (nics == null) {
            return;
        }
        for (MachineNetworkInterface nic : nics) {

            /** TODO check that the network exists */

            Network network = nic.getNetwork();

            List<Address> addresses = nic.getAddresses();
            for (Address address : addresses) {
                this.em.persist(address);
            }

            NetworkPort networkPort = nic.getNetworkPort();

            this.em.persist(networkPort);

            this.em.persist(nic);
            persisted.addNetworkInterface(nic);
        }
        this.em.flush();
    }

    private boolean isDiskAdd(final Job job) {
        /** TODO */
        return false;
    }

    private boolean isNetworkInterfaceAdd(final Job job) {
        return false;
    }

    /**
     * targetEntity should be a machine and first affected entity volume
     */
    private boolean isVolumeAttach(final Job job) {

        if ((job.getTargetEntity() instanceof Machine) && (job.getAffectedEntities().get(0) instanceof Volume)) {
            Volume volume = (Volume) job.getAffectedEntities().get(0);
            try {
                this.volumeManager.getVolumeById(volume.getId().toString());
            } catch (ResourceNotFoundException e) {
                MachineManager.logger.info(" Volume " + volume.getId() + "seems to have disappeared !");
            }
            return true;
        }
        return false;
    }

    private boolean isVolumeDetach(final Job job) {
        List<CloudResource> resources = job.getAffectedEntities();
        if ((job.getAction().equals("delete") == false) || (resources == null) || (resources.size() == 0)) {
            return false;
        }

        CloudResource resource = resources.get(0);
        if (resource instanceof MachineVolume) {
            return true;
        }
        return false;
    }

    /** remove the machine volume entry for machine */
    private void removeMachineVolumeEntry(final Machine m, final Volume v) {
        List<MachineVolume> items = m.getVolumes();

        for (MachineVolume mv : items) {
            if (mv.getVolume().getId().equals(v.getId())) {
                mv.setVolume(null);
                m.removeMachineVolume(mv);
                this.em.remove(mv);
                // only one MachineVolume entry expected per volume
                break;
            }
        }
        this.em.flush();
    }

    private MachineVolume getMachineVolume(final Machine m, final Volume v) {
        List<MachineVolume> list = m.getVolumes();
        for (MachineVolume mv : list) {
            if (mv.getVolume().getId().equals(v.getId())) {
                return mv;
            }
        }
        return null;
    }

    /**
     * 
     */
    private Machine getMachineFromConnector(final CloudProviderAccount cpa, final CloudProviderLocation loc,
        final String providerAssignedMachineId) {
        ICloudProviderConnector connector;

        try {
            connector = this.getCloudProviderConnector(cpa, loc);
        } catch (CloudProviderException e) {
            /** no point to return false? */
            MachineManager.logger.info("Could not get cloud connector " + e.getMessage());
            return null;
        }
        String connectorid = connector.getCloudProviderId();
        IComputeService computeService = null;
        Machine m = null;
        try {
            computeService = connector.getComputeService();
            m = computeService.getMachine(providerAssignedMachineId);
        } catch (ConnectorException e) {
            MachineManager.logger.info(" Could not get compute service " + e.getMessage());
            return null;
        }
        String cpType = cpa.getCloudProvider().getCloudProviderType();
        try {
            this.relConnector(cpType, connector);
        } catch (CloudProviderException e) {
            MachineManager.logger.info(" Could not release connector ");
        }
        return m;
    }

    /**
     * "add" operation completion for following operations: - machine create
     * (add machine to machine collection) - attach volume to machine (add
     * machinevolume to machinevolumecollection) - add disk to machine (add
     * MachineDisk to MachineDiskCollection) - add network interface (add
     * networkInterface to NetworkInterfacesCollection)
     */

    private Job getMachineCreateJob(final List<Job> children) {
        return children.get(0);
    }

    /**
     * Continue machine creation tasks in job completion handler. job : root
     * non-leaf job of machine creation
     * 
     * @throws CloudProviderException
     */
    private boolean machineCreationContinuation(final Job job, final Machine m) throws CloudProviderException {

        List<Job> children = job.getNestedJobs();
        MachineManager.logger.info("machineCreationContinuation child jobs " + children.size());
        job.setTimeOfStatusChange(new Date());
        this.updateJob(job);

        /**
         * If machine is not completed return immediately
         */
        Job machineCreate = this.getMachineCreateJob(children);

        if (machineCreate == null) {
            MachineManager.logger.info("Could not find the machine creation job!! ");
            return false;
        }
        if (machineCreate.getStatus() == Job.Status.RUNNING) {
            MachineManager.logger.info("machineCreationContinuation machine job status " + machineCreate.getId() + " "
                + machineCreate.getStatus());
            return true;
        }
        if (machineCreate.getStatus() == Job.Status.FAILED) {
            MachineManager.logger.info("machine create job failed ");
            job.setStatus(Job.Status.FAILED);
            // TODO cleanup of volumes

            return true;
        }
        /**
         * Machine is created, do attachments for volumes whose creation have
         * completed
         */
        List<MachineVolume> mvs = m.getVolumes();
        ArrayList<Job> newJobs = new ArrayList<Job>();

        for (MachineVolume mv : mvs) {
            Volume volume = mv.getVolume();
            MachineManager.logger.info(" machineCreateContinuation check volume " + volume.getState());
            if (volume.getState() == Volume.State.CREATING) {
                continue;
            } else if (volume.getState() == Volume.State.AVAILABLE) {
                if (mv.getState() == MachineVolume.State.PENDING) {
                    mv.setState(MachineVolume.State.ATTACHING);
                    Job j = null;
                    try {
                        j = this.addVolumeToMachine(m, mv);
                    } catch (Exception e) {
                        MachineManager.logger.info(" Could not attach volume " + e.getMessage());
                        mv.setState(MachineVolume.State.ERROR);

                        continue;
                    }
                    if (j.getStatus() == Job.Status.FAILED) {
                        MachineManager.logger.info(" machineCreateContinuation : attachment failed ");
                        mv.setState(MachineVolume.State.ERROR);
                        continue;
                    }
                    List<CloudResource> affected = new ArrayList<CloudResource>();
                    affected.add(mv.getVolume());
                    MachineManager.logger.info("machineCreationContinuation create job for attachment ");
                    Job child = this.createJob(m, affected, "add", j.getStatus(), job);
                    child.setProviderAssignedId(j.getProviderAssignedId());
                    this.updateJob(child);
                    newJobs.add(child);
                    MachineManager.logger.info("machineCreationContinuation attachment job " + child.getId() + " "
                        + child.getStatus());
                }
            } else {
                /** volume has an error */
                mv.setState(MachineVolume.State.ERROR);
            }
        }

        this.em.flush();
        /** mark parent as completed if every child has completed */
        boolean done = true;
        List<Job> allChildren = job.getNestedJobs();

        Job.Status parentStatus = Job.Status.SUCCESS;
        for (Job j : allChildren) {
            MachineManager.logger.info("machineCreationContinuation check status of child id: " + j.getId() + " status "
                + j.getStatus());
            if (j.getStatus() == Job.Status.RUNNING) {
                done = false;
            } else if (j.getStatus() == Job.Status.FAILED) {
                parentStatus = Job.Status.FAILED;
            }
        }
        // TODO
        for (Job j : newJobs) {
            if (j.getStatus() == Job.Status.RUNNING) {
                done = false;
            } else if (j.getStatus() == Job.Status.FAILED) {
                parentStatus = Job.Status.FAILED;
            }
        }
        if (done == true) {
            /** all actions for machine create have terminated */
            MachineManager.logger.info("machineCreationContinuation all children terminated for " + job.getId());
            job.setStatus(parentStatus);
            this.updateJob(job);
        }
        this.em.flush();
        return true;
    }

    /**
     * Complete machine creation: job corresponds to leaf returned by connector
     * remote : machine retrieved from connector local : machine as persisted in
     * database
     */
    private boolean completeMachineCreation(final Job notification, final Machine local, final Machine remote) {
        MachineManager.logger.info("completeMachineCreation for machine " + local.getId() + " " + notification.getId() + " "
            + notification.getStatus());
        local.setCreated(new Date());
        this.createDisks(local, remote);
        this.createNetworkInterfaces(local, remote);
        local.setState(remote.getState());
        return true;
    }

    /**
     * Device (volume, disk, network) attachments to machine if creation == true
     * then attachment is part of machine create otherwise an explicit
     * attachment request (add machine volume) by user
     */
    private boolean completeDeviceManagement(final boolean creation, final Job notification, final Machine local,
        final Machine remote) {
        if (this.isDiskAdd(notification) == true) {
            /**
             * targetEntity = machine affectedEntity = MachineDisk
             */

            MachineManager.logger.info(" TODO : disk add to machine ");
        } else if (this.isNetworkInterfaceAdd(notification) == true) {
            MachineManager.logger.info(" TODO : networkInterface add to machine ");
        } else if (this.isVolumeAttach(notification) == true) {
            /**
             * targetEntity = machine affectedEntity = volume
             */
            MachineManager.logger.info(" Volume attachment to machine " + local.getId() + " job " + notification.getId()
                + " status " + notification.getStatus());
            MachineVolume mv = this.getMachineVolume(local, (Volume) notification.getAffectedEntities().get(0));
            if (mv == null) {
                MachineManager.logger.info(" could not find machine volume!! ");
                return true;
            }
            if (notification.getStatus() == Job.Status.SUCCESS) {
                MachineManager.logger.info(" Volume attachment succeeded for machine " + local.getId());
                mv.setState(MachineVolume.State.ATTACHED);
            } else if (notification.getStatus() == Job.Status.FAILED) {
                /** job failed */
                MachineManager.logger.info(" Volume attachment failed for machine " + local.getId());
                mv.setState(MachineVolume.State.ERROR);
                if (creation == false) {
                    this.removeMachineVolumeEntry(local, (Volume) notification.getAffectedEntities().get(0));
                }
            }
        } else if (this.isVolumeDetach(notification) == true) {
            MachineVolume mv = (MachineVolume) notification.getAffectedEntities().get(0);
            MachineManager.logger.info(" detached volume " + mv.getVolume().getId() + " from machine " + local.getId() + " "
                + notification.getStatus());
            if (notification.getStatus() == Job.Status.SUCCESS) {
                mv.setState(MachineVolume.State.DETACHED);
                mv.setVolume(null);
                // this.em.remove(mv);

            } else {
                MachineManager.logger.info("completeDeviceAttachmentToMachine ");
            }
        } else {
            MachineManager.logger
                .info("Unknown operation on machine " + local.getId() + " notified by " + notification.getId());
            return false;
        }
        return true;
    }

    public boolean jobCompletionHandler(final String notification_id) throws CloudProviderException {
        Job notification;

        try {
            notification = this.jobManager.getJobById(notification_id);
        } catch (ResourceNotFoundException e1) {
            MachineManager.logger.info("Could not find job " + notification_id);
            return false;
        } catch (CloudProviderException e1) {
            MachineManager.logger.info("unable to get job " + notification_id);
            return false;
        }

        Machine mpersisted = null;
        Integer mid = notification.getTargetEntity().getId();

        try {
            mpersisted = this.em.find(Machine.class, mid);
        } catch (Exception e) {
            MachineManager.logger.info("Could not find machine " + mid);
            return false;
        }

        Machine updated = null;
        if (!notification.getAction().equals("delete")) {
            updated = this.getMachineFromConnector(mpersisted.getCloudProviderAccount(), mpersisted.getLocation(),
                mpersisted.getProviderAssignedId());
            if (updated == null) {
                MachineManager.logger.info(" unable to get updated state of machine from connector"
                    + mpersisted.getProviderAssignedId());
                return false;
            }

            if (updated != null) {
                mpersisted.setState(updated.getState());
            }
        }
        String op = notification.getAction();
        /**
         * TODO: unify method to identify operation and related entities!
         */
        if (notification.getProperties().containsKey("parent-machine")) {
            /**
             * Non-leaf node for machine creation and deletion
             */
            if (op.equals("delete")) {
                MachineManager.logger.info("machine deleted ok " + mpersisted.getId());
                this.removeMachine(mpersisted);
            } else if (op.equals("add")) {

                List<Job> children = notification.getNestedJobs();
                if (children.size() != 0) {
                    /** parent non-leaf job of a machine create request */
                    MachineManager.logger.info(" notification for a non-leaf job " + notification.getId());
                    return this.machineCreationContinuation(notification, mpersisted);
                } else {
                    MachineManager.logger.info(" Why am I here if there are no children !! " + notification.getId());
                }
            } else {
                /** operations on a started machine */
                mpersisted.setUpdated(new Date());
            }
        } else {
            if (op.equals("add")) {
                /**
                 * Machine creation Device attachment/detachment operations
                 */
                // TODO check for all possible events
                List<CloudResource> affected = notification.getAffectedEntities();
                if (affected == null || affected.size() == 0) {
                    /** machine creation leaf job: no affectedEntity */
                    MachineManager.logger.info("completeMachineCreation notification for machine " + notification.getId());
                    return this.completeMachineCreation(notification, mpersisted, updated);
                } else {
                    /**
                     * machine volume attachment or detachment leaf job as part
                     * of machine create
                     */
                    MachineManager.logger
                        .info("completeMachineCreation complete device management " + notification.getStatus());
                    return this.completeDeviceManagement(true, notification, mpersisted, updated);
                }
            } else if (op.equals("edit")) {
                mpersisted.setCpu(updated.getCpu());
                mpersisted.setMemory(updated.getMemory());
                mpersisted.setUpdated(new Date());
            } else if (op.equals("delete")) {
                /** machine volume attachment or detachment leaf job */
                MachineManager.logger.info("completeMachineCreation complete device management (delete ) "
                    + notification.getStatus());
                return this.completeDeviceManagement(true, notification, mpersisted, updated);
            } else {
                MachineManager.logger.info("unexpected notification " + notification.getId() + " on machine "
                    + mpersisted.getId());
            }
        }
        return true;
    }

    private void validateCredentials(final Credentials cred) throws CloudProviderException {

        // if (cred.getKey().length == 0) {
        // throw new
        // CloudProviderException("Incorrect credentials key length ");
        // }

    }

    private void validateNetworkInterface(final List<MachineTemplateNetworkInterface> nics) throws CloudProviderException {

    }

    private Object getObjectFromEM(final Class targetClass, final String id) throws InvalidRequestException,
        ResourceNotFoundException {
        if (id == null) {
            throw new InvalidRequestException(" null resource id");
        }
        Object o = this.em.find(targetClass, Integer.valueOf(id));
        if (o == null) {
            throw new ResourceNotFoundException(" Invalid id " + id);
        }
        return o;
    }

    public List<MachineVolume> getMachineVolumes(final String machineId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        Machine m = this.getMachineById(machineId);

        List<MachineVolume> volColl = m.getVolumes();
        if (volColl != null) {
            volColl.size();
        }
        List<MachineVolume> completed = new ArrayList<MachineVolume>();
        for (MachineVolume mv : volColl) {

            if (mv.getState() == MachineVolume.State.ATTACHED) {
                completed.add(mv);
            }
        }
        return completed;
    }

    private Job addVolumeToMachine(final Machine m, final MachineVolume mv) throws ServiceUnavailableException {
        /**
         * Invoke the connector to add volume to machine
         */
        ICloudProviderConnector connector = null;
        try {
            connector = this.getConnector(m);
        } catch (Exception e) {
            throw new ServiceUnavailableException(" " + e.getMessage() + " getting connector to add volume to machine "
                + m.getId() + " " + m.getProviderAssignedId());
        }
        IComputeService computeService;

        try {
            computeService = connector.getComputeService();
        } catch (ConnectorException e) {
            String eee = e.getMessage();
            throw new ServiceUnavailableException(" " + eee + " getting compute service to add volume to machine " + m.getId()
                + " " + m.getProviderAssignedId());
        }

        /**
         * action = addVolume targetEntity = machine affectedEntity = volume
         */
        Job j = null;
        mv.setState(MachineVolume.State.ATTACHING);
        try {
            j = computeService.addVolumeToMachine(m.getProviderAssignedId(), mv);
        } catch (ConnectorException e) {
            throw new ServiceUnavailableException(e.getMessage() + " in add volume to machine " + m.getId());
        }
        if (j.getStatus() != Job.Status.FAILED) {
            try {
                UtilsForManagers.emitJobListenerMessage(j.getProviderAssignedId(), this.ctx);
            } catch (Exception e) {
                throw new ServiceUnavailableException(e.getMessage());
            }
        }
        MachineManager.logger.info("addVolumeToMachine " + m.getId() + " " + mv.getVolume().getId() + " " + j.getId() + " "
            + j.getStatus());
        return j;
    }

    private Job addVolumeToMachine(final Machine m, final String volumeId, final String initialLocation)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException {

        MachineManager.logger.info("Adding volume when machine state is  " + m.getState());
        /**
         * Allow operation only in STARTED or STOPPED states.
         */
        if ((m.getState() != Machine.State.STARTED) && (m.getState() != Machine.State.STOPPED)) {
            throw new InvalidRequestException("Can add volume only in started or stopped state " + m.getState());
        }

        MachineManager.logger.info(" Check that volume exists " + volumeId);
        Volume volume = this.volumeManager.getVolumeById(volumeId);

        MachineVolume mv = new MachineVolume();
        mv.setVolume(volume);
        mv.setInitialLocation(initialLocation);
        mv.setState(MachineVolume.State.PENDING);

        Job j = this.addVolumeToMachine(m, mv);

        if (j.getStatus() == Job.Status.FAILED) {
            MachineManager.logger.info("Attach of volume failed ");
        } else {
            MachineManager.logger.info(" Attached volume " + volumeId + " to machine " + m.getId());
            this.em.persist(mv);
            m.addMachineVolume(mv);
            this.em.flush();
        }
        // TODO check
        List<CloudResource> affected = new ArrayList<CloudResource>();
        affected.add(mv.getVolume());

        Job persisted = this.createJob(m, affected, "add", j.getStatus(), null);
        persisted.setProviderAssignedId(j.getProviderAssignedId());
        this.updateJob(persisted);
        MachineManager.logger.info(" Add volume " + volumeId + " to machine " + m.getId() + " job state "
            + persisted.getStatus());
        return persisted;
    }

    public Job addVolumeToMachine(final String machineId, final MachineVolume machineVolume) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        Volume volume = machineVolume.getVolume();
        if ((machineId == null) || (volume == null) || (machineVolume.getInitialLocation() == null)) {
            throw new InvalidRequestException(" null arguments ");
        }
        MachineManager.logger.info(" Add volume " + machineVolume.getVolume().getId() + " to machine " + machineId);
        if (this.volumeInUse(volume) == true) {
            throw new InvalidRequestException(" volume " + volume.getId() + " is already in use");
        }

        Machine m = this.getMachineFromId(machineId);
        return this.addVolumeToMachine(m, volume.getId().toString(), machineVolume.getInitialLocation());
    }

    public Job removeVolumeFromMachine(final String machineId, final String mvId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        if ((machineId == null) || (mvId == null)) {
            throw new InvalidRequestException(" null arguments ");
        }
        Machine m = this.getMachineFromId(machineId);
        List<MachineVolume> volColl = m.getVolumes();

        if ((volColl == null) || (volColl.size() == 0)) {
            throw new CloudProviderException(" No machine volume collection for " + m.getId());
        }
        MachineVolume mv = (MachineVolume) this.getObjectFromEM(MachineVolume.class, mvId);

        volColl.size();
        if (volColl.contains(mv) == false) {
            throw new InvalidRequestException(" removing invalid machine volume " + mvId + " from machine  " + machineId);
        }

        /**
         * Invoke the connector to add volume to machine
         */
        ICloudProviderConnector connector = this.getConnector(m);
        IComputeService computeService;

        try {
            computeService = connector.getComputeService();
        } catch (ConnectorException e) {
            String eee = e.getMessage();
            throw new ServiceUnavailableException(" " + eee + " adding volume to machine " + m.getId() + " "
                + m.getProviderAssignedId());
        }

        /**
         * action = addVolume targetEntity = machine affectedEntity = volume
         * and/or machinevolume
         */
        Job j = null;
        mv.setState(MachineVolume.State.DETACHING);
        try {
            j = computeService.removeVolumeFromMachine(m.getProviderAssignedId(), mv);
        } catch (ConnectorException e) {
            throw new ServiceUnavailableException(e.getMessage() + " in remove volume from machine " + m.getId());
        }
        if (j == null) {
            MachineManager.logger.info("REMOVE THIS CHECK ");
            throw new ServiceUnavailableException(" in remove volume from machine " + m.getId());
        }
        if (j.getStatus() == Job.Status.FAILED) {
            throw new CloudProviderException("Could not remove volume to machine " + m.getId());
        }
        MachineManager.logger.info("removeVolumeFromMachine " + machineId + " volume " + mvId + " job status " + j.getStatus());
        try {
            UtilsForManagers.emitJobListenerMessage(j.getProviderAssignedId(), this.ctx);
        } catch (Exception e) {
            throw new ServiceUnavailableException(e.getMessage());
        }
        List<CloudResource> affectedEntities = new ArrayList<CloudResource>();
        affectedEntities.add(mv);

        Job persisted = this.createJob(m, affectedEntities, "delete", j.getStatus(), null);
        persisted.setProviderAssignedId(j.getProviderAssignedId());
        this.updateJob(persisted);
        return persisted;
    }

    public List<MachineVolumeTemplate> getMachineVolumeTemplates(final String mtId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        MachineTemplate mt = (MachineTemplate) this.getObjectFromEM(MachineTemplate.class, mtId);
        List<MachineVolumeTemplate> volTemplateColl = mt.getVolumeTemplates();

        if (volTemplateColl != null) {
            volTemplateColl.size();
        }
        return volTemplateColl;
    }

    private void addVolumeToMachineTemplate(final MachineTemplate mt, final String volumeId, final String initialLocation)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {

        Volume volume = this.volumeManager.getVolumeById(volumeId);
        MachineVolume mv = new MachineVolume();

        mv.setVolume(volume);
        mv.setInitialLocation(initialLocation);
        mt.addMachineVolume(mv);

        this.em.persist(mv);
        this.em.flush();
    }

    public void addVolumeToMachineTemplate(final String mtId, final String volumeId, final String initialLocation)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException {
        if ((mtId == null) || (volumeId == null) || (initialLocation == null)) {
            throw new InvalidRequestException(" null arguments ");
        }
        MachineTemplate mt = (MachineTemplate) this.getObjectFromEM(MachineTemplate.class, mtId);
        this.addVolumeToMachineTemplate(mt, volumeId, initialLocation);
    }

    private void addVolumeTemplateToMachineTemplate(final MachineTemplate mt, final String vtId, final String initialLocation)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {

        VolumeTemplate vt = this.volumeManager.getVolumeTemplateById(vtId);

        MachineVolumeTemplate mvt = new MachineVolumeTemplate();

        mvt.setVolumeTemplate(vt);
        mvt.setInitialLocation(initialLocation);
        mt.addMachineVolumeTemplate(mvt);

        this.em.persist(mvt);
        this.em.flush();
    }

    public void addVolumeTemplateToMachineTemplate(final String mtId, final String vtId, final String initialLocation)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException {
        if ((mtId == null) || (vtId == null) || (initialLocation == null)) {
            throw new InvalidRequestException(" null argument ");
        }

        MachineTemplate mt = (MachineTemplate) this.getObjectFromEM(MachineTemplate.class, mtId);

        this.addVolumeTemplateToMachineTemplate(mt, vtId, initialLocation);
    }

    public void removeVolumeFromMachineTemplate(final String mtId, final String mvId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {
        if ((mtId == null) || (mvId == null)) {
            throw new InvalidRequestException(" null argument ");
        }
        MachineTemplate mt = (MachineTemplate) this.getObjectFromEM(MachineTemplate.class, mtId);
        List<MachineVolume> vColl = mt.getVolumes();
        if (vColl == null) {
            throw new CloudProviderException("Error: volume collection for " + mtId + " is empty ");
        }
        vColl.size();
        MachineVolume mv = (MachineVolume) this.getObjectFromEM(MachineVolume.class, mvId);

        if (vColl.contains(mv) == false) {
            throw new InvalidRequestException(" removing invalid machine volume " + mvId + " from machine template " + mtId);
        }

        mt.removeMachineVolume(null);
        mv.setVolume(null);
        this.em.remove(mv);
        this.em.flush();
    }

    public void removeVolumeTemplateFromMachineTemplate(final String mtId, final String mvtId)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException {
        if ((mtId == null) || (mvtId == null)) {
            throw new InvalidRequestException(" null argument ");
        }
        MachineTemplate mt = (MachineTemplate) this.getObjectFromEM(MachineTemplate.class, mtId);
        List<MachineVolumeTemplate> vtColl = mt.getVolumeTemplates();
        if (vtColl == null) {
            throw new CloudProviderException("Error: volume template collection for " + mtId + " is empty ");
        }

        vtColl.size();
        MachineVolumeTemplate mvt = (MachineVolumeTemplate) this.getObjectFromEM(MachineVolumeTemplate.class, mvtId);

        if (vtColl.contains(mvt) == false) {
            throw new InvalidRequestException(" removing invalid machine volume template " + mvtId + " from machine template "
                + mtId);
        }

        mt.removeMachineVolumeTemplate(mvt);
        mvt.setVolumeTemplate(null);
        this.em.remove(mvt);
        this.em.flush();
    }

    @Override
    public Job updateVolumeOnMachine(final String machineId, final MachineVolume mVol) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {
        throw new ServiceUnavailableException(" Operation not permitted ");
    }

    @Override
    public MachineVolume getVolumeFromMachine(final String machineId, final String mVolId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        Machine m = this.getMachineById(machineId);
        List<MachineVolume> volumes = m.getVolumes();

        if (volumes != null) {

            volumes.size();
            for (MachineVolume v : volumes) {
                if (v.getId().toString().equals(mVolId)) {
                    return v;
                }
            }
        }
        throw new ResourceNotFoundException(" Volume  " + mVolId + " not found for machine " + machineId);
    }

    @Override
    public Job addDiskToMachine(final String machineId, final MachineDisk disk) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {
        throw new ServiceUnavailableException(" Operation not permitted ");
    }

    @Override
    public List<MachineDisk> getMachineDisks(final String machineId) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException {
        Machine m = this.getMachineById(machineId);

        List<MachineDisk> diskColl = m.getDisks();
        if (diskColl != null) {
            diskColl.size();
        }
        return diskColl;
    }

    @Override
    public Job removeDiskFromMachine(final String machineId, final String machineDiskId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {
        throw new ServiceUnavailableException(" Operation not permitted ");
    }

    @Override
    public MachineDisk getDiskFromMachine(final String machineId, final String machineDiskId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        Machine m = this.getMachineById(machineId);
        List<MachineDisk> disks = m.getDisks();

        if (disks != null) {

            disks.size();
            for (MachineDisk disk : disks) {
                if (disk.getId().toString().equals(machineDiskId)) {
                    return disk;
                }
            }
        }
        throw new ResourceNotFoundException(" Disk  " + machineDiskId + " not found for machine " + machineId);
    }

    @Override
    public Job updateDiskInMachine(final String machineId, final MachineDisk disk) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {
        throw new ServiceUnavailableException(" Operation not permitted ");
    }

    @Override
    public Job addNetworkInterfaceToMachine(final String machineId, final MachineNetworkInterface nic)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException {
        // TODO Auto-generated method stub
        throw new ServiceUnavailableException(" Operation not permitted ");
    }

    @Override
    public Job removeNetworkInterfaceFromMachine(final String machineId, final String nicId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {
        // TODO Auto-generated method stub
        throw new ServiceUnavailableException(" Operation not permitted ");
    }

    @Override
    public Job updateNetworkInterfaceInMachine(final String machineId, final MachineNetworkInterface nic)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException {
        // TODO Auto-generated method stub
        throw new ServiceUnavailableException(" Operation not permitted ");
    }

    @Override
    public MachineNetworkInterface getNetworkInterfaceFromMachine(final String machineId, final String nicId)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException {
        // TODO Auto-generated method stub

        Machine m = this.getMachineById(machineId);
        List<MachineNetworkInterface> nics = m.getNetworkInterfaces();

        if (nics != null) {

            nics.size();
            for (MachineNetworkInterface nic : nics) {
                if (nic.getId().toString().equals(nicId)) {
                    return nic;
                }
            }
        }
        throw new ResourceNotFoundException(" Disk  " + nicId + " not found for machine " + machineId);
    }

    @Override
    public List<MachineNetworkInterface> getMachineNetworkInterfaces(final String machineId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {
        // TODO Auto-generated method stub
        return null;
    }

}