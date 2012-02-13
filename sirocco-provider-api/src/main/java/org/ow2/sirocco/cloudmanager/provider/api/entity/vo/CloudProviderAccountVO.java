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

package org.ow2.sirocco.cloudmanager.provider.api.entity.vo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CloudProviderAccountVO implements Serializable {

    private String id;

    private String login;

    private String password;

    private String cloudProviderId;

    private String cloudProviderType;

    public CloudProviderAccountVO() {

    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getCloudProviderId() {
        return this.cloudProviderId;
    }

    public void setCloudProviderId(final String cloudProviderId) {
        this.cloudProviderId = cloudProviderId;
    }

    public void setCloudProviderType(final String cloudProviderType) {
        this.cloudProviderType = cloudProviderType;
    }

    public String getCloudProviderType() {
        return this.cloudProviderType;
    }

}
