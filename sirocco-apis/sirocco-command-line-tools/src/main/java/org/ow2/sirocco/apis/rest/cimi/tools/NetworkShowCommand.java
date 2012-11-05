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

import java.util.Map;

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiException;
import org.ow2.sirocco.apis.rest.cimi.sdk.Network;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "show network")
public class NetworkShowCommand implements Command {
    @Parameter(names = "-id", description = "id of the network", required = true)
    private String networkId;

    @Override
    public String getName() {
        return "network-show";
    }

    @Override
    public void execute(final CimiClient cimiClient) throws CimiException {
        Network net = Network.getNetworkByReference(cimiClient, this.networkId);

        Table table = new Table(2);
        table.addCell("Attribute");
        table.addCell("Value");

        table.addCell("id");
        table.addCell(net.getId());

        table.addCell("name");
        table.addCell(net.getName());
        table.addCell("description");
        table.addCell(net.getDescription());

        table.addCell("state");
        table.addCell(net.getState().toString());

        table.addCell("type");
        table.addCell(net.getNetworkType());

        table.addCell("created");
        table.addCell(net.getCreated().toString());
        table.addCell("updated");
        if (net.getUpdated() != null) {
            table.addCell(net.getUpdated().toString());
        } else {
            table.addCell("");
        }
        table.addCell("properties");
        StringBuffer sb = new StringBuffer();
        if (net.getProperties() != null) {
            for (Map.Entry<String, String> prop : net.getProperties().entrySet()) {
                sb.append("(" + prop.getKey() + "," + prop.getValue() + ") ");
            }
        }
        table.addCell(sb.toString());

        System.out.println(table.render());
    }

}
