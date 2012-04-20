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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.RequestHeader;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Basic tests "end to end" for managers MachineImage.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/context/managerContext.xml"})
public class CimiManagersMachineImageTest {

    @Autowired
    @Qualifier("IMachineImageManager")
    private IMachineImageManager service;

    @Autowired
    @Qualifier("CimiManagerCreateMachineImage")
    private CimiManager managerCreate;

    @Autowired
    @Qualifier("CimiManagerDeleteMachineImage")
    private CimiManager managerDelete;

    @Autowired
    @Qualifier("CimiManagerReadMachineImage")
    private CimiManager managerRead;

    @Autowired
    @Qualifier("CimiManagerUpdateMachineImage")
    private CimiManager managerUpdate;

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
        EasyMock.reset(this.service);
    }

    @Test
    public void testCreate() throws Exception {

        Job job = new Job();
        job.setId(123);
        EasyMock.expect(this.service.createMachineImage(EasyMock.anyObject(MachineImage.class))).andReturn(job);
        EasyMock.replay(this.service);

        CimiMachineImage cimi = new CimiMachineImage();
        cimi.setImageLocation(new ImageLocation("foo"));
        this.request.setCimiData(cimi);
        this.managerCreate.execute(this.request, this.response);

        Assert.assertEquals(202, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.JOB_PATH + "/123", ((CimiJob) this.response.getCimiData()).getId());
        EasyMock.verify(this.service);
    }

    @Test
    public void testRead() throws Exception {

        MachineImage machine = new MachineImage();
        machine.setId(1);
        EasyMock.expect(this.service.getMachineImageById("1")).andReturn(machine);
        EasyMock.replay(this.service);

        this.request.setId("1");
        this.managerRead.execute(this.request, this.response);

        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.MACHINE_IMAGE_PATH + "/1", ((CimiMachineImage) this.response.getCimiData()).getId());
        EasyMock.verify(this.service);
    }

    @Test
    public void testReadWithCimiSelect() throws Exception {

        List<String> list = Arrays.asList(new String[] {"name", "description"});
        MachineImage machine = new MachineImage();
        machine.setId(1);
        EasyMock.expect(this.service.getMachineImageAttributes(EasyMock.eq("1"), EasyMock.eq(list))).andReturn(machine);
        EasyMock.replay(this.service);

        this.request.setId("1");
        this.request.getHeader().getCimiSelect().setSelects(new String[] {"name", "description"});
        this.managerRead.execute(this.request, this.response);

        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.MACHINE_IMAGE_PATH + "/1", ((CimiMachineImage) this.response.getCimiData()).getId());
        EasyMock.verify(this.service);
    }

    @Test
    public void testDelete() throws Exception {

        this.service.deleteMachineImage("1");
        EasyMock.replay(this.service);

        this.request.setId("1");
        this.managerDelete.execute(this.request, this.response);

        Assert.assertEquals(200, this.response.getStatus());
        EasyMock.verify(this.service);
    }

    @Test
    public void testUpdate() throws Exception {

        this.service.updateMachineImage(EasyMock.anyObject(MachineImage.class));
        EasyMock.replay(this.service);

        CimiMachineImage cimi = new CimiMachineImage();
        cimi.setName("foo");
        this.request.setId("1");
        this.request.setCimiData(cimi);

        this.managerUpdate.execute(this.request, this.response);

        Assert.assertEquals(200, this.response.getStatus());
        EasyMock.verify(this.service);
    }

    @Test
    public void testUpdateWithCimiSelect() throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "fooName");
        map.put("description", "fooDescription");

        this.service.updateMachineImageAttributes(EasyMock.eq("1"), EasyMock.eq(map));
        EasyMock.replay(this.service);

        CimiMachineImage cimi = new CimiMachineImage();
        cimi.setName("fooName");
        cimi.setDescription("fooDescription");
        this.request.setId("1");
        this.request.setCimiData(cimi);
        this.request.getHeader().getCimiSelect().setSelects(new String[] {"name", "description"});

        this.managerUpdate.execute(this.request, this.response);

        Assert.assertEquals(200, this.response.getStatus());
        EasyMock.verify(this.service);
    }

}
