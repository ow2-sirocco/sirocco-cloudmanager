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

package org.ow2.sirocco.cloudmanager.connector.itests;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactory;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.api.IJobManager;
import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.impl.JobManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory.MemoryUnit;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterfaceMT;
import org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

public class CloudProviderConnectorTest {
    private static final String MACHINE_CONFIG_CPU_PROP = "machineconfig.cpu";

    private static final String MACHINE_CONFIG_MEMORY_PROP = "machineconfig.memory";

    private static final String MACHINE_CONFIG_DISKS_PROP = "machineconfig.disks";

    private static final String MACHINE_IMAGE_ID_PROP = "machineimage.id";

    private static final String VOLUME_CONFIG_SIZE_PROP = "volumeconfig.size";

    private static final String VOLUME_DEVICE_PROP = "volume.device";

    private static final String PUBLIC_KEY_PROP = "credentials.publickey";

    private static final int ASYNC_OPERATION_WAIT_TIME_IN_SECONDS = 240;

    private IJobManager jobManager;

    private ICloudProviderConnector connector;

    private int machineConfigCpu;

    private int machineConfigMemory;

    private int[] machineConfigDiskSizes;

    private int volumeConfigSizeGB;

    private String volumeDevice;

    private String imageId;

    private String key;

    private boolean testStopMachine, testVolumeAttach;

    @Before
    public void setUp() throws Exception {
        String providerName = System.getProperty("test.provider");
        if (providerName == null) {
            throw new Exception("Missing test.provider property");
        }
        Properties prop = new Properties();
        InputStream in = this.getClass().getResourceAsStream(providerName.toLowerCase() + ".properties");
        prop.load(in);
        in.close();

        this.machineConfigCpu = Integer.valueOf(prop.getProperty(CloudProviderConnectorTest.MACHINE_CONFIG_CPU_PROP));
        this.machineConfigMemory = Integer.valueOf(prop.getProperty(CloudProviderConnectorTest.MACHINE_CONFIG_MEMORY_PROP));
        this.imageId = prop.getProperty(CloudProviderConnectorTest.MACHINE_IMAGE_ID_PROP);
        String diskSizes[] = prop.getProperty(CloudProviderConnectorTest.MACHINE_CONFIG_DISKS_PROP).split(", ");
        this.machineConfigDiskSizes = new int[diskSizes.length];
        for (int i = 0; i < diskSizes.length; i++) {
            this.machineConfigDiskSizes[i] = Integer.valueOf(diskSizes[i]);
        }
        this.volumeConfigSizeGB = Integer.valueOf(prop.getProperty(CloudProviderConnectorTest.VOLUME_CONFIG_SIZE_PROP));
        this.volumeDevice = prop.getProperty(CloudProviderConnectorTest.VOLUME_DEVICE_PROP);

        String login = System.getProperty("test.login");
        String password = System.getProperty("test.password");
        String endpoint = System.getProperty("test.endpoint");

        this.key = prop.getProperty(CloudProviderConnectorTest.PUBLIC_KEY_PROP);

        this.testStopMachine = Boolean.valueOf(System.getProperty("machine.stop"));
        this.testVolumeAttach = Boolean.valueOf(System.getProperty("volume.attach"));

        this.jobManager = JobManager.newJobManager();
        String className = "org.ow2.sirocco.cloudmanager.connector." + providerName.toLowerCase() + "." + providerName
            + "CloudProviderConnectorFactory";
        Class<?> connectorFactoryClass = Class.forName(className);

        Constructor<?> ctor = connectorFactoryClass.getDeclaredConstructor(IJobManager.class);

        ICloudProviderConnectorFactory factory = (ICloudProviderConnectorFactory) ctor.newInstance(this.jobManager);

        CloudProviderLocation location = factory.listCloudProviderLocations().iterator().next();
        CloudProviderAccount cloudProviderAccount = new CloudProviderAccount();
        cloudProviderAccount.setLogin(login);
        cloudProviderAccount.setPassword(password);

        CloudProvider cloudProvider = new CloudProvider();
        cloudProvider.setEndPoint(endpoint);

        cloudProviderAccount.setCloudProvider(cloudProvider);

        this.connector = factory.getCloudProviderConnector(cloudProviderAccount, location);
    }

    private void waitForJobCompletion(Job job) throws Exception {
        int counter = CloudProviderConnectorTest.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        String jobId = job.getProviderAssignedId().toString();
        while (true) {
            job = this.jobManager.getJobById(jobId);
            if (job.getStatus() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("Operation time out");
            }
        }
        Assert.assertTrue("Job failed: " + job.getStatusMessage(), job.getStatus() == Job.Status.SUCCESS);
    }

    @Test
    public void computeAndVolumeServiceTest() throws Exception {
        IComputeService computeService = this.connector.getComputeService();
        IVolumeService volumeService = this.connector.getVolumeService();

        MachineCreate machineCreate = new MachineCreate();
        MachineTemplate machineTemplate = new MachineTemplate();
        MachineConfiguration machineConfiguration = new MachineConfiguration();
        Cpu cpu = new Cpu();
        cpu.setNumberCpu(this.machineConfigCpu);
        machineConfiguration.setCpu(cpu);
        Memory memory = new Memory();
        memory.setQuantity((float) this.machineConfigMemory);
        memory.setUnit(MemoryUnit.MEGIBYTE);
        machineConfiguration.setMemory(memory);
        List<DiskTemplate> disks = new ArrayList<DiskTemplate>();
        for (int diskSizeGB : this.machineConfigDiskSizes) {
            DiskTemplate disk = new DiskTemplate();
            disk.setQuantity((float) diskSizeGB);
            disk.setUnit(StorageUnit.GIGABYTE);
            disks.add(disk);
        }

        machineConfiguration.setDiskTemplates(disks);
        machineTemplate.setMachineConfiguration(machineConfiguration);

        MachineImage machineImage = new MachineImage();
        machineImage.setProviderAssignedId(this.imageId);
        machineTemplate.setMachineImage(machineImage);

        List<NetworkInterface> nics = new ArrayList<NetworkInterface>();
        NetworkInterfaceMT nic = new NetworkInterfaceMT();
        nic.setNetworkType(Network.Type.PRIVATE);
        nics.add(nic);
        nic = new NetworkInterfaceMT();
        nic.setNetworkType(Network.Type.PUBLIC);
        nics.add(nic);

        machineTemplate.setNetworkInterfaces(nics);

        if (this.key != null) {
            Credentials credentials = new Credentials();
            credentials.setPublicKey(this.key.getBytes());
            machineTemplate.setCredentials(credentials);
        }

        machineCreate.setMachineTemplate(machineTemplate);
        machineCreate.setName("test");

        Job job = computeService.createMachine(machineCreate);
        this.waitForJobCompletion(job);
        String machineId = job.getTargetEntity().getProviderAssignedId();

        Machine machine = computeService.getMachine(machineId);
        System.out.println("Machine id=" + machine.getProviderAssignedId() + " state=" + machine.getState());
        for (NetworkInterface netInterface : machine.getNetworkInterfaces()) {
            System.out.print("\t Network " + netInterface.getNetworkType() + " addresses=");
            if (netInterface.getAddresses() != null) {
                for (Address addr : netInterface.getAddresses()) {
                    System.out.print(addr.getIp() + " ");
                }
            }
            System.out.println();
        }

        if (this.testStopMachine) {
            if (machine.getState() == State.STOPPED) {
                job = computeService.startMachine(machineId);
                this.waitForJobCompletion(job);
                machine = computeService.getMachine(machineId);
                Assert.assertEquals(Machine.State.STARTED, machine.getState());
            } else {
                job = computeService.stopMachine(machineId);
                this.waitForJobCompletion(job);
                machine = computeService.getMachine(machineId);
                Assert.assertEquals(Machine.State.STOPPED, machine.getState());
                job = computeService.startMachine(machineId);
                this.waitForJobCompletion(job);
                machine = computeService.getMachine(machineId);
                Assert.assertEquals(Machine.State.STARTED, machine.getState());
            }
        }

        VolumeCreate volumeCreate = new VolumeCreate();
        VolumeTemplate volumeTemplate = new VolumeTemplate();
        VolumeConfiguration volumeConfig = new VolumeConfiguration();
        Disk diskCapacity = new Disk();
        diskCapacity.setUnit(StorageUnit.GIGABYTE);
        diskCapacity.setQuantity((float) this.volumeConfigSizeGB);
        volumeConfig.setCapacity(diskCapacity);
        volumeTemplate.setVolumeConfig(volumeConfig);
        volumeCreate.setVolumeTemplate(volumeTemplate);
        volumeCreate.setName("test");
        volumeCreate.setDescription("a test volume");

        job = volumeService.createVolume(volumeCreate);
        this.waitForJobCompletion(job);
        String volumeId = job.getTargetEntity().getProviderAssignedId();

        Volume volume = volumeService.getVolume(volumeId);
        System.out.println("Volume id=" + volume.getProviderAssignedId() + " size=" + volume.getCapacity().getQuantity());

        if (this.testVolumeAttach) {
            MachineVolume machineVolume = new MachineVolume();
            machineVolume.setVolume(volume);
            machineVolume.setInitialLocation(this.volumeDevice);

            job = computeService.addVolumeToMachine(machineId, machineVolume);
            this.waitForJobCompletion(job);

            job = computeService.removeVolumeFromMachine(machineId, machineVolume);
            this.waitForJobCompletion(job);
        }

        job = volumeService.deleteVolume(volumeId);
        this.waitForJobCompletion(job);

        Assert.assertNull(volumeService.getVolume(volumeId));

        job = computeService.deleteMachine(machineId);
        this.waitForJobCompletion(job);

        Assert.assertNull(computeService.getMachine(machineId));

    }
}
