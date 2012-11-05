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
import org.ow2.sirocco.apis.rest.cimi.domain.NestedJob;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiJobCollectionRoot;

public class Job extends Resource<CimiJob> {
    public final int DEFAULT_POLL_PERIOD_IN_SECONDS = 10;

    public static enum Status {
        RUNNING, SUCCESS, FAILED, CANCELLED
    };

    Job(final CimiClient cimiClient, final CimiJob cimiJob) {
        super(cimiClient, cimiJob);
    }

    public Status getState() {
        return Status.valueOf(this.cimiObject.getStatus());
    }

    public String getTargetResourceRef() {
        return this.cimiObject.getTargetResource().getHref();
    }

    public String[] getAffectedResourceRefs() {
        String result[] = new String[this.cimiObject.getAffectedResources() != null ? this.cimiObject.getAffectedResources().length
            : 0];
        for (int i = 0; i < result.length; i++) {
            result[i] = this.cimiObject.getAffectedResources()[i].getHref();
        }
        return result;
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

    public void waitForCompletion(final long timeout, final long period, final TimeUnit unit) throws CimiException,
        TimeoutException, InterruptedException {
        long endTime = java.lang.System.nanoTime() + unit.toNanos(timeout);
        long periodInMilliseconds = TimeUnit.MILLISECONDS.convert(period, unit);
        while (true) {
            this.cimiObject = this.cimiClient.getCimiObjectByReference(this.getId(), CimiJob.class);
            if (this.getState() != Job.Status.RUNNING) {
                break;
            }
            if (java.lang.System.nanoTime() > endTime) {
                throw new TimeoutException();
            }
            Thread.sleep(periodInMilliseconds);
        }
    }

    public void waitForCompletion(final long timeout, final TimeUnit unit) throws CimiException, TimeoutException,
        InterruptedException {
        long period = unit.convert(this.DEFAULT_POLL_PERIOD_IN_SECONDS, TimeUnit.SECONDS);
        this.waitForCompletion(timeout, period, unit);
    }

    public static List<Job> getJobs(final CimiClient client, final QueryParams queryParams) throws CimiException {
        if (client.cloudEntryPoint.getJobs() == null) {
            throw new CimiException("Unsupported operation");
        }
        org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiJobCollection jobCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getJobs().getHref()), CimiJobCollectionRoot.class, queryParams);

        List<Job> result = new ArrayList<Job>();

        if (jobCollection.getCollection() != null) {
            for (CimiJob cimiJob : jobCollection.getCollection().getArray()) {
                result.add(new Job(client, cimiJob));
            }
        }
        return result;
    }

    public static Job getJobByReference(final CimiClient client, final String ref) throws CimiException {
        return new Job(client, client.getCimiObjectByReference(ref, CimiJob.class));
    }

}
