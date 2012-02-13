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

package org.ow2.sirocco.cloudmanager.provider.api.entity.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ow2.sirocco.cloudmanager.provider.api.entity.PerfMetric;

@SuppressWarnings("serial")
public class PerfMetricVO implements Serializable {

    private Date timeStamp;

    private float value;

    public static PerfMetricVO from(final PerfMetric m) {
        PerfMetricVO to = new PerfMetricVO();
        to.setTimeStamp(new Date(m.getTime()));
        to.setValue(m.getValue());
        return to;
    }

    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(final Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(final float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "PerfMetric [timeStamp=" + new SimpleDateFormat("EEEE d MMMM yyyy HH:mm:ss").format(this.getTimeStamp())
            + ", value=" + this.getValue() + "]";

    }
}
