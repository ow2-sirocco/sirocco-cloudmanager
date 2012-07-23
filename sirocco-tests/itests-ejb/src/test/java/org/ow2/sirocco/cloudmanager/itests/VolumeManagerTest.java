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
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.itests.util.CustomDBUnitDeleteAllOperation;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeVolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

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

    private int counterVolume = 0;

    private int counterVolumeConfig = 0;

    private int counterVolumeTemplate = 0;

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
                Object o = context.lookup(ICloudProviderManager.EJB_JNDI_NAME);

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

        User user = this.userManager.createUser("Lov", "Maps", "lov@maps.com", VolumeManagerTest.USER_NAME, "232908Ivry");
        CloudProvider provider = this.cloudProviderManager.createCloudProvider(VolumeManagerTest.CLOUD_PROVIDER_TYPE, "mock");
        CloudProviderAccount account = this.cloudProviderManager.createCloudProviderAccount(provider.getId().toString(),
            VolumeManagerTest.ACCOUNT_LOGIN, VolumeManagerTest.ACCOUNT_CREDENTIALS);
        this.cloudProviderManager.addCloudProviderAccountToUser(user.getId().toString(), account.getId().toString());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private void waitForJobCompletion(Job job) throws Exception {
        int counter = VolumeManagerTest.VOLUME_ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        String jobId = job.getId().toString();
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

    String createVolume() throws Exception {
        VolumeCreate volumeCreate = new VolumeCreate();
        volumeCreate.setName("myVolume" + this.counterVolume++);
        volumeCreate.setDescription("my volume");
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("department", "MAPS");
        volumeCreate.setProperties(properties);
        VolumeTemplate volumeTemplate = new VolumeTemplate();
        VolumeConfiguration volumeConfig = new VolumeConfiguration();
        volumeConfig.setCapacity(500 * 1000);
        volumeConfig.setType("http://schemas.dmtf.org/cimi/1/mapped");
        volumeTemplate.setVolumeConfig(volumeConfig);
        volumeCreate.setVolumeTemplate(volumeTemplate);

        Job job = this.volumeManager.createVolume(volumeCreate);
        Assert.assertNotNull("createVolume returns no job", job);

        Assert.assertNotNull(job.getId());
        Assert.assertTrue("job action is invalid", job.getAction().equals("add"));
        String volumeId = job.getTargetEntity().getId().toString();
        Assert.assertNotNull("job target entity is invalid", volumeId);

        this.waitForJobCompletion(job);

        return volumeId;
    }

    @Test
    public void testCRUDVolume() throws Exception {
        String volumeId = this.createVolume();

        Volume volume = this.volumeManager.getVolumeById(volumeId);
        Assert.assertNotNull("cannot find volume just created", volume);
        Assert.assertEquals("Created volume is not AVAILABLE", volume.getState(), Volume.State.AVAILABLE);
        Assert.assertNotNull(volume.getProviderAssignedId());
        Assert.assertNotNull(volume.getId());
        Assert.assertEquals(volume.getName(), "myVolume0");
        Assert.assertEquals(volume.getDescription(), "my volume");
        Assert.assertNotNull(volume.getProperties());
        Assert.assertTrue(volume.getProperties().get("department").equals("MAPS"));
        Assert.assertNotNull(volume.getCapacity());
        Assert.assertEquals(volume.getCapacity().intValue(), 500 * 1000);

        // TODO update volume

        this.deleteVolume(volume.getId().toString());
    }

    void deleteVolume(final String volumeId) throws Exception {
        Job job = this.volumeManager.deleteVolume(volumeId);
        Assert.assertNotNull("deleteVolume returns no job", job);

        Assert.assertTrue("job action is invalid", job.getAction().equals("delete"));
        Assert.assertEquals("job target entity is invalid", volumeId, job.getTargetEntity().getId().toString());

        this.waitForJobCompletion(job);

        try {
            this.volumeManager.getVolumeById(volumeId);
            throw new Exception();
        } catch (ResourceNotFoundException e) {
        }
    }

    VolumeConfiguration createVolumeConfiguration() throws Exception {
        VolumeConfiguration inVolumeConfig = new VolumeConfiguration();
        inVolumeConfig.setCapacity(500 * 1000);
        inVolumeConfig.setType("http://schemas.dmtf.org/cimi/1/mapped");
        inVolumeConfig.setName("myVolumeConfig" + this.counterVolumeConfig++);
        inVolumeConfig.setDescription("a volume config");

        VolumeConfiguration outVolumeConfiguration = this.volumeManager.createVolumeConfiguration(inVolumeConfig);
        Assert.assertNotNull(outVolumeConfiguration);
        Assert.assertNotNull(outVolumeConfiguration.getId());
        Assert.assertEquals(inVolumeConfig.getCapacity(), outVolumeConfiguration.getCapacity());
        Assert.assertEquals(inVolumeConfig.getType(), outVolumeConfiguration.getType());
        Assert.assertEquals(inVolumeConfig.getName(), outVolumeConfiguration.getName());
        Assert.assertEquals(inVolumeConfig.getDescription(), outVolumeConfiguration.getDescription());

        return outVolumeConfiguration;
    }

    public VolumeTemplate createVolumeTemplate() throws Exception {
        VolumeTemplate inVolumeTemplate = new VolumeTemplate();
        inVolumeTemplate.setName("myVolumeTemplate" + this.counterVolumeTemplate++);
        inVolumeTemplate.setDescription("a volume template");

        VolumeConfiguration volumeConfig = this.createVolumeConfiguration();
        inVolumeTemplate.setVolumeConfig(volumeConfig);

        VolumeTemplate outVolumeTemplate = this.volumeManager.createVolumeTemplate(inVolumeTemplate);
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

        VolumeTemplate readVolumeTemplate = this.volumeManager.getVolumeTemplateById(createdVolumeTemplate.getId().toString());
        Assert.assertNotNull(readVolumeTemplate);
        Assert.assertEquals(createdVolumeTemplate.getId(), readVolumeTemplate.getId());
        Assert.assertEquals(createdVolumeTemplate.getVolumeConfig().getId(), readVolumeTemplate.getVolumeConfig().getId());

        VolumeConfiguration newVolumeConfiguration = this.createVolumeConfiguration();
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("volumeConfig", newVolumeConfiguration);
        this.volumeManager.updateVolumeTemplateAttributes(readVolumeTemplate.getId().toString(), attributes);
        readVolumeTemplate = this.volumeManager.getVolumeTemplateById(readVolumeTemplate.getId().toString());
        Assert.assertEquals(newVolumeConfiguration.getId(), readVolumeTemplate.getVolumeConfig().getId());

        this.volumeManager.deleteVolumeTemplate(readVolumeTemplate.getId().toString());
        this.volumeManager.deleteVolumeConfiguration(newVolumeConfiguration.getId().toString());
        this.volumeManager.deleteVolumeConfiguration(createdVolumeTemplate.getVolumeConfig().getId().toString());

        try {
            this.volumeManager.getVolumeTemplateById(readVolumeTemplate.getId().toString());
            this.volumeManager.getVolumeConfigurationById(newVolumeConfiguration.getId().toString());
            this.volumeManager.getVolumeConfigurationById(createdVolumeTemplate.getVolumeConfig().getId().toString());
        } catch (ResourceNotFoundException e) {
        }
    }

    String createVolumeImage(final Volume volumeToSnapshot) throws Exception {
        VolumeImage volumeImage = new VolumeImage();
        volumeImage.setName("myVolumeImage" + this.counterVolume++);
        volumeImage.setDescription("my volumeImage");
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("department", "MAPS");
        volumeImage.setProperties(properties);
        volumeImage.setBootable(false);
        volumeImage.setImageLocation("http://dummy");

        Job job;
        if (volumeToSnapshot == null) {
            job = this.volumeManager.createVolumeImage(volumeImage);
        } else {
            job = this.volumeManager.createVolumeSnapshot(volumeToSnapshot, volumeImage);
        }

        Assert.assertNotNull("createVolumeImage returns no job", job);

        Assert.assertNotNull(job.getId());
        Assert.assertTrue("job action is invalid", job.getAction().equals("add"));
        String volumeImageId = job.getTargetEntity().getId().toString();
        Assert.assertNotNull("job target entity is invalid", volumeImageId);

        this.waitForJobCompletion(job);

        return volumeImageId;
    }

    void deleteVolumeImage(final String volumeImageId) throws Exception {
        Job job = this.volumeManager.deleteVolumeImage(volumeImageId);
        Assert.assertNotNull("deleteVolumeImage returns no job", job);

        Assert.assertTrue("job action is invalid", job.getAction().equals("delete"));
        Assert.assertEquals("job target entity is invalid", volumeImageId, job.getTargetEntity().getId().toString());

        this.waitForJobCompletion(job);

        try {
            this.volumeManager.getVolumeImageById(volumeImageId);
            throw new Exception();
        } catch (ResourceNotFoundException e) {
        }
    }

    @Test
    public void testCRUDVolumeImage() throws Exception {
        String volumeId = this.createVolume();
        Volume volume = this.volumeManager.getVolumeById(volumeId);

        // CREATE snapshot

        String volumeImageId = this.createVolumeImage(volume);

        VolumeImage volumeImage = this.volumeManager.getVolumeImageById(volumeImageId);
        Assert.assertNotNull("Cannot find volume image just created", volumeImage);
        Assert.assertEquals("Created volume  image is not AVAILABLE", volumeImage.getState(), VolumeImage.State.AVAILABLE);
        Assert.assertNotNull(volumeImage.getId());
        Assert.assertFalse(volumeImage.getBootable());
        Assert.assertEquals(volumeImage.getImageLocation(), "http://dummy");
        Assert.assertEquals(volumeImage.getName(), "myVolumeImage1");
        Assert.assertEquals(volumeImage.getDescription(), "my volumeImage");
        Assert.assertNotNull(volumeImage.getProperties());
        Assert.assertTrue(volumeImage.getProperties().get("department").equals("MAPS"));

        List<VolumeImage> volumeImages = this.volumeManager.getVolumeImages();
        Assert.assertTrue(volumeImages.contains(volumeImage));

        // read

        volume = this.volumeManager.getVolumeById(volumeId);

        List<VolumeVolumeImage> volumeVolumeImages = volume.getImages();
        Assert.assertEquals(volumeVolumeImages.size(), 1);
        VolumeVolumeImage volumeVolumeImage = volumeVolumeImages.get(0);
        String volumeVolumeImageId = volumeVolumeImage.getId().toString();
        Assert.assertEquals(volumeVolumeImage.getVolumeImage().getId().toString(), volumeImageId);

        volumeVolumeImage = this.volumeManager.getVolumeImageFromVolume(volumeId, volumeVolumeImageId);

        volumeVolumeImages = this.volumeManager.getVolumeVolumeImages(volumeId);
        Assert.assertEquals(1, volumeVolumeImages.size());
        Assert.assertEquals(volumeVolumeImageId, volumeVolumeImages.get(0).getId().toString());

        QueryResult<VolumeVolumeImage> query = this.volumeManager.getVolumeVolumeImages(volumeId, 0, 10, null, null);
        Assert.assertEquals(1, query.getCount());
        Assert.assertEquals(volumeVolumeImageId, query.getItems().get(0).getId().toString());

        // UPDATE VolumeImage

        Map<String, Object> updatedAttributes = new HashMap<String, Object>();
        updatedAttributes.put("name", "myNewVolumeImage");
        Job job = this.volumeManager.updateVolumeImageAttributes(volumeImageId, updatedAttributes);
        this.waitForJobCompletion(job);
        volumeImage = this.volumeManager.getVolumeImageById(volumeImageId);
        Assert.assertEquals(volumeImage.getName(), "myNewVolumeImage");

        // Remove from Volume

        job = this.volumeManager.removeVolumeImageFromVolume(volumeId, volumeVolumeImageId);
        this.waitForJobCompletion(job);
        volumeImage = this.volumeManager.getVolumeImageById(volumeImageId);
        volume = this.volumeManager.getVolumeById(volumeId);
        Assert.assertEquals(volume.getImages().size(), 0);

        // DELETE

        this.deleteVolumeImage(volumeImage.getId().toString());
        this.deleteVolume(volume.getId().toString());

        try {
            this.volumeManager.getVolumeById(volumeId);
            Assert.fail("entity not deleted");
        } catch (ResourceNotFoundException ex) {
        }

        try {
            this.volumeManager.getVolumeImageById(volumeImageId);
            Assert.fail("entity not deleted");
        } catch (ResourceNotFoundException ex) {
            // ok
        }

        try {
            this.volumeManager.getVolumeImageFromVolume(volumeId, volumeVolumeImageId);
            Assert.fail("entity not deleted");
        } catch (ResourceNotFoundException ex) {
            // ok
        }
    }

    @Test
    public void testVolumeCollectionQueries() throws Exception {
        for (int i = 0; i < 20; i++) {
            this.createVolume();
        }
        List<Volume> volumes = this.volumeManager.getVolumes();
        Assert.assertEquals(20, volumes.size());

        List<String> attributes = new ArrayList<String>();
        attributes.add("name");
        QueryResult<Volume> query = this.volumeManager.getVolumes(0, 9, null, attributes);
        Assert.assertEquals(query.getCount(), 20);
        Assert.assertEquals(10, query.getItems().size());
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals("myVolume" + i, query.getItems().get(i).getName());
        }
        query = this.volumeManager.getVolumes(10, 25, null, attributes);
        Assert.assertEquals(query.getCount(), 20);
        Assert.assertEquals(10, query.getItems().size());
        query = this.volumeManager.getVolumes(20, 100, null, attributes);
        Assert.assertEquals(query.getCount(), 20);
        Assert.assertEquals(0, query.getItems().size());

        query = this.volumeManager.getVolumes(-1, -1, null, attributes);
        Assert.assertEquals(query.getCount(), 20);
        Assert.assertEquals(20, query.getItems().size());
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals("myVolume" + i, query.getItems().get(i).getName());
            this.deleteVolume(volumes.get(i).getId().toString());
        }
        volumes = this.volumeManager.getVolumes();
        Assert.assertEquals(0, volumes.size());
    }

    @Test
    public void testVolumeConfigurationCollectionQueries() throws Exception {
        for (int i = 0; i < 20; i++) {
            this.createVolumeConfiguration();
        }
        List<VolumeConfiguration> volumeConfigs = this.volumeManager.getVolumeConfigurations();
        Assert.assertEquals(20, volumeConfigs.size());

        List<String> attributes = new ArrayList<String>();
        attributes.add("name");
        QueryResult<VolumeConfiguration> query = this.volumeManager.getVolumeConfigurations(0, 9, null, attributes);
        Assert.assertEquals(query.getCount(), 20);
        Assert.assertEquals(10, query.getItems().size());
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals("myVolumeConfig" + i, query.getItems().get(i).getName());
        }
        query = this.volumeManager.getVolumeConfigurations(10, 25, null, attributes);
        Assert.assertEquals(query.getCount(), 20);
        Assert.assertEquals(10, query.getItems().size());
        query = this.volumeManager.getVolumeConfigurations(20, 100, null, attributes);
        Assert.assertEquals(query.getCount(), 20);
        Assert.assertEquals(0, query.getItems().size());

        query = this.volumeManager.getVolumeConfigurations(-1, -1, null, attributes);
        Assert.assertEquals(query.getCount(), 20);
        Assert.assertEquals(20, query.getItems().size());
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals("myVolumeConfig" + i, query.getItems().get(i).getName());
            this.volumeManager.deleteVolumeConfiguration(query.getItems().get(i).getId().toString());
        }
        volumeConfigs = this.volumeManager.getVolumeConfigurations();
        Assert.assertEquals(0, volumeConfigs.size());
    }

    @Test
    public void testVolumeTemplateCollectionQueries() throws Exception {
        for (int i = 0; i < 20; i++) {
            this.createVolumeTemplate();
        }
        List<VolumeTemplate> volumeTemplates = this.volumeManager.getVolumeTemplates();
        Assert.assertEquals(20, volumeTemplates.size());

        List<String> attributes = new ArrayList<String>();
        attributes.add("name");
        QueryResult<VolumeTemplate> query = this.volumeManager.getVolumeTemplates(0, 9, null, attributes);
        Assert.assertEquals(query.getCount(), 20);
        Assert.assertEquals(10, query.getItems().size());
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals("myVolumeTemplate" + i, query.getItems().get(i).getName());
        }
        query = this.volumeManager.getVolumeTemplates(10, 25, null, attributes);
        Assert.assertEquals(query.getCount(), 20);
        Assert.assertEquals(10, query.getItems().size());
        query = this.volumeManager.getVolumeTemplates(20, 100, null, attributes);
        Assert.assertEquals(query.getCount(), 20);
        Assert.assertEquals(0, query.getItems().size());

        query = this.volumeManager.getVolumeTemplates(-1, -1, null, attributes);
        Assert.assertEquals(query.getCount(), 20);
        Assert.assertEquals(20, query.getItems().size());
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals("myVolumeTemplate" + i, query.getItems().get(i).getName());
            this.volumeManager.deleteVolumeTemplate(query.getItems().get(i).getId().toString());
            this.volumeManager.deleteVolumeConfiguration(query.getItems().get(i).getVolumeConfig().getId().toString());
        }
        volumeTemplates = this.volumeManager.getVolumeTemplates();
        Assert.assertEquals(0, volumeTemplates.size());
    }
}
