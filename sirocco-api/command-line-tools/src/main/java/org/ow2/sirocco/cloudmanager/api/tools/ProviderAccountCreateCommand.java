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
 */
package org.ow2.sirocco.cloudmanager.api.tools;

import java.util.List;

import org.ow2.sirocco.cloudmanager.api.model.Location;
import org.ow2.sirocco.cloudmanager.api.model.ProviderAccount;
import org.ow2.sirocco.cloudmanager.api.model.ProviderAccountCreate;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "create provider account")
public class ProviderAccountCreateCommand implements Command {
    private static String COMMAND_NAME = "provider-account-create";

    @Parameter(names = "-endpoint", description = "provider endpoint", required = true)
    private String endpoint;

    @Parameter(names = "-type", description = "provider type", required = true)
    private String type;

    @Parameter(names = "-identity", description = "identity", required = true)
    private String clientId;

    @Parameter(names = "-credential", description = "credential", required = true)
    private String clientSecret;

    @Parameter(names = "-iso3166_1", description = "iso3166-1 code", required = true)
    private String iso3166_1;

    @Parameter(names = "-country", description = "country", required = false)
    private String countryName;

    @Parameter(names = "-name", description = "name of provider account", required = false)
    private String name;

    @Parameter(names = "-description", description = "description of the provider account", required = false)
    private String description;

    @Parameter(names = "-properties", variableArity = true, description = "key value pairs", required = false)
    private List<String> properties;

    @Override
    public String getName() {
        return ProviderAccountCreateCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        ProviderAccountCreate account = new ProviderAccountCreate();
        account.setName(this.name);
        account.setDescription(this.description);
        if (this.properties != null) {
            for (int i = 0; i < this.properties.size() / 2; i++) {
                account.addProperty(this.properties.get(i * 2), this.properties.get(i * 2 + 1));
            }
        }
        account.setIdentity(this.clientId);
        account.setCredential(this.clientSecret);
        account.setEndpoint(this.endpoint);
        account.setType(this.type);

        Location location = new Location();
        location.setIso3166_1(this.iso3166_1);
        location.setCountryName(this.countryName);
        account.setLocation(location);

        ProviderAccount newAccount = restClient.postCreateRequest("providers/accounts", account, ProviderAccount.class);
        System.out.println(newAccount.getId());
    }

}
