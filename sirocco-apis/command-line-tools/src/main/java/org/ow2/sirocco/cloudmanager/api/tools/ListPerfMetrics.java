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

package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.PerfMetric;
import org.ow2.sirocco.cloudmanager.api.spec.PerfMetricSpec;
import org.ow2.sirocco.cloudmanager.api.spec.PerfMetrics;
import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;

import com.beust.jcommander.Parameter;

public class ListPerfMetrics extends Client {
    @Parameter(names = "-target", description = "target", required = true)
    private String target;

    @Parameter(names = "-metricId", description = "metricId", required = true)
    private String metricId;

    @Parameter(names = "-startTime", description = "startTime", required = true)
    private String startTime;

    @Parameter(names = "-endTime", description = "endTime", required = true)
    private String endTime;

    public ListPerfMetrics() {
        this.commandName = "sirocco-metrics-list";
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        PerfMetrics metrics = null;
        if (this.target != null) {
            PerfMetricSpec pms = new PerfMetricSpec();
            pms.setTarget(this.target);
            pms.setMetricId(this.metricId);
            pms.setStartTime(this.startTime);
            pms.setEndTime(this.endTime);

            metrics = proxy.getPerfMetrics(pms);
        }

        if (metrics.getPerfMetrics() != null) {
            System.out.format("%-40s %-40s\n", "TimeStamp", "Value");
            for (PerfMetric metric : metrics.getPerfMetrics()) {
                System.out.format("%-40s %-40s\n", metric.getTimeStamp(), metric.getValue());
            }
        }
    }

    public static void main(final String[] args) {
        new ListPerfMetrics().run(args);
    }

}
