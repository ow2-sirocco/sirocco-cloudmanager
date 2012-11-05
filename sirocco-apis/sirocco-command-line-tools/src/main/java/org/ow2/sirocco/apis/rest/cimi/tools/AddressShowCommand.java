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
import org.ow2.sirocco.apis.rest.cimi.sdk.Address;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "show address")
public class AddressShowCommand implements Command {
    @Parameter(names = "-id", description = "id of the address", required = true)
    private String addressId;

    @Override
    public String getName() {
        return "address-show";
    }

    @Override
    public void execute(final CimiClient cimiClient) throws CimiException {
        Address address = Address.getAddressByReference(cimiClient, this.addressId);

        Table table = new Table(2);
        table.addCell("Attribute");
        table.addCell("Value");

        table.addCell("id");
        table.addCell(address.getId());

        table.addCell("name");
        table.addCell(address.getName());
        table.addCell("description");
        table.addCell(address.getDescription());

        table.addCell("ip");
        table.addCell(address.getIp());

        table.addCell("hostname");
        table.addCell(address.getHostname());

        table.addCell("allocation");
        table.addCell(address.getAllocation());

        table.addCell("defaultGateway");
        table.addCell(address.getDefaultGateway());

        table.addCell("dns");
        StringBuffer sb = new StringBuffer();
        if (address.getDns() != null) {
            for (String dns : address.getDns()) {
                sb.append(dns + " ");
            }
        }
        table.addCell(sb.toString());

        table.addCell("protocol");
        table.addCell(address.getProtocol());

        table.addCell("mask");
        table.addCell(address.getMask());

        table.addCell("network");
        if (address.getNetwork() != null) {
            table.addCell(address.getNetwork().getId());
        } else {
            table.addCell("");
        }

        table.addCell("created");
        table.addCell(address.getCreated().toString());
        table.addCell("updated");
        if (address.getUpdated() != null) {
            table.addCell(address.getUpdated().toString());
        } else {
            table.addCell("");
        }
        table.addCell("properties");
        sb = new StringBuffer();
        if (address.getProperties() != null) {
            for (Map.Entry<String, String> prop : address.getProperties().entrySet()) {
                sb.append("(" + prop.getKey() + "," + prop.getValue() + ") ");
            }
        }
        table.addCell(sb.toString());

        System.out.println(table.render());
    }

}
