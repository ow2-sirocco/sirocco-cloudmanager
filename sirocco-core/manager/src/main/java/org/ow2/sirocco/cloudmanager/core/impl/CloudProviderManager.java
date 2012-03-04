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
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.utils.PasswordValidator;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

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
    private EJBContext ctx;
    private String user;

    @EJB
    private IUserManager userManager;


    private void setUser() {
        user = ctx.getCallerPrincipal().getName();
    }

    @Override
    public CloudProvider createCloudProvider(String type, String description)
            throws CloudProviderException {

        CloudProvider cp = new CloudProvider();
        cp.setCloudProviderType(type);
        cp.setDescription(description);
        
        return createCloudProvider(cp);
    }
    
    @Override
    public CloudProvider createCloudProvider(CloudProvider cp) throws CloudProviderException
    {
        //if (!isCloudProviderValid(cp)){throw new CloudProviderException("CloudProvider validation failed");};

        this.em.persist(cp);
        return cp;
    }
    private String normalizeLabel(String label)
    {
        return label.toUpperCase();
    }
    private boolean isCloudProviderValid(CloudProvider cp)
    {        
        return true;
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
            String cloudProviderId, String login, String password)
            throws CloudProviderException {

        CloudProviderAccount cpa = new CloudProviderAccount();

        cpa.setCloudProvider(this.getCloudProviderById(cloudProviderId));
        cpa.setLogin(login);
        cpa.setPassword(password);
        
        cpa=createCloudProviderAccount(cpa);
        
       return cpa;
    }
    
    @Override
    public CloudProviderAccount createCloudProviderAccount(CloudProviderAccount cpa) throws CloudProviderException
    {
        
        //if (!isCloudProviderAccountValid(cpa)){throw new CloudProviderException("CloudProviderAccount validation failed");};

        this.em.persist(cpa);
        return cpa;
         
    }
    
    private boolean isCloudProviderAccountValid(CloudProviderAccount cpa)
    {
        
        if (cpa.getLogin()==null){return false;}
        if (cpa.getLogin().equals("")){return false;}
        
        if (cpa.getPassword()==null){return false;}
        if (cpa.getPassword().equals("")){return false;}
        
        if (cpa.getCloudProvider()==null){return false;}
        
        return true;
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
            CloudProviderException {

        CloudProviderAccount cpa = this
                .getCloudProviderAccountById(cloudProviderAccountId);
        User u = userManager.getUserById(userId);
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
    public void addCloudProviderAccountToUserByName(String userName,
            String cloudProviderAccountId) throws CloudProviderException,
            CloudProviderException {

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
            CloudProviderException {

        CloudProviderAccount cpa = this
                .getCloudProviderAccountById(cloudProviderAccountId);
        User u = userManager.getUserById(userId);
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
    public void removeCloudProviderAccountFromUserByName(String userName,
            String cloudProviderAccountId) throws CloudProviderException,
            CloudProviderException {

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
            UtilsForManagers.fillObject(lCP, updatedAttributes);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CloudProviderException();
        }

        return this.updateCloudProvider(lCP);
    }

    @Override
    public CloudProvider updateCloudProvider(CloudProvider CP)
            throws CloudProviderException {

        //if (!isCloudProviderValid(CP)){throw new CloudProviderException("CloudProvider validation failed");}
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
            UtilsForManagers.fillObject(lCPA, updatedAttributes);
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
        //if (!isCloudProviderAccountValid(CPA)){throw new CloudProviderException("CloudProviderAccount validation failed");};

        this.em.merge(CPA);

        return this.getCloudProviderAccountById(CPAId.toString());
    }

    @Override
    public CloudProviderLocation createCloudProviderLocation(
            String Iso3166_1_Code,String Iso3166_2_Code,String postalCode,Double altitude,Double latitude,Double longitude, String countryName, String stateName,String cityName)
            throws CloudProviderException {

        CloudProviderLocation cpl = new CloudProviderLocation();

        cpl.setIso3166_1(Iso3166_1_Code);
        cpl.setIso3166_2(Iso3166_2_Code);
        cpl.setGPS_Altitude(altitude);
        cpl.setGPS_Latitude(latitude);
        cpl.setGPS_Longitude(longitude);
        cpl.setCountryName(countryName);
        cpl.setStateName(stateName);
        
        return this.createCloudProviderLocation(cpl);
    }
    
    @Override
    public CloudProviderLocation createCloudProviderLocation(CloudProviderLocation cpl) throws CloudProviderException
    {
        cpl.setCityName(normalizeLabel(cpl.getCityName()));
        cpl.setCountryName(normalizeLabel(cpl.getCountryName()));
        cpl.setStateName(normalizeLabel(cpl.getStateName()));
        cpl.setIso3166_1(normalizeLabel(cpl.getIso3166_1()));
        cpl.setIso3166_2(normalizeLabel(cpl.getIso3166_2()));
        cpl.setPostalCode(normalizeLabel(cpl.getPostalCode()));
        
        //if (!isCloudProviderLocationValid(cpl)){throw new CloudProviderException("CloudProviderLocation validation failed");}
      
        this.em.persist(cpl);
        
        return cpl;
    }

    private boolean isCloudProviderLocationValid(CloudProviderLocation cpl)
    {        
        if (cpl.getIso3166_1()==null){return false;}
        if (cpl.getIso3166_1().equals("")){return false;}
        
        if (cpl.getIso3166_2()==null){return false;}
        if (cpl.getIso3166_2().equals("")){return false;}
        
        if (cpl.getPostalCode()==null){return false;}
        if (cpl.getPostalCode().equals("")){return false;}
        
        if (cpl.getGPS_Altitude()==null){return false;}
        if (cpl.getGPS_Altitude().equals("")){return false;}
        
        if (cpl.getGPS_Latitude()==null){return false;}
        if (cpl.getGPS_Latitude().equals("")){return false;}
        
        if (cpl.getGPS_Longitude()==null){return false;}
        if (cpl.getGPS_Longitude().equals("")){return false;}

        
        return true;
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
            UtilsForManagers.fillObject(lCPL, updatedAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CloudProviderException();
        }

        return this.updateCloudProviderLocation(lCPL);
    }

    @Override
    public CloudProviderLocation updateCloudProviderLocation(
            CloudProviderLocation CPL) throws CloudProviderException {

        //if (!isCloudProviderLocationValid(CPL)){throw new CloudProviderException("CloudProviderLocation validation failed");}
        
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
    /**
     * Method to evaluate distance between 2 different locations
     * <br>** Only works if the points are close enough that you can omit 
     * that earth is not regular shape **
     * <br><br><i>see http://androidsnippets.com/distance-between-two-gps-coordinates-in-meter</i>
     * @return
     */
    @Override
    public double locationDistance(CloudProviderLocation pointA,CloudProviderLocation pointB) {
        
        float pk = (float) (180/3.14159265);

        double a1 = pointA.getGPS_Latitude() / pk;
        double a2 = pointA.getGPS_Longitude() / pk;
        double b1 = pointB.getGPS_Latitude() / pk;
        double b2 = pointB.getGPS_Longitude() / pk;

        double t1 = Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2);
        double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
        double t3 = Math.sin(a1)*Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000*tt;
    }
    

}
