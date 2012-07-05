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
 * Class MachineNetworkInterface.
 */
@XmlRootElement(name = "MachineNetworkInterface")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiMachineNetworkInterface extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "addresses".
     */
    private CimiMachineNetworkInterfaceAddressCollection addresses;

    /**
     * Field "network".
     */
    @ValidChild
    private CimiNetwork network;

    /**
     * Field "networkPort".
     */
    // TODO networkPort
    // @ValidChild
    // private CimiNetworkPort networkPort;

    /**
     * Field "state".
     */
    private String state;

    /**
     * Field "macAddress".
     */
    private String macAddress;

    /**
     * Field "mtu".
     */
    private Integer mtu;

    /**
     * Default constructor.
     */
    public CimiMachineNetworkInterface() {
        super();
    }

    /**
     * Return the value of field "addresses".
     * 
     * @return The value
     */
    public CimiMachineNetworkInterfaceAddressCollection getAddresses() {
        return this.addresses;
    }

    /**
     * Set the value of field "addresses".
     * 
     * @param addresses The value
     */
    public void setAddresses(final CimiMachineNetworkInterfaceAddressCollection addresses) {
        this.addresses = addresses;
    }

    /**
     * Return the value of field "network".
     * 
     * @return The value
     */
    public CimiNetwork getNetwork() {
        return this.network;
    }

    /**
     * Set the value of field "network".
     * 
     * @param network The value
     */
    public void setNetwork(final CimiNetwork network) {
        this.network = network;
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
     * Return the value of field "macAddress".
     * 
     * @return The value
     */
    public String getMacAddress() {
        return this.macAddress;
    }

    /**
     * Set the value of field "macAddress".
     * 
     * @param macAddress The value
     */
    public void setMacAddress(final String macAddress) {
        this.macAddress = macAddress;
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
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        // Next read-only
        // has = has || (null != this.getAddresses());
        has = has || (null != this.getMtu());
        has = has || (null != this.getMacAddress());
        has = has || (null != this.getNetwork());
        // TODO NetworkPort
        // has = has || (null != this.getNetworkPort());
        has = has || (null != this.getState());
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
        return ExchangeType.MachineNetworkInterface;
    }
}
