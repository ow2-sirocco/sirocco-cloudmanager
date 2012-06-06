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

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Class CloudEntryPoint.
 * <p>
 */
@XmlRootElement(name = "CloudEntryPoint")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiCloudEntryPoint extends CimiObjectCommonImpl {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Field "machineTemplates".
     */
    private CimiMachineTemplateCollection machineTemplates;

    /**
     * Field "machineConfigs".
     */
    private CimiMachineConfigurationCollection machineConfigs;

    /**
     * Field "machineImages".
     */
    private CimiMachineImageCollection machineImages;

    /**
     * Field "CredentialsTemplates".
     */
    private CimiCredentialsTemplateCollection CredentialsTemplates;

    /**
     * Field "Credentials".
     */
    private CimiCredentialsCollection credentials;

    /**
     * Field "machines".
     */
    private CimiMachineCollection machines;

    /**
     * Field "volumeTemplates".
     */
    private CimiVolumeTemplateCollection volumeTemplates;

    /**
     * Field "volumeConfigurations".
     */
    private CimiVolumeConfigurationCollection volumeConfigurations;

    /**
     * Field "volumeImages".
     */
    private CimiVolumeImageCollection volumeImages;

    /**
     * Field "volumes".
     */
    private CimiVolumeCollection volumes;

    /**
     * Field "jobTime".
     */
    private Integer jobTime;

    /**
     * Default constructor.
     */
    public CimiCloudEntryPoint() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param href The reference
     */
    public CimiCloudEntryPoint(final String href) {
        super(href);
    }

    /**
     * Return the value of field "machineTemplates".
     * 
     * @return The value
     */
    public CimiMachineTemplateCollection getMachineTemplates() {
        return this.machineTemplates;
    }

    /**
     * Set the value of field "machineTemplates".
     * 
     * @param machineTemplates The value
     */
    public void setMachineTemplates(final CimiMachineTemplateCollection machineTemplates) {
        this.machineTemplates = machineTemplates;
    }

    /**
     * Return the value of field "machineConfigs".
     * 
     * @return The value
     */
    public CimiMachineConfigurationCollection getMachineConfigs() {
        return this.machineConfigs;
    }

    /**
     * Set the value of field "machineConfigs".
     * 
     * @param machineConfigs The value
     */
    public void setMachineConfigs(final CimiMachineConfigurationCollection machineConfigs) {
        this.machineConfigs = machineConfigs;
    }

    /**
     * Return the value of field "machineImages".
     * 
     * @return The value
     */
    public CimiMachineImageCollection getMachineImages() {
        return this.machineImages;
    }

    /**
     * Set the value of field "machineImages".
     * 
     * @param machineImages The value
     */
    public void setMachineImages(final CimiMachineImageCollection machineImages) {
        this.machineImages = machineImages;
    }

    /**
     * Return the value of field "CredentialsTemplates".
     * 
     * @return The value
     */
    public CimiCredentialsTemplateCollection getCredentialsTemplates() {
        return this.CredentialsTemplates;
    }

    /**
     * Set the value of field "CredentialsTemplates".
     * 
     * @param CredentialsTemplates The value
     */
    public void setCredentialsTemplates(final CimiCredentialsTemplateCollection CredentialsTemplates) {
        this.CredentialsTemplates = CredentialsTemplates;
    }

    /**
     * Return the value of field "credentials".
     * 
     * @return The value
     */
    public CimiCredentialsCollection getCredentials() {
        return this.credentials;
    }

    /**
     * Set the value of field "credentials".
     * 
     * @param Credentialss The value
     */
    public void setCredentials(final CimiCredentialsCollection credentials) {
        this.credentials = credentials;
    }

    /**
     * Return the value of field "machines".
     * 
     * @return The value
     */
    public CimiMachineCollection getMachines() {
        return this.machines;
    }

    /**
     * Set the value of field "machines".
     * 
     * @param machines The value
     */
    public void setMachines(final CimiMachineCollection machines) {
        this.machines = machines;
    }

    /**
     * Return the value of field "volumeTemplates".
     * 
     * @return The value
     */
    public CimiVolumeTemplateCollection getVolumeTemplates() {
        return this.volumeTemplates;
    }

    /**
     * Set the value of field "volumeTemplates".
     * 
     * @param volumeTemplates The value
     */
    public void setVolumeTemplates(final CimiVolumeTemplateCollection volumeTemplates) {
        this.volumeTemplates = volumeTemplates;
    }

    /**
     * Return the value of field "volumeConfigurations".
     * 
     * @return The value
     */
    public CimiVolumeConfigurationCollection getVolumeConfigurations() {
        return this.volumeConfigurations;
    }

    /**
     * Set the value of field "volumeConfigurations".
     * 
     * @param volumeConfigurations The value
     */
    public void setVolumeConfigurations(final CimiVolumeConfigurationCollection volumeConfigurations) {
        this.volumeConfigurations = volumeConfigurations;
    }

    /**
     * Return the value of field "volumeImages".
     * 
     * @return The value
     */
    public CimiVolumeImageCollection getVolumeImages() {
        return this.volumeImages;
    }

    /**
     * Set the value of field "volumeImages".
     * 
     * @param volumeImages The value
     */
    public void setVolumeImages(final CimiVolumeImageCollection volumeImages) {
        this.volumeImages = volumeImages;
    }

    /**
     * Return the value of field "volumes".
     * 
     * @return The value
     */
    public CimiVolumeCollection getVolumes() {
        return this.volumes;
    }

    /**
     * Set the value of field "volumes".
     * 
     * @param volumes The value
     */
    public void setVolumes(final CimiVolumeCollection volumes) {
        this.volumes = volumes;
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
}
