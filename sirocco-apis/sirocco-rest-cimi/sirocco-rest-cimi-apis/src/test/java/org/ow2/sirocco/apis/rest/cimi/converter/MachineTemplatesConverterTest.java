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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredential;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateVolumeTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiExpand;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.RequestParams;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converters tests of machines resources.
 */
public class MachineTemplatesConverterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MachineTemplatesConverterTest.class);

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
        Assert.assertNull(cimi.getCredential());
        Assert.assertNull(cimi.getMachineConfig());
        Assert.assertNull(cimi.getMachineImage());

        // Full Cimi -> Service : without arrays
        cimi = new CimiMachineTemplate();
        cimi.setCredential(new CimiCredential());
        cimi.setMachineConfig(new CimiMachineConfiguration());
        cimi.setMachineImage(new CimiMachineImage());

        service = (MachineTemplate) this.context.convertToService(cimi);
        Assert.assertEquals(Credentials.class, service.getCredentials().getClass());
        Assert.assertEquals(MachineConfiguration.class, service.getMachineConfiguration().getClass());
        Assert.assertEquals(MachineImage.class, service.getMachineImage().getClass());

        // Full Service -> Cimi : without arrays
        service = new MachineTemplate();
        service.setCredentials(new Credentials());
        service.getCredentials().setId(10);
        service.setMachineConfiguration(new MachineConfiguration());
        service.getMachineConfiguration().setId(11);
        service.setMachineImage(new MachineImage());
        service.getMachineImage().setId(12);

        cimi = (CimiMachineTemplate) this.context.convertToCimi(service, CimiMachineTemplate.class);
        Assert.assertEquals(CimiCredential.class, cimi.getCredential().getClass());
        Assert.assertEquals(CimiMachineConfiguration.class, cimi.getMachineConfig().getClass());
        Assert.assertEquals(CimiMachineImage.class, cimi.getMachineImage().getClass());

        // Full Cimi -> Service : with MachineVolume
        cimi = new CimiMachineTemplate();
        CimiMachineTemplateVolume cimiMVFMT_1 = new CimiMachineTemplateVolume();
        cimiMVFMT_1.setInitialLocation("initialLocation_1");
        cimiMVFMT_1.setName("name_1");
        CimiMachineTemplateVolume cimiMVFMT_2 = new CimiMachineTemplateVolume();
        cimiMVFMT_2.setInitialLocation("initialLocation_2");
        cimiMVFMT_2.setName("name_2");
        cimi.setVolumes(new CimiMachineTemplateVolume[] {cimiMVFMT_1, cimiMVFMT_2});

        service = (MachineTemplate) this.context.convertToService(cimi);
        Assert.assertNotNull(service.getVolumes());
        Assert.assertEquals(2, service.getVolumes().size());
        Assert.assertEquals("initialLocation_1", service.getVolumes().get(0).getInitialLocation());
        Assert.assertEquals("name_1", service.getVolumes().get(0).getVolume().getName());
        Assert.assertEquals("initialLocation_2", service.getVolumes().get(1).getInitialLocation());
        Assert.assertEquals("name_2", service.getVolumes().get(1).getVolume().getName());

        // Full Service -> Cimi : with MachineVolume
        service = new MachineTemplate();
        service.setId(10);
        service.setVolumes(new ArrayList<MachineVolume>());
        MachineVolume mv1 = new MachineVolume();
        mv1.setId(11);
        mv1.setInitialLocation("initialLocation_1");
        mv1.setVolume(new Volume());
        mv1.getVolume().setId(111);
        mv1.getVolume().setName("name_1");
        service.getVolumes().add(mv1);
        MachineVolume mv2 = new MachineVolume();
        mv2.setId(12);
        mv2.setInitialLocation("initialLocation_2");
        mv2.setVolume(new Volume());
        mv2.getVolume().setId(121);
        mv2.getVolume().setName("name_2");
        service.getVolumes().add(mv2);

        // expand = *
        this.request.getParams().setCimiExpand(new CimiExpand("*"));
        cimi = (CimiMachineTemplate) this.context.convertToCimi(service, CimiMachineTemplate.class);

        Assert.assertNotNull(cimi.getVolumes());
        Assert.assertEquals(2, cimi.getVolumes().length);
        Assert.assertEquals("initialLocation_1", cimi.getVolumes()[0].getInitialLocation());
        Assert.assertEquals("name_1", cimi.getVolumes()[0].getName());
        Assert.assertEquals("initialLocation_2", cimi.getVolumes()[1].getInitialLocation());
        Assert.assertEquals("name_2", cimi.getVolumes()[1].getName());

        // Full Cimi -> Service : with MachineVolumeTemplate
        cimi = new CimiMachineTemplate();
        service.setId(10);
        CimiMachineTemplateVolumeTemplate cimiMTVT_1 = new CimiMachineTemplateVolumeTemplate();
        cimiMTVT_1.setInitialLocation("initialLocation_1");
        cimiMTVT_1.setName("name_1");
        CimiMachineTemplateVolumeTemplate cimiMTVT_2 = new CimiMachineTemplateVolumeTemplate();
        cimiMTVT_2.setInitialLocation("initialLocation_2");
        cimiMTVT_2.setName("name_2");
        cimi.setVolumeTemplates(new CimiMachineTemplateVolumeTemplate[] {cimiMTVT_1, cimiMTVT_2});

        service = (MachineTemplate) this.context.convertToService(cimi);
        Assert.assertNotNull(service.getVolumeTemplates());
        Assert.assertEquals(2, service.getVolumeTemplates().size());
        Assert.assertEquals("initialLocation_1", service.getVolumeTemplates().get(0).getInitialLocation());
        Assert.assertEquals("name_1", service.getVolumeTemplates().get(0).getVolumeTemplate().getName());
        Assert.assertEquals("initialLocation_2", service.getVolumeTemplates().get(1).getInitialLocation());
        Assert.assertEquals("name_2", service.getVolumeTemplates().get(1).getVolumeTemplate().getName());

        // Full Service -> Cimi : with MachineVolumeTemplate
        service = new MachineTemplate();
        service.setId(10);
        service.setVolumeTemplates(new ArrayList<MachineVolumeTemplate>());
        MachineVolumeTemplate mvt1 = new MachineVolumeTemplate();
        mvt1.setInitialLocation("initialLocation_1");
        mvt1.setVolumeTemplate(new VolumeTemplate());
        mvt1.getVolumeTemplate().setId(111);
        mvt1.getVolumeTemplate().setName("name_1");
        service.getVolumeTemplates().add(mvt1);
        MachineVolumeTemplate mvt2 = new MachineVolumeTemplate();
        mvt2.setInitialLocation("initialLocation_2");
        mvt2.setVolumeTemplate(new VolumeTemplate());
        mvt2.getVolumeTemplate().setId(121);
        mvt2.getVolumeTemplate().setName("name_2");
        service.getVolumeTemplates().add(mvt2);

        cimi = (CimiMachineTemplate) this.context.convertToCimi(service, CimiMachineTemplate.class);
        Assert.assertNotNull(cimi.getVolumeTemplates());
        Assert.assertEquals(2, cimi.getVolumeTemplates().length);
        Assert.assertEquals("initialLocation_1", cimi.getVolumeTemplates()[0].getInitialLocation());
        Assert.assertEquals("name_1", cimi.getVolumeTemplates()[0].getName());
        Assert.assertEquals("initialLocation_2", cimi.getVolumeTemplates()[1].getInitialLocation());
        Assert.assertEquals("name_2", cimi.getVolumeTemplates()[1].getName());

    }

    @Test
    public void testCimiMachineTemplateExpand() throws Exception {
        CimiMachineTemplate cimi;
        MachineTemplate service;

        // Prepare Trace
        Writer strWriter;
        ObjectMapper mapper = new ObjectMapper();
        JAXBContext context = JAXBContext.newInstance(CimiMachineTemplate.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        // Prepare service
        service = new MachineTemplate();
        service.setId(10);

        service.setCredentials(new Credentials());
        service.getCredentials().setId(100);
        service.getCredentials().setName("Credential_Name_100");
        service.getCredentials().setUserName("Credential_UserName_100");

        service.setMachineConfiguration(new MachineConfiguration());
        service.getMachineConfiguration().setId(101);
        service.getMachineConfiguration().setName("MachineConfiguration_Name_101");
        service.getMachineConfiguration().setCpu(101);

        service.setMachineImage(new MachineImage());
        service.getMachineImage().setId(102);
        service.getMachineImage().setName("MachineImage_Name_102");
        service.getMachineImage().setImageLocation("MachineImage_ImageLocation_102");

        service.setVolumes(new ArrayList<MachineVolume>());
        service.getVolumes().add(new MachineVolume());
        service.getVolumes().get(0).setId(11);
        service.getVolumes().get(0).setInitialLocation("initialLocation_1");
        service.getVolumes().get(0).setVolume(new Volume());
        service.getVolumes().get(0).getVolume().setId(111);
        service.getVolumes().get(0).getVolume().setName("name_1");
        service.getVolumes().add(new MachineVolume());
        service.getVolumes().get(1).setId(12);
        service.getVolumes().get(1).setInitialLocation("initialLocation_2");
        service.getVolumes().get(1).setVolume(new Volume());
        service.getVolumes().get(1).getVolume().setId(121);
        service.getVolumes().get(1).getVolume().setName("name_2");

        service.setVolumeTemplates(new ArrayList<MachineVolumeTemplate>());
        service.getVolumeTemplates().add(new MachineVolumeTemplate());
        service.getVolumeTemplates().get(0).setInitialLocation("initialLocation_1");
        service.getVolumeTemplates().get(0).setVolumeTemplate(new VolumeTemplate());
        service.getVolumeTemplates().get(0).getVolumeTemplate().setId(111);
        service.getVolumeTemplates().get(0).getVolumeTemplate().setName("name_1");
        service.getVolumeTemplates().add(new MachineVolumeTemplate());
        service.getVolumeTemplates().get(1).setInitialLocation("initialLocation_2");
        service.getVolumeTemplates().get(1).setVolumeTemplate(new VolumeTemplate());
        service.getVolumeTemplates().get(1).getVolumeTemplate().setId(121);
        service.getVolumeTemplates().get(1).getVolumeTemplate().setName("name_2");

        // ---------------------------------
        // not expand
        this.request.getParams().setCimiExpand(new CimiExpand());
        cimi = (CimiMachineTemplate) this.context.convertToCimi(service, CimiMachineTemplate.class);

        // Trace
        strWriter = new StringWriter();
        mapper.writeValue(strWriter, cimi);
        MachineTemplatesConverterTest.LOGGER.debug("JSON:\n\t{}", strWriter);
        strWriter = new StringWriter();
        m.marshal(cimi, strWriter);
        MachineTemplatesConverterTest.LOGGER.debug("XML:\n\t{}", strWriter);

        // ---------------------------------
        // expand = *
        this.request.getParams().setCimiExpand(new CimiExpand("*"));
        cimi = (CimiMachineTemplate) this.context.convertToCimi(service, CimiMachineTemplate.class);

        // Trace
        strWriter = new StringWriter();
        mapper.writeValue(strWriter, cimi);
        MachineTemplatesConverterTest.LOGGER.debug("JSON:\n\t{}", strWriter);
        strWriter = new StringWriter();
        m.marshal(cimi, strWriter);
        MachineTemplatesConverterTest.LOGGER.debug("XML:\n\t{}", strWriter);

        Assert.assertNotNull(cimi.getVolumes());
        Assert.assertEquals(2, cimi.getVolumes().length);
        Assert.assertEquals("initialLocation_1", cimi.getVolumes()[0].getInitialLocation());
        Assert.assertEquals("name_1", cimi.getVolumes()[0].getName());
        Assert.assertEquals("initialLocation_2", cimi.getVolumes()[1].getInitialLocation());
        Assert.assertEquals("name_2", cimi.getVolumes()[1].getName());

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
        Assert.assertNotNull(cimi.getArray()[0].getId());
        Assert.assertNotNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathname() + "/2",
            cimi.getArray()[1].getHref());
        Assert.assertNotNull(cimi.getArray()[1].getId());
        Assert.assertNotNull(cimi.getArray()[1].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathname() + "/3",
            cimi.getArray()[2].getHref());
        Assert.assertNotNull(cimi.getArray()[2].getId());
        Assert.assertNotNull(cimi.getArray()[2].getName());

        cimi = (CimiMachineTemplateCollection) this.context.convertToCimi(
            Arrays.asList(new MachineTemplate[] {MachineTemplate3, MachineTemplate1}), CimiMachineTemplateCollection.class);
        Assert.assertEquals(2, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathname() + "/3",
            cimi.getArray()[0].getHref());
        Assert.assertNotNull(cimi.getArray()[0].getId());
        Assert.assertNotNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathname() + "/1",
            cimi.getArray()[1].getHref());
        Assert.assertNotNull(cimi.getArray()[1].getId());
        Assert.assertNotNull(cimi.getArray()[1].getName());
    }
}