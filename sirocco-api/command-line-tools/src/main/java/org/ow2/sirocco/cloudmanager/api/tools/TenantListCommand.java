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
import org.ow2.sirocco.cloudmanager.api.model.MultiCloudTenant;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "list tenants")
public class TenantListCommand implements Command {
    private static String COMMAND_NAME = "tenant-list";

    @Override
    public String getName() {
        return TenantListCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        MultiCloudTenant.Collection tenants = restClient.getRequest("tenants", MultiCloudTenant.Collection.class);

        Table table = new Table(3);
        table.addCell("Id");
        table.addCell("Name");
        table.addCell("Description");

        for (MultiCloudTenant tenant : tenants.getTenants()) {
            table.addCell(tenant.getId());
            table.addCell(tenant.getName());
            table.addCell(tenant.getDescription());
        }
        System.out.println(table.render());

    }

}
