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
 *
 */
package org.ow2.sirocco.cloudmanager.core.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to specify query parameters
 */
public class QueryParams {
    private final Integer first;

    private final Integer last;

    private final String marker;

    private final Integer limit;

    private final List<String> filters;

    private final List<String> attributes;

    private final String providerUuid;

    private final String locationUuid;

    /**
     * QueryParams builder class
     */
    public static class Builder {
        private Integer first;

        private Integer last;

        private String marker;

        private Integer limit;

        private List<String> filters = new ArrayList<>();

        private List<String> attributes = new ArrayList<>();

        private String providerUuid;

        private String locationUuid;

        /**
         * 
         */
        public Builder() {
        }

        /**
         * Index of the first item to return. Ignored if marker or limit is set.
         */
        public Builder first(final int val) {
            if (val >= 0) {
                this.first = val;
            }
            return this;
        }

        /**
         * Index of the last item to return. Ignored if marker or limit is set.
         */
        public Builder last(final int val) {
            if (val >= 0) {
                this.last = val;
            }
            return this;
        }

        /**
         * Sets the identifier of the marker. Indicates that the query should
         * return items with a creation date older that the marker (or with a
         * identifier lower that the marker if the marker item is not yet
         * created. If a marker is specified, the first and last parameters are
         * ignored.
         */
        public Builder marker(final String id) {
            this.marker = id;
            return this;
        }

        /**
         * Maximum number of items to returns. If a limit is specified, the
         * first and last parameters are ignored.
         */
        public Builder limit(final int val) {
            this.limit = val;
            return this;
        }

        /**
         * Provides filters compliant with the DMTF CIMI filter syntax. Provided
         * filters are ANDed together.
         */
        public Builder filters(final List<String> val) {
            this.filters = val;
            return this;
        }

        /**
         * Filters by provider
         * 
         * @param providerUuid uuid of provider
         */
        public Builder filterByProvider(final String providerUuid) {
            this.providerUuid = providerUuid;
            return this;
        }

        /**
         * Filters by location
         * 
         * @param locationUuid uuid of location
         */
        public Builder filterByLocation(final String locationUuid) {
            this.locationUuid = locationUuid;
            return this;
        }

        /**
         * Selects the item attributes to return. Other attributes will have a
         * null value. If the list is empty, all attributes are selected.
         */
        public Builder attributes(final List<String> val) {
            this.attributes = val;
            return this;
        }

        /**
         * Adds a filter compliant with the DMTF CIMI filter syntax.
         */
        public Builder filter(final String val) {
            this.filters.add(val);
            return this;
        }

        /**
         * Adds an attribute to return.
         */
        public Builder attribute(final String val) {
            this.attributes.add(val);
            return this;
        }

        /**
         * Builds a QueryParams instance
         */
        public QueryParams build() {
            return new QueryParams(this);
        }
    }

    private QueryParams(final Builder builder) {
        this.first = builder.first;
        this.last = builder.last;
        this.marker = builder.marker;
        this.limit = builder.limit;
        this.filters = builder.filters;
        this.attributes = builder.attributes;
        this.providerUuid = builder.providerUuid;
        this.locationUuid = builder.locationUuid;
    }

    /**
     * Index of the first item to return. Ignored if marker or limit is set.
     */
    public Integer getFirst() {
        return this.first;
    }

    /**
     * Index of the last item to return. Ignored if marker or limit is set.
     */
    public Integer getLast() {
        return this.last;
    }

    /**
     * Identifier of the marker if any. Indicates that the query should return
     * items with a creation date older that the one of marker (or with a
     * identifier lower that the marker's one if the marker item is not yet
     * created. If a marker is specified, the first and last parameters are
     * ignored.
     */
    public String getMarker() {
        return this.marker;
    }

    /**
     * Maximum number of items to returns. If a limit is specified, the first
     * and last parameters are ignored.
     */
    public Integer getLimit() {
        return this.limit;
    }

    /**
     * Filters compliant with the DMTF CIMI filter syntax.
     */
    public List<String> getFilters() {
        return this.filters;
    }

    /**
     * Cloud Provider uuid
     */
    public String getProviderUuid() {
        return this.providerUuid;
    }

    /**
     * Location uuid
     */
    public String getLocationUuid() {
        return this.locationUuid;
    }

    /**
     * Attributes selected
     */
    public List<String> getAttributes() {
        return this.attributes;
    }

}
