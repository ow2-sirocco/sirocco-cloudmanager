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
import org.ow2.sirocco.apis.rest.cimi.sdk.CreateResult;
import org.ow2.sirocco.apis.rest.cimi.sdk.VolumeTemplate;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "create volume template")
public class VolumeTemplateCreateCommand implements Command {
    @Parameter(names = "-name", description = "name of the template", required = false)
    private String name;

    @Parameter(names = "-description", description = "description of the template", required = false)
    private String description;

    @Parameter(names = "-properties", variableArity = true, description = "key value pairs", required = false)
    private List<String> properties;

    @Parameter(names = "-config", description = "volume config id", required = true)
    private String volumeConfigId;

    @Parameter(names = "-image", description = "volume image id", required = false)
    private String volumeImageId;

    @Override
    public String getName() {
        return "volumetemplate-create";
    }

    @Override
    public void execute(final CimiClient cimiClient) throws CimiException {
        VolumeTemplate volumeTemplate = new VolumeTemplate();
        volumeTemplate.setName(this.name);
        volumeTemplate.setDescription(this.description);
        if (this.properties != null) {
            for (int i = 0; i < this.properties.size() / 2; i++) {
                volumeTemplate.addProperty(this.properties.get(i * 2), this.properties.get(i * 2 + 1));
            }
        }
        volumeTemplate.setVolumeConfigRef(this.volumeConfigId);
        if (this.volumeImageId != null) {
            volumeTemplate.setVolumeImageRef(this.volumeImageId);
        }

        CreateResult<VolumeTemplate> result = VolumeTemplate.createVolumeTemplate(cimiClient, volumeTemplate);
        if (result.getJob() != null) {
            System.out.println("VolumeTemplate " + result.getJob().getTargetResourceRef() + " being created");
            JobListCommand.printJob(result.getJob());
        } else {
            VolumeTemplateShowCommand.printVolumeTemplate(result.getResource());
        }
    }

}
