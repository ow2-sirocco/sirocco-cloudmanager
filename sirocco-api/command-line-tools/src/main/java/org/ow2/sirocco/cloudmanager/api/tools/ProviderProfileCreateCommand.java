/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
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
 *
 */
package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.model.ProviderProfile;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "create provider profile")
public class ProviderProfileCreateCommand implements Command {
    private static String COMMAND_NAME = "provider-profile-create";

    @Parameter(names = "-type", description = "provider type", required = true)
    private String type;

    @Parameter(names = "-connectorClass", description = "java connection class name", required = true)
    private String connectorClass;

    @Override
    public String getName() {
        return ProviderProfileCreateCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        ProviderProfile providerProfile = new ProviderProfile();
        providerProfile.setType(this.type);
        providerProfile.setConnectorClass(this.connectorClass);

        providerProfile = restClient.postCreateRequest("providers/profiles", providerProfile, ProviderProfile.class);
        System.out.println(providerProfile.getId());
    }

}
