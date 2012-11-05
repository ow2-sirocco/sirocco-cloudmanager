/**
 *
 * SIROCCO
 * Copyright (C) 2012 France Telecom
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
package org.ow2.sirocco.apis.rest.cimi.sdk.auth;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.ow2.sirocco.apis.rest.cimi.sdk.AuthPlugin;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;

public class OpenStackKeystoneAuthPlugin implements AuthPlugin {

    private static class AuthInfo {
        static class PasswordCredentials {
            String username;

            String password;

            public String getUsername() {
                return this.username;
            }

            public String getPassword() {
                return this.password;
            }

            public void setPassword(final String password) {
                this.password = password;
            }

            public void setUsername(final String username) {
                this.username = username;
            }
        }

        PasswordCredentials passwordCredentials;

        String tenantName;

        public PasswordCredentials getPasswordCredentials() {
            return this.passwordCredentials;
        }

        public void setPasswordCredentials(final PasswordCredentials passwordCredentials) {
            this.passwordCredentials = passwordCredentials;
        }

        public String getTenantName() {
            return this.tenantName;
        }

        public void setTenantName(final String tenantName) {
            this.tenantName = tenantName;
        }
    }

    private static class AuthMessage {
        AuthInfo auth;

        public AuthInfo getAuth() {
            return this.auth;
        }

        public void setAuth(final AuthInfo auth) {
            this.auth = auth;
        }
    }

    @Override
    public Map<String, String> authenticate(final String user, final String password) throws CimiException {
        String[] tenantAndUser = user.split(":");
        if (tenantAndUser.length != 2) {
            throw new CimiException("Invalid tenant:user value");
        }
        String openStackAuthUrl = System.getenv("OS_AUTH_URL");
        if (openStackAuthUrl == null) {
            throw new CimiException("OS_AUTH_URL environment variable muste be set");
        }

        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(config);
        client.addFilter(new LoggingFilter());
        WebResource webResource = client.resource(openStackAuthUrl).path("/tokens");

        AuthMessage authMessage = new AuthMessage();
        AuthInfo authInfo = new AuthInfo();
        authInfo.setTenantName(tenantAndUser[0]);
        AuthInfo.PasswordCredentials passwordCredentials = new AuthInfo.PasswordCredentials();
        passwordCredentials.setUsername(tenantAndUser[1]);
        passwordCredentials.setPassword(password);
        authInfo.setPasswordCredentials(passwordCredentials);
        authMessage.setAuth(authInfo);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
            .entity(authMessage, MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class);

        if (response.getStatus() == 401) {
            throw new CimiException("Unauthorized");
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> access;
        try {
            access = mapper.readValue(response.getEntityInputStream(), Map.class);
        } catch (Exception e) {
            throw new CimiException("Unable to parse Keystone response: " + e.getMessage());
        }
        String tokenId = (String) ((Map<String, Object>) ((Map<String, Object>) access.get("access")).get("token")).get("id");

        client.destroy();
        return Collections.singletonMap("X-Auth-Token", tokenId);
    }

    public static void main(final String args[]) throws Exception {
        OpenStackKeystoneAuthPlugin auth = new OpenStackKeystoneAuthPlugin();

        System.out.println(auth.authenticate("admin:admin", "admin"));
    }
}
