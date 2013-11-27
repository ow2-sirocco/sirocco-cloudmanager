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

package org.ow2.sirocco.cloudmanager.core.api;

import java.io.Serializable;
import java.util.List;

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntityCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderProfile;

/**
 * Cloud provider manager
 */
public interface ICloudProviderManager {
    static final String EJB_JNDI_NAME = "java:global/sirocco/sirocco-core/CloudProviderManager!org.ow2.sirocco.cloudmanager.core.api.IRemoteCloudProviderManager";

    public static class Placement implements Serializable {
        private static final long serialVersionUID = 1L;

        final CloudProviderAccount account;

        final CloudProviderLocation location;

        public Placement(final CloudProviderAccount account, final CloudProviderLocation location) {
            super();
            this.account = account;
            this.location = location;
        }

        public CloudProviderAccount getAccount() {
            return this.account;
        }

        public CloudProviderLocation getLocation() {
            return this.location;
        }

    }

    public static class CreateCloudProviderAccountOptions {
        private boolean importMachineConfigs = true;

        private boolean importMachineImages = true;

        private boolean importOnlyOwnerMachineImages = false;

        private boolean importNetworks = true;

        public CreateCloudProviderAccountOptions importMachineConfigs(final boolean importMachineConfigs) {
            this.importMachineConfigs = importMachineConfigs;
            return this;
        }

        public CreateCloudProviderAccountOptions importMachineImages(final boolean importMachineImages) {
            this.importMachineImages = importMachineImages;
            return this;
        }

        public CreateCloudProviderAccountOptions importOnlyOwnerMachineImages(final boolean importOnlyOwnerMachineImages) {
            this.importOnlyOwnerMachineImages = importOnlyOwnerMachineImages;
            return this;
        }

        public CreateCloudProviderAccountOptions importNetworks(final boolean importNetworks) {
            this.importNetworks = importNetworks;
            return this;
        }

        public boolean isImportMachineConfigs() {
            return this.importMachineConfigs;
        }

        public boolean isImportMachineImages() {
            return this.importMachineImages;
        }

        public boolean isImportOnlyOwnerMachineImages() {
            return this.importOnlyOwnerMachineImages;
        }

        public boolean isImportNetworks() {
            return this.importNetworks;
        }

    }

    CloudProvider createCloudProvider(String type, String description) throws CloudProviderException;

    CloudProvider createCloudProvider(CloudProvider cp) throws CloudProviderException;

    CloudProvider getCloudProviderById(int cloudProviderId) throws CloudProviderException;

    CloudProvider getCloudProviderByUuid(String cloudProviderId) throws CloudProviderException;

    List<CloudProvider> getCloudProviderByType(String type) throws CloudProviderException;

    void deleteCloudProvider(String cloudProviderId) throws CloudProviderException;

    CloudProviderAccount createCloudProviderAccount(String providerId, CloudProviderAccount cpa,
        CreateCloudProviderAccountOptions... options) throws CloudProviderException;

    CloudProviderAccount createCloudProviderAccount(CloudProvider provider, CloudProviderLocation location,
        CloudProviderAccount account, CreateCloudProviderAccountOptions... options) throws CloudProviderException;

    CloudProviderAccount getCloudProviderAccountById(int cloudProviderAccountId) throws CloudProviderException;

    CloudProviderAccount getCloudProviderAccountByUuid(String cloudProviderAccountId) throws CloudProviderException;

    void deleteCloudProviderAccount(String cloudProviderAccountId) throws CloudProviderException;

    CloudProviderLocation createCloudProviderLocation(String Iso3166_1_Code, String Iso3166_2_Code, String postalCode,
        Double altitude, Double latitude, Double longitude, String countryName, String stateName, String cityName)
        throws CloudProviderException;

    CloudProviderLocation createCloudProviderLocation(CloudProviderLocation cpl) throws CloudProviderException;

    CloudProviderLocation getCloudProviderLocationById(int cloudProviderLocationId) throws CloudProviderException;

    CloudProviderLocation getCloudProviderLocationByUuid(String cloudProviderLocationUuid) throws CloudProviderException;

    void deleteCloudProviderLocation(String cloudProviderLocationId) throws CloudProviderException;

    void addLocationToCloudProvider(String cloudProviderId, String locationId) throws CloudProviderException;

    void addLocationToCloudProvider(String cloudProviderId, CloudProviderLocation location) throws CloudProviderException;

    void addCloudProviderAccountToTenant(String tenantId, String cloudProviderAccountId) throws CloudProviderException;

    void removeCloudProviderAccountFromTenant(String tenantId, String cloudProviderAccountId) throws CloudProviderException;

    List<CloudProviderAccount> getCloudProviderAccountsByProvider(String providerId) throws CloudProviderException;

    List<CloudProviderAccount> getCloudProviderAccountsByTenant(String tenantId) throws CloudProviderException;

    List<CloudProviderLocation> getCloudProviderLocations() throws CloudProviderException;

    List<CloudProvider> getCloudProviders() throws CloudProviderException;

    Placement placeResource(int tenantId, CloudEntityCreate create) throws CloudProviderException;

    CloudProviderProfile createCloudProviderProfile(CloudProviderProfile providerType);

    void addCloudProviderProfileMetadata(String profileId, CloudProviderProfile.AccountParameter metadata)
        throws ResourceNotFoundException;

    List<CloudProviderProfile> getCloudProviderProfiles();

}
