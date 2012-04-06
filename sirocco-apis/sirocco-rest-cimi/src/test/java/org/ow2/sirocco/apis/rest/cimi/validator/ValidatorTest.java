package org.ow2.sirocco.apis.rest.cimi.validator;

import javax.validation.groups.Default;

import org.junit.Assert;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
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
        cimi.setDisks(new CimiDiskConfiguration[0]);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration()});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(), new CimiDiskConfiguration()});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration("format", "attachementPoint")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration("format", "attachementPoint"),
            new CimiDiskConfiguration()});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration("format", "attachementPoint"),
            new CimiDiskConfiguration("format", "attachementPoint")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(new CimiCapacity(), "format", "attachementPoint")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("A");
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(new CimiCapacity(1, "units"), "format",
            "attachementPoint")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));

        cimi.setName("0");
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(new CimiCapacity(1, "units"), "format",
            "attachementPoint")});
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, Default.class, GroupCreate.class));
    }
}
