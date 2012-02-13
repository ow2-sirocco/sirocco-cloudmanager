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

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "org.ow2.sirocco.cloudmanager.api.spec.PerfMetrics")
@XmlRootElement(name = "org.ow2.sirocco.cloudmanager.api.spec.PerfMetrics")
public class PerfMetrics {
    private Collection<PerfMetric> perfMetrics;

    public PerfMetrics() {

    }

    @Override
    public String toString() {
        String result = this.getClass().getName();
        if (this.perfMetrics == null) {
            result = result + ": null";
        } else {
            result = result + "[";
            for (PerfMetric pm_temp : this.perfMetrics) {
                result = result + pm_temp + "\n";
            }
            result = result + "]";
        }
        return result;
    }

    public void setPerfMetrics(final Collection<PerfMetric> perfMetrics) {
        this.perfMetrics = perfMetrics;
    }

    public Collection<PerfMetric> getPerfMetrics() {
        return this.perfMetrics;
    }
}
