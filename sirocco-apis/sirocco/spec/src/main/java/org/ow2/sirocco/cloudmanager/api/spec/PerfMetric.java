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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "org.ow2.sirocco.cloudmanager.api.spec.PerfMetric", propOrder = {"timeStamp", "value"})
@XmlRootElement(name = "org.ow2.sirocco.cloudmanager.api.spec.PerfMetric")
public class PerfMetric {
    private Date timeStamp;

    private float value;

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
        return this.getClass().getName() + " [timeStamp="
            + new SimpleDateFormat("EEEE d MMMM yyyy HH:mm:ss").format(this.getTimeStamp()) + ", value=" + this.getValue()
            + "]";

    }
}
