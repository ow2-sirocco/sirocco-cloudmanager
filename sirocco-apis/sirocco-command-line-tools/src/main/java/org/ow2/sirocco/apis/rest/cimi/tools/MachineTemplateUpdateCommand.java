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
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.sdk.UpdateResult;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "update machine template")
public class MachineTemplateUpdateCommand implements Command {
    @Parameter(names = "-id", description = "id of the machine template", required = true)
    private String machineTemplateId;

    @Parameter(names = "-name", description = "name of the template", required = false)
    private String name;

    @Parameter(names = "-description", description = "description of the template", required = false)
    private String description;

    @Parameter(names = "-properties", variableArity = true, description = "key value pairs", required = false)
    private List<String> properties;

    @Parameter(names = "-config", description = "machine config id", required = false)
    private String machineConfigId;

    @Parameter(names = "-image", description = "machine image id", required = false)
    private String machineImageId;

    @Parameter(names = "-cred", description = "credential id", required = false)
    private String credentialId;

    @Override
    public String getName() {
        return "machinetemplate-update";
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
        if (this.machineConfigId != null) {
            attributeValues.put("machineConfig", this.machineConfigId);
        }
        if (this.credentialId != null) {
            attributeValues.put("credential", this.credentialId);
        }
        if (this.machineImageId != null) {
            attributeValues.put("machineImage", this.machineImageId);
        }

        UpdateResult<MachineTemplate> result = MachineTemplate.updateMachineTemplate(cimiClient, this.machineTemplateId,
            attributeValues);
        if (result.getJob() != null) {
            System.out.println("MachineTemplate " + result.getJob().getTargetResourceRef() + " being updated");
            JobListCommand.printJob(result.getJob());
        } else {
            System.out.println("MachineTemplate: " + this.machineTemplateId + " updated");
        }
    }

}
