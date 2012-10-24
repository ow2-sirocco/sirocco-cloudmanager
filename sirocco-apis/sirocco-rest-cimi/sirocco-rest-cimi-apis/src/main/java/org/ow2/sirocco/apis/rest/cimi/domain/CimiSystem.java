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
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemAddressCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemCredentialCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemForwardingGroupCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemMachineCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemNetworkCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemNetworkPortCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemSystemCollection;
import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiSystemVolumeCollection;

/**
 * Class System.
 */
@XmlRootElement(name = "System")
@XmlType(propOrder = {"id", "name", "description", "created", "updated", "propertyArray", "state", "systems", "machines",
    "credentials", "volumes", "networks", "networkPorts", "addresses", "forwardingGroups", "eventLog", "operations"})
@JsonPropertyOrder({"resourceURI", "id", "name", "description", "created", "updated", "properties", "state", "systems",
    "machines", "credentials", "volumes", "networks", "networkPorts", "addresses", "forwardingGroups", "eventLog", "operations"})
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiSystem extends CimiObjectCommonAbstract {

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
     * Field "credentials".
     * <p>
     * Read only
     * </p>
     */
    private CimiSystemCredentialCollection credentials;

    /**
     * Field "machines".
     * <p>
     * Read only
     * </p>
     */
    private CimiSystemMachineCollection machines;

    /**
     * Field "systems".
     * <p>
     * Read only
     * </p>
     */
    private CimiSystemSystemCollection systems;

    /**
     * Field "volumes".
     * <p>
     * Read only
     * </p>
     */
    private CimiSystemVolumeCollection volumes;

    /**
     * Field "networks".
     * <p>
     * Read only
     * </p>
     */
    private CimiSystemNetworkCollection networks;

    /**
     * Field "networkPorts".
     * <p>
     * Read only
     * </p>
     */
    private CimiSystemNetworkPortCollection networkPorts;

    /**
     * Field "addresses".
     * <p>
     * Read only
     * </p>
     */
    private CimiSystemAddressCollection addresses;

    /**
     * Field "forwardingGroups".
     * <p>
     * Read only
     * </p>
     */
    private CimiSystemForwardingGroupCollection forwardingGroups;

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
     * Return the value of field "networks".
     * 
     * @return The value
     */
    public CimiSystemNetworkCollection getNetworks() {
        return this.networks;
    }

    /**
     * Set the value of field "networks".
     * 
     * @param networks The value
     */
    public void setNetworks(final CimiSystemNetworkCollection networks) {
        this.networks = networks;
    }

    /**
     * Return the value of field "networkPorts".
     * 
     * @return The value
     */
    public CimiSystemNetworkPortCollection getNetworkPorts() {
        return this.networkPorts;
    }

    /**
     * Set the value of field "networkPorts".
     * 
     * @param networkPorts The value
     */
    public void setNetworkPorts(final CimiSystemNetworkPortCollection networkPorts) {
        this.networkPorts = networkPorts;
    }

    /**
     * Return the value of field "addresses".
     * 
     * @return The value
     */
    public CimiSystemAddressCollection getAddresses() {
        return this.addresses;
    }

    /**
     * Set the value of field "addresses".
     * 
     * @param addresses The value
     */
    public void setAddresses(final CimiSystemAddressCollection addresses) {
        this.addresses = addresses;
    }

    /**
     * Return the value of field "forwardingGroups".
     * 
     * @return The value
     */
    public CimiSystemForwardingGroupCollection getForwardingGroups() {
        return this.forwardingGroups;
    }

    /**
     * Set the value of field "forwardingGroups".
     * 
     * @param forwardingGroups The value
     */
    public void setForwardingGroups(final CimiSystemForwardingGroupCollection forwardingGroups) {
        this.forwardingGroups = forwardingGroups;
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
        // Next read-only
        // has = has || (null != this.getCredentials());
        // has = has || (null != this.getMachines());
        // has = has || (null != this.getSystems());
        // has = has || (null != this.getVolumes());
        // has = has || (null != this.getNetworks());
        // has = has || (null != this.getNetworkPorts());
        // has = has || (null != this.getAddresses());
        // has = has || (null != this.getForwardingGroups());
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
