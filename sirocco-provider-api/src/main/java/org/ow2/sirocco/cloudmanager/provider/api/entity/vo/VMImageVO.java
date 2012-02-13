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

import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Visibility;

/**
 * Business object representing a VM image. A VM Image represents a prebuilt
 * disk image with a given software stack (operating system, middleware,
 * services...). The creation of a new virtual machine required the
 * specification of a VM image that will be used as a template to initialize the
 * disk of the VM. A VirtualMachineImage can have two levels of visibility:
 * <ul>
 * <li>a public VM image is made available by the cloud service and can be used
 * by any user
 * <li>a private VM image has been created by a specific user (by snapshotting
 * one of his VMs) and is visible by this user only
 * </ul>
 * In the former case, the username field of a the VMImageVO instance is unset.
 */
@SuppressWarnings("serial")
public class VMImageVO implements Serializable {

    private Integer id;

    private String name;

    private String osType;

    private String description;

    private String hypervisor;

    private String username;

    private String projectId;

    private String projectName;

    private MachineImage.State status;

    private Visibility visibility;

    private String architecture;

    private Integer diskSizeMB;

    private Integer imageSizeMB;

    private String cloudProviderAccountId;

    private String cloudProviderId;

    private String location;

    /**
     * Returns the symbolic name of the VM image
     * 
     * @return the symbolic name of the VM image
     */
    public String getName() {
        return this.name;
    }

    /**
     * the symbolic name of the VM image
     * 
     * @param name the symbolic name of the VM image
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the category of the VM image
     * 
     * @return the category of the VM image
     */
    public String getOsType() {
        return this.osType;
    }

    /**
     * Sets the category of the VM image
     * 
     * @param osType of the VM image
     */
    public void setOsType(final String osType) {
        this.osType = osType;
    }

    /**
     * Returns the description of the VM image
     * 
     * @return the description of the VM image
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the VM image
     * 
     * @param description the description of the VM image
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Returns the identifier of the VM image
     * 
     * @return the identifier of the VM image
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the identifier of the VM image
     * 
     * @param id the identifier of the VM image
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    /**
     * Returns the hypervisor this image is compatible with
     * 
     * @return the hypervisor this image is compatible with
     */
    public String getHypervisor() {
        return this.hypervisor;
    }

    /**
     * Sets the hypervisor this image is compatible with
     * 
     * @param hypervisor the hypervisor this image is compatible with
     */
    public void setHypervisor(final String hypervisor) {
        this.hypervisor = hypervisor;
    }

    /**
     * Returns the username of the user this image belongs to (or a null value
     * if this image is public)
     * 
     * @return the username of the user this image belongs to
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the username of the user this image belongs to
     * 
     * @param username the username of the user this image belongs to
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    public String getArchitecture() {
        return this.architecture;
    }

    public void setArchitecture(final String architecture) {
        this.architecture = architecture;
    }

    public Integer getDiskSizeMB() {
        return this.diskSizeMB;
    }

    public void setDiskSizeMB(final Integer diskSizeMB) {
        this.diskSizeMB = diskSizeMB;
    }

    public Integer getImageSizeMB() {
        return this.imageSizeMB;
    }

    public void setImageSizeMB(final Integer imageSizeMB) {
        this.imageSizeMB = imageSizeMB;
    }

    public MachineImage.State getStatus() {
        return this.status;
    }

    public void setStatus(final MachineImage.State status) {
        this.status = status;
    }

    public void setVisibility(final Visibility visibility) {
        this.visibility = visibility;
    }

    public Visibility getVisibility() {
        return this.visibility;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public String getCloudProviderAccountId() {
        return this.cloudProviderAccountId;
    }

    public void setCloudProviderAccountId(final String cloudProviderAccountId) {
        this.cloudProviderAccountId = cloudProviderAccountId;
    }

    public String getCloudProviderId() {
        return this.cloudProviderId;
    }

    public void setCloudProviderId(final String cloudProviderId) {
        this.cloudProviderId = cloudProviderId;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "VMImageVO [id=" + this.id + ", name=" + this.name + ", osType=" + this.osType + ", description="
            + this.description + ", hypervisor=" + this.hypervisor + ", username=" + this.username + ", projectId="
            + this.projectId + ", projectName=" + this.projectName + ", status=" + this.status + ", visibility="
            + this.visibility + ", architecture=" + this.architecture + ", diskSizeMB=" + this.diskSizeMB + ", imageSizeMB="
            + this.imageSizeMB + ", cloudProviderAccountId=" + this.cloudProviderAccountId + ", cloudProviderId="
            + this.cloudProviderId + ", location=" + this.location + "]";
    }

}
