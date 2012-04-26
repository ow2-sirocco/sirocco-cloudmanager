package org.ow2.sirocco.apis.rest.cimi.validator.constraints;

import org.junit.Assert;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.ActionType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiAction;
import org.ow2.sirocco.apis.rest.cimi.validator.CimiValidatorHelper;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;

public class AssertActionValidatorTest {

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
