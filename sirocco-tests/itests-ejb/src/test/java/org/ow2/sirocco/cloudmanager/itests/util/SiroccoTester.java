package org.ow2.sirocco.cloudmanager.itests.util;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
import org.ow2.sirocco.cloudmanager.core.api.IRemoteSystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;

@SuppressWarnings("unused")
public class SiroccoTester {

    /**
     * Initial Context Factory.
     */
    protected static final String INITIAL_CONTEXT_FACTORY = "org.ow2.carol.jndi.spi.MultiOrbInitialContextFactory";

    /**
     * Timeout (in seconds) for Sirocco to initialize.
     */
    protected static final int INITIALIZE_TIMEOUT = 30;

    protected static final int ASYNC_OPERATION_WAIT_TIME_IN_SECONDS = 30;

    protected IRemoteMachineManager machineManager;

    protected IRemoteCredentialsManager credManager;

    protected IRemoteMachineImageManager machineImageManager;

    protected IRemoteCloudProviderManager cloudProviderManager;

    protected IRemoteSystemManager systemManager;

    protected IRemoteVolumeManager volumeManager;

    protected IRemoteUserManager userManager;

    protected IRemoteJobManager jobManager;

    protected void connectToCloudManager() throws Exception {
        String carolPortString = System.getProperty("carol.port");
        Assert.assertNotNull("carol.port not set!", carolPortString);
        int carolPort = Integer.parseInt(carolPortString);

        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, SiroccoTester.INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, "rmi://localhost:" + carolPort);
        final long timeout = System.currentTimeMillis() + SiroccoTester.INITIALIZE_TIMEOUT * 1000;
        while (true) {
            try {
                Context context = new InitialContext(env);
                this.machineManager = (IRemoteMachineManager) context.lookup(IMachineManager.EJB_JNDI_NAME);
                this.cloudProviderManager = (IRemoteCloudProviderManager) context.lookup(ICloudProviderManager.EJB_JNDI_NAME);
                this.userManager = (IRemoteUserManager) context.lookup(IUserManager.EJB_JNDI_NAME);
                this.credManager = (IRemoteCredentialsManager) context.lookup(ICredentialsManager.EJB_JNDI_NAME);
                this.machineImageManager = (IRemoteMachineImageManager) context.lookup(IMachineImageManager.EJB_JNDI_NAME);
                this.systemManager = (IRemoteSystemManager) context.lookup(ISystemManager.EJB_JNDI_NAME);
                this.volumeManager = (IRemoteVolumeManager) context.lookup(IVolumeManager.EJB_JNDI_NAME);
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

    protected void setUpDatabase() throws Exception {
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

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    protected void waitForJobCompletion(Job job) throws Exception {
        int counter = SiroccoTester.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
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

    public void initDatabase() throws Exception {
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

        volumeId = job.getTargetEntity().getId().toString();
        System.out.println(" createVolume: wait for volume creation completion ");
        this.waitForJobCompletion(job);

        /**
         * Retrieve the Volume information
         */

        Volume volume = this.volumeManager.getVolumeById(volumeId);
        System.out.println(volume);
        return volume;
    }

}
