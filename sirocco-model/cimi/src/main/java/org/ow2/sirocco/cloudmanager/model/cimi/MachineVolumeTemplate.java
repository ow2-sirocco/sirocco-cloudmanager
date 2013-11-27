/**
 *
 * SIROCCO
 * Copyright (C) 2012 France Telecom
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
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Used to attach a volume to a machine at machine creation time
 */
@Entity
public class MachineVolumeTemplate implements Serializable, Identifiable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String uuid = UUID.randomUUID().toString();

    private String initialLocation;

    private VolumeTemplate volumeTemplate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    // TODO unidirectional
    @OneToOne
    public VolumeTemplate getVolumeTemplate() {
        return this.volumeTemplate;
    }

    public void setVolumeTemplate(final VolumeTemplate volumeTemplate) {
        this.volumeTemplate = volumeTemplate;
    }

    public String getInitialLocation() {
        return this.initialLocation;
    }

    public void setInitialLocation(final String initialLocation) {
        this.initialLocation = initialLocation;
    }

}
