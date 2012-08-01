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
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

@Entity
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Job extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum Status {
        RUNNING, SUCCESS, FAILED, CANCELLED
    };

    private CloudProviderLocation location;

    private Status status;

    private Date timeOfStatusChange;

    private CloudResource targetEntity;

    private Integer returnCode;

    private String action;

    private String statusMessage;

    private Job parentJob;

    private List<Job> nestedJobs;

    private Boolean isCancellable;

    private Integer progress;

    private List<CloudResource> affectedEntities;

    /*
     * protected long versionNum;
     * @Version
     * @Column(name="OPTLOCK") protected long getVersionNum() { return
     * versionNum; } protected void setVersionNum(long versionNum) {
     * this.versionNum = versionNum; }
     */

    public Job() {
    }

    @Enumerated(EnumType.STRING)
    public Status getStatus() {
        return this.status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public Date getTimeOfStatusChange() {
        return this.timeOfStatusChange;
    }

    public void setTimeOfStatusChange(final Date timeOfStatusChange) {
        this.timeOfStatusChange = timeOfStatusChange;
    }

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public CloudResource getTargetEntity() {
        return this.targetEntity;
    }

    public void setTargetEntity(final CloudResource targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Integer getReturnCode() {
        return this.returnCode;
    }

    public void setReturnCode(final Integer returnCode) {
        this.returnCode = returnCode;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public void setStatusMessage(final String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public Job getParentJob() {
        return this.parentJob;
    }

    public void setParentJob(final Job parentJob) {
        this.parentJob = parentJob;
    }

    public Boolean getIsCancellable() {
        return this.isCancellable;
    }

    public void setIsCancellable(final Boolean isCancellable) {
        this.isCancellable = isCancellable;
    }

    public Integer getProgress() {
        return this.progress;
    }

    public void setProgress(final Integer progress) {
        this.progress = progress;
    }

    @OneToMany(mappedBy = "parentJob")
    @LazyCollection(LazyCollectionOption.FALSE)
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public List<Job> getNestedJobs() {
        return this.nestedJobs;
    }

    public void setNestedJobs(final List<Job> nestedJobs) {
        this.nestedJobs = nestedJobs;
    }

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public List<CloudResource> getAffectedEntities() {
        return this.affectedEntities;
    }

    public void setAffectedEntities(final List<CloudResource> affectedEntities) {
        this.affectedEntities = affectedEntities;
    }

}
