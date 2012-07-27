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
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;
import org.ow2.sirocco.apis.rest.cimi.validator.ValidChild;
import org.ow2.sirocco.apis.rest.cimi.validator.constraints.NotEmptyIfNotNull;

/**
 * Class ForwardingGroupTemplate.
 */
@XmlRootElement(name = "ForwardingGroupTemplate")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiForwardingGroupTemplate extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /** Field "networks". */
    @ValidChild
    @NotEmptyIfNotNull(groups = {GroupWrite.class})
    private CimiForwardingGroupTemplateNetworkArray networks;

    /**
     * Default constructor.
     */
    public CimiForwardingGroupTemplate() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiForwardingGroupTemplate(final String href) {
        super(href);
    }

    /**
     * Return the value of field "networks".
     * 
     * @return The value
     */
    @XmlElement(name = "network")
    @JsonProperty(value = "networks")
    public CimiNetwork[] getNetworks() {
        CimiNetwork[] items = null;
        if (null != this.networks) {
            items = this.networks.getArray();
        }
        return items;
    }

    /**
     * Set the value of field "networks".
     * 
     * @param networks The value
     */
    public void setNetworks(final CimiNetwork[] networks) {
        if (null == networks) {
            this.networks = null;
        } else {
            this.networks = new CimiForwardingGroupTemplateNetworkArray();
            this.networks.setArray(networks);
        }
    }

    /**
     * Return the value of field "networks".
     * 
     * @return The value
     */
    @XmlTransient
    @JsonIgnore
    public List<CimiNetwork> getListNetworks() {
        return this.networks;
    }

    /**
     * Set the value of field "networks".
     * 
     * @param networks The value
     */
    public void setListNetworks(final List<CimiNetwork> networks) {
        if (null == networks) {
            this.networks = null;
        } else {
            this.networks = new CimiForwardingGroupTemplateNetworkArray();
            this.networks.addAll(networks);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        has = has || (null != this.getNetworks());
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
        return ExchangeType.ForwardingGroupTemplate;
    }

    /**
     * Concrete class of the collection.
     */
    public class CimiForwardingGroupTemplateNetworkArray extends CimiArrayAbstract<CimiNetwork> {

        /** Serial number */
        private static final long serialVersionUID = 1L;

        /**
         * {@inheritDoc}
         * 
         * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiArray#newEmptyArraySized()
         */
        @Override
        public CimiNetwork[] newEmptyArraySized() {
            return new CimiNetwork[this.size()];
        }
    }
}
