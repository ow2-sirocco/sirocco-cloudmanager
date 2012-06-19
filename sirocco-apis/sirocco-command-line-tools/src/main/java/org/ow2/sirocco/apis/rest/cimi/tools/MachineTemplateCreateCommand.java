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

import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiException;
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineImage;
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineTemplate;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "create machine template")
public class MachineTemplateCreateCommand implements Command {
    @Parameter(names = "-name", description = "name of the template", required = false)
    private String name;

    @Parameter(names = "-description", description = "description of the template", required = false)
    private String description;

    @Parameter(names = "-properties", variableArity = true, description = "key value pairs", required = false)
    private List<String> properties;

    @Parameter(names = "-config", description = "machine config id", required = true)
    private String machineConfigId;

    @Parameter(names = "-image", description = "machine image id", required = true)
    private String machineImageId;

    @Override
    public String getName() {
        return "machinetemplate-create";
    }

    @Override
    public void execute(final CimiClient cimiClient) throws CimiException {
        MachineTemplate machineTemplate = new MachineTemplate();
        machineTemplate.setName(this.name);
        machineTemplate.setDescription(this.description);
        if (this.properties != null) {
            for (int i = 0; i < this.properties.size() / 2; i++) {
                machineTemplate.addProperty(this.properties.get(i * 2), this.properties.get(i * 2 + 1));
            }
        }
        MachineConfiguration machineConfig = new MachineConfiguration(cimiClient, this.machineConfigId);
        machineTemplate.setMachineConfig(machineConfig);
        MachineImage machineImage = new MachineImage(cimiClient, this.machineImageId);
        machineTemplate.setMachineImage(machineImage);

        machineTemplate = MachineTemplate.createMachineTemplate(cimiClient, machineTemplate);

        System.out.println("MachineTemplate: " + machineTemplate.getId() + " created");

    }

}
