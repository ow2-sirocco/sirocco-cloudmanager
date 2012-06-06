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

import org.ow2.sirocco.apis.rest.cimi.domain.ResourceType;

public class Config {

    /** Mapper by entity types. */
    private Map<ResourceType, ItemConfig> byEntityTypes;

    /** Mapper by entity classes. */
    private Map<Class<?>, ItemConfig> byClasses;

    /**
     * Set the configuration for CimiEntities.
     * 
     * @param configs A list of ItemConfig
     */
    public void setItems(final List<ItemConfig> configs) {
        this.buildMappers(configs);
    }

    /**
     * Build configuration mapper.
     */
    protected void buildMappers(final List<ItemConfig> configs) {
        this.byEntityTypes = new HashMap<ResourceType, ItemConfig>();
        this.byClasses = new HashMap<Class<?>, ItemConfig>();

        for (ItemConfig item : configs) {
            if (null != item.getType()) {
                this.byEntityTypes.put(item.getType(), item);
            }
            this.byClasses.put(item.getKlass(), item);
        }
    }

    /**
     * Find the item associate to the given class.
     * 
     * @param classToFind The class to find
     * @return The item or null if class not found
     */
    public ItemConfig find(final Class<?> classToFind) {
        return this.byClasses.get(classToFind);
    }

    /**
     * Find the item associate to the given Entity type.
     * 
     * @param typeToFind The CimiEntityType to find
     * @return The item or null if type not found
     */
    public ItemConfig find(final ResourceType typeToFind) {
        return this.byEntityTypes.get(typeToFind);
    }
}