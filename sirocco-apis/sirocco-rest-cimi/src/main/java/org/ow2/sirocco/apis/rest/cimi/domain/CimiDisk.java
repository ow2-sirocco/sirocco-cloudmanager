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
import org.ow2.sirocco.apis.rest.cimi.validator.GroupCreate;

/**
 * Class Disk.
 * <p>
 * </p>
 */
@XmlRootElement(name = "Disk")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiDisk implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "capacity".
     * <p>
     * </p>
     */
    @Valid
    private CimiCapacity capacity;

    /**
     * Field "format".
     * <p>
     * </p>
     */
    @NotNull(groups = {GroupCreate.class})
    private String format;

    /**
     * Field "attachmentPoint".
     * <p>
     * </p>
     */
    @NotNull(groups = {GroupCreate.class})
    private String attachmentPoint;

    /**
     * Default constructor.
     */
    public CimiDisk() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param format The format
     * @param attachmentPoint The attachment point
     */
    public CimiDisk(final String format, final String attachmentPoint) {
        super();
        this.format = format;
        this.attachmentPoint = attachmentPoint;
    }

    /**
     * Parameterized constructor.
     * 
     * @param format The format
     * @param attachmentPoint The attachment point
     * @param capacity The capacity
     */
    public CimiDisk(final String format, final String attachmentPoint, final CimiCapacity capacity) {
        super();
        this.format = format;
        this.attachmentPoint = attachmentPoint;
        this.capacity = capacity;
    }

    /**
     * Return the value of field "capacity".
     * 
     * @return The value
     */
    public CimiCapacity getCapacity() {
        return this.capacity;
    }

    /**
     * Set the value of field "capacity".
     * 
     * @param capacity The value
     */
    public void setCapacity(final CimiCapacity capacity) {
        this.capacity = capacity;
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
     * Return the value of field "attachmentPoint".
     * 
     * @return The value
     */
    public String getAttachmentPoint() {
        return this.attachmentPoint;
    }

    /**
     * Set the value of field "attachmentPoint".
     * 
     * @param attachmentPoint The value
     */
    public void setAttachmentPoint(final String attachmentPoint) {
        this.attachmentPoint = attachmentPoint;
    }

}
