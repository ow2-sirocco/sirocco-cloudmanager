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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;
import org.ow2.sirocco.apis.rest.cimi.validator.constraints.Identifier;
import org.ow2.sirocco.apis.rest.cimi.validator.constraints.NotEmptyIfNotNull;

/**
 * Class Common.
 * <p>
 * Attributes common to all entities
 * </p>
 */
public class CimiCommon implements CimiData {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "name".
     * <p>
     * The human readable name of this entity; assigned by the creator as a part
     * of the entity creation input.
     * </p>
     */
    @Identifier(groups = {GroupWrite.class})
    private String name;

    /**
     * Field "description".
     * <p>
     * The human readable description of this entity; assigned by the creator as
     * a part of the entity creation input.
     * </p>
     */
    private String description;

    /**
     * Field "properties".
     * <p>
     * A list of key/value pairs, some of which may control one or more aspects
     * this entity. Properties may also serve as an extension point, allowing
     * Consumers to record additional information about the resource.
     * </p>
     */
    @JsonProperty
    @NotEmptyIfNotNull(groups = {GroupWrite.class})
    private Map<String, String> properties;

    /**
     * Return the value of field "name".
     * 
     * @return The value
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the value of field "name".
     * 
     * @param name The value
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Return the value of field "description".
     * 
     * @return The value
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the value of field "description".
     * 
     * @param description The value
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Return the value of field "properties".
     * 
     * @return The value
     */
    @XmlTransient
    public Map<String, String> getProperties() {
        return this.properties;
    }

    @JsonIgnore
    @XmlElement(name = "property")
    public Property[] getPropertyArray() {
        List<Property> list = new ArrayList<Property>();
        if (null != this.properties) {
            for (Entry<String, String> entry : this.properties.entrySet()) {
                Property mapEntry = new Property();
                mapEntry.key = entry.getKey();
                mapEntry.value = entry.getValue();
                list.add(mapEntry);
            }
        }
        return list.toArray(new Property[list.size()]);
    }

    public void setPropertyArray(final Property[] props) {
        this.properties = new HashMap<String, String>();
        for (Property prop : props) {
            this.properties.put(prop.key, prop.value);
        }
    }

    /**
     * Set the value of field "properties".
     * 
     * @param properties The value
     */
    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    private static class Property {

        @XmlAttribute
        private String key;

        @XmlValue
        private String value;
    }

}
