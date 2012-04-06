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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.HelperRequest;
import org.ow2.sirocco.apis.rest.cimi.request.HelperResponse;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Job REST resource.
 * <p>
 * Operations supports :
 * <ul>
 * <li>Delete a job</li>
 * <li>Read a job</li>
 * <li>Update a job</li>
 * </ul>
 * </p>
 */
@Component
@Path(ConstantsPath.JOB_PATH)
public class CimiJobResource extends CimiResourceAbstract {

    @Autowired
    @Qualifier("CimiManagerReadJob")
    private CimiManager cimiManagerReadJob;

    @Autowired
    @Qualifier("CimiManagerDeleteJob")
    private CimiManager cimiManagerDeleteJob;

    @Autowired
    @Qualifier("CimiManagerUpdateJob")
    private CimiManager cimiManagerUpdateJob;

    /**
     * Get a job.
     * 
     * @param id The ID of job to get
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{id}")
    public Response read(@PathParam("id") final String id) {
        CimiRequest request = HelperRequest.buildRequest(this.getJaxRsRequestInfos(), id);
        CimiResponse response = new CimiResponse();
        this.cimiManagerReadJob.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

    /**
     * Update a job.
     * 
     * @param id The ID of job to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{id}")
    public Response update(@PathParam("id") final String id, final CimiJob cimiData) {
        CimiRequest request = HelperRequest.buildRequest(this.getJaxRsRequestInfos(), id, cimiData);
        CimiResponse response = new CimiResponse();
        this.cimiManagerUpdateJob.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

    /**
     * Delete a job.
     * 
     * @param id The ID of job to delete
     * @return The REST response
     */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") final String id) {
        CimiRequest request = HelperRequest.buildRequest(this.getJaxRsRequestInfos(), id);
        CimiResponse response = new CimiResponse();
        this.cimiManagerDeleteJob.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

}
