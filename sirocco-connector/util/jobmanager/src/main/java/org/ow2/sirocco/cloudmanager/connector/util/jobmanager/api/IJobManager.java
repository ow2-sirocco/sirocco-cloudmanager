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
 *  $Id: JobManager.java 913 2012-02-20 09:34:20Z dangtran $
 *
 */
package org.ow2.sirocco.cloudmanager.connector.util.jobmanager.api;

import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.exception.JobException;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.JobCollection;

public interface IJobManager {
    
    static final String EJB_JNDI_NAME = "JobManager";
    
	Job createJob(String targetEntity, String action) throws JobException;

	Job getJobById(String id) throws JobException;
	
	Job updateJob(Job job) throws JobException;

	JobCollection getJobCollection() throws JobException;
	
	JobCollection updateJobCollection(JobCollection jobColl) throws JobException;
	
	void deleteJob(String id) throws JobException;

}
