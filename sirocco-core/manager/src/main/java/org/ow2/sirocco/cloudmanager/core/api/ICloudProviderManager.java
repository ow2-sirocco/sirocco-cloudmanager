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

import java.util.Map;

import org.ow2.sirocco.cloudmanager.core.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.exception.UserException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderLocation;

public interface ICloudProviderManager {
	
	static final String EJB_JNDI_NAME = "CloudProviderManager";
	
	CloudProvider createCloudProvider(String type,String description) throws CloudProviderException;
    CloudProvider getCloudProviderById(String cloudProviderId) throws CloudProviderException;
    CloudProvider updateCloudProvider(final String id,Map<String, Object> updatedAttributes) throws CloudProviderException;
    CloudProvider updateCloudProvider(CloudProvider CP) throws CloudProviderException;
    void deleteCloudProvider(String cloudProviderId) throws CloudProviderException;

    CloudProviderAccount createCloudProviderAccount(String cloudProviderId, String user, String login,String password)
        throws CloudProviderException;
    CloudProviderAccount getCloudProviderAccountById(String cloudProviderAccountId) throws CloudProviderException;
    CloudProviderAccount updateCloudProviderAccount(final String id,Map<String, Object> updatedAttributes) throws CloudProviderException;
    CloudProviderAccount updateCloudProviderAccount(CloudProviderAccount CP) throws CloudProviderException;   
    void deleteCloudProviderAccount(String cloudProviderAccountId) throws CloudProviderException;

    CloudProviderLocation createCloudProviderLocation(String Iso3166Code,String countryName, String stateName) throws CloudProviderException;
    CloudProviderLocation getCloudProviderLocationById(String cloudProviderLocationId) throws CloudProviderException;
    CloudProviderLocation updateCloudProviderLocation(final String id,Map<String, Object> updatedAttributes) throws CloudProviderException;
    CloudProviderLocation updateCloudProviderLocation(CloudProviderLocation CP) throws CloudProviderException;
    void deleteCloudProviderLocation(String cloudProviderLocationId) throws CloudProviderException;

    
    
    void addCloudProviderAccountToUser(String userId, String cloudProviderAccountId) throws CloudProviderException,UserException;
    void addCloudProviderAccountToUserByName(String userName, String cloudProviderAccountId) throws CloudProviderException,UserException;
 	void removeCloudProviderAccountFromUser(String userId,
			String cloudProviderAccountId) throws CloudProviderException,
			UserException;
	void removeCloudProviderAccountFromUserByName(String userName,
			String cloudProviderAccountId) throws CloudProviderException,
			UserException;

}
