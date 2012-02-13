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

package org.ow2.sirocco.cloudmanager.clustermanager.api;

import java.io.Serializable;
import java.util.Map;

/**
 * Business object representing a host. A host is a physical server on which a
 * hypervisor has been installed allowing the creation and concurrent execution
 * of virtual machines on the shared resources of the host.
 */
public class Host extends ResourceContainer implements Serializable {
    private static final long serialVersionUID = 4564692436756738301L;

    private String id;

    private String hostName;

    private int numCPUs;

    private Map<String, String> cpuInfo;

    private Map<String, String> hypervisorInfo;

    /**
     * Returns the unique identifier of the host
     * 
     * @return the unique identifier of the host
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the unique identifier of the host
     * 
     * @param id the unique identifier of the host
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Returns the name of the host
     * 
     * @return the name of the host
     */
    public String getHostName() {
        return this.hostName;
    }

    /**
     * Sets the name of the host
     * 
     * @param hostName the name of the host
     */
    public void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    /**
     * Returns the number of physical CPUs of the host
     * 
     * @return the number of physical CPUs
     */
    public int getNumCPUs() {
        return this.numCPUs;
    }

    /**
     * Sets the number of physical CPUs
     * 
     * @param numCPUs the number of physical CPUs
     */
    public void setNumCPUs(final int numCPUs) {
        this.numCPUs = numCPUs;
    }

    /**
     * Returns information on the CPU of the host as a map of key-value pairs
     * 
     * @return a map of key value-pairs. The following keys are guaranteed to be
     *         included in this map:
     *         <ul>
     *         <li>model
     *         <li>speedMHz
     *         <li>vendor
     *         </ul>
     */
    public Map<String, String> getCpuInfo() {
        return this.cpuInfo;
    }

    /**
     * Sets the information on the CPU of the host
     * 
     * @param cpuInfo a map of key value-pairs
     */
    public void setCpuInfo(final Map<String, String> cpuInfo) {
        this.cpuInfo = cpuInfo;
    }

    /**
     * Returns some information about the hypervisor installed on the host as a
     * map of key-value pairs. The following keys are guaranteed to be included
     * in this map:
     * <ul>
     * <li>name
     * <li>vendor
     * </ul>
     * 
     * @return a map of key-value pairs containing information on the hypervisor
     */
    public Map<String, String> getHypervisorInfo() {
        return this.hypervisorInfo;
    }

    /**
     * Sets the information on the hypervisor installed on the host
     * 
     * @param hypervisorInfo a map of key-value pairs containing information on
     *        the hypervisor
     */
    public void setHypervisorInfo(final Map<String, String> hypervisorInfo) {
        this.hypervisorInfo = hypervisorInfo;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.dump(stringBuilder, "");
        return stringBuilder.toString();
    }

    void dump(final StringBuilder str, final String lineHeader) {
        str.append(lineHeader + "Host(id=" + this.id + ",name=" + this.hostName + ")\n");
        str.append(lineHeader + "\t" + super.toString() + "\n");
        str.append(lineHeader + "\tnumCPUs=" + this.numCPUs + "\n");
        str.append(lineHeader + "\tCPUInfo=(model=" + this.cpuInfo.get("model") + ",speedMHz=" + this.cpuInfo.get("speedMHz")
            + ")\n");
        str.append(lineHeader + "\tHypervisor=(name=" + this.hypervisorInfo.get("name") + ",vendor="
            + this.hypervisorInfo.get("vendor") + ")\n");
    }

}
