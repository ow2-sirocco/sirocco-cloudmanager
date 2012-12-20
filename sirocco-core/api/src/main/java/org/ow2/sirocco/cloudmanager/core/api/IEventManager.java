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

package org.ow2.sirocco.cloudmanager.core.api;

import java.util.List;
import java.util.Map;

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.event.Event;
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLog;
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLogCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLogTemplate;

/**
 * Events management operations
 */
public interface IEventManager {

    static final String EJB_JNDI_NAME = "java:global/sirocco/sirocco-core/EventManager!org.ow2.sirocco.cloudmanager.core.api.IRemoteEventManager";

    /** EventLog Template operations */
    EventLogTemplate createEventLogTemplate(EventLogTemplate eventLogTemplate) throws CloudProviderException,
        ResourceNotFoundException;

    void updateEventLogTemplate(EventLogTemplate eventLogTemplate) throws CloudProviderException, InvalidRequestException;

    EventLogTemplate getEventLogTemplateById(String eventLogTemplateId) throws CloudProviderException,
        ResourceNotFoundException;

    void deleteEventLogTemplate(String EventLogTemplateId) throws ResourceNotFoundException, CloudProviderException;

    void updateEventLogTemplateAttributes(String eventLogTemplateId, Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    QueryResult<EventLogTemplate> getEventLogTemplates(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    List<EventLogTemplate> getEventLogTemplates() throws CloudProviderException;

    /** EventLog operations */
    EventLog createEventLog(EventLogCreate eventLogCreate) throws CloudProviderException;

    void deleteEventLog(String eventLogId) throws CloudProviderException, ResourceNotFoundException;

    void updateEventLogAttributes(String eventLogId, Map<String, Object> attributes) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;

    void updateEventLog(EventLog eventLog) throws InvalidRequestException, CloudProviderException;

    QueryResult<EventLog> getEventLog(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    List<EventLog> getEventLog() throws CloudProviderException;

    EventLog getEventLogById(String eventLogId) throws ResourceNotFoundException, CloudProviderException;

    List<Event> getEvents(String eventLogId) throws CloudProviderException;

    QueryResult<Event> getEvents(String eventLogId, int first, int last, List<String> filter, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    Event getEventFromEventLog(String eventLogId, String eventId) throws CloudProviderException, ResourceNotFoundException;

    void removeEventFromEventLog(String eventLogId, String eventId) throws CloudProviderException, ResourceNotFoundException;

    Event getEvent(String eventId) throws InvalidRequestException, CloudProviderException;

    void deleteEvent(String eventId) throws InvalidRequestException, CloudProviderException;
    // TODO CIMI spec says events are updateable???
}