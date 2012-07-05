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

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupCreateByValue;
import org.ow2.sirocco.apis.rest.cimi.validator.ValidChild;

/**
 * Class AddressTemplate.
 */
@XmlRootElement(name = "AddressTemplate")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiAddressTemplate extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /** Field "ip". */
    private String ip;

    /** Field "hostname". */
    private String hostname;

    /** Field "allocation". */
    private String allocation;

    /** Field "defaultGateway". */
    private String defaultGateway;

    /** Field "dns". */
    private String dns;

    /** Field "protocol". */
    private String protocol;

    /** Field "mask". */
    private String mask;

    /** Field "network". */
    @ValidChild
    @NotNull(groups = GroupCreateByValue.class)
    private CimiNetwork network;

    /**
     * Default constructor.
     */
    public CimiAddressTemplate() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiAddressTemplate(final String href) {
        super(href);
    }

    /**
     * Return the value of field "ip".
     * 
     * @return The value
     */
    public String getIp() {
        return this.ip;
    }

    /**
     * Set the value of field "ip".
     * 
     * @param ip The value
     */
    public void setIp(final String ip) {
        this.ip = ip;
    }

    /**
     * Return the value of field "hostname".
     * 
     * @return The value
     */
    public String getHostname() {
        return this.hostname;
    }

    /**
     * Set the value of field "hostname".
     * 
     * @param hostname The value
     */
    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    /**
     * Return the value of field "allocation".
     * 
     * @return The value
     */
    public String getAllocation() {
        return this.allocation;
    }

    /**
     * Set the value of field "allocation".
     * 
     * @param allocation The value
     */
    public void setAllocation(final String allocation) {
        this.allocation = allocation;
    }

    /**
     * Return the value of field "defaultGateway".
     * 
     * @return The value
     */
    public String getDefaultGateway() {
        return this.defaultGateway;
    }

    /**
     * Set the value of field "defaultGateway".
     * 
     * @param defaultGateway The value
     */
    public void setDefaultGateway(final String defaultGateway) {
        this.defaultGateway = defaultGateway;
    }

    /**
     * Return the value of field "dns".
     * 
     * @return The value
     */
    public String getDns() {
        return this.dns;
    }

    /**
     * Set the value of field "dns".
     * 
     * @param dns The value
     */
    public void setDns(final String dns) {
        this.dns = dns;
    }

    /**
     * Return the value of field "protocol".
     * 
     * @return The value
     */
    public String getProtocol() {
        return this.protocol;
    }

    /**
     * Set the value of field "protocol".
     * 
     * @param protocol The value
     */
    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    /**
     * Return the value of field "mask".
     * 
     * @return The value
     */
    public String getMask() {
        return this.mask;
    }

    /**
     * Set the value of field "mask".
     * 
     * @param mask The value
     */
    public void setMask(final String mask) {
        this.mask = mask;
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
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        has = has || (null != this.getAllocation());
        has = has || (null != this.getDefaultGateway());
        has = has || (null != this.getDns());
        has = has || (null != this.getHostname());
        has = has || (null != this.getIp());
        has = has || (null != this.getMask());
        has = has || (null != this.getNetwork());
        has = has || (null != this.getProtocol());
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
        return ExchangeType.AddressTemplate;
    }

}
