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

package org.ow2.sirocco.cloudmanager.core.impl;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "JobCompletion")})
public class JobCompletionHandlerBean implements MessageListener {
    private static Logger logger = Logger.getLogger(JobCompletionHandlerBean.class.getName());

    private static final String JMS_TOPIC_CONNECTION_FACTORY_NAME = "JTCF";

    private static final String JMS_TOPIC_NAME = "JobCompletion";

    private static final long JMS_REDELIVERY_DELAY = 1 * 2000;

    @EJB
    private IJobManager jobManager;

    @Resource
    private EJBContext ctx;

    @Override
    // @dTransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void onMessage(final Message msg) {
        if (msg instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) msg;
            Object payload;
            try {
                payload = objectMessage.getObject();
                JobCompletionHandlerBean.logger.debug("On topic JobCompletion: received " + payload);
            } catch (JMSException ex) {
                JobCompletionHandlerBean.logger.error("Failed to extract from JMS message", ex);
                return;
            }
            Job providerJob = (Job) payload;
            String jobId = providerJob.getProviderAssignedId();
            // we call jobManager to deal with events
            try {
                this.jobManager.handleWorkflowEvent(providerJob);
            } catch (Exception e) {
                this.ctx.setRollbackOnly();
                JobCompletionHandlerBean.logger.warn("JobCompletion message rollbacked - " + jobId);
                // + e.getMessage(), e);

                try {
                    // not possible to set a redelevery time in Joram/Jonas
                    Thread.sleep(JobCompletionHandlerBean.JMS_REDELIVERY_DELAY
                        + (long) Math.floor(Math.random() * JobCompletionHandlerBean.JMS_REDELIVERY_DELAY * 2));
                } catch (InterruptedException e1) {
                    JobCompletionHandlerBean.logger.warn("InterruptedException! - " + jobId);
                    // e1.printStackTrace();
                }
            }
        }
    }

}