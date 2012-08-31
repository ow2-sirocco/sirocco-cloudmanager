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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiEventLogTemplate;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.ContextHelper;
import org.ow2.sirocco.apis.rest.cimi.request.ResponseHelper;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * EventLog Template REST resource.
 * <p>
 * Operations supports :
 * <ul>
 * <li>Create a eventLog template</li>
 * <li>Delete a eventLog template</li>
 * <li>Read a eventLog template</li>
 * <li>Read a collection of eventLogs templates</li>
 * <li>Update a eventLog template</li>
 * </ul>
 * </p>
 */
@Component
@Path(ConstantsPath.EVENT_LOG_TEMPLATE_PATH)
public class EventLogTemplateRestResource extends RestResourceAbstract {

    @Autowired
    @Qualifier("CimiManagerReadEventLogTemplate")
    private CimiManager cimiManagerReadEventLogTemplate;

    @Autowired
    @Qualifier("CimiManagerReadEventLogTemplateCollection")
    private CimiManager cimiManagerReadEventLogTemplateCollection;

    @Autowired
    @Qualifier("CimiManagerDeleteEventLogTemplate")
    private CimiManager cimiManagerDeleteEventLogTemplate;

    @Autowired
    @Qualifier("CimiManagerUpdateEventLogTemplate")
    private CimiManager cimiManagerUpdateEventLogTemplate;

    @Autowired
    @Qualifier("CimiManagerCreateEventLogTemplate")
    private CimiManager cimiManagerCreateEventLogTemplate;

    /**
     * Get a eventLog template.
     * 
     * @param id The ID of eventLog template to get
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response read(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerReadEventLogTemplate.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Get a collection of eventLogs templates.
     * 
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response read() {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos());
        this.cimiManagerReadEventLogTemplateCollection.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Update a eventLog template.
     * 
     * @param id The ID of eventLog template to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response update(@PathParam("id") final String id, final CimiEventLogTemplate cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id, cimiData);
        this.cimiManagerUpdateEventLogTemplate.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Create a eventLog template.
     * 
     * @return The REST response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(final CimiEventLogTemplate cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), cimiData);
        this.cimiManagerCreateEventLogTemplate.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Delete a eventLog template.
     * 
     * @param id The ID of eventLog template to delete
     * @return The REST response
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerDeleteEventLogTemplate.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

}
