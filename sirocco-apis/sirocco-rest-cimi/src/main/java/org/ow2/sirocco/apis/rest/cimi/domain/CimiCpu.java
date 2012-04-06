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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Class Cpu.
 */
@XmlRootElement(name = "Cpu")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiCpu implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "numberVirtualCpus".
     */
    private Integer numberVirtualCpus;

    /**
     * Field "frequency".
     */
    private Integer frequency;

    /**
     * Field "units".
     * <p>
     * enum : hertz (Hz), decahertz (daHz), hectohertz (hHz), kilohertz (kHz),
     * megahertz (MHz), gigahertz (GHz), terahertz (THz), petahertz (PHz),
     * exahertz (EHz), zettahertz (ZHz), yottahertz (YHz)
     * </p>
     */
    // private FrequencyUnit units;
    private String units;

    /**
     * Return the value of field "numberVirtualCpus".
     * 
     * @return The value
     */
    @XmlAttribute
    public Integer getNumberVirtualCpus() {
        return this.numberVirtualCpus;
    }

    /**
     * Set the value of field "numberVirtualCpus".
     * 
     * @param numberVirtualCpus The value
     */
    public void setNumberVirtualCpus(final Integer numberVirtualCpus) {
        this.numberVirtualCpus = numberVirtualCpus;
    }

    /**
     * Return the value of field "frequency".
     * 
     * @return The value
     */
    @XmlAttribute
    public Integer getFrequency() {
        return this.frequency;
    }

    /**
     * Set the value of field "frequency".
     * 
     * @param frequency The value
     */
    public void setFrequency(final Integer frequency) {
        this.frequency = frequency;
    }

    /**
     * Return the value of field "units".
     * 
     * @return The value
     */
    @XmlAttribute
    public String getUnits() {
        return this.units;
    }

    /**
     * Set the value of field "units".
     * 
     * @param units The value
     */
    public void setUnits(final String units) {
        this.units = units;
    }
}
