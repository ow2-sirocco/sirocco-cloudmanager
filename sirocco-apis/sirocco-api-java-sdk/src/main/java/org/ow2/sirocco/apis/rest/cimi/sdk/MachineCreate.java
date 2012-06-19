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

import java.util.HashMap;
import java.util.Map;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;

public class MachineCreate {
    CimiMachineCreate cimiMachineCreate;

    private MachineTemplate machineTemplate;

    public MachineCreate() {
        this.cimiMachineCreate = new CimiMachineCreate();
    }

    public String getName() {
        return this.cimiMachineCreate.getName();
    }

    public void setName(final String name) {
        this.cimiMachineCreate.setName(name);
    }

    public String getDescription() {
        return this.cimiMachineCreate.getDescription();
    }

    public void setDescription(final String description) {
        this.cimiMachineCreate.setDescription(description);
    }

    public Map<String, String> getProperties() {
        return this.cimiMachineCreate.getProperties();
    }

    public void setProperties(final Map<String, String> properties) {
        this.cimiMachineCreate.setProperties(properties);
    }

    public void addProperty(final String key, final String value) {
        if (this.cimiMachineCreate.getProperties() == null) {
            this.cimiMachineCreate.setProperties(new HashMap<String, String>());
        }
        this.cimiMachineCreate.getProperties().put(key, value);
    }

    public MachineTemplate getMachineTemplate() {
        return this.machineTemplate;
    }

    public void setMachineTemplate(final MachineTemplate machineTemplate) {
        this.machineTemplate = machineTemplate;
        this.cimiMachineCreate.setMachineTemplate(machineTemplate.cimiObject);
    }

}
