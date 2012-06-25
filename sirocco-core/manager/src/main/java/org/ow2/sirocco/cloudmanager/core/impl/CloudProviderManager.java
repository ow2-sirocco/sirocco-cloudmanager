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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.log4j.Logger;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@Stateless
@Remote(IRemoteCloudProviderManager.class)
@Local(ICloudProviderManager.class)
@SuppressWarnings("unused")
public class CloudProviderManager implements ICloudProviderManager {

    private static Logger logger = Logger.getLogger(MachineImageManager.class.getName());

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Resource
    private EJBContext ctx;

    @EJB
    private IUserManager userManager;

    private User getUser() throws CloudProviderException {
        String username = this.ctx.getCallerPrincipal().getName();
        return this.userManager.getUserByUsername(username);
    }

    @Override
    public CloudProvider createCloudProvider(final String type, final String description) throws CloudProviderException {

        CloudProvider cp = new CloudProvider();
        cp.setCloudProviderType(type);
        cp.setDescription(description);

        return this.createCloudProvider(cp);
    }

    @Override
    public CloudProvider createCloudProvider(final CloudProvider cp) throws CloudProviderException {
        // if (!isCloudProviderValid(cp)){throw new
        // CloudProviderException("CloudProvider validation failed");};

        this.em.persist(cp);
        return cp;
    }

    private String normalizeLabel(final String label) {
        return label.toUpperCase();
    }

    private boolean isCloudProviderValid(final CloudProvider cp) {
        return true;
    }

    @Override
    public CloudProvider getCloudProviderById(final String cloudProviderId) throws CloudProviderException {

        CloudProvider result = this.em.find(CloudProvider.class, new Integer(cloudProviderId));

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CloudProvider> getCloudProviders() throws CloudProviderException {
        return this.em.createQuery("Select p From CloudProvider p").getResultList();
    }

    @Override
    public void deleteCloudProvider(final String cloudProviderId) throws CloudProviderException {
        CloudProvider result = this.em.find(CloudProvider.class, new Integer(cloudProviderId));
        this.em.remove(result);
    }

    @Override
    public CloudProviderAccount createCloudProviderAccount(final String cloudProviderId, final String login,
        final String password) throws CloudProviderException {

        CloudProviderAccount cpa = new CloudProviderAccount();

        cpa.setCloudProvider(this.getCloudProviderById(cloudProviderId));
        cpa.setLogin(login);
        cpa.setPassword(password);

        cpa = this.createCloudProviderAccount(cpa);

        return cpa;
    }

    @Override
    public CloudProviderAccount createCloudProviderAccount(final CloudProviderAccount cpa) throws CloudProviderException {

        // if (!isCloudProviderAccountValid(cpa)){throw new
        // CloudProviderException("CloudProviderAccount validation failed");};

        this.em.persist(cpa);
        return cpa;

    }

    private boolean isCloudProviderAccountValid(final CloudProviderAccount cpa) {

        if (cpa.getLogin() == null) {
            return false;
        }
        if (cpa.getLogin().equals("")) {
            return false;
        }

        if (cpa.getPassword() == null) {
            return false;
        }
        if (cpa.getPassword().equals("")) {
            return false;
        }

        if (cpa.getCloudProvider() == null) {
            return false;
        }

        return true;
    }

    @Override
    public CloudProviderAccount getCloudProviderAccountById(final String cloudProviderAccountId) throws CloudProviderException {

        CloudProviderAccount result = this.em.find(CloudProviderAccount.class, new Integer(cloudProviderAccountId));
        if (result != null) {
            result.getUsers().size();
        }
        return result;
    }

    /**
     * add a provider to user by providing an user id
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager#addCloudProviderAccountToUser(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void addCloudProviderAccountToUser(final String userId, final String cloudProviderAccountId)
        throws CloudProviderException {

        CloudProviderAccount cpa = this.getCloudProviderAccountById(cloudProviderAccountId);
        User u = this.userManager.getUserById(userId);
        Set<User> users = cpa.getUsers();
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
    public void addCloudProviderAccountToUserByName(final String userName, final String cloudProviderAccountId)
        throws CloudProviderException {

        this.addCloudProviderAccountToUser(this.userManager.getUserByUsername(userName).getId().toString(),
            cloudProviderAccountId);

    }

    /**
     * add a provider to user by providing an user id
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager#addCloudProviderAccountToUser(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void removeCloudProviderAccountFromUser(final String userId, final String cloudProviderAccountId)
        throws CloudProviderException {

        CloudProviderAccount cpa = this.getCloudProviderAccountById(cloudProviderAccountId);
        User u = this.userManager.getUserById(userId);
        Set<User> users = cpa.getUsers();
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
    public void removeCloudProviderAccountFromUserByName(final String userName, final String cloudProviderAccountId)
        throws CloudProviderException {

        this.removeCloudProviderAccountFromUser(this.userManager.getUserByUsername(userName).getId().toString(),
            cloudProviderAccountId);

    }

    @Override
    public void deleteCloudProviderAccount(final String cloudProviderAccountId) throws CloudProviderException {
        CloudProviderAccount result = this.em.find(CloudProviderAccount.class, new Integer(cloudProviderAccountId));
        this.em.remove(result);

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CloudProviderAccount> getCloudProviderAccounts() throws CloudProviderException {
        return this.em.createQuery("Select p From CloudProviderAccount p").getResultList();
    }

    @Override
    public List<CloudProviderAccount> getCloudProviderAccountsByUser(final String userId) throws CloudProviderException {
        User user = this.userManager.getUserById(userId);
        return new ArrayList<CloudProviderAccount>(user.getCloudProviderAccounts());
    }

    @Override
    public CloudProvider updateCloudProvider(final String id, final Map<String, Object> updatedAttributes)
        throws CloudProviderException {

        CloudProvider lCP = this.getCloudProviderById(id);

        try {
            UtilsForManagers.fillObject(lCP, updatedAttributes);
        } catch (Exception e) {
            CloudProviderManager.logger.info(e.getMessage());
            throw new CloudProviderException();
        }

        return this.updateCloudProvider(lCP);
    }

    @Override
    public CloudProvider updateCloudProvider(final CloudProvider CP) throws CloudProviderException {

        // if (!isCloudProviderValid(CP)){throw new
        // CloudProviderException("CloudProvider validation failed");}
        Integer CPId = CP.getId();
        this.em.merge(CP);

        return this.getCloudProviderById(CPId.toString());
    }

    @Override
    public CloudProviderAccount updateCloudProviderAccount(final String id, final Map<String, Object> updatedAttributes)
        throws CloudProviderException {

        CloudProviderAccount lCPA = this.getCloudProviderAccountById(id);

        try {
            UtilsForManagers.fillObject(lCPA, updatedAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CloudProviderException();
        }

        return this.updateCloudProviderAccount(lCPA);
    }

    @Override
    public CloudProviderAccount updateCloudProviderAccount(final CloudProviderAccount CPA) throws CloudProviderException {

        Integer CPAId = CPA.getId();
        // if (!isCloudProviderAccountValid(CPA)){throw new
        // CloudProviderException("CloudProviderAccount validation failed");};

        this.em.merge(CPA);

        return this.getCloudProviderAccountById(CPAId.toString());
    }

    @Override
    public CloudProviderLocation createCloudProviderLocation(final String Iso3166_1_Code, final String Iso3166_2_Code,
        final String postalCode, final Double altitude, final Double latitude, final Double longitude,
        final String countryName, final String stateName, final String cityName) throws CloudProviderException {

        CloudProviderLocation cpl = new CloudProviderLocation();

        cpl.setIso3166_1(Iso3166_1_Code);
        cpl.setIso3166_2(Iso3166_2_Code);
        cpl.setPostalCode(postalCode);
        cpl.setGPS_Altitude(altitude);
        cpl.setGPS_Latitude(latitude);
        cpl.setGPS_Longitude(longitude);
        cpl.setCountryName(countryName);
        cpl.setStateName(stateName);
        cpl.setCityName(cityName);

        return this.createCloudProviderLocation(cpl);
    }

    private CloudProviderLocation normalizeCloudProviderLocation(final CloudProviderLocation cpl) throws CloudProviderException {
        cpl.setCityName(this.normalizeLabel(cpl.getCityName()));
        cpl.setCountryName(this.normalizeLabel(cpl.getCountryName()));
        cpl.setStateName(this.normalizeLabel(cpl.getStateName()));
        cpl.setIso3166_1(this.normalizeLabel(cpl.getIso3166_1()));
        cpl.setIso3166_2(this.normalizeLabel(cpl.getIso3166_2()));
        cpl.setPostalCode(this.normalizeLabel(cpl.getPostalCode()));
        return cpl;
    }

    @Override
    public CloudProviderLocation createCloudProviderLocation(final CloudProviderLocation cpl) throws CloudProviderException {
        this.normalizeCloudProviderLocation(cpl);

        // if (!isCloudProviderLocationValid(cpl)){throw new
        // CloudProviderException("CloudProviderLocation validation failed");}

        this.em.persist(cpl);

        return cpl;
    }

    private boolean isCloudProviderLocationValid(final CloudProviderLocation cpl) {
        if (cpl.getIso3166_1() == null) {
            return false;
        }
        if (cpl.getIso3166_1().equals("")) {
            return false;
        }

        if (cpl.getIso3166_2() == null) {
            return false;
        }
        if (cpl.getIso3166_2().equals("")) {
            return false;
        }

        if (cpl.getPostalCode() == null) {
            return false;
        }
        if (cpl.getPostalCode().equals("")) {
            return false;
        }

        if (cpl.getGPS_Altitude() == null) {
            return false;
        }
        if (cpl.getGPS_Altitude().equals("")) {
            return false;
        }

        if (cpl.getGPS_Latitude() == null) {
            return false;
        }
        if (cpl.getGPS_Latitude().equals("")) {
            return false;
        }

        if (cpl.getGPS_Longitude() == null) {
            return false;
        }
        if (cpl.getGPS_Longitude().equals("")) {
            return false;
        }

        return true;
    }

    @Override
    public CloudProviderLocation getCloudProviderLocationById(final String cloudProviderLocationId)
        throws CloudProviderException {

        CloudProviderLocation result = this.em.find(CloudProviderLocation.class, new Integer(cloudProviderLocationId));

        return result;
    }

    @Override
    public CloudProviderLocation updateCloudProviderLocation(final String id, final Map<String, Object> updatedAttributes)
        throws CloudProviderException {

        CloudProviderLocation lCPL = this.getCloudProviderLocationById(id);

        try {
            UtilsForManagers.fillObject(lCPL, updatedAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CloudProviderException();
        }

        return this.updateCloudProviderLocation(lCPL);
    }

    @Override
    public CloudProviderLocation updateCloudProviderLocation(final CloudProviderLocation CPL) throws CloudProviderException {

        // if (!isCloudProviderLocationValid(CPL)){throw new
        // CloudProviderException("CloudProviderLocation validation failed");}

        Integer CPLId = CPL.getId();
        this.normalizeCloudProviderLocation(CPL);
        this.em.merge(CPL);

        return this.getCloudProviderLocationById(CPLId.toString());
    }

    @Override
    public void deleteCloudProviderLocation(final String cloudProviderLocationId) throws CloudProviderException {

        CloudProviderLocation result = this.em.find(CloudProviderLocation.class, new Integer(cloudProviderLocationId));
        this.em.remove(result);

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CloudProviderLocation> getCloudProviderLocations() throws CloudProviderException {
        return UtilsForManagers.getEntityList("CloudProviderLocation", this.em, this.getUser().getUsername());
    }

    /**
     * Method to evaluate distance between 2 different locations <br>
     * ** Only works if the points are close enough that you can omit that earth
     * is not regular shape ** <br>
     * <br>
     * <i>see
     * http://androidsnippets.com/distance-between-two-gps-coordinates-in-
     * meter</i>
     * 
     * @return
     */
    @Override
    public double locationDistance(final CloudProviderLocation pointA, final CloudProviderLocation pointB) {

        float pk = (float) (180 / 3.14159265);

        double a1 = pointA.getGPS_Latitude() / pk;
        double a2 = pointA.getGPS_Longitude() / pk;
        double b1 = pointB.getGPS_Latitude() / pk;
        double b2 = pointB.getGPS_Longitude() / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

}
