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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.api.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.ServerWebApplicationException;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.ow2.sirocco.cloudmanager.api.spec.ErrorReport;
import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public abstract class Client {

    private static Logger log = Logger.getLogger(Client.class.getName());

    public static class CommonClientOptions {
        @Parameter(names = {"-user", "-u"}, description = "user login")
        public String user;

        @Parameter(names = {"-password", "-p"}, description = "user password", password = true)
        public String password;

        @Parameter(names = "-server", description = "server hostname or IP address")
        public String server = "localhost";

        @Parameter(names = "-port", description = "server port")
        public Integer port = 9000;

        @Override
        public String toString() {
            return "CommonClientOptions [user=" + this.user + ", password=" + this.password + ", server=" + this.server
                + ", port=" + this.port + "]";
        }
    }

    private UserAPI proxy;

    protected String commandName;

    private CommonClientOptions commonOptions;

    protected Client() {
        this.commonOptions = new CommonClientOptions();
    }

    private String buildUrlBase() {
        return "http://" + this.commonOptions.server + ":" + this.commonOptions.port + "/SiroccoApi";
    }

    protected abstract Object getOptions();

    protected abstract void operation(UserAPI proxy) throws Exception;

    private void loadProperties() {
        Properties props = new Properties();

        try {
            props.load(new FileInputStream(new File(System.getProperty("default.properties"))));
        } catch (Exception e) {
            Client.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            return;
        }
        this.commonOptions.user = props.getProperty("user");
        this.commonOptions.password = props.getProperty("password");
        this.commonOptions.server = props.getProperty("server", "localhost");
        this.commonOptions.port = Integer.parseInt(props.getProperty("port", "9000"));
    }

    protected void run(final String[] args) {
        this.loadProperties();
        Object clientOptions = this.getOptions();
        JCommander argParser = null;
        try {
            if (clientOptions == null) {
                argParser = new JCommander(this.commonOptions);
            } else {
                argParser = new JCommander(new Object[] {this.commonOptions, clientOptions});
            }
            argParser.parse(args);
        } catch (ParameterException ex) {
            StringBuilder sb = new StringBuilder();
            argParser.usage(sb);
            System.out.println(sb.toString().replaceFirst("<main class>", this.commandName));
            System.exit(1);
        }

        this.proxy = JAXRSClientFactory.create(this.buildUrlBase(), UserAPI.class);

        // WebClient
        // .client(this.proxy)
        // .header("Authorization",
        // "Basic " + Base64Encoder.encodeString(this.commonOptions.user + ":" +
        // this.commonOptions.password))
        // .accept(javax.ws.rs.core.MediaType.APPLICATION_XML);

        org.apache.cxf.jaxrs.client.Client client = WebClient.client(this.proxy);

        client.header("Authorization",
            "Basic " + Base64Encoder.encodeString(this.commonOptions.user + ":" + this.commonOptions.password)).accept(
            javax.ws.rs.core.MediaType.APPLICATION_XML);

        HTTPConduit conduit = WebClient.getConfig(client).getHttpConduit();
        // XXX HTTP REST time-out timeout time out (en milli-secondes).
        conduit.getClient().setReceiveTimeout(180000);

        try {
            this.operation(this.proxy);
        } catch (ServerWebApplicationException ex) {
            String message = ex.getMessage();
            Response r = ex.getResponse();
            int status = r.getStatus();
            if (status == Response.Status.UNAUTHORIZED.getStatusCode()) {
                System.out.println("Unauthorized");
                System.exit(1);
            } else if (status == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                System.out.println("Internal server error");
                System.out.println(message);
                System.exit(1);
            } else if (message.indexOf("<error>") != -1) {
                try {
                    JAXBContext jc = JAXBContext.newInstance(ErrorReport.class);
                    Unmarshaller u = jc.createUnmarshaller();
                    JAXBElement<ErrorReport> root = u.unmarshal(new StreamSource(new ByteArrayInputStream(message.getBytes())),
                        ErrorReport.class);
                    System.out.println("Error: " + root.getValue().getMessage());
                } catch (JAXBException e) {
                    Client.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
                }
            } else {
                System.out.println(message);
            }

        } catch (Fault e) {
            if (e.getCause() != null) {
                Client.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
                Client.log.severe(e.getCause().getMessage());
            } else {
                Client.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            }
        } catch (Exception e) {
            Client.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
        }

    }
}
