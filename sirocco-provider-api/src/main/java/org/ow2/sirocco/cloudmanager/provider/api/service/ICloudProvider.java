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

package org.ow2.sirocco.cloudmanager.provider.api.service;

import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;

public interface ICloudProvider {

    /**
     * @return the cloudProvider's Id.
     */
    String getCloudProviderId();

    /**
     * @return the cloudProviderAccount.
     */
    CloudProviderAccount getCloudProviderAccount();

    /**
     * @return the cloudProviderLocation.
     */
    CloudProviderLocation getCloudProviderLocation();

    /**
     * @return the computeService.
     * @throws MultiCloudException
     */
    IComputeService getComputeService() throws CloudProviderException;

    /**
     * @return
     * @throws MultiCloudException
     */
    IImageService getImageService() throws CloudProviderException;

    IMonitoringService getMonitoringService() throws CloudProviderException;

    IPhysicalInfrastructureManagement getPhysicalInfrastructureManagementService() throws CloudProviderException;

    IVolumeService getVolumeService() throws CloudProviderException;

    // NetworkService getNetworkService();

}
