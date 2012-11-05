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

import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiException;
import org.ow2.sirocco.apis.rest.cimi.sdk.CreateResult;
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineVolume;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "attach volume to machine")
public class MachineVolumeCreateCommand implements Command {
    @Parameter(names = "-machine", description = "id of the machine", required = true)
    private String machineId;

    @Parameter(names = "-volume", description = "id of the volume", required = true)
    private String volumeId;

    @Parameter(names = "-name", description = "name of the MachineVolume", required = false)
    private String name;

    @Parameter(names = "-description", description = "description of the MachineVolume", required = false)
    private String description;

    @Parameter(names = "-properties", variableArity = true, description = "key value pairs", required = false)
    private List<String> properties;

    @Override
    public String getName() {
        return "machinevolume-create";
    }

    @Override
    public void execute(final CimiClient cimiClient) throws CimiException {
        MachineVolume machineVolume = new MachineVolume();
        machineVolume.setVolumeRef(this.volumeId);
        machineVolume.setName(this.name);
        machineVolume.setDescription(this.description);
        if (this.properties != null) {
            for (int i = 0; i < this.properties.size() / 2; i++) {
                machineVolume.addProperty(this.properties.get(i * 2), this.properties.get(i * 2 + 1));
            }
        }
        CreateResult<MachineVolume> result = MachineVolume.createMachineVolume(cimiClient, this.machineId, machineVolume);
        if (result.getJob() != null) {
            System.out.println("Volume " + this.volumeId + " being attached");
            JobListCommand.printJob(result.getJob());
        } else {
            MachineVolumeShowCommand.printMachineVolume(result.getResource());
        }

    }
}
