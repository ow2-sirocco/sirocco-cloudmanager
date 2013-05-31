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

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBContext;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.glassfish.osgicdi.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This MDB is used to guarantee that we send messages to listen provider jobs
 * AFTER EJB commit, to ensure that handling of provider events (by JobManager)
 * can rely on an up-to-date database
 * 
 * @author ycas7461
 */
@MessageDriven(mappedName = "jms/JobEmission", activationConfig = {@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")})
public class JobSendHandlerBean implements MessageListener {
    private static Logger logger = LoggerFactory.getLogger(JobSendHandlerBean.class.getName());

    private static final long JMS_REDELIVERY_DELAY = 5 * 1000;

    @Resource
    private EJBContext ctx;



    @Override
    public void onMessage(final Message msg) {
        if (msg instanceof ObjectMessage) {

            try {
                ObjectMessage objectMessage = (ObjectMessage) msg;

                Object payload = objectMessage.getObject();
                JobSendHandlerBean.logger.debug("setting up Job completion listener");
              //TODO:workflowthis.jobManager.setNotificationOnJobCompletion((String) payload);
            } catch (Exception e2) {
                JobSendHandlerBean.logger.warn("Exception " + e2.getMessage() + " - message rollback");
                this.ctx.setRollbackOnly();
                try {
                    // not possible to set a redelevery time in Joram/Jonas
                    Thread.sleep(JobSendHandlerBean.JMS_REDELIVERY_DELAY
                        + (long) Math.floor(Math.random() * JobSendHandlerBean.JMS_REDELIVERY_DELAY));
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}