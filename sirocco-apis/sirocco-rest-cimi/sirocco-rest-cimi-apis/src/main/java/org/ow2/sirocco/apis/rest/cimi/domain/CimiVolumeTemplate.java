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
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;
import org.ow2.sirocco.apis.rest.cimi.validator.ValidChild;
import org.ow2.sirocco.apis.rest.cimi.validator.constraints.NotEmptyIfNotNull;

/**
 * Class VolumeTemplate.
 */
@XmlRootElement(name = "VolumeTemplate")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiVolumeTemplate extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "volumeConfig".
     */
    @ValidChild
    @NotEmptyIfNotNull(groups = {GroupWrite.class})
    private CimiVolumeConfiguration volumeConfig;

    /**
     * Field "volumeImage".
     */
    @ValidChild
    @NotEmptyIfNotNull(groups = {GroupWrite.class})
    private CimiVolumeImage volumeImage;

    /**
     * Default constructor.
     */
    public CimiVolumeTemplate() {
        super();
    }

    /**
     * Return the value of field "volumeConfig".
     * 
     * @return The value
     */
    public CimiVolumeConfiguration getVolumeConfig() {
        return this.volumeConfig;
    }

    /**
     * Set the value of field "volumeConfig".
     * 
     * @param volumeConfig The value
     */
    public void setVolumeConfig(final CimiVolumeConfiguration volumeConfig) {
        this.volumeConfig = volumeConfig;
    }

    /**
     * Return the value of field "volumeImage".
     * 
     * @return The value
     */
    public CimiVolumeImage getVolumeImage() {
        return this.volumeImage;
    }

    /**
     * Set the value of field "volumeImage".
     * 
     * @param volumeImage The value
     */
    public void setVolumeImage(final CimiVolumeImage volumeImage) {
        this.volumeImage = volumeImage;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        has = has || (null != this.getVolumeConfig());
        has = has || (null != this.getVolumeImage());
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
        return ExchangeType.VolumeTemplate;
    }

}
