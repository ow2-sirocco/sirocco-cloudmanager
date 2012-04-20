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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.RequestHeader;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
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
    @Qualifier("IMachineManager")
    private IMachineManager machineService;

    @Autowired
    @Qualifier("ICredentialsManager")
    private ICredentialsManager credentialsService;

    @Autowired
    @Qualifier("IMachineImageManager")
    private IMachineImageManager machineImageService;

    @Autowired
    @Qualifier("CimiManagerReadCloudEntryPoint")
    private CimiManager managerRead;

    private CimiRequest request;

    private CimiResponse response;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.request = new CimiRequest();
        this.response = new CimiResponse();

        this.request.setBaseUri("/");
        this.request.setContext(new CimiContextImpl(this.request));
        RequestHeader header = new RequestHeader();
        header.setCimiSelect(new CimiSelect());
        this.request.setHeader(header);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        EasyMock.reset(this.machineService);
        EasyMock.reset(this.machineImageService);
        EasyMock.reset(this.credentialsService);
    }

    @Test
    public void testRead() throws Exception {
        CloudEntryPoint cloud = new CloudEntryPoint();
        cloud.setId(999);

        EasyMock.expect(this.machineService.getCloudEntryPoint()).andReturn(cloud);
        EasyMock.expect(this.machineService.getMachineCollection()).andReturn(null);
        EasyMock.expect(this.machineService.getMachineTemplateCollection()).andReturn(null);
        EasyMock.expect(this.machineService.getMachineConfigurationCollection()).andReturn(null);
        EasyMock.replay(this.machineService);

        EasyMock.expect(this.machineImageService.getMachineImageCollection()).andReturn(null);
        EasyMock.replay(this.machineImageService);

        EasyMock.expect(this.credentialsService.getCredentialsCollection()).andReturn(null);
        EasyMock.expect(this.credentialsService.getCredentialsTemplateCollection()).andReturn(null);
        EasyMock.replay(this.credentialsService);

        this.request.setId("1");
        this.managerRead.execute(this.request, this.response);

        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.CLOUDENTRYPOINT_PATH, ((CimiCloudEntryPoint) this.response.getCimiData()).getId());
        EasyMock.verify(this.machineService);
        EasyMock.verify(this.machineImageService);
        EasyMock.verify(this.credentialsService);
    }

    @Test
    @Ignore
    public void testReadWithCimiSelect() throws Exception {
        // TODO
    }
}
