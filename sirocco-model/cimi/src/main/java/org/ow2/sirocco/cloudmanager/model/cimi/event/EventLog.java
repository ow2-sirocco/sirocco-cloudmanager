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
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;

@Entity
public class EventLog extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private CloudResource targetResource;

    private Set<Event> events;

    private EventLogTemplate.Persistence persistence;

    @Embedded
    private EventLogSummary summary;

    // TODO check
    @OneToOne
    public CloudResource getTargetResource() {
        return this.targetResource;
    }

    public void setTargetResource(final CloudResource targetResource) {
        this.targetResource = targetResource;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public Set<Event> getEvents() {
        return this.events;
    }

    public void setEvents(final Set<Event> events) {
        this.events = events;
    }

    public EventLogTemplate.Persistence getPersistence() {
        return this.persistence;
    }

    public void setPersistence(final EventLogTemplate.Persistence persistence) {
        this.persistence = persistence;
    }

    @Embedded
    public EventLogSummary getSummary() {
        return this.summary;
    }

    public void setSummary(final EventLogSummary summary) {
        this.summary = summary;
    }

}
