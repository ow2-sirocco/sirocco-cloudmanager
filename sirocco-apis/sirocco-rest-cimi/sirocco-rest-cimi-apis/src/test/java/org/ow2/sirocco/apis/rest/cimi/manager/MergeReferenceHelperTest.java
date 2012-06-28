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
package org.ow2.sirocco.apis.rest.cimi.manager;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineImage;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.RequestHeader;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Interface test.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/context/managerContext.xml"})
public class MergeReferenceHelperTest {

    @Autowired
    @Qualifier("IMachineImageManager")
    private IMachineImageManager serviceMachineImage;

    @Autowired
    @Qualifier("IMachineManager")
    private IMachineManager serviceMachine;

    @Autowired
    @Qualifier("ICredentialsManager")
    private ICredentialsManager serviceCredentials;

    @Autowired
    @Qualifier("MergeReferenceHelper")
    private MergeReferenceHelper helper;

    private CimiRequest request;

    private CimiResponse response;

    private CimiContext context;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setBaseUri("/");
        RequestHeader header = new RequestHeader();
        header.setCimiSelect(new CimiSelect());
        this.request.setHeader(header);

        this.response = new CimiResponse();
        this.context = new CimiContextImpl(this.request, this.response);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        EasyMock.reset(this.serviceCredentials);
        EasyMock.reset(this.serviceMachine);
        EasyMock.reset(this.serviceMachineImage);
    }

    @Test
    public void testCimiCredentials() throws Exception {
        System.out.println("testCimiCredentials");
        CimiCredentials cimi;

        Credentials reference;
        reference = new Credentials();
        reference.setId(456);
        reference.setName("refName");

        // Only by value
        EasyMock.replay(this.serviceCredentials);

        cimi = new CimiCredentials();
        cimi.setName("name");
        this.helper.merge(this.context, cimi);

        EasyMock.verify(this.serviceCredentials);
        EasyMock.reset(this.serviceCredentials);

        // Only by reference
        EasyMock.expect(this.serviceCredentials.getCredentialsById("456")).andReturn(reference);
        EasyMock.replay(this.serviceCredentials);

        cimi = new CimiCredentials(this.request.getBaseUri() + ExchangeType.Credential.getPathType().getPathname() + "/456");
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("refName", cimi.getName());

        EasyMock.verify(this.serviceCredentials);
        EasyMock.reset(this.serviceCredentials);

        // By reference and by value
        EasyMock.expect(this.serviceCredentials.getCredentialsById("456")).andReturn(reference);
        EasyMock.replay(this.serviceCredentials);

        cimi = new CimiCredentials(this.request.getBaseUri() + ExchangeType.Credential.getPathType().getPathname() + "/456");
        cimi.setName("name");
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("name", cimi.getName());

        EasyMock.verify(this.serviceCredentials);
        EasyMock.reset(this.serviceCredentials);
    }

    @Test
    public void testCimiCredentialsCreate() throws Exception {
        System.out.println("testCimiCredentialsCreate");
        // Mock only one method
        MergeReferenceHelperImpl mockedClass = EasyMock.createMockBuilder(MergeReferenceHelperImpl.class)
            .addMockedMethod("merge", CimiContext.class, CimiCredentialsTemplate.class).createMock();

        // Prepare parameters
        CimiCredentialsTemplate template;
        template = new CimiCredentialsTemplate();
        template.setName("name");

        CimiCredentialsCreate cimi;
        cimi = new CimiCredentialsCreate();
        cimi.setCredentialTemplate(template);

        // Prepare the call to test with the final parameters
        mockedClass.merge(this.context, template);
        EasyMock.replay(mockedClass);

        // Call the method that must call the mock method
        mockedClass.merge(this.context, cimi);
        EasyMock.verify(mockedClass);
    }

    @Test
    public void testCimiCredentialsTemplate() throws Exception {
        System.out.println("testCimiCredentialsTemplate");
        CimiCredentialsTemplate cimi;

        CredentialsTemplate reference;
        reference = new CredentialsTemplate();
        reference.setId(456);
        reference.setName("refName");
        reference.setDescription("refDescription");

        // Only by value
        EasyMock.replay(this.serviceCredentials);

        cimi = new CimiCredentialsTemplate();
        cimi.setName("name");
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("name", cimi.getName());
        Assert.assertNull("description", cimi.getDescription());

        EasyMock.verify(this.serviceCredentials);
        EasyMock.reset(this.serviceCredentials);

        // Only by reference
        EasyMock.expect(this.serviceCredentials.getCredentialsTemplateById("456")).andReturn(reference);
        EasyMock.replay(this.serviceCredentials);

        cimi = new CimiCredentialsTemplate(this.request.getBaseUri()
            + ExchangeType.CredentialTemplate.getPathType().getPathname() + "/456");
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("refName", cimi.getName());
        Assert.assertEquals("refDescription", cimi.getDescription());

        EasyMock.verify(this.serviceCredentials);
        EasyMock.reset(this.serviceCredentials);

        // By reference and by value
        EasyMock.expect(this.serviceCredentials.getCredentialsTemplateById("456")).andReturn(reference);
        EasyMock.replay(this.serviceCredentials);

        cimi = new CimiCredentialsTemplate(this.request.getBaseUri()
            + ExchangeType.CredentialTemplate.getPathType().getPathname() + "/456");
        cimi.setName("name");
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("name", cimi.getName());
        Assert.assertEquals("refDescription", cimi.getDescription());

        EasyMock.verify(this.serviceCredentials);
        EasyMock.reset(this.serviceCredentials);
    }

    @Test
    public void testCimiMachineImage() throws Exception {
        System.out.println("testCimiMachineImage");
        CimiMachineImage cimi;

        MachineImage reference;
        reference = new MachineImage();
        reference.setId(456);
        reference.setName("refName");

        // Only by value
        EasyMock.replay(this.serviceMachineImage);

        cimi = new CimiMachineImage();
        cimi.setName("name");
        this.helper.merge(this.context, cimi);

        EasyMock.verify(this.serviceMachineImage);
        EasyMock.reset(this.serviceMachineImage);

        // Only by reference
        EasyMock.expect(this.serviceMachineImage.getMachineImageById("456")).andReturn(reference);
        EasyMock.replay(this.serviceMachineImage);

        cimi = new CimiMachineImage(this.request.getBaseUri() + ExchangeType.MachineImage.getPathType().getPathname() + "/456");
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("refName", cimi.getName());

        EasyMock.verify(this.serviceMachineImage);
        EasyMock.reset(this.serviceMachineImage);

        // By reference and by value
        EasyMock.expect(this.serviceMachineImage.getMachineImageById("456")).andReturn(reference);
        EasyMock.replay(this.serviceMachineImage);

        cimi = new CimiMachineImage(this.request.getBaseUri() + ExchangeType.MachineImage.getPathType().getPathname() + "/456");
        cimi.setName("name");
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("name", cimi.getName());

        EasyMock.verify(this.serviceMachineImage);
        EasyMock.reset(this.serviceMachineImage);
    }

    @Test
    public void testCimiMachineConfiguration() throws Exception {
        System.out.println("testCimiMachineConfiguration");
        CimiMachineConfiguration cimi;

        MachineConfiguration reference;
        reference = new MachineConfiguration();
        reference.setId(456);
        reference.setName("refName");

        // Only by value
        EasyMock.replay(this.serviceMachine);

        cimi = new CimiMachineConfiguration();
        cimi.setName("name");
        this.helper.merge(this.context, cimi);

        EasyMock.verify(this.serviceMachine);
        EasyMock.reset(this.serviceMachine);

        // Only by reference
        EasyMock.expect(this.serviceMachine.getMachineConfigurationById("456")).andReturn(reference);
        EasyMock.replay(this.serviceMachine);

        cimi = new CimiMachineConfiguration(this.request.getBaseUri()
            + ExchangeType.MachineConfiguration.getPathType().getPathname() + "/456");
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("refName", cimi.getName());

        EasyMock.verify(this.serviceMachine);
        EasyMock.reset(this.serviceMachine);

        // By reference and by value
        EasyMock.expect(this.serviceMachine.getMachineConfigurationById("456")).andReturn(reference);
        EasyMock.replay(this.serviceMachine);

        cimi = new CimiMachineConfiguration(this.request.getBaseUri()
            + ExchangeType.MachineConfiguration.getPathType().getPathname() + "/456");
        cimi.setName("name");
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("name", cimi.getName());

        EasyMock.verify(this.serviceMachine);
        EasyMock.reset(this.serviceMachine);
    }

    @Test
    // TODO Volumes, Network, ..
    public void testCimiMachineTemplate() throws Exception {
        System.out.println("testCimiMachineTemplate");
        CimiMachineTemplate cimi;
        CimiCredentials cimiCredentials;
        CimiMachineImage cimiImage;
        CimiMachineConfiguration cimiConfiguration;

        MachineTemplate reference;
        reference = new MachineTemplate();
        reference.setId(123);
        reference.setName("refName");
        reference.setDescription("refDescription");

        Credentials referenceCredentials;
        referenceCredentials = new Credentials();
        referenceCredentials.setId(234);
        referenceCredentials.setName("refNameCredentials");
        referenceCredentials.setDescription("refDescriptionCredentials");

        MachineImage referenceImage;
        referenceImage = new MachineImage();
        referenceImage.setId(345);
        referenceImage.setName("refNameImage");
        referenceImage.setDescription("refDescriptionImage");

        MachineConfiguration referenceConfiguration;
        referenceConfiguration = new MachineConfiguration();
        referenceConfiguration.setId(456);
        referenceConfiguration.setName("refNameConfiguration");
        referenceConfiguration.setDescription("refDescriptionConfiguration");

        // ---------------------------------------------------
        // Only by value : template
        EasyMock.replay(this.serviceCredentials);
        EasyMock.replay(this.serviceMachine);
        EasyMock.replay(this.serviceMachineImage);

        cimi = new CimiMachineTemplate();
        cimi.setName("name");
        this.helper.merge(this.context, cimi);

        EasyMock.verify(this.serviceCredentials);
        EasyMock.verify(this.serviceMachine);
        EasyMock.verify(this.serviceMachineImage);
        EasyMock.reset(this.serviceCredentials);
        EasyMock.reset(this.serviceMachine);
        EasyMock.reset(this.serviceMachineImage);

        // ---------------------------------------------------
        // Only by reference : template
        EasyMock.replay(this.serviceCredentials);
        EasyMock.expect(this.serviceMachine.getMachineTemplateById("123")).andReturn(reference);
        EasyMock.replay(this.serviceMachine);
        EasyMock.replay(this.serviceMachineImage);

        cimi = new CimiMachineTemplate(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathType().getPathname()
            + "/123");
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("refName", cimi.getName());

        EasyMock.verify(this.serviceCredentials);
        EasyMock.verify(this.serviceMachine);
        EasyMock.verify(this.serviceMachineImage);
        EasyMock.reset(this.serviceCredentials);
        EasyMock.reset(this.serviceMachine);
        EasyMock.reset(this.serviceMachineImage);

        // ---------------------------------------------------
        // By reference and by value : template
        EasyMock.replay(this.serviceCredentials);
        EasyMock.expect(this.serviceMachine.getMachineTemplateById("123")).andReturn(reference);
        EasyMock.replay(this.serviceMachine);
        EasyMock.replay(this.serviceMachineImage);

        cimi = new CimiMachineTemplate(this.request.getBaseUri() + ExchangeType.MachineTemplate.getPathType().getPathname()
            + "/123");
        cimi.setName("name");
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("name", cimi.getName());

        EasyMock.verify(this.serviceCredentials);
        EasyMock.verify(this.serviceMachine);
        EasyMock.verify(this.serviceMachineImage);
        EasyMock.reset(this.serviceCredentials);
        EasyMock.reset(this.serviceMachine);
        EasyMock.reset(this.serviceMachineImage);

        // ---------------------------------------------------
        // Only by value : template and internal entities
        EasyMock.replay(this.serviceCredentials);
        EasyMock.replay(this.serviceMachine);
        EasyMock.replay(this.serviceMachineImage);

        cimiCredentials = new CimiCredentials();
        cimiCredentials.setName("nameCredentials");
        cimiConfiguration = new CimiMachineConfiguration();
        cimiConfiguration.setName("nameConfiguration");
        cimiImage = new CimiMachineImage();
        cimiImage.setName("nameImage");

        cimi = new CimiMachineTemplate();
        cimi.setCredentials(cimiCredentials);
        cimi.setMachineConfig(cimiConfiguration);
        cimi.setMachineImage(cimiImage);
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("nameCredentials", cimi.getCredentials().getName());
        Assert.assertNull(cimi.getCredentials().getDescription());
        Assert.assertEquals("nameConfiguration", cimi.getMachineConfig().getName());
        Assert.assertNull(cimi.getMachineConfig().getDescription());
        Assert.assertEquals("nameImage", cimi.getMachineImage().getName());
        Assert.assertNull(cimi.getMachineImage().getDescription());

        EasyMock.verify(this.serviceCredentials);
        EasyMock.verify(this.serviceMachine);
        EasyMock.verify(this.serviceMachineImage);
        EasyMock.reset(this.serviceCredentials);
        EasyMock.reset(this.serviceMachine);
        EasyMock.reset(this.serviceMachineImage);

        // ---------------------------------------------------
        // Template by value, internal entities only by reference
        EasyMock.expect(this.serviceCredentials.getCredentialsById("234")).andReturn(referenceCredentials);
        EasyMock.replay(this.serviceCredentials);
        EasyMock.expect(this.serviceMachine.getMachineConfigurationById("345")).andReturn(referenceConfiguration);
        EasyMock.replay(this.serviceMachine);
        EasyMock.expect(this.serviceMachineImage.getMachineImageById("456")).andReturn(referenceImage);
        EasyMock.replay(this.serviceMachineImage);

        cimi = new CimiMachineTemplate();
        cimi.setCredentials(new CimiCredentials(this.request.getBaseUri() + ExchangeType.Credential.getPathType().getPathname()
            + "/234"));
        cimi.setMachineConfig(new CimiMachineConfiguration(this.request.getBaseUri()
            + ExchangeType.MachineConfiguration.getPathType().getPathname() + "/345"));
        cimi.setMachineImage(new CimiMachineImage(this.request.getBaseUri()
            + ExchangeType.MachineImage.getPathType().getPathname() + "/456"));
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("refNameCredentials", cimi.getCredentials().getName());
        Assert.assertEquals("refDescriptionCredentials", cimi.getCredentials().getDescription());
        Assert.assertEquals("refNameConfiguration", cimi.getMachineConfig().getName());
        Assert.assertEquals("refDescriptionConfiguration", cimi.getMachineConfig().getDescription());
        Assert.assertEquals("refNameImage", cimi.getMachineImage().getName());
        Assert.assertEquals("refDescriptionImage", cimi.getMachineImage().getDescription());

        EasyMock.verify(this.serviceCredentials);
        EasyMock.verify(this.serviceMachine);
        EasyMock.verify(this.serviceMachineImage);
        EasyMock.reset(this.serviceCredentials);
        EasyMock.reset(this.serviceMachine);
        EasyMock.reset(this.serviceMachineImage);

        // ---------------------------------------------------
        // Template by value, internal entities by reference and by value
        EasyMock.expect(this.serviceCredentials.getCredentialsById("234")).andReturn(referenceCredentials);
        EasyMock.replay(this.serviceCredentials);
        EasyMock.expect(this.serviceMachine.getMachineConfigurationById("345")).andReturn(referenceConfiguration);
        EasyMock.replay(this.serviceMachine);
        EasyMock.expect(this.serviceMachineImage.getMachineImageById("456")).andReturn(referenceImage);
        EasyMock.replay(this.serviceMachineImage);

        cimi = new CimiMachineTemplate();
        cimi.setCredentials(new CimiCredentials(this.request.getBaseUri() + ExchangeType.Credential.getPathType().getPathname()
            + "/234"));
        cimi.getCredentials().setName("nameCredentials");
        cimi.setMachineConfig(new CimiMachineConfiguration(this.request.getBaseUri()
            + ExchangeType.MachineConfiguration.getPathType().getPathname() + "/345"));
        cimi.getMachineConfig().setName("nameConfiguration");
        cimi.setMachineImage(new CimiMachineImage(this.request.getBaseUri()
            + ExchangeType.MachineImage.getPathType().getPathname() + "/456"));
        cimi.getMachineImage().setName("nameImage");
        this.helper.merge(this.context, cimi);

        Assert.assertEquals("nameCredentials", cimi.getCredentials().getName());
        Assert.assertEquals("refDescriptionCredentials", cimi.getCredentials().getDescription());
        Assert.assertEquals("nameConfiguration", cimi.getMachineConfig().getName());
        Assert.assertEquals("refDescriptionConfiguration", cimi.getMachineConfig().getDescription());
        Assert.assertEquals("nameImage", cimi.getMachineImage().getName());
        Assert.assertEquals("refDescriptionImage", cimi.getMachineImage().getDescription());

        EasyMock.verify(this.serviceCredentials);
        EasyMock.verify(this.serviceMachine);
        EasyMock.verify(this.serviceMachineImage);
        EasyMock.reset(this.serviceCredentials);
        EasyMock.reset(this.serviceMachine);
        EasyMock.reset(this.serviceMachineImage);
    }

    @Test
    public void testCimiMachineCreate() throws Exception {
        System.out.println("testCimiMachineCreate");
        // Mock only one method
        MergeReferenceHelperImpl mockedClass = EasyMock.createMockBuilder(MergeReferenceHelperImpl.class)
            .addMockedMethod("merge", CimiContext.class, CimiMachineTemplate.class).createMock();

        // Prepare parameters
        CimiMachineTemplate template;
        template = new CimiMachineTemplate();
        template.setName("name");

        CimiMachineCreate cimi;
        cimi = new CimiMachineCreate();
        cimi.setMachineTemplate(template);

        // Prepare the call to test with the final parameters
        mockedClass.merge(this.context, template);
        EasyMock.replay(mockedClass);

        // Call the method that must call the mock method
        mockedClass.merge(this.context, cimi);
        EasyMock.verify(mockedClass);
    }

}
