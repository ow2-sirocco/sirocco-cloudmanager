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
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    private String publicKey;

    private Collection<MachineImage> images;

    private Collection<Machine> machines;

    private Collection<Volume> volumes;

    public User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
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

    @Column(unique = true)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

}
