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
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CollectionOfElements;

@Entity
public class MachineTemplate extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private MachineConfiguration machineConfiguration;

    private MachineImage machineImage;

    private MachineAdmin machineAdmin;

    private List<String> attachmentPoints;

    private List<Volume> volumes;

    /** private List<VolumeTemplate> volumesTemplates; */
    private List<NetworkInterface> networkInterfaces;

    public MachineTemplate() {
        this.volumes = new ArrayList<Volume>();
        /** this.volumeTemplates = new ArrayList<VolumeTemplate>(); */
        this.attachmentPoints = new ArrayList<String>();
        this.networkInterfaces = new ArrayList<NetworkInterface>();
    }

    @ManyToOne
    public MachineConfiguration getMachineConfiguration() {
        return this.machineConfiguration;
    }

    public void setMachineConfiguration(final MachineConfiguration machineConfiguration) {
        this.machineConfiguration = machineConfiguration;
    }

    @ManyToOne
    public MachineImage getMachineImage() {
        return this.machineImage;
    }

    public void setMachineImage(final MachineImage machineImage) {
        this.machineImage = machineImage;
    }

    @ManyToOne
    public MachineAdmin getMachineAdmin() {
        return this.machineAdmin;
    }

    public void setMachineAdmin(final MachineAdmin machineAdmin) {
        this.machineAdmin = machineAdmin;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "MACHINETEMPLATE_VOLUME")
    public List<Volume> getVolumes() {
        return this.volumes;
    }

    public void setVolumes(final List<Volume> volumes) {
        this.volumes = volumes;
    }

    /**
     * @ManyToMany(cascade = CascadeType.PERSIST, CascadeType.MERGE)
     * @JoinTable(name="MACHINETEMPLATE_VOLUMETEMPLATE") public
     *                                                   List<VolumeTemplate>
     *                                                   getVolumeTemplates() {
     *                                                   return
     *                                                   this.volumeTemplates; }
     *                                                   public void
     *                                                   setVolumeTemplates
     *                                                   (final
     *                                                   List<VolumeTemplate>
     *                                                   volumeTemplates) {
     *                                                   this.volumeTemplates =
     *                                                   volumeTemplates; }
     */
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
}
