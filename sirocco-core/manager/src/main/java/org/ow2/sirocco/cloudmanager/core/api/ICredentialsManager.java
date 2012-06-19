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

package org.ow2.sirocco.cloudmanager.core.api;

import java.util.List;
import java.util.Map;

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;

/**
 * Credentials management operations
 */
public interface ICredentialsManager {

    static final String EJB_JNDI_NAME = "org.ow2.sirocco.cloudmanager.core.impl.CredentialsManager_org.ow2.sirocco.cloudmanager.core.api.IRemoteCredentialsManager@Remote";

    Credentials createCredentials(CredentialsCreate credentialsCreate) throws CloudProviderException;

    void updateCredentials(Credentials credentials) throws CloudProviderException;

    Credentials getCredentialsById(String credentialsId) throws CloudProviderException;

    void deleteCredentials(String credentialsId) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    void updateCredentialsAttributes(String credentialsId, Map<String, Object> attributes) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    List<Credentials> getCredentials(List<String> attributes, String filterExpression) throws InvalidRequestException,
        CloudProviderException;

    List<Credentials> getCredentials(int first, int last, List<String> attributes) throws InvalidRequestException,
        CloudProviderException;

    CredentialsTemplate createCredentialsTemplate(CredentialsTemplate credentialsTemplate) throws CloudProviderException;

    void updateCredentialsTemplate(CredentialsTemplate credentialsTemplate) throws CloudProviderException;

    CredentialsTemplate getCredentialsTemplateById(String credentialsTemplateId) throws CloudProviderException;

    void deleteCredentialsTemplate(String credentialsTemplateId) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    void updateCredentialsTemplateAttributes(String credentialsTemplateId, Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    List<CredentialsTemplate> getCredentialsTemplates(List<String> attributes, String filterExpression)
        throws InvalidRequestException, CloudProviderException;

    List<CredentialsTemplate> getCredentialsTemplates(int first, int last, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;
}
