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
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;

public class CreatingResourceValidatorTest {

    private CimiRequest request;

    private CimiResponse response;

    private CimiContext context;

    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setBaseUri("http://www.test.org/");
        this.response = new CimiResponse();
        this.context = new CimiContextImpl(this.request, this.response);
    }

    @Test
    public void testCimiMachineImage() throws Exception {
        CimiMachineImage cimi;

        // KO empty
        cimi = new CimiMachineImage();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by values
        cimi = new CimiMachineImage();
        cimi.setImageLocation(new ImageLocation("foo"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by reference
        cimi = new CimiMachineImage(this.request.getBaseUri() + ExchangeType.MachineImage.getPathType().getPathname() + "/31");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by reference and value
        cimi = new CimiMachineImage(this.request.getBaseUri() + ExchangeType.MachineImage.getPathType().getPathname() + "/31");
        cimi.setImageLocation(new ImageLocation("foo"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // KO by reference and value
        cimi = new CimiMachineImage(this.request.getBaseUri() + ExchangeType.MachineImage.getPathType().getPathname() + "/31");
        cimi.setImageLocation(new ImageLocation());
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));
    }

    @Test
    public void testCimiMachineConfiguration() throws Exception {
        CimiMachineConfiguration cimi;

        // KO empty
        cimi = new CimiMachineConfiguration();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by values
        cimi = new CimiMachineConfiguration();
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by reference
        cimi = new CimiMachineConfiguration(this.request.getBaseUri()
            + ExchangeType.MachineConfiguration.getPathType().getPathname() + "/17");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by reference and value
        cimi = new CimiMachineConfiguration(this.request.getBaseUri()
            + ExchangeType.MachineConfiguration.getPathType().getPathname() + "/17");
        cimi.setCpu(new CimiCpu(1f, "unit", 1));
        cimi.setMemory(new CimiMemory(1, "unit"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // KO by reference and value : BAD CPU
        cimi = new CimiMachineConfiguration(this.request.getBaseUri()
            + ExchangeType.MachineConfiguration.getPathType().getPathname() + "/17");
        cimi.setCpu(new CimiCpu(3f, null, null));
        cimi.setMemory(new CimiMemory(1, "unit"));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));
    }

    @Test
    public void testCimiCredentialsCreate() throws Exception {
        CimiCredentialsCreate cimi;
        CimiCredentialsTemplate template;

        // KO empty
        cimi = new CimiCredentialsCreate();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by values
        template = new CimiCredentialsTemplate("user", "pass", null);
        cimi = new CimiCredentialsCreate();
        cimi.setCredentialsTemplate(template);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by reference
        template = new CimiCredentialsTemplate(this.request.getBaseUri()
            + ExchangeType.CredentialsTemplate.getPathType().getPathname() + "/17");
        cimi = new CimiCredentialsCreate();
        cimi.setCredentialsTemplate(template);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by reference and value
        template = new CimiCredentialsTemplate(this.request.getBaseUri()
            + ExchangeType.CredentialsTemplate.getPathType().getPathname() + "/17");
        template.setUserName("user");
        template.setPassword("pass");
        cimi = new CimiCredentialsCreate();
        cimi.setCredentialsTemplate(template);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // KO by reference and value
        template = new CimiCredentialsTemplate(this.request.getBaseUri()
            + ExchangeType.CredentialsTemplate.getPathType().getPathname() + "/17");
        template.setKey(new byte[0]);
        cimi = new CimiCredentialsCreate();
        cimi.setCredentialsTemplate(template);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));
    }

    @Test
    public void testCimiCredentialsTemplate() throws Exception {
        CimiCredentialsTemplate cimi;

        // KO empty
        cimi = new CimiCredentialsTemplate();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by values
        cimi = new CimiCredentialsTemplate("user", "pass", null);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // KO by values
        cimi = new CimiCredentialsTemplate("user", null, null);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // KO by values
        cimi = new CimiCredentialsTemplate(null, "pass", null);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by reference
        cimi = new CimiCredentialsTemplate(this.request.getBaseUri()
            + ExchangeType.CredentialsTemplate.getPathType().getPathname() + "/17");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by reference and value
        cimi = new CimiCredentialsTemplate(this.request.getBaseUri()
            + ExchangeType.CredentialsTemplate.getPathType().getPathname() + "/17");
        cimi.setUserName("user");
        cimi.setPassword("pass");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // KO by reference and value
        cimi = new CimiCredentialsTemplate(this.request.getBaseUri()
            + ExchangeType.CredentialsTemplate.getPathType().getPathname() + "/17");
        cimi.setKey(new byte[0]);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));
    }

    @Test
    public void testCimiMachineCreate() throws Exception {
        CimiMachineCreate cimi;
        CimiMachineTemplate template;

        // KO empty
        cimi = new CimiMachineCreate();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by values
        template = new CimiMachineTemplate();
        template.setMachineConfig(new CimiMachineConfiguration(new CimiCpu(1f, "unit", 1), new CimiMemory(1, "unit")));
        template.setMachineImage(new CimiMachineImage(new ImageLocation("foo")));
        cimi = new CimiMachineCreate();
        cimi.setMachineTemplate(template);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by reference
        cimi = new CimiMachineCreate();
        cimi.setMachineTemplate(new CimiMachineTemplate(this.request.getBaseUri()
            + ExchangeType.MachineTemplate.getPathType().getPathname() + "/7"));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by reference and value
        template = new CimiMachineTemplate(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathType().getPathname()
            + "/7");
        template.setMachineConfig(new CimiMachineConfiguration(new CimiCpu(1f, "unit", 1), new CimiMemory(1, "unit")));
        template.setMachineImage(new CimiMachineImage(new ImageLocation("foo")));
        cimi = new CimiMachineCreate();
        cimi.setMachineTemplate(template);
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // KO by reference and value
        template = new CimiMachineTemplate("hrefFoo");
        template.setMachineConfig(new CimiMachineConfiguration(new CimiCpu(1f, "unit", 1), new CimiMemory(1, "unit")));
        template.setMachineImage(new CimiMachineImage(new ImageLocation("foo")));
        cimi = new CimiMachineCreate();
        cimi.setMachineTemplate(template);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // KO by reference and value
        template = new CimiMachineTemplate(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathType().getPathname()
            + "/7");
        template.setMachineConfig(new CimiMachineConfiguration(new CimiCpu(1f, null, 1), new CimiMemory(1, "unit")));
        cimi = new CimiMachineCreate();
        cimi.setMachineTemplate(template);
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));
    }

    @Test
    public void testCimiMachineTemplate() throws Exception {
        CimiMachineTemplate cimi;

        // KO empty
        cimi = new CimiMachineTemplate();
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by values
        cimi = new CimiMachineTemplate();
        cimi.setMachineConfig(new CimiMachineConfiguration(new CimiCpu(1f, "unit", 1), new CimiMemory(1, "unit")));
        cimi.setMachineImage(new CimiMachineImage(new ImageLocation("foo")));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by reference
        cimi = new CimiMachineTemplate(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathType().getPathname()
            + "/7");
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // OK by reference and value
        cimi = new CimiMachineTemplate(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathType().getPathname()
            + "/7");
        cimi.setMachineConfig(new CimiMachineConfiguration(new CimiCpu(1f, "unit", 1), new CimiMemory(1, "unit")));
        cimi.setMachineImage(new CimiMachineImage(new ImageLocation("foo")));
        Assert.assertTrue(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));

        // KO by reference and value
        cimi = new CimiMachineTemplate(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathType().getPathname()
            + "/7");
        cimi.setMachineImage(new CimiMachineImage(new ImageLocation()));
        Assert.assertFalse(CimiValidatorHelper.getInstance().validateToCreate(this.context, cimi));
    }
}
