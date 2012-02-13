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
package org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api;

import java.io.Serializable;

import org.ow2.sirocco.cloudmanager.provider.api.entity.Job;

public class JobCompletionEvent implements Serializable {
    static final long serialVersionUID = 1;

    private final String jobId;

    private final String targetEntity;

    private final String action;

    private final Job.Status status;

    private final String statusMessage;

    public <T> JobCompletionEvent(final Job<T> job) {
        this.jobId = job.getId();
        this.targetEntity = job.getTargetEntity();
        this.action = job.getAction();
        this.status = job.getStatus();
        this.statusMessage = job.getStatusMessage();
    }

    public String getJobId() {
        return this.jobId;
    }

    public String getTargetEntity() {
        return this.targetEntity;
    }

    public String getAction() {
        return this.action;
    }

    public Job.Status getStatus() {
        return this.status;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    @Override
    public String toString() {
        return "JobCompletionEvent [jobId=" + this.jobId + ", targetEntity=" + this.targetEntity + ", action=" + this.action
            + ", status=" + this.status + ", statusMessage=" + this.statusMessage + "]";
    }

}
