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
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplateCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.JobCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateCollection;
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
        CredentialsCollection credentialsCollection = new CredentialsCollection();
        credentialsCollection.setId(600);
        CredentialsTemplateCollection credentialsTemplateCollection = new CredentialsTemplateCollection();
        credentialsTemplateCollection.setId(700);
        // Jobs
        JobCollection jobCollection = new JobCollection();
        jobCollection.setId(800);
        // Machines
        MachineCollection machineCollection = new MachineCollection();
        machineCollection.setId(200);
        MachineTemplateCollection machineTemplateCollection = new MachineTemplateCollection();
        machineTemplateCollection.setId(300);
        MachineConfigurationCollection machineConfigurationCollection = new MachineConfigurationCollection();
        machineConfigurationCollection.setId(400);
        MachineImageCollection machineImageCollection = new MachineImageCollection();
        machineImageCollection.setId(500);

        EasyMock.expect(this.serviceCredentials.getCredentialsCollection()).andReturn(credentialsCollection);
        EasyMock.expect(this.serviceCredentials.getCredentialsTemplateCollection()).andReturn(credentialsTemplateCollection);
        EasyMock.replay(this.serviceCredentials);

        EasyMock.expect(this.serviceJob.getJobCollection()).andReturn(jobCollection);
        EasyMock.replay(this.serviceJob);

        EasyMock.expect(this.serviceMachine.getCloudEntryPoint()).andReturn(cloud);
        EasyMock.expect(this.serviceMachine.getMachineCollection()).andReturn(machineCollection);
        EasyMock.expect(this.serviceMachine.getMachineTemplateCollection()).andReturn(machineTemplateCollection);
        EasyMock.expect(this.serviceMachine.getMachineConfigurationCollection()).andReturn(machineConfigurationCollection);
        EasyMock.replay(this.serviceMachine);

        EasyMock.expect(this.serviceMachineImage.getMachineImageCollection()).andReturn(machineImageCollection);
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
