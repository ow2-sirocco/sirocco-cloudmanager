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
import javax.jms.JMSContext;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.validation.ConstraintViolationException;

import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager.Placement;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.QueryParams;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.ResourceStateChangeEvent;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ServiceUnavailableException;
import org.ow2.sirocco.cloudmanager.core.api.remote.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.core.impl.command.MachineActionCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.MachineCaptureCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.MachineCreateCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.MachineDeleteCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.VolumeAttachCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.VolumeDetachCommand;
import org.ow2.sirocco.cloudmanager.core.utils.QueryHelper;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Action;
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
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
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
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
    private IMachineImageManager machineImageManager;

    @EJB
    private ICredentialsManager credentialManager;

    @EJB
    private IJobManager jobManager;

    @Inject
    private IdentityContext identityContext;

    @Resource
    private SessionContext ctx;

    @Resource(lookup = "jms/RequestQueue")
    private Queue requestQueue;

    @Resource(lookup = "jms/ResourceStateChangeTopic")
    private Topic resourceStateChangeTopic;

    @Inject
    private JMSContext jmsContext;

    private Tenant getTenant() throws CloudProviderException {
        return this.tenantManager.getTenant(this.identityContext);
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

    private boolean volumeShareable(final Volume volume) {
        List<MachineVolume> list = this.em
            .createQuery("SELECT v FROM MachineVolume v WHERE v.volume.id=:vid", MachineVolume.class)
            .setParameter("vid", volume.getId()).getResultList();
        for (MachineVolume mv : list) {
            if ((mv.getState() == MachineVolume.State.ATTACHED) || (mv.getState() == MachineVolume.State.ATTACHING)) {
                return false;
            }
        }
        return true;
    }

    public Job createMachine(final MachineCreate machineCreate) throws CloudProviderException {
        MachineManager.logger.info("Creating Machine " + machineCreate.getName());
        Tenant tenant = this.getTenant();

        Placement placement = this.cloudProviderManager.placeResource(tenant.getId(), machineCreate);

        Machine machine = new Machine();

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
        machine.setImage(machineCreate.getMachineTemplate().getMachineImage());
        machine.setConfig(machineCreate.getMachineTemplate().getMachineConfig());

        for (DiskTemplate diskTemplate : machineCreate.getMachineTemplate().getMachineConfig().getDisks()) {
            MachineDisk disk = new MachineDisk();
            disk.setCapacity(diskTemplate.getCapacity());
            machine.addMachineDisk(disk);
        }

        this.em.persist(machine);

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

        ObjectMessage message = this.jmsContext
            .createObjectMessage(new MachineCreateCommand(machineCreate).setAccount(placement.getAccount())
                .setLocation(placement.getLocation()).setResourceId(machine.getId()).setJob(job));
        this.jmsContext.createProducer().send(this.requestQueue, message);
        return job;
    }

    @Override
    public void syncMachine(final int machineId, final Machine updatedMachine, final int jobId) {
        Machine machine = this.em.find(Machine.class, machineId);
        Job job = this.em.find(Job.class, jobId);
        if (updatedMachine == null || updatedMachine.getState() == State.DELETED) {
            machine.setState(Machine.State.DELETED);
            machine.setDeleted(new Date());
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
                this.createNetworkInterfaces(machine, updatedMachine);
            }
            machine.setUpdated(new Date());
        }
        this.fireResourceStateChangeEvent(machine);
        job.setState(Job.Status.SUCCESS);
    }

    @Override
    public void syncVolumeAttachment(final int machineId, final MachineVolume volumeAttachment, final int jobId) {
        MachineManager.logger.info("SYNC syncVolumeAttachment");
        Machine machine = this.em.find(Machine.class, machineId);
        Job job = this.em.find(Job.class, jobId);
        for (MachineVolume mv : machine.getVolumes()) {
            if (mv.getVolume().getProviderAssignedId().equals(volumeAttachment.getVolume().getProviderAssignedId())) {
                mv.setState(volumeAttachment.getState());
                this.fireResourceStateChangeEvent(mv);
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
    public QueryResult<Machine> getMachines(final QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException {
        if (queryParams.length == 0) {
            @SuppressWarnings("unchecked")
            List<Machine> machines = QueryHelper.getEntityList("Machine", this.em, this.getTenant().getId(),
                Machine.State.DELETED, false);
            return new QueryResult<Machine>(machines.size(), machines);
        }
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("Machine", Machine.class)
            .tenantId(this.getTenant().getId()).stateToIgnore(Machine.State.DELETED).params(queryParams[0]);
        return QueryHelper.getEntityList(this.em, params);
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
        Machine machine = this.getMachineByUuid(machineId);
        MachineManager.logger.info("Capturing machine " + machine.getName());
        Tenant tenant = this.getTenant();

        MachineImage newMachineImage = new MachineImage();
        newMachineImage.setTenant(tenant);
        newMachineImage.setCloudProviderAccount(machine.getCloudProviderAccount());
        newMachineImage.setLocation(machine.getLocation());
        newMachineImage.setName(machineImage.getName());
        newMachineImage.setDescription(machineImage.getDescription());
        newMachineImage.setProperties(machineImage.getProperties());
        newMachineImage.setState(MachineImage.State.CREATING);

        this.em.persist(newMachineImage);

        Job job = new Job();
        job.setTenant(tenant);
        job.setTargetResource(machine);
        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(newMachineImage);
        job.setAffectedResources(affectedResources);
        job.setCreated(new Date());
        job.setDescription("Machine capture");
        job.setState(Job.Status.RUNNING);
        job.setAction("capture");
        job.setTimeOfStatusChange(new Date());
        this.em.persist(job);
        this.em.flush();

        ObjectMessage message = this.jmsContext.createObjectMessage(new MachineCaptureCommand(newMachineImage.getId())
            .setResourceId(machine.getId()).setJob(job));
        this.jmsContext.createProducer().send(this.requestQueue, message);

        return job;
    }

    private Job doService(final String machineId, final String action, final Object... params) throws CloudProviderException {
        Machine machine = this.getMachineByUuid(machineId);
        MachineManager.logger.info("Starting action " + action.toUpperCase() + " on Machine " + machine.getName());

        // checking compatible op
        if (machine.getOperations().contains(action) == false) {
            throw new InvalidRequestException(" Cannot " + action + "  machine at state " + machine.getState());
        }

        boolean force = (params.length > 0 && params[0] instanceof Boolean) ? ((Boolean) params[0]) : false;

        // choosing the right action

        if (action.equals("start")) {
            machine.setState(Machine.State.STARTING);
        } else if (action.equals("stop")) {
            machine.setState(Machine.State.STOPPING);
        } else if (action.equals("suspend")) {
            machine.setState(Machine.State.SUSPENDING);
        } else if (action.equals("pause")) {
            machine.setState(Machine.State.PAUSING);
        }
        this.fireResourceStateChangeEvent(machine);
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

        ObjectMessage message = this.jmsContext.createObjectMessage(new MachineActionCommand(action).setForce(force)
            .setResourceId(machine.getId()).setJob(job));
        this.jmsContext.createProducer().send(this.requestQueue, message);

        return job;
    }

    // Delete may be done in any state of the machine
    public Job deleteMachine(final String machineId) throws CloudProviderException {
        Tenant tenant = this.getTenant();

        Machine machine = this.getMachineByUuid(machineId);
        MachineManager.logger.info("Deleting machine " + machine.getName());

        if (machine.getState() == State.ERROR && machine.getProviderAssignedId() == null) {
            machine.setState(Machine.State.DELETED);
            this.fireResourceStateChangeEvent(machine);
            Job job = Job.newBuilder().tenant(tenant).target(machine).description("Machine deletion").status(Status.SUCCESS)
                .action(Action.DELETE).build();
            this.em.persist(job);
            return job;
        }

        machine.setState(Machine.State.DELETING);
        this.fireResourceStateChangeEvent(machine);

        // creating Job
        Job job = Job.newBuilder().tenant(tenant).target(machine).description("Machine deletion").action(Action.DELETE).build();
        this.em.persist(job);
        this.em.flush();

        ObjectMessage message = this.jmsContext.createObjectMessage(new MachineDeleteCommand().setResourceId(machine.getId())
            .setJob(job));
        this.jmsContext.createProducer().send(this.requestQueue, message);

        return job;
    }

    private Machine getMachineFromId(final int machineId) throws ResourceNotFoundException, CloudProviderException {
        Machine m = this.em.find(Machine.class, machineId);
        if (m == null || m.getState() == Machine.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid machine id " + machineId);
        }
        return m;
    }

    public Machine getMachineById(final int machineId) throws ResourceNotFoundException, CloudProviderException {
        Machine m = this.getMachineFromId(machineId);
        this.readMachineAttributes(m);
        return m;
    }

    @Override
    public Machine getMachineByUuid(final String uuid) throws ResourceNotFoundException, CloudProviderException {
        try {
            Machine result = this.em.createNamedQuery("Machine.findByUuid", Machine.class).setParameter("uuid", uuid)
                .getSingleResult();
            this.readMachineAttributes(result);
            return result;
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    public MachineVolume getMachineVolumeByUuid(final String uuid) throws ResourceNotFoundException, CloudProviderException {
        try {
            return this.em.createNamedQuery("MachineVolume.findByUuid", MachineVolume.class).setParameter("uuid", uuid)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    public Machine getMachineAttributes(final String machineId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        Machine m = this.getMachineByUuid(machineId);
        this.readMachineAttributes(m);
        return UtilsForManagers.fillResourceAttributes(m, attributes);
    }

    @Override
    public void updateMachineState(final int machineId, final State state) throws CloudProviderException {
        Machine machine = this.getMachineById(machineId);
        machine.setState(state);
        this.fireResourceStateChangeEvent(machine);
    }

    @Override
    public void updateMachineVolumeState(final int machineVolumeId, final MachineVolume.State state)
        throws CloudProviderException {
        MachineVolume machineVolume = this.em.find(MachineVolume.class, machineVolumeId);
        if (machineVolume == null) {
            throw new ResourceNotFoundException("MachineVolume " + machineVolumeId + " nout found");
        }
        machineVolume.setState(state);
        this.fireResourceStateChangeEvent(machineVolume);
    }

    private void fireResourceStateChangeEvent(final CloudResource resource) {
        this.jmsContext.createProducer().setProperty("tenantId", resource.getTenant().getUuid())
            .send(this.resourceStateChangeTopic, new ResourceStateChangeEvent(resource));
    }

    @Override
    public Job updateMachine(final Machine machine) throws ResourceNotFoundException, CloudProviderException {
        // TODO
        return null;
    }

    // TODO
    @SuppressWarnings("unchecked")
    public Job updateMachineAttributes(final String machineId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        Job j = null;

        Machine m = this.getMachineByUuid(machineId);

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
    public MachineConfiguration getMachineConfigurationById(final int mcId) throws CloudProviderException {
        MachineConfiguration mc = this.em.find(MachineConfiguration.class, mcId);
        if (mc == null) {
            throw new ResourceNotFoundException("Unknown machine configuration " + mcId);
        }
        mc.getDisks().size();
        return mc;
    }

    @Override
    public MachineConfiguration getMachineConfigurationByUuid(final String uuid) throws ResourceNotFoundException,
        CloudProviderException {
        try {
            return this.em.createNamedQuery("MachineConfiguration.findByUuid", MachineConfiguration.class)
                .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public MachineConfiguration getMachineConfigurationAttributes(final String machineConfigurationId,
        final List<String> attributes) throws ResourceNotFoundException, CloudProviderException {
        MachineConfiguration mc = this.getMachineConfigurationByUuid(machineConfigurationId);
        return UtilsForManagers.fillResourceAttributes(mc, attributes);
    }

    @Override
    public void updateMachineConfiguration(final MachineConfiguration machineConfiguration) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO
    }

    public void updateMachineConfigurationAttributes(final String mcId, final MachineConfiguration from,
        final List<String> attributes) throws CloudProviderException {
        if ((mcId == null) || (attributes == null)) {
            throw new InvalidRequestException(" null machine configuration id");
        }
        MachineConfiguration mc = this.getMachineConfigurationByUuid(mcId);
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
        MachineConfiguration config = this.getMachineConfigurationByUuid(mcId);

        List<MachineTemplate> mts = null;
        try {
            /**
             * Refuse delete if configuration is being used.
             */
            mts = this.em
                .createQuery("SELECT m FROM MachineTemplate m WHERE m.machineConfiguration.uuid=:mcid", MachineTemplate.class)
                .setParameter("mcid", mcId).getResultList();
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

    private List<MachineConfiguration> getMachineConfigurations() throws CloudProviderException {
        List<MachineConfiguration> machineConfigs = this.em
            .createQuery("SELECT c FROM MachineConfiguration c WHERE c.tenant.id=:tenantId OR c.visibility=:visibility",
                MachineConfiguration.class).setParameter("tenantId", this.getTenant().getId())
            .setParameter("visibility", Visibility.PUBLIC).getResultList();
        for (MachineConfiguration machineConfig : machineConfigs) {
            machineConfig.getDisks().size();
        }
        return machineConfigs;
    }

    @Override
    public QueryResult<MachineConfiguration> getMachineConfigurations(final QueryParams... queryParams)
        throws InvalidRequestException, CloudProviderException {
        if (queryParams.length == 0) {
            List<MachineConfiguration> configs = this.getMachineConfigurations();
            return new QueryResult<MachineConfiguration>(configs.size(), configs);
        }
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("MachineConfiguration",
            MachineConfiguration.class);
        QueryResult<MachineConfiguration> machineConfigs = QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).params(queryParams[0]).returnPublicEntities());
        for (MachineConfiguration machineConfig : machineConfigs.getItems()) {
            if (machineConfig.getDisks() != null) {
                machineConfig.getDisks().size();
            }
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
        machineConfig.setTenant(this.getTenant());
        machineConfig.setCreated(new Date());

        if (machineConfig.getProperties() != null) {
            String providerAccountId = machineConfig.getProperties().get("providerAccountId");
            String providerAssignedId = machineConfig.getProperties().get("providerAssignedId");
            if (providerAccountId != null && providerAssignedId != null) {
                CloudProviderAccount account = this.cloudProviderManager.getCloudProviderAccountByUuid(providerAccountId);
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

        try {
            this.em.persist(machineConfig);
        } catch (ConstraintViolationException e) {
            throw new InvalidRequestException(e);
        }
        this.em.flush();
        return machineConfig;
    }

    /**
     * Operations on MachineTemplate
     */
    public MachineTemplate getMachineTemplateById(final int mtId) throws CloudProviderException {
        MachineTemplate mt = this.em.find(MachineTemplate.class, mtId);
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
    public MachineTemplate getMachineTemplateByUuid(final String uuid) throws ResourceNotFoundException, CloudProviderException {
        try {
            MachineTemplate result = this.em.createNamedQuery("MachineTemplate.findByUuid", MachineTemplate.class)
                .setParameter("uuid", uuid).getSingleResult();
            if (result.getVolumes() != null) {
                result.getVolumes().size();
            }
            if (result.getVolumeTemplates() != null) {
                result.getVolumeTemplates().size();
            }
            result.getNetworkInterfaces().size();
            result.getMachineConfig().getDisks().size();
            return result;
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public MachineTemplate getMachineTemplateAttributes(final String machineTemplateId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        MachineTemplate mt = this.getMachineTemplateByUuid(machineTemplateId);
        return UtilsForManagers.fillResourceAttributes(mt, attributes);
    }

    @Override
    public void updateMachineTemplate(final MachineTemplate machineTemplate) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO
    }

    @SuppressWarnings("unchecked")
    public void updateMachineTemplateAttributes(final String mtId, final Map<String, Object> attributes)
        throws CloudProviderException {
        MachineTemplate mt = this.getMachineTemplateByUuid(mtId);
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
                String mcUuid = (String) attributes.get("machineConfig");
                MachineConfiguration config = this.getMachineConfigurationByUuid(mcUuid);
                mt.setMachineConfig(config);
            }
            if (attributes.containsKey("machineImage")) {
                String imageUuid = (String) attributes.get("machineImage");
                MachineImage image = this.machineImageManager.getMachineImageByUuid(imageUuid);
                mt.setMachineImage(image);
            }
            if (attributes.containsKey("credential")) {
                String credentialUuid = (String) attributes.get("credential");
                Credentials cred = this.credentialManager.getCredentialsByUuid(credentialUuid);
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
        MachineTemplate mt = this.getMachineTemplateByUuid(mtId);
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
                this.volumeManager.getVolumeTemplateByUuid(vt.getUuid());
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
        MachineConfiguration mc1 = this.getMachineConfigurationByUuid(mc.getUuid());
        if (mc1 == null) {
            throw new InvalidRequestException("Invalid reference to machine configuraiton " + mc.getId());
        }
        mt.setMachineConfig(mc1);
        // this.validateMachineConfiguration(mt.getMachineConfig());

        MachineImage mi = mt.getMachineImage();
        if (mi == null) {
            throw new InvalidRequestException("No machine image ");
        }
        mi = this.machineImageManager.getMachineImageByUuid(mi.getUuid());
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
    public QueryResult<MachineTemplate> getMachineTemplates(final QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException {
        if (queryParams.length == 0) {
            List<MachineTemplate> templates = this.getMachineTemplates();
            return new QueryResult<MachineTemplate>(templates.size(), templates);
        }
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder
            .builder("MachineTemplate", MachineTemplate.class);
        return QueryHelper.getEntityList(this.em, params.tenantId(this.getTenant().getId()).params(queryParams[0])
            .filterEmbbededTemplate().returnPublicEntities());
    }

    private List<MachineTemplate> getMachineTemplates() throws CloudProviderException {
        List<MachineTemplate> machineTemplates = this.em
            .createQuery(
                "SELECT c FROM MachineTemplate c WHERE (c.tenant.id=:tenantId OR c.visibility=:visibility) AND c.isEmbeddedInSystemTemplate=false",
                MachineTemplate.class).setParameter("tenantId", this.getTenant().getId())
            .setParameter("visibility", Visibility.PUBLIC).getResultList();
        for (MachineTemplate machineTemplate : machineTemplates) {
            machineTemplate.getMachineConfig().getDisks().size();
        }
        return machineTemplates;
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
        this.systemManager.handleEntityStateChange(deleted.getClass(), deleted.getId(), true);

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
            nic.setId(null);
            nic.setNetworkPort(null);

            if (nic.getNetwork() != null) {
                if (nic.getNetwork().getProviderAssignedId() != null) {
                    Network net = this.networkManager.getNetworkByProviderAssignedId(nic.getNetwork().getProviderAssignedId());
                    if (net != null) {
                        nic.setNetwork(net);
                    } else {
                        MachineManager.logger.error("Unknown network with provider-assigned id "
                            + nic.getNetwork().getProviderAssignedId());
                        nic.setNetwork(null);
                    }
                } else {
                    MachineManager.logger.error("Missing provider-assigned id in nic.network for machine with id "
                        + persisted.getId());
                    continue;
                }
            }

            List<MachineNetworkInterfaceAddress> entries = nic.getAddresses();
            if (entries != null) {
                for (MachineNetworkInterfaceAddress entry : entries) {
                    if (entry.getAddress() != null) {
                        entry.getAddress().setNetwork(null);
                        entry.getAddress().setResource(null);
                    }
                }
            }
            this.em.persist(nic);
            persisted.addNetworkInterface(nic);
        }
        this.em.flush();
    }

    public List<MachineVolume> getMachineVolumes(final String machineId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        Machine m = this.getMachineByUuid(machineId);
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
    public QueryResult<MachineVolume> getMachineVolumes(final String machineId, final QueryParams... queryParams)
        throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("MachineVolume", MachineVolume.class);
        if (queryParams.length > 0) {
            params.params(queryParams[0]);
        }
        return QueryHelper.getCollectionItemList(this.em, params.tenantId(this.getTenant().getId()).containerType("Machine")
            .containerId(machineId).containerAttributeName("volumes"));
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

        Machine machine = this.getMachineByUuid(machineId);

        if (machine.getState() != Machine.State.STARTED && machine.getState() != Machine.State.STOPPED) {
            throw new InvalidRequestException("Can add volume only in started or stopped state " + machine.getState());
        }

        MachineVolume mv = new MachineVolume();
        mv.setTenant(volume.getTenant());
        mv.setVolume(volume);
        mv.setInitialLocation(machineVolume.getInitialLocation());
        mv.setState(MachineVolume.State.ATTACHING);

        this.em.persist(mv);
        machine.addMachineVolume(mv);
        this.em.flush();

        this.fireResourceStateChangeEvent(mv);

        List<CloudResource> affected = new ArrayList<CloudResource>();
        affected.add(mv);

        Job job = this.createJob(machine, affected, "add", Job.Status.RUNNING, null);
        job.setDescription("Volume attachment");
        this.em.flush();

        ObjectMessage message = this.jmsContext.createObjectMessage(new VolumeAttachCommand(mv).setResourceId(machine.getId())
            .setJob(job));
        this.jmsContext.createProducer().send(this.requestQueue, message);

        return job;
    }

    public Job removeVolumeFromMachine(final String machineId, final String mvId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        if ((machineId == null) || (mvId == null)) {
            throw new InvalidRequestException(" null arguments ");
        }
        Machine machine = this.getMachineByUuid(machineId);
        List<MachineVolume> volColl = machine.getVolumes();

        MachineVolume mv = this.getMachineVolumeByUuid(mvId);

        if (volColl.contains(mv) == false) {
            throw new InvalidRequestException(" removing invalid machine volume " + mvId + " from machine  " + machineId);
        }

        MachineManager.logger.info("Removing volume " + mv.getVolume().getId() + " from machine " + machineId);

        mv.setState(MachineVolume.State.DETACHING);

        List<CloudResource> affectedEntities = new ArrayList<CloudResource>();
        affectedEntities.add(mv);
        Job job = this.createJob(machine, affectedEntities, "delete", Job.Status.RUNNING, null);
        job.setDescription("Volume detachment");
        this.em.flush();

        this.fireResourceStateChangeEvent(mv);

        ObjectMessage message = this.jmsContext.createObjectMessage(new VolumeDetachCommand(mv).setResourceId(machine.getId())
            .setJob(job));
        this.jmsContext.createProducer().send(this.requestQueue, message);

        return job;
    }

    public List<MachineVolumeTemplate> getMachineVolumeTemplates(final String mtId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        MachineTemplate mt = this.getMachineTemplateByUuid(mtId);
        List<MachineVolumeTemplate> volTemplateColl = mt.getVolumeTemplates();

        if (volTemplateColl != null) {
            volTemplateColl.size();
        }
        return volTemplateColl;
    }

    private void addVolumeToMachineTemplate(final MachineTemplate mt, final String volumeId, final String initialLocation)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {

        Volume volume = this.volumeManager.getVolumeByUuid(volumeId);
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
        MachineTemplate mt = this.getMachineTemplateByUuid(mtId);
        this.addVolumeToMachineTemplate(mt, volumeId, initialLocation);
    }

    private void addVolumeTemplateToMachineTemplate(final MachineTemplate mt, final String vtId, final String initialLocation)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {

        VolumeTemplate vt = this.volumeManager.getVolumeTemplateByUuid(vtId);

        MachineVolumeTemplate mvt = new MachineVolumeTemplate();

        mvt.setVolumeTemplate(vt);
        mvt.setInitialLocation(initialLocation);
        mt.addMachineVolumeTemplate(mvt);

        this.em.persist(mvt);
        this.em.flush();
    }

    public void addVolumeTemplateToMachineTemplate(final String mtId, final String vtId, final String initialLocation)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException {
        MachineTemplate mt = this.getMachineTemplateByUuid(mtId);
        this.addVolumeTemplateToMachineTemplate(mt, vtId, initialLocation);
    }

    public void removeVolumeFromMachineTemplate(final String mtId, final String mvId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {
        MachineTemplate mt = this.getMachineTemplateByUuid(mtId);
        List<MachineVolume> vColl = mt.getVolumes();
        MachineVolume mv = this.getMachineVolumeByUuid(mvId);

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
        MachineTemplate mt = this.getMachineTemplateByUuid(mtId);
        List<MachineVolumeTemplate> vtColl = mt.getVolumeTemplates();
        MachineVolumeTemplate mvt = this.getMachineVolumeTemplateByUuid(mvtId);

        if (vtColl.contains(mvt) == false) {
            throw new InvalidRequestException(" removing invalid machine volume template " + mvtId + " from machine template "
                + mtId);
        }
        mt.removeMachineVolumeTemplate(mvt);
        mvt.setVolumeTemplate(null);
        this.em.remove(mvt);
        this.em.flush();
    }

    private MachineVolumeTemplate getMachineVolumeTemplateByUuid(final String uuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("MachineVolumeTemplate.findByUuid", MachineVolumeTemplate.class)
                .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
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
        // TODO
        throw new ServiceUnavailableException(" Operation not permitted ");
    }

    @Override
    public MachineVolume getVolumeFromMachine(final String machineId, final String macVolId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        Machine m = this.getMachineByUuid(machineId);
        List<MachineVolume> volumes = m.getVolumes();

        if (volumes != null) {
            volumes.size();
            for (MachineVolume v : volumes) {
                if (v.getUuid().equals(macVolId) && v.getState() != MachineVolume.State.DELETED) {
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
    public QueryResult<MachineDisk> getMachineDisks(final String machineId, final QueryParams... queryParams)
        throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("MachineDisk", MachineDisk.class);
        if (queryParams.length > 0) {
            params.params(queryParams[0]);
        }
        return QueryHelper.getCollectionItemList(this.em, params.tenantId(this.getTenant().getId()).containerType("Machine")
            .containerId(machineId).containerAttributeName("disks"));
    }

    @Override
    public Job removeDiskFromMachine(final String machineId, final String machineDiskId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {
        throw new ServiceUnavailableException(" Operation not permitted ");
    }

    @Override
    public MachineDisk getDiskFromMachine(final String machineId, final String machineDiskId) throws ResourceNotFoundException,
        CloudProviderException, InvalidRequestException {

        Machine m = this.getMachineByUuid(machineId);
        List<MachineDisk> disks = m.getDisks();

        if (disks != null) {
            disks.size();
            for (MachineDisk disk : disks) {
                if (disk.getUuid().equals(machineDiskId)) {
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

        Machine m = this.getMachineByUuid(machineId);
        List<MachineNetworkInterface> nics = m.getNetworkInterfaces();

        if (nics != null) {
            nics.size();
            for (MachineNetworkInterface nic : nics) {
                if (nic.getUuid().equals(nicId)) {
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
    public QueryResult<MachineNetworkInterface> getMachineNetworkInterfaces(final String machineId,
        final QueryParams... queryParams) throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("MachineNetworkInterface",
            MachineNetworkInterface.class);
        if (queryParams.length > 0) {
            params.params(queryParams[0]);
        }
        return QueryHelper.getCollectionItemList(this.em, params.tenantId(this.getTenant().getId()).containerType("Machine")
            .containerId(machineId).containerAttributeName("networkInterfaces"));
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
        final String nicId, final QueryParams... queryParams) throws InvalidRequestException, CloudProviderException {

        Machine m = this.getMachineByUuid(machineId);
        if (m == null || m.getState().equals(Machine.State.DELETED)) {
            throw new InvalidRequestException(" Bad machine id ");
        }
        List<MachineNetworkInterface> nics = m.getNetworkInterfaces();
        for (MachineNetworkInterface nic : nics) {
            if (nic.getUuid().equals(nicId)) {
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
    public Job updateMachineNetworkInterfaceAddress(final String machineId, final String nicId,
        final MachineNetworkInterfaceAddress addressEntry) throws InvalidRequestException, CloudProviderException {
        throw new InvalidRequestException(" Address entry cannot be updated ");
    }

}