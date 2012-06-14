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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentials;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsCreate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiCredentialsTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplateCollection;

/**
 * Converters tests of credentials resources.
 */
public class CredentialsConverterTest {

    private CimiRequest request;

    private CimiContext context;

    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setBaseUri("http://www.test.org/");
        this.context = new CimiContextImpl(this.request, new CimiResponse());
    }

    @Test
    public void testCimiCredentials() throws Exception {
        CimiCredentials cimi;
        Credentials service;

        // Empty Cimi -> Service
        service = (Credentials) this.context.convertToService(new CimiCredentials());
        Assert.assertNull(service.getPassword());
        Assert.assertNull(service.getUserName());
        Assert.assertNull(service.getPublicKey());

        // Empty Service -> Cimi
        cimi = (CimiCredentials) this.context.convertToCimi(new Credentials(), CimiCredentials.class);
        Assert.assertNull(cimi.getPassword());
        Assert.assertNull(cimi.getUserName());
        Assert.assertNull(cimi.getKey());

        // Full Cimi -> Service
        cimi = new CimiCredentials();
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

        cimi = (CimiCredentials) this.context.convertToCimi(service, CimiCredentials.class);
        Assert.assertNull(cimi.getPassword());
        Assert.assertEquals("userName", cimi.getUserName());
        Assert.assertNull(cimi.getKey());

        // Full Service -> Cimi with "write only" data
        this.context.setConvertedWriteOnly(true);
        cimi = (CimiCredentials) this.context.convertToCimi(service, CimiCredentials.class);
        Assert.assertEquals("password", cimi.getPassword());
        Assert.assertEquals("userName", cimi.getUserName());
        Assert.assertArrayEquals(service.getPublicKey(), cimi.getKey());
    }

    @Test
    public void testCimiCredentialsCollection() throws Exception {
        CimiCredentialsCollection cimi;
        CredentialsCollection service;

        // Empty Cimi -> Service
        service = (CredentialsCollection) this.context.convertToService(new CimiCredentialsCollection());
        Assert.assertNull(service.getCredentials());

        // Empty Service -> Cimi : list null
        cimi = (CimiCredentialsCollection) this.context.convertToCimi(new CredentialsCollection(),
            CimiCredentialsCollection.class);
        Assert.assertNull(cimi.getArray());

        // Empty Service -> Cimi : list empty
        service = new CredentialsCollection();
        service.setCredentials(Arrays.asList(new Credentials[] {}));
        cimi = (CimiCredentialsCollection) this.context.convertToCimi(new CredentialsCollection(),
            CimiCredentialsCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiCredentialsCollection();
        cimi.setArray(new CimiCredentials[] {new CimiCredentials(), new CimiCredentials()});

        service = (CredentialsCollection) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.getCredentials().size());

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

        service = new CredentialsCollection();
        service.setCredentials(Arrays.asList(new Credentials[] {Credentials1, Credentials2, Credentials3}));

        cimi = (CimiCredentialsCollection) this.context.convertToCimi(service, CimiCredentialsCollection.class);
        Assert.assertEquals(3, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Credentials.getPathname() + "/1",
            cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Credentials.getPathname() + "/2",
            cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Credentials.getPathname() + "/3",
            cimi.getArray()[2].getHref());
        Assert.assertNull(cimi.getArray()[2].getId());
        Assert.assertNull(cimi.getArray()[2].getName());

        cimi = (CimiCredentialsCollection) this.context.convertToCimi(
            Arrays.asList(new Credentials[] {Credentials3, Credentials1}), CimiCredentialsCollection.class);
        Assert.assertEquals(2, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Credentials.getPathname() + "/3",
            cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Credentials.getPathname() + "/1",
            cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
    }

    @Test
    public void testCimiCredentialsTemplate() throws Exception {
        CimiCredentialsTemplate cimi;
        CredentialsTemplate service;

        // Empty Cimi -> Service
        service = (CredentialsTemplate) this.context.convertToService(new CimiCredentialsTemplate());
        Assert.assertNull(service.getPassword());
        Assert.assertNull(service.getUserName());
        Assert.assertNull(service.getPublicKey());

        // Empty Service -> Cimi
        cimi = (CimiCredentialsTemplate) this.context.convertToCimi(new CredentialsTemplate(), CimiCredentialsTemplate.class);
        Assert.assertNull(cimi.getPassword());
        Assert.assertNull(cimi.getUserName());
        Assert.assertNull(cimi.getKey());

        // Full Cimi -> Service
        cimi = new CimiCredentialsTemplate();
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
    public void testCimiCredentialsTemplateCollection() throws Exception {
        CimiCredentialsTemplateCollection cimi;
        CredentialsTemplateCollection service;

        // Empty Cimi -> Service
        service = (CredentialsTemplateCollection) this.context.convertToService(new CimiCredentialsTemplateCollection());
        Assert.assertNull(service.getCredentialsTemplates());

        // Empty Service -> Cimi
        cimi = (CimiCredentialsTemplateCollection) this.context.convertToCimi(new CredentialsTemplateCollection(),
            CimiCredentialsTemplateCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiCredentialsTemplateCollection();
        cimi.setArray(new CimiCredentialsTemplate[] {new CimiCredentialsTemplate(), new CimiCredentialsTemplate()});

        service = (CredentialsTemplateCollection) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.getCredentialsTemplates().size());

        // Full Service -> Cimi
        CredentialsTemplate CredentialsTemplate1 = new CredentialsTemplate();
        CredentialsTemplate1.setId(1);
        CredentialsTemplate1.setName("nameOne");
        CredentialsTemplate CredentialsTemplate2 = new CredentialsTemplate();
        CredentialsTemplate2.setId(2);
        CredentialsTemplate2.setName("nameTwo");
        CredentialsTemplate CredentialsTemplate3 = new CredentialsTemplate();
        CredentialsTemplate3.setId(3);
        CredentialsTemplate3.setName("nameThree");

        service = new CredentialsTemplateCollection();
        service.setCredentialsTemplates(Arrays.asList(new CredentialsTemplate[] {CredentialsTemplate1, CredentialsTemplate2,
            CredentialsTemplate3}));

        cimi = (CimiCredentialsTemplateCollection) this.context.convertToCimi(service, CimiCredentialsTemplateCollection.class);
        Assert.assertEquals(3, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialsTemplate.getPathname() + "/1",
            cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialsTemplate.getPathname() + "/2",
            cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialsTemplate.getPathname() + "/3",
            cimi.getArray()[2].getHref());
        Assert.assertNull(cimi.getArray()[2].getId());
        Assert.assertNull(cimi.getArray()[2].getName());

        cimi = (CimiCredentialsTemplateCollection) this.context.convertToCimi(
            Arrays.asList(new CredentialsTemplate[] {CredentialsTemplate3, CredentialsTemplate1}),
            CimiCredentialsTemplateCollection.class);
        Assert.assertEquals(2, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialsTemplate.getPathname() + "/3",
            cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.CredentialsTemplate.getPathname() + "/1",
            cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
    }

    @Test
    public void testCimiCredentialsCreate() throws Exception {
        CimiCredentialsCreate cimi;
        CredentialsCreate service;

        // Empty Cimi -> Service
        service = (CredentialsCreate) this.context.convertToService(new CimiCredentialsCreate());
        Assert.assertNull(service.getCredentialsTemplate());

        // Full Cimi -> Service
        cimi = new CimiCredentialsCreate();
        cimi.setCredentialsTemplate(new CimiCredentialsTemplate());

        service = (CredentialsCreate) this.context.convertToService(cimi);
        Assert.assertEquals(CredentialsTemplate.class, service.getCredentialsTemplate().getClass());
    }
}