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

import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;
import org.ow2.sirocco.apis.rest.cimi.validator.constraints.ValidReference;

/**
 * Referenced entity who can be referenced by another entity.
 */
@ValidReference(groups = GroupWrite.class)
public interface CimiHref extends CimiData {

    /**
     * Get the entity reference.
     * 
     * @return The reference
     */
    String getHref();

    /**
     * Set the entity reference.
     * 
     * @param href The reference
     */
    void setHref(final String href);

    /**
     * Flag indicating whether the contents of the entity has a reference.
     * 
     * @return True if the entity has a reference
     */
    boolean hasReference();

    /**
     * Flag indicating whether the contents of the entity has values.
     * 
     * @return True if the entity has values
     */
    boolean hasValues();

    /**
     * Get the type of parameters passing.
     * 
     * @return The type of parameters passing
     */
    PassingType getPassingType();

}
