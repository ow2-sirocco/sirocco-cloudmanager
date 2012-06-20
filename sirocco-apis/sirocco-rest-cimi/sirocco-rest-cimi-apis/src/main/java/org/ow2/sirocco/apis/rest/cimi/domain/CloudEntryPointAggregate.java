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

import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfiguration;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;

/**
 * Aggregation of CloudEntryPoint to add all items necesary for CIMI.
 */
// TODO Others resources : Volumes, ...
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
     * Field "jobTime".
     */
    private Integer jobTime;

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

    // /**
    // * Field "volumeTemplates".
    // */
    // private VolumeTemplate> volumeTemplates;
    //
    // /**
    // * Field "volumeConfigurations".
    // */
    // private VolumeConfiguration> volumeConfigurations;
    //
    // /**
    // * Field "volumeImages".
    // */
    // private VolumeImage> volumeImages;
    //
    // /**
    // * Field "volumes".
    // */
    // private Volume> volumes;

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
     * @param Credentialss The value
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
     * Return the value of field "jobTime".
     * 
     * @return The value
     */
    public Integer getJobTime() {
        return this.jobTime;
    }

    /**
     * Set the value of field "jobTime".
     * 
     * @param jobTime The value
     */
    public void setJobTime(final Integer jobTime) {
        this.jobTime = jobTime;
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

    // /**
    // * Return the value of field "volumeTemplates".
    // *
    // * @return The value
    // */
    // public VolumeTemplate> getVolumeTemplates() {
    // return this.volumeTemplates;
    // }
    //
    // /**
    // * Set the value of field "volumeTemplates".
    // *
    // * @param volumeTemplates The value
    // */
    // public void setVolumeTemplates(final VolumeTemplate>
    // volumeTemplates) {
    // this.volumeTemplates = volumeTemplates;
    // }
    //
    // /**
    // * Return the value of field "volumeConfigurations".
    // *
    // * @return The value
    // */
    // public VolumeConfiguration> getVolumeConfigurations() {
    // return this.volumeConfigurations;
    // }
    //
    // /**
    // * Set the value of field "volumeConfigurations".
    // *
    // * @param volumeConfigurations The value
    // */
    // public void setVolumeConfigurations(final VolumeConfiguration>
    // volumeConfigurations) {
    // this.volumeConfigurations = volumeConfigurations;
    // }
    //
    // /**
    // * Return the value of field "volumeImages".
    // *
    // * @return The value
    // */
    // public VolumeImage> getVolumeImages() {
    // return this.volumeImages;
    // }
    //
    // /**
    // * Set the value of field "volumeImages".
    // *
    // * @param volumeImages The value
    // */
    // public void setVolumeImages(final VolumeImage> volumeImages) {
    // this.volumeImages = volumeImages;
    // }
    //
    // /**
    // * Return the value of field "volumes".
    // *
    // * @return The value
    // */
    // public Volume> getVolumes() {
    // return this.volumes;
    // }
    //
    // /**
    // * Set the value of field "volumes".
    // *
    // * @param volumes The value
    // */
    // public void setVolumes(final Volume> volumes) {
    // this.volumes = volumes;
    // }

}
