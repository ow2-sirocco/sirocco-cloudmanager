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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;

/**
 * Represents a process performed by the provider
 */
@Entity
@NamedQueries({@NamedQuery(name = "Job.findByUuid", query = "SELECT j from Job j WHERE j.uuid=:uuid")})
public class Job extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum Status {
        RUNNING, SUCCESS, FAILED, CANCELLED
    };

    public static enum Action {
        ADD, DELETE, START, STOP, SUSPEND, PAUSE, RESTART, CAPTURE
    }

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

    public Job() {
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Tenant tenant;

        private CloudResource target;

        private String description;

        private Job.Status status;

        private String action;

        public Builder tenant(final Tenant tenant) {
            this.tenant = tenant;
            return this;
        }

        public Builder target(final CloudResource target) {
            this.target = target;
            return this;
        }

        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        public Builder status(final Job.Status status) {
            this.status = status;
            return this;
        }

        public Builder action(final Action action) {
            this.action = action.toString().toLowerCase();
            return this;
        }

        public Job build() {
            Job job = new Job();
            job.setTenant(this.tenant);
            job.setTargetResource(this.target);
            List<CloudResource> affectedResources = new ArrayList<CloudResource>();
            affectedResources.add(this.target);
            job.setAffectedResources(affectedResources);
            job.setCreated(new Date());
            job.setAction(this.action);
            job.setDescription(this.description);
            job.setState(this.status);
            if (this.status == Status.SUCCESS) {
                job.setTimeOfStatusChange(job.getCreated());
            }
            return job;
        }
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
