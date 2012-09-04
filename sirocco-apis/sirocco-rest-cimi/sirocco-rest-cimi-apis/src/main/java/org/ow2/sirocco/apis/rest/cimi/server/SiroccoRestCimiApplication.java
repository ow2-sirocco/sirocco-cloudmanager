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

import org.ow2.sirocco.apis.rest.cimi.resource.AddressRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.AddressTemplateRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.CloudEntryPointRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.CredentialRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.CredentialTemplateRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.EventLogRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.EventLogTemplateRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.EventRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.ForwardingGroupRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.JobRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.MachineConfigurationRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.MachineImageRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.MachineRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.MachineTemplateRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.NetworkConfigurationRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.NetworkPortConfigurationRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.NetworkPortRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.NetworkPortTemplateRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.NetworkRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.NetworkTemplateRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.SystemRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.SystemTemplateRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.VolumeConfigurationRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.VolumeImageRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.VolumeRestResource;
import org.ow2.sirocco.apis.rest.cimi.resource.VolumeTemplateRestResource;

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
        classes.add(AddressRestResource.class);
        classes.add(AddressTemplateRestResource.class);
        classes.add(CloudEntryPointRestResource.class);
        classes.add(CredentialRestResource.class);
        classes.add(CredentialTemplateRestResource.class);
        classes.add(EventLogRestResource.class);
        classes.add(EventLogTemplateRestResource.class);
        classes.add(EventRestResource.class);
        classes.add(ForwardingGroupRestResource.class);
        classes.add(ForwardingGroupRestResource.class);
        classes.add(JobRestResource.class);
        classes.add(MachineConfigurationRestResource.class);
        classes.add(MachineImageRestResource.class);
        classes.add(MachineRestResource.class);
        classes.add(MachineTemplateRestResource.class);
        classes.add(NetworkConfigurationRestResource.class);
        classes.add(NetworkRestResource.class);
        classes.add(NetworkTemplateRestResource.class);
        classes.add(NetworkPortConfigurationRestResource.class);
        classes.add(NetworkPortRestResource.class);
        classes.add(NetworkPortTemplateRestResource.class);
        classes.add(SystemRestResource.class);
        classes.add(SystemTemplateRestResource.class);
        classes.add(VolumeRestResource.class);
        classes.add(VolumeConfigurationRestResource.class);
        classes.add(VolumeImageRestResource.class);
        classes.add(VolumeTemplateRestResource.class);

        return classes;
    }
}
