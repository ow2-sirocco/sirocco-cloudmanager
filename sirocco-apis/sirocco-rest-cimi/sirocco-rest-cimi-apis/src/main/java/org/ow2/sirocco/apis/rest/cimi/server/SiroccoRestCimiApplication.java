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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.server;

/**
 *

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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.ow2.sirocco.apis.rest.cimi.resource.CloudEntryPointRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.CredentialsRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.CredentialsTemplateRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.JobRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.MachineConfigurationRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.MachineImageRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.MachineRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.MachineTemplateRestResource;

/**
 * Define the REST Application and the REST resources.
 */
public class SiroccoRestCimiApplication extends Application {

    /**
     * {@inheritDoc}
     * 
     * @see javax.ws.rs.core.Application#getClasses()
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();

        // Configuration
        classes.add(JacksonConfigurator.class);

        // Resources
        classes.add(CloudEntryPointRestResource.class);
        classes.add(CredentialsRestResource.class);
        classes.add(CredentialsTemplateRestResource.class);
        classes.add(JobRestResource.class);
        classes.add(MachineRestResource.class);
        classes.add(MachineConfigurationRestResource.class);
        classes.add(MachineImageRestResource.class);
        classes.add(MachineTemplateRestResource.class);

        return classes;
    }
}
