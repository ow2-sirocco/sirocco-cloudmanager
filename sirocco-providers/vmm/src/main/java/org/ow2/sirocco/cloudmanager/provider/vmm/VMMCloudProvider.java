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
 * $Id$
 *  
 */

package org.ow2.sirocco.cloudmanager.provider.vmm;

import java.util.UUID;

import org.ow2.sirocco.cloudmanager.clustermanager.api.IClusterManager;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProvider;
import org.ow2.sirocco.cloudmanager.provider.api.service.IComputeService;
import org.ow2.sirocco.cloudmanager.provider.api.service.IImageService;
import org.ow2.sirocco.cloudmanager.provider.api.service.IMonitoringService;
import org.ow2.sirocco.cloudmanager.provider.api.service.IPhysicalInfrastructureManagement;
import org.ow2.sirocco.cloudmanager.provider.api.service.IVolumeService;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobManager;
import org.ow2.sirocco.cloudmanager.provider.util.vncproxy.api.VNCProxy;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

public class VMMCloudProvider implements ICloudProvider {

    private static Log logger = LogFactory.getLog(VMMCloudProvider.class);

    private final String cloudProviderId;

    private final CloudProviderLocation cloudProviderLocation;

    private VMMComputeService vmmService;

    private final IClusterManager clusterManager;

    private final JobManager jobManager;

    private final VNCProxy webSocketProxyManager;

    public VMMCloudProvider(final CloudProviderLocation cloudProviderLocation, final IClusterManager clusterManager,
        final JobManager jobManager, final VNCProxy webSocketProxyManager) {
        this.cloudProviderId = UUID.randomUUID().toString();
        this.cloudProviderLocation = cloudProviderLocation;
        this.clusterManager = clusterManager;
        this.jobManager = jobManager;
        this.webSocketProxyManager = webSocketProxyManager;
    }

    @Override
    public String getCloudProviderId() {
        return this.cloudProviderId;
    }

    @Override
    public CloudProviderAccount getCloudProviderAccount() {
        VMMCloudProvider.logger.trace("CloudProviderAccount is ignored.");
        return null;
    }

    @Override
    public CloudProviderLocation getCloudProviderLocation() {
        return this.cloudProviderLocation;
    }

    public IClusterManager getClusterManager() {
        return this.clusterManager;
    }

    private synchronized VMMComputeService getVMMService() throws CloudProviderException {
        if (this.vmmService == null) {
            this.vmmService = new VMMComputeService(this.cloudProviderLocation, this.clusterManager, this.jobManager,
                this.webSocketProxyManager);
        }
        return this.vmmService;
    }

    @Override
    public IComputeService getComputeService() throws CloudProviderException {
        return this.getVMMService();
    }

    @Override
    public IImageService getImageService() throws CloudProviderException {
        return this.getVMMService();
    }

    @Override
    public IVolumeService getVolumeService() throws CloudProviderException {
        return this.getVMMService();
    }

    @Override
    public IMonitoringService getMonitoringService() throws CloudProviderException {
        return this.getVMMService();
    }

    @Override
    public IPhysicalInfrastructureManagement getPhysicalInfrastructureManagementService() throws CloudProviderException {
        return this.getVMMService();
    }
}
