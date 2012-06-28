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
import org.ow2.sirocco.apis.rest.cimi.request.RequestHeader;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;

/**
 * Converters tests of CloudEntryPoint resources.
 */
public class CloudEntryPointConverterTest {

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
    // TODO Volumes ...
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
        Assert.assertNull(cimi.getJobTime());
        Assert.assertNull(cimi.getMachineConfigs());
        Assert.assertNull(cimi.getMachineImages());
        Assert.assertNull(cimi.getMachines());
        Assert.assertNull(cimi.getMachineTemplates());
        Assert.assertNull(cimi.getMachineTemplates());

        // Full Service -> Cimi
        service = new CloudEntryPointAggregate(new CloudEntryPoint());
        service.setCredentials(new ArrayList<Credentials>());
        service.setCredentialsTemplates(new ArrayList<CredentialsTemplate>());
        service.setJobs(new ArrayList<Job>());
        service.setMachineConfigs(new ArrayList<MachineConfiguration>());
        service.setMachineImages(new ArrayList<MachineImage>());
        service.setMachines(new ArrayList<Machine>());
        service.setMachineTemplates(new ArrayList<MachineTemplate>());

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
    }

}