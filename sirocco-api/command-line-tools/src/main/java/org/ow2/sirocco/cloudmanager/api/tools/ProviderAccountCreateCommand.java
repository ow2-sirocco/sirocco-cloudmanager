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

import org.ow2.sirocco.cloudmanager.api.model.ProviderAccount;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "create provider account")
public class ProviderAccountCreateCommand implements Command {
    private static String COMMAND_NAME = "provider-account-create";

    @Parameter(names = "-providerId", description = "provider id", required = true)
    private String providerId;

    @Parameter(names = "-clientId", description = "client id", required = true)
    private String clientId;

    @Parameter(names = "-clientSecret", description = "client secret", required = true)
    private String clientSecret;

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
        ProviderAccount account = new ProviderAccount();
        account.setName(this.name);
        account.setDescription(this.description);
        if (this.properties != null) {
            for (int i = 0; i < this.properties.size() / 2; i++) {
                account.addProperty(this.properties.get(i * 2), this.properties.get(i * 2 + 1));
            }
        }
        account.setClientId(this.clientId);
        account.setClientSecret(this.clientSecret);

        account = restClient.postCreateRequest("providers/" + this.providerId + "/accounts", account, ProviderAccount.class);
        System.out.println(account.getId());
    }

}
