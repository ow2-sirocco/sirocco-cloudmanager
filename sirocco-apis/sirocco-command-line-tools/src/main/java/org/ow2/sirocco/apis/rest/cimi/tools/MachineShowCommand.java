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
import org.ow2.sirocco.apis.rest.cimi.sdk.Machine;
import org.ow2.sirocco.apis.rest.cimi.sdk.NetworkInterface;
import org.ow2.sirocco.apis.rest.cimi.sdk.QueryParams;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "show machine")
public class MachineShowCommand implements Command {
    @Parameter(names = "-id", description = "id of the machine", required = true)
    private String machineId;

    @Parameter(names = "-expand", description = "machine properties to expand", required = false)
    private String expand;

    @Override
    public String getName() {
        return "machine-show";
    }

    @Override
    public void execute(final CimiClient cimiClient) throws CimiException {
        Machine machine = Machine.getMachineByReference(cimiClient, this.machineId, QueryParams.build().setExpand(this.expand));
        MachineShowCommand.printMachine(machine);
    }

    public static void printMachine(final Machine machine) throws CimiException {
        Table table = new Table(2);
        table.addCell("Attribute");
        table.addCell("Value");

        table.addCell("id");
        table.addCell(machine.getId());

        table.addCell("name");
        table.addCell(machine.getName());
        table.addCell("description");
        table.addCell(machine.getDescription());
        table.addCell("status");
        table.addCell(machine.getState().toString());
        table.addCell("created");
        table.addCell(machine.getCreated().toString());
        table.addCell("updated");
        if (machine.getUpdated() != null) {
            table.addCell(machine.getUpdated().toString());
        } else {
            table.addCell("");
        }
        table.addCell("properties");
        StringBuffer sb = new StringBuffer();
        if (machine.getProperties() != null) {
            for (Map.Entry<String, String> prop : machine.getProperties().entrySet()) {
                sb.append("(" + prop.getKey() + "," + prop.getValue() + ") ");
            }
        }
        table.addCell(sb.toString());

        table.addCell("cpu");
        table.addCell(Integer.toString(machine.getCpu()));

        table.addCell("memory");
        table.addCell(Integer.toString(machine.getMemory()));

        table.addCell("IP addresses");
        sb = new StringBuffer();
        for (NetworkInterface nic : machine.getNetworkInterface()) {
            sb.append(nic.getType() + "=" + nic.getIp() + " ");
        }
        table.addCell(sb.toString());

        System.out.println(table.render());
    }

}
