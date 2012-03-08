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
 * Class CloudEntryPoint.
 * <p>
 * </p>
 */
@XmlRootElement(name = "cloudEntryPoint")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiCloudEntryPoint extends CimiCommon implements Serializable {

	/** Serial number */
	private static final long serialVersionUID = 1L;

	// ---------------------------------------- Fields

	/**
	 * Field "href".
	 * <p>
	 * </p>
	 */
	private String href;

	/**
	 * Field "machineTemplates".
	 * <p>
	 * </p>
	 */
	private CimiMachineTemplate machineTemplates;

	/**
	 * Field "machineConfigs".
	 * <p>
	 * </p>
	 */
	private CimiMachineConfiguration machineConfigs;

	/**
	 * Field "machineImages".
	 * <p>
	 * </p>
	 */
	private CimiMachineImage machineImages;

	/**
	 * Field "CredentialsTemplates".
	 * <p>
	 * </p>
	 */
	private CimiCredentialsTemplate CredentialsTemplates;

	/**
	 * Field "Credentialss".
	 * <p>
	 * </p>
	 */
	private CimiCredentials credentials;

	/**
	 * Field "machines".
	 * <p>
	 * </p>
	 */
	private CimiMachine machines;

	/**
	 * Field "volumeTemplates".
	 * <p>
	 * </p>
	 */
	private CimiVolumeTemplate volumeTemplates;

	/**
	 * Field "volumeConfigurations".
	 * <p>
	 * </p>
	 */
	private CimiVolumeConfiguration volumeConfigurations;

	/**
	 * Field "volumeImages".
	 * <p>
	 * </p>
	 */
	private CimiVolumeImage volumeImages;

	/**
	 * Field "volumes".
	 * <p>
	 * </p>
	 */
	private CimiVolume volumes;

	/**
	 * Field "jobTime".
	 * <p>
	 * </p>
	 */
	private Long jobTime;

	// ---------------------------------------- Constructors

	/**
	 * Default constructor.
	 */
	public CimiCloudEntryPoint() {
		super();
	}

	// ---------------------------------------- ???com-accesseurs???

	/**
	 * Return the value of field "machineTemplates".
	 * 
	 * @return The value
	 */
	public CimiMachineTemplate getMachineTemplates() {
		return this.machineTemplates;
	}

	/**
	 * Set the value of field "machineTemplates".
	 * 
	 * @param machineTemplates
	 *            The value
	 */
	public void setMachineTemplates(CimiMachineTemplate machineTemplates) {
		this.machineTemplates = machineTemplates;
	}

	/**
	 * Return the value of field "machineConfigs".
	 * 
	 * @return The value
	 */
	public CimiMachineConfiguration getMachineConfigs() {
		return this.machineConfigs;
	}

	/**
	 * Set the value of field "machineConfigs".
	 * 
	 * @param machineConfigs
	 *            The value
	 */
	public void setMachineConfigs(CimiMachineConfiguration machineConfigs) {
		this.machineConfigs = machineConfigs;
	}

	/**
	 * Return the value of field "machineImages".
	 * 
	 * @return The value
	 */
	public CimiMachineImage getMachineImages() {
		return this.machineImages;
	}

	/**
	 * Set the value of field "machineImages".
	 * 
	 * @param machineImages
	 *            The value
	 */
	public void setMachineImages(CimiMachineImage machineImages) {
		this.machineImages = machineImages;
	}

	/**
	 * Return the value of field "CredentialsTemplates".
	 * 
	 * @return The value
	 */
	public CimiCredentialsTemplate getCredentialsTemplates() {
		return this.CredentialsTemplates;
	}

	/**
	 * Set the value of field "CredentialsTemplates".
	 * 
	 * @param CredentialsTemplates
	 *            The value
	 */
	public void setCredentialsTemplates(CimiCredentialsTemplate CredentialsTemplates) {
		this.CredentialsTemplates = CredentialsTemplates;
	}

	/**
	 * Return the value of field "credentials".
	 * 
	 * @return The value
	 */
	public CimiCredentials getCredentials() {
		return this.credentials;
	}

	/**
	 * Set the value of field "credentials".
	 * 
	 * @param Credentialss
	 *            The value
	 */
	public void setCredentials(CimiCredentials credentials) {
		this.credentials = credentials;
	}

	/**
	 * Return the value of field "machines".
	 * 
	 * @return The value
	 */
	public CimiMachine getMachines() {
		return this.machines;
	}

	/**
	 * Set the value of field "machines".
	 * 
	 * @param machines
	 *            The value
	 */
	public void setMachines(CimiMachine machines) {
		this.machines = machines;
	}

	/**
	 * Return the value of field "volumeTemplates".
	 * 
	 * @return The value
	 */
	public CimiVolumeTemplate getVolumeTemplates() {
		return this.volumeTemplates;
	}

	/**
	 * Set the value of field "volumeTemplates".
	 * 
	 * @param volumeTemplates
	 *            The value
	 */
	public void setVolumeTemplates(CimiVolumeTemplate volumeTemplates) {
		this.volumeTemplates = volumeTemplates;
	}

	/**
	 * Return the value of field "volumeConfigurations".
	 * 
	 * @return The value
	 */
	public CimiVolumeConfiguration getVolumeConfigurations() {
		return this.volumeConfigurations;
	}

	/**
	 * Set the value of field "volumeConfigurations".
	 * 
	 * @param volumeConfigurations
	 *            The value
	 */
	public void setVolumeConfigurations(CimiVolumeConfiguration volumeConfigurations) {
		this.volumeConfigurations = volumeConfigurations;
	}

	/**
	 * Return the value of field "volumeImages".
	 * 
	 * @return The value
	 */
	public CimiVolumeImage getVolumeImages() {
		return this.volumeImages;
	}

	/**
	 * Set the value of field "volumeImages".
	 * 
	 * @param volumeImages
	 *            The value
	 */
	public void setVolumeImages(CimiVolumeImage volumeImages) {
		this.volumeImages = volumeImages;
	}

	/**
	 * Return the value of field "volumes".
	 * 
	 * @return The value
	 */
	public CimiVolume getVolumes() {
		return this.volumes;
	}

	/**
	 * Set the value of field "volumes".
	 * 
	 * @param volumes
	 *            The value
	 */
	public void setVolumes(CimiVolume volumes) {
		this.volumes = volumes;
	}

	/**
	 * Return the value of field "jobTime".
	 * 
	 * @return The value
	 */
	public Long getJobTime() {
		return this.jobTime;
	}

	/**
	 * Set the value of field "jobTime".
	 * 
	 * @param jobTime
	 *            The value
	 */
	public void setJobTime(Long jobTime) {
		this.jobTime = jobTime;
	}

	/**
	 * @return the href
	 */
	@XmlAttribute
	public String getHref() {
		return href;
	}

	/**
	 * @param href
	 *            the href to set
	 */
	public void setHref(String href) {
		this.href = href;
	}
}
