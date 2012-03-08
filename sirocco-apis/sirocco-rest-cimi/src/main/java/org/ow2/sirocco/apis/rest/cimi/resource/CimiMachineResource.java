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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.CimiManagerActionMachine;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.CimiManagerDeleteMachine;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.CimiManagerReadMachine;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.CimiManagerUpdateMachine;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.HelperRequest;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.apis.rest.cimi.utils.MediaTypeCimi;

/**
 * This entity supports the Read, Update and Delete operations. Action : start +
 * stop + restart + pause + suspend + capture + snapshot + restore
 */
@Path(ConstantsPath.MACHINE_PATH)
public class CimiMachineResource {

    @Context
    UriInfo uriInfo;

    @Context
    HttpHeaders headers;

    public CimiMachineResource() {

    }

    // CRUD method
    /**
     * Read operation
     */
    @GET
    @Produces({MediaTypeCimi.APPLICATION_CIMI_MACHINE_JSON})
    @Path("{id}")
    public CimiMachine getMachine(@PathParam("id") String id) {
        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo, id));

        CimiResponse response = new CimiResponse();

        CimiManagerReadMachine manager = getManagerMachineRead();
        manager.execute(request, response);

        return (CimiMachine) response.getCimiData();
    }

    /**
     * Update
     */
    @PUT
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINE_JSON})
    @Path("{id}")
    public Response putMachine(CimiMachine machine, @PathParam("id") String id,
            @QueryParam("CIMISelect") List<String> listQueryParam) {

        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo, id, machine));

        CimiResponse response = new CimiResponse();

        CimiManagerUpdateMachine manager = getManagerMachineUpdate();
        manager.execute(request, response);

        return Response.status(response.getStatus()).build();
    }

    /**
     * actions
     */
    @POST
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_ACTION_JSON})
    @Path("{id}")
    public Response postActionMachine(CimiMachine machine, @PathParam("id") String id) {

        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo, id, machine));

        CimiResponse response = new CimiResponse();

        CimiManagerActionMachine manager = getManagerMachineAction();
        manager.execute(request, response);

        return Response.status(response.getStatus()).build();
    }

    /**
     * delete operation
     */
    @DELETE
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINE_JSON})
    @Path("{id}")
    public Response deleteMachine(@PathParam("id") String id) {

        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo, id));

        CimiResponse response = new CimiResponse();

        CimiManagerDeleteMachine manager = getManagerMachineDelete();
        manager.execute(request, response);

        return Response.status(response.getStatus()).build();
    }

    private CimiManagerActionMachine getManagerMachineAction() {
        CimiManagerActionMachine manager = new CimiManagerActionMachine();
        return manager;
    }

    private CimiManagerDeleteMachine getManagerMachineDelete() {
        CimiManagerDeleteMachine manager = new CimiManagerDeleteMachine();
        return manager;
    }

    private CimiManagerUpdateMachine getManagerMachineUpdate() {
        CimiManagerUpdateMachine manager = new CimiManagerUpdateMachine();
        return manager;
    }

    private CimiManagerReadMachine getManagerMachineRead() {
        CimiManagerReadMachine manager = new CimiManagerReadMachine();
        return manager;
    }

}
