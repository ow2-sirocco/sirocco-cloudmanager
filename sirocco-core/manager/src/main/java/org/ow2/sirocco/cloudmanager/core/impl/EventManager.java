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

import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.ow2.sirocco.cloudmanager.core.api.IEventManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteEventManager;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.event.Event;
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLog;
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLogCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLogTemplate;

@Stateless
@Remote(IRemoteEventManager.class)
@Local(IEventManager.class)
@IdentityInterceptorBinding
public class EventManager implements IEventManager {

    @Override
    public EventLogTemplate createEventLogTemplate(final EventLogTemplate eventLogTemplate) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateEventLogTemplate(final EventLogTemplate eventLogTemplate) throws CloudProviderException,
        InvalidRequestException {
        // TODO Auto-generated method stub

    }

    @Override
    public EventLogTemplate getEventLogTemplateById(final String eventLogTemplateId) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteEventLogTemplate(final String EventLogTemplateId) throws ResourceNotFoundException,
        CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateEventLogTemplateAttributes(final String eventLogTemplateId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public QueryResult<EventLogTemplate> getEventLogTemplates(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EventLogTemplate> getEventLogTemplates() throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QueryResult<EventLog> getEventLog(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EventLog> getEventLog() throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EventLog getEventLogById(final String eventLogId) throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QueryResult<Event> getEvents(final String eventLogId, final int first, final int last, final List<String> filter,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EventLog createEventLog(final EventLogCreate eventLogCreate) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteEventLog(final String eventLogId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateEventLogAttributes(final String eventLogId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateEventLog(final EventLog eventLog) throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Event> getEvents(final String eventLogId) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Event getEventFromEventLog(final String eventLogId, final String eventId) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeEventFromEventLog(final String eventLogId, final String eventId) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    public Event getEvent(final String eventId) throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteEvent(final String eventId) throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub

    }

}
