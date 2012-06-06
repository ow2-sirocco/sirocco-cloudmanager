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

/**
 * Interface of a CIMI Collection.
 */
public interface CimiCollection<E> extends CimiResource {

    /**
     * Get the real collection.
     * 
     * @return The collection
     */
    CimiArray<E> getCollection();

    /**
     * Set the real collection.
     * 
     * @param items The collection
     */
    void setCollection(CimiArray<E> items);

    /**
     * Create a new collection of typed elements.
     * 
     * @return The new collection
     */
    CimiArray<E> newCollection();

    /**
     * Add a item in collection.
     * 
     * @param item The item to add
     */
    void add(E item);

    /**
     * Get the items array.
     * 
     * @return The array
     */
    E[] getArray();

    /**
     * Set the items array.
     * 
     * @param items
     */
    void setArray(E[] items);
}
