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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDiskCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.domain.StorageUnit;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiExpand;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.RequestHeader;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.Disk;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.Type;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;

/**
 * Converters tests of machines resources.
 */
public class MachinesConverterTest {

    private CimiRequest request;

    private CimiContext context;

    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setBaseUri("http://www.test.org/");
        RequestHeader header = new RequestHeader();
        header.setCimiSelect(new CimiSelect());
        header.setCimiExpand(new CimiExpand());
        this.request.setHeader(header);

        this.context = new CimiContextImpl(this.request, new CimiResponse());
    }

    @Test
    public void testCimiMachineConfiguration() throws Exception {
        CimiMachineConfiguration cimi;
        MachineConfiguration service;

        // Empty Cimi -> Service
        service = (MachineConfiguration) this.context.convertToService(new CimiMachineConfiguration());
        Assert.assertNull(service.getCpu());
        Assert.assertNull(service.getDiskTemplates());
        Assert.assertNull(service.getMemory());

        // Empty Service -> Cimi
        cimi = (CimiMachineConfiguration) this.context
            .convertToCimi(new MachineConfiguration(), CimiMachineConfiguration.class);
        Assert.assertNull(cimi.getCpu());
        Assert.assertNull(cimi.getDisks());
        Assert.assertNull(cimi.getMemory());

        // Full Cimi -> Service
        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu());
        cimi.setMemory(new CimiMemory());

        service = (MachineConfiguration) this.context.convertToService(cimi);
        Assert.assertEquals(Cpu.class, service.getCpu().getClass());
        Assert.assertEquals(Memory.class, service.getMemory().getClass());
        Assert.assertNull(service.getDiskTemplates());

        // Full Cimi -> Service
        cimi = new CimiMachineConfiguration();
        cimi.setDisks(new CimiDiskConfiguration[] {});

        service = (MachineConfiguration) this.context.convertToService(cimi);
        Assert.assertNull(service.getDiskTemplates());

        // Full Cimi -> Service
        cimi = new CimiMachineConfiguration();
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(), new CimiDiskConfiguration()});

        service = (MachineConfiguration) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.getDiskTemplates().size());

        // Full Service -> Cimi
        service = new MachineConfiguration();
        service.setCpu(new Cpu());
        service.setMemory(new Memory());

        cimi = (CimiMachineConfiguration) this.context.convertToCimi(service, CimiMachineConfiguration.class);
        Assert.assertEquals(CimiCpu.class, cimi.getCpu().getClass());
        Assert.assertEquals(CimiMemory.class, cimi.getMemory().getClass());
        Assert.assertNull(cimi.getDisks());

        // Full Service -> Cimi
        service = new MachineConfiguration();
        service.setDiskTemplates(new ArrayList<DiskTemplate>());

        cimi = (CimiMachineConfiguration) this.context.convertToCimi(service, CimiMachineConfiguration.class);
        Assert.assertNull(cimi.getDisks());

        // Full Service -> Cimi
        service = new MachineConfiguration();
        service.setDiskTemplates(new ArrayList<DiskTemplate>(Arrays.asList(new DiskTemplate[] {new DiskTemplate(),
            new DiskTemplate(), new DiskTemplate()})));

        cimi = (CimiMachineConfiguration) this.context.convertToCimi(service, CimiMachineConfiguration.class);
        Assert.assertEquals(3, cimi.getDisks().length);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCimiMachineConfigurationCollection() throws Exception {
        CimiMachineConfigurationCollection cimi;
        List<MachineConfiguration> service;

        // Empty Cimi -> Service
        service = (List<MachineConfiguration>) this.context.convertToService(new CimiMachineConfigurationCollection());
        Assert.assertNotNull(service);
        Assert.assertEquals(0, service.size());

        // Empty Service -> Cimi
        cimi = (CimiMachineConfigurationCollection) this.context.convertToCimi(new ArrayList<MachineConfiguration>(),
            CimiMachineConfigurationCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiMachineConfigurationCollection();
        cimi.setArray(new CimiMachineConfiguration[] {new CimiMachineConfiguration(), new CimiMachineConfiguration()});

        service = (List<MachineConfiguration>) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.size());

        // Full Service -> Cimi
        MachineConfiguration machineConfiguration1 = new MachineConfiguration();
        machineConfiguration1.setId(1);
        machineConfiguration1.setName("nameOne");
        MachineConfiguration machineConfiguration2 = new MachineConfiguration();
        machineConfiguration2.setId(2);
        machineConfiguration2.setName("nameTwo");
        MachineConfiguration machineConfiguration3 = new MachineConfiguration();
        machineConfiguration3.setId(3);
        machineConfiguration3.setName("nameThree");

        service = new ArrayList<MachineConfiguration>();
        service.add(machineConfiguration1);
        service.add(machineConfiguration2);
        service.add(machineConfiguration3);

        cimi = (CimiMachineConfigurationCollection) this.context.convertToCimi(service,
            CimiMachineConfigurationCollection.class);
        Assert.assertEquals(3, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineConfiguration.getPathname() + "/1",
            cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineConfiguration.getPathname() + "/2",
            cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineConfiguration.getPathname() + "/3",
            cimi.getArray()[2].getHref());
        Assert.assertNull(cimi.getArray()[2].getId());
        Assert.assertNull(cimi.getArray()[2].getName());

        cimi = (CimiMachineConfigurationCollection) this.context.convertToCimi(
            Arrays.asList(new MachineConfiguration[] {machineConfiguration3, machineConfiguration1}),
            CimiMachineConfigurationCollection.class);
        Assert.assertEquals(2, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineConfiguration.getPathname() + "/3",
            cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineConfiguration.getPathname() + "/1",
            cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
    }

    @Test
    public void testCimiMachineConfigurationCollectionWithExpandAll() throws Exception {
        CimiMachineConfigurationCollection cimi;
        List<MachineConfiguration> service;

        // Prepare Service
        MachineConfiguration machineConfiguration1 = new MachineConfiguration();
        machineConfiguration1.setId(1);
        machineConfiguration1.setName("nameOne");
        MachineConfiguration machineConfiguration2 = new MachineConfiguration();
        machineConfiguration2.setId(2);
        machineConfiguration2.setName("nameTwo");
        MachineConfiguration machineConfiguration3 = new MachineConfiguration();
        machineConfiguration3.setId(3);
        machineConfiguration3.setName("nameThree");

        service = new ArrayList<MachineConfiguration>();
        service.add(machineConfiguration1);
        service.add(machineConfiguration2);
        service.add(machineConfiguration3);

        // Prepare request
        this.request.getHeader().setCimiExpand(new CimiExpand(CimiExpand.EXPAND_ALL));

        // Convert
        cimi = (CimiMachineConfigurationCollection) this.context.convertToCimi(service,
            CimiMachineConfigurationCollection.class);

        // Verify
        Assert.assertEquals(3, cimi.getArray().length);

        Assert.assertEquals(ExchangeType.MachineConfiguration.makeHref(this.request.getBaseUri(), "1"),
            cimi.getArray()[0].getHref());
        Assert.assertEquals(ExchangeType.MachineConfiguration.makeHref(this.request.getBaseUri(), "1"),
            cimi.getArray()[0].getId());
        Assert.assertEquals("nameOne", cimi.getArray()[0].getName());

        Assert.assertEquals(ExchangeType.MachineConfiguration.makeHref(this.request.getBaseUri(), "2"),
            cimi.getArray()[1].getHref());
        Assert.assertEquals(ExchangeType.MachineConfiguration.makeHref(this.request.getBaseUri(), "2"),
            cimi.getArray()[1].getId());
        Assert.assertEquals("nameTwo", cimi.getArray()[1].getName());

        Assert.assertEquals(ExchangeType.MachineConfiguration.makeHref(this.request.getBaseUri(), "3"),
            cimi.getArray()[2].getHref());
        Assert.assertEquals(ExchangeType.MachineConfiguration.makeHref(this.request.getBaseUri(), "3"),
            cimi.getArray()[2].getId());
        Assert.assertEquals("nameThree", cimi.getArray()[2].getName());
    }

    @Test
    public void testCimiMachineImage() throws Exception {
        CimiMachineImage cimi;
        MachineImage service;

        // Empty Cimi -> Service
        service = (MachineImage) this.context.convertToService(new CimiMachineImage());
        Assert.assertNull(service.getImageLocation());
        Assert.assertNull(service.getState());
        Assert.assertNull(service.getType());

        // Empty Service -> Cimi
        cimi = (CimiMachineImage) this.context.convertToCimi(new MachineImage(), CimiMachineImage.class);
        Assert.assertNull(cimi.getImageLocation());
        Assert.assertNull(cimi.getState());
        Assert.assertNull(cimi.getType());

        // Full Cimi -> Service
        cimi = new CimiMachineImage();
        cimi.setImageLocation(new ImageLocation("href"));
        cimi.setState("state");
        cimi.setType("type");

        service = (MachineImage) this.context.convertToService(cimi);
        Assert.assertEquals("href", service.getImageLocation());
        Assert.assertNull(service.getState());
        Assert.assertNull(service.getType());

        // Full Service -> Cimi
        service = new MachineImage();
        service.setImageLocation("hrefImageLocation");
        service.setState(State.AVAILABLE);
        service.setType(Type.IMAGE);

        cimi = (CimiMachineImage) this.context.convertToCimi(service, CimiMachineImage.class);
        Assert.assertEquals("hrefImageLocation", cimi.getImageLocation().getHref());
        Assert.assertEquals("AVAILABLE", cimi.getState());
        Assert.assertEquals("IMAGE", cimi.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCimiMachineImageCollection() throws Exception {
        CimiMachineImageCollection cimi;
        List<MachineImage> service;

        // Empty Cimi -> Service
        service = (List<MachineImage>) this.context.convertToService(new CimiMachineImageCollection());
        Assert.assertNotNull(service);
        Assert.assertEquals(0, service.size());

        // Empty Service -> Cimi
        cimi = (CimiMachineImageCollection) this.context.convertToCimi(new ArrayList<MachineImage>(),
            CimiMachineImageCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiMachineImageCollection();
        cimi.setArray(new CimiMachineImage[] {new CimiMachineImage(), new CimiMachineImage()});

        service = (List<MachineImage>) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.size());

        // Full Service -> Cimi
        MachineImage machineImage1 = new MachineImage();
        machineImage1.setId(1);
        machineImage1.setName("nameOne");
        MachineImage machineImage2 = new MachineImage();
        machineImage2.setId(2);
        machineImage2.setName("nameTwo");
        MachineImage machineImage3 = new MachineImage();
        machineImage3.setId(3);
        machineImage3.setName("nameThree");

        service = new ArrayList<MachineImage>();
        service.add(machineImage1);
        service.add(machineImage2);
        service.add(machineImage3);

        cimi = (CimiMachineImageCollection) this.context.convertToCimi(service, CimiMachineImageCollection.class);
        Assert.assertEquals(3, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/1",
            cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/2",
            cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/3",
            cimi.getArray()[2].getHref());
        Assert.assertNull(cimi.getArray()[2].getId());
        Assert.assertNull(cimi.getArray()[2].getName());

        cimi = (CimiMachineImageCollection) this.context.convertToCimi(
            Arrays.asList(new MachineImage[] {machineImage3, machineImage1}), CimiMachineImageCollection.class);
        Assert.assertEquals(2, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/3",
            cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/1",
            cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
    }

    // TODO Volumes, Network, ...
    // FIXME Disk collection
    @Test
    public void testCimiMachine() throws Exception {
        CimiMachine cimi;
        Machine service;

        // Empty Cimi -> Service
        service = (Machine) this.context.convertToService(new CimiMachine());
        Assert.assertNull(service.getCpu());
        Assert.assertNull(service.getMemory());
        Assert.assertNull(service.getState());
        Assert.assertNull(service.getDisks());

        // Empty Service -> Cimi
        cimi = (CimiMachine) this.context.convertToCimi(new Machine(), CimiMachine.class);
        Assert.assertNull(cimi.getCpu());
        Assert.assertNotNull(cimi.getDisks());
        Assert.assertNull(cimi.getDisks().getCollection());
        Assert.assertNull(cimi.getMemory());
        Assert.assertNull(cimi.getState());

        // Full Cimi -> Service : without collections
        cimi = new CimiMachine();
        cimi.setCpu(new CimiCpu());
        cimi.setMemory(new CimiMemory());
        cimi.setState("state");

        service = (Machine) this.context.convertToService(cimi);
        Assert.assertEquals(Cpu.class, service.getCpu().getClass());
        Assert.assertEquals(Memory.class, service.getMemory().getClass());
        Assert.assertNull(service.getState());

        // Full Cimi -> Service : Empty MachineDisk Collection
        cimi = new CimiMachine();
        cimi.setDisks(new CimiMachineDiskCollection());

        service = (Machine) this.context.convertToService(cimi);
        Assert.assertNotNull(service.getDisks());
        Assert.assertNull(cimi.getDisks().getCollection());

        // Full Cimi -> Service : Full MachineDisk Collection
        cimi = new CimiMachine();
        CimiMachineDiskCollection cimiMachineDisks = new CimiMachineDiskCollection();
        cimiMachineDisks.add(new CimiMachineDisk());
        cimiMachineDisks.add(new CimiMachineDisk());
        cimi.setDisks(cimiMachineDisks);

        service = (Machine) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.getDisks().size());

        // Full Service -> Cimi : without collections
        service = new Machine();
        service.setCpu(new Cpu());
        service.setMemory(new Memory());
        service.setState(Machine.State.CREATING);

        cimi = (CimiMachine) this.context.convertToCimi(service, CimiMachine.class);
        Assert.assertEquals(CimiCpu.class, cimi.getCpu().getClass());
        Assert.assertEquals(CimiMemory.class, cimi.getMemory().getClass());
        Assert.assertEquals(Machine.State.CREATING.toString(), cimi.getState());
        Assert.assertNull(cimi.getDisks().getCollection());

        // Full Service -> Cimi : Empty MachineDisk Collection
        service = new Machine();
        service.setDisks(new ArrayList<MachineDisk>());

        cimi = (CimiMachine) this.context.convertToCimi(service, CimiMachine.class);
        Assert.assertNotNull(cimi.getDisks());
        Assert.assertNull(cimi.getDisks().getCollection());

        // Full Service -> Cimi : with MachineDisks
        service = new Machine();
        service.setDisks(new ArrayList<MachineDisk>());
        service.getDisks().add(new MachineDisk());
        service.getDisks().add(new MachineDisk());
        service.getDisks().add(new MachineDisk());

        cimi = (CimiMachine) this.context.convertToCimi(service, CimiMachine.class);
        Assert.assertEquals(3, cimi.getDisks().getCollection().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCimiMachineCollection() throws Exception {
        CimiMachineCollection cimi;
        List<Machine> service;

        // Empty Cimi -> Service
        service = (List<Machine>) this.context.convertToService(new CimiMachineCollection());
        Assert.assertNotNull(service);
        Assert.assertEquals(0, service.size());

        // Empty Service -> Cimi
        cimi = (CimiMachineCollection) this.context.convertToCimi(new ArrayList<Machine>(), CimiMachineCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiMachineCollection();
        cimi.setArray(new CimiMachine[] {new CimiMachine(), new CimiMachine()});

        service = (List<Machine>) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.size());

        // Full Service -> Cimi
        Machine Machine1 = new Machine();
        Machine1.setId(1);
        Machine1.setName("nameOne");
        Machine Machine2 = new Machine();
        Machine2.setId(2);
        Machine2.setName("nameTwo");
        Machine Machine3 = new Machine();
        Machine3.setId(3);
        Machine3.setName("nameThree");

        service = new ArrayList<Machine>();
        service.add(Machine1);
        service.add(Machine2);
        service.add(Machine3);

        cimi = (CimiMachineCollection) this.context.convertToCimi(service, CimiMachineCollection.class);
        Assert.assertEquals(3, cimi.getArray().length);
        Assert
            .assertEquals(this.request.getBaseUri() + ExchangeType.Machine.getPathname() + "/1", cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert
            .assertEquals(this.request.getBaseUri() + ExchangeType.Machine.getPathname() + "/2", cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
        Assert
            .assertEquals(this.request.getBaseUri() + ExchangeType.Machine.getPathname() + "/3", cimi.getArray()[2].getHref());
        Assert.assertNull(cimi.getArray()[2].getId());
        Assert.assertNull(cimi.getArray()[2].getName());

        cimi = (CimiMachineCollection) this.context.convertToCimi(Arrays.asList(new Machine[] {Machine3, Machine1}),
            CimiMachineCollection.class);
        Assert.assertEquals(2, cimi.getArray().length);
        Assert
            .assertEquals(this.request.getBaseUri() + ExchangeType.Machine.getPathname() + "/3", cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert
            .assertEquals(this.request.getBaseUri() + ExchangeType.Machine.getPathname() + "/1", cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
    }

    // TODO Volumes, Network, ...
    @Test
    public void testCimiMachineTemplate() throws Exception {
        CimiMachineTemplate cimi;
        MachineTemplate service;

        // Empty Cimi -> Service
        service = (MachineTemplate) this.context.convertToService(new CimiMachineTemplate());
        Assert.assertNull(service.getCredentials());
        Assert.assertNull(service.getMachineConfiguration());
        Assert.assertNull(service.getMachineImage());

        // Empty Service -> Cimi
        cimi = (CimiMachineTemplate) this.context.convertToCimi(new MachineTemplate(), CimiMachineTemplate.class);
        Assert.assertNull(cimi.getCredentials());
        Assert.assertNull(cimi.getMachineConfig());
        Assert.assertNull(cimi.getMachineImage());

        // Full Cimi -> Service
        cimi = new CimiMachineTemplate();
        cimi.setCredentials(new CimiCredentials());
        cimi.setMachineConfig(new CimiMachineConfiguration());
        cimi.setMachineImage(new CimiMachineImage());

        service = (MachineTemplate) this.context.convertToService(cimi);
        Assert.assertEquals(Credentials.class, service.getCredentials().getClass());
        Assert.assertEquals(MachineConfiguration.class, service.getMachineConfiguration().getClass());
        Assert.assertEquals(MachineImage.class, service.getMachineImage().getClass());

        // Full Service -> Cimi
        service = new MachineTemplate();
        service.setCredentials(new Credentials());
        service.setMachineConfiguration(new MachineConfiguration());
        service.setMachineImage(new MachineImage());

        cimi = (CimiMachineTemplate) this.context.convertToCimi(service, CimiMachineTemplate.class);
        Assert.assertEquals(CimiCredentials.class, cimi.getCredentials().getClass());
        Assert.assertEquals(CimiMachineConfiguration.class, cimi.getMachineConfig().getClass());
        Assert.assertEquals(CimiMachineImage.class, cimi.getMachineImage().getClass());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCimiMachineTemplateCollection() throws Exception {
        CimiMachineTemplateCollection cimi;
        List<MachineTemplate> service;

        // Empty Cimi -> Service
        service = (List<MachineTemplate>) this.context.convertToService(new CimiMachineTemplateCollection());
        Assert.assertNotNull(service);
        Assert.assertEquals(0, service.size());

        // Empty Service -> Cimi
        cimi = (CimiMachineTemplateCollection) this.context.convertToCimi(new ArrayList<MachineTemplate>(),
            CimiMachineTemplateCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiMachineTemplateCollection();
        cimi.setArray(new CimiMachineTemplate[] {new CimiMachineTemplate(), new CimiMachineTemplate()});

        service = (List<MachineTemplate>) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.size());

        // Full Service -> Cimi
        MachineTemplate MachineTemplate1 = new MachineTemplate();
        MachineTemplate1.setId(1);
        MachineTemplate1.setName("nameOne");
        MachineTemplate MachineTemplate2 = new MachineTemplate();
        MachineTemplate2.setId(2);
        MachineTemplate2.setName("nameTwo");
        MachineTemplate MachineTemplate3 = new MachineTemplate();
        MachineTemplate3.setId(3);
        MachineTemplate3.setName("nameThree");

        service = new ArrayList<MachineTemplate>();
        service.add(MachineTemplate1);
        service.add(MachineTemplate2);
        service.add(MachineTemplate3);

        cimi = (CimiMachineTemplateCollection) this.context.convertToCimi(service, CimiMachineTemplateCollection.class);
        Assert.assertEquals(3, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathname() + "/1",
            cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathname() + "/2",
            cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathname() + "/3",
            cimi.getArray()[2].getHref());
        Assert.assertNull(cimi.getArray()[2].getId());
        Assert.assertNull(cimi.getArray()[2].getName());

        cimi = (CimiMachineTemplateCollection) this.context.convertToCimi(
            Arrays.asList(new MachineTemplate[] {MachineTemplate3, MachineTemplate1}), CimiMachineTemplateCollection.class);
        Assert.assertEquals(2, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathname() + "/3",
            cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathname() + "/1",
            cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
    }

    // TODO Volumes, Network, ...
    @Test
    public void testCimiMachineCreate() throws Exception {
        CimiMachineCreate cimi;
        MachineCreate service;

        // Empty Cimi -> Service
        service = (MachineCreate) this.context.convertToService(new CimiMachineCreate());
        Assert.assertNull(service.getMachineTemplate());

        // Full Cimi -> Service
        cimi = new CimiMachineCreate();
        cimi.setMachineTemplate(new CimiMachineTemplate());

        service = (MachineCreate) this.context.convertToService(cimi);
        Assert.assertEquals(MachineTemplate.class, service.getMachineTemplate().getClass());
    }

    @Test
    public void testCimiMachineDisk() throws Exception {
        CimiMachineDisk cimi;
        MachineDisk service;

        // Empty Cimi -> Service
        service = (MachineDisk) this.context.convertToService(new CimiMachineDisk());
        Assert.assertNull(service.getDisk());
        Assert.assertNull(service.getInitialLocation());

        // Empty Service -> Cimi
        cimi = (CimiMachineDisk) this.context.convertToCimi(new MachineDisk(), CimiMachineDisk.class);
        Assert.assertNull(cimi.getCapacity());
        Assert.assertNull(cimi.getInitialLocation());

        // Full Cimi -> Service
        cimi = new CimiMachineDisk();
        cimi.setCapacity(new CimiCapacity(5, StorageUnit.BYTE.getLabel()));
        cimi.setInitialLocation("initialLocation");

        service = (MachineDisk) this.context.convertToService(cimi);
        Assert.assertNotNull(service.getDisk());
        Assert.assertEquals(5, service.getDisk().getQuantity().longValue());
        Assert.assertEquals(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.BYTE, service.getDisk().getUnits());
        Assert.assertEquals("initialLocation", service.getInitialLocation());

        // Full Service -> Cimi
        service = new MachineDisk();
        Disk disk = new Disk();
        disk.setQuantity(7f);
        disk.setUnit(org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.MEGABYTE);
        service.setDisk(disk);
        service.setInitialLocation("initialLocation");

        cimi = (CimiMachineDisk) this.context.convertToCimi(service, CimiMachineDisk.class);
        Assert.assertEquals(7, cimi.getCapacity().getQuantity().intValue());
        Assert.assertEquals(StorageUnit.MEGABYTE.getLabel(), cimi.getCapacity().getUnits());
        Assert.assertEquals("initialLocation", cimi.getInitialLocation());
    }
}