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
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLogTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLogCreate;

/**
 * Events management operations
 */
public interface IEventManager {

    static final String EJB_JNDI_NAME = "org.ow2.sirocco.cloudmanager.core.impl.EventManager_org.ow2.sirocco.cloudmanager.core.api.IRemoteEventManager@Remote";

    
    /** EventLog Template operations */
    EventLogTemplate createEventLogTemplate(EventLogTemplate eventLogTemplate) throws CloudProviderException, ResourceNotFoundException;

    void updateEventLogTemplate(EventLogTemplate eventLogTemplate) throws CloudProviderException, InvalidRequestException;

    EventLogTemplate getEventLogTemplateById(String eventLogTemplateId) throws CloudProviderException, ResourceNotFoundException;

    void deleteEventLogTemplate(String EventLogTemplateId) throws ResourceNotFoundException,
        CloudProviderException;

    void updateEventLogTemplateAttributes(String eventLogTemplateId, Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    List<EventLogTemplate> getEventLogTemplates(List<String> attributes, String filterExpression)
        throws InvalidRequestException, CloudProviderException;
    
    /** EventLog operations */
    EventLog createEventLog(EventLogCreate eventLogCreate) throws CloudProviderException;
    
    void deleteEventLog(String eventLogId) throws CloudProviderException, ResourceNotFoundException;
    
    void updateEventLog(String eventLogId, Map<String, Object> attributes) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException;
    
    List<Event> getEvents(String eventLogId) throws CloudProviderException;
    
    Event getEvent(String eventLogId, String eventId) throws CloudProviderException, ResourceNotFoundException;
    
    void deleteEvent(String eventLogId, String eventId) throws CloudProviderException, ResourceNotFoundException;
    
    // TODO CIMI spec says events are updateable???
}