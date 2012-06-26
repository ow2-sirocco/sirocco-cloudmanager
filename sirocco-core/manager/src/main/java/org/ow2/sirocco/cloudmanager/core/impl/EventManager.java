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

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.ow2.sirocco.cloudmanager.core.api.IEventManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteEventManager;
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
public class EventManager implements IEventManager {

    @Override
    public EventLogTemplate createEventLogTemplate(EventLogTemplate eventLogTemplate) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateEventLogTemplate(EventLogTemplate eventLogTemplate) throws CloudProviderException,
        InvalidRequestException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public EventLogTemplate getEventLogTemplateById(String eventLogTemplateId) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteEventLogTemplate(String EventLogTemplateId) throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateEventLogTemplateAttributes(String eventLogTemplateId, Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<EventLogTemplate> getEventLogTemplates(List<String> attributes, String filterExpression)
        throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EventLog createEventLog(EventLogCreate eventLogCreate) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteEventLog(String eventLogId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateEventLog(String eventLogId, Map<String, Object> attributes) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Event> getEvents(String eventLogId) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Event getEvent(String eventLogId, String eventId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteEvent(String eventLogId, String eventId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        
    }
    
}


