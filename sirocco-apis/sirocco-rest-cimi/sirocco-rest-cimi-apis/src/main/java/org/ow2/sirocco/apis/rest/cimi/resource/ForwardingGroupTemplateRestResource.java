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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiForwardingGroupTemplate;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.ContextHelper;
import org.ow2.sirocco.apis.rest.cimi.request.ResponseHelper;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * ForwardingGroup Template REST resource.
 * <p>
 * Operations supports :
 * <ul>
 * <li>Create a forwardingGroup template</li>
 * <li>Delete a forwardingGroup template</li>
 * <li>Read a forwardingGroup template</li>
 * <li>Read a collection of forwardingGroups templates</li>
 * <li>Update a forwardingGroup template</li>
 * </ul>
 * </p>
 */
@Component
@Path(ConstantsPath.FORWARDING_GROUP_TEMPLATE_PATH)
public class ForwardingGroupTemplateRestResource extends RestResourceAbstract {

    @Autowired
    @Qualifier("CimiManagerReadForwardingGroupTemplate")
    private CimiManager cimiManagerReadForwardingGroupTemplate;

    @Autowired
    @Qualifier("CimiManagerReadForwardingGroupTemplateCollection")
    private CimiManager cimiManagerReadForwardingGroupTemplateCollection;

    @Autowired
    @Qualifier("CimiManagerDeleteForwardingGroupTemplate")
    private CimiManager cimiManagerDeleteForwardingGroupTemplate;

    @Autowired
    @Qualifier("CimiManagerUpdateForwardingGroupTemplate")
    private CimiManager cimiManagerUpdateForwardingGroupTemplate;

    @Autowired
    @Qualifier("CimiManagerCreateForwardingGroupTemplate")
    private CimiManager cimiManagerCreateForwardingGroupTemplate;

    /**
     * Get a forwardingGroup template.
     * 
     * @param id The ID of forwardingGroup template to get
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response read(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerReadForwardingGroupTemplate.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Get a collection of forwardingGroups templates.
     * 
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response read() {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos());
        this.cimiManagerReadForwardingGroupTemplateCollection.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Update a forwardingGroup template.
     * 
     * @param id The ID of forwardingGroup template to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response update(@PathParam("id") final String id, final CimiForwardingGroupTemplate cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id, cimiData);
        this.cimiManagerUpdateForwardingGroupTemplate.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Create a forwardingGroup template.
     * 
     * @return The REST response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(final CimiForwardingGroupTemplate cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), cimiData);
        this.cimiManagerCreateForwardingGroupTemplate.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Delete a forwardingGroup template.
     * 
     * @param id The ID of forwardingGroup template to delete
     * @return The REST response
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerDeleteForwardingGroupTemplate.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

}
