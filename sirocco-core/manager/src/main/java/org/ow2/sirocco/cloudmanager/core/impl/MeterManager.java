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
import org.ow2.sirocco.cloudmanager.core.api.IMeterManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMeterManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.Meter;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.MeterConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.MeterCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.MeterTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.MeterSample;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;

@Stateless
@Remote(IRemoteMeterManager.class)
@Local(IMeterManager.class)

public class MeterManager implements IMeterManager {

    @Override
    public MeterConfiguration createMeterConfiguration(MeterConfiguration meterConfiguration) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteMeterConfiguration(String meterConfigId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public MeterConfiguration getMeterConfiguration(String meterConfigId) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QueryResult<MeterConfiguration> getMeterConfigurations(int first, int last, List<String> filters, List<String> attributes) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateMeterConfiguration(String meterConfigId, Map<String, Object> attributes) throws CloudProviderException,
        InvalidRequestException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public MeterTemplate createMeterTemplate(MeterTemplate meterTemplate) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateMeterTemplate(MeterTemplate meterTemplate) throws CloudProviderException, InvalidRequestException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public MeterTemplate getMeterTemplateById(String meterTemplateId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteMeterTemplate(String meterTemplateId) throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateMeterTemplateAttributes(String meterTemplateId, Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public QueryResult<MeterTemplate> getMeterTemplates(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job createMeter(MeterCreate meterCreate) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job createMeter(MeterCreate meterCreate, String cloudResourceId) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job deleteMeter(String meterId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateMeter(String meterId, Map<String, Object> attributes) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public QueryResult<Meter> getMeters(int first, int last, List<String> filters, List<String> attributes) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QueryResult<Meter> getMeters(String cloudResourceId, int first, int last, List<String> filters, List<String> attributes) throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Meter getMeter(String cloudResourceId, String meterId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job deleteMeter(String cloudResourceId, String meterId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

   
    @Override
    public List<MeterSample> getMeterSamples(String meterId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MeterSample> getMeterSamples(String cloudResourceId, String meterId) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job startMeter(String cloudResourceId, String meterId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job stopMeter(String cloudResourceId, String meterId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
