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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.domain.CloudEntryPointAggregate;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiExpand;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.CimiStringParams;
import org.ow2.sirocco.apis.rest.cimi.request.RequestParams;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converters tests of CloudEntryPoint resources.
 */
public class CloudEntryPointConverterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEntryPointConverterTest.class);

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

    private CloudEntryPointAggregate buildFull() {
        CloudEntryPointAggregate service;
        service = new CloudEntryPointAggregate(new CloudEntryPoint());

        service.setCredentials(new ArrayList<Credentials>());
        service.getCredentials().add(new Credentials());
        service.getCredentials().get(0).setId(100);
        service.getCredentials().get(0).setName("Credential_Name_100");

        service.setCredentialsTemplates(new ArrayList<CredentialsTemplate>());
        service.getCredentialsTemplates().add(new CredentialsTemplate());
        service.getCredentialsTemplates().get(0).setId(101);
        service.getCredentialsTemplates().get(0).setName("CredentialTemplate_Name_101");

        service.setJobs(new ArrayList<Job>());
        service.getJobs().add(new Job());
        service.getJobs().get(0).setId(110);
        service.getJobs().get(0).setName("Job_Name_110");

        service.setMachineConfigs(new ArrayList<MachineConfiguration>());
        service.getMachineConfigs().add(new MachineConfiguration());
        service.getMachineConfigs().get(0).setId(200);
        service.getMachineConfigs().get(0).setName("MachineConfiguration_Name_200");

        service.setMachineImages(new ArrayList<MachineImage>());
        service.getMachineImages().add(new MachineImage());
        service.getMachineImages().get(0).setId(120);
        service.getMachineImages().get(0).setName("MachineImage_Name_120");

        service.setMachines(new ArrayList<Machine>());
        service.getMachines().add(new Machine());
        service.getMachines().get(0).setId(150);
        service.getMachines().get(0).setName("Machine_Name_150");

        service.setMachineTemplates(new ArrayList<MachineTemplate>());
        service.getMachineTemplates().add(new MachineTemplate());
        service.getMachineTemplates().get(0).setId(160);
        service.getMachineTemplates().get(0).setName("MachineTemplate_Name_160");

        service.setSystems(new ArrayList<System>());
        service.getSystems().add(new System());
        service.getSystems().get(0).setId(200);
        service.getSystems().get(0).setName("System_Name_200");

        service.setSystemTemplates(new ArrayList<SystemTemplate>());
        service.getSystemTemplates().add(new SystemTemplate());
        service.getSystemTemplates().get(0).setId(210);
        service.getSystemTemplates().get(0).setName("SystemTemplate_Name_210");

        service.setVolumeConfigurations(new ArrayList<VolumeConfiguration>());
        service.getVolumeConfigurations().add(new VolumeConfiguration());
        service.getVolumeConfigurations().get(0).setId(300);
        service.getVolumeConfigurations().get(0).setName("VolumeConfiguration_Name_300");

        service.setVolumeImages(new ArrayList<VolumeImage>());
        service.getVolumeImages().add(new VolumeImage());
        service.getVolumeImages().get(0).setId(310);
        service.getVolumeImages().get(0).setName("VolumeImage_Name_310");

        service.setVolumes(new ArrayList<Volume>());
        service.getVolumes().add(new Volume());
        service.getVolumes().get(0).setId(320);
        service.getVolumes().get(0).setName("Volume_Name_320");

        service.setVolumeTemplates(new ArrayList<VolumeTemplate>());
        service.getVolumeTemplates().add(new VolumeTemplate());
        service.getVolumeTemplates().get(0).setId(102);
        service.getVolumeTemplates().get(0).setName("VolumeTemplate_Name_330");

        return service;
    }

    @Test
    public void testCimiCloudEntryPoint() throws Exception {
        CimiCloudEntryPoint cimi;
        CloudEntryPointAggregate service;

        // Empty Service -> Cimi
        service = new CloudEntryPointAggregate(new CloudEntryPoint());
        cimi = (CimiCloudEntryPoint) this.context.convertToCimi(service, CimiCloudEntryPoint.class);
        Assert.assertEquals(this.request.getBaseUri(), cimi.getBaseURI());
        Assert.assertNull(cimi.getCredentials());
        Assert.assertNull(cimi.getCredentialTemplates());
        Assert.assertNull(cimi.getJobs());
        Assert.assertNull(cimi.getMachineConfigs());
        Assert.assertNull(cimi.getMachineImages());
        Assert.assertNull(cimi.getMachines());
        Assert.assertNull(cimi.getMachineTemplates());
        Assert.assertNull(cimi.getSystems());
        Assert.assertNull(cimi.getSystemTemplates());
        Assert.assertNull(cimi.getVolumeConfigs());
        Assert.assertNull(cimi.getVolumeImages());
        Assert.assertNull(cimi.getVolumes());
        Assert.assertNull(cimi.getVolumeTemplates());

        service = this.buildFull();

        cimi = (CimiCloudEntryPoint) this.context.convertToCimi(service, CimiCloudEntryPoint.class);

        Assert.assertEquals(this.request.getBaseUri(), cimi.getBaseURI());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialCollection.getPathname(), cimi.getCredentials()
            .getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialTemplateCollection.getPathname(), cimi
            .getCredentialTemplates().getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.JobCollection.getPathname(), cimi.getJobs().getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineConfigurationCollection.getPathname(), cimi
            .getMachineConfigs().getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImageCollection.getPathname(), cimi
            .getMachineImages().getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineCollection.getPathname(), cimi.getMachines()
            .getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineTemplateCollection.getPathname(), cimi
            .getMachineTemplates().getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.SystemCollection.getPathname(), cimi.getSystems()
            .getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.SystemTemplateCollection.getPathname(), cimi
            .getSystemTemplates().getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.VolumeConfigurationCollection.getPathname(), cimi
            .getVolumeConfigs().getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.VolumeImageCollection.getPathname(), cimi
            .getVolumeImages().getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.VolumeCollection.getPathname(), cimi.getVolumes()
            .getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.VolumeTemplateCollection.getPathname(), cimi
            .getVolumeTemplates().getHref());
    }

    @Test
    public void testCimiCloudEntryPointExpand() throws Exception {
        CimiCloudEntryPoint cimi;
        CloudEntryPointAggregate service;

        // Prepare Trace
        Writer strWriter;
        ObjectMapper mapper = new ObjectMapper();
        JAXBContext context = JAXBContext.newInstance(CimiCloudEntryPoint.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        // Prepare Service
        service = this.buildFull();

        // no expand
        this.request.getParams().setCimiExpand(new CimiExpand());
        cimi = (CimiCloudEntryPoint) this.context.convertToCimi(service, CimiCloudEntryPoint.class);

        // Trace
        strWriter = new StringWriter();
        mapper.writeValue(strWriter, cimi);
        CloudEntryPointConverterTest.LOGGER.debug("JSON:\n\t{}", strWriter);
        strWriter = new StringWriter();
        m.marshal(cimi, strWriter);
        CloudEntryPointConverterTest.LOGGER.debug("XML:\n\t{}", strWriter);

        Assert.assertEquals(this.request.getBaseUri(), cimi.getBaseURI());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialCollection.getPathname(), cimi.getCredentials()
            .getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialTemplateCollection.getPathname(), cimi
            .getCredentialTemplates().getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.JobCollection.getPathname(), cimi.getJobs().getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineConfigurationCollection.getPathname(), cimi
            .getMachineConfigs().getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImageCollection.getPathname(), cimi
            .getMachineImages().getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineCollection.getPathname(), cimi.getMachines()
            .getHref());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineTemplateCollection.getPathname(), cimi
            .getMachineTemplates().getHref());

        // expand = *
        this.request.getParams().setCimiExpand(new CimiExpand(CimiStringParams.ALL));
        cimi = (CimiCloudEntryPoint) this.context.convertToCimi(service, CimiCloudEntryPoint.class);

        // Trace
        strWriter = new StringWriter();
        mapper.writeValue(strWriter, cimi);
        CloudEntryPointConverterTest.LOGGER.debug("JSON:\n\t{}", strWriter);
        strWriter = new StringWriter();
        m.marshal(cimi, strWriter);
        CloudEntryPointConverterTest.LOGGER.debug("XML:\n\t{}", strWriter);
    }
}