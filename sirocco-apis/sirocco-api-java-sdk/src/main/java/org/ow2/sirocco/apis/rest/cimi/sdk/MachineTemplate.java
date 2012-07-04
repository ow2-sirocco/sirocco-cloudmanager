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

package org.ow2.sirocco.apis.rest.cimi.sdk;

import java.util.ArrayList;
import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class MachineTemplate extends Resource<CimiMachineTemplate> {
    private MachineImage machineImage;

    private MachineConfiguration machineConfig;

    public MachineTemplate() {
        super(null, new CimiMachineTemplate());
    }

    public MachineTemplate(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiMachineTemplate());
        this.cimiObject.setHref(id);
        this.cimiObject.setId(id);
    }

    public MachineTemplate(final CimiClient cimiClient, final CimiMachineTemplate cimiObject) {
        super(cimiClient, cimiObject);
        this.machineImage = new MachineImage(cimiClient, cimiObject.getMachineImage());
        this.machineConfig = new MachineConfiguration(cimiClient, cimiObject.getMachineConfig());
    }

    public MachineImage getMachineImage() {
        return this.machineImage;
    }

    public void setMachineImage(final MachineImage machineImage) {
        this.machineImage = machineImage;
        this.cimiObject.setMachineImage(machineImage.cimiObject);
    }

    public MachineConfiguration getMachineConfig() {
        return this.machineConfig;
    }

    public void setMachineConfig(final MachineConfiguration machineConfig) {
        this.machineConfig = machineConfig;
        this.cimiObject.setMachineConfig(machineConfig.cimiObject);
    }

    public void delete() throws CimiException {
        this.cimiClient.deleteRequest(this.cimiClient.extractPath(this.getId()));
    }

    public static MachineTemplate createMachineTemplate(final CimiClient client, final MachineTemplate machineTemplate)
        throws CimiException {
        CimiMachineTemplate cimiObject = client.postRequest(ConstantsPath.MACHINE_TEMPLATE_PATH, machineTemplate.cimiObject,
            CimiMachineTemplate.class);
        return new MachineTemplate(client, cimiObject);
    }

    public static List<MachineTemplate> getMachineTemplates(final CimiClient client) throws CimiException {
        CimiMachineTemplateCollection machineTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getMachineTemplates().getHref()), CimiMachineTemplateCollection.class);

        List<MachineTemplate> result = new ArrayList<MachineTemplate>();

        if (machineTemplateCollection.getCollection() != null) {
            for (CimiMachineTemplate cimiMachineTemplate : machineTemplateCollection.getCollection().getArray()) {
                result.add(new MachineTemplate(client, cimiMachineTemplate));
            }
        }
        return result;
    }

    public static MachineTemplate getMachineTemplateByReference(final CimiClient client, final String ref) throws CimiException {
        return new MachineTemplate(client, client.getCimiObjectByReference(ref, CimiMachineTemplate.class));
    }

    public static MachineTemplate getMachineTemplateById(final CimiClient client, final String id) throws CimiException {
        String path = client.getMachineTemplatesPath() + "/" + id;
        return new MachineTemplate(client, client.getCimiObjectByReference(path, CimiMachineTemplate.class));
    }

}
