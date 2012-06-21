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
import org.ow2.sirocco.apis.rest.cimi.validator.ValidChild;

/**
 * Class MachineVolume.
 */
@XmlRootElement(name = "MachineVolume")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiMachineVolume extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "initialLocation".
     */
    private String initialLocation;

    /**
     * Field "volume".
     */
    @ValidChild
    private CimiVolume volume;

    /**
     * Default constructor.
     */
    public CimiMachineVolume() {
        super();
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

    /**
     * Return the value of field "volume".
     * 
     * @return The value
     */
    public CimiVolume getVolume() {
        return this.volume;
    }

    /**
     * Set the value of field "volume".
     * 
     * @param volume The value
     */
    public void setVolume(final CimiVolume volume) {
        this.volume = volume;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        has = has || (null != this.getVolume());
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
        return ExchangeType.MachineVolume;
    }

}
