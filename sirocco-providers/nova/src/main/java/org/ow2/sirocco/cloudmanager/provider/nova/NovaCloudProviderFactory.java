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

import java.util.Collections;
import java.util.Dictionary;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
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

public class NovaCloudProviderFactory implements ICloudProviderFactory, ManagedService {
    private static String NOVA_ENDPOINT_PROPERTY_NAME = "nova.endpoint";

    private static String NOVA_LOCATION_ID_PROPERTY_NAME = "nova.location.id";

    private static String NOVA_LOCATION_DESCRIPTION_PROPERTY_NAME = "nova.location.description";

    private static final int THREADPOOL_SIZE = 10;

    private static Log logger = LogFactory.getLog(NovaCloudProviderFactory.class);

    private Set<ICloudProvider> cloudProvidersInUse = new LinkedHashSet<ICloudProvider>();

    private String novaEndpoint;

    private CloudProviderLocation novaLocation;

    private JobManager jobManager;

    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors
        .newFixedThreadPool(NovaCloudProviderFactory.THREADPOOL_SIZE));

    public NovaCloudProviderFactory(final BundleContext context) {
        ServiceReference jobManagerServiceRef = context.getServiceReference(JobManager.class.getName());
        if (jobManagerServiceRef != null) {
            this.jobManager = (JobManager) context.getService(jobManagerServiceRef);
        }
    }

    public NovaCloudProviderFactory(final JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public void updated(final Dictionary properties) throws ConfigurationException {
        if (properties != null) {
            this.novaEndpoint = (String) properties.get(NovaCloudProviderFactory.NOVA_ENDPOINT_PROPERTY_NAME);
            String location = (String) properties.get(NovaCloudProviderFactory.NOVA_LOCATION_ID_PROPERTY_NAME);
            if (location != null) {
                String locationDescription = (String) properties
                    .get(NovaCloudProviderFactory.NOVA_LOCATION_DESCRIPTION_PROPERTY_NAME);
                this.novaLocation = new CloudProviderLocation(location, locationDescription != null ? locationDescription
                    : location);
            }
        }
        if (this.novaEndpoint == null || this.novaLocation == null) {
            NovaCloudProviderFactory.logger.fatal("Bad configuration: no endpoint or location provided");
        } else {
            NovaCloudProviderFactory.logger.info("Ready endpoint=" + this.novaEndpoint + " location=" + this.novaLocation);
        }
    }

    @Override
    public synchronized ICloudProvider getCloudProviderInstance(final CloudProviderAccount cloudProviderAccount,
        final CloudProviderLocation cloudProviderLocation) {
        if (this.novaEndpoint == null || this.novaLocation == null) {
            return null;
        }
        ICloudProvider result;
        for (ICloudProvider cloudProvider : this.cloudProvidersInUse) {
            if (cloudProvider.getCloudProviderAccount().getLogin().equals(cloudProviderAccount.getLogin())) {
                if (cloudProvider.getCloudProviderLocation().equals(cloudProviderLocation)) {
                    result = cloudProvider;
                    return result;
                }
            }
        }
        result = new NovaCloudProvider(this, cloudProviderAccount, cloudProviderLocation);
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

    public void setNovaEndpoint(final String novaEndpoint) {
        this.novaEndpoint = novaEndpoint;
    }

    public String getNovaEndpoint() {
        return this.novaEndpoint;
    }

    public JobManager getJobManager() {
        return this.jobManager;
    }

    ListeningExecutorService getExecutorService() {
        return this.executorService;
    }

    @Override
    public List<CloudProviderLocation> listCloudProviderLocations() {
        if (this.novaLocation != null) {
            return Collections.singletonList(this.novaLocation);
        } else {
            return Collections.emptyList();
        }
    }
}
