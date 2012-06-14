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

import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntryPoint;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplateCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.JobCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineConfigurationCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplateCollection;

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
    private CredentialsCollection credentials;

    /**
     * Field "CredentialsTemplates".
     */
    private CredentialsTemplateCollection credentialsTemplates;

    /**
     * Field "Jobs".
     */
    private JobCollection jobs;

    /**
     * Field "jobTime".
     */
    private Integer jobTime;

    /**
     * Field "machineConfigs".
     */
    private MachineConfigurationCollection machineConfigs;

    /**
     * Field "machineImages".
     */
    private MachineImageCollection machineImages;

    /**
     * Field "machines".
     */
    private MachineCollection machines;

    // /**
    // * Field "volumeTemplates".
    // */
    // private VolumeTemplateCollection volumeTemplates;
    //
    // /**
    // * Field "volumeConfigurations".
    // */
    // private VolumeConfigurationCollection volumeConfigurations;
    //
    // /**
    // * Field "volumeImages".
    // */
    // private VolumeImageCollection volumeImages;
    //
    // /**
    // * Field "volumes".
    // */
    // private VolumeCollection volumes;

    /**
     * Field "machineTemplates".
     */
    private MachineTemplateCollection machineTemplates;

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
    public CredentialsCollection getCredentials() {
        return this.credentials;
    }

    /**
     * Set the value of field "credentials".
     * 
     * @param Credentialss The value
     */
    public void setCredentials(final CredentialsCollection credentials) {
        this.credentials = credentials;
    }

    /**
     * Return the value of field "CredentialsTemplates".
     * 
     * @return The value
     */
    public CredentialsTemplateCollection getCredentialsTemplates() {
        return this.credentialsTemplates;
    }

    /**
     * Set the value of field "CredentialsTemplates".
     * 
     * @param CredentialsTemplates The value
     */
    public void setCredentialsTemplates(final CredentialsTemplateCollection credentialsTemplates) {
        this.credentialsTemplates = credentialsTemplates;
    }

    /**
     * Return the value of field "jobs".
     * 
     * @return The value
     */
    public JobCollection getJobs() {
        return this.jobs;
    }

    /**
     * Set the value of field "jobs".
     * 
     * @param Jobs The value
     */
    public void setJobs(final JobCollection jobs) {
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
    public MachineConfigurationCollection getMachineConfigs() {
        return this.machineConfigs;
    }

    /**
     * Set the value of field "machineConfigs".
     * 
     * @param machineConfigs The value
     */
    public void setMachineConfigs(final MachineConfigurationCollection machineConfigs) {
        this.machineConfigs = machineConfigs;
    }

    /**
     * Return the value of field "machineImages".
     * 
     * @return The value
     */
    public MachineImageCollection getMachineImages() {
        return this.machineImages;
    }

    /**
     * Set the value of field "machineImages".
     * 
     * @param machineImages The value
     */
    public void setMachineImages(final MachineImageCollection machineImages) {
        this.machineImages = machineImages;
    }

    /**
     * Return the value of field "machines".
     * 
     * @return The value
     */
    public MachineCollection getMachines() {
        return this.machines;
    }

    /**
     * Set the value of field "machines".
     * 
     * @param machines The value
     */
    public void setMachines(final MachineCollection machines) {
        this.machines = machines;
    }

    // /**
    // * Return the value of field "volumeTemplates".
    // *
    // * @return The value
    // */
    // public VolumeTemplateCollection getVolumeTemplates() {
    // return this.volumeTemplates;
    // }
    //
    // /**
    // * Set the value of field "volumeTemplates".
    // *
    // * @param volumeTemplates The value
    // */
    // public void setVolumeTemplates(final VolumeTemplateCollection
    // volumeTemplates) {
    // this.volumeTemplates = volumeTemplates;
    // }
    //
    // /**
    // * Return the value of field "volumeConfigurations".
    // *
    // * @return The value
    // */
    // public VolumeConfigurationCollection getVolumeConfigurations() {
    // return this.volumeConfigurations;
    // }
    //
    // /**
    // * Set the value of field "volumeConfigurations".
    // *
    // * @param volumeConfigurations The value
    // */
    // public void setVolumeConfigurations(final VolumeConfigurationCollection
    // volumeConfigurations) {
    // this.volumeConfigurations = volumeConfigurations;
    // }
    //
    // /**
    // * Return the value of field "volumeImages".
    // *
    // * @return The value
    // */
    // public VolumeImageCollection getVolumeImages() {
    // return this.volumeImages;
    // }
    //
    // /**
    // * Set the value of field "volumeImages".
    // *
    // * @param volumeImages The value
    // */
    // public void setVolumeImages(final VolumeImageCollection volumeImages) {
    // this.volumeImages = volumeImages;
    // }
    //
    // /**
    // * Return the value of field "volumes".
    // *
    // * @return The value
    // */
    // public VolumeCollection getVolumes() {
    // return this.volumes;
    // }
    //
    // /**
    // * Set the value of field "volumes".
    // *
    // * @param volumes The value
    // */
    // public void setVolumes(final VolumeCollection volumes) {
    // this.volumes = volumes;
    // }

    /**
     * Return the value of field "machineTemplates".
     * 
     * @return The value
     */
    public MachineTemplateCollection getMachineTemplates() {
        return this.machineTemplates;
    }

    /**
     * Set the value of field "machineTemplates".
     * 
     * @param machineTemplates The value
     */
    public void setMachineTemplates(final MachineTemplateCollection machineTemplates) {
        this.machineTemplates = machineTemplates;
    }
}
