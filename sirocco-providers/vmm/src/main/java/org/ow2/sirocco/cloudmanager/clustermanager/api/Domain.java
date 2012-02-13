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
 * Business object representing a Domain. A Domain is the top-level entity of
 * the datacenter. it represents an administrative domain and acts as container
 * of pools of physical servers and recursively sub-domains.
 */
public class Domain extends ResourceContainer implements Serializable {
    private static final long serialVersionUID = 855699163671862748L;

    private String id;

    private String name;

    private List<Domain> subDomains;

    private List<ServerPool> serverPools;

    /**
     * Constructs a new Domain object
     */
    public Domain() {
        this.subDomains = new ArrayList<Domain>();
        this.serverPools = new ArrayList<ServerPool>();
    }

    /**
     * Returns the unique identifier of the domain
     * 
     * @return unique identifier of the domain
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the unique identifier of the domain
     * 
     * @param id unique identifier of the domain
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Returns the symbolic name of the domain. Note that two sub-domains within
     * the same domain are not allowed to have the same name.
     * 
     * @return the symbolic name of the domain
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the symbolic name of the domain
     * 
     * @param name the symbolic name of the domain
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the child domains (if any)
     * 
     * @return a (possibly empty) list of Domain objects
     */
    public List<Domain> getSubDomains() {
        return this.subDomains;
    }

    /**
     * Returns the server pools belonging to this domain
     * 
     * @return a (possibly empty) list of ServerPool objects
     */
    public List<ServerPool> getServerPools() {
        return this.serverPools;
    }

    public void setSubDomains(final List<Domain> subDomains) {
        this.subDomains = subDomains;
    }

    public void setServerPools(final List<ServerPool> serverPools) {
        this.serverPools = serverPools;
    }

    /**
     * Adds a sub-domain to this domain
     * 
     * @param subDomain the sub-domain to add
     */
    public void addSubDomain(final Domain subDomain) {
        this.subDomains.add(subDomain);
    }

    /**
     * Adds a server pool to this domain
     * 
     * @param serverPool a ServerPool object
     */
    public void addServerPool(final ServerPool serverPool) {
        this.serverPools.add(serverPool);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.dump(stringBuilder, "");
        return stringBuilder.toString();
    }

    private void dump(final StringBuilder str, final String lineHeader) {
        str.append(lineHeader + "Domain(id=" + this.id + ",name=" + this.name + ")\n");
        str.append(lineHeader + "\t" + super.toString() + "\n");
        for (Domain subDomain : this.subDomains) {
            subDomain.dump(str, lineHeader + "\t");
        }
        for (ServerPool pool : this.serverPools) {
            pool.dump(str, lineHeader + "\t");
        }
    }

}
