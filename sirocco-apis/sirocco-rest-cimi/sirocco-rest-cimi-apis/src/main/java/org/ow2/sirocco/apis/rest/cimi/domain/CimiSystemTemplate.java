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
 * Class SystemTemplate.
 */
@XmlRootElement(name = "SystemTemplate")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiSystemTemplate extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "componentDescriptors".
     */
    @ValidChild
    @NotEmptyIfNotNull(groups = {GroupWrite.class})
    private CimiComponentDescriptorArray componentDescriptors;

    /**
     * Default constructor.
     */
    public CimiSystemTemplate() {
        super();
    }

    /**
     * Return the value of field "componentDescriptors".
     * 
     * @return The value
     */
    @XmlTransient
    @JsonIgnore
    public List<CimiComponentDescriptor> getListComponentDescriptors() {
        return this.componentDescriptors;
    }

    /**
     * Set the value of field "componentDescriptors".
     * 
     * @param componentDescriptors The value
     */
    public void setListComponentDescriptors(final List<CimiComponentDescriptor> componentDescriptors) {
        if (null == componentDescriptors) {
            this.componentDescriptors = null;
        } else {
            this.componentDescriptors = new CimiComponentDescriptorArray();
            this.componentDescriptors.addAll(componentDescriptors);
        }
    }

    /**
     * Add a list of "componentDescriptors".
     * 
     * @param toAddComponents The list to add
     */
    public void addAllComponentDescriptors(final List<CimiComponentDescriptor> toAddComponents) {
        if (null == this.componentDescriptors) {
            this.componentDescriptors = new CimiComponentDescriptorArray();
        }
        this.componentDescriptors.addAll(toAddComponents);
    }

    /**
     * Return the value of field "componentDescriptors".
     * 
     * @return The value
     */
    @XmlElement(name = "componentDescriptor")
    @JsonProperty(value = "componentDescriptors")
    public CimiComponentDescriptor[] getComponentDescriptors() {
        CimiComponentDescriptor[] items = null;
        if (null != this.componentDescriptors) {
            items = this.componentDescriptors.getArray();
        }
        return items;
    }

    /**
     * Set the value of field "componentDescriptors".
     * 
     * @param componentDescriptors The value
     */
    public void setComponentDescriptors(final CimiComponentDescriptor[] componentDescriptors) {
        if (null == componentDescriptors) {
            this.componentDescriptors = null;
        } else {
            this.componentDescriptors = new CimiComponentDescriptorArray();
            this.componentDescriptors.setArray(componentDescriptors);
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
        has = has || (null != this.getComponentDescriptors());
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
        return ExchangeType.SystemTemplate;
    }

    /**
     * Concrete class of the collection.
     */
    public class CimiComponentDescriptorArray extends CimiArrayAbstract<CimiComponentDescriptor> {

        /** Serial number */
        private static final long serialVersionUID = 1L;

        /**
         * {@inheritDoc}
         * 
         * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiArray#newEmptyArraySized()
         */
        @Override
        public CimiComponentDescriptor[] newEmptyArraySized() {
            return new CimiComponentDescriptor[this.size()];
        }
    }

}
