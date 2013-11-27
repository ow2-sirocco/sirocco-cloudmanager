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

package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnector;
import org.ow2.sirocco.cloudmanager.connector.api.ICloudProviderConnectorFinder;
import org.ow2.sirocco.cloudmanager.connector.api.ProviderTarget;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.OperationFailureException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.core.api.remote.IRemoteCloudProviderManager;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntityCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderProfile;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Remote(IRemoteCloudProviderManager.class)
@Local(ICloudProviderManager.class)
@SuppressWarnings("unused")
public class CloudProviderManager implements ICloudProviderManager {

    private static Logger logger = LoggerFactory.getLogger(CloudProviderManager.class.getName());

    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Resource
    private EJBContext ctx;

    @EJB
    private IUserManager userManager;

    @EJB
    private ITenantManager tenantManager;

    @EJB
    private IMachineManager machineManager;

    @EJB
    private IMachineImageManager machineImageManager;

    @EJB
    private INetworkManager networkManager;

    @EJB
    private ICloudProviderConnectorFinder connectorFinder;

    @Inject
    private IdentityContext identityContext;

    @Override
    public CloudProvider createCloudProvider(final String type, final String description) throws CloudProviderException {

        CloudProvider cp = new CloudProvider();
        cp.setCloudProviderType(type);
        cp.setDescription(description);

        return this.createCloudProvider(cp);
    }

    @Override
    public CloudProvider createCloudProvider(final CloudProvider cp) throws CloudProviderException {
        // if (!isCloudProviderValid(cp)){throw new
        // CloudProviderException("CloudProvider validation failed");};
        cp.setCreated(new Date());
        this.em.persist(cp);
        this.em.flush();
        return cp;
    }

    private String normalizeLabel(final String label) {
        return label.toUpperCase();
    }

    private boolean isCloudProviderValid(final CloudProvider cp) {
        return true;
    }

    @Override
    public CloudProvider getCloudProviderById(final int cloudProviderId) throws CloudProviderException {
        CloudProvider result = this.em.find(CloudProvider.class, cloudProviderId);
        return result;
    }

    @Override
    public CloudProvider getCloudProviderByUuid(final String uuid) throws CloudProviderException {
        try {
            return this.em.createNamedQuery("CloudProvider.findByUuid", CloudProvider.class).setParameter("uuid", uuid)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public List<CloudProvider> getCloudProviderByType(final String type) throws CloudProviderException {
        return this.em.createQuery("Select p From CloudProvider p where p.cloudProviderType=:type").setParameter("type", type)
            .getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CloudProvider> getCloudProviders() throws CloudProviderException {
        return this.em.createQuery("Select p From CloudProvider p").getResultList();
    }

    @Override
    public void deleteCloudProvider(final String cloudProviderId) throws CloudProviderException {
        CloudProvider result = this.em.find(CloudProvider.class, new Integer(cloudProviderId));
        this.em.remove(result);
    }

    private static CreateCloudProviderAccountOptions defaultCreateCloudProviderAccountOptions = new CreateCloudProviderAccountOptions();

    private ICloudProviderConnector getCloudProviderConnector(final CloudProviderAccount cloudProviderAccount)
        throws CloudProviderException {
        ICloudProviderConnector connector = this.connectorFinder.getCloudProviderConnector(cloudProviderAccount
            .getCloudProvider().getCloudProviderType());
        if (connector == null) {
            CloudProviderManager.logger.error("Cannot find connector for cloud provider type "
                + cloudProviderAccount.getCloudProvider().getCloudProviderType());
            return null;
        }
        return connector;
    }

    private Tenant getTenant() throws CloudProviderException {
        return this.tenantManager.getTenant(this.identityContext);
    }

    @Override
    public CloudProviderAccount createCloudProviderAccount(final CloudProvider provider, final CloudProviderLocation location,
        final CloudProviderAccount account, final CreateCloudProviderAccountOptions... options) throws CloudProviderException {
        CloudProvider newProvider = this.createCloudProvider(provider);
        CloudProviderManager.logger.info("New provider with uuid=", newProvider.getUuid());
        this.addLocationToCloudProvider(newProvider.getUuid(), location);
        return this.createCloudProviderAccount(newProvider.getUuid(), account, options);
    }

    @Override
    public CloudProviderAccount createCloudProviderAccount(final String providerId, final CloudProviderAccount account,
        final CreateCloudProviderAccountOptions... _options) throws CloudProviderException {
        CloudProvider provider = this.getCloudProviderByUuid(providerId);
        account.setCloudProvider(provider);
        account.setCreated(new Date());
        this.em.persist(account);
        provider.getCloudProviderAccounts().add(account);

        CreateCloudProviderAccountOptions options = _options.length > 0 ? _options[0]
            : CloudProviderManager.defaultCreateCloudProviderAccountOptions;

        ICloudProviderConnector connector = this.getCloudProviderConnector(account);
        if (account == null) {
            throw new OperationFailureException("Cannot find connector for provider type " + provider.getCloudProviderType());
        }
        if (options.isImportMachineConfigs()) {
            // XXX pick first location only, some providers might offer
            // different configs per location
            CloudProviderLocation location = provider.getCloudProviderLocations().iterator().next();
            try {
                List<MachineConfiguration> machineConfigs = connector.getComputeService().getMachineConfigs(
                    new ProviderTarget().account(account).location(location));
                for (MachineConfiguration config : machineConfigs) {
                    this.machineManager.createMachineConfiguration(config);
                }
            } catch (Exception e) {
                CloudProviderManager.logger.error("Import MachineConfigs failure", e);
                throw new OperationFailureException("Cannot import machine configs: " + e.getMessage());
            }
        }
        if (options.isImportMachineImages()) {
            for (CloudProviderLocation location : provider.getCloudProviderLocations()) {
                try {
                    List<MachineImage> images = connector.getImageService().getMachineImages(
                        options.isImportOnlyOwnerMachineImages(), null,
                        new ProviderTarget().account(account).location(location));
                    for (MachineImage image : images) {
                        image.setLocation(location);
                        this.machineImageManager.createMachineImage(image);
                    }
                } catch (Exception e) {
                    CloudProviderManager.logger.error("Import MachineImages failure", e);
                    throw new OperationFailureException("Cannot import machine images: " + e.getMessage());
                }
            }
        }
        if (options.isImportNetworks()) {
            for (CloudProviderLocation location : provider.getCloudProviderLocations()) {
                try {
                    List<Network> nets = connector.getNetworkService().getNetworks(
                        new ProviderTarget().account(account).location(location));
                    for (Network net : nets) {
                        net.setTenant(this.getTenant());
                        net.setCreated(new Date());
                        net.setCloudProviderAccount(account);
                        net.setLocation(location);
                        this.em.persist(net);
                    }
                } catch (Exception e) {
                    CloudProviderManager.logger.error("Import Networks failure", e);
                    throw new OperationFailureException("Cannot import networks: " + e.getMessage());
                }
            }
        }
        this.em.flush();
        return account;
    }

    private boolean isCloudProviderAccountValid(final CloudProviderAccount cpa) {

        if (cpa.getLogin() == null) {
            return false;
        }
        if (cpa.getLogin().equals("")) {
            return false;
        }

        if (cpa.getPassword() == null) {
            return false;
        }
        if (cpa.getPassword().equals("")) {
            return false;
        }

        if (cpa.getCloudProvider() == null) {
            return false;
        }

        return true;
    }

    @Override
    public CloudProviderAccount getCloudProviderAccountById(final int cloudProviderAccountId) throws CloudProviderException {
        CloudProviderAccount result = this.em.find(CloudProviderAccount.class, new Integer(cloudProviderAccountId));
        if (result != null) {
            result.getTenants().size();
        }
        return result;
    }

    @Override
    public CloudProviderAccount getCloudProviderAccountByUuid(final String uuid) throws CloudProviderException {
        try {
            return this.em.createNamedQuery("CloudProviderAccount.findByUuid", CloudProviderAccount.class)
                .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * add a provider to user by providing an user id
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager#addCloudProviderAccountToUser(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void addCloudProviderAccountToTenant(final String tenantId, final String cloudProviderAccountId)
        throws CloudProviderException {

        CloudProviderAccount account = this.getCloudProviderAccountByUuid(cloudProviderAccountId);
        Tenant tenant = this.tenantManager.getTenantByUuid(tenantId);
        account.getTenants().add(tenant);
        tenant.getCloudProviderAccounts().add(account);

        this.em.merge(account);

        CloudProviderManager.logger.info("Added CloudProvider account " + account.getCloudProvider().getCloudProviderType()
            + " to tenant " + tenant.getName());

    }

    /**
     * add a provider to user by providing an user id
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager#addCloudProviderAccountToUser(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void removeCloudProviderAccountFromTenant(final String tenantId, final String cloudProviderAccountId)
        throws CloudProviderException {

        CloudProviderAccount account = this.getCloudProviderAccountByUuid(cloudProviderAccountId);
        Tenant tenant = this.tenantManager.getTenantByUuid(tenantId);
        account.getTenants().remove(account);
        this.em.merge(account);
    }

    @Override
    public void deleteCloudProviderAccount(final String cloudProviderAccountId) throws CloudProviderException {
        CloudProviderAccount account = this.em.find(CloudProviderAccount.class, new Integer(cloudProviderAccountId));
        if (account == null) {
            throw new ResourceNotFoundException();
        }
        account.getCloudProvider().getCloudProviderAccounts().remove(account);
        this.em.remove(account);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CloudProviderAccount> getCloudProviderAccountsByProvider(final String providerId) throws CloudProviderException {
        return this.em.createQuery("Select p From CloudProviderAccount p where p.cloudProvider.id=:providerId")
            .setParameter("providerId", new Integer(providerId)).getResultList();
    }

    @Override
    public List<CloudProviderAccount> getCloudProviderAccountsByTenant(final String tenantId) throws CloudProviderException {
        Tenant tenant = this.tenantManager.getTenantByUuid(tenantId);
        return new ArrayList<CloudProviderAccount>(tenant.getCloudProviderAccounts());
    }

    @Override
    public CloudProviderLocation createCloudProviderLocation(final String Iso3166_1_Code, final String Iso3166_2_Code,
        final String postalCode, final Double altitude, final Double latitude, final Double longitude,
        final String countryName, final String stateName, final String cityName) throws CloudProviderException {

        CloudProviderLocation cpl = new CloudProviderLocation();

        cpl.setIso3166_1(Iso3166_1_Code);
        cpl.setIso3166_2(Iso3166_2_Code);
        cpl.setCountryName(countryName);
        cpl.setStateName(stateName);
        cpl.setCityName(cityName);

        return this.createCloudProviderLocation(cpl);
    }

    private CloudProviderLocation normalizeCloudProviderLocation(final CloudProviderLocation cpl) throws CloudProviderException {
        cpl.setCityName(this.normalizeLabel(cpl.getCityName()));
        cpl.setCountryName(this.normalizeLabel(cpl.getCountryName()));
        cpl.setStateName(this.normalizeLabel(cpl.getStateName()));
        cpl.setIso3166_1(this.normalizeLabel(cpl.getIso3166_1()));
        cpl.setIso3166_2(this.normalizeLabel(cpl.getIso3166_2()));
        return cpl;
    }

    @Override
    public CloudProviderLocation createCloudProviderLocation(final CloudProviderLocation cpl) throws CloudProviderException {
        // this.normalizeCloudProviderLocation(cpl);

        // if (!isCloudProviderLocationValid(cpl)){throw new
        // CloudProviderException("CloudProviderLocation validation failed");}

        this.em.persist(cpl);

        return cpl;
    }

    private boolean isCloudProviderLocationValid(final CloudProviderLocation cpl) {
        if (cpl.getIso3166_1() == null) {
            return false;
        }
        if (cpl.getIso3166_1().equals("")) {
            return false;
        }

        if (cpl.getIso3166_2() == null) {
            return false;
        }
        if (cpl.getIso3166_2().equals("")) {
            return false;
        }

        return true;
    }

    @Override
    public CloudProviderLocation getCloudProviderLocationById(final int cloudProviderLocationId) throws CloudProviderException {
        CloudProviderLocation result = this.em.find(CloudProviderLocation.class, cloudProviderLocationId);
        return result;
    }

    @Override
    public CloudProviderLocation getCloudProviderLocationByUuid(final String uuid) throws CloudProviderException {
        try {
            return this.em.createNamedQuery("CloudProviderLocation.findByUuid", CloudProviderLocation.class)
                .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public void deleteCloudProviderLocation(final String cloudProviderLocationId) throws CloudProviderException {

        CloudProviderLocation result = this.em.find(CloudProviderLocation.class, new Integer(cloudProviderLocationId));
        this.em.remove(result);

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CloudProviderLocation> getCloudProviderLocations() throws CloudProviderException {
        return this.em.createQuery("Select p From CloudProviderLocation p").getResultList();
    }

    @Override
    public void addLocationToCloudProvider(final String cloudProviderId, final String locationId) throws CloudProviderException {
        CloudProvider provider = this.getCloudProviderByUuid(cloudProviderId);
        CloudProviderLocation location = this.getCloudProviderLocationByUuid(locationId);
        if (location == null) {
            throw new ResourceNotFoundException("Wrong location id: " + locationId);
        }
        provider.getCloudProviderLocations().add(location);
        location.getCloudProviders().add(provider);
    }

    @Override
    public void addLocationToCloudProvider(final String cloudProviderId, CloudProviderLocation location)
        throws CloudProviderException {
        CloudProvider provider = this.getCloudProviderByUuid(cloudProviderId);
        location = this.createCloudProviderLocation(location);
        provider.getCloudProviderLocations().add(location);
        location.setCloudProviders(new HashSet<CloudProvider>());
        location.getCloudProviders().add(provider);
    }

    @Override
    public Placement placeResource(final int tenantId, final CloudEntityCreate create) throws CloudProviderException {
        Tenant tenant = this.tenantManager.getTenantById(tenantId);

        CloudProviderAccount targetAccount = null;
        CloudProviderLocation targetLocation = null;

        String providerAccountId = create.getProviderAccountId();
        if (providerAccountId == null) {
            List<CloudProviderAccount> accounts = this.getCloudProviderAccountsByTenant(tenant.getUuid());
            if (accounts.isEmpty()) {
                throw new CloudProviderException("No provider account");
            }
            targetAccount = accounts.get(0);
        } else {
            targetAccount = this.getCloudProviderAccountByUuid(providerAccountId);
            if (targetAccount == null) {
                throw new CloudProviderException("Provider account with id=" + providerAccountId);
            }
            boolean accountAllowed = false;
            for (Tenant t : targetAccount.getTenants()) {
                if (t.getId() == tenant.getId()) {
                    accountAllowed = true;
                    break;
                }
            }
            if (!accountAllowed) {
                throw new CloudProviderException("Access not allowed to provider account " + providerAccountId);
            }
        }

        String location = create.getLocation();
        if (location == null) {
            targetLocation = targetAccount.getCloudProvider().getCloudProviderLocations().iterator().next();
        } else {
            for (CloudProviderLocation loc : targetAccount.getCloudProvider().getCloudProviderLocations()) {
                if (loc.getCountryName().equalsIgnoreCase(location)) {
                    targetLocation = loc;
                    break;
                }
            }
            if (targetLocation == null) {
                throw new CloudProviderException("Location " + location + " not supported by provider "
                    + targetAccount.getCloudProvider().getDescription());
            }
        }

        return new Placement(targetAccount, targetLocation);
    }

    @Override
    public CloudProviderProfile createCloudProviderProfile(final CloudProviderProfile providerProfile) {
        this.em.persist(providerProfile);
        return providerProfile;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CloudProviderProfile> getCloudProviderProfiles() {
        return this.em.createQuery("Select p From CloudProviderProfile p").getResultList();
    }

    public CloudProviderProfile getCloudProviderProfileByUuid(final String uuid) throws ResourceNotFoundException {
        try {
            return this.em.createNamedQuery("CloudProviderProfile.findByUuid", CloudProviderProfile.class)
                .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public void addCloudProviderProfileMetadata(final String profileUuid,
        final org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderProfile.AccountParameter metadata)
        throws ResourceNotFoundException {
        CloudProviderProfile profile = this.getCloudProviderProfileByUuid(profileUuid);
        profile.getAccountParameters().add(metadata);
    }

}
