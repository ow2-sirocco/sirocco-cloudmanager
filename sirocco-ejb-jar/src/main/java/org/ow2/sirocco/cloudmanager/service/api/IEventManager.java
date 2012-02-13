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

package org.ow2.sirocco.cloudmanager.service.api;

import java.util.Date;
import java.util.List;

import org.ow2.sirocco.cloudmanager.provider.api.entity.Event;

public interface IEventManager {
    static final String EJB_JNDI_NAME = "EventManagerBean";

    /**
     * @param startTime
     * @param endTime
     * @param pageNumber
     * @param pageSize
     * @return
     */
    List<Event> findEvents(Date startTime, Date endTime, final int pageNumber, final int pageSize);

    /**
     * @param startTime
     * @param endTime
     * @return
     */
    int countEvents(Date startTime, Date endTime);

    /**
     * @param projectId
     * @param startTime
     * @param endTime
     * @param pageNumber
     * @param pageSize
     * @return
     */
    List<Event> findEventsByProject(final String projectId, Date startTime, Date endTime, final int pageNumber,
        final int pageSize);

    /**
     * @param projectId
     * @param startTime
     * @param endTime
     * @return
     */
    int countEventsByProject(final String projectId, Date startTime, Date endTime);

    /**
     * @param level
     * @param description
     * @param projectId
     * @param objectId
     * @param type
     * @param detail
     * @return
     */
    Event newEvent(final Event.Level level, String description, final String projectId, final String objectId,
        final String type, final String detail);

}
