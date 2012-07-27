/**
 *
 * SIROCCO
 * Copyright (C) 2011 France Telecom
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
 *  $Id$
 *
 */

package org.ow2.sirocco.apis.rest.cimi.tools;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "create cloud provider location")
public class CloudProviderLocationCreateCommand implements Command {
    public static String COMMAND_NAME = "cloud-provider-location-create";

    @Parameter(names = "-iso3166-1", description = "country code", required = true)
    private String iso3166_1;

    @Parameter(names = "-iso3166-2", description = "region code", required = false)
    private String iso3166_2;

    @Parameter(names = "-country", description = "country name", required = false)
    private String countryName;

    @Parameter(names = "-region", description = "region name", required = false)
    private String stateName;

    @Override
    public String getName() {
        return CloudProviderLocationCreateCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final CimiClient cimiClient) throws Exception {
        Context context = new InitialContext();
        IRemoteCloudProviderManager cloudProviderManager = (IRemoteCloudProviderManager) context
            .lookup(ICloudProviderManager.EJB_JNDI_NAME);

        CloudProviderLocation location = new CloudProviderLocation();
        location.setIso3166_1(this.iso3166_1);
        location.setIso3166_2(this.iso3166_2);
        location.setCountryName(this.countryName);
        location.setStateName(this.stateName);

        location = cloudProviderManager.createCloudProviderLocation(location);

        Table table = new Table(5);
        table.addCell("Cloud Provider Location ID");
        table.addCell("iso3166_1");
        table.addCell("iso3166_2");
        table.addCell("Country");
        table.addCell("Region");

        table.addCell(location.getId().toString());
        table.addCell(location.getIso3166_1());
        table.addCell(location.getIso3166_2());
        table.addCell(location.getCountryName());
        table.addCell(location.getStateName());

        System.out.println(table.render());
    }
}