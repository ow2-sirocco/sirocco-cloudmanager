package org.ow2.sirocco.cloudmanager.itests;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collections;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.ws.rs.core.MediaType;

import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.jonas.security.auth.callback.NoInputCallbackHandler;
import org.ow2.sirocco.apis.rest.cimi.domain.ActionType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAction;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiOperation;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.utils.Constants;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.itests.util.CustomDBUnitDeleteAllOperation;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.Base64;

public class RestCimiPrimerScenarioTest {

    private static final String USER_NAME = "sirocco-test";

    private static final String USER_PASSWORD = "232908Ivry";

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

    // private IRemoteCredentialsManager credManager;

    private IRemoteMachineImageManager machineImageManager;

    private IRemoteCloudProviderManager cloudProviderManager;

    private IRemoteUserManager userManager;

    private IRemoteJobManager jobManager;

    private String baseURI;

    private void connectToCloudManager() throws Exception {
        // Get Carol port
        String carolPortString = System.getProperty("carol.port");
        Assert.assertNotNull("carol.port not set!", carolPortString);
        int carolPort = Integer.parseInt(carolPortString);

        // Get JAAS config file
        String jaasConfig = System.getProperty("jaas.config.file");
        Assert.assertNotNull("Property 'jaas.config.file' not set!", jaasConfig);
        System.setProperty("java.security.auth.login.config", jaasConfig);

        // Obtain a CallbackHandler
        CallbackHandler handler = new NoInputCallbackHandler(RestCimiPrimerScenarioTest.USER_NAME,
            RestCimiPrimerScenarioTest.USER_PASSWORD);

        // Set properties for Initial Context : JAAS and EJB
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, RestCimiPrimerScenarioTest.INITIAL_CONTEXT_FACTORY);
        System.setProperty(Context.PROVIDER_URL, "rmi://localhost:" + carolPort);

        final long timeout = System.currentTimeMillis() + RestCimiPrimerScenarioTest.INITIALIZE_TIMEOUT * 1000;
        while (true) {
            try {
                // Obtain a initial context
                Context context = new InitialContext();
                // Obtain a LoginContext
                LoginContext lc = null;
                try {
                    lc = new LoginContext("jaasclient", handler);
                } catch (LoginException le) {
                    System.err.println("Cannot create LoginContext: " + le.getMessage());
                    throw le;
                } catch (SecurityException se) {
                    System.err.println("Cannot create LoginContext: " + se.getMessage());
                    throw se;
                }
                // Login
                try {
                    lc.login();
                } catch (LoginException le) {
                    System.err.println("Authentication failed : " + le.getMessage());
                    throw le;
                }
                // Authentication is ok
                // System.out.println("Authentication succeeded");

                // Obtain EJBs
                this.machineManager = (IRemoteMachineManager) context.lookup(IMachineManager.EJB_JNDI_NAME);
                this.cloudProviderManager = (IRemoteCloudProviderManager) context.lookup(ICloudProviderManager.EJB_JNDI_NAME);
                this.userManager = (IRemoteUserManager) context.lookup(IUserManager.EJB_JNDI_NAME);
                // this.credManager = (IRemoteCredentialsManager)
                // context.lookup(ICredentialsManager.EJB_JNDI_NAME);
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
        User user = this.userManager.createUser("Lov", "Maps", "lov@maps.com", RestCimiPrimerScenarioTest.USER_NAME,
            RestCimiPrimerScenarioTest.USER_PASSWORD);
        CloudProvider provider = this.cloudProviderManager.createCloudProvider(RestCimiPrimerScenarioTest.CLOUD_PROVIDER_TYPE,
            "mock");
        CloudProviderAccount account = this.cloudProviderManager.createCloudProviderAccount(provider.getId().toString(),
            RestCimiPrimerScenarioTest.ACCOUNT_LOGIN, RestCimiPrimerScenarioTest.ACCOUNT_CREDENTIALS);
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
        int counter = RestCimiPrimerScenarioTest.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
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

    private void waitForJobCompletion(final WebResource webResource, final String idJob, final MediaType mediaType)
        throws Exception {
        ClientResponse response;
        CimiJob jobRead;

        int counter = RestCimiPrimerScenarioTest.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;

        while (true) {

            /*
             * Read Job
             */
            this.printTitleTest("Read Job", true);
            WebResource service = webResource.path(this.extractPath(idJob));
            response = this.authentication(service).accept(mediaType).get(ClientResponse.class);
            Assert.assertEquals(200, response.getStatus());
            jobRead = response.getEntity(CimiJob.class);

            System.out.println("====== " + ExchangeType.Job);
            System.out.println("jobRead.getId:" + jobRead.getId());
            System.out.println("jobRead.getStatus:" + jobRead.getStatus());
            System.out.println("jobRead.getTargetResource:" + jobRead.getTargetResource());
            this.printTitleTest("Read Job", false);

            if (false == "RUNNING".equals(jobRead.getStatus())) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- <= 0) {
                throw new Exception("Job operation time out");
            }
        }
    }

    private MachineConfiguration buildMachineConfiguration(final String name, final String description, final int numCpus,
        final int ramSizeInMB, final int diskSizeInGB) {
        MachineConfiguration machineConfig = new MachineConfiguration();
        machineConfig.setName(name);
        machineConfig.setDescription(description);

        DiskTemplate disk = new DiskTemplate();
        disk.setCapacity(diskSizeInGB * 1000 * 1000);
        disk.setFormat("ext3");
        disk.setInitialLocation("initialLocation");

        machineConfig.setCpu(numCpus);
        machineConfig.setMemory(ramSizeInMB * 1024);
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
        String webPortString = System.getProperty("webcontainer.port");
        Assert.assertNotNull("webcontainer.port not set!", webPortString);
        sb.append("http://localhost:").append(webPortString);
        sb.append('/').append("sirocco-rest").append("/cimi");
        return sb.toString();
    }

    private String extractPath(final String href) {
        return href.substring(this.baseURI.length());
    }

    private void printTitleTest(final String title, final boolean begin) {
        StringBuilder sb = new StringBuilder();
        if (true == begin) {
            sb.append('\n');
            sb.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        } else {
            sb.append("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        }
        sb.append(' ').append(title);
        System.out.println(sb.toString());
    }

    private String encodeBasicAuthentication(final String userName, final String password) {
        StringBuilder sbToEncode = new StringBuilder();
        sbToEncode.append(userName).append(':').append(password);
        StringBuilder sb = new StringBuilder();
        sb.append("Basic ").append(new String(Base64.encode(sbToEncode.toString())));
        return sb.toString();
    }

    private WebResource.Builder authentication(final WebResource webResource) {
        return this.authentication(webResource, RestCimiPrimerScenarioTest.USER_NAME, RestCimiPrimerScenarioTest.USER_PASSWORD);
    }

    private WebResource.Builder authentication(final WebResource webResource, final String userName, final String password) {
        WebResource.Builder builder = null;
        builder = webResource.header("Authorization", this.encodeBasicAuthentication(userName, password));
        return builder;
    }

    private void runScenarioOne(final MediaType mediaType) throws Exception {
        ClientResponse response;
        // Init data
        this.initDatabase();
        // Init client REST
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(config);
        client.addFilter(new LoggingFilter());
        WebResource webResource = client.resource(this.baseURI);
        WebResource service = null;

        /*
         * Retrieve the CEP
         */
        this.printTitleTest("Retrieve the CEP", true);
        service = webResource.path(ConstantsPath.CLOUDENTRYPOINT_PATH);
        response = this.authentication(service).accept(mediaType).get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        CimiCloudEntryPoint cloudEntryPoint = response.getEntity(CimiCloudEntryPoint.class);
        Assert.assertEquals(200, response.getStatus());

        System.out.println("====== " + ExchangeType.CloudEntryPoint);
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
        this.printTitleTest("Retrieve the CEP", false);

        /*
         * Retrieve the list of Machine Images
         */
        this.printTitleTest("Retrieve the list of Machine Images", true);
        service = webResource.path(this.extractPath(cloudEntryPoint.getMachineImages().getHref()));
        response = this.authentication(service).accept(mediaType).get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        CimiMachineImageCollection machineImagesCollection = response.getEntity(CimiMachineImageCollection.class);

        System.out.println("====== " + ExchangeType.MachineImageCollection);
        System.out.println("ID: " + machineImagesCollection.getId());
        if (null != machineImagesCollection.getArray()) {
            for (CimiMachineImage mImage : machineImagesCollection.getArray()) {
                System.out.println("machineImage: " + mImage.getHref());
            }
        }
        this.printTitleTest("Retrieve the list of Machine Images", false);

        /*
         * Choose a Machine Image (first one)
         */
        this.printTitleTest("Choose a Machine Image (first one)", true);
        service = webResource.path(this.extractPath(machineImagesCollection.getArray()[0].getHref()));
        response = this.authentication(service).accept(mediaType).get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        CimiMachineImage machineImage = response.getEntity(CimiMachineImage.class);

        System.out.println("====== " + ExchangeType.MachineImage);
        System.out.println("ID: " + machineImage.getId());
        System.out.println("name: " + machineImage.getName());
        System.out.println("imageLocation: " + machineImage.getImageLocation().getHref());
        this.printTitleTest("Choose a Machine Image (first one)", false);

        /*
         * Retrieve the list of Machine Configurations
         */
        this.printTitleTest("Retrieve the list of Machine Configurations", true);
        service = webResource.path(this.extractPath(cloudEntryPoint.getMachineConfigs().getHref()));
        response = this.authentication(service).accept(mediaType).get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        CimiMachineConfigurationCollection machineConfigsCollection = response
            .getEntity(CimiMachineConfigurationCollection.class);

        System.out.println("====== " + ExchangeType.MachineConfigurationCollection);
        System.out.println("ID: " + machineConfigsCollection.getId());
        if (null != machineConfigsCollection.getArray()) {
            for (CimiMachineConfiguration mConfig : machineConfigsCollection.getArray()) {
                System.out.println("machineConfiguration: " + mConfig.getHref());
            }
        }
        this.printTitleTest("Retrieve the list of Machine Configurations", false);

        /*
         * Choose a Machine Configuration (first one)
         */
        this.printTitleTest("Choose a Machine Configuration (first one)", true);
        service = webResource.path(this.extractPath(machineConfigsCollection.getArray()[0].getHref()));
        response = this.authentication(service).accept(mediaType).get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        CimiMachineConfiguration machineConfiguration = response.getEntity(CimiMachineConfiguration.class);

        System.out.println("====== " + ExchangeType.MachineConfiguration);
        System.out.println("ID: " + machineConfiguration.getId());
        System.out.println("getName: " + machineConfiguration.getName());
        System.out.println("getDescription: " + machineConfiguration.getDescription());
        System.out.println("getCreated: " + machineConfiguration.getCreated());
        System.out.println("getCpu: " + machineConfiguration.getCpu());
        System.out.println("getMemory: " + machineConfiguration.getMemory());
        for (CimiDiskConfiguration diskConfig : machineConfiguration.getDisks()) {
            System.out.println("disk.getInitialLocation: " + diskConfig.getInitialLocation());
            System.out.println("disk.getCapacity: " + diskConfig.getCapacity());
            System.out.println("disk.getFormat: " + diskConfig.getFormat());
        }
        this.printTitleTest("Choose a Machine Configuration (first one)", false);

        /*
         * Retrieve the list of Credentials
         */
        // FIXME Impossible to achieve : CredentialsCollection don't exists in
        // database

        /*
         * Create a new Credentials resource
         */
        this.printTitleTest("Create a new Credentials resource", true);
        CimiCredentialsTemplate credentialsTemplate = new CimiCredentialsTemplate();
        credentialsTemplate.setUserName("JoeSmith");
        credentialsTemplate.setPassword("letmein");
        CimiCredentialsCreate credentialsCreate = new CimiCredentialsCreate();
        credentialsCreate.setCredentialsTemplate(credentialsTemplate);
        credentialsCreate.setName("Default");
        credentialsCreate.setDescription("Default User");

        service = webResource.path(ConstantsPath.CREDENTIALS_PATH);
        response = this.authentication(service).accept(mediaType).entity(credentialsCreate, mediaType)
            .post(ClientResponse.class);
        Assert.assertEquals(201, response.getStatus());
        CimiCredentials credentials = response.getEntity(CimiCredentials.class);

        System.out.println("====== " + ExchangeType.Credentials + " : create");
        System.out.println("Location: " + response.getLocation());
        System.out.println("Header: " + response.getHeaders());
        System.out.println("credentials.getId: " + credentials.getId());
        System.out.println("credentials.getName: " + credentials.getName());
        System.out.println("credentials.getUserNam: " + credentials.getUserName());
        System.out.println("credentials.getPassword: " + credentials.getPassword());
        System.out.println("credentials.getCreated: " + credentials.getCreated());
        this.printTitleTest("Create a new Credentials resource", false);

        /*
         * Retrieve the list of Machine
         */
        // FIXME machine Collection not supported in rest interface
        // CimiMachineCollection machineCollection =
        //
        // service.path(this.extractPath(cloudEntryPoint.getMachines().getHref())).accept(mediaType)
        // .get(CimiMachineCollection.class);
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
         * Create a new Machine
         */
        this.printTitleTest("Create a new Machine", true);
        CimiMachineTemplate machineTemplate = new CimiMachineTemplate();
        machineTemplate.setMachineConfig(new CimiMachineConfiguration(machineConfiguration.getId()));
        machineTemplate.setMachineImage(new CimiMachineImage(machineImage.getId()));
        machineTemplate.setCredentials(new CimiCredentialsTemplate(credentials.getId()));

        CimiMachineCreate machineCreate = new CimiMachineCreate();
        machineCreate.setName("myMachine1");
        machineCreate.setDescription("My very first machine");
        machineCreate.setMachineTemplate(machineTemplate);

        service = webResource.path(ConstantsPath.MACHINE_PATH);
        response = this.authentication(service).accept(mediaType).entity(machineCreate, mediaType).post(ClientResponse.class);
        Assert.assertEquals(202, response.getStatus());
        CimiJob jobMachineCreate = response.getEntity(CimiJob.class);

        System.out.println("====== " + ExchangeType.Job + " for machine creation");
        System.out.println("Location: " + response.getLocation());
        System.out.println("Header: " + response.getHeaders());
        System.out.println("jobMachineCreate.ID:" + jobMachineCreate.getId());
        System.out.println("jobMachineCreate.Status:" + jobMachineCreate.getStatus());
        System.out.println("jobMachineCreate.TargetResource:" + jobMachineCreate.getTargetResource());
        this.printTitleTest("Create a new Machine", false);

        /*
         * Read and wait end of Job
         */
        this.waitForJobCompletion(webResource, jobMachineCreate.getId(), mediaType);

        /*
         * Query the Machine
         */
        this.printTitleTest("Query the Machine", true);
        service = webResource.path(this.extractPath(jobMachineCreate.getTargetResource()));
        response = this.authentication(service).accept(mediaType).get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        CimiMachine machine = response.getEntity(CimiMachine.class);

        System.out.println("====== " + ExchangeType.Machine);
        System.out.println("ID: " + machine.getId());
        System.out.println("getName: " + machine.getName());
        System.out.println("getDescription: " + machine.getDescription());
        System.out.println("getCreated: " + machine.getCreated());
        System.out.println("getUpdated: " + machine.getUpdated());
        System.out.println("State: " + machine.getState());
        System.out.println("getCpu: " + machine.getCpu());
        System.out.println("getMemory: " + machine.getMemory());
        if ((null != machine.getDisks()) && (null != machine.getDisks().getCollection())) {
            for (CimiMachineDisk disk : machine.getDisks().getCollection()) {
                System.out.println("disk.getCapacity: " + disk.getCapacity());
            }
        }
        for (CimiOperation operation : machine.getOperations()) {
            System.out.println("operation: " + operation.getRel() + ", " + operation.getHref());
        }
        this.printTitleTest("Query the Machine", false);

        /*
         * Start the Machine
         */
        this.printTitleTest("Start the Machine", true);
        CimiAction actionStart = new CimiAction();
        actionStart.setAction(ActionType.START.getPath());
        service = webResource.path(this.extractPath(machine.getId()));
        response = this.authentication(service).accept(mediaType).entity(actionStart, mediaType).post(ClientResponse.class);
        Assert.assertEquals(202, response.getStatus());
        CimiJob jobMachineStart = response.getEntity(CimiJob.class);
        this.printTitleTest("Start the Machine", false);

        /*
         * Read and wait end of Job
         */
        this.waitForJobCompletion(webResource, jobMachineStart.getId(), mediaType);

        /*
         * Query the Machine to verify if it started
         */
        this.printTitleTest("Query the Machine to verify if it started", true);
        service = webResource.path(this.extractPath(jobMachineCreate.getTargetResource()));
        response = this.authentication(service).accept(mediaType).get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        machine = response.getEntity(CimiMachine.class);
        Assert.assertEquals("STARTED", machine.getState());

        System.out.println("====== " + ExchangeType.Machine);
        System.out.println("ID: " + machine.getId());
        System.out.println("getName: " + machine.getName());
        System.out.println("getDescription: " + machine.getDescription());
        System.out.println("getCreated: " + machine.getCreated());
        System.out.println("getUpdated: " + machine.getUpdated());
        System.out.println("State: " + machine.getState());
        this.printTitleTest("Query the Machine to verify if it started", false);

        /*
         * Stop the Machine
         */
        this.printTitleTest("Stop the Machine", true);
        CimiAction actionStop = new CimiAction();
        actionStop.setAction(ActionType.STOP.getPath());
        service = webResource.path(this.extractPath(machine.getId()));
        response = this.authentication(service).accept(mediaType).entity(actionStop, mediaType).post(ClientResponse.class);
        Assert.assertEquals(202, response.getStatus());
        CimiJob jobMachineStop = response.getEntity(CimiJob.class);
        this.printTitleTest("Stop the Machine", false);

        /*
         * Read and wait end of Job
         */
        this.waitForJobCompletion(webResource, jobMachineStop.getId(), mediaType);

        /*
         * Query the Machine to verify if it stopped
         */
        this.printTitleTest("Query the Machine to verify if it stopped", true);
        service = webResource.path(this.extractPath(jobMachineCreate.getTargetResource()));
        response = this.authentication(service).accept(mediaType).get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        machine = response.getEntity(CimiMachine.class);
        Assert.assertEquals("STOPPED", machine.getState());

        System.out.println("====== " + ExchangeType.Machine);
        System.out.println("ID: " + machine.getId());
        System.out.println("getName: " + machine.getName());
        System.out.println("getDescription: " + machine.getDescription());
        System.out.println("getCreated: " + machine.getCreated());
        System.out.println("getUpdated: " + machine.getUpdated());
        System.out.println("State: " + machine.getState());
        this.printTitleTest("Query the Machine to verify if it stopped", false);

        /*
         * Update the Machine's name and description
         */
        this.printTitleTest("Update the Machine's name and description", true);
        CimiMachine machineToUpdate = new CimiMachine();
        machineToUpdate.setName("myMachine1Updated");
        service = webResource.path(this.extractPath(machine.getId())).queryParam(Constants.PARAM_CIMI_SELECT, "name")
            .queryParam(Constants.PARAM_CIMI_SELECT, "description");
        response = this.authentication(service).accept(mediaType).entity(machineToUpdate, mediaType).put(ClientResponse.class);
        Assert.assertEquals(202, response.getStatus());
        CimiJob jobMachineUpdate = response.getEntity(CimiJob.class);
        this.printTitleTest("Update the Machine's name and description", false);

        /*
         * Read and wait end of Job
         */
        this.waitForJobCompletion(webResource, jobMachineUpdate.getId(), mediaType);

        /*
         * Query the Machine to verify if it updated
         */
        this.printTitleTest("Query the Machine to verify if it updated", true);
        service = webResource.path(this.extractPath(jobMachineCreate.getTargetResource()));
        response = this.authentication(service).accept(mediaType).get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        machine = response.getEntity(CimiMachine.class);
        Assert.assertEquals("myMachine1Updated", machine.getName());

        System.out.println("====== " + ExchangeType.Machine);
        System.out.println("ID: " + machine.getId());
        System.out.println("getName: " + machine.getName());
        System.out.println("getDescription: " + machine.getDescription());
        System.out.println("getCreated: " + machine.getCreated());
        System.out.println("getUpdated: " + machine.getUpdated());
        System.out.println("State: " + machine.getState());
        this.printTitleTest("Query the Machine to verify if it updated", false);
    }

    @Test
    public void testBasicAuthenticationBad() throws Exception {
        ClientResponse response;
        // Init data
        this.initDatabase();

        // Init client REST
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(config);
        client.addFilter(new LoggingFilter());
        WebResource webResource = client.resource(this.baseURI);
        WebResource service = null;

        // GET : None Authentication
        this.printTitleTest("GET: None Authentication", true);
        service = webResource.path(ConstantsPath.CLOUDENTRYPOINT_PATH);
        response = service.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        Assert.assertEquals(401, response.getStatus());
        this.printTitleTest("GET: None Authentication", false);

        // POST : None Authentication
        this.printTitleTest("POST: None Authentication", true);
        CimiMachineTemplate machineTemplate = new CimiMachineTemplate();
        CimiMachineCreate machineCreate = new CimiMachineCreate();
        machineCreate.setMachineTemplate(machineTemplate);

        service = webResource.path(ConstantsPath.MACHINE_PATH);
        response = service.accept(MediaType.APPLICATION_XML_TYPE).entity(machineCreate, MediaType.APPLICATION_XML_TYPE)
            .post(ClientResponse.class);
        Assert.assertEquals(401, response.getStatus());
        this.printTitleTest("POST: None Authentication", false);

        // PUT : None Authentication
        this.printTitleTest("PUT: None Authentication", true);
        service = webResource.path(ConstantsPath.MACHINE_PATH);
        response = service.accept(MediaType.APPLICATION_XML_TYPE).entity(new CimiMachine(), MediaType.APPLICATION_XML_TYPE)
            .put(ClientResponse.class);
        Assert.assertEquals(401, response.getStatus());
        this.printTitleTest("PUT: None Authentication", false);

        // DELETE : None Authentication
        this.printTitleTest("DELETE: None Authentication", true);
        service = webResource.path(ConstantsPath.MACHINE_PATH + "/1235");
        response = service.accept(MediaType.APPLICATION_XML_TYPE).delete(ClientResponse.class);
        Assert.assertEquals(401, response.getStatus());
        this.printTitleTest("DELETE: None Authentication", false);

        // Bad Basic Authentication : user and password unknown
        this.printTitleTest("Bad Basic Authentication : user and password unknown", true);
        service = webResource.path(ConstantsPath.CLOUDENTRYPOINT_PATH);
        response = this.authentication(service, "foo", "foo").accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        Assert.assertEquals(401, response.getStatus());
        this.printTitleTest("Bad Basic Authentication : user and password unknown", false);

        // Bad Basic Authentication : password unknown
        this.printTitleTest("Bad Basic Authentication : password unknown", true);
        service = webResource.path(ConstantsPath.CLOUDENTRYPOINT_PATH);
        response = this.authentication(service, RestCimiPrimerScenarioTest.USER_NAME, "foo")
            .accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        Assert.assertEquals(401, response.getStatus());
        this.printTitleTest("Bad Basic Authentication : password unknown", false);

    }

    @Test
    public void testScenarioOneJson() throws Exception {
        this.runScenarioOne(MediaType.APPLICATION_JSON_TYPE);
    }

    @Test
    public void testScenarioOneXml() throws Exception {
        this.runScenarioOne(MediaType.APPLICATION_XML_TYPE);
    }

}
