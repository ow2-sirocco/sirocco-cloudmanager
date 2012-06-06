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

/**
 * Interface of a generic array based on {@link List}.
 */
public interface CimiArray<E> extends List<E> {

    /**
     * Set a array.
     * 
     * @param items The array to set
     */
    void setArray(E[] items);

    /**
     * Get a array.
     * 
     * @return The current array
     */
    E[] getArray();

    /**
     * Create a new instance of empty array with the size of list.
     * 
     * @return
     * @see List#toArray(Object[])
     */
    E[] newEmptyArraySized();

}
