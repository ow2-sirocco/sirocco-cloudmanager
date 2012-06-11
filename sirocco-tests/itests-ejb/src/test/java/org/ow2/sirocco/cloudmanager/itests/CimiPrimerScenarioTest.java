package org.ow2.sirocco.cloudmanager.itests;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
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
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplateCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterfaceMT;
import org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit;
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

    private static final int ASYNC_OPERATION_WAIT_TIME_IN_SECONDS = 30;

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
        //this.setUpDatabase();
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
        final int ramSizeInMB, final int diskSizeInGB) {
        MachineConfiguration machineConfig = new MachineConfiguration();
        machineConfig.setName(name);
        machineConfig.setDescription(description);
        Cpu cpu = new Cpu();
        cpu.setCpuSpeedUnit(Cpu.Frequency.GIGA);
        cpu.setQuantity((float) 3.5);
        cpu.setNumberCpu(numCpus);
        Memory mem = new Memory();
        mem.setUnit(Memory.MemoryUnit.MEGIBYTE);
        mem.setQuantity((float) ramSizeInMB);

        DiskTemplate disk = new DiskTemplate();
        disk.setUnit(StorageUnit.MEGABYTE);
        disk.setQuantity((float) diskSizeInGB);
        disk.setFormat("ext3");

        machineConfig.setCpu(cpu);
        machineConfig.setMemory(mem);
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
        Disk disk = new Disk();
        disk.setUnit(StorageUnit.MEGABYTE);
        disk.setQuantity((float) capacityInMBytes);
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
            "small: 1 CPU 3.5Ghz, 512MB RAM, 1GB disk", 1, 512, 1);
        this.machineManager.createMachineConfiguration(machineConfig);
        machineConfig = this.buildMachineConfiguration("medium", "medium: 2 CPU 3.5Ghz, 2GB RAM, 10GB disk", 2, 1024 * 2, 10);
        this.machineManager.createMachineConfiguration(machineConfig);
        machineConfig = this.buildMachineConfiguration("large", "large: 4 CPU 3.5Ghz, 8GB RAM, 50GB disk", 4, 8 * 1024, 50);
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

        List<MachineImage> machineImages = this.machineImageManager.getMachineImageCollection().getImages();
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

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurationCollection()
            .getMachineConfigurations();
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

        MachineVolumeCollection volColl = new MachineVolumeCollection();
        volColl.setItems(Collections.<MachineVolume> emptyList());
        machineTemplate.setVolumes(volColl);
        MachineVolumeTemplateCollection vtColl = new MachineVolumeTemplateCollection();
        vtColl.setItems(Collections.<MachineVolumeTemplate> emptyList());
        machineTemplate.setVolumeTemplates(vtColl);

        // create network interfaces
        NetworkInterfaceMT mtnic = null;
        for (int i = 0; i < 2; i++) {
            mtnic = new NetworkInterfaceMT();
            mtnic.setState(NetworkInterface.InterfaceState.STANDBY);
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
            List<NetworkInterface> items = mtt.getNetworkInterfaces();
            if (items.size() == 0) {
                System.out.println(" Strange no network interface?");
            }
            for (NetworkInterface intf : items) {
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

        List<MachineImage> machineImages = this.machineImageManager.getMachineImageCollection().getImages();
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

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurationCollection()
            .getMachineConfigurations();
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

        MachineVolumeCollection volColl = new MachineVolumeCollection();
        volColl.setItems(Collections.<MachineVolume> emptyList());
        machineTemplate.setVolumes(volColl);
        MachineVolumeTemplateCollection vtColl = new MachineVolumeTemplateCollection();
        vtColl.setItems(Collections.<MachineVolumeTemplate> emptyList());
        machineTemplate.setVolumeTemplates(vtColl);

        machineTemplate.setNetworkInterfaces(Collections.<NetworkInterface> emptyList());
        machineCreate.setMachineTemplate(machineTemplate);

        Job job = this.machineManager.createMachine(machineCreate);
        String machineId = job.getTargetEntity().getId().toString();
        System.out.println("createMachine wait for job completion " +machineId);
        this.waitForJobCompletion(job);
        return machineId;
    }

    
    String createMachineWithPreExistingVolumes() throws Exception {
        /**
         * Retrieve the list of Machine Images
         */
    	System.out.println("createMachineWithPreExistingVolumes ");

        List<MachineImage> machineImages = this.machineImageManager.getMachineImageCollection().getImages();
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

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurationCollection()
            .getMachineConfigurations();
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
        Volume v = createVolume("testVolumeAttach");
        System.out.println(" createMachineWithPreExistingVolumes created volume " +v.getId().toString());
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

        MachineVolumeCollection volColl = new MachineVolumeCollection();
        
        
        
        ArrayList<MachineVolume> vtItems = new ArrayList<MachineVolume>();
        MachineVolume mv = new MachineVolume();
        mv.setVolume(v);
        mv.setInitialLocation("/dev/sda");
        vtItems.add(mv);
        volColl.setItems(vtItems);
        machineTemplate.setVolumes(volColl);
        
        MachineVolumeTemplateCollection vtColl = new MachineVolumeTemplateCollection();
        vtColl.setItems(Collections.<MachineVolumeTemplate> emptyList());
        machineTemplate.setVolumeTemplates(vtColl);

        machineTemplate.setNetworkInterfaces(Collections.<NetworkInterface> emptyList());
        machineCreate.setMachineTemplate(machineTemplate);

        System.out.println(" createMachineWithPreExistingVolumes : create machine now");
        Job job = this.machineManager.createMachine(machineCreate);
        String machineId = job.getTargetEntity().getId().toString();
        System.out.println(" createMachineWithPreExistingVolumes : new machine id " +machineId +" with job " +job.getId());
        this.waitForJobCompletion(job);
        return machineId;
    }
    
    
    String createMachineWithNewVolumes() throws Exception {
        /**
         * Retrieve the list of Machine Images
         */

        List<MachineImage> machineImages = this.machineImageManager.getMachineImageCollection().getImages();
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

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurationCollection()
            .getMachineConfigurations();
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

        MachineVolumeCollection volColl = new MachineVolumeCollection();
        volColl.setItems(Collections.<MachineVolume> emptyList());
        machineTemplate.setVolumes(volColl);
        
        ArrayList<MachineVolumeTemplate> vtItems = new ArrayList<MachineVolumeTemplate>();
        MachineVolumeTemplate mvt = new MachineVolumeTemplate();
        VolumeTemplate vt = createVolumeTemplate("dummy");
        
        mvt.setVolumeTemplate(vt);
        mvt.setInitialLocation("/dev/sda");
        vtItems.add(mvt);
        
        MachineVolumeTemplateCollection vtColl = new MachineVolumeTemplateCollection();
        vtColl.setItems(vtItems);
        machineTemplate.setVolumeTemplates(vtColl);

        machineTemplate.setNetworkInterfaces(Collections.<NetworkInterface> emptyList());
        machineCreate.setMachineTemplate(machineTemplate);

        Job job = this.machineManager.createMachine(machineCreate);
        String machineId = job.getTargetEntity().getId().toString();

        this.waitForJobCompletion(job);
        return machineId;
    }
    
    private VolumeTemplate createVolumeTemplate(String name) throws Exception {
    	/**
         * Retrieve the list of Volume Configurations
         */
    	System.out.println("createVolumeTemplate " +name);
        List<VolumeConfiguration> volumeConfigs = this.volumeManager.getVolumeConfigurationCollection()
            .getVolumeConfigurations();
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
    
    private Volume createVolume(String name) throws Exception {
    	String volumeId;
    	/**
         * Retrieve the list of Volume Configurations
         */

        List<VolumeConfiguration> volumeConfigs = this.volumeManager.getVolumeConfigurationCollection()
            .getVolumeConfigurations();
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

        List<VolumeConfiguration> volumeConfigs = this.volumeManager.getVolumeConfigurationCollection()
            .getVolumeConfigurations();
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
        job = this.machineManager.addVolumeToMachine(machineId, volumeId, "/dev/sdb");
        System.out.println(" testScenarioTwo: add volume to machine wait for job completion " + job.getId() + " "
            + job.getStatus());
        this.waitForJobCompletion(job);

        /**
         * Query the Machine's volume collection to verify the update
         */
        System.out.println(" testScenarioTwo: get machine volumes ");
        List<MachineVolume> machineVolumes = this.machineManager.getMachineVolumes(machineId);
        System.out.println(" testScenarioTwo: found " + machineVolumes.size() + " machine volumes ");
        for (MachineVolume machineVolume : machineVolumes) {
            System.out.println(machineVolume);
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
