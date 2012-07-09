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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiResource;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Identifiable;
import org.ow2.sirocco.cloudmanager.model.cimi.Resource;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiObjectCommon}</li>
 * <li>Service model: {@link Resource} or {@link Identifiable}</li>
 * </ul>
 * </p>
 */
public abstract class ObjectCommonConverter extends CommonConverter implements CimiConverter {

    /**
     * Fill the identity data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void fill(final CimiContext context, final Identifiable dataService, final CimiObjectCommon dataCimi) {
        if (true == context.mustBeExpanded(dataCimi)) {
            dataCimi.setResourceURI(dataCimi.getExchangeType().getResourceURI());
            if (null != dataService.getId()) {
                dataCimi.setId(context.makeHref(dataCimi, dataService.getId().toString()));
            }
        }
        if (true == context.mustBeReferenced(dataCimi)) {
            dataCimi.setHref(context.makeHref(dataCimi, dataService.getId().toString()));
        }
    }

    /**
     * Fill the identity data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination service object
     */
    protected void fill(final CimiContext context, final CimiObjectCommon dataCimi, final Identifiable dataService) {
        if (null != dataCimi.getId()) {
            dataService.setId(PathHelper.extractId(dataCimi.getId()));
        }
    }

    /**
     * Fill the common data from a service object to a CIMI object.
     * 
     * @param context The current context
     * @param dataService Source service object
     * @param dataCimi Destination CIMI object
     */
    protected void fill(final CimiContext context, final Resource dataService, final CimiObjectCommon dataCimi) {
        if (true == context.mustBeExpanded(dataCimi)) {
            this.fill(dataService, dataCimi);
            dataCimi.setResourceURI(dataCimi.getExchangeType().getResourceURI());
            dataCimi.setCreated(dataService.getCreated());
            dataCimi.setUpdated(dataService.getUpdated());
            if (null != dataService.getId()) {
                dataCimi.setId(context.makeHref(dataCimi, dataService.getId().toString()));
            }
        }
        if (true == context.mustBeReferenced(dataCimi)) {
            dataCimi.setHref(context.makeHref(dataCimi, dataService.getId().toString()));
        }
    }

    /**
     * Fill the common data from a CIMI object to a service object.
     * 
     * @param context The current context
     * @param dataCimi Source CIMI object
     * @param dataService Destination service object
     */
    protected void fill(final CimiContext context, final CimiObjectCommon dataCimi, final Resource dataService) {
        this.fill(dataCimi, dataService);
        if (null != dataCimi.getId()) {
            dataService.setId(PathHelper.extractId(dataCimi.getId()));
        }
    }

    /**
     * Find the associated CIMI class in the configuration to build a reference
     * HREF with the ID of an unknown service resource.
     * 
     * @param context The current context
     * @param targetDataService The service resource
     * @return The CIMI HREF
     */
    protected String buildHrefTargetResource(final CimiContext context, final Resource targetDataService) {
        String href = null;
        if (null != targetDataService) {
            Class<? extends CimiResource> targetType = context.findAssociate(targetDataService.getClass());
            href = context.makeHref(targetType, this.getTargetId(targetDataService).toString());
        }
        return href;
    }

    /**
     * Get ID of an unknown service resource.
     * 
     * @param targetDataService The service resource
     * @return Th service resource ID
     */
    protected Integer getTargetId(final Resource targetDataService) {
        Integer id = null;
        if (true == CloudResource.class.isAssignableFrom(targetDataService.getClass())) {
            id = ((CloudResource) targetDataService).getId();
        } else {
            id = ((CloudEntity) targetDataService).getId();
        }
        return id;
    }
}
