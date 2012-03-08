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
 * $Id: CimiCommon.java 127 2012-03-08 00:26:28Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

/**
 * Class Common. <p> Attributes common to all entities </p>
 */
public class CimiCommon implements Serializable, CimiData {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    // ---------------------------------------- Fields

    /**
     * Field "id". <p> The unique self-reference to this entity; assigned upon
     * entity creation. This attribute value shall be unique in the Provider’s
     * cloud. </p>
     */
    private String id;

    /**
     * Field "name". <p> The human readable name of this entity; assigned by the
     * creator as a part of the entity creation input. </p>
     */
    private String name;

    /**
     * Field "description". <p> The human readable description of this entity;
     * assigned by the creator as a part of the entity creation input. </p>
     */
    private String description;

    /**
     * Field "created". <p> The timestamp when this entity was created. The
     * format is DateTimeUTC (ISO 8601), example : 2012-02-06T08:39:57Z. </p>
     */
    private Date created;

    /**
     * Field "updated". <p> The time at which the last explicit attribute update
     * was made on the resoure. Note, while operations such as 'stop' do
     * implicitly modify the 'state' attribute it does not change the
     * 'updated_time'. The format is DateTimeUTC (ISO 8601), example :
     * 2012-02-06T08:39:57Z. </p>
     */
    private Date updated;

    /**
     * Field "properties". <p> A list of key/value pairs, some of which may
     * control one or more aspects this entity. Properties may also serve as an
     * extension point, allowing Consumers to record additional information
     * about the resource. </p>
     */
    @JsonProperty
    private Map<String, String> properties;

    /**
     * Field "operations". <p> A list of possible operations. </p>
     */
    private Operation[] operations;

    // ---------------------------------------- Constructors

    /**
     * Default constructor.
     */
    public CimiCommon() {
        super();
    }

    // ---------------------------------------- ???com-accesseurs???

    /**
     * Return the value of field "id".
     * @return The value
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set the value of field "id".
     * @param id The value
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Return the value of field "name".
     * @return The value
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the value of field "name".
     * @param name The value
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the value of field "description".
     * @return The value
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the value of field "description".
     * @param description The value
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Return the value of field "created".
     * @return The value
     */
    public Date getCreated() {
        return this.created;
    }

    /**
     * Set the value of field "created".
     * @param created The value
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * Return the value of field "updated".
     * @return The value
     */
    public Date getUpdated() {
        return this.updated;
    }

    /**
     * Set the value of field "updated".
     * @param updated The value
     */
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    /**
     * Return the value of field "properties".
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
        if (null != properties) {
            for (Entry<String, String> entry : properties.entrySet()) {
                Property mapEntry = new Property();
                mapEntry.key = entry.getKey();
                mapEntry.value = entry.getValue();
                list.add(mapEntry);
            }
        }
        return list.toArray(new Property[list.size()]);
    }

    public void setPropertyArray(Property[] props) {
        this.properties = new HashMap<String, String>();
        for (Property prop : props) {
            properties.put(prop.key, prop.value);
        }
    }

    /**
     * Set the value of field "properties".
     * @param properties The value
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Return the value of field "operations".
     * @return The value
     */
    public Operation[] getOperations() {
        return this.operations;
    }

    /**
     * Set the value of field "operations".
     * @param operations The value
     */
    public void setOperations(Operation[] operations) {
        this.operations = operations;
    }

    private static class Property {

        @XmlAttribute
        private String key;

        @XmlValue
        private String value;
    }

}
