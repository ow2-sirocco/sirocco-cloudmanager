package org.ow2.sirocco.apis.rest.cimi.validator.constraints;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.configuration.AppConfig;
import org.ow2.sirocco.apis.rest.cimi.configuration.ConfigFactory;
import org.ow2.sirocco.apis.rest.cimi.configuration.ItemConfig;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommonId;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiEntityType;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.validator.CimiValidatorHelper;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;

public class ValidReferenceValidatorTest {
    private CimiRequest request;

    private CimiResponse response;

    @BeforeClass
    public static void init() throws Exception {

        class MyConfigFactory extends ConfigFactory {

            @Override
            protected ItemConfig buildEntityItem(final CimiEntityType type) {
                ItemConfig item = null;
                switch (type) {
                case CloudEntryPoint:
                    item = new ItemConfig(CimiEntityType.CloudEntryPoint, MyCloudEntryPoint.class);
                    break;
                case Credentials:
                    item = new ItemConfig(CimiEntityType.Credentials, MyCredentials.class);
                    break;
                case MachineImage:
                    item = new ItemConfig(CimiEntityType.MachineImage, MyImage.class);
                    break;
                case MachineCollection:
                    item = new ItemConfig(CimiEntityType.MachineCollection, MyCollectionMachine.class);
                    break;
                case MachineConfigurationCollection:
                    item = new ItemConfig(CimiEntityType.MachineConfigurationCollection, MyCollectionConfig.class);
                    break;
                default:
                    item = super.buildEntityItem(type);
                    break;
                }
                return item;
            }
        }

        AppConfig.initialize(new MyConfigFactory().getConfig());
    }

    @AfterClass
    public static void afterClass() throws Exception {
        AppConfig.initialize(null);
    }

    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setContext(new CimiContextImpl(this.request));
        this.request.setBaseUri("http://www.test.org/");
        this.response = new CimiResponse();
    }

    @ValidReference(groups = GroupWrite.class)
    private class MyCloudEntryPoint extends CimiCommonId {
        private static final long serialVersionUID = 1L;
    }

    @ValidReference(groups = GroupWrite.class)
    private class MyCredentials extends CimiCommonId {
        private static final long serialVersionUID = 1L;
    }

    @ValidReference(groups = GroupWrite.class)
    private class MyImage extends CimiCommonId {
        private static final long serialVersionUID = 1L;
    }

    @ValidReference(groups = GroupWrite.class)
    private class MyCollectionMachine extends CimiCommonId {
        private static final long serialVersionUID = 1L;
    }

    @ValidReference(groups = GroupWrite.class)
    private class MyCollectionConfig extends CimiCommonId {
        private static final long serialVersionUID = 1L;
    }

    @Test
    public void testMyCloudEntryPoint() {

        MyCloudEntryPoint toTest = new MyCloudEntryPoint();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + CimiEntityType.CloudEntryPoint.getPathType().getPathname());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref("A");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref("");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + CimiEntityType.Credentials.getPathType().getPathname());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + "foo/1");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + CimiEntityType.Credentials.getPathType().getPathname() + "/foo/1");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getContext().makeHrefBase(toTest));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));
    }

    @Test
    public void testMyCredentials() {

        MyCredentials toTest = new MyCredentials();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + CimiEntityType.Credentials.getPathType().getPathname() + "/1");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref("A");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref("");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + CimiEntityType.Credentials.getPathType().getPathname());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + "foo/1");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + CimiEntityType.Credentials.getPathType().getPathname() + "/foo/1");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getContext().makeHrefBase(toTest));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));
    }

    @Test
    public void testMyImage() {

        MyImage toTest = new MyImage();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname() + "/1");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref("A");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref("");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + CimiEntityType.MachineImage.getPathType().getPathname());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + "toto/1");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getContext().makeHrefBase(toTest));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));
    }

    @Test
    public void testMyCollectionMachine() {

        MyCollectionMachine toTest = new MyCollectionMachine();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + CimiEntityType.MachineCollection.getPathType().getPathname());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref("A");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref("");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + CimiEntityType.MachineCollection.getPathType().getPathname() + "/1");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + "toto/1");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getContext().makeHrefBase(toTest));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));
    }

    @Test
    public void testMyCollectionConfig() {

        MyCollectionConfig toTest = new MyCollectionConfig();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + CimiEntityType.MachineConfigurationCollection.getPathType().getPathname());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref("A");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref("");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + CimiEntityType.MachineConfigurationCollection.getPathType().getPathname()
            + "/1");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getBaseUri() + "toto/1");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));

        toTest.setHref(this.request.getContext().makeHrefBase(toTest));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, toTest, GroupWrite.class));
    }
}
