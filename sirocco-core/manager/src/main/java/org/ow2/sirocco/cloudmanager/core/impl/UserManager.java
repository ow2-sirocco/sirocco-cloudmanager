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

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.commons.validator.routines.EmailValidator;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.utils.PasswordValidator;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Remote(IRemoteUserManager.class)
@Local(IUserManager.class)
@SuppressWarnings("unused")
public class UserManager implements IUserManager {

    private static Logger logger = LoggerFactory.getLogger(UserManager.class.getName());

    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Inject
    private IdentityContext identityContext;

    public String md5(final String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    @Override
    public User createUser(final String firstName, final String lastName, final String email, final String username,
        final String password) throws CloudProviderException {
        try {
            this.em.createQuery("SELECT u FROM User u WHERE u.username=:name").setParameter("name", username).getSingleResult();
            throw new CloudProviderException("User with username " + username + " already exists");
        } catch (NoResultException e) {
        }
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
        u.setRole("sirocco-user");
        u.setPassword(this.md5(u.getPassword()));
        this.em.persist(u);
        return u;
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
        if (result == null) {
            throw new ResourceNotFoundException();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> getUsers() throws CloudProviderException {
        return this.em.createQuery("SELECT  u From User u").getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public User getUserByUsername(final String userName) throws CloudProviderException {

        User u = null;

        List<User> l = this.em.createQuery("SELECT u FROM User u WHERE u.username=:usrname").setParameter("usrname", userName)
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

        if (result != null) {
            this.em.remove(result);
        }

    }

}
