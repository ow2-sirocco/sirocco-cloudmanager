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
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.ActionType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAction;
import org.ow2.sirocco.apis.rest.cimi.validator.CimiValidatorHelper;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;

public class AssertActionPathValidatorTest {

    @Test
    public void testActionPath() {

        CimiAction toTest = new CimiAction();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest, GroupWrite.class));

        toTest.setAction(ActionType.CAPTURE.getPath());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest, GroupWrite.class));

        toTest.setAction(ActionType.PAUSE.getPath());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest, GroupWrite.class));

        toTest.setAction(ActionType.RESTART.getPath());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest, GroupWrite.class));

        toTest.setAction(ActionType.START.getPath());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest, GroupWrite.class));

        toTest.setAction(ActionType.STOP.getPath());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest, GroupWrite.class));

        toTest.setAction(ActionType.SUSPEND.getPath());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest, GroupWrite.class));

        toTest.setAction(ActionType.BASE_PATH);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest, GroupWrite.class));

        toTest.setAction(ActionType.START.name());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest, GroupWrite.class));
    }

}
