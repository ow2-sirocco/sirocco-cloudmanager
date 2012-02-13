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

import org.ow2.sirocco.cloudmanager.api.spec.CloudProviderAccountInfo;
import org.ow2.sirocco.cloudmanager.api.spec.CloudProviderAccountSpec;
import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

import com.beust.jcommander.Parameter;

public class CreateCloudProviderAccountClient extends Client {
    @Parameter(names = "-project", description = "project id", required = true)
    private String projectId;

    @Parameter(names = "-providerId", description = "provider id", required = true)
    private String providerId;

    @Parameter(names = "-login", description = "login", required = true)
    private String login;

    @Parameter(names = "-credential", description = "credential", required = true)
    private String credential;

    public CreateCloudProviderAccountClient() {
        this.commandName = "sirocco-cloudprovider-account-create";
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        CloudProviderAccountSpec spec = new CloudProviderAccountSpec();
        spec.setLogin(this.login);
        spec.setPassword(this.credential);
        spec.setProjectId(this.projectId);
        CloudProviderAccountInfo account = proxy.createCloudProviderAccount(this.providerId, spec);
        System.out.format("%-13s %-12s %-30s\n", "Account Id", "Name", "Credential");
        System.out.format("%-13s %-12s %-30s\n", account.getId(), account.getLogin(), account.getPassword());
    }

    public static void main(final String[] args) {
        new CreateCloudProviderAccountClient().run(args);
    }
}
