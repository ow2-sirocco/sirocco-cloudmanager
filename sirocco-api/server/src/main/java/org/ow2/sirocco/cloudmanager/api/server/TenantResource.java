package org.ow2.sirocco.cloudmanager.api.server;

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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
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

import org.ow2.sirocco.cloudmanager.api.model.AccountAccess;
import org.ow2.sirocco.cloudmanager.api.model.MultiCloudTenant;
import org.ow2.sirocco.cloudmanager.api.model.UserTenantMembership;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@ResourceInterceptorBinding
@ManagedBean
@Path("/tenants")
public class TenantResource extends ResourceBase {
    @Inject
    private ITenantManager tenantManager;

    @Inject
    private ICloudProviderManager providerManager;

    @Context
    UriInfo uri;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public MultiCloudTenant.Collection getTenants() {
        MultiCloudTenant.Collection result = new MultiCloudTenant.Collection();
        List<MultiCloudTenant> tenants = new ArrayList<MultiCloudTenant>();
        result.setTenants(tenants);
        try {
            for (Tenant tenant : this.tenantManager.getTenants()) {
                tenants.add(this.toMultiCloudTenant(tenant));
            }
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @GET
    @Path("/{tenantId}")
    @Produces({MediaType.APPLICATION_JSON})
    public MultiCloudTenant getTenant(@PathParam("tenantId") final String tenantId) {
        try {
            Tenant tenant = this.tenantManager.getTenantById(tenantId);
            return this.toMultiCloudTenant(tenant);
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createTenant(final MultiCloudTenant apiTenant) {
        try {
            Tenant tenant = this.toTenant(apiTenant);
            this.tenantManager.createTenant(tenant);
            apiTenant.setId(tenant.getId().toString());
            apiTenant.setHref(this.uri.getBaseUri() + "providers/" + apiTenant.getId());
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Response.status(Response.Status.CREATED).entity(apiTenant).build();
    }

    @DELETE
    @Path("/{id}")
    public void deleteTenant(@PathParam("id") final String tenantId) {
        try {
            this.tenantManager.deleteTenant(tenantId);
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{tenantId}/accounts")
    @Produces({MediaType.APPLICATION_JSON})
    public AccountAccess.Collection getTenantAccounts(@PathParam("tenantId") final String tenantId) {
        AccountAccess.Collection result = new AccountAccess.Collection();
        List<AccountAccess> accountAccesses = new ArrayList<AccountAccess>();
        result.setAccountAccesses(accountAccesses);
        try {
            for (CloudProviderAccount account : this.providerManager.getCloudProviderAccountsByTenant(tenantId)) {
                AccountAccess access = new AccountAccess();
                access.setAccountId(account.getId().toString());
                access.setProviderId(account.getCloudProvider().getId().toString());
                accountAccesses.add(access);
            }
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @POST
    @Path("{tenantId}/accounts")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public void addAccountToTenant(@PathParam("tenantId") final String tenantId, final AccountAccess accountAccess) {
        try {
            this.providerManager.addCloudProviderAccountToTenant(tenantId, accountAccess.getAccountId());
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Path("{tenantId}/accounts/{accountId}")
    public void removeAccountFromTenant(@PathParam("tenantId") final String tenantId,
        @PathParam("accountId") final String accountId) {
        try {
            this.providerManager.removeCloudProviderAccountFromTenant(tenantId, accountId);
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{tenantId}/users")
    @Produces({MediaType.APPLICATION_JSON})
    public UserTenantMembership.Collection getTenantUsers(@PathParam("tenantId") final String tenantId) {
        UserTenantMembership.Collection result = new UserTenantMembership.Collection();
        List<UserTenantMembership> userTenantMemberships = new ArrayList<UserTenantMembership>();
        result.setUserTenantMemberships(userTenantMemberships);
        try {
            for (User user : this.tenantManager.getTenantUsers(tenantId)) {
                UserTenantMembership membership = new UserTenantMembership();
                membership.setTenantId(tenantId);
                membership.setUserId(user.getId().toString());
                userTenantMemberships.add(membership);
            }
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @POST
    @Path("{tenantId}/users")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public void addUserToTenant(@PathParam("tenantId") final String tenantId, final UserTenantMembership userTenantMembership) {
        try {
            this.tenantManager.addUserToTenant(tenantId, userTenantMembership.getUserId());
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (ResourceConflictException e) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Path("{tenantId}/users/{userId}")
    public void removeUserFromTenant(@PathParam("tenantId") final String tenantId, @PathParam("userId") final String userId) {
        try {
            this.tenantManager.removeUserFromTenant(tenantId, userId);
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (ResourceConflictException e) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private MultiCloudTenant toMultiCloudTenant(final Tenant tenant) {
        MultiCloudTenant result = new MultiCloudTenant();
        result.setId(tenant.getId().toString());
        result.setHref(this.uri.getBaseUri() + "tenants/" + result.getId());
        result.setName(tenant.getName());
        result.setDescription(tenant.getDescription());
        result.setCreated(tenant.getCreated());
        result.setUpdated(tenant.getUpdated());
        result.setProperties(tenant.getProperties());
        return result;
    }

    private Tenant toTenant(final MultiCloudTenant tenant) {
        Tenant result = new Tenant();
        result.setName(tenant.getName());
        result.setDescription(tenant.getDescription());
        result.setCreated(tenant.getCreated());
        result.setUpdated(tenant.getUpdated());
        result.setProperties(tenant.getProperties());
        return result;
    }

}
