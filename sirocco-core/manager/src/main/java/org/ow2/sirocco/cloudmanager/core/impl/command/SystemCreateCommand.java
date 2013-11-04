/**
 *
 * SIROCCO
 * Copyright (C) 2013 Orange
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
 */
package org.ow2.sirocco.cloudmanager.core.impl.command;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCreate;

public class SystemCreateCommand extends ResourceCommand {
    private static final long serialVersionUID = 1L;

    public static final String SYSTEM_CREATE = "systemCreate";

    private final SystemCreate systemCreate;

    private CloudProviderAccount account;

    private CloudProviderLocation location;

    public SystemCreateCommand(final SystemCreate systemCreate) {
        super(SystemCreateCommand.SYSTEM_CREATE);
        this.systemCreate = systemCreate;
    }

    public SystemCreate getSystemCreate() {
        return this.systemCreate;
    }

    public CloudProviderAccount getAccount() {
        return this.account;
    }

    public SystemCreateCommand setAccount(final CloudProviderAccount account) {
        this.account = account;
        return this;
    }

    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public SystemCreateCommand setLocation(final CloudProviderLocation location) {
        this.location = location;
        return this;
    }

}