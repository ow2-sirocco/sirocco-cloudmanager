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
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CollectionOfElements;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@Entity
public class Job implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum Status {
        RUNNING, SUCCESS, FAILED, CANCELLED
    };

    private Integer id;

    private User user;

    private String name;

    private String description;

    private Map<String, String> properties;

    private Date created;

    private Date deleted;

    private Date updated;

    private String providerAssignedId;

    private CloudProviderLocation location;

    private Status status;

    private Date timeOfStatusChange;

    private CloudEntity targetEntity;

    private Integer returnCode;

    private String action;

    private String statusMessage;

    private Job parentJob;

    private List<Job> nestedJobs;

    private Boolean isCancellable;

    private Integer progress;

    private List<CloudEntity> affectedEntities;

    public Job() {
    }

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
    public CloudEntity getTargetEntity() {
        return this.targetEntity;
    }

    public void setTargetEntity(final CloudEntity targetEntity) {
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
    public List<Job> getNestedJobs() {
        return this.nestedJobs;
    }

    public void setNestedJobs(final List<Job> nestedJobs) {
        this.nestedJobs = nestedJobs;
    }

    @ManyToOne
    public CloudProviderLocation getLocation() {
        return this.location;
    }

    public void setLocation(final CloudProviderLocation location) {
        this.location = location;
    }

    public String getProviderAssignedId() {
        return this.providerAssignedId;
    }

    public void setProviderAssignedId(final String providerAssignedId) {
        this.providerAssignedId = providerAssignedId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    @CollectionOfElements(fetch = FetchType.EAGER)
    public Map<String, String> getProperties() {
        return this.properties;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return this.created;
    }

    public String getName() {
        return this.name;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public void setDeleted(final Date deleted) {
        this.deleted = deleted;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getDeleted() {
        return this.deleted;
    }

    public void setUpdated(final Date updated) {
        this.updated = updated;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdated() {
        return this.updated;
    }

    @ManyToOne
    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    @OneToMany
    public List<CloudEntity> getAffectedEntities() {
        return this.affectedEntities;
    }

    public void setAffectedEntities(final List<CloudEntity> affectedEntities) {
        this.affectedEntities = affectedEntities;
    }

}
