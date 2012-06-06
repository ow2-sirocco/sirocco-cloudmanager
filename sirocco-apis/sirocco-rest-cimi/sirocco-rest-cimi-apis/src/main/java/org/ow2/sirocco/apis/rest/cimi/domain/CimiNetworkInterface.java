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

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Class NetworkInterface.
 * <p>
 */
@XmlRootElement(name = "NetworkInterface")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiNetworkInterface extends CimiObjectCommonImpl {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "hostname".
     */
    private String hostname;

    /**
     * Field "macAddress".
     */
    private String macAddress;

    /**
     * Field "state".
     */
    private String state;

    /**
     * Field "protocol".
     */
    private String protocol;

    /**
     * Field "allocation".
     */
    private String allocation;

    /**
     * Field "address".
     */
    private String address;

    /**
     * Field "defaultGateway".
     */
    private String defaultGateway;

    /**
     * Field "dns".
     */
    private String dns;

    /**
     * Field "macTransmissionUnit".
     */
    private Integer macTransmissionUnit;

    /**
     * Default constructor.
     */
    public CimiNetworkInterface() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiNetworkInterface(final String href) {
        super(href);
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
     * Return the value of field "address".
     * 
     * @return The value
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Set the value of field "address".
     * 
     * @param address The value
     */
    public void setAddress(final String address) {
        this.address = address;
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
     * Return the value of field "macTransmissionUnit".
     * 
     * @return The value
     */
    public Integer getMacTransmissionUnit() {
        return this.macTransmissionUnit;
    }

    /**
     * Set the value of field "macTransmissionUnit".
     * 
     * @param macTransmissionUnit The value
     */
    public void setMacTransmissionUnit(final Integer macTransmissionUnit) {
        this.macTransmissionUnit = macTransmissionUnit;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonImpl#hasValues()
     */
    @Override
    public boolean hasValues() {
        // TODO Auto-generated method stub
        return false;
    }

}
