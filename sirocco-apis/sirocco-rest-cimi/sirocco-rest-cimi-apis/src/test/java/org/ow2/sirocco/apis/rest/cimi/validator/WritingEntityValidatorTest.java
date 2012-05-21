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
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCapacity;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCommon;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCpu;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDisk;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiDiskConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiEntityType;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiHref;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJobCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachine;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfigurationCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImageCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateCollection;
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
    public void testAllEntitiesWithHref() throws Exception {
        CimiHref cimi = null;

        for (CimiEntityType type : CimiEntityType.values()) {
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
            default:
                Assert.fail(type.name());
                break;
            }
            if (null != cimi) {
                cimi.setHref(this.request.getContext().makeHref(cimi, 1));
                Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

                cimi.setHref(this.request.getContext().makeHrefBase(cimi));
                if (true == this.request.getContext().mustHaveIdInReference(cimi)) {

                    Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));
                } else {
                    Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));
                }

                cimi.setHref("foo");
                Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));
            }
        }

    }

    @Test
    public void testCimiCommon() throws Exception {
        CimiCommon cimi;
        Map<String, String> props;

        // --------------- OK

        cimi = new CimiCommon();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiCommon();
        cimi.setName("A");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiCommon();
        cimi.setName("_");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiCommon();
        props = new HashMap<String, String>();
        props.put("A", "a");
        props.put("B", "b");
        cimi.setProperties(props);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        // --------------- KO

        cimi = new CimiCommon();
        cimi.setName("0");
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiCommon();
        props = new HashMap<String, String>();
        cimi.setProperties(props);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiCommon();
        props = new HashMap<String, String>();
        props.put("A", "a");
        props.put("B", null);
        cimi.setProperties(props);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));
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
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiCredentials();
        cimi.setKey(filledKeySize3);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiCredentials();
        cimi.setKey(new byte[1]);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        // --------------- KO

        cimi = new CimiCredentials();
        cimi.setKey(new byte[0]);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

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
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiCredentialsTemplate();
        cimi.setKey(filledKeySize3);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiCredentialsTemplate();
        cimi.setKey(new byte[1]);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        // --------------- KO

        cimi = new CimiCredentialsTemplate();
        cimi.setKey(new byte[0]);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));
    }

    @Test
    public void testCimiMachine() throws Exception {

        CimiMachine cimi;

        // --------------- OK

        cimi = new CimiMachine();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiMachine();
        cimi.setDisks(new CimiDisk[] {new CimiDisk(new CimiCapacity(1, "unit"))});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        // --------------- KO

        cimi = new CimiMachine();
        cimi.setDisks(new CimiDisk[0]);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));
    }

    @Test
    public void testCimiMachineConfiguration() throws Exception {
        CimiMachineConfiguration cimi;

        // --------------- OK

        cimi = new CimiMachineConfiguration();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(new CimiCapacity(1, "unit"), "f", "ap")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration(new CimiCapacity(1, "unit"), "f", "ap"),
            new CimiDiskConfiguration(new CimiCapacity(2, "unit2"), "f2", "ap2")});
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        // --------------- KO

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        cimi.setDisks(new CimiDiskConfiguration[0]);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        cimi.setDisks(new CimiDiskConfiguration[] {new CimiDiskConfiguration()});
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        cimi.setDisks(new CimiDiskConfiguration[] {null, null});
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));
    }

    @Test
    public void testCimiMachineImage() throws Exception {
        CimiMachineImage cimi;

        // --------------- OK

        cimi = new CimiMachineImage();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiMachineImage();
        cimi.setImageLocation(new ImageLocation("foo"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        // --------------- KO

        cimi = new CimiMachineImage();
        cimi.setImageLocation(new ImageLocation());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));
    }

    @Test
    public void testCimiMachineTemplate() throws Exception {
        CimiMachineTemplate cimi;

        // --------------- OK

        cimi = new CimiMachineTemplate();
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        cimi = new CimiMachineTemplate();
        cimi.setCredentials(new CimiCredentials());
        cimi.setMachineConfig(new CimiMachineConfiguration());
        cimi.setMachineImage(new CimiMachineImage());
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));

        // --------------- KO

        cimi = new CimiMachineTemplate();
        cimi.setCredentials(new CimiCredentials());
        cimi.setMachineConfig(new CimiMachineConfiguration());
        cimi.setMachineImage(new CimiMachineImage(new ImageLocation()));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToWrite(this.request, this.response, cimi));
    }
}
