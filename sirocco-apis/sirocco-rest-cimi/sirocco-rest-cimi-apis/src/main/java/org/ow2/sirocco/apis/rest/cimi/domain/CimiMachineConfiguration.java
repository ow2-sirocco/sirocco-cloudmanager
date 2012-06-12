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
    @Valid
    @NotNull(groups = {GroupCreateByValue.class})
    private CimiCpu cpu;

    /**
     * Field "memory".
     */
    @Valid
    @NotNull(groups = {GroupCreateByValue.class})
    private CimiMemory memory;

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
    public CimiMachineConfiguration(final CimiCpu cpu, final CimiMemory memory) {
        super();
        this.cpu = cpu;
        this.memory = memory;
    }

    /**
     * Return the value of field "cpu".
     * 
     * @return The value
     */
    public CimiCpu getCpu() {
        return this.cpu;
    }

    /**
     * Set the value of field "cpu".
     * 
     * @param cpu The value
     */
    public void setCpu(final CimiCpu cpu) {
        this.cpu = cpu;
    }

    /**
     * Return the value of field "memory".
     * 
     * @return The value
     */
    public CimiMemory getMemory() {
        return this.memory;
    }

    /**
     * Set the value of field "memory".
     * 
     * @param memory The value
     */
    public void setMemory(final CimiMemory memory) {
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
