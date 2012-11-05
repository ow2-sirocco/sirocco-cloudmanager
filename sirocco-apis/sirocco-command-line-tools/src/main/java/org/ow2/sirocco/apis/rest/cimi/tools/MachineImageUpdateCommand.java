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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiException;
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineImage;
import org.ow2.sirocco.apis.rest.cimi.sdk.UpdateResult;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "update machine image")
public class MachineImageUpdateCommand implements Command {
    @Parameter(names = "-id", description = "id of the machine image", required = true)
    private String machineImageId;

    @Parameter(names = "-name", description = "name of the image", required = false)
    private String name;

    @Parameter(names = "-description", description = "description of the image", required = false)
    private String description;

    @Parameter(names = "-properties", variableArity = true, description = "key value pairs", required = false)
    private List<String> properties;

    @Parameter(names = "-location", description = "image location", required = false)
    private String imageLocation;

    @Override
    public String getName() {
        return "machineimage-update";
    }

    @Override
    public void execute(final CimiClient cimiClient) throws CimiException {
        Map<String, Object> attributeValues = new HashMap<String, Object>();
        if (this.name != null) {
            attributeValues.put("name", this.name);
        }
        if (this.description != null) {
            attributeValues.put("description", this.description);
        }
        if (this.properties != null) {
            Map<String, String> props = new HashMap<String, String>();
            for (int i = 0; i < this.properties.size() / 2; i++) {
                props.put(this.properties.get(i * 2), this.properties.get(i * 2 + 1));
            }
            attributeValues.put("properties", props);
        }
        if (this.imageLocation != null) {
            attributeValues.put("imageLocation", this.imageLocation);
        }

        UpdateResult<MachineImage> result = MachineImage.updateMachineImage(cimiClient, this.machineImageId, attributeValues);
        if (result.getJob() != null) {
            System.out.println("MachineImage " + result.getJob().getTargetResourceRef() + " being updated");
            JobListCommand.printJob(result.getJob());
        } else {
            System.out.println("MachineImage: " + this.machineImageId + " updated");
        }
    }

}
