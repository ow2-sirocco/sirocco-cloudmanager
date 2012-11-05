/**
 *
 * SIROCCO
 * Copyright (C) 2012 France Telecom
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
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineVolume;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "show MachineVolume")
public class MachineVolumeShowCommand implements Command {
    @Parameter(names = "-id", description = "id of the MachineVolume", required = true)
    private String machineVolumeId;

    @Override
    public String getName() {
        return "machinevolume-show";
    }

    @Override
    public void execute(final CimiClient cimiClient) throws CimiException {
        MachineVolume machineVolume = MachineVolume.getMachineVolumeByReference(cimiClient, this.machineVolumeId);
        MachineVolumeShowCommand.printMachineVolume(machineVolume);
    }

    public static void printMachineVolume(final MachineVolume machineVolume) {
        Table table = new Table(2);
        table.addCell("Attribute");
        table.addCell("Value");

        table.addCell("id");
        table.addCell(machineVolume.getId());

        table.addCell("volume");
        table.addCell(machineVolume.getVolume().getId());

        table.addCell("description");
        table.addCell(machineVolume.getDescription());
        if (machineVolume.getCreated() != null) {
            table.addCell("created");
            table.addCell(machineVolume.getCreated().toString());
        }
        if (machineVolume.getUpdated() != null) {
            table.addCell("updated");
            table.addCell(machineVolume.getUpdated().toString());
        }
        table.addCell("properties");
        StringBuffer sb = new StringBuffer();
        if (machineVolume.getProperties() != null) {
            for (Map.Entry<String, String> prop : machineVolume.getProperties().entrySet()) {
                sb.append("(" + prop.getKey() + "," + prop.getValue() + ") ");
            }
        }
        table.addCell(sb.toString());

        System.out.println(table.render());
    }

}
