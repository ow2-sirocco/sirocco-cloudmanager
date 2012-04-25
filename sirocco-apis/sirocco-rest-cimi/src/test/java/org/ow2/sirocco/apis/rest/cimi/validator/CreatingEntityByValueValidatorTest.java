package org.ow2.sirocco.apis.rest.cimi.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;

public class CreatingEntityByValueValidatorTest {

    private CimiRequest request;

    private CimiResponse response;

    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setContext(new CimiContextImpl(this.request));
        this.request.setBaseUri("http://www.test.org/");
        this.response = new CimiResponse();
    }

    @Test
    public void testCimiMachineImage() {
        CimiMachineImage cimi;

        cimi = new CimiMachineImage();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));

        cimi = new CimiMachineImage();
        cimi.setImageLocation(new ImageLocation("foo"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));
    }

    @Test
    public void testCimiMachineConfiguration() {
        CimiMachineConfiguration cimi;

        cimi = new CimiMachineConfiguration();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));
    }

    @Test
    public void testCimiCredentialsCreate() {
        CimiCredentialsCreate cimi;
        CimiCredentialsTemplate template;

        cimi = new CimiCredentialsCreate();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));

        template = new CimiCredentialsTemplate("user", "pass", null);
        cimi = new CimiCredentialsCreate();
        cimi.setCredentialsTemplate(template);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));
    }

    @Test
    public void testCimiCredentialsTemplate() {
        CimiCredentialsTemplate cimi;

        cimi = new CimiCredentialsTemplate();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));

        cimi = new CimiCredentialsTemplate("user", "pass", null);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));

        cimi = new CimiCredentialsTemplate("user", "pass", new byte[1]);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));

        cimi = new CimiCredentialsTemplate("user", null, null);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));

        cimi = new CimiCredentialsTemplate(null, "pass", null);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));
    }

    @Test
    public void testCimiMachineCreate() {
        CimiMachineCreate cimi;
        CimiMachineTemplate template;

        cimi = new CimiMachineCreate();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));

        template = new CimiMachineTemplate();
        template.setMachineConfig(new CimiMachineConfiguration(new CimiCpu(1f, "unit", 1), new CimiMemory(1, "unit")));
        template.setMachineImage(new CimiMachineImage(new ImageLocation("foo")));
        cimi = new CimiMachineCreate();
        cimi.setMachineTemplate(template);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));
    }

    @Test
    public void testCimiMachineTemplate() {
        CimiMachineTemplate cimi;

        cimi = new CimiMachineTemplate();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));

        cimi = new CimiMachineTemplate();
        cimi.setMachineConfig(new CimiMachineConfiguration(new CimiCpu(1f, "unit", 1), new CimiMemory(1, "unit")));
        cimi.setMachineImage(new CimiMachineImage(new ImageLocation("foo")));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi,
            GroupCreateByValue.class));
    }
}
