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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJob;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiJobCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContextImpl;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.JobCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;

/**
 * Converters tests of monitoring resources.
 */
public class MonitoringConverterTest {

    private CimiRequest request;

    private CimiContext context;

    @Before
    public void setUp() throws Exception {

        this.request = new CimiRequest();
        this.request.setBaseUri("http://www.test.org/");
        this.context = new CimiContextImpl(this.request, new CimiResponse());
    }

    @Test
    public void testCimiJob() throws Exception {
        CimiJob cimi;
        Job service;

        // Empty Service -> Cimi
        cimi = (CimiJob) this.context.convertToCimi(new Job(), CimiJob.class);
        Assert.assertNull(cimi.getAction());
        Assert.assertNull(cimi.getIsCancellable());
        Assert.assertNull(cimi.getNestedJobs());
        Assert.assertNull(cimi.getParentJob());
        Assert.assertNull(cimi.getProgress());
        Assert.assertNull(cimi.getReturnCode());
        Assert.assertNull(cimi.getStatus());
        Assert.assertNull(cimi.getStatusMessage());
        Assert.assertNull(cimi.getTargetEntity());
        Assert.assertNull(cimi.getTimeOfStatusChange());

        // Full Service -> Cimi
        CloudResource targetResource = new Machine();
        targetResource.setId(321);
        Date timeOfStatusChange = new Date();
        Job parentJob = new Job();
        parentJob.setId(789);

        service = new Job();
        service.setAction("action");
        service.setIsCancellable(Boolean.TRUE);
        service.setParentJob(parentJob);
        service.setProgress(13);
        service.setReturnCode(11);
        service.setStatus(Job.Status.RUNNING);
        service.setStatusMessage("statusMessage");
        service.setTargetEntity(targetResource);
        service.setTimeOfStatusChange(timeOfStatusChange);

        cimi = (CimiJob) this.context.convertToCimi(service, CimiJob.class);
        Assert.assertEquals("action", cimi.getAction());
        Assert.assertEquals(Boolean.TRUE, cimi.getIsCancellable());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Job.getPathname() + "/789", cimi.getParentJob().getHref());
        Assert.assertEquals(13, cimi.getProgress().intValue());
        Assert.assertEquals(11, cimi.getReturnCode().intValue());
        Assert.assertEquals(Job.Status.RUNNING.toString(), cimi.getStatus());
        Assert.assertEquals("statusMessage", cimi.getStatusMessage());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Machine.getPathname() + "/321", cimi.getTargetEntity());
        Assert.assertEquals(timeOfStatusChange, cimi.getTimeOfStatusChange());

        // Full Service -> Cimi
        service = new Job();
        service.setNestedJobs(new ArrayList<Job>());

        cimi = (CimiJob) this.context.convertToCimi(service, CimiJob.class);
        Assert.assertNull(cimi.getNestedJobs());

        // Full Service -> Cimi
        List<Job> listJob = new ArrayList<Job>();
        for (int i = 0; i < 3; i++) {
            Job job = new Job();
            job.setId(i + 100);
            listJob.add(job);
        }
        service = new Job();
        service.setNestedJobs(listJob);

        cimi = (CimiJob) this.context.convertToCimi(service, CimiJob.class);
        Assert.assertNotNull(cimi.getNestedJobs());
        Assert.assertEquals(3, cimi.getNestedJobs().length);
        for (int i = 0; i < cimi.getNestedJobs().length; i++) {
            Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Job.getPathname() + "/" + (i + 100),
                cimi.getNestedJobs()[i].getHref());
        }
    }

    @Test
    public void testCimiJobCollection() throws Exception {
        CimiJobCollection cimi;
        JobCollection service;

        // Empty Cimi -> Service
        service = (JobCollection) this.context.convertToService(new CimiJobCollection());
        Assert.assertNull(service.getJobs());

        // Empty Service -> Cimi
        cimi = (CimiJobCollection) this.context.convertToCimi(new JobCollection(), CimiJobCollection.class);
        Assert.assertNull(cimi.getArray());

        // Full Cimi -> Service
        cimi = new CimiJobCollection();
        cimi.setArray(new CimiJob[] {new CimiJob(), new CimiJob()});

        service = (JobCollection) this.context.convertToService(cimi);
        Assert.assertEquals(2, service.getJobs().size());

        // Full Service -> Cimi
        Job Job1 = new Job();
        Job1.setId(1);
        Job1.setName("nameOne");
        Job Job2 = new Job();
        Job2.setId(2);
        Job2.setName("nameTwo");
        Job Job3 = new Job();
        Job3.setId(3);
        Job3.setName("nameThree");

        service = new JobCollection();
        service.setJobs(Arrays.asList(new Job[] {Job1, Job2, Job3}));

        cimi = (CimiJobCollection) this.context.convertToCimi(service, CimiJobCollection.class);
        Assert.assertEquals(3, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Job.getPathname() + "/1", cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Job.getPathname() + "/2", cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Job.getPathname() + "/3", cimi.getArray()[2].getHref());
        Assert.assertNull(cimi.getArray()[2].getId());
        Assert.assertNull(cimi.getArray()[2].getName());

        cimi = (CimiJobCollection) this.context.convertToCimi(Arrays.asList(new Job[] {Job3, Job1}), CimiJobCollection.class);
        Assert.assertEquals(2, cimi.getArray().length);
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Job.getPathname() + "/3", cimi.getArray()[0].getHref());
        Assert.assertNull(cimi.getArray()[0].getId());
        Assert.assertNull(cimi.getArray()[0].getName());
        Assert.assertEquals(this.request.getBaseUri() + ExchangeType.Job.getPathname() + "/1", cimi.getArray()[1].getHref());
        Assert.assertNull(cimi.getArray()[1].getId());
        Assert.assertNull(cimi.getArray()[1].getName());
    }
}