package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactory;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactoryFinder;
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImageCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplateCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@Stateless(name = IVolumeManager.EJB_JNDI_NAME, mappedName = IVolumeManager.EJB_JNDI_NAME)
@Remote(IRemoteVolumeManager.class)
@Local(IVolumeManager.class)
public class VolumeManager implements IVolumeManager {
    private static Logger logger = Logger.getLogger(VolumeManager.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Resource
    private EJBContext context;

    @OSGiResource
    private ICloudProviderConnectorFactoryFinder connectorFactoryFinder;

    @EJB
    private IUserManager userManager;

    private ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount cloudProviderAccount) {
        VolumeManager.logger.info("Getting connector for cloud provider type "
            + cloudProviderAccount.getCloudProvider().getCloudProviderType());
        ICloudProviderConnectorFactory connectorFactory = this.connectorFactoryFinder
            .getCloudProviderConnectorFactory(cloudProviderAccount.getCloudProvider().getCloudProviderType());
        if (connectorFactory == null) {
            VolumeManager.logger.error("Cannot find connector for cloud provider type "
                + cloudProviderAccount.getCloudProvider().getCloudProviderType());
            return null;
        }
        return connectorFactory.getCloudProviderConnector(cloudProviderAccount, null);
    }

    private User getUser() throws CloudProviderException {
        String username = this.context.getCallerPrincipal().getName();
        User user = this.userManager.getUserByUsername(username);
        if (user == null) {
            throw new CloudProviderException("unknown user: " + username);
        }
        return user;
    }

    @Override
    public Job createVolume(final VolumeCreate volumeCreate) throws CloudProviderException {
        VolumeManager.logger.info("Creating Volume");

        // retrieve user
        User user = this.getUser();

        // pick up first cloud provider account associated with user
        if (user.getCloudProviderAccounts().isEmpty()) {
            throw new CloudProviderException("No cloud provider account for user " + user.getUsername());
        }
        CloudProviderAccount defaultAccount = user.getCloudProviderAccounts().iterator().next();
        ICloudProviderConnector connector = this.getCloudProviderConnector(defaultAccount);
        if (connector == null) {
            throw new CloudProviderException("Cannot find cloud provider connector "
                + defaultAccount.getCloudProvider().getCloudProviderType());
        }

        // delegates volume creation to cloud provider connector
        Job providerJob = null;

        try {
            IVolumeService volumeService = connector.getVolumeService();
            providerJob = volumeService.createVolume(volumeCreate);
        } catch (ConnectorException e) {
            VolumeManager.logger.error("Failed to create volume: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        // if by chance the job is done and has failed, bail out
        if (providerJob.getStatus() == Job.Status.CANCELLED || providerJob.getStatus() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        // prepare the Volume entity to be persisted
        // TODO: this volume should/could has been initialized by the connector
        // and returned as job.targetEntity ?

        Volume volume = new Volume();

        volume.setProviderAssignedId(providerJob.getTargetEntity().getProviderAssignedId());
        volume.setCloudProviderAccount(defaultAccount);
        volume.setCapacity(volumeCreate.getVolumeTemplate().getVolumeConfig().getCapacity());
        volume.setType(volumeCreate.getVolumeTemplate().getVolumeConfig().getType());
        // XXX no way to specify whether the volume is bootable ?
        volume.setBootable(false);
        volume.setName(volumeCreate.getName());
        volume.setDescription(volumeCreate.getDescription());
        volume.setProperties(volumeCreate.getProperties());
        volume.setUser(user);

        if (providerJob.getStatus() == Job.Status.RUNNING) {
            // job is running: persist volume+job and set up notification on job
            // completion
            volume.setState(Volume.State.CREATING);
            this.em.persist(volume);
            this.em.flush();

            Job job = new Job();
            job.setTargetEntity(volume);
            job.setCreated(new Date());
            job.setProviderAssignedId(providerJob.getProviderAssignedId());
            job.setStatus(providerJob.getStatus());
            job.setAction(providerJob.getAction());
            job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
            this.em.persist(job);
            this.em.flush();

            try {
                connector.setNotificationOnJobCompletion(providerJob.getProviderAssignedId());
            } catch (ConnectorException e) {
                VolumeManager.logger.error(e.getMessage(), e);
            }
            return job;
        } else {
            // job is done and successful: retrieve the state of the volume and
            // persist volume+job
            try {
                volume.setState(connector.getVolumeService().getVolumeState(
                    providerJob.getTargetEntity().getProviderAssignedId()));
            } catch (ConnectorException e) {
                throw new CloudProviderException(e.getMessage());
            }
            volume.setCreated(new Date());
            this.em.persist(volume);

            Job job = new Job();
            job.setTargetEntity(volume);
            job.setCreated(new Date());
            job.setProviderAssignedId(providerJob.getProviderAssignedId());
            job.setStatus(providerJob.getStatus());
            job.setAction(providerJob.getAction());
            job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
            this.em.persist(job);
            this.em.flush();
            return job;
        }
    }

    @Override
    public VolumeConfiguration createVolumeConfiguration(final VolumeConfiguration volumeConfig) throws CloudProviderException {
        User user = this.getUser();

        if (volumeConfig.getName() != null) {
            if (!this.em.createQuery("FROM VolumeConfiguration v WHERE v.user.username=:username AND v.name=:name")
                .setParameter("username", user.getUsername()).setParameter("name", volumeConfig.getName()).getResultList()
                .isEmpty()) {
                throw new CloudProviderException("VolumeConfiguration already exists with name " + volumeConfig.getName());
            }
        }
        volumeConfig.setUser(user);
        this.em.persist(volumeConfig);
        this.em.flush();
        return volumeConfig;
    }

    @Override
    public VolumeTemplate createVolumeTemplate(final VolumeTemplate volumeTemplate) throws CloudProviderException {
        User user = this.getUser();

        if (volumeTemplate.getName() != null) {
            if (!this.em.createQuery("FROM VolumeTemplate v WHERE v.user.username=:username AND v.name=:name")
                .setParameter("username", user.getUsername()).setParameter("name", volumeTemplate.getName()).getResultList()
                .isEmpty()) {
                throw new CloudProviderException("VolumeTemplate already exists with name " + volumeTemplate.getName());
            }
        }
        volumeTemplate.setUser(user);
        this.em.persist(volumeTemplate);
        this.em.flush();
        return volumeTemplate;
    }

    @Override
    public Volume getVolumeById(final String volumeId) throws ResourceNotFoundException {
        Volume volume = this.em.find(Volume.class, Integer.valueOf(volumeId));
        if (volume == null || volume.getState() == Volume.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid volume id " + volumeId);
        }
        return volume;
    }

    @Override
    public VolumeConfiguration getVolumeConfigurationById(final String volumeConfigId) throws CloudProviderException {
        VolumeConfiguration result = this.em.find(VolumeConfiguration.class, Integer.valueOf(volumeConfigId));
        if (result == null) {
            throw new ResourceNotFoundException(" Invalid Volume Configuration id " + volumeConfigId);
        }
        return result;
    }

    @Override
    public VolumeConfiguration getVolumeConfigurationAttributes(final String volumeConfigId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        VolumeConfiguration result = this.em.find(VolumeConfiguration.class, Integer.valueOf(volumeConfigId));
        if (result == null) {
            throw new ResourceNotFoundException(" Invalid Volume Configuration id " + volumeConfigId);
        }
        return result;
    }

    @Override
    public VolumeTemplate getVolumeTemplateById(final String volumeTemplateId) throws CloudProviderException {
        VolumeTemplate result = this.em.find(VolumeTemplate.class, Integer.valueOf(volumeTemplateId));
        if (result == null) {
            throw new ResourceNotFoundException(" Invalid Volume Template id " + volumeTemplateId);
        }
        return result;
    }

    @Override
    public VolumeTemplate getVolumeTemplateAttributes(final String volumeTemplateId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        VolumeTemplate result = this.em.find(VolumeTemplate.class, Integer.valueOf(volumeTemplateId));
        if (result == null) {
            throw new ResourceNotFoundException(" Invalid Volume Template id " + volumeTemplateId);
        }
        return result;
    }

    @Override
    public Volume getVolumeAttributes(final String volumeId, final List<String> attributes) throws ResourceNotFoundException,
        CloudProviderException {
        Volume result = this.em.find(Volume.class, Integer.valueOf(volumeId));
        if (result == null) {
            throw new ResourceNotFoundException(" Invalid volume id " + volumeId);
        }
        return result;
    }

    @Override
    public List<Volume> getVolumes(final List<String> attributes, final String filterExpression) throws CloudProviderException {
        // TODO
        if (filterExpression != null && !filterExpression.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        User user = this.getUser();
        return this.em.createQuery("FROM Volume v WHERE v.user.username=:username AND v.state<>'DELETED' ORDER BY v.id ")
            .setParameter("username", user.getUsername()).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Volume> getVolumes(final int first, final int last, final List<String> attributes)
        throws CloudProviderException {
        User user = this.getUser();
        Query query = this.em.createQuery("FROM Volume v WHERE v.user.username=:username AND v.state<>'DELETED' ORDER BY v.id");
        query.setParameter("username", user.getUsername());
        query.setMaxResults(last - first + 1);
        query.setFirstResult(first);
        return query.setFirstResult(first).setMaxResults(last - first + 1).getResultList();
    }

    @Override
    public List<VolumeConfiguration> getVolumeConfigurations(final List<String> attributes, final String filterExpression)
        throws CloudProviderException {
        if (filterExpression != null && !filterExpression.isEmpty()) {
            // TODO
            throw new UnsupportedOperationException();
        }
        User user = this.getUser();
        return this.em.createQuery("FROM VolumeConfiguration v WHERE v.user.username=:username ORDER BY v.id")
            .setParameter("username", user.getUsername()).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<VolumeConfiguration> getVolumeConfigurations(final int first, final int last, final List<String> attributes)
        throws CloudProviderException {
        User user = this.getUser();
        Query query = this.em.createQuery("FROM VolumeConfiguration v WHERE v.user.username=:username ORDER BY v.id");
        query.setParameter("username", user.getUsername());
        query.setMaxResults(last - first + 1);
        query.setFirstResult(first);
        return query.setFirstResult(first).setMaxResults(last - first + 1).getResultList();
    }

    @Override
    public List<VolumeTemplate> getVolumeTemplates(final List<String> attributes, final String filterExpression)
        throws CloudProviderException {
        if (filterExpression != null && !filterExpression.isEmpty()) {
            // TODO
            throw new UnsupportedOperationException();
        }
        User user = this.getUser();
        return this.em.createQuery("FROM VolumeTemplate v WHERE v.user.username=:username ORDER BY v.id")
            .setParameter("username", user.getUsername()).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<VolumeTemplate> getVolumeTemplates(final int first, final int last, final List<String> attributes)
        throws CloudProviderException {
        User user = this.getUser();
        Query query = this.em.createQuery("FROM VolumeTemplate v WHERE v.user.username=:username ORDER BY v.id");
        query.setParameter("username", user.getUsername());
        query.setMaxResults(last - first + 1);
        query.setFirstResult(first);
        return query.setFirstResult(first).setMaxResults(last - first + 1).getResultList();
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

    @Override
    public Job updateVolumeAttributes(final String volumeId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        Volume volume = this.getVolumeById(volumeId);
        if (volume == null) {
            throw new ResourceNotFoundException("Volume " + volumeId + " doesn't not exist");
        }
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Job updateVolume(final Volume volume) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
        Volume volumeInDb = this.getVolumeById(volume.getId().toString());
        if (volumeInDb == null) {
            throw new ResourceNotFoundException("Volume " + volume.getId() + " doesn't not exist");
        }
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
        VolumeConfiguration volumeConfig = this.getVolumeConfigurationById(volumeConfigId);
        if (volumeConfig == null) {
            throw new ResourceNotFoundException();
        }
        boolean updated = this.updateCloudEntityAttributes(volumeConfig, attributes);
        if (attributes.containsKey("format")) {
            volumeConfig.setFormat((String) attributes.get("format"));
            updated = true;
        }
        if (attributes.containsKey("capacity")) {
            volumeConfig.setCapacity((Disk) attributes.get("capacity"));
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
        VolumeTemplate volumeTemplate = this.getVolumeTemplateById(volumeTemplateId);
        if (volumeTemplate == null) {
            throw new ResourceNotFoundException();
        }
        boolean updated = this.updateCloudEntityAttributes(volumeTemplate, attributes);
        if (attributes.containsKey("volumeConfig")) {
            volumeTemplate.setVolumeConfig((VolumeConfiguration) attributes.get("volumeConfig"));
            updated = true;
        }
        if (updated) {
            volumeTemplate.setUpdated(new Date());
        }
    }

    @Override
    public Job deleteVolume(final String volumeId) throws ResourceNotFoundException, CloudProviderException {
        Volume volume = this.getVolumeById(volumeId);
        if (volume == null) {
            throw new ResourceNotFoundException("Volume " + volumeId + " doesn't not exist");
        }

        // delegates volume deletion to cloud provider connector
        ICloudProviderConnector connector = this.getCloudProviderConnector(volume.getCloudProviderAccount());
        Job providerJob = null;

        try {
            IVolumeService volumeService = connector.getVolumeService();
            providerJob = volumeService.deleteVolume(volume.getProviderAssignedId());
        } catch (ConnectorException e) {
            VolumeManager.logger.error("Failed to delete volume: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        // if by change the job is done and has failed, bail out
        if (providerJob.getStatus() == Job.Status.CANCELLED || providerJob.getStatus() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        if (providerJob.getStatus() == Job.Status.RUNNING) {
            // job is running: persist volume+job and set up notification on job
            // completion
            volume.setState(Volume.State.DELETING);
            this.em.persist(volume);
            this.em.flush();

            Job job = new Job();
            job.setTargetEntity(volume);
            job.setCreated(new Date());
            job.setProviderAssignedId(providerJob.getProviderAssignedId());
            job.setStatus(providerJob.getStatus());
            job.setAction(providerJob.getAction());
            job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
            this.em.persist(job);
            this.em.flush();

            try {
                connector.setNotificationOnJobCompletion(providerJob.getProviderAssignedId());
            } catch (ConnectorException e) {
                VolumeManager.logger.error("", e);
            }
            return job;
        } else {
            // job is done and successful: retrieve the state of the volume and
            // persist volume+job
            volume.setState(Volume.State.DELETED);
            this.em.persist(volume);
            this.em.flush();

            Job job = new Job();
            job.setTargetEntity(volume);
            job.setCreated(new Date());
            job.setProviderAssignedId(providerJob.getProviderAssignedId());
            job.setStatus(providerJob.getStatus());
            job.setAction(providerJob.getAction());
            job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
            this.em.persist(job);
            this.em.flush();
            return job;
        }

    }

    @Override
    public void deleteVolumeConfiguration(final String volumeConfigId) throws CloudProviderException {
        VolumeConfiguration volumeConfig = this.em.find(VolumeConfiguration.class, Integer.valueOf(volumeConfigId));
        if (volumeConfig == null) {
            throw new CloudProviderException("VolumeConfiguration does't exist with id " + volumeConfig);
        }
        this.em.remove(volumeConfig);
    }

    @Override
    public void deleteVolumeTemplate(final String volumeTemplateId) throws CloudProviderException {
        VolumeTemplate volumeTemplate = this.em.find(VolumeTemplate.class, Integer.valueOf(volumeTemplateId));
        if (volumeTemplate == null) {
            throw new CloudProviderException("VolumeTemplate does't exist with id " + volumeTemplateId);
        }
        this.em.remove(volumeTemplate);
    }

    @Override
    public VolumeCollection getVolumeCollection() throws CloudProviderException {
        User user = this.getUser();
        @SuppressWarnings("unchecked")
        List<Volume> volumes = this.em
            .createQuery("SELECT v FROM Volume v WHERE v.user.username=:username AND v.state<>'DELETED'")
            .setParameter("username", user.getUsername()).getResultList();
        VolumeCollection collection = (VolumeCollection) this.em
            .createQuery("FROM VolumeCollection m WHERE m.user.username=:username")
            .setParameter("username", user.getUsername()).getSingleResult();
        collection.setVolumes(volumes);
        return collection;
    }

    @Override
    public VolumeConfigurationCollection getVolumeConfigurationCollection() throws CloudProviderException {
        User user = this.getUser();
        @SuppressWarnings("unchecked")
        List<VolumeConfiguration> volumeConfigs = this.em
            .createQuery("SELECT v FROM VolumeConfiguration v WHERE v.user.username=:username")
            .setParameter("username", user.getUsername()).getResultList();
        VolumeConfigurationCollection collection = (VolumeConfigurationCollection) this.em
            .createQuery("FROM VolumeConfigurationCollection v WHERE v.user.username=:username")
            .setParameter("username", user.getUsername()).getSingleResult();
        collection.setVolumeConfigurations(volumeConfigs);
        return collection;
    }

    @Override
    public VolumeTemplateCollection getVolumeTemplateCollection() throws CloudProviderException {
        User user = this.getUser();
        @SuppressWarnings("unchecked")
        List<VolumeTemplate> volumeTemplates = this.em
            .createQuery("SELECT v FROM VolumeTemplate v WHERE v.user.username=:username")
            .setParameter("username", user.getUsername()).getResultList();
        VolumeTemplateCollection collection = (VolumeTemplateCollection) this.em
            .createQuery("FROM VolumeTemplateCollection v WHERE v.user.username=:username")
            .setParameter("username", user.getUsername()).getSingleResult();
        collection.setVolumeTemplates(volumeTemplates);
        return collection;
    }

    @Override
    public void updateVolumeCollection(final Map<String, Object> attributes) throws CloudProviderException {
        User user = this.getUser();
        VolumeCollection collection = (VolumeCollection) this.em
            .createQuery("FROM VolumeCollection v WHERE v.user.username=:username")
            .setParameter("username", user.getUsername()).getSingleResult();
        if (this.updateCloudEntityAttributes(collection, attributes)) {
            collection.setUpdated(new Date());
        }
    }

    @Override
    public void updateVolumeConfigurationCollection(final Map<String, Object> attributes) throws CloudProviderException {
        User user = this.getUser();
        VolumeConfigurationCollection collection = (VolumeConfigurationCollection) this.em
            .createQuery("FROM VolumeConfigurationCollection v WHERE v.user.username=:username")
            .setParameter("username", user.getUsername()).getSingleResult();
        if (this.updateCloudEntityAttributes(collection, attributes)) {
            collection.setUpdated(new Date());
        }
    }

    @Override
    public void updateVolumeTemplateCollection(final Map<String, Object> attributes) throws CloudProviderException {
        User user = this.getUser();
        VolumeTemplateCollection collection = (VolumeTemplateCollection) this.em
            .createQuery("FROM VolumeTemplateCollection v WHERE v.user.username=:username")
            .setParameter("username", user.getUsername()).getSingleResult();
        if (this.updateCloudEntityAttributes(collection, attributes)) {
            collection.setUpdated(new Date());
        }
    }

    private Volume getVolumeByProviderAssignedId(final String providerAssignedId) {
        Volume volume = (Volume) this.em.createNamedQuery(Volume.GET_VOLUME_BY_PROVIDER_ASSIGNED_ID)
            .setParameter("providerAssignedId", providerAssignedId).getSingleResult();
        return volume;
    }

    private VolumeImage getVolumeImageByProviderAssignedId(final String providerAssignedId) {
        VolumeImage volumeImage = (VolumeImage) this.em.createNamedQuery(VolumeImage.GET_VOLUMEIMAGE_BY_PROVIDER_ASSIGNED_ID)
            .setParameter("providerAssignedId", providerAssignedId).getSingleResult();
        return volumeImage;
    }

    @Override
    public boolean jobCompletionHandler(final Job job) {
        if (job.getTargetEntity() instanceof Volume) {
            return this.volumeCompletionHandler(job);
        } else if (job.getTargetEntity() instanceof VolumeImage) {
            return this.volumeImageCompletionHandler(job);
        }
        return false;
    }

    public boolean volumeCompletionHandler(final Job providerJob) {
        // retrieve the Volume whose providerAssignedId is job.getTargetEntity()
        Volume volume = null;

        try {
            volume = this.getVolumeByProviderAssignedId(providerJob.getTargetEntity().getProviderAssignedId());
        } catch (PersistenceException e) {
            VolumeManager.logger.error("Cannot find Volume with provider-assigned id " + providerJob.getTargetEntity());
            return false;
        }

        // update Volume entity
        ICloudProviderConnector connector = this.getCloudProviderConnector(volume.getCloudProviderAccount());

        if (providerJob.getAction().equals("add")) {
            if (providerJob.getStatus() == Job.Status.SUCCESS) {
                try {
                    volume.setState(connector.getVolumeService().getVolumeState(volume.getProviderAssignedId()));
                    volume.setCreated(new Date());
                    this.em.persist(volume);
                } catch (Exception ex) {
                    VolumeManager.logger.error("Failed to create volume " + volume.getName(), ex);
                }
            } else if (providerJob.getStatus() == Job.Status.FAILED) {
                volume.setState(Volume.State.ERROR);
                VolumeManager.logger.error("Failed to create volume  " + volume.getName() + ": "
                    + providerJob.getStatusMessage());
                this.em.persist(volume);
            }
        } else if (providerJob.getAction().equals("delete")) {
            if (providerJob.getStatus() == Job.Status.SUCCESS) {
                volume.setState(Volume.State.DELETED);
                this.em.persist(volume);
                this.em.flush();
            } else if (providerJob.getStatus() == Job.Status.FAILED) {
                volume.setState(Volume.State.ERROR);
                VolumeManager.logger.error("Failed to delete volume  " + volume.getName() + ": "
                    + providerJob.getStatusMessage());
                this.em.persist(volume);
            }
        }

        return true;
    }

    public boolean volumeImageCompletionHandler(final Job providerJob) {
        VolumeImage volumeImage = null;

        try {
            volumeImage = this.getVolumeImageByProviderAssignedId(providerJob.getTargetEntity().getProviderAssignedId());
        } catch (PersistenceException e) {
            VolumeManager.logger.error("Cannot find VolumeImage with provider-assigned id " + providerJob.getTargetEntity());
            return false;
        }

        ICloudProviderConnector connector = this.getCloudProviderConnector(volumeImage.getCloudProviderAccount());

        if (providerJob.getAction().equals("add")) {
            if (providerJob.getStatus() == Job.Status.SUCCESS) {
                try {
                    volumeImage.setState(connector.getVolumeService().getVolumeImage(volumeImage.getProviderAssignedId())
                        .getState());
                    volumeImage.setCreated(new Date());
                    this.em.persist(volumeImage);
                    Volume snapshottedVolume = volumeImage.getOwner();
                    if (snapshottedVolume != null) {
                        snapshottedVolume.getImages().add(volumeImage);
                        this.em.persist(volumeImage);
                    }
                } catch (Exception ex) {
                    VolumeManager.logger.error("Failed to create volume image " + volumeImage.getName(), ex);
                }
            } else if (providerJob.getStatus() == Job.Status.FAILED) {
                volumeImage.setState(VolumeImage.State.ERROR);
                VolumeManager.logger.error("Failed to create volume image  " + volumeImage.getName() + ": "
                    + providerJob.getStatusMessage());
                this.em.persist(volumeImage);
            }
        } else if (providerJob.getAction().equals("delete")) {
            if (providerJob.getStatus() == Job.Status.SUCCESS) {
                volumeImage.setState(VolumeImage.State.DELETED);
                this.em.persist(volumeImage);
                this.em.flush();
            } else if (providerJob.getStatus() == Job.Status.FAILED) {
                volumeImage.setState(VolumeImage.State.ERROR);
                VolumeManager.logger.error("Failed to delete volume image  " + volumeImage.getName() + ": "
                    + providerJob.getStatusMessage());
                this.em.persist(volumeImage);
            }
        }

        return true;
    }

    @Override
    public VolumeImageCollection getVolumeImageCollection() throws CloudProviderException {
        User user = this.getUser();
        @SuppressWarnings("unchecked")
        List<VolumeImage> volumeImages = this.em
            .createQuery("SELECT v FROM VolumeImage v WHERE v.user.username=:username AND v.state<>'DELETED'")
            .setParameter("username", user.getUsername()).getResultList();
        VolumeImageCollection collection = (VolumeImageCollection) this.em
            .createQuery("FROM VolumeImageCollection m WHERE m.user.username=:username")
            .setParameter("username", user.getUsername()).getSingleResult();
        collection.setVolumeImages(volumeImages);
        return collection;
    }

    @Override
    public List<VolumeImage> getVolumeImagesCollection(final String volumeId) throws ResourceNotFoundException,
        CloudProviderException {
        Volume volume = this.getVolumeById(volumeId);
        return volume.getImages();
    }

    @Override
    public VolumeImage getVolumeImageById(final String volumeImageId) throws ResourceNotFoundException {
        VolumeImage volumeImage = this.em.find(VolumeImage.class, Integer.valueOf(volumeImageId));
        if (volumeImage == null || volumeImage.getState() == VolumeImage.State.DELETED) {
            throw new ResourceNotFoundException(" Invalid volumeImage id " + volumeImageId);
        }
        return volumeImage;
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
     * Create a new VolumeImage either from binary data (initialLocation) or by
     * snapshotting a volume
     * 
     * @param volumeImage
     * @param volumeToSnapshot if not null, volume to snapshot
     * @return
     * @throws InvalidRequestException
     * @throws CloudProviderException
     */
    private Job newVolumeImage(final VolumeImage volumeImage, final Volume volumeToSnapshot) throws InvalidRequestException,
        CloudProviderException {
        VolumeManager.logger.info("Creating VolumeImage");

        // retrieve user
        User user = this.getUser();

        // pick up first cloud provider account associated with user
        if (user.getCloudProviderAccounts().isEmpty()) {
            throw new CloudProviderException("No cloud provider account for user " + user.getUsername());
        }
        CloudProviderAccount defaultAccount = user.getCloudProviderAccounts().iterator().next();
        ICloudProviderConnector connector = this.getCloudProviderConnector(defaultAccount);
        if (connector == null) {
            throw new CloudProviderException("Cannot find cloud provider connector "
                + defaultAccount.getCloudProvider().getCloudProviderType());
        }

        // delegates volume creation to cloud provider connector
        Job providerJob = null;

        try {
            IVolumeService volumeService = connector.getVolumeService();
            providerJob = volumeService.createVolumeImage(volumeImage);
        } catch (ConnectorException e) {
            VolumeManager.logger.error("Failed to create volume: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        // if by chance the job is done and has failed, bail out
        if (providerJob.getStatus() == Job.Status.CANCELLED || providerJob.getStatus() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        // prepare the VolumeImage entity to be persisted

        volumeImage.setProviderAssignedId(providerJob.getTargetEntity().getProviderAssignedId());
        volumeImage.setCloudProviderAccount(defaultAccount);
        volumeImage.setUser(user);

        volumeImage.setState(VolumeImage.State.CREATING);
        volumeImage.setOwner(volumeToSnapshot);
        this.em.persist(volumeImage);
        this.em.flush();

        Job job = new Job();
        job.setTargetEntity(volumeImage);
        job.setCreated(new Date());
        job.setProviderAssignedId(providerJob.getProviderAssignedId());
        job.setStatus(providerJob.getStatus());
        job.setAction(providerJob.getAction());
        job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
        this.em.persist(job);
        this.em.flush();

        try {
            connector.setNotificationOnJobCompletion(providerJob.getProviderAssignedId());
        } catch (ConnectorException e) {
            VolumeManager.logger.error(e.getMessage(), e);
        }
        return job;
    }

    @Override
    public List<VolumeImage> getVolumeImages(final int first, final int last, final List<String> attributes)
        throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        Query query = this.em
            .createQuery("FROM VolumeImage v WHERE v.user.username=:username AND v.state<>'DELETED' ORDER BY v.id");
        query.setParameter("username", user.getUsername());
        query.setMaxResults(last - first + 1);
        query.setFirstResult(first);
        return query.setFirstResult(first).setMaxResults(last - first + 1).getResultList();
    }

    @Override
    public List<VolumeImage> getVolumeImages(final List<String> attributes, final String filterExpression)
        throws InvalidRequestException, CloudProviderException {
        // TODO
        if (filterExpression != null && !filterExpression.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        User user = this.getUser();
        return this.em.createQuery("FROM VolumeImage v WHERE v.user.username=:username AND v.state<>'DELETED' ORDER BY v.id ")
            .setParameter("username", user.getUsername()).getResultList();
    }

    @Override
    public Job updateVolumeImage(final VolumeImage volumeImage) throws InvalidRequestException, ResourceNotFoundException,
        CloudProviderException {
        VolumeImage volumeImageInDb = this.getVolumeImageById(volumeImage.getId().toString());
        if (volumeImageInDb == null) {
            throw new ResourceNotFoundException("VolumeImage " + volumeImage.getId() + " doesn't not exist");
        }
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Job updateVolumeImageAttributes(final String volumeImageId, final Map<String, Object> updatedAttributes)
        throws InvalidRequestException, ResourceNotFoundException, CloudProviderException {
        VolumeImage volumeImage = this.getVolumeImageById(volumeImageId);
        if (volumeImage == null) {
            throw new ResourceNotFoundException("VolumeImage " + volumeImageId + " doesn't not exist");
        }
        boolean updated = this.updateCloudEntityAttributes(volumeImage, updatedAttributes);
        if (updated) {
            volumeImage.setUpdated(new Date());
        }
        // TODO call job manager
        Job j = new Job();
        j.setTargetEntity(volumeImage);
        j.setAction("edit");
        j.setStatus(Status.SUCCESS);
        this.em.persist(j);
        return j;
    }

    @Override
    public Job deleteVolumeImage(final String volumeImageId) throws ResourceNotFoundException, CloudProviderException {
        VolumeImage volumeImage = this.getVolumeImageById(volumeImageId);
        if (volumeImage == null) {
            throw new ResourceNotFoundException("VolumeImage " + volumeImageId + " doesn't not exist");
        }

        // delegates volume deletion to cloud provider connector
        ICloudProviderConnector connector = this.getCloudProviderConnector(volumeImage.getCloudProviderAccount());
        Job providerJob = null;

        try {
            IVolumeService volumeService = connector.getVolumeService();
            providerJob = volumeService.deleteVolumeImage(volumeImage.getProviderAssignedId());
        } catch (ConnectorException e) {
            VolumeManager.logger.error("Failed to delete volumeImage: ", e);
            throw new CloudProviderException(e.getMessage());
        }

        // if by change the job is done and has failed, bail out
        if (providerJob.getStatus() == Job.Status.CANCELLED || providerJob.getStatus() == Job.Status.FAILED) {
            throw new CloudProviderException(providerJob.getStatusMessage());
        }

        volumeImage.setState(VolumeImage.State.DELETING);
        this.em.persist(volumeImage);
        this.em.flush();

        Job job = new Job();
        job.setTargetEntity(volumeImage);
        job.setCreated(new Date());
        job.setProviderAssignedId(providerJob.getProviderAssignedId());
        job.setStatus(providerJob.getStatus());
        job.setAction(providerJob.getAction());
        job.setTimeOfStatusChange(providerJob.getTimeOfStatusChange());
        this.em.persist(job);
        this.em.flush();

        try {
            connector.setNotificationOnJobCompletion(providerJob.getProviderAssignedId());
        } catch (ConnectorException e) {
            VolumeManager.logger.error("", e);
        }
        return job;
    }

    @Override
    public Job removeImageFromVolume(final String volumeId, final String imageId) throws ResourceNotFoundException,
        CloudProviderException {
        // XXX ask the connector to perform the operation ?
        Volume volume = this.getVolumeById(volumeId);
        if (volume == null) {
            throw new ResourceNotFoundException("Volume " + volumeId + " doesn't not exist");
        }
        VolumeImage volumeImage = this.getVolumeImageById(imageId);
        if (volumeImage == null) {
            throw new ResourceNotFoundException("VolumeImage " + imageId + " doesn't not exist");
        }
        volume.getImages().remove(volumeImage);
        volumeImage.setOwner(null);

        // TODO call job manager
        Job j = new Job();
        j.setTargetEntity(volume);
        j.setAction("delete");
        j.setStatus(Status.SUCCESS);
        this.em.persist(j);
        return j;
    }

}
