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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineConfigurationCollectionRoot;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiExpand;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.CimiStringParams;
import org.ow2.sirocco.apis.rest.cimi.request.RequestParams;
import org.ow2.sirocco.cloudmanager.model.cimi.DiskTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converters tests of machines resources.
 */
public class MachineConfigurationsConverterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MachineConfigurationsConverterTest.class);

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
        cimi.setCpu(3);
        cimi.setMemory(1024);

        service = (MachineConfiguration) this.context.convertToService(cimi);
        Assert.assertEquals(3, service.getCpu().intValue());
        Assert.assertEquals(1024, service.getMemory().intValue());
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
        service.setCpu(1);
        service.setMemory(Integer.MAX_VALUE);

        cimi = (CimiMachineConfiguration) this.context.convertToCimi(service, CimiMachineConfiguration.class);
        Assert.assertEquals(1, cimi.getCpu().intValue());
        Assert.assertEquals(Integer.MAX_VALUE, cimi.getMemory().intValue());
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
        Assert.assertNotNull(cimi.getArray()[0].getId());
        Assert.assertNotNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineConfiguration.getPathname() + "/2",
            cimi.getArray()[1].getHref());
        Assert.assertNotNull(cimi.getArray()[1].getId());
        Assert.assertNotNull(cimi.getArray()[1].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineConfiguration.getPathname() + "/3",
            cimi.getArray()[2].getHref());
        Assert.assertNotNull(cimi.getArray()[2].getId());
        Assert.assertNotNull(cimi.getArray()[2].getName());

        cimi = (CimiMachineConfigurationCollection) this.context.convertToCimi(
            Arrays.asList(new MachineConfiguration[] {machineConfiguration3, machineConfiguration1}),
            CimiMachineConfigurationCollection.class);
        Assert.assertEquals(2, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineConfiguration.getPathname() + "/3",
            cimi.getArray()[0].getHref());
        Assert.assertNotNull(cimi.getArray()[0].getId());
        Assert.assertNotNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineConfiguration.getPathname() + "/1",
            cimi.getArray()[1].getHref());
        Assert.assertNotNull(cimi.getArray()[1].getId());
        Assert.assertNotNull(cimi.getArray()[1].getName());
    }

    @Test
    public void testCimiMachineConfigurationCollectionExpand() throws Exception {
        CimiMachineConfigurationCollectionRoot cimi;
        List<MachineConfiguration> service;

        // Prepare Trace
        Writer strWriter;
        ObjectMapper mapper = new ObjectMapper();
        JAXBContext context = JAXBContext.newInstance(CimiMachineConfigurationCollectionRoot.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

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

        // ---------------------------------------------------
        // Prepare request
        this.request.getParams().setCimiExpand(new CimiExpand(CimiStringParams.ALL));

        // Convert
        cimi = (CimiMachineConfigurationCollectionRoot) this.context.convertToCimi(service,
            CimiMachineConfigurationCollectionRoot.class);

        // Trace
        strWriter = new StringWriter();
        mapper.writeValue(strWriter, cimi);
        MachineConfigurationsConverterTest.LOGGER.debug("JSON:\n\t{}", strWriter);
        strWriter = new StringWriter();
        m.marshal(cimi, strWriter);
        MachineConfigurationsConverterTest.LOGGER.debug("XML:\n\t{}", strWriter);

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

        // ---------------------------------------------------
        // Prepare request
        this.request.getParams().setCimiExpand(new CimiExpand());

        // Convert
        cimi = (CimiMachineConfigurationCollectionRoot) this.context.convertToCimi(service,
            CimiMachineConfigurationCollectionRoot.class);

        // Trace
        strWriter = new StringWriter();
        mapper.writeValue(strWriter, cimi);
        MachineConfigurationsConverterTest.LOGGER.debug("JSON:\n\t{}", strWriter);
        strWriter = new StringWriter();
        m.marshal(cimi, strWriter);
        MachineConfigurationsConverterTest.LOGGER.debug("XML:\n\t{}", strWriter);

        // Verify
        Assert.assertEquals(3, cimi.getArray().length);

        Assert.assertEquals(ExchangeType.MachineConfiguration.makeHref(this.request.getBaseUri(), "1"),
            cimi.getArray()[0].getHref());
        Assert.assertNotNull(cimi.getArray()[0].getId());
        Assert.assertNotNull(cimi.getArray()[0].getName());

        Assert.assertEquals(ExchangeType.MachineConfiguration.makeHref(this.request.getBaseUri(), "2"),
            cimi.getArray()[1].getHref());
        Assert.assertNotNull(cimi.getArray()[1].getId());
        Assert.assertNotNull(cimi.getArray()[1].getName());

        Assert.assertEquals(ExchangeType.MachineConfiguration.makeHref(this.request.getBaseUri(), "3"),
            cimi.getArray()[2].getHref());
        Assert.assertNotNull(cimi.getArray()[2].getId());
        Assert.assertNotNull(cimi.getArray()[2].getName());

        // ---------------------------------------------------
        // Prepare request
        this.request.getParams().setCimiExpand(new CimiExpand("machineConfigurations"));

        // Convert
        cimi = (CimiMachineConfigurationCollectionRoot) this.context.convertToCimi(service,
            CimiMachineConfigurationCollectionRoot.class);

        // Trace
        strWriter = new StringWriter();
        mapper.writeValue(strWriter, cimi);
        MachineConfigurationsConverterTest.LOGGER.debug("JSON:\n\t{}", strWriter);
        strWriter = new StringWriter();
        m.marshal(cimi, strWriter);
        MachineConfigurationsConverterTest.LOGGER.debug("XML:\n\t{}", strWriter);

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
}