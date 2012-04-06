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

import org.ow2.sirocco.apis.rest.cimi.resource.CimiCloudEntryPointResource;
import org.ow2.sirocco.apis.rest.cimi.resource.CimiCredentialsResource;
import org.ow2.sirocco.apis.rest.cimi.resource.CimiCredentialsTemplateResource;
import org.ow2.sirocco.apis.rest.cimi.resource.CimiJobResource;
import org.ow2.sirocco.apis.rest.cimi.resource.CimiMachineConfigurationResource;
import org.ow2.sirocco.apis.rest.cimi.resource.CimiMachineImageResource;
import org.ow2.sirocco.apis.rest.cimi.resource.CimiMachineResource;
import org.ow2.sirocco.apis.rest.cimi.resource.CimiMachineTemplateResource;

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
        classes.add(CimiCloudEntryPointResource.class);
        classes.add(CimiCredentialsResource.class);
        classes.add(CimiCredentialsTemplateResource.class);
        classes.add(CimiJobResource.class);
        classes.add(CimiMachineResource.class);
        classes.add(CimiMachineConfigurationResource.class);
        classes.add(CimiMachineImageResource.class);
        classes.add(CimiMachineTemplateResource.class);

        return classes;
    }
}
