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
package org.ow2.sirocco.cloudmanager.provider.awsec2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;

import org.jclouds.Constants;
import org.jclouds.aws.ec2.AWSEC2AsyncClient;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.services.AWSInstanceAsyncClient;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Volume;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.InstanceStateChange;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.options.RunInstancesOptions;
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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Module;

public class EC2ComputeService implements IComputeService {
    private static int DEFAULT_MACHINE_STATE_CHANGE_WAIT_TIME_IN_SECONDS = 180;

    final String accessKeyId;

    final String secretKeyId;

    final String region;

    private ComputeServiceContext computeServiceContext;

    private AWSEC2AsyncClient asyncClient;

    private AWSEC2Client syncClient;

    private AWSInstanceAsyncClient instanceAsyncClient;

    private ComputeService computeService;

    private EC2CloudProviderFactory ec2CloudProviderFactory;

    public EC2ComputeService(final EC2CloudProviderFactory ec2CloudProviderFactory, final CloudProviderAccount account,
        final CloudProviderLocation location) {
        this.accessKeyId = account.getLogin();
        this.secretKeyId = account.getPassword();
        this.region = location.getLocationId();
        this.ec2CloudProviderFactory = ec2CloudProviderFactory;

        Properties overrides = new Properties();

        String httpProxyHost = System.getProperty("http.proxyHost");
        String httpProxyPort = System.getProperty("http.proxyPort");
        if (httpProxyHost != null) {
            overrides.setProperty(Constants.PROPERTY_PROXY_HOST, httpProxyHost);
        }
        if (httpProxyPort != null) {
            overrides.setProperty(Constants.PROPERTY_PROXY_PORT, httpProxyPort);
        }
        overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
        overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");

        this.computeServiceContext = new ComputeServiceContextFactory().createContext("aws-ec2", this.accessKeyId,
            this.secretKeyId, ImmutableSet.<Module> of(new JschSshClientModule()), overrides);
        this.computeService = this.computeServiceContext.getComputeService();
        this.asyncClient = (AWSEC2AsyncClient) this.computeServiceContext.getProviderSpecificContext().getAsyncApi();
        this.syncClient = (AWSEC2Client) this.computeServiceContext.getProviderSpecificContext().getApi();
        this.instanceAsyncClient = this.asyncClient.getInstanceServices();
    }

    // @Override
    // public Job<Machine> createMachine(final MachineConfiguration
    // machineConfig, final VmImageConfiguration vmImageConfig,
    // final VolumeOptions volumeOptions, final CreateMachineOptions
    // createMachineOptions) throws CloudProviderException {
    // try {
    // RunInstancesOptions options =
    // RunInstancesOptions.Builder.asType("t1.micro").withKeyName("fredKey-US-east");
    // ListenableFuture<Reservation<? extends AWSRunningInstance>> result =
    // this.instanceAsyncClient.runInstancesInRegion(
    // this.region, null, vmImageConfig.getVmImageId(), 1, 1, options);
    // return this.ec2CloudProviderFactory.getJobManager().newJob(
    // "",
    // "machine.create",
    // new FutureWrapper<Reservation<? extends AWSRunningInstance>,
    // Machine>(result,
    // EC2ComputeService.runningInstance2MachineConverter));
    // } catch (Exception ex) {
    // throw new CloudProviderException(ex.getMessage());
    // }
    // }

    @Override
    public Job<Machine> createMachine(final MachineCreate machineCreate) throws CloudProviderException {
        final Callable<Machine> createTask = new Callable<Machine>() {
            @Override
            public Machine call() throws Exception {
                // String group = "default";//
                // createMachineOptions.getTags().get("name");
                // TemplateOptions options;
                // if (createMachineOptions.getPublicKey() != null) {
                // // TODO adds tags
                // options =
                // TemplateOptions.Builder.authorizePublicKey(createMachineOptions.getPublicKey())
                // .blockUntilRunning(true);
                // } else {
                // options = TemplateOptions.NONE;
                // }
                // Template template =
                // EC2ComputeService.this.computeService.templateBuilder().hardwareId("t1.micro")
                // .imageId(EC2ComputeService.this.region + "/" +
                // vmImageConfig.getVmImageId())
                // .locationId(EC2ComputeService.this.region).options(options).build();
                // Set<? extends NodeMetadata> result =
                // EC2ComputeService.this.computeService.createNodesInGroup(group,
                // 1,
                // template);
                //
                // Machine machine =
                // EC2ComputeService.nodeMetadata2MachineConverter.convert(result);
                // return machine;

                KeyPair keyPair = null;
                if (machineCreate.getMachineTemplate().getMachineAdmin() != null) {
                    try {
                        keyPair = EC2ComputeService.this.syncClient.getKeyPairServices().importKeyPairInRegion(
                            EC2ComputeService.this.region, "sirocco#default",
                            machineCreate.getMachineTemplate().getMachineAdmin().getPublicKey());
                    } catch (IllegalStateException e) {
                        keyPair = Iterables.getFirst(EC2ComputeService.this.syncClient.getKeyPairServices()
                            .describeKeyPairsInRegion(EC2ComputeService.this.region, "sirocco#default"), null);
                    }
                }

                RunInstancesOptions options = RunInstancesOptions.Builder.asType("t1.micro");
                if (keyPair != null) {
                    options = options.withKeyName(keyPair.getKeyName());
                }
                Reservation<? extends AWSRunningInstance> result = EC2ComputeService.this.syncClient.getInstanceServices()
                    .runInstancesInRegion(EC2ComputeService.this.region, null,
                        machineCreate.getMachineTemplate().getMachineImage().getProviderAssignedId(), 1, 1, options);

                String instanceIdPrefixedWithRegion = result.iterator().next().getId();
                String instanceId = instanceIdPrefixedWithRegion.substring(instanceIdPrefixedWithRegion.indexOf("/") + 1);

                int waitTimeInSeconds = EC2ComputeService.DEFAULT_MACHINE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                Machine machine;
                do {
                    Reservation<? extends AWSRunningInstance> reservation = Iterables.getFirst(
                        EC2ComputeService.this.syncClient.getInstanceServices().describeInstancesInRegion(
                            EC2ComputeService.this.region, instanceId), null);
                    machine = EC2ComputeService.runningInstance2MachineConverter.convert(Iterables.getFirst(reservation, null));
                    if (machine.getState() == Machine.State.STARTED) {
                        break;
                    }
                    Thread.sleep(1000);
                } while (waitTimeInSeconds-- > 0);
                return machine;
            }
        };
        ListenableFuture<Machine> result = this.ec2CloudProviderFactory.getExecutorService().submit(createTask);
        return this.ec2CloudProviderFactory.getJobManager().newJob("", "machine.create", result);
    }

    private ListenableFuture<Machine.State> waitForStateChange(final Machine.State targetState, final String machineId) {
        final Callable<Machine.State> waitForStateChangeTask = new Callable<Machine.State>() {
            @Override
            public Machine.State call() throws Exception {
                Machine.State status;
                int waitTimeInSeconds = EC2ComputeService.DEFAULT_MACHINE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                do {
                    status = EC2ComputeService.this.getMachineState(machineId);
                    if (status == targetState) {
                        break;
                    }
                    Thread.sleep(1000);
                } while (waitTimeInSeconds-- > 0);
                return status;
            }
        };
        return this.ec2CloudProviderFactory.getExecutorService().submit(waitForStateChangeTask);
    }

    private ListenableFuture<Void> waitForInstanceDeath(final String machineId) {
        final Callable<Void> waitForInstanceDeathTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Machine.State state;
                int waitTimeInSeconds = EC2ComputeService.DEFAULT_MACHINE_STATE_CHANGE_WAIT_TIME_IN_SECONDS;
                do {
                    state = EC2ComputeService.this.getMachineState(machineId);
                    if (state == Machine.State.DELETED) {
                        break;
                    }
                    Thread.sleep(1000);
                } while (waitTimeInSeconds-- > 0);
                return null;
            }
        };
        return this.ec2CloudProviderFactory.getExecutorService().submit(waitForInstanceDeathTask);
    }

    @Override
    public Job<Machine.State> startMachine(final String machineId) throws CloudProviderException {
        try {
            ListenableFuture<Set<? extends InstanceStateChange>> startResult = this.instanceAsyncClient.startInstancesInRegion(
                this.region, machineId);
            return this.ec2CloudProviderFactory.getJobManager().newJob(machineId, "machine.start",
                this.waitForStateChange(Machine.State.STARTED, machineId));
        } catch (Exception ex) {
            throw new CloudProviderException(ex.getMessage());
        }
    }

    // @Override
    // public Job<State> startMachine(final String machineId) throws
    // CloudProviderException {
    // try {
    // ListenableFuture<Set<? extends InstanceStateChange>> result =
    // this.instanceAsyncClient.startInstancesInRegion(
    // this.region, machineId);
    // return this.jobManager.newJob(machineId, "machine.start",
    // new FutureWrapper<Set<? extends InstanceStateChange>,
    // Machine.State>(result,
    // EC2ComputeService.instanceStateChange2MachineStateConverter));
    // } catch (Exception ex) {
    // throw new CloudProviderException(ex.getMessage());
    // }
    // }

    @Override
    public Job<Machine.State> stopMachine(final String machineId) throws CloudProviderException {
        try {
            ListenableFuture<Set<? extends InstanceStateChange>> stopResult = this.instanceAsyncClient.stopInstancesInRegion(
                this.region, true, machineId);
            return this.ec2CloudProviderFactory.getJobManager().newJob(machineId, "machine.stop",
                this.waitForStateChange(Machine.State.STOPPED, machineId));
        } catch (Exception ex) {
            throw new CloudProviderException(ex.getMessage());
        }
    }

    @Override
    public Job<Machine.State> suspendMachine(final String machineId) throws CloudProviderException {
        throw new CloudProviderException("unsupported operation");
    }

    @Override
    public Job<Machine.State> resumeMachine(final String machineId) throws CloudProviderException {
        throw new CloudProviderException("unsupported operation");
    }

    @Override
    public Job<Void> destroyMachine(final String machineId) throws CloudProviderException {
        try {
            ListenableFuture<Set<? extends InstanceStateChange>> result = this.instanceAsyncClient.terminateInstancesInRegion(
                this.region, machineId);
            return this.ec2CloudProviderFactory.getJobManager().newJob(machineId, "machine.destroy",
                this.waitForInstanceDeath(machineId));
        } catch (Exception ex) {
            throw new CloudProviderException(ex.getMessage());
        }
    }

    @Override
    public Machine.State getMachineState(final String machineId) throws CloudProviderException {
        try {
            Reservation<? extends AWSRunningInstance> reservation = Iterables.getFirst(this.syncClient.getInstanceServices()
                .describeInstancesInRegion(this.region, machineId), null);
            return EC2ComputeService.instanceState2MachineStateConverter.convert(Iterables.getFirst(reservation, null)
                .getInstanceState());
        } catch (Exception ex) {
            throw new CloudProviderException(ex.getMessage());
        }
    }

    @Override
    public MachineConfiguration getMachineConfiguration(final String machineId) throws CloudProviderException {
        String instanceType = null;
        try {
            instanceType = this.syncClient.getInstanceServices().getInstanceTypeForInstanceInRegion(this.region, machineId);
        } catch (Exception ex) {
            throw new CloudProviderException(ex.getMessage());
        }
        Set<? extends Hardware> hardwareProfiles = this.computeService.listHardwareProfiles();
        for (Hardware hardwareProfile : hardwareProfiles) {
            if (hardwareProfile.getId().equals(instanceType)) {
                MachineConfiguration machineConfiguration = new MachineConfiguration();
                machineConfiguration.setNumCPUs(hardwareProfile.getProcessors().size());
                machineConfiguration.setMemorySizeMB(hardwareProfile.getRam());
                Volume volume = hardwareProfile.getVolumes().get(0);
                if (volume.getSize() != null) {
                    machineConfiguration.setDiskSizeMB((long) (volume.getSize() * 1024));
                }

                return machineConfiguration;
            }
        }

        throw new CloudProviderException("internal error: cannot find harware profile " + instanceType);
    }

    @Override
    public List<NetworkInterface> getMachineNetworkInterfaces(final String machineId) throws CloudProviderException {
        try {
            Reservation<? extends AWSRunningInstance> reservation = Iterables.getFirst(this.syncClient.getInstanceServices()
                .describeInstancesInRegion(this.region, machineId), null);
            AWSRunningInstance runningInstance = Iterables.getFirst(reservation, null);
            NetworkInterface publicNetworkInterface = new NetworkInterface();
            publicNetworkInterface.setAddress(runningInstance.getIpAddress());
            publicNetworkInterface.setHostname(runningInstance.getDnsName());
            NetworkInterface privateNetworkInterface = new NetworkInterface();
            privateNetworkInterface.setAddress(runningInstance.getPrivateIpAddress());
            privateNetworkInterface.setHostname(runningInstance.getPrivateDnsName());
            return Arrays.asList(privateNetworkInterface, publicNetworkInterface);
        } catch (Exception ex) {
            throw new CloudProviderException(ex.getMessage());
        }
    }

    @Override
    public String getMachineGraphicalConsoleUrl(final String machineId) throws CloudProviderException {
        throw new CloudProviderException("unsupported operation");
    }

    @Override
    public List<String> listMachines() throws CloudProviderException {
        try {
            Set<? extends Reservation<? extends AWSRunningInstance>> reservations = this.instanceAsyncClient
                .describeInstancesInRegion(this.region).get();
            List<String> result = new ArrayList<String>();
            for (Reservation<? extends AWSRunningInstance> reservation : reservations) {
                for (AWSRunningInstance instance : reservation) {
                    result.add(instance.getId());
                }
            }
            return result;
        } catch (Exception ex) {
            throw new CloudProviderException(ex.getMessage());
        }
    }

    private static Converter<InstanceState, Machine.State> instanceState2MachineStateConverter = new Converter<InstanceState, Machine.State>() {
        public Machine.State convert(final InstanceState state) {
            switch (state) {
            case PENDING:
                return Machine.State.CREATING;
            case RUNNING:
                return Machine.State.STARTED;
            case SHUTTING_DOWN:
                return Machine.State.STOPPING;
            case STOPPING:
                return Machine.State.STOPPING;
            case STOPPED:
                return Machine.State.STOPPED;
            case TERMINATED:
                return Machine.State.DELETED;
            case UNRECOGNIZED:
                return Machine.State.ERROR;
            default:
                return Machine.State.ERROR;
            }
        }
    };

    private static Converter<NodeState, Machine.State> nodeState2MachineStateConverter = new Converter<NodeState, Machine.State>() {
        public Machine.State convert(final NodeState state) {
            switch (state) {
            case ERROR:
                return Machine.State.ERROR;
            case PENDING:
                return Machine.State.CREATING;
            case RUNNING:
                return Machine.State.STARTED;
            case SUSPENDED:
                return Machine.State.STOPPED;
            case TERMINATED:
                return Machine.State.DELETED;
            case UNRECOGNIZED:
                return Machine.State.ERROR;
            default:
                return Machine.State.ERROR;
            }
        }
    };

    private static Converter<InstanceStateChange, Machine.State> instanceStateChange2MachineStateConverter = new Converter<InstanceStateChange, Machine.State>() {
        public Machine.State convert(final InstanceStateChange stateChange) {
            InstanceState state = stateChange.getCurrentState();
            return EC2ComputeService.instanceState2MachineStateConverter.convert(state);
        }
    };

    private static Converter<AWSRunningInstance, Machine> runningInstance2MachineConverter = new Converter<AWSRunningInstance, Machine>() {
        public Machine convert(final AWSRunningInstance runningInstance) {
            Machine machine = new Machine();
            machine.setProviderAssignedId(runningInstance.getId());
            machine.setState(EC2ComputeService.instanceState2MachineStateConverter.convert(runningInstance.getInstanceState()));
            NetworkInterface publicNetworkInterface = new NetworkInterface();
            publicNetworkInterface.setAddress(runningInstance.getIpAddress());
            publicNetworkInterface.setHostname(runningInstance.getDnsName());
            NetworkInterface privateNetworkInterface = new NetworkInterface();
            privateNetworkInterface.setAddress(runningInstance.getPrivateIpAddress());
            privateNetworkInterface.setHostname(runningInstance.getPrivateDnsName());
            machine.setNetworkInterfaces(Arrays.asList(privateNetworkInterface, publicNetworkInterface));
            return machine;
        }
    };

    private static Converter<NodeMetadata, Machine> nodeMetadata2MachineConverter = new Converter<NodeMetadata, Machine>() {
        public Machine convert(final NodeMetadata nodeMetadata) {
            Machine machine = new Machine();
            machine.setProviderAssignedId(nodeMetadata.getId().substring(nodeMetadata.getId().indexOf("/") + 1));
            machine.setState(EC2ComputeService.nodeState2MachineStateConverter.convert(nodeMetadata.getState()));
            NetworkInterface publicNetworkInterface = new NetworkInterface();
            publicNetworkInterface.setAddress(nodeMetadata.getPublicAddresses().iterator().next());
            NetworkInterface privateNetworkInterface = new NetworkInterface();
            privateNetworkInterface.setAddress(nodeMetadata.getPrivateAddresses().iterator().next());
            machine.setNetworkInterfaces(Arrays.asList(privateNetworkInterface, publicNetworkInterface));
            return machine;
        }
    };

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
    // EC2CloudProviderFactory cloudProviderFactory = new
    // EC2CloudProviderFactory(jobManager);
    // EC2ComputeService computeService = new
    // EC2ComputeService(cloudProviderFactory, new
    // CPAccount("AKIAJN724E53IRLVZSPA",
    // "mqgnizHSa9rgnrNX2Te6YG4o6YzxZE+c/ZOZLgqF"), new
    // CloudProviderLocation("us-west-1"));
    //
    // try {
    // // List<String> machineIds = computeService.listMachines();
    // // for (String id : machineIds) {
    // // System.out.println("STATUS=" +
    // // computeService.getMachineState(id));
    // // }
    //
    // List<String> machineIds = computeService.listMachines();
    // System.out.println("machines=" + machineIds);
    //
    // System.out.println("creating machine...");
    // String machineId = EC2ComputeService.create(computeService);
    //
    // System.out.println("Net=" +
    // computeService.getMachineNetworkInterfaces(machineId));
    //
    // Thread.sleep(2000);
    //
    // System.out.println("Stopping " + machineId);
    // Job<Machine.State> job = computeService.stopMachine(machineId);
    // System.out.println("status=" + job.getResult().get());
    //
    // System.out.println("Starting " + machineId);
    // job = computeService.startMachine(machineId);
    // System.out.println("status=" + job.getResult().get());
    //
    // Job<Void> job2 = computeService.destroyMachine(machineId);
    // System.out.println("status=" + job2.getState());
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    //
    // }
    //
    // static String create(final EC2ComputeService computeService) throws
    // Exception {
    // VmImageConfiguration imageConfig = new
    // VmImageConfiguration("ami-11d68a54");
    //
    // CreateMachineOptions createOptions = new CreateMachineOptions();
    // createOptions
    // .setPublicKey("ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA1q2X/nCFT2JNWzIyiUCaizp1i4aBfrW7riokU1M85IoXePp451iyAcuQRb+ujhzPCZ1BAqDZbIMfoYWe0nREw+YjmJdpb/4notmPMgWy+l/Asf70xgyygnrKVw8RgiUd+ypLX5Lj0jQoharuQBRXQlIctPcWElcBtDv3tc/912TZVxsjAFvd4vAwsFsfUtCwnwq9/xUKJNg+3vmScTCptZWMzLUCXPV5eu/3pULHjvjCISf9/PRpEykxFi90JqJ8j11sVZcblGiVovUOpOw3d1rUKDteNhflRTiVIUGagZq0C3TXi407taJXrOz6al4w333cVzdojpPEWttzQx7stw== dangtran@p-dhcp-475-1859.rd.francetelecom.fr");
    // Map<String, String> tags = new HashMap<String, String>();
    // tags.put("name", "hello");
    // createOptions.setTags(tags);
    //
    // Job<Machine> job = computeService.createMachine(null, imageConfig, null,
    // createOptions);
    // if (job.getState() == Job.State.RUNNING) {
    // Thread.sleep(2000);
    // }
    // System.out.println("Job status=" + job.getState() + " mesg=" +
    // job.getStateMessage());
    // Machine machine = job.getResult().get();
    // System.out.println("NEW MACHINE: " + machine);
    // return machine.getProviderId();
    // }
}
