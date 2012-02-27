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

package org.ow2.sirocco.cloudmanager.core.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.exception.UserException;
import org.ow2.sirocco.cloudmanager.core.utils.utilForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.User;

@Stateless(name = ICloudProviderManager.EJB_JNDI_NAME, mappedName = ICloudProviderManager.EJB_JNDI_NAME)
@Remote(IRemoteCloudProviderManager.class)
@Local(ICloudProviderManager.class)
@SuppressWarnings("unused")
public class CloudProviderManager implements ICloudProviderManager {

    private static Logger logger = Logger.getLogger(MachineImageManager.class
            .getName());
    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Resource
    private SessionContext ctx;
    private String user;

    @EJB
    private IUserManager userManager;

    @Resource
    public void setSessionContext(SessionContext ctx) {
        this.ctx = ctx;
    }

    private void setUser() {
        user = ctx.getCallerPrincipal().getName();
    }

    @Override
    public CloudProvider createCloudProvider(String type, String description)
            throws CloudProviderException {

        CloudProvider cp = new CloudProvider();
        cp.setCloudProviderType(type);
        cp.setDescription(description);
        this.em.persist(cp);
        return cp;
    }

    @Override
    public CloudProvider getCloudProviderById(String cloudProviderId)
            throws CloudProviderException {

        CloudProvider result = this.em.find(CloudProvider.class, new Integer(
                cloudProviderId));

        return result;
    }

    @Override
    public void deleteCloudProvider(String cloudProviderId)
            throws CloudProviderException {
        CloudProvider result = this.em.find(CloudProvider.class, new Integer(
                cloudProviderId));
        this.em.remove(result);
    }

    @Override
    public CloudProviderAccount createCloudProviderAccount(
            String cloudProviderId, String user, String login, String password)
            throws CloudProviderException {

        CloudProviderAccount cpa = new CloudProviderAccount();

        cpa.setCloudProvider(this.getCloudProviderById(cloudProviderId));
        cpa.setLogin(login);
        cpa.setPassword(password);

        this.em.persist(cpa);
        return cpa;
    }

    @Override
    public CloudProviderAccount getCloudProviderAccountById(
            String cloudProviderAccountId) throws CloudProviderException {

        CloudProviderAccount result = this.em.find(CloudProviderAccount.class,
                new Integer(cloudProviderAccountId));
        return result;
    }

    /**
     * add a provider to user by providing an user id
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager#addCloudProviderAccountToUser(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void addCloudProviderAccountToUser(String userId,
            String cloudProviderAccountId) throws CloudProviderException,
            UserException {

        CloudProviderAccount cpa = this
                .getCloudProviderAccountById(cloudProviderAccountId);
        User u = userManager.getUserById(userId);
        List<User> users = cpa.getUsers();
        users.add(u);
        cpa.setUsers(users);

        this.em.merge(cpa);

    }

    /**
     * add a provider to user by providing an user name
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager#addCloudProviderAccountToUserByName(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void addCloudProviderAccountToUserByName(String userName,
            String cloudProviderAccountId) throws CloudProviderException,
            UserException {

        this.addCloudProviderAccountToUser(
                userManager.getUserByUsername(userName).getId().toString(),
                cloudProviderAccountId);

    }

    /**
     * add a provider to user by providing an user id
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager#addCloudProviderAccountToUser(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void removeCloudProviderAccountFromUser(String userId,
            String cloudProviderAccountId) throws CloudProviderException,
            UserException {

        CloudProviderAccount cpa = this
                .getCloudProviderAccountById(cloudProviderAccountId);
        User u = userManager.getUserById(userId);
        List<User> users = cpa.getUsers();
        users.remove(u);
        cpa.setUsers(users);
        this.em.merge(cpa);

    }

    /**
     * add a provider to user by providing an user name
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager#addCloudProviderAccountToUserByName(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void removeCloudProviderAccountFromUserByName(String userName,
            String cloudProviderAccountId) throws CloudProviderException,
            UserException {

        this.removeCloudProviderAccountFromUser(
                userManager.getUserByUsername(userName).getId().toString(),
                cloudProviderAccountId);

    }

    @Override
    public void deleteCloudProviderAccount(String cloudProviderAccountId)
            throws CloudProviderException {
        CloudProviderAccount result = this.em.find(CloudProviderAccount.class,
                new Integer(cloudProviderAccountId));
        this.em.remove(result);

    }

    @Override
    public CloudProvider updateCloudProvider(String id,
            Map<String, Object> updatedAttributes)
            throws CloudProviderException {

        CloudProvider lCP = this.getCloudProviderById(id);

        try {
            utilForManagers.fillObject(lCP, updatedAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CloudProviderException();
        }

        return this.updateCloudProvider(lCP);
    }

    @Override
    public CloudProvider updateCloudProvider(CloudProvider CP)
            throws CloudProviderException {

        Integer CPId = CP.getId();
        this.em.merge(CP);

        return this.getCloudProviderById(CPId.toString());
    }

    @Override
    public CloudProviderAccount updateCloudProviderAccount(String id,
            Map<String, Object> updatedAttributes)
            throws CloudProviderException {

        CloudProviderAccount lCPA = this.getCloudProviderAccountById(id);

        try {
            utilForManagers.fillObject(lCPA, updatedAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CloudProviderException();
        }

        return this.updateCloudProviderAccount(lCPA);
    }

    @Override
    public CloudProviderAccount updateCloudProviderAccount(
            CloudProviderAccount CPA) throws CloudProviderException {

        Integer CPAId = CPA.getId();
        this.em.merge(CPA);

        return this.getCloudProviderAccountById(CPAId.toString());
    }

    @Override
    public CloudProviderLocation createCloudProviderLocation(
            String Iso3166Code, String countryName, String stateName)
            throws CloudProviderException {

        CloudProviderLocation cpl = new CloudProviderLocation();

        cpl.setIso3166Code(Iso3166Code);
        cpl.setCountryName(countryName);
        cpl.setStateName(stateName);

        this.em.persist(cpl);
        return cpl;
    }

    @Override
    public CloudProviderLocation getCloudProviderLocationById(
            String cloudProviderLocationId) throws CloudProviderException {

        CloudProviderLocation result = this.em.find(
                CloudProviderLocation.class, new Integer(
                        cloudProviderLocationId));

        return result;
    }

    @Override
    public CloudProviderLocation updateCloudProviderLocation(String id,
            Map<String, Object> updatedAttributes)
            throws CloudProviderException {

        CloudProviderLocation lCPL = this.getCloudProviderLocationById(id);

        try {
            utilForManagers.fillObject(lCPL, updatedAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CloudProviderException();
        }

        return this.updateCloudProviderLocation(lCPL);
    }

    @Override
    public CloudProviderLocation updateCloudProviderLocation(
            CloudProviderLocation CPL) throws CloudProviderException {

        Integer CPLId = CPL.getId();
        this.em.merge(CPL);

        return this.getCloudProviderLocationById(CPLId.toString());
    }

    @Override
    public void deleteCloudProviderLocation(String cloudProviderLocationId)
            throws CloudProviderException {

        CloudProviderLocation result = this.em.find(
                CloudProviderLocation.class, new Integer(
                        cloudProviderLocationId));
        this.em.remove(result);

    }

}
