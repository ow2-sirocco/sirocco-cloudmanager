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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemTemplate;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.ContextHelper;
import org.ow2.sirocco.apis.rest.cimi.request.ResponseHelper;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

// FIXME SystemTemplate Actions : import, export !!!

/**
 * System Template REST resource.
 * <p>
 * Operations supports :
 * <ul>
 * <li>Create a system template</li>
 * <li>Delete a system template</li>
 * <li>Read a system template</li>
 * <li>Read a collection of systems templates</li>
 * <li>Update a system template</li>
 * </ul>
 * </p>
 */
@Component
@Path(ConstantsPath.SYSTEM_TEMPLATE_PATH)
public class SystemTemplateRestResource extends RestResourceAbstract {

    @Autowired
    @Qualifier("CimiManagerReadSystemTemplate")
    private CimiManager cimiManagerReadSystemTemplate;

    @Autowired
    @Qualifier("CimiManagerReadSystemTemplateCollection")
    private CimiManager cimiManagerReadSystemTemplateCollection;

    @Autowired
    @Qualifier("CimiManagerDeleteSystemTemplate")
    private CimiManager cimiManagerDeleteSystemTemplate;

    @Autowired
    @Qualifier("CimiManagerUpdateSystemTemplate")
    private CimiManager cimiManagerUpdateSystemTemplate;

    @Autowired
    @Qualifier("CimiManagerCreateSystemTemplate")
    private CimiManager cimiManagerCreateSystemTemplate;

    /**
     * Get a system template.
     * 
     * @param id The ID of system template to get
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response read(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerReadSystemTemplate.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Get a collection of systems templates.
     * 
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response read() {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos());
        this.cimiManagerReadSystemTemplateCollection.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Update a system template.
     * 
     * @param id The ID of system template to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response update(@PathParam("id") final String id, final CimiSystemTemplate cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id, cimiData);
        this.cimiManagerUpdateSystemTemplate.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Create a system template.
     * 
     * @return The REST response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(final CimiSystemTemplate cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), cimiData);
        this.cimiManagerCreateSystemTemplate.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Delete a system template.
     * 
     * @param id The ID of system template to delete
     * @return The REST response
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerDeleteSystemTemplate.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

}
