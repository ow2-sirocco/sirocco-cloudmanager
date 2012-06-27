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
package org.ow2.sirocco.apis.rest.cimi.validator;

import org.junit.Assert;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk;

public class WritingCompositionValidatorTest {

    @Test
    public void testCimiMachineDisk() throws Exception {
        CimiMachineDisk cimi;

        cimi = new CimiMachineDisk();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiMachineDisk();
        cimi.setCapacity(123);
        cimi.setInitialLocation("il");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(cimi));

    }

    @Test
    public void testCimiDiskConfiguration() throws Exception {
        CimiDiskConfiguration cimi;

        cimi = new CimiDiskConfiguration();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiDiskConfiguration();
        cimi.setCapacity(123);
        cimi.setFormat("f");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiDiskConfiguration();
        cimi.setCapacity(123);
        cimi.setFormat("f");
        cimi.setInitialLocation("il");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiDiskConfiguration();
        cimi.setFormat("f");
        cimi.setInitialLocation("il");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiDiskConfiguration();
        cimi.setCapacity(123);
        cimi.setInitialLocation("il");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

    }

}
