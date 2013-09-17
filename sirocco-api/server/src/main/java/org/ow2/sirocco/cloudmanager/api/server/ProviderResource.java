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

import org.ow2.sirocco.cloudmanager.api.model.Location;
import org.ow2.sirocco.cloudmanager.api.model.Provider;
import org.ow2.sirocco.cloudmanager.api.model.ProviderAccount;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

@ResourceInterceptorBinding
@RequestScoped
@Path("/providers")
public class ProviderResource extends ResourceBase {
    @EJB
    private ICloudProviderManager providerManager;

    @Context
    UriInfo uri;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @ResourceInterceptorBinding
    public Provider.Collection getProviders() {
        Provider.Collection result = new Provider.Collection();
        List<Provider> providers = new ArrayList<Provider>();
        result.setProviders(providers);
        try {
            for (CloudProvider provider : this.providerManager.getCloudProviders()) {
                providers.add(this.toApiProvider(provider));
            }
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Provider getProvider(@PathParam("id") final String providerId) {
        try {
            CloudProvider provider = this.providerManager.getCloudProviderById(providerId);
            return this.toApiProvider(provider);
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createProvider(final Provider apiProvider) {
        try {
            CloudProvider provider = this.toProvider(apiProvider);
            this.providerManager.createCloudProvider(provider);
            apiProvider.setId(provider.getId().toString());
            apiProvider.setHref(this.uri.getBaseUri() + "providers/" + apiProvider.getId());
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Response.status(Response.Status.CREATED).entity(apiProvider).build();
    }

    @DELETE
    @Path("/{id}")
    public void deleteProvider(@PathParam("id") final String providerId) {
        try {
            this.providerManager.deleteCloudProvider(providerId);
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{providerId}/locations")
    @Produces({MediaType.APPLICATION_JSON})
    public Location.Collection getProviderLocations(@PathParam("providerId") final String providerId) {
        Location.Collection result = new Location.Collection();
        List<Location> locations = new ArrayList<Location>();
        result.setLocations(locations);
        try {
            CloudProvider provider = this.providerManager.getCloudProviderById(providerId);
            for (CloudProviderLocation loc : provider.getCloudProviderLocations()) {
                locations.add(ProviderResource.toLocation(loc));
            }
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @POST
    @Path("{providerId}/locations")
    @Consumes({MediaType.APPLICATION_JSON})
    public void addLocationToProvider(@PathParam("providerId") final String providerId, final Location location) {
        try {
            this.providerManager.addLocationToCloudProvider(providerId, this.toCloudProviderLocation(location));
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{providerId}/accounts")
    @Produces({MediaType.APPLICATION_JSON})
    public ProviderAccount.Collection getProviderAccounts(@PathParam("providerId") final String providerId) {
        ProviderAccount.Collection result = new ProviderAccount.Collection();
        List<ProviderAccount> accounts = new ArrayList<ProviderAccount>();
        result.setProviderAccounts(accounts);
        try {
            for (CloudProviderAccount account : this.providerManager.getCloudProviderAccountsByProvider(providerId)) {
                accounts.add(this.toApiProviderAccount(account));
            }
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @GET
    @Path("{providerId}/accounts/{accountId}")
    @Produces({MediaType.APPLICATION_JSON})
    public ProviderAccount getProviderAccount(@PathParam("providerId") final String providerId,
        @PathParam("accountId") final String accountId) {
        try {
            CloudProviderAccount account = this.providerManager.getCloudProviderAccountById(accountId);
            if (account == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            return this.toApiProviderAccount(account);
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("{providerId}/accounts")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createAccount(@PathParam("providerId") final String providerId, final ProviderAccount apiAccount) {
        try {
            CloudProviderAccount account = this.toProviderAccount(apiAccount);
            this.providerManager.createCloudProviderAccount(providerId, account);
            apiAccount.setId(account.getId().toString());
            apiAccount.setHref(this.uri.getBaseUri() + "providers/" + apiAccount.getId());
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Response.status(Response.Status.CREATED).entity(apiAccount).build();
    }

    @DELETE
    @Path("{providerId}/accounts/{accountId}")
    public void deleteProviderAccount(@PathParam("id") final String providerId, @PathParam("accountId") final String accountId) {
        try {
            this.providerManager.deleteCloudProviderAccount(accountId);
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (CloudProviderException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Provider toApiProvider(final CloudProvider provider) {
        Provider p = new Provider();
        p.setId(provider.getId().toString());
        p.setEndpoint(provider.getEndpoint());
        p.setApi(provider.getCloudProviderType());
        p.setDescription(provider.getDescription());
        p.setName(provider.getDescription());
        p.setHref(this.uri.getBaseUri() + "providers/" + p.getId());
        return p;
    }

    private CloudProvider toProvider(final Provider apiProvider) {
        CloudProvider provider = new CloudProvider();
        provider.setDescription(apiProvider.getDescription());
        provider.setEndpoint(apiProvider.getEndpoint());
        provider.setCloudProviderType(apiProvider.getApi());
        provider.setProperties(apiProvider.getProperties());
        return provider;
    }

    private ProviderAccount toApiProviderAccount(final CloudProviderAccount account) {
        ProviderAccount a = new ProviderAccount();
        a.setId(account.getId().toString());
        a.setClientId(account.getLogin());
        a.setClientSecret(account.getPassword());
        a.setProperties(account.getProperties());
        a.setHref(this.uri.getBaseUri() + "providers/" + account.getCloudProvider().getId().toString() + "/accounts/"
            + a.getId());
        return a;
    }

    private CloudProviderAccount toProviderAccount(final ProviderAccount apiAccount) {
        CloudProviderAccount account = new CloudProviderAccount();
        account.setLogin(apiAccount.getClientId());
        account.setPassword(apiAccount.getClientSecret());
        account.setProperties(apiAccount.getProperties());
        return account;
    }

    static Location toLocation(final CloudProviderLocation location) {
        Location result = new Location();
        result.setIso3166_1(location.getIso3166_1());
        result.setIso3166_2(location.getIso3166_2());
        result.setCountryName(location.getCountryName());
        result.setRegionName(location.getStateName());
        result.setCityName(location.getCityName());
        return result;
    }

    private CloudProviderLocation toCloudProviderLocation(final Location location) {
        CloudProviderLocation result = new CloudProviderLocation();
        result.setIso3166_1(location.getIso3166_1());
        result.setIso3166_2(location.getIso3166_2());
        result.setCountryName(location.getCountryName());
        result.setStateName(location.getRegionName());
        result.setCityName(location.getCityName());
        return result;
    }

}
