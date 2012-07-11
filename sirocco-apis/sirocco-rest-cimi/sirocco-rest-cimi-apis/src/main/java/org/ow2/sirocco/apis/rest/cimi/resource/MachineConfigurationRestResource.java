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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.ContextHelper;
import org.ow2.sirocco.apis.rest.cimi.request.ResponseHelper;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Machine Configuration REST resource.
 * <p>
 * Operations supports :
 * <ul>
 * <li>Create a machine configuration</li>
 * <li>Delete a machine configuration</li>
 * <li>Read a machine configuration</li>
 * <li>Read a collection of machines configurations</li>
 * <li>Update a machine configuration</li>
 * </ul>
 * </p>
 */
@Component
@Path(ConstantsPath.MACHINE_CONFIGURATION_PATH)
public class MachineConfigurationRestResource extends RestResourceAbstract {

    @Autowired
    @Qualifier("CimiManagerReadMachineConfiguration")
    private CimiManager cimiManagerReadMachineConfiguration;

    @Autowired
    @Qualifier("CimiManagerReadMachineConfigurationCollection")
    private CimiManager cimiManagerReadMachineConfigurationCollection;

    @Autowired
    @Qualifier("CimiManagerDeleteMachineConfiguration")
    private CimiManager cimiManagerDeleteMachineConfiguration;

    @Autowired
    @Qualifier("CimiManagerUpdateMachineConfiguration")
    private CimiManager cimiManagerUpdateMachineConfiguration;

    @Autowired
    @Qualifier("CimiManagerUpdateMachineConfigurationCollection")
    private CimiManager cimiManagerUpdateMachineConfigurationCollection;

    @Autowired
    @Qualifier("CimiManagerCreateMachineConfiguration")
    private CimiManager cimiManagerCreateMachineConfiguration;

    /**
     * Get a machine configuration.
     * 
     * @param id The ID of machine configuration to get
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response read(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerReadMachineConfiguration.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Get a collection of machines configurations.
     * 
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response read() {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos());
        this.cimiManagerReadMachineConfigurationCollection.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Update a machine configuration.
     * 
     * @param id The ID of machine configuration to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response update(@PathParam("id") final String id, final CimiMachineConfiguration cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id, cimiData);
        this.cimiManagerUpdateMachineConfiguration.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Update a collection of machines configurations.
     * 
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Deprecated
    public Response update(final CimiMachineConfigurationCollection cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), cimiData);
        this.cimiManagerUpdateMachineConfigurationCollection.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Create a machine configuration.
     * 
     * @return The REST response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(final CimiMachineConfiguration cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), cimiData);
        this.cimiManagerCreateMachineConfiguration.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Delete a machine configuration.
     * 
     * @param id The ID of machine configuration to delete
     * @return The REST response
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerDeleteMachineConfiguration.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }
}
