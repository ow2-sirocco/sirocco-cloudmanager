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
import org.junit.Test;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteNetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.itests.util.CustomDBUnitDeleteAllOperation;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

/**
 * This class requires the following system properties: -Dcarol.port=1099
 * -Ddbunit.connectionUrl=jdbc:mysql://localhost:3306/sirocco
 * -Ddbunit.driverClass=org.gjt.mm.mysql.Driver -Ddbunit.username=admcloud
 * -Ddbunit.password=admcloud -Ddbunit.schema=sirocco
 * -Ddbunit.dataset=src/sirocco-config/db/sirocco_db_empty.xml
 */
public class NetworkManagerTest {
    private static final String USER_NAME = "ANONYMOUS";

    private static final String CLOUD_PROVIDER_TYPE = "mock";

    private static final String ACCOUNT_LOGIN = "ignored";

    private static final String ACCOUNT_USER = "machinetest";

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

    private IRemoteNetworkManager networkManager;

    private IRemoteCloudProviderManager cloudProviderManager;

    private IRemoteUserManager userManager;

    private IRemoteJobManager jobManager;

    List<MachineImage> images = new ArrayList<MachineImage>();

    List<MachineConfiguration> configs = new ArrayList<MachineConfiguration>();

    List<MachineTemplate> templates = new ArrayList<MachineTemplate>();

    List<Machine> machines = new ArrayList<Machine>();

    List<Credentials> creds = new ArrayList<Credentials>();

    private void connectToCloudManager() throws Exception {
        String carolPortString = System.getProperty("carol.port");
        Assert.assertNotNull("carol.port not set!", carolPortString);
        int carolPort = Integer.parseInt(carolPortString);

        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, NetworkManagerTest.INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, "rmi://localhost:" + carolPort);
        final long timeout = System.currentTimeMillis() + NetworkManagerTest.INITIALIZE_TIMEOUT * 1000;
        while (true) {
            try {
                Context context = new InitialContext(env);
                this.networkManager = (IRemoteNetworkManager) context.lookup(INetworkManager.EJB_JNDI_NAME);
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
        // change password that is not validated by user manager
        User user = this.userManager.createUser("Lov", "Maps", "lov@maps.com", NetworkManagerTest.USER_NAME, "232908Ivry");
        CloudProvider provider = this.cloudProviderManager.createCloudProvider(NetworkManagerTest.CLOUD_PROVIDER_TYPE, "mock");
        CloudProviderAccount account = this.cloudProviderManager.createCloudProviderAccount(provider.getId().toString(),
            NetworkManagerTest.ACCOUNT_LOGIN, NetworkManagerTest.ACCOUNT_CREDENTIALS);
        this.cloudProviderManager.addCloudProviderAccountToUser(user.getId().toString(), account.getId().toString());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private void waitForJobCompletion(Job job) throws Exception {
        int counter = NetworkManagerTest.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
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

    NetworkConfiguration newNetworkConfiguration(final String name) {
        NetworkConfiguration netConfig = new NetworkConfiguration();
        netConfig.setName(name);
        netConfig.setDescription("dummy net config");
        netConfig.setProperties(Collections.singletonMap("color", "blue"));
        netConfig.setClassOfService("silver");
        netConfig.setNetworkType(Network.Type.PUBLIC);
        return netConfig;
    }

    @Test
    public void testCRUDNetworkConfiguration() throws Exception {
        // create
        NetworkConfiguration netConfig = this.networkManager.createNetworkConfiguration(this
            .newNetworkConfiguration("myConfig"));

        Assert.assertNotNull(netConfig.getId());
        Assert.assertEquals("myConfig", netConfig.getName());
        Assert.assertEquals("dummy net config", netConfig.getDescription());
        Assert.assertEquals("blue", netConfig.getProperties().get("color"));
        Assert.assertEquals("silver", netConfig.getClassOfService());
        Assert.assertEquals(Network.Type.PUBLIC, netConfig.getNetworkType());

        // attempt to create netconfig with same name

        try {
            this.networkManager.createNetworkConfiguration(this.newNetworkConfiguration("myConfig"));
            Assert.fail();
        } catch (CloudProviderException ex) {
        }

        // read
        String netConfigId = netConfig.getId().toString();
        netConfig = this.networkManager.getNetworkConfigurationById(netConfigId);
        Assert.assertEquals(netConfig.getId().toString(), netConfigId);
        Assert.assertEquals("myConfig", netConfig.getName());
        Assert.assertEquals("dummy net config", netConfig.getDescription());
        Assert.assertEquals("blue", netConfig.getProperties().get("color"));
        Assert.assertEquals("silver", netConfig.getClassOfService());
        Assert.assertEquals(Network.Type.PUBLIC, netConfig.getNetworkType());

        // update

        // delete

        this.networkManager.deleteNetworkConfiguration(netConfigId);

        try {
            this.networkManager.getNetworkConfigurationById(netConfigId);
            Assert.fail();
        } catch (ResourceNotFoundException ex) {
        }
    }

    NetworkTemplate newNetworkTemplate(final String name, final NetworkConfiguration netConfig,
        final ForwardingGroup forwardingGroup) {
        NetworkTemplate netTemplate = new NetworkTemplate();
        netTemplate.setName(name);
        netTemplate.setDescription("dummy net template");
        netTemplate.setProperties(Collections.singletonMap("color", "blue"));
        netTemplate.setNetworkConfig(netConfig);
        netTemplate.setForwardingGroup(forwardingGroup);
        return netTemplate;
    }

    @Test
    public void testCRUDNetworkTemplateConfiguration() throws Exception {
        // create

        NetworkConfiguration netConfig = this.networkManager.createNetworkConfiguration(this
            .newNetworkConfiguration("myConfig"));
        netConfig = this.networkManager.getNetworkConfigurationById(netConfig.getId().toString());

        NetworkTemplate netTemplate = this.networkManager.createNetworkTemplate(this.newNetworkTemplate("myTemplate",
            netConfig, null));
        String netTemplateId = netTemplate.getId().toString();
        Assert.assertEquals(netTemplate.getId().toString(), netTemplateId);
        Assert.assertEquals("myTemplate", netTemplate.getName());
        Assert.assertEquals("dummy net template", netTemplate.getDescription());
        Assert.assertEquals("blue", netTemplate.getProperties().get("color"));
        Assert.assertEquals(netTemplate.getNetworkConfig().getId(), netConfig.getId());

        // attempt to create net template with same name

        try {
            this.networkManager.createNetworkTemplate(this.newNetworkTemplate("myTemplate", netConfig, null));
            Assert.fail();
        } catch (CloudProviderException ex) {
        }

        // read
        netTemplate = this.networkManager.getNetworkTemplateById(netTemplateId);
        Assert.assertEquals(netTemplate.getId().toString(), netTemplateId);
        Assert.assertEquals("myTemplate", netTemplate.getName());
        Assert.assertEquals("dummy net template", netTemplate.getDescription());
        Assert.assertEquals("blue", netTemplate.getProperties().get("color"));
        Assert.assertEquals(netTemplate.getNetworkConfig().getId(), netConfig.getId());

        // update

        // delete

        // attempt to delete netconfig should fail
        try {
            this.networkManager.deleteNetworkConfiguration(netConfig.getId().toString());
            Assert.fail();
        } catch (CloudProviderException ex) {

        }

        this.networkManager.deleteNetworkTemplate(netTemplateId);

        try {
            this.networkManager.getNetworkTemplateById(netTemplateId);
            Assert.fail();
        } catch (ResourceNotFoundException ex) {
        }

        this.networkManager.deleteNetworkConfiguration(netConfig.getId().toString());
    }

    private String createNetwork() throws Exception {
        NetworkConfiguration netConfig = this.networkManager.createNetworkConfiguration(this
            .newNetworkConfiguration("myConfig"));
        netConfig = this.networkManager.getNetworkConfigurationById(netConfig.getId().toString());

        NetworkTemplate netTemplate = this.networkManager.createNetworkTemplate(this.newNetworkTemplate("myTemplate",
            netConfig, null));

        NetworkCreate networkCreate = new NetworkCreate();
        networkCreate.setName("myNetwork");
        networkCreate.setDescription("dummy network");
        networkCreate.setProperties(Collections.singletonMap("color", "blue"));

        networkCreate.setNetworkTemplate(netTemplate);

        Job job = this.networkManager.createNetwork(networkCreate);
        Assert.assertEquals("add", job.getAction());
        Assert.assertTrue(job.getTargetEntity() instanceof Network);
        Assert.assertTrue(job.getTargetEntity().getId() != null);

        this.waitForJobCompletion(job);

        return job.getTargetEntity().getId().toString();
    }

    @Test
    public void testCRUDNetwork() throws Exception {
        // create

        NetworkConfiguration netConfig = this.networkManager.createNetworkConfiguration(this
            .newNetworkConfiguration("myConfig"));
        netConfig = this.networkManager.getNetworkConfigurationById(netConfig.getId().toString());

        NetworkTemplate netTemplate = this.networkManager.createNetworkTemplate(this.newNetworkTemplate("myTemplate",
            netConfig, null));

        NetworkCreate networkCreate = new NetworkCreate();
        networkCreate.setName("myNetwork");
        networkCreate.setDescription("dummy network");
        networkCreate.setProperties(Collections.singletonMap("color", "blue"));

        networkCreate.setNetworkTemplate(netTemplate);

        Job job = this.networkManager.createNetwork(networkCreate);
        Assert.assertEquals("add", job.getAction());
        Assert.assertTrue(job.getTargetEntity() instanceof Network);
        Assert.assertTrue(job.getTargetEntity().getId() != null);

        this.waitForJobCompletion(job);

        // read
        String netId = job.getTargetEntity().getId().toString();

        Network network = this.networkManager.getNetworkById(netId);
        Assert.assertEquals(network.getId().toString(), netId);
        Assert.assertEquals("myNetwork", network.getName());
        Assert.assertEquals("dummy network", network.getDescription());
        Assert.assertEquals("blue", network.getProperties().get("color"));

        // update

        // delete

        job = this.networkManager.deleteNetwork(netId);
        Assert.assertEquals("delete", job.getAction());
        Assert.assertTrue(job.getTargetEntity() instanceof Network);
        Assert.assertEquals(job.getTargetEntity().getId().toString(), netId);

        this.waitForJobCompletion(job);

        try {
            this.networkManager.getNetworkById(netId);
            Assert.fail();
        } catch (ResourceNotFoundException ex) {
        }

    }

    NetworkPortConfiguration newNetworkPortConfiguration(final String name) {
        NetworkPortConfiguration netPortConfig = new NetworkPortConfiguration();
        netPortConfig.setName(name);
        netPortConfig.setDescription("my net port");
        netPortConfig.setProperties(Collections.singletonMap("color", "blue"));
        netPortConfig.setClassOfService("silver");
        netPortConfig.setPortType("ACCESS");
        return netPortConfig;
    }

    @Test
    public void testCRUDNetworkPortConfiguration() throws Exception {
        // create
        NetworkPortConfiguration netPortConfig = this.networkManager.createNetworkPortConfiguration(this
            .newNetworkPortConfiguration("myNetPortConfig"));

        Assert.assertNotNull(netPortConfig.getId());
        Assert.assertEquals("myNetPortConfig", netPortConfig.getName());
        Assert.assertEquals("my net port", netPortConfig.getDescription());
        Assert.assertEquals("blue", netPortConfig.getProperties().get("color"));
        Assert.assertEquals("silver", netPortConfig.getClassOfService());
        Assert.assertEquals("ACCESS", netPortConfig.getPortType());

        // attempt to create netportconfig with same name

        try {
            this.networkManager.createNetworkPortConfiguration(this.newNetworkPortConfiguration("myNetPortConfig"));
            Assert.fail();
        } catch (CloudProviderException ex) {
        }

        // read
        String netPortConfigId = netPortConfig.getId().toString();
        netPortConfig = this.networkManager.getNetworkPortConfigurationById(netPortConfigId);
        Assert.assertEquals(netPortConfig.getId().toString(), netPortConfigId);
        Assert.assertEquals("myNetPortConfig", netPortConfig.getName());
        Assert.assertEquals("my net port", netPortConfig.getDescription());
        Assert.assertEquals("blue", netPortConfig.getProperties().get("color"));
        Assert.assertEquals("silver", netPortConfig.getClassOfService());
        Assert.assertEquals("ACCESS", netPortConfig.getPortType());

        // update

        // delete

        this.networkManager.deleteNetworkPortConfiguration(netPortConfigId);

        try {
            this.networkManager.getNetworkPortConfigurationById(netPortConfigId);
            Assert.fail();
        } catch (ResourceNotFoundException ex) {
        }
    }

    NetworkPortTemplate newNetworkPortTemplate(final String name, final NetworkPortConfiguration netPortConfig,
        final Network network) {
        NetworkPortTemplate netTemplate = new NetworkPortTemplate();
        netTemplate.setName(name);
        netTemplate.setDescription("my net port template");
        netTemplate.setProperties(Collections.singletonMap("color", "blue"));
        netTemplate.setNetworkPortConfig(netPortConfig);
        netTemplate.setNetwork(network);
        return netTemplate;
    }

    @Test
    public void testCRUDNetworkPortTemplate() throws Exception {
        // create Network

        NetworkConfiguration netConfig = this.networkManager.createNetworkConfiguration(this
            .newNetworkConfiguration("myConfig"));
        netConfig = this.networkManager.getNetworkConfigurationById(netConfig.getId().toString());

        NetworkTemplate netTemplate = this.networkManager.createNetworkTemplate(this.newNetworkTemplate("myTemplate",
            netConfig, null));

        NetworkCreate networkCreate = new NetworkCreate();
        networkCreate.setName("myNetwork");
        networkCreate.setDescription("dummy network");
        networkCreate.setProperties(Collections.singletonMap("color", "blue"));

        networkCreate.setNetworkTemplate(netTemplate);
        Job job = this.networkManager.createNetwork(networkCreate);

        this.waitForJobCompletion(job);
        Network network = this.networkManager.getNetworkById(job.getTargetEntity().getId().toString());

        // create

        NetworkPortConfiguration netPortConfig = this.networkManager.createNetworkPortConfiguration(this
            .newNetworkPortConfiguration("myNetPortConfig"));
        netPortConfig = this.networkManager.getNetworkPortConfigurationById(netPortConfig.getId().toString());

        NetworkPortTemplate netPortTemplate = this.networkManager.createNetworkPortTemplate(this.newNetworkPortTemplate(
            "myNetPortTemplate", netPortConfig, network));
        String netPortTemplateId = netPortTemplate.getId().toString();
        Assert.assertEquals(netPortTemplate.getId().toString(), netPortTemplateId);
        Assert.assertEquals("myNetPortTemplate", netPortTemplate.getName());
        Assert.assertEquals("my net port template", netPortTemplate.getDescription());
        Assert.assertEquals("blue", netPortTemplate.getProperties().get("color"));
        Assert.assertEquals(netPortTemplate.getNetworkPortConfig().getId(), netPortConfig.getId());

        // attempt to create net template with same name

        try {
            this.networkManager.createNetworkPortTemplate(this.newNetworkPortTemplate("myNetPortTemplate", netPortConfig,
                network));
            Assert.fail();
        } catch (CloudProviderException ex) {
        }

        // read
        netPortTemplate = this.networkManager.getNetworkPortTemplateById(netPortTemplateId);
        Assert.assertEquals(netPortTemplate.getId().toString(), netPortTemplateId);
        Assert.assertEquals("myNetPortTemplate", netPortTemplate.getName());
        Assert.assertEquals("my net port template", netPortTemplate.getDescription());
        Assert.assertEquals("blue", netPortTemplate.getProperties().get("color"));
        Assert.assertEquals(netPortTemplate.getNetworkPortConfig().getId(), netPortConfig.getId());

        // update

        // delete

        // attempt to delete net port config should fail
        try {
            this.networkManager.deleteNetworkPortConfiguration(netPortConfig.getId().toString());
            Assert.fail();
        } catch (CloudProviderException ex) {

        }

        this.networkManager.deleteNetworkPortTemplate(netPortTemplateId);

        try {
            this.networkManager.getNetworkTemplateById(netPortTemplateId);
            Assert.fail();
        } catch (ResourceNotFoundException ex) {
        }

        this.networkManager.deleteNetworkPortConfiguration(netPortConfig.getId().toString());
    }

    @Test
    public void testCRUDNetworkPort() throws Exception {
        // create Network

        NetworkConfiguration netConfig = this.networkManager.createNetworkConfiguration(this
            .newNetworkConfiguration("myConfig"));
        netConfig = this.networkManager.getNetworkConfigurationById(netConfig.getId().toString());

        NetworkTemplate netTemplate = this.networkManager.createNetworkTemplate(this.newNetworkTemplate("myTemplate",
            netConfig, null));

        NetworkCreate networkCreate = new NetworkCreate();
        networkCreate.setName("myNetwork");
        networkCreate.setDescription("dummy network");
        networkCreate.setProperties(Collections.singletonMap("color", "blue"));

        networkCreate.setNetworkTemplate(netTemplate);
        Job job = this.networkManager.createNetwork(networkCreate);

        this.waitForJobCompletion(job);
        Network network = this.networkManager.getNetworkById(job.getTargetEntity().getId().toString());

        // create port

        NetworkPortConfiguration netPortConfig = this.networkManager.createNetworkPortConfiguration(this
            .newNetworkPortConfiguration("myNetPortConfig"));
        netPortConfig = this.networkManager.getNetworkPortConfigurationById(netPortConfig.getId().toString());

        NetworkPortTemplate netPortTemplate = this.networkManager.createNetworkPortTemplate(this.newNetworkPortTemplate(
            "myNetPortTemplate", netPortConfig, network));

        NetworkPortCreate networkPortCreate = new NetworkPortCreate();
        networkPortCreate.setName("myNetworkPort");
        networkPortCreate.setDescription("my network port");
        networkPortCreate.setProperties(Collections.singletonMap("color", "blue"));

        networkPortCreate.setNetworkPortTemplate(netPortTemplate);

        job = this.networkManager.createNetworkPort(networkPortCreate);
        Assert.assertEquals("add", job.getAction());
        Assert.assertTrue(job.getTargetEntity() instanceof NetworkPort);
        Assert.assertTrue(job.getTargetEntity().getId() != null);

        this.waitForJobCompletion(job);

        // read
        String netPortId = job.getTargetEntity().getId().toString();

        NetworkPort networkPort = this.networkManager.getNetworkPortById(netPortId);
        Assert.assertEquals(networkPort.getId().toString(), netPortId);
        Assert.assertEquals("myNetworkPort", networkPort.getName());
        Assert.assertEquals("my network port", networkPort.getDescription());
        Assert.assertEquals("blue", networkPort.getProperties().get("color"));

        // update

        // delete

        job = this.networkManager.deleteNetworkPort(netPortId);
        Assert.assertEquals("delete", job.getAction());
        Assert.assertTrue(job.getTargetEntity() instanceof NetworkPort);
        Assert.assertEquals(job.getTargetEntity().getId().toString(), netPortId);

        this.waitForJobCompletion(job);

        try {
            this.networkManager.getNetworkPortById(netPortId);
            Assert.fail();
        } catch (ResourceNotFoundException ex) {
        }

    }

    @Test
    public void testCRUDForwardingGroupTemplate() throws Exception {
        // TODO
    }

    @Test
    public void testCRUDForwardingGroup() throws Exception {
        ForwardingGroupTemplate forwardingGroupTemplate = new ForwardingGroupTemplate();
        forwardingGroupTemplate.setName("myForwardingGroupTemplate");
        forwardingGroupTemplate.setDescription("my forwarding group template");
        forwardingGroupTemplate.setProperties(Collections.singletonMap("color", "blue"));

        ForwardingGroupCreate forwardingGroupCreate = new ForwardingGroupCreate();
        forwardingGroupCreate.setForwardingGroupTemplate(forwardingGroupTemplate);
        forwardingGroupCreate.setName("myForwardingGroup");
        forwardingGroupCreate.setDescription("my forwarding group");
        forwardingGroupCreate.setProperties(Collections.singletonMap("color", "blue"));

        Job job = this.networkManager.createForwardingGroup(forwardingGroupCreate);
        Assert.assertEquals("add", job.getAction());
        Assert.assertTrue(job.getTargetEntity() instanceof ForwardingGroup);
        Assert.assertTrue(job.getTargetEntity().getId() != null);

        // read
        String forwardingGroupId = job.getTargetEntity().getId().toString();

        this.waitForJobCompletion(job);

        ForwardingGroup forwardingGroup = this.networkManager.getForwardingGroupById(forwardingGroupId);
        Assert.assertEquals(forwardingGroup.getId().toString(), forwardingGroupId);
        Assert.assertEquals("myForwardingGroup", forwardingGroup.getName());
        Assert.assertEquals("my forwarding group", forwardingGroup.getDescription());
        Assert.assertEquals("blue", forwardingGroup.getProperties().get("color"));

        // update: add network

        NetworkConfiguration netConfig = this.networkManager.createNetworkConfiguration(this
            .newNetworkConfiguration("myConfig"));
        netConfig = this.networkManager.getNetworkConfigurationById(netConfig.getId().toString());

        NetworkTemplate netTemplate = this.networkManager.createNetworkTemplate(this.newNetworkTemplate("myTemplate",
            netConfig, null));

        NetworkCreate networkCreate = new NetworkCreate();
        networkCreate.setName("myNetwork1");
        networkCreate.setDescription("dummy network");
        networkCreate.setProperties(Collections.singletonMap("color", "blue"));

        networkCreate.setNetworkTemplate(netTemplate);

        job = this.networkManager.createNetwork(networkCreate);
        this.waitForJobCompletion(job);
        String network1Id = job.getTargetEntity().getId().toString();

        Network network1 = this.networkManager.getNetworkById(network1Id);

        job = this.networkManager.addNetworkToForwardingGroup(forwardingGroupId, network1Id);
        Assert.assertEquals("add", job.getAction());
        Assert.assertEquals(job.getTargetEntity().getId().toString(), forwardingGroupId);
        Assert.assertEquals(job.getAffectedEntities().get(0).getId().toString(), network1Id);
        Assert.assertTrue(job.getTargetEntity().getId() != null);
        this.waitForJobCompletion(job);
        forwardingGroup = this.networkManager.getForwardingGroupById(forwardingGroupId);
        Assert.assertEquals(forwardingGroup.getNetworks().size(), 1);
        Assert.assertEquals(forwardingGroup.getNetworks().get(0).getId().toString(), network1Id);

        // update: remove network

        job = this.networkManager.removeNetworkFromForwardingGroup(forwardingGroupId, network1Id);
        Assert.assertEquals("delete", job.getAction());
        Assert.assertEquals(job.getTargetEntity().getId().toString(), forwardingGroupId);
        Assert.assertEquals(job.getAffectedEntities().get(0).getId().toString(), network1Id);
        Assert.assertTrue(job.getTargetEntity().getId() != null);
        this.waitForJobCompletion(job);
        forwardingGroup = this.networkManager.getForwardingGroupById(forwardingGroupId);
        Assert.assertEquals(forwardingGroup.getNetworks().size(), 0);

        // delete

        job = this.networkManager.deleteForwardingGroup(forwardingGroupId);
        Assert.assertEquals("delete", job.getAction());
        Assert.assertTrue(job.getTargetEntity() instanceof ForwardingGroup);
        Assert.assertEquals(job.getTargetEntity().getId().toString(), forwardingGroupId);

        this.waitForJobCompletion(job);

        try {
            this.networkManager.getForwardingGroupById(forwardingGroupId);
            Assert.fail();
        } catch (ResourceNotFoundException ex) {
        }
    }
}
