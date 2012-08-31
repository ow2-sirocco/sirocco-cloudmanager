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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiAction;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetworkPort;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiNetworkPortCreate;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.ContextHelper;
import org.ow2.sirocco.apis.rest.cimi.request.ResponseHelper;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * NetworkPort REST resource.
 * <p>
 * Operations supports :
 * <ul>
 * <li>Create a networkPort</li>
 * <li>Delete a networkPort</li>
 * <li>Read a networkPort</li>
 * <li>Read a collection of networkPorts</li>
 * <li>Update a networkPort</li>
 * </ul>
 * </p>
 */
@Component
@Path(ConstantsPath.NETWORK_PORT_PATH)
public class NetworkPortRestResource extends RestResourceAbstract {

    @Autowired
    @Qualifier("CimiManagerReadNetworkPort")
    private CimiManager cimiManagerReadNetworkPort;

    @Autowired
    @Qualifier("CimiManagerReadNetworkPortCollection")
    private CimiManager cimiManagerReadNetworkPortCollection;

    @Autowired
    @Qualifier("CimiManagerDeleteNetworkPort")
    private CimiManager cimiManagerDeleteNetworkPort;

    @Autowired
    @Qualifier("CimiManagerUpdateNetworkPort")
    private CimiManager cimiManagerUpdateNetworkPort;

    @Autowired
    @Qualifier("CimiManagerCreateNetworkPort")
    private CimiManager cimiManagerCreateNetworkPort;

    @Autowired
    @Qualifier("CimiManagerActionNetworkPort")
    private CimiManager cimiManagerActionNetworkPort;

    /**
     * Get a networkPort.
     * 
     * @param id The ID of networkPort to get
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response read(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerReadNetworkPort.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Get a collection of networkPorts.
     * 
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response read() {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos());
        this.cimiManagerReadNetworkPortCollection.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Update a networkPort.
     * 
     * @param id The ID of networkPort to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response update(@PathParam("id") final String id, final CimiNetworkPort cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id, cimiData);
        this.cimiManagerUpdateNetworkPort.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Create a networkPort.
     * 
     * @return The REST response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(final CimiNetworkPortCreate cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), cimiData);
        this.cimiManagerCreateNetworkPort.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Actions on networkPort.
     * 
     * @return The REST response
     */
    @POST
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response action(@PathParam("id") final String id, final CimiAction cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id, cimiData);
        this.cimiManagerActionNetworkPort.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Delete a networkPort.
     * 
     * @param id The ID of networkPort to delete
     * @return The REST response
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerDeleteNetworkPort.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

}
