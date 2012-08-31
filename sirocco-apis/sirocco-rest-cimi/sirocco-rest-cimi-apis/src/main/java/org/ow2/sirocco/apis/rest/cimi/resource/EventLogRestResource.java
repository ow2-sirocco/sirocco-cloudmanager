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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiEventLog;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiEventLogCreate;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.ContextHelper;
import org.ow2.sirocco.apis.rest.cimi.request.IdRequest;
import org.ow2.sirocco.apis.rest.cimi.request.ResponseHelper;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * EventLog REST resource.
 * <p>
 * Operations supports :
 * <ul>
 * <li>Create a eventLog</li>
 * <li>Delete a eventLog</li>
 * <li>Read a eventLog</li>
 * <li>Read a collection of eventLogs</li>
 * <li>Update a eventLog</li>
 * </ul>
 * </p>
 */
@Component
@Path(ConstantsPath.EVENT_LOG_PATH)
public class EventLogRestResource extends RestResourceAbstract {

    @Autowired
    @Qualifier("CimiManagerReadEventLog")
    private CimiManager cimiManagerReadEventLog;

    @Autowired
    @Qualifier("CimiManagerReadEventLogCollection")
    private CimiManager cimiManagerReadEventLogCollection;

    @Autowired
    @Qualifier("CimiManagerDeleteEventLog")
    private CimiManager cimiManagerDeleteEventLog;

    @Autowired
    @Qualifier("CimiManagerUpdateEventLog")
    private CimiManager cimiManagerUpdateEventLog;

    @Autowired
    @Qualifier("CimiManagerCreateEventLog")
    private CimiManager cimiManagerCreateEventLog;

    @Autowired
    @Qualifier("CimiManagerReadEventLogEvent")
    private CimiManager cimiManagerReadEventLogEvent;

    @Autowired
    @Qualifier("CimiManagerReadEventLogEventCollection")
    private CimiManager cimiManagerReadEventLogEventCollection;

    @Autowired
    @Qualifier("CimiManagerDeleteEventLogEvent")
    private CimiManager cimiManagerDeleteEventLogEvent;

    /**
     * Get a eventLog.
     * 
     * @param id The ID of eventLog to get
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response read(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerReadEventLog.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Get a collection of eventLogs.
     * 
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response read() {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos());
        this.cimiManagerReadEventLogCollection.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Update a eventLog.
     * 
     * @param id The ID of eventLog to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response update(@PathParam("id") final String id, final CimiEventLog cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id, cimiData);
        this.cimiManagerUpdateEventLog.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Create a eventLog.
     * 
     * @return The REST response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(final CimiEventLogCreate cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), cimiData);
        this.cimiManagerCreateEventLog.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Delete a eventLog.
     * 
     * @param id The ID of eventLog to delete
     * @return The REST response
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerDeleteEventLog.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Read a collection of events of a eventLog.
     * 
     * @param idParent ID eventLog
     * @return The REST response
     */
    @GET
    @Path("{idParent}" + ConstantsPath.EVENT_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response readEvents(@PathParam("idParent") final String idParent) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(null, idParent));
        this.cimiManagerReadEventLogEventCollection.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Read a event of a eventLog.
     * 
     * @param idParent ID eventLog
     * @param id ID event to read
     * @return The REST response
     */
    @GET
    @Path("/{idParent}" + ConstantsPath.EVENT_PATH + "/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response readEvent(@PathParam("idParent") final String idParent, @PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent));
        this.cimiManagerReadEventLogEvent.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Delete a event of a eventLog.
     * 
     * @param idParent ID eventLog
     * @param id ID event to delete
     * @return The REST response
     */
    @DELETE
    @Path("/{idParent}" + ConstantsPath.EVENT_PATH + "/{id}")
    public Response deleteEvent(@PathParam("idParent") final String idParent, @PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent));
        this.cimiManagerDeleteEventLogEvent.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

}
