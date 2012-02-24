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
package org.ow2.sirocco.cloudmanager.connector.util.jobmanager.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.api.IJobManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.google.common.util.concurrent.ListenableFuture;

public class JobManager implements IJobManager, ManagedService {
	private static Log logger = LogFactory.getLog(JobManager.class);

	public static String JOB_WATCHER_PERIOD_PROP_NAME = "jobWatcherPeriodInSeconds";

	public static String JOB_RETENTION_TIME_PROP_NAME = "jobRetentionPeriodInSeconds";

	public static long DEFAULT_JOB_WATCHER_PERIOD_IN_SECONDS = 5 * 60;

	public static long DEFAULT_JOB_RETENTION_TIME_IN_SECONDS = 5 * 60;

	private static final String JMS_TOPIC_CONNECTION_FACTORY_NAME = "JTCF";

	private static final String JMS_TOPIC_NAME = "JobCompletion";

	private static class JobEntry {
		JobEntry(final Job job, final ListenableFuture<?> result) {
			this.job = job;
			this.result = result;
		}

		Job job;
		ListenableFuture<?> result;
	};

	private long jobWatcherPeriodInSeconds = JobManager.DEFAULT_JOB_WATCHER_PERIOD_IN_SECONDS;

	private long jobRetentionPeriodInSeconds = JobManager.DEFAULT_JOB_RETENTION_TIME_IN_SECONDS;

	private final Map<String, JobEntry> jobs = new ConcurrentHashMap<String, JobEntry>();

	private final ScheduledExecutorService scheduler = Executors
			.newSingleThreadScheduledExecutor();

	private final ExecutorService jobCompletionExecutorService = Executors
			.newSingleThreadExecutor();

	private ScheduledFuture<?> jobWatcherHandle;

	private JobManager() {
	}

	@SuppressWarnings("rawtypes")
	public void updated(final Dictionary properties)
			throws ConfigurationException {
		if (properties != null) {
			String s = (String) properties
					.get(JobManager.JOB_WATCHER_PERIOD_PROP_NAME);
			if (s != null) {
				try {
					this.jobWatcherPeriodInSeconds = Integer.parseInt(s);
				} catch (NumberFormatException ex) {
					JobManager.logger
							.error("Illegal value for jobWatcherPeriodInSeconds property: "
									+ s);
				}
			}
			s = (String) properties
					.get(JobManager.JOB_RETENTION_TIME_PROP_NAME);
			if (s != null) {
				try {
					this.jobRetentionPeriodInSeconds = Integer.parseInt(s);
				} catch (NumberFormatException ex) {
					JobManager.logger
							.error("Illegal value for jobRetentionPeriodInSeconds property: "
									+ s);
				}
			}

		}
		JobManager.logger.info("JobManager ready, watcher period: "
				+ this.jobWatcherPeriodInSeconds + "s, job retention time: "
				+ this.jobRetentionPeriodInSeconds + " s");
	}

	public void start() {
		final Runnable jobWatcher = new Runnable() {
			@Override
			public void run() {
				JobManager.logger.debug("Job watcher woken up");
				Date now = new Date();
				for (JobEntry jobEntry : JobManager.this.jobs.values()) {
					Job job = jobEntry.job;
					if (job.getStatus() != Job.Status.RUNNING) {
						if (TimeUnit.MILLISECONDS.toSeconds(now.getTime()
								- job.getTimeOfStatusChange().getTime()) > JobManager.this.jobRetentionPeriodInSeconds) {
							JobManager.logger.info("Reaping job "
									+ job.getProviderAssignedId());
							JobManager.this.jobs.remove(job
									.getProviderAssignedId());
						}
					}
				}
			}
		};
		this.jobWatcherHandle = this.scheduler.scheduleAtFixedRate(jobWatcher,
				0, this.jobWatcherPeriodInSeconds, TimeUnit.SECONDS);
	}

	public void shutdown() {
		this.jobWatcherHandle.cancel(true);
		this.scheduler.shutdown();
		System.out.println("JobManager shutdowned");
	}

	public static JobManager newJobManager() {
		JobManager jobManager = new JobManager();
		return jobManager;
	}

	public Job newJob(final String targetEntityId, final String action,
			final ListenableFuture<?> result) {
		String jobId = UUID.randomUUID().toString();
		final Job job = new Job();
		job.setProviderAssignedId(jobId);
		job.setTargetEntity(targetEntityId);
		job.setAction(action);
		job.setIsCancellable(false);
		job.setStatus(Job.Status.RUNNING);
		this.jobs.put(jobId, new JobEntry(job, result));
		return job;
	}

	@Override
	public void setNotificationOnJobCompletion(final String jobId)
			throws Exception {
		final JobEntry jobEntry = this.jobs.get(jobId);
		if (jobEntry == null) {
			throw new Exception("Invalid jobId: " + jobId);
		}
		jobEntry.result.addListener(new Runnable() {
			@Override
			public void run() {
				if (jobEntry.result.isCancelled()) {
					jobEntry.job.setStatus(Job.Status.CANCELLED);
					jobEntry.job.setStatusMessage("cancelled");
				} else {
					boolean interrupted = false;
					try {
						while (true) {
							try {
								jobEntry.result.get();
								jobEntry.job.setStatus(Job.Status.SUCCESS);
								break;
							} catch (InterruptedException ex) {
								interrupted = true;
								// retry until not interrupted
							} catch (ExecutionException ex) {
								jobEntry.job.setStatusMessage(ex.getCause()
										.getMessage());
								jobEntry.job.setStatus(Job.Status.FAILED);
								break;
							} catch (CancellationException ex) {
								jobEntry.job.setStatus(Job.Status.CANCELLED);
								jobEntry.job.setStatusMessage("cancelled");
								break;
							}
						}
					} finally {
						if (interrupted) {
							Thread.currentThread().interrupt();
						}
					}
				}
				jobEntry.job.setTimeOfStatusChange(new Date());
				JobManager.this.emitJobCompletionEvent(jobEntry.job);
			}
		}, this.jobCompletionExecutorService);

	}

	public Job getJobById(final String id) {
		JobEntry jobEntry = this.jobs.get(id);
		if (jobEntry == null) {
			return null;
		}
		return jobEntry.job;
	}

	public List<Job> getAllJobs() {
		ArrayList<Job> result = new ArrayList<Job>();
		for (JobEntry jobEntry : this.jobs.values()) {
			result.add(jobEntry.job);
		}
		return result;
	}

	private <T> void emitJobCompletionEvent(final Job job) {
		try {
			this.emitMessage(job);
		} catch (Exception ex) {
			JobManager.logger.error("Failed to emit message", ex);
		}
	}

	private void emitMessage(final Serializable payload) throws Exception {
		InitialContext ctx = new InitialContext();
		TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) ctx
				.lookup(JobManager.JMS_TOPIC_CONNECTION_FACTORY_NAME);
		TopicConnection connection = topicConnectionFactory
				.createTopicConnection();
		TopicSession session = connection.createTopicSession(false,
				Session.AUTO_ACKNOWLEDGE);
		Topic cloudAdminTopic = (Topic) ctx
				.lookup(JobManager.JMS_TOPIC_NAME);
		TopicPublisher topicPublisher = session
				.createPublisher(cloudAdminTopic);
		ObjectMessage message = session.createObjectMessage();
		message.setObject(payload);
		topicPublisher.publish(message);
		JobManager.logger.info("EMITTED EVENT " + payload.toString()
				+ " on " + JobManager.JMS_TOPIC_NAME + " topic");
		topicPublisher.close();
		session.close();
		connection.close();
	}

}
