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

import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "org.ow2.sirocco.cloudmanager.api.spec.ReservationSpec")
public class ReservationSpec {

    private String projectId;

    private Calendar startTime;

    private Calendar endTime;

    private Integer numberOfCPUCore;

    private Integer memorySizeInMB;

    private Integer storageSizeInMB;

    private String hypervisorType;

    private String location;

    public ReservationSpec() {

    }

    public final String getProjectId() {
        return this.projectId;
    }

    public final void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public final Calendar getStartTime() {
        return this.startTime;
    }

    public final void setStartTime(final Calendar startTime) {
        this.startTime = startTime;
    }

    public final Calendar getEndTime() {
        return this.endTime;
    }

    public final void setEndTime(final Calendar endTime) {
        this.endTime = endTime;
    }

    public final Integer getNumberOfCPUCore() {
        return this.numberOfCPUCore;
    }

    public final void setNumberOfCPUCore(final Integer numberOfCPUCore) {
        this.numberOfCPUCore = numberOfCPUCore;
    }

    public final Integer getMemorySizeInMB() {
        return this.memorySizeInMB;
    }

    public final void setMemorySizeInMB(final Integer memorySizeInMB) {
        this.memorySizeInMB = memorySizeInMB;
    }

    public final Integer getStorageSizeInMB() {
        return this.storageSizeInMB;
    }

    public final void setStorageSizeInMB(final Integer storageSizeInMB) {
        this.storageSizeInMB = storageSizeInMB;
    }

    public final String getHypervisorType() {
        return this.hypervisorType;
    }

    public final void setHypervisorType(final String hypervisorType) {
        this.hypervisorType = hypervisorType;
    }

    public final String getLocation() {
        return this.location;
    }

    public final void setLocation(final String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " [projectId=" + this.getProjectId() + " startTime=" + this.getStartTime()
            + " endTime=" + this.getEndTime() + " numberOfCPUCore=" + this.getNumberOfCPUCore() + " memorySizeInMB="
            + this.getMemorySizeInMB() + " storageSizeInMB=" + this.getStorageSizeInMB() + " hypervisorType="
            + this.getHypervisorType() + " location=" + this.getLocation() + "]";
    }

}
