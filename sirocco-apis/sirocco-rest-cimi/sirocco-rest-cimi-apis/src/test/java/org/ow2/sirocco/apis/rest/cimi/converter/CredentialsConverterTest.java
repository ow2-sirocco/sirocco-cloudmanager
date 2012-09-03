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
package org.ow2.sirocco.apis.rest.cimi.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredential;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCredentialTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiExpand;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.apis.rest.cimi.request.CimiSelect;
import org.ow2.sirocco.apis.rest.cimi.request.RequestParams;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;

/**
 * Converters tests of credential resources.
 */
public class CredentialsConverterTest {

    private CimiRequest request;

    private CimiContext context;

    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setBaseUri("http://www.test.org/");
        RequestParams header = new RequestParams();
        header.setCimiSelect(new CimiSelect());
        header.setCimiExpand(new CimiExpand());
        this.request.setParams(header);

        this.context = new CimiContextImpl(this.request, new CimiResponse());
    }

    @Test
    public void testCimiCredential() throws Exception {
        CimiCredential cimi;
        Credentials service;

        // Empty Cimi -> Service
        service = (Credentials) this.context.convertToService(new CimiCredential());
        Assert.assertNull(service.getPassword());
        Assert.assertNull(service.getUserName());
        Assert.assertNull(service.getPublicKey());

        // Empty Service -> Cimi
        cimi = (CimiCredential) this.context.convertToCimi(new Credentials(), CimiCredential.class);
        Assert.assertNull(cimi.getPassword());
        Assert.assertNull(cimi.getUserName());
        Assert.assertNull(cimi.getKey());

        // Full Cimi -> Service
        cimi = new CimiCredential();
        cimi.setPassword("password");
        cimi.setUserName("userName");
        cimi.setKey(new byte[] {1, 2, 3, 4, 5});

        service = (Credentials) this.context.convertToService(cimi);
        Assert.assertEquals("password", service.getPassword());
        Assert.assertEquals("userName", service.getUserName());
        Assert.assertArrayEquals(cimi.getKey(), service.getPublicKey());

        // Full Service -> Cimi
        service = new Credentials();
        service.setPassword("password");
        service.setUserName("userName");
        service.setPublicKey(new byte[] {6, 7, 8, 9, 10, 11});

        cimi = (CimiCredential) this.context.convertToCimi(service, CimiCredential.class);
        Assert.assertNull(cimi.getPassword());
        Assert.assertEquals("userName", cimi.getUserName());
        Assert.assertArrayEquals(service.getPublicKey(), cimi.getKey());

        // Full Service -> Cimi with "write only" data
        this.context.setConvertedWriteOnly(true);
        cimi = (CimiCredential) this.context.convertToCimi(service, CimiCredential.class);
        Assert.assertEquals("password", cimi.getPassword());
        Assert.assertEquals("userName", cimi.getUserName());
        Assert.assertArrayEquals(service.getPublicKey(), cimi.getKey());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCimiCredentialsCollection() throws Exception {
        CimiCredentialCollection cimi;
        List<Credentials> service;

        // Empty Cimi -> Service
        service = (List<Credentials>) this.context.convertToService(new CimiCredentialCollection());
        Assert.assertNotNull(service);
        Assert.assertEquals(0, service.size());

        // Empty Service -> Cimi
        service = new ArrayList<Credentials>();
        cimi = (CimiCredentialCollection) this.context.convertToCimi(new ArrayList<Credentials>(),
            CimiCredentialCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiCredentialCollection();
        cimi.setArray(new CimiCredential[] {new CimiCredential(), new CimiCredential()});

        service = (List<Credentials>) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.size());

        // Full Service -> Cimi
        Credentials Credentials1 = new Credentials();
        Credentials1.setId(1);
        Credentials1.setName("nameOne");
        Credentials Credentials2 = new Credentials();
        Credentials2.setId(2);
        Credentials2.setName("nameTwo");
        Credentials Credentials3 = new Credentials();
        Credentials3.setId(3);
        Credentials3.setName("nameThree");

        service = new ArrayList<Credentials>();
        service.addAll(Arrays.asList(new Credentials[] {Credentials1, Credentials2, Credentials3}));

        cimi = (CimiCredentialCollection) this.context.convertToCimi(service, CimiCredentialCollection.class);
        Assert.assertEquals(3, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Credential.getPathname() + "/1",
            cimi.getArray()[0].getHref());
        Assert.assertNotNull(cimi.getArray()[0].getId());
        Assert.assertNotNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Credential.getPathname() + "/2",
            cimi.getArray()[1].getHref());
        Assert.assertNotNull(cimi.getArray()[1].getId());
        Assert.assertNotNull(cimi.getArray()[1].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Credential.getPathname() + "/3",
            cimi.getArray()[2].getHref());
        Assert.assertNotNull(cimi.getArray()[2].getId());
        Assert.assertNotNull(cimi.getArray()[2].getName());

        cimi = (CimiCredentialCollection) this.context.convertToCimi(
            Arrays.asList(new Credentials[] {Credentials3, Credentials1}), CimiCredentialCollection.class);
        Assert.assertEquals(2, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Credential.getPathname() + "/3",
            cimi.getArray()[0].getHref());
        Assert.assertNotNull(cimi.getArray()[0].getId());
        Assert.assertNotNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Credential.getPathname() + "/1",
            cimi.getArray()[1].getHref());
        Assert.assertNotNull(cimi.getArray()[1].getId());
        Assert.assertNotNull(cimi.getArray()[1].getName());
    }

    @Test
    public void testCimiCredentialTemplate() throws Exception {
        CimiCredentialTemplate cimi;
        CredentialsTemplate service;

        // Empty Cimi -> Service
        service = (CredentialsTemplate) this.context.convertToService(new CimiCredentialTemplate());
        Assert.assertNull(service.getPassword());
        Assert.assertNull(service.getUserName());
        Assert.assertNull(service.getPublicKey());

        // Empty Service -> Cimi
        cimi = (CimiCredentialTemplate) this.context.convertToCimi(new CredentialsTemplate(), CimiCredentialTemplate.class);
        Assert.assertNull(cimi.getPassword());
        Assert.assertNull(cimi.getUserName());
        Assert.assertNull(cimi.getKey());

        // Full Cimi -> Service
        cimi = new CimiCredentialTemplate();
        cimi.setPassword("password");
        cimi.setUserName("userName");
        cimi.setKey(new byte[] {1, 2, 3, 4, 5});

        service = (CredentialsTemplate) this.context.convertToService(cimi);
        Assert.assertEquals("password", service.getPassword());
        Assert.assertEquals("userName", service.getUserName());
        Assert.assertArrayEquals(cimi.getKey(), service.getPublicKey());

        // Full Service -> Cimi
        service = new CredentialsTemplate();
        service.setPassword("password");
        service.setUserName("userName");
        service.setPublicKey(new byte[] {6, 7, 8, 9, 10, 11});
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCimiCredentialsTemplateCollection() throws Exception {

        CimiCredentialTemplateCollection cimi;
        List<CredentialsTemplate> service;

        // Empty Cimi -> Service
        service = (List<CredentialsTemplate>) this.context.convertToService(new CimiCredentialTemplateCollection());
        Assert.assertNotNull(service);
        Assert.assertEquals(0, service.size());

        // Empty Service -> Cimi
        cimi = (CimiCredentialTemplateCollection) this.context.convertToCimi(new ArrayList<CredentialsTemplate>(),
            CimiCredentialTemplateCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiCredentialTemplateCollection();
        cimi.add(new CimiCredentialTemplate());
        cimi.add(new CimiCredentialTemplate());

        service = (List<CredentialsTemplate>) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.size());

        // Full Service -> Cimi
        CredentialsTemplate credentialTemplate1 = new CredentialsTemplate();
        credentialTemplate1.setId(1);
        credentialTemplate1.setName("nameOne");
        CredentialsTemplate credentialTemplate2 = new CredentialsTemplate();
        credentialTemplate2.setId(2);
        credentialTemplate2.setName("nameTwo");
        CredentialsTemplate credentialTemplate3 = new CredentialsTemplate();
        credentialTemplate3.setId(3);
        credentialTemplate3.setName("nameThree");

        service = new ArrayList<CredentialsTemplate>();
        service.add(credentialTemplate1);
        service.add(credentialTemplate2);
        service.add(credentialTemplate3);

        cimi = (CimiCredentialTemplateCollection) this.context.convertToCimi(service, CimiCredentialTemplateCollection.class);
        Assert.assertEquals(3, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialTemplate.getPathname() + "/1",
            cimi.getArray()[0].getHref());
        Assert.assertNotNull(cimi.getArray()[0].getId());
        Assert.assertNotNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialTemplate.getPathname() + "/2",
            cimi.getArray()[1].getHref());
        Assert.assertNotNull(cimi.getArray()[1].getId());
        Assert.assertNotNull(cimi.getArray()[1].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialTemplate.getPathname() + "/3",
            cimi.getArray()[2].getHref());
        Assert.assertNotNull(cimi.getArray()[2].getId());
        Assert.assertNotNull(cimi.getArray()[2].getName());

        cimi = (CimiCredentialTemplateCollection) this.context.convertToCimi(
            Arrays.asList(new CredentialsTemplate[] {credentialTemplate3, credentialTemplate1}),
            CimiCredentialTemplateCollection.class);
        Assert.assertEquals(2, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialTemplate.getPathname() + "/3",
            cimi.getArray()[0].getHref());
        Assert.assertNotNull(cimi.getArray()[0].getId());
        Assert.assertNotNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialTemplate.getPathname() + "/1",
            cimi.getArray()[1].getHref());
        Assert.assertNotNull(cimi.getArray()[1].getId());
        Assert.assertNotNull(cimi.getArray()[1].getName());
    }

    @Test
    public void testCimiCredentialsCreate() throws Exception {
        CimiCredentialCreate cimi;
        CredentialsCreate service;

        // Empty Cimi -> Service
        service = (CredentialsCreate) this.context.convertToService(new CimiCredentialCreate());
        Assert.assertNull(service.getCredentialsTemplate());

        // Full Cimi -> Service
        cimi = new CimiCredentialCreate();
        cimi.setCredentialTemplate(new CimiCredentialTemplate());

        service = (CredentialsCreate) this.context.convertToService(cimi);
        Assert.assertEquals(CredentialsTemplate.class, service.getCredentialsTemplate().getClass());
    }
}