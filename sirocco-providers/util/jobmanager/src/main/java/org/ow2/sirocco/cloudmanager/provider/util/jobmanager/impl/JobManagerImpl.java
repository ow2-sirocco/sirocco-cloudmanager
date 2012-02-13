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
package org.ow2.sirocco.cloudmanager.provider.util.jobmanager.impl;

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
import java.util.concurrent.Future;
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
import org.ow2.sirocco.cloudmanager.provider.api.entity.Job;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobCompletionEvent;
import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobManager;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.google.common.util.concurrent.ListenableFuture;

public class JobManagerImpl implements JobManager, ManagedService {
    private static Log logger = LogFactory.getLog(JobManagerImpl.class);

    public static String JOB_WATCHER_PERIOD_PROP_NAME = "jobWatcherPeriodInSeconds";

    public static String JOB_RETENTION_TIME_PROP_NAME = "jobRetentionPeriodInSeconds";

    public static long DEFAULT_JOB_WATCHER_PERIOD_IN_SECONDS = 5 * 60;

    public static long DEFAULT_JOB_RETENTION_TIME_IN_SECONDS = 5 * 60;

    private static final String JMS_TOPIC_CONNECTION_FACTORY_NAME = "JTCF";

    private static final String JMS_TOPIC_NAME = "JobCompletion";

    private long jobWatcherPeriodInSeconds = JobManagerImpl.DEFAULT_JOB_WATCHER_PERIOD_IN_SECONDS;

    private long jobRetentionPeriodInSeconds = JobManagerImpl.DEFAULT_JOB_RETENTION_TIME_IN_SECONDS;

    private final Map<String, JobImpl<?>> jobs = new ConcurrentHashMap<String, JobImpl<?>>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final ExecutorService jobCompletionExecutorService = Executors.newSingleThreadExecutor();

    private ScheduledFuture<?> jobWatcherHandle;

    private JobManagerImpl() {
    }

    @SuppressWarnings("rawtypes")
    public void updated(final Dictionary properties) throws ConfigurationException {
        if (properties != null) {
            String s = (String) properties.get(JobManagerImpl.JOB_WATCHER_PERIOD_PROP_NAME);
            if (s != null) {
                try {
                    this.jobWatcherPeriodInSeconds = Integer.parseInt(s);
                } catch (NumberFormatException ex) {
                    JobManagerImpl.logger.error("Illegal value for jobWatcherPeriodInSeconds property: " + s);
                }
            }
            s = (String) properties.get(JobManagerImpl.JOB_RETENTION_TIME_PROP_NAME);
            if (s != null) {
                try {
                    this.jobRetentionPeriodInSeconds = Integer.parseInt(s);
                } catch (NumberFormatException ex) {
                    JobManagerImpl.logger.error("Illegal value for jobRetentionPeriodInSeconds property: " + s);
                }
            }

        }
        JobManagerImpl.logger.info("JobManager ready, watcher period: " + this.jobWatcherPeriodInSeconds
            + "s, job retention time: " + this.jobRetentionPeriodInSeconds + " s");
    }

    public void start() {
        final Runnable jobWatcher = new Runnable() {
            @Override
            public void run() {
                JobManagerImpl.logger.debug("Job watcher woken up");
                Date now = new Date();
                for (JobImpl<?> job : JobManagerImpl.this.jobs.values()) {
                    if (job.getStatus() != Job.Status.RUNNING) {
                        // if (!job.notificationFired) {
                        // JobManagerImpl.this.emitJobCompletionEvent(job);
                        // }
                        if (TimeUnit.MILLISECONDS.toSeconds(now.getTime() - job.getTimeOfStatusChange().getTime()) > JobManagerImpl.this.jobRetentionPeriodInSeconds) {
                            System.out.println("Reaping job " + job.getId());
                            JobManagerImpl.this.jobs.remove(job.getId());
                        }
                    }
                }
            }
        };
        this.jobWatcherHandle = this.scheduler.scheduleAtFixedRate(jobWatcher, 0, this.jobWatcherPeriodInSeconds,
            TimeUnit.SECONDS);
    }

    public void shutdown() {
        this.jobWatcherHandle.cancel(true);
        this.scheduler.shutdown();
        System.out.println("JobManager shutdowned");
    }

    public static JobManagerImpl newJobManager() {
        JobManagerImpl jobManager = new JobManagerImpl();
        return jobManager;
    }

    public <T> Job<T> newJob(final String targetEntityId, final String action, final ListenableFuture<T> result) {
        String jobId = UUID.randomUUID().toString();
        final JobImpl<T> job = new JobImpl<T>(jobId, result, targetEntityId, action);
        this.jobs.put(jobId, job);
        result.addListener(new Runnable() {
            @Override
            public void run() {
                JobManagerImpl.this.emitJobCompletionEvent(job);
            }
        }, this.jobCompletionExecutorService);
        return job;
    }

    public Job<?> getJobById(final String id) {
        return this.jobs.get(id);
    }

    public List<Job<?>> getAllJobs() {
        return new ArrayList<Job<?>>(this.jobs.values());
    }

    private <T> void emitJobCompletionEvent(final JobImpl<T> job) {
        try {
            this.emitMessage(new JobCompletionEvent(job));
            job.notificationFired = true;
        } catch (Exception ex) {
            JobManagerImpl.logger.error("Failed to emit message", ex);
        }
    }

    private void emitMessage(final Serializable payload) throws Exception {
        InitialContext ctx = new InitialContext();
        TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) ctx
            .lookup(JobManagerImpl.JMS_TOPIC_CONNECTION_FACTORY_NAME);
        TopicConnection connection = topicConnectionFactory.createTopicConnection();
        TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic cloudAdminTopic = (Topic) ctx.lookup(JobManagerImpl.JMS_TOPIC_NAME);
        TopicPublisher topicPublisher = session.createPublisher(cloudAdminTopic);
        ObjectMessage message = session.createObjectMessage();
        message.setObject(payload);
        topicPublisher.publish(message);
        JobManagerImpl.logger.info("EMITTED EVENT " + payload.toString() + " on " + JobManagerImpl.JMS_TOPIC_NAME + " topic");
        topicPublisher.close();
        session.close();
        connection.close();
    }

    private static class JobImpl<T> implements Job<T> {
        private final String id;

        private final Future<T> result;

        private final String targetEntity;

        private final String action;

        private Date timeOfStatusChange;

        private Status status;

        private String statusMessage;

        boolean notificationFired;

        JobImpl(final String id, final Future<T> result, final String targetEntity, final String action) {
            this.id = id;
            this.result = result;
            this.targetEntity = targetEntity;
            this.action = action;
            this.updateStatus();
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public String getTargetEntity() {
            return this.targetEntity;
        }

        @Override
        public String getAction() {
            return this.action;
        }

        @Override
        public Future<T> getResult() {
            return this.result;
        }

        @Override
        public boolean isCancellable() {
            // TODO
            return false;
        }

        @Override
        public boolean cancel() {
            // TODO
            return false;
        }

        private synchronized void updateStatus() {
            if (this.status == null || this.status == Status.RUNNING) {
                Status newStatus = this.status;
                if (this.result.isCancelled()) {
                    newStatus = Status.CANCELLED;
                    this.statusMessage = "cancelled";
                } else if (!this.result.isDone()) {
                    newStatus = Status.RUNNING;
                    this.statusMessage = "running";
                } else {
                    boolean interrupted = false;
                    try {
                        while (true) {
                            try {
                                this.result.get();
                                newStatus = Status.SUCCESS;
                                break;
                            } catch (InterruptedException ex) {
                                interrupted = true;
                                // retry until not interrupted
                            } catch (ExecutionException ex) {
                                this.statusMessage = ex.getCause().getMessage();
                                newStatus = Status.FAILED;
                                break;
                            } catch (CancellationException ex) {
                                newStatus = Status.CANCELLED;
                                this.statusMessage = "cancelled";
                                break;
                            }
                        }
                    } finally {
                        if (interrupted) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                if (newStatus != this.status) {
                    this.status = newStatus;
                    this.timeOfStatusChange = new Date();
                }
            }
        }

        @Override
        public Status getStatus() {
            this.updateStatus();
            return this.status;
        }

        @Override
        public String getStatusMessage() {
            this.updateStatus();
            return this.statusMessage;
        }

        @Override
        public int getProgress() {
            // TODO
            return 0;
        }

        @Override
        public Date getTimeOfStatusChange() {
            this.updateStatus();
            return this.timeOfStatusChange;
        }

        @Override
        public String toString() {
            this.updateStatus();
            return "JobImpl [id=" + this.id + ", targetEntityId=" + this.targetEntity + ", action=" + this.action
                + ", timeOfStatusChange=" + this.timeOfStatusChange + ", status=" + this.status + ", statusMessage="
                + this.statusMessage + "]";
        }
    }
}
