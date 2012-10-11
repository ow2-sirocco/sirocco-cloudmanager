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
 *  $Id: System.java 788 2012-04-17 11:49:55Z ycas7461 $
 *
 */
package org.ow2.sirocco.cloudmanager.model.cimi.system;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLogTemplate;

@Entity
//@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class SystemTemplate extends CloudTemplate {
    private static final long serialVersionUID = 1L;

    private Set<ComponentDescriptor> componentDescriptors;

    private EventLogTemplate eventLogTemplate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "system_temp_id")
    //@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public Set<ComponentDescriptor> getComponentDescriptors() {
        return this.componentDescriptors;
    }

    public void setComponentDescriptors(final Set<ComponentDescriptor> componentDescriptors) {
        this.componentDescriptors = componentDescriptors;
    }

    @OneToOne
    public EventLogTemplate getEventLogTemplate() {
        return this.eventLogTemplate;
    }

    public void setEventLogTemplate(final EventLogTemplate eventLogTemplate) {
        this.eventLogTemplate = eventLogTemplate;
    }

}
