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
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Class SystemNetworkPort.
 */
@XmlRootElement(name = "SystemNetworkPort")
@XmlType(propOrder = {"id", "name", "description", "created", "updated", "propertyArray", "networkPort", "operations"})
@JsonPropertyOrder({"resourceURI", "id", "name", "description", "created", "updated", "properties", "networkPort", "operations"})
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiSystemNetworkPort extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "networkPort".
     * <p>
     * Read only
     * </p>
     */
    private CimiNetworkPort networkPort;

    /**
     * Default constructor.
     */
    public CimiSystemNetworkPort() {
        super();
    }

    /**
     * Return the value of field "networkPort".
     * 
     * @return The value
     */
    public CimiNetworkPort getNetworkPort() {
        return this.networkPort;
    }

    /**
     * Set the value of field "networkPort".
     * 
     * @param networkPort The value
     */
    public void setNetworkPort(final CimiNetworkPort networkPort) {
        this.networkPort = networkPort;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        // Next read only
        // has = has || (null != this.getNetworkPort());
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
        return ExchangeType.SystemNetworkPort;
    }

}
