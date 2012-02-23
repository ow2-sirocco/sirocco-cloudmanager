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
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit;
import org.ow2.sirocco.cloudmanager.model.cimi.User;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplateCollection;

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

    private static IRemoteVolumeManager volumeManager;

    private static IRemoteCloudProviderManager cloudProviderManager;

    private static IRemoteUserManager userManager;

    private static IRemoteJobManager jobManager;

    private int counterVolume = 0;

    private int counterVolumeConfig = 0;

    private int counterVolumeTemplate = 0;

    private static void connectToCloudManager() throws Exception {
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
                VolumeManagerTest.volumeManager = (IRemoteVolumeManager) context.lookup(IVolumeManager.EJB_JNDI_NAME);
                Object o = context.lookup(ICloudProviderManager.EJB_JNDI_NAME);

                VolumeManagerTest.cloudProviderManager = (IRemoteCloudProviderManager) context
                    .lookup(ICloudProviderManager.EJB_JNDI_NAME);
                VolumeManagerTest.userManager = (IRemoteUserManager) context.lookup(IUserManager.EJB_JNDI_NAME);
                VolumeManagerTest.jobManager = (IRemoteJobManager) context.lookup(IJobManager.EJB_JNDI_NAME);
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
        VolumeManagerTest.connectToCloudManager();

        User user = VolumeManagerTest.userManager.createUser("", "", "", VolumeManagerTest.USER_NAME, "password", "");
        CloudProvider provider = VolumeManagerTest.cloudProviderManager.createCloudProvider(
            VolumeManagerTest.CLOUD_PROVIDER_TYPE, "test");
        CloudProviderAccount account = VolumeManagerTest.cloudProviderManager.createCloudProviderAccount(provider.getId()
            .toString(), VolumeManagerTest.USER_NAME, VolumeManagerTest.ACCOUNT_LOGIN, VolumeManagerTest.ACCOUNT_CREDENTIALS);
        VolumeManagerTest.cloudProviderManager.addCloudProviderAccountToUser(user.getId().toString(), account.getId()
            .toString());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    String createVolume() throws Exception {
        VolumeCreate volumeCreate = new VolumeCreate();
        volumeCreate.setName("myVolume" + this.counterVolume++);
        volumeCreate.setDescription("my volume");
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("department", "MAPS");
        volumeCreate.setProperties(properties);
        VolumeTemplate volumeTemplate = new VolumeTemplate();
        VolumeConfiguration volumeConfig = new VolumeConfiguration();
        Disk capacity = new Disk();
        capacity.setQuantity((float) 512);
        capacity.setUnit(StorageUnit.MEGABYTE);
        volumeConfig.setCapacity(capacity);
        volumeConfig.setSupportsSnapshots(false);
        volumeTemplate.setVolumeConfig(volumeConfig);
        volumeCreate.setVolumeTemplate(volumeTemplate);

        Job job = VolumeManagerTest.volumeManager.createVolume(volumeCreate);
        Assert.assertNotNull("createVolume returns no job", job);

        Assert.assertNotNull(job.getId());
        Assert.assertTrue("job action is invalid", job.getAction().equals("volume.create"));
        String volumeId = job.getTargetEntity();
        Assert.assertNotNull("job target entity is invalid", volumeId);

        String jobId = job.getId().toString();

        int counter = VolumeManagerTest.VOLUME_ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        while (true) {
            job = VolumeManagerTest.jobManager.getJobById(jobId);
            if (job.getStatus() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("Volume creation time out");
            }
        }

        Assert.assertTrue("volume creation failed: " + job.getStatusMessage(), job.getStatus() == Job.Status.SUCCESS);
        return volumeId;
    }

    @Test
    public void testCRUDVolume() throws Exception {
        String volumeId = this.createVolume();

        Volume volume = VolumeManagerTest.volumeManager.getVolumeById(volumeId);
        Assert.assertNotNull("cannot find volume juste created", volume);
        Assert.assertEquals("Created volume is not AVAILABLE", volume.getState(), Volume.State.AVAILABLE);
        Assert.assertNotNull(volume.getProviderAssignedId());
        Assert.assertNotNull(volume.getId());
        Assert.assertEquals(volume.getName(), "myVolume0");
        Assert.assertEquals(volume.getDescription(), "my volume");
        Assert.assertNotNull(volume.getProperties());
        Assert.assertTrue(volume.getProperties().get("department").equals("MAPS"));
        Assert.assertNotNull(volume.getCapacity());
        Assert.assertEquals(volume.getCapacity().getQuantity().intValue(), 512);
        Assert.assertEquals(volume.getCapacity().getUnit(), StorageUnit.MEGABYTE);

        // TODO update volume

        this.deleteVolume(volume.getId().toString());
    }

    void deleteVolume(final String volumeId) throws Exception {
        Job job = VolumeManagerTest.volumeManager.deleteVolume(volumeId);
        Assert.assertNotNull("deleteVolume returns no job", job);

        Assert.assertTrue("job action is invalid", job.getAction().equals("volume.delete"));
        Assert.assertEquals("job target entity is invalid", volumeId, job.getTargetEntity());

        String jobId = job.getId().toString();

        int counter = VolumeManagerTest.VOLUME_ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        while (true) {
            job = VolumeManagerTest.jobManager.getJobById(jobId);
            if (job.getStatus() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("Volume operation time out");
            }
        }

        Assert.assertTrue("volume deletion failed: " + job.getStatusMessage(), job.getStatus() == Job.Status.SUCCESS);
        try {
            VolumeManagerTest.volumeManager.getVolumeById(volumeId);
        } catch (ResourceNotFoundException e) {
        }
    }

    VolumeConfiguration createVolumeConfiguration() throws Exception {
        VolumeConfiguration inVolumeConfig = new VolumeConfiguration();
        Disk capacity = new Disk();
        capacity.setQuantity((float) 512);
        capacity.setUnit(StorageUnit.MEGABYTE);
        inVolumeConfig.setCapacity(capacity);
        inVolumeConfig.setSupportsSnapshots(false);
        inVolumeConfig.setName("myVolumeConfig" + this.counterVolumeConfig++);
        inVolumeConfig.setDescription("a volume config");

        VolumeConfiguration outVolumeConfiguration = VolumeManagerTest.volumeManager.createVolumeConfiguration(inVolumeConfig);
        Assert.assertNotNull(outVolumeConfiguration);
        Assert.assertNotNull(outVolumeConfiguration.getId());
        Assert.assertEquals(inVolumeConfig.getCapacity(), outVolumeConfiguration.getCapacity());
        Assert.assertFalse(outVolumeConfiguration.isSupportsSnapshots());
        Assert.assertEquals(inVolumeConfig.getName(), outVolumeConfiguration.getName());
        Assert.assertEquals(inVolumeConfig.getDescription(), outVolumeConfiguration.getDescription());

        return outVolumeConfiguration;
    }

    VolumeTemplate createVolumeTemplate() throws Exception {
        VolumeTemplate inVolumeTemplate = new VolumeTemplate();
        inVolumeTemplate.setName("myVolumeTemplate" + this.counterVolumeTemplate++);
        inVolumeTemplate.setDescription("a volume template");

        VolumeConfiguration volumeConfig = this.createVolumeConfiguration();
        inVolumeTemplate.setVolumeConfig(volumeConfig);

        VolumeTemplate outVolumeTemplate = VolumeManagerTest.volumeManager.createVolumeTemplate(inVolumeTemplate);
        Assert.assertNotNull(outVolumeTemplate);
        Assert.assertNotNull(outVolumeTemplate.getId());
        Assert.assertNotNull(outVolumeTemplate.getVolumeConfig());
        Assert.assertEquals(volumeConfig.getId(), outVolumeTemplate.getVolumeConfig().getId());

        Assert.assertEquals(inVolumeTemplate.getName(), outVolumeTemplate.getName());
        Assert.assertEquals(inVolumeTemplate.getDescription(), outVolumeTemplate.getDescription());
        return outVolumeTemplate;
    }

    @Test
    public void testCRUDVolumeTemplateAndConfiguration() throws Exception {
        VolumeTemplate createdVolumeTemplate = this.createVolumeTemplate();

        VolumeTemplate readVolumeTemplate = VolumeManagerTest.volumeManager.getVolumeTemplateById(createdVolumeTemplate.getId()
            .toString());
        Assert.assertNotNull(readVolumeTemplate);
        Assert.assertEquals(createdVolumeTemplate.getId(), readVolumeTemplate.getId());
        Assert.assertEquals(createdVolumeTemplate.getVolumeConfig().getId(), readVolumeTemplate.getVolumeConfig().getId());

        VolumeConfiguration newVolumeConfiguration = this.createVolumeConfiguration();
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("volumeConfig", newVolumeConfiguration);
        VolumeManagerTest.volumeManager.updateVolumeTemplateAttributes(readVolumeTemplate.getId().toString(), attributes);
        readVolumeTemplate = VolumeManagerTest.volumeManager.getVolumeTemplateById(readVolumeTemplate.getId().toString());
        Assert.assertEquals(newVolumeConfiguration.getId(), readVolumeTemplate.getVolumeConfig().getId());

        VolumeManagerTest.volumeManager.deleteVolumeTemplate(readVolumeTemplate.getId().toString());
        VolumeManagerTest.volumeManager.deleteVolumeConfiguration(newVolumeConfiguration.getId().toString());
        VolumeManagerTest.volumeManager.deleteVolumeConfiguration(createdVolumeTemplate.getVolumeConfig().getId().toString());

        try {
            VolumeManagerTest.volumeManager.getVolumeTemplateById(readVolumeTemplate.getId().toString());
            VolumeManagerTest.volumeManager.getVolumeConfigurationById(newVolumeConfiguration.getId().toString());
            VolumeManagerTest.volumeManager.getVolumeConfigurationById(createdVolumeTemplate.getVolumeConfig().getId()
                .toString());
        } catch (ResourceNotFoundException e) {
        }
    }

    @Test
    public void testVolumeCollectionQueries() throws Exception {
        for (int i = 0; i < 20; i++) {
            this.createVolume();
        }
        VolumeCollection volumeCollection = VolumeManagerTest.volumeManager.getVolumeCollection();
        Assert.assertEquals(20, volumeCollection.getVolumes().size());

        List<String> attributes = new ArrayList<String>();
        attributes.add("name");
        List<Volume> volumes = VolumeManagerTest.volumeManager.getVolumesAttributes(0, 9, attributes);
        Assert.assertEquals(10, volumes.size());
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals("myVolume" + i, volumes.get(i).getName());
        }
        volumes = VolumeManagerTest.volumeManager.getVolumesAttributes(10, 25, attributes);
        Assert.assertEquals(10, volumes.size());
        volumes = VolumeManagerTest.volumeManager.getVolumesAttributes(20, 100, attributes);
        Assert.assertEquals(0, volumes.size());

        volumes = VolumeManagerTest.volumeManager.getVolumesAttributes(attributes, null);
        Assert.assertEquals(20, volumes.size());
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals("myVolume" + i, volumes.get(i).getName());
            this.deleteVolume(volumes.get(i).getId().toString());
        }
        volumeCollection = VolumeManagerTest.volumeManager.getVolumeCollection();
        Assert.assertEquals(0, volumeCollection.getVolumes().size());
    }

    @Test
    public void testVolumeConfigurationCollectionQueries() throws Exception {
        for (int i = 0; i < 20; i++) {
            this.createVolumeConfiguration();
        }
        VolumeConfigurationCollection volumeConfigCollection = VolumeManagerTest.volumeManager
            .getVolumeConfigurationCollection();
        Assert.assertEquals(20, volumeConfigCollection.getVolumeConfigurations().size());

        List<String> attributes = new ArrayList<String>();
        attributes.add("name");
        List<VolumeConfiguration> volumeConfigs = VolumeManagerTest.volumeManager.getVolumeConfigurationsAttributes(0, 9,
            attributes);
        Assert.assertEquals(10, volumeConfigs.size());
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals("myVolumeConfig" + i, volumeConfigs.get(i).getName());
        }
        volumeConfigs = VolumeManagerTest.volumeManager.getVolumeConfigurationsAttributes(10, 25, attributes);
        Assert.assertEquals(10, volumeConfigs.size());
        volumeConfigs = VolumeManagerTest.volumeManager.getVolumeConfigurationsAttributes(20, 100, attributes);
        Assert.assertEquals(0, volumeConfigs.size());

        volumeConfigs = VolumeManagerTest.volumeManager.getVolumeConfigurationsAttributes(attributes, null);
        Assert.assertEquals(20, volumeConfigs.size());
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals("myVolumeConfig" + i, volumeConfigs.get(i).getName());
            VolumeManagerTest.volumeManager.deleteVolumeConfiguration(volumeConfigs.get(i).getId().toString());
        }
        volumeConfigCollection = VolumeManagerTest.volumeManager.getVolumeConfigurationCollection();
        Assert.assertEquals(0, volumeConfigCollection.getVolumeConfigurations().size());
    }

    @Test
    public void testVolumeTemplateCollectionQueries() throws Exception {
        for (int i = 0; i < 20; i++) {
            this.createVolumeTemplate();
        }
        VolumeTemplateCollection volumeTemplateCollection = VolumeManagerTest.volumeManager.getVolumeTemplateCollection();
        Assert.assertEquals(20, volumeTemplateCollection.getVolumeTemplates().size());

        List<String> attributes = new ArrayList<String>();
        attributes.add("name");
        List<VolumeTemplate> volumeTemplates = VolumeManagerTest.volumeManager.getVolumeTemplatesAttributes(0, 9, attributes);
        Assert.assertEquals(10, volumeTemplates.size());
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals("myVolumeTemplate" + i, volumeTemplates.get(i).getName());
        }
        volumeTemplates = VolumeManagerTest.volumeManager.getVolumeTemplatesAttributes(10, 25, attributes);
        Assert.assertEquals(10, volumeTemplates.size());
        volumeTemplates = VolumeManagerTest.volumeManager.getVolumeTemplatesAttributes(20, 100, attributes);
        Assert.assertEquals(0, volumeTemplates.size());

        volumeTemplates = VolumeManagerTest.volumeManager.getVolumeTemplatesAttributes(attributes, null);
        Assert.assertEquals(20, volumeTemplates.size());
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals("myVolumeTemplate" + i, volumeTemplates.get(i).getName());
            VolumeManagerTest.volumeManager.deleteVolumeTemplate(volumeTemplates.get(i).getId().toString());
            VolumeManagerTest.volumeManager.deleteVolumeConfiguration(volumeTemplates.get(i).getVolumeConfig().getId()
                .toString());
        }
        volumeTemplateCollection = VolumeManagerTest.volumeManager.getVolumeTemplateCollection();
        Assert.assertEquals(0, volumeTemplateCollection.getVolumeTemplates().size());
    }
}
