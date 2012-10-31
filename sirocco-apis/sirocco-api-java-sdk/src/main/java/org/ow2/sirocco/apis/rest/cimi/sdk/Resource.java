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

package org.ow2.sirocco.apis.rest.cimi.sdk;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract;

public abstract class Resource<E extends CimiObjectCommonAbstract> {

    protected CimiClient cimiClient;

    protected E cimiObject;

    Resource(final CimiClient cimiClient, final E cimiObject) {
        this.cimiClient = cimiClient;
        this.cimiObject = cimiObject;
        // this.cimiObject.setProperties(new HashMap<String, String>()); // XXX
    }

    public String getName() {
        return this.cimiObject.getName();
    }

    public String getId() {
        if (this.cimiObject.getId() != null) {
            return this.cimiObject.getId();
        } else {
            return this.cimiObject.getHref();
        }
    }

    public String getDescription() {
        return this.cimiObject.getDescription();
    }

    public Date getCreated() {
        return this.cimiObject.getCreated();
    }

    public Map<String, String> getProperties() {
        return this.cimiObject.getProperties();
    }

    public Date getUpdated() {
        return this.cimiObject.getUpdated();
    }

    public void setName(final String name) {
        this.cimiObject.setName(name);
    }

    public void setDescription(final String description) {
        this.cimiObject.setDescription(description);
    }

    public void setProperties(final Map<String, String> properties) {
        this.cimiObject.setProperties(properties);
    }

    public void addProperty(final String key, final String value) {
        if (this.cimiObject.getProperties() == null) {
            this.cimiObject.setProperties(new HashMap<String, String>());
        }
        this.cimiObject.getProperties().put(key, value);
    }

    public E getResource() {
        return this.cimiObject;
    }

}
