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

package org.ow2.sirocco.cloudmanager.api.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.ow2.sirocco.cloudmanager.api.spec.CloudProviderAccountAssociationSpec;
import org.ow2.sirocco.cloudmanager.api.spec.CloudProviderAccountInfo;
import org.ow2.sirocco.cloudmanager.api.spec.CloudProviderAccountInfos;
import org.ow2.sirocco.cloudmanager.api.spec.CloudProviderAccountSpec;
import org.ow2.sirocco.cloudmanager.api.spec.CloudProviderInfo;
import org.ow2.sirocco.cloudmanager.api.spec.CloudProviderInfos;
import org.ow2.sirocco.cloudmanager.api.spec.CloudProviderSpec;
import org.ow2.sirocco.cloudmanager.api.spec.ErrorReport;
import org.ow2.sirocco.cloudmanager.api.spec.PerfMetric;
import org.ow2.sirocco.cloudmanager.api.spec.PerfMetricInfo;
import org.ow2.sirocco.cloudmanager.api.spec.PerfMetricInfoSpec;
import org.ow2.sirocco.cloudmanager.api.spec.PerfMetricInfos;
import org.ow2.sirocco.cloudmanager.api.spec.PerfMetricSpec;
import org.ow2.sirocco.cloudmanager.api.spec.PerfMetrics;
import org.ow2.sirocco.cloudmanager.api.spec.ProjectInfo;
import org.ow2.sirocco.cloudmanager.api.spec.ProjectInfos;
import org.ow2.sirocco.cloudmanager.api.spec.ServerSpec;
import org.ow2.sirocco.cloudmanager.api.spec.SshKey;
import org.ow2.sirocco.cloudmanager.api.spec.SystemInfo;
import org.ow2.sirocco.cloudmanager.api.spec.SystemInfos;
import org.ow2.sirocco.cloudmanager.api.spec.SystemSpec;
import org.ow2.sirocco.cloudmanager.api.spec.SystemTemplateInfo;
import org.ow2.sirocco.cloudmanager.api.spec.SystemTemplateInfos;
import org.ow2.sirocco.cloudmanager.api.spec.SystemTemplateSpec;
import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;
import org.ow2.sirocco.cloudmanager.api.spec.UserInfo;
import org.ow2.sirocco.cloudmanager.api.spec.UserSpec;
import org.ow2.sirocco.cloudmanager.api.spec.VmImageInfo;
import org.ow2.sirocco.cloudmanager.api.spec.VmImageInfos;
import org.ow2.sirocco.cloudmanager.api.spec.VmInfo;
import org.ow2.sirocco.cloudmanager.api.spec.VmInfos;
import org.ow2.sirocco.cloudmanager.api.spec.VmSize;
import org.ow2.sirocco.cloudmanager.api.spec.VmSizes;
import org.ow2.sirocco.cloudmanager.api.spec.VolumeAttachmentSpec;
import org.ow2.sirocco.cloudmanager.api.spec.VolumeInfo;
import org.ow2.sirocco.cloudmanager.api.spec.VolumeInfos;
import org.ow2.sirocco.cloudmanager.api.spec.VolumeSpec;
import org.ow2.sirocco.cloudmanager.common.jndilocator.ServiceLocator;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProvider;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.entity.NetworkInterface;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Project;
import org.ow2.sirocco.cloudmanager.provider.api.entity.SystemInstance;
import org.ow2.sirocco.cloudmanager.provider.api.entity.SystemTemplate;
import org.ow2.sirocco.cloudmanager.provider.api.entity.User;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Volume;
import org.ow2.sirocco.cloudmanager.provider.api.entity.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.PerfMetricInfoVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.PerfMetricVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VirtualMachineSpec;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.DuplicateNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InternalErrorException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineConfigurationException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineImageException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidMachineStateException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidNameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidPerfMetricIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidPowerStateException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidProjectIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidSystemInstanceStatusException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidSystemTemplateStatusException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidUsernameException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidVolumeIdException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.OVFImporterException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.PermissionDeniedException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.ResourceQuotaExceededException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.UserAlreadyExistsException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.VolumeInUseException;
import org.ow2.sirocco.cloudmanager.provider.api.service.VolumeCreate;
import org.ow2.sirocco.cloudmanager.service.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.service.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.service.api.IMonitoringManager;
import org.ow2.sirocco.cloudmanager.service.api.IMonitoringManager.Entity;
import org.ow2.sirocco.cloudmanager.service.api.IOVFImporter;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteUserProjectManager;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteVolumeManager;
import org.ow2.sirocco.cloudmanager.service.api.ISystemInstanceManager;
import org.ow2.sirocco.cloudmanager.service.api.ISystemTemplateManager;
import org.ow2.sirocco.cloudmanager.service.api.IUserProjectManager;
import org.ow2.sirocco.cloudmanager.service.api.IVolumeManager;

public class UserAPIImpl implements UserAPI {
    private static final Logger Log = Logger.getLogger(UserAPIImpl.class.getName());

    private transient IRemoteMachineManager machineManager;

    private transient IRemoteUserProjectManager userProjectManager;

    private transient IRemoteVolumeManager volumeManager;

    private transient IMonitoringManager monitoringManager;

    private transient ICloudProviderManager cloudProviderManager;

    private transient ISystemInstanceManager systemManager;

    private transient ISystemTemplateManager systemTemplateManager;

    private transient IOVFImporter ovfImporter;

    @Context
    private HttpHeaders httpHeaders;

    public UserAPIImpl() {
        this.initialize();
    }

    private void initialize() {
        this.machineManager = (IRemoteMachineManager) ServiceLocator.getInstance().getRemoteObject(
            IMachineManager.EJB_JNDI_NAME);
        this.userProjectManager = (IRemoteUserProjectManager) ServiceLocator.getInstance().getRemoteObject(
            IUserProjectManager.EJB_JNDI_NAME);
        this.volumeManager = (IRemoteVolumeManager) ServiceLocator.getInstance().getRemoteObject(IVolumeManager.EJB_JNDI_NAME);
        this.monitoringManager = (IMonitoringManager) ServiceLocator.getInstance().getRemoteObject(
            IMonitoringManager.EJB_JNDI_NAME);
        this.cloudProviderManager = (ICloudProviderManager) ServiceLocator.getInstance().getRemoteObject(
            ICloudProviderManager.EJB_JNDI_NAME);
        this.systemManager = (ISystemInstanceManager) ServiceLocator.getInstance().getRemoteObject(
            ISystemInstanceManager.EJB_JNDI_NAME);
        this.systemTemplateManager = (ISystemTemplateManager) ServiceLocator.getInstance().getRemoteObject(
            ISystemTemplateManager.EJB_JNDI_NAME);
        this.ovfImporter = (IOVFImporter) ServiceLocator.getInstance().getRemoteObject(IOVFImporter.EJB_JNDI_NAME);
    }

    private String getUserName() {
        return this.decodeBasicHTTPAuthentificationHeader(this.httpHeaders.getRequestHeader("Authorization").get(0))[0];
    }

    private void throwWebApplicationException(final ErrorReport.ErrorCode errorCode, final String message) {
        Response resp = Response.status(Response.Status.BAD_REQUEST).entity(new ErrorReport(errorCode, message)).build();
        throw new WebApplicationException(resp);
    }

    @POST
    @Path("/users")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public UserInfo createUser(final UserSpec userSpec) {
        UserAPIImpl.Log.info("Executing operation createUser");

        String fromUsername = this.getUserName();
        if (!this.userProjectManager.getUserByUsername(fromUsername).getAdmin()) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission denied!");
        }

        User user = null;
        try {
            user = this.userProjectManager.createUser(userSpec.getUsername(), userSpec.getPassword(), userSpec.getUserMail(),
                userSpec.getUserFirstName(), userSpec.getUserLastName());
        } catch (UserAlreadyExistsException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.USERNAME_ALREADY_EXISTS, "Username already exists");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(userSpec.getUsername());
        userInfo.setApiPassword(user.getApiPassword());
        return userInfo;
    }

    @POST
    @Path("/servers")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public VmInfo createServer(final ServerSpec serverSpec) {
        UserAPIImpl.Log.info("Executing operation createServer");

        String userName = this.getUserName();

        VirtualMachineSpec spec = new VirtualMachineSpec();
        spec.setVmName(serverSpec.getName());
        spec.setSizeName(serverSpec.getSize());
        spec.setImageId(serverSpec.getImageId());
        spec.setUserData(serverSpec.getUserData());

        if (serverSpec.getReservationId() == null) {
            spec.setReservationId(null);
        } else {
            spec.setReservationId(new Integer(serverSpec.getReservationId()));
        }

        String cloudProviderAccountId = serverSpec.getCloudProviderAccountId();

        // TODO Fix this hardcoded value.
        boolean startVm = true;

        Machine vm = null;
        try {
            vm = this.machineManager.createMachine(serverSpec.getProjectId(), userName, spec, cloudProviderAccountId, startVm);
        } catch (InvalidUsernameException ex) {
            // should not happen
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error");
        } catch (InvalidNameException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VM_NAME, "The provided VM name is invalid");
        } catch (InvalidMachineImageException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VM_IMAGE_ID, "The provided VM image ID is invalid");
        } catch (ResourceQuotaExceededException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.RESOURCE_QUOTA_EXCEEDED, "Resource quota exceeded");
        } catch (DuplicateNameException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.DUPLICATE_VM_NAME, "Duplicate VM name");
        } catch (InvalidMachineConfigurationException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VM_CLASS, "Invalid VM size: " + spec.getSizeName());
        } catch (InvalidProjectIdException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID,
                "Invalid projectId : " + serverSpec.getProjectId());
        } catch (PermissionDeniedException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission denied for user " + userName);
        } catch (CloudProviderException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error " + ex.getMessage());
        }

        VmInfo vmInfo = new VmInfo();
        vmInfo.setImageId(serverSpec.getImageId());
        vmInfo.setInstanceId(vm.getId().toString());
        vmInfo.setProjectId(serverSpec.getProjectId());
        vmInfo.setPrivateIp("pending");
        vmInfo.setSize(serverSpec.getSize());
        vmInfo.setStatus(Machine.State.CREATING.toString());
        vmInfo.setUpTime(0); // XX

        return vmInfo;
    }

    @GET
    @Path("/images/{projectId}")
    @Produces({"application/xml", "application/json"})
    public VmImageInfos listImages(@PathParam("projectId") String projectId) {
        UserAPIImpl.Log.info("Executing operation listImages");

        String userName = this.getUserName();

        VmImageInfos result = new VmImageInfos();
        result.setImage(new ArrayList<VmImageInfo>());

        if (projectId == null) {
            projectId = userName; // XXX projectId = userName
        }

        List<MachineImage> images = null;
        try {
            images = this.userProjectManager.getVMImagesByProjectId(projectId);
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID, "Invalid projectId");
        }

        for (MachineImage image : images) {
            result.getImage().add(this.toVmImageInfo(image));
        }
        return result;
    }

    @GET
    @Path("/images")
    @Produces({"application/xml", "application/json"})
    public VmImageInfos listImages() {
        return this.listImages(null);
    }

    @GET
    @Path("/servers")
    @Produces({"application/xml", "application/json"})
    public VmInfos listServers() {
        return this.listServers2(null);
    }

    @GET
    @Path("/servers/{instanceId}")
    @Produces({"application/xml", "application/json"})
    public VmInfo listServers(@PathParam("instanceId") final String instanceId) {
        VmInfos vmInfos = this.listServers2(instanceId);
        return vmInfos.getServer().iterator().next();
    }

    private VmInfos listServers2(@PathParam("instanceId") final String instanceId) {
        String userName = this.getUserName();
        UserAPIImpl.Log.info("Executing operation listServers for user " + userName);

        VmInfos result = new VmInfos();
        result.setServer(new ArrayList<VmInfo>());

        List<Machine> vmList = null;
        if (instanceId != null) {
            vmList = new ArrayList<Machine>();
            Machine vm = null;
            vm = this.machineManager.getMachineById(instanceId);
            if (vm == null) {
                this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VM_INSTANCE_ID, "Invalid VM id: " + instanceId);
            }
            vmList.add(vm);
        } else {
            try {
                vmList = this.userProjectManager.getVirtualMachinesByUsername(userName, false);
            } catch (InvalidUsernameException ex) {
                this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_USERNAME, "Invalid username");
            }
        }

        for (Machine vm : vmList) {
            VmInfo vmInfo = this.toVmInfo(vm);
            try {
                vmInfo.setSize(this.machineManager.getMachineConfigurationName(vm.getId().toString()));
            } catch (InvalidMachineIdException ex) {
                UserAPIImpl.Log.log(Level.WARNING, " instanceId=" + vm.getId(), ex);
                this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VM_INSTANCE_ID, "Invalid VM id");
            }

            String state = "";
            switch (vm.getState()) {
            case CREATING:
                state = "CREATING";
                break;
            case DELETED:
                state = "DELETED";
                break;
            case ERROR:
                state = "ERROR";
                break;
            case STARTED:
                state = "STARTED";
                long uptime = (System.currentTimeMillis() - vm.getCreated().getTime()) / 1000;
                vmInfo.setUpTime(uptime);
                break;
            case STARTING:
                state = "STARTING";
                break;
            case STOPPED:
                state = "STOPPED";
                break;
            case STOPPING:
                state = "STOPPING";
                break;
            }
            vmInfo.setStatus(state);
            result.getServer().add(vmInfo);
        }

        return result;
    }

    @GET
    @Path("/sizes")
    @Produces({"application/xml", "application/json"})
    public VmSizes listVmSizes() {
        UserAPIImpl.Log.info("Executing operation listVmSizes");

        VmSizes result = new VmSizes();
        result.setSize(new ArrayList<VmSize>());

        List<MachineConfiguration> vmSizes = this.machineManager.getMachineConfigurations();
        for (MachineConfiguration vmSize : vmSizes) {
            result.getSize().add(this.toVmSize(vmSize));
        }
        return result;
    }

    @PUT
    @Path("/servers/{vmInstanceId}/reboot")
    @Produces({"application/xml", "application/json"})
    public void rebootServer(@PathParam("vmInstanceId") final String vmInstanceId) {

        UserAPIImpl.Log.info("Executing operation rebootServer");
        String fromUsername = this.getUserName();

        try {
            this.machineManager.restartMachine(vmInstanceId, fromUsername);
        } catch (InvalidMachineIdException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VM_INSTANCE_ID, "Invalid VM id: " + vmInstanceId);
        } catch (InvalidPowerStateException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_POWER_STATE, "Invalid power state!");
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission Denied!");
        }
    }

    @PUT
    @Path("/sshkey")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public void sshSetPublicKey(final SshKey key) {
        UserAPIImpl.Log.info("Executing operation sshSetPublicKey");

        String userName = this.getUserName();

        try {
            this.userProjectManager.updateUserSSHKey(userName, key.getValue());
        } catch (InvalidUsernameException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_USERNAME, "Invalid username!");
        }
    }

    @DELETE
    @Path("/servers/{vmInstanceId}")
    @Produces({"application/xml", "application/json"})
    public void terminateServer(@PathParam("vmInstanceId") final String vmInstanceId) {
        UserAPIImpl.Log.info("Executing operation terminateServer");

        String fromUsername = this.getUserName();

        try {
            this.machineManager.deleteMachine(fromUsername, vmInstanceId);
        } catch (InvalidMachineIdException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VM_INSTANCE_ID, "Invalid VM id: " + vmInstanceId);
        } catch (InvalidPowerStateException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_POWER_STATE, "Invalid power state!");
        } catch (PermissionDeniedException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission denied!");
        } catch (InvalidArgumentException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, "Invalid argument " + e.getMessage());
        } catch (InvalidUsernameException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_USERNAME, "Invalid username! " + e.getMessage());
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID, "Invalid project id! " + e.getMessage());
        } catch (InvalidMachineStateException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VM_STATE, "Invalid vm state! " + e.getMessage());
        } catch (InternalErrorException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR,
                "Internal error exception! " + e.getMessage());
        }
    }

    @GET
    @Path("/projects")
    @Produces({"application/xml", "application/json"})
    public ProjectInfos listProjects() {
        UserAPIImpl.Log.info("Executing operation listProjects");

        String userName = this.getUserName();

        ProjectInfos result = new ProjectInfos();
        result.setProjectInfos((new ArrayList<ProjectInfo>()));

        List<Project> projectList = null;
        try {
            projectList = this.userProjectManager.getProjectsByUsername(userName);
        } catch (InvalidUsernameException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_USERNAME, "Invalid username");
        }

        for (Project project : projectList) {
            result.getProjectInfos().add(this.toProjectInfo(project));
        }
        return result;
    }

    private String[] decodeBasicHTTPAuthentificationHeader(final String authHeader) {
        if (authHeader == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(authHeader);
        if (st.hasMoreTokens()) {
            String basic = st.nextToken();

            if (basic.equalsIgnoreCase("Basic")) {
                String credentials = st.nextToken();

                String userPass = null;
                userPass = new String(Base64Coder.decode(credentials));

                int p = userPass.indexOf(":");
                if (p != -1) {
                    String result[] = new String[2];
                    result[0] = userPass.substring(0, p);
                    result[1] = userPass.substring(p + 1);
                    return result;
                }
            }
        }
        return null;
    }

    private VmInfo toVmInfo(final Machine from) {
        VmInfo vm = new VmInfo();
        vm.setName(from.getName());
        vm.setInstanceId(from.getId().toString());
        vm.setImageId(from.getImage().getId());
        if (!from.getNetworkInterfaces().isEmpty()) {
            Iterator<NetworkInterface> it = from.getNetworkInterfaces().iterator();
            NetworkInterface nic = it.next();
            vm.setPrivateIp(nic.getAddress());
            vm.setPrivateHostName(nic.getHostname());
            if (it.hasNext()) {
                nic = it.next();
                vm.setPublicIp(nic.getAddress());
                vm.setPublicHostName(nic.getHostname());
            }
        }

        vm.setProjectId(from.getProject().getProjectId());
        vm.setCloudProviderAccountId(from.getCloudProviderAccount().getId().toString());
        return vm;
    }

    private SystemInfo toSystemInfo(final SystemInstance from) {
        SystemInfo system = new SystemInfo();
        system.setId(from.getId().toString());
        system.setProjectId(from.getProject().getProjectId());
        system.setStatus(from.getState().toString());
        return system;
    }

    private SystemTemplateInfo toSystemTemplateInfo(final SystemTemplate from) {
        SystemTemplateInfo systemTemplate = new SystemTemplateInfo();
        systemTemplate.setId(from.getId().toString());
        systemTemplate.setName(from.getName());
        systemTemplate.setProjectId(from.getProject().getProjectId());
        systemTemplate.setStatus(from.getState().toString());
        return systemTemplate;
    }

    private VolumeInfo toVolumeInfo(final Volume from) {
        VolumeInfo volInfo = new VolumeInfo();
        volInfo.setBootable(from.getBootable());
        volInfo.setCapacityInMB(from.getCapacityInMB());
        volInfo.setDescription(from.getDescription());
        volInfo.setId(from.getId().toString());
        volInfo.setName(from.getName());
        if (from.getProject() != null) {
            volInfo.setProjectId(from.getProject().getProjectId());
        }
        if (from.getUser() != null) {
            volInfo.setUser(from.getUser().getUsername());
        }
        volInfo.setStatus(from.getState().toString());
        return volInfo;
    }

    private VmImageInfo toVmImageInfo(final MachineImage from) {
        VmImageInfo image = new VmImageInfo();
        image.setImageId(from.getId());
        image.setName(from.getName());
        image.setDescription(from.getDescription());
        if (from.getProject() != null) {
            image.setProjectId(from.getProject().getProjectId());
        }
        image.setVisibility(from.getVisibility().toString());
        return image;
    }

    private VmSize toVmSize(final MachineConfiguration from) {
        VmSize size = new VmSize();
        size.setName(from.getName());
        size.setNumCpu(from.getNumCPUs());
        size.setMemorySizeMB(from.getMemorySizeMB());
        return size;
    }

    private ProjectInfo toProjectInfo(final Project from) {
        ProjectInfo project = new ProjectInfo();
        project.setProjectId(from.getProjectId());
        project.setName(from.getName());
        project.setDescription(from.getDescription());
        return project;
    }

    private CloudProviderInfo toCloudProviderInfo(final CloudProvider from) {
        CloudProviderInfo cp = new CloudProviderInfo();
        cp.setDescription(from.getDescription());
        cp.setId(from.getId().toString());
        cp.setName(from.getCloudProviderType());
        return cp;
    }

    private CloudProviderAccountInfo toCloudProviderAccountInfo(final CloudProviderAccount from) {
        CloudProviderAccountInfo account = new CloudProviderAccountInfo();
        account.setId(from.getId().toString());
        account.setLogin(from.getLogin());
        account.setPassword(from.getPassword());
        account.setCloudProviderType(from.getCloudProvider().getCloudProviderType());
        return account;
    }

    @PUT
    @Path("/volumes/{volumeId}/attach")
    @Produces({"application/xml", "application/json"})
    public void attachVolume(@PathParam("volumeId") final String volumeId, final VolumeAttachmentSpec spec) {
        UserAPIImpl.Log.info("Executing operation attachVolume");
        try {
            this.machineManager.attachVolumeToMachine(this.getUserName(), volumeId, spec.getVmId());
        } catch (InvalidUsernameException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal error");
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID, "The provided projet Id is invalid");
        } catch (InvalidVolumeIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VOLUME_ID, "VolumeId " + volumeId + " is invalid");
        } catch (InvalidMachineIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VM_INSTANCE_ID, "Invalid VM id: " + spec.getVmId());
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission denied");
        } catch (VolumeInUseException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.VOLUME_IN_USE, "VolumeId " + volumeId
                + " is already attached");
        } catch (CloudProviderException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    @PUT
    @Path("/volumes/{volumeId}/detach")
    @Produces({"application/xml", "application/json"})
    public void detachVolume(@PathParam("volumeId") final String volumeId, final VolumeAttachmentSpec spec) {
        UserAPIImpl.Log.info("Executing operation detachVolume");
        try {
            this.machineManager.detachVolumeFromMachine(this.getUserName(), volumeId, spec.getVmId());
        } catch (InvalidUsernameException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal error");
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID, "The provided projet Id is invalid");
        } catch (InvalidVolumeIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VOLUME_ID, "VolumeId " + volumeId + " is invalid");
        } catch (InvalidMachineIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VM_INSTANCE_ID, "Invalid VM id: " + spec.getVmId());
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission denied");
        } catch (CloudProviderException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, e.getMessage());
        }

    }

    @POST
    @Path("/volumes")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public VolumeInfo createVolume(final VolumeSpec volumeSpec) {
        UserAPIImpl.Log.info("Executing operation createVolume");
        String userName = this.getUserName();
        Volume volume = null;

        VolumeCreate volumeCreate = new VolumeCreate();
        volumeCreate.setName(volumeSpec.getName());
        volumeCreate.setDescription(volumeSpec.getDescription());
        VolumeConfiguration volumeConfiguration = new VolumeConfiguration();
        volumeConfiguration.setCapacityInMB(volumeSpec.getCapacityInMB());
        volumeCreate.setConfiguration(volumeConfiguration);

        CloudProviderLocation location = null;
        if (volumeSpec.getLocation() != null) {
            location = new CloudProviderLocation(volumeSpec.getLocation());
        }

        try {
            volume = this.volumeManager.createVolume(volumeSpec.getProjectId(), userName,
                volumeSpec.getCloudProviderAccountId(), location, volumeCreate);
        } catch (InvalidUsernameException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal error");
        } catch (InvalidNameException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VM_NAME, "The provided volume name is invalid");
        } catch (ResourceQuotaExceededException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.RESOURCE_QUOTA_EXCEEDED, "Resource quota exceeded");
        } catch (DuplicateNameException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.DUPLICATE_VM_NAME, "The provided volume name is invalid");
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID, "The provided projet Id is invalid");
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission denied");
        } catch (CloudProviderException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, e.getMessage());
        }

        return this.toVolumeInfo(volume);
    }

    @DELETE
    @Path("/volumes/{volumeId}")
    @Produces({"application/xml", "application/json"})
    public void destroyVolume(@PathParam("volumeId") final String volumeId) {
        UserAPIImpl.Log.info("Executing operation destroyVolume");
        try {
            this.volumeManager.deleteVolume(this.getUserName(), volumeId);
        } catch (InvalidUsernameException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal error");
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID, "The provided projet Id is invalid");
        } catch (InvalidVolumeIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_VOLUME_ID, "VolumeId " + volumeId + " is invalid");
        } catch (VolumeInUseException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.VOLUME_IN_USE, "Volume " + volumeId
                + " is currently attached to a VM");
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission denied");
        } catch (CloudProviderException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, e.getMessage());
        }

    }

    @GET
    @Path("/volumes")
    @Produces({"application/xml", "application/json"})
    public VolumeInfos listVolumes() {
        return this.listVolumes(null);
    }

    @GET
    @Path("/volumes/{projectId}")
    @Produces({"application/xml", "application/json"})
    public VolumeInfos listVolumes(@PathParam("projectId") String projectId) {
        UserAPIImpl.Log.info("Executing operation listVolumes");

        String userName = this.getUserName();

        VolumeInfos result = new VolumeInfos();
        result.setVolume(new ArrayList<VolumeInfo>());

        if (projectId == null) {
            projectId = userName; // XXX projectId = userName
        }

        List<Volume> volumes = null;
        try {
            volumes = this.userProjectManager.getVolumeByProjectId(projectId);
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID, "Invalid projectId");
        }

        for (Volume volume : volumes) {
            result.getVolume().add(this.toVolumeInfo(volume));
        }
        return result;
    }

    @POST
    @Path("/metricsinfo")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public PerfMetricInfos listPerfMetricInfos(final PerfMetricInfoSpec target) {
        UserAPIImpl.Log.info("Executing operation listPerfMetricInfos:" + target);

        PerfMetricInfos result = new PerfMetricInfos();
        result.setPerfMetricInfos(new ArrayList<PerfMetricInfo>());
        Entity type = null;
        String id = null;
        List<PerfMetricInfoVO> result_temp = null;

        if (target == null || target.getTarget().isEmpty()) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT,
                "Invalid target (vm=vmId|user=userName|project=projectId|host=hostname)");
        }

        String tmp[] = target.getTarget().split("=");
        if (tmp.length != 2) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT,
                "Invalid target (vm=vmId|user=userName|project=projectId|host=hostname)");
        }
        if (tmp[0].equals("vm")) {
            type = Entity.VIRTUALMACHINE;
        } else if (tmp[0].equals("host")) {
            type = Entity.HOST;
        } else if (tmp[0].equals("user")) {
            type = Entity.USER;
        } else if (tmp[0].equals("project")) {
            type = Entity.PROJECT;
        } else {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT,
                "Invalid target (vm=vmId|user=userName|project=projectId|host=hostname)");
        }

        id = tmp[1];

        try {
            result_temp = this.monitoringManager.listPerfMetricInfos(type, id);
        } catch (InvalidArgumentException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, "Invalid target id " + id);
        } catch (CloudProviderException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, e.getMessage());
        }

        for (PerfMetricInfoVO pRRVO : result_temp) {
            // UserAPIImpl.Log.info("USERAPPIIMPL:" + result_temp.toString());
            result.getPerfMetricInfos().add(this.fromPerfMetricInfoValueObjectToPerfMetricInfo(pRRVO));
        }

        return result;
    }

    private PerfMetricInfo fromPerfMetricInfoValueObjectToPerfMetricInfo(final PerfMetricInfoVO pVO) {
        PerfMetricInfo result = new PerfMetricInfo();
        result.setId(pVO.getId());
        result.setDescription(pVO.getDescription());
        result.setIntervalSample(pVO.getIntervalSample());
        result.setName(pVO.getName());
        result.setTypeSample(pVO.getTypeSample());
        result.setUnit(pVO.getUnit().toString());
        return result;
    }

    @POST
    @Path("/metrics")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public PerfMetrics getPerfMetrics(final PerfMetricSpec request) {
        UserAPIImpl.Log.info("Executing operation getPerfMetrics");

        PerfMetrics result = new PerfMetrics();
        result.setPerfMetrics(new ArrayList<PerfMetric>());
        String tmp[] = request.getTarget().split("=");
        Entity type = null;
        String id = null;

        List<PerfMetricVO> result_temp = null;

        if (request.getTarget() == null || request.getTarget().isEmpty()) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT,
                "Invalid target (vm=vmId|user=userName|project=projectId|host=hostname)");
        }

        if (tmp[0] != null && !tmp[0].isEmpty()) {
            if (tmp[0].equals("vm")) {
                type = Entity.VIRTUALMACHINE;
            }
            if (tmp[0].equals("host")) {
                type = Entity.HOST;
            }
            if (tmp[0].equals("user")) {
                type = Entity.USER;
            }
            if (tmp[0].equals("project")) {
                type = Entity.PROJECT;
            }
        } else {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT,
                "Invalid target (vm=vmId|user=userName|project=projectId|host=hostname)");
        }

        if (tmp[1] != null && !tmp[1].isEmpty()) {
            id = tmp[1];
        } else {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT,
                "Invalid target (vm=vmId|user=userName|project=projectId|host=hostname)");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        sdf.setLenient(true);
        Date start = new Date();
        Date end = new Date();

        try {
            start = sdf.parse(request.getStartTime());
            end = sdf.parse(request.getEndTime());
        } catch (ParseException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT,
                "Invalid date format (dd-MM-yyyy-hh-mm-ss)");
        }

        try {
            result_temp = this.monitoringManager.getPerfMetrics(type, id, request.getMetricId(), start, end);
        } catch (InvalidArgumentException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, "Invalid target " + request.getTarget());
        } catch (InvalidPerfMetricIdException ex) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, "MetricId " + request.getMetricId()
                + " doesn't exist for this target");
        } catch (CloudProviderException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, e.getMessage());
        }

        for (PerfMetricVO pRRVO : result_temp) {
            result.getPerfMetrics().add(this.fromPerfMetricValueObjectToPerfMetric(pRRVO));
        }
        return result;
    }

    private PerfMetric fromPerfMetricValueObjectToPerfMetric(final PerfMetricVO pVO) {
        PerfMetric result = new PerfMetric();
        result.setTimeStamp(pVO.getTimeStamp());
        result.setValue(pVO.getValue());
        return result;
    }

    @Override
    public CloudProviderInfos listCloudProviders() {
        CloudProviderInfos result = new CloudProviderInfos();
        List<CloudProviderInfo> cloudProviderInfoList = new ArrayList<CloudProviderInfo>();
        for (CloudProvider cp : this.cloudProviderManager.listCloudProviders()) {
            cloudProviderInfoList.add(this.toCloudProviderInfo(cp));
        }
        result.setCloudProviderInfos(cloudProviderInfoList);
        return result;
    }

    @Override
    @POST
    @Path("/cloudproviders")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public CloudProviderInfo createCloudProvider(final CloudProviderSpec cloudProviderSpec) {
        CloudProvider cloudProvider = null;
        try {
            cloudProvider = this.cloudProviderManager.createCloudProvider(this.getUserName(), cloudProviderSpec.getName(),
                cloudProviderSpec.getDescription());
        } catch (InvalidUsernameException e) {
            // should not happen
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error");
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED,
                "Permission denied for user " + this.getUserName());
        } catch (InvalidArgumentException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, e.getMessage());
        }
        return this.toCloudProviderInfo(cloudProvider);
    }

    @Override
    @GET
    @Path("/cloudproviders/{providerId}/accounts")
    @Produces({"application/xml", "application/json"})
    public CloudProviderAccountInfos listCloudProviderAccounts(@PathParam("providerId") final String providerId) {
        CloudProviderAccountInfos result = new CloudProviderAccountInfos();
        List<CloudProviderAccountInfo> cloudProviderAccountInfoList = new ArrayList<CloudProviderAccountInfo>();
        for (CloudProviderAccount account : this.cloudProviderManager.listCloudProviderAccounts()) {
            cloudProviderAccountInfoList.add(this.toCloudProviderAccountInfo(account));
        }
        result.setCloudProviderAccountInfos(cloudProviderAccountInfoList);
        return result;
    }

    @Override
    @GET
    @Path("/projects/{projectId}/cloudprovideraccounts")
    @Produces({"application/xml", "application/json"})
    public CloudProviderAccountInfos listCloudProviderAccountsByProject(@PathParam("projectId") final String projectId) {
        CloudProviderAccountInfos result = new CloudProviderAccountInfos();
        List<CloudProviderAccountInfo> cloudProviderAccountInfoList = new ArrayList<CloudProviderAccountInfo>();
        try {
            for (CloudProviderAccount account : this.cloudProviderManager.listCloudProviderAccountsByProjectId(projectId)) {
                cloudProviderAccountInfoList.add(this.toCloudProviderAccountInfo(account));
            }
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID, "Invalid projectId : " + projectId);
        }
        result.setCloudProviderAccountInfos(cloudProviderAccountInfoList);
        return result;
    }

    @Override
    @POST
    @Path("/cloudproviders/{providerId}/accounts")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public CloudProviderAccountInfo createCloudProviderAccount(@PathParam("providerId") final String cloudProviderId,
        final CloudProviderAccountSpec cloudProviderAccountSpec) {
        CloudProviderAccount account = null;
        try {
            account = this.cloudProviderManager.createCloudProviderAccount(cloudProviderAccountSpec.getProjectId(),
                this.getUserName(), cloudProviderAccountSpec.getLogin(), cloudProviderAccountSpec.getPassword(),
                cloudProviderId);
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID, "Invalid projectId : "
                + cloudProviderAccountSpec.getProjectId());
        } catch (InvalidUsernameException e) {
            // should not happen
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error");
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED,
                "Permission denied for user " + this.getUserName());
        } catch (InvalidArgumentException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, e.getMessage());
        }
        return this.toCloudProviderAccountInfo(account);
    }

    @Override
    @PUT
    @Path("/cloudprovideraccounts/{accountId}/associate")
    @Produces({"application/xml", "application/json"})
    public void associateCloudProviderAccountWithProject(@PathParam("accountId") final String accountId,
        final CloudProviderAccountAssociationSpec spec) {
        try {
            this.cloudProviderManager.associateCloudProviderAccountWithProject(spec.getProjectId(), this.getUserName(),
                accountId);
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID,
                "Invalid projectId : " + spec.getProjectId());
        } catch (InvalidUsernameException e) {
            // should not happen
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error");
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED,
                "Permission denied for user " + this.getUserName());
        } catch (InvalidArgumentException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, e.getMessage());
        }
    }

    @Override
    @PUT
    @Path("/cloudprovideraccounts/{accountId}/dissociate")
    @Produces({"application/xml", "application/json"})
    public void dissociateCloudProviderAccountFromProject(@PathParam("accountId") final String accountId,
        final CloudProviderAccountAssociationSpec spec) {
        try {
            this.cloudProviderManager.dissociateCloudProviderAccountFromProject(spec.getProjectId(), this.getUserName(),
                accountId);
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID,
                "Invalid projectId : " + spec.getProjectId());
        } catch (InvalidUsernameException e) {
            // should not happen
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error");
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED,
                "Permission denied for user " + this.getUserName());
        } catch (InvalidArgumentException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, e.getMessage());
        }
    }

    @DELETE
    @Path("/projects/{projectId}/cloudprovideraccounts/{accountId")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public void deleteCloudProviderAccount(@PathParam("projectId") final String projectId,
        @PathParam("accountId") final String accountId) {
        try {
            this.cloudProviderManager.deleteCloudProviderAccount(projectId, this.getUserName(), accountId);
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID, "Invalid projectId : " + projectId);
        } catch (InvalidUsernameException e) {
            // should not happen
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error");
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED,
                "Permission denied for user " + this.getUserName());
        } catch (InvalidArgumentException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, e.getMessage());
        } catch (CloudProviderException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.CLOUDPROVIDER_ACCOUNT_IN_USE, e.getMessage());
        }
    }

    @DELETE
    @Path("/cloudproviders/{providerId}")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public void deleteCloudProvider(@PathParam("providerId") final String providerId) {
        try {
            this.cloudProviderManager.deleteCloudProvider(this.getUserName(), providerId);
        } catch (InvalidUsernameException e) {
            // should not happen
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error");
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED,
                "Permission denied for user " + this.getUserName());
        } catch (InvalidArgumentException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, e.getMessage());
        } catch (CloudProviderException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.CLOUDPROVIDER_IN_USE, e.getMessage());
        }
    }

    @Override
    public SystemTemplateInfos listSystemTemplates() {
        SystemTemplateInfos systemTemplates = new SystemTemplateInfos();
        systemTemplates.setSystemTemplateInfo(new ArrayList<SystemTemplateInfo>());

        for (SystemTemplate template : this.systemTemplateManager.listSystemTemplates()) {
            systemTemplates.getSystemTemplateInfo().add(this.toSystemTemplateInfo(template));
        }
        return systemTemplates;
    }

    @Override
    public SystemTemplateInfos listSystemTemplates(final String projectId) {
        SystemTemplateInfos systemTemplates = new SystemTemplateInfos();
        systemTemplates.setSystemTemplateInfo(new ArrayList<SystemTemplateInfo>());

        try {
            for (SystemTemplate template : this.userProjectManager.getSystemTemplatesByProjectId(projectId)) {
                systemTemplates.getSystemTemplateInfo().add(this.toSystemTemplateInfo(template));
            }
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID, "Invalid projectId : " + projectId);
        }
        return systemTemplates;
    }

    @Override
    public SystemTemplateInfo importOVF(final String projectId, final SystemTemplateSpec spec) {
        String userName = this.getUserName();

        SystemTemplateInfo result = null;

        try {
            result = this.toSystemTemplateInfo(this.ovfImporter.createSystemTemplateFromOVF(projectId, userName,
                spec.getCloudProviderAccountId(), spec.getUrl()));
        } catch (InvalidUsernameException e) {
            // should not happen
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error");
        } catch (InvalidArgumentException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, e.getMessage());
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission denied for user " + userName);
        } catch (DuplicateNameException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.DUPLICATE_VM_NAME, "Duplicate name " + e.getMessage());
        } catch (OVFImporterException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error " + e.getMessage());
        } catch (CloudProviderException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error " + e.getMessage());
        }

        return result;
    }

    @Override
    public void destroySystemTemplate(final String systemTemplateId) {
        String userName = this.getUserName();

        try {
            this.systemTemplateManager.deleteSystemTemplate(userName, systemTemplateId);
        } catch (InvalidUsernameException e) {
            // should not happen
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error");
        } catch (InvalidArgumentException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, e.getMessage());
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission denied for user " + userName);
        } catch (InvalidSystemInstanceStatusException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error " + e.getMessage());
        } catch (InvalidSystemTemplateStatusException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error " + e.getMessage());
        }
    }

    @Override
    public void purgeAllSystemTemplates() {
        String userName = this.getUserName();
        // TODO pass username and check permission
        try {
            this.systemTemplateManager.purgeDeletedSystemTemplates();
        } catch (InvalidSystemInstanceStatusException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error " + e.getMessage());
        }
    }

    @Override
    public SystemInfos listSystems() {
        SystemInfos systems = new SystemInfos();
        systems.setSystemInfo(new ArrayList<SystemInfo>());

        for (SystemInstance system : this.systemManager.listSystemInstances()) {
            systems.getSystemInfo().add(this.toSystemInfo(system));
        }
        return systems;
    }

    @Override
    public SystemInfos listSystems(final String projectId) {
        SystemInfos systems = new SystemInfos();
        systems.setSystemInfo(new ArrayList<SystemInfo>());

        try {
            for (SystemInstance system : this.userProjectManager.getSystemInstancesByProjectId(projectId)) {
                systems.getSystemInfo().add(this.toSystemInfo(system));
            }
        } catch (InvalidProjectIdException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_PROJECT_ID, "Invalid projectId : " + projectId);
        }
        return systems;
    }

    @Override
    public SystemInfo createSystem(final String projectId, final SystemSpec spec) {
        String userName = this.getUserName();
        SystemInfo result = null;
        try {
            result = this.toSystemInfo(this.systemManager.createSystemInstance(userName, projectId, spec.getSystemTemplateId(),
                spec.getCloudProviderAccountId(), null));
        } catch (ResourceQuotaExceededException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.RESOURCE_QUOTA_EXCEEDED, "Resource quota exceeded");
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission denied for user " + userName);
        } catch (CloudProviderException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error " + e.getMessage());
        }
        return result;
    }

    @Override
    public void startSystem(final String systemId) {
        String userName = this.getUserName();

        try {
            this.systemManager.startSystemInstance(userName, systemId);
        } catch (InvalidArgumentException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, e.getMessage());
        } catch (InvalidSystemInstanceStatusException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_POWER_STATE, "Invalid operation");
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission denied for user " + userName);
        } catch (CloudProviderException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error " + e.getMessage());
        }
    }

    @Override
    public void stopSystem(final String systemId) {
        String userName = this.getUserName();

        try {
            this.systemManager.stopSystemInstance(userName, systemId);
        } catch (InvalidArgumentException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, e.getMessage());
        } catch (InvalidSystemInstanceStatusException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_POWER_STATE, "Invalid operation");
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission denied for user " + userName);
        } catch (CloudProviderException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error " + e.getMessage());
        }
    }

    @Override
    public void destroySystem(final String systemId) {
        String userName = this.getUserName();

        try {
            this.systemManager.deleteSystemInstance(userName, systemId);
        } catch (InvalidArgumentException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_ARGUMENT, e.getMessage());
        } catch (InvalidSystemInstanceStatusException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INVALID_POWER_STATE, "Invalid operation");
        } catch (PermissionDeniedException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.PERMISSION_DENIED, "Permission denied for user " + userName);
        } catch (CloudProviderException e) {
            this.throwWebApplicationException(ErrorReport.ErrorCode.INTERNAL_ERROR, "Internal Error " + e.getMessage());
        }
    }

    @Override
    public void purgeAllMachines() {
        String userName = this.getUserName();
        // TODO check permission
        this.machineManager.purgeDeletedMachines();
    }

    @Override
    public void purgeAllSystems() {
        String userName = this.getUserName();
        // TODO check permission
        this.systemManager.purgeDeletedSystemInstances();
    }

}
