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
package org.ow2.sirocco.apis.rest.cimi.manager;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.RequestHeader;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Basic tests "end to end" for managers Machine.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/context/managerContext.xml"})
public class CimiManagersCloudEntryPointTest {

    @Autowired
    @Qualifier("ICredentialsManager")
    private ICredentialsManager serviceCredentials;

    @Autowired
    @Qualifier("IJobManager")
    private IJobManager serviceJob;

    @Autowired
    @Qualifier("IMachineManager")
    private IMachineManager serviceMachine;

    @Autowired
    @Qualifier("IMachineImageManager")
    private IMachineImageManager serviceMachineImage;

    @Autowired
    @Qualifier("CimiManagerReadCloudEntryPoint")
    private CimiManager managerRead;

    private CimiRequest request;

    private CimiResponse response;

    private CimiContext context;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setBaseUri("/");
        RequestHeader header = new RequestHeader();
        header.setCimiSelect(new CimiSelect());
        this.request.setHeader(header);

        this.response = new CimiResponse();
        this.context = new CimiContextImpl(this.request, this.response);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        EasyMock.reset(this.serviceCredentials);
        EasyMock.reset(this.serviceJob);
        EasyMock.reset(this.serviceMachine);
        EasyMock.reset(this.serviceMachineImage);
    }

    @Test
    // TODO Others resources : Volumes, ...
    public void testRead() throws Exception {
        CloudEntryPoint cloud = new CloudEntryPoint();
        cloud.setId(10);

        // Credentials
        List<Credentials> credentialsCollection = new ArrayList<Credentials>();
        // credentialsCollection.setId(600);
        List<CredentialsTemplate> credentialsTemplateCollection = new ArrayList<CredentialsTemplate>();
        // credentialsTemplateCollection.setId(700);
        // Jobs
        List<Job> jobCollection = new ArrayList<Job>();
        // jobCollection.setId(800);
        // Machines
        List<Machine> machineCollection = new ArrayList<Machine>();
        // machineCollection.setId(200);
        List<MachineTemplate> machineTemplateCollection = new ArrayList<MachineTemplate>();
        // machineTemplateCollection.setId(300);
        List<MachineConfiguration> machineConfigurationCollection = new ArrayList<MachineConfiguration>();
        // machineConfigurationCollection.setId(400);
        List<MachineImage> machineImageCollection = new ArrayList<MachineImage>();
        // machineImageCollection.setId(500);

        EasyMock.expect(this.serviceCredentials.getCredentials()).andReturn(credentialsCollection);
        EasyMock.expect(this.serviceCredentials.getCredentialsTemplates()).andReturn(credentialsTemplateCollection);
        EasyMock.replay(this.serviceCredentials);

        EasyMock.expect(this.serviceJob.getJobs()).andReturn(jobCollection);
        EasyMock.replay(this.serviceJob);

        EasyMock.expect(this.serviceMachine.getCloudEntryPoint()).andReturn(cloud);
        EasyMock.expect(this.serviceMachine.getMachines()).andReturn(machineCollection);
        EasyMock.expect(this.serviceMachine.getMachineTemplates()).andReturn(machineTemplateCollection);
        EasyMock.expect(this.serviceMachine.getMachineConfigurations()).andReturn(machineConfigurationCollection);
        EasyMock.replay(this.serviceMachine);

        EasyMock.expect(this.serviceMachineImage.getMachineImages()).andReturn(machineImageCollection);
        EasyMock.replay(this.serviceMachineImage);

        // this.request.setId("1");
        this.managerRead.execute(this.context);

        Assert.assertEquals(200, this.response.getStatus());
        CimiCloudEntryPoint cimiCloud = (CimiCloudEntryPoint) this.response.getCimiData();

        Assert.assertEquals(ConstantsPath.CLOUDENTRYPOINT_PATH, cimiCloud.getId());
        Assert.assertEquals(ConstantsPath.CREDENTIALS_PATH, cimiCloud.getCredentials().getHref());
        Assert.assertEquals(ConstantsPath.CREDENTIALS_TEMPLATE_PATH, cimiCloud.getCredentialsTemplates().getHref());
        Assert.assertEquals(ConstantsPath.JOB_PATH, cimiCloud.getJobs().getHref());
        Assert.assertEquals(ConstantsPath.MACHINE_CONFIGURATION_PATH, cimiCloud.getMachineConfigs().getHref());
        Assert.assertEquals(ConstantsPath.MACHINE_IMAGE_PATH, cimiCloud.getMachineImages().getHref());
        Assert.assertEquals(ConstantsPath.MACHINE_PATH, cimiCloud.getMachines().getHref());
        Assert.assertEquals(ConstantsPath.MACHINE_TEMPLATE_PATH, cimiCloud.getMachineTemplates().getHref());

        EasyMock.verify(this.serviceCredentials);
        EasyMock.verify(this.serviceJob);
        EasyMock.verify(this.serviceMachine);
        EasyMock.verify(this.serviceMachineImage);
    }

    @Test
    @Ignore
    public void testReadWithCimiSelect() throws Exception {
        // TODO
    }
}
