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

package org.ow2.sirocco.cloudmanager.provider.nova;

import java.util.UUID;

import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProvider;
import org.ow2.sirocco.cloudmanager.provider.api.service.IComputeService;
import org.ow2.sirocco.cloudmanager.provider.api.service.IImageService;
import org.ow2.sirocco.cloudmanager.provider.api.service.IMonitoringService;
import org.ow2.sirocco.cloudmanager.provider.api.service.IPhysicalInfrastructureManagement;
import org.ow2.sirocco.cloudmanager.provider.api.service.IVolumeService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

public class NovaCloudProvider implements ICloudProvider {

    private static Log logger = LogFactory.getLog(NovaCloudProvider.class);

    private final String cloudProviderId;

    private final CloudProviderAccount cloudProviderAccount;

    private final CloudProviderLocation cloudProviderLocation;

    private IComputeService computeService;

    private final NovaCloudProviderFactory novaCloudProviderFactory;

    public NovaCloudProvider(final NovaCloudProviderFactory novaCloudProviderFactory,
        final CloudProviderAccount cloudProviderAccount, final CloudProviderLocation cloudProviderLocation) {
        this.novaCloudProviderFactory = novaCloudProviderFactory;
        this.cloudProviderId = UUID.randomUUID().toString();
        this.cloudProviderAccount = cloudProviderAccount;
        this.cloudProviderLocation = cloudProviderLocation;
    }

    @Override
    public String getCloudProviderId() {
        return this.cloudProviderId;
    }

    @Override
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    @Override
    public CloudProviderLocation getCloudProviderLocation() {
        return this.cloudProviderLocation;
    }

    @Override
    public IComputeService getComputeService() throws CloudProviderException {
        if (this.computeService == null) {
            this.computeService = new NovaComputeService(this.novaCloudProviderFactory, this.cloudProviderAccount,
                this.cloudProviderLocation);
        }
        return this.computeService;
    }

    @Override
    public IImageService getImageService() throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IMonitoringService getMonitoringService() throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IPhysicalInfrastructureManagement getPhysicalInfrastructureManagementService() throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IVolumeService getVolumeService() throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

}
