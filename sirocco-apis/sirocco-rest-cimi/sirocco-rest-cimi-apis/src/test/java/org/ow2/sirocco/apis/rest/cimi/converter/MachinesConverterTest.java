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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.Type;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateCollection;
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
    public void testCimiMachineConfigurationCollection() throws Exception {
        CimiMachineConfigurationCollection cimi;
        MachineConfigurationCollection service;

        // Empty Cimi -> Service
        service = (MachineConfigurationCollection) this.context.convertToService(new CimiMachineConfigurationCollection());
        Assert.assertNull(service.getMachineConfigurations());

        // Empty Service -> Cimi
        cimi = (CimiMachineConfigurationCollection) this.context.convertToCimi(new MachineConfigurationCollection(),
            CimiMachineConfigurationCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiMachineConfigurationCollection();
        cimi.setArray(new CimiMachineConfiguration[] {new CimiMachineConfiguration(), new CimiMachineConfiguration()});

        service = (MachineConfigurationCollection) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.getMachineConfigurations().size());

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

        service = new MachineConfigurationCollection();
        service.setMachineConfigurations(Arrays.asList(new MachineConfiguration[] {machineConfiguration1,
            machineConfiguration2, machineConfiguration3}));

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
    public void testCimiMachineImageCollection() throws Exception {
        CimiMachineImageCollection cimi;
        MachineImageCollection service;

        // Empty Cimi -> Service
        service = (MachineImageCollection) this.context.convertToService(new CimiMachineImageCollection());
        Assert.assertNull(service.getImages());

        // Empty Service -> Cimi
        cimi = (CimiMachineImageCollection) this.context.convertToCimi(new MachineImageCollection(),
            CimiMachineImageCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiMachineImageCollection();
        cimi.setArray(new CimiMachineImage[] {new CimiMachineImage(), new CimiMachineImage()});

        service = (MachineImageCollection) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.getImages().size());

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

        service = new MachineImageCollection();
        service.setImages(Arrays.asList(new MachineImage[] {machineImage1, machineImage2, machineImage3}));

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
    @Test
    public void testCimiMachine() throws Exception {
        CimiMachine cimi;
        Machine service;

        // Empty Cimi -> Service
        service = (Machine) this.context.convertToService(new CimiMachine());
        Assert.assertNull(service.getCpu());
        Assert.assertNull(service.getMemory());
        Assert.assertNull(service.getState());
        // FIXME Disk collection
        // Assert.assertEquals(0, service.getDisks().size());

        // Empty Service -> Cimi
        cimi = (CimiMachine) this.context.convertToCimi(new Machine(), CimiMachine.class);
        Assert.assertNull(cimi.getCpu());
        Assert.assertNull(cimi.getDisks());
        Assert.assertNull(cimi.getMemory());
        Assert.assertNull(cimi.getState());

        // Full Cimi -> Service
        cimi = new CimiMachine();
        cimi.setCpu(new CimiCpu());
        cimi.setMemory(new CimiMemory());
        cimi.setState("state");

        service = (Machine) this.context.convertToService(cimi);
        Assert.assertEquals(Cpu.class, service.getCpu().getClass());
        Assert.assertEquals(Memory.class, service.getMemory().getClass());
        Assert.assertNull(service.getState());

        // Full Cimi -> Service
        cimi = new CimiMachine();
        cimi.setDisks(new CimiDisk[] {});

        service = (Machine) this.context.convertToService(cimi);
        // FIXME Disk collection
        // Assert.assertEquals(0, service.getDisks().size());

        // Full Cimi -> Service
        cimi = new CimiMachine();
        cimi.setDisks(new CimiDisk[] {new CimiDisk(), new CimiDisk()});

        service = (Machine) this.context.convertToService(cimi);
        // FIXME Disk collection
        // Assert.assertEquals(2, service.getDisks().size());

        // Full Service -> Cimi
        service = new Machine();
        service.setCpu(new Cpu());
        service.setMemory(new Memory());
        service.setState(Machine.State.CREATING);

        cimi = (CimiMachine) this.context.convertToCimi(service, CimiMachine.class);
        Assert.assertEquals(CimiCpu.class, cimi.getCpu().getClass());
        Assert.assertEquals(CimiMemory.class, cimi.getMemory().getClass());
        Assert.assertEquals(Machine.State.CREATING.toString(), cimi.getState());
        Assert.assertNull(cimi.getDisks());

        // Full Service -> Cimi
        service = new Machine();
        // FIXME Disk collection
        // service.setDisks(new ArrayList<Disk>());

        cimi = (CimiMachine) this.context.convertToCimi(service, CimiMachine.class);
        Assert.assertNull(cimi.getDisks());

        // Full Service -> Cimi
        service = new Machine();
        // FIXME Disk collection
        // service.setDisks(new ArrayList<Disk>(Arrays.asList(new Disk[] {new
        // Disk(), new Disk(), new Disk()})));

        cimi = (CimiMachine) this.context.convertToCimi(service, CimiMachine.class);
        // FIXME Disk collection
        // Assert.assertEquals(3, cimi.getDisks().length);
    }

    @Test
    public void testCimiMachineCollection() throws Exception {
        CimiMachineCollection cimi;
        MachineCollection service;

        // Empty Cimi -> Service
        service = (MachineCollection) this.context.convertToService(new CimiMachineCollection());
        Assert.assertNull(service.getMachines());

        // Empty Service -> Cimi
        cimi = (CimiMachineCollection) this.context.convertToCimi(new MachineCollection(), CimiMachineCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiMachineCollection();
        cimi.setArray(new CimiMachine[] {new CimiMachine(), new CimiMachine()});

        service = (MachineCollection) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.getMachines().size());

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

        service = new MachineCollection();
        service.setMachines(Arrays.asList(new Machine[] {Machine1, Machine2, Machine3}));

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
    public void testCimiMachineTemplateCollection() throws Exception {
        CimiMachineTemplateCollection cimi;
        MachineTemplateCollection service;

        // Empty Cimi -> Service
        service = (MachineTemplateCollection) this.context.convertToService(new CimiMachineTemplateCollection());
        Assert.assertNull(service.getMachineTemplates());

        // Empty Service -> Cimi
        cimi = (CimiMachineTemplateCollection) this.context.convertToCimi(new MachineTemplateCollection(),
            CimiMachineTemplateCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiMachineTemplateCollection();
        cimi.setArray(new CimiMachineTemplate[] {new CimiMachineTemplate(), new CimiMachineTemplate()});

        service = (MachineTemplateCollection) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.getMachineTemplates().size());

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

        service = new MachineTemplateCollection();
        service
            .setMachineTemplates(Arrays.asList(new MachineTemplate[] {MachineTemplate1, MachineTemplate2, MachineTemplate3}));

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
}