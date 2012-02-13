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
import java.util.Date;
import java.util.Map;

import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;

/**
 * Utility class that contains the specification of a virtual machine to create.
 * The resource capacity of a virtual machine can be specified in two ways:
 * <ul>
 * <li>with explicit disk, memory and number of CPUs properties
 * <li>with a VM size
 * </ul>
 * If the sizeName attribute of a VirtualMachineSpec instance is set, the
 * memorySizeMB, diskSizeMB and numCPUs properties are ignored.
 * 
 * @see VMSizeVO
 */
@SuppressWarnings("serial")
public class VirtualMachineSpec implements Serializable {

    private String vmName;

    private String sizeName;

    private int memorySizeMB;

    private Integer diskSizeMB;

    private int numCPUs;

    // TODO to be moved from int to String.
    private int imageId;

    private CloudProviderLocation cloudProviderLocation;

    private String dnsName;

    private Date expirationDate;

    private Map<String, String> userData;

    private Map<String, String> networkParameters;

    private Map<String, String> additionalProperties;

    private Integer reservationId;

    private long speedInMHz;

    /**
     * Returns the symbolic name of the virtual machine
     * 
     * @return the symbolic name of the virtual machine
     */
    public String getVmName() {
        return this.vmName;
    }

    /**
     * Sets the symbolic name of the virtual machine
     * 
     * @param vmName the symbolic name of the virtual machine
     */
    public void setVmName(final String vmName) {
        this.vmName = vmName;
    }

    /**
     * Returns the VM size name
     * 
     * @return the VM size name
     */
    public String getSizeName() {
        return this.sizeName;
    }

    /**
     * Sets the VM size name
     * 
     * @param sizeName the VM size name
     */
    public void setSizeName(final String sizeName) {
        this.sizeName = sizeName;
    }

    /**
     * Returns the VM's memory size in megabytes
     * 
     * @return the memory size in megabytes
     */
    public int getMemorySizeMB() {
        return this.memorySizeMB;
    }

    /**
     * Sets the VM's memory size in megabytes
     * 
     * @param memorySizeMB
     */
    public void setMemorySizeMB(final int memorySizeMB) {
        this.memorySizeMB = memorySizeMB;
    }

    /**
     * Returns the disk size in megabytes
     * 
     * @return the disk size in megabytes
     */
    public Integer getDiskSizeMB() {
        return this.diskSizeMB;
    }

    /**
     * Sets the disk size in megabytes
     * 
     * @param diskSizeMB the disk size in megabytes
     */
    public void setDiskSizeMB(final Integer diskSizeMB) {
        this.diskSizeMB = diskSizeMB;
    }

    /**
     * Returns the VM's number of CPUs
     * 
     * @return the number of CPUs
     */
    public int getNumCPUs() {
        return this.numCPUs;
    }

    /**
     * Sets the VM's number of CPUs
     * 
     * @param numCPUs the number of CPUs
     */
    public void setNumCPUs(final int numCPUs) {
        this.numCPUs = numCPUs;
    }

    /**
     * Returns the VM image ID
     * 
     * @return the VM image ID
     */
    public int getImageId() {
        return this.imageId;
    }

    /**
     * Sets the VM image ID
     * 
     * @param imageId the VM image ID
     */
    public void setImageId(final int imageId) {
        this.imageId = imageId;
    }

    /**
     * Returns the location of the VM
     * 
     * @return the location of the VM
     */
    public CloudProviderLocation getCloudProviderLocation() {
        return this.cloudProviderLocation;
    }

    /**
     * Sets the location of the VM
     * 
     * @param cloudProviderLocation the location of the VM
     */
    public void setCloudProviderLocation(final CloudProviderLocation cloudProviderLocation) {
        this.cloudProviderLocation = cloudProviderLocation;
    }

    /**
     * Sets the DNS name of the VM
     * 
     * @return DNS name of the VM
     */
    public String getDnsName() {
        return this.dnsName;
    }

    /**
     * Returns the DNS name of the VM
     * 
     * @param dNSName DNS name of the VM
     */
    public void setDnsName(final String dnsName) {
        this.dnsName = dnsName;
    }

    public Map<String, String> getUserData() {
        return this.userData;
    }

    public void setUserData(final Map<String, String> userData) {
        this.userData = userData;
    }

    public Map<String, String> getNetworkParameters() {
        return this.networkParameters;
    }

    public void setNetworkParameters(final Map<String, String> networkParameters) {
        this.networkParameters = networkParameters;
    }

    public void setExpirationDate(final Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getExpirationDate() {
        return this.expirationDate;
    }

    public void setAdditionalProperties(final Map<String, String> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public Map<String, String> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public final Integer getReservationId() {
        return this.reservationId;
    }

    public final void setReservationId(final Integer reservationId) {
        this.reservationId = reservationId;
    }

    public void setSpeedInMHz(final long speedInMHz) {
        this.speedInMHz = speedInMHz;
    }

    public long getSpeedInMHz() {
        return this.speedInMHz;
    }

    @Override
    public String toString() {
        String s = this.getClass().getSimpleName() + "=[vmName=" + this.getVmName() + ", sizeName=" + this.getSizeName()
            + ", memorySizeMB=" + this.getMemorySizeMB() + ", diskSizeMB=" + this.getDiskSizeMB() + ", numCPUs="
            + this.getNumCPUs() + ", imageId=" + this.getImageId() + ", cloudProviderLocation="
            + this.getCloudProviderLocation() + ", dnsName=" + this.getDnsName() + ", expirationDate="
            + this.getExpirationDate() + ", userData=" + this.getUserData() + ", networkParameters="
            + this.getNetworkParameters() + ", additionalProperties=" + this.getAdditionalProperties() + ", reservationId="
            + this.getReservationId() + ", speedInMHz=" + this.getSpeedInMHz() + "]";
        return s;
    }

}
