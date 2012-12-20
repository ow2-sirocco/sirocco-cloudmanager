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

import org.ow2.sirocco.cloudmanager.core.api.IMeterManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMeterManager;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.Meter;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.MeterConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.MeterCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.MeterSample;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.MeterTemplate;

@Stateless
@Remote(IRemoteMeterManager.class)
@Local(IMeterManager.class)
public class MeterManager implements IMeterManager {

    @Override
    public MeterConfiguration createMeterConfiguration(final MeterConfiguration meterConfiguration)
        throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteMeterConfiguration(final String meterConfigId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    public MeterConfiguration getMeterConfiguration(final String meterConfigId) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QueryResult<MeterConfiguration> getMeterConfigurations(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateMeterConfiguration(final String meterConfigId, final Map<String, Object> attributes)
        throws CloudProviderException, InvalidRequestException, ResourceNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    public MeterTemplate createMeterTemplate(final MeterTemplate meterTemplate) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateMeterTemplate(final MeterTemplate meterTemplate) throws CloudProviderException, InvalidRequestException {
        // TODO Auto-generated method stub

    }

    @Override
    public MeterTemplate getMeterTemplateById(final String meterTemplateId) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteMeterTemplate(final String meterTemplateId) throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateMeterTemplateAttributes(final String meterTemplateId, final Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public QueryResult<MeterTemplate> getMeterTemplates(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job createMeter(final MeterCreate meterCreate) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job createMeter(final MeterCreate meterCreate, final String cloudResourceId) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job deleteMeter(final String meterId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateMeter(final String meterId, final Map<String, Object> attributes) throws ResourceNotFoundException,
        InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public QueryResult<Meter> getMeters(final int first, final int last, final List<String> filters,
        final List<String> attributes) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QueryResult<Meter> getMeters(final String cloudResourceId, final int first, final int last,
        final List<String> filters, final List<String> attributes) throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Meter getMeter(final String cloudResourceId, final String meterId) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job deleteMeter(final String cloudResourceId, final String meterId) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MeterSample> getMeterSamples(final String meterId) throws CloudProviderException, ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MeterSample> getMeterSamples(final String cloudResourceId, final String meterId) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job startMeter(final String cloudResourceId, final String meterId) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job stopMeter(final String cloudResourceId, final String meterId) throws CloudProviderException,
        ResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

}
