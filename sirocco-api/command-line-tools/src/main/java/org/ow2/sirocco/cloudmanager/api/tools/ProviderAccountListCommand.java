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

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.cloudmanager.api.model.ProviderAccount;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "list provider account")
public class ProviderAccountListCommand implements Command {
    private static String COMMAND_NAME = "provider-account-list";

    @Parameter(names = "-providerId", description = "provider id", required = true)
    private String providerId;

    @Override
    public String getName() {
        return ProviderAccountListCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        ProviderAccount.Collection accounts = restClient.getRequest("providers/" + this.providerId + "/accounts",
            ProviderAccount.Collection.class);

        Table table = new Table(3);
        table.addCell("Id");
        table.addCell("ClientId");
        table.addCell("ClientSecret");

        for (ProviderAccount account : accounts.getProviderAccounts()) {
            table.addCell(account.getId());
            table.addCell(account.getClientId());
            table.addCell(account.getClientSecret());
        }
        System.out.println(table.render());

    }

}
