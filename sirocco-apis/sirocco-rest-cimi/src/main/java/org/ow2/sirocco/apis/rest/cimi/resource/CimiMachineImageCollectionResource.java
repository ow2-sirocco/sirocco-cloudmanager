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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.image.CimiManagerCreateMachineImage;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.image.CimiManagerReadMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.manager.machine.image.CimiManagerUpdateMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.HelperRequest;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.apis.rest.cimi.utils.MediaTypeCimi;

/**
 * Supports read and update operations + create a new Machine Image
 */
// FIXME A regouper

@Path(ConstantsPath.MACHINE_IMAGE_PATH + "_COLLECTION")
public class CimiMachineImageCollectionResource {

    @Context
    UriInfo uriInfo;

    @Context
    HttpHeaders headers;

    // --------------------------- constructor

    public CimiMachineImageCollectionResource() {
    }

    // --------------------------- CRUD method
    /**
     * Read operation Retrieve the machine collection
     */
    @GET
    @Produces({MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGECOLLECTION_JSON})
    public CimiMachineImageCollection getMachineImageCollection() {

        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo));

        CimiResponse response = new CimiResponse();

        CimiManagerReadMachineImageCollection manager = getManagerMachineImageCollectionRead();
        manager.execute(request, response);

        return (CimiMachineImageCollection) response.getCimiData();
    }

    /**
     * Creating a new machine image
     * @return
     */
    @POST
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGECREATE_JSON})
    public Response postMachineImage(CimiMachineImage entity) {

        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo, null, entity));

        CimiResponse response = new CimiResponse();

        CimiManagerCreateMachineImage manager = getManagerMachineImageCreate();
        manager.execute(request, response);

        return Response.status(response.getStatus()).build();
    }

    /**
     * Update operation
     * @return
     */
    @PUT
    @Consumes({MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGECOLLECTION_JSON})
    public Response putMachineImage(CimiMachineImageCollection machineImageCo,
            @QueryParam("CIMISelect") List<String> listQueryParam) {

        CimiRequest request = new CimiRequest();
        request.setHeader(HelperRequest.buildRequestHeader(headers, uriInfo, null, machineImageCo));

        CimiResponse response = new CimiResponse();

        CimiManagerUpdateMachineImageCollection manager = getManagerMachineImageCollectionUpdate();
        manager.execute(request, response);

        return Response.status(response.getStatus()).build();

    }

    private CimiManagerUpdateMachineImageCollection getManagerMachineImageCollectionUpdate() {
        CimiManagerUpdateMachineImageCollection manager = new CimiManagerUpdateMachineImageCollection();
        return manager;
    }

    private CimiManagerCreateMachineImage getManagerMachineImageCreate() {
        CimiManagerCreateMachineImage manager = new CimiManagerCreateMachineImage();
        return manager;
    }

    private CimiManagerReadMachineImageCollection getManagerMachineImageCollectionRead() {
        CimiManagerReadMachineImageCollection manager = new CimiManagerReadMachineImageCollection();
        return manager;
    }

}
