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
package org.ow2.sirocco.cloudmanager.provider.util.jobmanager.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedService;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobManager;

public class Activator implements BundleActivator {
    private JobManagerImpl jobManager;

    public void start(final BundleContext context) throws Exception {
        Dictionary<String, String> props = new Hashtable<String, String>();
        props.put(Constants.SERVICE_PID, "JobManager");
        this.jobManager = JobManagerImpl.newJobManager();
        this.jobManager.start();
        context.registerService(new String[] {ManagedService.class.getName(), JobManager.class.getName()}, this.jobManager,
            props);
    }

    public void stop(final BundleContext context) throws Exception {
        this.jobManager.shutdown();
    }

}
