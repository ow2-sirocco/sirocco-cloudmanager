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

package org.ow2.sirocco.cloudmanager.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.ow2.sirocco.cloudmanager.service.api.ITimer;

@SuppressWarnings("serial")
public class LoadTimers extends HttpServlet {

    private static Logger log = Logger.getLogger(LoadTimers.class.getName());

    private InitialContext context;

    private ITimer destroyVMOfExpiredReservationTimer;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        try {
            this.context = new InitialContext();
            // destroyVMOfExpiredReservationTimer=(ITimer)
            // this.context.lookup("DestroyVMOfExpiredReservationTimerSessionBean");
        } catch (NamingException e) {
            LoadTimers.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
        }
        LoadTimers.log.info("Starting all timers...");
        // this.destroyVMOfExpiredReservationTimer.stopTimer();
        // destroyVMOfExpiredReservationTimer.startTimer();
    }

    public void contextDestroyed(final ServletContextEvent event) {
        LoadTimers.log.info("Context Destroyed !!!");// XXX
    }

}
