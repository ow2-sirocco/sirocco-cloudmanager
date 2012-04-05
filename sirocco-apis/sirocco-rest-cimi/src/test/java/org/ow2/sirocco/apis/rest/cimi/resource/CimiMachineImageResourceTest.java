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
package org.ow2.sirocco.apis.rest.cimi.resource;

import org.junit.Ignore;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.server.SiroccoRestCimiApplication;
import org.ow2.sirocco.apis.rest.cimi.utils.Constants;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;
import org.ow2.sirocco.apis.rest.cimi.utils.MediaTypeCimi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

public class CimiMachineImageResourceTest extends JerseyTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CimiMachineImageResourceTest.class);

    /**
     * {@inheritDoc}
     * 
     * @see com.sun.jersey.test.framework.JerseyTest#configure()
     */
    @Override
    protected AppDescriptor configure() {
        // return new WebAppDescriptor.Builder("javax.ws.rs.Application",
        // SiroccoRestCimiApplication.class.getName())
        // .initParam(JSONConfiguration.FEATURE_POJO_MAPPING,
        // "true").contextPath("sirocco-rest")
        // .servletClass(SpringServlet.class).contextListenerClass(ContextLoaderListener.class)
        // .contextParam("contextConfigLocation",
        // "classpath:context/resourcesContext.xml").build();
        return new WebAppDescriptor.Builder("javax.ws.rs.Application", SiroccoRestCimiApplication.class.getName())
            .initParam(JSONConfiguration.FEATURE_POJO_MAPPING, "true").contextPath("sirocco-rest")
            .servletClass(SpringServlet.class).contextListenerClass(ContextLoaderListener.class)
            .contextParam("contextConfigLocation", "classpath:context/serializationResourcesContext.xml").build();
    }

    @Test
    @Ignore
    public final void testGetMachineImage() throws Exception {
        ClientResponse clientResponse = null;

        // JSON : Ok
        CimiMachineImageResourceTest.LOGGER.debug("\n===== GET : {}", "JSON : ok");
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI).get(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        // JSON : Invalid Version
        CimiMachineImageResourceTest.LOGGER.debug("\n===== GET : {}", "JSON : Invalid Version");
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON_TYPE).header(Constants.HEADER_CIMI_VERSION, "232")
            .get(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        // JSON : ID not found
        CimiMachineImageResourceTest.LOGGER.debug("\n===== GET : {}", "JSON : ID not found");
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/foo")
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON_TYPE).get(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        // XML : no version ok
        CimiMachineImageResourceTest.LOGGER.debug("\n===== GET : {}", "XML : no version ok");
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML_TYPE).get(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        // XML : Invalid Version : ko
        CimiMachineImageResourceTest.LOGGER.debug("\n===== GET : {}", "XML : Invalid Version : ko");
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML_TYPE).header(Constants.HEADER_CIMI_VERSION, "1.6")
            .get(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

    }

    @Test
    @Ignore
    public final void testGetMachineImageCollection() throws Exception {
        ClientResponse clientResponse = null;

        // JSON
        CimiMachineImageResourceTest.LOGGER.debug("\n===== GET : {}", "JSON : ok");
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE)
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGECOLLECTION_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI).get(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        // XML
        CimiMachineImageResourceTest.LOGGER.debug("\n===== GET : {}", "XML : ok");
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE)
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGECOLLECTION_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI).get(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());
    }

    @Test
    @Ignore
    public final void testPutMachineImage() {
        ClientResponse clientResponse = null;
        String json;

        // JSON : ok
        CimiMachineImageResourceTest.LOGGER.debug("\n===== PUT : {}", "JSON : ok");

        json = "{\"name\":\"nameValue\",\"description\":\"descriptionValue\"}";
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(json, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON).put(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        // JSON : unknown
        CimiMachineImageResourceTest.LOGGER.debug("\n===== PUT : {}", "JSON : unknown");

        json = "{\"nameFALSE\":\"nameValue\",\"description\":\"descriptionValue\"}";
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(json, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON).put(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        // JSON : malformed
        CimiMachineImageResourceTest.LOGGER.debug("\n===== PUT : {}", "JSON : malformed");

        json = "{\"name\":\"nameValue\",\"description\",\"descriptionValue\"}";
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(json, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON).put(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        String xml;
        // XML : ok
        CimiMachineImageResourceTest.LOGGER.debug("\n===== PUT : {}", "XML : ok");

        xml = "<machineImage><name>nameValue</name><description>descriptionValue</description></machineImage>";
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(xml, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML).put(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        // XML : unknown
        CimiMachineImageResourceTest.LOGGER.debug("\n===== PUT : {}", "XML : unknown");

        xml = "<machineImage><nameFALSE>nameValue</nameFALSE><description>descriptionValue</description></machineImage>";
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(xml, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML).put(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

    }

    @Test
    @Ignore
    public final void testPutMachineImageCollection() {
        ClientResponse clientResponse = null;
        String json;

        // JSON : ok
        CimiMachineImageResourceTest.LOGGER.debug("\n===== PUT : {}", "JSON : ok");
        json = "{\"name\":\"nameValue\",\"description\":\"descriptionValue\"}";
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE).queryParam(Constants.PARAM_CIMI_SELECT, "name")
            .queryParam(Constants.PARAM_CIMI_SELECT, "description")
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(json, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON).put(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        String xml;
        // XML : ok
        CimiMachineImageResourceTest.LOGGER.debug("\n===== PUT : {}", "XML : ok");

        xml = "<machineImageCollection><name>nameValue</name><description>descriptionValue</description></machineImageCollection>";
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE).queryParam(Constants.PARAM_CIMI_SELECT, "name")
            .queryParam(Constants.PARAM_CIMI_SELECT, "description")
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(xml, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML).put(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());
    }

    @Test
    @Ignore
    public final void testPostMachineImage() {
        ClientResponse clientResponse = null;
        String json;

        // JSON : ok
        CimiMachineImageResourceTest.LOGGER.debug("\n===== POST : {}", "JSON : ok");
        json = "{\"name\":\"nameValue\",\"description\":\"descriptionValue\",\"imageLocation\":\"imageLocationValue\"}";
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE).accept(MediaTypeCimi.APPLICATION_CIMI_JOB_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(json, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGECREATE_JSON).post(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        String xml;
        // XML : ok
        CimiMachineImageResourceTest.LOGGER.debug("\n===== POST : {}", "XML : ok");

        xml = "<machineImage><name>nameValue</name><description>descriptionValue</description><imageLocation>imageLocationValue</imageLocation></machineImage>";
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE).accept(MediaTypeCimi.APPLICATION_CIMI_JOB_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI)
            .entity(xml, MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGECREATE_XML).post(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());
    }

    @Test
    @Ignore
    public final void testDeleteMachineImage() {
        ClientResponse clientResponse = null;

        // JSON
        CimiMachineImageResourceTest.LOGGER.debug("\n===== DELETE: {}", "JSON");

        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI).delete(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());

        // XML
        CimiMachineImageResourceTest.LOGGER.debug("\n===== DELETE: {}", "XML");

        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI).delete(ClientResponse.class);

        CimiMachineImageResourceTest.LOGGER.debug("COMPLETE:\n\t{}", clientResponse);
        CimiMachineImageResourceTest.LOGGER.debug("STATUS: {}", clientResponse.getStatus());
        CimiMachineImageResourceTest.LOGGER.debug("ENTITY:\n\t{}", clientResponse.getEntity(String.class));
        CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());
    }
}
