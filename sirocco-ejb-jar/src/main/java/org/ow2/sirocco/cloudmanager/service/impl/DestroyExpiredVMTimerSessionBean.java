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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.ow2.sirocco.cloudmanager.common.DbParameters;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.service.api.ITimer;
import org.ow2.sirocco.cloudmanager.utils.MailUtils;

@Stateless(name = "DestroyExpiredVMTimerSessionBean", mappedName = "DestroyExpiredVMTimerSessionBean")
@Local(ITimer.class)
public class DestroyExpiredVMTimerSessionBean implements ITimer {
    private static Logger logger = Logger.getLogger(DestroyExpiredVMTimerSessionBean.class.getName());

    public static String TIMER_NAME = "DestroyExpiredVMTimer";

    @Resource
    private TimerService timerService;

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    private static final int INITIAL_DURATION_IN_MILLISECONDS_10000 = 10000;

    private static final int INTERVAL_DURATION_IN_MILLISECONDS_86400000 = 86400000;

    public void startTimer() {
        this.timerService.createTimer(DestroyExpiredVMTimerSessionBean.INITIAL_DURATION_IN_MILLISECONDS_10000,
            DestroyExpiredVMTimerSessionBean.INTERVAL_DURATION_IN_MILLISECONDS_86400000,
            DestroyExpiredVMTimerSessionBean.TIMER_NAME);
        DestroyExpiredVMTimerSessionBean.logger.info("DestroyExpiredVMTimerSessionBean set");
    }

    @Timeout
    public void handleTimeout(final Timer timer) {

        // query database for expired VM
        Query query = this.em.createNativeQuery(
            "SELECT * FROM VirtualMachine v WHERE v.expirationDate < CURRENT_DATE() AND v.state<>'DELETED'", Machine.class);

        @SuppressWarnings("unchecked")
        List<Machine> result = query.getResultList();
        if (result.size() > 0) {
            for (Object obj : result) {
                Machine vs = (Machine) obj;
                try {
                    String to = vs.getUser().getEmail();

                    // used to set $ variables in DbParameters mail
                    final Map<String, String> varMap = new HashMap<String, String>();
                    varMap.put("vm", vs.getName());
                    varMap.put("url", DbParameters.getInstance().SERVICE_URL);
                    varMap.put("user", vs.getUser().getEmail());

                    MailUtils.sendMail(DbParameters.getAdminMail(), to, DbParameters.getInstance().SERVICE_NAME,
                        MailUtils.buildMail(DbParameters.getInstance().VM_EXPIRED_MAIL1, varMap));
                    MailUtils.sendMail(DbParameters.getAdminMail(), DbParameters.getAdminMail(),
                        DbParameters.getInstance().SERVICE_NAME,
                        MailUtils.buildMail(DbParameters.getInstance().VM_EXPIRED_MAIL2, varMap));
                } catch (Exception ex) {
                    DestroyExpiredVMTimerSessionBean.logger.info("Error while sending mail");
                }
            }
        }
    }

    public void checkStatus() {
        @SuppressWarnings("unchecked")
        Collection<Timer> timers = this.timerService.getTimers();
        for (Timer timer : timers) {
            DestroyExpiredVMTimerSessionBean.logger.info("Timer will expire after " + timer.getTimeRemaining()
                + " milliseconds.\n");
        }
    }

    public void stopTimer() {
        @SuppressWarnings("unchecked")
        Collection<Timer> timers = this.timerService.getTimers();
        for (Timer timer : timers) {
            if (timer.getInfo().equals(DestroyExpiredVMTimerSessionBean.TIMER_NAME)) {
                timer.cancel();
            }
        }
        DestroyExpiredVMTimerSessionBean.logger.info("DestroyExpiredVMTimerSessionBean stopped");
    }
}
