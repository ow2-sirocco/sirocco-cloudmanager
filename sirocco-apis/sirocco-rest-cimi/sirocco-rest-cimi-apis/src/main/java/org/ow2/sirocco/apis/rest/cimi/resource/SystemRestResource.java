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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystem;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemCredential;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemSystem;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiSystemVolume;
import org.ow2.sirocco.apis.rest.cimi.manager.CimiManager;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.ContextHelper;
import org.ow2.sirocco.apis.rest.cimi.request.IdRequest;
import org.ow2.sirocco.apis.rest.cimi.request.ResponseHelper;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

// TODO SystemNetwork, SystemNetworkPort, SystemAddress, SystemForwardingGroup, SystemMeter
/**
 * System REST resource.
 * <p>
 * Operations supports :
 * <ul>
 * <li>Create a system</li>
 * <li>Delete a system</li>
 * <li>Read a system</li>
 * <li>Read a collection of systems</li>
 * <li>Update a system</li>
 * </ul>
 * </p>
 */
@Component
@Path(ConstantsPath.SYSTEM_PATH)
public class SystemRestResource extends RestResourceAbstract {

    @Autowired
    @Qualifier("CimiManagerReadSystem")
    private CimiManager cimiManagerReadSystem;

    @Autowired
    @Qualifier("CimiManagerReadSystemCollection")
    private CimiManager cimiManagerReadSystemCollection;

    @Autowired
    @Qualifier("CimiManagerDeleteSystem")
    private CimiManager cimiManagerDeleteSystem;

    @Autowired
    @Qualifier("CimiManagerUpdateSystem")
    private CimiManager cimiManagerUpdateSystem;

    @Autowired
    @Qualifier("CimiManagerCreateSystem")
    private CimiManager cimiManagerCreateSystem;

    @Autowired
    @Qualifier("CimiManagerActionSystem")
    private CimiManager cimiManagerActionSystem;

    @Autowired
    @Qualifier("CimiManagerReadSystemEntity")
    private CimiManager cimiManagerReadSystemEntity;

    @Autowired
    @Qualifier("CimiManagerCreateSystemEntity")
    private CimiManager cimiManagerCreateSystemEntity;

    @Autowired
    @Qualifier("CimiManagerDeleteSystemEntity")
    private CimiManager cimiManagerDeleteSystemEntity;

    @Autowired
    @Qualifier("CimiManagerUpdateSystemEntity")
    private CimiManager cimiManagerUpdateSystemEntity;

    @Autowired
    @Qualifier("CimiManagerReadSystemCredentialCollection")
    private CimiManager cimiManagerReadSystemCredentialCollection;

    @Autowired
    @Qualifier("CimiManagerReadSystemMachineCollection")
    private CimiManager cimiManagerReadSystemMachineCollection;

    @Autowired
    @Qualifier("CimiManagerReadSystemSystemCollection")
    private CimiManager cimiManagerReadSystemSystemCollection;

    @Autowired
    @Qualifier("CimiManagerReadSystemVolumeCollection")
    private CimiManager cimiManagerReadSystemVolumeCollection;

    /**
     * Get a system.
     * 
     * @param id The ID of system to get
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response read(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerReadSystem.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Get a collection of systems.
     * 
     * @return The REST response
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response read() {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos());
        this.cimiManagerReadSystemCollection.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Update a system.
     * 
     * @param id The ID of system to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}")
    public Response update(@PathParam("id") final String id, final CimiSystem cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id, cimiData);
        this.cimiManagerUpdateSystem.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Create a system.
     * 
     * @return The REST response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(final CimiSystemCreate cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), cimiData);
        this.cimiManagerCreateSystem.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Actions on system.
     * 
     * @return The REST response
     */
    @POST
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response action(@PathParam("id") final String id, final CimiAction cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id, cimiData);
        this.cimiManagerActionSystem.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Delete a system.
     * 
     * @param id The ID of system to delete
     * @return The REST response
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), id);
        this.cimiManagerDeleteSystem.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Read a collection of credentials of a system.
     * 
     * @param idParent ID system
     * @return The REST response
     */
    @GET
    @Path("{idParent}" + ConstantsPath.CREDENTIAL_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response readCredentials(@PathParam("idParent") final String idParent) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(null, idParent));
        this.cimiManagerReadSystemCredentialCollection.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Read a credential of a system.
     * 
     * @param idParent ID system
     * @param id ID credential to read
     * @return The REST response
     */
    @GET
    @Path("/{idParent}" + ConstantsPath.CREDENTIAL_PATH + "/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response readCredential(@PathParam("idParent") final String idParent, @PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent));
        this.cimiManagerReadSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Update a credential of a system.
     * 
     * @param idParent ID system
     * @param id ID credential to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{idParent}" + ConstantsPath.CREDENTIAL_PATH + "/{id}")
    public Response updateCredential(@PathParam("idParent") final String idParent, @PathParam("id") final String id,
        final CimiSystemCredential cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent), cimiData);
        this.cimiManagerUpdateSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Create a credential of a system.
     * 
     * @param idParent ID system
     * @return The REST response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{idParent}" + ConstantsPath.CREDENTIAL_PATH)
    public Response createCredential(@PathParam("idParent") final String idParent, final CimiSystemCredential cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(null, idParent), cimiData);
        this.cimiManagerCreateSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Delete a credential of a system.
     * 
     * @param idParent ID system
     * @param id ID credential to delete
     * @return The REST response
     */
    @DELETE
    @Path("/{idParent}" + ConstantsPath.CREDENTIAL_PATH + "/{id}")
    public Response deleteCredential(@PathParam("idParent") final String idParent, @PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent));
        this.cimiManagerDeleteSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Read a collection of machines of a system.
     * 
     * @param idParent ID system
     * @return The REST response
     */
    @GET
    @Path("{idParent}" + ConstantsPath.MACHINE_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response readMachines(@PathParam("idParent") final String idParent) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(null, idParent));
        this.cimiManagerReadSystemMachineCollection.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Read a machine of a system.
     * 
     * @param idParent ID system
     * @param id ID machine to read
     * @return The REST response
     */
    @GET
    @Path("/{idParent}" + ConstantsPath.MACHINE_PATH + "/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response readMachine(@PathParam("idParent") final String idParent, @PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent));
        this.cimiManagerReadSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Update a machine of a system.
     * 
     * @param idParent ID system
     * @param id ID machine to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{idParent}" + ConstantsPath.MACHINE_PATH + "/{id}")
    public Response updateMachine(@PathParam("idParent") final String idParent, @PathParam("id") final String id,
        final CimiSystemMachine cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent), cimiData);
        this.cimiManagerUpdateSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Create a machine of a system.
     * 
     * @param idParent ID system
     * @return The REST response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{idParent}" + ConstantsPath.MACHINE_PATH)
    public Response createMachine(@PathParam("idParent") final String idParent, final CimiSystemMachine cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(null, idParent), cimiData);
        this.cimiManagerCreateSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Delete a machine of a system.
     * 
     * @param idParent ID system
     * @param id ID machine to delete
     * @return The REST response
     */
    @DELETE
    @Path("/{idParent}" + ConstantsPath.MACHINE_PATH + "/{id}")
    public Response deleteMachine(@PathParam("idParent") final String idParent, @PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent));
        this.cimiManagerDeleteSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Read a collection of systems of a system.
     * 
     * @param idParent ID system
     * @return The REST response
     */
    @GET
    @Path("{idParent}" + ConstantsPath.SYSTEM_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response readSystems(@PathParam("idParent") final String idParent) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(null, idParent));
        this.cimiManagerReadSystemSystemCollection.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Read a system of a system.
     * 
     * @param idParent ID system
     * @param id ID system to read
     * @return The REST response
     */
    @GET
    @Path("/{idParent}" + ConstantsPath.SYSTEM_PATH + "/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response readSystem(@PathParam("idParent") final String idParent, @PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent));
        this.cimiManagerReadSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Update a system of a system.
     * 
     * @param idParent ID system
     * @param id ID system to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{idParent}" + ConstantsPath.SYSTEM_PATH + "/{id}")
    public Response updateSystem(@PathParam("idParent") final String idParent, @PathParam("id") final String id,
        final CimiSystemSystem cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent), cimiData);
        this.cimiManagerUpdateSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Create a system of a system.
     * 
     * @param idParent ID system
     * @return The REST response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{idParent}" + ConstantsPath.SYSTEM_PATH)
    public Response createSystem(@PathParam("idParent") final String idParent, final CimiSystemSystem cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(null, idParent), cimiData);
        this.cimiManagerCreateSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Delete a system of a system.
     * 
     * @param idParent ID system
     * @param id ID system to delete
     * @return The REST response
     */
    @DELETE
    @Path("/{idParent}" + ConstantsPath.SYSTEM_PATH + "/{id}")
    public Response deleteSystem(@PathParam("idParent") final String idParent, @PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent));
        this.cimiManagerDeleteSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Read a collection of volumes of a system.
     * 
     * @param idParent ID system
     * @return The REST response
     */
    @GET
    @Path("{idParent}" + ConstantsPath.VOLUME_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response readVolumes(@PathParam("idParent") final String idParent) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(null, idParent));
        this.cimiManagerReadSystemVolumeCollection.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Read a volume of a system.
     * 
     * @param idParent ID system
     * @param id ID volume to read
     * @return The REST response
     */
    @GET
    @Path("/{idParent}" + ConstantsPath.VOLUME_PATH + "/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response readVolume(@PathParam("idParent") final String idParent, @PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent));
        this.cimiManagerReadSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Update a volume of a system.
     * 
     * @param idParent ID system
     * @param id ID volume to update
     * @return The REST response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{idParent}" + ConstantsPath.VOLUME_PATH + "/{id}")
    public Response updateVolume(@PathParam("idParent") final String idParent, @PathParam("id") final String id,
        final CimiSystemVolume cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent), cimiData);
        this.cimiManagerUpdateSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Create a volume of a system.
     * 
     * @param idParent ID system
     * @return The REST response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{idParent}" + ConstantsPath.VOLUME_PATH)
    public Response createVolume(@PathParam("idParent") final String idParent, final CimiSystemVolume cimiData) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(null, idParent), cimiData);
        this.cimiManagerCreateSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

    /**
     * Delete a volume of a system.
     * 
     * @param idParent ID system
     * @param id ID volume to delete
     * @return The REST response
     */
    @DELETE
    @Path("/{idParent}" + ConstantsPath.VOLUME_PATH + "/{id}")
    public Response deleteVolume(@PathParam("idParent") final String idParent, @PathParam("id") final String id) {
        CimiContext context = ContextHelper.buildContext(this.getJaxRsRequestInfos(), new IdRequest(id, idParent));
        this.cimiManagerDeleteSystemEntity.execute(context);
        return ResponseHelper.buildResponse(context.getResponse());
    }

}
