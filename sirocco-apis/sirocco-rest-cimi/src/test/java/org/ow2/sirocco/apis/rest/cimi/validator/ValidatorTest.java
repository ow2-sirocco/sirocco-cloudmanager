package org.ow2.sirocco.apis.rest.cimi.validator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMemory;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;

public class ValidatorTest {

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

    @Test
    public void testCimiCommon() {
        CimiCommon cimi;
        Map<String, String> props;

        cimi = new CimiCommon();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiCommon();
        cimi.setName("A");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiCommon();
        cimi.setName("_");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiCommon();
        cimi.setName("0");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiCommon();
        props = new HashMap<String, String>();
        props.put("A", "a");
        props.put("B", "b");
        cimi.setProperties(props);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiCommon();
        props = new HashMap<String, String>();
        cimi.setProperties(props);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiCommon();
        props = new HashMap<String, String>();
        props.put("A", "a");
        props.put("B", null);
        cimi.setProperties(props);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));
    }

    @Test
    public void testCimiCommonId() {
        this.testCimiCommon();
    }

    @Test
    public void testCimiMachineImage() {
        CimiMachineImage cimi;

        cimi = new CimiMachineImage();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiMachineImage();
        cimi.setName("A");
        cimi.setImageLocation(new ImageLocation());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiMachineImage();
        cimi.setImageLocation(new ImageLocation());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiMachineImage();
        cimi.setName("A");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));
    }

    @Test
    public void testCimiMachineConfiguration() {
        CimiMachineConfiguration cimi;

        cimi = new CimiMachineConfiguration();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", null));
        cimi.setMemory(new CimiMemory(1, "unit"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiMachineConfiguration();
        cimi.setName("A");
        cimi.setCpu(new CimiCpu(1f, "unit", null));
        cimi.setMemory(new CimiMemory(1, "unit"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiMachineConfiguration();
        cimi.setName("A");
        cimi.setCpu(new CimiCpu(1f, "unit", null));
        cimi.setMemory(new CimiMemory(1, "unit"));
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(new CimiCapacity(1, "unit"), "f", "ap")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiMachineConfiguration();
        cimi.setName("A");
        cimi.setCpu(new CimiCpu(1f, "unit", null));
        cimi.setMemory(new CimiMemory(1, "unit"));
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(new CimiCapacity(1, "unit"), "f", "ap"),
            new CimiDiskConfiguration(new CimiCapacity(2, "unit2"), "f2", "ap2")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", null));
        cimi.setMemory(new CimiMemory(1, "unit"));
        cimi.setDisks(new CimiDiskConfiguration[0]);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiMachineConfiguration();
        cimi.setName("A");
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration()});
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi = new CimiMachineConfiguration();
        cimi.setName("A");
        cimi.setDisks(new CimiDiskConfiguration[] {null, null});
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

    }

    @Test
    public void testCimiCredentials() {

        byte[] filledKeySize3 = new byte[3];
        for (int i = 0; i < filledKeySize3.length; i++) {
            filledKeySize3[i] = (byte) (i + 2);
        }

        CimiCredentials cimi = new CimiCredentials();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi.setName("A");
        cimi.setKey(null);
        cimi.setPassword(null);
        cimi.setUserName(null);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi.setName("A");
        cimi.setKey(filledKeySize3);
        cimi.setPassword("A");
        cimi.setUserName("A");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi.setName("A");
        cimi.setKey(new byte[0]);
        cimi.setPassword("A");
        cimi.setUserName("A");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

        cimi.setName("A");
        cimi.setKey(new byte[1]);
        cimi.setPassword("A");
        cimi.setUserName("A");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validate(cimi, GroupWrite.class));

    }
}
