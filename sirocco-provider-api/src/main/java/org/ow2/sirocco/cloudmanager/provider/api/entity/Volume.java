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

package org.ow2.sirocco.cloudmanager.provider.api.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VolumeVO;

@NamedQueries(value = {
    @NamedQuery(name = "FIND_VOLUMES_WITH_JOB", query = "SELECT v FROM Volume v WHERE v.activeJob IS NOT NULL"),
    @NamedQuery(name = "GET_VOLUME_BY_JOB_ID", query = "SELECT v FROM Volume v WHERE v.activeJob=:activeJob"),
    @NamedQuery(name = "FIND_VOLUMES_BY_PROVIDER_ID", query = "SELECT v FROM Volume v WHERE v.providerAssignedId=:providerAssignedId")})
@Entity
public class Volume extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    public static final String FIND_VOLUMES_WITH_JOB = "FIND_VOLUMES_WITH_JOB";

    public static final String GET_VOLUME_BY_JOB_ID = "GET_VOLUME_BY_JOB_ID";

    public static final String FIND_VOLUMES_BY_PROVIDER_ID = "FIND_VOLUMES_BY_PROVIDER_ID";

    private State state;

    private String providerAssignedId;

    private Long capacityInMB;

    private Boolean bootable;

    private Collection<Machine> machines;

    private Visibility visibility;

    private CloudProviderAccount cloudProviderAccount;

    private String activeJob;

    public Volume() {
        this.machines = new ArrayList<Machine>();
    }

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public Long getCapacityInMB() {
        return this.capacityInMB;
    }

    public void setCapacityInMB(final Long capacityInMB) {
        this.capacityInMB = capacityInMB;
    }

    public Boolean getBootable() {
        return this.bootable;
    }

    public void setBootable(final Boolean bootable) {
        this.bootable = bootable;
    }

    @ManyToMany(mappedBy = "volumes", fetch = FetchType.EAGER)
    public Collection<Machine> getMachines() {
        return this.machines;
    }

    public void setMachines(final Collection<Machine> machines) {
        this.machines = machines;
    }

    @Override
    @Column(unique = true)
    public String getProviderAssignedId() {
        return this.providerAssignedId;
    }

    @Override
    public void setProviderAssignedId(final String providerAssignedId) {
        this.providerAssignedId = providerAssignedId;
    }

    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return this.visibility;
    }

    public void setVisibility(final Visibility visibility) {
        this.visibility = visibility;
    }

    @ManyToOne
    @JoinColumn(name = "CloudProviderAccount_id")
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }

    public String getActiveJob() {
        return this.activeJob;
    }

    public void setActiveJob(final String activeJob) {
        this.activeJob = activeJob;
    }

    public VolumeVO toValueObject() {
        VolumeVO vol = new VolumeVO();
        vol.setVolumeId(this.getId().toString());
        if (this.getUser() != null) {
            vol.setUserName(this.getUser().getUsername());
        } else {
            vol.setUserName(null);
        }
        if (this.getProject() != null) {
            vol.setProjectId((this.getProject().getProjectId()));
        } else {
            vol.setProjectId(null);
        }
        vol.setBootable(this.getBootable());
        vol.setCapacityInMB(this.getCapacityInMB());
        vol.setDescription(this.getDescription());
        vol.setName(this.getName());
        vol.setStatus(this.getState().toString());
        vol.setVisibility(this.getVisibility().toString());
        List<String> attachedVMs = new ArrayList<String>();
        for (Machine vm : this.getMachines()) {
            attachedVMs.add(vm.getId().toString());
        }
        vol.setAttachedVirtualMachineIds(attachedVMs);
        vol.setCreationDate(this.getCreated());
        if (this.getCloudProviderAccount() == null) {
            vol.setCloudProviderAccountId(null);
        } else {
            vol.setCloudProviderAccountId(this.getCloudProviderAccount().getId().toString());
        }

        if (this.getCloudProviders() == null) {
            vol.setCloudProviderId(null);
        } else {
            vol.setCloudProviderId(((CloudEntity) this.getCloudProviders().toArray()[0]).getId().toString());
        }
        vol.setLocation(this.location);
        return vol;
    }

}
