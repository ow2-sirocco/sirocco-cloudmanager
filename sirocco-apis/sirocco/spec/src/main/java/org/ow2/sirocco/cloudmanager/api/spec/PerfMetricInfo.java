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

package org.ow2.sirocco.cloudmanager.api.spec;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "org.ow2.sirocco.cloudmanager.api.spec.PerfMetricInfo", propOrder = {"id", "name", "description", "unit",
    "starttime", "endtime", "expirationtime", "typeSample", "intervalSample"})
@XmlRootElement(name = "org.ow2.sirocco.cloudmanager.api.spec.PerfMetricInfo")
public class PerfMetricInfo {

    private String id;

    private String name;

    private String description;

    private String unit;

    private Date starttime;

    private Date endtime;

    private Date expirationtime;

    private String typeSample; // value's type (average, max, min)

    private Long intervalSample;

    public void setId(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public Date getStarttime() {
        return this.starttime;
    }

    public void setStarttime(final Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return this.endtime;
    }

    public void setEndtime(final Date endtime) {
        this.endtime = endtime;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }

    public void setExpirationtime(final Date expirationtime) {
        this.expirationtime = expirationtime;
    }

    public Date getExpirationtime() {
        return this.expirationtime;
    }

    public void setIntervalSample(final Long intervalSample) {
        this.intervalSample = intervalSample;
    }

    public Long getIntervalSample() {
        return this.intervalSample;
    }

    public void setTypeSample(final String typeSample) {
        this.typeSample = typeSample;
    }

    public String getTypeSample() {
        return this.typeSample;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " [id=" + this.getId() + ", name=" + this.getName() + ", description="
            + this.getDescription() + ", unit=" + this.getUnit() + ", startTime=" + this.getStarttime() + ", endTime="
            + this.getEndtime() + ", expirationTime=" + this.getTypeSample() + ", intervalSample=" + this.getIntervalSample()
            + "]";

    }

}
