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
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.ProjectVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.ResourceQuotaVO;

@Entity
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String projectId;

    private String name;

    private String description;

    private ResourceQuota resourceQuota;

    private Date createDate;

    private List<Machine> machines;

    private List<Volume> volumes;

    private List<MachineImage> images;

    private List<RoleAssignment> assignments;

    private User owner;

    private List<SystemTemplate> systemTemplates;

    private List<SystemInstance> systemInstances;

    private List<CloudProviderAccount> cloudProviderAccounts;

    public Project() {
        this.assignments = new ArrayList<RoleAssignment>();
        this.systemTemplates = new ArrayList<SystemTemplate>();
        this.systemInstances = new ArrayList<SystemInstance>();
        this.cloudProviderAccounts = new ArrayList<CloudProviderAccount>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    @OneToMany(mappedBy = "project")
    public Collection<RoleAssignment> getAssignments() {
        return this.assignments;
    }

    @OneToMany(mappedBy = "project")
    public Collection<MachineImage> getImages() {
        return this.images;
    }

    @OneToMany(mappedBy = "project")
    public Collection<Machine> getMachines() {
        return this.machines;
    }

    @OneToMany(mappedBy = "project")
    public Collection<Volume> getVolumes() {
        return this.volumes;
    }

    @OneToOne(cascade = CascadeType.ALL)
    public ResourceQuota getResourceQuota() {
        return this.resourceQuota;
    }

    @OneToOne
    public User getOwner() {
        return this.owner;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreateDate() {
        return this.createDate;
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setAssignments(final List<RoleAssignment> assignments) {
        this.assignments = assignments;
    }

    public void setImages(final List<MachineImage> images) {
        this.images = images;
    }

    public void setMachines(final List<Machine> machines) {
        this.machines = machines;
    }

    public void setVolumes(final List<Volume> volumes) {
        this.volumes = volumes;
    }

    public void setResourceQuota(final ResourceQuota resourceQuota) {
        this.resourceQuota = resourceQuota;
    }

    public void setOwner(final User owner) {
        this.owner = owner;
    }

    @Column(unique = true)
    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<SystemTemplate> getSystemTemplates() {
        return this.systemTemplates;
    }

    public void setSystemTemplates(final List<SystemTemplate> systemTemplates) {
        this.systemTemplates = systemTemplates;
    }

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<SystemInstance> getSystemInstances() {
        return this.systemInstances;
    }

    public void setSystemInstances(final List<SystemInstance> systemInstances) {
        this.systemInstances = systemInstances;
    }

    @ManyToMany
    @JoinTable(name = "PROJECT_CLOUDPROVIDERACCOUNT")
    /**
     * Project is the OWNER, we must use it (instead of CloudProviderAccount) in order to associate, persist, ...
     */
    public List<CloudProviderAccount> getCloudProviderAccounts() {
        return this.cloudProviderAccounts;
    }

    public void setCloudProviderAccounts(final List<CloudProviderAccount> cloudProviderAccounts) {
        this.cloudProviderAccounts = cloudProviderAccounts;
    }

    public ProjectVO toValueObject() {
        ProjectVO projectVo = new ProjectVO();
        projectVo.setProjectId(this.getProjectId());
        projectVo.setCreateDate(this.getCreateDate());
        projectVo.setDescription(this.getDescription());
        projectVo.setName(this.getName());
        ResourceQuotaVO rqVo = new ResourceQuotaVO();
        rqVo.setCpuQuota(this.getResourceQuota().getCpuQuota());
        rqVo.setDiskQuotaInMB(this.getResourceQuota().getDiskQuotaInMB());
        rqVo.setMaxNumberOfVMs(this.getResourceQuota().getVmQuota());
        rqVo.setRamQuotaInMB(this.getResourceQuota().getRamQuotaInMB());
        projectVo.setQuotaVo(rqVo);
        projectVo.setOwner(this.getOwner().getUsername());
        return projectVo;
    }

}
