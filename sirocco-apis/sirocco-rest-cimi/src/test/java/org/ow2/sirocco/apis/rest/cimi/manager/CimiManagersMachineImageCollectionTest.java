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
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.RequestHeader;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Basic tests "end to end" for managers MachineImageCollection.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/context/managerContext.xml"})
public class CimiManagersMachineImageCollectionTest {

    @Autowired
    @Qualifier("IMachineImageManager")
    private IMachineImageManager service;

    @Autowired
    @Qualifier("CimiManagerReadMachineImageCollection")
    private CimiManager managerReadCollection;

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
    public void testRead() throws Exception {
        MachineImage machine;
        MachineImageCollection collect = new MachineImageCollection();
        List<MachineImage> list = new ArrayList<MachineImage>();
        collect.setImages(list);
        for (int i = 0; i < 3; i++) {
            machine = new MachineImage();
            machine.setId(i + 13);
            list.add(machine);
        }

        EasyMock.expect(this.service.getMachineImageCollection()).andReturn(collect);
        EasyMock.replay(this.service);

        this.managerReadCollection.execute(this.request, this.response);

        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.MACHINE_IMAGE_PATH,
            ((CimiMachineImageCollection) this.response.getCimiData()).getId());
        CimiMachineImageCollection cimiCollect = (CimiMachineImageCollection) this.response.getCimiData();
        Assert.assertNotNull(cimiCollect.getMachineImages());
        Assert.assertEquals(3, cimiCollect.getMachineImages().length);
        for (int i = 0; i < cimiCollect.getMachineImages().length; i++) {
            Assert.assertEquals(ConstantsPath.MACHINE_IMAGE_PATH + "/" + (i + 13), cimiCollect.getMachineImages()[i].getHref());
        }
        EasyMock.verify(this.service);
    }

    @Test
    public void testReadWithCimiSelectAttributes() throws Exception {

        List<MachineImage> list = new ArrayList<MachineImage>();
        EasyMock.expect(
            this.service.getMachineImages(EasyMock.eq(Arrays.asList(new String[] {"operations"})), EasyMock.eq((String) null)))
            .andReturn(list);
        EasyMock.replay(this.service);

        this.request.getHeader().getCimiSelect().setSelects(new String[] {"operations"});
        this.managerReadCollection.execute(this.request, this.response);

        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.MACHINE_IMAGE_PATH,
            ((CimiMachineImageCollection) this.response.getCimiData()).getId());

        EasyMock.verify(this.service);
    }

    @Test
    public void testReadWithCimiSelectArrays() throws Exception {
        MachineImage machine;
        List<MachineImage> list = new ArrayList<MachineImage>();
        for (int i = 0; i < 23; i++) {
            machine = new MachineImage();
            machine.setId(i + 13);
            list.add(machine);
        }

        EasyMock.expect(
            this.service.getMachineImages(EasyMock.eq(1), EasyMock.eq(23),
                EasyMock.eq(Arrays.asList(new String[] {"machineImages"})))).andReturn(list);
        EasyMock.replay(this.service);

        this.request.getHeader().getCimiSelect().setSelects(new String[] {"machineImages[1-23]"});
        this.managerReadCollection.execute(this.request, this.response);

        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.MACHINE_IMAGE_PATH,
            ((CimiMachineImageCollection) this.response.getCimiData()).getId());
        CimiMachineImageCollection cimiCollect = (CimiMachineImageCollection) this.response.getCimiData();
        Assert.assertNotNull(cimiCollect.getMachineImages());
        Assert.assertEquals(23, cimiCollect.getMachineImages().length);
        for (int i = 0; i < cimiCollect.getMachineImages().length; i++) {
            Assert.assertEquals(ConstantsPath.MACHINE_IMAGE_PATH + "/" + (i + 13), cimiCollect.getMachineImages()[i].getHref());
        }
        EasyMock.verify(this.service);
    }
}
