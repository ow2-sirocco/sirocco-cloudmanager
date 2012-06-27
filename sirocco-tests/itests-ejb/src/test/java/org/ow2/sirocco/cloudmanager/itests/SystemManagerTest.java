package org.ow2.sirocco.cloudmanager.itests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.itests.util.SiroccoTester;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;
import org.ow2.sirocco.cloudmanager.model.cimi.system.ComponentDescriptor;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.ComponentDescriptor.ComponentType;

@SuppressWarnings("unused")
public class SystemManagerTest extends SiroccoTester {

    @Override
    @Before
    public void setUp() throws Exception {

        this.setUpDatabase();
        this.connectToCloudManager();

        // change password that is not validated by user manager
        User user = this.userManager.createUser("Jeanne", "Calmant", "jeanne.calmant@vieux.com", "ANONYMOUS", "titigrosminet");
        CloudProvider provider = this.cloudProviderManager.createCloudProvider("mock", "mock");
        CloudProviderAccount account = this.cloudProviderManager.createCloudProviderAccount(provider.getId().toString(),
            "ignored", "machinetest");
        this.cloudProviderManager.addCloudProviderAccountToUser(user.getId().toString(), account.getId().toString());

    }

    @Test
    public void testSystemManager() throws Exception {

        // User user = userManager.createUser("Jeanne", "Calmant",
        // "jeanne.calmant@vieux.com", "jeanne.calmant", "titigrosminet");

        // creating machine template
        MachineTemplate machineTemplate = new MachineTemplate();
        MachineManagerTest machineTest = new MachineManagerTest();

        MachineConfiguration in_c = machineTest.initMachineConfiguration();
        MachineConfiguration out_c = this.machineManager.createMachineConfiguration(in_c);

        MachineConfiguration machineConfig = out_c;
        machineTemplate.setMachineConfiguration(machineConfig);

        MachineImage in_i = machineTest.initMachineImage();
        Job out_j = this.machineImageManager.createMachineImage(in_i);

        machineTemplate.setMachineImage((MachineImage) out_j.getTargetEntity());

        Credentials out_cr = this.credManager.createCredentials(machineTest.initCredentials());

        machineTemplate.setCredentials(out_cr);
        machineTemplate.setVolumes(new ArrayList<MachineVolume>());
        machineTemplate.setVolumeTemplates(new ArrayList<MachineVolumeTemplate>());
        machineTemplate.setNetworkInterfaces(new ArrayList<MachineTemplateNetworkInterface>());

        // machineTemplate=machineManager.createMachineTemplate(machineTemplate);

        ComponentDescriptor component1 = new ComponentDescriptor();
        component1.setName("MaMachine");
        component1.setComponentQuantity(2);
        component1.setComponentType(ComponentType.MACHINE);
        component1.setDescription("desc-comp");
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("testProp", "testPropValue");
        component1.setComponentProperties(map1);
        component1.setComponentTemplate(machineTemplate);

        ComponentDescriptor component2 = new ComponentDescriptor();
        component2.setName("MaMachineBisque");
        component2.setComponentQuantity(3);
        component2.setComponentType(ComponentType.MACHINE);
        component2.setDescription("desc-comp2");
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("testProp", "testPropValue2");
        component2.setComponentProperties(map2);
        component2.setComponentTemplate(machineTemplate);

        HashSet<ComponentDescriptor> componentDescriptors1 = new HashSet<ComponentDescriptor>();

        componentDescriptors1.add(component1);

        HashSet<ComponentDescriptor> componentDescriptors2 = new HashSet<ComponentDescriptor>();
        componentDescriptors2.add(component2);

        SystemTemplate systemTemplate1 = new SystemTemplate();

        systemTemplate1.setDescription("descr-st1");
        systemTemplate1.setName("systemTemplateTest1");
        systemTemplate1.setComponentDescriptors(componentDescriptors1);

        SystemTemplate systemTemplate2 = new SystemTemplate();

        systemTemplate2.setDescription("descr-st2");
        systemTemplate2.setName("systemTemplateTest2");
        systemTemplate2.setComponentDescriptors(componentDescriptors2);

        SystemCreate systemCreate1 = new SystemCreate();

        systemCreate1.setDescription("descr-sc1");
        systemCreate1.setName("systemTest1");
        systemCreate1.setSystemTemplate(systemTemplate1);

        // systemTemplate2=systemManager.createSystemTemplate(systemTemplate2);

        ComponentDescriptor component3 = new ComponentDescriptor();
        component3.setName("MonSystemeBisque");
        component3.setComponentQuantity(2);
        component3.setComponentType(ComponentType.SYSTEM);
        component3.setDescription("desc-comp3");
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("testProp", "testPropValue3");
        component3.setComponentProperties(map3);
        component3.setComponentTemplate(systemTemplate2);
        componentDescriptors1.add(component3);

        // systemTemplate1=systemManager.createSystemTemplate(systemTemplate1);

        Job j = this.systemManager.createSystem(systemCreate1);
        String jobId = j.getId().toString();
        String systemId = j.getTargetEntity().getId().toString();
        int counter = 130;
        while (true) {
            j = this.jobManager.getJobById(jobId);
            if (j.getStatus() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("system operation time out");
            }
        }

        // verif
        org.ow2.sirocco.cloudmanager.model.cimi.system.System sv1 = systemManager.getSystemById(systemId);

        Assert.assertEquals(sv1.getName(), "systemTest1");
        Assert.assertEquals(sv1.getDescription(), "descr-sc1");

        Assert.assertEquals(sv1.getMachines().size(), 2);
        Assert.assertEquals(sv1.getMachines().get(0).getName(), "MaMachine0");
        Assert.assertEquals(sv1.getMachines().get(0).getDescription(), "desc-comp");
        // Assert.assertEquals(sv1.getMachines().get(0).getCpu().getCpuSpeedUnit(),Cpu.Frequency.GIGA);
        Assert.assertEquals(sv1.getMachines().get(1).getName(), "MaMachine1");
        Assert.assertEquals(sv1.getMachines().get(1).getDescription(), "desc-comp");

        Assert.assertEquals(sv1.getSystems().size(), 2);
        org.ow2.sirocco.cloudmanager.model.cimi.system.System s1 = systemManager.getSystemById(sv1.getSystems().get(0).getId()
            .toString());
        Assert.assertEquals(s1.getName(), "MonSystemeBisque0");
        Assert.assertEquals(s1.getDescription(), "desc-comp3");
        Assert.assertEquals(s1.getMachines().size(), 3);
        Assert.assertEquals(s1.getMachines().get(0).getName(), "MaMachineBisque0");
        Assert.assertEquals(s1.getMachines().get(0).getDescription(), "desc-comp2");
        Assert.assertEquals(s1.getMachines().get(1).getName(), "MaMachineBisque1");
        Assert.assertEquals(s1.getMachines().get(1).getDescription(), "desc-comp2");
        Assert.assertEquals(s1.getMachines().get(2).getName(), "MaMachineBisque2");
        Assert.assertEquals(s1.getMachines().get(2).getDescription(), "desc-comp2");
        org.ow2.sirocco.cloudmanager.model.cimi.system.System s2 = systemManager.getSystemById(sv1.getSystems().get(1).getId()
            .toString());
        Assert.assertEquals(s2.getName(), "MonSystemeBisque1");
        Assert.assertEquals(s2.getDescription(), "desc-comp3");
        Assert.assertEquals(s2.getMachines().size(), 3);

        j = systemManager.startSystem(systemId);
        jobId = j.getId().toString();
        counter = 130;

        while (true) {
            j = this.jobManager.getJobById(jobId);
            if (j.getStatus() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("system operation time out");
            }
        }

        sv1 = this.systemManager.getSystemById(systemId);
        Assert.assertEquals(sv1.getState(), System.State.STARTED);
        Assert.assertEquals(sv1.getSystems().get(0).getState(), System.State.STARTED);
        Assert.assertEquals(sv1.getSystems().get(1).getState(), System.State.STARTED);
        Assert.assertEquals(sv1.getMachines().get(0).getState(), Machine.State.STARTED);
        Assert.assertEquals(sv1.getMachines().get(1).getState(), Machine.State.STARTED);
        s1 = this.systemManager.getSystemById(sv1.getSystems().get(0).getId().toString());
        Assert.assertEquals(s1.getMachines().get(0).getState(), Machine.State.STARTED);
        Assert.assertEquals(s1.getMachines().get(1).getState(), Machine.State.STARTED);
        Assert.assertEquals(s1.getMachines().get(2).getState(), Machine.State.STARTED);
        s2 = this.systemManager.getSystemById(sv1.getSystems().get(1).getId().toString());
        Assert.assertEquals(s2.getMachines().get(0).getState(), Machine.State.STARTED);
        Assert.assertEquals(s2.getMachines().get(1).getState(), Machine.State.STARTED);
        Assert.assertEquals(s2.getMachines().get(2).getState(), Machine.State.STARTED);

        j = this.systemManager.stopSystem(systemId);
        jobId = j.getId().toString();
        counter = 130;
        while (true) {
            j = this.jobManager.getJobById(jobId);
            if (j.getStatus() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("system operation time out");
            }
        }

        sv1 = this.systemManager.getSystemById(systemId);
        Assert.assertEquals(sv1.getState(), System.State.STOPPED);
        Assert.assertEquals(sv1.getSystems().get(0).getState(), System.State.STOPPED);
        Assert.assertEquals(sv1.getSystems().get(1).getState(), System.State.STOPPED);
        Assert.assertEquals(sv1.getMachines().get(0).getState(), Machine.State.STOPPED);
        Assert.assertEquals(sv1.getMachines().get(1).getState(), Machine.State.STOPPED);
        s1 = this.systemManager.getSystemById(sv1.getSystems().get(0).getId().toString());
        Assert.assertEquals(s1.getMachines().get(0).getState(), Machine.State.STOPPED);
        Assert.assertEquals(s1.getMachines().get(1).getState(), Machine.State.STOPPED);
        Assert.assertEquals(s1.getMachines().get(2).getState(), Machine.State.STOPPED);
        s2 = this.systemManager.getSystemById(sv1.getSystems().get(1).getId().toString());
        Assert.assertEquals(s2.getMachines().get(0).getState(), Machine.State.STOPPED);
        Assert.assertEquals(s2.getMachines().get(1).getState(), Machine.State.STOPPED);
        Assert.assertEquals(s2.getMachines().get(2).getState(), Machine.State.STOPPED);

        // some manipulations
        // this.systemManager.removeMachineFromSystem(sv1.getMachines().get(1).getId().toString(),
        // systemId);
        // this.systemManager.removeSystemFromSystem(sv1.getSystems().get(0).getId().toString(),
        // systemId);
        // sv1 = this.systemManager.getSystemById(systemId);

        // Assert.assertEquals(sv1.getMachines().size(), 1);
        // Assert.assertEquals(sv1.getSystems().size(), 1);

        j = this.systemManager.deleteSystem(systemId);
        jobId = j.getId().toString();
        counter = 130;
        while (true) {
            j = this.jobManager.getJobById(jobId);
            if (j.getStatus() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("system operation time out");
            }
        }

        try {
            sv1 = this.systemManager.getSystemById(systemId);
        } catch (ResourceNotFoundException e) {
            sv1 = null;
        }

        Assert.assertNull(sv1);

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
                java.lang.System.out.println("start thread");
                // lockedID=jobManager.lock(jo.getId().toString());
                java.lang.System.out.println("1-job " + this.jo.getId().toString() + " locked with ID " + lockedID + "!");
                this.jo = SystemManagerTest.this.jobManager.getJobById(this.jo.getId().toString());
                java.lang.System.out.println("1-job date " + this.jo.getLockedTime());
            } catch (Exception e) {

                java.lang.System.out.println("1-job " + this.jo.getId().toString() + " not locked!");
                java.lang.System.out.println("1-Exception " + e.getClass().getName());
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

    // @Tvcvest
    public void testJob() throws Exception {

        java.lang.System.out.println("start");

        Job j = this.jobManager.createJob(null, "bob", null);

        new bob(j).start();
        Thread.sleep(12000);
        try {
            // jobManager.unlock(j.getId().toString(),"fsdfsdfds");
        } catch (Exception e) {

            java.lang.System.out.println("2-job " + j.getId().toString() + " not unlocked!");
            java.lang.System.out.println("2-Exception " + e.getClass().getName());
        }
        String lockedID = "";

        try {
            // lockedID=jobManager.lock(j.getId().toString());
            j = this.jobManager.getJobById(j.getId().toString());
            java.lang.System.out.println("2-job " + j.getId().toString() + " locked with ID " + lockedID + "!and date "
                + j.getLockedTime());
        } catch (Exception e) {

            java.lang.System.out.println("2-job " + j.getId().toString() + " not locked!");
            java.lang.System.out.println("2-Exception " + e.getClass().getName());
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
    }

}
