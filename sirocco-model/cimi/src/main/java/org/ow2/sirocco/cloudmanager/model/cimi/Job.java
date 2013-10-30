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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;

/**
 * Represents a process performed by the provider
 */
@Entity
public class Job extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum Status {
        RUNNING, SUCCESS, FAILED, CANCELLED
    };

    private CloudProviderLocation location;

    private Status state;

    private Date timeOfStatusChange;

    private CloudResource targetResource;

    private Integer returnCode;

    private String action;

    private String statusMessage;

    private Job parentJob;

    private List<Job> nestedJobs;

    private Integer progress;

    private List<CloudResource> affectedResources;

    private Boolean isCancellable;

    /*
     * protected long versionNum;
     * @Version
     * @Column(name="OPTLOCK") protected long getVersionNum() { return
     * versionNum; } protected void setVersionNum(long versionNum) {
     * this.versionNum = versionNum; }
     */

    public Job(final CloudProviderLocation location, final Status status, final Date timeOfStatusChange,
        final CloudResource targetEntity, final Integer returnCode, final String action, final String statusMessage,
        final Job parentJob, final List<Job> nestedJobs, final Boolean isCancellable, final Integer progress,
        final List<CloudResource> affectedEntities) {

        System.out.println("using job full constructor");
        this.location = location;
        this.state = status;
        this.timeOfStatusChange = timeOfStatusChange;
        this.targetResource = targetEntity;
        this.returnCode = returnCode;
        this.action = action;
        this.statusMessage = statusMessage;
        this.parentJob = parentJob;
        this.nestedJobs = nestedJobs;
        this.progress = progress;
        this.affectedResources = affectedEntities;

    }

    public Job() {
    }

    @Enumerated(EnumType.STRING)
    public Status getState() {
        return this.state;
    }

    public void setState(final Status status) {
        this.state = status;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getTimeOfStatusChange() {
        return this.timeOfStatusChange;
    }

    public void setTimeOfStatusChange(final Date timeOfStatusChange) {
        this.timeOfStatusChange = timeOfStatusChange;
    }

    @ManyToOne
    public CloudResource getTargetResource() {
        return this.targetResource;
    }

    public void setTargetResource(final CloudResource targetEntity) {
        this.targetResource = targetEntity;
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
    public Job getParentJob() {
        return this.parentJob;
    }

    public void setParentJob(final Job parentJob) {
        this.parentJob = parentJob;
    }

    public Integer getProgress() {
        return this.progress;
    }

    public void setProgress(final Integer progress) {
        this.progress = progress;
    }

    @OneToMany(mappedBy = "parentJob", fetch = FetchType.EAGER)
    public List<Job> getNestedJobs() {
        return this.nestedJobs;
    }

    public void setNestedJobs(final List<Job> nestedJobs) {
        this.nestedJobs = nestedJobs;
    }

    public void addNestedJob(final Job nestedJob) {
        if (this.nestedJobs == null) {
            this.nestedJobs = new ArrayList<Job>();
        }
        this.nestedJobs.add(nestedJob);
        nestedJob.setParentJob(this);
    }

    @ManyToOne
    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    public List<CloudResource> getAffectedResources() {
        return this.affectedResources;
    }

    public void setAffectedResources(final List<CloudResource> affectedEntities) {
        this.affectedResources = affectedEntities;
    }

    public Boolean getIsCancellable() {
        return this.isCancellable;
    }

    public void setIsCancellable(final Boolean isCancellable) {
        this.isCancellable = isCancellable;
    }

}
