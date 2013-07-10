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
package org.ow2.sirocco.cloudmanager.api.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonPropertyOrder({"iso3166_1", "iso3166_2", "countryName", "regionName", "cityName"})
@JsonSerialize(include = Inclusion.NON_NULL)
public class Location {
    private String iso3166_1;

    private String iso3166_2;

    private String countryName;

    private String regionName;

    private String cityName;

    public String getIso3166_1() {
        return this.iso3166_1;
    }

    public void setIso3166_1(final String iso3166_1) {
        this.iso3166_1 = iso3166_1;
    }

    public String getIso3166_2() {
        return this.iso3166_2;
    }

    public void setIso3166_2(final String iso3166_2) {
        this.iso3166_2 = iso3166_2;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public void setCountryName(final String countryName) {
        this.countryName = countryName;
    }

    public String getRegionName() {
        return this.regionName;
    }

    public void setRegionName(final String regionName) {
        this.regionName = regionName;
    }

    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(final String cityName) {
        this.cityName = cityName;
    }

    public static class Collection {
        private List<Location> locations;

        public List<Location> getLocations() {
            return this.locations;
        }

        public void setLocations(final List<Location> locations) {
            this.locations = locations;
        }
    }
}
