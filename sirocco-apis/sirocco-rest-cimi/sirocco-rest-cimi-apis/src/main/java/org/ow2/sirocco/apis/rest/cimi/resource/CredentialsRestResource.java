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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.HelperContext;
import org.ow2.sirocco.apis.rest.cimi.request.HelperResponse;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Credentials REST resource.
 * <p>
 * Operations supports :
 * <ul>
 * <li>Create a credentials</li>
 * <li>Delete a credentials</li>
 * <li>Read a credentials</li>
 * <li>Read a collection of credentials</li>
 * <li>Update a credentials</li>
 * </ul>
 * </p>
 */
@Component
@Path(ConstantsPath.CREDENTIALS_PATH)
public class CredentialsRestResource extends RestResourceAbstract {

    @Autowired
    @Qualifier("CimiManagerReadCredentials")
    private CimiManager cimiManagerReadCredentials;

    @Autowired
    @Qualifier("CimiManagerReadCredentialsCollection")
    private CimiManager cimiManagerReadCredentialsCollection;

    @Autowired
    @Qualifier("CimiManagerDeleteCredentials")
    private CimiManager cimiManagerDeleteCredentials;

    @Autowired
    @Qualifier("CimiManagerUpdateCredentials")
    private CimiManager cimiManagerUpdateCredentials;

    @Autowired
    @Qualifier("CimiManagerCreateCredentials")
    private CimiManager cimiManagerCreateCredentials;

    /**
     * Get a credentials.
     * 
     * @param id The ID of credentials to get
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response read(@PathParam("id") final String id) {
        CimiContext context = HelperContext.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerReadCredentials.execute(context);
        return HelperResponse.buildResponse(context.getResponse());
    }

    /**
     * Get a collection of credentials.
     * 
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response read() {
        CimiContext context = HelperContext.buildContext(this.getJaxRsRequestInfos());
        this.cimiManagerReadCredentialsCollection.execute(context);
        return HelperResponse.buildResponse(context.getResponse());
    }

    /**
     * Update a credentials.
     * 
     * @param id The ID of credentials to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response update(@PathParam("id") final String id, final CimiCredentials cimiData) {
        CimiContext context = HelperContext.buildContext(this.getJaxRsRequestInfos(), id, cimiData);
        this.cimiManagerUpdateCredentials.execute(context);
        return HelperResponse.buildResponse(context.getResponse());
    }

    /**
     * Create a credentials.
     * 
     * @return The REST response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(final CimiCredentialsCreate cimiData) {
        CimiContext context = HelperContext.buildContext(this.getJaxRsRequestInfos(), cimiData);
        this.cimiManagerCreateCredentials.execute(context);
        return HelperResponse.buildResponse(context.getResponse());
    }

    /**
     * Delete a credentials.
     * 
     * @param id The ID of credentials to delete
     * @return The REST response
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") final String id) {
        CimiContext context = HelperContext.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerDeleteCredentials.execute(context);
        return HelperResponse.buildResponse(context.getResponse());
    }

}
