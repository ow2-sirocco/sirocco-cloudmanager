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
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemCredentialCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemSystemCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemVolumeCollection;
import org.ow2.sirocco.apis.rest.cimi.validator.GroupWrite;
import org.ow2.sirocco.apis.rest.cimi.validator.ValidChild;
import org.ow2.sirocco.apis.rest.cimi.validator.constraints.NotEmptyIfNotNull;

/**
 * Class System.
 */
// TODO Network, ...
@XmlRootElement(name = "System")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiSystem extends CimiObjectCommonAbstract {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "state".
     */
    private String state;

    /**
     * Field "credentials".
     */
    @ValidChild
    @NotEmptyIfNotNull(groups = {GroupWrite.class})
    private CimiSystemCredentialCollection credentials;

    /**
     * Field "machines".
     */
    @ValidChild
    @NotEmptyIfNotNull(groups = {GroupWrite.class})
    private CimiSystemMachineCollection machines;

    /**
     * Field "systems".
     */
    @ValidChild
    @NotEmptyIfNotNull(groups = {GroupWrite.class})
    private CimiSystemSystemCollection systems;

    /**
     * Field "volumes".
     */
    @ValidChild
    @NotEmptyIfNotNull(groups = {GroupWrite.class})
    private CimiSystemVolumeCollection volumes;

    /**
     * Default constructor.
     */
    public CimiSystem() {
        super();
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
     * Return the value of field "credentials".
     * 
     * @return The value
     */
    public CimiSystemCredentialCollection getCredentials() {
        return this.credentials;
    }

    /**
     * Set the value of field "credentials".
     * 
     * @param credentials The value
     */
    public void setCredentials(final CimiSystemCredentialCollection credentials) {
        this.credentials = credentials;
    }

    /**
     * Return the value of field "machines".
     * 
     * @return The value
     */
    public CimiSystemMachineCollection getMachines() {
        return this.machines;
    }

    /**
     * Set the value of field "machines".
     * 
     * @param machines The value
     */
    public void setMachines(final CimiSystemMachineCollection machines) {
        this.machines = machines;
    }

    /**
     * Return the value of field "systems".
     * 
     * @return The value
     */
    public CimiSystemSystemCollection getSystems() {
        return this.systems;
    }

    /**
     * Set the value of field "systems".
     * 
     * @param systems The value
     */
    public void setSystems(final CimiSystemSystemCollection systems) {
        this.systems = systems;
    }

    /**
     * Return the value of field "volumes".
     * 
     * @return The value
     */
    public CimiSystemVolumeCollection getVolumes() {
        return this.volumes;
    }

    /**
     * Set the value of field "volumes".
     * 
     * @param volumes The value
     */
    public void setVolumes(final CimiSystemVolumeCollection volumes) {
        this.volumes = volumes;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.domain.CimiObjectCommonAbstract#hasValues()
     */
    @Override
    public boolean hasValues() {
        boolean has = super.hasValues();
        has = has || (null != this.getCredentials());
        has = has || (null != this.getMachines());
        has = has || (null != this.getSystems());
        has = has || (null != this.getVolumes());
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
        return ExchangeType.System;
    }

}
