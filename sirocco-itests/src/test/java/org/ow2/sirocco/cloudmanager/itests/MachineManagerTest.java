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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;

public class MachineManagerTest extends AbstractTestBase {
    private int credcounter = 1;

    public CredentialsCreate initCredentials() {
        CredentialsCreate credentialsCreate = new CredentialsCreate();
        CredentialsTemplate in = new CredentialsTemplate();
        credentialsCreate.setCredentialTemplate(in);
        credentialsCreate.setName("testCred_" + this.credcounter);
        credentialsCreate.setDescription("testCred_" + this.credcounter + " description ");
        credentialsCreate.setProperties(new HashMap<String, String>());

        in.setUserName("madras");
        in.setPassword("bombaydelhi");
        // String key = new String("parisnewyork" + this.credcounter);
        // in.setKey(key.getBytes());
        this.credcounter += 1;
        return credentialsCreate;
    }

    public Credentials createCredentials() throws Exception {
        Credentials out_c = this.credManager.createCredentials(this.initCredentials());
        Assert.assertNotNull("createCredentials returns no credentials", out_c);
        return out_c;
    }

    private int ccounter = 1;

    public MachineConfiguration initMachineConfiguration() {
        MachineConfiguration in_c = new MachineConfiguration();
        in_c.setName("testConfig_" + UUID.randomUUID());
        this.ccounter += 1;
        in_c.setDescription("testConfig_" + this.ccounter + " description");
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("entity", "machineconfiguration");
        in_c.setProperties(properties);

        List<DiskTemplate> dTemplates = new ArrayList<DiskTemplate>();

        for (int i = 0; i < 2; i++) {
            DiskTemplate dt = new DiskTemplate();
            dt.setCapacity(4500);
            dt.setFormat("ext3");
            dt.setInitialLocation("/dev/sd" + i);
            dTemplates.add(dt);
        }
        in_c.setCpu(1);
        in_c.setMemory(1024 + 512);
        in_c.setDisks(dTemplates);
        return in_c;
    }

    private int imagecounter = 1;

    public MachineImage initMachineImage() {
        MachineImage mimage = new MachineImage();
        mimage.setName("image_" + this.imagecounter);
        mimage.setDescription("image description " + this.imagecounter);
        mimage.setImageLocation("http://example.com/images/WinXP-SP2" + this.imagecounter);
        this.imagecounter += 1;
        return mimage;
    }

    public MachineImage createMachineImage() throws Exception {
        MachineImage in_i = this.initMachineImage();
        Job out_j = this.machineImageManager.createMachineImage(in_i);
        Assert.assertNotNull("machineImageCreate returns no machineimage", out_j);
        boolean done = false;
        MachineImage i = null;
        int loop = 0;
        while (done != true) {
            int counter = AbstractTestBase.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
            i = this.machineImageManager.getMachineImageById(out_j.getTargetResource().getId().toString());

            if (i == null) {

                throw new Exception(" createMachineImage returned null");
            }

            if (i.getState() == MachineImage.State.AVAILABLE) {
                done = true;
            }
            if (loop == 5) {
                done = true;
            }
            loop++;
            Thread.sleep(1000);
        }
        if ((i.getState() != MachineImage.State.AVAILABLE)) {
            throw new Exception(" failed to create machine image");
        }
        return i;
    }

    @Test
    public void testCreateMachineImage() throws Exception {
        this.createMachineImage();
    }

    public MachineConfiguration createMachineConfiguration() throws Exception {
        MachineConfiguration in_c = this.initMachineConfiguration();

        MachineConfiguration out_c = this.machineManager.createMachineConfiguration(in_c);
        Assert.assertNotNull("machineConfigurationCreate returns no machineconfiguration", out_c);
        // this.configs.add(in_c);
        return out_c;
    }

    @Test
    public void testCreateMachineConfiguration() throws Exception {
        this.createMachineConfiguration();
    }

    private int mcounter = 0;

    @Test
    public void testCreateMachine() throws Exception {

        MachineCreate machineCreate = new MachineCreate();
        machineCreate.setName("myMachine_" + this.mcounter);
        machineCreate.setDescription("my machine" + this.mcounter);
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("department", "MAPS");
        machineCreate.setProperties(properties);
        MachineTemplate machineTemplate = new MachineTemplate();

        MachineConfiguration machineConfig = this.createMachineConfiguration();
        machineTemplate.setMachineConfig(machineConfig);

        machineTemplate.setMachineImage(this.createMachineImage());

        machineTemplate.setCredential(this.createCredentials());
        machineTemplate.setVolumes(new ArrayList<MachineVolume>());
        machineTemplate.setVolumeTemplates(new ArrayList<MachineVolumeTemplate>());
        machineTemplate.setNetworkInterfaces(new ArrayList<MachineTemplateNetworkInterface>());
        machineCreate.setMachineTemplate(machineTemplate);

        Job job = this.machineManager.createMachine(machineCreate);
        Assert.assertNotNull("machineCreate returns no job", job);

        Assert.assertNotNull(job.getId());
        Assert.assertTrue("job action is invalid", job.getAction().equals("add"));
        String machineId = job.getTargetResource().getId().toString();
        Assert.assertNotNull("job target entity is invalid", machineId);

        String jobId = job.getId().toString();

        int counter = AbstractTestBase.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;

        Machine machine = this.machineManager.getMachineById(machineId);

        while (true) {
            job = this.jobManager.getJobById(jobId);

            if (job.getState() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("Machine creation time out");
            }
        }

        Assert.assertTrue("machine creation failed: " + job.getStatusMessage(), job.getState() == Job.Status.SUCCESS);

        machine = this.machineManager.getMachineById(machineId);
        Assert.assertNotNull("cannot find machine juste created", machine);
        Assert.assertEquals("Created machine is not STOPPED", machine.getState(), Machine.State.STOPPED);

        Assert.assertNotNull(machine.getId());
        Assert.assertEquals(machine.getName(), "myMachine_" + this.mcounter);
        Assert.assertEquals(machine.getDescription(), "my machine" + this.mcounter);

        this.startMachine(machine.getId().toString());

        this.stopMachine(machine.getId().toString());

        this.deleteMachine(machine.getId().toString());

        Machine mm = null;
        try {
            mm = this.machineManager.getMachineById(machine.getId().toString());
        } catch (Exception e) {

        }
        Assert.assertNull(" deleted machine still there", mm);
        System.out.println(" end of testCreateMachine until delete ");
    }

    void deleteMachine(final String machineId) throws Exception {

        Job job = this.machineManager.deleteMachine(machineId);
        Assert.assertNotNull("deleteMachine returns no job", job);

        Assert.assertTrue("job action is invalid", job.getAction().equals("delete"));
        Assert.assertEquals("job target entity is invalid", machineId, job.getTargetResource().getId().toString());

        String jobId = job.getId().toString();

        int counter = AbstractTestBase.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        while (true) {
            job = this.jobManager.getJobById(jobId);
            if (job.getState() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("Machine operation time out");
            }
        }

        Assert.assertTrue("machine deletion failed: " + job.getStatusMessage(), job.getState() == Job.Status.SUCCESS);

    }

    void startMachine(final String machineId) throws Exception {

        Job job = this.machineManager.startMachine(machineId, null);
        Assert.assertNotNull("startMachine returns no job", job);

        Assert.assertTrue("job action is invalid", job.getAction().equals("start"));
        Assert.assertEquals("job target entity is invalid", machineId, job.getTargetResource().getId().toString());

        String jobId = job.getId().toString();

        int counter = AbstractTestBase.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        while (true) {
            job = this.jobManager.getJobById(jobId);
            if (job.getState() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("Machine operation time out");
            }
        }

        Assert.assertTrue("machine start failed: " + job.getStatusMessage(), job.getState() == Job.Status.SUCCESS);

    }

    void stopMachine(final String machineId) throws Exception {

        Job job = this.machineManager.stopMachine(machineId, false, null);
        Assert.assertNotNull("stopMachine returns no job", job);

        Assert.assertTrue("job action is invalid", job.getAction().equals("stop"));
        Assert.assertEquals("job target entity is invalid", machineId, job.getTargetResource().getId().toString());

        String jobId = job.getId().toString();

        int counter = AbstractTestBase.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        while (true) {
            job = this.jobManager.getJobById(jobId);
            if (job.getState() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("Machine operation time out");
            }
        }

        Assert.assertTrue("machine stop failed: " + job.getStatusMessage(), job.getState() == Job.Status.SUCCESS);

    }
}
