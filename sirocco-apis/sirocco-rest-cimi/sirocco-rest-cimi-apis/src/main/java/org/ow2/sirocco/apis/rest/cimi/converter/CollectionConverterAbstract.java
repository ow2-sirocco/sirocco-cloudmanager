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
package org.ow2.sirocco.apis.rest.cimi.converter;

import java.util.Collection;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiArray;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiResource;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.Resource;

/**
 * Abstract class to convert the collection of the CIMI model and the service
 * model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiCollection<E>}</li>
 * <li>Service model: {@link Resource}</li>
 * </ul>
 * </p>
 */
public abstract class CollectionConverterAbstract implements CimiConverter {

    /**
     * Fill the common data from a service collection to a CIMI collection.
     * 
     * @param context The current context
     * @param dataService Source service collection
     * @param dataCimi Destination CIMI collection
     */
    protected <E extends CimiResource> void fill(final CimiContext context, final Resource dataService,
        final CimiCollection<E> dataCimi) {
        if (true == context.mustBeExpanded(dataCimi)) {
            dataCimi.setResourceURI(dataCimi.getExchangeType().getResourceURI());
            if (null == dataService.getId()) {
                dataCimi.setId(context.makeHrefBase(dataCimi));
            } else {
                dataCimi.setId(context.makeHref(dataCimi, dataService.getId().toString()));
            }
        }
        if (true == context.mustBeReferenced(dataCimi)) {
            if (null == dataService.getId()) {
                dataCimi.setHref(context.makeHrefBase(dataCimi));
            } else {
                dataCimi.setHref(context.makeHref(dataCimi, dataService.getId().toString()));
            }
        }
    }

    /**
     * Fill the common data from a CIMI collection to a service collection.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI collection
     * @param dataService Destination service collection
     */
    protected <E extends CimiResource> void fill(final CimiContext context, final CimiCollection<E> dataCimi,
        final Resource dataService) {
        if (null != dataCimi.getId()) {
            dataService.setId(HrefHelper.extractId(dataCimi.getId()));
        }
    }

    /**
     * Get the child collection of the resource collection of service.
     * 
     * @param resourceCollection The resource collection of service
     * @return The child collection
     */
    protected abstract Collection<?> getChildCollection(final Resource resourceCollection);

    /**
     * Create a new child collection and set it in the resource collection of
     * service.
     * 
     * @param resourceCollection The resource collection of service
     */
    protected abstract void setNewChildCollection(final Resource resourceCollection);

    /**
     * Add the item in the child collection of the resource collection of
     * service.
     * 
     * @param resourceCollection The resource collection of service
     * @param item The item to add
     */
    protected abstract void addItemChildCollection(final Resource resourceCollection, final Object item);

    /**
     * Copy data from a service collection to a CIMI collection.
     * 
     * @param context The current context
     * @param dataService Source service collection
     * @param dataCimi Destination CIMI collection
     */
    @SuppressWarnings("unchecked")
    protected <E extends CimiResource> void doCopyToCimi(final CimiContext context, final Resource collectionService,
        final CimiCollection<E> collectionCimi) {
        this.fill(context, collectionService, collectionCimi);
        if (true == context.mustBeExpanded(collectionCimi)) {
            Collection<?> serviceList = this.getChildCollection(collectionService);
            if ((null != serviceList) && (serviceList.size() > 0)) {
                CimiArray<E> cimiList = collectionCimi.newCollection();
                for (Object serviceItem : serviceList) {
                    cimiList.add((E) context.convertNextCimi(serviceItem, collectionCimi.getItemClass()));
                }
                collectionCimi.setCollection(cimiList);
            }
        }
    }

    /**
     * Copy data from a CIMI collection to a service collection.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI collection
     * @param dataService Destination Service collection
     */
    protected <E extends CimiResource> void doCopyToService(final CimiContext context, final CimiCollection<E> collectionCimi,
        final Resource collectionService) {
        CimiArray<E> cimiList = collectionCimi.getCollection();
        if ((null != cimiList) && (cimiList.size() > 0)) {
            this.setNewChildCollection(collectionService);
            for (E cimiItem : cimiList) {
                this.addItemChildCollection(collectionService, context.convertNextService(cimiItem));
            }
        }
    }
}
