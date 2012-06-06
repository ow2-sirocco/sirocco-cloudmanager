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
 * Class with the common attributes of multiple resources.
 */
public class CimiCommon implements CimiDataCommon {

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
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiDataCommon#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiDataCommon#setName(java.lang
     *      .String)
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiDataCommon#getDescription()
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiDataCommon#setDescription(java
     *      .lang.String)
     */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiDataCommon#getProperties()
     */
    @Override
    @XmlTransient
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiDataCommon#setProperties(java
     *      .util.Map)
     */
    @Override
    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Get all keys-values properties for JAXB.
     * 
     * @return An array with all the keys-values properties
     */
    @JsonIgnore
    @XmlElement(name = "property")
    public KeyValue[] getPropertyArray() {
        List<KeyValue> list = new ArrayList<KeyValue>();
        if (null != this.properties) {
            for (Entry<String, String> entry : this.properties.entrySet()) {
                KeyValue mapEntry = new KeyValue();
                mapEntry.key = entry.getKey();
                mapEntry.value = entry.getValue();
                list.add(mapEntry);
            }
        }
        return list.toArray(new KeyValue[list.size()]);
    }

    /**
     * Set all keys-values properties for JAXB.
     * 
     * @param props An array with all the keys-values properties
     */
    public void setPropertyArray(final KeyValue[] props) {
        this.properties = new HashMap<String, String>();
        for (KeyValue prop : props) {
            this.properties.put(prop.key, prop.value);
        }
    }

    /**
     * Key-value class for JAXB.
     */
    private static class KeyValue {

        @XmlAttribute
        private String key;

        @XmlValue
        private String value;
    }

}
