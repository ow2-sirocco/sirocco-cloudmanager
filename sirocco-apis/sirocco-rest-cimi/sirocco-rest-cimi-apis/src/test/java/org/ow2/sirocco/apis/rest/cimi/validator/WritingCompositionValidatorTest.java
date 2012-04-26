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
    public void testCimiCapacity() {
        CimiCapacity cimi;

        cimi = new CimiCapacity();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiCapacity();
        cimi.setQuantity(1);
        cimi.setUnits("unit");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiCapacity();
        cimi.setQuantity(1);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiCapacity();
        cimi.setUnits("unit");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));
    }

    @Test
    public void testCimiDisk() {
        CimiDisk cimi;

        cimi = new CimiDisk();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiDisk();
        cimi.setCapacity(new CimiCapacity(123, "unit"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiDisk();
        cimi.setCapacity(new CimiCapacity());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiDisk();
        cimi.setCapacity(new CimiCapacity(123, null));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiDisk();
        cimi.setCapacity(new CimiCapacity(null, "unit"));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));
    }

    @Test
    public void testCimiDiskConfiguration() {
        CimiDiskConfiguration cimi;

        cimi = new CimiDiskConfiguration();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiDiskConfiguration();
        cimi.setAttachmentPoint("ap");
        cimi.setCapacity(new CimiCapacity(123, "unit"));
        cimi.setFormat("f");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiDiskConfiguration();
        cimi.setAttachmentPoint("ap");
        cimi.setFormat("f");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiDiskConfiguration();
        cimi.setAttachmentPoint("ap");
        cimi.setCapacity(new CimiCapacity(123, "unit"));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiDiskConfiguration();
        cimi.setCapacity(new CimiCapacity(123, "unit"));
        cimi.setFormat("f");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiDiskConfiguration();
        cimi.setAttachmentPoint("ap");
        cimi.setCapacity(new CimiCapacity());
        cimi.setFormat("f");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));
    }

    @Test
    public void testCimiCpu() {
        CimiCpu cimi;

        cimi = new CimiCpu();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiCpu();
        cimi.setFrequency(1f);
        cimi.setNumberVirtualCpus(1);
        cimi.setUnits("unit");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiCpu();
        cimi.setFrequency(1f);
        cimi.setUnits("unit");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiCpu();
        cimi.setFrequency(1f);
        cimi.setNumberVirtualCpus(1);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiCpu();
        cimi.setNumberVirtualCpus(1);
        cimi.setUnits("unit");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));
    }

    @Test
    public void testCimiMemory() {
        CimiMemory cimi;

        cimi = new CimiMemory();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiMemory();
        cimi.setQuantity(1);
        cimi.setUnits("unit");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiMemory();
        cimi.setQuantity(1);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiMemory();
        cimi.setQuantity(null);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));
    }

}
