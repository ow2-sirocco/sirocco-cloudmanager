/**
 *
 * SIROCCO
 * Copyright (C) 2013 Orange
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
 */
package org.ow2.sirocco.cloudmanager.itests.ejb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.QueryParams;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.AddressCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Subnet;
import org.ow2.sirocco.cloudmanager.model.cimi.SubnetConfig;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.SecurityGroupRule;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(Arquillian.class)
public class ComputeTest extends AbstractTestBase {
    final static String MACHINE_NAME = "testMachine";

    final static String MACHINE_NAME2 = "newTestMachine";

    final static String MACHINE_DESCRIPTION = "a machine";

    final static int MACHINE_NUMBER = 10;

    private static final String MACHINE_PROPERTY_COLOR_KEY = "color";

    private static final String MACHINE_PROPERTY_COLOR_VALUE0 = "blue";

    private static final String MACHINE_PROPERTY_COLOR_VALUE1 = "red";

    private static final String MACHINE_PROPERTY_WEIGHT_KEY = "10";

    private static final String MACHINE_PROPERTY_WEIGHT_VALUE0 = "20";

    private static final String IMAGE_CAPTURED_FROM_MACHINE = "capturedImage";

    @EJB
    IMachineManager machineManager;

    @EJB
    IMachineImageManager machineImageManager;

    @EJB
    INetworkManager networkManager;

    private String buildString(final int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    @Test
    public void testMachineConfig() throws Exception {
        MachineConfiguration machineConfig = new MachineConfiguration();
        machineConfig.setName(this.buildString(200));
        machineConfig.setCpu(1);
        machineConfig.setMemory(512);
        Map<String, String> props = new HashMap<>();
        props.put("color", "blue");
        props.put("age", "old");
        machineConfig.setProperties(props);
        DiskTemplate disk = new DiskTemplate();
        disk.setCapacity(1024);
        machineConfig.setDisks(Collections.singletonList(disk));
        machineConfig = this.machineManager.createMachineConfiguration(machineConfig);
        Assert.assertNotNull(machineConfig.getUuid());
        Assert.assertNotNull(machineConfig.getCreated());
        Assert.assertNotNull(machineConfig.getTenant());

        machineConfig = this.machineManager.getMachineConfigurationByUuid(machineConfig.getUuid());
        Assert.assertEquals(1, (int) machineConfig.getCpu());
        Assert.assertEquals(512, (int) machineConfig.getMemory());
        Assert.assertEquals(1, machineConfig.getDisks().size());
        Assert.assertEquals(1024, (int) machineConfig.getDisks().get(0).getCapacity());
        Assert.assertNotNull(machineConfig.getUuid());
        Assert.assertNotNull(machineConfig.getCreated());
        Assert.assertNotNull(machineConfig.getTenant());

        Assert.assertEquals("blue", machineConfig.getProperties().get("color"));
        Assert.assertEquals("old", machineConfig.getProperties().get("age"));

        String machineId = machineConfig.getUuid();

        Map<String, String> props2 = new HashMap<>();
        props2.put("color", "red");
        props2.put("age", "young");
        MachineConfiguration updatedMachineConfig = new MachineConfiguration();
        updatedMachineConfig.setProperties(props2);
        this.machineManager.updateMachineConfigurationAttributes(machineId, updatedMachineConfig,
            Collections.singletonList("properties"));

        machineConfig = this.machineManager.getMachineConfigurationByUuid(machineConfig.getUuid());

        Assert.assertEquals("red", machineConfig.getProperties().get("color"));
        Assert.assertEquals("young", machineConfig.getProperties().get("age"));

    }

    private Machine createMachine(final int index, final MachineConfiguration machineConfig, final MachineImage image,
        final Network network, final String... securityGroupIds) throws Exception {
        MachineCreate machineCreate = new MachineCreate();
        machineCreate.setName(ComputeTest.MACHINE_NAME + index);
        machineCreate.setDescription(ComputeTest.MACHINE_DESCRIPTION);
        Map<String, String> machineProperties = Maps.newHashMap();
        machineProperties.put(ComputeTest.MACHINE_PROPERTY_COLOR_KEY, ComputeTest.MACHINE_PROPERTY_COLOR_VALUE0);
        machineProperties.put(ComputeTest.MACHINE_PROPERTY_WEIGHT_KEY, ComputeTest.MACHINE_PROPERTY_WEIGHT_VALUE0);
        machineCreate.setProperties(machineProperties);
        MachineTemplate machineTemplate = new MachineTemplate();
        machineTemplate.setMachineConfig(machineConfig);
        machineTemplate.setMachineImage(image);
        MachineTemplateNetworkInterface nic = new MachineTemplateNetworkInterface();
        nic.setNetwork(network);
        machineTemplate.setNetworkInterfaces(Collections.singletonList(nic));
        machineTemplate.setInitialState(Machine.State.STARTED);
        if (securityGroupIds.length > 0) {
            List<String> sgIds = Lists.newArrayList(securityGroupIds);
            machineTemplate.setSecurityGroupUuids(sgIds);
        }
        machineCreate.setMachineTemplate(machineTemplate);
        Job job = this.machineManager.createMachine(machineCreate);

        Machine machine = (Machine) job.getTargetResource();
        Assert.assertNotNull(machine.getId());
        Assert.assertNotNull(machine.getUuid());
        Assert.assertNotNull(job.getId());
        Assert.assertNotNull(job.getUuid());

        Job.Status status = this.waitForJobCompletion(job);
        Assert.assertEquals(Job.Status.SUCCESS, status);
        machine = this.machineManager.getMachineByUuid(machine.getUuid());
        Assert.assertNotNull(machine.getCreated());
        Assert.assertTrue(machine.getState() == Machine.State.STARTED);
        Assert.assertEquals(ComputeTest.MACHINE_NAME + index, machine.getName());
        Assert.assertEquals(ComputeTest.MACHINE_DESCRIPTION, machine.getDescription());
        Assert.assertTrue(Maps.difference(machineProperties, machine.getProperties()).areEqual());
        Assert.assertEquals(machineConfig.getUuid(), machine.getConfig().getUuid());
        Assert.assertEquals(machineConfig.getCpu(), machine.getCpu());
        Assert.assertEquals(machineConfig.getMemory(), machine.getMemory());
        Assert.assertEquals(machineConfig.getDisks().size(), machine.getDisks().size());
        Assert.assertEquals(image.getUuid(), machine.getImage().getUuid());
        Assert.assertEquals(machineTemplate.getNetworkInterfaces().size(), machine.getNetworkInterfaces().size());
        Assert.assertEquals(network.getUuid(), machine.getNetworkInterfaces().get(0).getNetwork().getUuid());
        return machine;
    }

    private void deleteMachine(final String machineUuid) throws Exception {
        try {
            Job job = this.machineManager.deleteMachine(machineUuid);
            Job.Status status = this.waitForJobCompletion(job);
            Assert.assertEquals(Job.Status.SUCCESS, status);
            Machine machine = this.machineManager.getMachineByUuid(machineUuid);
            Assert.assertTrue(machine.getState() == State.DELETED);
            Assert.assertNotNull(machine.getDeleted());
        } catch (ResourceNotFoundException e) {
            // OK
        }
    }

    private void stopMachine(final String machineUuid) throws Exception {
        Date now = new Date();
        Job job = this.machineManager.stopMachine(machineUuid);
        Job.Status status = this.waitForJobCompletion(job);
        Assert.assertEquals(Job.Status.SUCCESS, status);
        Machine machine = this.machineManager.getMachineByUuid(machineUuid);
        Assert.assertTrue(machine.getUpdated().after(now));
        Assert.assertTrue(machine.getState() == Machine.State.STOPPED);
    }

    @Test
    public void testMachineCRUD() throws Exception {
        QueryResult<Machine> result = this.machineManager.getMachines();
        Assert.assertEquals(0, result.getCount());
        Assert.assertEquals(0, result.getItems().size());

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations().getItems();
        MachineConfiguration machineConfig = machineConfigs.get(0);
        List<MachineImage> machineImages = this.machineImageManager.getMachineImages();
        MachineImage image = machineImages.get(0);
        List<Network> networks = this.networkManager.getNetworks().getItems();
        Network network = networks.get(0);

        Machine machine = this.createMachine(0, machineConfig, image, network);

        // find all machines
        result = this.machineManager.getMachines();
        Assert.assertEquals(1, result.getCount());
        Assert.assertEquals(1, result.getItems().size());

        // find machine with given uuid
        result = this.machineManager.getMachines(new QueryParams.Builder().filter("uuid='" + machine.getUuid() + "'").build());
        Assert.assertEquals(1, result.getCount());
        Assert.assertEquals(1, result.getItems().size());

        // update machine name

        Map<String, Object> updatedAttributes = new HashMap<>();
        updatedAttributes.put("name", ComputeTest.MACHINE_NAME2);
        Job job = this.machineManager.updateMachineAttributes(machine.getUuid(), updatedAttributes);
        machine = (Machine) job.getTargetResource();
        Assert.assertNotNull(machine.getUpdated());
        Assert.assertEquals(ComputeTest.MACHINE_NAME2, machine.getName());

        this.deleteMachine(machine.getUuid());

        result = this.machineManager.getMachines();
        Assert.assertEquals(0, result.getCount());
        Assert.assertEquals(0, result.getItems().size());

    }

    @Test
    public void testMachineCollection() throws Exception {
        QueryResult<Machine> result = this.machineManager.getMachines();
        Assert.assertEquals(0, result.getCount());
        Assert.assertEquals(0, result.getItems().size());

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations().getItems();
        MachineConfiguration machineConfig = machineConfigs.get(0);
        List<MachineImage> machineImages = this.machineImageManager.getMachineImages();
        MachineImage image = machineImages.get(0);
        List<Network> networks = this.networkManager.getNetworks().getItems();
        Network network = networks.get(0);

        Machine[] machines = new Machine[ComputeTest.MACHINE_NUMBER];
        for (int i = 0; i < ComputeTest.MACHINE_NUMBER; i++) {
            machines[i] = this.createMachine(i, machineConfig, image, network);
        }

        // find all machines
        result = this.machineManager.getMachines();
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER, result.getCount());
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER, result.getItems().size());

        // find machines filtered by image
        result = this.machineManager.getMachines(new QueryParams.Builder().filter("image.uuid='" + image.getUuid() + "'")
            .build());
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER, result.getCount());
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER, result.getItems().size());

        // find machines filtered by config
        result = this.machineManager.getMachines(new QueryParams.Builder().filter(
            "config.uuid='" + machineConfig.getUuid() + "'").build());
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER, result.getCount());
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER, result.getItems().size());

        // paginated machine collection, marker only
        result = this.machineManager.getMachines(new QueryParams.Builder().marker(
            machines[ComputeTest.MACHINE_NUMBER / 2 - 1].getUuid()).build());

        Assert.assertEquals(ComputeTest.MACHINE_NUMBER, result.getCount());
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER / 2, result.getItems().size());

        // paginated machine collection, limit only
        result = this.machineManager.getMachines(new QueryParams.Builder().limit(ComputeTest.MACHINE_NUMBER / 3).build());
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER, result.getCount());
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER / 3, result.getItems().size());

        // paginated machine collection, marker and limit
        result = this.machineManager.getMachines(new QueryParams.Builder()
            .marker(machines[ComputeTest.MACHINE_NUMBER / 2 - 1].getUuid()).limit(2).build());
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER, result.getCount());
        Assert.assertEquals(2, result.getItems().size());

        // wait 1 second and stop every other machine
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        Date dateBeforeUpdate = new Date();

        for (int i = 0; i < ComputeTest.MACHINE_NUMBER; i++) {
            if (i % 2 == 0) {
                this.stopMachine(machines[i].getUuid());
            }
        }

        // find STARTED machines
        result = this.machineManager.getMachines(new QueryParams.Builder().filter("state=STARTED").build());
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER / 2, result.getCount());
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER / 2, result.getItems().size());

        // combine filter and pagination

        result = this.machineManager.getMachines(new QueryParams.Builder().filter("state=STARTED")
            .marker(machines[ComputeTest.MACHINE_NUMBER / 2 - 1].getUuid()).limit(ComputeTest.MACHINE_NUMBER / 2).build());
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER / 2, result.getCount());
        Assert.assertEquals(3, result.getItems().size());

        // get machines updated after given time: last machine update time
        // minus 1 hour 0 1 2 3 4 5 6 7 8 9

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        String dateTime = df.format(dateBeforeUpdate);
        System.out.println(dateTime);
        result = this.machineManager.getMachines(new QueryParams.Builder().filter("updated >" + dateTime).build());
        for (Machine m : result.getItems()) {
            System.out.println("MACHINE " + m.getName() + " updated at " + m.getUpdated());
        }
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER / 2, result.getCount());
        Assert.assertEquals(ComputeTest.MACHINE_NUMBER / 2, result.getItems().size());

        for (int i = 0; i < 10; i++) {
            this.deleteMachine(machines[i].getUuid());
        }

        result = this.machineManager.getMachines();
        Assert.assertEquals(0, result.getCount());
        Assert.assertEquals(0, result.getItems().size());
    }

    @Test
    public void testMachineProperties() throws Exception {
        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations().getItems();
        MachineConfiguration machineConfig = machineConfigs.get(0);
        List<MachineImage> machineImages = this.machineImageManager.getMachineImages();
        MachineImage image = machineImages.get(0);
        List<Network> networks = this.networkManager.getNetworks().getItems();
        Network network = networks.get(0);

        Machine machine = this.createMachine(0, machineConfig, image, network);

        // update property "color"
        // remove property "weight"

        Map<String, String> newProps = Maps.newHashMap(machine.getProperties());
        newProps.put(ComputeTest.MACHINE_PROPERTY_COLOR_KEY, ComputeTest.MACHINE_PROPERTY_COLOR_VALUE1);
        newProps.remove(ComputeTest.MACHINE_PROPERTY_WEIGHT_KEY);
        Map<String, Object> newAttributeMap = Maps.newHashMap();
        newAttributeMap.put("properties", newProps);
        Date timeBeforeUpdate = new Date();
        Job job = this.machineManager.updateMachineAttributes(machine.getUuid(), newAttributeMap);
        Assert.assertEquals(Job.Status.SUCCESS, job.getState());

        machine = this.machineManager.getMachineByUuid(machine.getUuid());
        Assert.assertTrue(machine.getUpdated().after(timeBeforeUpdate));
        Assert.assertEquals(1, machine.getProperties().size());
        Assert.assertEquals(ComputeTest.MACHINE_PROPERTY_COLOR_VALUE1,
            machine.getProperties().get(ComputeTest.MACHINE_PROPERTY_COLOR_KEY));

    }

    @Test
    public void testMachineCapture() throws Exception {
        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations().getItems();
        MachineConfiguration machineConfig = machineConfigs.get(0);
        List<MachineImage> machineImages = this.machineImageManager.getMachineImages();
        MachineImage image = machineImages.get(0);
        List<Network> networks = this.networkManager.getNetworks().getItems();
        Network network = networks.get(0);

        Machine machine = this.createMachine(0, machineConfig, image, network);

        MachineImage machineImage = new MachineImage();
        machineImage.setName(ComputeTest.IMAGE_CAPTURED_FROM_MACHINE);
        Map<String, String> imageProperties = Maps.newHashMap();
        imageProperties.put("imageType", "Gold");
        machineImage.setProperties(imageProperties);
        Job job = this.machineManager.captureMachine(machine.getUuid(), machineImage);
        MachineImage newImage = (MachineImage) job.getAffectedResources().get(0);
        Assert.assertNotNull(newImage.getUuid());
        Job.Status status = this.waitForJobCompletion(job);
        Assert.assertEquals(Job.Status.SUCCESS, status);

        newImage = this.machineImageManager.getMachineImageByUuid(newImage.getUuid());
        Assert.assertEquals(ComputeTest.IMAGE_CAPTURED_FROM_MACHINE, newImage.getName());
        Assert.assertTrue(Maps.difference(imageProperties, newImage.getProperties()).areEqual());

        this.deleteMachineImage(newImage.getUuid());
        this.deleteMachine(machine.getUuid());

    }

    private void deleteMachineImage(final String imageUuid) throws Exception {
        try {
            Job job = this.machineImageManager.deleteMachineImage(imageUuid);
            Job.Status status = this.waitForJobCompletion(job);
            Assert.assertEquals(Job.Status.SUCCESS, status);
            MachineImage image = this.machineImageManager.getMachineImageByUuid(imageUuid);
            Assert.assertTrue(image.getState() == MachineImage.State.DELETED);
            Assert.assertNotNull(image.getDeleted());
        } catch (ResourceNotFoundException e) {
            // OK
        }
    }

    final static String SECURITY_GROUP_0_NAME = "myGroup0";

    final static String SECURITY_GROUP_0_DESCRIPTION = "my security group0";

    final static String SECURITY_GROUP_1_NAME = "myGroup1";

    final static String SECURITY_GROUP_1_DESCRIPTION = "my security group1";

    final static String RULE0_CIDR = "0.0.0.0/0";

    final static String RULE_PROTOCOL = "TCP";

    final static int RULE_FROM_PORT = 8000;

    final static int RULE_TO_PORT = 900;

    @Test
    public void testSecurityGroups() throws Exception {
        // create security group 0
        SecurityGroupCreate securityGroupCreate = new SecurityGroupCreate();
        securityGroupCreate.setName(ComputeTest.SECURITY_GROUP_0_NAME);
        securityGroupCreate.setDescription(ComputeTest.SECURITY_GROUP_0_DESCRIPTION);

        Job job = this.networkManager.createSecurityGroup(securityGroupCreate);

        SecurityGroup secGroup0 = (SecurityGroup) job.getTargetResource();
        Assert.assertNotNull(secGroup0.getId());
        Assert.assertNotNull(secGroup0.getUuid());
        Assert.assertNotNull(job.getId());
        Assert.assertNotNull(job.getUuid());
        Job.Status status = this.waitForJobCompletion(job);
        Assert.assertEquals(Job.Status.SUCCESS, status);

        secGroup0 = this.networkManager.getSecurityGroupByUuid(secGroup0.getUuid());
        Assert.assertEquals(SecurityGroup.State.AVAILABLE, secGroup0.getState());
        Assert.assertEquals(ComputeTest.SECURITY_GROUP_0_NAME, secGroup0.getName());
        Assert.assertEquals(ComputeTest.SECURITY_GROUP_0_DESCRIPTION, secGroup0.getDescription());
        Assert.assertNotNull(secGroup0.getCreated());

        // create security group 1

        securityGroupCreate = new SecurityGroupCreate();
        securityGroupCreate.setName(ComputeTest.SECURITY_GROUP_1_NAME);
        securityGroupCreate.setDescription(ComputeTest.SECURITY_GROUP_1_DESCRIPTION);

        job = this.networkManager.createSecurityGroup(securityGroupCreate);
        SecurityGroup secGroup1 = (SecurityGroup) job.getTargetResource();
        status = this.waitForJobCompletion(job);
        Assert.assertEquals(Job.Status.SUCCESS, status);

        // list groups

        QueryResult<SecurityGroup> secGroups = this.networkManager.getSecurityGroups();
        Assert.assertEquals(2, secGroups.getCount());
        Assert.assertEquals(2, secGroups.getItems().size());
        Assert.assertEquals(secGroup0.getUuid(), secGroups.getItems().get(1).getUuid());
        Assert.assertEquals(secGroup1.getUuid(), secGroups.getItems().get(0).getUuid());

        // create rule with cidr for group 0

        SecurityGroupRule rule = this.networkManager.addRuleToSecurityGroupUsingIpRange(secGroup0.getUuid(),
            ComputeTest.RULE0_CIDR, ComputeTest.RULE_PROTOCOL, ComputeTest.RULE_FROM_PORT, ComputeTest.RULE_TO_PORT);
        Assert.assertNotNull(rule.getId());
        Assert.assertNotNull(rule.getUuid());
        Assert.assertNotNull(rule.getProviderAssignedId());
        Assert.assertNull(rule.getSourceGroup());
        Assert.assertEquals(ComputeTest.RULE0_CIDR, rule.getSourceIpRange());
        Assert.assertEquals(ComputeTest.RULE_PROTOCOL, rule.getIpProtocol());
        Assert.assertTrue(ComputeTest.RULE_FROM_PORT == rule.getFromPort());
        Assert.assertTrue(ComputeTest.RULE_TO_PORT == rule.getToPort());

        SecurityGroupRule rule2 = this.networkManager.getSecurityGroupRuleByUuid(rule.getUuid());
        Assert.assertEquals(ComputeTest.RULE0_CIDR, rule2.getSourceIpRange());
        Assert.assertEquals(ComputeTest.RULE_PROTOCOL, rule2.getIpProtocol());
        Assert.assertTrue(ComputeTest.RULE_FROM_PORT == rule2.getFromPort());
        Assert.assertTrue(ComputeTest.RULE_TO_PORT == rule2.getToPort());

        secGroup0 = this.networkManager.getSecurityGroupByUuid(secGroup0.getUuid());
        Assert.assertEquals(1, secGroup0.getRules().size());
        Assert.assertEquals(secGroup0.getRules().get(0).getUuid(), rule.getUuid());

        // create machine in security group 0

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations().getItems();
        MachineConfiguration machineConfig = machineConfigs.get(0);
        List<MachineImage> machineImages = this.machineImageManager.getMachineImages();
        MachineImage image = machineImages.get(0);
        List<Network> networks = this.networkManager.getNetworks().getItems();
        Network network = networks.get(0);

        Machine machine = this.createMachine(0, machineConfig, image, network, secGroup0.getUuid());
        Assert.assertEquals(1, machine.getSecurityGroups().size());
        Assert.assertEquals(secGroup0.getUuid(), machine.getSecurityGroups().get(0).getUuid());

        secGroup0 = this.networkManager.getSecurityGroupByUuid(secGroup0.getUuid());
        Assert.assertEquals(1, secGroup0.getMembers().size());
        Assert.assertEquals(machine.getUuid(), secGroup0.getMembers().get(0).getUuid());

        // attempting to delete the security group should fail

        try {
            this.networkManager.deleteSecurityGroup(secGroup0.getUuid());
            Assert.fail("deleting a security group in use should fail");
        } catch (ResourceConflictException e) {
            // OK
        }

        // delete machine

        this.deleteMachine(machine.getUuid());

        secGroup0 = this.networkManager.getSecurityGroupByUuid(secGroup0.getUuid());
        Assert.assertEquals(0, secGroup0.getMembers().size());

        // create rule with source group 0 for group 1

        SecurityGroupRule rule1 = this.networkManager.addRuleToSecurityGroupUsingSourceGroup(secGroup1.getUuid(),
            secGroup0.getUuid(), ComputeTest.RULE_PROTOCOL, ComputeTest.RULE_FROM_PORT, ComputeTest.RULE_TO_PORT);
        Assert.assertNotNull(rule1.getId());
        Assert.assertNotNull(rule1.getUuid());
        Assert.assertNotNull(rule1.getProviderAssignedId());
        Assert.assertNull(rule1.getSourceIpRange());
        Assert.assertEquals(secGroup0.getUuid(), rule1.getSourceGroup().getUuid());
        Assert.assertEquals(ComputeTest.RULE_PROTOCOL, rule1.getIpProtocol());
        Assert.assertTrue(ComputeTest.RULE_FROM_PORT == rule1.getFromPort());
        Assert.assertTrue(ComputeTest.RULE_TO_PORT == rule1.getToPort());

        secGroup1 = this.networkManager.getSecurityGroupByUuid(secGroup1.getUuid());
        Assert.assertEquals(1, secGroup1.getRules().size());
        Assert.assertEquals(secGroup1.getRules().get(0).getUuid(), rule1.getUuid());

        // deleting sec group 0 should fail because it is used by sec group 1

        try {
            this.networkManager.deleteSecurityGroup(secGroup0.getUuid());
            Assert.fail("deleting a security group in use should fail");
        } catch (ResourceConflictException e) {
            // OK
            System.out.println(e.getMessage());
        }

        // delete rule for sec group 0

        this.networkManager.deleteRuleFromSecurityGroup(rule.getUuid());

        secGroup0 = this.networkManager.getSecurityGroupByUuid(secGroup0.getUuid());
        Assert.assertTrue(secGroup0.getRules().isEmpty());

        // delete security groups

        job = this.networkManager.deleteSecurityGroup(secGroup1.getUuid());
        Assert.assertNotNull(job.getId());
        Assert.assertNotNull(job.getUuid());
        status = this.waitForJobCompletion(job);
        Assert.assertEquals(Job.Status.SUCCESS, status);

        job = this.networkManager.deleteSecurityGroup(secGroup0.getUuid());
        Assert.assertNotNull(job.getId());
        Assert.assertNotNull(job.getUuid());
        status = this.waitForJobCompletion(job);
        Assert.assertEquals(Job.Status.SUCCESS, status);

        try {
            secGroup0 = this.networkManager.getSecurityGroupByUuid(secGroup0.getUuid());
            Assert.assertTrue(secGroup0.getState() == SecurityGroup.State.DELETED);
            Assert.assertNotNull(secGroup0.getDeleted());
        } catch (ResourceNotFoundException e) {
            // OK
            System.out.println(e.getMessage());
        }

        try {
            secGroup1 = this.networkManager.getSecurityGroupByUuid(secGroup1.getUuid());
            Assert.assertTrue(secGroup1.getState() == SecurityGroup.State.DELETED);
            Assert.assertNotNull(secGroup1.getDeleted());
        } catch (ResourceNotFoundException e) {
            // OK
        }

    }

    @Test
    public void testAddresses() throws Exception {
        AddressCreate addressCreate = new AddressCreate();

        // create Address

        Job job = this.networkManager.createAddress(addressCreate);
        Assert.assertNotNull(job.getId());
        Assert.assertNotNull(job.getUuid());
        Assert.assertEquals(Job.Status.SUCCESS, job.getState());

        Address publicAddress = (Address) job.getTargetResource();
        Assert.assertNotNull(publicAddress.getCreated());
        Assert.assertNotNull(publicAddress.getUpdated());
        Assert.assertNotNull(publicAddress.getId());
        Assert.assertNotNull(publicAddress.getProviderAssignedId());
        Assert.assertNotNull(publicAddress.getIp());
        Assert.assertEquals(Address.State.CREATED, publicAddress.getState());

        // get address by uuid

        Address address = this.networkManager.getAddressByUuid(publicAddress.getUuid());
        Assert.assertEquals(publicAddress.getIp(), address.getIp());

        // get addresses

        QueryResult<Address> result = this.networkManager.getAddresses();
        Assert.assertEquals(1, result.getCount());
        Assert.assertEquals(1, result.getItems().size());
        Assert.assertEquals(publicAddress.getIp(), result.getItems().get(0).getIp());

        // create machine

        List<MachineConfiguration> machineConfigs = this.machineManager.getMachineConfigurations().getItems();
        MachineConfiguration machineConfig = machineConfigs.get(0);
        List<MachineImage> machineImages = this.machineImageManager.getMachineImages();
        MachineImage image = machineImages.get(0);
        List<Network> networks = this.networkManager.getNetworks().getItems();
        Network network = networks.get(0);

        Machine machine = this.createMachine(0, machineConfig, image, network);

        // associate address to machine

        job = this.networkManager.addAddressToMachine(machine.getUuid(), publicAddress.getIp());
        Assert.assertNotNull(job.getId());
        Assert.assertNotNull(job.getUuid());
        Assert.assertEquals(Job.Status.SUCCESS, job.getState());

        publicAddress = this.networkManager.getAddressByUuid(publicAddress.getUuid());
        Assert.assertNotNull(publicAddress.getResource());
        Machine attachedMachine = (Machine) publicAddress.getResource();
        Assert.assertEquals(machine.getUuid(), attachedMachine.getUuid());

        // dissociate address from machine

        job = this.networkManager.removeAddressFromMachine(machine.getUuid(), publicAddress.getIp());
        Assert.assertNotNull(job.getId());
        Assert.assertNotNull(job.getUuid());
        Assert.assertEquals(Job.Status.SUCCESS, job.getState());

        publicAddress = this.networkManager.getAddressByUuid(publicAddress.getUuid());
        Assert.assertNull(publicAddress.getResource());

        // delete address
        job = this.networkManager.deleteAddress(publicAddress.getUuid());
        Assert.assertNotNull(job.getId());
        Assert.assertNotNull(job.getUuid());
        Assert.assertEquals(Job.Status.SUCCESS, job.getState());

        try {
            address = this.networkManager.getAddressByUuid(publicAddress.getUuid());
            Assert.assertEquals(Address.State.DELETED, address.getState());
        } catch (ResourceNotFoundException e) {
            // OK
        }

        result = this.networkManager.getAddresses();
        Assert.assertEquals(0, result.getCount());
        Assert.assertEquals(0, result.getItems().size());

    }

    private final String CIDR0 = "10.10.10.0/28";

    @Test
    public void testNetworksAndSubnets() throws Exception {
        // create network
        NetworkCreate networkCreate = new NetworkCreate();
        networkCreate.setName("NET0");
        networkCreate.setDescription("NET0 description");

        NetworkTemplate template = new NetworkTemplate();
        NetworkConfiguration config = new NetworkConfiguration();
        SubnetConfig subnetConfig = new SubnetConfig();
        subnetConfig.setCidr(this.CIDR0);
        config.setSubnets(Collections.singletonList(subnetConfig));
        template.setNetworkConfig(config);
        networkCreate.setNetworkTemplate(template);

        Job job = this.networkManager.createNetwork(networkCreate);
        Network net = (Network) job.getTargetResource();
        Assert.assertNotNull(net.getId());
        Assert.assertNotNull(net.getUuid());
        Assert.assertNotNull(net.getCreated());
        Assert.assertNotNull(job.getId());
        Assert.assertNotNull(job.getUuid());

        Job.Status status = this.waitForJobCompletion(job);
        Assert.assertEquals(Job.Status.SUCCESS, status);

        // retrieve network
        QueryResult<Network> nets = this.networkManager.getNetworks();
        // another network has been imported from the mock provider
        Assert.assertEquals(2, nets.getCount());
        Assert.assertEquals(2, nets.getItems().size());

        net = this.networkManager.getNetworkByUuid(net.getUuid());
        Assert.assertEquals(Network.State.STARTED, net.getState());
        Subnet subnet = net.getSubnets().get(0);
        Assert.assertEquals(Subnet.State.AVAILABLE, subnet.getState());
        Assert.assertEquals(this.CIDR0, subnet.getCidr());
        Assert.assertNotNull(subnet.getCreated());
        Assert.assertNotNull(subnet.getId());
        Assert.assertNotNull(subnet.getUuid());

        subnet = this.networkManager.getSubnetByUuid(subnet.getUuid());
        Assert.assertEquals(this.CIDR0, subnet.getCidr());
        QueryResult<Subnet> subnets = this.networkManager.getSubnets();
        Assert.assertEquals(2, subnets.getCount());
        Assert.assertEquals(2, subnets.getItems().size());

        // delete network

        job = this.networkManager.deleteNetwork(net.getUuid());
        status = this.waitForJobCompletion(job);
        Assert.assertEquals(Job.Status.SUCCESS, status);
        try {
            net = this.networkManager.getNetworkByUuid(net.getUuid());
            Assert.assertTrue(net.getState() == Network.State.DELETED);
            for (Subnet sn : net.getSubnets()) {
                Assert.assertEquals(Subnet.State.DELETED, sn.getState());
            }
            Assert.assertNotNull(net.getDeleted());
        } catch (ResourceNotFoundException e) {
            // OK
        }

        // network subnet should be deleted

        subnets = this.networkManager.getSubnets();
        Assert.assertEquals(1, subnets.getCount());
        Assert.assertEquals(1, subnets.getItems().size());

        try {
            this.networkManager.getSubnetByUuid(subnet.getUuid());
        } catch (ResourceNotFoundException e) {
            // OK
        }

    }

}
