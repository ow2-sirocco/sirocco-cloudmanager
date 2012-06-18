/**
 *
 * SIROCCO
 * Copyright (C) 2012 France Telecom
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

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class NetworkInterface implements Serializable, Identifiable {
    private static final long serialVersionUID = 1L;

    protected Integer id;

    public static enum InterfaceState {
        ACTIVE, STANDBY
    }

    @OneToMany
    private List<Address> addresses;

    @ManyToOne
    private Network network;

    @OneToOne
    private NetworkPort networkPort;

    private InterfaceState state;

    private Integer mtu;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @Enumerated(EnumType.STRING)
    public InterfaceState getState() {
        return this.state;
    }

    public void setState(final InterfaceState state) {
        this.state = state;
    }

    public Integer getMtu() {
        return this.mtu;
    }

    public void setMtu(final Integer mtu) {
        this.mtu = mtu;
    }

    public void setNetworkPort(final NetworkPort networkPort) {
        this.networkPort = networkPort;
    }

    public NetworkPort getNetworkPort() {
        return this.networkPort;
    }

    @OneToMany
    public List<Address> getAddresses() {
        return this.addresses;
    }

    public void setAddresses(final List<Address> addresses) {
        this.addresses = addresses;
    }

    @ManyToOne
    public Network getNetwork() {
        return this.network;
    }

    public void setNetwork(final Network network) {
        this.network = network;
    }

}