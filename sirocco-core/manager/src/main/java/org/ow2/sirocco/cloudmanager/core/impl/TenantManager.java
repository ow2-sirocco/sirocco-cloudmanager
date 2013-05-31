package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.core.api.IRemoteTenantManager;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Local(ITenantManager.class)
@Remote(IRemoteTenantManager.class)
public class TenantManager implements ITenantManager {
    private static Logger logger = LoggerFactory.getLogger(UserManager.class.getName());

    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @EJB
    private IUserManager userManager;

    @Override
    public Tenant createTenant(final Tenant tenant) throws CloudProviderException {
        this.em.persist(tenant);
        this.em.flush();
        return tenant;
    }

    @Override
    public Tenant getTenantById(final String tenantId) throws CloudProviderException {
        Tenant result = this.em.find(Tenant.class, new Integer(tenantId));
        if (result == null) {
            throw new ResourceNotFoundException();
        }
        return result;
    }

    @Override
    public Tenant getTenant(final IdentityContext context) throws CloudProviderException {
        if (context.getTenantId() != null) {
            return this.getTenantById(context.getTenantId());
        }
        User user = this.userManager.getUserByUsername(context.getUserName());
        if (!user.getTenants().isEmpty()) {
            return user.getTenants().iterator().next();
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public List<Tenant> getTenants() throws CloudProviderException {
        return this.em.createQuery("SELECT t FROM Tenant t").getResultList();
    }

    @Override
    public void deleteTenant(final String tenantId) throws CloudProviderException {
        Tenant result = this.getTenantById(tenantId);

        if (result != null) {
            this.em.remove(result);
        }
    }

    @Override
    public void addUserToTenant(final String tenantId, final String userId) throws CloudProviderException {
        Tenant tenant = this.getTenantById(tenantId);
        User user = this.userManager.getUserById(userId);
        if (!tenant.getUsers().add(user)) {
            throw new ResourceConflictException();
        }
        user.getTenants().add(tenant);
    }

    @Override
    public void removeUserFromTenant(final String tenantId, final String userId) throws CloudProviderException {
        Tenant tenant = this.getTenantById(tenantId);
        User user = this.userManager.getUserById(userId);
        if (!tenant.getUsers().remove(user)) {
            throw new ResourceConflictException();
        }
        user.getTenants().remove(tenant);
    }

    @Override
    public List<User> getTenantUsers(final String tenantId) throws CloudProviderException {
        Tenant tenant = this.getTenantById(tenantId);
        return tenant.getUsers();
    }

}
