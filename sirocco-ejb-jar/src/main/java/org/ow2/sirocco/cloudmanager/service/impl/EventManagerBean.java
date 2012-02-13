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

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.provider.api.entity.Event;
import org.ow2.sirocco.cloudmanager.service.api.IEventManager;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteEventManager;

@Stateless(mappedName = IEventManager.EJB_JNDI_NAME)
@Remote(IRemoteEventManager.class)
@Local(IEventManager.class)
public class EventManagerBean implements IEventManager {

    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(EventManagerBean.class.getName());

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<Event> findEvents(Date startTime, Date endTime, final int pageNumber, final int pageSize) {
        if (startTime == null) {
            startTime = new Date(0);
        }
        if (endTime == null) {
            endTime = new Date();
        }
        return this.em.createNamedQuery(Event.FIND_EVENTS).setParameter("startTime", startTime)
            .setParameter("endTime", endTime).setMaxResults(pageSize).setFirstResult(pageNumber * pageSize).getResultList();

    }

    public int countEvents(Date startTime, Date endTime) {
        if (startTime == null) {
            startTime = new Date(0);
        }
        if (endTime == null) {
            endTime = new Date();
        }
        return ((Long) this.em.createNamedQuery(Event.COUNT_EVENTS).setParameter("startTime", startTime)
            .setParameter("endTime", endTime).getSingleResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<Event> findEventsByProject(final String projectId, Date startTime, Date endTime, final int pageNumber,
        final int pageSize) {
        if (startTime == null) {
            startTime = new Date(0);
        }
        if (endTime == null) {
            endTime = new Date();
        }

        return this.em.createNamedQuery(Event.FIND_EVENTS_BY_PROJECT_ID).setParameter("projectId", projectId)
            .setParameter("startTime", startTime).setParameter("endTime", endTime).setMaxResults(pageSize)
            .setFirstResult(pageNumber * pageSize).getResultList();

    }

    public int countEventsByProject(final String projectId, Date startTime, Date endTime) {
        if (startTime == null) {
            startTime = new Date(0);
        }
        if (endTime == null) {
            endTime = new Date();
        }
        return ((Long) this.em.createNamedQuery(Event.COUNT_EVENTS_BY_PROJECT_ID).setParameter("projectId", projectId)
            .setParameter("startTime", startTime).setParameter("endTime", endTime).getSingleResult()).intValue();
    }

    public Event newEvent(final Event.Level level, final String description, final String projectId, final String objectId,
        final String type, String detail) {
        Event event = new Event();
        event.setLevel(level);
        event.setDescription(description);
        event.setProjectId(projectId);
        event.setObjectId(objectId);
        event.setType(type);
        if (detail != null) {
            detail = detail.substring(0, 79);
        }
        event.setDetail(detail);
        event.setTime(new Date());
        this.em.persist(event);
        this.em.flush();
        return event;
    }

}
