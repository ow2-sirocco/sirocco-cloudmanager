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
 * Class NetworkTemplate.
 */
@XmlRootElement(name = "NetworkTemplate")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiNetworkTemplate extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /** Field "networkConfig". */
    private CimiNetworkConfiguration networkConfig;

    /** Field "forwardingGroup". */
    private CimiForwardingGroup forwardingGroup;

    /** Field "eventLogTemplate". */
    private CimiEventLogTemplate eventLogTemplate;

    /**
     * Default constructor.
     */
    public CimiNetworkTemplate() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiNetworkTemplate(final String href) {
        super(href);
    }

    /**
     * Return the value of field "networkConfig".
     * 
     * @return The value
     */
    public CimiNetworkConfiguration getNetworkConfig() {
        return this.networkConfig;
    }

    /**
     * Set the value of field "networkConfig".
     * 
     * @param networkConfig The value
     */
    public void setNetworkConfig(final CimiNetworkConfiguration networkConfig) {
        this.networkConfig = networkConfig;
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
     * Return the value of field "eventLogTemplate".
     * 
     * @return The value
     */
    public CimiEventLogTemplate getEventLogTemplate() {
        return this.eventLogTemplate;
    }

    /**
     * Set the value of field "eventLogTemplate".
     * 
     * @param eventLogTemplate The value
     */
    public void setEventLogTemplate(final CimiEventLogTemplate eventLogTemplate) {
        this.eventLogTemplate = eventLogTemplate;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        has = has || (null != this.getEventLogTemplate());
        has = has || (null != this.getForwardingGroup());
        has = has || (null != this.getNetworkConfig());
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
        return ExchangeType.NetworkTemplate;
    }

}
