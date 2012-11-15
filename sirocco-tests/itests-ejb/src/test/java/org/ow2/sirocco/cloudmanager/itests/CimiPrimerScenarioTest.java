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
package org.ow2.sirocco.cloudmanager.itests;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.itests.util.CustomDBUnitDeleteAllOperation;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterfaceAddress;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

public class CimiPrimerScenarioTest {
    private static final String USER_NAME = "ANONYMOUS";

    private static final String CLOUD_PROVIDER_TYPE = "mock";

    private static final String ACCOUNT_LOGIN = "ignored";

    private static final String ACCOUNT_CREDENTIALS = "ignored";

    private static final int ASYNC_OPERATION_WAIT_TIME_IN_SECONDS = 60;

    /**
     * Initial Context Factory.
     */
    private static final String INITIAL_CONTEXT_FACTORY = "org.ow2.carol.jndi.spi.MultiOrbInitialContextFactory";

    /**
     * Timeout (in seconds) for Sirocco to initialize.
     */
    private static final int INITIALIZE_TIMEOUT = 30;

    private IRemoteMachineManager machineManager;

    private IVolumeManager volumeManager;

    private IRemoteCredentialsManager credManager;

    private IRemoteMachineImageManager machineImageManager;

    private IRemoteCloudProviderManager cloudProviderManager;

    private IRemoteUserManager userManager;

    private IRemoteJobManager jobManager;

    private void connectToCloudManager() throws Exception {
        String carolPortString = System.getProperty("carol.port");
        Assert.assertNotNull("carol.port not set!", carolPortString);
        int carolPort = Integer.parseInt(carolPortString);

        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, CimiPrimerScenarioTest.INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, "rmi://localhost:" + carolPort);
        final long timeout = System.currentTimeMillis() + CimiPrimerScenarioTest.INITIALIZE_TIMEOUT * 1000;
        while (true) {
            try {
                Context context = new InitialContext(env);
                this.machineManager = (IRemoteMachineManager) context.lookup(IMachineManager.EJB_JNDI_NAME);
                this.volumeManager = (IRemoteVolumeManager) context.lookup(IVolumeManager.EJB_JNDI_NAME);
                this.cloudProviderManager = (IRemoteCloudProviderManager) context.lookup(ICloudProviderManager.EJB_JNDI_NAME);
                this.userManager = (IRemoteUserManager) context.lookup(IUserManager.EJB_JNDI_NAME);
                this.credManager = (IRemoteCredentialsManager) context.lookup(ICredentialsManager.EJB_JNDI_NAME);
                this.machineImageManager = (IRemoteMachineImageManager) context.lookup(IMachineImageManager.EJB_JNDI_NAME);

                this.jobManager = (IRemoteJobManager) context.lookup(IJobManager.EJB_JNDI_NAME);
                break;
            } catch (NamingException e) {
                if (System.currentTimeMillis() > timeout) {
                    throw e;
                } else {
                    Thread.sleep(1000);
                }
            }
        }
    }

    private void setUpDatabase() throws Exception {
        PropertiesBasedJdbcDatabaseTester databaseTest;
        Reader reader = new FileReader(new File(System.getProperty("dbunit.dataset")));
        XmlDataSet dataSet = new XmlDataSet(reader);
        databaseTest = new PropertiesBasedJdbcDatabaseTester();
        databaseTest.setDataSet(dataSet);
        Assert.assertNotNull("database.type not set!", System.getProperty("database.type"));
        // databaseTest.setSetUpOperation(DatabaseOperation.DELETE_ALL);
        databaseTest.setSetUpOperation(new CustomDBUnitDeleteAllOperation(System.getProperty("database.type")));
        databaseTest.onSetup();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        this.setUpDatabase();
        this.connectToCloudManager();
        User user = this.userManager.createUser("Lov", "Maps", "lov@maps.com", CimiPrimerScenarioTest.USER_NAME, "232908Ivry");
        CloudProvider provider = this.cloudProviderManager.createCloudProvider(CimiPrimerScenarioTest.CLOUD_PROVIDER_TYPE,
            "mock");
        CloudProviderAccount account = this.cloudProviderManager.createCloudProviderAccount(provider.getId().toString(),
            CimiPrimerScenarioTest.ACCOUNT_LOGIN, CimiPrimerScenarioTest.ACCOUNT_CREDENTIALS);
        this.cloudProviderManager.addCloudProviderAccountToUser(user.getId().toString(), account.getId().toString());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private void waitForJobCompletion(Job job) throws Exception {
        int counter = CimiPrimerScenarioTest.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        String jobId = job.getId().toString();
        while (true) {
            job = this.jobManager.getJobById(jobId);
            if (job.getState() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);

            if (counter-- == 0) {
                throw new Exception("Machine operation time out");
            }
        }
    }

    private MachineConfiguration buildMachineConfiguration(final String name, final String description, final int numCpus,
        final int ramSizeInMB, final int diskSizeInGB, final String location) {
        MachineConfiguration machineConfig = new MachineConfiguration();
        machineConfig.setName(name);
        machineConfig.setDescription(description);

        DiskTemplate disk = new DiskTemplate();
        disk.setCapacity(diskSizeInGB * 1000 * 1000);
        disk.setInitialLocation(location);
        disk.setFormat("ext3");

        machineConfig.setCpu(numCpus);
        machineConfig.setMemory(ramSizeInMB * 1024);
        machineConfig.setDisks(Collections.singletonList(disk));
        return machineConfig;
    }

    private VolumeConfiguration buildVolumeConfiguration(final String name, final String description, final String format,
        final int capacityInMBytes) {
        VolumeConfiguration volumeConfig = new VolumeConfiguration();
        volumeConfig.setName(name);
        volumeConfig.setDescription(description);
        volumeConfig.setFormat(format);
        volumeConfig.setType("http://schemas.dmtf.org/cimi/1/mapped");
        volumeConfig.setCapacity(capacityInMBytes * 1000);
        return volumeConfig;
    }

    public void initDatabase() throws Exception {
        MachineImage image = new MachineImage();
        image.setName("WinXP SP2");
        image.setDescription("Windows XP with Service Pack 2");
        image.setImageLocation("http://ow2.org/sirocco/data/1234");
        Job job = this.machineImageManager.createMachineImage(image);
        System.out.println(" initDatabase wait for job " + job.getId());
        this.waitForJobCompletion(job);

        image.setName("Win7");
        image.setDescription("Windows 7");
        image.setImageLocation("http://ow2.org/sirocco/data/5678");
        job = this.machineImageManager.createMachineImage(image);
        this.waitForJobCompletion(job);

        image.setName("Linux OpenSuse");
        image.setDescription("OpenSuse v10");
        image.setImageLocation("http://ow2.org/sirocco/data/9012");
        job = this.machineImageManager.createMachineImage(image);
        this.waitForJobCompletion(job);

        MachineConfiguration machineConfig = this.buildMachineConfiguration("small",
            "small: 1 CPU 3.5Ghz, 512MB RAM, 1GB disk", 1, 512, 1, "/dev/sda");
        this.machineManager.createMachineConfiguration(machineConfig);
        machineConfig = this.buildMachineConfiguration("medium", "medium: 2 CPU 3.5Ghz, 2GB RAM, 10GB disk", 2, 1024 * 2, 10,
            "/dev/sdb");
        this.machineManager.createMachineConfiguration(machineConfig);
        machineConfig = this.buildMachineConfiguration("large", "large: 4 CPU 3.5Ghz, 8GB RAM, 50GB disk", 4, 8 * 1024, 50,
            "/dev/sdc");
        this.machineManager.createMachineConfiguration(machineConfig);

        VolumeConfiguration volumeConfig = this.buildVolumeConfiguration("small", "Small Ext3 Volume", "ext3", 60);
        this.volumeManager.createVolumeConfiguration(volumeConfig);
        volumeConfig = this.buildVolumeConfiguration("medium", "Medium Ext3 Volume", "ext3", 120);
        this.volumeManager.createVolumeConfiguration(volumeConfig);
        volumeConfig = this.buildVolumeConfiguration("large", "Large Ext3 Volume", "ext3", 240);
        this.volumeManager.createVolumeConfiguration(volumeConfig);
    }

    void testMachineTemplateCreate() throws Exception {
        /**
         * Retrieve the list of Machine Images
         */

        List<MachineImage> machineImages = this.machineImageManager.getMachineImages();
        for (MachineImage image : machineImages) {
            System.out.println("MachineImage id=" + image.getId());
        }

        /**
         * Choose a Machine Image (first one)
         */

        MachineImage image = this.machineImageManager.getMachineImageById(machineImages.get(0).getId().toString());
        System.out.println("MachineImage [id=" + image.getId() + ", name=" + image.getName() + ", description="
            + image.getDescription() + ", created=" + image.getCreated() + ", location=" + image.getImageLocation() + "]");

        /**
         * Retrieve the list of Machine Configurations
         */

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations();
        for (MachineConfiguration machineConfig : machineConfigs) {
            System.out.println("MachineConfiguration id=" + machineConfig.getId());
        }

        /**
         * Choose a Machine Configuration (first one)
         */

        MachineConfiguration machineConfig = this.machineManager.getMachineConfigurationById(machineConfigs.get(0).getId()
            .toString());
        System.out.println("MachineConfiguration [id=" + machineConfig.getId() + ", name=" + machineConfig.getName()
            + ", description=" + machineConfig.getDescription() + ", created=" + machineConfig.getCreated() + ", cpu="
            + machineConfig.getCpu() + ", memory=" + machineConfig.getMemory() + ", disks=" + machineConfig.getDisks()
            + "]");

        /**
         * Create a new Credentials entity
         */

        CredentialsTemplate credentialsTemplate = new CredentialsTemplate();
        credentialsTemplate.setUserName("PattySmith");
        credentialsTemplate.setPassword("doesnotsing");
        CredentialsCreate credentialsCreate = new CredentialsCreate();
        credentialsCreate.setCredentialTemplate(credentialsTemplate);
        credentialsCreate.setName("Default");
        credentialsCreate.setDescription("Default User");
        Credentials credentials = this.credManager.createCredentials(credentialsCreate);

        System.out.println("New Credentials id=" + credentials.getId());
        MachineTemplate machineTemplate = new MachineTemplate();

        machineTemplate.setMachineConfig(machineConfig);
        machineTemplate.setMachineImage(image);
        machineTemplate.setCredential(credentials);

        List<MachineVolume> volColl = new ArrayList<MachineVolume>();

        machineTemplate.setVolumes(volColl);
        List<MachineVolumeTemplate> vtColl = new ArrayList<MachineVolumeTemplate>();

        machineTemplate.setVolumeTemplates(vtColl);

        // create network interfaces
        MachineTemplateNetworkInterface mtnic = null;
        MachineTemplateNetworkInterface.InterfaceState ss = MachineTemplateNetworkInterface.InterfaceState.ACTIVE;
        for (int i = 0; i < 2; i++) {
            mtnic = new MachineTemplateNetworkInterface();
            mtnic.setState(MachineTemplateNetworkInterface.InterfaceState.ACTIVE);
            machineTemplate.addNetworkInterface(mtnic);
        }
        MachineTemplate mt = null;
        try {
            mt = this.machineManager.createMachineTemplate(machineTemplate);
        } catch (Exception e) {
            System.out.println("testMachineTemplateCreate failed " + e.getMessage());
            throw new Exception("machine template create failed ");
        }
        System.out.println("testMachineTemplateCreate returned " + mt.getId());

        try {
            MachineTemplate mtt = this.machineManager.getMachineTemplateById(mt.getId().toString());
            List<MachineTemplateNetworkInterface> items = mtt.getNetworkInterfaces();
            if (items.size() == 0) {
                System.out.println(" Strange no network interface?");
            }
            for (MachineTemplateNetworkInterface intf : items) {
                System.out.println("testMachineTemplateCreate " + intf.getId() + " " + intf.getState());
            }
        } catch (Exception e) {
            throw new Exception("read of newly created machine template create failed ");
        }
    }

    String createMachine() throws Exception {
        /**
         * Retrieve the list of Machine Images
         */

        List<MachineImage> machineImages = this.machineImageManager.getMachineImages();
        for (MachineImage image : machineImages) {
            System.out.println("MachineImage id=" + image.getId());
        }

        /**
         * Choose a Machine Image (first one)
         */

        MachineImage image = this.machineImageManager.getMachineImageById(machineImages.get(0).getId().toString());
        System.out.println("MachineImage [id=" + image.getId() + ", name=" + image.getName() + ", description="
            + image.getDescription() + ", created=" + image.getCreated() + ", location=" + image.getImageLocation() + "]");

        /**
         * Retrieve the list of Machine Configurations
         */

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations();
        for (MachineConfiguration machineConfig : machineConfigs) {
            System.out.println("MachineConfiguration id=" + machineConfig.getId());
        }

        /**
         * Choose a Machine Configuration (first one)
         */

        MachineConfiguration machineConfig = this.machineManager.getMachineConfigurationById(machineConfigs.get(0).getId()
            .toString());
        System.out.println("MachineConfiguration [id=" + machineConfig.getId() + ", name=" + machineConfig.getName()
            + ", description=" + machineConfig.getDescription() + ", created=" + machineConfig.getCreated() + ", cpu="
            + machineConfig.getCpu() + ", memory=" + machineConfig.getMemory() + ", disks=" + machineConfig.getDisks()
            + "]");

        /**
         * Create a new Credentials entity
         */

        CredentialsTemplate credentialsTemplate = new CredentialsTemplate();
        credentialsTemplate.setUserName("JoeSmith");
        credentialsTemplate.setPassword("letmein");
        CredentialsCreate credentialsCreate = new CredentialsCreate();
        credentialsCreate.setCredentialTemplate(credentialsTemplate);
        credentialsCreate.setName("Default");
        credentialsCreate.setDescription("Default User");
        Credentials credentials = this.credManager.createCredentials(credentialsCreate);

        System.out.println("New Credentials id=" + credentials.getId());

        /**
         * Create a new Machine
         */

        MachineCreate machineCreate = new MachineCreate();
        machineCreate.setName("myMachine1");
        machineCreate.setDescription("My very first machine");

        MachineTemplate machineTemplate = new MachineTemplate();

        machineTemplate.setMachineConfig(machineConfig);
        machineTemplate.setMachineImage(image);
        machineTemplate.setCredential(credentials);

        List<MachineVolume> volColl = new ArrayList<MachineVolume>();

        machineTemplate.setVolumes(volColl);
        List<MachineVolumeTemplate> vtColl = new ArrayList<MachineVolumeTemplate>();

        machineTemplate.setVolumeTemplates(vtColl);

        machineTemplate.setNetworkInterfaces(Collections.<MachineTemplateNetworkInterface> emptyList());
        machineCreate.setMachineTemplate(machineTemplate);

        Job job = this.machineManager.createMachine(machineCreate);
        String machineId = job.getTargetResource().getId().toString();
        System.out.println("createMachine wait for job completion " + machineId);
        this.waitForJobCompletion(job);
        return machineId;
    }

    public String createMachineWithPreExistingVolumes(final int loop) throws Exception {
        /**
         * Retrieve the list of Machine Images
         */
        System.out.println("createMachineWithPreExistingVolumes " + loop);

        List<MachineImage> machineImages = this.machineImageManager.getMachineImages();
        for (MachineImage image : machineImages) {
            System.out.println("MachineImage id=" + image.getId());
        }

        /**
         * Choose a Machine Image (first one)
         */

        MachineImage image = this.machineImageManager.getMachineImageById(machineImages.get(0).getId().toString());
        System.out.println("MachineImage [id=" + image.getId() + ", name=" + image.getName() + ", description="
            + image.getDescription() + ", created=" + image.getCreated() + ", location=" + image.getImageLocation() + "]");

        /**
         * Retrieve the list of Machine Configurations
         */

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations();
        for (MachineConfiguration machineConfig : machineConfigs) {
            System.out.println("MachineConfiguration id=" + machineConfig.getId());
        }

        /**
         * Choose a Machine Configuration (first one)
         */

        MachineConfiguration machineConfig = this.machineManager.getMachineConfigurationById(machineConfigs.get(0).getId()
            .toString());
        System.out.println("MachineConfiguration [id=" + machineConfig.getId() + ", name=" + machineConfig.getName()
            + ", description=" + machineConfig.getDescription() + ", created=" + machineConfig.getCreated() + ", cpu="
            + machineConfig.getCpu() + ", memory=" + machineConfig.getMemory() + ", disks=" + machineConfig.getDisks()
            + "]");

        /**
         * Create a new Credentials entity
         */

        CredentialsTemplate credentialsTemplate = new CredentialsTemplate();
        credentialsTemplate.setUserName("JoeSmith" + loop);
        credentialsTemplate.setPassword("letmein" + loop);
        CredentialsCreate credentialsCreate = new CredentialsCreate();
        credentialsCreate.setCredentialTemplate(credentialsTemplate);
        credentialsCreate.setName("Default" + loop);
        credentialsCreate.setDescription("Default User" + loop);
        Credentials credentials = this.credManager.createCredentials(credentialsCreate);

        System.out.println("New Credentials id=" + credentials.getId());

        /**
         * Create a new volume
         */
        Volume v1 = this.createVolume("testVolumeAttach1" + loop);
        Assert.assertNotNull(v1.getId().toString());
        System.out.println(" createMachineWithPreExistingVolumes created volume " + v1.getId().toString());
        Volume v2 = this.createVolume("testVolumeAttach2" + loop);
        Assert.assertNotNull(v2.getId().toString());

        /**
         * Create a new Machine
         */

        MachineCreate machineCreate = new MachineCreate();
        machineCreate.setName("myMachine1" + loop);
        machineCreate.setDescription("My very first machine " + loop);

        MachineTemplate machineTemplate = new MachineTemplate();

        machineTemplate.setMachineConfig(machineConfig);
        machineTemplate.setMachineImage(image);
        machineTemplate.setCredential(credentials);

        ArrayList<MachineVolume> vtItems = new ArrayList<MachineVolume>();
        MachineVolume mv1 = new MachineVolume();
        mv1.setVolume(v1);
        mv1.setInitialLocation("/dev/sda1" + loop);
        vtItems.add(mv1);

        MachineVolume mv2 = new MachineVolume();
        mv2.setVolume(v2);
        mv2.setInitialLocation("/dev/sda2" + loop);
        vtItems.add(mv2);
        machineTemplate.setVolumes(vtItems);

        List<MachineVolumeTemplate> vtColl = new ArrayList<MachineVolumeTemplate>();
        machineTemplate.setVolumeTemplates(vtColl);

        machineTemplate.setNetworkInterfaces(Collections.<MachineTemplateNetworkInterface> emptyList());
        machineCreate.setMachineTemplate(machineTemplate);

        System.out.println(" createMachineWithPreExistingVolumes : create machine now");
        Job job = this.machineManager.createMachine(machineCreate);
        String machineId = job.getTargetResource().getId().toString();
        System.out.println(" createMachineWithPreExistingVolumes : new machine id " + machineId + " with job " + job.getId());
        this.waitForJobCompletion(job);

        Machine m = this.machineManager.getMachineById(machineId);
        Assert.assertNotNull(m.getVolumes());
        Assert.assertEquals(2, m.getVolumes().size());

        int input = v1.getId() + v2.getId();
        int output = m.getVolumes().get(0).getVolume().getId() + m.getVolumes().get(1).getVolume().getId();
        Assert.assertEquals(input, output);
        input = v1.getId() * v2.getId();
        output = m.getVolumes().get(0).getVolume().getId() * m.getVolumes().get(1).getVolume().getId();
        Assert.assertEquals(input, output);
        return machineId;
    }

    public String createMachineWithNewVolumesAndNetworks(final int loop) throws Exception {
        /**
         * Retrieve the list of Machine Images
         */

        List<MachineImage> machineImages = this.machineImageManager.getMachineImages();
        for (MachineImage image : machineImages) {
            System.out.println("MachineImage id=" + image.getId());
        }

        /**
         * Choose a Machine Image (first one)
         */

        MachineImage image = this.machineImageManager.getMachineImageById(machineImages.get(0).getId().toString());
        System.out.println("MachineImage [id=" + image.getId() + ", name=" + image.getName() + ", description="
            + image.getDescription() + ", created=" + image.getCreated() + ", location=" + image.getImageLocation() + "]");

        /**
         * Retrieve the list of Machine Configurations
         */

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations();
        for (MachineConfiguration machineConfig : machineConfigs) {
            System.out.println("MachineConfiguration id=" + machineConfig.getId());
        }

        /**
         * Choose a Machine Configuration (first one)
         */

        MachineConfiguration machineConfig = this.machineManager.getMachineConfigurationById(machineConfigs.get(0).getId()
            .toString());
        System.out.println("MachineConfiguration [id=" + machineConfig.getId() + ", name=" + machineConfig.getName()
            + ", description=" + machineConfig.getDescription() + ", created=" + machineConfig.getCreated() + ", cpu="
            + machineConfig.getCpu() + ", memory=" + machineConfig.getMemory() + ", disks=" + machineConfig.getDisks()
            + "]");

        /**
         * Create a new Credentials entity
         */

        CredentialsTemplate credentialsTemplate = new CredentialsTemplate();
        credentialsTemplate.setUserName("JoeSmith" + loop);
        credentialsTemplate.setPassword("letmein" + loop);
        CredentialsCreate credentialsCreate = new CredentialsCreate();
        credentialsCreate.setCredentialTemplate(credentialsTemplate);
        credentialsCreate.setName("Default" + loop);
        credentialsCreate.setDescription("Default User" + loop);
        Credentials credentials = this.credManager.createCredentials(credentialsCreate);

        System.out.println("New Credentials id=" + credentials.getId());

        /**
         * Create a new Machine
         */

        MachineCreate machineCreate = new MachineCreate();
        machineCreate.setName("myMachine1" + loop);
        machineCreate.setDescription("Testing with machine" + loop);

        MachineTemplate machineTemplate = new MachineTemplate();

        machineTemplate.setMachineConfig(machineConfig);
        machineTemplate.setMachineImage(image);
        machineTemplate.setCredential(credentials);

        List<MachineVolume> volColl = new ArrayList<MachineVolume>();

        machineTemplate.setVolumes(volColl);

        ArrayList<MachineVolumeTemplate> vtItems = new ArrayList<MachineVolumeTemplate>();
        MachineVolumeTemplate mvt1 = new MachineVolumeTemplate();
        VolumeTemplate vt1 = this.createVolumeTemplate("dummy1" + loop);

        mvt1.setVolumeTemplate(vt1);
        mvt1.setInitialLocation("/dev/sda1" + loop);
        vtItems.add(mvt1);
        MachineVolumeTemplate mvt2 = new MachineVolumeTemplate();
        VolumeTemplate vt2 = this.createVolumeTemplate("dummy2" + loop);

        mvt2.setVolumeTemplate(vt2);
        mvt2.setInitialLocation("/dev/sda2" + loop);
        vtItems.add(mvt2);
        machineTemplate.setVolumeTemplates(vtItems);

        MachineTemplateNetworkInterface mtnic = null;

        for (int i = 0; i < 4; i++) {
            mtnic = new MachineTemplateNetworkInterface();
            mtnic.setState(MachineTemplateNetworkInterface.InterfaceState.ACTIVE);
            List<Address> addresses = new ArrayList<Address>();
            for (int j = 0; j < 3; j++) {
                Address addr = new Address();
                String ip = "AA.BB.CC.D" + i + loop;

                if (j == 0) {
                    addr.setAllocation("static");
                } else {
                    addr.setAllocation("dynamic");
                }

                addr.setIp(ip);
                Set<String> dnsEntries = new HashSet<String>();
                dnsEntries.add("162.99.11.1" + i + loop);
                addr.setDns(dnsEntries);
                addresses.add(addr);
            }

            mtnic.setAddresses(addresses);
            machineTemplate.addNetworkInterface(mtnic);
        }

        machineCreate.setMachineTemplate(machineTemplate);
        System.out.println("createMachineWithNewVolumesAndNetworks create machine ");
        Job job = this.machineManager.createMachine(machineCreate);
        String machineId = job.getTargetResource().getId().toString();
        System.out.println("createMachineWithNewVolumes wait for job completion ");
        this.waitForJobCompletion(job);
        Machine m = this.machineManager.getMachineById(machineId);
        Assert.assertNotNull(m.getVolumes());
        Assert.assertEquals(2, m.getVolumes().size());
        List<MachineNetworkInterface> nics = m.getNetworkInterfaces();
        Assert.assertEquals(4, nics.size());
        System.out.println(" createMachineWithNewVolumes query address entries for one of " + nics.size()
            + " network interfaces ");
        QueryResult<MachineNetworkInterfaceAddress> addressEntries = this.machineManager.getMachineNetworkInterfaceAddresses(
            machineId, nics.get(0).getId().toString(), -1, -1, null, null);
        Assert.assertNotNull(addressEntries);
        Assert.assertEquals(addressEntries.getItems().size(), 3);
        Assert.assertEquals(addressEntries.getCount(), 3);
        MachineNetworkInterfaceAddress aaa = addressEntries.getItems().get(0);
        System.out.println("createMachineWithNewVolumesAndNetworks " + aaa.getAddress().getIp() + " "
            + aaa.getAddress().getAllocation());
        return machineId;
    }

    public String createMachineWithNewVolumes() throws Exception {
        /**
         * Retrieve the list of Machine Images
         */

        List<MachineImage> machineImages = this.machineImageManager.getMachineImages();
        for (MachineImage image : machineImages) {
            System.out.println("MachineImage id=" + image.getId());
        }

        /**
         * Choose a Machine Image (first one)
         */

        MachineImage image = this.machineImageManager.getMachineImageById(machineImages.get(0).getId().toString());
        System.out.println("MachineImage [id=" + image.getId() + ", name=" + image.getName() + ", description="
            + image.getDescription() + ", created=" + image.getCreated() + ", location=" + image.getImageLocation() + "]");

        /**
         * Retrieve the list of Machine Configurations
         */

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations();
        for (MachineConfiguration machineConfig : machineConfigs) {
            System.out.println("MachineConfiguration id=" + machineConfig.getId());
        }

        /**
         * Choose a Machine Configuration (first one)
         */

        MachineConfiguration machineConfig = this.machineManager.getMachineConfigurationById(machineConfigs.get(0).getId()
            .toString());
        System.out.println("MachineConfiguration [id=" + machineConfig.getId() + ", name=" + machineConfig.getName()
            + ", description=" + machineConfig.getDescription() + ", created=" + machineConfig.getCreated() + ", cpu="
            + machineConfig.getCpu() + ", memory=" + machineConfig.getMemory() + ", disks=" + machineConfig.getDisks()
            + "]");

        /**
         * Create a new Credentials entity
         */

        CredentialsTemplate credentialsTemplate = new CredentialsTemplate();
        credentialsTemplate.setUserName("JoeSmith");
        credentialsTemplate.setPassword("letmein");
        CredentialsCreate credentialsCreate = new CredentialsCreate();
        credentialsCreate.setCredentialTemplate(credentialsTemplate);
        credentialsCreate.setName("Default");
        credentialsCreate.setDescription("Default User");
        Credentials credentials = this.credManager.createCredentials(credentialsCreate);

        System.out.println("New Credentials id=" + credentials.getId());

        /**
         * Create a new Machine
         */

        MachineCreate machineCreate = new MachineCreate();
        machineCreate.setName("myMachine1");
        machineCreate.setDescription("My very first machine");

        MachineTemplate machineTemplate = new MachineTemplate();

        machineTemplate.setMachineConfig(machineConfig);
        machineTemplate.setMachineImage(image);
        machineTemplate.setCredential(credentials);

        List<MachineVolume> volColl = new ArrayList<MachineVolume>();

        machineTemplate.setVolumes(volColl);

        ArrayList<MachineVolumeTemplate> vtItems = new ArrayList<MachineVolumeTemplate>();
        MachineVolumeTemplate mvt = new MachineVolumeTemplate();
        VolumeTemplate vt = this.createVolumeTemplate("dummy");

        mvt.setVolumeTemplate(vt);
        mvt.setInitialLocation("/dev/sda");
        vtItems.add(mvt);

        machineTemplate.setVolumeTemplates(vtItems);

        machineTemplate.setNetworkInterfaces(Collections.<MachineTemplateNetworkInterface> emptyList());
        machineCreate.setMachineTemplate(machineTemplate);
        System.out.println("createMachineWithNewVolumes create machine ");
        Job job = this.machineManager.createMachine(machineCreate);
        String machineId = job.getTargetResource().getId().toString();
        System.out.println("createMachineWithNewVolumes wait for job completion ");
        this.waitForJobCompletion(job);
        return machineId;
    }

    public VolumeTemplate createVolumeTemplate(final String name) throws Exception {
        /**
         * Retrieve the list of Volume Configurations
         */
        System.out.println("createVolumeTemplate " + name);
        List<VolumeConfiguration> volumeConfigs = this.volumeManager.getVolumeConfigurations();
        for (VolumeConfiguration volumeConfig : volumeConfigs) {
            System.out.println("VolumeConfiguration id=" + volumeConfig.getId());
        }

        /**
         * Choose a VolumeConfiguration (first one)
         */

        VolumeConfiguration smallVolumeConfig = this.volumeManager.getVolumeConfigurationById(volumeConfigs.get(0).getId()
            .toString());
        System.out.println(smallVolumeConfig);

        VolumeTemplate inVolumeTemplate = new VolumeTemplate();
        inVolumeTemplate.setVolumeConfig(smallVolumeConfig);

        VolumeTemplate outVolumeTemplate = this.volumeManager.createVolumeTemplate(inVolumeTemplate);
        Assert.assertNotNull(outVolumeTemplate);
        Assert.assertNotNull(outVolumeTemplate.getId());
        Assert.assertNotNull(outVolumeTemplate.getVolumeConfig());
        Assert.assertEquals(smallVolumeConfig.getId(), outVolumeTemplate.getVolumeConfig().getId());

        Assert.assertEquals(inVolumeTemplate.getName(), outVolumeTemplate.getName());
        Assert.assertEquals(inVolumeTemplate.getDescription(), outVolumeTemplate.getDescription());
        return outVolumeTemplate;

    }

    public Volume createVolume(final String name) throws Exception {
        String volumeId;
        /**
         * Retrieve the list of Volume Configurations
         */

        List<VolumeConfiguration> volumeConfigs = this.volumeManager.getVolumeConfigurations();
        for (VolumeConfiguration volumeConfig : volumeConfigs) {
            System.out.println("VolumeConfiguration id=" + volumeConfig.getId());
        }

        /**
         * Choose a VolumeConfiguration (first one)
         */

        VolumeConfiguration smallVolumeConfig = this.volumeManager.getVolumeConfigurationById(volumeConfigs.get(0).getId()
            .toString());
        System.out.println(smallVolumeConfig);

        /**
         * Create Volume
         */

        VolumeCreate volumeCreate = new VolumeCreate();
        volumeCreate.setName(name);
        volumeCreate.setDescription("My first new volume");
        VolumeTemplate volumeTemplate = new VolumeTemplate();
        volumeTemplate.setVolumeConfig(smallVolumeConfig);
        volumeCreate.setVolumeTemplate(volumeTemplate);

        Job job = this.volumeManager.createVolume(volumeCreate);

        volumeId = job.getTargetResource().getId().toString();
        System.out.println(" createVolume: wait for volume creation completion ");
        this.waitForJobCompletion(job);

        /**
         * Retrieve the Volume information
         */

        Volume volume = this.volumeManager.getVolumeById(volumeId);
        System.out.println(volume);
        return volume;
    }

    @Test
    public void testScenarioOne() throws Exception {
        this.initDatabase();
        /**
         * Retrieve the CEP
         */

        CloudEntryPoint cep = this.machineManager.getCloudEntryPoint();
        System.out.println(" test machinetemplate create");
        this.testMachineTemplateCreate();
        System.out.println(" test machine create ");
        String machineId = this.createMachine();
        System.out.println(" query newly created machine ");
        /**
         * Query the Machine
         */

        Machine machine = this.machineManager.getMachineById(machineId);
        System.out.println("Machine [id=" + machine.getId() + ", name=" + machine.getName() + ", description="
            + machine.getDescription() + ", " + machine.getCreated() + ", state=" + machine.getState() + ", "
            + machine.getCpu() + ", memory=" + machine.getMemory() + ", disks=" + machine.getDisks() + ", networkInterfaces="
            + machine.getNetworkInterfaces() + "]");

        /**
         * Start the Machine
         */

        Job job = this.machineManager.startMachine(machineId, null);
        this.waitForJobCompletion(job);

        /**
         * Query the Machine to verify if it started
         */

        machine = this.machineManager.getMachineById(machineId);
        System.out.println("Machine [id=" + machine.getId() + ", name=" + machine.getName() + ", description="
            + machine.getDescription() + ", " + machine.getCreated() + ", state=" + machine.getState() + ", "
            + machine.getCpu() + ", memory=" + machine.getMemory() + ", disks=" + machine.getDisks() + ", networkInterfaces="
            + machine.getNetworkInterfaces() + "]");

        /**
         * Stop the Machine
         */

        job = this.machineManager.stopMachine(machineId, false, null);
        this.waitForJobCompletion(job);

        /**
         * Update the Machine's name and description
         */
        Map<String, Object> attributeToUpdate = new HashMap<String, Object>();
        attributeToUpdate.put("name", "Cool Demo #1");
        attributeToUpdate.put("description", null);
        job = this.machineManager.updateMachineAttributes(machineId, attributeToUpdate);
        this.waitForJobCompletion(job);

        machine = this.machineManager.getMachineById(machineId);
        System.out.println("Machine [id=" + machine.getId() + ", name=" + machine.getName() + ", description="
            + machine.getDescription() + ", " + machine.getCreated() + ", state=" + machine.getState() + ", "
            + machine.getCpu() + ", memory=" + machine.getMemory() + ", disks=" + machine.getDisks() + ", networkInterfaces="
            + machine.getNetworkInterfaces() + "]");

        System.out.println("testScenarioOne leave ");
    }

    @Test
    public void testScenarioTwo() throws Exception {
        this.initDatabase();
        /**
         * Retrieve the CEP
         */
        System.out.println("testScenarioTwo ");
        CloudEntryPoint cep = this.machineManager.getCloudEntryPoint();
        System.out.println(" testScenarioTwo: create machine ");
        String machineId = this.createMachine();
        System.out.println(" testScenarioTwo: create volumes ");
        /**
         * Retrieve the list of Volume Configurations
         */

        List<VolumeConfiguration> volumeConfigs = this.volumeManager.getVolumeConfigurations();
        for (VolumeConfiguration volumeConfig : volumeConfigs) {
            System.out.println("VolumeConfiguration id=" + volumeConfig.getId());
        }

        /**
         * Choose a VolumeConfiguration (first one)
         */

        VolumeConfiguration smallVolumeConfig = this.volumeManager.getVolumeConfigurationById(volumeConfigs.get(0).getId()
            .toString());
        System.out.println(smallVolumeConfig);

        /**
         * Create Volume
         */

        VolumeCreate volumeCreate = new VolumeCreate();
        volumeCreate.setName("myVolume1");
        volumeCreate.setDescription("My first new volume");
        VolumeTemplate volumeTemplate = new VolumeTemplate();
        volumeTemplate.setVolumeConfig(smallVolumeConfig);
        volumeCreate.setVolumeTemplate(volumeTemplate);

        Job job = this.volumeManager.createVolume(volumeCreate);

        String volumeId = job.getTargetResource().getId().toString();
        System.out.println(" testScenarioTwo: wait for volume creation completion ");
        this.waitForJobCompletion(job);

        /**
         * Retrieve the Volume information
         */

        Volume volume = this.volumeManager.getVolumeById(volumeId);
        System.out.println(volume);

        /**
         * Connect the new Volume to the Machine
         */
        System.out.println(" testScenarioTwo: add volume to machine ");
        MachineVolume machineVolume = new MachineVolume();
        machineVolume.setVolume(volume);
        machineVolume.setInitialLocation("/dev/sdb");
        job = this.machineManager.addVolumeToMachine(machineId, machineVolume);
        System.out.println(" testScenarioTwo: add volume to machine wait for job completion " + job.getId() + " "
            + job.getState());
        this.waitForJobCompletion(job);

        /**
         * Query the Machine's volume collection to verify the update
         */
        System.out.println(" testScenarioTwo: get machine volumes ");
        List<MachineVolume> machineVolumes = this.machineManager.getMachineVolumes(machineId);
        System.out.println(" testScenarioTwo: found " + machineVolumes.size() + " machine volumes ");
        for (MachineVolume mv : machineVolumes) {
            System.out.println(mv);
        }

        /**
         * Detach the volume now
         */
        System.out.println(" testScenarioTwo: detach machine volumes ");
        machineVolumes = this.machineManager.getMachineVolumes(machineId);
        for (MachineVolume mv : machineVolumes) {
            System.out.println("testScenarioTwo removing " + mv);
            this.machineManager.removeVolumeFromMachine(machineId, mv.getId().toString());
        }
    }

    @Test
    public void testScenarioThree() throws Exception {
        this.initDatabase();
        /**
         * Retrieve the CEP
         */
        System.out.println("testScenarioThree with volume attachment during machine create ");
        CloudEntryPoint cep = this.machineManager.getCloudEntryPoint();
        System.out.println(" test machinetemplate create");
        this.testMachineTemplateCreate();
        System.out.println(" test machine create with volumes ");
        String machineId = this.createMachineWithPreExistingVolumes(1);

        /**
         * Query the Machine
         */

        Machine machine = this.machineManager.getMachineById(machineId);
        System.out.println("Machine [id=" + machine.getId() + ", name=" + machine.getName() + ", description="
            + machine.getDescription() + ", " + machine.getCreated() + ", state=" + machine.getState() + ", "
            + machine.getCpu() + ", memory=" + machine.getMemory() + ", disks=" + machine.getDisks() + ", networkInterfaces="
            + machine.getNetworkInterfaces() + "]");

        /**
         * Start the Machine
         */

        Job job = this.machineManager.startMachine(machineId, null);
        this.waitForJobCompletion(job);

        /**
         * Query the Machine to verify if it started
         */

        machine = this.machineManager.getMachineById(machineId);
        System.out.println("Machine [id=" + machine.getId() + ", name=" + machine.getName() + ", description="
            + machine.getDescription() + ", " + machine.getCreated() + ", state=" + machine.getState() + ", "
            + machine.getCpu() + ", memory=" + machine.getMemory() + ", disks=" + machine.getDisks() + ", networkInterfaces="
            + machine.getNetworkInterfaces() + ", volumes=" + machine.getVolumes() + "]");

        /**
         * Stop the Machine
         */

        job = this.machineManager.stopMachine(machineId, false, null);
        this.waitForJobCompletion(job);

        /**
         * Update the Machine's name and description
         */
        Map<String, Object> attributeToUpdate = new HashMap<String, Object>();
        attributeToUpdate.put("name", "Cool Demo #1");
        attributeToUpdate.put("description", null);
        job = this.machineManager.updateMachineAttributes(machineId, attributeToUpdate);
        this.waitForJobCompletion(job);

        machine = this.machineManager.getMachineById(machineId);
        System.out.println("Machine [id=" + machine.getId() + ", name=" + machine.getName() + ", description="
            + machine.getDescription() + ", " + machine.getCreated() + ", state=" + machine.getState() + ", "
            + machine.getCpu() + ", memory=" + machine.getMemory() + ", disks=" + machine.getDisks() + ", networkInterfaces="
            + machine.getNetworkInterfaces() + "]");

        List<MachineDisk> disks = machine.getDisks();
        for (MachineDisk d : disks) {
            System.out.println("testScenarioThree: " + machineId + " " + d.getId());
            MachineDisk read = this.machineManager.getDiskFromMachine(machineId, d.getId().toString());
            Assert.assertNotNull(read);
            Assert.assertEquals(read.getId(), d.getId());
            if (read.getInitialLocation() != null) {
                System.out.println(" testScenarioThree " + read.getId() + " " + read.getInitialLocation());
            }

        }
        List<MachineVolume> attached = machine.getVolumes();
        Assert.assertNotNull(attached);

        System.out.println(" machine " + machineId + " has " + attached.size() + " volumes ");

        /** delete machine without detaching volumes */
        boolean caught = false;
        Job deleteJob = null;
        try {
            deleteJob = this.machineManager.deleteMachine(machineId);
        } catch (Exception e) {
            caught = true;
        }
        System.out.println("deletion of " + machineId + " returned with exception " + caught);
        if ((attached != null) && (attached.size() > 0)) {
            Assert.assertEquals(caught, true);

            System.out.println(" Machine deletion failed correct behaviour " + caught + " exception ");
            /** detach volumes first */
            Volume reattached = null;
            for (MachineVolume volume : attached) {
                reattached = volume.getVolume();
                deleteJob = this.machineManager.removeVolumeFromMachine(machineId, volume.getId().toString());
                this.waitForJobCompletion(deleteJob);
                System.out.println("detach of volume " + volume.getVolume().getId() + " terminated ");
            }
            System.out.println(" reattach one of the volumes " + machineId + " " + reattached.getId());
            Machine mm1 = this.machineManager.getMachineById(machineId);
            Assert.assertEquals(0, mm1.getVolumes().size());
            /** reattach one of the volumes */
            if (reattached != null) {
                System.out.println("reattach volume " + reattached.getId());
                MachineVolume mv = new MachineVolume();
                mv.setVolume(reattached);
                mv.setInitialLocation("/idontcare");
                Job reattachJob = this.machineManager.addVolumeToMachine(machineId, mv);
                if (reattachJob.getState() != Job.Status.FAILED) {
                    this.waitForJobCompletion(deleteJob);

                    Machine mm = this.machineManager.getMachineById(machineId);
                    List<MachineVolume> mvs = mm.getVolumes();

                    MachineVolume toremove = null;
                    for (MachineVolume temp : mvs) {
                        if (temp.getVolume().getId().equals(reattached.getId())) {
                            toremove = temp;
                        }
                    }
                    Assert.assertNotNull(toremove);
                    /** detach this volume now */
                    Job detachJob = this.machineManager.removeVolumeFromMachine(machineId, toremove.getId().toString());
                    if (detachJob.getState().equals(Job.Status.FAILED)) {
                        throw new Exception(" detach of reattached volume failed " + machineId);
                    }
                    this.waitForJobCompletion(detachJob);
                } else {
                    System.out.println(" Reattach of volume " + reattached.getId() + " to machine " + machineId + " failed ");
                }
            }
        }

        deleteJob = this.machineManager.deleteMachine(machineId);

        if (deleteJob.getState() == Job.Status.RUNNING) {
            System.out.println(" testScenarioThree : wait for deletion of machine " + machineId);
            this.waitForJobCompletion(deleteJob);
        } else {
            System.out.println("Machine deletion completed " + machineId);
        }

    }

    @Test
    public void testScenarioFour() throws Exception {
        this.initDatabase();
        /**
         * Retrieve the CEP
         */
        System.out.println("testScenarioFour with volume creation during machine create ");
        CloudEntryPoint cep = this.machineManager.getCloudEntryPoint();
        System.out.println(" test machinetemplate create");
        this.testMachineTemplateCreate();
        System.out.println(" test machine create with volumes ");
        String machineId = this.createMachineWithNewVolumes();

        /**
         * Query the Machine
         */

        Machine machine = this.machineManager.getMachineById(machineId);
        System.out.println("Machine [id=" + machine.getId() + ", name=" + machine.getName() + ", description="
            + machine.getDescription() + ", " + machine.getCreated() + ", state=" + machine.getState() + ", "
            + machine.getCpu() + ", memory=" + machine.getMemory() + ", disks=" + machine.getDisks() + ", networkInterfaces="
            + machine.getNetworkInterfaces() + "]");

        /**
         * Start the Machine
         */

        Job job = this.machineManager.startMachine(machineId, null);
        this.waitForJobCompletion(job);

        /**
         * Query the Machine to verify if it started
         */

        machine = this.machineManager.getMachineById(machineId);
        System.out.println("Machine [id=" + machine.getId() + ", name=" + machine.getName() + ", description="
            + machine.getDescription() + ", " + machine.getCreated() + ", state=" + machine.getState() + ", "
            + machine.getCpu() + ", memory=" + machine.getMemory() + ", disks=" + machine.getDisks() + ", networkInterfaces="
            + machine.getNetworkInterfaces() + "]");

        System.out.println("Machine id=" + machine.getId() + " volumes " + machine.getVolumes());

        /**
         * Stop the Machine
         */

        job = this.machineManager.stopMachine(machineId, false, null);
        this.waitForJobCompletion(job);

        /**
         * Update the Machine's name and description
         */
        Map<String, Object> attributeToUpdate = new HashMap<String, Object>();
        attributeToUpdate.put("name", "Cool Demo #1");
        attributeToUpdate.put("description", null);
        job = this.machineManager.updateMachineAttributes(machineId, attributeToUpdate);
        this.waitForJobCompletion(job);

        machine = this.machineManager.getMachineById(machineId);
        System.out.println("Machine [id=" + machine.getId() + ", name=" + machine.getName() + ", description="
            + machine.getDescription() + ", " + machine.getCreated() + ", state=" + machine.getState() + ", "
            + machine.getCpu() + ", memory=" + machine.getMemory() + ", disks=" + machine.getDisks() + ", networkInterfaces="
            + machine.getNetworkInterfaces() + "]");

    }

    @Test
    public void testScenarioFive() throws Exception {
        this.initDatabase();
        /**
         * Retrieve the CEP
         */
        System.out.println("testScenarioFive with volume creation during machine create ");
        CloudEntryPoint cep = this.machineManager.getCloudEntryPoint();
        System.out.println(" test machinetemplate create");
        this.testMachineTemplateCreate();
        System.out.println(" test machine create with volumes ");
        String machineId = this.createMachineWithNewVolumesAndNetworks(1);

        /**
         * Query the Machine
         */

        Machine machine = this.machineManager.getMachineById(machineId);
        System.out.println("Machine [id=" + machine.getId() + ", name=" + machine.getName() + ", description="
            + machine.getDescription() + ", " + machine.getCreated() + ", state=" + machine.getState() + ", "
            + machine.getCpu() + ", memory=" + machine.getMemory() + ", disks=" + machine.getDisks() + ", networkInterfaces="
            + machine.getNetworkInterfaces() + ", volumes=" + machine.getVolumes() + "]");

        /**
         * Start the Machine
         */

        Job job = this.machineManager.startMachine(machineId, null);
        this.waitForJobCompletion(job);

        /**
         * Query the Machine to verify if it started
         */

        machine = this.machineManager.getMachineById(machineId);
        System.out.println("Machine [id=" + machine.getId() + ", name=" + machine.getName() + ", description="
            + machine.getDescription() + ", " + machine.getCreated() + ", state=" + machine.getState() + ", "
            + machine.getCpu() + ", memory=" + machine.getMemory() + ", disks=" + machine.getDisks() + ", networkInterfaces="
            + machine.getNetworkInterfaces() + ", volumes=" + machine.getVolumes() + "]");

        System.out.println("Machine id=" + machine.getId() + " volumes " + machine.getVolumes());

        /**
         * Stop the Machine
         */

        job = this.machineManager.stopMachine(machineId, false, null);
        this.waitForJobCompletion(job);

        /**
         * Update the Machine's name and description
         */
        Map<String, Object> attributeToUpdate = new HashMap<String, Object>();
        attributeToUpdate.put("name", "Cool Demo #1");
        attributeToUpdate.put("description", null);
        job = this.machineManager.updateMachineAttributes(machineId, attributeToUpdate);
        this.waitForJobCompletion(job);

        machine = this.machineManager.getMachineById(machineId);
        System.out.println("Machine [id=" + machine.getId() + ", name=" + machine.getName() + ", description="
            + machine.getDescription() + ", " + machine.getCreated() + ", state=" + machine.getState() + ", "
            + machine.getCpu() + ", memory=" + machine.getMemory() + ", disks=" + machine.getDisks() + ", networkInterfaces="
            + machine.getNetworkInterfaces() + "]");

        List<MachineVolume> volumes = machine.getVolumes();

        for (MachineVolume mv : volumes) {

            Job deleteJob = this.machineManager.removeVolumeFromMachine(machineId, mv.getId().toString());
            System.out.println(" testScenarioFive status detach of volume " + mv.getId() + " job status = "
                + deleteJob.getState());
            this.waitForJobCompletion(deleteJob);

        }
        machine = this.machineManager.getMachineById(machineId);
        volumes = machine.getVolumes();
        Assert.assertEquals(0, volumes.size());

        Job deleteJob = this.machineManager.deleteMachine(machineId);

        if (deleteJob.getState() == Job.Status.RUNNING) {
            System.out.println(" testScenarioFive : wait for deletion of machine " + machineId);
            this.waitForJobCompletion(deleteJob);
        } else {
            System.out.println("Machine deletion completed " + machineId);
        }
    }

}
