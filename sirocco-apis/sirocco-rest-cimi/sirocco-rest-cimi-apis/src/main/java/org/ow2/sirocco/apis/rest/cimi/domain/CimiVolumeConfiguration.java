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
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupCreateByValue;

/**
 * Class VolumeConfiguration.
 */
@XmlRootElement(name = "VolumeConfiguration")
@XmlType(propOrder = {"id", "name", "description", "created", "updated", "propertyArray", "type", "format", "capacity",
    "operations"})
@JsonPropertyOrder({"resourceURI", "id", "name", "description", "created", "updated", "properties", "type", "format",
    "capacity", "operations"})
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiVolumeConfiguration extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "type".
     */
    @NotNull(groups = {GroupCreateByValue.class})
    private String type;

    /**
     * Field "format".
     */
    private String format;

    /**
     * Field "capacity".
     */
    @NotNull(groups = {GroupCreateByValue.class})
    private Integer capacity;

    /**
     * Default constructor.
     */
    public CimiVolumeConfiguration() {
        super();
    }

    /**
     * Return the value of field "format".
     * 
     * @return The value
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * Set the value of field "format".
     * 
     * @param format The value
     */
    public void setFormat(final String format) {
        this.format = format;
    }

    /**
     * Return the value of field "capacity".
     * 
     * @return The value
     */
    public Integer getCapacity() {
        return this.capacity;
    }

    /**
     * Set the value of field "capacity".
     * 
     * @param capacity The value
     */
    public void setCapacity(final Integer capacity) {
        this.capacity = capacity;
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
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        has = has || (null != this.getCapacity());
        has = has || (null != this.getFormat());
        has = has || (null != this.getType());
        return has;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiExchange#getExchangeType()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public ExchangeType getExchangeType() {
        return ExchangeType.VolumeConfiguration;
    }

}
