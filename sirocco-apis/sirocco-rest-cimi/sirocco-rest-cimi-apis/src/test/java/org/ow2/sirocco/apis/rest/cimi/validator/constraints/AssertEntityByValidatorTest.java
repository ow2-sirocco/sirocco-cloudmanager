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
package org.ow2.sirocco.apis.rest.cimi.validator.constraints;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.validator.CimiValidatorHelper;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupCreateByRefOrByValue;

public class AssertEntityByValidatorTest {
    private CimiRequest request;

    private CimiResponse response;

    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setContext(new CimiContextImpl(this.request));
        this.request.setBaseUri("http://www.test.org/");
        this.response = new CimiResponse();
    }

    @Test
    public void testWithCimiMachineImage() {
        CimiMachineImage toTest;

        // OK entity with ref (even if ref is bad)
        toTest = new CimiMachineImage();
        toTest.setHref("A");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest,
            GroupCreateByRefOrByValue.class));

        // OK entity with value (even if bad value)
        toTest = new CimiMachineImage();
        toTest.setName("foo");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest,
            GroupCreateByRefOrByValue.class));

        // KO entity empty
        toTest = new CimiMachineImage();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest,
            GroupCreateByRefOrByValue.class));
    }

}
