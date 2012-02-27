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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  $Id: User.java 794 2011-12-21 20:39:46Z dangtran $
 *
 */

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Date createDate;

    private String email;

    private String firstName;

    private String lastName;

    /**
     * This is the User's identifier.
     */
    private String username;

    private String password;

    private Set<MachineImage> images;

    private Set<Machine> machines;

    private Set<Volume> volumes;

    private Set<MachineTemplate> machineTemplates;

    private Set<MachineConfiguration> machineConfigurations;

    private Set<VolumeTemplate> volumeTemplates;

    private Set<VolumeConfiguration> volumeConfigurations;

    private Set<CloudProviderAccount> cloudProviderAccounts;

    public User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    @OneToMany(mappedBy = "user")
    public Set<MachineImage> getImages() {
        return this.images;
    }

    @OneToMany(mappedBy = "user")
    public Set<Machine> getMachines() {
        return this.machines;
    }

    @OneToMany(mappedBy = "user")
    public Set<Volume> getVolumes() {
        return this.volumes;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreateDate() {
        return this.createDate;
    }

    public String getEmail() {
        return this.email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setImages(final Set<MachineImage> images) {
        this.images = images;
    }

    public void setMachines(final Set<Machine> machines) {
        this.machines = machines;
    }

    public void setVolumes(final Set<Volume> volumes) {
        this.volumes = volumes;
    }

    @Column(unique = true)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    @OneToMany(mappedBy = "user")
    public Set<MachineTemplate> getMachineTemplates() {
        return this.machineTemplates;
    }

    public void setMachineTemplates(final Set<MachineTemplate> machineTemplates) {
        this.machineTemplates = machineTemplates;
    }

    @OneToMany(mappedBy = "user")
    public Set<MachineConfiguration> getMachineConfigurations() {
        return this.machineConfigurations;
    }

    public void setMachineConfigurations(final Set<MachineConfiguration> machineConfigurations) {
        this.machineConfigurations = machineConfigurations;
    }

    @OneToMany(mappedBy = "user")
    public Set<VolumeTemplate> getVolumeTemplates() {
        return this.volumeTemplates;
    }

    public void setVolumeTemplates(final Set<VolumeTemplate> volumeTemplates) {
        this.volumeTemplates = volumeTemplates;
    }

    @OneToMany(mappedBy = "user")
    public Set<VolumeConfiguration> getVolumeConfigurations() {
        return this.volumeConfigurations;
    }

    public void setVolumeConfigurations(final Set<VolumeConfiguration> volumeConfigurations) {
        this.volumeConfigurations = volumeConfigurations;
    }

    @ManyToMany(mappedBy = "users")
    public Set<CloudProviderAccount> getCloudProviderAccounts() {
        return this.cloudProviderAccounts;
    }

    public void setCloudProviderAccounts(final Set<CloudProviderAccount> cloudProviderAccounts) {
        this.cloudProviderAccounts = cloudProviderAccounts;
    }

}
