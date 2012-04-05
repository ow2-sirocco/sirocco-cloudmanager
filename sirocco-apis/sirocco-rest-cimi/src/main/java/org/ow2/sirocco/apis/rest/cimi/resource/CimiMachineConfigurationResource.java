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
import javax.ws.rs.core.Response;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
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
public class CimiMachineConfigurationResource extends CimiResourceAbstract {

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
    @Produces({MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATION_JSON,
        MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATION_XML})
    @Path("{id}")
    public Response read(@PathParam("id") final String id) {
        CimiRequest request = HelperRequest.buildRequest(this.getJaxRsRequestInfos(), id);
        CimiResponse response = new CimiResponse();
        this.cimiManagerReadMachineConfiguration.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

    /**
     * Get a collection of machines configurations.
     * 
     * @return The REST response
     */
    @GET
    @Produces({MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATIONCOLLECTION_JSON,
        MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATIONCOLLECTION_XML})
    public Response readMachineConfigurationCollection() {
        CimiRequest request = HelperRequest.buildRequest(this.getJaxRsRequestInfos());
        CimiResponse response = new CimiResponse();
        this.cimiManagerReadMachineConfigurationCollection.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

    /**
     * Update a machine configuration.
     * 
     * @param id The ID of machine configuration to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATION_JSON,
        MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATION_XML})
    @Produces({MediaTypeCimi.APPLICATION_CIMI_JOB_JSON, MediaTypeCimi.APPLICATION_CIMI_JOB_XML})
    @Path("{id}")
    public Response update(@PathParam("id") final String id, final CimiMachineConfiguration cimiData) {
        CimiRequest request = HelperRequest.buildRequest(this.getJaxRsRequestInfos(), id, cimiData);
        CimiResponse response = new CimiResponse();
        this.cimiManagerUpdateMachineConfiguration.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

    /**
     * Update a collection of machines configurations.
     * 
     * @return The REST response
     */
    @PUT
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATION_JSON,
        MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATION_XML})
    public Response updateMachineConfigurationCollection(final CimiMachineConfigurationCollection cimiData) {
        CimiRequest request = HelperRequest.buildRequest(this.getJaxRsRequestInfos(), cimiData);
        CimiResponse response = new CimiResponse();
        this.cimiManagerUpdateMachineConfigurationCollection.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

    /**
     * Create a machine configuration.
     * 
     * @return The REST response
     */
    @POST
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATIONCREATE_JSON,
        MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATIONCREATE_XML})
    @Produces({MediaTypeCimi.APPLICATION_CIMI_JOB_JSON, MediaTypeCimi.APPLICATION_CIMI_JOB_XML})
    public Response create(final CimiMachineConfiguration cimiData) {
        CimiRequest request = HelperRequest.buildRequest(this.getJaxRsRequestInfos(), cimiData);
        CimiResponse response = new CimiResponse();
        this.cimiManagerCreateMachineConfiguration.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

    /**
     * Delete a machine configuration.
     * 
     * @param id The ID of machine configuration to delete
     * @return The REST response
     */
    @DELETE
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATION_JSON,
        MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATION_XML})
    @Path("{id}")
    public Response delete(@PathParam("id") final String id) {
        CimiRequest request = HelperRequest.buildRequest(this.getJaxRsRequestInfos(), id);
        CimiResponse response = new CimiResponse();
        this.cimiManagerDeleteMachineConfiguration.execute(request, response);
        return HelperResponse.buildResponse(response);
    }

}
