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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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
import org.ow2.sirocco.cloudmanager.itests.util.CustomDBUnitDeleteAllOperation;
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
        System.out.println("CimiPrimerScenarioTest : setUp ");
        // this.setUpDatabase();
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
            if (job.getStatus() != Job.Status.RUNNING) {
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
        machineConfig.setDiskTemplates(Collections.singletonList(disk));
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

    private void initDatabase() throws Exception {
        MachineImage image = new MachineImage();
        image.setName("WinXP SP2");
        image.setDescription("Windows XP with Service Pack 2");
        image.setImageLocation("http://ow2.org/sirocco/data/1234");
        Job job = this.machineImageManager.createMachineImage(image);
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

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations(new ArrayList<String>(), "");
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
            + machineConfig.getCpu() + ", memory=" + machineConfig.getMemory() + ", disks=" + machineConfig.getDiskTemplates()
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

        machineTemplate.setMachineConfiguration(machineConfig);
        machineTemplate.setMachineImage(image);
        machineTemplate.setCredentials(credentials);

        List<MachineVolume> volColl = new ArrayList<MachineVolume>();

        machineTemplate.setVolumes(volColl);
        List<MachineVolumeTemplate> vtColl = new ArrayList<MachineVolumeTemplate>();

        machineTemplate.setVolumeTemplates(vtColl);

        // create network interfaces
        MachineTemplateNetworkInterface mtnic = null;
        for (int i = 0; i < 2; i++) {
            mtnic = new MachineTemplateNetworkInterface();
            mtnic.setState(MachineTemplateNetworkInterface.InterfaceState.STANDBY);
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

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations(new ArrayList<String>(), "");
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
            + machineConfig.getCpu() + ", memory=" + machineConfig.getMemory() + ", disks=" + machineConfig.getDiskTemplates()
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

        machineTemplate.setMachineConfiguration(machineConfig);
        machineTemplate.setMachineImage(image);
        machineTemplate.setCredentials(credentials);

        List<MachineVolume> volColl = new ArrayList<MachineVolume>();

        machineTemplate.setVolumes(volColl);
        List<MachineVolumeTemplate> vtColl = new ArrayList<MachineVolumeTemplate>();

        machineTemplate.setVolumeTemplates(vtColl);

        machineTemplate.setNetworkInterfaces(Collections.<MachineTemplateNetworkInterface> emptyList());
        machineCreate.setMachineTemplate(machineTemplate);

        Job job = this.machineManager.createMachine(machineCreate);
        String machineId = job.getTargetEntity().getId().toString();
        System.out.println("createMachine wait for job completion " + machineId);
        this.waitForJobCompletion(job);
        return machineId;
    }

    String createMachineWithPreExistingVolumes() throws Exception {
        /**
         * Retrieve the list of Machine Images
         */
        System.out.println("createMachineWithPreExistingVolumes ");

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

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations(new ArrayList<String>(), "");
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
            + machineConfig.getCpu() + ", memory=" + machineConfig.getMemory() + ", disks=" + machineConfig.getDiskTemplates()
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
         * Create a new volume
         */
        Volume v = this.createVolume("testVolumeAttach");
        System.out.println(" createMachineWithPreExistingVolumes created volume " + v.getId().toString());
        /**
         * Create a new Machine
         */

        MachineCreate machineCreate = new MachineCreate();
        machineCreate.setName("myMachine1");
        machineCreate.setDescription("My very first machine");

        MachineTemplate machineTemplate = new MachineTemplate();

        machineTemplate.setMachineConfiguration(machineConfig);
        machineTemplate.setMachineImage(image);
        machineTemplate.setCredentials(credentials);

        ArrayList<MachineVolume> vtItems = new ArrayList<MachineVolume>();
        MachineVolume mv = new MachineVolume();
        mv.setVolume(v);
        mv.setInitialLocation("/dev/sda");
        vtItems.add(mv);
        machineTemplate.setVolumes(vtItems);

        List<MachineVolumeTemplate> vtColl = new ArrayList<MachineVolumeTemplate>();
        machineTemplate.setVolumeTemplates(vtColl);

        machineTemplate.setNetworkInterfaces(Collections.<MachineTemplateNetworkInterface> emptyList());
        machineCreate.setMachineTemplate(machineTemplate);

        System.out.println(" createMachineWithPreExistingVolumes : create machine now");
        Job job = this.machineManager.createMachine(machineCreate);
        String machineId = job.getTargetEntity().getId().toString();
        System.out.println(" createMachineWithPreExistingVolumes : new machine id " + machineId + " with job " + job.getId());
        this.waitForJobCompletion(job);
        return machineId;
    }

    String createMachineWithNewVolumes() throws Exception {
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

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations(new ArrayList<String>(), "");
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
            + machineConfig.getCpu() + ", memory=" + machineConfig.getMemory() + ", disks=" + machineConfig.getDiskTemplates()
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

        machineTemplate.setMachineConfiguration(machineConfig);
        machineTemplate.setMachineImage(image);
        machineTemplate.setCredentials(credentials);

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
        String machineId = job.getTargetEntity().getId().toString();
        System.out.println("createMachineWithNewVolumes wait for job completion ");
        this.waitForJobCompletion(job);
        return machineId;
    }

    private VolumeTemplate createVolumeTemplate(final String name) throws Exception {
        /**
         * Retrieve the list of Volume Configurations
         */
        System.out.println("createVolumeTemplate " + name);
        List<VolumeConfiguration> volumeConfigs = this.volumeManager.getVolumeConfigurations(new ArrayList<String>(), "");
        for (VolumeConfiguration volumeConfig : volumeConfigs) {
            System.out.println("VolumeConfiguration id=" + volumeConfig.getId());
        }

        /**
         * Choose a VolumeConfiguration (first one)
         */

        VolumeConfiguration smallVolumeConfig = this.volumeManager.getVolumeConfigurationById(volumeConfigs.get(0).getId()
            .toString());
        System.out.println(smallVolumeConfig);

        VolumeTemplate volumeTemplate = new VolumeTemplate();
        volumeTemplate.setVolumeConfig(smallVolumeConfig);
        System.out.println("CreateVolumeTemplate return");

        return volumeTemplate;
    }

    private Volume createVolume(final String name) throws Exception {
        String volumeId;
        /**
         * Retrieve the list of Volume Configurations
         */

        List<VolumeConfiguration> volumeConfigs = this.volumeManager.getVolumeConfigurations(new ArrayList<String>(), "");
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

        volumeId = job.getTargetEntity().getId().toString();
        System.out.println(" testScenarioTwo: wait for volume creation completion ");
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

        Job job = this.machineManager.startMachine(machineId);
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

        job = this.machineManager.stopMachine(machineId);
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

        List<VolumeConfiguration> volumeConfigs = this.volumeManager.getVolumeConfigurations(new ArrayList<String>(), "");
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

        String volumeId = job.getTargetEntity().getId().toString();
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
            + job.getStatus());
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
        String machineId = this.createMachineWithPreExistingVolumes();

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

        Job job = this.machineManager.startMachine(machineId);
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

        job = this.machineManager.stopMachine(machineId);
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
        if (attached == null) {
            System.out.println("no volumes for machine " +machineId);
        } else {
            System.out.println(" machine " +machineId +" has " +attached.size() +" volumes ");
        }
        /** delete machine without detaching volumes */
        boolean caught = false;
        Job deleteJob = null;
        try {
            deleteJob = this.machineManager.deleteMachine(machineId);
        } catch (Exception e) {
            caught = true;
        }
        System.out.println("deletion of " +machineId +" returned with exception "+caught);
        if ((attached != null) && (attached.size() > 0)) {
            Assert.assertEquals(caught, true);
            
            System.out.println(" Machine deletion failed correct behaviour " +caught + " exception ");
            /** detach volumes first */
            for (MachineVolume volume : attached) {
                
                deleteJob = this.machineManager.removeVolumeFromMachine(machineId, volume.getId().toString());
                this.waitForJobCompletion(deleteJob);
                System.out.println("detach of volume " +volume.getVolume().getId() +" terminated " );
            }
            System.out.println(" delete machine now that volumes are detached " +machineId);

            deleteJob = this.machineManager.deleteMachine(machineId);

        } 
       
        if (deleteJob.getStatus() == Job.Status.RUNNING) {
            System.out.println(" testScenarioThree : wait for deletion of machine " +machineId);
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

        Job job = this.machineManager.startMachine(machineId);
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

        job = this.machineManager.stopMachine(machineId);
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
}
