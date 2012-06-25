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
import org.ow2.sirocco.cloudmanager.connector.api.ConnectorException;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFactory;
import org.ow2.sirocco.cloudmanager.connector.api.IComputeService;
import org.ow2.sirocco.cloudmanager.connector.api.IVolumeService;
import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.api.IJobManager;
import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.impl.JobManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory.MemoryUnit;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
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

    private static final String MACHINE_STOP_PROP = "machine.stop";

    private static final String VOLUME_ATTACH_PROP = "volume.attach";

    private static final String VOLUME_CONFIG_SIZE_PROP = "volumeconfig.size";

    private static final String VOLUME_DEVICE_PROP = "volume.device";

    private static final String PUBLIC_KEY_PROP = "credentials.publickey";

    private static final String LOCATION_COUNTRY_PROP = "location.country";

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

        this.testStopMachine = Boolean.valueOf(prop.getProperty(CloudProviderConnectorTest.MACHINE_STOP_PROP));
        this.testVolumeAttach = Boolean.valueOf(prop.getProperty(CloudProviderConnectorTest.VOLUME_ATTACH_PROP));

        this.jobManager = JobManager.newJobManager();
        String className = "org.ow2.sirocco.cloudmanager.connector." + providerName.toLowerCase() + "." + providerName
            + "CloudProviderConnectorFactory";
        Class<?> connectorFactoryClass = Class.forName(className);

        Constructor<?> ctor = connectorFactoryClass.getDeclaredConstructor(IJobManager.class);

        ICloudProviderConnectorFactory factory = (ICloudProviderConnectorFactory) ctor.newInstance(this.jobManager);

        CloudProviderLocation location = null;
        String country = prop.getProperty(CloudProviderConnectorTest.LOCATION_COUNTRY_PROP);
        if (country == null) {
            location = factory.listCloudProviderLocations().iterator().next();
        } else {
            for (CloudProviderLocation loc : factory.listCloudProviderLocations()) {
                if (loc.getCountryName().equals(country)) {
                    location = loc;
                    break;
                }
            }
        }
        if (location == null) {
            throw new Exception("Cannot find suitable provider location for country " + country);
        }
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
        List<MachineTemplateNetworkInterface> nics = new ArrayList<MachineTemplateNetworkInterface>();
        MachineTemplateNetworkInterface nic = new MachineTemplateNetworkInterface();
        nic.setNetworkType(Network.Type.PRIVATE);
        nics.add(nic);
        nic = new MachineTemplateNetworkInterface();
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

        System.out.println("Creating machine...");
        Job job = computeService.createMachine(machineCreate);
        this.waitForJobCompletion(job);

        String machineId = job.getTargetEntity().getProviderAssignedId();
        Machine machine = computeService.getMachine(machineId);
        System.out.println("Machine id=" + machine.getProviderAssignedId() + " state=" + machine.getState());
        for (MachineNetworkInterface netInterface : machine.getNetworkInterfaces()) {
            System.out.print("\t Network " + netInterface.getNetworkType() + " addresses=");
            if (netInterface.getAddresses() != null) {
                for (Address addr : netInterface.getAddresses()) {
                    System.out.print(addr.getIp() + " ");
                }
            }
            System.out.println();
        }
        if (this.testStopMachine) {
            if (machine.getState() == Machine.State.STOPPED) {
                System.out.println("Starting machine " + machineId);
                job = computeService.startMachine(machineId);
                this.waitForJobCompletion(job);
                machine = computeService.getMachine(machineId);
                Assert.assertEquals(Machine.State.STARTED, machine.getState());
            } else {
                System.out.println("Stopping machine " + machineId);
                job = computeService.stopMachine(machineId);
                this.waitForJobCompletion(job);
                machine = computeService.getMachine(machineId);
                Assert.assertEquals(Machine.State.STOPPED, machine.getState());
                System.out.println("Starting machine " + machineId);
                job = computeService.startMachine(machineId);
                this.waitForJobCompletion(job);
                machine = computeService.getMachine(machineId);
                Assert.assertEquals(Machine.State.STARTED, machine.getState());
            }
        }

        VolumeCreate volumeCreate = new VolumeCreate();
        VolumeTemplate volumeTemplate = new VolumeTemplate();
        VolumeConfiguration volumeConfig = new VolumeConfiguration();
        volumeConfig.setCapacity(this.volumeConfigSizeGB * 1000 * 1000);
        volumeTemplate.setVolumeConfig(volumeConfig);
        volumeCreate.setVolumeTemplate(volumeTemplate);
        volumeCreate.setName("test");
        volumeCreate.setDescription("a test volume");

        System.out.println("Creating Volume size=" + this.volumeConfigSizeGB + "GB");
        job = volumeService.createVolume(volumeCreate);
        this.waitForJobCompletion(job);
        String volumeId = job.getTargetEntity().getProviderAssignedId();

        Volume volume = volumeService.getVolume(volumeId);
        System.out.println("Volume id=" + volume.getProviderAssignedId() + " size=" + volume.getCapacity() + " KB");

        if (this.testVolumeAttach) {
            MachineVolume machineVolume = new MachineVolume();
            machineVolume.setVolume(volume);
            machineVolume.setInitialLocation(this.volumeDevice);
            System.out.println("Attaching volume " + volume.getProviderAssignedId() + " to machine " + machineId);
            job = computeService.addVolumeToMachine(machineId, machineVolume);
            this.waitForJobCompletion(job);
            System.out.println("Detaching volume " + volume.getProviderAssignedId() + " from machine " + machineId);
            job = computeService.removeVolumeFromMachine(machineId, machineVolume);
            this.waitForJobCompletion(job);
        }

        System.out.println("Deleting volume " + volumeId);
        job = volumeService.deleteVolume(volumeId);
        this.waitForJobCompletion(job);

        try {
            volume = volumeService.getVolume(volumeId);
            if (volume.getState() != Volume.State.DELETED) {
                throw new Exception("Volume still exists after deletion");
            }
        } catch (ConnectorException ex) {
            // OK
        }

        System.out.println("Deleting machine " + machineId);
        job = computeService.deleteMachine(machineId);
        this.waitForJobCompletion(job);

        try {
            machine = computeService.getMachine(machineId);
            if (machine.getState() != Machine.State.DELETED) {
                throw new Exception("Machine still exists after deletion");
            }
        } catch (ConnectorException ex) {
            // OK
        }

    }
}
