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
 */

package org.ow2.sirocco.cloudmanager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProvider;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Project;
import org.ow2.sirocco.cloudmanager.provider.api.entity.User;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidCloudProviderIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactory;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactoryFinder;
import org.ow2.sirocco.cloudmanager.service.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.service.api.IUserProjectManager;
import org.ow2.sirocco.cloudmanager.utils.PermissionChecker;

@Stateless(name = ICloudProviderManager.EJB_JNDI_NAME, mappedName = ICloudProviderManager.EJB_JNDI_NAME)
@Remote(IRemoteCloudProviderManager.class)
@Local(ICloudProviderManager.class)
public class CloudProviderManagerBean implements IRemoteCloudProviderManager, ICloudProviderManager {

    private static Logger log = Logger.getLogger(CloudProviderManagerBean.class.getName());

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @EJB
    private IUserProjectManager userProjectManager;

    @OSGiResource
    private ICloudProviderFactoryFinder cloudProviderFactoryFinder;

    @Override
    public CloudProvider getCloudProviderById(final String cloudProviderId) {
        int id;
        try {
            id = Integer.valueOf(cloudProviderId);
        } catch (NumberFormatException ex) {
            return null;
        }
        return this.em.find(CloudProvider.class, id);
    }

    @Override
    public List<CloudProvider> listCloudProviders() {
        return this.em.createQuery("FROM " + CloudProvider.class.getSimpleName() + " c").getResultList();
    }

    @Override
    public CloudProvider createCloudProvider(final String userName, final String cloudProviderType, final String description)
        throws InvalidUsernameException, PermissionDeniedException, InvalidArgumentException {

        // Check user presence.
        User user = this.userProjectManager.getUserByUsername(userName);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + userName);
        } // no "else" needed.

        // Check project authorization.
        if (!new PermissionChecker().canManageCloudProviders(user)) {
            throw new PermissionDeniedException("Permission denied for user " + userName + ".");
        } // no "else" needed.

        // Check the given cloudProviderType.
        if (cloudProviderType == null) {
            throw new InvalidArgumentException("The given cloudProviderType can NOT be: " + cloudProviderType + ".");
        } else if (cloudProviderType.equals("")) {
            throw new InvalidArgumentException("The given cloudProviderType can NOT be: \"\".");
        } // no "else" needed.

        // Check that there is NO CloudProvider with the given cloudProviderType
        // present in DB.
        Query cloudProvidersQuery = this.em.createQuery(
            "FROM " + CloudProvider.class.getSimpleName() + " c WHERE c.cloudProviderType=:cloudProviderType").setParameter(
            "cloudProviderType", cloudProviderType);
        if (cloudProvidersQuery != null) {
            if (!(cloudProvidersQuery.getResultList().isEmpty())) {
                throw new InvalidArgumentException("The given cloudProviderType: " + cloudProviderType
                    + " is ALREADY defined in system.");
            } // no "else" needed.
        } // no "else" needed.

        CloudProvider cloudProvider = new CloudProvider();
        cloudProvider.setCloudProviderType(cloudProviderType);
        cloudProvider.setDescription(description);

        // cloudProvider.getCloudProviderAccount().add(cPA);
        // cPA.setCloudProvider(cloudProvider);

        this.em.persist(cloudProvider);
        this.em.flush();

        return cloudProvider;
    }

    @Override
    public void deleteCloudProvider(final String userName, final String cloudProviderId) throws InvalidArgumentException,
        CloudProviderException, InvalidUsernameException, PermissionDeniedException {

        // Check user presence.
        User user = this.userProjectManager.getUserByUsername(userName);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + userName);
        } // no "else" needed.

        // Check project authorization.
        if (!new PermissionChecker().canManageCloudProviders(user)) {
            throw new PermissionDeniedException("Permission denied for user " + userName + ".");
        } // no "else" needed.

        // Check the given cloudProviderType.
        if (cloudProviderId == null) {
            throw new InvalidArgumentException("The given cloudProviderId can NOT be: " + cloudProviderId + ".");
        } else if (cloudProviderId.equals("")) {
            throw new InvalidArgumentException("The given cloudProviderId can NOT be: \"\".");
        } // no "else" needed.

        // Find cloudProvider corresponding to the given cloudProviderId.
        Query cloudProviderListQuery = this.em.createQuery(
            "FROM " + CloudProvider.class.getSimpleName() + " c WHERE c.id=:cPId").setParameter("cPId",
            Integer.valueOf(cloudProviderId));
        if (cloudProviderListQuery == null) {
            throw new InvalidArgumentException("The given cloudProviderId: " + cloudProviderId + " is UNKNOWN to the system.");
        } else {
            CloudProvider cloudProvider = (CloudProvider) cloudProviderListQuery.getSingleResult();
            if (cloudProvider.getCloudProviderAccount().isEmpty()) {
                this.em.remove(cloudProvider);
                this.em.flush();
            } else {
                throw new CloudProviderException("The cloudProvider corresponding to the given cloudProviderId: "
                    + cloudProviderId + " is associated with at least one cloudProviderAccount: "
                    + cloudProvider.getCloudProviderAccount().iterator().next().getId()
                    + ". As consequence, it is NOT possible to delete it.");
            }
        }
    }

    @Override
    public List<CloudProviderLocation> listCloudProviderLocations() {
        List<CloudProviderLocation> result = new ArrayList<CloudProviderLocation>();
        for (ICloudProviderFactory cloudProviderFactory : this.cloudProviderFactoryFinder.listCloudProviderFactories()) {
            result.addAll(cloudProviderFactory.listCloudProviderLocations());
        }
        return result;
    }

    @Override
    public List<CloudProviderLocation> listCloudProviderLocationsByCloudProviderId(final String cloudProviderId)
        throws InvalidCloudProviderIdException {
        CloudProvider cloudProvider = this.getCloudProviderById(cloudProviderId);
        if (cloudProvider == null) {
            throw new InvalidCloudProviderIdException();
        }
        ICloudProviderFactory cloudProviderFactory = this.cloudProviderFactoryFinder.getCloudProviderFactory(cloudProvider
            .getCloudProviderType());
        if (cloudProviderFactory != null) {
            return cloudProviderFactory.listCloudProviderLocations();
        } else {
            throw new InvalidCloudProviderIdException();
        }
    }

    @Override
    public CloudProviderAccount getCloudProviderAccountById(final String accountId) {
        int id;
        try {
            id = Integer.valueOf(accountId);
        } catch (NumberFormatException ex) {
            return null;
        }
        return this.em.find(CloudProviderAccount.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CloudProviderAccount> listCloudProviderAccounts() {
        return this.em.createQuery("FROM " + CloudProviderAccount.class.getSimpleName() + " c").getResultList();
    }

    @Override
    public CloudProviderAccount createCloudProviderAccount(final String projectId, final String userName, final String login,
        final String password, final String cloudProviderId) throws InvalidProjectIdException, InvalidUsernameException,
        PermissionDeniedException, InvalidArgumentException {

        // Check project presence.
        Project project = this.userProjectManager.getProjectByProjectId(projectId);
        if (project == null) {
            throw new InvalidProjectIdException("Unknown project " + projectId);
        } // no "else" needed.

        // Check user presence.
        User user = this.userProjectManager.getUserByUsername(userName);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + userName);
        } // no "else" needed.

        // Check project authorization.
        if (!new PermissionChecker().canManageCloudProviderAccounts(user, project.getProjectId())) {
            throw new PermissionDeniedException("Permission denied for user " + userName + " in the projectId " + projectId);
        } // no "else" needed.

        // Check the given login.
        if (login == null) {
            throw new InvalidArgumentException("The given login can NOT be: " + login + ".");
        } else if (login.equals("")) {
            throw new InvalidArgumentException("The given login can NOT be: \"\".");
        } // no "else" needed.

        // Check the given password.
        if (password == null) {
            throw new InvalidArgumentException("The given password can NOT be: " + password + ".");
        } else if (password.equals("")) {
            throw new InvalidArgumentException("The given password can NOT be: \"\".");
        } // no "else" needed.

        // Check the given cloudProviderId.
        if (cloudProviderId == null) {
            throw new InvalidArgumentException("The given cloudProviderId can NOT be: " + cloudProviderId + ".");
        } else if (cloudProviderId.equals("")) {
            throw new InvalidArgumentException("The given cloudProviderId can NOT be: \"\".");
        } // no "else" needed.

        // Check cloudProviderId presence.
        Query cloudProvidersQuery = this.em.createQuery("FROM " + CloudProvider.class.getSimpleName() + " c WHERE c.id=:cPId")
            .setParameter("cPId", Integer.valueOf(cloudProviderId));
        CloudProvider cloudProvider = null;
        if (cloudProvidersQuery == null) {
            throw new InvalidArgumentException("The given cloudProviderId: " + cloudProviderId + " is UNKNOWN to the system.");
        } else {
            if (cloudProvidersQuery.getResultList().isEmpty()) {
                throw new InvalidArgumentException("The given cloudProviderId: " + cloudProviderId
                    + " is UNKNOWN to the system.");
            } else {
                cloudProvider = (CloudProvider) cloudProvidersQuery.getResultList().iterator().next();
            }
        }

        CloudProviderAccount cloudProviderAccount = new CloudProviderAccount();

        cloudProviderAccount.setLogin(login);
        cloudProviderAccount.setPassword(password);

        cloudProvider.getCloudProviderAccount().add(cloudProviderAccount);
        cloudProviderAccount.setCloudProvider(cloudProvider);

        // Associate CLoudProviderAccount and Project.
        project.getCloudProviderAccounts().add(cloudProviderAccount);
        cloudProviderAccount.getProjects().add(project);

        this.em.persist(cloudProviderAccount);
        this.em.flush();

        return cloudProviderAccount;
    }

    @Override
    public void deleteCloudProviderAccount(final String projectId, final String userName, final String cloudProviderAccountId)
        throws InvalidArgumentException, CloudProviderException, InvalidProjectIdException, InvalidUsernameException,
        PermissionDeniedException {

        // Check project presence.
        Project project = this.userProjectManager.getProjectByProjectId(projectId);
        if (project == null) {
            throw new InvalidProjectIdException("Unknown project " + projectId);
        } // no "else" needed.

        // Check user presence.
        User user = this.userProjectManager.getUserByUsername(userName);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + userName);
        } // no "else" needed.

        // Check project authorization.
        if (!new PermissionChecker().canManageCloudProviderAccounts(user, project.getProjectId())) {
            throw new PermissionDeniedException("Permission denied for user " + userName + " in the projectId " + projectId);
        } // no "else" needed.

        // Check the given cloudProviderId.
        if (cloudProviderAccountId == null) {
            throw new InvalidArgumentException("The given cloudProviderAccountId can NOT be: " + cloudProviderAccountId + ".");
        } else if (cloudProviderAccountId.equals("")) {
            throw new InvalidArgumentException("The given cloudProviderAccountId can NOT be: \"\".");
        } // no "else" needed.

        // Find cloudProviderAccount corresponding to the given
        // cloudProviderAccountId.
        Query cloudProviderAccountListQuery = this.em.createQuery(
            "FROM " + CloudProviderAccount.class.getSimpleName() + " c WHERE c.id=:cPAId").setParameter("cPAId",
            Integer.valueOf(cloudProviderAccountId));
        if (cloudProviderAccountListQuery == null) {
            throw new InvalidArgumentException("The given cloudProviderAccountId: " + cloudProviderAccountId
                + " is UNKNOWN to the system.");
        } else {
            CloudProviderAccount cloudProviderAccount = (CloudProviderAccount) cloudProviderAccountListQuery.getSingleResult();
            if (!(cloudProviderAccount.getProjects().isEmpty())) {
                throw new CloudProviderException("The cloudProviderAccount corresponding to the given cloudProviderAccountId: "
                    + cloudProviderAccountId + " is associated with, at least, one project: "
                    + cloudProviderAccount.getProjects().iterator().next().getId()
                    + ". As consequence, it is NOT possible to delete it.");
            } else if (!(cloudProviderAccount.getMachines().isEmpty())) {
                throw new CloudProviderException("The cloudProviderAccount corresponding to the given cloudProviderAccountId: "
                    + cloudProviderAccountId + " is associated with, at least, one virtualMachine: "
                    + cloudProviderAccount.getMachines().iterator().next().getId()
                    + ". As consequence, it is NOT possible to delete it.");
            } else if (!(cloudProviderAccount.getImages().isEmpty())) {
                throw new CloudProviderException("The cloudProviderAccount corresponding to the given cloudProviderAccountId: "
                    + cloudProviderAccountId + " is associated with, at least, one vmImage: "
                    + cloudProviderAccount.getImages().iterator().next().getId()
                    + ". As consequence, it is NOT possible to delete it.");
            } else if (!(cloudProviderAccount.getVolumes().isEmpty())) {
                throw new CloudProviderException("The cloudProviderAccount corresponding to the given cloudProviderAccountId: "
                    + cloudProviderAccountId + " is associated with, at least, one volume: "
                    + cloudProviderAccount.getVolumes().iterator().next().getId()
                    + ". As consequence, it is NOT possible to delete it.");
            } else {
                CloudProvider cloudProvider = cloudProviderAccount.getCloudProvider();
                if (cloudProvider != null) {
                    cloudProvider.getCloudProviderAccount().remove(cloudProviderAccount);
                    cloudProviderAccount.setCloudProvider(null);
                } // no "else" needed.

                this.em.remove(cloudProviderAccount);
                this.em.flush();
            }
        }

    }

    @Override
    public void associateCloudProviderAccountWithProject(final String projectId, final String userName,
        final String cloudProviderAccountId) throws InvalidProjectIdException, InvalidUsernameException,
        PermissionDeniedException, InvalidArgumentException {

        // Check project presence.
        Project project = this.userProjectManager.getProjectByProjectId(projectId);
        if (project == null) {
            throw new InvalidProjectIdException("Unknown project " + projectId);
        } // no "else" needed.

        // Check user presence.
        User user = this.userProjectManager.getUserByUsername(userName);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + userName);
        } // no "else" needed.

        // Check project authorization.
        if (!new PermissionChecker().canManageCloudProviderAccounts(user, project.getProjectId())) {
            throw new PermissionDeniedException("Permission denied for user " + userName + " in the projectId " + projectId);
        } // no "else" needed.

        // Check the given cloudProviderId.
        if (cloudProviderAccountId == null) {
            throw new InvalidArgumentException("The given cloudProviderAccountId can NOT be: " + cloudProviderAccountId + ".");
        } else if (cloudProviderAccountId.equals("")) {
            throw new InvalidArgumentException("The given cloudProviderAccountId can NOT be: \"\".");
        } // no "else" needed.

        // Find cloudProviderAccount corresponding to the given
        // cloudProviderAccountId.
        Query cloudProviderAccountListQuery = this.em.createQuery(
            "FROM " + CloudProviderAccount.class.getSimpleName() + " c WHERE c.id=:cPAId").setParameter("cPAId",
            Integer.valueOf(cloudProviderAccountId));
        if (cloudProviderAccountListQuery == null) {
            throw new InvalidArgumentException("The given cloudProviderAccountId: " + cloudProviderAccountId
                + " is UNKNOWN to the system.");
        } else {
            CloudProviderAccount cloudProviderAccount = (CloudProviderAccount) cloudProviderAccountListQuery.getSingleResult();
            if (cloudProviderAccount.getProjects().contains(project)) {
                throw new InvalidArgumentException(
                    "The project corresponding to the given projectId: "
                        + projectId
                        + " is already associated with the cloudProviderAccount corresponding to the given cloudProviderAccountId: "
                        + cloudProviderAccountId + ".");
            } else {
                project.getCloudProviderAccounts().add(cloudProviderAccount);
                cloudProviderAccount.getProjects().add(project);
                this.em.persist(cloudProviderAccount);
                this.em.flush();
            }
        }
    }

    @Override
    public void dissociateCloudProviderAccountFromProject(final String projectId, final String userName,
        final String cloudProviderAccountId) throws InvalidProjectIdException, InvalidUsernameException,
        PermissionDeniedException, InvalidArgumentException {

        // Check project presence.
        Project project = this.userProjectManager.getProjectByProjectId(projectId);
        if (project == null) {
            throw new InvalidProjectIdException("Unknown project " + projectId);
        } // no "else" needed.

        // Check user presence.
        User user = this.userProjectManager.getUserByUsername(userName);
        if (user == null) {
            throw new InvalidUsernameException("Unknown user " + userName);
        } // no "else" needed.

        // Check project authorization.
        if (!new PermissionChecker().canManageCloudProviderAccounts(user, project.getProjectId())) {
            throw new PermissionDeniedException("Permission denied for user " + userName + " in the projectId " + projectId);
        } // no "else" needed.

        // Check the given cloudProviderId.
        if (cloudProviderAccountId == null) {
            throw new InvalidArgumentException("The given cloudProviderAccountId can NOT be: " + cloudProviderAccountId + ".");
        } else if (cloudProviderAccountId.equals("")) {
            throw new InvalidArgumentException("The given cloudProviderAccountId can NOT be: \"\".");
        } // no "else" needed.

        // Find cloudProviderAccount corresponding to the given
        // cloudProviderAccountId.
        Query cloudProviderAccountListQuery = this.em.createQuery(
            "FROM " + CloudProviderAccount.class.getSimpleName() + " c WHERE c.id=:cPAId").setParameter("cPAId",
            Integer.valueOf(cloudProviderAccountId));
        if (cloudProviderAccountListQuery == null) {
            throw new InvalidArgumentException("The given cloudProviderAccountId: " + cloudProviderAccountId
                + " is UNKNOWN to the system.");
        } else {
            CloudProviderAccount cloudProviderAccount = (CloudProviderAccount) cloudProviderAccountListQuery.getSingleResult();
            if (cloudProviderAccount.getProjects().contains(project)) {
                project.getCloudProviderAccounts().remove(cloudProviderAccount);
                cloudProviderAccount.getProjects().remove(project);
                this.em.persist(cloudProviderAccount);
                this.em.flush();
            } else {
                throw new InvalidArgumentException("The project corresponding to the given projectId: " + projectId
                    + " is NOT associated with the cloudProviderAccount corresponding to the given cloudProviderAccountId: "
                    + cloudProviderAccountId + ". Dissociation can NOT be done.");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CloudProviderAccount> listCloudProviderAccountsByProjectId(final String projectId)
        throws InvalidProjectIdException {
        Project project = (Project) this.em.createQuery("FROM " + Project.class.getSimpleName() + " p WHERE p.projectId=:pId")
            .setParameter("pId", projectId).getSingleResult();
        if (project == null) {
            throw new InvalidProjectIdException();
        }
        List<CloudProviderAccount> result = new ArrayList<CloudProviderAccount>(project.getCloudProviderAccounts());
        List<CloudProviderAccount> publicAccounts = this.em.createQuery(
            "FROM " + CloudProviderAccount.class.getSimpleName() + " a WHERE a.projects is empty").getResultList();
        result.addAll(publicAccounts);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CloudProviderAccount> listCloudProviderAccountsByCloudProviderId(final String cloudProviderId)
        throws InvalidCloudProviderIdException {
        CloudProvider cloudProvider = (CloudProvider) this.em
            .createQuery("FROM " + CloudProvider.class.getSimpleName() + " c WHERE c.id=:cId")
            .setParameter("cId", Integer.valueOf(cloudProviderId)).getSingleResult();
        if (cloudProvider == null) {
            throw new InvalidCloudProviderIdException();
        }
        cloudProvider.getCloudProviderAccount().size();
        return cloudProvider.getCloudProviderAccount();
    }
}