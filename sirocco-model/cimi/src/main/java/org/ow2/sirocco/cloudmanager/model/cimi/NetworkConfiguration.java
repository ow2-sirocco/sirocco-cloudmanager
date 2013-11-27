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

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.Visibility;

/**
 * Network configuration
 */
@Entity
@NamedQueries({@NamedQuery(name = "NetworkConfiguration.findByUuid", query = "SELECT n from NetworkConfiguration n WHERE n.uuid=:uuid")})
public class NetworkConfiguration extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Network.Type networkType;

    private Integer mtu;

    private String classOfService;

    private Visibility visibility = Visibility.PRIVATE;

    private List<Subnet> subnets;

    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return this.visibility;
    }

    public void setVisibility(final Visibility visibility) {
        this.visibility = visibility;
    }

    @Enumerated(EnumType.STRING)
    public Network.Type getNetworkType() {
        return this.networkType;
    }

    public void setNetworkType(final Network.Type networkType) {
        this.networkType = networkType;
    }

    public Integer getMtu() {
        return this.mtu;
    }

    public void setMtu(final Integer mtu) {
        this.mtu = mtu;
    }

    public String getClassOfService() {
        return this.classOfService;
    }

    public void setClassOfService(final String classOfService) {
        this.classOfService = classOfService;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    public List<Subnet> getSubnets() {
        return this.subnets;
    }

    public void setSubnets(final List<Subnet> subnets) {
        this.subnets = subnets;
    }

}
