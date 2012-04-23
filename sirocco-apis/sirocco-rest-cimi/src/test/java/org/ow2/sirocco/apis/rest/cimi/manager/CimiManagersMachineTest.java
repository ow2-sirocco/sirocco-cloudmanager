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
import org.ow2.sirocco.apis.rest.cimi.domain.ActionType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAction;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.RequestHeader;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Basic tests "end to end" for managers Machine.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/context/managerContext.xml"})
public class CimiManagersMachineTest {

    @Autowired
    @Qualifier("IMachineManager")
    private IMachineManager service;

    @Autowired
    @Qualifier("CimiManagerActionMachine")
    private CimiManager managerAction;

    @Autowired
    @Qualifier("CimiManagerCreateMachine")
    private CimiManager managerCreate;

    @Autowired
    @Qualifier("CimiManagerDeleteMachine")
    private CimiManager managerDelete;

    @Autowired
    @Qualifier("CimiManagerReadMachine")
    private CimiManager managerRead;

    @Autowired
    @Qualifier("CimiManagerUpdateMachine")
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
    public void testActionStart() throws Exception {
        Job job = new Job();
        job.setId(123);

        EasyMock.expect(this.service.startMachine("1")).andReturn(job);
        EasyMock.replay(this.service);

        CimiAction cimi = new CimiAction();
        cimi.setAction(ActionType.START.getPath());
        this.request.setId("1");
        this.request.setCimiData(cimi);
        this.managerAction.execute(this.request, this.response);

        Assert.assertEquals(202, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.JOB_PATH + "/123", ((CimiJob) this.response.getCimiData()).getId());
        EasyMock.verify(this.service);
    }

    @Test
    public void testActionStop() throws Exception {
        Job job = new Job();
        job.setId(123);

        EasyMock.expect(this.service.stopMachine("1")).andReturn(job);
        EasyMock.replay(this.service);

        CimiAction cimi = new CimiAction();
        cimi.setAction(ActionType.STOP.getPath());
        this.request.setId("1");
        this.request.setCimiData(cimi);
        this.managerAction.execute(this.request, this.response);

        Assert.assertEquals(202, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.JOB_PATH + "/123", ((CimiJob) this.response.getCimiData()).getId());
        EasyMock.verify(this.service);
    }

    @Test
    public void testCreate() throws Exception {
        Job job = new Job();
        job.setId(123);

        EasyMock.expect(this.service.createMachine(EasyMock.anyObject(MachineCreate.class))).andReturn(job);
        EasyMock.replay(this.service);

        CimiMachineTemplate template = new CimiMachineTemplate();
        template.setMachineConfig(new CimiMachineConfiguration(new CimiCpu(1f, "megaHertz", 1), new CimiMemory(1, "mebiByte")));
        template.setMachineImage(new CimiMachineImage(new ImageLocation("foo")));
        CimiMachineCreate cimi = new CimiMachineCreate();
        cimi.setMachineTemplate(template);
        this.request.setCimiData(cimi);

        this.managerCreate.execute(this.request, this.response);

        Assert.assertEquals(202, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.JOB_PATH + "/123", ((CimiJob) this.response.getCimiData()).getId());
        EasyMock.verify(this.service);
    }

    @Test
    public void testRead() throws Exception {
        Machine machine = new Machine();
        machine.setId(1);

        EasyMock.expect(this.service.getMachineById("1")).andReturn(machine);
        EasyMock.replay(this.service);

        this.request.setId("1");
        this.managerRead.execute(this.request, this.response);

        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.MACHINE_PATH + "/1", ((CimiMachine) this.response.getCimiData()).getId());
        EasyMock.verify(this.service);
    }

    @Test
    public void testReadWithCimiSelect() throws Exception {
        List<String> list = Arrays.asList(new String[] {"name", "description"});
        Machine machine = new Machine();
        machine.setId(1);

        EasyMock.expect(this.service.getMachineAttributes(EasyMock.eq("1"), EasyMock.eq(list))).andReturn(machine);
        EasyMock.replay(this.service);

        this.request.setId("1");
        this.request.getHeader().getCimiSelect().setSelects(new String[] {"name", "description"});
        this.managerRead.execute(this.request, this.response);

        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.MACHINE_PATH + "/1", ((CimiMachine) this.response.getCimiData()).getId());
        EasyMock.verify(this.service);
    }

    @Test
    public void testDelete() throws Exception {
        Job job = new Job();
        job.setId(123);

        EasyMock.expect(this.service.deleteMachine("1")).andReturn(job);
        EasyMock.replay(this.service);

        this.request.setId("1");
        this.managerDelete.execute(this.request, this.response);

        Assert.assertEquals(202, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.JOB_PATH + "/123", ((CimiJob) this.response.getCimiData()).getId());
        EasyMock.verify(this.service);
    }

    @Test
    public void testUpdate() throws Exception {
        Job job = new Job();
        job.setId(123);

        EasyMock.expect(this.service.updateMachine(EasyMock.anyObject(Machine.class))).andReturn(job);
        EasyMock.replay(this.service);

        CimiMachine cimi = new CimiMachine();
        cimi.setName("foo");
        this.request.setId("1");
        this.request.setCimiData(cimi);

        this.managerUpdate.execute(this.request, this.response);

        Assert.assertEquals(202, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.JOB_PATH + "/123", ((CimiJob) this.response.getCimiData()).getId());
        EasyMock.verify(this.service);
    }

    @Test
    public void testUpdateWithCimiSelect() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "fooName");
        map.put("description", "fooDescription");
        Job job = new Job();
        job.setId(123);

        EasyMock.expect(this.service.updateMachineAttributes(EasyMock.eq("1"), EasyMock.eq(map))).andReturn(job);
        EasyMock.replay(this.service);

        CimiMachine cimi = new CimiMachine();
        cimi.setName("fooName");
        cimi.setDescription("fooDescription");
        this.request.setId("1");
        this.request.setCimiData(cimi);
        this.request.getHeader().getCimiSelect().setSelects(new String[] {"name", "description"});

        this.managerUpdate.execute(this.request, this.response);

        Assert.assertEquals(202, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.JOB_PATH + "/123", ((CimiJob) this.response.getCimiData()).getId());
        EasyMock.verify(this.service);
    }

}
