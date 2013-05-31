package org.ow2.sirocco.cloudmanager.itests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@SuppressWarnings("unused")
public class CloudProviderManagerTest extends AbstractTestBase {

    private CloudProviderLocation createCPL(final String iso3166_1, final String iso3166_2, final String postalCode,
        final Double GPS_Altitude, final Double GPS_Latitude, final Double GPS_Longitude, final String countryName,
        final String stateName, final String cityName) throws CloudProviderException {

        CloudProviderLocation lcpl = this.cloudProviderManager.createCloudProviderLocation(iso3166_1, iso3166_2, postalCode,
            GPS_Altitude, GPS_Latitude, GPS_Longitude, countryName, stateName, cityName);

        // create ok?
        Assert.assertNotNull(lcpl);
        Assert.assertEquals(iso3166_1.toUpperCase(), lcpl.getIso3166_1());
        Assert.assertEquals(iso3166_2.toUpperCase(), lcpl.getIso3166_2());
        Assert.assertEquals(postalCode.toUpperCase(), lcpl.getPostalCode());
        Assert.assertEquals(GPS_Altitude, lcpl.getGPS_Altitude());
        Assert.assertEquals(GPS_Latitude, lcpl.getGPS_Latitude());
        Assert.assertEquals(GPS_Longitude, lcpl.getGPS_Longitude());
        Assert.assertEquals(countryName.toUpperCase(), lcpl.getCountryName().toUpperCase());
        Assert.assertEquals(stateName.toUpperCase(), lcpl.getStateName().toUpperCase());
        Assert.assertEquals(cityName.toUpperCase(), lcpl.getCityName().toUpperCase());

        return lcpl;
    }

    private CloudProvider createCP(final String cpt, final String desc) throws CloudProviderException {

        CloudProvider lcp = this.cloudProviderManager.createCloudProvider(cpt, desc);
        // create ok?
        Assert.assertNotNull(lcp);
        Assert.assertEquals(desc, lcp.getDescription());
        Assert.assertEquals(cpt, lcp.getCloudProviderType());
        return lcp;

    }

    private CloudProviderAccount createCPA(final String cloudProviderId, final String login, final String password)
        throws CloudProviderException {
        CloudProviderAccount cpa = new CloudProviderAccount();
        cpa.setLogin(login);
        cpa.setPassword(password);
        cpa = this.cloudProviderManager.createCloudProviderAccount(cloudProviderId, cpa);
        // create ok?
        Assert.assertNotNull(cpa);
        Assert.assertEquals(login, cpa.getLogin());
        Assert.assertEquals(password, cpa.getPassword());
        Assert.assertEquals(cloudProviderId, cpa.getCloudProvider().getId().toString());
        return cpa;

    }

    @Test
    public void testCloudProviderManager() throws Exception {

        User user = this.userManager.createUser("Jeanne", "Calmant", "jeanne.calmant@vieux.com", "jeanne.calmant",
            "titigrosminet");

        // create/get tests
        CloudProvider cp1 = this.createCP("mock", "desccp1");
        Assert.assertEquals(cp1.getId(), this.cloudProviderManager.getCloudProviderById(cp1.getId().toString()).getId());
        CloudProviderLocation cpl1 = this.createCPL("FR", "FR-55", "55000", 280.0, 48.771, 5.173, "France", "Meuse",
            "Bar-le-Duc");
        Assert.assertEquals(cpl1.getId(), this.cloudProviderManager.getCloudProviderLocationById(cpl1.getId().toString())
            .getId());

        CloudProviderLocation cpl2 = this.createCPL("FR", "FR-55", "55100", 200.0, 49.164, 5.392, "France", "Meuse", "Verdun");
        Assert.assertEquals(cpl2.getId(), this.cloudProviderManager.getCloudProviderLocationById(cpl2.getId().toString())
            .getId());

        CloudProviderAccount cpa1 = this.createCPA(cp1.getId().toString(), "jean.dujardin", "passwort");
        Assert.assertEquals(cpa1.getId(), this.cloudProviderManager.getCloudProviderAccountById(cpa1.getId().toString())
            .getId());

        CloudProviderAccount cpa2 = this.createCPA(cp1.getId().toString(), "bruno.salomone", "passwort2");
        Assert.assertEquals(cpa2.getId(), this.cloudProviderManager.getCloudProviderAccountById(cpa2.getId().toString())
            .getId());

        // distance
        Double dist = this.cloudProviderManager.locationDistance(cpl1, cpl2);
        Assert.assertEquals(46495, dist.longValue());

        // add user
        // this.cloudProviderManager.addCloudProviderAccountToUserByName("jeanne.calmant",
        // cpa1.getId().toString());
        // cpa1 =
        // this.cloudProviderManager.getCloudProviderAccountById(cpa1.getId().toString());
        // Assert.assertEquals(user.getUsername(), ((User)
        // (cpa1.getUsers().toArray()[0])).getUsername());

        // remove user
        // this.cloudProviderManager.removeCloudProviderAccountFromUserByName("jeanne.calmant",
        // cpa1.getId().toString());
        // cpa1 =
        // this.cloudProviderManager.getCloudProviderAccountById(cpa1.getId().toString());
        // Assert.assertEquals(0, cpa1.getUsers().size());

        // update
        HashMap<String, Object> attrs1 = new HashMap<String, Object>();
        attrs1.put("cloudProviderType", "typecp1-updated");
        cp1 = this.cloudProviderManager.updateCloudProvider(cp1.getId().toString(), attrs1);
        Assert.assertEquals("typecp1-updated", cp1.getCloudProviderType());

        HashMap<String, Object> attrs2 = new HashMap<String, Object>();
        attrs2.put("countryName", "Frankreich");
        attrs2.put("stateName", "Lothringen");
        Set<CloudProvider> sett = cpl1.getCloudProviders();
        if (sett == null) {
            sett = new HashSet<CloudProvider>();
        }
        sett.add(cp1);
        attrs2.put("cloudProviders", sett);
        cpl1 = this.cloudProviderManager.updateCloudProviderLocation(cpl1.getId().toString(), attrs2);

        Assert.assertEquals("FRANKREICH", cpl1.getCountryName());
        Assert.assertEquals("LOTHRINGEN", cpl1.getStateName());

        HashMap<String, Object> attrs3 = new HashMap<String, Object>();
        attrs3.put("login", "cyril.auboin");
        attrs3.put("password", "passepasse");

        cpa1 = this.cloudProviderManager.updateCloudProviderAccount(cpa1.getId().toString(), attrs3);
        Assert.assertEquals("cyril.auboin", cpa1.getLogin());
        Assert.assertEquals("passepasse", cpa1.getPassword());

        // delete

        Assert.assertNotNull(this.cloudProviderManager.getCloudProviderLocationById(cpl1.getId().toString()));
        this.cloudProviderManager.deleteCloudProviderLocation(cpl1.getId().toString());
        Assert.assertNull(this.cloudProviderManager.getCloudProviderLocationById(cpl1.getId().toString()));
        Assert.assertNotNull(this.cloudProviderManager.getCloudProviderLocationById(cpl2.getId().toString()));
        this.cloudProviderManager.deleteCloudProviderLocation(cpl2.getId().toString());
        Assert.assertNull(this.cloudProviderManager.getCloudProviderLocationById(cpl2.getId().toString()));

        Assert.assertNotNull(this.cloudProviderManager.getCloudProviderAccountById(cpa1.getId().toString()));
        this.cloudProviderManager.deleteCloudProviderAccount(cpa1.getId().toString());
        Assert.assertNull(this.cloudProviderManager.getCloudProviderAccountById(cpa1.getId().toString()));
        Assert.assertNotNull(this.cloudProviderManager.getCloudProviderAccountById(cpa2.getId().toString()));
        this.cloudProviderManager.deleteCloudProviderAccount(cpa2.getId().toString());
        Assert.assertNull(this.cloudProviderManager.getCloudProviderAccountById(cpa2.getId().toString()));

        Assert.assertNotNull(this.cloudProviderManager.getCloudProviderById(cp1.getId().toString()));
        this.cloudProviderManager.deleteCloudProvider(cp1.getId().toString());
        Assert.assertNull(this.cloudProviderManager.getCloudProviderById(cp1.getId().toString()));

    }

    class bob extends Thread {
        Job jo;

        bob(final Job j) {
            this.jo = j;
        }

        @Override
        public void run() {

            String lockedID = "";
            try {
                System.out.println("start thread");
                // lockedID=jobManager.lock(jo.getId().toString());
                System.out.println("1-job " + this.jo.getId().toString() + " locked with ID " + lockedID + "!");
                this.jo = CloudProviderManagerTest.this.jobManager.getJobById(this.jo.getId().toString());
                // System.out.println("1-job date " + this.jo.getLockedTime());
            } catch (Exception e) {

                System.out.println("1-job " + this.jo.getId().toString() + " not locked!");
                System.out.println("1-Exception " + e.getClass().getName());
            }

            /*
             * try { jobManager.unLock(jo.getId().toString(),"fdfd");
             * System.out.println("1-job "+jo.getId().toString()+" unlocked!");
             * } catch (Exception e) {
             * System.out.println("1-job "+jo.getId().toString
             * ()+" not unlocked!");
             * System.out.println("1-Exception "+e.getClass().getName()); }
             */

        }

    }

    // @Tefst
    public void testJob() throws Exception {

        System.out.println("start");

        Job j = this.jobManager.createJob(null, "bob", null);

        new bob(j).start();
        Thread.sleep(12000);
        try {
            // jobManager.unlock(j.getId().toString(),"fsdfsdfds");
        } catch (Exception e) {

            System.out.println("2-job " + j.getId().toString() + " not unlocked!");
            System.out.println("2-Exception " + e.getClass().getName());
        }
        String lockedID = "";

        try {
            // lockedID=jobManager.lock(j.getId().toString());
            j = this.jobManager.getJobById(j.getId().toString());
            // System.out.println("2-job " + j.getId().toString() +
            // " locked with ID " + lockedID + "!and date "
            // + j.getLockedTime());
        } catch (Exception e) {

            System.out.println("2-job " + j.getId().toString() + " not locked!");
            System.out.println("2-Exception " + e.getClass().getName());
        }
        Thread.sleep(16000);

        // jobManager.sendJobNotification(j.getId().toString(),5000L);

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

    @Override
    @After
    public void tearDown() throws Exception {
        this.databaseManager.cleanup();
    }

}
