package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceConflictException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.remote.IRemoteTenantManager;
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

    @Inject
    private IdentityContext identityContext;

    @Override
    public Tenant createTenant(final Tenant tenant) throws CloudProviderException {
        try {
            this.getTenantByName(tenant.getName());
            throw new ResourceConflictException("Tenant " + tenant.getName() + " already exists");
        } catch (ResourceNotFoundException e) {
            this.em.persist(tenant);
            this.em.flush();
            return tenant;
        }
    }

    @Override
    public Tenant getTenantById(final int tenantId) throws CloudProviderException {
        Tenant result = this.em.find(Tenant.class, new Integer(tenantId));
        if (result == null) {
            throw new ResourceNotFoundException();
        }
        return result;
    }

    @Override
    public Tenant getTenantByUuid(final String tenantUuid) throws CloudProviderException {
        try {
            return this.em.createNamedQuery("Tenant.findByUuid", Tenant.class).setParameter("uuid", tenantUuid)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException("Tenant " + tenantUuid + " not found");
        }
    }

    @Override
    public Tenant getTenantByName(final String tenantName) throws CloudProviderException {
        try {
            return this.em.createNamedQuery("Tenant.findByName", Tenant.class).setParameter("name", tenantName)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException("Tenant " + tenantName + " not found");
        }
    }

    @Override
    public Tenant getTenant(final IdentityContext context) throws CloudProviderException {
        if (context.getTenantId() != null && !context.getTenantId().isEmpty()) {
            return this.getTenantByUuid(context.getTenantId());
        }
        if (context.getTenantName() != null && !context.getTenantName().isEmpty()) {
            return this.getTenantByName(context.getTenantName());
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
        User user = this.userManager.getUserByUsername(this.identityContext.getUserName());
        if (user.isAdmin()) {
            return this.em.createQuery("SELECT t FROM Tenant t", Tenant.class).getResultList();
        } else {
            return new ArrayList<Tenant>(user.getTenants());
        }
    }

    @Override
    public void deleteTenant(final String tenantUuid) throws CloudProviderException {
        Tenant result = this.getTenantByUuid(tenantUuid);
        this.em.remove(result);
    }

    @Override
    public void addUserToTenant(final String tenantUuid, final String userUuid) throws CloudProviderException {
        Tenant tenant = this.getTenantByUuid(tenantUuid);
        User user = this.userManager.getUserByUuid(userUuid);
        if (!tenant.getUsers().add(user)) {
            throw new ResourceConflictException();
        }
        user.getTenants().add(tenant);
    }

    @Override
    public void removeUserFromTenant(final String tenantUuid, final String userUuid) throws CloudProviderException {
        Tenant tenant = this.getTenantByUuid(tenantUuid);
        User user = this.userManager.getUserByUuid(userUuid);
        if (!tenant.getUsers().remove(user)) {
            throw new ResourceConflictException();
        }
        user.getTenants().remove(tenant);
    }

    @Override
    public List<User> getTenantUsers(final String tenantUuid) throws CloudProviderException {
        Tenant tenant = this.getTenantByUuid(tenantUuid);
        return tenant.getUsers();
    }

}
