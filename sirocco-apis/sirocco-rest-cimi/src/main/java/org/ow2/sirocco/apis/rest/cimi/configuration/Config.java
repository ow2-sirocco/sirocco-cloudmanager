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
package org.ow2.sirocco.apis.rest.cimi.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ow2.sirocco.apis.rest.cimi.utils.CimiEntityType;

public class Config {

    /** Mapper by entity types. */
    private Map<CimiEntityType, ItemConfig> byCimiEntityTypes;

    /** Mapper by entity classes. */
    private Map<Class<?>, ItemConfig> byCimiEntityClasses;

    /**
     * Set the configuration for CimiEntities.
     * 
     * @param configs A list of ItemConfig
     */
    public void setCimiEntityItems(final List<ItemConfig> configs) {
        this.buildCimiEntityMappers(configs);
    }

    /**
     * Build CimiEntity configuration mapper.
     */
    protected void buildCimiEntityMappers(final List<ItemConfig> configs) {
        this.byCimiEntityTypes = new HashMap<CimiEntityType, ItemConfig>();
        this.byCimiEntityClasses = new HashMap<Class<?>, ItemConfig>();

        for (ItemConfig item : configs) {
            this.byCimiEntityTypes.put(item.getType(), item);
            this.byCimiEntityClasses.put(item.getKlass(), item);
        }
    }

    /**
     * Find the item associate to the given class.
     * 
     * @param classToFind The class to find
     * @return The item or null if class not found
     */
    public ItemConfig find(final Class<?> classToFind) {
        return this.byCimiEntityClasses.get(classToFind);
    }

    /**
     * Find the item associate to the given Entity type.
     * 
     * @param typeToFind The CimiEntityType to find
     * @return The item or null if type not found
     */
    public ItemConfig find(final CimiEntityType typeToFind) {
        return this.byCimiEntityTypes.get(typeToFind);
    }
}