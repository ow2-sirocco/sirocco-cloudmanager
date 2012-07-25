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
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiExpand;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.RequestParams;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.State;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.Type;

/**
 * Converters tests of machines resources.
 */
public class MachineImagesConverterTest {

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(MachineImagesConverterTest.class);

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

    @Test
    public void testCimiMachineImage() throws Exception {
        CimiMachineImage cimi;
        MachineImage service;

        // Empty Cimi -> Service
        service = (MachineImage) this.context.convertToService(new CimiMachineImage());
        Assert.assertNull(service.getImageLocation());
        Assert.assertNull(service.getState());
        Assert.assertNull(service.getType());

        // Empty Service -> Cimi
        cimi = (CimiMachineImage) this.context.convertToCimi(new MachineImage(), CimiMachineImage.class);
        Assert.assertNull(cimi.getImageLocation());
        Assert.assertNull(cimi.getState());
        Assert.assertNull(cimi.getType());

        // Full Cimi -> Service
        cimi = new CimiMachineImage();
        cimi.setImageLocation(new ImageLocation("href"));
        cimi.setState("state");
        cimi.setType("type");

        service = (MachineImage) this.context.convertToService(cimi);
        Assert.assertEquals("href", service.getImageLocation());
        Assert.assertNull(service.getState());
        Assert.assertNull(service.getType());

        // Full Service -> Cimi
        service = new MachineImage();
        service.setImageLocation("hrefImageLocation");
        service.setState(State.AVAILABLE);
        service.setType(Type.IMAGE);

        cimi = (CimiMachineImage) this.context.convertToCimi(service, CimiMachineImage.class);
        Assert.assertEquals("hrefImageLocation", cimi.getImageLocation().getHref());
        Assert.assertEquals("AVAILABLE", cimi.getState());
        Assert.assertEquals("IMAGE", cimi.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCimiMachineImageCollection() throws Exception {
        CimiMachineImageCollection cimi;
        List<MachineImage> service;

        // Empty Cimi -> Service
        service = (List<MachineImage>) this.context.convertToService(new CimiMachineImageCollection());
        Assert.assertNotNull(service);
        Assert.assertEquals(0, service.size());

        // Empty Service -> Cimi
        cimi = (CimiMachineImageCollection) this.context.convertToCimi(new ArrayList<MachineImage>(),
            CimiMachineImageCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiMachineImageCollection();
        cimi.setArray(new CimiMachineImage[] {new CimiMachineImage(), new CimiMachineImage()});

        service = (List<MachineImage>) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.size());

        // Full Service -> Cimi
        MachineImage machineImage1 = new MachineImage();
        machineImage1.setId(1);
        machineImage1.setName("nameOne");
        MachineImage machineImage2 = new MachineImage();
        machineImage2.setId(2);
        machineImage2.setName("nameTwo");
        MachineImage machineImage3 = new MachineImage();
        machineImage3.setId(3);
        machineImage3.setName("nameThree");

        service = new ArrayList<MachineImage>();
        service.add(machineImage1);
        service.add(machineImage2);
        service.add(machineImage3);

        cimi = (CimiMachineImageCollection) this.context.convertToCimi(service, CimiMachineImageCollection.class);
        Assert.assertEquals(3, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/1",
            cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/2",
            cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/3",
            cimi.getArray()[2].getHref());
        Assert.assertNull(cimi.getArray()[2].getId());
        Assert.assertNull(cimi.getArray()[2].getName());

        cimi = (CimiMachineImageCollection) this.context.convertToCimi(
            Arrays.asList(new MachineImage[] {machineImage3, machineImage1}), CimiMachineImageCollection.class);
        Assert.assertEquals(2, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/3",
            cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.MachineImage.getPathname() + "/1",
            cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
    }
}