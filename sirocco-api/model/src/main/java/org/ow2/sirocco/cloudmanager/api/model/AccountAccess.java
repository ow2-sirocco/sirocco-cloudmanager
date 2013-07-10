package org.ow2.sirocco.cloudmanager.api.model;

import java.util.List;

public class AccountAccess {
    private String tenantId;

    private String providerName;

    private String providerApi;

    private String providerId;

    private String accountId;

    private List<Location> locations;

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

    public String getProviderName() {
        return this.providerName;
    }

    public void setProviderName(final String providerName) {
        this.providerName = providerName;
    }

    public String getProviderApi() {
        return this.providerApi;
    }

    public void setProviderApi(final String providerApi) {
        this.providerApi = providerApi;
    }

    public List<Location> getLocations() {
        return this.locations;
    }

    public void setLocations(final List<Location> locations) {
        this.locations = locations;
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
