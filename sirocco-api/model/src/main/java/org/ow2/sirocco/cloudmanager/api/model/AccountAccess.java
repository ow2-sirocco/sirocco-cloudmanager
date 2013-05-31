package org.ow2.sirocco.cloudmanager.api.model;

import java.util.List;

public class AccountAccess {
    private String tenantId;

    private String providerId;

    private String accountId;

    public String getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    public String getProviderId() {
        return this.providerId;
    }

    public void setProviderId(final String providerId) {
        this.providerId = providerId;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public static class Collection {
        List<AccountAccess> accountAccesses;

        public List<AccountAccess> getAccountAccesses() {
            return this.accountAccesses;
        }

        public void setAccountAccesses(final List<AccountAccess> accountAccesses) {
            this.accountAccesses = accountAccesses;
        }
    }
}
