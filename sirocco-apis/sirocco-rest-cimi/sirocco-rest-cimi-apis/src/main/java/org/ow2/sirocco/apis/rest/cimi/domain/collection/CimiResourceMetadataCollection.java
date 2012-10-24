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
package org.ow2.sirocco.apis.rest.cimi.domain.collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiArray;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiArrayAbstract;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiResourceMetadata;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;

/**
 * Collection of ResourceMetadata.
 */
@XmlRootElement
@XmlType(propOrder = {"id", "count", "array", "operations"})
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiResourceMetadataCollection extends CimiCollectionAbstract<CimiResourceMetadata> {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCollectionAbstract#getArray()
     */
    @Override
    @XmlElement(name = "ResourceMetadata")
    @JsonProperty(value = "resourceMetadatas")
    public CimiResourceMetadata[] getArray() {
        return super.getArray();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCollectionAbstract#setArray(E[])
     */
    @Override
    @JsonProperty(value = "resourceMetadatas")
    public void setArray(final CimiResourceMetadata[] items) {
        super.setArray(items);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCollection#newCollection()
     */
    @Override
    public CimiArray<CimiResourceMetadata> newCollection() {
        return new CimiResourceMetadatasArray();
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
        return ExchangeType.ResourceMetadataCollection;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCollection#getItemClass()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public Class<CimiResourceMetadata> getItemClass() {
        return CimiResourceMetadata.class;
    }

    /**
     * Concrete class of the collection.
     */
    public class CimiResourceMetadatasArray extends CimiArrayAbstract<CimiResourceMetadata> {

        /** Serial number */
        private static final long serialVersionUID = 1L;

        @Override
        public CimiResourceMetadata[] newEmptyArraySized() {
            return new CimiResourceMetadata[this.size()];
        }
    }
}
