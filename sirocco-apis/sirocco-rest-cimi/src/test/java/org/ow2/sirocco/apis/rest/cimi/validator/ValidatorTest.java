package org.ow2.sirocco.apis.rest.cimi.validator;

import javax.validation.groups.Default;

import org.junit.Assert;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;

public class ValidatorTest {

    @Test
    public void testCimiMachineImage() {

        CimiMachineImage cimi = new CimiMachineImage();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setImageLocation(new ImageLocation());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setImageLocation(null);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("0");
        cimi.setImageLocation(null);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("0");
        cimi.setImageLocation(new ImageLocation());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));
    }

    @Test
    public void testCimiMachineConfiguration() {

        CimiMachineConfiguration cimi = new CimiMachineConfiguration();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(null);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDisk[0]);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDisk[] {new CimiDisk()});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDisk[] {new CimiDisk(), new CimiDisk()});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDisk[] {new CimiDisk("format", "attachementPoint")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDisk[] {new CimiDisk("format", "attachementPoint"), new CimiDisk()});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDisk[] {new CimiDisk("format", "attachementPoint"), new CimiDisk("format", "attachementPoint")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDisk[] {new CimiDisk("format", "attachementPoint", new CimiCapacity())});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDisk[] {new CimiDisk("format", "attachementPoint", new CimiCapacity(1, "units"))});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("0");
        cimi.setDisks(new CimiDisk[] {new CimiDisk("format", "attachementPoint", new CimiCapacity(1, "units"))});
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));
    }
}
