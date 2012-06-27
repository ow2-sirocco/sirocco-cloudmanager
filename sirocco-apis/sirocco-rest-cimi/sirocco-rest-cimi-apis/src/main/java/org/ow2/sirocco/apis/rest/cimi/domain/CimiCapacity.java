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

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;

/**
 * Class Capacity.
 * <p>
 * </p>
 */
@XmlRootElement(name = "Capacity")
@JsonSerialize(include = Inclusion.NON_NULL)
@Deprecated
public class CimiCapacity implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /** Field "quantity". */
    @NotNull(groups = {GroupWrite.class})
    private Integer quantity;

    /**
     * Field "units".
     * <p>
     * enum : byte (B), kilobyte (kB), megabyte (MB), gigabyte (GB), terabyte
     * (TB), petabyte (PB), exabyte (EB), zettabyte (ZB), yottabyte (YB)
     * </p>
     */
    @NotNull(groups = {GroupWrite.class})
    private String units;

    /**
     * Default constructor.
     */
    public CimiCapacity() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param quantity The quantity
     * @param units The units
     */
    public CimiCapacity(final Integer quantity, final String units) {
        super();
        this.quantity = quantity;
        this.units = units;
    }

    /**
     * Return the value of field "quantity".
     * 
     * @return The value
     */
    @XmlAttribute
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
     * Return the value of field "units".
     * 
     * @return The value
     */
    @XmlAttribute
    public String getUnits() {
        return this.units;
    }

    /**
     * Set the value of field "units".
     * 
     * @param units The value
     */
    public void setUnits(final String units) {
        this.units = units;
    }

}
