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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiResource;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.Resource;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiObjectCommon}</li>
 * <li>Service model: {@link Resource}</li>
 * </ul>
 * </p>
 */
public class CollectionConverter implements ResourceConverter {

    /**
     * Fill the common data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected <E extends CimiResource> void fill(final CimiContext context, final Resource dataService,
        final CimiCollection<E> dataCimi) {
        if (true == context.mustBeExpanded(dataCimi)) {
            // if (null != dataService.getId()) {
            dataCimi.setId(context.makeHref(dataCimi, dataService.getId()));
            // }
        }
        if (true == context.mustBeReferenced(dataCimi)) {
            dataCimi.setHref(context.makeHref(dataCimi, dataService.getId()));
        }
    }

    /**
     * Fill the common data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination service object
     */
    protected <E extends CimiResource> void fill(final CimiContext context, final CimiCollection<E> dataCimi,
        final Resource dataService) {
        if (null != dataCimi.getId()) {
            dataService.setId(CollectionConverter.extractId(dataCimi.getId()));
        }
    }

    /**
     * Extract the ID service of the HREF.
     * 
     * @param href The HREF
     * @return The ID service
     */
    public static String extractIdString(final String href) {
        String id = null;
        int posId = href.lastIndexOf('/');
        id = href.substring(posId + 1);
        return id;
    }

    /**
     * Extract the ID service of the HREF.
     * 
     * @param href The HREF
     * @return The ID service
     */
    public static Integer extractId(final String href) {
        return Integer.valueOf(CollectionConverter.extractIdString(href));
    }

    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void copyToCimi(final CimiContext context, final Object dataService, final Object dataCimi) {
        // TODO Auto-generated method stub

    }

    @Override
    public void copyToService(final CimiContext context, final Object dataCimi, final Object dataService) {
        // TODO Auto-generated method stub

    }
}