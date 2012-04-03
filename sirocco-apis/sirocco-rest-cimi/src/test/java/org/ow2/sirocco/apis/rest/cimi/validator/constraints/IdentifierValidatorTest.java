package org.ow2.sirocco.apis.rest.cimi.validator.constraints;

import org.junit.Assert;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommonId;
import org.ow2.sirocco.apis.rest.cimi.validator.CimiValidatorHelper;

public class IdentifierValidatorTest {

    @Test
    public void testIdentifier() {

        CimiCommonId cimi = new CimiCommonId();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("a");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("z");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("A");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("Z");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("_");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("0");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("9");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName(".");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("-");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("/");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("_abcdefghijklmnopqrstuvwxyz");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("_ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("_0123456789");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("_AAA+BBB");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("_AAA-BBB");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi));

        cimi.setName("_AAA/BBB");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi));
    }

}
