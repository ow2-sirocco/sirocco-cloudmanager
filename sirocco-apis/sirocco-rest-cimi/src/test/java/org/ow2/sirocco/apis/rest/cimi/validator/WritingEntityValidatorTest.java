package org.ow2.sirocco.apis.rest.cimi.validator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;

public class WritingEntityValidatorTest {

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
    public void testCimiCommon() {
        CimiCommon cimi;
        Map<String, String> props;

        cimi = new CimiCommon();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));

        cimi = new CimiCommon();
        cimi.setName("A");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));

        cimi = new CimiCommon();
        cimi.setName("_");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));

        cimi = new CimiCommon();
        cimi.setName("0");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));

        cimi = new CimiCommon();
        props = new HashMap<String, String>();
        props.put("A", "a");
        props.put("B", "b");
        cimi.setProperties(props);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));

        cimi = new CimiCommon();
        props = new HashMap<String, String>();
        cimi.setProperties(props);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));

        cimi = new CimiCommon();
        props = new HashMap<String, String>();
        props.put("A", "a");
        props.put("B", null);
        cimi.setProperties(props);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    }

    @Test
    public void testCimiCredentials() {
    
        CimiCredentials cimi;
        byte[] filledKeySize3 = new byte[3];
        for (int i = 0; i < filledKeySize3.length; i++) {
            filledKeySize3[i] = (byte) (i + 2);
        }
    
        cimi = new CimiCredentials();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiCredentials();
        cimi.setKey(filledKeySize3);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiCredentials();
        cimi.setKey(new byte[1]);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiCredentials();
        cimi.setKey(new byte[0]);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
    }

    @Test
    public void testCimiCredentialsTemplate() {
    
        CimiCredentialsTemplate cimi;
        byte[] filledKeySize3 = new byte[3];
        for (int i = 0; i < filledKeySize3.length; i++) {
            filledKeySize3[i] = (byte) (i + 2);
        }
    
        cimi = new CimiCredentialsTemplate();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiCredentialsTemplate();
        cimi.setKey(filledKeySize3);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiCredentialsTemplate();
        cimi.setKey(new byte[1]);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiCredentialsTemplate();
        cimi.setKey(new byte[0]);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    }

    @Test
    public void testCimiMachine() {
    
        CimiMachine cimi;
    
        cimi = new CimiMachine();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiMachine();
        cimi.setDisks(new CimiDisk[] {new CimiDisk(new CimiCapacity(1, "unit"))});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiMachine();
        cimi.setDisks(new CimiDisk[0]);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    }

    @Test
    public void testCimiMachineConfiguration() {
        CimiMachineConfiguration cimi;
    
        cimi = new CimiMachineConfiguration();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(new CimiCapacity(1, "unit"), "f", "ap")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(new CimiCapacity(1, "unit"), "f", "ap"),
            new CimiDiskConfiguration(new CimiCapacity(2, "unit2"), "f2", "ap2")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        cimi.setDisks(new CimiDiskConfiguration[0]);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration()});
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    
        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        cimi.setDisks(new CimiDiskConfiguration[] {null, null});
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    }

    @Test
    public void testCimiMachineImage() {
        CimiMachineImage cimi;

        cimi = new CimiMachineImage();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));

        cimi = new CimiMachineImage();
        cimi.setImageLocation(new ImageLocation("foo"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));

        cimi = new CimiMachineImage();
        cimi.setImageLocation(new ImageLocation());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    }

    @Test
    public void testCimiMachineTemplate() {

        CimiMachineTemplate cimi;
        cimi = new CimiMachineTemplate();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));

        cimi = new CimiMachineTemplate();
        cimi.setCredentials(new CimiCredentials());
        cimi.setMachineConfig(new CimiMachineConfiguration());
        cimi.setMachineImage(new CimiMachineImage());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(this.request, this.response, cimi, GroupWrite.class));
    }
}
