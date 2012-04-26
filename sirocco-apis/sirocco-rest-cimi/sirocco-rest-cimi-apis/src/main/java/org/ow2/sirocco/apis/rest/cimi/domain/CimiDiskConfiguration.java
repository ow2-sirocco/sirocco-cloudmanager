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

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;

/**
 * Class Disk.
 */
@XmlRootElement(name = "Disk")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiDiskConfiguration extends CimiDisk {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "format".
     */
    @NotNull(groups = {GroupWrite.class})
    private String format;

    /**
     * Field "attachmentPoint".
     */
    @NotNull(groups = {GroupWrite.class})
    private String attachmentPoint;

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
     * @param attachmentPoint The attachment point
     */
    public CimiDiskConfiguration(final String format, final String attachmentPoint) {
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
    public CimiDiskConfiguration(final CimiCapacity capacity, final String format, final String attachmentPoint) {
        super(capacity);
        this.format = format;
        this.attachmentPoint = attachmentPoint;
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
