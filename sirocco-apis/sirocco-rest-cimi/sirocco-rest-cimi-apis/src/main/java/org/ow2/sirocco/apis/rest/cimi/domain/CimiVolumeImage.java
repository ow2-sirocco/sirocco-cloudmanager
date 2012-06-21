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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupCreateByValue;

/**
 * Class VolumeImage.
 */
@XmlRootElement(name = "VolumeImage")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiVolumeImage extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "state".
     */
    private String state;

    /**
     * Field "imageLocation".
     */
    @Valid
    @NotNull(groups = {GroupCreateByValue.class})
    private ImageLocation imageLocation;

    /**
     * Field "bootable".
     */
    @NotNull(groups = {GroupCreateByValue.class})
    private Boolean bootable;

    /**
     * Default constructor.
     */
    public CimiVolumeImage() {
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
     * Return the value of field "imageLocation".
     * 
     * @return The value
     */
    public ImageLocation getImageLocation() {
        return this.imageLocation;
    }

    /**
     * Set the value of field "imageLocation".
     * 
     * @param imageLocation The value
     */
    public void setImageLocation(final ImageLocation imageLocation) {
        this.imageLocation = imageLocation;
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
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        has = has || (null != this.getBootable());
        has = has || (null != this.getImageLocation());
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
        return ExchangeType.VolumeImage;
    }
}
