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

import java.util.List;

import org.ow2.sirocco.cloudmanager.model.cimi.Address;
import org.ow2.sirocco.cloudmanager.model.cimi.AddressTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroupTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPortTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLog;
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLogTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemTemplate;

/**
 * Aggregation of CloudEntryPoint to add all items necesary for CIMI.
 */
public class CloudEntryPointAggregate extends CloudEntryPoint {

    /** Serial. */
    private static final long serialVersionUID = 1L;

    /**
     * Field "Credentials".
     */
    private List<Credentials> credentials;

    /**
     * Field "CredentialsTemplates".
     */
    private List<CredentialsTemplate> credentialsTemplates;

    /**
     * Field "Jobs".
     */
    private List<Job> jobs;

    /**
     * Field "machineConfigs".
     */
    private List<MachineConfiguration> machineConfigs;

    /**
     * Field "machineImages".
     */
    private List<MachineImage> machineImages;

    /**
     * Field "machines".
     */
    private List<Machine> machines;

    /**
     * Field "machineTemplates".
     */
    private List<MachineTemplate> machineTemplates;

    /**
     * Field "volumes".
     */
    private List<Volume> volumes;

    /**
     * Field "volumeTemplates".
     */
    private List<VolumeTemplate> volumeTemplates;

    /**
     * Field "volumeConfigurations".
     */
    private List<VolumeConfiguration> volumeConfigurations;

    /**
     * Field "volumeImages".
     */
    private List<VolumeImage> volumeImages;

    /**
     * Field "systems".
     */
    private List<System> systems;

    /**
     * Field "systemTemplates".
     */
    private List<SystemTemplate> systemTemplates;

    /**
     * Field "networks".
     */
    private List<Network> networks;

    /**
     * Field "networkTemplates".
     */
    private List<NetworkTemplate> networkTemplates;

    /**
     * Field "networkConfigurations".
     */
    private List<NetworkConfiguration> networkConfigurations;

    /**
     * Field "networkPorts".
     */
    private List<NetworkPort> networkPorts;

    /**
     * Field "networkPortTemplates".
     */
    private List<NetworkPortTemplate> networkPortTemplates;

    /**
     * Field "networkPortConfigurations".
     */
    private List<NetworkPortConfiguration> networkPortConfigurations;

    /**
     * Field "addresses".
     */
    private List<Address> addresses;

    /**
     * Field "addressTemplates".
     */
    private List<AddressTemplate> addressTemplates;

    /**
     * Field "forwardingGroups".
     */
    private List<ForwardingGroup> forwardingGroups;

    /**
     * Field "forwardingGroupTemplates".
     */
    private List<ForwardingGroupTemplate> forwardingGroupTemplates;

    /**
     * Field "eventLogs".
     */
    private List<EventLog> eventLogs;

    /**
     * Field "eventLogTemplates".
     */
    private List<EventLogTemplate> eventLogTemplates;

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CloudEntryPointAggregate(final CloudEntryPoint cloud) {
        this.setCreated(cloud.getCreated());
        this.setDeleted(cloud.getDeleted());
        this.setDescription(cloud.getDescription());
        this.setId(cloud.getId());
        this.setName(cloud.getName());
        this.setProperties(cloud.getProperties());
        this.setProviderAssignedId(cloud.getProviderAssignedId());
        this.setUpdated(cloud.getUpdated());
        this.setUser(cloud.getUser());
    }

    /**
     * Return the value of field "credentials".
     * 
     * @return The value
     */
    public List<Credentials> getCredentials() {
        return this.credentials;
    }

    /**
     * Set the value of field "credentials".
     * 
     * @param Credentials The value
     */
    public void setCredentials(final List<Credentials> credentials) {
        this.credentials = credentials;
    }

    /**
     * Return the value of field "CredentialsTemplates".
     * 
     * @return The value
     */
    public List<CredentialsTemplate> getCredentialsTemplates() {
        return this.credentialsTemplates;
    }

    /**
     * Set the value of field "CredentialsTemplates".
     * 
     * @param CredentialsTemplates The value
     */
    public void setCredentialsTemplates(final List<CredentialsTemplate> credentialsTemplates) {
        this.credentialsTemplates = credentialsTemplates;
    }

    /**
     * Return the value of field "jobs".
     * 
     * @return The value
     */
    public List<Job> getJobs() {
        return this.jobs;
    }

    /**
     * Set the value of field "jobs".
     * 
     * @param Jobs The value
     */
    public void setJobs(final List<Job> jobs) {
        this.jobs = jobs;
    }

    /**
     * Return the value of field "machineConfigs".
     * 
     * @return The value
     */
    public List<MachineConfiguration> getMachineConfigs() {
        return this.machineConfigs;
    }

    /**
     * Set the value of field "machineConfigs".
     * 
     * @param machineConfigs The value
     */
    public void setMachineConfigs(final List<MachineConfiguration> machineConfigs) {
        this.machineConfigs = machineConfigs;
    }

    /**
     * Return the value of field "machineImages".
     * 
     * @return The value
     */
    public List<MachineImage> getMachineImages() {
        return this.machineImages;
    }

    /**
     * Set the value of field "machineImages".
     * 
     * @param machineImages The value
     */
    public void setMachineImages(final List<MachineImage> machineImages) {
        this.machineImages = machineImages;
    }

    /**
     * Return the value of field "machines".
     * 
     * @return The value
     */
    public List<Machine> getMachines() {
        return this.machines;
    }

    /**
     * Set the value of field "machines".
     * 
     * @param machines The value
     */
    public void setMachines(final List<Machine> machines) {
        this.machines = machines;
    }

    /**
     * Return the value of field "machineTemplates".
     * 
     * @return The value
     */
    public List<MachineTemplate> getMachineTemplates() {
        return this.machineTemplates;
    }

    /**
     * Set the value of field "machineTemplates".
     * 
     * @param machineTemplates The value
     */
    public void setMachineTemplates(final List<MachineTemplate> machineTemplates) {
        this.machineTemplates = machineTemplates;
    }

    /**
     * Return the value of field "volumeConfigurations".
     * 
     * @return The value
     */
    public List<VolumeConfiguration> getVolumeConfigurations() {
        return this.volumeConfigurations;
    }

    /**
     * Set the value of field "volumeConfigurations".
     * 
     * @param volumeConfigurations The value
     */
    public void setVolumeConfigurations(final List<VolumeConfiguration> volumeConfigurations) {
        this.volumeConfigurations = volumeConfigurations;
    }

    /**
     * Return the value of field "volumeImages".
     * 
     * @return The value
     */
    public List<VolumeImage> getVolumeImages() {
        return this.volumeImages;
    }

    /**
     * Set the value of field "volumeImages".
     * 
     * @param volumeImages The value
     */
    public void setVolumeImages(final List<VolumeImage> volumeImages) {
        this.volumeImages = volumeImages;
    }

    /**
     * Return the value of field "volumes".
     * 
     * @return The value
     */
    public List<Volume> getVolumes() {
        return this.volumes;
    }

    /**
     * Set the value of field "volumes".
     * 
     * @param volumes The value
     */
    public void setVolumes(final List<Volume> volumes) {
        this.volumes = volumes;
    }

    /**
     * Return the value of field "volumeTemplates".
     * 
     * @return The value
     */
    public List<VolumeTemplate> getVolumeTemplates() {
        return this.volumeTemplates;
    }

    /**
     * Set the value of field "volumeTemplates".
     * 
     * @param volumeTemplates The value
     */
    public void setVolumeTemplates(final List<VolumeTemplate> volumeTemplates) {
        this.volumeTemplates = volumeTemplates;
    }

    /**
     * Return the value of field "systems".
     * 
     * @return The value
     */
    public List<System> getSystems() {
        return this.systems;
    }

    /**
     * Set the value of field "systems".
     * 
     * @param systems The value
     */
    public void setSystems(final List<System> systems) {
        this.systems = systems;
    }

    /**
     * Return the value of field "systemTemplates".
     * 
     * @return The value
     */
    public List<SystemTemplate> getSystemTemplates() {
        return this.systemTemplates;
    }

    /**
     * Set the value of field "systemTemplates".
     * 
     * @param systemTemplates The value
     */
    public void setSystemTemplates(final List<SystemTemplate> systemTemplates) {
        this.systemTemplates = systemTemplates;
    }

    /**
     * Return the value of field "networks".
     * 
     * @return The value
     */
    public List<Network> getNetworks() {
        return this.networks;
    }

    /**
     * Set the value of field "networks".
     * 
     * @param networks The value
     */
    public void setNetworks(final List<Network> networks) {
        this.networks = networks;
    }

    /**
     * Return the value of field "networkConfigurations".
     * 
     * @return The value
     */
    public List<NetworkConfiguration> getNetworkConfigurations() {
        return this.networkConfigurations;
    }

    /**
     * Set the value of field "networkConfigurations".
     * 
     * @param networkConfigurations The value
     */
    public void setNetworkConfigurations(final List<NetworkConfiguration> networkConfigurations) {
        this.networkConfigurations = networkConfigurations;
    }

    /**
     * Return the value of field "networkTemplates".
     * 
     * @return The value
     */
    public List<NetworkTemplate> getNetworkTemplates() {
        return this.networkTemplates;
    }

    /**
     * Set the value of field "networkTemplates".
     * 
     * @param networkTemplates The value
     */
    public void setNetworkTemplates(final List<NetworkTemplate> networkTemplates) {
        this.networkTemplates = networkTemplates;
    }

    /**
     * Return the value of field "networkPorts".
     * 
     * @return The value
     */
    public List<NetworkPort> getNetworkPorts() {
        return this.networkPorts;
    }

    /**
     * Set the value of field "networkPorts".
     * 
     * @param networkPorts The value
     */
    public void setNetworkPorts(final List<NetworkPort> networkPorts) {
        this.networkPorts = networkPorts;
    }

    /**
     * Return the value of field "networkPortConfigurations".
     * 
     * @return The value
     */
    public List<NetworkPortConfiguration> getNetworkPortConfigurations() {
        return this.networkPortConfigurations;
    }

    /**
     * Set the value of field "networkPortConfigurations".
     * 
     * @param networkPortConfigurations The value
     */
    public void setNetworkPortConfigurations(final List<NetworkPortConfiguration> networkPortConfigurations) {
        this.networkPortConfigurations = networkPortConfigurations;
    }

    /**
     * Return the value of field "networkPortTemplates".
     * 
     * @return The value
     */
    public List<NetworkPortTemplate> getNetworkPortTemplates() {
        return this.networkPortTemplates;
    }

    /**
     * Set the value of field "networkPortTemplates".
     * 
     * @param networkPortTemplates The value
     */
    public void setNetworkPortTemplates(final List<NetworkPortTemplate> networkPortTemplates) {
        this.networkPortTemplates = networkPortTemplates;
    }

    /**
     * Return the value of field "addresses".
     * 
     * @return The value
     */
    public List<Address> getAddresses() {
        return this.addresses;
    }

    /**
     * Set the value of field "addresss".
     * 
     * @param addresss The value
     */
    public void setAddresses(final List<Address> addresses) {
        this.addresses = addresses;
    }

    /**
     * Return the value of field "addressTemplates".
     * 
     * @return The value
     */
    public List<AddressTemplate> getAddressTemplates() {
        return this.addressTemplates;
    }

    /**
     * Set the value of field "addressTemplates".
     * 
     * @param addressTemplates The value
     */
    public void setAddressTemplates(final List<AddressTemplate> addressTemplates) {
        this.addressTemplates = addressTemplates;
    }

    /**
     * Return the value of field "forwardingGroups".
     * 
     * @return The value
     */
    public List<ForwardingGroup> getForwardingGroups() {
        return this.forwardingGroups;
    }

    /**
     * Set the value of field "forwardingGroups".
     * 
     * @param forwardingGroups The value
     */
    public void setForwardingGroups(final List<ForwardingGroup> forwardingGroups) {
        this.forwardingGroups = forwardingGroups;
    }

    /**
     * Return the value of field "forwardingGroupTemplates".
     * 
     * @return The value
     */
    public List<ForwardingGroupTemplate> getForwardingGroupTemplates() {
        return this.forwardingGroupTemplates;
    }

    /**
     * Set the value of field "forwardingGroupTemplates".
     * 
     * @param forwardingGroupTemplates The value
     */
    public void setForwardingGroupTemplates(final List<ForwardingGroupTemplate> forwardingGroupTemplates) {
        this.forwardingGroupTemplates = forwardingGroupTemplates;
    }

    /**
     * Return the value of field "eventLogs".
     * 
     * @return The value
     */
    public List<EventLog> getEventLogs() {
        return this.eventLogs;
    }

    /**
     * Set the value of field "eventLogs".
     * 
     * @param eventLogs The value
     */
    public void setEventLogs(final List<EventLog> eventLogs) {
        this.eventLogs = eventLogs;
    }

    /**
     * Return the value of field "eventLogTemplates".
     * 
     * @return The value
     */
    public List<EventLogTemplate> getEventLogTemplates() {
        return this.eventLogTemplates;
    }

    /**
     * Set the value of field "eventLogTemplates".
     * 
     * @param eventLogTemplates The value
     */
    public void setEventLogTemplates(final List<EventLogTemplate> eventLogTemplates) {
        this.eventLogTemplates = eventLogTemplates;
    }
}
