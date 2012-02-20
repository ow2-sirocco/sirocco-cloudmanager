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
import org.ow2.sirocco.cloudmanager.model.cimi.User;

@Stateless(name = IUserManager.EJB_JNDI_NAME, mappedName = ICloudProviderManager.EJB_JNDI_NAME)
@Remote(IRemoteUserManager.class)
@Local(IUserManager.class)
public class UserManager implements IUserManager {

	private static Logger logger = Logger.getLogger(MachineImageManager.class
			.getName());
	@PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
	private EntityManager em;

	@Resource
	private SessionContext ctx;

	@Override
	public User createUser(String firstName, String lastName, String email,
			String username, String password, String publicKey)
			throws UserException {
		User u = new User();
		u.setFirstName(firstName);
		u.setLastName(lastName);
		u.setEmail(email);
		u.setUsername(username);
		u.setPassword(password);
		u.setPublicKey(publicKey);

		this.em.persist(u);
		return u;
	}

	@Override
	public User getUserById(String userId) throws UserException {

		User result = this.em.find(User.class, new Integer(userId));

		return result;
	}

	@Override
	public User getUserByUsername(String userName) throws UserException {

		User u = null;
		try {
			u = (User) this.em
					.createQuery("FROM User u WHERE u.username=:usrname")
					.setParameter("usrname", userName).getResultList().get(0);
		} catch (IndexOutOfBoundsException e) {
			throw new UserException();
		}

		return u;
	}

	@Override
	public User updateUser(User user)
			throws UserException {
		/*User u=new User(); //= getUserById(userId);
		if (firstName!=null){u.setFirstName(firstName);}
		if (lastName!=null){u.setLastName(lastName);}
		if (email!=null){u.setEmail(email);}
		if (username!=null){u.setUsername(username);}
		if (password!=null){u.setPassword(password);}
		if (publicKey!=null){u.setPublicKey(publicKey);}*/
		
		this.em.merge(user);
		
		return user;
	}

	@Override
	public void deleteUser(String userId) throws UserException {

		User result = this.em.find(User.class, new Integer(userId));
		this.em.remove(result);

	}

}
