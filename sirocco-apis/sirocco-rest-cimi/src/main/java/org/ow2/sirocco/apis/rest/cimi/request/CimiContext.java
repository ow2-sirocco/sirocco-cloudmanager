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
package org.ow2.sirocco.apis.rest.cimi.request;

import org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter;
import org.ow2.sirocco.apis.rest.cimi.converter.EntityConverter;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiData;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiEntityType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiHref;

/**
 *
 */
public interface CimiContext {

    /**
     * Get the entity type for a instance.
     * 
     * @param data The entity
     * @return The entity type
     */
    CimiEntityType getType(CimiData data);

    /**
     * Get the converter for the entity root to convert.
     * <p>
     * The given type is saved in context and can be got with
     * {@link #getCurrentRootConverting()}.
     * </p>
     * 
     * @param type The type of entity to convert
     * @return The converter
     */
    CimiConverter getRootConverter(CimiEntityType type);

    /**
     * Get the converter for a class.
     * 
     * @param klass The class to convert
     * @return The converter
     */
    CimiConverter getConverter(Class<?> klass);

    /**
     * Get the entity converter for a class.
     * 
     * @param klass The class to convert
     * @return The entity converter
     */
    EntityConverter getEntityConverter(Class<?> klass);

    /**
     * Get the current root converting setted by
     * {@link #getRootConverter(CimiEntityType)}.
     * 
     * @return The current root type converting
     */
    CimiEntityType getCurrentRootConverting();

    /**
     * Get the current request.
     * 
     * @return The current request
     */
    CimiRequest getRequest();

    /**
     * Returns true if the given entity must be expanded.
     * 
     * @param entity The instance of a CIMI entity
     * @return True if must be expanded.
     */
    boolean mustBeExpanded(CimiData entity);

    /**
     * Returns true if the given entity must be referenced.
     * 
     * @param entity The instance of a CIMI entity
     * @return True if must be referenced.
     */
    boolean mustBeReferenced(CimiData data);

    /**
     * Returns true if the given entity must have an identifier in its
     * reference.
     * 
     * @param entity The instance of a CIMI entity
     * @return True if must have a ID.
     */
    boolean mustHaveIdInReference(final CimiHref entity);

    /**
     * Make the base HREF without ID.
     * 
     * @param data
     * @return
     */
    String makeHrefBase(final CimiHref data);

    /**
     * Make a HREF.
     * 
     * @param entity The instance of entity
     * @param id Service ID
     * @return The HREF made
     */
    String makeHref(final CimiHref entity, final String id);

    /**
     * Make a HREF.
     * 
     * @param entity The instance of entity
     * @param id Service ID
     * @return The HREF made
     */
    String makeHref(final CimiHref entity, final Integer id);

}