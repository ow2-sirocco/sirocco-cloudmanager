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
 * Class NetworkPort.
 */
@XmlRootElement(name = "NetworkPort")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiNetworkPort extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /** Field "state". */
    private String state;

    /** Field "network". */
    private CimiNetwork network;

    /** Field "portType". */
    private String portType;

    /** Field "classOfService". */
    private String classOfService;

    /** Field "eventLog". */
    private CimiEventLog eventLog;

    /**
     * Default constructor.
     */
    public CimiNetworkPort() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiNetworkPort(final String href) {
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
     * Return the value of field "portType".
     * 
     * @return The value
     */
    public String getPortType() {
        return this.portType;
    }

    /**
     * Set the value of field "portType".
     * 
     * @param portType The value
     */
    public void setPortType(final String portType) {
        this.portType = portType;
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
