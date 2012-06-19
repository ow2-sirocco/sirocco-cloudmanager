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

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiException;
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineConfiguration;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "list machine config")
public class MachineConfigListCommand implements Command {
    public static String COMMAND_NAME = "machineconfig-list";

    @Parameter(names = "-first", description = "First index of entity to return")
    private Integer first = -1;

    @Parameter(names = "-last", description = "Last index of entity to return")
    private Integer last = -1;

    @Parameter(names = "-filter", description = "Filter expression")
    private String filter;

    @Override
    public String getName() {
        return MachineConfigListCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final CimiClient cimiClient) throws CimiException {
        List<MachineConfiguration> machineConfigs = MachineConfiguration.getMachineConfigurations(cimiClient);

        Table table = new Table(6);
        table.addCell("ID");
        table.addCell("Name");
        table.addCell("Description");
        table.addCell("Cpu");
        table.addCell("Memory (MB)");
        table.addCell("Disks (GB)");

        for (MachineConfiguration machineConfig : machineConfigs) {
            table.addCell(machineConfig.getId());
            table.addCell(machineConfig.getName());
            table.addCell(machineConfig.getDescription());
            table.addCell(Integer.toString(machineConfig.getCpu()));
            table.addCell(Integer.toString(machineConfig.getMemory()));

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < machineConfig.getDisks().length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(machineConfig.getDisks()[i].capacity);
            }
            table.addCell((sb.toString()));
        }
        System.out.println(table.render());

    }
}
