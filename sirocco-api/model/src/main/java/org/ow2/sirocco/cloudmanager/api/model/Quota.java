/**
 *
 * SIROCCO
 * Copyright (C) 2014 Orange
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
 *
 */
package org.ow2.sirocco.cloudmanager.api.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include = Inclusion.NON_NULL)
public class Quota implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Resource implements Serializable {
        private static final long serialVersionUID = 1L;

        private String type;

        private long limit;

        private long used;

        private String unit;

        public String getType() {
            return this.type;
        }

        public void setType(final String type) {
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

        public String getUnit() {
            return this.unit;
        }

        public void setUnit(final String unit) {
            this.unit = unit;
        }

    }

    private List<Resource> resources;

    public List<Resource> getResources() {
        return this.resources;
    }

    public void setResources(final List<Resource> resources) {
        this.resources = resources;
    }

}
