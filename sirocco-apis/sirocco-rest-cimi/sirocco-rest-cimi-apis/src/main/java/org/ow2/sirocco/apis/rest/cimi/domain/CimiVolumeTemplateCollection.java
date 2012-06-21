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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Collection of VolumeTemplate.
 */
@XmlRootElement(name = "Collection")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiVolumeTemplateCollection extends CimiCollectionAbstract<CimiVolumeTemplate> {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiCollectionAbstract#getArray()
     */
    @Override
    @XmlElement(name = "VolumeTemplate")
    @JsonProperty(value = "volumeTemplates")
    public CimiVolumeTemplate[] getArray() {
        return super.getArray();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiCollectionAbstract#setArray(E[])
     */
    @Override
    @JsonProperty(value = "volumeTemplates")
    public void setArray(final CimiVolumeTemplate[] items) {
        super.setArray(items);
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
        return ExchangeType.VolumeTemplateCollection;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiCollection#newCollection()
     */
    @Override
    public CimiArray<CimiVolumeTemplate> newCollection() {
        return new CimiVolumeTemplateArray();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiCollection#getItemClass()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public Class<CimiVolumeTemplate> getItemClass() {
        return CimiVolumeTemplate.class;
    }

    /**
     * Concrete class of the collection.
     */
    public class CimiVolumeTemplateArray extends CimiArrayAbstract<CimiVolumeTemplate> {

        /** Serial number */
        private static final long serialVersionUID = 1L;

        @Override
        public CimiVolumeTemplate[] newEmptyArraySized() {
            return new CimiVolumeTemplate[this.size()];
        }
    }
}
