package org.ow2.sirocco.cloudmanager.itests;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteSystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;
import org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

public class CloudProviderManagerTest {

    private static final String INITIAL_CONTEXT_FACTORY = "org.ow2.carol.jndi.spi.MultiOrbInitialContextFactory";
    private static final Integer INITIALIZE_TIMEOUT = 30;

    // private static final String INITIAL_CONTEXT_FACTORY2
    // ="com.sun.jndi.rmi.registry.RegistryContextFactory";

    IRemoteCloudProviderManager cpm;
    IRemoteUserManager um;

    @Before
    public void setUp() throws Exception {

        setupEJB();
        //setupDatabase();
    }

    private void setupEJB() throws Exception {

        String carolPortString = "1099";//System.getProperty("carol.port");
        Assert.assertNotNull("carol.port not set!", carolPortString);

        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.SECURITY_PRINCIPAL, "guest");
        env.put(Context.SECURITY_CREDENTIALS, "guest");
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                CloudProviderManagerTest.INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, "rmi://localhost:" + carolPortString);
        final long timeout = System.currentTimeMillis()
                + CloudProviderManagerTest.INITIALIZE_TIMEOUT * 1000;
        while (true) {
            try {
                Context context = new InitialContext(env);
                NamingEnumeration<NameClassPair> ns= context.list("");// .lookup(IRemoteUserManager.EJB_JNDI_NAME);
                
                while (ns.hasMoreElements())
                {
                    NameClassPair nss=ns.next();
                    System.out.println(nss.toString());
                }
                Object toto= (Object) context
                        .lookup(IRemoteCloudProviderManager.EJB_JNDI_NAME);
                IRemoteCloudProviderManager cloudProviderManager=(org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager)toto;
                this.um = (IRemoteUserManager) context
                        .lookup(IRemoteUserManager.EJB_JNDI_NAME);
                
                toto= (Object) context
                        .lookup(IRemoteSystemManager.EJB_JNDI_NAME);
                IRemoteSystemManager systemManager=(org.ow2.sirocco.cloudmanager.core.api.IRemoteSystemManager)toto;

                toto= (Object) context
                        .lookup(IRemoteMachineManager.EJB_JNDI_NAME);
                IRemoteMachineManager machineManager=(org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager)toto;

                toto= (Object) context
                        .lookup(IRemoteUserManager.EJB_JNDI_NAME);
                IRemoteUserManager userManager=(org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager)toto;

                toto= (Object) context
                        .lookup(IRemoteCloudProviderManager.EJB_JNDI_NAME);
                IRemoteCloudProviderManager cpManager=(org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager)toto;

                toto= (Object) context
                        .lookup(IRemoteMachineImageManager.EJB_JNDI_NAME);
                IRemoteMachineImageManager machineImageManager=(org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineImageManager)toto;
                
                
                
                //CloudProvider cp=titi.createCloudProvider("bibi", "blabla");
                
                User user = userManager.createUser("Lov", "Maps", "lov@maps.com", "username6", "232908Ivry");

                CloudProvider provider = cpManager.createCloudProvider("mock", "mock");
                CloudProviderAccount account = cpManager.createCloudProviderAccount(provider.getId().toString(),"","");

                cpManager.addCloudProviderAccountToUser(user.getId().toString(), account.getId().toString());

                MachineConfiguration in_c = new MachineConfiguration();
                in_c.setName("testConfig_" + UUID.randomUUID());
                in_c.setDescription("testConfig_" + 1 + " description");
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
                    dt.setAttachmentPoint("/dev/sd" + i);
                    dTemplates.add(dt);
                }
                in_c.setCpu(cpu);
                in_c.setMemory(mem);
                in_c.setDiskTemplates(dTemplates);
                
                MachineImage mimage = new MachineImage();
                mimage.setName("image_" + 1);
                mimage.setDescription("image description " + 1);
                mimage.setImageLocation("http://example.com/images/WinXP-SP2" + 1);
                
                Job out_j = machineImageManager.createMachineImage(mimage);
                

                //titi2.getEntityFromId(cp.getId().toString());
                
                

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

   /* private void setupDatabase() throws Exception {
        PropertiesBasedJdbcDatabaseTester databaseTest;
        Reader reader = new FileReader(new File(
                System.getProperty("dbunit.dataset")));
        XmlDataSet dataSet = new XmlDataSet(reader);
        databaseTest = new PropertiesBasedJdbcDatabaseTester();
        databaseTest.setDataSet(dataSet);
        databaseTest.setSetUpOperation(DatabaseOperation.DELETE_ALL);
        databaseTest.onSetup();
    }*/

    private CloudProviderLocation createCPL(String iso3166_1, String iso3166_2,
            String postalCode, Double GPS_Altitude, Double GPS_Latitude,
            Double GPS_Longitude, String countryName, String stateName,
            String cityName) throws CloudProviderException {

        CloudProviderLocation lcpl = cpm.createCloudProviderLocation(iso3166_1,
                iso3166_2, postalCode, GPS_Altitude, GPS_Latitude,
                GPS_Longitude, countryName, stateName, cityName);

        // create ok?
        Assert.assertNotNull(lcpl);
        Assert.assertEquals(iso3166_1, lcpl.getIso3166_1());
        Assert.assertEquals(iso3166_2, lcpl.getIso3166_2());
        Assert.assertEquals(postalCode, lcpl.getPostalCode());
        Assert.assertEquals(GPS_Altitude, lcpl.getGPS_Altitude());
        Assert.assertEquals(GPS_Latitude, lcpl.getGPS_Latitude());
        Assert.assertEquals(GPS_Longitude, lcpl.getGPS_Latitude());
        Assert.assertEquals(countryName, lcpl.getCountryName());
        Assert.assertEquals(stateName, lcpl.getStateName());
        Assert.assertEquals(cityName, lcpl.getCityName());

        return lcpl;
    }

    private CloudProvider createCP(String cpt, String desc)
            throws CloudProviderException {

        CloudProvider lcp = cpm.createCloudProvider(cpt, desc);
        // create ok?
        Assert.assertNotNull(lcp);
        Assert.assertEquals(desc, lcp.getDescription());
        Assert.assertEquals(cpt, lcp.getCloudProviderType());
        return lcp;

    }
    
    private CloudProviderAccount createCPA(String cloudProviderId, String login, String password)throws CloudProviderException{
        CloudProviderAccount cpa=cpm.createCloudProviderAccount(cloudProviderId, login, password);
        // create ok?
        Assert.assertNotNull(cpa);
        Assert.assertEquals(login, cpa.getLogin());
        Assert.assertEquals(password, cpa.getLogin());
        Assert.assertEquals(cloudProviderId, cpa.getCloudProvider().getId());
        return cpa;
        
    }

    @Test
    public void testProvider() throws Exception {

        CloudProvider cp1 = createCP("mock", "desccp1");
        CloudProviderLocation cpl1 = createCPL("FR", "FR-55", "55000", 280.0,
                48.771, 5.173, "France", "Meuse", "Bar-le-Duc");
        CloudProviderLocation cpl2 = createCPL("FR", "FR-55", "55100", 200.0,
                49.164, 5.392, "France", "Meuse", "Verdun");
        CloudProviderAccount cpa1 = createCPA(cp1.getId().toString(), "jean.dujardin", "password");
        CloudProviderAccount cpa2 = createCPA(cp1.getId().toString(), "bruno.salomone", "passwort2");
        
        Double dist=cpm.locationDistance(cpl1, cpl2);
        
        

        HashMap<String, Object> attrs1 = new HashMap<String, Object>();
        attrs1.put("cloudProviderType", "typecp1-updated");
        cp1 = cpm.updateCloudProvider(cp1.getId().toString(), attrs1);
        System.out.println("cp1 type: " + cp1.getCloudProviderType());

        HashMap<String, Object> attrs2 = new HashMap<String, Object>();
        attrs2.put("countryName", "Frankreich");
        attrs2.put("stateName", "Lothringen");
        Set<CloudProvider> sett = cpl1.getCloudProviders();
        if (sett == null) {
            sett = new HashSet<CloudProvider>();
        }
        sett.add(cp1);
        attrs2.put("cloudProviders", sett);
        cpl1 = cpm.updateCloudProviderLocation(cpl1.getId().toString(), attrs2);
        System.out.println("cp1 type: " + cp1.getCloudProviderType());
    }

    public void testJob() throws Exception {

        // IJobManager jm=(IJobManager)
        // context.lookup(IRemoteJobManager.EJB_JNDI_NAME);
        // ICloudProviderManager cpm=(ICloudProviderManager)
        // context.lookup(ICloudProviderManager.EJB_JNDI_NAME);
        // cpm.createCloudProvider("type1", "desc");

        // Job parent=jm.createJob("pere", "http://",null);
        // parent.setTargetEntity(parent.getTargetEntity()+"-"+parent.getId().toString());
        // parent=jm.updateJob(parent);
        // System.out.println("created job "+parent.getTargetEntity());
        // String parentId=parent.getId().toString();
        // Job fiston=jm.createJob("fils", "http://", parentId);
        // fiston.setTargetEntity(fiston.getTargetEntity()+"-"+fiston.getId().toString());
        // fiston=jm.updateJob(fiston);
        // System.out.println("created job "+fiston.getTargetEntity());

        // parent=jm.getJobById(parentId);

        // java.util.List l=parent.getNestedJobs();
        // System.out.println("list nested: "+((Job)l.get(0)).getTargetEntity());
    }

    @After
    public void tearDown() throws Exception {
    }

}
