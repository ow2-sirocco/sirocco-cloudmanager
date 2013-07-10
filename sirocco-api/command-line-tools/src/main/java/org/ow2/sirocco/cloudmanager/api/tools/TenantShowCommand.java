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
import org.ow2.sirocco.cloudmanager.api.model.MultiCloudTenant;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "show tenant")
public class TenantShowCommand implements Command {
    private static String COMMAND_NAME = "tenant-show";

    @Parameter(description = "<tenant id>", required = true)
    private List<String> tenantIds;

    @Override
    public String getName() {
        return TenantShowCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        MultiCloudTenant tenant = restClient.getRequest("tenants/" + this.tenantIds.get(0), MultiCloudTenant.class);

        Table table = new Table(2);
        table.addCell("Attribute");
        table.addCell("Value");

        table.addCell("Id");
        table.addCell(tenant.getId());

        table.addCell("Name");
        table.addCell(tenant.getName());

        table.addCell("Description");
        table.addCell(tenant.getDescription());

        System.out.println(table.render());

    }

}
