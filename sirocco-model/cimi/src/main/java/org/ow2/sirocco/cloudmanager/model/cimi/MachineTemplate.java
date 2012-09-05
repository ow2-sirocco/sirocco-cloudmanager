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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLogTemplate;

@Entity
//@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class MachineTemplate extends CloudTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    private MachineConfiguration machineConfiguration;

    private MachineImage machineImage;

    private Credentials credentials;

    private List<MachineVolume> volumes;

    private List<MachineVolumeTemplate> volumeTemplates;

    private List<MachineTemplateNetworkInterface> networkInterfaces;

    private String userData;

    private Machine.State initialState;

    private EventLogTemplate eventLogTemplate;

    public MachineTemplate() {

        this.networkInterfaces = new ArrayList<MachineTemplateNetworkInterface>();
    }

    @ManyToOne
    //@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public MachineConfiguration getMachineConfiguration() {
        return this.machineConfiguration;
    }

    public void setMachineConfiguration(final MachineConfiguration machineConfiguration) {
        this.machineConfiguration = machineConfiguration;
    }

    @ManyToOne
    //@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public MachineImage getMachineImage() {
        return this.machineImage;
    }

    public void setMachineImage(final MachineImage machineImage) {
        this.machineImage = machineImage;
    }

    @ManyToOne
    //@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public Credentials getCredentials() {
        return this.credentials;
    }

    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    //@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public List<MachineVolume> getVolumes() {
        return this.volumes;
    }

    public void addMachineVolume(final MachineVolume mv) {
        if (!this.getVolumes().contains(mv)) {
            this.getVolumes().add(mv);
        }
    }

    public void removeMachineVolume(final MachineVolume mv) {
        if (this.getVolumes().contains(mv)) {
            this.getVolumes().remove(mv);
        }
    }

    public void setVolumes(final List<MachineVolume> volumes) {
        this.volumes = volumes;
    }

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    //@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public List<MachineVolumeTemplate> getVolumeTemplates() {
        return this.volumeTemplates;
    }

    public void addMachineVolumeTemplate(final MachineVolumeTemplate mvt) {
        if (!this.getVolumeTemplates().contains(mvt)) {
            this.getVolumeTemplates().add(mvt);
        }
    }

    public void removeMachineVolumeTemplate(final MachineVolumeTemplate mvt) {
        if (this.getVolumeTemplates().contains(mvt)) {
            this.getVolumeTemplates().remove(mvt);
        }
    }

    public void setVolumeTemplates(final List<MachineVolumeTemplate> volumeTemplates) {
        this.volumeTemplates = volumeTemplates;
    }

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "machinetemplate_id", referencedColumnName = "id")
    //@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    public List<MachineTemplateNetworkInterface> getNetworkInterfaces() {
        return this.networkInterfaces;
    }

    public void addNetworkInterface(final MachineTemplateNetworkInterface networkInterface) {
        if (!this.getNetworkInterfaces().contains(networkInterface)) {
            this.getNetworkInterfaces().add(networkInterface);
        }
    }

    public void setNetworkInterfaces(final List<MachineTemplateNetworkInterface> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    public String getUserData() {
        return this.userData;
    }

    public void setUserData(final String userData) {
        this.userData = userData;
    }

    public Machine.State getInitialState() {
        return this.initialState;
    }

    public void setInitialState(final Machine.State initialState) {
        this.initialState = initialState;
    }

    @OneToOne
    public EventLogTemplate getEventLogTemplate() {
        return this.eventLogTemplate;
    }

    public void setEventLogTemplate(final EventLogTemplate eventLogTemplate) {
        this.eventLogTemplate = eventLogTemplate;
    }

}
