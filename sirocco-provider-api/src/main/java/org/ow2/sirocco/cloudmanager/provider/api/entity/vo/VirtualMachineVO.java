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
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;

/**
 * Business object representing a virtual machine created by a user of the cloud
 * service. Note that depending on the status of the virtual machine, not all
 * fields of the virtual machine will be initialized. For example, the IP
 * address and DNS name of the virtual machine are set only when the VM is
 * running.
 */
@SuppressWarnings("serial")
public class VirtualMachineVO implements Serializable {

    private String id;

    private Machine.State status;

    private String name;

    private String providerId;

    private String username;

    private String userPublicKey;

    private String userEMail;

    private long memoryMB;

    private long storageMB;

    private int numCpus;

    private String publicIp;

    private String publicDnsName;

    private String privateIp;

    private String privateDnsName;

    private String machine;

    private String imageName;

    private Integer imageVmiId;

    private String imageOsType;

    private Date creationDate;

    private Date expirationDate;

    private Date deleteDate;

    private CloudProviderLocation location;

    private String hostId;

    private String projectId;

    private String projectName;

    private Map<String, String> additionalProperties;

    private Integer reservationId;

    private Integer systemInstanceId;

    private Integer virtualMachineTemplate;

    // private Collection<Volume> volumes;

    private String cloudProviderAccountId;

    /**
     * Returns the status of the virtual machine
     * 
     * @return the status of the virtual machine
     */
    public Machine.State getStatus() {
        return this.status;
    }

    /**
     * Sets the status of the virtual machine
     * 
     * @param status the status of the virtual machine
     */
    public void setStatus(final Machine.State status) {
        this.status = status;
    }

    /**
     * Returns the symbolic name of the virtual machine
     * 
     * @return the symbolic name of the virtual machine
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the symbolic name of the virtual machine
     * 
     * @param name the symbolic name of the virtual machine
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the hypervisor-assigned providerId of the virtual machine
     * 
     * @return the hypervisor-assigned providerId of the virtual machine
     */
    public String getProviderId() {
        return this.providerId;
    }

    /**
     * Sets the hypervisor-assigned providerId of the virtual machine
     * 
     * @param providerId the hypervisor-assigned providerId of the virtual
     *        machine
     */
    public void setProviderId(final String providerId) {
        this.providerId = providerId;
    }

    /**
     * Returns the username of the user this virtual machine belongs to
     * 
     * @return the username of the user this virtual machine belongs to
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the username of the user this virtual machine belongs to
     * 
     * @param username the username of the user this virtual machine belongs to
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Returns the memory capacity of the virtual machine in megabytes
     * 
     * @return the memory capacity of the virtual machine in megabytes
     */
    public long getMemoryMB() {
        return this.memoryMB;
    }

    /**
     * Sets the memory capacity of the virtual machine in megabytes
     * 
     * @param memoryMB the memory capacity of the virtual machine in megabytes
     */
    public void setMemoryMB(final long memoryMB) {
        this.memoryMB = memoryMB;
    }

    /**
     * Returns the storage capacity of the virtual machine in megabytes
     * 
     * @return the memory capacity of the virtual machine in megabytes
     */
    public long getStorageMB() {
        return this.storageMB;
    }

    /**
     * Sets the memory capacity of the virtual machine in megabytes
     * 
     * @param storageMB the memory capacity of the virtual machine in megabytes
     */
    public void setStorageMB(final long storageMB) {
        this.storageMB = storageMB;
    }

    /**
     * Returns the number of CPUs of the virtual machine
     * 
     * @return the number of CPUs of the virtual machine
     */
    public int getNumCpus() {
        return this.numCpus;
    }

    /**
     * Sets the number of CPUs of the virtual machine
     * 
     * @param numCpus the number of CPUs of the virtual machine
     */
    public void setNumCpus(final int numCpus) {
        this.numCpus = numCpus;
    }

    /**
     * Returns the IP address assigned to the virtual machine
     * 
     * @return the IP address assigned to the virtual machine
     */
    public String getPrivateIp() {
        return this.privateIp;
    }

    /**
     * Sets the IP address assigned to the virtual machine
     * 
     * @param ip the IP address assigned to the virtual machine
     */
    public void setPrivateIp(final String privateIp) {
        this.privateIp = privateIp;
    }

    /**
     * Returns the DNS name assigned to the virtual machine
     * 
     * @return the DNS name assigned to the virtual machine
     */
    public String getPrivateDnsName() {
        return this.privateDnsName;
    }

    /**
     * Sets the DNS name assigned to the virtual machine
     * 
     * @param dnsName the DNS name assigned to the virtual machine
     */
    public void setPrivateDnsName(final String privateDnsName) {
        this.privateDnsName = privateDnsName;
    }

    public String getPublicIp() {
        return this.publicIp;
    }

    public void setPublicIp(final String publicIp) {
        this.publicIp = publicIp;
    }

    public String getPublicDnsName() {
        return this.publicDnsName;
    }

    public void setPublicDnsName(final String publicDnsName) {
        this.publicDnsName = publicDnsName;
    }

    public String getMachine() {
        return this.machine;
    }

    public void setMachine(final String machine) {
        this.machine = machine;
    }

    /**
     * Returns the date of creation of the virtual machine
     * 
     * @return the date of creation of the virtual machine
     */
    public Date getCreationDate() {
        return this.creationDate;
    }

    /**
     * Sets the date of creation of the virtual machine
     * 
     * @param creationDate the date of creation of the virtual machine
     */
    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Returns the location of the virtual machine
     * 
     * @return the location of the virtual machine
     */
    public CloudProviderLocation getLocation() {
        return this.location;
    }

    /**
     * Sets the location of the virtual machine
     * 
     * @param cloudProviderLocation the location of the virtual machine
     */
    public void setLocation(final CloudProviderLocation cloudProviderLocation) {
        this.location = cloudProviderLocation;
    }

    /**
     * Returns the vmId of the host containing this VM
     * 
     * @return the vmId of the host containing this VM
     */
    public String getHostId() {
        return this.hostId;
    }

    /**
     * Sets the vmId of the host containing this VM
     * 
     * @param hostId vmId of the host containing this VM
     */
    public void setHostId(final String hostId) {
        this.hostId = hostId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setImageName(final String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return this.imageName;
    }

    /**
     * Sets the VM image vmId of the virtual machine
     * 
     * @param imageVmiId the VM image vmId of the virtual machine
     */
    public void setImageVmiId(final Integer imageVmiId) {
        this.imageVmiId = imageVmiId;
    }

    /**
     * Returns the VMImage vmId of the virtual machine
     * 
     * @return the VMImage vmId of the virtual machine
     */
    public Integer getImageVmiId() {
        return this.imageVmiId;
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

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public void setDeleteDate(final Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    public Date getDeleteDate() {
        return this.deleteDate;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getUserPublicKey() {
        return this.userPublicKey;
    }

    public void setUserPublicKey(final String userPublicKey) {
        this.userPublicKey = userPublicKey;
    }

    public String getUserEMail() {
        return this.userEMail;
    }

    public void setUserEMail(final String userEMail) {
        this.userEMail = userEMail;
    }

    public String getImageOsType() {
        return this.imageOsType;
    }

    public void setImageOsType(final String imageOsType) {
        this.imageOsType = imageOsType;
    }

    public Integer getSystemInstanceId() {
        return this.systemInstanceId;
    }

    public void setSystemInstanceId(final Integer systemInstanceId) {
        this.systemInstanceId = systemInstanceId;
    }

    public Integer getVirtualMachineTemplate() {
        return this.virtualMachineTemplate;
    }

    public void setVirtualMachineTemplate(final Integer virtualMachineTemplate) {
        this.virtualMachineTemplate = virtualMachineTemplate;
    }

    public String getCloudProviderAccountId() {
        return this.cloudProviderAccountId;
    }

    public void setCloudProviderAccountId(final String cloudProviderAccountId) {
        this.cloudProviderAccountId = cloudProviderAccountId;
    }

    @Override
    public String toString() {
        String s = this.getClass().getSimpleName() + "=[id=" + this.getId() + ", status=" + this.getStatus() + ", name="
            + this.getName() + ", providerId=" + this.getProviderId() + "]";
        return s;
    }

}
