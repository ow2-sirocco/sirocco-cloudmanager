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
 * Class NetworkPortConfiguration.
 */
@XmlRootElement(name = "NetworkPortConfiguration")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiNetworkPortConfiguration extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /** Field "portType". */
    private String portType;

    /** Field "classOfService". */
    private String classOfService;

    /**
     * Default constructor.
     */
    public CimiNetworkPortConfiguration() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiNetworkPortConfiguration(final String href) {
        super(href);
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
