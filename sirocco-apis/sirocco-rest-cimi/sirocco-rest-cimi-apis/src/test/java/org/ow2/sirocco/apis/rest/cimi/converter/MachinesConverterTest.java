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

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiExpand;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.RequestParams;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineDisk;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converters tests of machines resources.
 */
public class MachinesConverterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MachinesConverterTest.class);

    private CimiRequest request;

    private CimiContext context;

    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setBaseUri("http://www.test.org/");
        RequestParams header = new RequestParams();
        header.setCimiSelect(new CimiSelect());
        header.setCimiExpand(new CimiExpand());
        this.request.setParams(header);

        this.context = new CimiContextImpl(this.request, new CimiResponse());
    }

    // TODO Volumes, Network, ...
    // FIXME Disk collection
    @Test
    public void testCimiMachine() throws Exception {
        CimiMachine cimi;
        Machine service;
        MachineDisk machineDisk;

        // Empty Cimi -> Service
        service = (Machine) this.context.convertToService(new CimiMachine());
        Assert.assertNull(service.getCpu());
        Assert.assertNull(service.getMemory());
        Assert.assertNull(service.getState());
        Assert.assertNotNull(service.getDisks());
        Assert.assertEquals(0, service.getDisks().size());
        Assert.assertNotNull(service.getNetworkInterfaces());
        Assert.assertEquals(0, service.getNetworkInterfaces().size());
        Assert.assertNotNull(service.getVolumes());
        Assert.assertEquals(0, service.getVolumes().size());

        // Empty Service -> Cimi
        cimi = (CimiMachine) this.context.convertToCimi(new Machine(), CimiMachine.class);
        Assert.assertNull(cimi.getCpu());
        Assert.assertNotNull(cimi.getDisks());
        Assert.assertNull(cimi.getDisks().getCollection());
        Assert.assertNull(cimi.getMemory());
        Assert.assertNull(cimi.getState());

        // Full Cimi -> Service
        cimi = new CimiMachine();
        cimi.setCpu(3);
        cimi.setMemory(1024);
        cimi.setState("state");

        service = (Machine) this.context.convertToService(cimi);
        Assert.assertEquals(3, service.getCpu().intValue());
        Assert.assertEquals(1024, service.getMemory().intValue());
        Assert.assertNull(service.getState());
        Assert.assertEquals(0, service.getDisks().size());
        Assert.assertEquals(0, service.getNetworkInterfaces().size());
        Assert.assertEquals(0, service.getVolumes().size());

        // Full Service -> Cimi : without collections
        service = new Machine();
        service.setCpu(2);
        service.setMemory(512);
        service.setState(Machine.State.CREATING);

        cimi = (CimiMachine) this.context.convertToCimi(service, CimiMachine.class);
        Assert.assertEquals(2, cimi.getCpu().intValue());
        Assert.assertEquals(512, cimi.getMemory().intValue());
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
        service.setId(7);
        service.setDisks(new ArrayList<MachineDisk>());
        machineDisk = new MachineDisk();
        machineDisk.setId(111);
        machineDisk.setCapacity(111);
        machineDisk.setName("Disk111");
        service.getDisks().add(machineDisk);
        machineDisk = new MachineDisk();
        machineDisk.setId(222);
        machineDisk.setCapacity(222);
        machineDisk.setName("Disk222");
        service.getDisks().add(machineDisk);
        machineDisk = new MachineDisk();
        machineDisk.setId(333);
        machineDisk.setCapacity(333);
        machineDisk.setName("Disk333");
        service.getDisks().add(machineDisk);

    }

    @Test
    public void testCimiMachineExpand() throws Exception {
        CimiMachine cimi;
        Machine service;
        MachineDisk machineDisk;
        MachineVolume machineVolume;

        // Prepare Trace
        Writer strWriter;
        ObjectMapper mapper = new ObjectMapper();
        JAXBContext context = JAXBContext.newInstance(CimiMachine.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        // Build Service Machine
        service = new Machine();
        service.setId(7);

        service.setDisks(new ArrayList<MachineDisk>());
        machineDisk = new MachineDisk();
        machineDisk.setId(111);
        machineDisk.setCapacity(111);
        machineDisk.setName("Disk111");
        service.getDisks().add(machineDisk);
        machineDisk = new MachineDisk();
        machineDisk.setId(222);
        machineDisk.setCapacity(222);
        machineDisk.setName("Disk222");
        service.getDisks().add(machineDisk);
        machineDisk = new MachineDisk();
        machineDisk.setId(333);
        machineDisk.setCapacity(333);
        machineDisk.setName("Disk333");
        service.getDisks().add(machineDisk);

        service.setVolumes(new ArrayList<MachineVolume>());
        machineVolume = new MachineVolume();
        machineVolume.setId(1119);
        machineVolume.setInitialLocation("InitialLoc1119");
        machineVolume.setName("Volume1119");
        service.getVolumes().add(machineVolume);
        machineVolume = new MachineVolume();
        machineVolume.setId(2229);
        machineVolume.setInitialLocation("InitialLoc2229");
        machineVolume.setName("Volume2229");
        service.getVolumes().add(machineVolume);

        // no expand
        this.request.getParams().setCimiExpand(new CimiExpand());
        cimi = (CimiMachine) this.context.convertToCimi(service, CimiMachine.class);
        Assert.assertNotNull(cimi.getDisks().getHref());
        Assert.assertNull(cimi.getDisks().getId());

        Assert.assertNotNull(cimi.getVolumes().getHref());
        Assert.assertNull(cimi.getVolumes().getId());

        Assert.assertNotNull(cimi.getNetworkInterfaces().getHref());
        Assert.assertNull(cimi.getNetworkInterfaces().getId());

        // Trace
        strWriter = new StringWriter();
        mapper.writeValue(strWriter, cimi);
        MachinesConverterTest.LOGGER.debug("JSON:\n\t{}", strWriter);
        strWriter = new StringWriter();
        m.marshal(cimi, strWriter);
        MachinesConverterTest.LOGGER.debug("XML:\n\t{}", strWriter);

        // ----------------------------------
        // expand = *
        this.request.getParams().setCimiExpand(new CimiExpand("*"));
        cimi = (CimiMachine) this.context.convertToCimi(service, CimiMachine.class);

        // Trace
        strWriter = new StringWriter();
        mapper.writeValue(strWriter, cimi);
        MachinesConverterTest.LOGGER.debug("JSON:\n\t{}", strWriter);
        strWriter = new StringWriter();
        m.marshal(cimi, strWriter);
        MachinesConverterTest.LOGGER.debug("XML:\n\t{}", strWriter);

        // Verify
        Assert.assertNotNull(cimi.getDisks().getHref());
        Assert.assertNotNull(cimi.getDisks().getId());
        Assert.assertEquals(3, cimi.getDisks().getCollection().size());
        Assert.assertNotNull(cimi.getDisks().getCollection().get(0).getHref());
        Assert.assertNull(cimi.getDisks().getCollection().get(0).getId());

        Assert.assertNotNull(cimi.getVolumes().getHref());
        Assert.assertNotNull(cimi.getVolumes().getId());
        Assert.assertEquals(2, cimi.getVolumes().getCollection().size());
        Assert.assertNotNull(cimi.getVolumes().getCollection().get(0).getHref());
        Assert.assertNull(cimi.getVolumes().getCollection().get(0).getId());

        Assert.assertNotNull(cimi.getNetworkInterfaces().getHref());
        Assert.assertNotNull(cimi.getNetworkInterfaces().getId());

        // ----------------------------------
        // expand = volumes
        this.request.getParams().setCimiExpand(new CimiExpand("volumes"));
        cimi = (CimiMachine) this.context.convertToCimi(service, CimiMachine.class);
        Assert.assertNotNull(cimi.getDisks().getHref());
        Assert.assertNull(cimi.getDisks().getId());

        Assert.assertNotNull(cimi.getVolumes().getHref());
        Assert.assertNotNull(cimi.getVolumes().getId());
        Assert.assertEquals(2, cimi.getVolumes().getCollection().size());

        Assert.assertNotNull(cimi.getNetworkInterfaces().getHref());
        Assert.assertNull(cimi.getNetworkInterfaces().getId());

        // Trace
        strWriter = new StringWriter();
        mapper.writeValue(strWriter, cimi);
        MachinesConverterTest.LOGGER.debug("JSON:\n\t{}", strWriter);
        strWriter = new StringWriter();
        m.marshal(cimi, strWriter);
        MachinesConverterTest.LOGGER.debug("XML:\n\t{}", strWriter);

        // ----------------------------------
        // expand = disks
        this.request.getParams().setCimiExpand(new CimiExpand("disks"));
        cimi = (CimiMachine) this.context.convertToCimi(service, CimiMachine.class);
        Assert.assertNotNull(cimi.getDisks().getHref());
        Assert.assertNotNull(cimi.getDisks().getId());
        Assert.assertEquals(3, cimi.getDisks().getCollection().size());

        Assert.assertNotNull(cimi.getVolumes().getHref());
        Assert.assertNull(cimi.getVolumes().getId());

        Assert.assertNotNull(cimi.getNetworkInterfaces().getHref());
        Assert.assertNull(cimi.getNetworkInterfaces().getId());

        // Trace
        strWriter = new StringWriter();
        mapper.writeValue(strWriter, cimi);
        MachinesConverterTest.LOGGER.debug("JSON:\n\t{}", strWriter);
        strWriter = new StringWriter();
        m.marshal(cimi, strWriter);
        MachinesConverterTest.LOGGER.debug("XML:\n\t{}", strWriter);

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

    @Test
    @SuppressWarnings("unchecked")
    public void testCimiMachineCollectionRoot() throws Exception {
        CimiMachineCollection cimi;
        List<Machine> service;

        // Empty Cimi -> Service
        service = (List<Machine>) this.context.convertToService(new CimiMachineCollectionRoot());
        Assert.assertNotNull(service);
        Assert.assertEquals(0, service.size());

        // Empty Service -> Cimi
        cimi = (CimiMachineCollectionRoot) this.context
            .convertToCimi(new ArrayList<Machine>(), CimiMachineCollectionRoot.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiMachineCollectionRoot();
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

        cimi = (CimiMachineCollectionRoot) this.context.convertToCimi(service, CimiMachineCollectionRoot.class);
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
        Assert.assertNull(service.getCapacity());
        Assert.assertNull(service.getInitialLocation());

        // Empty Service -> Cimi
        cimi = (CimiMachineDisk) this.context.convertToCimi(new MachineDisk(), CimiMachineDisk.class);
        Assert.assertNull(cimi.getCapacity());
        Assert.assertNull(cimi.getInitialLocation());

        // Full Cimi -> Service
        cimi = new CimiMachineDisk();
        cimi.setCapacity(5);
        cimi.setInitialLocation("initialLocation");

        service = (MachineDisk) this.context.convertToService(cimi);
        Assert.assertEquals(5, service.getCapacity().intValue());
        Assert.assertEquals("initialLocation", service.getInitialLocation());

        // Full Service -> Cimi
        service = new MachineDisk();
        service.setCapacity(7);
        service.setInitialLocation("initialLocation");

        cimi = (CimiMachineDisk) this.context.convertToCimi(service, CimiMachineDisk.class);
        Assert.assertEquals(7, cimi.getCapacity().intValue());
        Assert.assertEquals("initialLocation", cimi.getInitialLocation());
    }
}