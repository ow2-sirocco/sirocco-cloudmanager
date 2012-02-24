package org.ow2.sirocco.cloudmanager.itests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit;
import org.ow2.sirocco.cloudmanager.model.cimi.User;

public class MachineManagerTest {
    private static final String USER_NAME = "ANONYMOUS";

    private static final String CLOUD_PROVIDER_TYPE = "mock";

    private static final String ACCOUNT_LOGIN = "ignored";

    private static final String ACCOUNT_USER = "machinetest";

    private static final String ACCOUNT_CREDENTIALS = "ignored";

    private static final int MACHINE_ASYNC_OPERATION_WAIT_TIME_IN_SECONDS = 30;

    /**
     * Initial Context Factory.
     */
    private static final String INITIAL_CONTEXT_FACTORY = "org.ow2.carol.jndi.spi.MultiOrbInitialContextFactory";

    /**
     * Timeout (in seconds) for Sirocco to initialize.
     */
    private static final int INITIALIZE_TIMEOUT = 30;

    private static IRemoteMachineManager machineManager;
    private static IRemoteCredentialsManager credManager;
    private static IRemoteMachineImageManager machineImageManager;

    private static IRemoteCloudProviderManager cloudProviderManager;

    private static IRemoteUserManager userManager;

    private IRemoteJobManager jobManager;

    List<MachineImage> images = new ArrayList<MachineImage>();
    List<MachineConfiguration> configs = new ArrayList<MachineConfiguration>();
    List<MachineTemplate> templates = new ArrayList<MachineTemplate>();
    List<Machine> machines = new ArrayList<Machine>();
    List<Credentials> creds = new ArrayList<Credentials>();
    
    private static void connectToCloudManager() throws Exception {
        String carolPortString = System.getProperty("carol.port");
        Assert.assertNotNull("carol.port not set!", carolPortString);
        int carolPort = Integer.parseInt(carolPortString);

        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, MachineManagerTest.INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, "rmi://localhost:" + carolPort);
        final long timeout = System.currentTimeMillis() + MachineManagerTest.INITIALIZE_TIMEOUT * 1000;
        while (true) {
            try {
                Context context = new InitialContext(env);
                MachineManagerTest.machineManager = (IRemoteMachineManager) context.lookup(IMachineManager.EJB_JNDI_NAME);
                MachineManagerTest.cloudProviderManager = (IRemoteCloudProviderManager) context
                    .lookup(ICloudProviderManager.EJB_JNDI_NAME);
                MachineManagerTest.userManager = (IRemoteUserManager) context.lookup(IUserManager.EJB_JNDI_NAME);
                MachineManagerTest.machineImageManager = (IRemoteMachineImageManager) context
                    .lookup(IMachineImageManager.EJB_JNDI_NAME);
                // this.jobManager = (IRemoteJobManager)
                // context.lookup(IJobManager.EJB_JNDI_NAME);
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

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        MachineManagerTest.connectToCloudManager();
        User user = MachineManagerTest.userManager.createUser("Lov", "Maps", "lov@maps", MachineManagerTest.USER_NAME,
            "password", "foobar");
        CloudProvider provider = MachineManagerTest.cloudProviderManager.createCloudProvider(
            MachineManagerTest.CLOUD_PROVIDER_TYPE, "mock");
        CloudProviderAccount account = MachineManagerTest.cloudProviderManager.createCloudProviderAccount(provider.getId()
            .toString(), MachineManagerTest.ACCOUNT_USER, MachineManagerTest.ACCOUNT_LOGIN,
            MachineManagerTest.ACCOUNT_CREDENTIALS);
        MachineManagerTest.cloudProviderManager.addCloudProviderAccountToUser(user.getId().toString(), account.getId()
            .toString());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private int credcounter = 1;

    private Credentials initCredentials() {
        Credentials in = new Credentials();
        in.setName("testCred_" + this.credcounter);
        in.setDescription("testCred_" + this.credcounter + " description ");
        in.setProperties(null);

        in.setUserName("madras");
        in.setPassword("bombaydelhi");
        String key = new String("parisnewyork" + this.credcounter);
        in.setKey(key.getBytes());
        this.credcounter += 1;
        return in;
    }
    private Credentials createCredentials() throws Exception {
    	Credentials in_c = initCredentials();
    	Credentials out_c = this.credManager.createCredentials(in_c);
    	Assert.assertNotNull("createCredentials returns no credentials", out_c);
    	creds.add(out_c);
    	return out_c;
    }
    private int ccounter = 1;
    
    private MachineConfiguration initMachineConfiguration() {
        MachineConfiguration in_c = new MachineConfiguration();
        in_c.setName("testConfig_" + this.ccounter);
        this.ccounter += 1;
        in_c.setDescription("testConfig_" + this.ccounter + " description");
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("entity", "machineconfiguration");
        in_c.setProperties(properties);

        Cpu cpu = new Cpu();
        cpu.setCpuSpeedUnit(Cpu.Frequency.GIGA);
        cpu.setQuantity((float) 3.5);
        cpu.setNumberCpu(1);
        Memory mem = new Memory();
        mem.setUnit(Memory.MemoryUnit.MEGIBYTE);
        mem.setQuantity((float) 1.5);

        List<DiskTemplate> dTemplates = new ArrayList<DiskTemplate>();

        for (int i = 0; i < 2; i++) {
            DiskTemplate dt = new DiskTemplate();
            dt.setUnit(StorageUnit.MEGABYTE);
            dt.setQuantity((float) 4.5);
            dt.setFormat("ext3");
            dt.setAttachmentPoint("/dev/sd"+i);
            dTemplates.add(dt);
        }
        in_c.setCpu(cpu);
        in_c.setMemory(mem);
        in_c.setDiskTemplates(dTemplates);
        return in_c;
    }

    private int imagecounter = 1;

    private MachineImage initMachineImage() {
        MachineImage mimage = new MachineImage();
        mimage.setName("image_" + this.imagecounter);
        mimage.setDescription("image description " + this.imagecounter);
        mimage.setImageLocation("http://example.com/images/WinXP-SP2"+this.imagecounter);
        this.imagecounter += 1;
        return mimage;
    }
    
    private MachineImage createMachineImage() throws Exception {
    	MachineImage in_i = this.initMachineImage();
        Job out_j = MachineManagerTest.machineImageManager.createMachineImage(in_i);
        Assert.assertNotNull("machineImageCreate returns no machineimage", out_j);
        boolean done = false;
        MachineImage i = null;
        while (done != true) {
        	int counter = MachineManagerTest.MACHINE_ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        	i = MachineManagerTest.machineImageManager.getMachineImage(out_j.getTargetEntity());
        	Thread.sleep(1000);
        	
        	if (counter-- == 100) {
        		throw new Exception("Machine image create time out");
        	}
        }
        return i;
    }
    
    @Test
    public void testCreateMachineImage() throws Exception {
        createMachineImage();
    }

    
   
    private MachineConfiguration createMachineConfiguration() throws Exception {
        MachineConfiguration in_c = initMachineConfiguration();
        
        MachineConfiguration out_c = MachineManagerTest.machineManager.createMachineConfiguration(in_c);
        Assert.assertNotNull("machineConfigurationCreate returns no machineconfiguration", out_c);
        configs.add(in_c);
        return out_c;
    }

    @Test
    public void testCreateMachineConfiguration() throws Exception {
    	createMachineConfiguration();
    }
    
    private int mcounter = 0;
    @Test
    public void testCreateMachine() throws Exception {
        MachineCreate machineCreate = new MachineCreate();
        machineCreate.setName("myMachine_"+mcounter);
        machineCreate.setDescription("my machine" +mcounter);
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("department", "MAPS");
        machineCreate.setProperties(properties);
        MachineTemplate machineTemplate = new MachineTemplate();
        MachineConfiguration machineConfig = this.createMachineConfiguration();
        machineTemplate.setMachineConfiguration(machineConfig);
        machineTemplate.setMachineImage(this.createMachineImage());
        
        machineTemplate.setCredentials(this.createCredentials());
        machineTemplate.setVolumes(new ArrayList<MachineVolume>());
        machineTemplate.setVolumeTemplates(new ArrayList<MachineVolumeTemplate>());
        machineTemplate.setNetworkInterfaces(new ArrayList<NetworkInterface>());
        machineCreate.setMachineTemplate(machineTemplate);

        Job job = MachineManagerTest.machineManager.createMachine(machineCreate);
        Assert.assertNotNull("machineCreate returns no job", job);

        Assert.assertNotNull(job.getId());
        Assert.assertTrue("job action is invalid", job.getAction().equals("volume.create"));
        String machineId = job.getTargetEntity();
        Assert.assertNotNull("job target entity is invalid", machineId);

        String jobId = job.getId().toString();

        int counter = MachineManagerTest.MACHINE_ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        while (true) {
            // job = this.jobManager.getJobById(jobId);
            if (job.getStatus() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("Machine creation time out");
            }
        }

        Assert.assertTrue("machine creation failed: " + job.getStatusMessage(), job.getStatus() == Job.Status.SUCCESS);

        Machine machine = MachineManagerTest.machineManager.getMachineById(machineId);
        Assert.assertNotNull("cannot find machine juste created", machine);
        Assert.assertEquals("Created machine is STARTED", machine.getState(), Machine.State.STARTED);
        
        Assert.assertNotNull(machine.getId());
        Assert.assertEquals(machine.getName(), "myMachine");
        Assert.assertEquals(machine.getDescription(), "my machine");

        Assert.assertEquals(machine.getMemory().getUnit(), Memory.MemoryUnit.MEGIBYTE);

        this.deleteMachine(machine.getId().toString());
    }

    void deleteMachine(final String machineId) throws Exception {
        Job job = MachineManagerTest.machineManager.deleteMachine(machineId);
        Assert.assertNotNull("deleteMachine returns no job", job);

        Assert.assertTrue("job action is invalid", job.getAction().equals("machine.delete"));
        Assert.assertEquals("job target entity is invalid", machineId, job.getTargetEntity());

        String jobId = job.getId().toString();

        int counter = MachineManagerTest.MACHINE_ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        while (true) {
            // job = this.jobManager.getJobById(jobId);
            if (job.getStatus() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("Machine operation time out");
            }
        }

        Assert.assertTrue("machine deletion failed: " + job.getStatusMessage(), job.getStatus() == Job.Status.SUCCESS);

    }
}
