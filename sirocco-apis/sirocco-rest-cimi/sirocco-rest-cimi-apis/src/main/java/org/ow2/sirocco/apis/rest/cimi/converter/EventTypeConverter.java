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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiEventType;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventType;

/**
 * Abstract class to convert the common data of a Event Type.
 */
public abstract class EventTypeConverter implements CimiConverter {

    /**
     * Fill the common data from a service object to a CIMI object.
     * 
     * @param context Current context of the REST request
     * @param dataService Destination Service object
     * @param dataCimi Source CIMI object
     */
    protected void fill(final CimiContext context, final EventType dataService, final CimiEventType dataCimi) {
        dataCimi.setDetail(dataService.getDetail());
        dataCimi.setResName(dataService.getResName());
        dataCimi.setResource(ConverterHelper.buildTargetResource(context, dataService.getResource()));
        dataCimi.setResType(ConverterHelper.buildResourceUri(context, dataService.getResource()));
    }
}
