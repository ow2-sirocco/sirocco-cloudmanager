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

package org.ow2.sirocco.cloudmanager.provider.api.entity;

import java.util.Date;

/**
 * Performance metric info type
 */
public class PerfMetricInfo {
    /**
     * Unit type
     */
    public enum Unit {
        /**
         * A quantity of items (for example the number of CPUs)
         */
        NUMBER,
        /**
         * Percentage
         */
        PERCENT,
        /**
         * The time in milliseconds
         */
        MILLISECOND,
        /**
         * The time in seconds
         */
        SECOND,
        /**
         * bytes per second
         */
        BYTES_PER_SEC,
        /**
         * Kilobytes per second
         */
        KILOBYTES_PER_SEC, OPERATIONS_PER_SEC, PACKETS_PER_SEC, ERRORS_PER_SEC,
        /**
         * Kilobits per second
         */
        KILOBITS_PER_SEC,
        /**
         * Kilobytes
         */
        KILOBYTES,
        /**
         * Megabytes
         */
        MEGABYTES;

        public String value() {
            return this.name();
        }

        public static Unit fromValue(final String v) {
            for (Unit u : Unit.values()) {
                if (v.equals(u.value())) {
                    return u;
                }
            }
            return null;
        }
    }

    private String name;

    private String description;

    private Date startTime;

    private Unit unit;

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

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }

    public Unit getUnit() {
        return this.unit;
    }

    public void setUnit(final Unit unit) {
        this.unit = unit;
    }

}
