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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.domain;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupCreateByValue;
import org.ow2.sirocco.apis.rest.cimi.validator.ValidChild;

/**
 * Class ComponentDescriptor.
 */
@XmlRootElement(name = "ComponentDescriptor")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiComponentDescriptor extends CimiCommon {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "type".
     */
    private String type;

    /**
     * Field "quantity".
     */
    private Integer quantity;

    /**
     * Field "component".
     */
    @ValidChild
    @NotNull(groups = GroupCreateByValue.class)
    private CimiObjectCommon component;

    /**
     * Default constructor.
     */
    public CimiComponentDescriptor() {
        super();
    }

    /**
     * Return the value of field "type".
     * 
     * @return The value
     */
    public String getType() {
        return this.type;
    }

    /**
     * Set the value of field "type".
     * 
     * @param type The value
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Return the value of field "quantity".
     * 
     * @return The value
     */
    public Integer getQuantity() {
        return this.quantity;
    }

    /**
     * Set the value of field "quantity".
     * 
     * @param quantity The value
     */
    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Return the associated component.
     * 
     * @return The component
     */
    @XmlTransient
    @JsonIgnore
    public CimiObjectCommon getComponent() {
        return this.component;
    }

    /**
     * Set the associated component.
     * 
     * @param component The component
     */
    public void setComponent(final CimiObjectCommon component) {
        this.component = component;
    }

    /**
     * Return the value of field "credentialsTemplate".
     * 
     * @return The value
     */
    public CimiCredentialsTemplate getCredentialsTemplate() {
        CimiCredentialsTemplate template = null;
        if (this.component instanceof CimiCredentialsTemplate) {
            template = (CimiCredentialsTemplate) this.component;
        }
        return template;
    }

    /**
     * Set the value of field "credentialsTemplate".
     * 
     * @param credentialsTemplate The value
     */
    public void setCredentialsTemplate(final CimiCredentialsTemplate credentialsTemplate) {
        this.component = credentialsTemplate;
    }

    /**
     * Return the value of field "machineTemplate".
     * 
     * @return The value
     */
    public CimiMachineTemplate getMachineTemplate() {
        CimiMachineTemplate template = null;
        if (this.component instanceof CimiMachineTemplate) {
            template = (CimiMachineTemplate) this.component;
        }
        return template;
    }

    /**
     * Set the value of field "machineTemplate".
     * 
     * @param machineTemplate The value
     */
    public void setMachineTemplate(final CimiMachineTemplate machineTemplate) {
        this.component = machineTemplate;
    }

    /**
     * Return the value of field "systemTemplate".
     * 
     * @return The value
     */
    public CimiSystemTemplate getSystemTemplate() {
        CimiSystemTemplate template = null;
        if (this.component instanceof CimiSystemTemplate) {
            template = (CimiSystemTemplate) this.component;
        }
        return template;
    }

    /**
     * Set the value of field "systemTemplate".
     * 
     * @param systemTemplate The value
     */
    public void setSystemTemplate(final CimiSystemTemplate systemTemplate) {
        this.component = systemTemplate;
    }

    /**
     * Return the value of field "volumeTemplate".
     * 
     * @return The value
     */
    public CimiVolumeTemplate getVolumeTemplate() {
        CimiVolumeTemplate template = null;
        if (this.component instanceof CimiVolumeTemplate) {
            template = (CimiVolumeTemplate) this.component;
        }
        return template;
    }

    /**
     * Set the value of field "volumeTemplate".
     * 
     * @param volumeTemplate The value
     */
    public void setVolumeTemplate(final CimiVolumeTemplate volumeTemplate) {
        this.component = volumeTemplate;
    }

}
