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

package org.ow2.sirocco.cloudmanager.provider.impl;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactory;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactoryFinder;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

public class CloudProviderFactoryFinderImpl implements ICloudProviderFactoryFinder {

    private static Log logger = LogFactory.getLog(CloudProviderFactoryFinderImpl.class);

    private BundleContext context;

    public CloudProviderFactoryFinderImpl(final BundleContext context) {
        this.setContext(context);
    }

    private BundleContext getContext() {
        return this.context;
    }

    private void setContext(final BundleContext context) {
        this.context = context;
    }

    @Override
    public List<ICloudProviderFactory> listCloudProviderFactories() {
        List<ICloudProviderFactory> result = new ArrayList<ICloudProviderFactory>();
        ServiceTracker serviceTracker = new ServiceTracker(this.getContext(), ICloudProviderFactory.class.getName(), null);
        serviceTracker.open();
        Object[] services = serviceTracker.getServices();
        for (Object service : services) {
            result.add((ICloudProviderFactory) service);
        }
        return result;
    }

    @Override
    public ICloudProviderFactory getCloudProviderFactory(final String cloudProviderType) {
        if (cloudProviderType == null) {
            throw new NullPointerException();
        }
        try {
            String filterExpression = "(&(objectclass=" + ICloudProviderFactory.class.getName() + ") ("
                + ICloudProviderFactory.CLOUD_PROVIDER_TYPE_PROPERTY + "=" + cloudProviderType + "))";
            Filter filter = this.getContext().createFilter(filterExpression);
            ServiceTracker serviceTracker = new ServiceTracker(this.getContext(), filter, null);
            serviceTracker.open();
            ICloudProviderFactory result = (ICloudProviderFactory) serviceTracker.getService();
            return result;
        } catch (InvalidSyntaxException ex) {
            CloudProviderFactoryFinderImpl.logger.error("oops", ex);
            return null;
        }
    }
}
