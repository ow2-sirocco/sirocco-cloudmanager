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

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.exception.UserException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.User;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplateCollection;

@Stateless(name = IUserManager.EJB_JNDI_NAME, mappedName = IUserManager.EJB_JNDI_NAME)
@Remote(IRemoteUserManager.class)
@Local(IUserManager.class)
@SuppressWarnings("unused")
public class UserManager implements IUserManager {

    private static Logger logger = Logger
            .getLogger(UserManager.class.getName());
    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;
    
    @Resource
    private SessionContext ctx;

    @Override
    public User createUser(String firstName, String lastName, String email,
            String username, String password)
            throws UserException {
        User u = new User();
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setEmail(email);
        u.setUsername(username);
        u.setPassword(password);

        this.em.persist(u);

        // create collection objects and attach them to the user
        MachineImageCollection mic = new MachineImageCollection();
        mic.setUser(u);
        MachineCollection mc = new MachineCollection();
        mc.setUser(u);
        VolumeCollection vc = new VolumeCollection();
        vc.setUser(u);
        MachineTemplateCollection mtc = new MachineTemplateCollection();
        mtc.setUser(u);
        MachineConfigurationCollection mcc = new MachineConfigurationCollection();
        mcc.setUser(u);
        VolumeTemplateCollection vtc = new VolumeTemplateCollection();
        vtc.setUser(u);
        VolumeConfigurationCollection vcc = new VolumeConfigurationCollection();
        vcc.setUser(u);

        // persist them in the database
        this.em.persist(mic);
        this.em.persist(mc);
        this.em.persist(vc);
        this.em.persist(mtc);
        this.em.persist(mcc);
        this.em.persist(vtc);
        this.em.persist(vcc);

        return u;
    }

    @Override
    public User getUserById(String userId) throws UserException {

        User result = this.em.find(User.class, new Integer(userId));

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public User getUserByUsername(String userName) throws UserException {

        User u = null;

        List<User> l = this.em
                .createQuery("FROM User u WHERE u.username=:usrname")
                .setParameter("usrname", userName).getResultList();

        return l.get(0);
    }

    @Override
    public User updateUser(User user) throws UserException {

        Integer userId = user.getId();
        this.em.merge(user);

        return this.getUserById(userId.toString());
    }

    @Override
    public void deleteUser(String userId) throws UserException {

        User result = this.getUserById(userId);

        removeCollection("MachineImageCollection", result);
        removeCollection("MachineCollection", result);
        removeCollection("VolumeCollection", result);
        removeCollection("MachineTemplateCollection", result);
        removeCollection("MachineConfigurationCollection", result);
        removeCollection("VolumeTemplateCollection", result);
        removeCollection("VolumeConfigurationCollection", result);

        if (result!=null){this.em.remove(result);} 
        

    }

    @SuppressWarnings("unchecked")
    private void removeCollection(String Type, User u) {
        List<CloudEntity> l = this.em
                .createQuery("FROM " + Type + " t WHERE t.user=:usrid")
                .setParameter("usrid", u).getResultList();
        for (CloudEntity lmic : l) {
            lmic.setUser(null);
            this.em.remove(lmic);
        }
    }

}
