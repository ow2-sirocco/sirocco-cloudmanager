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
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "org.ow2.sirocco.cloudmanager.api.spec.ReservationInfo", propOrder = {"id", "resourcesReservationState",
    "projectId", "startTime", "endTime", "reservedNumberOfCPUCore", "reservedMemorySizeInMB", "reservedStorageSizeInMB",
    "freeNumberOfCpuCores", "freeMemorySizeInMB", "freeStorageSizeInMB", "hypervisorType", "location"})
@XmlRootElement(name = "org.ow2.sirocco.cloudmanager.api.spec.ReservationInfo")
public class ReservationInfo {

    /**
     * C'est le reservation Id
     */
    private int id;

    private String resourcesReservationState;

    private String projectId;

    /**
     * startTime is a real Calendar object. The one given by the user.
     */
    private Calendar startTime;

    /**
     * endTime is a real Calendar object. The one given by the user.
     */
    private Calendar endTime;

    /**
     * Number of CPU core(s) reserved.
     */
    private Integer reservedNumberOfCPUCore;

    /**
     * Memeory size in MB/Mo reserved.
     */
    private Integer reservedMemorySizeInMB;

    /**
     * Storage size in MB/Mo reserved.
     */
    private Integer reservedStorageSizeInMB;

    /**
     * Remaining number of CPU Cores.
     */
    private Integer freeNumberOfCpuCores;

    /**
     * Remaining memory in MB.
     */
    private Integer freeMemorySizeInMB;

    /**
     * Remaining storage in MB.
     */
    private Integer freeStorageSizeInMB;

    private String hypervisorType;

    private String location;

    public ReservationInfo() {
    }

    public final int getId() {
        return this.id;
    }

    public final void setId(final int id) {
        this.id = id;
    }

    public final String getResourcesReservationState() {
        return this.resourcesReservationState;
    }

    public final void setResourcesReservationState(final String resourcesReservationState) {
        this.resourcesReservationState = resourcesReservationState;
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

    public final Integer getReservedNumberOfCPUCore() {
        return this.reservedNumberOfCPUCore;
    }

    public final void setReservedNumberOfCPUCore(final Integer reservedNumberOfCPUCore) {
        this.reservedNumberOfCPUCore = reservedNumberOfCPUCore;
    }

    public final Integer getReservedMemorySizeInMB() {
        return this.reservedMemorySizeInMB;
    }

    public final void setReservedMemorySizeInMB(final Integer reservedMemorySizeInMB) {
        this.reservedMemorySizeInMB = reservedMemorySizeInMB;
    }

    public final Integer getReservedStorageSizeInMB() {
        return this.reservedStorageSizeInMB;
    }

    public final void setReservedStorageSizeInMB(final Integer reservedStorageSizeInMB) {
        this.reservedStorageSizeInMB = reservedStorageSizeInMB;
    }

    public final Integer getFreeNumberOfCpuCores() {
        return this.freeNumberOfCpuCores;
    }

    public final void setFreeNumberOfCpuCores(final Integer freeNumberOfCpuCores) {
        this.freeNumberOfCpuCores = freeNumberOfCpuCores;
    }

    public final Integer getFreeMemorySizeInMB() {
        return this.freeMemorySizeInMB;
    }

    public final void setFreeMemorySizeInMB(final Integer freeMemorySizeInMB) {
        this.freeMemorySizeInMB = freeMemorySizeInMB;
    }

    public final Integer getFreeStorageSizeInMB() {
        return this.freeStorageSizeInMB;
    }

    public final void setFreeStorageSizeInMB(final Integer freeStorageSizeInMB) {
        this.freeStorageSizeInMB = freeStorageSizeInMB;
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
        return this.getClass().getName() + " [id=" + this.getId() + ", resourcesReservationState="
            + this.getResourcesReservationState() + ", projectId=" + this.getProjectId() + ", startTime="
            + this.getStartTime().getTime() + ", endTime=" + this.getEndTime().getTime() + ", reservedNumberOfCPUCore="
            + this.getReservedNumberOfCPUCore() + ", reservedMemorySizeInMB=" + this.getReservedMemorySizeInMB()
            + ", reservedStorageSizeInMB=" + this.getReservedStorageSizeInMB() + ", freeNumberOfCpuCores="
            + this.getFreeNumberOfCpuCores() + ", freeMemorySizeInMB=" + this.getFreeMemorySizeInMB()
            + ", freeStorageSizeInMB=" + this.getFreeStorageSizeInMB() + ", hypervisorType=" + this.getHypervisorType()
            + ", location=" + this.getLocation() + "]";
    }

}
