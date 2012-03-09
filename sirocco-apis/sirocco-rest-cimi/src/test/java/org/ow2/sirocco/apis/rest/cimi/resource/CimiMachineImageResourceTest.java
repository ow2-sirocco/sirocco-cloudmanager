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
        return new WebAppDescriptor.Builder("javax.ws.rs.Application", SiroccoRestCimiApplication.class.getName())
            .initParam(JSONConfiguration.FEATURE_POJO_MAPPING, "true").contextPath("sirocco-rest")
            .servletClass(SpringServlet.class).contextListenerClass(ContextLoaderListener.class)
            .contextParam("contextConfigLocation", "classpath:context/resourcesContext.xml").build();
    }

    @Test
    public final void testGetMachineImage() throws Exception {
        ClientResponse clientResponse = null;

        // JSON : Version invalide : ko
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON_TYPE).header(Constants.HEADER_CIMI_VERSION, "232")
            .get(ClientResponse.class);
        if (clientResponse.getStatus() == 400) {
            CimiMachineImageResourceTest.LOGGER.debug("JSON:\n\t{}", clientResponse.getEntity(String.class));
            CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());
        }

        // JSON : Ok
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI).get(ClientResponse.class);
        if (clientResponse.getStatus() == 200) {
            CimiMachineImageResourceTest.LOGGER.debug("JSON:\n\t{}", clientResponse.getEntity(String.class));
            CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());
        }

        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/foo")
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_JSON_TYPE).get(ClientResponse.class);
        if (clientResponse.getStatus() == 400) {
            System.out.println("=========================================");
            System.out.println("REPONSE: " + clientResponse);
        }

        // XML : sans version : ok
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML_TYPE).get(ClientResponse.class);
        if (clientResponse.getStatus() == 200) {
            CimiMachineImageResourceTest.LOGGER.debug("XML:\n\t{}", clientResponse.getEntity(String.class));
            CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());
        }

        // XML : Version invalide : ko
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE + "/834752")
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML_TYPE).header(Constants.HEADER_CIMI_VERSION, "1.6")
            .get(ClientResponse.class);
        if (clientResponse.getStatus() == 200) {
            CimiMachineImageResourceTest.LOGGER.debug("JSON:\n\t{}", clientResponse.getEntity(String.class));
            CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());
        }

        // clientResponse = resource().path(ConstantsPath.MACHINE_IMAGE +
        // "/foo")
        // .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGE_XML_TYPE).get(ClientResponse.class);
        // if (clientResponse.getStatus() == 400) {
        // System.out.println("=========================================");
        // System.out.println("REPONSE: " + clientResponse);
        // }

    }

    @Test
    public final void testGetMachineImageCollection() throws Exception {
        ClientResponse clientResponse = null;

        // JSON
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE)
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGECOLLECTION_JSON_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI).get(ClientResponse.class);
        if (clientResponse.getStatus() == 200) {
            CimiMachineImageResourceTest.LOGGER.debug("JSON:\n\t{}", clientResponse.getEntity(String.class));
            CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());
        }
        // XML
        clientResponse = this.resource().path(ConstantsPath.MACHINE_IMAGE)
            .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINEIMAGECOLLECTION_XML_TYPE)
            .header(Constants.HEADER_CIMI_VERSION, Constants.VERSION_DMTF_CIMI).get(ClientResponse.class);
        if (clientResponse.getStatus() == 200) {
            CimiMachineImageResourceTest.LOGGER.debug("JSON:\n\t{}", clientResponse.getEntity(String.class));
            CimiMachineImageResourceTest.LOGGER.debug("HEADER:\n\t{}", clientResponse.getHeaders());
        }
    }

    // @Test
    // public final void testPutMachine() {
    // String clientResponse = null;
    // MultivaluedMap<String, String> queryParam = new MultivaluedMapImpl();
    // queryParam.add("CIMISelect", "name");
    // queryParam.add("CIMISelect", "description");
    // String json = "{\"name\":\"Cool Demo 1\"}";
    // clientResponse =
    // resource().path("machines/834752").queryParams(queryParam)
    // .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINE_JSON)
    // .entity(json,
    // MediaTypeCimi.APPLICATION_CIMI_MACHINE_JSON).put(String.class);
    // System.out.println("=========================================");
    // System.out.println("REPONSE: " + clientResponse);
    // }
    //
    // @Test
    // public final void testPut2Machine() {
    // String clientResponse = null;
    // MultivaluedMap<String, String> queryParam = new MultivaluedMapImpl();
    // queryParam.add("CIMISelect", "name,description");
    // String json = "{\"name\":\"Cool Demo 1\"}";
    // clientResponse =
    // resource().path("machines/834752").queryParams(queryParam)
    // .accept(MediaTypeCimi.APPLICATION_CIMI_MACHINE_JSON)
    // .entity(json,
    // MediaTypeCimi.APPLICATION_CIMI_MACHINE_JSON).put(String.class);
    // System.out.println("=========================================");
    // System.out.println("REPONSE: " + clientResponse);
    // }
    //
    // @Test
    // public final void testPostStopMachine() {
    // String clientResponse = null;
    // String json = "{\"action\":\"http://www.dmtf.org/cimi/action/stop\"}";
    // clientResponse =
    // resource().path("machines/834752").accept(MediaTypeCimi.APPLICATION_CIMI_ACTION_JSON)
    // .entity(json,
    // MediaTypeCimi.APPLICATION_CIMI_ACTION_JSON).post(String.class);
    // System.out.println("=========================================");
    // System.out.println("REPONSE: " + clientResponse);
    // }
    //
    // @Test
    // public final void testDeleteMachine() {
    // String clientResponse = null;
    // clientResponse =
    // resource().path("machines/834752").accept(MediaTypeCimi.APPLICATION_CIMI_ACTION_JSON)
    // .delete(String.class);
    // System.out.println("=========================================");
    // System.out.println("REPONSE: " + clientResponse);
    // }
}
