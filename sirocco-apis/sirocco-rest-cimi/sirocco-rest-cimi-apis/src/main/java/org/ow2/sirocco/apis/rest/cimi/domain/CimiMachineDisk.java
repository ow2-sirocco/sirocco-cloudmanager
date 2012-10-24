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

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Class Disk.
 */
@XmlRootElement(name = "Disk")
@XmlType(propOrder = {"id", "name", "description", "created", "updated", "propertyArray", "capacity", "initialLocation",
    "operations"})
@JsonPropertyOrder({"resourceURI", "id", "name", "description", "created", "updated", "properties", "capacity",
    "initialLocation", "operations"})
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiMachineDisk extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "capacity".
     */
    @Valid
    private Integer capacity;

    /**
     * Field "initialLocation".
     */
    private String initialLocation;

    /**
     * Default constructor.
     */
    public CimiMachineDisk() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param capacity The capacity
     */
    public CimiMachineDisk(final Integer capacity) {
        super();
        this.setCapacity(capacity);
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
     * Return the value of field "initialLocation".
     * 
     * @return The value
     */
    public String getInitialLocation() {
        return this.initialLocation;
    }

    /**
     * Set the value of field "initialLocation".
     * 
     * @param initialLocation The value
     */
    public void setInitialLocation(final String initialLocation) {
        this.initialLocation = initialLocation;
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
        return ExchangeType.Disk;
    }
}
