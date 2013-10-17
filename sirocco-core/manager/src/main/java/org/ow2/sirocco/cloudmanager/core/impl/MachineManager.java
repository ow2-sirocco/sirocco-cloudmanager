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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFinder;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager.Placement;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IResourceWatcher;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.BadStateException;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ServiceUnavailableException;
import org.ow2.sirocco.cloudmanager.core.utils.QueryHelper;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
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
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.Network.Type;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.ProviderMapping;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Visibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Remote(IRemoteMachineManager.class)
@Local(IMachineManager.class)
// @IdentityInterceptorBinding
public class MachineManager implements IMachineManager {

    static final String EJB_JNDI_NAME = "MachineManager";

    private static Logger logger = LoggerFactory.getLogger(MachineManager.class.getName());

    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @EJB
    private ITenantManager tenantManager;

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

    @EJB
    private IResourceWatcher resourceWatcher;

    @Inject
    private IdentityContext identityContext;

    @Resource
    private SessionContext ctx;

    @EJB
    private ICloudProviderConnectorFinder connectorFinder;

    private Tenant getTenant() throws CloudProviderException {
        return this.tenantManager.getTenant(this.identityContext);
    }

    private boolean checkQuota(final Tenant t, final MachineConfiguration mc) {
        return true;
    }

    /**
     * User could have passed by value or by reference. Validation is expected to be done by REST layer
     */
    private void checkVolumes(final MachineTemplate mt, final Tenant tenant) throws CloudProviderException {

        List<MachineVolume> volumes = mt.getVolumes();
        if (volumes != null && volumes.size() != 0) {
            for (MachineVolume mv : volumes) {
                if (mv.getInitialLocation() == null) {
                    throw new InvalidRequestException("initialLocation not set for volume ");
                }
                if (mv.getSystemVolumeName() == null) {
                    Volume v = mv.getVolume();
                    /**
                     * Volume should not be passed by value. Check that the volume id exists.
                     */
                    if ((v == null) || (v.getId() == null)) {
                        throw new InvalidRequestException("No volume id ");
                    }
                    this.volumeManager.getVolumeById(v.getId().toString());
                }
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

    private void validateMachineImage(final MachineTemplate mt, final Tenant tenant) throws InvalidRequestException {
        MachineImage mi = mt.getMachineImage();
        if ((mi == null) || (mi.getId() == null)) {
            throw new InvalidRequestException(" MachineImage should be set");
        }

        MachineImage mimage = this.em.find(MachineImage.class, mi.getId());
        if (mimage == null) {
            throw new InvalidRequestException("Unknown machine image in request ");
        }
    }

    private void validateCreationParameters(final MachineTemplate mt, final Tenant tenant) throws CloudProviderException {
        // TODO check all references
        this.checkVolumes(mt, tenant);
        this.validateMachineConfiguration(mt.getMachineConfig());
        this.validateMachineImage(mt, tenant);
    }

    private Job createJob(final CloudResource targetResource, final List<CloudResource> affectedResources, final String action,
        final Job.Status status, final Job parent) throws CloudProviderException {

        Job j = new Job();
        j.setTargetResource(targetResource);
        j.setAffectedResources(affectedResources);
        j.setAction(action);
        j.setState(status);
        j.setTenant(this.getTenant());
        j.setCreated(new Date());
        j.setProperties(new HashMap<String, String>());
        if (parent != null) {
            parent.addNestedJob(j);
        }

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

            parent.addNestedJob(connJob);
            this.updateJob(connJob);

            MachineManager.logger.info("addVolumes job " + connJob.getId());

            MachineVolume mv = new MachineVolume();
            mv.setVolume((Volume) connJob.getTargetResource());
            mv.setInitialLocation(mvt.getInitialLocation());
            mv.setState(MachineVolume.State.PENDING);
            this.em.persist(mv);
            m.addMachineVolume(mv);
        }
    }

    private boolean volumeShareable(final Volume volume) {
        Query q = this.em.createQuery("SELECT v FROM MachineVolume v WHERE v.volume.id=:vid");
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

    private ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount cloudProviderAccount)
        throws CloudProviderException {
        ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(cloudProviderAccount
            .getCloudProvider().getCloudProviderType());
        if (connector == null) {
            MachineManager.logger.error("Cannot find connector for cloud provider type "
                + cloudProviderAccount.getCloudProvider().getCloudProviderType());
            return null;
        }
        return connector;
    }

    public Job createMachine(final MachineCreate machineCreate) throws CloudProviderException {
        MachineManager.logger.info("Creating Machine");

        Tenant tenant = this.getTenant();

        Placement placement = this.cloudProviderManager.placeResource(tenant.getId().toString(), machineCreate.getProperties());
        ICloudProviderConnector connector = this.getCloudProviderConnector(placement.getAccount());
        if (connector == null) {
            throw new CloudProviderException("Cannot retrieve cloud provider connector "
                + placement.getAccount().getCloudProvider().getCloudProviderType());
        }

        Machine machine;

        try {
            IComputeService computeService = connector.getComputeService();
            machine = computeService.createMachine(machineCreate, new ProviderTarget().account(placement.getAccount())
                .location(placement.getLocation()));
        } catch (ConnectorException e) {
            MachineManager.logger.error("Failed to create machine: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        machine.setName(machineCreate.getName());
        machine.setDescription(machineCreate.getDescription());
        machine.setProperties(machineCreate.getProperties() == null ? new HashMap<String, String>()
            : new HashMap<String, String>(machineCreate.getProperties()));
        machine.setState(Machine.State.CREATING);
        machine.setTenant(tenant);
        machine.setCloudProviderAccount(placement.getAccount());
        machine.setLocation(placement.getLocation());
        machine.setCpu(machineCreate.getMachineTemplate().getMachineConfig().getCpu());
        machine.setMemory(machineCreate.getMachineTemplate().getMachineConfig().getMemory());

        // XXX we ignore the nics returned by the (creating) instance
        // nics will be persisted when the machine is ready
        machine.setNetworkInterfaces(null);

        this.em.persist(machine);

        // creating Job
        Job job = new Job();
        job.setTenant(tenant);
        job.setTargetResource(machine);
        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(machine);
        job.setAffectedResources(affectedResources);
        job.setCreated(new Date());
        job.setDescription("Machine creation");
        job.setState(Job.Status.RUNNING);
        job.setAction("add");// TODO: normalize!!
        job.setTimeOfStatusChange(new Date());// now
        this.em.persist(job);
        this.em.flush();

        this.resourceWatcher.watchMachine(machine, job, Machine.State.STARTED, Machine.State.STOPPED);

        return job;
    }

    @Override
    public void syncMachine(final String machineId, final Machine updatedMachine, final String jobId) {
        Machine machine = this.em.find(Machine.class, Integer.valueOf(machineId));
        Job job = this.em.find(Job.class, Integer.valueOf(jobId));
        if (updatedMachine == null || updatedMachine.getState() == State.DELETED) {
            machine.setState(Machine.State.DELETED);
            // delete volume attachments
            for (Iterator<MachineVolume> it = machine.getVolumes().iterator(); it.hasNext();) {
                MachineVolume attachment = it.next();
                it.remove();
                attachment.setOwner(null);
                attachment.setState(MachineVolume.State.DELETED);
                attachment.setVolume(null);
                this.em.persist(attachment);
            }
        } else {
            machine.setState(updatedMachine.getState());
            if (machine.getCreated() == null) {
                machine.setCreated(new Date());
                this.createDisks(machine, updatedMachine);
                this.createNetworkInterfaces(machine, updatedMachine);
            }
            machine.setUpdated(new Date());
        }
        job.setState(Job.Status.SUCCESS);
    }

    @Override
    public void syncVolumeAttachment(final String machineId, final MachineVolume volumeAttachment, final String jobId) {
        MachineManager.logger.info("SYNC syncVolumeAttachment");
        Machine machine = this.em.find(Machine.class, Integer.valueOf(machineId));
        Job job = this.em.find(Job.class, Integer.valueOf(jobId));
        for (MachineVolume mv : machine.getVolumes()) {
            if (mv.getVolume().getProviderAssignedId().equals(volumeAttachment.getVolume().getProviderAssignedId())) {
                mv.setState(volumeAttachment.getState());
                MachineManager.logger.info("VolumeAttachment completed, state=" + volumeAttachment.getState());
                mv.setProviderAssignedId(volumeAttachment.getProviderAssignedId());
                job.setState(Job.Status.SUCCESS);
                if (mv.getState() == MachineVolume.State.DELETED) {
                    machine.removeMachineVolume(mv);
                    mv.setOwner(null);
                    mv.setVolume(null);
                }
                break;
            }
        }
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
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("Machine", Machine.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .stateToIgnore(Machine.State.DELETED));
    }

    @Override
    public List<Machine> getMachines() throws CloudProviderException {
        return QueryHelper.getEntityList("Machine", this.em, this.getTenant().getId(), Machine.State.DELETED, false);
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

    public Job stopMachine(final String machineId, final boolean force) throws CloudProviderException {
        return this.doService(machineId, "stop");
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

    @Override
    public Job captureMachine(final String machineId, final MachineImage machineImage) throws CloudProviderException {
        Job j;
        Machine m = this.checkOps(machineId, "capture");
        // TODO:workflowICloudProviderConnector connector =
        // this.getConnector(m);
        IComputeService computeService;
        // TODO:workflowtry {
        // TODO:workflow computeService = connector.getComputeService();
        // TODO:workflow} catch (ConnectorException e) {
        // TODO:workflow String eee = e.getMessage();
        // TODO:workflow throw new ServiceUnavailableException(" " + eee +
        // " action capture machine " + machineId + " "
        // TODO:workflow + m.getProviderAssignedId());
        // TODO:workflow}
        MachineImage capturedMachineImage = new MachineImage();
        capturedMachineImage.setTenant(this.getTenant());
        capturedMachineImage.setName(machineImage.getName());
        capturedMachineImage.setDescription(machineImage.getDescription());
        capturedMachineImage.setProperties(machineImage.getProperties());
        capturedMachineImage.setState(MachineImage.State.CREATING);
        capturedMachineImage.setType(MachineImage.Type.IMAGE);

        // TODO:workflowtry {
        // TODO:workflowj =
        // computeService.captureMachine(m.getProviderAssignedId(),
        // capturedMachineImage);
        // TODO:workflow} catch
        // (org.ow2.sirocco.cloudmanager.connector.api.BadStateException e) {
        // TODO:workflow throw new BadStateException(e.getMessage() +
        // " action capture machine id " + m.getProviderAssignedId() + " "
        // TODO:workflow + m.getId());
        // TODO:workflow} catch (ConnectorException e) {
        // TODO:workflow throw new ServiceUnavailableException(e.getMessage() +
        // " action capture machine id " + m.getProviderAssignedId()
        // TODO:workflow + " " + m.getId());
        // TODO:workflow}

        this.em.persist(capturedMachineImage);
        this.em.flush();

        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(capturedMachineImage);
        affectedResources.add(m);
        // TODO:workflowJob job = this.createJob(capturedMachineImage,
        // affectedResources, "add", j.getState(), null);
        // TODO:workflowjob.setProviderAssignedId(j.getProviderAssignedId());
        // TODO:workflowthis.updateJob(job);
        // TODO:workflowjob.setDescription("Machine capture");

        // TODO:workflowif (j.getState() != Job.Status.FAILED) {
        // TODO:workflow try {
        // TODO:workflow
        // UtilsForManagers.emitJobListenerMessage(job.getProviderAssignedId(),
        // this.ctx);
        // TODO:workflow } catch (Exception e) {
        // TODO:workflow throw new ServiceUnavailableException(e.getMessage() +
        // "  capture");
        // TODO:workflow }
        // TODO:workflow}
        // TODO:workflowMachineManager.logger.info("operation capture requested "
        // + j.getState());
        // TODO:workflowthis.relConnector(m, connector);
        // TODO:workflowreturn job;
        return null;
    }

    private Job doService(final String machineId, final String action, final Object... params) throws CloudProviderException {
        MachineManager.logger.info("controlling(" + action + ") Machine " + machineId);
        Machine machine = this.getMachineById(machineId);
        if (machine == null) {
            throw new ResourceNotFoundException("Machine " + machineId + " doesn't not exist");
        }

        // checking compatible op
        if (machine.getOperations().contains(action) == false) {
            throw new BadStateException(" Cannot " + action + "  machine at state " + machine.getState());
        }

        ICloudProviderConnector connector = this.getCloudProviderConnector(machine.getCloudProviderAccount());

        boolean force = (params.length > 0 && params[0] instanceof Boolean) ? ((Boolean) params[0]) : false;

        try {
            IComputeService computeService = connector.getComputeService();
            ProviderTarget target = new ProviderTarget().account(machine.getCloudProviderAccount()).location(
                machine.getLocation());
            if (action.equals("start")) {
                computeService.startMachine(machine.getProviderAssignedId(), target);
            } else if (action.equals("stop")) {
                computeService.stopMachine(machine.getProviderAssignedId(), force, target);
            } else if (action.equals("suspend")) {
                computeService.suspendMachine(machine.getProviderAssignedId(), target);
            } else if (action.equals("pause")) {
                computeService.pauseMachine(machine.getProviderAssignedId(), target);
            }

        } catch (ConnectorException e) {
            MachineManager.logger.error("Failed to " + action + " machine: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        // choosing the right action

        Machine.State expectedFinalState = null;
        if (action.equals("start")) {
            machine.setState(Machine.State.STARTING);
            expectedFinalState = State.STARTED;
        } else if (action.equals("stop")) {
            machine.setState(Machine.State.STOPPING);
            expectedFinalState = State.STOPPED;
        } else if (action.equals("suspend")) {
            machine.setState(Machine.State.SUSPENDING);
            expectedFinalState = State.SUSPENDED;
        } else if (action.equals("pause")) {
            machine.setState(Machine.State.PAUSING);
            expectedFinalState = State.PAUSED;
        }
        this.em.merge(machine);

        Tenant tenant = this.getTenant();

        // creating Job
        Job job = new Job();
        job.setTenant(tenant);
        job.setTargetResource(machine);
        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(machine);
        job.setAffectedResources(affectedResources);
        job.setCreated(new Date());
        job.setDescription("Machine " + action);
        job.setState(Job.Status.RUNNING);
        job.setAction(action);// TODO: normalize!!
        job.setTimeOfStatusChange(new Date());// now
        this.em.persist(job);
        this.em.flush();

        this.resourceWatcher.watchMachine(machine, job, expectedFinalState);

        return job;
    }

    // Delete may be done in any state of the machine
    public Job deleteMachine(final String machineId) throws CloudProviderException {
        MachineManager.logger.info("Deleting machine " + machineId);
        Tenant tenant = this.getTenant();

        Machine machine = this.getMachineById(machineId);
        if (machine == null) {
            throw new ResourceNotFoundException("Machine " + machineId + " doesn't not exist");
        }

        ICloudProviderConnector connector = this.getCloudProviderConnector(machine.getCloudProviderAccount());

        try {
            IComputeService computeService = connector.getComputeService();
            computeService.deleteMachine(machine.getProviderAssignedId(),
                new ProviderTarget().account(machine.getCloudProviderAccount()).location(machine.getLocation()));
        } catch (org.ow2.sirocco.cloudmanager.connector.api.ResourceNotFoundException e) {
            // ignore
        } catch (ConnectorException e) {
            MachineManager.logger.error("Failed to delete machine: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        machine.setState(Machine.State.DELETING);
        this.em.merge(machine);

        // creating Job
        Job job = new Job();
        job.setTenant(tenant);
        job.setTargetResource(machine);
        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(machine);
        job.setAffectedResources(affectedResources);
        job.setCreated(new Date());
        job.setDescription("Machine deletion");
        job.setState(Job.Status.RUNNING);
        job.setAction("delete");// TODO: normalize!!
        job.setTimeOfStatusChange(new Date());// now
        this.em.persist(job);
        this.em.flush();

        this.resourceWatcher.watchMachine(machine, job, Machine.State.DELETED);

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
        return UtilsForManagers.fillResourceAttributes(m, attributes);
    }

    /**
     * for each update operation change the local state of machine only after having received the new state from server.
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

        if (attributes.containsKey("properties")) {
            m.setProperties((Map<String, String>) attributes.get("properties"));
        }
        m.setUpdated(new Date());

        j = new Job();
        j.setTargetResource(m);
        j.setState(Job.Status.SUCCESS);
        j.setAction("update");
        j.setParentJob(null);
        j.setNestedJobs(null);
        j.setReturnCode(0);
        j.setTenant(this.getTenant());
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
        mc.getDisks().size();
        return mc;
    }

    @Override
    public MachineConfiguration getMachineConfigurationAttributes(final String machineConfigurationId,
        final List<String> attributes) throws ResourceNotFoundException, CloudProviderException {
        MachineConfiguration mc = this.getMachineConfigurationById(machineConfigurationId);
        return UtilsForManagers.fillResourceAttributes(mc, attributes);
    }

    @Override
    public void updateMachineConfiguration(final MachineConfiguration machineConfiguration) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    public void updateMachineConfigurationAttributes(final String mcId, final MachineConfiguration from,
        final List<String> attributes) throws CloudProviderException {
        if ((mcId == null) || (attributes == null)) {
            throw new InvalidRequestException(" null machine configuration id");
        }
        MachineConfiguration mc = this.em.find(MachineConfiguration.class, Integer.valueOf(mcId));
        if (mc == null) {
            throw new ResourceNotFoundException("Unknown machine configuration " + mcId);
        }
        if (attributes.contains("name")) {
            mc.setName(from.getName());
        }
        if (attributes.contains("description")) {
            mc.setDescription(from.getDescription());
        }
        if (attributes.contains("properties")) {
            mc.setProperties(from.getProperties());
        }
        if (attributes.contains("cpu")) {
            mc.setCpu(from.getCpu());
        }
        if (attributes.contains("memory")) {
            mc.setMemory(from.getMemory());
        }

        if (attributes.contains("disks")) {
            mc.setDisks(from.getDisks());
        }
        mc.setUpdated(new Date());
        this.em.flush();
    }

    public void deleteMachineConfiguration(final String mcId) throws CloudProviderException {

        MachineConfiguration config = (MachineConfiguration) this.getObjectFromEM(MachineConfiguration.class, mcId);

        List<MachineTemplate> mts = null;
        try {
            /**
             * Refuse delete if configuration is being used.
             */
            mts = this.em.createQuery("SELECT m FROM MachineTemplate m WHERE m.machineConfiguration.id=:mcid")
                .setParameter("mcid", Integer.valueOf(mcId)).getResultList();
        } catch (Exception e) {
            return;
        }
        if ((mts != null) && (mts.size() > 0)) {
            throw new ResourceConflictException("MachineTemplates " + mts.get(0).getId() + " uses the configuration " + mcId);
        }
        config.setTenant(null);

        this.em.remove(config);
        this.em.flush();
    }

    @Override
    public List<MachineConfiguration> getMachineConfigurations() throws CloudProviderException {
        List<MachineConfiguration> machineConfigs = this.em
            .createQuery("SELECT c FROM MachineConfiguration c WHERE c.tenant.id=:tenantId OR c.visibility=:visibility")
            .setParameter("tenantId", this.getTenant().getId()).setParameter("visibility", Visibility.PUBLIC).getResultList();
        for (MachineConfiguration machineConfig : machineConfigs) {
            machineConfig.getDisks().size();
        }
        return machineConfigs;
    }

    @Override
    public QueryResult<MachineConfiguration> getMachineConfigurations(final int first, final int last,
        final List<String> filters, final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("MachineConfiguration",
            MachineConfiguration.class);
        QueryResult<MachineConfiguration> machineConfigs = QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .returnPublicEntities());
        for (MachineConfiguration machineConfig : machineConfigs.getItems()) {
            if (machineConfig.getDisks() != null) {
                machineConfig.getDisks().size();
            }
        }
        return machineConfigs;
    }

    public MachineConfiguration createMachineConfiguration(final MachineConfiguration machineConfig)
        throws CloudProviderException {
        this.validateMachineConfiguration(machineConfig);
        machineConfig.setTenant(this.getTenant());
        machineConfig.setCreated(new Date());

        if (machineConfig.getProperties() != null) {
            String providerAccountId = machineConfig.getProperties().get("providerAccountId");
            String providerAssignedId = machineConfig.getProperties().get("providerAssignedId");
            if (providerAccountId != null && providerAssignedId != null) {
                CloudProviderAccount account = this.cloudProviderManager.getCloudProviderAccountById(providerAccountId);
                if (account == null) {
                    throw new CloudProviderException("Invalid provider account id: " + providerAccountId);
                }
                ProviderMapping providerMapping = new ProviderMapping();
                providerMapping.setProviderAssignedId(providerAssignedId);
                providerMapping.setProviderAccount(account);
                providerMapping.setProviderLocation(account.getCloudProvider().getCloudProviderLocations().iterator().next());
                machineConfig.setProviderMappings(Collections.singletonList(providerMapping));
            }
        }

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
        mt.getMachineConfig().getDisks().size();

        return mt;
    }

    @Override
    public MachineTemplate getMachineTemplateAttributes(final String machineTemplateId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        MachineTemplate mt = this.getMachineTemplateById(machineTemplateId);
        return UtilsForManagers.fillResourceAttributes(mt, attributes);
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
            if (attributes.containsKey("machineConfig")) {

                String mc = (String) attributes.get("machineConfig");

                MachineConfiguration config = this.em.find(MachineConfiguration.class, Integer.valueOf(mc));
                if (config == null) {
                    throw new InvalidRequestException(" Could not find machine configuration" + mc);
                }
                mt.setMachineConfig(config);
            }
            if (attributes.containsKey("machineImage")) {
                String mi = (String) attributes.get("machineImage");

                MachineImage image = this.em.find(MachineImage.class, Integer.valueOf(mi));
                if (image == null) {
                    throw new InvalidRequestException(" Could not find machine image" + mi);
                }
                mt.setMachineImage(image);
            }
            if (attributes.containsKey("credential")) {
                String credentials = (String) attributes.get("credential");

                Credentials cred = this.em.find(Credentials.class, Integer.valueOf(credentials));
                if (cred == null) {
                    throw new InvalidRequestException(" Could not find credentials" + credentials);
                }
                mt.setCredential(cred);
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
        mt.setTenant(null);
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
        if (mt.getTenant().equals(this.getTenant()) == false) {
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
            if (v != null) {
                try {
                    this.em.find(Volume.class, v.getId());
                } catch (Exception e) {
                    MachineManager.logger.info(" Incorrect volume being attached to machine template " + v.getId()
                        + " ignoring ");
                    continue;
                }
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
            Set<Address> addresses = nic.getAddresses();
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
     * All checks done in CIMI REST layer: REST Layer has validated that referenced MachineConfiguration etc do really exist.
     */
    public MachineTemplate createMachineTemplate(final MachineTemplate mt) throws CloudProviderException {

        Integer tenantId = this.getTenant().getId();
        boolean exists = true;
        try {
            this.em.createQuery("SELECT m FROM MachineTemplate m WHERE m.tenant.id=:tenantId AND m.name=:name")
                .setParameter("tenantId", tenantId).setParameter("name", mt.getName()).getSingleResult();
        } catch (NoResultException e) {
            exists = false;
        }
        if (exists == true) {
            throw new InvalidRequestException("MachineTemplate by name already exists " + mt.getName());
        }
        MachineConfiguration mc = mt.getMachineConfig();
        if (mc == null) {
            throw new InvalidRequestException("No machineconfiguration ");
        }
        MachineConfiguration mc1 = this.em.find(MachineConfiguration.class, Integer.valueOf(mc.getId()));
        if (mc1 == null) {
            throw new InvalidRequestException("Invalid reference to machine configuraiton " + mc.getId());
        }
        mt.setMachineConfig(mc1);
        // this.validateMachineConfiguration(mt.getMachineConfig());

        MachineImage mi = mt.getMachineImage();
        if (mi == null) {
            throw new InvalidRequestException("No machine image ");
        }
        mi = this.em.find(MachineImage.class, Integer.valueOf(mi.getId()));
        if (mi == null || mi.getState() == MachineImage.State.DELETED) {
            throw new InvalidRequestException("Invalid reference to machine image " + mi.getId());
        }
        mt.setMachineImage(mi);

        /**
         * create volume and volume template collection.
         */
        this.createVolumeCollectionForMt(mt);
        this.createVolumeTemplateCollectionForMt(mt);
        this.createNetworkInterfaces(mt);

        mt.setTenant(this.getTenant());
        mt.setCreated(new Date());
        this.em.persist(mt);
        this.em.flush();
        if (mt.getMachineConfig().getDisks() != null) {
            mt.getMachineConfig().getDisks().size();
        }
        if (mt.getMachineConfig().getProperties() != null) {
            mt.getMachineConfig().getProperties().size();
        }
        if (mt.getVolumes() != null) {
            mt.getVolumes().size();
        }
        return mt;
    }

    @Override
    public QueryResult<MachineTemplate> getMachineTemplates(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder
            .builder("MachineTemplate", MachineTemplate.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .filterEmbbededTemplate().returnPublicEntities());
    }

    @Override
    public List<MachineTemplate> getMachineTemplates() throws CloudProviderException {
        List<MachineTemplate> machineTemplates = this.em
            .createQuery(
                "SELECT c FROM MachineTemplate c WHERE (c.tenant.id=:tenantId OR c.visibility=:visibility) AND c.isEmbeddedInSystemTemplate=false")
            .setParameter("tenantId", this.getTenant().getId()).setParameter("visibility", Visibility.PUBLIC).getResultList();
        for (MachineTemplate machineTemplate : machineTemplates) {
            machineTemplate.getMachineConfig().getDisks().size();
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

        if (mc.getMemory() == null) {
            throw new InvalidRequestException(" Memory attribute should be set");
        }

        if (mc.getMemory() < 0) {
            throw new InvalidRequestException("Incorrect MachineConfiguration ");
        }

        List<DiskTemplate> disks = mc.getDisks();
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
                    // TODO why only for static addresses ?
                    if (address != null && address.getAllocation().equals("static")) {
                        addr.setAddress(null);
                        this.em.remove(address);
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
            disk.setId(null);
            disk.setCreated(new Date());
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
            nic.setId(null);
            nic.setNetworkPort(null);

            if (nic.getNetwork() != null) {
                if (nic.getNetwork().getProviderAssignedId() != null) {
                    Network net = this.networkManager.getNetworkByProviderAssignedId(nic.getNetwork().getProviderAssignedId());
                    if (net != null) {
                        nic.setNetwork(net);
                    } else if (nic.getNetwork().getNetworkType() == Type.PUBLIC) {
                        nic.setNetwork(this.networkManager.getPublicNetwork());
                    }
                } else if (nic.getNetwork().getNetworkType() == Type.PUBLIC) {
                    nic.setNetwork(this.networkManager.getPublicNetwork());
                }
            }

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
        List<CloudResource> resources = job.getAffectedResources();
        if ((job.getAction().equals("add") == false) || (resources == null) || (resources.size() == 0)) {
            return false;
        }
        if ((job.getTargetResource() instanceof Machine) && (job.getAffectedResources() != null)
            && job.getAffectedResources().size() != 0 && (job.getAffectedResources().get(0) instanceof MachineVolume)) {
            Volume volume = ((MachineVolume) job.getAffectedResources().get(0)).getVolume();
            try {
                this.volumeManager.getVolumeById(volume.getId().toString());
            } catch (CloudProviderException e) {
                MachineManager.logger.info(" Volume " + volume.getId() + "seems to have disappeared !");
            }
            return true;
        }
        return false;
    }

    private boolean isVolumeDetach(final Job job) {
        List<CloudResource> resources = job.getAffectedResources();
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
                mv.setState(MachineVolume.State.DELETED);
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
     * Device (volume, disk, network) attachments to machine if creation == true then attachment is part of machine create
     * otherwise an explicit attachment request (add machine volume) by user
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
             * targetEntity = machine affectedEntity = machine volume
             */
            MachineManager.logger.info(" Volume attachment to machine " + local.getId() + " job " + notification.getId()
                + " status " + notification.getState());
            // MachineVolume mv = this.getMachineVolume(local, (Volume)
            // notification.getAffectedResources().get(0));
            // if (mv == null) {
            // MachineManager.logger.info(" could not find machine volume!! " +
            // local.getId());
            // return true;
            // }
            MachineVolume mv = (MachineVolume) notification.getAffectedResources().get(0);
            if (notification.getState() == Job.Status.SUCCESS) {
                MachineManager.logger.info(" Volume attachment succeeded for machine " + local.getId());
                mv.setState(MachineVolume.State.ATTACHED);
            } else if (notification.getState() == Job.Status.FAILED) {
                /** job failed */
                MachineManager.logger.info(" Volume attachment failed for machine " + local.getId());
                mv.setState(MachineVolume.State.ERROR);
                if (machineCreation == false) {
                    this.removeMachineVolumeEntry(local, (Volume) notification.getAffectedResources().get(0));
                }
            }
        } else if (this.isVolumeDetach(notification) == true) {
            MachineVolume mv = (MachineVolume) notification.getAffectedResources().get(0);
            MachineManager.logger.info(" detached volume " + mv.getVolume().getId() + " from machine " + local.getId() + " "
                + notification.getState());
            if (notification.getState() == Job.Status.SUCCESS) {
                notification.getAffectedResources().remove(0);
                this.removeMachineVolumeEntry(local, mv.getVolume());
            } else {
                MachineManager.logger.info("completeDeviceAttachmentToMachine attach failed " + notification.getState());
            }
        } else {
            MachineManager.logger
                .info("Unknown operation on machine " + local.getId() + " notified by " + notification.getId());
            return false;
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
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("MachineVolume", MachineVolume.class);
        return QueryHelper.getCollectionItemList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .containerType("Machine").containerId(machineId).containerAttributeName("volumes"));
    }

    public Job addVolumeToMachine(final String machineId, final MachineVolume machineVolume) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {
        MachineManager.logger.info(" Add volume " + machineVolume.getVolume().getId() + " to machine " + machineId);

        Volume volume = machineVolume.getVolume();
        if ((machineId == null) || (volume == null)) {
            throw new InvalidRequestException(" null arguments ");
        }
        if (this.volumeShareable(volume) == false) {
            throw new InvalidRequestException(" volume " + volume.getId() + " is already in use");
        }

        Machine machine = this.getMachineFromId(machineId);

        if (machine.getState() != Machine.State.STARTED && machine.getState() != Machine.State.STOPPED) {
            throw new InvalidRequestException("Can add volume only in started or stopped state " + machine.getState());
        }

        ICloudProviderConnector connector = this.getCloudProviderConnector(machine.getCloudProviderAccount());

        try {
            IComputeService computeService = connector.getComputeService();
            computeService.addVolumeToMachine(machine.getProviderAssignedId(), machineVolume,
                new ProviderTarget().account(machine.getCloudProviderAccount()).location(machine.getLocation()));
        } catch (ConnectorException e) {
            MachineManager.logger.error("Failed to attach volume to machine: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        MachineVolume mv = new MachineVolume();
        mv.setVolume(volume);
        mv.setInitialLocation(machineVolume.getInitialLocation());
        mv.setState(MachineVolume.State.ATTACHING);

        this.em.persist(mv);
        machine.addMachineVolume(mv);
        this.em.flush();

        List<CloudResource> affected = new ArrayList<CloudResource>();
        affected.add(mv);

        Job job = this.createJob(machine, affected, "add", Job.Status.RUNNING, null);
        job.setDescription("Volume attachment");
        this.em.flush();

        this.resourceWatcher.watchVolumeAttachment(machine, mv, job);

        return job;
    }

    public Job removeVolumeFromMachine(final String machineId, final String mvId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        if ((machineId == null) || (mvId == null)) {
            throw new InvalidRequestException(" null arguments ");
        }
        Machine machine = this.getMachineFromId(machineId);
        List<MachineVolume> volColl = machine.getVolumes();

        MachineVolume mv = (MachineVolume) this.getObjectFromEM(MachineVolume.class, mvId);

        if (volColl.contains(mv) == false) {
            throw new InvalidRequestException(" removing invalid machine volume " + mvId + " from machine  " + machineId);
        }

        MachineManager.logger.info("Removing volume " + mv.getVolume().getId() + " from machine " + machineId);

        ICloudProviderConnector connector = this.getCloudProviderConnector(machine.getCloudProviderAccount());

        try {
            IComputeService computeService = connector.getComputeService();
            computeService.removeVolumeFromMachine(machine.getProviderAssignedId(), mv,
                new ProviderTarget().account(machine.getCloudProviderAccount()).location(machine.getLocation()));
        } catch (ConnectorException e) {
            MachineManager.logger.error("Failed to detach volume from machine: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        mv.setState(MachineVolume.State.DETACHING);

        List<CloudResource> affectedEntities = new ArrayList<CloudResource>();
        affectedEntities.add(mv);
        Job job = this.createJob(machine, affectedEntities, "delete", Job.Status.RUNNING, null);
        job.setDescription("Volume detachment");
        this.em.flush();

        this.resourceWatcher.watchVolumeAttachment(machine, mv, job);

        return job;
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
                if (v.getId().toString().equals(macVolId) && v.getState() != MachineVolume.State.DELETED) {
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
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("MachineDisk", MachineDisk.class);
        return QueryHelper.getCollectionItemList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .containerType("Machine").containerId(machineId).containerAttributeName("disks"));
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
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("MachineNetworkInterface",
            MachineNetworkInterface.class);
        return QueryHelper.getCollectionItemList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .containerType("Machine").containerId(machineId).containerAttributeName("networkInterfaces"));
    }

    /**
     * Methods used by system manager when a system is created, deleted or operations such as stop and start are performed.
     */

    @Override
    public void persistMachineInSystem(final Machine machine) throws CloudProviderException {

        // new unknown machine
        if (machine.getId() != null) {
            throw new CloudProviderException(" Machine " + machine.getId() + " already persisted ");
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
                            addr.getAddress().setNetwork(null);
                            addr.getAddress().setResource(null);
                            addr.getAddress().setCreated(new Date());
                            addr.getAddress().setTenant(machine.getTenant());
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

        List<MachineVolume> vols = machine.getVolumes();
        if (vols != null) {
            for (MachineVolume v : vols) {
                Volume volume = v.getVolume();
                if (volume != null) {
                    if (volume.getId() == null) {
                        this.em.persist(volume);
                    }
                }
                if (v.getId() == null) {
                    this.em.persist(v);
                }
                v.setOwner(machine);
            }
            this.em.flush();
        }

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

        Machine m = this.em.find(Machine.class, Integer.valueOf(machineId));
        if (m == null || m.getState().equals(Machine.State.DELETED)) {
            throw new InvalidRequestException(" Bad machine id ");
        }
        List<MachineNetworkInterface> nics = m.getNetworkInterfaces();
        for (MachineNetworkInterface nic : nics) {
            if (nic.getId().toString().equals(nicId)) {
                List<MachineNetworkInterfaceAddress> addresses = nic.getAddresses();
                if (nic.getAddresses() == null) {
                    return new QueryResult<MachineNetworkInterfaceAddress>(0, new ArrayList<MachineNetworkInterfaceAddress>());
                }
                return new QueryResult<MachineNetworkInterfaceAddress>(addresses.size(), addresses);
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

    @Override
    public List<MachineNetworkInterfaceAddress> getMachineNetworkInterfaceAddresses(final String machineId, final String nicId)
        throws InvalidRequestException, CloudProviderException {
        QueryResult<MachineNetworkInterfaceAddress> result = this.getMachineNetworkInterfaceAddresses(machineId, nicId, -1, -1,
            null, null);
        return result.getItems();
    }

    @Override
    public Job updateMachineNetworkInterfaceAddress(final String machineId, final String nicId,
        final MachineNetworkInterfaceAddress addressEntry) throws InvalidRequestException, CloudProviderException {
        throw new InvalidRequestException(" Address entry cannot be updated ");
    }

}