package org.ow2.sirocco.apis.rest.cimi.validator.constraints;

import org.junit.Assert;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.validator.CimiValidatorHelper;

public class IdentifierValidatorTest {

    @Test
    public void testIdentifier() {

        class MyTest {
            @Identifier
            String name;

            public void setName(final String name) {
                this.name = name;
            }
        }
        MyTest toTest = new MyTest();
        Assert.assertNull(toTest.name);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("a");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("z");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("A");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("Z");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("0");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("9");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName(".");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("-");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("/");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_abcdefghijklmnopqrstuvwxyz");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_0123456789");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_AAA+BBB");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_AAA-BBB");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));

        toTest.setName("_AAA/BBB");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(toTest));
    }

}
