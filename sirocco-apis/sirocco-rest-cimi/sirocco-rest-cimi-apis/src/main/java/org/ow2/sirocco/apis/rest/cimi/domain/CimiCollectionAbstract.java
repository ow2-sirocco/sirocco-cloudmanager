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
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Abstract class of a CIMI Collection.
 */
public abstract class CimiCollectionAbstract<E> extends CimiResourceAbstract implements CimiCollection<E> {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * The items of the collection
     */
    private CimiArray<E> items;

    /**
     * Returns the size of the collection.
     * 
     * @return The collection size
     */
    @XmlElement(name = "count")
    @JsonProperty(value = "count")
    public Integer getCount() {
        Integer count = null;
        if (null != this.items) {
            count = this.items.size();
        }
        return count;
    }

    /**
     * Normally, set the size of the collection, but this method does nothing.
     * It exists only for the JSON serializer.
     * 
     * @param count The collection size
     */
    public void setCount(final Integer count) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiCollection#getCollection()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public CimiArray<E> getCollection() {
        return this.items;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiCollection#setCollection(org.ow2.sirocco.apis.rest.cimi.domain.CimiArray)
     */
    @Override
    public void setCollection(final CimiArray<E> items) {
        this.items = items;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiCollection#add(java.lang.Object)
     */
    @Override
    public void add(final E item) {
        if (null == this.items) {
            this.items = this.newCollection();
        }
        this.items.add(item);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiCollection#getArray()
     */
    @Override
    @XmlTransient
    public E[] getArray() {
        E[] retItems = null;
        if (null != this.items) {
            retItems = this.items.getArray();
        }
        return retItems;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiCollection#setArray(E[])
     */
    @Override
    public void setArray(final E[] items) {
        if (null == items) {
            this.items = null;
        } else {
            this.items = this.newCollection();
            this.items.setArray(items);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiResource#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean ret = false;
        if (null != this.items) {
            ret = (false == this.items.isEmpty());
        }
        return ret;
    }
}
