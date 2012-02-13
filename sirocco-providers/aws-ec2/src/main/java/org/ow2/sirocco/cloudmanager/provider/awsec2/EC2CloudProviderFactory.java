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

package org.ow2.sirocco.cloudmanager.provider.awsec2;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProvider;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactory;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobManager;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class EC2CloudProviderFactory implements ICloudProviderFactory {

    private static Log logger = LogFactory.getLog(EC2CloudProviderFactory.class);

    private static final int THREADPOOL_SIZE = 10;

    private JobManager jobManager;

    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors
        .newFixedThreadPool(EC2CloudProviderFactory.THREADPOOL_SIZE));

    // TODO add new CloudProviderLocation("us-west-2", "US West (Oregon)") as
    // soon as jclouds supports it
    private static List<CloudProviderLocation> locations = Arrays.asList(new CloudProviderLocation("us-east-1",
        "US East (Virginia)"), new CloudProviderLocation("us-west-1", "US West (N. California)"), new CloudProviderLocation(
        "eu-west-1", "EU West (Ireland)"), new CloudProviderLocation("ap-southeast-1", "Asia Pacific (Singapore)"),
        new CloudProviderLocation("ap-northeast-1", "Asia Pacific (Tokyo)"));

    private Set<ICloudProvider> cloudProvidersInUse = new LinkedHashSet<ICloudProvider>();

    public EC2CloudProviderFactory(final BundleContext context) {
        ServiceReference jobManagerServiceRef = context.getServiceReference(JobManager.class.getName());
        if (jobManagerServiceRef != null) {
            this.jobManager = (JobManager) context.getService(jobManagerServiceRef);
        }
    }

    public EC2CloudProviderFactory(final JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public synchronized ICloudProvider getCloudProviderInstance(final CloudProviderAccount cloudProviderAccount,
        final CloudProviderLocation cloudProviderLocation) {
        ICloudProvider result;
        for (ICloudProvider cloudProvider : this.cloudProvidersInUse) {
            if (cloudProvider.getCloudProviderAccount().getLogin().equals(cloudProviderAccount.getLogin())) {
                if (cloudProvider.getCloudProviderLocation().equals(cloudProviderLocation)) {
                    return cloudProvider;
                }
            }
        }
        result = new EC2CloudProvider(this, cloudProviderAccount, cloudProviderLocation);
        this.cloudProvidersInUse.add(result);
        return result;
    }

    @Override
    public synchronized void disposeCloudProvider(final String cloudProviderId) throws CloudProviderException {
        ICloudProvider cloudProviderToBeDeleted = null;
        for (ICloudProvider cloudProvider : this.cloudProvidersInUse) {
            if (cloudProvider.getCloudProviderId().equals(cloudProviderId)) {
                cloudProviderToBeDeleted = cloudProvider;
                break;
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
        return EC2CloudProviderFactory.locations;
    }

    JobManager getJobManager() {
        return this.jobManager;
    }

    ListeningExecutorService getExecutorService() {
        return this.executorService;
    }

}
