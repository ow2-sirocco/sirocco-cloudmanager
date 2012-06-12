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
 *  $Id: JobSendHandlerBean.java 1296 2012-06-11 15:34:26Z ycas7461 $
 *
 */

package org.ow2.sirocco.cloudmanager.core.impl;

import java.io.Serializable;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBContext;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;
import org.apache.log4j.Logger;
import org.osgi.framework.*;
import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.api.IJobManager;
import org.ow2.sirocco.cloudmanager.connector.util.jobmanager.impl.JobManager;

/**
 * This MDB is used to guarantee that we send messages to listen provider jobs
 * AFTER EJB commit, to ensure that handling of provider events (by JobManager)
 * can rely on an up-to-date database
 * 
 * @author ycas7461
 * 
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "JobEmission") })
public class JobSendHandlerBean implements MessageListener {
    private static Logger logger = Logger.getLogger(JobSendHandlerBean.class
            .getName());

    private static final String JMS_QUEUE_CONNECTION_FACTORY_NAME = "JQCF";

    private static final String JMS_QUEUE_NAME = "JobEmission";

    private static final long JMS_REDELIVERY_DELAY = 5 * 1000;

    @Resource
    private EJBContext ctx;

    @OSGiResource
    BundleContext context;

    private IJobManager getJobManager() {
        ServiceReference sr = context.getServiceReference(IJobManager.class.getName());
        return (IJobManager) context.getService(sr);
    }

    @Override
    public void onMessage(final Message msg) {
        if (msg instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) msg;

            IJobManager jobM = getJobManager();
            if (jobM == null) {
                JobSendHandlerBean.logger
                        .warn("JobManager is null - message rollback");
                ctx.setRollbackOnly();
            }
            try {
                Object payload = objectMessage.getObject();
                JobSendHandlerBean.logger.info("setting up Job completion listener");
                jobM.setNotificationOnJobCompletion((String) payload);
            } catch (Exception e2) {
                JobSendHandlerBean.logger
                .warn("Exception "+e2.getMessage()+" - message rollback");
                ctx.setRollbackOnly();
            }

            // try {
            // payload = objectMessage.getObject();
            // JobSendHandlerBean.logger
            // .info("On topic JobCompletion: received " + payload);
            // } catch (JMSException ex) {
            // JobSendHandlerBean.logger.error(
            // "Failed to extract from JMS message", ex);
            // return;
            // }
            // Job providerJob = (Job) payload;
            // we call jobManager to deal with events
            try {
                // jobManager.handleWorkflowEvent(providerJob);
            } catch (Exception e) {
                ctx.setRollbackOnly();
                JobSendHandlerBean.logger
                        .warn("JobCompletion message rollbacked - "
                                + e.getMessage());

                try {
                    // not possible to set a redelevery time in Joram/Jonas
                    Thread.sleep(JMS_REDELIVERY_DELAY
                            + (long) Math.floor(Math.random()
                                    * JMS_REDELIVERY_DELAY));
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private void emitMessage(final Serializable payload, InitialContext ctx)
            throws Exception {

        QueueConnectionFactory qcf = (QueueConnectionFactory) ctx
                .lookup("JQCF");
        QueueConnection queueCon = qcf.createQueueConnection();
        QueueSession queueSession = queueCon.createQueueSession(false,
                Session.AUTO_ACKNOWLEDGE);
        Queue queue = (Queue) ctx.lookup("JobEmission");
        QueueSender sender = queueSession.createSender(queue);
        Message msg = queueSession.createObjectMessage(payload);
        // msg.setLongProperty("scheduleDate",System.currentTimeMillis() +
        // delayMilli);
        sender.send(msg);
        JobSendHandlerBean.logger.info("EMITTED SEND EVENT for payload "
                + payload.toString());
    }

}