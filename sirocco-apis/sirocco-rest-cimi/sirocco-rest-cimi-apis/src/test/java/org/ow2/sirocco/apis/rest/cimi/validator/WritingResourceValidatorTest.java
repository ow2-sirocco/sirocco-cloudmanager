/**
 *
 * SIROCCO
 * Copyright (C) 2011 France Telecom
 * Contact: sirocco@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.validator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDataCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJobCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineDiskCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineVolumeCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiResource;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolume;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeVolumeImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiVolumeVolumeImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.domain.ImageLocation;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;

public class WritingResourceValidatorTest {

    private CimiRequest request;

    private CimiResponse response;

    private CimiContext context;

    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setBaseUri("http://www.sirocco.test.org/");
        this.response = new CimiResponse();
        this.context = new CimiContextImpl(this.request, this.response);
    }

    @Test
    public void testAllResourcesWithHref() throws Exception {
        CimiResource cimi = null;

        for (ExchangeType type : ExchangeType.values()) {

            cimi = this.newResource(type);

            if (null != cimi) {
                // System.out.println(type);
                cimi.setHref(type.makeHref(this.request.getBaseUri(), "987", "123"));
                // System.out.println(cimi.getHref());
                Assert.assertTrue("Test " + type, CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

                if (true == type.hasParent()) {
                    cimi.setHref(type.makeHref(this.request.getBaseUri(), "123"));
                } else {
                    cimi.setHref(type.makeHref(this.request.getBaseUri(), (String) null));
                }
                // System.out.println(cimi.getHref());
                if (true == type.hasIdInReference()) {
                    Assert.assertFalse("Test " + type, CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));
                } else {
                    Assert.assertTrue("Test " + type, CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));
                }

                cimi.setHref("foo");
                Assert.assertFalse("Test " + type, CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

                // Test with all other resource
                for (ExchangeType otherType : ExchangeType.values()) {
                    CimiResource cimiOther = this.newResource(otherType);
                    if ((null != cimiOther) && (type != otherType)) {
                        cimi.setHref(otherType.makeHref(this.request.getBaseUri(), "987", "123"));
                        Assert.assertFalse("Test " + type + " with " + otherType, CimiValidatorHelper.getInstance()
                            .validateToWrite(this.context, cimi));
                    }
                }
            }
        }

    }

    private CimiResource newResource(final ExchangeType type) {
        CimiResource cimi = null;
        switch (type) {
        case CloudEntryPoint:
            cimi = new CimiCloudEntryPoint();
            break;
        case Credentials:
            cimi = new CimiCredentials();
            break;
        case CredentialsCollection:
            cimi = new CimiCredentialsCollection();
            break;
        case CredentialsCreate:
            cimi = null;
            break;
        case CredentialsTemplate:
            cimi = new CimiCredentialsTemplate();
            break;
        case CredentialsTemplateCollection:
            cimi = new CimiCredentialsTemplateCollection();
            break;
        case Disk:
            cimi = new CimiMachineDisk();
            break;
        case DiskCollection:
            cimi = new CimiMachineDiskCollection();
            break;
        case Job:
            cimi = new CimiJob();
            break;
        case JobCollection:
            cimi = new CimiJobCollection();
            break;
        case Machine:
            cimi = new CimiMachine();
            break;
        case MachineAction:
            cimi = null;
            break;
        case MachineCollection:
            cimi = new CimiMachineCollection();
            break;
        case MachineConfiguration:
            cimi = new CimiMachineConfiguration();
            break;
        case MachineConfigurationCollection:
            cimi = new CimiMachineConfigurationCollection();
            break;
        case MachineCreate:
            cimi = null;
            break;
        case MachineImage:
            cimi = new CimiMachineImage();
            break;
        case MachineImageCollection:
            cimi = new CimiMachineImageCollection();
            break;
        case MachineTemplate:
            cimi = new CimiMachineTemplate();
            break;
        case MachineTemplateCollection:
            cimi = new CimiMachineTemplateCollection();
            break;
        case MachineVolume:
            cimi = new CimiMachineVolume();
            break;
        case MachineVolumeCollection:
            cimi = new CimiMachineVolumeCollection();
            break;
        case Volume:
            cimi = new CimiVolume();
            break;
        case VolumeCollection:
            cimi = new CimiVolumeCollection();
            break;
        case VolumeConfiguration:
            cimi = new CimiVolumeConfiguration();
            break;
        case VolumeConfigurationCollection:
            cimi = new CimiVolumeConfigurationCollection();
            break;
        case VolumeCreate:
            cimi = null;
            break;
        case VolumeImage:
            cimi = new CimiVolumeImage();
            break;
        case VolumeImageCollection:
            cimi = new CimiVolumeImageCollection();
            break;
        case VolumeTemplate:
            cimi = new CimiVolumeTemplate();
            break;
        case VolumeTemplateCollection:
            cimi = new CimiVolumeTemplateCollection();
            break;
        case VolumeVolumeImage:
            cimi = new CimiVolumeVolumeImage();
            break;
        case VolumeVolumeImageCollection:
            cimi = new CimiVolumeVolumeImageCollection();
            break;
        default:
            Assert.fail("In test method \"newResource\" : " + type.name() + " not found");
            break;
        }

        return cimi;
    }

    @Test
    public void testCimiCommon() throws Exception {
        CimiDataCommon cimi;
        Map<String, String> props;

        // --------------- OK

        cimi = new CimiCommon();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiCommon();
        cimi.setName("A");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiCommon();
        cimi.setName("_");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiCommon();
        cimi.setName("0");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiCommon();
        cimi.setName("0 A");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiCommon();
        props = new HashMap<String, String>();
        props.put("A", "a");
        props.put("B", "b");
        cimi.setProperties(props);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        // --------------- KO

        cimi = new CimiCommon();
        props = new HashMap<String, String>();
        cimi.setProperties(props);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiCommon();
        props = new HashMap<String, String>();
        props.put("A", "a");
        props.put("B", null);
        cimi.setProperties(props);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));
    }

    @Test
    public void testCimiCredentials() throws Exception {

        CimiCredentials cimi;
        byte[] filledKeySize3 = new byte[3];
        for (int i = 0; i < filledKeySize3.length; i++) {
            filledKeySize3[i] = (byte) (i + 2);
        }

        // --------------- OK

        cimi = new CimiCredentials();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiCredentials();
        cimi.setKey(filledKeySize3);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiCredentials();
        cimi.setKey(new byte[1]);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        // --------------- KO

        cimi = new CimiCredentials();
        cimi.setKey(new byte[0]);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

    }

    @Test
    public void testCimiCredentialsTemplate() throws Exception {

        CimiCredentialsTemplate cimi;
        byte[] filledKeySize3 = new byte[3];
        for (int i = 0; i < filledKeySize3.length; i++) {
            filledKeySize3[i] = (byte) (i + 2);
        }

        // --------------- OK

        cimi = new CimiCredentialsTemplate();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiCredentialsTemplate();
        cimi.setKey(filledKeySize3);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiCredentialsTemplate();
        cimi.setKey(new byte[1]);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        // --------------- KO

        cimi = new CimiCredentialsTemplate();
        cimi.setKey(new byte[0]);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));
    }

    @Test
    public void testCimiMachine() throws Exception {

        CimiMachine cimi;

        // --------------- OK

        cimi = new CimiMachine();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiMachine();
        cimi.setCpu(11);
        cimi.setMemory(22);
        cimi.setDisks(new CimiMachineDiskCollection());
        cimi.getDisks().add(new CimiMachineDisk(123));
        cimi.getDisks().add(new CimiMachineDisk(456));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        // --------------- KO

        cimi = new CimiMachine();
        cimi.setDisks(new CimiMachineDiskCollection());

        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));
    }

    @Test
    public void testCimiMachineConfiguration() throws Exception {
        CimiMachineConfiguration cimi;

        // --------------- OK

        cimi = new CimiMachineConfiguration();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(11);
        cimi.setMemory(22);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(11);
        cimi.setMemory(22);
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(1024, "f", "ap")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(11);
        cimi.setMemory(22);
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(1024, "f", "ap"),
            new CimiDiskConfiguration(2048, "f2", "ap2")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        // --------------- KO

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(11);
        cimi.setMemory(22);
        cimi.setDisks(new CimiDiskConfiguration[0]);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(11);
        cimi.setMemory(22);
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration()});
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(11);
        cimi.setMemory(22);
        cimi.setDisks(new CimiDiskConfiguration[] {null, null});
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));
    }

    @Test
    public void testCimiMachineImage() throws Exception {
        CimiMachineImage cimi;

        // --------------- OK

        cimi = new CimiMachineImage();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiMachineImage();
        cimi.setImageLocation(new ImageLocation("foo"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        // --------------- KO

        cimi = new CimiMachineImage();
        cimi.setImageLocation(new ImageLocation());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));
    }

    @Test
    public void testCimiMachineTemplate() throws Exception {
        CimiMachineTemplate cimi;

        // --------------- OK

        cimi = new CimiMachineTemplate();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        cimi = new CimiMachineTemplate();
        cimi.setCredentials(new CimiCredentials());
        cimi.setMachineConfig(new CimiMachineConfiguration());
        cimi.setMachineImage(new CimiMachineImage());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));

        // --------------- KO

        cimi = new CimiMachineTemplate();
        cimi.setCredentials(new CimiCredentials());
        cimi.setMachineConfig(new CimiMachineConfiguration());
        cimi.setMachineImage(new CimiMachineImage(new ImageLocation()));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.context, cimi));
    }
}
