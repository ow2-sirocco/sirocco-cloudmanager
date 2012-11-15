package org.ow2.sirocco.cloudmanager.itests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.cloudmanager.itests.util.SiroccoTester;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;
import org.ow2.sirocco.cloudmanager.model.cimi.system.ComponentDescriptor;
import org.ow2.sirocco.cloudmanager.model.cimi.system.ComponentDescriptor.ComponentType;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemMachine;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemVolume;

@SuppressWarnings("unused")
public class SystemManagerTest extends SiroccoTester {

    static final int JOB_DELAY = 1300;

    User user = null;

    @Override
    @Before
    public void setUp() throws Exception {

        this.setUpDatabase();
        this.connectToCloudManager();

        // change password that is not validated by user manager
        this.user = this.userManager.createUser("Jeanne", "Calmant", "jeanne.calmant@vieux.com", "ANONYMOUS", "titigrosminet");
        CloudProvider provider = this.cloudProviderManager.createCloudProvider("mock", "mock");
        CloudProviderAccount account = this.cloudProviderManager.createCloudProviderAccount(provider.getId().toString(),
            "ignored", "machinetest");
        this.cloudProviderManager.addCloudProviderAccountToUser(this.user.getId().toString(), account.getId().toString());

    }

    MachineTemplate createMachineTemplate() throws Exception {

        MachineManagerTest machineTest = new MachineManagerTest();

        MachineTemplate machineTemplate = new MachineTemplate();
        MachineConfiguration in_c = machineTest.initMachineConfiguration();
        MachineConfiguration out_c = this.machineManager.createMachineConfiguration(in_c);

        MachineConfiguration machineConfig = out_c;
        machineTemplate.setMachineConfig(machineConfig);

        MachineImage in_i = machineTest.initMachineImage();
        Job out_j = this.machineImageManager.createMachineImage(in_i);

        machineTemplate.setMachineImage((MachineImage) out_j.getTargetResource());

        Credentials out_cr = this.credManager.createCredentials(machineTest.initCredentials());

        machineTemplate.setCredential(out_cr);

        Volume v = this.createVolume("testVolumeAttach");

        ArrayList<MachineVolume> vtItems = new ArrayList<MachineVolume>();
        MachineVolume mv = new MachineVolume();
        mv.setVolume(v);
        mv.setInitialLocation("/dev/sda");
        vtItems.add(mv);

        machineTemplate.setVolumes(vtItems);
        machineTemplate.setVolumeTemplates(new ArrayList<MachineVolumeTemplate>());

        MachineTemplateNetworkInterface mtnic = null;

        for (int i = 0; i < 2; i++) {
            mtnic = new MachineTemplateNetworkInterface();
            mtnic.setState(MachineTemplateNetworkInterface.InterfaceState.ACTIVE);
            List<Address> addresses = new ArrayList<Address>();
            Address addr = new Address();
            String ip = "AA.BB.CC.D" + i;
            addr.setIp(ip);
            addr.setAllocation("static");
            addresses.add(addr);
            mtnic.setAddresses(addresses);
            machineTemplate.addNetworkInterface(mtnic);
        }

        return machineTemplate;

    }

    @Test
    public void testSystemManagerConnector() throws Exception {

        this.initDatabase();

        // setting connector use for system
        this.systemManager.setConfiguration("mockConnectorImplementsSystem", true);

        java.lang.System.out.println("testing systemManager using a connector");
        this._testSystemManager();
    }

    @Test
    public void testSystemManagerStandalone() throws Exception {

        // User user = userManager.createUser("Jeanne", "Calmant",
        // "jeanne.calmant@vieux.com", "jeanne.calmant", "titigrosminet");

        this.initDatabase();

        // setting no connector use for system
        this.systemManager.setConfiguration("mockConnectorImplementsSystem", false);

        java.lang.System.out.println("testing systemManager with no connector");
        this._testSystemManager();

    }

    public void _testSystemManager() throws Exception {
        // creating machine template

        MachineManagerTest machineTest = new MachineManagerTest();
        VolumeManagerTest volumeTest = new VolumeManagerTest();

        MachineTemplate machineTemplate1 = this.createMachineTemplate();
        MachineTemplate machineTemplate2 = this.createMachineTemplate();

        // machineTemplate=machineManager.createMachineTemplate(machineTemplate);

        ComponentDescriptor component1 = new ComponentDescriptor();
        component1.setName("MaMachine");
        component1.setComponentQuantity(2);
        component1.setComponentType(ComponentType.MACHINE);
        component1.setDescription("desc-comp");
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("testProp", "testPropValue");
        component1.setProperties(map1);
        component1.setComponentTemplate(machineTemplate1);

        ComponentDescriptor component2 = new ComponentDescriptor();
        component2.setName("MaMachineBisque");
        component2.setComponentQuantity(3);
        component2.setComponentType(ComponentType.MACHINE);
        component2.setDescription("desc-comp2");
        HashMap<String, String> map2 = new HashMap<String, String>();
        map2.put("testProp", "testPropValue2");
        component2.setProperties(map2);
        component2.setComponentTemplate(machineTemplate2);

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
        HashMap<String, String> map3 = new HashMap<String, String>();
        map3.put("testProp", "testPropValue3");
        component3.setProperties(map3);
        component3.setComponentTemplate(systemTemplate2);
        componentDescriptors1.add(component3);

        // systemTemplate1=systemManager.createSystemTemplate(systemTemplate1);

        java.lang.System.out.println("creating a non trivial system");
        Job j = this.systemManager.createSystem(systemCreate1);
        Assert.assertEquals(Job.Status.SUCCESS, this.waitForJobCompletion(j));
        String systemId = j.getTargetResource().getId().toString();

        // verif
        org.ow2.sirocco.cloudmanager.model.cimi.system.System sv1 = this.systemManager.getSystemById(systemId);

        Assert.assertEquals("systemTest1", sv1.getName());
        Assert.assertEquals("descr-sc1", sv1.getDescription());

        Assert.assertEquals(2, sv1.getMachines().size());
        Assert.assertEquals("MaMachine1", sv1.getMachines().get(0).getResource().getName());
        Assert.assertEquals("desc-comp", sv1.getMachines().get(0).getResource().getDescription());
        // Assert.assertEquals(sv1.getMachines().get(0).getCpu().getCpuSpeedUnit(),Cpu.Frequency.GIGA);
        Assert.assertEquals("MaMachine2", sv1.getMachines().get(1).getResource().getName());
        Assert.assertEquals("desc-comp", sv1.getMachines().get(1).getResource().getDescription());

        Assert.assertEquals(2, sv1.getSystems().size());
        org.ow2.sirocco.cloudmanager.model.cimi.system.System s1 = this.systemManager.getSystemById(sv1.getSystems().get(0)
            .getResource().getId().toString());
        Assert.assertEquals("MonSystemeBisque1", s1.getName());
        Assert.assertEquals("desc-comp3", s1.getDescription());
        Assert.assertEquals(3, s1.getMachines().size());
        Assert.assertEquals("MaMachineBisque1", s1.getMachines().get(0).getResource().getName());
        Assert.assertEquals("desc-comp2", s1.getMachines().get(0).getResource().getDescription());
        Assert.assertEquals("MaMachineBisque2", s1.getMachines().get(1).getResource().getName());
        Assert.assertEquals("desc-comp2", s1.getMachines().get(1).getResource().getDescription());
        Assert.assertEquals("MaMachineBisque3", s1.getMachines().get(2).getResource().getName());
        Assert.assertEquals("desc-comp2", s1.getMachines().get(2).getResource().getDescription());
        org.ow2.sirocco.cloudmanager.model.cimi.system.System s2 = this.systemManager.getSystemById(sv1.getSystems().get(1)
            .getResource().getId().toString());
        Assert.assertEquals("MonSystemeBisque2", s2.getName());
        Assert.assertEquals("desc-comp3", s2.getDescription());
        Assert.assertEquals(3, s2.getMachines().size());

        // start system
        java.lang.System.out.println("start system");
        Assert.assertEquals(Job.Status.SUCCESS, this.waitForJobCompletion(this.systemManager.startSystem(systemId, null)));

        sv1 = this.systemManager.getSystemById(systemId);
        Assert.assertEquals(System.State.STARTED, sv1.getState());
        Assert.assertEquals(System.State.STARTED, ((System) sv1.getSystems().get(0).getResource()).getState());
        Assert.assertEquals(System.State.STARTED, ((System) sv1.getSystems().get(1).getResource()).getState());
        Assert.assertEquals(Machine.State.STARTED, ((Machine) sv1.getMachines().get(0).getResource()).getState());
        Assert.assertEquals(Machine.State.STARTED, ((Machine) sv1.getMachines().get(1).getResource()).getState());
        s1 = this.systemManager.getSystemById(sv1.getSystems().get(0).getResource().getId().toString());
        Assert.assertEquals(Machine.State.STARTED, ((Machine) s1.getMachines().get(0).getResource()).getState());
        Assert.assertEquals(Machine.State.STARTED, ((Machine) s1.getMachines().get(1).getResource()).getState());
        Assert.assertEquals(Machine.State.STARTED, ((Machine) s1.getMachines().get(2).getResource()).getState());
        s2 = this.systemManager.getSystemById(sv1.getSystems().get(1).getResource().getId().toString());
        Assert.assertEquals(Machine.State.STARTED, ((Machine) s2.getMachines().get(0).getResource()).getState());
        Assert.assertEquals(Machine.State.STARTED, ((Machine) s2.getMachines().get(1).getResource()).getState());
        Assert.assertEquals(Machine.State.STARTED, ((Machine) s2.getMachines().get(2).getResource()).getState());

        // playing directly with machines
        java.lang.System.out.println("playing directly with machines");
        Machine mm = (Machine) s2.getMachines().get(1).getResource();

        java.lang.System.out.println("pausing machine");
        Assert.assertEquals(Job.Status.SUCCESS,
            this.waitForJobCompletion(this.machineManager.pauseMachine(mm.getId().toString(), null)));
        // refreshing system
        s2 = this.systemManager.getSystemById(sv1.getSystems().get(1).getResource().getId().toString());
        // system state to mixed?
        Assert.assertEquals(s2.getState(), System.State.MIXED);

        // gets
        java.lang.System.out.println("testing gets");
        @SuppressWarnings("unchecked")
        List<SystemMachine> sms = (List<SystemMachine>) this.systemManager.getEntityListFromSystem(s2.getId().toString(),
            SystemMachine.class);
        Assert.assertEquals(Machine.State.STARTED, ((Machine) sms.get(0).getResource()).getState());
        Assert.assertEquals(Machine.State.PAUSED, ((Machine) sms.get(1).getResource()).getState());
        Assert.assertEquals(Machine.State.STARTED, ((Machine) sms.get(2).getResource()).getState());

        List<System> ls = this.systemManager.getSystems();
        Assert.assertEquals(3, ls.size());
        Assert.assertEquals("systemTest1", ls.get(0).getName());
        Assert.assertEquals("MonSystemeBisque1", ls.get(1).getName());
        Assert.assertEquals("MonSystemeBisque2", ls.get(2).getName());

        List<SystemTemplate> sts1 = this.systemManager.getSystemTemplates();
        int nbTemplates = sts1.size();

        java.lang.System.out.println("testing create on system templates");
        SystemTemplate systemTemplate1_1 = this.systemManager.createSystemTemplate(systemTemplate1);

        List<SystemTemplate> sts2 = this.systemManager.getSystemTemplates();
        Assert.assertEquals(nbTemplates + 2, sts2.size());

        SystemTemplate systemTemplate2_1 = this.systemManager.createSystemTemplate(systemTemplate2);
        Assert.assertEquals("systemTemplateTest1", systemTemplate1_1.getName());
        Assert.assertEquals("systemTemplateTest2", systemTemplate2_1.getName());

        java.lang.System.out.println("testing gets on system templates");
        SystemTemplate systemTemplate1_2 = this.systemManager.getSystemTemplateById(systemTemplate1_1.getId().toString());
        Assert.assertEquals("systemTemplateTest1", systemTemplate1_2.getName());
        Assert.assertEquals(this.user.getId(), systemTemplate1_2.getUser().getId());
        Iterator<ComponentDescriptor> it = systemTemplate1_2.getComponentDescriptors().iterator();
        int tot = 0;
        while (it.hasNext()) {
            ComponentDescriptor cd = it.next();
            if ("MaMachine".equals(cd.getName())) {
                tot++;
            }
            if ("MonSystemeBisque".equals(cd.getName())) {
                tot++;
            }
        }
        Assert.assertEquals(2, tot);

        List<SystemTemplate> sts = this.systemManager.getSystemTemplates();
        Assert.assertEquals(nbTemplates + 3, sts.size());

        java.lang.System.out.println("testing adding a new volume to a system");
        Volume vc = this.createVolume("testVolumeAddSys");
        SystemVolume sysVol = new SystemVolume();
        sysVol.setResource(vc);

        Assert.assertEquals(Job.Status.SUCCESS,
            this.waitForJobCompletion(this.systemManager.addEntityToSystem(s2.getId().toString(), sysVol)));
        // refreshing system
        s2 = this.systemManager.getSystemById(sv1.getSystems().get(1).getResource().getId().toString());

        Assert.assertEquals("testVolumeAddSys", s2.getVolumes().iterator().next().getResource().getName());
        //

        // stop system
        java.lang.System.out.println("stop system");
        Assert
            .assertEquals(Job.Status.SUCCESS, this.waitForJobCompletion(this.systemManager.stopSystem(systemId, false, null)));

        sv1 = this.systemManager.getSystemById(systemId);
        Assert.assertEquals(System.State.STOPPED, sv1.getState());
        Assert.assertEquals(System.State.STOPPED, ((System) sv1.getSystems().get(0).getResource()).getState());
        Assert.assertEquals(System.State.MIXED, ((System) sv1.getSystems().get(1).getResource()).getState());
        Assert.assertEquals(Machine.State.STOPPED, ((Machine) sv1.getMachines().get(0).getResource()).getState());
        Assert.assertEquals(Machine.State.STOPPED, ((Machine) sv1.getMachines().get(1).getResource()).getState());
        s1 = this.systemManager.getSystemById(sv1.getSystems().get(0).getResource().getId().toString());
        Assert.assertEquals(Machine.State.STOPPED, ((Machine) s1.getMachines().get(0).getResource()).getState());
        Assert.assertEquals(Machine.State.STOPPED, ((Machine) s1.getMachines().get(1).getResource()).getState());
        Assert.assertEquals(Machine.State.STOPPED, ((Machine) s1.getMachines().get(2).getResource()).getState());
        s2 = this.systemManager.getSystemById(sv1.getSystems().get(1).getResource().getId().toString());
        Assert.assertEquals(Machine.State.STOPPED, ((Machine) s2.getMachines().get(0).getResource()).getState());
        Assert.assertEquals(Machine.State.PAUSED, ((Machine) s2.getMachines().get(1).getResource()).getState());
        Assert.assertEquals(Machine.State.STOPPED, ((Machine) s2.getMachines().get(2).getResource()).getState());

        // some manipulations
        // this.systemManager.removeMachineFromSystem(sv1.getMachines().get(1).getId().toString(),
        // systemId);
        // this.systemManager.removeSystemFromSystem(sv1.getSystems().get(0).getId().toString(),
        // systemId);
        // sv1 = this.systemManager.getSystemById(systemId);

        // Assert.assertEquals(sv1.getMachines().size(), 1);
        // Assert.assertEquals(sv1.getSystems().size(), 1);

        // detaching volumes from machines

        java.lang.System.out.println("detaching volumes from machines");
        sv1 = this.systemManager.getSystemById(systemId);
        s1 = this.systemManager.getSystemById(sv1.getSystems().get(0).getResource().getId().toString());
        s2 = this.systemManager.getSystemById(sv1.getSystems().get(1).getResource().getId().toString());

        for (SystemMachine m : sv1.getMachines()) {
            Machine ma = (Machine) m.getResource();
            for (MachineVolume mv : ma.getVolumes()) {
                Assert.assertEquals(Job.Status.SUCCESS, this.waitForJobCompletion(this.machineManager.removeVolumeFromMachine(
                    ma.getId().toString(), mv.getId().toString())));
            }
        }
        for (SystemMachine m : s1.getMachines()) {
            Machine ma = (Machine) m.getResource();
            for (MachineVolume mv : ma.getVolumes()) {
                Assert.assertEquals(Job.Status.SUCCESS, this.waitForJobCompletion(this.machineManager.removeVolumeFromMachine(
                    ma.getId().toString(), mv.getId().toString())));
            }
        }
        for (SystemMachine m : s2.getMachines()) {
            Machine ma = (Machine) m.getResource();
            for (MachineVolume mv : ma.getVolumes()) {
                Assert.assertEquals(Job.Status.SUCCESS, this.waitForJobCompletion(this.machineManager.removeVolumeFromMachine(
                    ma.getId().toString(), mv.getId().toString())));
            }
        }

        java.lang.System.out.println("deleting entire system");
        Assert.assertEquals(Job.Status.SUCCESS, this.waitForJobCompletion(this.systemManager.deleteSystem(systemId)));

        try {
            sv1 = this.systemManager.getSystemById(systemId);
        } catch (Exception e) {
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
                // java.lang.System.out.println("1-job date " +
                // this.jo.getLockedTime());
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
            // java.lang.System.out.println("2-job " + j.getId().toString() +
            // " locked with ID " + lockedID + "!and date "
            // + j.getLockedTime());
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
