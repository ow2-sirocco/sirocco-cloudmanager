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
 */

package org.ow2.sirocco.cloudmanager.service.api;

import java.util.List;

import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProvider;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidCloudProviderIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;

public interface ICloudProviderManager {
    static final String EJB_JNDI_NAME = "CloudProviderManagerBean";

    CloudProvider getCloudProviderById(String id);

    /**
     * @return The list of the CloudProviders available in system's DB.
     */
    List<CloudProvider> listCloudProviders();

    /**
     * Add a CloudProvider
     * 
     * @param userName
     * @param cloudProviderType
     * @param description
     * @return the id of the just created CloudProvider.
     * @throws InvalidUsernameException
     * @throws PermissionDeniedException
     * @throws InvalidArgumentException
     */
    CloudProvider createCloudProvider(final String userName, final String cloudProviderType, final String description)
        throws InvalidUsernameException, PermissionDeniedException, InvalidArgumentException;

    /**
     * Delete the CloudProvider corresponding to cloudProviderId from system's
     * DB. The CloudProvider to be removed must NOT be associated to any
     * CloudProviderAccount otherwise the removal is rejected.
     * 
     * @param userName
     * @param cloudProviderId
     * @throws InvalidArgumentException
     * @throws MultiCloudException
     * @throws InvalidUsernameException
     * @throws PermissionDeniedException
     */
    void deleteCloudProvider(final String userName, final String cloudProviderId) throws InvalidArgumentException,
        CloudProviderException, InvalidUsernameException, PermissionDeniedException;

    List<CloudProviderLocation> listCloudProviderLocations();

    List<CloudProviderLocation> listCloudProviderLocationsByCloudProviderId(final String cloudProviderId)
        throws InvalidCloudProviderIdException;

    CloudProviderAccount getCloudProviderAccountById(String accountId);

    /**
     * @return The list of the CloudProviderAccounts available in system's DB.
     */
    List<CloudProviderAccount> listCloudProviderAccounts();

    /**
     * Add a CloudProviderAccount
     * 
     * @param projectId
     * @param userName
     * @param login
     * @param password
     * @param cloudProviderId
     * @return the id of the just created CloudProvider.
     * @throws InvalidProjectIdException
     * @throws InvalidUsernameException
     * @throws PermissionDeniedException
     * @throws InvalidArgumentException
     */
    CloudProviderAccount createCloudProviderAccount(final String projectId, final String userName, final String login,
        final String password, final String cloudProviderId) throws InvalidProjectIdException, InvalidUsernameException,
        PermissionDeniedException, InvalidArgumentException;

    /**
     * Delete the CloudProviderAccount corresponding to cloudProviderAccountId
     * from system's DB. The CloudProviderAccount to be removed must NOT be
     * associated to any other entities (except CloudProvider) otherwise the
     * removal is rejected.
     * 
     * @param projectId
     * @param userName
     * @param cloudProviderAccountId
     * @throws InvalidArgumentException
     * @throws MultiCloudException
     * @throws InvalidProjectIdException
     * @throws InvalidUsernameException
     * @throws PermissionDeniedException
     */
    void deleteCloudProviderAccount(final String projectId, final String userName, final String cloudProviderAccountId)
        throws InvalidArgumentException, CloudProviderException, InvalidProjectIdException, InvalidUsernameException,
        PermissionDeniedException;

    /**
     * "Link" CloudPRoviderAccount and Project.
     * 
     * @param projectId
     * @param userName
     * @param cloudProviderAccountId
     * @throws InvalidProjectIdException
     * @throws InvalidUsernameException
     * @throws PermissionDeniedException
     * @throws InvalidArgumentException
     */
    void associateCloudProviderAccountWithProject(final String projectId, final String userName,
        final String cloudProviderAccountId) throws InvalidProjectIdException, InvalidUsernameException,
        PermissionDeniedException, InvalidArgumentException;

    /**
     * Remove the "link" between CloudProviderAccount and Project.
     * 
     * @param projectId
     * @param userName
     * @param cloudProviderAccountId
     * @throws InvalidProjectIdException
     * @throws InvalidUsernameException
     * @throws PermissionDeniedException
     * @throws InvalidArgumentException
     */
    void dissociateCloudProviderAccountFromProject(final String projectId, final String userName,
        final String cloudProviderAccountId) throws InvalidProjectIdException, InvalidUsernameException,
        PermissionDeniedException, InvalidArgumentException;

    /**
     * @param projectId
     * @return the cloudProviderAccount associated with the given project
     *         (identified by its projectId).
     */
    List<CloudProviderAccount> listCloudProviderAccountsByProjectId(String projectId) throws InvalidProjectIdException;

    /**
     * @param cloudProviderId
     * @return the cloudProviderAccount associated with the given cloudProvider
     *         (identified by its cloudProviderId).
     */
    List<CloudProviderAccount> listCloudProviderAccountsByCloudProviderId(final String cloudProviderId)
        throws InvalidCloudProviderIdException;

}
