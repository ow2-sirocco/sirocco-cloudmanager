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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Class VolumeConfiguration. <p> </p>
 */
@XmlRootElement(name = "volumeConfiguration")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiVolumeConfiguration extends CimiCommonId implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    // ---------------------------------------- Fields
    private String href;

    /**
     * Field "format". <p> </p>
     */
    private String format;

    /**
     * Field "capacity". <p> </p>
     */
    private CimiCapacity capacity;

    /**
     * Field "supportsSnapshots". <p> </p>
     */
    private Boolean supportsSnapshots;

    /**
     * Field "guestInterface". <p> </p>
     */
    private String guestInterface;

    // ---------------------------------------- Constructors

    /**
     * Default constructor.
     */
    public CimiVolumeConfiguration() {
        super();
    }

    // ---------------------------------------- ???com-accesseurs???

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
     * Return the value of field "supportsSnapshots".
     * @return The value
     */
    public Boolean getSupportsSnapshots() {
        return this.supportsSnapshots;
    }

    /**
     * Set the value of field "supportsSnapshots".
     * @param supportsSnapshots The value
     */
    public void setSupportsSnapshots(Boolean supportsSnapshots) {
        this.supportsSnapshots = supportsSnapshots;
    }

    /**
     * Return the value of field "guestInterface".
     * @return The value
     */
    public String getGuestInterface() {
        return this.guestInterface;
    }

    /**
     * Set the value of field "guestInterface".
     * @param guestInterface The value
     */
    public void setGuestInterface(String guestInterface) {
        this.guestInterface = guestInterface;
    }

    /**
     * @return the href
     */
    @XmlAttribute
    public String getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public void setHref(String href) {
        this.href = href;
    }
}
