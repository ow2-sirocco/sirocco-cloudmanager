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
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.utils.PasswordValidator;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplateCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImageCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplateCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@Stateless
@Remote(IRemoteUserManager.class)
@Local(IUserManager.class)
@SuppressWarnings("unused")
public class UserManager implements IUserManager {

    private static Logger logger = Logger.getLogger(UserManager.class.getName());

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Resource
    private SessionContext ctx;

    @Override
    public User createUser(final String firstName, final String lastName, final String email, final String username,
        final String password) throws CloudProviderException {
        User u = new User();
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setEmail(email);
        u.setUsername(username);
        u.setPassword(password);

        return this.createUser(u);

    }

    @Override
    public User createUser(final User u) throws CloudProviderException {
        // if (!isUserValid(u)) {
        // throw new UserException("user validation failed");
        // }
        this.createAllCollections(u);
        this.em.persist(u);
        return u;
    }

    private void createAllCollections(final User u) {
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
        VolumeImageCollection vic = new VolumeImageCollection();
        vic.setUser(u);
        CredentialsCollection creds = new CredentialsCollection();
        creds.setUser(u);
        CredentialsTemplateCollection credsTemplates = new CredentialsTemplateCollection();
        credsTemplates.setUser(u);
        CloudEntryPoint cep = new CloudEntryPoint();
        cep.setUser(u);

        // persist them in the database
        this.em.persist(mic);
        this.em.persist(mc);
        this.em.persist(vc);
        this.em.persist(mtc);
        this.em.persist(mcc);
        this.em.persist(vtc);
        this.em.persist(vcc);
        this.em.persist(vic);
        this.em.persist(creds);
        this.em.persist(credsTemplates);
        this.em.persist(cep);
    }

    private boolean isUserValid(final User u) {
        if (u.getFirstName() == null) {
            return false;
        }
        if (u.getFirstName().equals("")) {
            return false;
        }

        if (u.getLastName() == null) {
            return false;
        }
        if (u.getLastName().equals("")) {
            return false;
        }

        if (u.getEmail() == null) {
            return false;
        }
        if (!(EmailValidator.getInstance().isValid(u.getEmail()))) {
            return false;
        }

        if (u.getPassword() == null) {
            return false;
        }
        if (!(new PasswordValidator().validate(u.getPassword()))) {
            return false;
        }

        return true;
    }

    @Override
    public User getUserById(final String userId) throws CloudProviderException {

        User result = this.em.find(User.class, new Integer(userId));

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public User getUserByUsername(final String userName) throws CloudProviderException {

        User u = null;

        List<User> l = this.em.createQuery("FROM User u WHERE u.username=:usrname").setParameter("usrname", userName)
            .getResultList();

        if (!l.isEmpty()) {
            return l.get(0);
        } else {
            UserManager.logger.info("User " + userName + " unknown");
            return null;
        }
    }

    @Override
    public User updateUser(final String id, final Map<String, Object> updatedAttributes) throws CloudProviderException {

        User u = this.getUserById(id);

        try {
            UtilsForManagers.fillObject(u, updatedAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CloudProviderException();
        }

        return this.updateUser(u);
    }

    @Override
    public User updateUser(final User user) throws CloudProviderException {

        Integer userId = user.getId();
        // if (!isUserValid(user)) {
        // throw new UserException("user validation failed");
        // }
        this.em.merge(user);

        return user;
    }

    @Override
    public void deleteUser(final String userId) throws CloudProviderException {

        User result = this.getUserById(userId);

        this.removeCollection("MachineImageCollection", result);
        this.removeCollection("MachineCollection", result);
        this.removeCollection("VolumeCollection", result);
        this.removeCollection("MachineTemplateCollection", result);
        this.removeCollection("MachineConfigurationCollection", result);
        this.removeCollection("VolumeTemplateCollection", result);
        this.removeCollection("VolumeConfigurationCollection", result);

        if (result != null) {
            this.em.remove(result);
        }

    }

    @SuppressWarnings("unchecked")
    private void removeCollection(final String Type, final User u) {
        List<CloudEntity> l = this.em.createQuery("FROM " + Type + " t WHERE t.user=:usrid").setParameter("usrid", u)
            .getResultList();
        for (CloudEntity lmic : l) {
            this.em.remove(lmic);
        }
    }

}
