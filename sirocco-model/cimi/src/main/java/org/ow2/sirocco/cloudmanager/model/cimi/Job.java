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
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Job extends CloudEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static enum Status {
        RUNNING, SUCCESS, FAILED, CANCELLED
    };
    
    private Status status;
    private Date timeOfStatusChange;
    private String targetEntity;
    private Integer returnCode;
    private String action;
    private String statusMessage;
    private Job parentJob;
    private List<Job> nestedJobs;
    private Boolean isCancellable;
    private Integer progress;

    public Job() {
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getTimeOfStatusChange() {
        return timeOfStatusChange;
    }

    public void setTimeOfStatusChange(Date timeOfStatusChange) {
        this.timeOfStatusChange = timeOfStatusChange;
    }

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @ManyToOne
    public Job getParentJob() {
        return parentJob;
    }

    public void setParentJob(Job parentJob) {
        this.parentJob = parentJob;
    }

    public Boolean getIsCancellable() {
        return isCancellable;
    }

    public void setIsCancellable(Boolean isCancellable) {
        this.isCancellable = isCancellable;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }


    @OneToMany(mappedBy="parentJob",fetch=FetchType.EAGER)
    public List<Job> getNestedJobs() {
        return nestedJobs;
    }


    public void setNestedJobs(List<Job> nestedJobs) {
        this.nestedJobs = nestedJobs;
    }

}



