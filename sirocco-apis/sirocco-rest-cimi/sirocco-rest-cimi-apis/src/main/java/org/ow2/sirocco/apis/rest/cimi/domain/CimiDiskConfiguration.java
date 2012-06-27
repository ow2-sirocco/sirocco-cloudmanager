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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;

/**
 * Class Disk.
 */
@XmlRootElement(name = "Disk")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiDiskConfiguration implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "capacity".
     */
    @Valid
    @NotNull(groups = {GroupWrite.class})
    private Integer capacity;

    /**
     * Field "format".
     */
    @NotNull(groups = {GroupWrite.class})
    private String format;

    /**
     * Field "initialLocation".
     */
    private String initialLocation;

    /**
     * Default constructor.
     */
    public CimiDiskConfiguration() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param format The format
     * @param initialLocation The initial location
     */
    public CimiDiskConfiguration(final String format, final String initialLocation) {
        super();
        this.setFormat(format);
        this.setInitialLocation(initialLocation);
    }

    /**
     * Parameterized constructor.
     * 
     * @param format The format
     * @param initialLocation The initial location
     * @param capacity The capacity
     */
    public CimiDiskConfiguration(final Integer capacity, final String format, final String initialLocation) {
        super();
        this.setCapacity(capacity);
        this.setFormat(format);
        this.setInitialLocation(initialLocation);
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
}
