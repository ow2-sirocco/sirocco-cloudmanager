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

import java.util.List;

import org.ow2.sirocco.cloudmanager.api.model.Provider;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "create provider")
public class ProviderCreateCommand implements Command {
    private static String COMMAND_NAME = "provider-create";

    @Parameter(names = "-endpoint", description = "provider endpoint", required = true)
    private String endpoint;

    @Parameter(names = "-api", description = "provider api", required = true)
    private String api;

    @Parameter(names = "-name", description = "name of provider", required = true)
    private String name;

    @Parameter(names = "-description", description = "description of the provider", required = false)
    private String description;

    @Parameter(names = "-properties", variableArity = true, description = "key value pairs", required = false)
    private List<String> properties;

    @Override
    public String getName() {
        return ProviderCreateCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        Provider provider = new Provider();
        provider.setName(this.name);
        provider.setEndpoint(this.endpoint);
        provider.setDescription(this.description);
        if (this.properties != null) {
            for (int i = 0; i < this.properties.size() / 2; i++) {
                provider.addProperty(this.properties.get(i * 2), this.properties.get(i * 2 + 1));
            }
        }
        provider.setApi(this.api);

        provider = restClient.postCreateRequest("providers", provider, Provider.class);
        System.out.println(provider.getId());
    }

}
