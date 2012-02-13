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

package org.ow2.sirocco.cloudmanager.clustermanager.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Business object representing a pool of physical servers.
 */
public class ServerPool extends ResourceContainer implements Serializable {
    private static final long serialVersionUID = -1336165175351100553L;

    private String id;

    private String name;

    private List<Host> hosts;

    /**
     * Constructs a new ServerPool object
     */
    public ServerPool() {
        this.hosts = new ArrayList<Host>();
    }

    /**
     * Returns the unique identifier of the server pool
     * 
     * @return the unique identifier of the server pool
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the unique identifier of the server pool
     * 
     * @param id the unique identifier of the server pool
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Returns the symbolic name of server pool
     * 
     * @return the symbolic name of server pool
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the symbolic name of server pool
     * 
     * @param name the symbolic name of server pool
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the hosts belonging to the server pool
     * 
     * @return a list of Host objects
     */
    public List<Host> getHosts() {
        return this.hosts;
    }

    public void setHosts(final List<Host> hosts) {
        this.hosts = hosts;
    }

    /**
     * Adds a host to the server pool
     * 
     * @param host the Host object to add
     */
    public void addHost(final Host host) {
        this.hosts.add(host);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.dump(stringBuilder, "");
        return stringBuilder.toString();
    }

    void dump(final StringBuilder str, final String lineHeader) {
        str.append(lineHeader + "ServerPool(id=" + this.id + ",name=" + this.name + ")\n");
        str.append(lineHeader + "\t" + super.toString() + "\n");
        for (Host host : this.hosts) {
            host.dump(str, lineHeader + "\t");
        }
    }

}
