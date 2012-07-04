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
 *  $Id: $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi.meter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Column;

import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;

@Entity
public class Meter extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    protected CloudResource   targetResource;
    
    protected String          units;
    
    protected Integer         sampleInterval;
    
   
    protected MeterConfiguration.TimeScope       timeScope;
    
    
    protected MeterConfiguration.IntervalDuration    intervalDuration;
    
    protected boolean             isContinuous;
    
    protected List<MeterSample>   samples;
    
    protected String              minValue;
    
    protected String              maxValue;
    
    protected Date              stopTime;
    
    protected Date              expiresTime;

    @ManyToOne
    public CloudResource getTargetResource() {
        return targetResource;
    }

    public void setTargetResource(CloudResource targetResource) {
        this.targetResource = targetResource;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Integer getSampleInterval() {
        return sampleInterval;
    }

    public void setSampleInterval(Integer sampleInterval) {
        this.sampleInterval = sampleInterval;
    }
    @Enumerated(EnumType.STRING)
    public MeterConfiguration.TimeScope getTimeScope() {
        return timeScope;
    }

    public void setTimeScope(MeterConfiguration.TimeScope timeScope) {
        this.timeScope = timeScope;
    }
    @Enumerated(EnumType.STRING)
    public MeterConfiguration.IntervalDuration getIntervalDuration() {
        return intervalDuration;
    }

    public void setIntervalDuration(MeterConfiguration.IntervalDuration intervalDuration) {
        this.intervalDuration = intervalDuration;
    }

    public boolean isContinuous() {
        return isContinuous;
    }

    public void setContinuous(boolean isContinuous) {
        this.isContinuous = isContinuous;
    }

    @OneToMany
    public List<MeterSample> getSamples() {
        return samples;
    }

    public void setSamples(List<MeterSample> samples) {
        this.samples = samples;
    }

    @Column(name="minValueMeter")
    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }
    
    @Column(name="maxValueMeter")
    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(Date expiresTime) {
        this.expiresTime = expiresTime;
    }
}