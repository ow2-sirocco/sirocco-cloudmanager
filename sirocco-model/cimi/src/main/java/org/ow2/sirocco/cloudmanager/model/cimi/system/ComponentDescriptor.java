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

package org.ow2.sirocco.cloudmanager.model.cimi.system;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudTemplate;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ComponentDescriptor extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum ComponentType {
        SYSTEM, VOLUME, MACHINE, CREDENTIALS, NETWORK
    }

    /**
     * Type of component to be instanciated
     */
    private ComponentType componentType;

    /**
     * Id of template entity to be used to instanciate the component
     */
    private CloudTemplate componentTemplate;

    private Integer componentQuantity;

    public ComponentType getComponentType() {
        return this.componentType;
    }

    public void setComponentType(final ComponentType type) {
        this.componentType = type;
    }

    public Integer getComponentQuantity() {
        return this.componentQuantity;
    }

    public void setComponentQuantity(final Integer quantity) {
        this.componentQuantity = quantity;
    }

    @OneToOne(optional = true)
    @JoinColumn(name = "comp_desc_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public CloudTemplate getComponentTemplate() {
        return this.componentTemplate;
    }

    public void setComponentTemplate(final CloudTemplate componentTemplate) {
        this.componentTemplate = componentTemplate;
    }

}