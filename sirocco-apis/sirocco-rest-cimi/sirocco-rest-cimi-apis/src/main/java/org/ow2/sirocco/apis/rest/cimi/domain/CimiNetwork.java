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
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiNetworkNetworkPortCollection;

/**
 * Class Network.
 */
@XmlRootElement(name = "Network")
@XmlType(propOrder = {"id", "name", "description", "created", "updated", "propertyArray", "state", "networkType", "mtu",
    "classOfService", "networkPorts", "forwardingGroup", "eventLog", "operations"})
@JsonPropertyOrder({"resourceURI", "id", "name", "description", "created", "updated", "properties", "state", "networkType",
    "mtu", "classOfService", "networkPorts", "forwardingGroup", "eventLog", "operations"})
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiNetwork extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "state".
     * <p>
     * Read only
     * </p>
     */
    private String state;

    /** Field "networkType". */
    private String networkType;

    /** Field "mtu". */
    private Integer mtu;

    /** Field "classOfService". */
    private String classOfService;

    /**
     * Field "networkPorts".
     * <p>
     * Read only
     * </p>
     */
    private CimiNetworkNetworkPortCollection networkPorts;

    /**
     * Field "forwardingGroup".
     * <p>
     * Read only
     * </p>
     */
    private CimiForwardingGroup forwardingGroup;

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
    public CimiNetwork() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiNetwork(final String href) {
        super(href);
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
     * Return the value of field "networkPorts".
     * 
     * @return The value
     */
    public CimiNetworkNetworkPortCollection getNetworkPorts() {
        return this.networkPorts;
    }

    /**
     * Set the value of field "networkPorts".
     * 
     * @param networkPorts The value
     */
    public void setNetworkPorts(final CimiNetworkNetworkPortCollection networkPorts) {
        this.networkPorts = networkPorts;
    }

    /**
     * Return the value of field "forwardingGroup".
     * 
     * @return The value
     */
    public CimiForwardingGroup getForwardingGroup() {
        return this.forwardingGroup;
    }

    /**
     * Set the value of field "forwardingGroup".
     * 
     * @param forwardingGroup The value
     */
    public void setForwardingGroup(final CimiForwardingGroup forwardingGroup) {
        this.forwardingGroup = forwardingGroup;
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
        has = has || (null != this.getClassOfService());
        has = has || (null != this.getMtu());
        has = has || (null != this.getNetworkType());
        // Next read-only
        // has = has || (null != this.getForwardingGroup());
        // has = has || (null != this.getEventLog());
        // has = has || (null != this.getNetworkPorts());
        // has = has || (null != this.getState());
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
        return ExchangeType.Network;
    }

}
