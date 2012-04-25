package org.ow2.sirocco.apis.rest.cimi;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;

import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.ActionType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAction;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiEntityType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiOperation;
import org.ow2.sirocco.apis.rest.cimi.utils.Constants;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
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
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;
import org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class ClientAccess {

    private static final String PORT_WEBCONTAINER = "1581";

    private static final String PORT_CAROL = "1582";

    private static final String PORT_DB = "1584";

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

    private IRemoteCredentialsManager credManager;

    private IRemoteMachineImageManager machineImageManager;

    private IRemoteCloudProviderManager cloudProviderManager;

    private IRemoteUserManager userManager;

    private IRemoteJobManager jobManager;

    private String baseURI;

    private void connectToCloudManager() throws Exception {
        // String carolPortString = System.getProperty("carol.port");
        String carolPortString = ClientAccess.PORT_CAROL;
        Assert.assertNotNull("carol.port not set!", carolPortString);
        int carolPort = Integer.parseInt(carolPortString);

        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, ClientAccess.INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, "rmi://localhost:" + carolPort);
        final long timeout = System.currentTimeMillis() + ClientAccess.INITIALIZE_TIMEOUT * 1000;
        while (true) {
            try {
                Context context = new InitialContext(env);
                this.machineManager = (IRemoteMachineManager) context.lookup(IMachineManager.EJB_JNDI_NAME);
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
        // Reader reader = new FileReader(new
        // File(System.getProperty("dbunit.dataset")));
        Reader reader = new FileReader(new File("itest/db-sirocco.xml"));
        XmlDataSet dataSet = new XmlDataSet(reader);

        //
        // <database.url>jdbc:hsqldb:hsql://localhost:${db.port}/db_jonas</database.url>
        // <database.driver>org.hsqldb.jdbcDriver</database.driver>
        // <database.mapper>rdb.hsql</database.mapper>
        // <database.schema>PUBLIC</database.schema>
        // <database.username>jonas</database.username>
        // <database.password>jonas</database.password>
        // <database.teststmt>select count(1) from
        // information_schema.system_tables</database.teststmt>
        //
        // <dbunit.databasefactory>org.dbunit.ext.hsqldb.HsqldbDataTypeFactory</dbunit.databasefactory>
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.hsqldb.jdbcDriver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:hsqldb:hsql://localhost:"
            + ClientAccess.PORT_DB + "/db_jonas");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "jonas");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "jonas");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_SCHEMA, "PUBLIC");

        databaseTest = new PropertiesBasedJdbcDatabaseTester();
        databaseTest.setDataSet(dataSet);
        databaseTest.setSetUpOperation(DatabaseOperation.DELETE_ALL);
        databaseTest.onSetup();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.setUpDatabase();
        this.connectToCloudManager();
        User user = this.userManager.createUser("Lov", "Maps", "lov@maps.com", ClientAccess.USER_NAME, "232908Ivry");
        CloudProvider provider = this.cloudProviderManager.createCloudProvider(ClientAccess.CLOUD_PROVIDER_TYPE, "mock");
        CloudProviderAccount account = this.cloudProviderManager.createCloudProviderAccount(provider.getId().toString(),
            ClientAccess.ACCOUNT_LOGIN, ClientAccess.ACCOUNT_CREDENTIALS);
        this.cloudProviderManager.addCloudProviderAccountToUser(user.getId().toString(), account.getId().toString());

        this.baseURI = this.buildBaseURI();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private void waitForJobCompletion(Job job) throws Exception {
        int counter = ClientAccess.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
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

    private void waitForJobCompletion(final WebResource service, final String idJob) throws Exception {
        ClientResponse response;
        CimiJob jobRead;

        int counter = ClientAccess.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;

        while (true) {

            /*
             * Read Job
             */
            response = service.path(this.extractPath(idJob)).accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
            Assert.assertEquals(200, response.getStatus());
            jobRead = response.getEntity(CimiJob.class);

            System.out.println("====== " + CimiEntityType.Job);
            System.out.println("jobRead.getId:" + jobRead.getId());
            System.out.println("jobRead.getStatus:" + jobRead.getStatus());
            System.out.println("jobRead.getTargetEntity:" + jobRead.getTargetEntity());

            if (false == "RUNNING".equals(jobRead.getStatus())) {
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

    private void initDatabase() throws Exception {
        MachineImage image = new MachineImage();
        image.setName("WinXP_SP2");
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
    }

    private String buildBaseURI() {
        StringBuilder sb = new StringBuilder();
        // String webPortString = System.getProperty("webcontainer.port");
        String webPortString = ClientAccess.PORT_WEBCONTAINER;
        Assert.assertNotNull("webcontainer.port not set!", webPortString);
        sb.append("http://localhost:").append(webPortString);
        sb.append('/').append("sirocco-rest").append("/cimi");
        return sb.toString();
    }

    private String extractPath(final String href) {
        return href.substring(this.baseURI.length());
    }

    @Test
    public void testScenarioOne() throws Exception {
        this.initDatabase();

        ClientResponse response;

        ClientConfig config = new DefaultClientConfig();
        // config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
        // Boolean.TRUE);
        Client client = Client.create(config);
        WebResource service = client.resource(this.baseURI);

        /*
         * Retrieve the CEP
         */
        response = service.path(ConstantsPath.CLOUDENTRYPOINT_PATH).accept(MediaType.APPLICATION_XML_TYPE)
            .get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        CimiCloudEntryPoint cloudEntryPoint = response.getEntity(CimiCloudEntryPoint.class);

        System.out.println("====== " + CimiEntityType.CloudEntryPoint);
        System.out.println("ID: " + cloudEntryPoint.getId());
        if (null != cloudEntryPoint.getCredentials()) {
            System.out.println("getCredentials: " + cloudEntryPoint.getCredentials().getHref());
        }
        if (null != cloudEntryPoint.getCredentialsTemplates()) {
            System.out.println("getCredentialsTemplates: " + cloudEntryPoint.getCredentialsTemplates().getHref());
        }
        if (null != cloudEntryPoint.getMachineConfigs()) {
            System.out.println("getMachineConfigs: " + cloudEntryPoint.getMachineConfigs().getHref());
        }
        if (null != cloudEntryPoint.getMachineImages()) {
            System.out.println("getMachineImages: " + cloudEntryPoint.getMachineImages().getHref());
        }
        if (null != cloudEntryPoint.getMachines()) {
            System.out.println("getMachines: " + cloudEntryPoint.getMachines().getHref());
        }
        if (null != cloudEntryPoint.getMachineTemplates()) {
            System.out.println("getMachineTemplates: " + cloudEntryPoint.getMachineTemplates().getHref());
        }

        /*
         * Retrieve the list of Machine Images
         */
        response = service.path(this.extractPath(cloudEntryPoint.getMachineImages().getHref()))
            .accept("application/CIMI-MachineImageCollection+xml").get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        CimiMachineImageCollection machineImagesCollection = response.getEntity(CimiMachineImageCollection.class);

        System.out.println("====== " + CimiEntityType.MachineImageCollection);
        System.out.println("ID: " + machineImagesCollection.getId());
        if (null != machineImagesCollection.getMachineImages()) {
            for (CimiMachineImage mImage : machineImagesCollection.getMachineImages()) {
                System.out.println("machineImage: " + mImage.getHref());
            }
        }

        /*
         * Choose a Machine Image (first one)
         */
        response = service.path(this.extractPath(machineImagesCollection.getMachineImages()[0].getHref()))
            .accept("application/CIMI-MachineImage+xml").get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        CimiMachineImage machineImage = response.getEntity(CimiMachineImage.class);

        System.out.println("====== " + CimiEntityType.MachineImage);
        System.out.println("ID: " + machineImage.getId());
        System.out.println("name: " + machineImage.getName());
        System.out.println("imageLocation: " + machineImage.getImageLocation().getHref());

        /*
         * Retrieve the list of Machine Configurations
         */
        response = service.path(this.extractPath(cloudEntryPoint.getMachineConfigs().getHref()))
            .accept("application/CIMI-MachineConfigurationCollection+xml").get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        CimiMachineConfigurationCollection machineConfigsCollection = response
            .getEntity(CimiMachineConfigurationCollection.class);

        System.out.println("====== " + CimiEntityType.MachineConfigurationCollection);
        System.out.println("ID: " + machineConfigsCollection.getId());
        if (null != machineConfigsCollection.getMachineConfigurations()) {
            for (CimiMachineConfiguration mConfig : machineConfigsCollection.getMachineConfigurations()) {
                System.out.println("machineConfiguration: " + mConfig.getHref());
            }
        }

        /*
         * Choose a Machine Configuration (first one)
         */
        response = service.path(this.extractPath(machineConfigsCollection.getMachineConfigurations()[0].getHref()))
            .accept("application/CIMI-MachineConfiguration+xml").get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        CimiMachineConfiguration machineConfiguration = response.getEntity(CimiMachineConfiguration.class);

        System.out.println("====== " + CimiEntityType.MachineConfiguration);
        System.out.println("ID: " + machineConfiguration.getId());
        System.out.println("getName: " + machineConfiguration.getName());
        System.out.println("getDescription: " + machineConfiguration.getDescription());
        System.out.println("getCreated: " + machineConfiguration.getCreated());
        System.out.println("getCpu().getNumberVirtualCpus: " + machineConfiguration.getCpu().getNumberVirtualCpus());
        System.out.println("getCpu().getFrequency: " + machineConfiguration.getCpu().getFrequency());
        System.out.println("getCpu().getUnits: " + machineConfiguration.getCpu().getUnits());
        System.out.println("getMemory().getQuantity: " + machineConfiguration.getMemory().getQuantity());
        System.out.println("getMemory().getUnit: " + machineConfiguration.getMemory().getUnits());
        for (CimiDiskConfiguration diskConfig : machineConfiguration.getDisks()) {
            System.out.println("disk.getAttachmentPoint: " + diskConfig.getAttachmentPoint());
            System.out.println("disk.getCapacity.getQuantity: " + diskConfig.getCapacity().getQuantity());
            System.out.println("disk.getCapacity.getUnits: " + diskConfig.getCapacity().getUnits());
            System.out.println("disk.getFormat: " + diskConfig.getFormat());
        }

        /*
         * Retrieve the list of Credentials
         */
        // FIXME Impossible to achieve : CredentialsCollection don't exists in
        // database

        /*
         * Create a new Credentials entity
         */
        CimiCredentialsTemplate credentialsTemplate = new CimiCredentialsTemplate();
        credentialsTemplate.setUserName("JoeSmith");
        credentialsTemplate.setPassword("letmein");
        CimiCredentialsCreate credentialsCreate = new CimiCredentialsCreate();
        credentialsCreate.setCredentialsTemplate(credentialsTemplate);
        credentialsCreate.setName("Default");
        credentialsCreate.setDescription("Default User");

        response = service.path(ConstantsPath.CREDENTIALS_PATH).accept(MediaType.APPLICATION_XML_TYPE)
            .entity(credentialsCreate, MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        Assert.assertEquals(201, response.getStatus());
        CimiCredentials credentials = response.getEntity(CimiCredentials.class);

        System.out.println("====== " + CimiEntityType.Credentials + " : create");
        System.out.println("Location: " + response.getLocation());
        System.out.println("Header: " + response.getHeaders());
        System.out.println("credentials.getId: " + credentials.getId());
        System.out.println("credentials.getName: " + credentials.getName());
        System.out.println("credentials.getUserNam: " + credentials.getUserName());
        System.out.println("credentials.getPassword: " + credentials.getPassword());
        System.out.println("credentials.getCreated: " + credentials.getCreated());

        /*
         * Retrieve the list of Machine
         */
        // FIXME machine Collection not supported in rest interface
        // CimiMachineCollection machineCollection =
        // service.path(this.extractPath(cloudEntryPoint.getMachines().getHref()))
        // .accept(MediaType.APPLICATION_XML_TYPE).get(CimiMachineCollection.class);
        //
        // System.out.println("====== " + CimiEntityType.MachineCollection);
        // System.out.println("ID: " + machineCollection.getId());
        // if (null != machineCollection.getOperations()) {
        // for (CimiOperation maOps : machineCollection.getOperations()) {
        // System.out.println("machine operation: " + maOps.getRel() + ", " +
        // maOps.getHref());
        // }
        // }

        /*
         * Create a new Machine with values
         */
        // Adds attachmentPoint for creating a valid machine
        machineConfiguration.getDisks()[0].setAttachmentPoint("attachmentPoint");

        CimiMachineTemplate machineTemplate = new CimiMachineTemplate();
        machineTemplate.setMachineConfig(machineConfiguration);
        machineTemplate.setMachineImage(machineImage);
        // FIXME why credentialsTemplate
        machineTemplate.setCredentials(credentialsTemplate);
        CimiMachineCreate machineCreate = new CimiMachineCreate();
        machineCreate.setName("myMachine1");
        machineCreate.setDescription("My very first machine");
        machineCreate.setMachineTemplate(machineTemplate);

        response = service.path(ConstantsPath.MACHINE_PATH).accept(MediaType.APPLICATION_XML_TYPE)
            .entity(machineCreate, MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        Assert.assertEquals(202, response.getStatus());
        CimiJob jobMachineCreate = response.getEntity(CimiJob.class);

        System.out.println("====== " + CimiEntityType.Job + " for machine creation");
        System.out.println("Location: " + response.getLocation());
        System.out.println("Header: " + response.getHeaders());
        System.out.println("jobMachineCreate.ID:" + jobMachineCreate.getId());
        System.out.println("jobMachineCreate.Status:" + jobMachineCreate.getStatus());
        System.out.println("jobMachineCreate.TargetEntity:" + jobMachineCreate.getTargetEntity());

        /*
         * Read and wait end of Job
         */
        this.waitForJobCompletion(service, jobMachineCreate.getId());

        /*
         * Query the Machine
         */
        response = service.path(this.extractPath(jobMachineCreate.getTargetEntity())).accept(MediaType.APPLICATION_XML_TYPE)
            .get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        CimiMachine machine = response.getEntity(CimiMachine.class);

        System.out.println("====== " + CimiEntityType.Machine);
        System.out.println("ID: " + machine.getId());
        System.out.println("getName: " + machine.getName());
        System.out.println("getDescription: " + machine.getDescription());
        System.out.println("getCreated: " + machine.getCreated());
        System.out.println("getUpdated: " + machine.getUpdated());
        System.out.println("State: " + machine.getState());
        System.out.println("getCpu().getNumberVirtualCpus: " + machine.getCpu().getNumberVirtualCpus());
        System.out.println("getCpu().getFrequency: " + machine.getCpu().getFrequency());
        System.out.println("getCpu().getUnits: " + machine.getCpu().getUnits());
        System.out.println("getMemory().getQuantity: " + machine.getMemory().getQuantity());
        System.out.println("getMemory().getUnit: " + machine.getMemory().getUnits());
        if (null != machine.getDisks()) {
            for (CimiDisk disk : machine.getDisks()) {
                System.out.println("disk.getCapacity.getQuantity: " + disk.getCapacity().getQuantity());
                System.out.println("disk.getCapacity.getUnits: " + disk.getCapacity().getUnits());
            }
        }
        for (CimiOperation operation : machine.getOperations()) {
            System.out.println("operation: " + operation.getRel() + ", " + operation.getHref());
        }

        /*
         * Start the Machine
         */
        CimiAction actionStart = new CimiAction();
        actionStart.setAction(ActionType.START.getPath());
        response = service.path(this.extractPath(machine.getId())).accept(MediaType.APPLICATION_XML_TYPE)
            .entity(actionStart, MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        Assert.assertEquals(202, response.getStatus());
        CimiJob jobMachineStart = response.getEntity(CimiJob.class);

        /*
         * Read and wait end of Job
         */
        this.waitForJobCompletion(service, jobMachineStart.getId());

        /*
         * Query the Machine to verify if it started
         */
        response = service.path(this.extractPath(jobMachineCreate.getTargetEntity())).accept(MediaType.APPLICATION_XML_TYPE)
            .get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        machine = response.getEntity(CimiMachine.class);
        Assert.assertEquals("STARTED", machine.getState());

        System.out.println("====== " + CimiEntityType.Machine);
        System.out.println("ID: " + machine.getId());
        System.out.println("getName: " + machine.getName());
        System.out.println("getDescription: " + machine.getDescription());
        System.out.println("getCreated: " + machine.getCreated());
        System.out.println("getUpdated: " + machine.getUpdated());
        System.out.println("State: " + machine.getState());

        /*
         * Stop the Machine
         */
        CimiAction actionStop = new CimiAction();
        actionStop.setAction(ActionType.STOP.getPath());
        response = service.path(this.extractPath(machine.getId())).accept(MediaType.APPLICATION_XML_TYPE)
            .entity(actionStop, MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        Assert.assertEquals(202, response.getStatus());
        CimiJob jobMachineStop = response.getEntity(CimiJob.class);

        /*
         * Read and wait end of Job
         */
        this.waitForJobCompletion(service, jobMachineStop.getId());

        /*
         * Query the Machine to verify if it stopped
         */
        response = service.path(this.extractPath(jobMachineCreate.getTargetEntity())).accept(MediaType.APPLICATION_XML_TYPE)
            .get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        machine = response.getEntity(CimiMachine.class);
        Assert.assertEquals("STOPPED", machine.getState());

        System.out.println("====== " + CimiEntityType.Machine);
        System.out.println("ID: " + machine.getId());
        System.out.println("getName: " + machine.getName());
        System.out.println("getDescription: " + machine.getDescription());
        System.out.println("getCreated: " + machine.getCreated());
        System.out.println("getUpdated: " + machine.getUpdated());
        System.out.println("State: " + machine.getState());

        /*
         * Update the Machine's name and description
         */
        CimiMachine machineToUpdate = new CimiMachine();
        machineToUpdate.setName("myMachine1Updated");
        response = service.path(this.extractPath(machine.getId())).queryParam(Constants.PARAM_CIMI_SELECT, "name")
            .queryParam(Constants.PARAM_CIMI_SELECT, "description").accept(MediaType.APPLICATION_XML_TYPE)
            .entity(machineToUpdate, MediaType.APPLICATION_XML_TYPE).put(ClientResponse.class);
        Assert.assertEquals(202, response.getStatus());
        CimiJob jobMachineUpdate = response.getEntity(CimiJob.class);

        /*
         * Read and wait end of Job
         */
        this.waitForJobCompletion(service, jobMachineUpdate.getId());

        /*
         * Query the Machine to verify if it updated
         */
        response = service.path(this.extractPath(jobMachineCreate.getTargetEntity())).accept(MediaType.APPLICATION_XML_TYPE)
            .get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        machine = response.getEntity(CimiMachine.class);
        Assert.assertEquals("myMachine1Updated", machine.getName());

        System.out.println("====== " + CimiEntityType.Machine);
        System.out.println("ID: " + machine.getId());
        System.out.println("getName: " + machine.getName());
        System.out.println("getDescription: " + machine.getDescription());
        System.out.println("getCreated: " + machine.getCreated());
        System.out.println("getUpdated: " + machine.getUpdated());
        System.out.println("State: " + machine.getState());

    }
}
