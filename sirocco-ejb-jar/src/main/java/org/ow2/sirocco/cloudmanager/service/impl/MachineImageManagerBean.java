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

package org.ow2.sirocco.cloudmanager.service.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Job;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Project;
import org.ow2.sirocco.cloudmanager.provider.api.entity.User;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Visibility;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidCloudProviderAccountException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidCloudProviderIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineImageException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.MachineImageInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.ResourceQuotaExceededException;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProvider;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactory;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactoryFinder;
import org.ow2.sirocco.cloudmanager.provider.api.service.IImageService;
import org.ow2.sirocco.cloudmanager.provider.api.service.ImageUpload;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobCompletionEvent;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobManager;
import org.ow2.sirocco.cloudmanager.service.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.service.api.IEventPublisher;
import org.ow2.sirocco.cloudmanager.service.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteMachineImageManager;
import org.ow2.sirocco.cloudmanager.service.api.IUserProjectManager;
import org.ow2.sirocco.cloudmanager.utils.PermissionChecker;

@Stateless(name = IMachineImageManager.EJB_JNDI_NAME, mappedName = IMachineImageManager.EJB_JNDI_NAME)
@Remote(IRemoteMachineImageManager.class)
@Local(IMachineImageManager.class)
public class MachineImageManagerBean implements IMachineImageManager {

    private static Logger logger = Logger.getLogger(MachineImageManagerBean.class.getName());

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
    public void uploadMachineImage(final String projectId, final String userName, final String cloudProviderAccountId,
        CloudProviderLocation location, final ImageUpload imageUpload) throws InvalidUsernameException,
        InvalidProjectIdException, InvalidCloudProviderAccountException, PermissionDeniedException,
        ResourceQuotaExceededException, CloudProviderException {

        // Check userName.
        User user = this.userProjectManager.getUserByUsername(userName);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + userName);
        }

        // Check projectId.
        Project project = this.userProjectManager.getProjectByProjectId(projectId);
        if (project == null) {
            throw new InvalidProjectIdException("Unknown project " + projectId);
        }

        // Check project authorization.
        if (!new PermissionChecker().canUploadImage(user, project)) {
            throw new PermissionDeniedException("Permission denied for createVm for user " + userName + " in the projectId "
                + projectId + ".");
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
                MachineImageManagerBean.logger.log(Level.SEVERE, "internal error", ex);
            }
            if (locations != null && !locations.isEmpty()) {
                location = locations.get(0);
            } else {
                throw new InvalidArgumentException("Cannot find cloud provider location");
            }
        }

        MachineImage image = new MachineImage();
        image.setState(MachineImage.State.CREATING);
        image.setDescription(imageUpload.getDescription());
        image.setFormat(imageUpload.getFormat());
        image.setName(imageUpload.getName());

        image.setUser(user);
        image.setProject(project);
        image.setCloudProviderAccount(cloudProviderAccount);
        image.setCloudProvider(cloudProviderAccount.getCloudProvider());
        image.setLocation(location.getLocationId());
        image.setVisibility(Visibility.USER);

        try {
            IImageService imageService = this
                .getCloudProviderImageService(image.getCloudProviderAccount(), image.getLocation());
            Job<MachineImage> job = imageService.uploadImage(imageUpload);
            image.setActiveJob(job.getId());
        } catch (CloudProviderException e) {
            MachineImageManagerBean.logger.log(Level.SEVERE, "MultiCloudException: ", e);
            image.setState(MachineImage.State.ERROR);
            this.em.persist(image);
            this.em.flush();
        }

        this.em.persist(image);
        this.em.flush();
        this.eventPublisher.notifyImageCreation(image);

    }

    @SuppressWarnings("unchecked")
    public List<MachineImage> getAllMachineImages() {
        return this.em.createQuery("FROM MachineImage v WHERE v.state<>'DELETED'").getResultList();
    }

    public MachineImage getMachineImageById(final String vmiId) {
        return this.em.find(MachineImage.class, Integer.valueOf(vmiId));
    }

    public void updateMachineImage(final MachineImage from) throws InvalidMachineImageException {
        @SuppressWarnings("unchecked")
        List<MachineImage> list = this.em.createQuery("FROM MachineImage i WHERE i.id=:vmiId")
            .setParameter("vmiId", from.getId()).getResultList();
        if (list.size() == 0) {
            throw new InvalidMachineImageException("VM image not found");
        }
        MachineImage image = list.get(0);
        image.setName(from.getName());
        image.setDescription(from.getDescription());
        image.setOsType(from.getOsType());
        this.em.merge(image);
        this.em.persist(image);
        this.em.flush();
        this.eventPublisher.notifyImageChange(image);
    }

    public void deleteMachineImage(final String imageId, final String fromUsername) throws InvalidMachineImageException,
        MachineImageInUseException, PermissionDeniedException {

        Integer vmImageId = Integer.valueOf(imageId);
        MachineImage image = (MachineImage) this.em.createQuery("FROM MachineImage v WHERE v.id=:vmiId")
            .setParameter("vmiId", vmImageId).getSingleResult();
        if (image == null) {
            throw new InvalidMachineImageException("Invalid image id " + imageId);
        }

        // check authorization
        if (!new PermissionChecker().canDestroyVmImage(this.userProjectManager.getUserByUsername(fromUsername), image)) {
            throw new PermissionDeniedException("Permission denied!");
        }

        Query query = this.em.createQuery("FROM Machine v WHERE v.image.id=:vmiId AND v.state<>'DELETED'").setParameter(
            "vmiId", vmImageId);
        if (query.getResultList().size() > 0) {
            throw new MachineImageInUseException("Image id " + imageId + " still in used");
        }

        MachineImageManagerBean.logger.info("Destroying VM image " + image.getName());

        if (image.getCloudProviderAccount() == null) {
            image.setState(MachineImage.State.DELETED);
            this.em.merge(image);
            this.em.flush();
            this.eventPublisher.notifyImageChange(image);
        } else {
            try {
                IImageService imageService = this.getCloudProviderImageService(image.getCloudProviderAccount(),
                    image.getLocation());
                Job<Void> job = imageService.destroyImage(image.getProviderAssignedId());

                image.setState(MachineImage.State.DELETING);
                image.setActiveJob(job.getId());

                this.eventPublisher.notifyImageChange(image);

                this.em.merge(image);
                this.em.flush();
            } catch (CloudProviderException e) {
                MachineImageManagerBean.logger.log(Level.SEVERE, "MultiCloudException: ", e);
                image.setState(MachineImage.State.DELETED);
                this.em.merge(image);
                this.em.flush();
                this.eventPublisher.notifyImageChange(image);
            }
        }
        this.eventPublisher.notifyImageDeletion(image);
    }

    @Override
    public boolean handleJobCompletion(final JobCompletionEvent event) {
        MachineImage image = null;

        try {
            image = (MachineImage) this.em.createNamedQuery(MachineImage.GET_IMAGE_BY_JOB_ID)
                .setParameter("activeJob", event.getJobId()).getSingleResult();
        } catch (NoResultException ex) {
            MachineImageManagerBean.logger.severe("Cannot find Image with job id " + event.getJobId());
            return false;
        }
        Job<?> job = this.jobManager.getJobById(event.getJobId());
        if (job == null) {
            MachineImageManagerBean.logger.warning("Job of Image " + image.getName() + " has been reaped");
            image.setState(MachineImage.State.ERROR);
            image.setActiveJob(null);
            this.em.persist(image);
            return true;
        }
        image.setActiveJob(null);
        MachineImageManagerBean.logger.info("Job " + job);
        if (job.getAction().equals("machine.capture") || job.getAction().equals("image.upload")) {
            if (job.getStatus() == Job.Status.SUCCESS) {
                @SuppressWarnings("unchecked")
                Job<MachineImage> createJob = (Job<MachineImage>) job;
                try {
                    MachineImage imageFromProvider = createJob.getResult().get();
                    image.setProviderAssignedId(imageFromProvider.getProviderAssignedId());
                    image.setState(MachineImage.State.AVAILABLE);
                    this.eventPublisher.notifyImageChange(image);
                    this.em.persist(image);
                } catch (Exception ex) {
                    MachineImageManagerBean.logger.log(Level.SEVERE, "Failed to capture image" + image.getName(), ex);
                }
            } else if (job.getStatus() == Job.Status.FAILED) {
                image.setState(MachineImage.State.ERROR);
                this.eventPublisher.notifyImageChange(image);
                MachineImageManagerBean.logger.severe("Failed to capture image  " + image.getName() + ": "
                    + job.getStatusMessage());
                this.em.persist(image);
            }
        } else if (job.getAction().equals("image.destroy")) {
            if (job.getStatus() == Job.Status.SUCCESS) {
                try {
                    image.setState(MachineImage.State.DELETED);
                    this.eventPublisher.notifyImageDeletion(image);
                    this.em.persist(image);
                } catch (Exception ex) {
                    MachineImageManagerBean.logger.log(Level.SEVERE, "Failed to destroy image" + image.getName(), ex);
                }
            } else if (job.getStatus() == Job.Status.FAILED) {
                image.setState(MachineImage.State.ERROR);
                this.eventPublisher.notifyImageChange(image);
                MachineImageManagerBean.logger.severe("Failed to destroy image  " + image.getName() + ": "
                    + job.getStatusMessage());
                this.em.persist(image);
            }
        }
        return true;
    }

    private IImageService getCloudProviderImageService(final CloudProviderAccount cloudProviderAccount, final String location)
        throws CloudProviderException {
        ICloudProviderFactory cloudProviderFactory = this.cloudProviderFactoryFinder
            .getCloudProviderFactory(cloudProviderAccount.getCloudProvider().getCloudProviderType());
        ICloudProvider cloudProvider = cloudProviderFactory.getCloudProviderInstance(cloudProviderAccount,
            new CloudProviderLocation(location));
        return cloudProvider.getImageService();
    }

}
