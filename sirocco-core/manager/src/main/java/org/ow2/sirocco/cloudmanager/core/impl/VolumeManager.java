/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
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
 */
package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager.Placement;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
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
import org.ow2.sirocco.cloudmanager.core.api.remote.IRemoteVolumeManager;
import org.ow2.sirocco.cloudmanager.core.impl.command.VolumeCreateCommand;
import org.ow2.sirocco.cloudmanager.core.impl.command.VolumeDeleteCommand;
import org.ow2.sirocco.cloudmanager.core.utils.QueryHelper;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeVolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Remote(IRemoteVolumeManager.class)
@Local(IVolumeManager.class)
public class VolumeManager implements IVolumeManager {
    private static Logger logger = LoggerFactory.getLogger(VolumeManager.class.getName());

    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Inject
    private IdentityContext identityContext;

    @Resource
    private EJBContext context;

    @EJB
    private ITenantManager tenantManager;

    @EJB
    private ICloudProviderManager cloudProviderManager;

    @EJB
    private IJobManager jobManager;

    @Resource(lookup = "jms/RequestQueue")
    private Queue requestQueue;

    @Resource(lookup = "jms/ResourceStateChangeTopic")
    private Topic resourceStateChangeTopic;

    @Inject
    private JMSContext jmsContext;

    private Tenant getTenant() throws CloudProviderException {
        return this.tenantManager.getTenant(this.identityContext);
    }

    @Override
    public Job createVolume(final VolumeCreate volumeCreate) throws CloudProviderException {
        VolumeManager.logger.info("Creating Volume");

        Tenant tenant = this.getTenant();

        Placement placement = this.cloudProviderManager.placeResource(tenant.getId(), volumeCreate);

        Volume volume = new Volume();

        volume.setCapacity(volumeCreate.getVolumeTemplate().getVolumeConfig().getCapacity());
        volume.setType(volumeCreate.getVolumeTemplate().getVolumeConfig().getType());
        // XXX no way to specify whether the volume is bootable ?
        volume.setBootable(false);
        volume.setName(volumeCreate.getName());
        volume.setDescription(volumeCreate.getDescription());
        volume.setProperties(volumeCreate.getProperties() == null ? new HashMap<String, String>()
            : new HashMap<String, String>(volumeCreate.getProperties()));
        volume.setCloudProviderAccount(placement.getAccount());
        volume.setLocation(placement.getLocation());
        volume.setTenant(tenant);
        volume.setState(Volume.State.CREATING);

        volume.setCreated(new Date());
        volume.setUpdated(volume.getCreated());
        this.em.persist(volume);

        // creating Job
        Job job = new Job();
        job.setTenant(this.getTenant());
        job.setTargetResource(volume);
        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(volume);
        job.setAffectedResources(affectedResources);
        job.setCreated(new Date());
        job.setDescription("Volume creation");
        job.setState(Job.Status.RUNNING);
        job.setAction("add");// TODO: normalize!!
        job.setTimeOfStatusChange(new Date());// now
        this.em.persist(job);
        this.em.flush();

        ObjectMessage message = this.jmsContext.createObjectMessage(new VolumeCreateCommand(volumeCreate)
            .setAccount(placement.getAccount()).setLocation(placement.getLocation()).setResourceId(volume.getId()).setJob(job));
        this.jmsContext.createProducer().send(this.requestQueue, message);

        return job;
    }

    @Override
    public void syncVolume(final int volumeId, final Volume updatedVolume, final int jobId) throws CloudProviderException {
        Volume volume = this.em.find(Volume.class, volumeId);
        Job job = this.em.find(Job.class, jobId);
        if (updatedVolume == null) {
            volume.setState(Volume.State.DELETED);
            // detach deleted volume from machines if any
            List<MachineVolume> attachments = this.getVolumeAttachments(volume.getUuid());
            for (MachineVolume attachment : attachments) {
                attachment.getOwner().removeMachineVolume(attachment);
                attachment.setState(MachineVolume.State.DELETED);
                attachment.setOwner(null);
            }
        } else {
            volume.setState(updatedVolume.getState());
            volume.setUpdated(new Date());
        }
        this.fireVolumeStateChangeEvent(volume);
        job.setState(Job.Status.SUCCESS);
    }

    @Override
    public VolumeConfiguration createVolumeConfiguration(final VolumeConfiguration volumeConfig) throws CloudProviderException {
        Tenant tenant = this.getTenant();

        if (volumeConfig.getName() != null) {
            if (!this.em.createQuery("SELECT v FROM VolumeConfiguration v WHERE v.tenant.id=:tenantId AND v.name=:name")
                .setParameter("tenantId", tenant.getId()).setParameter("name", volumeConfig.getName()).getResultList()
                .isEmpty()) {
                throw new CloudProviderException("VolumeConfiguration already exists with name " + volumeConfig.getName());
            }
        }
        volumeConfig.setTenant(tenant);
        volumeConfig.setCreated(new Date());
        this.em.persist(volumeConfig);
        this.em.flush();
        return volumeConfig;
    }

    @Override
    public VolumeTemplate createVolumeTemplate(final VolumeTemplate volumeTemplate) throws CloudProviderException {
        Tenant tenant = this.getTenant();

        if (volumeTemplate.getName() != null) {
            if (!this.em.createQuery("SELECT v FROM VolumeTemplate v WHERE v.tenant.id=:tenantId AND v.name=:name")
                .setParameter("tenantId", tenant.getId()).setParameter("name", volumeTemplate.getName()).getResultList()
                .isEmpty()) {
                throw new CloudProviderException("VolumeTemplate already exists with name " + volumeTemplate.getName());
            }
        }
        volumeTemplate.setTenant(tenant);
        volumeTemplate.setCreated(new Date());
        this.em.persist(volumeTemplate);
        this.em.flush();
        return volumeTemplate;
    }

    @Override
    public Volume getVolumeById(final int volumeId) throws CloudProviderException {
        Volume volume = this.em.find(Volume.class, volumeId);
        if (volume == null || volume.getState() == Volume.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid volume id " + volumeId);
        }
        volume.setAttachments(this.getVolumeAttachments(volume.getUuid()));
        return volume;
    }

    @Override
    public Volume getVolumeByUuid(final String volumeUuid) throws ResourceNotFoundException, CloudProviderException {
        try {
            Volume result = this.em.createNamedQuery("Volume.findByUuid", Volume.class).setParameter("uuid", volumeUuid)
                .getSingleResult();
            result.setAttachments(this.getVolumeAttachments(result.getUuid()));
            return result;
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public VolumeConfiguration getVolumeConfigurationById(final int volumeConfigId) throws CloudProviderException {
        VolumeConfiguration result = this.em.find(VolumeConfiguration.class, volumeConfigId);
        if (result == null) {
            throw new ResourceNotFoundException(" Invalid Volume Configuration id " + volumeConfigId);
        }
        return result;
    }

    @Override
    public VolumeConfiguration getVolumeConfigurationByUuid(final String volumeConfigUuid) throws CloudProviderException {
        try {
            return this.em.createNamedQuery("VolumeConfiguration.findByUuid", VolumeConfiguration.class)
                .setParameter("uuid", volumeConfigUuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public VolumeConfiguration getVolumeConfigurationAttributes(final String volumeConfigUuid, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        VolumeConfiguration result = this.getVolumeConfigurationByUuid(volumeConfigUuid);
        return result;
    }

    @Override
    public VolumeTemplate getVolumeTemplateById(final int volumeTemplateId) throws CloudProviderException {
        VolumeTemplate result = this.em.find(VolumeTemplate.class, volumeTemplateId);
        if (result == null) {
            throw new ResourceNotFoundException(" Invalid Volume Template id " + volumeTemplateId);
        }
        return result;
    }

    @Override
    public VolumeTemplate getVolumeTemplateByUuid(final String volumeTemplateUuid) throws CloudProviderException {
        try {
            return this.em.createNamedQuery("VolumeTemplate.findByUuid", VolumeTemplate.class)
                .setParameter("uuid", volumeTemplateUuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public VolumeTemplate getVolumeTemplateAttributes(final String volumeTemplateUuid, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        VolumeTemplate result = this.getVolumeTemplateByUuid(volumeTemplateUuid);
        return result;
    }

    @Override
    public Volume getVolumeAttributes(final String volumeUuid, final List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException {
        Volume result = this.getVolumeByUuid(volumeUuid);
        return result;
    }

    private List<Volume> getVolumes() throws CloudProviderException {
        List<Volume> result = QueryHelper.getEntityList("Volume", this.em, this.getTenant().getId(), Volume.State.DELETED,
            false);
        // FIXME
        for (Volume vol : result) {
            vol.setAttachments(this.getVolumeAttachments(vol.getUuid()));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public QueryResult<Volume> getVolumes(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("Volume", Volume.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .stateToIgnore(Volume.State.DELETED));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public QueryResult<Volume> getVolumes(final QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException {
        if (queryParams.length == 0) {
            List<Volume> volumes = this.getVolumes();
            return new QueryResult<Volume>(volumes.size(), volumes);
        }
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("Volume", Volume.class).params(
            queryParams[0]);
        return QueryHelper
            .getEntityList(this.em, params.tenantId(this.getTenant().getId()).stateToIgnore(Volume.State.DELETED));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<VolumeConfiguration> getVolumeConfigurations() throws CloudProviderException {
        return this.em
            .createQuery("SELECT c FROM VolumeConfiguration c WHERE c.tenant.id=:tenantId", VolumeConfiguration.class)
            .setParameter("tenantId", this.getTenant().getId()).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public QueryResult<VolumeConfiguration> getVolumeConfigurations(final int first, final int last,
        final List<String> filters, final List<String> attributes) throws CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("VolumeConfiguration",
            VolumeConfiguration.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .returnPublicEntities());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<VolumeTemplate> getVolumeTemplates() throws CloudProviderException {
        return this.em.createQuery("SELECT c FROM VolumeTemplate c WHERE c.tenant.id=:tenantId", VolumeTemplate.class)
            .setParameter("tenantId", this.getTenant().getId()).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public QueryResult<VolumeTemplate> getVolumeTemplates(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("VolumeTemplate", VolumeTemplate.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .filterEmbbededTemplate().returnPublicEntities());
    }

    @SuppressWarnings("unchecked")
    private boolean updateCloudEntityAttributes(final CloudEntity entity, final Map<String, Object> attributes) {
        boolean updated = false;
        if (attributes.containsKey("name")) {
            entity.setName((String) attributes.get("name"));
            updated = true;
        }

        if (attributes.containsKey("description")) {
            entity.setDescription((String) attributes.get("description"));
            updated = true;
        }

        if (attributes.containsKey("properties")) {
            entity.setProperties((Map<String, String>) attributes.get("properties"));
            updated = true;
        }
        return updated;
    }

    @SuppressWarnings("unchecked")
    private boolean updateCloudResourceAttributes(final CloudResource entity, final Map<String, Object> attributes) {
        boolean updated = false;
        if (attributes.containsKey("name")) {
            entity.setName((String) attributes.get("name"));
            updated = true;
        }

        if (attributes.containsKey("description")) {
            entity.setDescription((String) attributes.get("description"));
            updated = true;
        }

        if (attributes.containsKey("properties")) {
            entity.setProperties((Map<String, String>) attributes.get("properties"));
            updated = true;
        }
        return updated;
    }

    @Override
    public Job updateVolumeAttributes(final String volumeId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        Volume volume = this.getVolumeByUuid(volumeId);
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Job updateVolume(final Volume volume) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateVolumeConfiguration(final VolumeConfiguration volumeConfiguration) throws InvalidRequestException,
        ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateVolumeConfigurationAttributes(final String volumeConfigId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        VolumeConfiguration volumeConfig = this.getVolumeConfigurationByUuid(volumeConfigId);
        if (volumeConfig == null) {
            throw new ResourceNotFoundException();
        }
        boolean updated = this.updateCloudEntityAttributes(volumeConfig, attributes);
        if (attributes.containsKey("format")) {
            volumeConfig.setFormat((String) attributes.get("format"));
            updated = true;
        }
        if (attributes.containsKey("capacity")) {
            volumeConfig.setCapacity((Integer) attributes.get("capacity"));
            updated = true;
        }
        if (attributes.containsKey("type")) {
            volumeConfig.setType((String) attributes.get("type"));
            updated = true;
        }
        if (updated) {
            volumeConfig.setUpdated(new Date());
        }
    }

    @Override
    public void updateVolumeTemplate(final VolumeTemplate volumeTemplate) throws InvalidRequestException,
        ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateVolumeTemplateAttributes(final String volumeTemplateId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        VolumeTemplate volumeTemplate = this.getVolumeTemplateByUuid(volumeTemplateId);
        if (volumeTemplate == null) {
            throw new ResourceNotFoundException();
        }
        boolean updated = this.updateCloudEntityAttributes(volumeTemplate, attributes);
        if (attributes.containsKey("volumeConfig")) {
            VolumeConfiguration config = (VolumeConfiguration) attributes.get("volumeConfig");
            config = this.getVolumeConfigurationByUuid(config.getUuid());
            volumeTemplate.setVolumeConfig(config);
            updated = true;
        }
        if (updated) {
            volumeTemplate.setUpdated(new Date());
        }
    }

    private void fireVolumeStateChangeEvent(final Volume volume) {
        this.jmsContext.createProducer().setProperty("tenantId", volume.getTenant().getUuid())
            .send(this.resourceStateChangeTopic, new ResourceStateChangeEvent(volume));
    }

    @Override
    public void updateVolumeState(final int volumeId, final Volume.State state) throws CloudProviderException {
        Volume volume = this.getVolumeById(volumeId);
        volume.setState(state);
        this.fireVolumeStateChangeEvent(volume);
    }

    @Override
    public Job deleteVolume(final String volumeId) throws ResourceNotFoundException, CloudProviderException {
        VolumeManager.logger.info("Deleting Volume " + volumeId);
        Volume volume = this.getVolumeByUuid(volumeId);

        if (!volume.getAttachments().isEmpty()) {
            throw new ResourceConflictException("Volume in use");
        }

        volume.setState(Volume.State.DELETING);
        this.fireVolumeStateChangeEvent(volume);
        this.em.merge(volume);

        Tenant tenant = this.getTenant();

        // creating Job
        Job job = new Job();
        job.setTenant(tenant);
        job.setTargetResource(volume);
        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(volume);
        job.setAffectedResources(affectedResources);
        job.setCreated(new Date());
        job.setDescription("Volume deletion");
        job.setState(Job.Status.RUNNING);
        job.setAction("delete");// TODO: normalize!!
        job.setTimeOfStatusChange(new Date());// now
        this.em.persist(job);
        this.em.flush();

        ObjectMessage message = this.jmsContext.createObjectMessage(new VolumeDeleteCommand().setResourceId(volume.getId())
            .setJob(job));
        this.jmsContext.createProducer().send(this.requestQueue, message);

        return job;
    }

    @Override
    public void deleteVolumeConfiguration(final String volumeConfigUuid) throws CloudProviderException {
        VolumeConfiguration volumeConfig = this.getVolumeConfigurationByUuid(volumeConfigUuid);
        this.em.remove(volumeConfig);
    }

    @Override
    public void deleteVolumeTemplate(final String volumeTemplateUuid) throws CloudProviderException {
        VolumeTemplate volumeTemplate = this.getVolumeTemplateByUuid(volumeTemplateUuid);
        this.em.remove(volumeTemplate);
    }

    private Volume getVolumeByProviderAssignedId(final String providerAssignedId) {
        Volume volume = (Volume) this.em.createNamedQuery("Volume.findVolumeByProviderAssignedId")
            .setParameter("providerAssignedId", providerAssignedId).getSingleResult();
        return volume;
    }

    private VolumeImage getVolumeImageByProviderAssignedId(final String providerAssignedId) {
        VolumeImage volumeImage = (VolumeImage) this.em.createNamedQuery("VolumeImage.findVolumeByProviderAssignedId")
            .setParameter("providerAssignedId", providerAssignedId).getSingleResult();
        return volumeImage;
    }

    @Override
    public VolumeImage getVolumeImageById(final int volumeImageId) throws ResourceNotFoundException {
        VolumeImage volumeImage = this.em.find(VolumeImage.class, volumeImageId);
        if (volumeImage == null || volumeImage.getState() == VolumeImage.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid volumeImage id " + volumeImageId);
        }
        return volumeImage;
    }

    @Override
    public VolumeImage getVolumeImageByUuid(final String volumeImageUuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("VolumeImage.findByUuid", VolumeImage.class).setParameter("uuid", volumeImageUuid)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public VolumeImage getVolumeImageAttributes(final String volumeImageId, final List<String> attributes)
        throws ResourceNotFoundException {
        VolumeImage volumeImage = this.getVolumeImageByUuid(volumeImageId);
        return UtilsForManagers.fillResourceAttributes(volumeImage, attributes);
    }

    @Override
    public Job createVolumeImage(final VolumeImage volumeImage) throws InvalidRequestException, CloudProviderException {
        return this.newVolumeImage(volumeImage, null);
    }

    @Override
    public Job createVolumeSnapshot(final Volume volume, final VolumeImage volumeImage) throws InvalidRequestException,
        CloudProviderException {
        return this.newVolumeImage(volumeImage, volume);
    }

    /**
     * Create a new VolumeImage either from binary data (initialLocation) or by snapshotting a volume
     * 
     * @param volumeImage
     * @param volumeToSnapshot if not null, volume to snapshot
     * @return
     * @throws InvalidRequestException
     * @throws CloudProviderException
     */
    private Job newVolumeImage(final VolumeImage volumeImage, Volume volumeToSnapshot) throws InvalidRequestException,
        CloudProviderException {
        VolumeManager.logger.info("Creating VolumeImage");

        // retrieve tenant
        Tenant tenant = this.getTenant();

        ICloudProviderConnector connector = null;
        Placement placement = null;

        if (volumeToSnapshot != null) {
            // TODO:workflowconnector =
            // this.getCloudProviderConnector(volumeToSnapshot.getCloudProviderAccount(),
            // TODO:workflow volumeToSnapshot.getLocation());
            placement = new Placement(volumeToSnapshot.getCloudProviderAccount(), volumeToSnapshot.getLocation());
        } else {
            connector = null;// this.getCloudProviderConnector(placement.getAccount(),
                             // placement.getLocation());
        }
        if (connector == null) {
            throw new CloudProviderException("Cannot retrieve cloud provider connector "
                + placement.getAccount().getCloudProvider().getCloudProviderType());
        }

        // delegates volume creation to cloud provider connector
        Job providerJob = null;

        try {
            IVolumeService volumeService = connector.getVolumeService();
            if (volumeToSnapshot == null) {
                // TODO:workflowproviderJob =
                // volumeService.createVolumeImage(volumeImage);
            } else {
                volumeToSnapshot = this.getVolumeById(volumeToSnapshot.getId());
                // TODO:workflowproviderJob =
                // volumeService.createVolumeSnapshot(volumeToSnapshot.getProviderAssignedId(),
                // volumeImage);
            }
        } catch (ConnectorException e) {
            VolumeManager.logger.error("Failed to create volume: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        // if by chance the job is done and has failed, bail out
        if (providerJob.getState() == Job.Status.CANCELLED || providerJob.getState() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        // prepare the VolumeImage entity to be persisted

        volumeImage.setProviderAssignedId(providerJob.getTargetResource().getProviderAssignedId());
        volumeImage.setCloudProviderAccount(placement.getAccount());
        volumeImage.setLocation(placement.getLocation());
        volumeImage.setTenant(tenant);

        volumeImage.setState(VolumeImage.State.CREATING);
        this.em.persist(volumeImage);

        if (volumeToSnapshot != null) {
            VolumeVolumeImage volumeVolumeImage = new VolumeVolumeImage();
            volumeVolumeImage.setVolumeImage(volumeImage);
            volumeVolumeImage.setState(VolumeVolumeImage.State.SNAPSHOTTING);
            this.em.persist(volumeVolumeImage);
            volumeToSnapshot.getImages().add(volumeVolumeImage);
        }

        this.em.flush();

        Job job = new Job();
        job.setTenant(tenant);
        job.setTargetResource(volumeImage);
        List<CloudResource> affectedResources = new ArrayList<CloudResource>();
        affectedResources.add(volumeImage);
        job.setAffectedResources(affectedResources);
        job.setCreated(new Date());
        job.setDescription("VolumeImage creation");
        job.setProviderAssignedId(providerJob.getProviderAssignedId());
        job.setState(providerJob.getState());
        job.setAction(providerJob.getAction());
        job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
        this.em.persist(job);
        this.em.flush();

        try {
            UtilsForManagers.emitJobListenerMessage(providerJob.getProviderAssignedId(), this.context);
        } catch (Exception e) {
            VolumeManager.logger.error(e.getMessage(), e);
        }
        return job;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public QueryResult<VolumeImage> getVolumeImages(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("VolumeImage", VolumeImage.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public QueryResult<VolumeImage> getVolumeImages(final QueryParams... queryParams) throws InvalidRequestException,
        CloudProviderException {
        if (queryParams.length == 0) {
            List<VolumeImage> images = this.getVolumeImages();
            return new QueryResult<VolumeImage>(images.size(), images);
        }
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("VolumeImage", VolumeImage.class)
            .params(queryParams[0]);
        return QueryHelper.getEntityList(this.em, params.tenantId(this.getTenant().getId()));
    }

    private List<VolumeImage> getVolumeImages() throws CloudProviderException {
        return QueryHelper.getEntityList("VolumeImage", this.em, this.getTenant().getId(), VolumeImage.State.DELETED, false);
    }

    @Override
    public Job updateVolumeImage(final VolumeImage volumeImage) throws InvalidRequestException, ResourceNotFoundException,
        CloudProviderException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Job updateVolumeImageAttributes(final String volumeImageId, final Map<String, Object> updatedAttributes)
        throws InvalidRequestException, ResourceNotFoundException, CloudProviderException {
        VolumeImage volumeImage = this.getVolumeImageByUuid(volumeImageId);
        boolean updated = this.updateCloudResourceAttributes(volumeImage, updatedAttributes);
        if (updated) {
            volumeImage.setUpdated(new Date());
        }
        // TODO call job manager
        Job j = new Job();
        j.setTargetResource(volumeImage);
        j.setAction("edit");
        j.setState(Status.SUCCESS);
        this.em.persist(j);
        return j;
    }

    @Override
    public Job deleteVolumeImage(final String volumeImageId) throws ResourceNotFoundException, CloudProviderException {
        VolumeImage volumeImage = this.getVolumeImageByUuid(volumeImageId);
        if (volumeImage == null) {
            throw new ResourceNotFoundException("VolumeImage " + volumeImageId + " doesn't not exist");
        }

        // delegates volume deletion to cloud provider connector
        // TODO:workflow ICloudProviderConnector connector =
        // this.getCloudProviderConnector(volumeImage.getCloudProviderAccount(),
        // TODO:workflow volumeImage.getLocation());
        Job providerJob = null;

        // TODO:workflowtry {
        // TODO:workflowIVolumeService volumeService =
        // connector.getVolumeService();
        // TODO:workflowproviderJob =
        // volumeService.deleteVolumeImage(volumeImage.getProviderAssignedId());
        // TODO:workflow} catch (ConnectorException e) {
        // TODO:workflowVolumeManager.logger.error("Failed to delete volumeImage: ",
        // e);
        // TODO:workflowthrow new CloudProviderException(e.getMessage());
        // TODO:workflow}

        // if by change the job is done and has failed, bail out
        if (providerJob.getState() == Job.Status.CANCELLED || providerJob.getState() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        volumeImage.setState(VolumeImage.State.DELETING);
        this.em.persist(volumeImage);
        this.em.flush();

        Job job = new Job();
        job.setTenant(this.getTenant());
        job.setTargetResource(volumeImage);
        job.setCreated(new Date());
        job.setDescription("VolumeImage deletion");
        job.setProviderAssignedId(providerJob.getProviderAssignedId());
        job.setState(providerJob.getState());
        job.setAction(providerJob.getAction());
        job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
        this.em.persist(job);
        this.em.flush();

        try {
            // connector.setNotificationOnJobCompletion(providerJob.getProviderAssignedId());
            UtilsForManagers.emitJobListenerMessage(providerJob.getProviderAssignedId(), this.context);
        } catch (Exception e) {
            VolumeManager.logger.error("", e);
        }
        return job;
    }

    @Override
    public VolumeVolumeImage getVolumeImageFromVolume(final String volumeId, final String volumeVolumeImageId)
        throws ResourceNotFoundException, CloudProviderException {
        Volume volume = this.getVolumeByUuid(volumeId);
        VolumeVolumeImage volumeVolumeImage = this.getVolumeVolumeImageByUuid(volumeVolumeImageId);
        if (!volume.getImages().contains(volumeVolumeImage)) {
            throw new ResourceNotFoundException();
        }
        return volumeVolumeImage;
    }

    @Override
    public List<VolumeVolumeImage> getVolumeVolumeImages(final String volumeId) throws ResourceNotFoundException,
        CloudProviderException {
        return this.getVolumeByUuid(volumeId).getImages();
    }

    public VolumeVolumeImage getVolumeVolumeImageByUuid(final String volumeVolumeImageUuid) throws ResourceNotFoundException,
        CloudProviderException {
        try {
            return this.em.createNamedQuery("VolumeVolumeImage.findByUuid", VolumeVolumeImage.class)
                .setParameter("uuid", volumeVolumeImageUuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public QueryResult<VolumeVolumeImage> getVolumeVolumeImages(final String volumeId, final int first, final int last,
        final List<String> filters, final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("VolumeVolumeImage",
            VolumeVolumeImage.class);
        return QueryHelper.getCollectionItemList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .containerType("Volume").containerId(volumeId).containerAttributeName("images"));
    }

    @Override
    public void updateVolumeImageInVolume(final String volumeId, final VolumeVolumeImage updatedVolumeVolumeImage)
        throws ResourceNotFoundException, CloudProviderException {
        VolumeVolumeImage volumeVolumeImage = this.getVolumeImageFromVolume(volumeId, updatedVolumeVolumeImage.getId()
            .toString());
        volumeVolumeImage.setName(updatedVolumeVolumeImage.getName());
        volumeVolumeImage.setDescription(updatedVolumeVolumeImage.getDescription());
        volumeVolumeImage.setProperties(updatedVolumeVolumeImage.getProperties());
        volumeVolumeImage.setUpdated(new Date());
    }

    // XXX the snapshot is removed from the list but the snapshot itself is not
    // deleted
    @Override
    public Job removeVolumeImageFromVolume(final String volumeId, final String volumeVolumeImageId)
        throws ResourceNotFoundException, CloudProviderException {
        // XXX ask the connector to perform the operation ?
        Volume volume = this.getVolumeByUuid(volumeId);
        VolumeVolumeImage volumeVolumeImage = this.getVolumeVolumeImageByUuid(volumeVolumeImageId);
        if (!volume.getImages().contains(volumeVolumeImage)) {
            throw new ResourceNotFoundException();
        }
        volume.getImages().remove(volumeVolumeImage);
        VolumeImage volumeImage = volumeVolumeImage.getVolumeImage();
        volumeVolumeImage.setVolumeImage(null);
        this.em.remove(volumeVolumeImage);

        // TODO call job manager
        Job j = new Job();
        // XXX should be VolumeVolumeImage
        j.setTargetResource(volumeImage);
        j.setAction("delete");
        j.setState(Status.SUCCESS);
        this.em.persist(j);
        return j;
    }

    @Override
    public Job addVolumeImageToVolume(final String volumeId, final VolumeVolumeImage volumeVolumeImage)
        throws ResourceNotFoundException, CloudProviderException, InvalidRequestException {
        throw new ServiceUnavailableException("Unsupported operation");
    }

    @Override
    public Job updateVolumeImageAttributesInVolume(final String volumeId, final String volumeVolumeImageId,
        final Map<String, Object> updatedAttributes) throws InvalidRequestException, ResourceNotFoundException,
        CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MachineVolume> getVolumeAttachments(final String volumeId) throws CloudProviderException {
        return this.em
            .createQuery("SELECT mv FROM MachineVolume mv WHERE mv.volume.uuid=:vid AND mv.state!=:state", MachineVolume.class)
            .setParameter("vid", volumeId).setParameter("state", MachineVolume.State.DELETED).getResultList();
    }

}
