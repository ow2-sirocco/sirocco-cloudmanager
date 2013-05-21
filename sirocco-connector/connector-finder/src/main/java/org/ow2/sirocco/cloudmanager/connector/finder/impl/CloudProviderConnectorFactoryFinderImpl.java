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

package org.ow2.sirocco.cloudmanager.connector.finder.impl;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudProviderConnectorFactoryFinderImpl implements ICloudProviderConnectorFinder {

    private static Logger logger = LoggerFactory.getLogger(CloudProviderConnectorFactoryFinderImpl.class);

    private BundleContext context;

    public CloudProviderConnectorFactoryFinderImpl(final BundleContext context) {
        this.setContext(context);
    }

    private BundleContext getContext() {
        return this.context;
    }

    private void setContext(final BundleContext context) {
        this.context = context;
    }

    @Override
    public List<ICloudProviderConnector> listCloudProviderConnectors() {
        List<ICloudProviderConnector> result = new ArrayList<ICloudProviderConnector>();
        ServiceTracker serviceTracker = new ServiceTracker(this.getContext(), ICloudProviderConnector.class.getName(), null);
        serviceTracker.open();
        Object[] services = serviceTracker.getServices();
        for (Object service : services) {
            result.add((ICloudProviderConnector) service);
        }
        return result;
    }

    @Override
    public ICloudProviderConnector getCloudProviderConnector(final String cloudProviderType) {
        if (cloudProviderType == null) {
            throw new NullPointerException();
        }
        try {
            String filterExpression = "(&(objectclass=" + ICloudProviderConnector.class.getName() + ") ("
                + ICloudProviderConnector.CLOUD_PROVIDER_TYPE_PROPERTY + "=" + cloudProviderType + "))";
            Filter filter = this.getContext().createFilter(filterExpression);
            ServiceTracker serviceTracker = new ServiceTracker(this.getContext(), filter, null);
            serviceTracker.open();
            ICloudProviderConnector result = (ICloudProviderConnector) serviceTracker.getService();
            return result;
        } catch (InvalidSyntaxException ex) {
            CloudProviderConnectorFactoryFinderImpl.logger.error("oops", ex);
            return null;
        }
    }
}
