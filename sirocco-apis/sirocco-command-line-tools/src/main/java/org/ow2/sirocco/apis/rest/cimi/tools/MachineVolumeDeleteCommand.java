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

import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiException;
import org.ow2.sirocco.apis.rest.cimi.sdk.Job;
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineVolume;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "detach volume from machine")
public class MachineVolumeDeleteCommand implements Command {
    @Parameter(names = "-id", description = "id of the machine volume", required = true)
    private String machineVolumeId;

    @Override
    public String getName() {
        return "machinevolume-delete";
    }

    @Override
    public void execute(final CimiClient cimiClient) throws CimiException {
        MachineVolume machineVolume = MachineVolume.getMachineVolumeByReference(cimiClient, this.machineVolumeId);
        Job job = machineVolume.delete();
        System.out.println("MachineVolume " + this.machineVolumeId + " being deleted");
        JobListCommand.printJob(job);
    }
}
