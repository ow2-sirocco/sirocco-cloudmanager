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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.api.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/")
@Produces("application/xml")
public interface UserAPI {
    @POST
    @Path("/users")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public UserInfo createUser(UserSpec userSpec);

    @POST
    @Path("/servers")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public VmInfo createServer(ServerSpec serverSpec);

    @GET
    @Path("/images")
    @Produces({"application/xml", "application/json"})
    public VmImageInfos listImages();

    @GET
    @Path("/images/{projectId}")
    @Produces({"application/xml", "application/json"})
    public VmImageInfos listImages(@PathParam("projectId") String projectId);

    @POST
    @Path("/volumes")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public VolumeInfo createVolume(VolumeSpec volumeSpec);

    @DELETE
    @Path("/volumes/{volumeId}")
    @Produces({"application/xml", "application/json"})
    public void destroyVolume(@PathParam("volumeId") String volumeId);

    @GET
    @Path("/volumes")
    @Produces({"application/xml", "application/json"})
    public VolumeInfos listVolumes();

    @GET
    @Path("/volumes/{projectId}")
    @Produces({"application/xml", "application/json"})
    public VolumeInfos listVolumes(@PathParam("projectId") String projectId);

    @PUT
    @Path("/volumes/{volumeId}/attach")
    @Produces({"application/xml", "application/json"})
    public void attachVolume(@PathParam("volumeId") String volumeId, VolumeAttachmentSpec spec);

    @PUT
    @Path("/volumes/{volumeId}/detach")
    @Produces({"application/xml", "application/json"})
    public void detachVolume(@PathParam("volumeId") String volumeId, VolumeAttachmentSpec spec);

    @GET
    @Path("/servers")
    @Produces({"application/xml", "application/json"})
    public VmInfos listServers();

    @GET
    @Path("/servers/{instanceId}")
    @Produces({"application/xml", "application/json"})
    public VmInfo listServers(@PathParam("instanceId") String instanceId);

    @GET
    @Path("/sizes")
    @Produces({"application/xml", "application/json"})
    public VmSizes listVmSizes();

    @PUT
    @Path("/servers/{vmInstanceId}/reboot")
    @Produces({"application/xml", "application/json"})
    public void rebootServer(@PathParam("vmInstanceId") String vmInstanceId);

    @PUT
    @Path("/sshkey")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public void sshSetPublicKey(SshKey key);

    @DELETE
    @Path("/servers/{vmInstanceId}")
    @Produces({"application/xml", "application/json"})
    public void terminateServer(@PathParam("vmInstanceId") String vmInstanceId);

    @GET
    @Path("/systemtemplates")
    @Produces({"application/xml", "application/json"})
    public SystemTemplateInfos listSystemTemplates();

    @GET
    @Path("/projects/{projectId}/systemtemplates")
    @Produces({"application/xml", "application/json"})
    public SystemTemplateInfos listSystemTemplates(@PathParam("projectId") String projectId);

    @POST
    @Path("/projects/{projectId}/systemtemplates")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public SystemTemplateInfo importOVF(@PathParam("projectId") String projectId, SystemTemplateSpec spec);

    @DELETE
    @Path("/systemtemplates/{systemTemplateId}")
    @Produces({"application/xml", "application/json"})
    public void destroySystemTemplate(@PathParam("systemTemplateId") String systemTemplateId);

    @DELETE
    @Path("/systemtemplates")
    @Produces({"application/xml", "application/json"})
    public void purgeAllSystemTemplates();

    @GET
    @Path("/systems")
    @Produces({"application/xml", "application/json"})
    public SystemInfos listSystems();

    @GET
    @Path("/projects/{projectId}/systems")
    @Produces({"application/xml", "application/json"})
    public SystemInfos listSystems(@PathParam("projectId") String projectId);

    @POST
    @Path("/projects/{projectId}/systems")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public SystemInfo createSystem(@PathParam("projectId") String projectId, SystemSpec spec);

    @PUT
    @Path("/systems/{systemId}/start")
    @Produces({"application/xml", "application/json"})
    public void startSystem(@PathParam("systemId") String systemId);

    @PUT
    @Path("/systems/{systemId}/stop")
    @Produces({"application/xml", "application/json"})
    public void stopSystem(@PathParam("systemId") String systemId);

    @DELETE
    @Path("/systems/{systemId}/start")
    @Produces({"application/xml", "application/json"})
    public void destroySystem(@PathParam("systemId") String systemId);

    @DELETE
    @Path("/machines")
    @Produces({"application/xml", "application/json"})
    public void purgeAllMachines();

    @DELETE
    @Path("/systems")
    @Produces({"application/xml", "application/json"})
    public void purgeAllSystems();

    @GET
    @Path("/projects")
    @Produces({"application/xml", "application/json"})
    public ProjectInfos listProjects();

    @POST
    @Path("/metricsinfo")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public PerfMetricInfos listPerfMetricInfos(PerfMetricInfoSpec target);

    @POST
    @Path("/metrics")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public PerfMetrics getPerfMetrics(PerfMetricSpec request);

    @GET
    @Path("/cloudproviders")
    @Produces({"application/xml", "application/json"})
    public CloudProviderInfos listCloudProviders();

    @POST
    @Path("/cloudproviders")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public CloudProviderInfo createCloudProvider(CloudProviderSpec cloudProviderSpec);

    @DELETE
    @Path("/cloudproviders/{providerId}")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public void deleteCloudProvider(@PathParam("providerId") String providerId);

    @GET
    @Path("/cloudproviders/{providerId}/accounts")
    @Produces({"application/xml", "application/json"})
    public CloudProviderAccountInfos listCloudProviderAccounts(@PathParam("providerId") String providerId);

    @GET
    @Path("/projects/{projectId}/cloudprovideraccounts")
    @Produces({"application/xml", "application/json"})
    public CloudProviderAccountInfos listCloudProviderAccountsByProject(@PathParam("projectId") String projectId);

    @POST
    @Path("/cloudproviders/{providerId}/accounts")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public CloudProviderAccountInfo createCloudProviderAccount(@PathParam("providerId") String providerId,
        CloudProviderAccountSpec cloudProviderAccountSpec);

    @DELETE
    @Path("/projects/{projectId}/cloudprovideraccounts/{accountId")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public void deleteCloudProviderAccount(@PathParam("projectId") String projectId, @PathParam("accountId") String accountId);

    @PUT
    @Path("/cloudprovideraccounts/{accountId}/associate")
    @Produces({"application/xml", "application/json"})
    public void associateCloudProviderAccountWithProject(@PathParam("accountId") String accountId,
        CloudProviderAccountAssociationSpec spec);

    @PUT
    @Path("/cloudprovideraccounts/{accountId}/dissociate")
    @Produces({"application/xml", "application/json"})
    public void dissociateCloudProviderAccountFromProject(@PathParam("accountId") String accountId,
        CloudProviderAccountAssociationSpec spec);

}
