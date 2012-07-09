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
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.log4j.Logger;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@Stateless
@Remote(IRemoteCredentialsManager.class)
@Local(ICredentialsManager.class)
public class CredentialsManager implements ICredentialsManager {

    private static Logger logger = Logger.getLogger(CredentialsManager.class.getName());

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Resource
    private SessionContext ctx;

    @EJB
    private IUserManager userManager;

    @Resource
    public void setSessionContext(final SessionContext ctx) {
        this.ctx = ctx;
    }

    private User getUser() throws CloudProviderException {
        String username = this.ctx.getCallerPrincipal().getName();
        return this.userManager.getUserByUsername(username);
    }

    private void validateCredentials(final Credentials cred) throws CloudProviderException {
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

    public Credentials createCredentials(final CredentialsCreate credentialsCreate) throws CloudProviderException {
        CredentialsManager.logger.info("validateCredentials ");

        Credentials credentials = new Credentials();
        credentials.setCreated(new Date());
        credentials.setName(credentialsCreate.getName());
        credentials.setDescription(credentialsCreate.getDescription());
        credentials.setProperties(credentialsCreate.getProperties());
        credentials.setUser(this.getUser());
        credentials.setUserName(credentialsCreate.getCredentialsTemplate().getUserName());
        credentials.setPassword(credentialsCreate.getCredentialsTemplate().getPassword());
        credentials.setPublicKey(credentialsCreate.getCredentialsTemplate().getPublicKey());

        this.validateCredentials(credentials);

        CredentialsManager.logger.info("Persist credentials  " + credentials.getName());
        this.em.persist(credentials);
        this.em.flush();
        CredentialsManager.logger.info("Persist credentials return " + credentials.getId());
        return credentials;
    }

    public void updateCredentials(final Credentials credentials) throws CloudProviderException {
        User user = this.getUser();

        Credentials c = this.em.find(Credentials.class, credentials.getId());
        if (c == null) {
            throw new ResourceNotFoundException(" Could not find credential " + credentials.getId());
        }
        if (c.getUser().getUsername() != user.getUsername()) {
            throw new CloudProviderException(" Unauthorized to change creds " + user.getUsername());
        }
        this.validateCredentials(credentials);
        this.em.merge(credentials);

    }

    public Credentials getCredentialsById(final String credentialsId) throws CloudProviderException {
        User user = this.getUser();
        if (credentialsId == null) {
            throw new InvalidRequestException("null credentials id");
        }
        Credentials cred = this.em.find(Credentials.class, Integer.valueOf(credentialsId));
        if (cred == null) {
            throw new ResourceNotFoundException("Credentials " + credentialsId + " not found");
        }
        return cred;
    }

    public void deleteCredentials(final String credentialsId) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
        User user = this.getUser();
        if (credentialsId == null) {
            throw new InvalidRequestException("null credentials id");
        }
        Credentials cred = this.em.find(Credentials.class, Integer.valueOf(credentialsId));
        if (cred == null) {
            throw new ResourceNotFoundException(" Invalid credential id " + credentialsId);
        }
        /**
         * if anymachine template refers to this credential do not delete.
         */
        List<MachineTemplate> mts = null;
        try {
            mts = this.em.createQuery("FROM MachineTemplate t WHERE t.credentials=:cred").setParameter("cred", cred)
                .getResultList();
        } catch (Exception e) {
            throw new CloudProviderException(" Internal Error");
        }
        if (mts.size() != 0) {
            throw new InvalidRequestException(" Credential " + credentialsId + " is in use");
        }

        /** delete credentials */
        this.em.remove(cred);
        this.em.flush();
        return;
    }

    public void updateCredentialsAttributes(final String credentialsId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {

        if (credentialsId == null) {
            throw new InvalidRequestException("null credentials id");
        }
        Credentials cred = this.em.find(Credentials.class, Integer.valueOf(credentialsId));
        if (cred == null) {
            throw new ResourceNotFoundException("Credentials " + credentialsId + " not found");
        }

        try {
            UtilsForManagers.fillObject(cred, attributes);
        } catch (Exception e) {
            throw new CloudProviderException(e.getMessage());
        }
        this.em.merge(cred);
        this.em.flush();
    }

    @Override
    public List<Credentials> getCredentials() throws CloudProviderException {
        return this.em.createQuery("SELECT c FROM Credentials c WHERE c.user.id=:userid")
            .setParameter("userid", this.getUser().getId()).getResultList();
    }

    @Override
    public QueryResult<Credentials> getCredentials(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        return UtilsForManagers.getEntityList("Credentials", this.em, user.getUsername(), first, last, filters, attributes,
            false);
    }

    @Override
    public CredentialsTemplate createCredentialsTemplate(final CredentialsTemplate credentialsTemplate)
        throws CloudProviderException {
        User user = this.getUser();

        credentialsTemplate.setUser(user);
        credentialsTemplate.setCreated(new Date());

        CredentialsManager.logger.info("Persist credentialsTemplate  " + credentialsTemplate.getName());
        this.em.persist(credentialsTemplate);
        this.em.flush();
        return credentialsTemplate;
    }

    @Override
    public void updateCredentialsTemplate(final CredentialsTemplate credentialsTemplate) throws CloudProviderException {
        // TODO Auto-generated method stub
    }

    @Override
    public CredentialsTemplate getCredentialsTemplateById(final String credentialsTemplateId) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteCredentialsTemplate(final String credentialsTemplateId) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateCredentialsTemplateAttributes(final String credentialsTemplateId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public List<CredentialsTemplate> getCredentialsTemplates() throws CloudProviderException {
        return this.em.createQuery("SELECT c FROM CredentialsTemplate c WHERE c.user.id=:userid")
            .setParameter("userid", this.getUser().getId()).getResultList();
    }

    @Override
    public QueryResult<CredentialsTemplate> getCredentialsTemplates(final int first, final int last,
        final List<String> filters, final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        User user = this.getUser();
        return UtilsForManagers.getEntityList("CredentialsTemplate", this.em, user.getUsername(), first, last, filters,
            attributes, false);
    }
}