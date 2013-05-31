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

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.utils.QueryHelper;
import org.ow2.sirocco.cloudmanager.core.utils.UtilsForManagers;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Remote(IRemoteCredentialsManager.class)
@Local(ICredentialsManager.class)
@IdentityInterceptorBinding
public class CredentialsManager implements ICredentialsManager {

    private static Logger logger = LoggerFactory.getLogger(CredentialsManager.class.getName());

    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Inject
    private IdentityContext identityContext;

    @EJB
    private ITenantManager tenantManager;

    private Tenant getTenant() throws CloudProviderException {
        return this.tenantManager.getTenant(this.identityContext);
    }

    private void validateCredentials(final Credentials cred) throws CloudProviderException {
        if (cred.getUserName() != null) {
            if (cred.getUserName().length() < 1) {
                throw new InvalidRequestException("Too short user name ");
            }
            if (Pattern.matches("[\\w]*$", cred.getUserName()) == false) {
                throw new InvalidRequestException("Non word characters in user name ");
            }
        }
        if (cred.getPassword() != null) {
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
    }

    public Credentials createCredentials(final CredentialsCreate credentialsCreate) throws CloudProviderException {
        CredentialsManager.logger.info("validateCredentials ");

        Credentials credentials = new Credentials();
        credentials.setCreated(new Date());
        credentials.setName(credentialsCreate.getName());
        credentials.setDescription(credentialsCreate.getDescription());
        credentials.setProperties(credentialsCreate.getProperties());
        credentials.setTenant(this.getTenant());
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
        Credentials c = this.em.find(Credentials.class, credentials.getId());
        if (c == null) {
            throw new ResourceNotFoundException(" Could not find credential " + credentials.getId());
        }
        this.validateCredentials(credentials);
        this.em.merge(credentials);

    }

    public Credentials getCredentialsById(final String credentialsId) throws CloudProviderException {
        if (credentialsId == null) {
            throw new InvalidRequestException("null credentials id");
        }
        Credentials cred = this.em.find(Credentials.class, Integer.valueOf(credentialsId));
        if (cred == null) {
            throw new ResourceNotFoundException("Credentials " + credentialsId + " not found");
        }
        return cred;
    }

    @Override
    public Credentials getCredentialsAttributes(final String credentialsId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        Credentials cred = this.getCredentialsById(credentialsId);
        return UtilsForManagers.fillResourceAttributes(cred, attributes);
    }

    public void deleteCredentials(final String credentialsId) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
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
            mts = this.em.createQuery("SELECT t FROM MachineTemplate t WHERE t.credential=:cred").setParameter("cred", cred)
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
        return this.em.createQuery("SELECT c FROM Credentials c WHERE c.tenant.id=:tenantId")
            .setParameter("tenantId", this.getTenant().getId()).getResultList();
    }

    @Override
    public QueryResult<Credentials> getCredentials(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("Credentials", Credentials.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes));
    }

    @Override
    public CredentialsTemplate createCredentialsTemplate(final CredentialsTemplate credentialsTemplate)
        throws CloudProviderException {

        credentialsTemplate.setTenant(this.getTenant());
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
    public CredentialsTemplate getCredentialsTemplateAttributes(final String credentialsTemplateId,
        final List<String> attributes) throws ResourceNotFoundException, CloudProviderException {
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
        return this.em.createQuery("SELECT c FROM CredentialsTemplate c WHERE c.tenant.id=:tenantId")
            .setParameter("tenantId", this.getTenant().getId()).getResultList();
    }

    @Override
    public QueryResult<CredentialsTemplate> getCredentialsTemplates(final int first, final int last,
        final List<String> filters, final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        QueryHelper.QueryParamsBuilder params = QueryHelper.QueryParamsBuilder.builder("CredentialsTemplate",
            CredentialsTemplate.class);
        return QueryHelper.getEntityList(this.em,
            params.tenantId(this.getTenant().getId()).first(first).last(last).filter(filters).attributes(attributes)
                .filterEmbbededTemplate());
    }
}