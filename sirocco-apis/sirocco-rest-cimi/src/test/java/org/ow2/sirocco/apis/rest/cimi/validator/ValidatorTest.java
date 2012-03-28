package org.ow2.sirocco.apis.rest.cimi.validator;

import javax.validation.groups.Default;

import org.junit.Assert;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;

public class ValidatorTest {

    @Test
    public void testCimiMachineImage() {

        CimiMachineImage image = new CimiMachineImage();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(image));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(image, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(image, Default.class, GroupCreate.class));

        image.setName("abslkjghazpuioj_KLJHALKJQD_23134867");
        image.setImageLocation(new ImageLocation());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(image));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(image, GroupCreate.class));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(image, Default.class, GroupCreate.class));

        image.setName("abslkjghazpuioj_KLJHALKJQD_23134867");
        image.setImageLocation(null);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(image));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(image, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(image, Default.class, GroupCreate.class));

        image.setName("AAA+BBB");
        image.setImageLocation(null);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(image));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(image, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(image, Default.class, GroupCreate.class));

        image.setName("AAA-BBB");
        image.setImageLocation(new ImageLocation());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(image));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(image, GroupCreate.class));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(image, Default.class, GroupCreate.class));
    }

}
