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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.manager.cep.CimiManagerReadCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.manager.cep.CimiManagerUpdateCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.HelperRequest;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.apis.rest.cimi.utils.MediaTypeCimi;

/**
 * Support read and update operation
 */
@Path(ConstantsPath.CLOUDENTRYPOINT_PATH)
public class CimiCloudEntryPointResource {

    @Context
    UriInfo uriInfo;

    @Context
    HttpHeaders headers;

    public CimiCloudEntryPointResource() {
    }

    // CRUD method
    /**
     * Read operation Retrieve the Cloud Entry Point : give the URL to each
     * collection
     */
    @GET
    @Produces({MediaTypeCimi.APPLICATION_CIMI_CLOUDENTRYPOINT_JSON})
    public CimiCloudEntryPoint getEntryPoint() {

        CimiRequest request = HelperRequest.buildRequest(headers, uriInfo);
        CimiResponse response = new CimiResponse();

        CimiManager manager = getManagerCloudEntryPointRead();
        manager.execute(request, response);

        return (CimiCloudEntryPoint) response.getCimiData();
    }

    @PUT
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_CLOUDENTRYPOINT_JSON})
    public Response updateCloudEntryPoint(CimiCloudEntryPoint cloudEntrypoint,
            @QueryParam("CIMISelect") List<String> listQueryParam) {

        CimiRequest request = HelperRequest.buildRequest(headers, uriInfo, cloudEntrypoint);
        CimiResponse response = new CimiResponse();

        CimiManagerUpdateCloudEntryPoint manager = getManagerCloudEntryPointUpdate();
        manager.execute(request, response);

        return Response.status(response.getStatus()).build();
    }

    /**
     * Spring prendra le relais
     * @return
     */
    private CimiManagerUpdateCloudEntryPoint getManagerCloudEntryPointUpdate() {
        CimiManagerUpdateCloudEntryPoint manager = new CimiManagerUpdateCloudEntryPoint();

        return manager;
    }

    /**
     * Spring prendra le relais
     * @return
     */
    private CimiManager getManagerCloudEntryPointRead() {
        CimiManager manager = new CimiManagerReadCloudEntryPoint();
        return manager;
    }

}
