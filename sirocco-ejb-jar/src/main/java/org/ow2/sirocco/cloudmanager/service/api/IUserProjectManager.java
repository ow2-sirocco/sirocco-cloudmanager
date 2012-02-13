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

package org.ow2.sirocco.cloudmanager.service.api;

import java.util.List;

import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Project;
import org.ow2.sirocco.cloudmanager.provider.api.entity.ResourceConsumption;
import org.ow2.sirocco.cloudmanager.provider.api.entity.ResourceQuota;
import org.ow2.sirocco.cloudmanager.provider.api.entity.RoleAssignment.Rights;
import org.ow2.sirocco.cloudmanager.provider.api.entity.SystemInstance;
import org.ow2.sirocco.cloudmanager.provider.api.entity.SystemTemplate;
import org.ow2.sirocco.cloudmanager.provider.api.entity.User;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Volume;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.MachineImageInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.MachineInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.ProjectInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.UserAlreadyExistsException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.UserAlreadyInProjectException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.VolumeInUseException;

public interface IUserProjectManager {
    static final String EJB_JNDI_NAME = "UserProjectManagerBean";

    /**
     * @param username
     * @param password
     * @param userMail
     * @param userFirstName
     * @param userLastName
     * @return
     * @throws UserAlreadyExistsException
     */
    User createUser(final String username, String password, final String userMail, final String userFirstName,
        final String userLastName) throws UserAlreadyExistsException;

    /**
     * Deletes the account associated with the provided username
     * 
     * @param username name of the account
     * @param fromUser TODO
     * @throws InvalidUsernameException if the provided username is invalid
     * @throws MachineInUseException if the provided account has still VM in
     *         used
     * @throws MachineImageInUseException
     * @throws InvalidProjectIdException
     * @throws ProjectInUseException
     * @throws PermissionDeniedException
     * @throws VolumeInUseException
     */
    void deleteUser(final String username, final String fromUser) throws InvalidUsernameException, MachineInUseException,
        MachineImageInUseException, InvalidProjectIdException, ProjectInUseException, PermissionDeniedException,
        VolumeInUseException;

    /**
     * @param projectId
     * @return
     */
    List<User> getUsersByProjectId(final String projectId);

    /**
     * @param username
     * @return
     */
    String getApiPasswordForUser(final String username);

    /**
     * Authenticates a user on the basis of his/her login name (username),
     * password and administrator status.
     * 
     * @param username the username (login) of the user
     * @param password the password of the user
     * @return true if the given user can be authenticated
     * @throws InvalidUsernameException if the provided username is unknown
     */
    boolean authenticateUser(final String username, final String password) throws InvalidUsernameException;

    /**
     * Returns all user accounts registered by the cloud service
     * 
     * @return a list of user account value objects
     */
    List<User> getAllUsers();

    /**
     * Returns a list of the virtual machines belonging to a specific user
     * 
     * @param username the username of the user
     * @return a list of VirtualMachine value objects
     * @throws InvalidUsernameException if the provided username is invalid
     */
    List<Machine> getVirtualMachinesByUsername(final String username, final boolean includeDestroyedVMs)
        throws InvalidUsernameException;

    /**
     * Returns a list of the volumes belonging to a specific user
     * 
     * @param username user's login
     * @return a list of Volume value objects
     * @throws InvalidUsernameException if the provided username is invalid
     */
    List<Volume> getVolumeByUser(final String username) throws InvalidUsernameException;

    /**
     * Returns the user account information for a specific user
     * 
     * @param username the username of the user
     * @return a user account value object
     * @throws InvalidUsernameException if the provided username is invalid
     */
    User getUserByUsername(final String username);

    /**
     * Updates the SSH public key of a user
     * 
     * @param username the username of the user
     * @param sshKey the new SSH key
     * @throws InvalidUsernameException if the provided username is invalid
     */
    void updateUserSSHKey(final String username, final String sshKey) throws InvalidUsernameException;

    /**
     * Updates username passwords (web portal, api)
     * 
     * @param username the username of the user
     * @param webPassword new Web portal password
     * @param apiPassword new API password
     */
    void updateUserPasswords(final String username, final String webPassword, final String apiPassword)
        throws InvalidUsernameException;

    /**
     * @return
     */
    List<User> getAllAdmins();

    /**
     * @param username
     * @return
     */
    User getUserLdapInfoByUsername(final String username);

    /**
     * @param username
     * @param projectId
     * @return
     */
    boolean checkIfUserBelongsToProject(final String username, final String projectId);

    /**
     * @param username
     * @param projectId
     * @return
     */
    Rights getRightsOfUserInProject(final String username, final String projectId);

    /**
     * @param projectId
     * @return
     */
    Integer countCPUsByProject(final String projectId);

    /**
     * @param projectId
     * @return
     */
    Integer countDiskByProject(final String projectId);

    /**
     * @param projectId
     * @return
     */
    Integer countRAMByProject(final String projectId);

    /**
     * @param projectId
     * @return
     */
    Integer countVMByProject(final String projectId);

    /**
     * @param projectId
     * @param projectName
     * @param desc
     * @param owner
     * @return
     * @throws UserAlreadyInProjectException
     */
    Project createProject(final String projectId, final String projectName, final String desc, final String owner)
        throws UserAlreadyInProjectException;

    /**
     * @return
     */
    List<Project> getAllProjects();

    /**
     * @param username
     * @return
     * @throws InvalidUsernameException
     */
    Project getUserDefaultProject(final String username) throws InvalidUsernameException;

    /**
     * @param username
     * @return
     * @throws InvalidUsernameException
     */
    List<Project> getProjectsByUsername(final String username) throws InvalidUsernameException;

    /**
     * @param username
     * @param projectId
     * @param rights
     * @param fromUsername
     * @throws UserAlreadyInProjectException
     */
    void addUserToProject(final String username, final String projectId, final String rights, final String fromUsername)
        throws UserAlreadyInProjectException;

    /**
     * @param username
     * @param projectId
     * @param fromUsername
     * @throws InvalidProjectIdException
     * @throws MachineInUseException
     * @throws MachineImageInUseException
     */
    void delUserFromProject(final String username, final String projectId, final String fromUsername)
        throws InvalidProjectIdException, MachineInUseException, MachineImageInUseException;

    /**
     * @param projectId
     * @throws InvalidProjectIdException
     * @throws MachineInUseException
     * @throws MachineImageInUseException
     * @throws VolumeInUseException
     */
    void deleteProject(final String projectId) throws InvalidProjectIdException, MachineInUseException,
        MachineImageInUseException, VolumeInUseException;

    /**
     * @param projectName
     * @return
     * @throws InvalidProjectNameException
     */
    Project getProjectByName(final String projectName) throws InvalidProjectNameException;

    /**
     * @param projectId
     * @return
     * @throws InvalidProjectIdException
     */
    Project getProjectByProjectId(final String projectId) throws InvalidProjectIdException;

    /**
     * @param projectId
     * @return
     * @throws InvalidProjectIdException
     */
    List<Machine> getVirtualMachinesByProjectId(final String projectId) throws InvalidProjectIdException;

    /**
     * @param projectId
     * @return
     * @throws InvalidProjectIdException
     */
    List<Volume> getVolumeByProjectId(final String projectId) throws InvalidProjectIdException;

    /**
     * @param projectId
     * @return
     * @throws InvalidProjectIdException
     */
    ResourceConsumption getProjectResourceConsumption(final String projectId) throws InvalidProjectIdException;

    /**
     * Returns the resource quota of a given project
     * 
     * @param projectId the projectId
     * @return a resouce quota value object
     * @throws InvalidProjectIdException if the provided project is invalid
     */
    ResourceQuota getProjectResourceQuota(final String projectId) throws InvalidProjectIdException;

    /**
     * Updates the resource quota of a given project
     * 
     * @param projectId of the project
     * @param quota the new resource quota to assign to the project
     * @throws InvalidProjectIdException if the provided project is invalid
     */
    void updateProjectResourceQuota(final String projectId, final ResourceQuota quota) throws InvalidProjectIdException;

    /**
     * Returns the VM images belonging to a given user in a project projectId
     * 
     * @param projectId
     * @return a list of VM image value objects
     * @throws InvalidProjectIdException
     */
    List<MachineImage> getVMImagesByProjectId(final String projectId) throws InvalidProjectIdException;

    List<SystemInstance> getSystemInstancesByProjectId(final String projectId) throws InvalidProjectIdException;

    List<SystemTemplate> getSystemTemplatesByProjectId(final String projectId) throws InvalidProjectIdException;

}
