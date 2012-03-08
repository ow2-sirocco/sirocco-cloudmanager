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
 * $Id: CimiMachineImageResource.java 134 2012-03-08 16:56:21Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.image.CimiManagerDeleteMachineImage;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.image.CimiManagerReadMachineImage;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.image.CimiManagerUpdateMachineImage;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.HelperRequest;
import org.ow2.sirocco.apis.rest.cimi.request.HelperResponse;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.apis.rest.cimi.utils.MediaTypeCimi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Operations supports : read, update and delete
 */
@Component
@Path(ConstantsPath.MACHINE_IMAGE_PATH)
public class CimiMachineImageResource {

    @Context
    UriInfo uriInfo;

    @Context
    HttpHeaders headers;

    @Autowired
    @Qualifier("CimiManagerReadMachineImage")
    private CimiManager cimiManagerReadMachineImage;

    /**
     * Get a machine image.
     */
    @GET
    @Produces({MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML})
    @Path("{id}")
    public Response getMachineImage(@PathParam("id") String id) {
        CimiRequest request = HelperRequest.buildRequest(headers, uriInfo, id);
        CimiResponse response = new CimiResponse();
        getManagerMachineImageRead().execute(request, response);
        return HelperResponse.buildResponse(response);
    }

    // /**
    // * Get a machine image.
    // */
    // @GET
    // @Produces({MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML})
    // @Path("{id}")
    // public MachineImage getMachineImageXml(@PathParam("id") String id) {
    // CimiManagerReadMachineImage manager = getManagerMachineImageRead();
    // manager.setRequest(HelperRequest.buildRequest(headers, uriInfo, id));
    // manager.setResponse(new CimiResponse());
    // manager.execute(null, null);
    //
    // return (MachineImage) manager.getResponse().getCimiData();
    // }

    /**
     * Update a machine image.
     */
    @PUT
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON})
    @Path("{id}")
    public Response putMachineImage(CimiMachineImage machineImage, @PathParam("id") String id,
            @QueryParam("CIMISelect") List<String> listQueryParam) {

        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo, id, machineImage));

        CimiResponse response = new CimiResponse();

        CimiManagerUpdateMachineImage manager = getManagerMachineImageUpdate();
        manager.execute(request, response);

        return Response.status(response.getStatus()).build();
    }

    /**
     * Delete a machine image.
     */
    @DELETE
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON})
    @Path("{id}")
    public Response deleteMachineImage(@PathParam("id") String id) {

        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo, id));

        CimiResponse response = new CimiResponse();

        CimiManagerDeleteMachineImage manager = getManagerMachineImageDelete();
        manager.execute(request, response);

        return Response.status(response.getStatus()).build();
    }

    private CimiManagerDeleteMachineImage getManagerMachineImageDelete() {
        CimiManagerDeleteMachineImage manager = new CimiManagerDeleteMachineImage();
        return manager;
    }

    private CimiManagerUpdateMachineImage getManagerMachineImageUpdate() {
        CimiManagerUpdateMachineImage manager = new CimiManagerUpdateMachineImage();
        return manager;
    }

    private CimiManagerReadMachineImage getManagerMachineImageRead() {
        return (CimiManagerReadMachineImage) cimiManagerReadMachineImage;
    }
}
