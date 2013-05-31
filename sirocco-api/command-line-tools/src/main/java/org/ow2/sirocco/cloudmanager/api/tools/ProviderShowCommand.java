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
import org.ow2.sirocco.cloudmanager.api.model.Provider;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "show provider")
public class ProviderShowCommand implements Command {
    private static String COMMAND_NAME = "provider-show";

    @Parameter(description = "<provider id>", required = true)
    private List<String> providerIds;

    @Override
    public String getName() {
        return ProviderShowCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        Provider provider = restClient.getRequest("providers/" + this.providerIds.get(0), Provider.class);

        Table table = new Table(2);
        table.addCell("Attribute");
        table.addCell("Value");

        table.addCell("Id");
        table.addCell(provider.getId());

        table.addCell("Name");
        table.addCell(provider.getName());

        table.addCell("API");
        table.addCell(provider.getApi());

        System.out.println(table.render());

    }

}
