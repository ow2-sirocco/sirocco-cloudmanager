/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
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
 *
 */

package org.ow2.sirocco.cloudmanager.api.tools;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.core.util.Base64;

public class RestClient {

    /** default media type. */
    public static final MediaType DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;

    public static class Options {
        private boolean debug;

        private String httpProxyHost;

        private String httpProxyPort;

        private Options() {

        }

        /**
         * Returns a new set of default options.
         * 
         * @return the options
         */
        public static Options build() {
            return new Options();
        }

        /**
         * Turns on or off logging of HTTP messages on standard output.
         * 
         * @param debug true if logging is desired
         * @return Options object
         */
        public Options setDebug(final boolean debug) {
            this.debug = debug;
            return this;
        }

        public Options setHttpProxyHost(final String httpProxyHost) {
            this.httpProxyHost = httpProxyHost;
            return this;
        }

        public Options setHttpProxyPort(final String httpProxyPort) {
            this.httpProxyPort = httpProxyPort;
            return this;
        }

    }

    private WebResource webResource;

    private MediaType mediaType = RestClient.DEFAULT_MEDIA_TYPE;

    private LoggingFilter loggingFilter = new LoggingFilter();

    private Map<String, String> authenticationHeaders;

    private void handleResponseStatus(final ClientResponse response) throws Exception {
        if (response.getStatus() == 400) {
            String message = response.getEntity(String.class);
            throw new ProviderException(message);
        } else if (response.getStatus() == 401) {
            throw new ProviderException("Unauthorized");
        } else if (response.getStatus() == 403) {
            String message = response.getEntity(String.class);
            throw new ProviderException("Forbidden");
        } else if (response.getStatus() == 404) {
            String message = response.getEntity(String.class);
            throw new ProviderException("Resource not found: " + message);
        } else if (response.getStatus() == 409) {
            String message = response.getEntity(String.class);
            throw new ProviderException(message);
        } else if (response.getStatus() == 503) {
            String message = response.getEntity(String.class);
            throw new ProviderException("Service unavailable");
        } else if (response.getStatus() == 500) {
            String message = response.getEntity(String.class);
            throw new ProviderException("Internal error: " + message);
        } else if (response.getStatus() == 501) {
            String message = response.getEntity(String.class);
            throw new ProviderException("Not implemented");
        } else if (response.getStatus() == 502) {
            String message = response.getEntity(String.class);
            throw new ProviderException("Bad gateway: " + message);
        }
    }

    private void initAuthenticationHeaders(final String userName, final String password, final String tenantId,
        final String tenantName) throws Exception {
        StringBuilder sbToEncode = new StringBuilder();
        sbToEncode.append(userName).append(':').append(password);
        StringBuilder sb = new StringBuilder();
        sb.append("Basic ").append(new String(Base64.encode(sbToEncode.toString())));
        this.authenticationHeaders = new HashMap<String, String>();
        this.authenticationHeaders.put("Authorization", sb.toString());
        if (tenantId != null) {
            this.authenticationHeaders.put("tenantId", tenantId);
        }
        if (tenantName != null) {
            this.authenticationHeaders.put("tenantName", tenantName);
        }
    }

    private WebResource.Builder addAuthenticationHeaders(final WebResource resource) {
        WebResource.Builder builder = resource.getRequestBuilder();
        for (Entry<String, String> header : this.authenticationHeaders.entrySet()) {
            builder = builder.header(header.getKey(), header.getValue());
        }
        return builder;
    }

    private Client createClient(final Options... optionList) {
        String proxyHost = null;
        String proxyPort = null;
        if (optionList.length > 0) {
            Options options = optionList[0];
            proxyHost = options.httpProxyHost;
            proxyPort = options.httpProxyPort;
        }

        if (proxyHost == null) {
            proxyHost = java.lang.System.getProperty("http.proxyHost");
        }
        if (proxyPort == null) {
            proxyPort = java.lang.System.getProperty("http.proxyPort");
        }

        if (proxyHost != null && proxyPort == null) {
            final DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
            if (proxyHost != null && proxyPort != null) {
                config.getProperties().put(ApacheHttpClientConfig.PROPERTY_PROXY_URI, "http://" + proxyHost + ":" + proxyPort);
            }
            config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
            return ApacheHttpClient.create(config);
        } else {
            ClientConfig config = new DefaultClientConfig();
            config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
            return Client.create(config);
        }
    }

    private RestClient(final String endpointUrl, final String userName, final String password, final String tenantId,
        final String tenantName, final Options... optionList) throws Exception {
        this.initAuthenticationHeaders(userName, password, tenantId, tenantName);
        // ClientConfig config = new DefaultClientConfig();
        // config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
        // Boolean.TRUE);
        // Client client = Client.create(config);
        Client client = this.createClient(optionList);
        for (Options options : optionList) {
            if (options.debug) {
                client.addFilter(this.loggingFilter);
            }
        }
        try {
            this.webResource = client.resource(endpointUrl);
        } catch (ClientHandlerException e) {
            String message = (e.getCause() != null && !(e.getCause() instanceof UnknownHostException)) ? e.getCause()
                .getMessage() : e.getMessage();
            throw new Exception(message, e);
        }
    }

    /**
     * Changes the media type used for HTTP requests and responses.
     * 
     * @param mediaType either XML or JSON
     */
    public void setMediaType(final MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public static RestClient login(final String cimiEndpointUrl, final String userName, final String password,
        final String tenantId, final String tenantName, final Options... options) throws Exception {
        return new RestClient(cimiEndpointUrl, userName, password, tenantId, tenantName, options);
    }

    <U> U getRequest(final String path, final Class<U> clazz, final Map<String, String> queryParams) throws Exception {
        WebResource service = this.webResource.path(path);
        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            service = service.queryParam(param.getKey(), param.getValue());
        }
        try {
            ClientResponse response = this.addAuthenticationHeaders(service).accept(this.mediaType).get(ClientResponse.class);
            this.handleResponseStatus(response);
            U obj = response.getEntity(clazz);
            return obj;
        } catch (ClientHandlerException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    <U> U getRequest(final String path, final Class<U> clazz) throws Exception {
        WebResource service = this.webResource.path(path);
        try {
            ClientResponse response = this.addAuthenticationHeaders(service).accept(this.mediaType).get(ClientResponse.class);
            this.handleResponseStatus(response);
            U obj = response.getEntity(clazz);
            return obj;
        } catch (ClientHandlerException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    <U> void actionRequest(final String path, final U input) throws Exception {
        WebResource service = this.webResource.path(path);
        try {
            ClientResponse response = this.addAuthenticationHeaders(service).accept(this.mediaType)
                .entity(input, this.mediaType).post(ClientResponse.class);
            this.handleResponseStatus(response);
        } catch (ClientHandlerException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    <U, V> V postCreateRequest(final String path, final U input, final Class<V> outputClazz) throws Exception {
        WebResource service = this.webResource.path(path);
        try {
            ClientResponse response = this.addAuthenticationHeaders(service).accept(this.mediaType)
                .entity(input, this.mediaType).post(ClientResponse.class);
            this.handleResponseStatus(response);
            V createResult = null;
            if (response.getStatus() == 201) {
                V resource = null;
                if (response.getLength() > 0
                    || (response.getType() != null && (response.getType().equals(MediaType.APPLICATION_XML_TYPE) || response
                        .getType().equals(MediaType.APPLICATION_JSON_TYPE)))) {
                    resource = response.getEntity(outputClazz);
                }
                return resource;
            }
            return createResult;
        } catch (ClientHandlerException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    void putRequest(final String path, final Map<String, String> queryParams) throws Exception {
        WebResource service = this.webResource.path(path);
        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            service = service.queryParam("key", param.getKey()).queryParam("value", param.getValue());
        }
        try {
            ClientResponse response = this.addAuthenticationHeaders(service).accept(this.mediaType).put(ClientResponse.class);
            this.handleResponseStatus(response);
        } catch (ClientHandlerException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    void deleteRequest(final String path) throws Exception {
        WebResource service = this.webResource.path(path);
        try {
            ClientResponse response = this.addAuthenticationHeaders(service).accept(this.mediaType)
                .delete(ClientResponse.class);
            this.handleResponseStatus(response);
        } catch (ClientHandlerException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

}
