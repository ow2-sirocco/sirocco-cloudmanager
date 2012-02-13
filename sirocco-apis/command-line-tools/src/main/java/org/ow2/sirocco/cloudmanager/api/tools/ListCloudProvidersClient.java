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

package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.CloudProviderInfo;
import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

public class ListCloudProvidersClient extends Client {

    public ListCloudProvidersClient() {
        this.commandName = "sirocco-cloudprovider-list";
    }

    @Override
    protected Object getOptions() {
        return null;
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        System.out.format("%-13s %-22s %-30s\n", "Provider Id", "Name", "Description");
        for (CloudProviderInfo cloudProviderInfo : proxy.listCloudProviders().getCloudProviderInfos()) {
            System.out.format("%-13s %-22s %-30s\n", cloudProviderInfo.getId(), cloudProviderInfo.getName(),
                cloudProviderInfo.getDescription());
        }
    }

    public static void main(final String[] args) {
        new ListCloudProvidersClient().run(args);
    }
}
