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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.ow2.sirocco.cloudmanager.common.DbParameters;
import org.ow2.sirocco.cloudmanager.common.jndilocator.ServiceLocator;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProvider;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Event;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Project;
import org.ow2.sirocco.cloudmanager.provider.api.entity.ResourceConsumption;
import org.ow2.sirocco.cloudmanager.provider.api.entity.ResourceQuota;
import org.ow2.sirocco.cloudmanager.provider.api.entity.User;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Visibility;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Volume;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.CloudProviderAccountVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.CloudProviderVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.DbParametersVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.EventVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.PerfMetricInfoVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.PerfMetricVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.ProjectVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.ResourceQuotaVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.UserVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VMImageVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VMSizeVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VirtualMachineSpec;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VirtualMachineVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VolumeVO;
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
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidPerfMetricIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidPowerStateException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidSessionException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidVolumeIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.MachineImageInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.MachineInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.ProjectInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.ResourceQuotaExceededException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.UserAlreadyExistsException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.UserAlreadyInProjectException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.VolumeInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.service.VolumeCreate;
import org.ow2.sirocco.cloudmanager.service.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.service.api.IEventManager;
import org.ow2.sirocco.cloudmanager.service.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.service.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.service.api.IMonitoringManager;
import org.ow2.sirocco.cloudmanager.service.api.IMonitoringManager.Entity;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteEventManager;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteUserProjectManager;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteVolumeManager;
import org.ow2.sirocco.cloudmanager.service.api.IUserProjectManager;
import org.ow2.sirocco.cloudmanager.service.api.IVolumeManager;

/**
 * POJO object that acts as the access point of Sirocco Cloud Manager
 * administration interface
 */
public class CloudService implements Serializable {

    private static final long serialVersionUID = 7488453566710948678L;

    private static Logger logger = Logger.getLogger(CloudService.class.getName());

    private transient IRemoteMachineManager machineManager;

    private transient IRemoteEventManager eventManager;

    private transient IRemoteUserProjectManager userProjectManager;

    private transient IRemoteVolumeManager volumeManager;

    private transient IMonitoringManager monitoringManager;

    private transient IMachineImageManager imageManager;

    private transient ICloudProviderManager cloudProviderManager;

    // A session token Id is associated with a username at logging time and
    // removed when logging out
    private static Map<String, String> tokenMap = new HashMap<String, String>();

    /**
     * Constructs a AdminService and binds it to the business tier
     */
    public CloudService() {
        this.initialize();
    }

    private void initialize() {
        try {
            this.machineManager = (IRemoteMachineManager) ServiceLocator.getInstance().getRemoteObject(
                IMachineManager.EJB_JNDI_NAME);
            this.eventManager = (IRemoteEventManager) ServiceLocator.getInstance().getRemoteObject(IEventManager.EJB_JNDI_NAME);
            this.userProjectManager = (IRemoteUserProjectManager) ServiceLocator.getInstance().getRemoteObject(
                IUserProjectManager.EJB_JNDI_NAME);
            this.volumeManager = (IRemoteVolumeManager) ServiceLocator.getInstance().getRemoteObject(
                IVolumeManager.EJB_JNDI_NAME);
            this.monitoringManager = (IMonitoringManager) ServiceLocator.getInstance().getRemoteObject(
                IMonitoringManager.EJB_JNDI_NAME);
            this.cloudProviderManager = (ICloudProviderManager) ServiceLocator.getInstance().getRemoteObject(
                ICloudProviderManager.EJB_JNDI_NAME);
            this.cloudProviderManager = (ICloudProviderManager) ServiceLocator.getInstance().getRemoteObject(
                ICloudProviderManager.EJB_JNDI_NAME);
            this.imageManager = (IMachineImageManager) ServiceLocator.getInstance().getRemoteObject(
                IMachineImageManager.EJB_JNDI_NAME);
        } catch (Exception ex) {
            CloudService.logger.severe(ex.getMessage());
        }
        CloudService.logger.info("AdminService initialized");
    }

    /**
     * @param sessionTokenId
     * @return userName corresponding to sessionTokenId.
     * @throws InvalidSessionException
     */
    private String sessionTokenIdToUsername(final String sessionTokenId) throws InvalidSessionException {
        String userName = CloudService.tokenMap.get(sessionTokenId);
        if (userName == null) {
            throw new InvalidSessionException("invalid token");
        }
        return userName;
    }

    public synchronized String authenticateUser(final String username, final String password) throws InvalidUsernameException {
        Boolean isAuth = this.userProjectManager.authenticateUser(username, password);
        if (isAuth) {
            // Generate a token
            String tokenId = UUID.randomUUID().toString();
            CloudService.tokenMap.put(tokenId, username);
            return tokenId;
        }
        return null;
    }

    public Boolean userLogout(final String tokenId) {
        CloudService.tokenMap.remove(tokenId);
        return true;
    }

    /**
     * @param projectId
     * @param userName
     * @param vmSpec
     * @param cloudProviderAccountId
     * @param startVm
     * @param sessionTokenId
     * @return
     * @throws InvalidSessionException
     * @throws InvalidUsernameException
     * @throws InvalidProjectIdException
     * @throws PermissionDeniedException
     * @throws InvalidMachineImageException
     * @throws InvalidNameException
     * @throws DuplicateNameException
     * @throws InvalidArgumentException
     * @throws ResourceQuotaExceededException
     * @throws ReservationException
     * @throws InvalidMachineConfigurationException
     * @throws InvalidProjectNameException
     * @throws InternalErrorException
     * @throws InsufficientResourceException
     * @throws NoRelevantResourcesPoolException
     */
    public String createVirtualMachine(final String projectId, final String userName, final VirtualMachineSpec vmSpec,
        final String cloudProviderAccountId, final boolean startVm, final String sessionTokenId)
        throws InvalidSessionException, InvalidUsernameException, InvalidProjectIdException, PermissionDeniedException,
        InvalidMachineImageException, InvalidNameException, DuplicateNameException, InvalidArgumentException,
        ResourceQuotaExceededException, InvalidMachineConfigurationException, InvalidProjectNameException,
        InternalErrorException, InsufficientResourceException {
        String username = this.sessionTokenIdToUsername(sessionTokenId);
        Machine vm = this.machineManager.createMachine(projectId, username, vmSpec, cloudProviderAccountId, startVm);
        return vm.getId().toString();
    }

    /**
     * @param sessionTokenId
     * @throws InvalidSessionException
     * @throws InvalidMachineIdException
     * @throws InvalidProjectIdException
     * @throws InvalidUsernameException
     * @throws PermissionDeniedException
     * @throws InternalErrorException
     * @throws InvalidMachineStateException
     */
    public void startVirtualMachine(final String vmId, final String sessionTokenId) throws InvalidSessionException,
        InvalidMachineIdException, InvalidProjectIdException, InvalidUsernameException, PermissionDeniedException,
        InternalErrorException, InvalidMachineStateException {
        String username = this.sessionTokenIdToUsername(sessionTokenId);
        this.machineManager.startMachine(username, vmId);
    }

    /**
     * @param vmId
     * @param sessionTokenId
     * @throws InvalidSessionException
     * @throws InvalidUsernameException
     * @throws InvalidProjectIdException
     * @throws PermissionDeniedException
     * @throws InvalidMachineIdException
     * @throws InternalErrorException
     * @throws InvalidMachineStateException
     */
    public void stopVirtualMachine(final String vmId, final String sessionTokenId) throws InvalidSessionException,
        InvalidUsernameException, InvalidProjectIdException, PermissionDeniedException, InvalidMachineIdException,
        InternalErrorException, InvalidMachineStateException {
        String username = this.sessionTokenIdToUsername(sessionTokenId);
        this.machineManager.stopMachine(username, vmId);
    }

    /**
     * @param vmId
     * @param stopVmBeforeDestruction
     * @param sessionTokenId
     * @throws InvalidSessionException
     * @throws InvalidUsernameException
     * @throws InvalidProjectIdException
     * @throws PermissionDeniedException
     * @throws InvalidMachineIdException
     * @throws InternalErrorException
     * @throws InvalidMachineStateException
     * @throws InvalidPowerStateException
     * @throws InvalidArgumentException
     * @throws NoRelevantResourcesPoolException
     * @throws ReservationException
     */
    public void destroyVirtualMachine(final String vmId, final boolean stopVmBeforeDestruction, final String sessionTokenId)
        throws InvalidSessionException, InvalidUsernameException, InvalidProjectIdException, PermissionDeniedException,
        InvalidMachineIdException, InternalErrorException, InvalidMachineStateException, InvalidPowerStateException,
        InvalidArgumentException {
        String username = this.sessionTokenIdToUsername(sessionTokenId);
        this.machineManager.deleteMachine(username, vmId);
    }

    public void createVMImage(final String projectId, final Visibility visibility, final String vmId, final String name,
        final String description, final String sessionTokenId) throws InvalidMachineIdException, InvalidNameException,
        DuplicateNameException, InvalidProjectIdException, CloudProviderException {
        String fromUsername = this.sessionTokenIdToUsername(sessionTokenId);
        this.machineManager.captureImageFromMachine(projectId, visibility, vmId, name, description, fromUsername);
    }

    public void destroyVMImage(final Integer imageId, final String sessionTokenId) throws InvalidMachineImageException,
        MachineImageInUseException, PermissionDeniedException, InvalidSessionException {
        String fromUser = this.sessionTokenIdToUsername(sessionTokenId);
        this.imageManager.deleteMachineImage(imageId.toString(), fromUser);
    }

    public List<UserVO> getAllUsersVo(final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<UserVO> result = new ArrayList<UserVO>();
        for (User user : this.userProjectManager.getAllUsers()) {
            result.add(user.toValueObject());
        }
        return result;
    }

    public List<ProjectVO> getAllProjectVo(final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<ProjectVO> result = new ArrayList<ProjectVO>();
        for (Project project : this.userProjectManager.getAllProjects()) {
            ProjectVO projectVo = project.toValueObject();
            List<UserVO> userVoList = new ArrayList<UserVO>();
            for (User user : this.userProjectManager.getUsersByProjectId(project.getProjectId())) {
                UserVO userVo = user.toValueObject();
                userVo.setUserRightInProjectVo(this.userProjectManager.getRightsOfUserInProject(userVo.getUsername(),
                    project.getProjectId()).toString());
                userVoList.add(userVo);
            }
            projectVo.setUserVoList(userVoList);
            result.add(projectVo);
        }
        return result;
    }

    public List<ProjectVO> getProjectsVoByUsername(final String username, final String sessionTokenId)
        throws InvalidUsernameException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<ProjectVO> result = new ArrayList<ProjectVO>();
        for (Project project : this.userProjectManager.getProjectsByUsername(username)) {
            ProjectVO projectVo = project.toValueObject();
            List<UserVO> userVoList = new ArrayList<UserVO>();
            for (User user : this.userProjectManager.getUsersByProjectId(project.getProjectId())) {
                UserVO userVo = user.toValueObject();
                userVo.setUserRightInProjectVo(this.userProjectManager.getRightsOfUserInProject(userVo.getUsername(),
                    project.getProjectId()).toString());
                userVoList.add(userVo);
            }
            projectVo.setUserVoList(userVoList);
            result.add(projectVo);
        }
        return result;
    }

    public void destroyProject(final String projectId, final String sessionTokenId) throws InvalidProjectIdException,
        MachineInUseException, MachineImageInUseException, InvalidSessionException, VolumeInUseException {
        this.sessionTokenIdToUsername(sessionTokenId);
        this.userProjectManager.deleteProject(projectId);
    }

    public void addUserToProject(final String username, final String projectId, final String rights, final String sessionTokenId)
        throws UserAlreadyInProjectException, InvalidSessionException {
        String fromUsername = this.sessionTokenIdToUsername(sessionTokenId);
        this.userProjectManager.addUserToProject(username, projectId, rights, fromUsername);
    }

    public void delUserFromProject(final String username, final String projectId, final String sessionTokenId)
        throws InvalidProjectIdException, MachineInUseException, MachineImageInUseException, InvalidSessionException {
        String fromUsername = this.sessionTokenIdToUsername(sessionTokenId);
        this.userProjectManager.delUserFromProject(username, projectId, fromUsername);
    }

    public void createProject(final String projectName, final String desc, final String owner, final String sessionTokenId)
        throws UserAlreadyInProjectException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        this.userProjectManager.createProject(null, projectName, desc, owner);
    }

    public void createUser(final String username, final String password, final String userMail, final String userFirstName,
        final String userLastName, final String sessionTokenId) throws UserAlreadyExistsException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        this.userProjectManager.createUser(username, password, userMail, userFirstName, userLastName);
    }

    public List<VMImageVO> getAllVMImagesVo(final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<VMImageVO> result = new ArrayList<VMImageVO>();
        for (MachineImage image : this.imageManager.getAllMachineImages()) {
            result.add(image.toValueObject());
        }
        return result;
    }

    public List<VirtualMachineVO> getAllVirtualMachinesVo(final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<VirtualMachineVO> result = new ArrayList<VirtualMachineVO>();
        for (Machine vm : this.machineManager.getAllVirtualMachines()) {
            result.add(vm.toValueObject());
        }
        return result;
    }

    public List<CloudProviderLocation> getCloudProviderLocations(final String cloudProviderId, final String sessionTokenId)
        throws InvalidSessionException, InvalidCloudProviderIdException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.cloudProviderManager.listCloudProviderLocationsByCloudProviderId(cloudProviderId);
    }

    public UserVO getUserVoByUsername(final String username, final String sessionTokenId) throws InvalidUsernameException,
        InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.userProjectManager.getUserByUsername(username).toValueObject();
    }

    public VirtualMachineVO getVirtualMachineVoByVmId(final String vmId, final String sessionTokenId)
        throws InvalidMachineIdException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.machineManager.getMachineById(vmId).toValueObject();
    }

    public List<VMImageVO> getVMImagesVoByProjectId(final String projectId, final String sessionTokenId)
        throws InvalidProjectIdException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<VMImageVO> result = new ArrayList<VMImageVO>();
        for (MachineImage image : this.userProjectManager.getVMImagesByProjectId(projectId)) {
            result.add(image.toValueObject());
        }
        return result;
    }

    public List<VirtualMachineVO> getVirtualMachinesVoByUsername(final String username, final String sessionTokenId)
        throws InvalidUsernameException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<VirtualMachineVO> result = new ArrayList<VirtualMachineVO>();
        for (Machine vm : this.userProjectManager.getVirtualMachinesByUsername(username, false)) {
            result.add(vm.toValueObject());
        }
        return result;
    }

    public List<VirtualMachineVO> getVirtualMachinesVoByProjectId(final String projectId, final String sessionTokenId)
        throws InvalidProjectIdException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<VirtualMachineVO> result = new ArrayList<VirtualMachineVO>();
        for (Machine vm : this.userProjectManager.getVirtualMachinesByProjectId(projectId)) {
            result.add(vm.toValueObject());
        }
        return result;
    }

    public String getVMSizeName(final String vmId, final String sessionTokenId) throws InvalidMachineIdException,
        InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.machineManager.getMachineConfigurationName(vmId);
    }

    public List<VMSizeVO> getVMSizesVo(final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<VMSizeVO> result = new ArrayList<VMSizeVO>();
        for (MachineConfiguration size : this.machineManager.getMachineConfigurations()) {
            result.add(size.toValueObject());
        }
        return result;

    }

    public void rebootVirtualMachine(final String vmId, final String sessionTokenId) throws InvalidMachineIdException,
        InvalidPowerStateException, PermissionDeniedException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        String fromUsername = CloudService.tokenMap.get(sessionTokenId);
        this.machineManager.restartMachine(vmId, fromUsername);
    }

    public void updateVirtualMachineExpirationDate(final String vmId, final Date expirationDate, final String sessionTokenId)
        throws InvalidMachineIdException, PermissionDeniedException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        String fromUsername = CloudService.tokenMap.get(sessionTokenId);
        this.machineManager.updateMachineExpirationDate(vmId, expirationDate, fromUsername);
    }

    public String getVirtualMachineConsole(final String vmId, final String sessionTokenId) throws InvalidMachineIdException,
        CloudProviderException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.machineManager.getMachineConsole(vmId);
    }

    public void pauseVirtualMachine(final String vmId, final String sessionTokenId) throws InvalidMachineIdException,
        InvalidPowerStateException, PermissionDeniedException, InvalidSessionException {
        String fromUsername = this.sessionTokenIdToUsername(sessionTokenId);
        this.machineManager.pauseMachine(vmId, fromUsername);
    }

    public void unpauseVirtualMachine(final String vmId, final String sessionTokenId) throws InvalidMachineIdException,
        InvalidPowerStateException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        this.machineManager.unpauseMachine(vmId);
    }

    public void migrateVirtualMachine(final String vmId, final String destinationHostId, final String sessionTokenId)
        throws InvalidMachineIdException, InvalidHostIdException, CloudProviderException {
        this.sessionTokenIdToUsername(sessionTokenId);
        this.machineManager.migrateMachine(vmId, destinationHostId);
    }

    public void updateUserSSHKey(final String username, final String sshKey, final String sessionTokenId)
        throws InvalidUsernameException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        this.userProjectManager.updateUserSSHKey(username, sshKey);
    }

    public void updateVMImage(final VMImageVO image, final String sessionTokenId) throws InvalidMachineImageException,
        InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        MachineImage from = new MachineImage();
        from.setName(image.getName());
        from.setDescription(image.getDescription());
        from.setOsType(image.getOsType());
        this.imageManager.updateMachineImage(from);
    }

    public ResourceQuotaVO getProjectResourceQuotaVo(final String projectId, final String sessionTokenId)
        throws InvalidProjectIdException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        ResourceQuota quota = this.userProjectManager.getProjectResourceQuota(projectId);
        ResourceQuotaVO quotaVo = new ResourceQuotaVO();
        quotaVo.setCpuQuota(quota.getCpuQuota());
        quotaVo.setDiskQuotaInMB(quota.getDiskQuotaInMB());
        quotaVo.setRamQuotaInMB(quota.getRamQuotaInMB());
        quotaVo.setMaxNumberOfVMs(quota.getVmQuota());
        return quotaVo;
    }

    public void updateProjectResourceQuota(final String projectId, final ResourceQuotaVO quotaVo, final String sessionTokenId)
        throws InvalidProjectIdException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        ResourceQuota quota = new ResourceQuota();
        quota.setCpuQuota(quotaVo.getCpuQuota());
        quota.setDiskQuotaInMB(quotaVo.getDiskQuotaInMB());
        quota.setRamQuotaInMB(quotaVo.getRamQuotaInMB());
        quota.setVmQuota(quotaVo.getMaxNumberOfVMs());
        this.userProjectManager.updateProjectResourceQuota(projectId, quota);
    }

    public void updateUserPasswords(final String username, final String webPassword, final String apiPassword,
        final String sessionTokenId) throws InvalidUsernameException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        this.userProjectManager.updateUserPasswords(username, webPassword, apiPassword);
    }

    // FIXME
    public Object getDataCenterResourceTree(final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return null;
    }

    public PerfMetricInfoVO[] getHostPerfMetricInfos(final String hostId, final String sessionTokenId)
        throws InvalidArgumentException, InvalidSessionException, CloudProviderException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.monitoringManager.listPerfMetricInfos(Entity.HOST, hostId).toArray(new PerfMetricInfoVO[0]);
    }

    public PerfMetricVO[] getHostPerfMetrics(final String hostId, final String metricId, final Date startTime,
        final Date endTime, final String sessionTokenId) throws InvalidArgumentException, InvalidPerfMetricIdException,
        InvalidSessionException, CloudProviderException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.monitoringManager.getPerfMetrics(Entity.HOST, hostId, metricId, startTime, endTime).toArray(
            new PerfMetricVO[0]);
    }

    public float[] getVirtualMachinesCPULoad(final String[] vmIds, final String sessionTokenId)
        throws InvalidMachineIdException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        // FIXME
        return new float[vmIds.length];
    }

    public long[] getVirtualMachinesMemoryUsedMB(final String[] vmIds, final String sessionTokenId)
        throws InvalidMachineIdException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        // FIXME
        return new long[vmIds.length];
    }

    public PerfMetricInfoVO[] getVirtualMachinePerfMetricInfos(final String vmId, final String sessionTokenId)
        throws InvalidArgumentException, InvalidSessionException, CloudProviderException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.monitoringManager.listPerfMetricInfos(Entity.VIRTUALMACHINE, vmId).toArray(new PerfMetricInfoVO[0]);
    }

    public PerfMetricVO[] getVirtualMachinePerfMetrics(final String vmId, final String metricId, final Date startTime,
        final Date endTime, final String sessionTokenId) throws InvalidArgumentException, InvalidPerfMetricIdException,
        InvalidSessionException, CloudProviderException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.monitoringManager.getPerfMetrics(Entity.VIRTUALMACHINE, vmId, metricId, startTime, endTime).toArray(
            new PerfMetricVO[0]);
    }

    public List<VirtualMachineVO> getVirtualMachinesVoByHostId(final String hostId, final String sessionTokenId)
        throws InvalidHostIdException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        // FIXME
        return new ArrayList<VirtualMachineVO>();
    }

    public void deleteUser(final String username, final String sessionTokenId) throws InvalidUsernameException,
        MachineInUseException, MachineImageInUseException, InvalidProjectIdException, ProjectInUseException,
        PermissionDeniedException, InvalidSessionException, VolumeInUseException {
        String fromUsername = this.sessionTokenIdToUsername(sessionTokenId);
        this.userProjectManager.deleteUser(username, fromUsername);
    }

    public List<EventVO> findEventsVo(final Date startTime, final Date endTime, final int pageNumber, final int pageSize,
        final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<EventVO> result = new ArrayList<EventVO>();
        for (Event event : this.eventManager.findEvents(startTime, endTime, pageNumber, pageSize)) {
            result.add(event.toValueObject());
        }
        return result;
    }

    public List<EventVO> findEventsVoByProject(final String projectId, final Date startTime, final Date endTime,
        final int pageNumber, final int pageSize, final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<EventVO> result = new ArrayList<EventVO>();
        for (Event event : this.eventManager.findEventsByProject(projectId, startTime, endTime, pageNumber, pageSize)) {
            result.add(event.toValueObject());
        }
        return result;
    }

    public int countEvents(final Date startTime, final Date endTime, final String sessionTokenId)
        throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.eventManager.countEvents(startTime, endTime);
    }

    public int countEventsByProject(final String projectId, final Date startTime, final Date endTime,
        final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.eventManager.countEventsByProject(projectId, startTime, endTime);
    }

    public Integer countCPUsByProject(final String projectId, final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.userProjectManager.countCPUsByProject(projectId);
    }

    public Integer countDiskByProject(final String projectId, final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.userProjectManager.countDiskByProject(projectId);
    }

    public Integer countRAMByProject(final String projectId, final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.userProjectManager.countRAMByProject(projectId);
    }

    public Integer countVMByProject(final String projectId, final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.userProjectManager.countVMByProject(projectId);
    }

    public ResourceConsumption getResourceConsumptionVoByProjectId(final String projectId, final String sessionTokenId)
        throws InvalidProjectIdException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.userProjectManager.getProjectResourceConsumption(projectId);
    }

    public UserVO getUserVoLdapInfoByUsername(final String username, final String sessionTokenId)
        throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return this.userProjectManager.getUserLdapInfoByUsername(username).toValueObject();
    }

    public DbParametersVO getDbParametersInstance(final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        return DbParameters.getInstance().toValueObject();
    }

    public void createVolume(final String projectId, final String username, final String cloudProviderAccountId,
        final CloudProviderLocation location, final VolumeCreate volumeCreate, final String sessionTokenId)
        throws InvalidUsernameException, InvalidNameException, ResourceQuotaExceededException, DuplicateNameException,
        InvalidProjectIdException, PermissionDeniedException, CloudProviderException, InvalidMachineConfigurationException,
        InvalidProjectIdException, PermissionDeniedException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        this.volumeManager.createVolume(projectId, username, cloudProviderAccountId, location, volumeCreate);
    }

    public void destroyVolume(final String volumeId, final String sessionTokenId) throws InvalidUsernameException,
        InvalidProjectIdException, InvalidVolumeIdException, VolumeInUseException, PermissionDeniedException,
        CloudProviderException, InvalidSessionException {
        String fromUser = this.sessionTokenIdToUsername(sessionTokenId);
        this.volumeManager.deleteVolume(fromUser, volumeId);
    }

    public void attachVolumeToVirtualMachine(final String volumeId, final String vmId, final String sessionTokenId)
        throws InvalidUsernameException, InvalidProjectIdException, InvalidVolumeIdException, InvalidMachineIdException,
        VolumeInUseException, PermissionDeniedException, CloudProviderException {
        String fromUser = this.sessionTokenIdToUsername(sessionTokenId);
        this.machineManager.attachVolumeToMachine(fromUser, volumeId, vmId);
    }

    public void detachVolumeFromVirtualMachine(final String volumeId, final String vmId, final String sessionTokenId)
        throws InvalidUsernameException, InvalidProjectIdException, InvalidVolumeIdException, InvalidMachineIdException,
        VolumeInUseException, PermissionDeniedException, CloudProviderException {
        String fromUser = this.sessionTokenIdToUsername(sessionTokenId);
        this.machineManager.detachVolumeFromMachine(fromUser, volumeId, vmId);
    }

    public List<VolumeVO> getAllVolumeVo(final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<VolumeVO> result = new ArrayList<VolumeVO>();
        for (Volume volume : this.volumeManager.getAllVolumes()) {
            result.add(volume.toValueObject());
        }
        return result;
    }

    public List<VolumeVO> getVolumesVoByProjectId(final String projectId, final String sessionTokenId)
        throws InvalidProjectIdException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<VolumeVO> result = new ArrayList<VolumeVO>();
        for (Volume volume : this.userProjectManager.getVolumeByProjectId(projectId)) {
            result.add(volume.toValueObject());
        }
        return result;
    }

    public List<CloudProviderAccountVO> getCloudProviderAccountsByProjectId(final String projectId, final String sessionTokenId)
        throws InvalidProjectIdException, InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<CloudProviderAccountVO> result = new ArrayList<CloudProviderAccountVO>();
        for (CloudProviderAccount account : this.cloudProviderManager.listCloudProviderAccountsByProjectId(projectId)) {
            result.add(account.toValueObject());
        }
        return result;
    }

    public List<CloudProviderVO> getCloudProviders(final String sessionTokenId) throws InvalidSessionException {
        this.sessionTokenIdToUsername(sessionTokenId);
        List<CloudProviderVO> result = new ArrayList<CloudProviderVO>();
        for (CloudProvider provider : this.cloudProviderManager.listCloudProviders()) {
            result.add(provider.toValueObject());
        }
        return result;
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            // reconnect EJB3 if needed
            if (this.machineManager == null) {
                this.initialize();
            }
        } catch (Exception e) {
            // Java 5 doesn't have new IOException(message, cause)
            IOException ioe = new IOException("Cannot read object");
            ioe.initCause(e);
            throw ioe;
        }

        // deserialize everything except for the transient fields
        in.defaultReadObject();
    }
}
