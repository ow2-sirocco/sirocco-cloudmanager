package org.ow2.sirocco.cloudmanager.api.server;

import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.DatatypeConverter;

import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;

@ResourceInterceptorBinding
public class ResourceBase {
    @Context
    private HttpHeaders headers;

    public IdentityContext getIdentityContext() {
        IdentityContext result = new IdentityContext();
        List<String> values = this.headers.getRequestHeader("tenantId");
        if (values != null && !values.isEmpty()) {
            result.setTenantId(values.get(0));
        }
        values = this.headers.getRequestHeader("tenantName");
        if (values != null && !values.isEmpty()) {
            result.setTenantName(values.get(0));
        }
        values = this.headers.getRequestHeader("Authorization");
        if (values != null && !values.isEmpty()) {
            String userPassword[] = ResourceBase.decode(values.get(0));
            result.setUserName(userPassword[0]);
        }
        return result;
    }

    public static String[] decode(String auth) {
        auth = auth.replaceFirst("[B|b]asic ", "");
        byte[] decodedBytes = DatatypeConverter.parseBase64Binary(auth);
        if (decodedBytes == null || decodedBytes.length == 0) {
            return null;
        }
        return new String(decodedBytes).split(":", 2);
    }
}
