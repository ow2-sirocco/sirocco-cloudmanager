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

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Class Disk. <p> </p>
 */
@XmlRootElement(name = "disk")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiDisk implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    // ---------------------------------------- Fields

    /**
     * Field "capacity". <p> </p>
     */
    private CimiCapacity capacity;

    /**
     * Field "format". <p> </p>
     */
    private String format;

    /**
     * Field "attachmentPoint". <p> </p>
     */
    private String attachmentPoint;

    // ---------------------------------------- Constructors

    /**
     * Default constructor.
     */
    public CimiDisk() {
        super();
    }

    // ---------------------------------------- ???com-accesseurs???

    /**
     * Return the value of field "capacity".
     * @return The value
     */
    public CimiCapacity getCapacity() {
        return this.capacity;
    }

    /**
     * Set the value of field "capacity".
     * @param capacity The value
     */
    public void setCapacity(CimiCapacity capacity) {
        this.capacity = capacity;
    }

    /**
     * Return the value of field "format".
     * @return The value
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * Set the value of field "format".
     * @param format The value
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Return the value of field "attachmentPoint".
     * @return The value
     */
    public String getAttachmentPoint() {
        return this.attachmentPoint;
    }

    /**
     * Set the value of field "attachmentPoint".
     * @param attachmentPoint The value
     */
    public void setAttachmentPoint(String attachmentPoint) {
        this.attachmentPoint = attachmentPoint;
    }

}
