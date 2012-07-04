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
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.CollectionOfElements;

import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;

@Entity
public class MeterConfiguration extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected List<String>    associatedTo;
    
    // TODO just a string
    
    protected String          aspect;
    
    protected String          units;
    
    protected Integer         sampleInterval;
    
    public static enum TimeScope {
        POINT, INTERVAL
    }
    
    protected TimeScope       timeScope;
    
    public static enum IntervalDuration {
        HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY
    }
    
    protected IntervalDuration    intervalDuration;
    
    protected boolean             isContinuous;

    @CollectionOfElements
    public List<String> getAssociatedTo() {
        return associatedTo;
    }

    public void setAssociatedTo(List<String> associatedTo) {
        this.associatedTo = associatedTo;
    }

    public String getAspect() {
        return aspect;
    }

    public void setAspect(String aspect) {
        this.aspect = aspect;
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
    public TimeScope getTimeScope() {
        return timeScope;
    }

    public void setTimeScope(TimeScope timeScope) {
        this.timeScope = timeScope;
    }
    @Enumerated(EnumType.STRING)
    public IntervalDuration getIntervalDuration() {
        return intervalDuration;
    }

    public void setIntervalDuration(IntervalDuration intervalDuration) {
        this.intervalDuration = intervalDuration;
    }

    public boolean isContinuous() {
        return isContinuous;
    }

    public void setContinuous(boolean isContinuous) {
        this.isContinuous = isContinuous;
    }
}