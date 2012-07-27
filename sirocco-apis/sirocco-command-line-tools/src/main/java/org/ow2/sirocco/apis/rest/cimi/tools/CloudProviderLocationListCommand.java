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

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "list cloud provider locations")
public class CloudProviderLocationListCommand implements Command {
    public static String COMMAND_NAME = "cloud-provider-location-list";

    @Override
    public String getName() {
        return CloudProviderLocationListCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final CimiClient cimiClient) throws Exception {
        Context context = new InitialContext();
        IRemoteCloudProviderManager cloudProviderManager = (IRemoteCloudProviderManager) context
            .lookup(ICloudProviderManager.EJB_JNDI_NAME);

        List<CloudProviderLocation> locations = cloudProviderManager.getCloudProviderLocations();

        Table table = new Table(5);
        table.addCell("Cloud Provider Location ID");
        table.addCell("iso3166_1");
        table.addCell("iso3166_2");
        table.addCell("Country");
        table.addCell("Region");

        for (CloudProviderLocation location : locations) {
            table.addCell(location.getId().toString());
            table.addCell(location.getIso3166_1());
            table.addCell(location.getIso3166_2());
            table.addCell(location.getCountryName());
            table.addCell(location.getStateName());
        }

        System.out.println(table.render());
    }
}