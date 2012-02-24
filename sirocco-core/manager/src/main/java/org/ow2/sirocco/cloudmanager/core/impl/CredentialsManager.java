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

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.User;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.exception.ResourceNotFoundException;


@Stateless(name = ICredentialsManager.EJB_JNDI_NAME, mappedName = ICredentialsManager.EJB_JNDI_NAME)
@Remote(IRemoteCredentialsManager.class)
@Local(ICredentialsManager.class)
public class CredentialsManager implements ICredentialsManager {

	private static Logger logger = Logger.getLogger(CredentialsManager.class.getName());
	@PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
	private EntityManager em;
	
	@Resource
	private SessionContext		ctx;
	
	@EJB
    private IUserManager userManager;
	private	User				user;
	
	@Resource
	public void setSessionContext(SessionContext ctx) {
		this.ctx = ctx;
	}

	private void setUser() throws CloudProviderException {
		String username = ctx.getCallerPrincipal().getName();
		user = userManager.getUserByUsername(username);
	}	
	
	private void validateCredentials(Credentials cred) throws CloudProviderException {
		if (cred.getUserName().length() < 1) {
			throw new InvalidRequestException("Too short user name ");
		}
		if (Pattern.matches("[\\w]*$", cred.getUserName()) == false) {
			throw new InvalidRequestException("Non word characters in user name ");
		}
		if (cred.getPassword().length() < 1) {
			throw new InvalidRequestException("Too short password ");
		}
		if (Pattern.matches("[\\w]*$", cred.getPassword()) == false) {
			throw new InvalidRequestException("Non word characters in user name ");
		}
		if (Pattern.matches("[^\\s]*$", cred.getPassword()) == false) {
			throw new InvalidRequestException("Spaces in password ");
		}
	}
	public Credentials createCredentials(Credentials credentials) throws CloudProviderException {
		setUser();
		// validate user
		validateCredentials(credentials);
		credentials.setCreated(new Date());
		credentials.setUser(user);
		this.em.persist(credentials);
		return credentials;
	}
	
    public Credentials updateCredentials(Credentials credentials) throws CloudProviderException {
    	throw new InvalidRequestException(" Cannot update credentials ");
    }
    
    public Credentials getCredentialsById(String credentialsId) throws CloudProviderException {
    	setUser();
    	if (credentialsId == null) {
    		throw new InvalidRequestException("null credentials id");
    	}
    	Credentials cred = this.em.find(Credentials.class, Integer.valueOf(credentialsId));
    	if (cred == null) {
    		throw new ResourceNotFoundException("Credentials " +credentialsId +" not found");
    	}
    	/** Would be nice to use em.detach instead
    	 */
    	Credentials c = new Credentials();
    	c.setUserName(cred.getUserName());
    	c.setName(cred.getName());
    	c.setId(cred.getId());
    	c.setProperties(cred.getProperties());
    	c.setCreated(cred.getCreated());
    	c.setUpdated(cred.getUpdated());
    	c.setPassword("");
    	c.setKey(null);
    	return cred;
    }
    
    
    public void deleteCredentials(String credentialsId) throws CloudProviderException {
    	setUser();
    	if (credentialsId == null) {
    		throw new InvalidRequestException("null credentials id");
    	}
    	Credentials cred = this.em.find(Credentials.class, Integer.valueOf(credentialsId));
    	/** if anymachine template refers to this credential
    	 * do not delete.
    	 */
    	List<MachineTemplate> mts = null;
    	try {
    		mts = this.em.createQuery("FROM MachineTemplate t WHERE t.credentials=:cred").setParameter("cred", cred).getResultList();
    	} catch (Exception e) {
    		throw new CloudProviderException(" Internal Error");
    	}
    	if (mts.size() != 0) {
    		throw new InvalidRequestException(" Credential " +credentialsId +" is in use");
    	}
    	
    	/** delete credentials */
    	this.em.remove(cred);
    	this.em.flush();
    	return;
    }

}