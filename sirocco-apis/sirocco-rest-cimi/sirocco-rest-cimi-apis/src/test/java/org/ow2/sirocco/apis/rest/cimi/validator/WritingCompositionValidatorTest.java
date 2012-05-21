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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;

public class WritingCompositionValidatorTest {

    @Test
    public void testCimiCapacity() throws Exception {
        CimiCapacity cimi;

        cimi = new CimiCapacity();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiCapacity();
        cimi.setQuantity(1);
        cimi.setUnits("unit");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiCapacity();
        cimi.setQuantity(1);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiCapacity();
        cimi.setUnits("unit");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));
    }

    @Test
    public void testCimiDisk() throws Exception {
        CimiDisk cimi;

        cimi = new CimiDisk();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiDisk();
        cimi.setCapacity(new CimiCapacity(123, "unit"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiDisk();
        cimi.setCapacity(new CimiCapacity());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiDisk();
        cimi.setCapacity(new CimiCapacity(123, null));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiDisk();
        cimi.setCapacity(new CimiCapacity(null, "unit"));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));
    }

    @Test
    public void testCimiDiskConfiguration() throws Exception {
        CimiDiskConfiguration cimi;

        cimi = new CimiDiskConfiguration();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiDiskConfiguration();
        cimi.setAttachmentPoint("ap");
        cimi.setCapacity(new CimiCapacity(123, "unit"));
        cimi.setFormat("f");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiDiskConfiguration();
        cimi.setAttachmentPoint("ap");
        cimi.setFormat("f");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiDiskConfiguration();
        cimi.setAttachmentPoint("ap");
        cimi.setCapacity(new CimiCapacity(123, "unit"));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiDiskConfiguration();
        cimi.setCapacity(new CimiCapacity(123, "unit"));
        cimi.setFormat("f");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiDiskConfiguration();
        cimi.setAttachmentPoint("ap");
        cimi.setCapacity(new CimiCapacity());
        cimi.setFormat("f");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));
    }

    @Test
    public void testCimiCpu() throws Exception {
        CimiCpu cimi;

        cimi = new CimiCpu();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiCpu();
        cimi.setFrequency(1f);
        cimi.setNumberVirtualCpus(1);
        cimi.setUnits("unit");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiCpu();
        cimi.setFrequency(1f);
        cimi.setUnits("unit");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiCpu();
        cimi.setFrequency(1f);
        cimi.setNumberVirtualCpus(1);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiCpu();
        cimi.setNumberVirtualCpus(1);
        cimi.setUnits("unit");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));
    }

    @Test
    public void testCimiMemory() throws Exception {
        CimiMemory cimi;

        cimi = new CimiMemory();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiMemory();
        cimi.setQuantity(1);
        cimi.setUnits("unit");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiMemory();
        cimi.setQuantity(1);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));

        cimi = new CimiMemory();
        cimi.setQuantity(null);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(cimi));
    }

}
