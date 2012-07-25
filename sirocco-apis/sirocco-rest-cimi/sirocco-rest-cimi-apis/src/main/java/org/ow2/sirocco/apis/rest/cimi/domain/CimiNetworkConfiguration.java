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

/**
 * Class NetworkConfiguration.
 */
@XmlRootElement(name = "NetworkConfiguration")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiNetworkConfiguration extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /** Field "networkType". */
    private String networkType;

    /** Field "mtu". */
    private Integer mtu;

    /** Field "classOfService". */
    private String classOfService;

    /**
     * Default constructor.
     */
    public CimiNetworkConfiguration() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiNetworkConfiguration(final String href) {
        super(href);
    }

    /**
     * Return the value of field "networkType".
     * 
     * @return The value
     */
    public String getNetworkType() {
        return this.networkType;
    }

    /**
     * Set the value of field "networkType".
     * 
     * @param networkType The value
     */
    public void setNetworkType(final String networkType) {
        this.networkType = networkType;
    }

    /**
     * Return the value of field "mtu".
     * 
     * @return The value
     */
    public Integer getMtu() {
        return this.mtu;
    }

    /**
     * Set the value of field "mtu".
     * 
     * @param mtu The value
     */
    public void setMtu(final Integer mtu) {
        this.mtu = mtu;
    }

    /**
     * Return the value of field "classOfService".
     * 
     * @return The value
     */
    public String getClassOfService() {
        return this.classOfService;
    }

    /**
     * Set the value of field "classOfService".
     * 
     * @param classOfService The value
     */
    public void setClassOfService(final String classOfService) {
        this.classOfService = classOfService;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        // TODO Auto-generated method stub
        return false;
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
        // TODO Auto-generated method stub
        return null;
    }

}
