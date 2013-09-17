/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
 * Contact: sirocco@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *
 */
package org.ow2.sirocco.cloudmanager.api.server;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.ow2.sirocco.cloudmanager.api.model.MultiCloudUser;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@ResourceInterceptorBinding
@RequestScoped
@Path("/users")
public class UserResource extends ResourceBase {
    @EJB
    private IUserManager userManager;

    @Context
    UriInfo uri;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public MultiCloudUser.Collection getUsers() {
        MultiCloudUser.Collection result = new MultiCloudUser.Collection();
        List<MultiCloudUser> users = new ArrayList<MultiCloudUser>();
        result.setUsers(users);
        try {
            for (User user : this.userManager.getUsers()) {
                users.add(this.toMultiCloudUser(user));
            }
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public MultiCloudUser getUser(@PathParam("id") final String userId) {
        try {
            User user = this.userManager.getUserById(userId);
            return this.toMultiCloudUser(user);
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createUser(final MultiCloudUser apiUser) {
        try {
            User user = this.toUser(apiUser);
            this.userManager.createUser(user);
            apiUser.setId(user.getId().toString());
            apiUser.setHref(this.uri.getBaseUri() + "users/" + apiUser.getId());
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Response.status(Response.Status.CREATED).entity(apiUser).build();
    }

    @DELETE
    @Path("/{userId}")
    public void deleteUser(@PathParam("userId") final String userId) {
        try {
            this.userManager.deleteUser(userId);
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private MultiCloudUser toMultiCloudUser(final User user) {
        MultiCloudUser result = new MultiCloudUser();
        result.setId(user.getId().toString());
        result.setHref(this.uri.getBaseUri() + "users/" + result.getId());
        result.setCreated(user.getCreated());
        result.setEmail(user.getEmail());
        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());
        result.setUserName(user.getUsername());
        result.setPassword(user.getPassword());
        return result;
    }

    private User toUser(final MultiCloudUser user) {
        User result = new User();
        result.setEmail(user.getEmail());
        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());
        result.setUsername(user.getUserName());
        result.setPassword(user.getPassword());
        return result;
    }

}
