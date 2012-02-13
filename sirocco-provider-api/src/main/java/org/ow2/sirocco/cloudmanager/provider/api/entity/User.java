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
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.provider.api.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.UserVO;

@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String apiPassword;

    private Date createDate;

    private String email;

    private String firstName;

    private String lastName;

    /**
     * This is the User's identifier.
     */
    private String username;

    private String password;

    private String publicKey;

    private boolean isAdmin = false;

    private Project defaultProject;

    private Collection<RoleAssignment> assignments;

    private Collection<MachineImage> images;

    private Collection<Machine> machines;

    private Collection<Volume> volumes;

    private Collection<SystemTemplate> systemTemplates;

    private Collection<SystemInstance> systemInstances;

    public User() {
        this.assignments = new ArrayList<RoleAssignment>();
        this.systemTemplates = new ArrayList<SystemTemplate>();
        this.systemInstances = new ArrayList<SystemInstance>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    @OneToOne(cascade = CascadeType.ALL)
    public Project getDefaultProject() {
        return this.defaultProject;
    }

    public void setDefaultProject(final Project defaultProject) {
        this.defaultProject = defaultProject;
    }

    @OneToMany(mappedBy = "user")
    public Collection<RoleAssignment> getAssignments() {
        return this.assignments;
    }

    @OneToMany(mappedBy = "user")
    public Collection<MachineImage> getImages() {
        return this.images;
    }

    @OneToMany(mappedBy = "user")
    public Collection<Machine> getMachines() {
        return this.machines;
    }

    @OneToMany(mappedBy = "user")
    public Collection<Volume> getVolumes() {
        return this.volumes;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreateDate() {
        return this.createDate;
    }

    @Column(columnDefinition = "TEXT")
    public String getPublicKey() {
        return this.publicKey;
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

    public void setApiPassword(final String apiPassword) {
        this.apiPassword = apiPassword;
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

    public void setAssignments(final Collection<RoleAssignment> assignments) {
        this.assignments = assignments;
    }

    public void setPublicKey(final String publicKey) {
        this.publicKey = publicKey;
    }

    public void setImages(final Collection<MachineImage> images) {
        this.images = images;
    }

    public void setMachines(final Collection<Machine> machines) {
        this.machines = machines;
    }

    public void setVolumes(final Collection<Volume> volumes) {
        this.volumes = volumes;
    }

    public String getApiPassword() {
        return this.apiPassword;
    }

    public void setAdmin(final boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean getAdmin() {
        return this.isAdmin;
    }

    @Column(unique = true)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Collection<SystemTemplate> getSystemTemplates() {
        return this.systemTemplates;
    }

    public void setSystemTemplates(final Collection<SystemTemplate> systemTemplates) {
        this.systemTemplates = systemTemplates;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Collection<SystemInstance> getSystemInstances() {
        return this.systemInstances;
    }

    public void setSystemInstances(final Collection<SystemInstance> systemInstances) {
        this.systemInstances = systemInstances;
    }

    /**
     * Builds a new UserVO from its business object counterpart
     * 
     * @param from the User business object
     * @param projectId Id of the project.
     * @param withProjectsList TODO
     * @return a new UserVO
     */
    public UserVO toValueObject() {
        UserVO userVo = new UserVO();
        userVo.setLastName(this.getLastName());
        userVo.setFirstName(this.getFirstName());
        userVo.setUsername(this.getUsername());
        userVo.setEmail(this.getEmail());
        userVo.setPublicKey(this.getPublicKey());
        userVo.setCreateDate(this.getCreateDate());
        userVo.setPassword(this.getPassword());
        userVo.setApiPassword(this.getApiPassword());
        userVo.setAdmin(this.getAdmin());
        if (this.getDefaultProject() != null) {
            userVo.setDefaultProject(this.getDefaultProject().toValueObject());
        }
        return userVo;
    }

}
