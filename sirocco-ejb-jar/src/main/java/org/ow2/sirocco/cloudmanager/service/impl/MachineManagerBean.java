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
import javax.persistence.Query;

import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Event;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Job;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine.State;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineAdmin;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineTemplate;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Project;
import org.ow2.sirocco.cloudmanager.provider.api.entity.User;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Visibility;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Volume;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VirtualMachineSpec;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.DuplicateNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InsufficientResourceException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InternalErrorException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidCloudProviderIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidHostIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineConfigurationException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineImageException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineStateException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidPowerStateException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidVolumeIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.ResourceQuotaExceededException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.VolumeInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProvider;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactory;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactoryFinder;
import org.ow2.sirocco.cloudmanager.provider.api.service.IComputeService;
import org.ow2.sirocco.cloudmanager.provider.api.service.IPhysicalInfrastructureManagement;
import org.ow2.sirocco.cloudmanager.provider.api.service.ImageCreate;
import org.ow2.sirocco.cloudmanager.provider.api.service.MachineCreate;
import org.ow2.sirocco.cloudmanager.provider.api.service.VolumeAttachment;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobCompletionEvent;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobManager;
import org.ow2.sirocco.cloudmanager.service.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.service.api.IEventManager;
import org.ow2.sirocco.cloudmanager.service.api.IEventPublisher;
import org.ow2.sirocco.cloudmanager.service.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.service.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.service.api.IUserProjectManager;
import org.ow2.sirocco.cloudmanager.service.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.utils.PermissionChecker;

@Stateless(name = IMachineManager.EJB_JNDI_NAME, mappedName = IMachineManager.EJB_JNDI_NAME)
@Remote(IRemoteMachineManager.class)
@Local(IMachineManager.class)
public class MachineManagerBean implements IMachineManager, IRemoteMachineManager {

    private static Logger logger = Logger.getLogger(MachineManagerBean.class.getName());

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @OSGiResource
    private ICloudProviderFactoryFinder cloudProviderFactoryFinder;

    @EJB
    private IUserProjectManager userProjectManager;

    @EJB
    private IEventManager eventManager;

    @EJB
    private IEventPublisher eventPublisher;

    @EJB
    private IVolumeManager volumeManager;

    @EJB
    private IMachineImageManager imageManager;

    @EJB
    private ICloudProviderManager cloudProviderManager;

    @OSGiResource
    private JobManager jobManager;

    private ICloudProvider getCloudProvider(final CloudProviderAccount cloudProviderAccount, final String location)
        throws CloudProviderException {
        ICloudProviderFactory cloudProviderFactory = this.cloudProviderFactoryFinder
            .getCloudProviderFactory(cloudProviderAccount.getCloudProvider().getCloudProviderType());
        return cloudProviderFactory.getCloudProviderInstance(cloudProviderAccount, new CloudProviderLocation(location));
    }

    @Override
    public Machine createMachine(final String projectId, final String userName, final VirtualMachineSpec vmSpec,
        final String cloudProviderAccountId, final boolean startVm) throws InvalidUsernameException, InvalidProjectIdException,
        PermissionDeniedException, InvalidMachineImageException, InvalidNameException, DuplicateNameException,
        InvalidArgumentException, ResourceQuotaExceededException, InvalidMachineConfigurationException,
        InvalidProjectNameException, InternalErrorException, InsufficientResourceException {

        MachineCreate machineCreate = new MachineCreate();
        machineCreate.setName(vmSpec.getVmName());
        MachineTemplate machineTemplate = new MachineTemplate();
        machineCreate.setMachineTemplate(machineTemplate);

        MachineConfiguration machineConfig = new MachineConfiguration();
        machineConfig.setMemorySizeMB(vmSpec.getMemorySizeMB());
        machineConfig.setNumCPUs(vmSpec.getNumCPUs());
        machineConfig.setDiskSizeMB(vmSpec.getDiskSizeMB());
        machineTemplate.setMachineConfig(machineConfig);
        machineTemplate.setHostname(vmSpec.getDnsName());

        String vmImageId = null;
        try {
            vmImageId = Integer.toString(vmSpec.getImageId());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Illegal vmImageId: " + vmSpec.getImageId());
        }
        MachineImage image = this.imageManager.getMachineImageById(vmImageId);
        if (image == null) {
            throw new InvalidMachineImageException("The given vmImageId: " + vmImageId + " is unknown.");
        }
        machineTemplate.setMachineImage(image);

        // Check userName.
        User user = this.userProjectManager.getUserByUsername(userName);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + userName);
        }

        String sshKey = user.getPublicKey();
        if (sshKey != null && !sshKey.isEmpty()) {
            MachineAdmin machineAdmin = new MachineAdmin();
            machineTemplate.setMachineAdmin(machineAdmin);
            machineAdmin.setPublicKey(sshKey);
        }
        return this
            .createMachine(projectId, userName, cloudProviderAccountId, vmSpec.getCloudProviderLocation(), machineCreate);
    }

    @Override
    public Machine createMachine(final String projectId, final String userName, final String cloudProviderAccountId,
        CloudProviderLocation cloudProviderLocation, final MachineCreate machineCreate) throws InvalidUsernameException,
        InvalidProjectIdException, PermissionDeniedException, InvalidMachineImageException, InvalidNameException,
        DuplicateNameException, InvalidArgumentException, ResourceQuotaExceededException, InvalidMachineConfigurationException,
        InvalidProjectNameException, InternalErrorException, InsufficientResourceException {

        MachineManagerBean.logger.info("Create a virtual machine for projectId: " + projectId + ", userName: " + userName
            + ", params: " + machineCreate + ".");

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
        if (!new PermissionChecker().canCreateVm(user, project)) {
            throw new PermissionDeniedException("Permission denied for createVm for user " + userName + " in the projectId "
                + projectId + ".");
        }

        if (machineCreate == null || machineCreate.getMachineTemplate() == null) {
            throw new IllegalArgumentException("machineTemplate argument is null");
        }

        // Check that vmSpec contains all the required info
        // (Optional info is ignored).
        String vmName = machineCreate.getName();
        if ((vmName == null) || vmName.isEmpty()) {
            throw new IllegalArgumentException("Wrong vmName argument: " + vmName);
        }

        // Check Vm's name validity.
        if (!(this.checkMachineName(vmName))) {
            throw new InvalidNameException("Invalid VM name " + vmName);
        }

        // check if Vm's name already taken.
        if (this.doesMachineNameExistsInProject(vmName, projectId)) {
            throw new DuplicateNameException("VM name " + vmName + " already exists in this project !");
        }

        long vmMemorySizeMB = machineCreate.getMachineTemplate().getMachineConfig().getMemorySizeMB();
        if (vmMemorySizeMB <= 0) {
            throw new IllegalArgumentException("vmMemorySizeMB: " + vmMemorySizeMB
                + " is a required argument/parameter, and must be > 0.");
        }

        int vmNumCPUs = machineCreate.getMachineTemplate().getMachineConfig().getNumCPUs();
        if (vmNumCPUs <= 0) {
            throw new IllegalArgumentException("vmNumCPUs must be > 0.");
        }

        long vmDiskSizeMB = machineCreate.getMachineTemplate().getMachineConfig().getDiskSizeMB();
        if (vmDiskSizeMB < 0) {
            throw new IllegalArgumentException("vmDiskSizeMB must be >= 0.");
        }

        // Check project's resources quota.
        if (!(this.validateMachineCreation(project, vmNumCPUs, vmMemorySizeMB, vmDiskSizeMB))) {
            throw new ResourceQuotaExceededException("Quota exceeded");
        }

        MachineImage vmImage = machineCreate.getMachineTemplate().getMachineImage();
        if (vmImage == null) {
            throw new InvalidMachineImageException("The given machineImage is null");
        }

        // Check that the given cloudProviderAccountId corresponds to a
        // CloudProviderAccount entity present in DB.
        CloudProviderAccount cloudProviderAccount = this.cloudProviderManager
            .getCloudProviderAccountById(cloudProviderAccountId);
        if (cloudProviderAccount == null) {
            throw new IllegalArgumentException("Wrong cloud provider account id: " + cloudProviderAccountId);
        }

        // Check that the cloudProviderAccount belongs to the project
        // (corresponding to the given projectId).
        if (!cloudProviderAccount.getProjects().contains(project)) {
            throw new IllegalArgumentException("The given cloud provider account ID: " + cloudProviderAccountId
                + " is NOT associated with  project " + projectId);
        }

        if (cloudProviderLocation == null) {
            List<CloudProviderLocation> locations = null;

            try {
                locations = this.cloudProviderManager.listCloudProviderLocationsByCloudProviderId(cloudProviderAccount
                    .getCloudProvider().getId().toString());
            } catch (InvalidCloudProviderIdException ex) {
                // should not happen
                MachineManagerBean.logger.log(Level.SEVERE, "internal error", ex);
            }
            if (locations != null && !locations.isEmpty()) {
                cloudProviderLocation = locations.get(0);
            } else {
                throw new InvalidArgumentException("Cannot find cloud provider location");
            }
        }

        if (!cloudProviderAccount.getImages().contains(vmImage)
            && !cloudProviderAccount.getCloudProvider().getImages().contains(vmImage)) {
            throw new InvalidArgumentException("The machine image is associated neither to cloud provider account "
                + cloudProviderAccountId + " nor to cloudProvider: " + cloudProviderAccount.getCloudProvider().getId());
        }

        Machine vm = new Machine();
        vm.setName(vmName);
        vm.setDescription(machineCreate.getDescription());
        vm.setProject(project);
        vm.setUser(user);
        vm.setCloudProviderAccount(cloudProviderAccount);
        vm.setState(Machine.State.CREATING);
        vm.setDiskCapacityInMB(vmDiskSizeMB);
        vm.setNumberOfCpus(vmNumCPUs);
        vm.setMemoryInMB(vmMemorySizeMB);
        vm.setLocation(cloudProviderLocation.getLocationId());
        vm.setImage(vmImage);

        try {
            IComputeService computeService = this.getCloudProvider(cloudProviderAccount, vm.getLocation()).getComputeService();
            Job<Machine> job = computeService.createMachine(machineCreate);
            vm.setActiveJob(job.getId());
        } catch (CloudProviderException e) {
            MachineManagerBean.logger.log(Level.SEVERE, "CloudProviderException: ", e);
            vm.setState(Machine.State.ERROR);
            this.em.persist(vm);
            this.em.flush();
            this.eventPublisher.notifyMachineChange(vm);
            throw new InternalErrorException(e.getMessage());
        }

        this.em.persist(vm);
        this.em.flush();
        this.eventPublisher.notifyMachineChange(vm);

        return vm;
    }

    @Override
    public boolean handleJobCompletion(final JobCompletionEvent event) {
        Machine vm = null;

        try {
            vm = (Machine) this.em.createNamedQuery(Machine.GET_MACHINE_BY_JOB_ID).setParameter("activeJob", event.getJobId())
                .getSingleResult();
        } catch (NoResultException ex) {
            MachineManagerBean.logger.severe("Cannot find Machine with job id " + event.getJobId());
            return false;
        }

        Job<?> job = this.jobManager.getJobById(event.getJobId());
        if (job == null) {
            MachineManagerBean.logger.warning("Job of VM " + vm.getName() + " has been reaped");
            vm.setState(Machine.State.ERROR);
            vm.setActiveJob(null);
            this.em.persist(vm);
            return true;
        }
        vm.setActiveJob(null);
        MachineManagerBean.logger.info("Job " + job);
        if (job.getAction().equals("machine.create")) {
            if (job.getStatus() == Job.Status.SUCCESS) {
                @SuppressWarnings("unchecked")
                Job<Machine> createJob = (Job<Machine>) job;
                try {
                    Machine machine = createJob.getResult().get();
                    vm.setProviderAssignedId(machine.getProviderAssignedId());
                    vm.setState(machine.getState());
                    vm.setNetworkInterfaces(machine.getNetworkInterfaces());
                    vm.setCreated(new Date());
                    this.em.persist(vm);
                    this.eventPublisher.notifyMachineChange(vm);
                    this.eventManager.newEvent(Event.Level.INFO, "Virtual Machine " + vm.getName() + " created", vm
                        .getProject().getProjectId(), vm.getId().toString(), Event.ITypes.VM_CREATION, null);
                } catch (Exception ex) {
                    MachineManagerBean.logger.log(Level.SEVERE, "Failed to create VM " + vm.getName(), ex);
                }
            } else if (job.getStatus() == Job.Status.FAILED) {
                vm.setState(Machine.State.ERROR);
                MachineManagerBean.logger.severe("Failed to create VM " + vm.getName() + ": " + job.getStatusMessage());
                this.em.persist(vm);
                this.eventPublisher.notifyMachineChange(vm);
                this.eventManager.newEvent(Event.Level.ERROR, "Virtual Machine " + vm.getName() + " creation failure", vm
                    .getProject().getProjectId(), vm.getId().toString(), Event.ITypes.VM_ERROR, job.getStatusMessage());
            }
        } else if (job.getAction().equals("machine.start")) {
            this.handleVirtualMachineActionCompletion(job, vm, Event.ITypes.VM_START);
        } else if (job.getAction().equals("machine.stop")) {
            this.handleVirtualMachineActionCompletion(job, vm, Event.ITypes.VM_STOP);
        } else if (job.getAction().equals("machine.pause")) {
            this.handleVirtualMachineActionCompletion(job, vm, Event.ITypes.VM_PAUSE);
        } else if (job.getAction().equals("machine.unpause")) {
            this.handleVirtualMachineActionCompletion(job, vm, Event.ITypes.VM_UNPAUSE);
        } else if (job.getAction().equals("machine.reboot")) {
        } else if (job.getAction().equals("machine.destroy")) {
            if (job.getStatus() == Job.Status.SUCCESS) {
                vm.setState(Machine.State.DELETED);
                this.em.persist(vm);
                this.eventPublisher.notifyMachineDeletion(vm);
                this.eventManager.newEvent(Event.Level.INFO, "Virtual Machine " + vm.getName() + " deleted", vm.getProject()
                    .getProjectId(), vm.getId().toString(), Event.ITypes.VM_DELETION, null);
            } else if (job.getStatus() == Job.Status.FAILED) {
                vm.setState(Machine.State.DELETED);
                MachineManagerBean.logger.severe("Failed to destroy VM " + vm.getName() + ": " + job.getStatusMessage());
                this.em.persist(vm);
                this.eventPublisher.notifyMachineChange(vm);
                this.eventManager.newEvent(Event.Level.ERROR, "Virtual Machine " + vm.getName() + " destruction error", vm
                    .getProject().getProjectId(), vm.getId().toString(), Event.ITypes.VM_ERROR, job.getStatusMessage());
            }
        } else if (job.getAction().equals("machine.capture")) {
            // do nothing, job handled by image manager
        } else if (job.getAction().equals("machine.attachvolume")) {
            if (job.getStatus() == Job.Status.SUCCESS) {
                @SuppressWarnings("unchecked")
                Job<VolumeAttachment> attachJob = (Job<VolumeAttachment>) job;
                try {
                    Volume volume = this.volumeManager.getVolumeByProviderAssignedId(attachJob.getResult().get().getVolumeId());
                    vm.getVolumes().add(volume);
                    this.em.persist(vm);
                    this.em.flush();
                    volume.getMachines().add(vm);
                    this.eventPublisher.notifyVolumeChange(volume);
                } catch (Exception ex) {
                    MachineManagerBean.logger.log(Level.SEVERE, "Failed to attach volume to VM " + vm.getName(), ex);
                }
            } else if (job.getStatus() == Job.Status.FAILED) {
                MachineManagerBean.logger.severe("Failed to attach volume to machine " + vm.getName() + ": "
                    + job.getStatusMessage());
                // XXX log error somewhere ?
            }
        } else if (job.getAction().equals("machine.detachvolume")) {
            if (job.getStatus() == Job.Status.SUCCESS) {
                @SuppressWarnings("unchecked")
                Job<String> detachJob = (Job<String>) job;
                try {
                    Volume volume = this.volumeManager.getVolumeByProviderAssignedId(detachJob.getResult().get());
                    vm.getVolumes().remove(volume);
                    this.em.persist(vm);
                    this.em.flush();
                    volume.getMachines().remove(vm);
                    this.eventPublisher.notifyVolumeChange(volume);
                } catch (Exception ex) {
                    MachineManagerBean.logger.log(Level.SEVERE, "Failed to detach volume to VM " + vm.getName(), ex);
                }
            } else if (job.getStatus() == Job.Status.FAILED) {
                MachineManagerBean.logger.severe("Failed to detach volume to machine " + vm.getName() + ": "
                    + job.getStatusMessage());
                // XXX log error somewhere ?
            }
        }
        return true;
    }

    private void handleVirtualMachineActionCompletion(final Job<?> job, final Machine vm, final String action) {
        if (job.getStatus() == Job.Status.SUCCESS) {
            @SuppressWarnings("unchecked")
            Job<Machine.State> lifecycleJob = (Job<Machine.State>) job;
            try {
                Machine.State state = lifecycleJob.getResult().get();
                vm.setState(state);
                this.em.persist(vm);
                this.eventPublisher.notifyMachineChange(vm);
                if (action.equals(Event.ITypes.VM_START)) {
                    this.eventManager.newEvent(Event.Level.INFO, "Virtual Machine " + vm.getName() + " started", vm
                        .getProject().getProjectId(), vm.getId().toString(), Event.ITypes.VM_START, null);
                } else if (action.equals(Event.ITypes.VM_STOP)) {
                    this.eventManager.newEvent(Event.Level.INFO, "Virtual Machine " + vm.getName() + " stopped", vm
                        .getProject().getProjectId(), vm.getId().toString(), Event.ITypes.VM_STOP, null);
                } else if (action.equals(Event.ITypes.VM_PAUSE)) {
                    this.eventManager.newEvent(Event.Level.INFO, "Virtual Machine " + vm.getName() + " paused", vm.getProject()
                        .getProjectId(), vm.getId().toString(), Event.ITypes.VM_PAUSE, null);
                } else if (action.equals(Event.ITypes.VM_UNPAUSE)) {
                    this.eventManager.newEvent(Event.Level.INFO, "Virtual Machine " + vm.getName() + " unpaused", vm
                        .getProject().getProjectId(), vm.getId().toString(), Event.ITypes.VM_UNPAUSE, null);
                }
            } catch (Exception ex) {
            }
        } else if (job.getStatus() == Job.Status.FAILED) {
            vm.setState(Machine.State.ERROR);
            MachineManagerBean.logger.severe("Failed to " + action + " VM " + vm.getName() + ": " + job.getStatusMessage());
            this.em.persist(vm);
            this.eventPublisher.notifyMachineChange(vm);
            this.eventManager.newEvent(Event.Level.ERROR, "Virtual Machine " + vm.getName() + " " + action + " error", vm
                .getProject().getProjectId(), vm.getId().toString(), Event.ITypes.VM_ERROR, job.getStatusMessage());
        }
    }

    @Override
    public void startMachine(final String userName, final String vmId) throws InvalidUsernameException,
        InvalidProjectIdException, PermissionDeniedException, InvalidMachineIdException, InvalidMachineStateException {
        MachineManagerBean.logger.info("Starting virtual machine " + vmId + " for user: " + userName + ".");

        User user = this.userProjectManager.getUserByUsername(userName);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + userName);
        }

        Machine vm = this.getMachineById(vmId);
        if (vm == null) {
            throw new InvalidMachineIdException("Invalid vm id: " + vmId);
        }

        Project project = vm.getProject();

        // Check project authorization.
        if (!new PermissionChecker().canStartVm(user, project)) {
            throw new PermissionDeniedException("Permission denied for user " + userName + " in the projectId "
                + project.getProjectId());
        }

        // Check vm status.
        if (vm.getState() != State.STOPPED) {
            throw new InvalidMachineStateException("VM " + vmId + " is not stopped");
        }

        try {
            IComputeService computeService = this.getCloudProvider(vm.getCloudProviderAccount(), vm.getLocation())
                .getComputeService();
            Job<Machine.State> job = computeService.startMachine(vm.getProviderAssignedId());
            vm.setActiveJob(job.getId());
            vm.setState(State.STARTING);
            this.eventPublisher.notifyMachineChange(vm);
            this.em.merge(vm);
            this.em.flush();
        } catch (CloudProviderException e) {
            MachineManagerBean.logger.log(Level.SEVERE, "CloudProviderException: ", e);
        }
    }

    @Override
    public void stopMachine(final String userName, final String vmId) throws InvalidUsernameException,
        InvalidProjectIdException, PermissionDeniedException, InvalidMachineIdException, InvalidMachineStateException {
        MachineManagerBean.logger.info("Stopping virtual machine " + vmId + " for user: " + userName + ".");

        User user = this.userProjectManager.getUserByUsername(userName);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + userName);
        }

        Machine vm = this.getMachineById(vmId);
        if (vm == null) {
            throw new InvalidMachineIdException("Invalid vm id: " + vmId);
        }

        Project project = vm.getProject();

        // Check project authorization.
        if (!new PermissionChecker().canStartVm(user, project)) {
            throw new PermissionDeniedException("Permission denied for user " + userName + " in the projectId "
                + project.getProjectId());
        }

        // Check vm status.
        if (vm.getState() != State.STARTED) {
            throw new InvalidMachineStateException("VM " + vmId + " is not stopped");
        }

        try {
            IComputeService computeService = this.getCloudProvider(vm.getCloudProviderAccount(), vm.getLocation())
                .getComputeService();
            Job<Machine.State> job = computeService.stopMachine(vm.getProviderAssignedId());
            vm.setActiveJob(job.getId());
            vm.setState(State.STOPPING);
            this.eventPublisher.notifyMachineChange(vm);
            this.em.merge(vm);
            this.em.flush();
        } catch (CloudProviderException e) {
            MachineManagerBean.logger.log(Level.SEVERE, "CloudProviderException: ", e);
        }
    }

    @Override
    public void deleteMachine(final String userName, final String vmId) throws InvalidMachineIdException,
        InvalidUsernameException, InvalidProjectIdException, PermissionDeniedException, InvalidMachineStateException,
        InvalidPowerStateException, InvalidArgumentException {
        MachineManagerBean.logger.info("Destroying  virtual machine " + vmId + " for user: " + userName + ".");

        // Check userName.
        User user = this.userProjectManager.getUserByUsername(userName);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user (with name: " + userName + ").");
        }

        Machine vm = this.getMachineById(vmId);
        if (vm == null) {
            throw new InvalidMachineIdException("Invalid vm id: " + vmId);
        }

        // Check project authorization.
        if (!new PermissionChecker().canDestroyVm(user, vm)) {
            throw new PermissionDeniedException("Permission denied for user " + userName + " in the projectId "
                + vm.getProject().getId() + ".");
        }

        try {
            IComputeService computeService = this.getCloudProvider(vm.getCloudProviderAccount(), vm.getLocation())
                .getComputeService();
            Job<Void> job = computeService.destroyMachine(vm.getProviderAssignedId());

            vm.setState(State.DELETING);
            vm.setActiveJob(job.getId());

            // detach all volumes attached to the VM
            vm.getVolumes().clear();
            this.eventPublisher.notifyMachineChange(vm);

            this.em.merge(vm);
            this.em.flush();
        } catch (CloudProviderException e) {
            MachineManagerBean.logger.log(Level.SEVERE, "CloudProviderException: ", e);
            vm.setState(State.DELETED);
            this.em.merge(vm);
            this.em.flush();
            this.eventPublisher.notifyMachineChange(vm);
        }
    }

    @Override
    public void attachVolumeToMachine(final String username, final String volumeId, final String vmId)
        throws InvalidUsernameException, InvalidProjectIdException, InvalidVolumeIdException, VolumeInUseException,
        InvalidMachineIdException, PermissionDeniedException, CloudProviderException {
        User user = this.userProjectManager.getUserByUsername(username);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + username);
        }

        Machine vm = this.getMachineById(vmId);
        if (vm == null) {
            throw new InvalidMachineIdException("vmId " + vmId + " invalid");
        }

        Volume volume = this.volumeManager.getVolumeById(volumeId);
        if (volume == null) {
            throw new InvalidVolumeIdException("Volume id " + volumeId + " invalid");
        }
        Project project = volume.getProject();
        // check project authorization
        if (!new PermissionChecker().canCreateVm(user, project)) {
            throw new PermissionDeniedException("Permission denied for user " + username + " in the projectId "
                + project.getProjectId());
        }

        if (volume.getMachines().size() > 0) {
            throw new VolumeInUseException("volume is already attached");
        }

        try {
            IComputeService computeService = this.getCloudProvider(vm.getCloudProviderAccount(), vm.getLocation())
                .getComputeService();
            VolumeAttachment volumeAttachement = new VolumeAttachment();
            volumeAttachement.setVolumeId(volume.getProviderAssignedId());
            // FIXME
            volumeAttachement.setAttachmentPoint("");
            volumeAttachement.setProtocol("");
            Job<?> job = computeService.attachVolume(vm.getProviderAssignedId(), volumeAttachement);

            vm.setActiveJob(job.getId());
            volume.setActiveJob(job.getId());

            this.em.merge(vm);
            this.em.merge(volume);
            this.em.flush();
        } catch (CloudProviderException e) {
            MachineManagerBean.logger.log(Level.SEVERE, "CloudProviderException: ", e);
            throw new CloudProviderException(e.getMessage());
        }

    }

    @Override
    public void detachVolumeFromMachine(final String username, final String volumeId, final String vmId)
        throws InvalidUsernameException, InvalidProjectIdException, InvalidVolumeIdException, InvalidMachineIdException,
        PermissionDeniedException, CloudProviderException {
        User user = this.userProjectManager.getUserByUsername(username);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + username);
        }

        Machine vm = this.getMachineById(vmId);
        if (vm == null) {
            throw new InvalidMachineIdException("vmId " + vmId + " invalid");
        }

        Volume volume = this.volumeManager.getVolumeById(volumeId);
        if (volume == null) {
            throw new InvalidVolumeIdException("Volume id " + volumeId + " invalid");
        }

        Project project = volume.getProject();

        boolean volumeIsAttached = false;
        for (Machine attachedVM : volume.getMachines()) {
            if (vm.getId() == attachedVM.getId()) {
                volumeIsAttached = true;
                break;
            }
        }
        if (!volumeIsAttached) {
            throw new InvalidMachineIdException("Volume " + volumeId + " is not attached to VM " + vmId);
        }

        // check project authorization
        if (!new PermissionChecker().canCreateVm(user, project)) {
            throw new PermissionDeniedException("Permission denied for user " + username + " in the project "
                + project.getProjectId());
        }

        try {
            IComputeService computeService = this.getCloudProvider(vm.getCloudProviderAccount(), vm.getLocation())
                .getComputeService();
            Job<?> job = computeService.detachVolume(vm.getProviderAssignedId(), volume.getProviderAssignedId());

            vm.setActiveJob(job.getId());
            volume.setActiveJob(job.getId());

            this.em.merge(vm);
            this.em.merge(volume);
            this.em.flush();
        } catch (CloudProviderException e) {
            MachineManagerBean.logger.log(Level.SEVERE, "CloudProviderException: ", e);
            throw new CloudProviderException(e.getMessage());
        }
    }

    @Override
    public void purgeDeletedMachines() {
        Query persistedVirtualMachineListQuery = this.em.createQuery("FROM " + Machine.class.getSimpleName() + " v");
        if (persistedVirtualMachineListQuery != null) {
            @SuppressWarnings("unchecked")
            List<Machine> persistedVirtualMachineList = persistedVirtualMachineListQuery.getResultList();
            for (Machine vm : persistedVirtualMachineList) {
                // Check vm status.
                if (vm.getState() == State.DELETED) {
                    // Let's clean up this destroyed vm from the DB.
                    CloudProviderAccount cloudProviderAccount = vm.getCloudProviderAccount();
                    if (cloudProviderAccount != null) {
                        cloudProviderAccount.getMachines().remove(vm);
                        vm.setCloudProviderAccount(null);
                    }

                    Project project = vm.getProject();
                    project.getMachines().remove(vm);
                    vm.setProject(null);

                    // Here, there is no need to handle: vm.getSystemInstance();

                    User user = vm.getUser();
                    user.getMachines().remove(vm);
                    vm.setUser(null);

                    // VMImage vmImage = vm.getVmImage();
                    // there is no navigation from VmImage to Vm.
                    vm.setImage(null);

                    this.em.remove(vm);
                    this.em.flush();
                }
            }
        }
    }

    public boolean checkMachineName(final String vmLabel) {
        if (vmLabel == null || vmLabel.equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_.-]*$");
        Matcher matcher = pattern.matcher(vmLabel);
        return matcher.find();
    }

    private boolean checkIfTemplateNameExistForProjectId(final String name, final String projectId) {
        @SuppressWarnings("unchecked")
        List<MachineImage> list = this.em
            .createQuery("FROM MachineImage v WHERE v.name=:name AND v.project.projectId=:projectId AND v.state<>'DELETED'")
            .setParameter("name", name).setParameter("projectId", projectId).getResultList();
        if (null == list || list.size() == 0) {
            return false; // don't exist
        }
        return true; // already exists
    }

    public boolean doesMachineNameExistsInProject(final String vmName, final String projectId) {
        @SuppressWarnings("unchecked")
        List<Machine> list = this.em
            .createQuery("FROM Machine v WHERE v.name=:name AND v.project.projectId=:projectId AND v.state<>'DELETED'")
            .setParameter("name", vmName).setParameter("projectId", projectId).getResultList();
        if (null == list || list.size() == 0) {
            return false; // don't exist
        }
        return true; // already exists
    }

    public void captureImageFromMachine(final String projectId, final Visibility visibility, final String vmId,
        final String name, final String description, final String fromUsername) throws CloudProviderException,
        InvalidMachineIdException, InvalidNameException, DuplicateNameException, InvalidProjectIdException {

        if (name.equals("")) {
            throw new InvalidNameException("Image name must be specified");
        }
        if (this.checkIfTemplateNameExistForProjectId(name, projectId)) {
            throw new DuplicateNameException("Image name " + name + " already used");
        }

        Machine vm = this.getMachineById(vmId);
        if (vm == null) {
            throw new InvalidMachineIdException("vmId " + vmId + " unknown");
        }
        Project proj = this.userProjectManager.getProjectByProjectId(projectId);
        if (proj == null) {
            throw new InvalidProjectIdException("projectId" + projectId + " unknown");
        }

        MachineImage image = new MachineImage();

        image.setState(MachineImage.State.CREATING);
        image.setDescription(description);
        image.setName(name);
        image.setHypervisor(vm.getImage().getHypervisor());
        image.setUser(this.userProjectManager.getUserByUsername(fromUsername));
        image.setProject(proj);
        image.setArchitecture(vm.getImage().getArchitecture());
        image.setDiskSizeMB(vm.getImage().getDiskSizeMB());
        image.setImageSizeMB(vm.getImage().getImageSizeMB());
        image.setOsType(vm.getImage().getOsType());
        image.setVisibility(visibility);
        image.setCloudProviderAccount(vm.getCloudProviderAccount());
        image.setLocation(vm.getLocation());

        try {
            IComputeService computeService = this.getCloudProvider(vm.getCloudProviderAccount(), vm.getLocation())
                .getComputeService();
            ImageCreate imageCreate = new ImageCreate();
            imageCreate.setName(name);
            imageCreate.setDescription(description);
            Job<MachineImage> job = computeService.captureImage(vm.getProviderAssignedId(), imageCreate);
            image.setActiveJob(job.getId());
            this.em.persist(image);
            this.em.flush();
            this.eventPublisher.notifyImageCreation(image);
            vm.setActiveJob(job.getId());
        } catch (CloudProviderException e) {
            MachineManagerBean.logger.log(Level.SEVERE, "CloudProviderException: ", e);
            image.setState(MachineImage.State.ERROR);
            this.em.persist(image);
            this.em.flush();
            this.eventPublisher.notifyImageChange(image);
        }

        this.em.persist(vm);
        this.em.flush();

    }

    @SuppressWarnings("unchecked")
    public List<Machine> getAllVirtualMachines() {
        List<Machine> result = this.em.createQuery("FROM Machine v WHERE v.state<>'DELETED' ORDER BY user.username,name")
            .getResultList();
        for (Machine machine : result) {
            machine.getNetworkInterfaces().size();
        }
        return result;
    }

    public Machine getMachineById(final String vmId) {
        int id;
        try {
            id = Integer.valueOf(vmId);
        } catch (NumberFormatException ex) {
            return null;
        }
        Machine result = this.em.find(Machine.class, id);
        if (result != null) {
            result.getNetworkInterfaces().size();
        }
        return result;
    }

    public Machine getMachineByProviderAssignedId(final String providerId) {
        @SuppressWarnings("unchecked")
        List<Machine> list = this.em.createQuery("FROM Machine v WHERE v.providerId=:providerId AND v.state<>'DELETED'")
            .setParameter("providerId", providerId).getResultList();
        Machine element = null;
        for (Machine vm : list) {
            element = vm;
            element.getNetworkInterfaces().size();
            break;
        }
        return element;
    }

    public String getMachineConsole(final String vmId) throws InvalidMachineIdException, CloudProviderException {
        Machine vm = this.getMachineById(vmId);
        if (vm == null) {
            throw new InvalidMachineIdException();
        }

        String consoleUrl = null;
        try {
            IComputeService computeService = this.getCloudProvider(vm.getCloudProviderAccount(), vm.getLocation())
                .getComputeService();
            consoleUrl = computeService.getMachineGraphicalConsoleUrl(vm.getProviderAssignedId());
        } catch (CloudProviderException e) {
            throw new CloudProviderException(e.getMessage());
        }
        return consoleUrl;
    }

    public String getMachineConfigurationName(final String vmId) throws InvalidMachineIdException {
        Machine vm = this.getMachineById(vmId);
        if (vm == null) {
            throw new InvalidMachineIdException("vmId " + vmId + " unknown");
        }
        return this.getVMSizeName(vm);
    }

    public String getVMSizeName(final Machine vm) {
        List<MachineConfiguration> sizes = this.getMachineConfigurations();
        for (MachineConfiguration size : sizes) {
            if (size.getNumCPUs() == vm.getNumberOfCpus() && size.getMemorySizeMB() == vm.getMemoryInMB()) {
                return size.getName();
            }
        }
        return "custom";
    }

    @SuppressWarnings("unchecked")
    public List<MachineConfiguration> getMachineConfigurations() {
        return this.em.createQuery("FROM MachineConfiguration").getResultList();
    }

    public MachineConfiguration getMachineConfigurationByName(final String name) {
        @SuppressWarnings("unchecked")
        List<MachineConfiguration> list = this.em.createQuery("FROM MachineConfiguration s where s.name=:name")
            .setParameter("name", name).getResultList();
        if (list != null & list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public void migrateMachine(final String vmId, final String destinationHostId) throws InvalidMachineIdException,
        InvalidHostIdException, CloudProviderException {
        Machine vm = this.getMachineById(vmId);
        if (vm == null) {
            throw new InvalidMachineIdException("vmId " + vmId + " unknown");
        }
        try {
            IPhysicalInfrastructureManagement infraManagement = this.getCloudProvider(vm.getCloudProviderAccount(),
                vm.getLocation()).getPhysicalInfrastructureManagementService();
            Job<Void> job = infraManagement.migrateMachine(vm.getProviderAssignedId(), destinationHostId);
            vm.setActiveJob(job.getId());
            this.em.merge(vm);
            this.em.flush();
        } catch (CloudProviderException e) {
            MachineManagerBean.logger.log(Level.SEVERE, "CloudProviderException: ", e);
        }
    }

    public void pauseMachine(final String vmId, final String fromUsername) throws InvalidMachineIdException,
        InvalidPowerStateException, PermissionDeniedException {
        Machine vm = this.getMachineById(vmId);
        if (vm == null) {
            throw new InvalidMachineIdException("vmId " + vmId + " unknown");
        }

        // check authorization
        if (!new PermissionChecker().canPauseVm(this.userProjectManager.getUserByUsername(fromUsername), vm)) {
            throw new PermissionDeniedException("Permission denied!");
        }

        try {
            IComputeService computeService = this.getCloudProvider(vm.getCloudProviderAccount(), vm.getLocation())
                .getComputeService();
            Job<Machine.State> job = computeService.pauseMachine(vm.getProviderAssignedId());
            vm.setActiveJob(job.getId());
            vm.setState(State.CREATING);
            this.eventPublisher.notifyMachineChange(vm);
            this.em.merge(vm);
            this.em.flush();
        } catch (CloudProviderException e) {
            MachineManagerBean.logger.log(Level.SEVERE, "CloudProviderException: ", e);
        }
    }

    public void restartMachine(final String vmId, final String fromUsername) throws InvalidMachineIdException,
        InvalidPowerStateException, PermissionDeniedException {

        Machine vm = this.getMachineById(vmId);

        if (vm == null) {
            throw new InvalidMachineIdException("vmId " + vmId + " unknown");
        }

        // check authorization
        if (!new PermissionChecker().canRebootVM(this.userProjectManager.getUserByUsername(fromUsername), vm)) {
            throw new PermissionDeniedException("Permission denied!");
        }

        try {
            IComputeService computeService = this.getCloudProvider(vm.getCloudProviderAccount(), vm.getLocation())
                .getComputeService();
            Job<Void> job = computeService.rebootMachine(vm.getProviderAssignedId());
            vm.setActiveJob(job.getId());
            this.em.merge(vm);
            this.em.flush();
        } catch (CloudProviderException e) {
            MachineManagerBean.logger.log(Level.SEVERE, "CloudProviderException: ", e);
        }
    }

    public void updateMachineExpirationDate(final String vmId, final Date expirationDate, final String fromUsername)
        throws InvalidMachineIdException, PermissionDeniedException {

        Machine vm = this.getMachineById(vmId);

        if (vm == null) {
            throw new InvalidMachineIdException("vmId " + vmId + " unknown");
        }

        // check authorization
        if (!new PermissionChecker().canUpdateVirtualMachineExpirationDate(
            this.userProjectManager.getUserByUsername(fromUsername), vm)) {
            throw new PermissionDeniedException("Permission denied!");
        }

        vm.setExpirationDate(expirationDate);
        this.em.merge(vm);
        this.em.flush();

    }

    public void unpauseMachine(final String vmId) throws InvalidMachineIdException, InvalidPowerStateException {
        Machine vm = this.getMachineById(vmId);
        if (vm == null) {
            throw new InvalidMachineIdException("vmId " + vmId + " unknown");
        }
        try {
            IComputeService computeService = this.getCloudProvider(vm.getCloudProviderAccount(), vm.getLocation())
                .getComputeService();
            Job<Machine.State> job = computeService.unpauseMachine(vm.getProviderAssignedId());
            vm.setActiveJob(job.getId());
            vm.setState(State.CREATING);
            this.eventPublisher.notifyMachineChange(vm);
            this.em.merge(vm);
            this.em.flush();
        } catch (CloudProviderException e) {
            MachineManagerBean.logger.log(Level.SEVERE, "CloudProviderException: ", e);
        }
    }

    public boolean validateMachineCreation(final Project project, final int numCPUs, final long memorySizeMB,
        final long diskSizeMB) {
        int cpuCoreUsed = this.userProjectManager.countCPUsByProject(project.getProjectId()) + numCPUs;
        long memoryUsedInMB = this.userProjectManager.countRAMByProject(project.getProjectId()) + memorySizeMB;
        long diskUsedInMB = this.userProjectManager.countDiskByProject(project.getProjectId()) + diskSizeMB;
        int vmUsed = this.userProjectManager.countVMByProject(project.getProjectId()) + 1;

        if (project.getResourceQuota().getCpuQuota() > 0 && cpuCoreUsed > project.getResourceQuota().getCpuQuota()) {
            return false;
        }
        if (project.getResourceQuota().getRamQuotaInMB() > 0 && memoryUsedInMB > project.getResourceQuota().getRamQuotaInMB()) {
            return false;
        }
        if (project.getResourceQuota().getDiskQuotaInMB() > 0 && diskUsedInMB > project.getResourceQuota().getDiskQuotaInMB()) {
            return false;
        }
        if (project.getResourceQuota().getVmQuota() > 0 && vmUsed > project.getResourceQuota().getVmQuota()) {
            return false;
        }
        return true;
    }

}
