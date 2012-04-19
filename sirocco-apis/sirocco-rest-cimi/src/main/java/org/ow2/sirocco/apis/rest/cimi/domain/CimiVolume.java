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
 * Class Volume.
 * <p>
 */
@XmlRootElement(name = "volume")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiVolume extends CimiCommonId implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    private String href;

    /**
     * Field "state".
     */
    private String state;

    /**
     * Field "capacity".
     */
    private CimiCapacity capacity;

    /**
     * Field "bootable".
     */
    private Boolean bootable;

    /**
     * Field "supportsSnapshots".
     */
    private Boolean supportsSnapshots;

    /**
     * Field "snapShots".
     */
    private SnapShot[] snapShots;

    /**
     * Field "guestInterface".
     */
    private String guestInterface;

    /**
     * Return the value of field "state".
     * 
     * @return The value
     */
    public String getState() {
        return this.state;
    }

    /**
     * Set the value of field "state".
     * 
     * @param state The value
     */
    public void setState(final String state) {
        this.state = state;
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
     * Return the value of field "bootable".
     * 
     * @return The value
     */
    public Boolean getBootable() {
        return this.bootable;
    }

    /**
     * Set the value of field "bootable".
     * 
     * @param bootable The value
     */
    public void setBootable(final Boolean bootable) {
        this.bootable = bootable;
    }

    /**
     * Return the value of field "supportsSnapshots".
     * 
     * @return The value
     */
    public Boolean getSupportsSnapshots() {
        return this.supportsSnapshots;
    }

    /**
     * Set the value of field "supportsSnapshots".
     * 
     * @param supportsSnapshots The value
     */
    public void setSupportsSnapshots(final Boolean supportsSnapshots) {
        this.supportsSnapshots = supportsSnapshots;
    }

    /**
     * Return the value of field "snapShots".
     * 
     * @return The value
     */
    public SnapShot[] getSnapShots() {
        return this.snapShots;
    }

    /**
     * Set the value of field "snapShots".
     * 
     * @param snapShots The value
     */
    public void setSnapShots(final SnapShot[] snapShots) {
        this.snapShots = snapShots;
    }

    /**
     * Return the value of field "guestInterface".
     * 
     * @return The value
     */
    public String getGuestInterface() {
        return this.guestInterface;
    }

    /**
     * Set the value of field "guestInterface".
     * 
     * @param guestInterface The value
     */
    public void setGuestInterface(final String guestInterface) {
        this.guestInterface = guestInterface;
    }

    /**
     * @return the href
     */
    @Override
    @XmlAttribute
    public String getHref() {
        return this.href;
    }

    /**
     * @param href the href to set
     */
    @Override
    public void setHref(final String href) {
        this.href = href;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiCommonId#hasValues()
     */
    @Override
    public boolean hasValues() {
        // TODO Auto-generated method stub
        return false;
    }

}
