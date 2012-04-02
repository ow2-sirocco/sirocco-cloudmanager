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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
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
 * Machine Image REST resource.
 * <p>
 * Operations supports :
 * <ul>
 * <li>Create a machine image</li>
 * <li>Delete a machine image</li>
 * <li>Read a machine image</li>
 * <li>Read a collection of machines images</li>
 * <li>Update a machine image</li>
 * </ul>
 * </p>
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

    @Autowired
    @Qualifier("CimiManagerReadMachineImageCollection")
    private CimiManager cimiManagerReadMachineImageCollection;

    @Autowired
    @Qualifier("CimiManagerDeleteMachineImage")
    private CimiManager cimiManagerDeleteMachineImage;

    @Autowired
    @Qualifier("CimiManagerUpdateMachineImage")
    private CimiManager cimiManagerUpdateMachineImage;

    @Autowired
    @Qualifier("CimiManagerUpdateMachineImageCollection")
    private CimiManager cimiManagerUpdateMachineImageCollection;

    @Autowired
    @Qualifier("CimiManagerCreateMachineImage")
    private CimiManager cimiManagerCreateMachineImage;

    /**
     * Get a machine image.
     * 
     * @param id The ID of machine image to get
     * @return The REST response
     */
    @GET
    @Produces({MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML})
    @Path("{id}")
    public Response readMachineImage(@PathParam("id") final String id) {
        CimiRequest request = HelperRequest.buildRequest(this.headers, this.uriInfo, id);
        CimiResponse response = new CimiResponse();
        this.cimiManagerReadMachineImage.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

    /**
     * Get a collection of machines images.
     * 
     * @return The REST response
     */
    @GET
    @Produces({MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGECOLLECTION_JSON,
        MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGECOLLECTION_XML})
    public Response readMachineImageCollection() {
        CimiRequest request = HelperRequest.buildRequest(this.headers, this.uriInfo);
        CimiResponse response = new CimiResponse();
        this.cimiManagerReadMachineImageCollection.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

    /**
     * Update a machine image.
     * 
     * @param id The ID of machine image to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML})
    @Produces({MediaTypeCimi.APPLICATION_CIMI_JOB_JSON, MediaTypeCimi.APPLICATION_CIMI_JOB_XML})
    @Path("{id}")
    public Response updateMachineImage(@PathParam("id") final String id, final CimiMachineImage machineImage) {
        CimiRequest request = HelperRequest.buildRequest(this.headers, this.uriInfo, id, machineImage);
        CimiResponse response = new CimiResponse();
        this.cimiManagerUpdateMachineImage.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

    /**
     * Update a collection of machines images.
     * 
     * @return The REST response
     */
    @PUT
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML})
    public Response updateMachineImageCollection(final CimiMachineImageCollection machineImageCollection) {
        CimiRequest request = HelperRequest.buildRequest(this.headers, this.uriInfo, machineImageCollection);
        CimiResponse response = new CimiResponse();
        this.cimiManagerUpdateMachineImageCollection.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

    /**
     * Create a machine image.
     * 
     * @return The REST response
     */
    @POST
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGECREATE_JSON, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGECREATE_XML})
    @Produces({MediaTypeCimi.APPLICATION_CIMI_JOB_JSON, MediaTypeCimi.APPLICATION_CIMI_JOB_XML})
    public Response createMachineImage(final CimiMachineImage machineImage) {
        CimiRequest request = HelperRequest.buildRequest(this.headers, this.uriInfo, machineImage);
        CimiResponse response = new CimiResponse();
        this.cimiManagerCreateMachineImage.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

    /**
     * Delete a machine image.
     * 
     * @param id The ID of machine image to delete
     * @return The REST response
     */
    @DELETE
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML})
    @Path("{id}")
    public Response deleteMachineImage(@PathParam("id") final String id) {
        CimiRequest request = HelperRequest.buildRequest(this.headers, this.uriInfo, id);
        CimiResponse response = new CimiResponse();
        this.cimiManagerDeleteMachineImage.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

}
