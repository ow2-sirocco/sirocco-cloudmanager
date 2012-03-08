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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.configuration.CimiManagerDeleteMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.configuration.CimiManagerReadMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.configuration.CimiManagerUpdateMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.HelperRequest;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.apis.rest.cimi.utils.MediaTypeCimi;

/**
 * path = /configs Support Read,Update,Delete operation
 */
@Path(ConstantsPath.MACHINE_CONFIGURATION_PATH)
public class CimiMachineConfigurationResource {

    @Context
    UriInfo uriInfo;

    @Context
    HttpHeaders headers;

    public CimiMachineConfigurationResource() {
    }

    // CRUD method

    /**
     * Read operation Choosing a machine configuration
     */
    @GET
    @Produces({MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATION_JSON})
    @Path("{id}")
    public CimiMachineConfiguration getMachineConfiguration(@PathParam("id") String id) {

        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo, id));

        CimiResponse response = new CimiResponse();

        CimiManagerReadMachineConfiguration manager = getManagerMachineConfigurationRead();
        manager.execute(request, response);

        return (CimiMachineConfiguration) response.getCimiData();
    }

    /**
     * Update
     */
    @PUT
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATION_JSON})
    @Path("{id}")
    public Response putMachineConfiguration(CimiMachineConfiguration machineConf, @PathParam("id") String id,
            @QueryParam("CIMISelect") List<String> listQueryParam) {

        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo, id, machineConf));

        CimiResponse response = new CimiResponse();

        CimiManagerUpdateMachineConfiguration manager = getManagerMachineConfigurationUpdate();
        manager.execute(request, response);

        return Response.status(response.getStatus()).build();
    }

    /**
     * delete operation
     */
    @DELETE
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINECONFIGURATION_JSON})
    @Path("{id}")
    public Response deleteMachineConfiguration(@PathParam("id") String id) {

        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo, id));

        CimiResponse response = new CimiResponse();

        CimiManagerDeleteMachineConfiguration manager = getManagerMachineConfigurationDelete();
        manager.execute(request, response);

        return Response.status(response.getStatus()).build();
    }

    private CimiManagerDeleteMachineConfiguration getManagerMachineConfigurationDelete() {
        CimiManagerDeleteMachineConfiguration manager = new CimiManagerDeleteMachineConfiguration();
        return manager;
    }

    private CimiManagerReadMachineConfiguration getManagerMachineConfigurationRead() {
        CimiManagerReadMachineConfiguration manager = new CimiManagerReadMachineConfiguration();
        return manager;
    }

    private CimiManagerUpdateMachineConfiguration getManagerMachineConfigurationUpdate() {
        CimiManagerUpdateMachineConfiguration manager = new CimiManagerUpdateMachineConfiguration();
        return manager;
    }
}
