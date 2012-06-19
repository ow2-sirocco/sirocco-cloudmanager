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

package org.ow2.sirocco.cloudmanager.connector.mock;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactory;
import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.api.IJobManager;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class MockCloudProviderConnectorFactory implements ICloudProviderConnectorFactory {

    private static Log logger = LogFactory.getLog(MockCloudProviderConnectorFactory.class);

    private static final int THREADPOOL_SIZE = 10;

    private IJobManager jobManager;

    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors
        .newFixedThreadPool(MockCloudProviderConnectorFactory.THREADPOOL_SIZE));

    private Set<ICloudProviderConnector> cloudProvidersInUse = new LinkedHashSet<ICloudProviderConnector>();

    public MockCloudProviderConnectorFactory(final BundleContext context) {
        ServiceReference jobManagerServiceRef = context.getServiceReference(IJobManager.class.getName());
        if (jobManagerServiceRef != null) {
            this.jobManager = (IJobManager) context.getService(jobManagerServiceRef);
        }
    }

    public MockCloudProviderConnectorFactory(final IJobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public synchronized ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount cloudProviderAccount,
        final CloudProviderLocation cloudProviderLocation) {
        ICloudProviderConnector result;
        for (ICloudProviderConnector cloudProvider : this.cloudProvidersInUse) {
            if (cloudProvider.getCloudProviderAccount().getLogin().equals(cloudProviderAccount.getLogin())) {
                if (cloudProviderLocation == null || cloudProvider.getCloudProviderLocation().equals(cloudProviderLocation)) {
                    return cloudProvider;
                }
            }
        }
        MockCloudProviderConnectorFactory.logger.info("Adding new connector account.login=" + cloudProviderAccount.getLogin()
            + " location=" + cloudProviderLocation);
        result = new MockCloudProviderConnector(this, cloudProviderAccount, cloudProviderLocation);
        this.cloudProvidersInUse.add(result);
        return result;
    }

    @Override
    public synchronized void disposeCloudProviderConnector(final String cloudProviderId) throws ConnectorException {
        ICloudProviderConnector cloudProviderToBeDeleted = null;
        for (ICloudProviderConnector cloudProvider : this.cloudProvidersInUse) {
            if (cloudProvider.getCloudProviderId().equals(cloudProviderId)) {
                cloudProviderToBeDeleted = cloudProvider;
                break;
            }
        }
        if (cloudProviderToBeDeleted == null) {
            throw new ConnectorException("The given cloudProviderId: " + cloudProviderId + " is unknown by the system.");
        } else {
            MockCloudProviderConnectorFactory.logger.info("Disposing connector account.login="
                + cloudProviderToBeDeleted.getCloudProviderAccount().getLogin() + " location="
                + cloudProviderToBeDeleted.getCloudProviderLocation());
            // not done for stateful mock connectors
            // this.cloudProvidersInUse.remove(cloudProviderToBeDeleted);
        }
    }

    @Override
    public Set<CloudProviderLocation> listCloudProviderLocations() {
        CloudProviderLocation mockLocation = new CloudProviderLocation();
        mockLocation.setCountryName("France");
        return Collections.singleton(mockLocation);
    }

    IJobManager getJobManager() {
        return this.jobManager;
    }

    ListeningExecutorService getExecutorService() {
        return this.executorService;
    }

}
