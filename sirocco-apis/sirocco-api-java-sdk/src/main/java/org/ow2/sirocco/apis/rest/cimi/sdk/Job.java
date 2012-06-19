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

package org.ow2.sirocco.apis.rest.cimi.sdk;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJobCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.NestedJob;

public class Job extends Resource<CimiJob> {
    public static enum Status {
        RUNNING, SUCCESS, FAILED, CANCELLED
    };

    Job(final CimiClient cimiClient, final CimiJob cimiJob) {
        super(cimiClient, cimiJob);
    }

    public Status getStatus() {
        return Status.valueOf(this.cimiObject.getStatus());
    }

    public String getTargetResourceRef() {
        return this.cimiObject.getTargetResource();
    }

    public String[] getAffectedResourceRefs() {
        return this.cimiObject.getAffectedResources();
    }

    public String getAction() {
        return this.cimiObject.getAction();
    }

    public String getStatusMessage() {
        return this.cimiObject.getStatusMessage();
    }

    public Date getTimeOfStatusChange() {
        return this.cimiObject.getTimeOfStatusChange();
    }

    public Boolean getIsCancellable() {
        return this.cimiObject.getIsCancellable();
    }

    public NestedJob[] getNestedJobs() {
        return this.cimiObject.getNestedJobs();
    }

    public CimiJob getCimiJob() {
        return this.cimiObject;
    }

    public void waitForCompletion(final long time, final TimeUnit unit) throws CimiException, TimeoutException,
        InterruptedException {
        long endTime = System.nanoTime() + unit.toNanos(time);
        while (true) {
            if (this.getStatus() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            this.cimiObject = this.cimiClient.getCimiObjectByReference(this.getId(), CimiJob.class);
            if (System.nanoTime() > endTime) {
                throw new TimeoutException();
            }
        }
    }

    public static List<Job> getJobs(final CimiClient client) throws CimiException {
        CimiJobCollection jobCollection = client.getRequest(client.extractPath(client.cloudEntryPoint.getJobs().getHref()),
            CimiJobCollection.class);

        List<Job> result = new ArrayList<Job>();

        for (CimiJob cimiJob : jobCollection.getCollection().getArray()) {
            result.add(Job.getJobByReference(client, cimiJob.getHref()));
        }
        return result;
    }

    public static Job getJobByReference(final CimiClient client, final String ref) throws CimiException {
        return new Job(client, client.getCimiObjectByReference(ref, CimiJob.class));
    }

    public static Job getJobById(final CimiClient client, final String id) throws CimiException {
        String path = client.getJobsPath() + "/" + id;
        return new Job(client, client.getCimiObjectByReference(path, CimiJob.class));
    }

}
