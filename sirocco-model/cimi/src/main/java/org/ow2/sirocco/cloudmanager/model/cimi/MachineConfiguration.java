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

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.IMultiCloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.ProviderMapping;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Visibility;

/**
 * Hardware configuration of a compute resource
 */
@Entity
@NamedQueries({@NamedQuery(name = "MachineConfiguration.findByUuid", query = "SELECT m from MachineConfiguration m WHERE m.uuid=:uuid")})
public class MachineConfiguration extends CloudEntity implements IMultiCloudResource, Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(1)
    private Integer cpu;

    @NotNull
    @Min(0)
    private Integer memory;

    @NotNull
    private List<DiskTemplate> disks;

    private Visibility visibility = Visibility.PRIVATE;

    private List<ProviderMapping> providerMappings;

    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return this.visibility;
    }

    public void setVisibility(final Visibility visibility) {
        this.visibility = visibility;
    }

    public Integer getCpu() {
        return this.cpu;
    }

    public void setCpu(final Integer cpu) {
        this.cpu = cpu;
    }

    public Integer getMemory() {
        return this.memory;
    }

    public void setMemory(final Integer memory) {
        this.memory = memory;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    public List<DiskTemplate> getDisks() {
        return this.disks;
    }

    public void setDisks(final List<DiskTemplate> diskTemplates) {
        this.disks = diskTemplates;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    public List<ProviderMapping> getProviderMappings() {
        return this.providerMappings;
    }

    public void setProviderMappings(final List<ProviderMapping> providerMappings) {
        this.providerMappings = providerMappings;
    }

}
