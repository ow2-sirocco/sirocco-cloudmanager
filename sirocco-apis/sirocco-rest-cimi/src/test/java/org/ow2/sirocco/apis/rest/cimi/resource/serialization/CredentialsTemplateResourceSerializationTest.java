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
package org.ow2.sirocco.apis.rest.cimi.resource.serialization;

import java.io.StringReader;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import junit.framework.Assert;
import net.javacrumbs.jsonunit.JsonAssert;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.resource.serialization.json.JsonLocator;
import org.ow2.sirocco.apis.rest.cimi.resource.serialization.xml.XmlLocator;
import org.ow2.sirocco.apis.rest.cimi.server.SiroccoRestCimiApplication;
import org.ow2.sirocco.apis.rest.cimi.utils.Constants;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

public class CredentialsTemplateResourceSerializationTest extends JerseyTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialsTemplateResourceSerializationTest.class);

    /**
     * {@inheritDoc}
     * 
     * @see com.sun.jersey.test.framework.JerseyTest#configure()
     */
    @Override
    protected AppDescriptor configure() {
        return new WebAppDescriptor.Builder("javax.ws.rs.Application", SiroccoRestCimiApplication.class.getName())
            .initParam(JSONConfiguration.FEATURE_POJO_MAPPING, "true").contextPath("sirocco-rest")
            .servletClass(SpringServlet.class).contextListenerClass(ContextLoaderListener.class)
            .contextParam("contextConfigLocation", "classpath:context/serializationResourcesContext.xml").build();
    }

    /**
     * @throws Exception In case of error
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreAttributeOrder(true);
    }

    /**
     * Test GET.
     * 
     * @throws Exception In case of error
     */
    @Test
    public final void testGetCredentialsTemplateJson() throws Exception {
        ClientResponse clientResponse = null;
        String entityResponse;
        int statusResponse;

        // JSON : id = 0
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE + "/0")
            .accept(MediaType.APPLICATION_JSON_TYPE).header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .get(ClientResponse.class);
        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        JsonAssert.assertJsonEquals(SerializationHelper.getResourceAsReader(JsonLocator.class, "CredentialsTemplate-0.json"),
            new StringReader(entityResponse));

        // JSON : id = 1
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE + "/1")
            .accept(MediaType.APPLICATION_JSON_TYPE).header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .get(ClientResponse.class);
        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        JsonAssert.assertJsonEquals(SerializationHelper.getResourceAsReader(JsonLocator.class, "CredentialsTemplate-1.json"),
            new StringReader(entityResponse));

        // JSON : id = 2
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE + "/2")
            .accept(MediaType.APPLICATION_JSON_TYPE).header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .get(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        JsonAssert.assertJsonEquals(SerializationHelper.getResourceAsReader(JsonLocator.class, "CredentialsTemplate-2.json"),
            new StringReader(entityResponse));
    }

    /**
     * Test GET.
     * 
     * @throws Exception In case of error
     */
    @Test
    public final void testGetCredentialsTemplateXml() throws Exception {
        ClientResponse clientResponse = null;
        String entityResponse;
        int statusResponse;

        // XML : id = 0
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE + "/0").accept(MediaType.APPLICATION_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI).get(ClientResponse.class);
        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        XMLAssert.assertXMLEqual(SerializationHelper.getResourceAsReader(XmlLocator.class, "CredentialsTemplate-0.xml"),
            new StringReader(entityResponse));

        // XML : id = 1
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE + "/1").accept(MediaType.APPLICATION_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI).get(ClientResponse.class);
        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        XMLAssert.assertXMLEqual(SerializationHelper.getResourceAsReader(XmlLocator.class, "CredentialsTemplate-1.xml"),
            new StringReader(entityResponse));

        // XML : id = 2
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE + "/2").accept(MediaType.APPLICATION_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI).get(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        XMLAssert.assertXMLEqual(SerializationHelper.getResourceAsReader(XmlLocator.class, "CredentialsTemplate-2.xml"),
            new StringReader(entityResponse));
    }

    /**
     * Test GET.
     * 
     * @throws Exception In case of error
     */
    @Test
    @Ignore
    public final void testGetCredentialsCollectionJson() throws Exception {
        ClientResponse clientResponse = null;
        String entityResponse;
        int statusResponse;

        // JSON : id = 0
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE).accept(MediaType.APPLICATION_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .header(Constants.HEADER_SIROCCO_INFO_TEST_ID, 0).get(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        JsonAssert.assertJsonEquals(
            SerializationHelper.getResourceAsReader(JsonLocator.class, "CredentialsTemplateCollection-0.json"),
            new StringReader(entityResponse));

        // JSON : id = 1
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE).accept(MediaType.APPLICATION_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .header(Constants.HEADER_SIROCCO_INFO_TEST_ID, 1).get(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        JsonAssert.assertJsonEquals(
            SerializationHelper.getResourceAsReader(JsonLocator.class, "CredentialsTemplateCollection-1.json"),
            new StringReader(entityResponse));

        // JSON : id = 3
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE).accept(MediaType.APPLICATION_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .header(Constants.HEADER_SIROCCO_INFO_TEST_ID, 3).get(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        JsonAssert.assertJsonEquals(
            SerializationHelper.getResourceAsReader(JsonLocator.class, "CredentialsTemplateCollection-3.json"),
            new StringReader(entityResponse));

        // JSON : id = 3, expand
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE).accept(MediaType.APPLICATION_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .header(Constants.HEADER_SIROCCO_INFO_TEST_ID, 3).header(Constants.HEADER_SIROCCO_INFO_TEST_EXPAND, true)
            .get(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        JsonAssert.assertJsonEquals(
            SerializationHelper.getResourceAsReader(JsonLocator.class, "CredentialsTemplateCollection-3-expand.json"),
            new StringReader(entityResponse));
    }

    /**
     * Test GET.
     * 
     * @throws Exception In case of error
     */
    @Test
    @Ignore
    public final void testGetCredentialsCollectionXml() throws Exception {
        ClientResponse clientResponse = null;
        String entityResponse;
        int statusResponse;

        // XML : id = 0
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE).accept(MediaType.APPLICATION_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .header(Constants.HEADER_SIROCCO_INFO_TEST_ID, 0).get(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        XMLAssert.assertXMLEqual(SerializationHelper.getResourceAsReader(XmlLocator.class,
            "CredentialsTemplateCollection-0.xml"), new StringReader(entityResponse));

        // XML : id = 1
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE).accept(MediaType.APPLICATION_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .header(Constants.HEADER_SIROCCO_INFO_TEST_ID, 1).get(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        XMLAssert.assertXMLEqual(SerializationHelper.getResourceAsReader(XmlLocator.class,
            "CredentialsTemplateCollection-1.xml"), new StringReader(entityResponse));

        // XML : id = 3
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE).accept(MediaType.APPLICATION_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .header(Constants.HEADER_SIROCCO_INFO_TEST_ID, 3).get(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        XMLAssert.assertXMLEqual(SerializationHelper.getResourceAsReader(XmlLocator.class,
            "CredentialsTemplateCollection-3.xml"), new StringReader(entityResponse));

        // XML : id = 3, expand
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE).accept(MediaType.APPLICATION_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .header(Constants.HEADER_SIROCCO_INFO_TEST_ID, 3).header(Constants.HEADER_SIROCCO_INFO_TEST_EXPAND, true)
            .get(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        XMLAssert.assertXMLEqual(
            SerializationHelper.getResourceAsReader(XmlLocator.class, "CredentialsTemplateCollection-3-expand.xml"),
            new StringReader(entityResponse));
    }

    /**
     * Test PUT.
     * 
     * @throws Exception In case of error
     */
    @Test
    public final void testPutCredentialsTemplateJson() throws Exception {
        ClientResponse clientResponse = null;
        String entityResponse;
        int statusResponse;

        // JSON : id = 0
        clientResponse = this
            .resource()
            .path(ConstantsPath.CREDENTIALS_TEMPLATE + "/0")
            .accept(MediaType.APPLICATION_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(SerializationHelper.getResourceAsString(JsonLocator.class, "CredentialsTemplate-0.json"),
                MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        JsonAssert.assertJsonEquals(SerializationHelper.getResourceAsReader(JsonLocator.class, "Job-0.json"), new StringReader(
            entityResponse));

        // JSON : id = 1
        clientResponse = this
            .resource()
            .path(ConstantsPath.CREDENTIALS_TEMPLATE + "/1")
            .accept(MediaType.APPLICATION_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(SerializationHelper.getResourceAsString(JsonLocator.class, "CredentialsTemplate-1.json"),
                MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        JsonAssert.assertJsonEquals(SerializationHelper.getResourceAsReader(JsonLocator.class, "Job-1.json"), new StringReader(
            entityResponse));

        // JSON : id = 2
        clientResponse = this
            .resource()
            .path(ConstantsPath.CREDENTIALS_TEMPLATE + "/2")
            .accept(MediaType.APPLICATION_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(SerializationHelper.getResourceAsString(JsonLocator.class, "CredentialsTemplate-2.json"),
                MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        JsonAssert.assertJsonEquals(SerializationHelper.getResourceAsReader(JsonLocator.class, "Job-2.json"), new StringReader(
            entityResponse));

    }

    /**
     * Test PUT.
     * 
     * @throws Exception In case of error
     */
    @Test
    public final void testPutCredentialsTemplateXml() throws Exception {
        ClientResponse clientResponse = null;
        String entityResponse;
        int statusResponse;

        // XML : id = 0
        clientResponse = this
            .resource()
            .path(ConstantsPath.CREDENTIALS_TEMPLATE + "/0")
            .accept(MediaType.APPLICATION_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(SerializationHelper.getResourceAsString(XmlLocator.class, "CredentialsTemplate-0.xml"),
                MediaType.APPLICATION_XML).put(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        XMLAssert.assertXMLEqual(SerializationHelper.getResourceAsReader(XmlLocator.class, "Job-0.xml"), new StringReader(
            entityResponse));

        // XML : id = 1
        clientResponse = this
            .resource()
            .path(ConstantsPath.CREDENTIALS_TEMPLATE + "/1")
            .accept(MediaType.APPLICATION_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(SerializationHelper.getResourceAsString(XmlLocator.class, "CredentialsTemplate-1.xml"),
                MediaType.APPLICATION_XML).put(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        XMLAssert.assertXMLEqual(SerializationHelper.getResourceAsReader(XmlLocator.class, "Job-1.xml"), new StringReader(
            entityResponse));

        // XML : id = 2
        clientResponse = this
            .resource()
            .path(ConstantsPath.CREDENTIALS_TEMPLATE + "/2")
            .accept(MediaType.APPLICATION_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(SerializationHelper.getResourceAsString(XmlLocator.class, "CredentialsTemplate-2.xml"),
                MediaType.APPLICATION_XML).put(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
        XMLAssert.assertXMLEqual(SerializationHelper.getResourceAsReader(XmlLocator.class, "Job-2.xml"), new StringReader(
            entityResponse));

    }

    /**
     * Test POST.
     * 
     * @throws Exception In case of error
     */
    @Test
    public final void testPostCredentialsTemplateJson() throws Exception {
        ClientResponse clientResponse = null;
        String entityResponse;
        int statusResponse;
        MultivaluedMap<String, String> heardersResponse;

        // JSON : id = 1
        clientResponse = this
            .resource()
            .path(ConstantsPath.CREDENTIALS_TEMPLATE)
            .accept(MediaType.APPLICATION_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(SerializationHelper.getResourceAsString(JsonLocator.class, "CredentialsTemplate-1.json"),
                MediaType.APPLICATION_JSON).post(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);
        heardersResponse = clientResponse.getHeaders();

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(202, statusResponse);

        Assert.assertTrue(heardersResponse.containsKey(Constants.HEADER_CIMI_JOB_URI));
        Assert.assertTrue(heardersResponse.get(Constants.HEADER_CIMI_JOB_URI).get(0).endsWith("idValue_1"));

        Assert.assertTrue(heardersResponse.containsKey(Constants.HEADER_LOCATION));
        Assert.assertTrue(heardersResponse.get(Constants.HEADER_LOCATION).get(0)
            .endsWith(ConstantsPath.CREDENTIALS_TEMPLATE + "/" + "targetEntityValue_1"));

        JsonAssert.assertJsonEquals(SerializationHelper.getResourceAsReader(JsonLocator.class, "Job-1.json"), new StringReader(
            entityResponse));
    }

    /**
     * Test POST.
     * 
     * @throws Exception In case of error
     */
    @Test
    public final void testPostCredentialsTemplateXml() throws Exception {
        ClientResponse clientResponse = null;
        String entityResponse;
        int statusResponse;
        MultivaluedMap<String, String> heardersResponse;

        // XML : id = 1
        clientResponse = this
            .resource()
            .path(ConstantsPath.CREDENTIALS_TEMPLATE)
            .accept(MediaType.APPLICATION_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(SerializationHelper.getResourceAsString(XmlLocator.class, "CredentialsTemplate-1.xml"),
                MediaType.APPLICATION_XML).post(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);
        heardersResponse = clientResponse.getHeaders();

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(202, statusResponse);

        Assert.assertTrue(heardersResponse.containsKey(Constants.HEADER_CIMI_JOB_URI));
        Assert.assertTrue(heardersResponse.get(Constants.HEADER_CIMI_JOB_URI).get(0).endsWith("idValue_1"));

        Assert.assertTrue(heardersResponse.containsKey(Constants.HEADER_LOCATION));
        Assert.assertTrue(heardersResponse.get(Constants.HEADER_LOCATION).get(0)
            .endsWith(ConstantsPath.CREDENTIALS_TEMPLATE + "/" + "targetEntityValue_1"));

        XMLAssert.assertXMLEqual(SerializationHelper.getResourceAsReader(XmlLocator.class, "Job-1.xml"), new StringReader(
            entityResponse));
    }

    @Test
    public final void testDeleteCredentialsTemplate() {
        ClientResponse clientResponse = null;
        String entityResponse;
        int statusResponse;

        // JSON and XML : id = 0
        clientResponse = this.resource().path(ConstantsPath.CREDENTIALS_TEMPLATE + "/0")
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI).delete(ClientResponse.class);

        statusResponse = clientResponse.getStatus();
        entityResponse = clientResponse.getEntity(String.class);

        CredentialsTemplateResourceSerializationTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("STATUS: {}", statusResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("ENTITY:\n\t{}", entityResponse);
        CredentialsTemplateResourceSerializationTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        Assert.assertEquals(200, statusResponse);
    }

}
