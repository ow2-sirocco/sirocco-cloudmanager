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
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager.Placement;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.BadStateException;
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
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
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
    private ICloudProviderManager cloudProviderManager;

    @EJB
    private IVolumeManager volumeManager;

    @EJB
    private ISystemManager systemManager;

    @EJB
    private INetworkManager networkManager;

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

    private boolean checkQuota(final User u, final MachineConfiguration mc) {
        return true;
    }

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
        try {
            return connectorFactory.getCloudProviderConnector(account, location);
        } catch (ConnectorException e) {
            throw new CloudProviderException(e.getMessage());
        }
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
        if (volumes != null && volumes.size() != 0) {
            for (MachineVolume mv : volumes) {
                if (mv.getInitialLocation() == null) {
                    throw new InvalidRequestException("initialLocation not set for volume ");
                }
                Volume v = mv.getVolume();
                /**
                 * Volume should not be passed by value. Check that the volume
                 * id exists.
                 */
                if ((v == null) || (v.getId() == null)) {
                    throw new InvalidRequestException("No volume id ");
                }
                this.volumeManager.getVolumeById(v.getId().toString());
            }
        }
        List<MachineVolumeTemplate> vts = mt.getVolumeTemplates();
        if (vts == null) {
            return;
        }
        if (vts.size() == 0) {
            return;
        }
        for (MachineVolumeTemplate mvt : vts) {
            if (mvt.getInitialLocation() == null) {
                throw new InvalidRequestException("initialLocation not set for volume template");
            }
            VolumeTemplate vt = mvt.getVolumeTemplate();
            if (vt == null || vt.getId() == null) {
                throw new InvalidRequestException("Unknown volume template ");
            }
            this.volumeManager.getVolumeTemplateById(vt.getId().toString());
        }
    }

    private void validateMachineImage(final MachineTemplate mt, final User u) throws InvalidRequestException {
        MachineImage mi = mt.getMachineImage();
        if ((mi == null) || (mi.getId() == null)) {
            throw new InvalidRequestException(" MachineImage should be set");
        }

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
            volumeCreate.setProperties(new HashMap<String, String>());
            volumeCreate.getProperties().put("provider", m.getCloudProviderAccount().getCloudProvider().getCloudProviderType());
            if (m.getLocation() != null) {
                volumeCreate.getProperties().put("location", m.getLocation().getCountryName());
            }
            // TODO
            volumeCreate.setName("internal");
            volumeCreate.setVolumeTemplate(mvt.getVolumeTemplate());
            try {
                connJob = this.volumeManager.createVolume(volumeCreate);
            } catch (CloudProviderException e) {
                MachineManager.logger.info(" Error in creating volume from template " + mvt.getVolumeTemplate().getId());
            }

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

    private boolean volumeShareable(final Volume volume) {
        Query q = this.em.createQuery("FROM MachineVolume v WHERE v.volume.id=:vid");
        q.setParameter("vid", volume.getId());
        List<MachineVolume> list = q.getResultList();
        for (MachineVolume mv : list) {
            if ((mv.getState() == MachineVolume.State.ATTACHED) || (mv.getState() == MachineVolume.State.ATTACHING)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Attach volumes to new machine
     */
    private void prepareAttachVolumes(final Machine m, final List<MachineVolume> volumes) {
        for (MachineVolume mvsrc : volumes) {
            Volume v = mvsrc.getVolume();
            if (v == null) { // paranoia
                continue;
            }
            if (this.volumeShareable(v) == false) {
                continue;
            }
            MachineVolume mv = new MachineVolume();
            mv.setVolume(v);
            mv.setInitialLocation(mvsrc.getInitialLocation());
            mv.setState(MachineVolume.State.PENDING);
            this.em.persist(mv);
            m.addMachineVolume(mv);
            this.em.flush();
        }
    }

    public Job createMachine(final MachineCreate machineCreate) throws CloudProviderException {

        MachineTemplate mt = machineCreate.getMachineTemplate();

        this.validateCreationParameters(mt, this.getUser());

        if (this.checkQuota(this.getUser(), mt.getMachineConfiguration()) == false) {
            throw new CloudProviderException("User exceeded quota ");
        }

        Placement placement = this.cloudProviderManager.placeResource(machineCreate.getProperties());
        ICloudProviderConnector connector = this.getCloudProviderConnector(placement.getAccount(), placement.getLocation());
        if (connector == null) {
            throw new CloudProviderException("Cannot retrieve cloud provider connector "
                + placement.getAccount().getCloudProvider().getCloudProviderType());
        }

        Job jobCreateMachine = null;
        IComputeService computeService = null;

        try {
            computeService = connector.getComputeService();
            jobCreateMachine = computeService.createMachine(machineCreate);
        } catch (Exception e) {
            throw new CloudProviderException(e.getMessage());
        }

        if (jobCreateMachine.getStatus() == Job.Status.FAILED) {
            throw new ServiceUnavailableException("Machine creation failed ");
        }

        Machine m = new Machine();

        m.setName(machineCreate.getName());
        m.setDescription(machineCreate.getDescription());
        m.setProperties(machineCreate.getProperties() == null ? new HashMap<String, String>() : new HashMap<String, String>(
            machineCreate.getProperties()));

        m.setState(Machine.State.CREATING);
        m.setUser(this.getUser());
        m.setCpu(mt.getMachineConfiguration().getCpu());
        m.setMemory(mt.getMachineConfiguration().getMemory());

        m.setCloudProviderAccount(placement.getAccount());
        m.setProviderAssignedId(jobCreateMachine.getTargetEntity().getProviderAssignedId());

        m.setLocation(placement.getLocation());
        m.setCreated(new Date());
        this.em.persist(m);

        MachineManager.logger.info("New machine id " + m.getId().toString());

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
            this.prepareAttachVolumes(m, vol);
        }
        MachineManager.logger.info("Return Job of new machine creation " + j.getId().toString());

        return j;
    }

    private void readMachineAttributes(final Machine m) {

        if (m.getDisks() != null) {
            m.getDisks().size();
        }
        if (m.getNetworkInterfaces() != null) {
            m.getNetworkInterfaces().size();
        }
        if (m.getVolumes() != null) {
            m.getVolumes().size();
        }
    }

    @Override
    public QueryResult<Machine> getMachines(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws CloudProviderException {

        QueryResult<Machine> result = UtilsForManagers.getEntityList("Machine", this.em, this.getUser().getUsername(), first,
            last, filters, attributes, true);

        for (Machine machine : result.getItems()) {
            this.readMachineAttributes(machine);
        }
        return result;
    }

    @Override
    public List<Machine> getMachines() throws CloudProviderException {
        return UtilsForManagers.getEntityList("Machine", this.em, this.getUser().getUsername());
    }

    private Machine checkOps(final String machineId, final String action) throws CloudProviderException {
        Machine m = null;

        if (machineId == null) {
            throw new InvalidRequestException("Null machine id ");
        }
        m = this.em.find(Machine.class, Integer.valueOf(machineId));

        if (m == null) {
            throw new ResourceNotFoundException("Machine " + machineId + " not found");
        }
        Set<String> actions = m.getOperations();
        if (actions.contains(action) == false) {
            throw new BadStateException(" Cannot " + action + "  machine at state " + m.getState());
        }

        return m;
    }

    public Job startMachine(final String machineId) throws CloudProviderException {
        return this.doService(machineId, "start");
    }

    public Job stopMachine(final String machineId) throws CloudProviderException {
        return this.doService(machineId, "stop");
    }

    public Job suspendMachine(final String machineId) throws CloudProviderException {
        return this.doService(machineId, "suspend");
    }

    public Job restartMachine(final String machineId, final boolean force) throws CloudProviderException {

        return this.doService(machineId, "restart", force);
    }

    public Job pauseMachine(final String machineId) throws CloudProviderException {
        return this.doService(machineId, "pause");
    }

    @Override
    public Job startMachine(final String machineId, final Map<String, String> properties) throws ResourceNotFoundException,
        CloudProviderException {
        return this.doService(machineId, "start");
    }

    @Override
    public Job stopMachine(final String machineId, final boolean force, final Map<String, String> properties)
        throws ResourceNotFoundException, CloudProviderException {
        return this.doService(machineId, "stop", force);
    }

    @Override
    public Job restartMachine(final String machineId, final boolean force, final Map<String, String> properties)
        throws ResourceNotFoundException, CloudProviderException {
        return this.doService(machineId, "restart", force);
    }

    @Override
    public Job pauseMachine(final String machineId, final Map<String, String> properties) throws ResourceNotFoundException,
        CloudProviderException {
        return this.doService(machineId, "pause");
    }

    @Override
    public Job suspendMachine(final String machineId, final Map<String, String> properties) throws ResourceNotFoundException,
        CloudProviderException {
        return this.doService(machineId, "suspend");
    }

    private Job doService(final String machineId, final String action, final Object... params) throws CloudProviderException {

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
                boolean force = (params.length > 0 && params[0] instanceof Boolean) ? ((Boolean) params[0]) : false;
                j = computeService.stopMachine(m.getProviderAssignedId(), force);
                m.setState(Machine.State.STOPPING);
            } else if (action.equals("suspend")) {
                j = computeService.suspendMachine(m.getProviderAssignedId());
                m.setState(Machine.State.SUSPENDING);
            } else if (action.equals("pause")) {
                j = computeService.pauseMachine(m.getProviderAssignedId());
                m.setState(Machine.State.PAUSING);
            } else if (action.equals("restart")) {
                boolean force = (params.length > 0 && params[0] instanceof Boolean) ? ((Boolean) params[0]) : false;
                j = computeService.restartMachine(m.getProviderAssignedId(), force);
            } else {
                throw new ServiceUnavailableException("Unsupported operation action " + action + " on machine id "
                    + m.getProviderAssignedId() + " " + m.getId());
            }
        } catch (org.ow2.sirocco.cloudmanager.connector.api.BadStateException e) {
            throw new BadStateException(e.getMessage() + " action " + action + " machine id " + m.getProviderAssignedId() + " "
                + m.getId());
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
        job.setDescription("Machine " + action);

        if (j.getStatus() != Job.Status.FAILED) {
            try {
                UtilsForManagers.emitJobListenerMessage(job.getProviderAssignedId(), this.ctx);
            } catch (Exception e) {
                throw new ServiceUnavailableException(e.getMessage() + "  " + action);
            }
        }
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

        m.setState(Machine.State.DELETING);

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
            Machine.State s = m.getState();
            try {
                s = computeService.getMachineState(m.getProviderAssignedId());
            } catch (ConnectorException e) {
            }
            m.setState(s);
        }

        Job job = this.createJob(m, null, "delete", j.getStatus(), null);
        job.setDescription("Machine deletion");
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
     * MachineConfiguration
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
    public QueryResult<MachineConfiguration> getMachineConfigurations(final int first, final int last,
        final List<String> filters, final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        QueryResult<MachineConfiguration> machineConfigs = UtilsForManagers.getEntityList("MachineConfiguration", this.em,
            user.getUsername(), first, last, filters, attributes, false);
        for (MachineConfiguration machineConfig : machineConfigs.getItems()) {
            machineConfig.getDiskTemplates().size();
        }
        return machineConfigs;
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
            VolumeTemplate vt = mvt.getVolumeTemplate();
            if ((vt == null) || (vt.getId() == null)) {
                continue;
            }
            try {
                this.getObjectFromEM(VolumeTemplate.class, vt.getId().toString());
            } catch (CloudProviderException e) {
                MachineManager.logger.info(" Incorrect volume template being attached to machine template " + vt.getId()
                    + " ignoring ");
                continue;
            }
            this.em.persist(mvt);
        }
    }

    private void createVolumeCollectionForMt(final MachineTemplate mt) throws CloudProviderException {

        List<MachineVolume> volumes = mt.getVolumes();
        if (volumes == null || volumes.size() == 0) {
            return;
        }
        for (MachineVolume mv : volumes) {
            Volume v = mv.getVolume();
            if (v == null || v.getId() == null) {
                continue;
            }
            try {
                this.em.find(Volume.class, v.getId());
            } catch (Exception e) {
                MachineManager.logger.info(" Incorrect volume being attached to machine template " + v.getId() + " ignoring ");
                continue;
            }
            this.em.persist(mv);
        }
    }

    private void createNetworkInterfaces(final MachineTemplate mt) throws CloudProviderException {
        List<MachineTemplateNetworkInterface> nics = mt.getNetworkInterfaces();
        if (nics == null) {
            return;
        }
        for (MachineTemplateNetworkInterface nic : nics) {
            List<Address> addresses = nic.getAddresses();
            if (addresses != null) {
                for (Address addr : addresses) {
                    if (addr.getId() == null) {
                        boolean verify = addr.validate();
                        if (verify != true) {
                            throw new CloudProviderException(" Invalid address values ");
                        }
                        this.em.persist(addr);
                    } else {
                        throw new CloudProviderException(" Reusing address object for new machine template " + addr.getId());
                    }
                }
            }
            this.em.persist(nic);
        }
        this.em.flush();
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
    public QueryResult<MachineTemplate> getMachineTemplates(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        return UtilsForManagers.getEntityList("MachineTemplate", this.em, this.getUser().getUsername(), first, last, filters,
            attributes, false);
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

    /**
     * To complete:
     */
    private void validateMachineConfiguration(final MachineConfiguration mc) throws CloudProviderException {

        if (mc.getCpu() == null) {
            throw new InvalidRequestException(" Cpu attribute should be set");
        }

        if (mc.getCpu() < 0) {
            throw new InvalidRequestException("Incorrect MachineConfiguration ");
        }
        if (mc.getCpu() < 0) {
            throw new InvalidRequestException("Incorrect MachineConfiguration ");
        }

        if (mc.getMemory() == null) {
            throw new InvalidRequestException(" Memory attribute should be set");
        }

        if (mc.getMemory() < 0) {
            throw new InvalidRequestException("Incorrect MachineConfiguration ");
        }

        List<DiskTemplate> disks = mc.getDiskTemplates();
        if (disks == null || disks.size() == 0) {
            return;
        }

        for (DiskTemplate d : disks) {
            if (d.getCapacity() < 0) {
                throw new InvalidRequestException("Incorrect MachineConfiguration ");
            }
            if (d.getFormat() == null) {
                throw new InvalidRequestException("Incorrect MachineConfiguration format should be set ");
            }
        }
    }

    private void removeMachine(final Machine deleted, final boolean fromSystem) {
        MachineManager.logger.info(" deleting machine " + deleted.getId() + " fromSystem " + fromSystem);

        deleted.setCloudProviderAccount(null);

        List<MachineVolume> volColl = deleted.getVolumes();
        List<MachineDisk> diskColl = deleted.getDisks();
        List<MachineNetworkInterface> nics = deleted.getNetworkInterfaces();
        deleted.setVolumes(null);
        deleted.setDisks(null);
        deleted.setNetworkInterfaces(null);

        if (volColl != null) {
            for (MachineVolume mv : volColl) {
                mv.setVolume(null);
                mv.setOwner(null);
                this.em.remove(mv);
            }
        }
        if (diskColl != null) {
            for (MachineDisk disk : diskColl) {
                this.em.remove(disk);
            }
        }

        if (nics != null) {
            for (MachineNetworkInterface nic : nics) {
                List<MachineNetworkInterfaceAddress> addrs = nic.getAddresses();
                for (MachineNetworkInterfaceAddress addr : addrs) {
                    Address address = addr.getAddress();
                    if (address != null && address.getAllocation().equals("static")) {
                        addr.setAddress(null);
                    }
                }
                nic.setNetwork(null);
                nic.setNetworkPort(null);
                this.em.remove(nic);
            }
        }
        deleted.setState(State.DELETED);
        this.em.flush();
        this.systemManager.handleEntityStateChange(deleted.getClass(), deleted.getId().toString(), true);

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
        MachineManager.logger.info("createNetworkInterfaces " + persisted.getId());
        List<MachineNetworkInterface> nics = created.getNetworkInterfaces();
        if (nics == null) {
            return;
        }
        MachineManager.logger.info("createNetworkInterfaces machine " + persisted.getId() + " has nics " + nics.size());
        for (MachineNetworkInterface nic : nics) {
            // TODO
            nic.setNetwork(null);
            nic.setNetworkPort(null);

            List<MachineNetworkInterfaceAddress> entries = nic.getAddresses();
            if (entries != null) {
                MachineManager.logger.info(" createNetworkInterfaces has addresses " + entries.size());
                for (MachineNetworkInterfaceAddress entry : entries) {
                    if (entry.getAddress() != null) {
                        entry.getAddress().setNetwork(null);
                        entry.getAddress().setResource(null);
                        MachineManager.logger.info(" createNetworkInterfaces: new addr IP " + entry.getAddress().getIp());
                    }
                }
            }
            MachineManager.logger.info("createNetworkInterfaces persist nic ");
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
     * targetEntity == machine, affectedEntities[0] == volume
     */
    private boolean isVolumeAttach(final Job job) {

        if ((job.getTargetEntity() instanceof Machine) && (job.getAffectedEntities() != null)
            && job.getAffectedEntities().size() != 0 && (job.getAffectedEntities().get(0) instanceof Volume)) {
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

    /** remove corresponding machine volume entry for machine */
    private void removeMachineVolumeEntry(final Machine m, final Volume v) {
        List<MachineVolume> items = m.getVolumes();
        if (items == null) {
            return;
        }
        for (MachineVolume mv : items) {
            if (mv.getVolume() != null && mv.getVolume().getId().equals(v.getId())) {
                mv.setVolume(null);
                m.removeMachineVolume(mv);
                this.em.remove(mv);
                break;
            }
        }
        this.em.flush();
    }

    private MachineVolume getMachineVolume(final Machine m, final Volume v) {
        List<MachineVolume> list = m.getVolumes();
        if (list == null) {
            return null;
        }
        for (MachineVolume mv : list) {
            if (mv.getVolume() != null && (mv.getVolume().getId().equals(v.getId()))) {
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
            return true;
        }
        /**
         * Attach created volumes
         */
        List<MachineVolume> mvs = m.getVolumes();
        ArrayList<Job> newJobs = new ArrayList<Job>();
        if (mvs == null) {
            MachineManager.logger.info("machineCreationContinuation  no machine volume for " + m.getId());
        } else {
            MachineManager.logger.info("machineCreationContinuation  has machine volumes " + mvs.size());
            for (MachineVolume mv : mvs) {
                String vid = "nullvolume";
                Volume.State vs = Volume.State.ERROR;
                if (mv.getVolume() != null) {
                    vid = mv.getVolume().getId().toString();
                    vs = mv.getVolume().getState();
                }
                MachineManager.logger.info("machineCreationContinuation : " + vid + " machine volume state " + mv.getState()
                    + " volume state " + vs);
            }
        }
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
                    child.setDescription("Volume attachment");
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
                MachineManager.logger.info("");
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
    private boolean completeDeviceManagement(final boolean machineCreation, final Job notification, final Machine local,
        final Machine remote) {
        if (this.isDiskAdd(notification) == true) {
            /**
             * targetEntity = machine affectedEntity = MachineDisk
             */

            MachineManager.logger.info(" TODO : disk add to machine " + local.getId());
        } else if (this.isNetworkInterfaceAdd(notification) == true) {
            MachineManager.logger.info(" TODO : networkInterface add to machine " + local.getId());
        } else if (this.isVolumeAttach(notification) == true) {
            /**
             * targetEntity = machine affectedEntity = volume
             */
            MachineManager.logger.info(" Volume attachment to machine " + local.getId() + " job " + notification.getId()
                + " status " + notification.getStatus());
            MachineVolume mv = this.getMachineVolume(local, (Volume) notification.getAffectedEntities().get(0));
            if (mv == null) {
                MachineManager.logger.info(" could not find machine volume!! " + local.getId());
                return true;
            }
            if (notification.getStatus() == Job.Status.SUCCESS) {
                MachineManager.logger.info(" Volume attachment succeeded for machine " + local.getId());
                mv.setState(MachineVolume.State.ATTACHED);
            } else if (notification.getStatus() == Job.Status.FAILED) {
                /** job failed */
                MachineManager.logger.info(" Volume attachment failed for machine " + local.getId());
                mv.setState(MachineVolume.State.ERROR);
                if (machineCreation == false) {
                    this.removeMachineVolumeEntry(local, (Volume) notification.getAffectedEntities().get(0));
                }
            }
        } else if (this.isVolumeDetach(notification) == true) {
            MachineVolume mv = (MachineVolume) notification.getAffectedEntities().get(0);
            MachineManager.logger.info(" detached volume " + mv.getVolume().getId() + " from machine " + local.getId() + " "
                + notification.getStatus());
            if (notification.getStatus() == Job.Status.SUCCESS) {
                notification.getAffectedEntities().remove(0);
                this.removeMachineVolumeEntry(local, mv.getVolume());
            } else {
                MachineManager.logger.info("completeDeviceAttachmentToMachine attach failed " + notification.getStatus());
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
                this.systemManager.handleEntityStateChange(mpersisted.getClass(), mpersisted.getId().toString(), false);
            }
        }
        String op = notification.getAction();

        if (notification.getProperties().containsKey("parent-machine")) {
            /**
             * Non-leaf node for machine creation and deletion
             */
            if (op.equals("delete")) {
                MachineManager.logger.info("machine deleted ok " + mpersisted.getId());
                this.removeMachine(mpersisted, false);
            } else if (op.equals("add")) {
                List<Job> children = notification.getNestedJobs();
                if (children != null && children.size() != 0) {
                    /** parent non-leaf job of a machine create request */
                    MachineManager.logger.info(" notification for a non-leaf job " + notification.getId());
                    return this.machineCreationContinuation(notification, mpersisted);
                } else {
                    MachineManager.logger.info(" Why am I here if there are no children !! " + notification.getId());
                }
            } else {
                /** machine life-cycle operations */
                mpersisted.setUpdated(new Date());
            }
        } else {
            if (op.equals("add")) {
                /**
                 * Machine creation Device attachment/detachment operations
                 */
                List<CloudResource> affected = notification.getAffectedEntities();
                if (affected == null || affected.size() == 0) {
                    /** machine creation leaf job: no affectedEntity */
                    MachineManager.logger.info("completeMachineCreation notification for machine " + notification.getId());
                    return this.completeMachineCreation(notification, mpersisted, updated);
                } else {
                    /**
                     * Device attachment
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
                /** Device detachment */
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

    @Override
    public QueryResult<MachineVolume> getMachineVolumes(final String machineId, final int first, final int last,
        final List<String> filters, final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        return UtilsForManagers.getCollectionItemList("MachineVolume", this.em, this.getUser().getUsername(), first, last,
            filters, attributes, false, "Machine", "volumes", machineId);
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
            throw new CloudProviderException("Could not attach volume " + volumeId + " to machine " + m.getId());
        } else {
            MachineManager.logger.info(" Attaching volume " + volumeId + " to machine " + m.getId());
            this.em.persist(mv);
            m.addMachineVolume(mv);
            this.em.flush();
        }

        List<CloudResource> affected = new ArrayList<CloudResource>();
        affected.add(mv.getVolume());

        Job persisted = this.createJob(m, affected, "add", j.getStatus(), null);
        persisted.setProviderAssignedId(j.getProviderAssignedId());
        persisted.setDescription("Volume attachment");
        this.updateJob(persisted);
        MachineManager.logger.info(" Add volume " + volumeId + " to machine " + m.getId() + " job state "
            + persisted.getStatus());
        return persisted;
    }

    public Job addVolumeToMachine(final String machineId, final MachineVolume machineVolume) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        Volume volume = machineVolume.getVolume();
        if ((machineId == null) || (volume == null)) {
            throw new InvalidRequestException(" null arguments ");
        }
        MachineManager.logger.info(" Add volume " + machineVolume.getVolume().getId() + " to machine " + machineId);
        if (this.volumeShareable(volume) == false) {
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

        if (volColl == null || volColl.size() == 0) {
            throw new CloudProviderException(" No machine volume collection for " + m.getId());
        }
        MachineVolume mv = (MachineVolume) this.getObjectFromEM(MachineVolume.class, mvId);

        if (volColl.contains(mv) == false) {
            throw new InvalidRequestException(" removing invalid machine volume " + mvId + " from machine  " + machineId);
        }
        if (mv.getState().equals(MachineVolume.State.ERROR)) {
            MachineManager.logger.info(" detaching volume from machine error case " + machineId);
            this.removeMachineVolumeEntry(m, mv.getVolume());
            Job j = new Job();
            j.setStatus(Job.Status.SUCCESS);

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
            throw new ServiceUnavailableException(" " + eee + " removing volume from machine " + m.getId() + " "
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
        persisted.setDescription("Volume detachment");
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
        mt.removeMachineVolume(mv);
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
    public Job updateVolumeAttributesInMachine(final String machineId, final String mvId,
        final Map<String, Object> updatedAttributes) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException {
        // TODO Auto-generated method stub
        throw new ServiceUnavailableException(" Operation not permitted ");
    }

    @Override
    public MachineVolume getVolumeFromMachine(final String machineId, final String macVolId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        Machine m = this.getMachineById(machineId);
        List<MachineVolume> volumes = m.getVolumes();

        if (volumes != null) {
            volumes.size();
            for (MachineVolume v : volumes) {
                if (v.getId().toString().equals(macVolId)) {
                    return v;
                }
            }
        }
        throw new ResourceNotFoundException(" Volume  " + macVolId + " not found for machine " + machineId);
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
    public QueryResult<MachineDisk> getMachineDisks(final String machineId, final int first, final int last,
        final List<String> filters, final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        return UtilsForManagers.getCollectionItemList("MachineDisk", this.em, this.getUser().getUsername(), first, last,
            filters, attributes, false, "Machine", "disks", machineId);
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
    public Job updateDiskAttributesInMachine(final String machineId, final String machineDiskId,
        final Map<String, Object> updatedAttributes) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException {
        // TODO Auto-generated method stub
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
    public Job updateNetworkInterfaceAttributesInMachine(final String machineId, final String nicId,
        final Map<String, Object> updatedAttributes) throws ResourceNotFoundException, CloudProviderException,
        InvalidRequestException {
        // TODO Auto-generated method stub
        throw new ServiceUnavailableException(" Operation not permitted ");
    }

    @Override
    public MachineNetworkInterface getNetworkInterfaceFromMachine(final String machineId, final String nicId)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException {

        Machine m = this.getMachineById(machineId);
        List<MachineNetworkInterface> nics = m.getNetworkInterfaces();

        if (nics != null) {
            nics.size();
            for (MachineNetworkInterface nic : nics) {
                if (nic.getId().toString().equals(nicId)) {
                    List<MachineNetworkInterfaceAddress> entries = nic.getAddresses();
                    if (entries != null) {
                        entries.size();
                    }
                    return nic;
                }
            }
        }
        throw new ResourceNotFoundException(" NetworkInterface  " + nicId + " not found for machine " + machineId);
    }

    @Override
    public List<MachineNetworkInterface> getMachineNetworkInterfaces(final String machineId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {
        Machine m = this.getMachineById(machineId);

        List<MachineNetworkInterface> nics = m.getNetworkInterfaces();
        if (nics != null) {
            nics.size();
            for (MachineNetworkInterface nic : nics) {
                List<MachineNetworkInterfaceAddress> entries = nic.getAddresses();
                if (entries != null) {
                    entries.size();
                }
            }
        }
        return nics;
    }

    @Override
    public QueryResult<MachineNetworkInterface> getMachineNetworkInterfaces(final String machineId, final int first,
        final int last, final List<String> filters, final List<String> attributes) throws InvalidRequestException,
        CloudProviderException {
        return UtilsForManagers.getCollectionItemList("MachineNetworkInterface", this.em, this.getUser().getUsername(), first,
            last, filters, attributes, false, "Machine", "networkInterfaces", machineId);
    }

    /**
     * Methods used by system manager when a system is created, deleted or
     * operations such as stop and start are performed.
     */

    @Override
    public void persistMachineInSystem(final Machine machine) throws CloudProviderException {

        // new unknown machine
        if (machine.getId() != null) {
            throw new CloudProviderException(" Machine " + machine.getId() + " already persisted ");
        }

        List<MachineVolume> vols = machine.getVolumes();
        if (vols != null) {
            for (MachineVolume v : vols) {
                Volume volume = v.getVolume();
                if (volume.getId() == null) {
                    this.em.persist(volume);
                }
            }
            this.em.flush();
        }
        List<MachineNetworkInterface> nics = machine.getNetworkInterfaces();
        if (nics != null && nics.size() > 0) {

            for (MachineNetworkInterface nic : nics) {
                if (nic.getId() != null) {
                    MachineManager.logger.info(" persistMachineInSystem strange interface is persisted entity " + nic.getId());
                }
                if (nic.getAddresses() != null) {
                    nic.getAddresses().size();
                    for (MachineNetworkInterfaceAddress addr : nic.getAddresses()) {
                        if (addr.getId() != null) {
                            MachineManager.logger.info(" persistMachineInSystem strange address is a persisted entity "
                                + addr.getId());
                        }
                        if (addr.getAddress() != null) {
                            this.em.persist(addr.getAddress());
                        } else {
                            MachineManager.logger
                                .info(" persistMachineInSystem strange no address allocated for InterfaceAddress "
                                    + addr.getId());
                        }
                    }
                }
            }
        }

        this.em.persist(machine);
        this.em.flush();

        nics = machine.getNetworkInterfaces();
        if (nics != null && nics.size() > 0) {
            for (MachineNetworkInterface nic : nics) {
                if (nic.getAddresses() != null) {
                    nic.getAddresses().size();
                    List<MachineNetworkInterfaceAddress> addresses = nic.getAddresses();
                    for (MachineNetworkInterfaceAddress addr : addresses) {
                        if (addr.getAddress() != null) {
                            addr.getAddress().setResource(machine);
                        }
                    }
                }
            }
        }

        this.em.merge(machine);
        this.em.flush();

    }

    @Override
    public void deleteMachineInSystem(final Machine machine) throws CloudProviderException {

        if (machine.getId() == null) {
            throw new CloudProviderException(" Deleting a machine not persisted yet ");
        }
        this.removeMachine(machine, true);

    }

    @Override
    public void updateMachineInSystem(final Machine machine) throws CloudProviderException {

        if (machine.getId() == null) {
            throw new CloudProviderException(" Updating a machine not persisted yet ");
        }
        this.em.merge(machine);
        this.em.flush();

    }

    @Override
    public QueryResult<MachineNetworkInterfaceAddress> getMachineNetworkInterfaceAddresses(final String machineId,
        final String nicId, final int first, final int last, final List<String> filters, final List<String> attributes)
        throws InvalidRequestException, CloudProviderException {

        int count = 0;

        Machine m = this.em.find(Machine.class, Integer.valueOf(machineId));
        if (m == null || m.getState().equals(Machine.State.DELETED)) {
            throw new InvalidRequestException(" Bad machine id ");
        }
        List<MachineNetworkInterface> nics = m.getNetworkInterfaces();
        for (MachineNetworkInterface nic : nics) {
            if (nic.getId().toString().equals(nicId)) {
                List<MachineNetworkInterfaceAddress> addresses = nic.getAddresses();
                count = addresses.size();

                return new QueryResult<MachineNetworkInterfaceAddress>(count, addresses);
            }
        }
        MachineManager.logger.info("getMachineNetworkInterface no interface addresss ");
        throw new InvalidRequestException(" Bad network interface id ");
    }

    @Override
    public Job addAddressToMachineNetworkInterface(final String machineId, final String nicId,
        final MachineNetworkInterfaceAddress addressEntry) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {

        throw new InvalidRequestException(" Address cannot be added to a created machine ");
    }

    @Override
    public Job removeAddressFromMachineNetworkInterface(final String machineId, final String nicId, final String addressEntryId)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        throw new InvalidRequestException(" Address cannot be removed from a created machine ");
    }

}