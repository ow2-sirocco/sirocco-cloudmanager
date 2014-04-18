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

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.cloudmanager.api.model.Quota;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "show provider account quota")
public class ProviderAccountQuotaShowCommand implements Command {
    private static String COMMAND_NAME = "provider-account-quota-show";

    @Parameter(description = "<provider account id>", required = true)
    private List<String> accountIds;

    @Override
    public String getName() {
        return ProviderAccountQuotaShowCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        Quota quota = restClient.getRequest("providers/accounts/" + this.accountIds.get(0) + "/quota", Quota.class);

        Table table = new Table(3);
        table.addCell("Resource");
        table.addCell("Limit");
        table.addCell("Used");

        for (Quota.Resource resource : quota.getResources()) {
            String unit = resource.getUnit();
            if (unit.equalsIgnoreCase("count")) {
                unit = "";
            }
            table.addCell(resource.getType());
            table.addCell(resource.getLimit() + unit);
            table.addCell(resource.getUsed() + unit);
        }

        System.out.println(table.render());
    }
}
