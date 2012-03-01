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

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "JobCompletion")})
public class JobCompletionHandlerBean implements MessageListener {
    private static Logger logger = Logger.getLogger(JobCompletionHandlerBean.class.getName());

    private static final String JMS_TOPIC_CONNECTION_FACTORY_NAME = "JTCF";

    private static final String JMS_TOPIC_NAME = "JobCompletion";

    @EJB
    private IVolumeManager volumeManager;

    @EJB
    private IMachineManager machineManager;

    @PersistenceContext
    private EntityManager em;

    public void onMessage(final Message msg) {
        if (msg instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) msg;
            Object payload;
            try {
                payload = objectMessage.getObject();
                JobCompletionHandlerBean.logger.info("On topic JobCompletion: received " + payload);
            } catch (JMSException ex) {
                JobCompletionHandlerBean.logger.error("Failed to extract from JMS message", ex);
                return;
            }
            Job providerJob = (Job) payload;
            boolean done = false;
            if (providerJob.getAction().startsWith("machine")) {
                done = this.machineManager.machineCompletionHandler(providerJob);
            } else if (providerJob.getAction().startsWith("volume")) {
                done = this.volumeManager.volumeCompletionHandler(providerJob);
            } else if (providerJob.getAction().startsWith("image")) {
            }

            // update Job entity
            try {
                Job job = (Job) this.em.createQuery("SELECT j FROM Job j WHERE j.providerAssignedId=:providerAssignedId")
                    .setParameter("providerAssignedId", providerJob.getProviderAssignedId()).getSingleResult();
                job.setStatus(providerJob.getStatus());
                job.setStatusMessage(providerJob.getStatusMessage());
                job.setReturnCode(providerJob.getReturnCode());
                job.setTimeOfStatusChange(new Date());
            } catch (NoResultException e) {
                // should not happen
                JobCompletionHandlerBean.logger.info("Cannot find job with providerAssignedId "
                    + providerJob.getProviderAssignedId());
            }

        }
    }

    private void emitMessage(final Serializable payload) throws Exception {
        InitialContext ctx = new InitialContext();
        TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) ctx
            .lookup(JobCompletionHandlerBean.JMS_TOPIC_CONNECTION_FACTORY_NAME);
        TopicConnection connection = topicConnectionFactory.createTopicConnection();
        TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic cloudAdminTopic = (Topic) ctx.lookup(JobCompletionHandlerBean.JMS_TOPIC_NAME);
        TopicPublisher topicPublisher = session.createPublisher(cloudAdminTopic);
        ObjectMessage message = session.createObjectMessage();
        message.setObject(payload);
        topicPublisher.publish(message);
        JobCompletionHandlerBean.logger.info("EMITTED EVENT " + payload.toString() + " on "
            + JobCompletionHandlerBean.JMS_TOPIC_NAME + " topic");
        topicPublisher.close();
        session.close();
        connection.close();
    }

}