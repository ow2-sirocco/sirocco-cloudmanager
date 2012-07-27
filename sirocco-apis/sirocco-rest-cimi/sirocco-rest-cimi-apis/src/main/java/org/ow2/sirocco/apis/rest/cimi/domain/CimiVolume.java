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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiVolumeVolumeImageCollection;

/**
 * Class Volume.
 */
@XmlRootElement(name = "Volume")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiVolume extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "state".
     * <p>
     * Read only
     * </p>
     */
    private String state;

    /**
     * Field "type".
     * <p>
     * Read only
     * </p>
     */
    private String type;

    /**
     * Field "capacity".
     */
    private Integer capacity;

    /**
     * Field "bootable".
     */
    private Boolean bootable;

    /**
     * Field "images".
     * <p>
     * Read only
     * </p>
     */
    private CimiVolumeVolumeImageCollection images;

    /**
     * Field "eventLog".
     * <p>
     * Read only
     * </p>
     */
    private CimiEventLog eventLog;

    /**
     * Default constructor.
     */
    public CimiVolume() {
        super();
    }

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
     * Return the value of field "images".
     * 
     * @return The value
     */
    public CimiVolumeVolumeImageCollection getImages() {
        return this.images;
    }

    /**
     * Set the value of field "images".
     * 
     * @param images The value
     */
    public void setImages(final CimiVolumeVolumeImageCollection images) {
        this.images = images;
    }

    /**
     * Return the value of field "eventLog".
     * 
     * @return The value
     */
    public CimiEventLog getEventLog() {
        return this.eventLog;
    }

    /**
     * Set the value of field "eventLog".
     * 
     * @param eventLog The value
     */
    public void setEventLog(final CimiEventLog eventLog) {
        this.eventLog = eventLog;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        has = has || (null != this.getBootable());
        has = has || (null != this.getCapacity());
        // Next read-only
        // has = has || (null != this.getImages());
        // has = has || (null != this.getEventLog());
        // has = has || (null != this.getState());
        // has = has || (null != this.getType());
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
        return ExchangeType.Volume;
    }

}
