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
package org.ow2.sirocco.apis.rest.cimi.sdk;

import java.util.Map;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiComponentDescriptor;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeTemplate;

public class ComponentDescriptor {
    private CimiComponentDescriptor cimiComponentDescriptor;

    private Object componentTemplate;

    public ComponentDescriptor() {
        this.cimiComponentDescriptor = new CimiComponentDescriptor();
    }

    ComponentDescriptor(final CimiComponentDescriptor cimiComponentDescriptor) {
        this.cimiComponentDescriptor = cimiComponentDescriptor;
        if (cimiComponentDescriptor.getComponent() instanceof CimiMachineTemplate) {
            CimiMachineTemplate cimiTemplate = (CimiMachineTemplate) cimiComponentDescriptor.getComponent();
            this.componentTemplate = new MachineTemplate(cimiTemplate);
        } else if (cimiComponentDescriptor.getComponent() instanceof CimiVolumeTemplate) {
            CimiVolumeTemplate cimiTemplate = (CimiVolumeTemplate) cimiComponentDescriptor.getComponent();
            this.componentTemplate = new VolumeTemplate(cimiTemplate);
        } else if (cimiComponentDescriptor.getComponent() instanceof CimiCredentialTemplate) {
            CimiCredentialTemplate cimiTemplate = (CimiCredentialTemplate) cimiComponentDescriptor.getComponent();
            this.componentTemplate = new CredentialTemplate(cimiTemplate);
        }
    }

    public int getQuantity() {
        return this.cimiComponentDescriptor.getQuantity();
    }

    public void setQuantity(final int quantity) {
        this.cimiComponentDescriptor.setQuantity(quantity);
    }

    public String getName() {
        return this.cimiComponentDescriptor.getName();
    }

    public void setName(final String name) {
        this.cimiComponentDescriptor.setName(name);
    }

    public String getDescription() {
        return this.cimiComponentDescriptor.getDescription();
    }

    public void setDescription(final String description) {
        this.cimiComponentDescriptor.setDescription(description);
    }

    public Map<String, String> getProperties() {
        return this.cimiComponentDescriptor.getProperties();
    }

    public void setProperties(final Map<String, String> properties) {
        this.cimiComponentDescriptor.setProperties(properties);
    }

    public Object getComponentTemplate() {
        return this.componentTemplate;
    }

    public void setComponentTemplate(final Object componentTemplate) throws Exception {
        if (componentTemplate instanceof MachineTemplate) {
            this.componentTemplate = componentTemplate;
            this.cimiComponentDescriptor.setComponent(((MachineTemplate) componentTemplate).cimiObject);
            this.cimiComponentDescriptor.setType(Machine.TYPE_URI);
        } else if (componentTemplate instanceof VolumeTemplate) {
            this.componentTemplate = componentTemplate;
            this.cimiComponentDescriptor.setComponent(((VolumeTemplate) componentTemplate).cimiObject);
            this.cimiComponentDescriptor.setType(Volume.TYPE_URI);
        } else if (componentTemplate instanceof CredentialTemplate) {
            this.componentTemplate = componentTemplate;
            this.cimiComponentDescriptor.setComponent(((CredentialTemplate) componentTemplate).cimiObject);
            this.cimiComponentDescriptor.setType(Credential.TYPE_URI);
        } else {
            throw new CimiException("Illegal template type");
        }

    }

}
