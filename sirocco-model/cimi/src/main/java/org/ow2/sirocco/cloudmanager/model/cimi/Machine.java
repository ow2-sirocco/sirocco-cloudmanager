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

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.CollectionOfElements;

@Entity
@NamedQueries({@NamedQuery(name = "GET_MACHINE_BY_STATE", query = "SELECT v from Machine v WHERE v.state=:state")})
public class Machine extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String GET_MACHINE_BY_STATE = "GET_MACHINE_BY_STATE";

    public static enum State {
        CREATING, STARTING, STARTED, STOPPING, STOPPED, PAUSING, PAUSED, SUSPENDING, SUSPENDED, DELETING, DELETED, ERROR
    }

    private State state;

    private Cpu cpu;

    /* private List<String> attachmentPoints; */
    private List<Volume> volumes;

    /** private List<VolumeTemplate> volumeTemplates; */
    private List<NetworkInterface> networkInterfaces;

    private CloudProviderAccount cloudProviderAccount;

    private Memory memory;

    private List<Disk> disks;

    private List<String> attachmentPoints;

    /** private List<VolumeTemplate> volumeTemplates; */

    public Machine() {
        this.volumes = new ArrayList<Volume>();
        /** this.volumeTemplates = new ArrayList<VolumeTemplate>(); */
        this.attachmentPoints = new ArrayList<String>();
        this.disks = new ArrayList<Disk>();
        this.networkInterfaces = new ArrayList<NetworkInterface>();
    }

    @Enumerated(EnumType.STRING)
    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    @Embedded
    public Cpu getCpu() {
        return this.cpu;
    }

    public void setCpu(final Cpu cpu) {
        this.cpu = cpu;
    }

    @Embedded
    public Memory getMemory() {
        return this.memory;
    }

    public void setMemory(final Memory memory) {
        this.memory = memory;
    }

    @CollectionOfElements
    public List<Disk> getDisks() {
        return this.disks;
    }

    public void setDisks(List<Disk> disks) {
        disks = this.disks;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "MACHINE_VOLUME")
    public List<Volume> getVolumes() {
        return this.volumes;
    }

    public void setVolumes(final List<Volume> volumes) {
        this.volumes = volumes;
    }

    @CollectionOfElements
    public List<String> getAttachmentPoints() {
        return this.attachmentPoints;
    }

    public void setAttachmentPoints(final List<String> attachmentPoints) {
        this.attachmentPoints = attachmentPoints;
    }

    @CollectionOfElements
    public List<NetworkInterface> getNetworkInterfaces() {
        return this.networkInterfaces;
    }

    public void setNetworkInterfaces(final List<NetworkInterface> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    @ManyToOne
    public CloudProviderAccount getCloudProviderAccount() {
        return this.cloudProviderAccount;
    }

    public void setCloudProviderAccount(final CloudProviderAccount cloudProviderAccount) {
        this.cloudProviderAccount = cloudProviderAccount;
    }
}
