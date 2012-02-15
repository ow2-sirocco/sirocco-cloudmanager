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

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.VMImageVO;

@NamedQueries(value = {@NamedQuery(name = "GET_IMAGE_BY_JOB_ID", query = "SELECT v FROM MachineImage v WHERE v.activeJob=:activeJob")})
@Entity
public class MachineImage extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String GET_IMAGE_BY_JOB_ID = "GET_IMAGE_BY_JOB_ID";

    public static enum State {
        CREATING, AVAILABLE, DELETING, DELETED, ERROR
    }

    private String providerAssignedId;

    private State state;

    private CloudProviderAccount cloudProviderAccount;

    private String activeJob;

    private String osType;

    private String hypervisor;

    private String architecture;

    private Integer diskSizeMB;

    private Integer imageSizeMB;

    private Visibility visibility;

    private String format;

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return this.visibility;
    }

    public void setVisibility(final Visibility visibility) {
        this.visibility = visibility;
    }

    public String getOsType() {
        return this.osType;
    }

    public void setOsType(final String osType) {
        this.osType = osType;
    }

    public String getHypervisor() {
        return this.hypervisor;
    }

    public void setHypervisor(final String hypervisor) {
        this.hypervisor = hypervisor;
    }

    @Override
    public String getProviderAssignedId() {
        return this.providerAssignedId;
    }

    @Override
    public void setProviderAssignedId(final String providerAssignedId) {
        this.providerAssignedId = providerAssignedId;
    }

    public String getArchitecture() {
        return this.architecture;
    }

    public void setArchitecture(final String architecture) {
        this.architecture = architecture;
    }

    public Integer getDiskSizeMB() {
        return this.diskSizeMB;
    }

    public void setDiskSizeMB(final Integer diskSizeMB) {
        this.diskSizeMB = diskSizeMB;
    }

    public Integer getImageSizeMB() {
        return this.imageSizeMB;
    }

    public void setImageSizeMB(final Integer imageSizeMB) {
        this.imageSizeMB = imageSizeMB;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(final String format) {
        this.format = format;
    }

    @ManyToOne
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

    /**
     * Builds a new VMImageVO out of its business object counterpart.
     * 
     * @return a new VM image value object
     */
    public VMImageVO toValueObject() {
        VMImageVO vmImageVO = new VMImageVO();
        vmImageVO.setName(this.getName());
        vmImageVO.setOsType(this.getOsType());
        vmImageVO.setDescription(this.getDescription());
        vmImageVO.setId(this.getId());
        vmImageVO.setHypervisor(this.getHypervisor());

        if (this.getUser() != null) {
            vmImageVO.setUsername(this.getUser().getUsername());
        } else {
            vmImageVO.setUsername(null);
        }

        if (this.getProject() != null) {
            vmImageVO.setProjectId((this.getProject().getProjectId()));
            vmImageVO.setProjectName(this.getProject().getName());
        } else {
            vmImageVO.setProjectId(null);
            vmImageVO.setProjectName(null);
        }

        vmImageVO.setStatus(this.getState());
        vmImageVO.setVisibility(this.getVisibility());
        vmImageVO.setArchitecture(this.getArchitecture());
        vmImageVO.setDiskSizeMB(this.getDiskSizeMB());
        vmImageVO.setImageSizeMB(this.getImageSizeMB());

        if (this.getCloudProviderAccount() == null) {
            vmImageVO.setCloudProviderAccountId(null);
        } else {
            vmImageVO.setCloudProviderAccountId(this.getCloudProviderAccount().getId().toString());
        }

        if (this.getCloudProviders() == null) {
            vmImageVO.setCloudProviderId(null);
        } else {
            vmImageVO.setCloudProviderId(((CloudProvider) this.getCloudProviders().toArray()[0]).getId().toString());
        }

        vmImageVO.setLocation(this.location);

        return vmImageVO;
    }

    @Override
    public String toString() {
        return "MachineImage [osType=" + this.osType + ", providerAssignedId=" + this.providerAssignedId + ", hypervisor="
            + this.hypervisor + ", architecture=" + this.architecture + ", diskSizeMB=" + this.diskSizeMB + ", imageSizeMB="
            + this.imageSizeMB + ", state=" + this.state + ", visibility=" + this.visibility + ", format=" + this.format
            + ", cloudProviderAccount=" + this.cloudProviderAccount + ", cloudProvider=" + this.cloudProviders + ", location="
            + this.location + ", activeJob=" + this.activeJob + "]";
    }

}
