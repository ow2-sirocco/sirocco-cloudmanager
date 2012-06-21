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
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

public interface ICloudProviderManager {

    static final String EJB_JNDI_NAME = "org.ow2.sirocco.cloudmanager.core.impl.CloudProviderManager_org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager@Remote";

    CloudProvider createCloudProvider(String type, String description) throws CloudProviderException;

    CloudProvider createCloudProvider(CloudProvider cp) throws CloudProviderException;

    CloudProvider getCloudProviderById(String cloudProviderId) throws CloudProviderException;

    CloudProvider updateCloudProvider(final String id, Map<String, Object> updatedAttributes) throws CloudProviderException;

    CloudProvider updateCloudProvider(CloudProvider CP) throws CloudProviderException;

    void deleteCloudProvider(String cloudProviderId) throws CloudProviderException;

    CloudProviderAccount createCloudProviderAccount(String cloudProviderId, String login, String password)
        throws CloudProviderException;

    CloudProviderAccount createCloudProviderAccount(CloudProviderAccount cpa) throws CloudProviderException;

    CloudProviderAccount getCloudProviderAccountById(String cloudProviderAccountId) throws CloudProviderException;

    CloudProviderAccount updateCloudProviderAccount(final String id, Map<String, Object> updatedAttributes)
        throws CloudProviderException;

    CloudProviderAccount updateCloudProviderAccount(CloudProviderAccount CP) throws CloudProviderException;

    void deleteCloudProviderAccount(String cloudProviderAccountId) throws CloudProviderException;

    CloudProviderLocation createCloudProviderLocation(String Iso3166_1_Code, String Iso3166_2_Code, String postalCode,
        Double altitude, Double latitude, Double longitude, String countryName, String stateName, String cityName)
        throws CloudProviderException;

    CloudProviderLocation createCloudProviderLocation(CloudProviderLocation cpl) throws CloudProviderException;

    CloudProviderLocation updateCloudProviderLocation(final String id, Map<String, Object> updatedAttributes)
        throws CloudProviderException;

    CloudProviderLocation updateCloudProviderLocation(CloudProviderLocation CP) throws CloudProviderException;

    CloudProviderLocation getCloudProviderLocationById(String cloudProviderLocationId) throws CloudProviderException;

    void deleteCloudProviderLocation(String cloudProviderLocationId) throws CloudProviderException;

    void addCloudProviderAccountToUser(String userId, String cloudProviderAccountId) throws CloudProviderException;

    void addCloudProviderAccountToUserByName(String userName, String cloudProviderAccountId) throws CloudProviderException;

    void removeCloudProviderAccountFromUser(String userId, String cloudProviderAccountId) throws CloudProviderException;

    void removeCloudProviderAccountFromUserByName(String userName, String cloudProviderAccountId) throws CloudProviderException;

    double locationDistance(CloudProviderLocation pointA, CloudProviderLocation pointB) throws CloudProviderException;

    List<CloudProviderAccount> getCloudProviderAccounts() throws CloudProviderException;

    List<CloudProviderAccount> getCloudProviderAccountsByUser(String userId) throws CloudProviderException;

    List<CloudProviderLocation> getCloudProviderLocations() throws CloudProviderException;

    List<CloudProvider> getCloudProviders() throws CloudProviderException;

}
