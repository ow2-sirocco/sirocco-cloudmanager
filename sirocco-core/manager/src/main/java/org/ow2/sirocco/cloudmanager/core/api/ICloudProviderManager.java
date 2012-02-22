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

import org.ow2.sirocco.cloudmanager.core.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.exception.UserException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderAccount;

public interface ICloudProviderManager {
	
	static final String EJB_JNDI_NAME = "CloudProviderManager";
	
	CloudProvider createCloudProvider(String type,String description) throws CloudProviderException;
    CloudProvider getCloudProviderById(String cloudProviderId) throws CloudProviderException;
    void deleteCloudProvider(String cloudProviderId) throws CloudProviderException;

    CloudProviderAccount createCloudProviderAccount(String cloudProviderId, String user, String login,String password)
        throws CloudProviderException;
    CloudProviderAccount getCloudProviderAccountById(String cloudProviderAccountId) throws CloudProviderException;
    void addCloudProviderAccountToUser(String userId, String cloudProviderAccountId) throws CloudProviderException,UserException;
    void addCloudProviderAccountToUserByName(String userName, String cloudProviderAccountId) throws CloudProviderException,UserException;
    void deleteCloudProviderAccount(String cloudProviderAccountId) throws CloudProviderException;
	void removeCloudProviderAccountFromUser(String userId,
			String cloudProviderAccountId) throws CloudProviderException,
			UserException;
	void removeCloudProviderAccountFromUserByName(String userName,
			String cloudProviderAccountId) throws CloudProviderException,
			UserException;

}
