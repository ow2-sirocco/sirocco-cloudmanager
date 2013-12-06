package org.ow2.sirocco.cloudmanager.core.api;

import java.util.List;

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

/**
 * Tenant management operations
 */
public interface ITenantManager {
    Tenant createTenant(Tenant tenant) throws CloudProviderException;

    Tenant getTenantById(int tenantId) throws CloudProviderException;

    Tenant getTenantByUuid(String tenantUuid) throws CloudProviderException;

    Tenant getTenantByName(String tenantName) throws CloudProviderException;

    Tenant getTenant(IdentityContext context) throws CloudProviderException;

    List<Tenant> getTenants() throws CloudProviderException;

    void deleteTenant(String tenantId) throws CloudProviderException;

    void addUserToTenant(String tenantId, String userId) throws CloudProviderException;

    void removeUserFromTenant(String tenantId, String userId) throws CloudProviderException;

    List<User> getTenantUsers(String tenantId) throws CloudProviderException;
}
