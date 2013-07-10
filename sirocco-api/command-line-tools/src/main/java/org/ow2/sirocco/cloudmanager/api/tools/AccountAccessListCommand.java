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

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.cloudmanager.api.model.AccountAccess;
import org.ow2.sirocco.cloudmanager.api.model.Location;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "show tenant provider accounts")
public class AccountAccessListCommand implements Command {
    private static String COMMAND_NAME = "tenant-account-list";

    @Parameter(description = "<tenant id>", required = true)
    private List<String> tenantIds;

    @Override
    public String getName() {
        return AccountAccessListCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        AccountAccess.Collection memberships = restClient.getRequest("tenants/" + this.tenantIds.get(0) + "/accounts",
            AccountAccess.Collection.class);

        Table table = new Table(4);
        table.addCell("Account Id");
        table.addCell("Provider API");
        table.addCell("Provider Name");
        table.addCell("Locations");

        for (AccountAccess membership : memberships.getAccountAccesses()) {
            table.addCell(membership.getAccountId());
            table.addCell(membership.getProviderApi());
            table.addCell(membership.getProviderName());
            StringBuilder sb = new StringBuilder();
            for (Location location : membership.getLocations()) {
                sb.append(location.getCountryName() + " ");
            }
            table.addCell(sb.toString());
        }
        System.out.println(table.render());
    }
}