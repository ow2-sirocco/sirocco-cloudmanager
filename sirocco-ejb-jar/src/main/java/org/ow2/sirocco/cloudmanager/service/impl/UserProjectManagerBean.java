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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.ow2.sirocco.cloudmanager.common.DbParameters;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Project;
import org.ow2.sirocco.cloudmanager.provider.api.entity.ResourceConsumption;
import org.ow2.sirocco.cloudmanager.provider.api.entity.ResourceQuota;
import org.ow2.sirocco.cloudmanager.provider.api.entity.RoleAssignment;
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
import org.ow2.sirocco.cloudmanager.realm.LdapUtil;
import org.ow2.sirocco.cloudmanager.service.api.IEventPublisher;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteUserProjectManager;
import org.ow2.sirocco.cloudmanager.service.api.IUserProjectManager;
import org.ow2.sirocco.cloudmanager.service.event.DelProjectEvent;
import org.ow2.sirocco.cloudmanager.service.event.DelUserEvent;
import org.ow2.sirocco.cloudmanager.utils.PasswordGenerator;
import org.ow2.sirocco.cloudmanager.utils.PermissionChecker;

@Stateless(name = IUserProjectManager.EJB_JNDI_NAME, mappedName = IUserProjectManager.EJB_JNDI_NAME)
@Remote(IRemoteUserProjectManager.class)
@Local(IUserProjectManager.class)
public class UserProjectManagerBean implements IUserProjectManager {

    private static Logger logger = Logger.getLogger(UserProjectManagerBean.class.getName());

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @EJB
    private IEventPublisher eventPublisher;

    public User createUser(final String username, final String password, final String userMail, final String userFirstName,
        final String userLastName) throws UserAlreadyExistsException {
        Date todayDate = new Date();

        Query queryVm = this.em.createQuery("FROM User u WHERE u.username=:username").setParameter("username", username);
        if (queryVm.getResultList().size() > 0) {
            throw new UserAlreadyExistsException("User " + username + " already exists");
        }

        User user = new User();
        user.setUsername(username.toLowerCase());
        user.setEmail(userMail);
        user.setFirstName(userFirstName);
        user.setLastName(userLastName);
        user.setCreateDate(todayDate);
        if (password != null) {
            user.setPassword(password);
        }
        user.setApiPassword(PasswordGenerator.getNext());

        this.em.persist(user);

        // Default ProjectId and project Name is username
        try {
            Project project = this.createProject(username, username, username + " default project", username);
            user.setDefaultProject(project);
        } catch (UserAlreadyInProjectException e) {
            UserProjectManagerBean.logger.log(Level.SEVERE, "User already in project!", e);
        }

        this.em.persist(user);

        return user;
    }

    public User getUserByUsername(final String username) {
        User user = null;
        try {
            user = (User) this.em.createQuery("FROM User WHERE username = :name").setParameter("name", username)
                .getSingleResult();
        } catch (NoResultException noResult) {
            if (DbParameters.getInstance().USE_LDAP_SERVER_FOR_AUTHENTIFICATION
                && DbParameters.getInstance().AUTO_CREATE_ACCOUNT) {
                String ldapAccount = LdapUtil.getInstance().getName(username);
                if (null != ldapAccount) {
                    try {
                        user = this.createUser(username, null, LdapUtil.getInstance().getEmail(username), LdapUtil
                            .getInstance().getFirstName(username), LdapUtil.getInstance().getLastName(username));
                    } catch (UserAlreadyExistsException e) {
                        UserProjectManagerBean.logger.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
                    }
                }
            }
        } catch (Exception ex) {
            UserProjectManagerBean.logger.log(Level.SEVERE, "Get user query problem", ex);
        }
        return user;
    }

    public User getUserLdapInfoByUsername(final String username) {
        User user = new User();
        String email = LdapUtil.getInstance().getEmail(username);
        if (email == null) {
            user.setEmail("Unknown user");
        } else {
            user.setEmail(email);
        }
        return user;
    }

    public List<User> getUsersByProjectId(final String projectId) {
        @SuppressWarnings("unchecked")
        List<User> list = this.em.createQuery("SELECT user FROM RoleAssignment r WHERE r.project.projectId=:projectId")
            .setParameter("projectId", projectId).getResultList();
        return list;
    }

    private String setApiPasswordForUser(final User user) {
        String pw = PasswordGenerator.getNext();
        user.setApiPassword(pw);
        this.em.merge(user);
        this.em.flush();
        return pw;
    }

    public String getApiPasswordForUser(final String username) {
        User u;
        try {
            u = (User) this.em.createQuery("FROM User WHERE username = :user").setParameter("user", username).getSingleResult();
        } catch (NoResultException e) {
            return null; // user does not exists
        }
        String passwd = null;
        if (u.getApiPassword() != null) {
            passwd = u.getApiPassword();
        } else {
            passwd = this.setApiPasswordForUser(u);
        }

        return passwd;
    }

    public boolean authenticateUser(final String username, final String password) throws InvalidUsernameException {
        User user = this.getUserByUsername(username);
        String pass = null;
        if (user == null) {
            throw new InvalidUsernameException("username " + username + " unknown");
        }
        try {
            pass = user.getPassword();
            if (pass.equals(password)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            if (DbParameters.getInstance().USE_LDAP_SERVER_FOR_AUTHENTIFICATION) {
                if (LdapUtil.getInstance().validate(username.toLowerCase(), password)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public List<User> getAllUsers() {
        Query query = this.em.createQuery("SELECT u FROM User u WHERE u.username.admin IS NOT true");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<User> getAllAdmins() {
        Query query = this.em.createQuery("SELECT u FROM User u WHERE u.username.admin IS true");
        return query.getResultList();
    }

    public List<Machine> getVirtualMachinesByUsername(final String username, final boolean includeDestroyedVMs)
        throws InvalidUsernameException {
        User user = this.getUserByUsername(username);
        if (user == null) {
            throw new InvalidUsernameException("username " + username + " unknown");
        }

        Collection<Machine> vmsList = user.getMachines();

        List<Machine> result = new ArrayList<Machine>();
        for (Machine vm : vmsList) {
            if (includeDestroyedVMs || vm.getState() != Machine.State.DELETED) {
                vm.getNetworkInterfaces().size();
                result.add(vm);
            }
        }
        return result;
    }

    @Override
    public List<Volume> getVolumeByUser(final String username) throws InvalidUsernameException {
        User user = this.getUserByUsername(username);
        return new ArrayList<Volume>(user.getVolumes());
    }

    public void updateUserSSHKey(final String username, final String sshKey) throws InvalidUsernameException {
        @SuppressWarnings("unchecked")
        List<User> users = this.em.createQuery("FROM User u WHERE u.username=:username").setParameter("username", username)
            .getResultList();
        if (users.size() > 0) {
            User user = users.get(0);
            user.setPublicKey(sshKey);
            this.em.merge(user);
            this.em.flush();
        } else {
            throw new InvalidUsernameException("username " + username + " unknown");
        }
    }

    public void updateUserPasswords(final String username, final String webPassword, final String apiPassword)
        throws InvalidUsernameException {
        User user = this.getUserByUsername(username);
        if (user == null) {
            throw new InvalidUsernameException("username " + username + " unknown");
        }
        user.setPassword(webPassword);
        user.setApiPassword(apiPassword);
        this.em.merge(user);
        this.em.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteUser(final String username, final String fromUsername) throws InvalidUsernameException,
        MachineInUseException, MachineImageInUseException, InvalidProjectIdException, ProjectInUseException,
        PermissionDeniedException, VolumeInUseException {

        User user = this.getUserByUsername(username);
        User fromUser = this.getUserByUsername(fromUsername);

        // check authorization
        if (!new PermissionChecker().canDestroyUser(fromUser)) {
            throw new PermissionDeniedException("Permission denied");
        }

        if (user == null || fromUser == null) {
            throw new InvalidUsernameException("User unknown");
        }

        Query queryVm = this.em.createQuery("FROM Machine v WHERE v.user.username=:username AND v.state<>'DELETED'")
            .setParameter("username", username);
        if (queryVm.getResultList().size() > 0) {
            throw new MachineInUseException("User " + username + " has still VM in used");
        }

        Query queryIm = this.em.createQuery("FROM MachineImage v WHERE v.user.username=:username AND v.state<>'DELETED'")
            .setParameter("username", username);
        if (queryIm.getResultList().size() > 0) {
            throw new MachineImageInUseException("User " + username + " has still VM images in used");
        }

        Query queryVol = this.em.createQuery("FROM Volume v WHERE v.user.username=:username AND v.state<>'DELETED'")
            .setParameter("username", username);
        if (queryVol.getResultList().size() > 0) {
            throw new VolumeInUseException("User " + username + " has still Volumes in used");
        }

        List<Project> projects = this.getProjectsByUsername(username);

        for (Project project : projects) {
            if (!project.getOwner().equals(username)) {
                this.delUserFromProject(username, project.getProjectId(), null);
            } else if ((!project.getProjectId().equals(project.getOwner()) && (project.getOwner().equals(username)))) {
                List<User> users = this.getUsersByProjectId(project.getProjectId());
                if (users.size() > 1) {
                    throw new ProjectInUseException("User " + username + " has still users in project " + project.getName());
                } else {
                    this.deleteProject(project.getProjectId());
                }
            }
        }

        projects = this.getProjectsByUsername(username);

        for (Project project : projects) {
            if (project.getProjectId().equals(project.getOwner())) {
                List<User> users = this.getUsersByProjectId(project.getProjectId());
                if (users.size() > 1) {
                    throw new ProjectInUseException("User " + username + " has still users in project " + project.getName());
                } else {
                    this.delUserFromProject(username, project.getProjectId(), null);
                    this.deleteProject(project.getProjectId());
                    this.em.remove(user);
                }
            }
        }

        this.eventPublisher.emitTopicMessage(new DelUserEvent(username), "admin", "true");
    }

    public Integer countCPUsByProject(final String projectId) {
        BigDecimal nb = BigDecimal.ZERO;
        Query q = this.em
            .createNativeQuery(
                "SELECT SUM(numberOfCpus) FROM Machine v, Project p WHERE p.id=v.project_id AND p.projectId=:projectId AND v.state<>'DELETED' GROUP BY p.projectId=:projectId")
            .setParameter("projectId", projectId);
        if (q.getResultList().size() > 0) {
            BigDecimal resultQ = null;
            resultQ = (BigDecimal) q.getResultList().get(0);
            nb = resultQ;
            return nb.intValue();
        } else {
            return 0;
        }
    }

    public Integer countDiskByProject(final String projectId) {
        BigDecimal totalDisk = BigDecimal.ZERO;
        Query q = this.em
            .createNativeQuery(
                "SELECT SUM(diskCapacityInMB) FROM Machine v, Project p WHERE p.id=v.project_id AND p.projectId=:projectId AND v.state<>'DELETED' GROUP BY p.projectId=:projectId")
            .setParameter("projectId", projectId);
        if (q.getResultList().size() > 0) {
            BigDecimal resultQ = null;
            resultQ = (BigDecimal) q.getResultList().get(0);
            totalDisk = resultQ;

        }

        q = this.em
            .createNativeQuery(
                "SELECT SUM(capacityInMB) FROM Volume v, Project p WHERE p.id=v.project_id AND p.projectId=:projectId AND v.state<>'DELETED' GROUP BY p.projectId=:projectId")
            .setParameter("projectId", projectId);
        if (q.getResultList().size() > 0) {
            BigDecimal resultQ = null;
            resultQ = (BigDecimal) q.getResultList().get(0);
            totalDisk = resultQ;
        }

        q = this.em
            .createNativeQuery(
                "SELECT SUM(diskSizeMB) FROM MachineImage v, Project p WHERE p.id=v.project_id AND p.projectId=:projectId AND v.state<>'DELETED' GROUP BY p.projectId=:projectId")
            .setParameter("projectId", projectId);
        if (q.getResultList().size() > 0) {
            BigDecimal resultQ = null;
            resultQ = (BigDecimal) q.getResultList().get(0);
            totalDisk = resultQ;
        }

        return totalDisk.intValue();
    }

    public Integer countRAMByProject(final String projectId) {
        BigDecimal totalMem = BigDecimal.ZERO;
        Query q = this.em
            .createNativeQuery(
                "SELECT SUM(memoryInMB) FROM Machine v, Project p WHERE p.id=v.project_id AND p.projectId=:projectId AND v.state<>'DELETED' GROUP BY p.projectId=:projectId")
            .setParameter("projectId", projectId);
        if (q.getResultList().size() > 0) {
            BigDecimal resultQ = null;
            resultQ = (BigDecimal) q.getResultList().get(0); // O is the first
            totalMem = resultQ;
            return totalMem.intValue();
        } else {
            return 0;
        }
    }

    public Integer countVMByProject(final String projectId) {
        @SuppressWarnings("unchecked")
        List<Machine> list = this.em.createQuery("FROM Machine v WHERE v.project.projectId=:projectId AND v.state<>'DELETED'")
            .setParameter("projectId", projectId).getResultList();
        return list.size();
    }

    public Project createProject(final String projectId, final String projectName, final String desc, final String owner)
        throws UserAlreadyInProjectException {

        String projId = projectId;
        Date todayDate = new Date();
        ResourceQuota quota = new ResourceQuota();
        quota.setCpuQuota(DbParameters.getInstance().DEFAULT_CPU_QUOTA);
        quota.setDiskQuotaInMB(DbParameters.getInstance().DEFAULT_DISK_QUOTA_IN_MB);
        quota.setRamQuotaInMB(DbParameters.getInstance().DEFAULT_RAM_QUOTA_IN_MB);
        quota.setVmQuota(DbParameters.getInstance().DEFAULT_VM_QUOTA);

        Project project = new Project();
        project.setCreateDate(todayDate);
        project.setDescription(desc);
        project.setName(projectName);
        project.setResourceQuota(quota);
        project.setOwner(this.getUserByUsername(owner));
        if (projId == null) {
            projId = "proj-" + UUID.randomUUID();
        }
        project.setProjectId(projId);

        this.em.persist(project);

        this.addUserToProject(owner, projId, Rights.ADMIN.toString(), null);

        return project;
    }

    public List<Project> getProjectsByUsername(final String username) throws InvalidUsernameException {
        @SuppressWarnings("unchecked")
        List<Project> assignList = this.em
            .createQuery("SELECT r.project FROM RoleAssignment r WHERE r.user.username=:username")
            .setParameter("username", username).getResultList();

        if (assignList == null) {
            throw new InvalidUsernameException("username " + username + " unknown");
        }

        return assignList;
    }

    public List<Machine> getVirtualMachinesByProjectId(final String projectId) throws InvalidProjectIdException {
        Project project = this.getProjectByProjectId(projectId);
        if (project == null) {
            throw new InvalidProjectIdException("projectId " + projectId + " unknown");
        }

        Collection<Machine> vmsList = project.getMachines();

        List<Machine> result = new ArrayList<Machine>();
        for (Machine vm : vmsList) {
            if (!vm.getState().equals(Machine.State.DELETED)) {
                vm.getNetworkInterfaces().size();
                result.add(vm);
            }
        }
        return result;
    }

    @Override
    public List<Volume> getVolumeByProjectId(final String projectId) throws InvalidProjectIdException {
        Project project = this.getProjectByProjectId(projectId);
        if (project == null) {
            throw new InvalidProjectIdException("projectId " + projectId + " unknown");
        }

        Collection<Volume> volList = project.getVolumes();

        @SuppressWarnings("unchecked")
        Collection<Volume> listPublicVolumes = this.em.createQuery(
            "FROM Volume v WHERE v.visibility='PUBLIC' AND v.state='AVAILABLE'").getResultList();

        volList.addAll(listPublicVolumes);

        List<Volume> result = new ArrayList<Volume>();
        for (Volume volume : volList) {
            if (!volume.getState().equals(Volume.State.DELETED)) {
                result.add(volume);
            }
        }
        return result;
    }

    @Override
    public List<MachineImage> getVMImagesByProjectId(final String projectId) throws InvalidProjectIdException {

        Project proj = null;
        try {
            proj = this.getProjectByProjectId(projectId);
        } catch (InvalidProjectIdException e) {
            throw new InvalidProjectIdException("projectId " + projectId + " unknown");
        }

        @SuppressWarnings("unchecked")
        Collection<MachineImage> listVmImages = this.em.createQuery(
            "FROM MachineImage v WHERE v.visibility='PUBLIC' AND v.state='AVAILABLE'").getResultList();

        listVmImages.addAll(proj.getImages());

        List<MachineImage> result = new ArrayList<MachineImage>();
        for (MachineImage image : listVmImages) {
            if (!image.getState().equals(MachineImage.State.DELETED)) {
                result.add(image);
            }
        }
        return result;
    }

    @Override
    public List<SystemInstance> getSystemInstancesByProjectId(final String projectId) throws InvalidProjectIdException {
        Project project = this.getProjectByProjectId(projectId);
        if (project == null) {
            throw new InvalidProjectIdException("projectId " + projectId + " unknown");
        }

        Collection<SystemInstance> systems = project.getSystemInstances();

        List<SystemInstance> result = new ArrayList<SystemInstance>();
        for (SystemInstance system : systems) {
            if (system.getState() != SystemInstance.State.DELETED) {
                result.add(system);
            }
        }
        return result;
    }

    @Override
    public List<SystemTemplate> getSystemTemplatesByProjectId(final String projectId) throws InvalidProjectIdException {
        Project project = this.getProjectByProjectId(projectId);
        if (project == null) {
            throw new InvalidProjectIdException("projectId " + projectId + " unknown");
        }

        Collection<SystemTemplate> systemTemplates = project.getSystemTemplates();

        List<SystemTemplate> result = new ArrayList<SystemTemplate>();
        for (SystemTemplate systemTemplate : systemTemplates) {
            if (systemTemplate.getState() != SystemTemplate.State.DELETED) {
                result.add(systemTemplate);
            }
        }
        return result;
    }

    @Override
    public Project getUserDefaultProject(final String username) throws InvalidUsernameException {
        Project proj = (Project) this.em
            .createQuery("SELECT r.project FROM RoleAssignment r WHERE r.user.username=:username AND r.project.name=:username")
            .setParameter("username", username).getSingleResult();

        if (proj == null) {
            throw new InvalidUsernameException("username " + username + " unknown");
        }

        return proj;
    }

    public boolean checkIfUserBelongsToProject(final String username, final String projectId) {
        @SuppressWarnings("unchecked")
        List<RoleAssignment> list = this.em
            .createQuery("FROM RoleAssignment r WHERE r.user.username=:username AND r.project.projectId=:projectId")
            .setParameter("username", username).setParameter("projectId", projectId).getResultList();
        if (null == list || list.size() == 0) {
            return false;
        }
        return true;
    }

    public Rights getRightsOfUserInProject(final String username, final String projectId) {
        Rights result = (Rights) this.em
            .createQuery(
                "SELECT r.rights FROM RoleAssignment r WHERE r.user.username=:username AND r.project.projectId=:projectId")
            .setParameter("username", username).setParameter("projectId", projectId).getSingleResult();
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Project> getAllProjects() {
        Query query = this.em.createQuery("SELECT p FROM Project p");
        return query.getResultList();
    }

    public void addUserToProject(final String username, final String projectId, final String rights, final String fromUsername)
        throws UserAlreadyInProjectException {
        User user = this.getUserByUsername(username);
        Project proj = null;
        try {
            proj = this.getProjectByProjectId(projectId);
        } catch (InvalidProjectIdException e) {
            UserProjectManagerBean.logger.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
        }

        if (this.checkIfUserBelongsToProject(username, projectId)) {
            throw new UserAlreadyInProjectException("User already member of this project!");
        }

        RoleAssignment assign = new RoleAssignment(user, proj, Rights.valueOf(rights));
        this.em.persist(assign);
        user.getAssignments().add(assign);
        proj.getAssignments().add(assign);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void delUserFromProject(final String username, final String projectId, final String fromUsername)
        throws InvalidProjectIdException, MachineInUseException, MachineImageInUseException {

        User user = this.getUserByUsername(username);
        Project proj = this.getProjectByProjectId(projectId);

        Query queryVm = this.em.createQuery("FROM Machine v WHERE v.project=:proj AND v.user=:user AND v.state<>'DELETED'")
            .setParameter("proj", proj).setParameter("user", user);
        if (queryVm.getResultList().size() > 0) {
            throw new MachineInUseException("User " + username + " has still VM in used in project " + proj.getName());
        }

        Query queryVmi = this.em
            .createQuery("FROM MachineImage v WHERE v.project=:proj AND v.user=:user AND v.state<>'DELETED'")
            .setParameter("proj", proj).setParameter("user", user);
        if (queryVmi.getResultList().size() > 0) {
            throw new MachineImageInUseException("User " + username + " has still VM image in project " + proj.getName());
        }

        long userid = user.getId();
        long projectid = proj.getId();
        RoleAssignment role = (RoleAssignment) this.em
            .createQuery("FROM RoleAssignment r WHERE r.userId=:userid AND r.projId=:projectid")
            .setParameter("projectid", projectid).setParameter("userid", userid).getSingleResult();
        this.em.remove(role);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteProject(final String projectId) throws InvalidProjectIdException, MachineInUseException,
        MachineImageInUseException, VolumeInUseException {
        Project project = null;
        try {
            project = (Project) this.em.createQuery("FROM Project WHERE projectId=:projectId")
                .setParameter("projectId", projectId).getSingleResult();
        } catch (NoResultException e) {
            throw new InvalidProjectIdException("project " + projectId + " unknown");
        }

        Query queryVm = this.em.createQuery("FROM Machine v WHERE v.project=:proj AND v.state<>'DELETED'").setParameter("proj",
            project);
        if (queryVm.getResultList().size() > 0) {
            throw new MachineInUseException("VM is used in project " + project.getName());
        }

        Query queryVmi = this.em.createQuery("FROM MachineImage v WHERE v.project=:proj AND v.state<>'DELETED'").setParameter(
            "proj", project);
        if (queryVmi.getResultList().size() > 0) {
            throw new MachineImageInUseException("VM image is used in project " + project.getName());
        }

        Query queryVol = this.em.createQuery("FROM Volume v WHERE v.project=:proj AND v.state<>'DELETED'").setParameter("proj",
            project);
        if (queryVol.getResultList().size() > 0) {
            throw new VolumeInUseException("Volume is used in project " + project.getName());
        }

        Query queryDelVmi = this.em.createQuery("DELETE MachineImage v WHERE v.project = :proj");
        queryDelVmi.setParameter("proj", project).executeUpdate();

        Query queryDelVol = this.em.createQuery("DELETE Volume v WHERE v.project = :proj");
        queryDelVol.setParameter("proj", project).executeUpdate();

        Query queryVm2 = this.em.createQuery("FROM Machine v WHERE v.project = :proj");
        @SuppressWarnings("unchecked")
        List<Machine> vms = queryVm2.setParameter("proj", project).getResultList();

        for (Machine v : vms) {
            v.getProperties().remove(0);
            this.em.remove(v);
        }

        // Remove users from project
        List<User> listUser = this.getUsersByProjectId(projectId);
        for (User u : listUser) {
            this.delUserFromProject(u.getUsername(), projectId, null);
        }

        this.em.remove(project);
        this.eventPublisher.emitTopicMessage(new DelProjectEvent(projectId), "admin", "true");
    }

    public Project getProjectByName(final String projectName) throws InvalidProjectNameException {
        Project project = null;
        try {
            project = (Project) this.em.createQuery("FROM Project WHERE name = :name").setParameter("name", projectName)
                .getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidProjectNameException("project " + projectName + " unknown");
        }
        return project;
    }

    public Project getProjectByProjectId(final String projectId) throws InvalidProjectIdException {
        Project project = null;
        try {
            project = (Project) this.em.createQuery("FROM " + Project.class.getSimpleName() + " WHERE projectId = :projectId")
                .setParameter("projectId", projectId).getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidProjectIdException("project " + projectId + " unknown");
        }
        return project;
    }

    public ResourceConsumption getProjectResourceConsumption(final String projectId) throws InvalidProjectIdException {
        if (this.getProjectByProjectId(projectId) == null) {
            throw new InvalidProjectIdException("project " + projectId + " unknown");
        }
        ResourceConsumption resourceConsumption = new ResourceConsumption();
        resourceConsumption.setMemoryMB(this.countRAMByProject(projectId));
        resourceConsumption.setStorageMB(this.countDiskByProject(projectId));
        resourceConsumption.setNumCPUs(this.countCPUsByProject(projectId));
        resourceConsumption.setNumVMs(this.countVMByProject(projectId));
        return resourceConsumption;
    }

    public ResourceQuota getProjectResourceQuota(final String projectId) throws InvalidProjectIdException {
        Project project = this.getProjectByProjectId(projectId);
        if (project == null) {
            throw new InvalidProjectIdException("project " + projectId + " unknown");
        }
        return project.getResourceQuota();
    }

    public void updateProjectResourceQuota(final String projectId, final ResourceQuota quota) throws InvalidProjectIdException {
        Project project = this.getProjectByProjectId(projectId);
        if (project == null) {
            throw new InvalidProjectIdException("project " + projectId + " unknown");
        }
        project.getResourceQuota().setCpuQuota(quota.getCpuQuota());
        project.getResourceQuota().setDiskQuotaInMB(quota.getDiskQuotaInMB());
        project.getResourceQuota().setRamQuotaInMB(quota.getRamQuotaInMB());
        project.getResourceQuota().setVmQuota(quota.getVmQuota());
        this.em.merge(project);
        this.em.flush();
    }
}
