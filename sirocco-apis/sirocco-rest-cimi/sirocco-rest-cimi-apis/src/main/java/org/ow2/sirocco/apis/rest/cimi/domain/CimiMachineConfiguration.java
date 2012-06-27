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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupCreateByValue;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;
import org.ow2.sirocco.apis.rest.cimi.validator.constraints.NotEmptyIfNotNull;

/**
 * Class MachineConfiguration.
 */
@XmlRootElement(name = "MachineConfiguration")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiMachineConfiguration extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "cpu".
     */
    private Integer cpu;

    /**
     * Field "cpuArch".
     */
    private String cpuArch;

    /**
     * Field "memory".
     */
    @NotNull(groups = {GroupCreateByValue.class})
    private Integer memory;

    /**
     * Field "disks".
     */
    @JsonProperty
    @Valid
    @NotEmptyIfNotNull(groups = {GroupWrite.class})
    private CimiDiskConfiguration[] disks;

    /**
     * Default constructor.
     */
    public CimiMachineConfiguration() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiMachineConfiguration(final String href) {
        super(href);
    }

    /**
     * Parameterized constructor.
     * 
     * @param cpu The CPU
     * @param memory The memory
     */
    public CimiMachineConfiguration(final Integer cpu, final Integer memory) {
        super();
        this.cpu = cpu;
        this.memory = memory;
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
    @XmlElement(name = "disk")
    @JsonIgnore
    public CimiDiskConfiguration[] getDisks() {
        return this.disks;
    }

    /**
     * Set the value of field "disks".
     * 
     * @param disks The value
     */
    public void setDisks(final CimiDiskConfiguration[] disks) {
        this.disks = disks;
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
        has = has || (null != this.getDisks());
        has = has || (null != this.getMemory());
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
        return ExchangeType.MachineConfiguration;
    }

}
