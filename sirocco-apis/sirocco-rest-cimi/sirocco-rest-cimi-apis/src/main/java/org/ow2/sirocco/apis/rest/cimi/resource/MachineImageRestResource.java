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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.HelperContext;
import org.ow2.sirocco.apis.rest.cimi.request.HelperResponse;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
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
public class MachineImageRestResource extends RestResourceAbstract {
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
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{id}")
    public Response read(@PathParam("id") final String id) {
        CimiContext context = HelperContext.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerReadMachineImage.execute(context);
        return HelperResponse.buildResponse(context.getResponse());
    }

    /**
     * Get a collection of machines images.
     * 
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response read() {
        CimiContext context = HelperContext.buildContext(this.getJaxRsRequestInfos());
        this.cimiManagerReadMachineImageCollection.execute(context);
        return HelperResponse.buildResponse(context.getResponse());
    }

    /**
     * Update a machine image.
     * 
     * @param id The ID of machine image to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{id}")
    public Response update(@PathParam("id") final String id, final CimiMachineImage cimiData) {
        CimiContext context = HelperContext.buildContext(this.getJaxRsRequestInfos(), id, cimiData);
        this.cimiManagerUpdateMachineImage.execute(context);
        return HelperResponse.buildResponse(context.getResponse());
    }

    /**
     * Update a collection of machines images.
     * 
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response update(final CimiMachineImageCollection cimiDataCollection) {
        CimiContext context = HelperContext.buildContext(this.getJaxRsRequestInfos(), cimiDataCollection);
        this.cimiManagerUpdateMachineImageCollection.execute(context);
        return HelperResponse.buildResponse(context.getResponse());
    }

    /**
     * Create a machine image.
     * 
     * @return The REST response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(final CimiMachineImage cimiData) {
        CimiContext context = HelperContext.buildContext(this.getJaxRsRequestInfos(), cimiData);
        this.cimiManagerCreateMachineImage.execute(context);
        return HelperResponse.buildResponse(context.getResponse());
    }

    /**
     * Delete a machine image.
     * 
     * @param id The ID of machine image to delete
     * @return The REST response
     */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") final String id) {
        CimiContext context = HelperContext.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerDeleteMachineImage.execute(context);
        return HelperResponse.buildResponse(context.getResponse());
    }

}
