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
import org.ow2.sirocco.cloudmanager.api.model.Location;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "list provider location")
public class ProviderLocationListCommand implements Command {
    private static String COMMAND_NAME = "provider-location-list";

    @Parameter(description = "<provider id>", required = true)
    private List<String> providerIds;

    @Override
    public String getName() {
        return ProviderLocationListCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        Location.Collection providers = restClient.getRequest("providers/" + this.providerIds.get(0) + "/locations",
            Location.Collection.class);

        Table table = new Table(3);
        table.addCell("Country");
        table.addCell("Region");
        table.addCell("City");

        for (Location location : providers.getLocations()) {
            if (location.getCountryName() != null) {
                table.addCell(location.getCountryName());
            } else if (location.getIso3166_1() != null) {
                table.addCell(location.getIso3166_1());
            } else {
                table.addCell("");
            }
            if (location.getRegionName() != null) {
                table.addCell(location.getRegionName());
            } else if (location.getIso3166_2() != null) {
                table.addCell(location.getIso3166_2());
            } else {
                table.addCell("");
            }
            if (location.getCityName() != null) {
                table.addCell(location.getCityName());
            } else {
                table.addCell("");
            }
        }
        System.out.println(table.render());

    }

}