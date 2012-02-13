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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "server", propOrder = {"cloudProviderAccountId", "name", "instanceId", "status", "imageId", "size", "upTime",
    "publicIp", "publicHostName", "privateIp", "privateHostName", "projectId"})
@XmlRootElement(name = "server")
public class VmInfo {
    private String name;

    private String instanceId;

    private Integer imageId;

    private String size;

    private long upTime;

    private String publicIp;

    private String publicHostName;

    private String privateIp;

    private String privateHostName;

    private String status;

    private String projectId;

    private String cloudProviderAccountId;

    public VmInfo() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(final String instanceId) {
        this.instanceId = instanceId;
    }

    public Integer getImageId() {
        return this.imageId;
    }

    public void setImageId(final Integer imageId) {
        this.imageId = imageId;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(final String size) {
        this.size = size;
    }

    public long getUpTime() {
        return this.upTime;
    }

    public void setUpTime(final long upTime) {
        this.upTime = upTime;
    }

    public String getPublicIp() {
        return this.publicIp;
    }

    public void setPublicIp(final String publicIp) {
        this.publicIp = publicIp;
    }

    public String getPublicHostName() {
        return this.publicHostName;
    }

    public void setPublicHostName(final String publicHostName) {
        this.publicHostName = publicHostName;
    }

    public String getPrivateIp() {
        return this.privateIp;
    }

    public void setPrivateIp(final String privateIp) {
        this.privateIp = privateIp;
    }

    public String getPrivateHostName() {
        return this.privateHostName;
    }

    public void setPrivateHostName(final String privateHostName) {
        this.privateHostName = privateHostName;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public String getCloudProviderAccountId() {
        return this.cloudProviderAccountId;
    }

    public void setCloudProviderAccountId(final String cloudProviderAccountId) {
        this.cloudProviderAccountId = cloudProviderAccountId;
    }

    @Override
    public String toString() {
        return "VmInfo [name=" + this.name + ", instanceId=" + this.instanceId + ", imageId=" + this.imageId + ", size="
            + this.size + ", upTime=" + this.upTime + ", publicIp=" + this.publicIp + ", publicHostName=" + this.publicHostName
            + ", privateIp=" + this.privateIp + ", privateHostName=" + this.privateHostName + ", status=" + this.status
            + ", projectId=" + this.projectId + ", cloudProviderAccountId=" + this.cloudProviderAccountId + "]";
    }

}
