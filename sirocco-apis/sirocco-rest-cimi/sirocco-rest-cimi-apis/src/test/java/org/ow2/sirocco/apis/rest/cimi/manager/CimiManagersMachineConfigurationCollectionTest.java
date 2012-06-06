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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.RequestHeader;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Basic tests "end to end" for managers MachineConfigurationCollection.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/context/managerContext.xml"})
public class CimiManagersMachineConfigurationCollectionTest {

    @Autowired
    @Qualifier("IMachineManager")
    private IMachineManager service;

    @Autowired
    @Qualifier("CimiManagerReadMachineConfigurationCollection")
    private CimiManager managerReadCollection;

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
        EasyMock.reset(this.service);
    }

    @Test
    public void testReadCollection() throws Exception {
        MachineConfiguration machine;
        MachineConfigurationCollection collect = new MachineConfigurationCollection();
        List<MachineConfiguration> list = new ArrayList<MachineConfiguration>();
        collect.setMachineConfigurations(list);
        for (int i = 0; i < 3; i++) {
            machine = new MachineConfiguration();
            machine.setId(i + 13);
            list.add(machine);
        }

        EasyMock.expect(this.service.getMachineConfigurationCollection()).andReturn(collect);
        EasyMock.replay(this.service);

        this.managerReadCollection.execute(this.context);

        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.MACHINE_CONFIGURATION_PATH,
            ((CimiMachineConfigurationCollection) this.response.getCimiData()).getId());
        CimiMachineConfigurationCollection cimiCollect = (CimiMachineConfigurationCollection) this.response.getCimiData();
        Assert.assertNotNull(cimiCollect.getArray());
        Assert.assertEquals(3, cimiCollect.getArray().length);
        for (int i = 0; i < cimiCollect.getArray().length; i++) {
            Assert.assertEquals(ConstantsPath.MACHINE_CONFIGURATION_PATH + "/" + (i + 13), cimiCollect.getArray()[i].getHref());
        }
        EasyMock.verify(this.service);
    }

    @Test
    public void testReadWithCimiSelectAttributes() throws Exception {

        List<MachineConfiguration> list = new ArrayList<MachineConfiguration>();
        EasyMock.expect(
            this.service.getMachineConfigurations(EasyMock.eq(Arrays.asList(new String[] {"operations"})),
                EasyMock.eq((String) null))).andReturn(list);
        EasyMock.replay(this.service);

        this.request.getHeader().getCimiSelect().setSelects(new String[] {"operations"});
        this.managerReadCollection.execute(this.context);

        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.MACHINE_CONFIGURATION_PATH,
            ((CimiMachineConfigurationCollection) this.response.getCimiData()).getId());

        EasyMock.verify(this.service);
    }

    @Test
    public void testReadWithCimiSelectArrays() throws Exception {
        MachineConfiguration machine;
        List<MachineConfiguration> list = new ArrayList<MachineConfiguration>();
        for (int i = 0; i < 7; i++) {
            machine = new MachineConfiguration();
            machine.setId(i + 13);
            list.add(machine);
        }

        EasyMock.expect(
            this.service.getMachineConfigurations(EasyMock.eq(1), EasyMock.eq(23),
                EasyMock.eq(Arrays.asList(new String[] {"machineConfigurations"})))).andReturn(list);
        EasyMock.replay(this.service);

        this.request.getHeader().getCimiSelect().setSelects(new String[] {"machineConfigurations[1-23]"});
        this.managerReadCollection.execute(this.context);

        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals(ConstantsPath.MACHINE_CONFIGURATION_PATH,
            ((CimiMachineConfigurationCollection) this.response.getCimiData()).getId());
        CimiMachineConfigurationCollection cimiCollect = (CimiMachineConfigurationCollection) this.response.getCimiData();
        Assert.assertNotNull(cimiCollect.getArray());
        Assert.assertEquals(7, cimiCollect.getArray().length);
        for (int i = 0; i < cimiCollect.getArray().length; i++) {
            Assert.assertEquals(ConstantsPath.MACHINE_CONFIGURATION_PATH + "/" + (i + 13), cimiCollect.getArray()[i].getHref());
        }
        EasyMock.verify(this.service);
    }
}
