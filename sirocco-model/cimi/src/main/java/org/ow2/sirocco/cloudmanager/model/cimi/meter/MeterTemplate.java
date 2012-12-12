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
 *  $Id $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi.meter;

import java.io.Serializable;

import javax.persistence.Entity;

import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;

@Entity
// @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class MeterTemplate extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unidirectional
    protected CloudResource targetResource;

    protected MeterConfiguration meterConfiguration;

    public MeterConfiguration getMeterConfiguration() {
        return this.meterConfiguration;
    }

    public void setMeterConfiguration(final MeterConfiguration meterConfiguration) {
        this.meterConfiguration = meterConfiguration;
    }

    public CloudResource getTargetResource() {
        return this.targetResource;
    }

    public void setTargetResource(final CloudResource targetResource) {
        this.targetResource = targetResource;
    }

}