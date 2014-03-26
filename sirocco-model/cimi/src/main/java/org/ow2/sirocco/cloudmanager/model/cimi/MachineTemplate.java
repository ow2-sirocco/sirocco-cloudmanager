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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.ow2.sirocco.cloudmanager.model.cimi.event.EventLogTemplate;

/**
 * Machine template
 */
@Entity
@NamedQueries({@NamedQuery(name = "MachineTemplate.findByUuid", query = "SELECT m from MachineTemplate m WHERE m.uuid=:uuid")})
public class MachineTemplate extends CloudTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    private MachineConfiguration machineConfig;

    private MachineImage machineImage;

    private Credentials credential;

    private String systemCredentialName;

    private List<MachineVolume> volumes;

    private List<MachineVolumeTemplate> volumeTemplates;

    private List<MachineTemplateNetworkInterface> networkInterfaces;

    private String userData;

    private Machine.State initialState;

    private EventLogTemplate eventLogTemplate;

    private List<String> securityGroupUuids;

    public MachineTemplate() {

        this.networkInterfaces = new ArrayList<MachineTemplateNetworkInterface>();
    }

    @ManyToOne
    public MachineConfiguration getMachineConfig() {
        return this.machineConfig;
    }

    public void setMachineConfig(final MachineConfiguration machineConfiguration) {
        this.machineConfig = machineConfiguration;
    }

    @ManyToOne
    public MachineImage getMachineImage() {
        return this.machineImage;
    }

    public void setMachineImage(final MachineImage machineImage) {
        this.machineImage = machineImage;
    }

    @ManyToOne
    public Credentials getCredential() {
        return this.credential;
    }

    public void setCredential(final Credentials credentials) {
        this.credential = credentials;
    }

    public String getSystemCredentialName() {
        return this.systemCredentialName;
    }

    public void setSystemCredentialName(final String systemCredentialName) {
        this.systemCredentialName = systemCredentialName;
    }

    @OneToMany(fetch = FetchType.EAGER)
    public List<MachineVolume> getVolumes() {
        return this.volumes;
    }

    public void addMachineVolume(final MachineVolume mv) {
        if (this.volumes == null) {
            this.volumes = new ArrayList<MachineVolume>();
        }
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

    @OneToMany(fetch = FetchType.EAGER)
    public List<MachineVolumeTemplate> getVolumeTemplates() {
        return this.volumeTemplates;
    }

    public void addMachineVolumeTemplate(final MachineVolumeTemplate mvt) {
        if (this.volumeTemplates == null) {
            this.volumeTemplates = new ArrayList<MachineVolumeTemplate>();
        }
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

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "machinetemplate_id", referencedColumnName = "id")
    public List<MachineTemplateNetworkInterface> getNetworkInterfaces() {
        return this.networkInterfaces;
    }

    public void addNetworkInterface(final MachineTemplateNetworkInterface networkInterface) {
        if (this.networkInterfaces == null) {
            this.networkInterfaces = new ArrayList<MachineTemplateNetworkInterface>();
        }
        if (!this.getNetworkInterfaces().contains(networkInterface)) {
            this.getNetworkInterfaces().add(networkInterface);
        }
    }

    public void setNetworkInterfaces(final List<MachineTemplateNetworkInterface> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    @Lob
    @Column(length = 1024)
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

    @Transient
    public List<String> getSecurityGroupUuids() {
        if (this.securityGroupUuids == null) {
            this.securityGroupUuids = new ArrayList<>();
        }
        return this.securityGroupUuids;
    }

    public void setSecurityGroupUuids(final List<String> securityGroupUuids) {
        this.securityGroupUuids = securityGroupUuids;
    }

}
