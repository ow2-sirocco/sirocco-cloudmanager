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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.validator.ValidChild;

/**
 * Class MachineTemplateNetworkInterface.
 */
@XmlRootElement(name = "MachineTemplateNetworkInterface")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiMachineTemplateNetworkInterface implements CimiData {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "addresses".
     */
    private CimiAddressArray addresses;

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
     * Field "networkType".
     */
    private String networkType;

    /**
     * Field "mtu".
     */
    private Integer mtu;

    /**
     * Default constructor.
     */
    public CimiMachineTemplateNetworkInterface() {
        super();
    }

    /**
     * Return the value of field "addresses".
     * 
     * @return The value
     */
    @XmlElement(name = "address")
    @JsonProperty(value = "addresses")
    public CimiAddress[] getAddresses() {
        CimiAddress[] items = null;
        if (null != this.addresses) {
            items = this.addresses.getArray();
        }
        return items;
    }

    /**
     * Set the value of field "addresses".
     * 
     * @param addresses The value
     */
    public void setAddresses(final CimiAddress[] addresses) {
        if (null == addresses) {
            this.addresses = null;
        } else {
            this.addresses = new CimiAddressArray();
            this.addresses.setArray(addresses);
        }
    }

    /**
     * Return the value of field "addresses".
     * 
     * @return The value
     */
    @XmlTransient
    @JsonIgnore
    public List<CimiAddress> getListAddresses() {
        return this.addresses;
    }

    /**
     * Set the value of field "addresses".
     * 
     * @param addresses The value
     */
    public void setListAddresses(final List<CimiAddress> addresses) {
        if (null == addresses) {
            this.addresses = null;
        } else {
            this.addresses = new CimiAddressArray();
            this.addresses.addAll(addresses);
        }
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
     * Concrete class of the collection.
     */
    public class CimiAddressArray extends CimiArrayAbstract<CimiAddress> {

        /** Serial number */
        private static final long serialVersionUID = 1L;

        /**
         * {@inheritDoc}
         * 
         * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiArray#newEmptyArraySized()
         */
        @Override
        public CimiAddress[] newEmptyArraySized() {
            return new CimiAddress[this.size()];
        }
    }
}
