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

package org.ow2.sirocco.cloudmanager.clustermanager.api.event;

import java.io.Serializable;

import org.ow2.sirocco.cloudmanager.clustermanager.api.Domain;

public class ResourceUsageEvent implements Serializable {
    private static final long serialVersionUID = -5008846798613491127L;

    private Domain domain;

    public ResourceUsageEvent(final Domain domain) {
        super();
        this.domain = domain;
    }

    public Domain getDomain() {
        return this.domain;
    }

    public void setDomain(final Domain domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "ResourceUsageEvent [domain=" + this.domain.getName() + "]";
    }

}
