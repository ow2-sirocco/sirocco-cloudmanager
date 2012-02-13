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

package org.ow2.sirocco.cloudmanager.service.impl;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import org.ow2.sirocco.cloudmanager.provider.util.jobmanager.api.JobCompletionEvent;
import org.ow2.sirocco.cloudmanager.service.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.service.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.service.api.IVolumeManager;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "JobCompletion")})
public class JobCompletionHandlerBean implements MessageListener {
    private static Logger logger = Logger.getLogger(JobCompletionHandlerBean.class.getName());

    private static final String JMS_TOPIC_CONNECTION_FACTORY_NAME = "JTCF";

    private static final String JMS_TOPIC_NAME = "JobCompletion";

    @EJB
    private IMachineManager machineManager;

    @EJB
    private IVolumeManager volumeManager;

    @EJB
    private IMachineImageManager imageManager;

    public void onMessage(final Message msg) {
        if (msg instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) msg;
            Object payload;
            try {
                payload = objectMessage.getObject();
                JobCompletionHandlerBean.logger.info("On topic JobCompletion: received " + payload);
            } catch (JMSException ex) {
                JobCompletionHandlerBean.logger.log(Level.SEVERE, "Failed to extract from JMS message", ex);
                return;
            }
            JobCompletionEvent jobCompletionEvent = (JobCompletionEvent) payload;
            boolean done = false;
            if (jobCompletionEvent.getAction().startsWith("machine")) {
                done = this.machineManager.handleJobCompletion(jobCompletionEvent);
            } else if (jobCompletionEvent.getAction().startsWith("volume")) {
                done = this.volumeManager.handleJobCompletion(jobCompletionEvent);
            } else if (jobCompletionEvent.getAction().startsWith("image")) {
                done = this.imageManager.handleJobCompletion(jobCompletionEvent);
            }

            if (!done) {
                try {
                    this.emitMessage(jobCompletionEvent);
                } catch (Exception e) {
                }
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