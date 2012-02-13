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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ManagedService;
import org.ow2.sirocco.cloudmanager.clustermanager.api.IClusterManager;
import org.ow2.sirocco.cloudmanager.clustermanager.impl.ClusterManagerImpl;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProvider;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactory;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobManager;
import org.ow2.sirocco.cloudmanager.provider.util.vncproxy.api.VNCProxy;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

public class VMMCloudProviderFactory implements ICloudProviderFactory {

    private static Log logger = LogFactory.getLog(VMMCloudProviderFactory.class);

    private Set<ICloudProvider> cloudProvidersInUse = new LinkedHashSet<ICloudProvider>();

    private ClusterManagerImpl clusterManager;

    private JobManager jobManager;

    private VNCProxy webSocketProxyManager;

    public VMMCloudProviderFactory(final BundleContext bundleContext) {
        VMMCloudProviderFactory.logger.info("Launching ClusterManager.");
        Dictionary<String, String> props = new Hashtable<String, String>();
        props.put(Constants.SERVICE_PID, "ClusterManager");
        this.clusterManager = new ClusterManagerImpl();
        this.clusterManager.start();
        bundleContext.registerService(new String[] {ManagedService.class.getName(), IClusterManager.class.getName()},
            this.clusterManager, props);

        ServiceReference jobManagerServiceRef = bundleContext.getServiceReference(JobManager.class.getName());
        if (jobManagerServiceRef != null) {
            this.jobManager = (JobManager) bundleContext.getService(jobManagerServiceRef);
        }

        ServiceReference webSocketProxyManagerRef = bundleContext.getServiceReference(VNCProxy.class.getName());
        if (webSocketProxyManagerRef != null) {
            this.webSocketProxyManager = (VNCProxy) bundleContext.getService(webSocketProxyManagerRef);
        }

        VMMCloudProviderFactory.logger.info("ClusterManager has been launched.");

    }

    @Override
    public ICloudProvider getCloudProviderInstance(final CloudProviderAccount cloudProviderAccount,
        final CloudProviderLocation cloudProviderLocation) {

        VMMCloudProviderFactory.logger.info("The given cloudProviderAccount: " + cloudProviderAccount + " is ignored.");
        ICloudProvider result;
        for (ICloudProvider cloudProvider : this.cloudProvidersInUse) {
            if (cloudProvider.getCloudProviderLocation().equals(cloudProviderLocation)) {
                result = cloudProvider;
                return result;
            }
        }
        result = new VMMCloudProvider(cloudProviderLocation, this.clusterManager, this.jobManager, this.webSocketProxyManager);
        this.cloudProvidersInUse.add(result);
        return result;
    }

    @Override
    public void disposeCloudProvider(final String cloudProviderId) throws CloudProviderException {
        ICloudProvider cloudProviderToBeDeleted = null;
        for (ICloudProvider cloudProvider : this.cloudProvidersInUse) {
            if (cloudProvider.getCloudProviderId().equals(cloudProviderId)) {
                cloudProviderToBeDeleted = cloudProvider;
            } else {
                VMMCloudProviderFactory.logger.trace("cloudProvider.getCloudProviderId().equals(cloudProviderId): "
                    + cloudProvider.getCloudProviderId().equals(cloudProviderId) + ".");
            }
        }
        if (cloudProviderToBeDeleted == null) {
            throw new CloudProviderException("The given cloudProviderId: " + cloudProviderId + " is unknown by the system.");
        } else {
            this.cloudProvidersInUse.remove(cloudProviderToBeDeleted);
        }
    }

    @Override
    public List<CloudProviderLocation> listCloudProviderLocations() {
        List<CloudProviderLocation> result = new ArrayList<CloudProviderLocation>();
        for (String location : this.clusterManager.getLocations()) {
            result.add(new CloudProviderLocation(location, location));
        }
        return result;
    }

}
