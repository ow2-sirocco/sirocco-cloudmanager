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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.domain;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineDiskCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineNetworkInterfaceCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiMachineVolumeCollection;

/**
 * Class Machine.
 */
@XmlRootElement(name = "Machine")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiMachine extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "state".
     * <p>
     * Read only
     * </p>
     */
    private String state;

    /**
     * Field "cpu".
     */
    private Integer cpu;

    /**
     * Field "cpuArch".
     * <p>
     * Read only
     * </p>
     */
    private String cpuArch;

    /**
     * Field "memory".
     */
    private Integer memory;

    /**
     * Field "disks".
     * <p>
     * Read only
     * </p>
     */
    private CimiMachineDiskCollection disks;

    /**
     * Field "volumes".
     * <p>
     * Read only
     * </p>
     */
    private CimiMachineVolumeCollection volumes;

    /**
     * Field "networkInterfaces".
     * <p>
     * Read only
     * </p>
     */
    private CimiMachineNetworkInterfaceCollection networkInterfaces;

    /**
     * Field "eventLog".
     * <p>
     * Read only
     * </p>
     */
    private CimiEventLog eventLog;

    /**
     * Default constructor.
     */
    public CimiMachine() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiMachine(final String href) {
        super(href);
    }

    /**
     * Return the value of field "state".
     * 
     * @return The value
     */
    public String getState() {
        return this.state;
    }

    /**
     * Set the value of field "state".
     * 
     * @param state The value
     */
    public void setState(final String state) {
        this.state = state;
    }

    /**
     * Return the value of field "cpu".
     * 
     * @return The value
     */
    public Integer getCpu() {
        return this.cpu;
    }

    /**
     * Set the value of field "cpu".
     * 
     * @param cpu The value
     */
    public void setCpu(final Integer cpu) {
        this.cpu = cpu;
    }

    /**
     * Return the value of field "cpuArch".
     * 
     * @return The value
     */
    public String getCpuArch() {
        return this.cpuArch;
    }

    /**
     * Set the value of field "cpuArch".
     * 
     * @param cpuArch The value
     */
    public void setCpuArch(final String cpuArch) {
        this.cpuArch = cpuArch;
    }

    /**
     * Return the value of field "memory".
     * 
     * @return The value
     */
    public Integer getMemory() {
        return this.memory;
    }

    /**
     * Set the value of field "memory".
     * 
     * @param memory The value
     */
    public void setMemory(final Integer memory) {
        this.memory = memory;
    }

    /**
     * Return the value of field "disks".
     * 
     * @return The value
     */
    public CimiMachineDiskCollection getDisks() {
        return this.disks;
    }

    /**
     * Set the value of field "disks".
     * 
     * @param disks The value
     */
    public void setDisks(final CimiMachineDiskCollection disks) {
        this.disks = disks;
    }

    /**
     * Return the value of field "volumes".
     * 
     * @return The value
     */
    public CimiMachineVolumeCollection getVolumes() {
        return this.volumes;
    }

    /**
     * Set the value of field "volumes".
     * 
     * @param volumes The value
     */
    public void setVolumes(final CimiMachineVolumeCollection volumes) {
        this.volumes = volumes;
    }

    /**
     * Return the value of field "networkInterfaces".
     * 
     * @return The value
     */
    public CimiMachineNetworkInterfaceCollection getNetworkInterfaces() {
        return this.networkInterfaces;
    }

    /**
     * Set the value of field "networkInterfaces".
     * 
     * @param networkInterfaces The value
     */
    public void setNetworkInterfaces(final CimiMachineNetworkInterfaceCollection networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    /**
     * Return the value of field "eventLog".
     * 
     * @return The value
     */
    public CimiEventLog getEventLog() {
        return this.eventLog;
    }

    /**
     * Set the value of field "eventLog".
     * 
     * @param eventLog The value
     */
    public void setEventLog(final CimiEventLog eventLog) {
        this.eventLog = eventLog;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        has = has || (null != this.getCpu());
        has = has || (null != this.getMemory());

        // Next read-only
        // has = has || (null != this.getState());
        // has = has || (null != this.getCpuArch());
        // has = has || (null != this.getDisks());
        // has = has || (null != this.getNetworkInterfaces());
        // has = has || (null != this.getVolumes());
        // has = has || (null != this.getEventLog());
        return has;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiExchange#getExchangeType()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public ExchangeType getExchangeType() {
        return ExchangeType.Machine;
    }

}
