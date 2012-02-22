package org.ow2.sirocco.cloudmanager.itests;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Capacity;
import org.ow2.sirocco.cloudmanager.model.cimi.Capacity.Unit;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;

public class VolumeManagerTest {
    private static final String USER_NAME = "ANONYMOUS";

    private static final String CLOUD_PROVIDER_TYPE = "mock";

    private static final String ACCOUNT_LOGIN = "ignored";

    private static final String ACCOUNT_CREDENTIALS = "ignored";

    private static final int VOLUME_ASYNC_OPERATION_WAIT_TIME_IN_SECONDS = 30;

    /**
     * Initial Context Factory.
     */
    private static final String INITIAL_CONTEXT_FACTORY = "org.ow2.carol.jndi.spi.MultiOrbInitialContextFactory";

    /**
     * Timeout (in seconds) for Sirocco to initialize.
     */
    private static final int INITIALIZE_TIMEOUT = 30;

    private IRemoteVolumeManager volumeManager;

    private IRemoteCloudProviderManager cloudProviderManager;

    private IRemoteUserManager userManager;

    private IRemoteJobManager jobManager;

    private void connectToCloudManager() throws Exception {
        String carolPortString = System.getProperty("carol.port");
        Assert.assertNotNull("carol.port not set!", carolPortString);
        int carolPort = Integer.parseInt(carolPortString);

        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, VolumeManagerTest.INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, "rmi://localhost:" + carolPort);
        final long timeout = System.currentTimeMillis() + VolumeManagerTest.INITIALIZE_TIMEOUT * 1000;
        while (true) {
            try {
                Context context = new InitialContext(env);
                this.volumeManager = (IRemoteVolumeManager) context.lookup(IVolumeManager.EJB_JNDI_NAME);
                this.cloudProviderManager = (IRemoteCloudProviderManager) context.lookup(ICloudProviderManager.EJB_JNDI_NAME);
                this.userManager = (IRemoteUserManager) context.lookup(IUserManager.EJB_JNDI_NAME);
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

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.connectToCloudManager();
        this.userManager.createUser("", "", "", VolumeManagerTest.USER_NAME, "password", "");
        CloudProvider provider = this.cloudProviderManager.createCloudProvider(VolumeManagerTest.CLOUD_PROVIDER_TYPE, "test");
        CloudProviderAccount account = this.cloudProviderManager.createCloudProviderAccount(provider.getId().toString(),
            VolumeManagerTest.USER_NAME, VolumeManagerTest.ACCOUNT_LOGIN, VolumeManagerTest.ACCOUNT_CREDENTIALS);
        this.cloudProviderManager.addCloudProviderAccountToUser(VolumeManagerTest.USER_NAME, account.getId().toString());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateVolume() throws Exception {
        VolumeCreate volumeCreate = new VolumeCreate();
        volumeCreate.setName("myVolume");
        volumeCreate.setDescription("my volume");
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("department", "MAPS");
        volumeCreate.setProperties(properties);
        VolumeTemplate volumeTemplate = new VolumeTemplate();
        VolumeConfiguration volumeConfig = new VolumeConfiguration();
        Disk capacity = new Disk();
        capacity.setQuantity((float)512);
        capacity.setUnit(StorageUnit.MEGABYTE);
        volumeConfig.setCapacity(capacity);
        volumeConfig.setSupportsSnapshots(false);
        volumeTemplate.setVolumeConfig(volumeConfig);
        volumeCreate.setVolumeTemplate(volumeTemplate);

        Job job = this.volumeManager.createVolume(volumeCreate);
        Assert.assertNotNull("createVolume returns no job", job);

        Assert.assertNotNull(job.getId());
        Assert.assertTrue("job action is invalid", job.getAction().equals("volume.create"));
        String volumeId = job.getTargetEntity();
        Assert.assertNotNull("job target entity is invalid", volumeId);

        String jobId = job.getId().toString();

        int counter = VolumeManagerTest.VOLUME_ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        while (true) {
            job = this.jobManager.getJobById(jobId);
            if (job.getStatus() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("Volume creation time out");
            }
        }

        Assert.assertTrue("volume creation failed: " + job.getStatusMessage(), job.getStatus() == Job.Status.SUCCESS);

        Volume volume = this.volumeManager.getVolumeById(volumeId);
        Assert.assertNotNull("cannot find volume juste created", volume);
        Assert.assertEquals("Created volume is not AVAILABLE", volume.getState(), Volume.State.AVAILABLE);
        Assert.assertNotNull(volume.getProviderAssignedId());
        Assert.assertNotNull(volume.getId());
        Assert.assertEquals(volume.getName(), "myVolume");
        Assert.assertEquals(volume.getDescription(), "my volume");
        // Assert.assertNotNull(volume.getProperties());
        // Assert.assertTrue(volume.getProperties().get("departement").equals("MAPS"));
        Assert.assertNotNull(volume.getCapacity());
        Assert.assertEquals(volume.getCapacity().getQuantity().intValue(), 512);
        Assert.assertEquals(volume.getCapacity().getUnit(), StorageUnit.MEGABYTE);

        this.deleteVolume(volume.getId().toString());
    }

    void deleteVolume(final String volumeId) throws Exception {
        Job job = this.volumeManager.deleteVolume(volumeId);
        Assert.assertNotNull("deleteVolume returns no job", job);

        Assert.assertTrue("job action is invalid", job.getAction().equals("volume.delete"));
        Assert.assertEquals("job target entity is invalid", volumeId, job.getTargetEntity());

        String jobId = job.getId().toString();

        int counter = VolumeManagerTest.VOLUME_ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        while (true) {
            job = this.jobManager.getJobById(jobId);
            if (job.getStatus() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("Volume operation time out");
            }
        }

        Assert.assertTrue("volume deletion failed: " + job.getStatusMessage(), job.getStatus() == Job.Status.SUCCESS);
    }

    @Test
    public void testCRUDVolumeConfigurationAndTemplate() throws Exception {

    }
}
