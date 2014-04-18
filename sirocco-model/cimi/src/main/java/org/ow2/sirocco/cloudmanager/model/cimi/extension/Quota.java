/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
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
 */
package org.ow2.sirocco.cloudmanager.model.cimi.extension;

import java.io.Serializable;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.ow2.sirocco.cloudmanager.model.utils.ResourceType;
import org.ow2.sirocco.cloudmanager.model.utils.Unit;

@Entity
public class Quota implements Serializable {
    @Embeddable
    public static class Resource implements Serializable {
        private ResourceType type;

        private long limit;

        private long used;

        private Unit unit;

        public Resource() {
        }

        public Resource(final ResourceType type, final Unit unit) {
            this.type = type;
            this.unit = unit;
        }

        @Enumerated(EnumType.STRING)
        public ResourceType getType() {
            return this.type;
        }

        public void setType(final ResourceType type) {
            this.type = type;
        }

        public long getLimit() {
            return this.limit;
        }

        public void setLimit(final long limit) {
            this.limit = limit;
        }

        public long getUsed() {
            return this.used;
        }

        public void setUsed(final long used) {
            this.used = used;
        }

        @Enumerated(EnumType.STRING)
        public Unit getUnit() {
            return this.unit;
        }

        public void setUnit(final Unit unit) {
            this.unit = unit;
        }

    }

    private Integer id;

    private List<Resource> resources;

    private CloudProviderAccount account;

    private CloudProviderLocation location;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    public List<Resource> getResources() {
        return this.resources;
    }

    public void setResources(final List<Resource> resources) {
        this.resources = resources;
    }

    @ManyToOne
    public CloudProviderAccount getAccount() {
        return this.account;
    }

    public void setAccount(final CloudProviderAccount account) {
        this.account = account;
    }

    @ManyToOne
    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }
}
