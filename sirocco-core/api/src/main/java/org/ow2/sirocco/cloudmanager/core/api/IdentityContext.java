package org.ow2.sirocco.cloudmanager.core.api;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

@SessionScoped
public class IdentityContext implements Serializable {
    private static final long serialVersionUID = 1L;

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
