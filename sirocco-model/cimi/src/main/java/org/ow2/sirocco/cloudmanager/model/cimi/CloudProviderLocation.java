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
 *  $Id: CloudProviderLocation.java 897 2012-02-15 10:13:25Z ycas7461 $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"countryName", "stateName"}))
public class CloudProviderLocation implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer id;

    private String Iso3166Code;

    private String countryName;

    private String stateName;
    
    private List<CloudProvider> cloudProviders;

    @Column(name = "iso3166code", nullable = false, length = 6, unique = true)
    public String getIso3166Code() {
        return this.Iso3166Code;
    }

    public void setIso3166Code(final String iso3166Code) {
        this.Iso3166Code = iso3166Code;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public void setCountryName(final String countryName) {
        this.countryName = countryName;
    }

    public String getStateName() {
        return this.stateName;
    }

    public void setStateName(final String stateName) {
        this.stateName = stateName;
    }

    public CloudProviderLocation() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer Id) {
        this.id = Id;
    }

    @ManyToMany
	public List<CloudProvider> getCloudProviders() {
		return cloudProviders;
	}

	public void setCloudProviders(List<CloudProvider> cloudProviders) {
		this.cloudProviders = cloudProviders;
	}

}
