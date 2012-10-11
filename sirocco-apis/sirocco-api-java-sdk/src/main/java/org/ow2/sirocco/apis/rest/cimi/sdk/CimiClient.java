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

package org.ow2.sirocco.apis.rest.cimi.sdk;

import javax.ws.rs.core.MediaType;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiCloudEntryPoint;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.Base64;

public class CimiClient {
    public static final MediaType DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;

    private static final String CIMI_QUERY_EXPAND_KEYWORD = "$expand";

    private static final String CIMI_QUERY_FILTER_KEYWORD = "$filter";

    private static final String CIMI_QUERY_FIRST_KEYWORD = "$first";

    private static final String CIMI_QUERY_LAST_KEYWORD = "$last";

    public static class Options {
        private boolean debug;

        private MediaType mediaType;

        private Options() {

        }

        public static Options build() {
            return new Options();
        }

        public Options setDebug(final boolean debug) {
            this.debug = debug;
            return this;
        }

        public void setMediaType(final MediaType mediaType) {
            this.mediaType = mediaType;
        }

    }

    private WebResource webResource;

    private MediaType mediaType = CimiClient.DEFAULT_MEDIA_TYPE;

    private String cimiEndpointUrl;

    CimiCloudEntryPoint cloudEntryPoint;

    private String userName;

    private String password;

    private LoggingFilter loggingFilter = new LoggingFilter();

    private WebResource.Builder authentication(final WebResource webResource, final String userName, final String password) {
        WebResource.Builder builder = null;
        builder = webResource.header("Authorization", this.encodeBasicAuthentication(userName, password));
        return builder;
    }

    String extractPath(final String href) {
        return href.substring(this.cimiEndpointUrl.length());
    }

    String getMachinesPath() {
        return this.cloudEntryPoint.getMachines().getHref();
    }

    String getMachineImagesPath() {
        return this.cloudEntryPoint.getMachineImages().getHref();
    }

    String getMachineTemplatesPath() {
        return this.cloudEntryPoint.getMachineTemplates().getHref();
    }

    String getMachineConfigurationsPath() {
        return this.cloudEntryPoint.getMachineConfigs().getHref();
    }

    String getVolumesPath() {
        return this.cloudEntryPoint.getVolumes().getHref();
    }

    String getVolumeImagesPath() {
        return this.cloudEntryPoint.getVolumeImages().getHref();
    }

    String getVolumeTemplatesPath() {
        return this.cloudEntryPoint.getVolumeTemplates().getHref();
    }

    String getVolumeConfigurationsPath() {
        return this.cloudEntryPoint.getVolumeConfigs().getHref();
    }

    String getCredentialsPath() {
        return this.cloudEntryPoint.getCredentials().getHref();
    }

    String getCredentialTemplatesPath() {
        return this.cloudEntryPoint.getCredentialTemplates().getHref();
    }

    String getJobsPath() {
        return this.cloudEntryPoint.getJobs().getHref();
    }

    String getSystemsPath() {
        return this.cloudEntryPoint.getSystems().getHref();
    }

    String getSystemTemplatesPath() {
        return this.cloudEntryPoint.getSystemTemplates().getHref();
    }

    private String encodeBasicAuthentication(final String userName, final String password) {
        StringBuilder sbToEncode = new StringBuilder();
        sbToEncode.append(userName).append(':').append(password);
        StringBuilder sb = new StringBuilder();
        sb.append("Basic ").append(new String(Base64.encode(sbToEncode.toString())));
        return sb.toString();
    }

    private void handleResponseStatus(final ClientResponse response) throws CimiException {
        if (response.getStatus() == 400) {
            String message = response.getEntity(String.class);
            throw new CimiException(message);
        } else if (response.getStatus() == 401) {
            throw new CimiException("Unauthorized");
        } else if (response.getStatus() == 403) {
            String message = response.getEntity(String.class);
            throw new CimiException("Forbidden: " + message);
        } else if (response.getStatus() == 404) {
            String message = response.getEntity(String.class);
            throw new CimiException("Resource not found: " + message);
        } else if (response.getStatus() == 409) {
            String message = response.getEntity(String.class);
            throw new CimiException(message);
        } else if (response.getStatus() == 503) {
            String message = response.getEntity(String.class);
            throw new CimiException("Service unavailable: " + message);
        } else if (response.getStatus() == 500) {
            String message = response.getEntity(String.class);
            throw new CimiException("Internal error: " + message);
        } else if (response.getStatus() == 501) {
            String message = response.getEntity(String.class);
            throw new CimiException("Not implemented: " + message);
        }
    }

    private CimiClient(final String cimiEndpointUrl, final String userName, final String password, final Options... optionList)
        throws CimiException {
        this.cimiEndpointUrl = cimiEndpointUrl;
        this.userName = userName;
        this.password = password;
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(config);
        for (Options options : optionList) {
            if (options.debug) {
                client.addFilter(this.loggingFilter);
            }
            if (options.mediaType != null) {
                this.mediaType = options.mediaType;
            }
        }
        this.webResource = client.resource(cimiEndpointUrl);

        WebResource service = this.webResource.path(ConstantsPath.CLOUDENTRYPOINT_PATH);
        ClientResponse response = this.authentication(service, userName, password).accept(this.mediaType)
            .get(ClientResponse.class);
        this.handleResponseStatus(response);
        this.cloudEntryPoint = response.getEntity(CimiCloudEntryPoint.class);
    }

    public void setMediaType(final MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public static CimiClient login(final String cimiEndpointUrl, final String userName, final String password,
        final Options... options) throws CimiException {
        return new CimiClient(cimiEndpointUrl, userName, password, options);
    }

    <U> U getRequest(final String path, final Class<U> clazz, final int first, final int last, final String expand,
        final String... filterExpressions) throws CimiException {
        WebResource service = this.webResource.path(path);
        if (expand != null) {
            service = service.queryParam(CimiClient.CIMI_QUERY_EXPAND_KEYWORD, expand);
        }

        for (String filterExpression : filterExpressions) {
            if (filterExpression != null) {
                service = service.queryParam(CimiClient.CIMI_QUERY_FILTER_KEYWORD, filterExpression);
            }
        }
        if (first != -1) {
            service = service.queryParam(CimiClient.CIMI_QUERY_FIRST_KEYWORD, Integer.toString(first));
        }
        if (last != -1) {
            service = service.queryParam(CimiClient.CIMI_QUERY_LAST_KEYWORD, Integer.toString(last));
        }
        ClientResponse response = this.authentication(service, this.userName, this.password).accept(this.mediaType)
            .get(ClientResponse.class);
        this.handleResponseStatus(response);
        U cimiObject = response.getEntity(clazz);
        return cimiObject;
    }

    <U, V> V postRequest(final String path, final U input, final Class<V> outputClazz) throws CimiException {
        WebResource service = this.webResource.path(path);
        ClientResponse response = this.authentication(service, this.userName, this.password).accept(this.mediaType)
            .entity(input, this.mediaType).post(ClientResponse.class);
        this.handleResponseStatus(response);
        return response.getEntity(outputClazz);
    }

    <V> V deleteRequest(final String path, final Class<V> outputClazz) throws CimiException {
        WebResource service = this.webResource.path(path);
        ClientResponse response = this.authentication(service, this.userName, this.password).accept(this.mediaType)
            .delete(ClientResponse.class);
        this.handleResponseStatus(response);
        return response.getEntity(outputClazz);
    }

    void deleteRequest(final String path) throws CimiException {
        WebResource service = this.webResource.path(path);
        ClientResponse response = this.authentication(service, this.userName, this.password).accept(this.mediaType)
            .delete(ClientResponse.class);
        this.handleResponseStatus(response);
    }

    <U extends CimiObjectCommonAbstract> U getCimiObjectByReference(final String ref, final Class<U> clazz)
        throws CimiException {
        return this.getRequest(this.extractPath(ref), clazz, -1, -1, null);
    }

}
