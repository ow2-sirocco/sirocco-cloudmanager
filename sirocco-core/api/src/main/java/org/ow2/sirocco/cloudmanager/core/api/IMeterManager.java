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
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.Meter;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.MeterConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.MeterCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.MeterSample;
import org.ow2.sirocco.cloudmanager.model.cimi.meter.MeterTemplate;

/**
 * Events management operations
 */
public interface IMeterManager {

    static final String EJB_JNDI_NAME = "java:global/sirocco/sirocco-core/MeterManager!org.ow2.sirocco.cloudmanager.core.api.IRemoteMeterManager";

    /** Meter configuration */
    MeterConfiguration createMeterConfiguration(MeterConfiguration meterConfiguration) throws CloudProviderException;

    void deleteMeterConfiguration(final String meterConfigId) throws CloudProviderException, ResourceNotFoundException;

    MeterConfiguration getMeterConfiguration(final String meterConfigId) throws CloudProviderException,
        ResourceNotFoundException;

    QueryResult<MeterConfiguration> getMeterConfigurations(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    void updateMeterConfiguration(final String meterConfigId, Map<String, Object> attributes) throws CloudProviderException,
        InvalidRequestException, ResourceNotFoundException;

    /** Meter Template operations */
    MeterTemplate createMeterTemplate(MeterTemplate meterTemplate) throws CloudProviderException, ResourceNotFoundException;

    void updateMeterTemplate(MeterTemplate meterTemplate) throws CloudProviderException, InvalidRequestException;

    MeterTemplate getMeterTemplateById(String meterTemplateId) throws CloudProviderException, ResourceNotFoundException;

    void deleteMeterTemplate(String meterTemplateId) throws ResourceNotFoundException, CloudProviderException;

    void updateMeterTemplateAttributes(String meterTemplateId, Map<String, Object> attributes)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException;

    QueryResult<MeterTemplate> getMeterTemplates(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    /** Meter operations */
    /** create meter at the global collection */
    Job createMeter(MeterCreate meterCreate) throws CloudProviderException;

    /** Create meter at the resource specific collection */
    Job createMeter(MeterCreate meterCreate, String cloudResourceId) throws CloudProviderException;

    Job deleteMeter(String meterId) throws CloudProviderException, ResourceNotFoundException;

    void updateMeter(String meterId, Map<String, Object> attributes) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException;

    QueryResult<Meter> getMeters(int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    /** get meters of specific cloud resource */

    QueryResult<Meter> getMeters(String cloudResourceId, int first, int last, List<String> filters, List<String> attributes)
        throws InvalidRequestException, CloudProviderException;

    Meter getMeter(String cloudResourceId, String meterId) throws CloudProviderException, ResourceNotFoundException;

    Job deleteMeter(String cloudResourceId, String meterId) throws CloudProviderException, ResourceNotFoundException;

    List<MeterSample> getMeterSamples(String meterId) throws CloudProviderException, ResourceNotFoundException;

    List<MeterSample> getMeterSamples(String cloudResourceId, String meterId) throws CloudProviderException,
        ResourceNotFoundException;

    Job startMeter(String cloudResourceId, String meterId) throws CloudProviderException, ResourceNotFoundException;

    Job stopMeter(String cloudResourceId, String meterId) throws CloudProviderException, ResourceNotFoundException;
}
