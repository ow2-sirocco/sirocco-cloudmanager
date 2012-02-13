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
package org.ow2.sirocco.cloudmanager.provider.nova;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.openstack.nova.NovaAsyncClient;
import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.openstack.nova.domain.Address;
import org.jclouds.openstack.nova.domain.Flavor;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;
import org.jclouds.openstack.nova.options.ListOptions;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Job;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.entity.NetworkInterface;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.service.IComputeService;
import org.ow2.sirocco.cloudmanager.provider.api.service.ImageCreate;
import org.ow2.sirocco.cloudmanager.provider.api.service.MachineCreate;
import org.ow2.sirocco.cloudmanager.provider.api.service.VolumeAttachment;
import org.ow2.sirocco.cloudmanager.provider.util.Converter;
import org.ow2.sirocco.cloudmanager.provider.util.FutureWrapper;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Module;

public class NovaComputeService implements IComputeService {
    private static Log logger = LogFactory.getLog(NovaComputeService.class);

    private static int DEFAULT_MACHINE_STATE_CHANGE_WAIT_TIME_IN_SECONDS = 180;

    private final NovaCloudProviderFactory novaCloudProviderFactory;

    private ComputeServiceContext computeServiceContext;

    private ComputeService computeService;

    private NovaClient syncClient;

    private NovaAsyncClient asyncClient;

    private final CloudProviderAccount cloudProviderAccount;

    private final CloudProviderLocation cloudProviderLocation;

    private Set<Flavor> flavors;

    public NovaComputeService(final NovaCloudProviderFactory novaCloudProviderFactory,
        final CloudProviderAccount cloudProviderAccount, final CloudProviderLocation cloudProviderLocation) {
        this.novaCloudProviderFactory = novaCloudProviderFactory;
        this.cloudProviderAccount = cloudProviderAccount;
        this.cloudProviderLocation = cloudProviderLocation;

        Properties overrides = new Properties();
        overrides.setProperty(Constants.PROPERTY_ENDPOINT, novaCloudProviderFactory.getNovaEndpoint());
        overrides.setProperty(Constants.PROPERTY_API_VERSION, "1.1");
        String httpProxyHost = System.getProperty("http.proxyHost");
        String httpProxyPort = System.getProperty("http.proxyPort");
        if (httpProxyHost != null) {
            overrides.setProperty(Constants.PROPERTY_PROXY_HOST, httpProxyHost);
        }
        if (httpProxyPort != null) {
            overrides.setProperty(Constants.PROPERTY_PROXY_PORT, httpProxyPort);
        }

        String user = this.cloudProviderAccount.getLogin();
        String apiKey = this.cloudProviderAccount.getPassword();
        this.computeServiceContext = new ComputeServiceContextFactory().createContext("nova", user, apiKey,
            ImmutableSet.<Module> of(new JschSshClientModule()), overrides);
        this.computeService = this.computeServiceContext.getComputeService();
        this.asyncClient = (NovaAsyncClient) this.computeServiceContext.getProviderSpecificContext().getAsyncApi();
        this.syncClient = (NovaClient) this.computeServiceContext.getProviderSpecificContext().getApi();
    }

    private Set<Flavor> getFlavors() {
        if (this.flavors == null) {
            this.flavors = this.syncClient.listFlavors(new ListOptions().withDetails());
        }
        return this.flavors;
    }

    private String findSuitableFlavor(final MachineConfiguration machineConfig) {
        for (Flavor flavor : this.getFlavors()) {
            if (machineConfig.getMemorySizeMB() <= flavor.getRam()) {
                return Integer.toString(flavor.getId());
            }
        }
        return null;
    }

    private Machine.State fromServerStateToMachineState(final ServerStatus serverState) {
        switch (serverState) {
        case ACTIVE:
            return Machine.State.STARTED;
        case BUILD:
            return Machine.State.CREATING;
        case DELETED:
            return Machine.State.DELETED;
        case DELETE_IP:
            return Machine.State.STARTED;
        case HARD_REBOOT:
            return Machine.State.STARTED;
        case PASSWORD:
            return Machine.State.STARTED;
        case PREP_RESIZE:
            return Machine.State.ERROR;
        case QUEUE_RESIZE:
            return Machine.State.ERROR;
        case REBOOT:
            return Machine.State.STARTED;
        case REBUILD:
            return Machine.State.ERROR;
        case RESCUE:
            return Machine.State.ERROR;
        case RESIZE:
            return Machine.State.ERROR;
        case SUSPENDED:
            return Machine.State.STOPPED;
        case UNKNOWN:
            return Machine.State.ERROR;
        case UNRECOGNIZED:
            return Machine.State.ERROR;
        case VERIFY_RESIZE:
            return Machine.State.ERROR;
        default:
            return Machine.State.ERROR;
        }
    }

    @Override
    public Job<Machine> createMachine(final MachineCreate machineCreate) throws CloudProviderException {
        final String flavorRef = this.findSuitableFlavor(machineCreate.getMachineTemplate().getMachineConfig());
        if (flavorRef == null) {
            throw new CloudProviderException("Cannot find suitable flavor");
        }
        final Callable<Machine> createTask = new Callable<Machine>() {
            @Override
            public Machine call() throws Exception {
                String serverName = machineCreate.getName() + "-" + UUID.randomUUID();
                Server server = NovaComputeService.this.syncClient.createServer(serverName, machineCreate.getMachineTemplate()
                    .getMachineImage().getProviderAssignedId(), flavorRef);

                Machine machine = new Machine();
                int waitTimeInSeconds = NovaComputeService.DEFAULT_MACHINE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                do {
                    server = NovaComputeService.this.syncClient.getServer(server.getId());
                    ServerStatus status = server.getStatus();
                    if (status == ServerStatus.ACTIVE) {
                        machine.setProviderAssignedId(Integer.toString(server.getId()));
                        machine.setState(NovaComputeService.this.fromServerStateToMachineState(server.getStatus()));
                        List<NetworkInterface> nics = new ArrayList<NetworkInterface>();
                        machine.setNetworkInterfaces(nics);
                        for (Address address : server.getAddresses().getPrivateAddresses()) {
                            NetworkInterface nic = new NetworkInterface();
                            nic.setAddress(address.getAddress());
                            nics.add(nic);
                        }
                        for (Address address : server.getAddresses().getPublicAddresses()) {
                            NetworkInterface nic = new NetworkInterface();
                            nic.setAddress(address.getAddress());
                            nics.add(nic);
                        }
                        break;
                    }
                    Thread.sleep(1000);
                } while (waitTimeInSeconds-- > 0);
                return machine;
            }
        };
        ListenableFuture<Machine> result = this.novaCloudProviderFactory.getExecutorService().submit(createTask);
        return this.novaCloudProviderFactory.getJobManager().newJob("", "machine.create", result);
    }

    @Override
    public Job<Machine.State> startMachine(final String machineId) throws CloudProviderException {
        throw new CloudProviderException("unsupported operation");
    }

    @Override
    public Job<Machine.State> stopMachine(final String machineId) throws CloudProviderException {
        throw new CloudProviderException("unsupported operation");
    }

    @Override
    public Job<Machine.State> suspendMachine(final String machineId) throws CloudProviderException {
        throw new CloudProviderException("unsupported operation");
    }

    @Override
    public Job<Machine.State> resumeMachine(final String machineId) throws CloudProviderException {
        throw new CloudProviderException("unsupported operation");
    }

    private Future<Machine.State> waitForStateChange(final Machine.State targetState, final String machineId) {
        final Callable<Machine.State> waitForStateChangeTask = new Callable<Machine.State>() {
            @Override
            public Machine.State call() throws Exception {
                Machine.State status;
                int waitTimeInSeconds = NovaComputeService.DEFAULT_MACHINE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                do {
                    status = NovaComputeService.this.getMachineState(machineId);
                    if (status == targetState) {
                        break;
                    }
                    Thread.sleep(1000);
                } while (waitTimeInSeconds-- > 0);
                return status;
            }
        };
        return this.novaCloudProviderFactory.getExecutorService().submit(waitForStateChangeTask);
    }

    @Override
    public Job<Void> destroyMachine(final String machineId) throws CloudProviderException {
        try {
            int id = Integer.valueOf(machineId);
            ListenableFuture<Boolean> result = this.asyncClient.deleteServer(id);
            return this.novaCloudProviderFactory.getJobManager().newJob(machineId, "machine.destroy",
                new FutureWrapper<Boolean, Void>(result, this.boolean2voidConverter));
        } catch (Exception ex) {
            throw new CloudProviderException(ex.getMessage());
        }
    }

    private Converter<Boolean, Void> boolean2voidConverter = new Converter<Boolean, Void>() {
        public Void convert(final Boolean v) {
            return null;
        };
    };

    private Server getServer(final String machineId) throws CloudProviderException {
        int id;
        try {
            id = Integer.valueOf(machineId);
        } catch (NumberFormatException ex) {
            throw new CloudProviderException("Invalid machine Id: " + machineId);
        }
        Server server = this.syncClient.getServer(id);
        if (server == null) {
            throw new CloudProviderException("Invalid machine Id: " + machineId);
        }
        return server;
    }

    @Override
    public Machine.State getMachineState(final String machineId) throws CloudProviderException {
        Server server = this.getServer(machineId);
        return this.fromServerStateToMachineState(server.getStatus());
    }

    @Override
    public MachineConfiguration getMachineConfiguration(final String machineId) throws CloudProviderException {
        Server server = this.getServer(machineId);
        int flavorId = Integer.valueOf(server.getFlavorRef());
        for (Flavor flavor : this.getFlavors()) {
            if (flavor.getId() == flavorId) {
                MachineConfiguration machineConfiguration = new MachineConfiguration();
                // TODO find where nova keeps VM cpu configuration ??
                machineConfiguration.setNumCPUs(1);
                machineConfiguration.setMemorySizeMB(flavor.getRam());
                machineConfiguration.setDiskSizeMB(flavor.getDisk());
                return machineConfiguration;
            }
        }
        throw new CloudProviderException("Internal error: cannot find VM flavor");
    }

    @Override
    public List<NetworkInterface> getMachineNetworkInterfaces(final String machineId) throws CloudProviderException {
        Server server = this.getServer(machineId);
        List<NetworkInterface> nics = new ArrayList<NetworkInterface>();
        for (Address address : server.getAddresses().getPrivateAddresses()) {
            NetworkInterface nic = new NetworkInterface();
            nic.setAddress(address.getAddress());
            nics.add(nic);
        }
        for (Address address : server.getAddresses().getPublicAddresses()) {
            NetworkInterface nic = new NetworkInterface();
            nic.setAddress(address.getAddress());
            nics.add(nic);
        }

        return nics;
    }

    @Override
    public String getMachineGraphicalConsoleUrl(final String machineId) throws CloudProviderException {
        throw new CloudProviderException("unsupported operation");
    }

    @Override
    public List<String> listMachines() throws CloudProviderException {
        List<String> result = new ArrayList<String>();
        Set<Server> servers = this.syncClient.listServers();
        for (Server server : servers) {
            result.add(Integer.toString(server.getId()));
        }
        return result;
    }

    @Override
    public Job<Void> rebootMachine(final String machineId) throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Job<Machine.State> pauseMachine(final String machineId) throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Job<Machine.State> unpauseMachine(final String machineId) throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Job<MachineImage> captureImage(final String machineId, final ImageCreate imageCreate) throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Job<VolumeAttachment> attachVolume(final String machineId, final VolumeAttachment attachement)
        throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Job<String> detachVolume(final String machineId, final String volumeId) throws CloudProviderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    // public static void main(final String[] args) {
    // JobManager jobManager = JobManagerImpl.newJobManager();
    // NovaCloudProviderFactory cloudProviderFactory = new
    // NovaCloudProviderFactory(jobManager);
    // cloudProviderFactory.setNovaEndpoint("http://10.193.137.149:8774/");
    // NovaComputeService computeService = new
    // NovaComputeService(cloudProviderFactory, new CPAccount("orange",
    // "cloud"),
    // new CloudProviderLocation("Paris"));
    //
    // try {
    // List<String> machineIds = computeService.listMachines();
    // for (String id : machineIds) {
    // System.out.println("STATUS=" + computeService.getMachineState(id));
    // }
    //
    // System.out.println("creating machine...");
    // String machineId = NovaComputeService.create(computeService);
    //
    // // System.out.println("destroying machine " + machineId + "...");
    // // Job<Void> destroyJob = computeService.destroyMachine(machineId);
    // // System.out.println("status=" + destroyJob.getResult().get());
    //
    // System.out.println("Net=" +
    // computeService.getMachineNetworkInterfaces(machineId));
    //
    // // System.out.println("Stopping " + machineId);
    // // Job<Machine.State> job = computeService.stopMachine(machineId);
    // // System.out.println("status=" + job.getResult().get());
    // //
    // // System.out.println("Starting " + machineId);
    // // job = computeService.startMachine(machineId);
    // // System.out.println("status=" + job.getResult().get());
    //
    // Job<Void> destroyJob = computeService.destroyMachine(machineId);
    // System.out.println("status=" + destroyJob.getResult().get());
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    //
    // static String create(final NovaComputeService computeService) throws
    // Exception {
    // VmImageConfiguration imageConfig = new VmImageConfiguration("1");
    //
    // MachineConfiguration machineConfiguration = new MachineConfiguration();
    // machineConfiguration.setMemoryInMB(512);
    //
    // CreateMachineOptions createOptions = new CreateMachineOptions();
    // Map<String, String> tags = new HashMap<String, String>();
    // tags.put("name", "foobar");
    // createOptions.setTags(tags);
    //
    // Job<Machine> job = computeService.createMachine(machineConfiguration,
    // imageConfig, null, createOptions);
    // while (job.getState() == Job.State.RUNNING) {
    // Thread.sleep(2000);
    // }
    // System.out.println("Job status=" + job.getState() + " mesg=" +
    // job.getStateMessage());
    // Machine machine = job.getResult().get();
    // System.out.println("NEW MACHINE: " + machine);
    // return machine.getProviderId();
    // }

}
