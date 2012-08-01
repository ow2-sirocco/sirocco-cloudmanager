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
 *  $Id: $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi.event;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudTemplate;

@Entity
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class EventLogTemplate extends CloudTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    private CloudResource targetResource;

    public static enum Persistence {
        YEARLY, MONTHLY, WEEKLY, DAILY, HOURLY
    }

    private Persistence persistence;

    @OneToOne
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public CloudResource getTargetResource() {
        return this.targetResource;
    }

    public void setTargetResource(final CloudResource targetResource) {
        this.targetResource = targetResource;
    }

    public Persistence getPersistence() {
        return this.persistence;
    }

    public void setPersistence(final Persistence persistence) {
        this.persistence = persistence;
    }

}
