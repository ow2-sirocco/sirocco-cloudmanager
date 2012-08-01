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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CollectionOfElements;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;

@Entity
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class MeterConfiguration extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    protected List<String> associatedTo;

    // TODO just a string

    protected String aspect;

    protected String units;

    protected Integer sampleInterval;

    public static enum TimeScope {
        POINT, INTERVAL
    }

    protected TimeScope timeScope;

    public static enum IntervalDuration {
        HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY
    }

    protected IntervalDuration intervalDuration;

    protected boolean isContinuous;

    @CollectionOfElements
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public List<String> getAssociatedTo() {
        return this.associatedTo;
    }

    public void setAssociatedTo(final List<String> associatedTo) {
        this.associatedTo = associatedTo;
    }

    public String getAspect() {
        return this.aspect;
    }

    public void setAspect(final String aspect) {
        this.aspect = aspect;
    }

    public String getUnits() {
        return this.units;
    }

    public void setUnits(final String units) {
        this.units = units;
    }

    public Integer getSampleInterval() {
        return this.sampleInterval;
    }

    public void setSampleInterval(final Integer sampleInterval) {
        this.sampleInterval = sampleInterval;
    }

    @Enumerated(EnumType.STRING)
    public TimeScope getTimeScope() {
        return this.timeScope;
    }

    public void setTimeScope(final TimeScope timeScope) {
        this.timeScope = timeScope;
    }

    @Enumerated(EnumType.STRING)
    public IntervalDuration getIntervalDuration() {
        return this.intervalDuration;
    }

    public void setIntervalDuration(final IntervalDuration intervalDuration) {
        this.intervalDuration = intervalDuration;
    }

    public boolean isContinuous() {
        return this.isContinuous;
    }

    public void setContinuous(final boolean isContinuous) {
        this.isContinuous = isContinuous;
    }
}