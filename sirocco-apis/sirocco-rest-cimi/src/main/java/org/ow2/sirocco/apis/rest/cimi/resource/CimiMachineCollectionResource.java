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
 * $Id: CimiMachineCollectionResource.java 123 2012-03-07 14:41:51Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.CimiManagerCreateMachine;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.CimiManagerReadMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.CimiManagerUpdateMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.HelperRequest;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.apis.rest.cimi.utils.MediaTypeCimi;

/**
 * Machine Collection path = /machines supports the read and update Operations +
 * creating a new machine
 */
// FIXME A regouper
@Path(ConstantsPath.MACHINE_PATH + "_COLLECTION")
public class CimiMachineCollectionResource {

    @Context
    UriInfo uriInfo;

    @Context
    HttpHeaders headers;

    public CimiMachineCollectionResource() {
    }

    // CRUD method
    /**
     * Read operation Retrieve the machine collection
     */
    @GET
    @Produces({MediaTypeCimi.APPLICATION_CIMI_MACHINECOLLECTION_JSON})
    public CimiMachineCollection getMachineCollection() {

        CimiRequest request = HelperRequest.buildRequest(headers, uriInfo);
        CimiResponse response = new CimiResponse();

        CimiManagerReadMachineCollection manager = getManagerMachineCollectionRead();
        manager.execute(request, response);

        return (CimiMachineCollection) response.getCimiData();

    }

    /**
     * Creating a new machine
     * @param a reference to a machine template or a machine template itself
     * @return
     */
    @POST
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINECREATE_JSON})
    public Response postMachine(CimiMachineTemplate entity) {

        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo, null, entity));

        CimiResponse response = new CimiResponse();

        CimiManagerCreateMachine manager = getManagerMachineCreate();
        manager.execute(request, response);

        return Response.status(response.getStatus()).build();
    }

    /**
     * Update operation
     * @param machine
     * @param id
     * @param listQueryParam
     * @return
     */
    @PUT
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINECOLLECTION_JSON})
    public Response putMachine(CimiMachineCollection machineCo, @QueryParam("CIMISelect") List<String> listQueryParam) {
        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo, null, machineCo));

        CimiResponse response = new CimiResponse();

        CimiManagerUpdateMachineCollection manager = getManagerMachineCollectionUpdate();
        manager.execute(request, response);

        return Response.status(response.getStatus()).build();
    }

    private CimiManagerUpdateMachineCollection getManagerMachineCollectionUpdate() {
        CimiManagerUpdateMachineCollection manager = new CimiManagerUpdateMachineCollection();
        return manager;
    }

    // FIXME pris en charge par spring
    private CimiManagerReadMachineCollection getManagerMachineCollectionRead() {
        CimiManagerReadMachineCollection manager = new CimiManagerReadMachineCollection();
        return manager;
    }

    private CimiManagerCreateMachine getManagerMachineCreate() {
        CimiManagerCreateMachine manager = new CimiManagerCreateMachine();
        return manager;
    }

}
