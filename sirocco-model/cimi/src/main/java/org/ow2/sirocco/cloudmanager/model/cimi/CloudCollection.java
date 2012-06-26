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
 *  $Id: CloudResource.java 1258 2012-05-21 12:35:04Z ycas7461 $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CloudCollection extends CloudEntity {
    private static final long serialVersionUID = 1L;
    private CloudResource resource;
    public static enum State {
        NOT_AVAILABLE, AVAILABLE, DELETED
    }

    private State state;

    @OneToOne(optional=false)
    @JoinColumn(name="cloudcoll_ent_id")
    public CloudResource getResource() {
        return resource;
    }

    public void setResource(
            CloudResource resource) {
        this.resource = resource;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
