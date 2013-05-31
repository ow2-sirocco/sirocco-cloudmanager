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

import org.ow2.sirocco.cloudmanager.api.model.Location;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "add location to provider")
public class AddLocationToProviderCommand implements Command {
    private static String COMMAND_NAME = "provider-location-add";

    @Parameter(names = "-providerId", description = "provider id", required = true)
    private String providerId;

    @Parameter(names = "-iso3166_1", description = "iso3166-1 code", required = true)
    private String iso3166_1;

    @Parameter(names = "-iso3166_2", description = "iso3166-2 code", required = false)
    private String iso3166_2;

    @Parameter(names = "-country", description = "country", required = false)
    private String countryName;

    @Parameter(names = "-region", description = "region", required = false)
    private String regionName;

    @Parameter(names = "-city", description = "city", required = false)
    private String cityName;

    @Override
    public String getName() {
        return AddLocationToProviderCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        Location location = new Location();
        location.setIso3166_1(this.iso3166_1);
        location.setIso3166_2(this.iso3166_2);
        location.setCountryName(this.countryName);
        location.setRegionName(this.regionName);
        location.setCityName(this.cityName);

        location = restClient.postCreateRequest("providers/" + this.providerId + "/locations", location, Location.class);
    }

}