package org.ow2.sirocco.cloudmanager.core.api;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class IdentityContext {
    private String tenantId;

    private String userName;

    public String getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

}
