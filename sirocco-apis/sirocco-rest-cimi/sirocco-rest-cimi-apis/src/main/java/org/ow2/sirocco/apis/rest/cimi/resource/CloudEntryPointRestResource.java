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
package org.ow2.sirocco.apis.rest.cimi.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.ContextHelper;
import org.ow2.sirocco.apis.rest.cimi.request.ResponseHelper;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Cloud Entry Point REST resource.
 * <p>
 * Operations supports :
 * <ul>
 * <li>Read a Cloud Entry Point</li>
 * <li>Update a Cloud Entry Point</li>
 * </ul>
 * </p>
 */
@Component
@Path(ConstantsPath.CLOUDENTRYPOINT_PATH)
public class CloudEntryPointRestResource extends RestResourceAbstract {

    @Autowired
    @Qualifier("CimiManagerReadCloudEntryPoint")
    private CimiManager cimiManagerReadCloudEntryPoint;

    @Autowired
    @Qualifier("CimiManagerUpdateCloudEntryPoint")
    private CimiManager cimiManagerUpdateCloudEntryPoint;

    /**
     * Get a Cloud Entry Point.
     * 
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response readCloudEntryPoint() {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos());
        this.cimiManagerReadCloudEntryPoint.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Update a Cloud Entry Point.
     * 
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCloudEntryPoint(final CimiCloudEntryPoint cloudEntryPoint) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), cloudEntryPoint);
        this.cimiManagerUpdateCloudEntryPoint.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

}
