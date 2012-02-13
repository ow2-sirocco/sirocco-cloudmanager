package org.ow2.sirocco.cloudmanager.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Job;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Project;
import org.ow2.sirocco.cloudmanager.provider.api.entity.User;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Visibility;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Volume;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.DuplicateNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidCloudProviderAccountException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidCloudProviderIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidVolumeIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.ResourceQuotaExceededException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.VolumeInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProvider;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactory;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactoryFinder;
import org.ow2.sirocco.cloudmanager.provider.api.service.IVolumeService;
import org.ow2.sirocco.cloudmanager.provider.api.service.VolumeCreate;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobCompletionEvent;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobManager;
import org.ow2.sirocco.cloudmanager.service.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.service.api.IEventPublisher;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteVolumeManager;
import org.ow2.sirocco.cloudmanager.service.api.IUserProjectManager;
import org.ow2.sirocco.cloudmanager.service.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.utils.PermissionChecker;

@Stateless(name = IVolumeManager.EJB_JNDI_NAME, mappedName = IVolumeManager.EJB_JNDI_NAME)
@Remote(IRemoteVolumeManager.class)
@Local(IVolumeManager.class)
public class VolumeManagerBean implements IVolumeManager {
    private static Logger logger = Logger.getLogger(VolumeManagerBean.class.getName());

    private static final long MAX_VOLUME_SIZE_IN_MB_ALLOWED = 500;

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @EJB
    private IUserProjectManager userProjectManager;

    @EJB
    private ICloudProviderManager cloudProviderManager;

    @EJB
    private IEventPublisher eventPublisher;

    @OSGiResource
    private ICloudProviderFactoryFinder cloudProviderFactoryFinder;

    @OSGiResource
    private JobManager jobManager;

    @Override
    public Volume createVolume(final String projectId, final String username, final String cloudProviderAccountId,
        CloudProviderLocation location, final VolumeCreate volumeCreate) throws InvalidUsernameException, InvalidNameException,
        ResourceQuotaExceededException, DuplicateNameException, InvalidProjectIdException, PermissionDeniedException,
        CloudProviderException {

        User user = this.userProjectManager.getUserByUsername(username);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + username);
        }

        Project project = this.userProjectManager.getProjectByProjectId(projectId);
        if (project == null) {
            throw new InvalidProjectIdException("Unknown project " + projectId);
        }

        // check project authorization
        if (!new PermissionChecker().canCreateVolume(user, project)) {
            throw new PermissionDeniedException("Permission denied for user " + username + " in the projectId " + projectId);
        }

        String volName = volumeCreate.getName();

        // check Volume name validity
        if (!this.volumeNameIsValid(volName)) {
            throw new InvalidNameException("Invalid Volume name " + volName);
        }

        // check if Volume name already taken
        if (this.checkIfVolumeNameExistForProject(volName, projectId)) {
            throw new DuplicateNameException("Volume name " + volName + " already exists in this project !");
        }

        long capacityInMB = volumeCreate.getConfiguration().getCapacityInMB();

        // check if capacity if correct
        if (capacityInMB <= 0 && capacityInMB > VolumeManagerBean.MAX_VOLUME_SIZE_IN_MB_ALLOWED) {
            throw new CloudProviderException("Invalid capacity");
        }

        if (!this.checkResourceQuota(project, capacityInMB)) {
            throw new ResourceQuotaExceededException("storage quota exceeded");
        }

        // Check the cloudProviderAccount Id
        CloudProviderAccount cloudProviderAccount = this.cloudProviderManager
            .getCloudProviderAccountById(cloudProviderAccountId);
        if (cloudProviderAccount == null) {
            throw new InvalidCloudProviderAccountException("Wrong cloud provider account id: " + cloudProviderAccountId);
        }

        // Check that the cloudProviderAccount belongs to the project
        if (!cloudProviderAccount.getProjects().contains(project)) {
            throw new IllegalArgumentException("The given cloud provider account ID: " + cloudProviderAccountId
                + " is NOT associated with  project " + projectId);
        }

        if (location == null) {
            List<CloudProviderLocation> locations = null;

            try {
                locations = this.cloudProviderManager.listCloudProviderLocationsByCloudProviderId(cloudProviderAccount
                    .getCloudProvider().getId().toString());
            } catch (InvalidCloudProviderIdException ex) {
                // should not happen
                VolumeManagerBean.logger.log(Level.SEVERE, "internal error", ex);
            }
            if (locations != null && !locations.isEmpty()) {
                location = locations.get(0);
            } else {
                throw new InvalidArgumentException("Cannot find cloud provider location");
            }
        }

        VolumeManagerBean.logger.info("Creating Volume " + volName + " size=" + capacityInMB + ".");
        Volume volume = new Volume();
        volume.setCapacityInMB(capacityInMB);
        // XXX
        volume.setBootable(false);
        volume.setDescription(volumeCreate.getDescription());
        volume.setName(volName);
        volume.setProject(project);
        volume.setState(Volume.State.CREATING);
        volume.setUser(user);
        volume.setVisibility(Visibility.USER);
        volume.setCloudProviderAccount(cloudProviderAccount);
        volume.setCloudProvider(cloudProviderAccount.getCloudProvider());
        volume.setLocation(location.getLocationId());

        this.em.persist(volume);
        this.em.flush();

        try {
            IVolumeService volumeService = this.getCloudProviderVolumeService(volume.getCloudProviderAccount(),
                volume.getLocation());
            Job<?> job = volumeService.createVolume(volumeCreate);
            volume.setActiveJob(job.getId());
        } catch (CloudProviderException e) {
            VolumeManagerBean.logger.log(Level.SEVERE, "CloudProviderException: ", e);
            volume.setState(Volume.State.ERROR);
            this.em.persist(volume);
            this.em.flush();
        }

        this.em.persist(volume);
        this.em.flush();
        this.eventPublisher.notifyVolumeCreation(volume);
        return volume;
    }

    private boolean volumeNameIsValid(final String volName) {
        if (volName == null || volName.equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_.-]*$");
        Matcher matcher = pattern.matcher(volName);
        return matcher.find();
    }

    private boolean checkResourceQuota(final Project project, final long capacityInMB) {
        long diskUsedInMB = this.userProjectManager.countDiskByProject(project.getProjectId()) + capacityInMB;

        if (project.getResourceQuota().getDiskQuotaInMB() > 0 && diskUsedInMB > project.getResourceQuota().getDiskQuotaInMB()) {
            return false;
        }
        return true;
    }

    private boolean checkIfVolumeNameExistForProject(final String volName, final String projectId) {
        @SuppressWarnings("unchecked")
        List<Volume> list = this.em
            .createQuery("FROM Volume v WHERE v.name=:name AND v.project.projectId=:projectId AND v.state<>'DELETED'")
            .setParameter("name", volName).setParameter("projectId", projectId).getResultList();
        if (null == list || list.size() == 0) {
            return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Volume> getAllVolumes() {
        return this.em.createQuery("FROM Volume v WHERE v.state<>'DELETED'").getResultList();
    }

    public Volume getVolumeByName(final String volumeName) {
        @SuppressWarnings("unchecked")
        List<Volume> list = this.em.createQuery("FROM Volume v WHERE v.name=:name").setParameter("name", volumeName)
            .getResultList();
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public Volume getVolumeById(final String volumeId) {
        return this.em.find(Volume.class, Integer.valueOf(volumeId));
    }

    @Override
    public Volume getVolumeByProviderAssignedId(final String volumeProviderAssignedId) {
        @SuppressWarnings("unchecked")
        List<Volume> list = this.em.createNamedQuery(Volume.FIND_VOLUMES_BY_PROVIDER_ID)
            .setParameter("providerAssignedId", volumeProviderAssignedId).getResultList();
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    @Override
    public void deleteVolume(final String username, final String volumeId) throws InvalidUsernameException,
        InvalidProjectIdException, InvalidVolumeIdException, VolumeInUseException, PermissionDeniedException,
        CloudProviderException {
        User user = this.userProjectManager.getUserByUsername(username);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + username);
        }

        Volume volume = this.getVolumeById(volumeId);
        if (volume == null) {
            throw new InvalidVolumeIdException("volumeId " + volumeId + " unknown");
        }

        if (volume.getMachines().size() > 0) {
            throw new VolumeInUseException("volume " + volume.getName() + " is in use");
        }

        // check project authorization
        if (!new PermissionChecker().canDestroyVolume(user, volume)) {
            throw new PermissionDeniedException("Permission denied for user " + username);
        }

        if (volume.getCloudProviderAccount() == null) {
            volume.setState(Volume.State.DELETED);
            this.em.merge(volume);
            this.em.flush();
            this.eventPublisher.notifyVolumeChange(volume);
        } else {
            try {
                IVolumeService volumeService = this.getCloudProviderVolumeService(volume.getCloudProviderAccount(),
                    volume.getLocation());
                Job<Void> job = volumeService.destroyVolume(volume.getProviderAssignedId());

                volume.setState(Volume.State.DELETING);
                volume.setActiveJob(job.getId());

                this.eventPublisher.notifyVolumeChange(volume);

                this.em.merge(volume);
                this.em.flush();
            } catch (CloudProviderException e) {
                VolumeManagerBean.logger.log(Level.SEVERE, "CloudProviderException: ", e);
                volume.setState(Volume.State.DELETED);
                this.em.merge(volume);
                this.em.flush();
                this.eventPublisher.notifyVolumeChange(volume);
            }
        }

    }

    @Override
    public List<Machine> getMachinesUsingVolume(final String volumeId) throws InvalidVolumeIdException {
        Volume volume = this.getVolumeById(volumeId);
        if (volume == null) {
            throw new InvalidVolumeIdException("Volume id " + volumeId + " invalid");
        }
        return new ArrayList<Machine>(volume.getMachines());
    }

    @Override
    public boolean handleJobCompletion(final JobCompletionEvent event) {
        Volume volume = null;

        try {
            volume = (Volume) this.em.createNamedQuery(Volume.GET_VOLUME_BY_JOB_ID).setParameter("activeJob", event.getJobId())
                .getSingleResult();
        } catch (NoResultException ex) {
            VolumeManagerBean.logger.severe("Cannot find Volume with job id " + event.getJobId());
            return false;
        }

        Job<?> job = this.jobManager.getJobById(event.getJobId());
        if (job == null) {
            VolumeManagerBean.logger.warning("Job of Volume " + volume.getName() + " has been reaped");
            volume.setState(Volume.State.ERROR);
            volume.setActiveJob(null);
            this.em.persist(volume);
            return true;
        }
        volume.setActiveJob(null);
        VolumeManagerBean.logger.info("Job " + job);
        if (job.getAction().equals("volume.create")) {
            if (job.getStatus() == Job.Status.SUCCESS) {
                @SuppressWarnings("unchecked")
                Job<Volume> createJob = (Job<Volume>) job;
                try {
                    Volume volumeFromProvider = createJob.getResult().get();
                    volume.setProviderAssignedId(volumeFromProvider.getProviderAssignedId());
                    volume.setState(Volume.State.AVAILABLE);
                    volume.setCreated(new Date());
                    this.eventPublisher.notifyVolumeChange(volume);
                    this.em.persist(volume);
                } catch (Exception ex) {
                    VolumeManagerBean.logger.log(Level.SEVERE, "Failed to create volume " + volume.getName(), ex);
                }
            } else if (job.getStatus() == Job.Status.FAILED) {
                volume.setState(Volume.State.ERROR);
                this.eventPublisher.notifyVolumeChange(volume);
                VolumeManagerBean.logger.severe("Failed to create volume  " + volume.getName() + ": " + job.getStatusMessage());
                this.em.persist(volume);
            }
        } else if (job.getAction().equals("volume.destroy")) {
            if (job.getStatus() == Job.Status.SUCCESS) {
                try {
                    volume.setState(Volume.State.DELETED);
                    volume.setDeleted(new Date());
                    this.eventPublisher.notifyVolumeDeletion(volume);
                    this.em.persist(volume);
                } catch (Exception ex) {
                    VolumeManagerBean.logger.log(Level.SEVERE, "Failed to destroy volume " + volume.getName(), ex);
                }
            } else if (job.getStatus() == Job.Status.FAILED) {
                volume.setState(Volume.State.ERROR);
                this.eventPublisher.notifyVolumeChange(volume);
                VolumeManagerBean.logger.severe("Failed to destroy volume " + volume.getName() + ": " + job.getStatusMessage());
                this.em.persist(volume);
            }
        } else if (job.getAction().equals("machine.attachvolume")) {
            // handled by MachineManager
        } else if (job.getAction().equals("machine.detachvolume")) {
            // handled by MachineManager
        }
        return true;
    }

    private IVolumeService getCloudProviderVolumeService(final CloudProviderAccount cloudProviderAccount, final String location)
        throws CloudProviderException {
        ICloudProviderFactory cloudProviderFactory = this.cloudProviderFactoryFinder
            .getCloudProviderFactory(cloudProviderAccount.getCloudProvider().getCloudProviderType());
        ICloudProvider cloudProvider = cloudProviderFactory.getCloudProviderInstance(cloudProviderAccount,
            new CloudProviderLocation(location));
        return cloudProvider.getVolumeService();
    }

}