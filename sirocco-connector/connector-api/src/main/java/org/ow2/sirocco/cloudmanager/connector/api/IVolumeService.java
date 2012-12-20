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

package org.ow2.sirocco.cloudmanager.connector.api;

import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;

public interface IVolumeService {

    Job createVolume(VolumeCreate volumeCreate) throws ConnectorException;

    Job deleteVolume(String volumeId) throws ConnectorException;

    Volume.State getVolumeState(String volumeId) throws ConnectorException;

    Volume getVolume(String volumeId) throws ConnectorException;

    Job createVolumeImage(VolumeImage volumeImage) throws ConnectorException;

    Job createVolumeSnapshot(String volumeId, VolumeImage volumeImage) throws ConnectorException;

    VolumeImage getVolumeImage(String volumeImageId) throws ConnectorException;

    Job deleteVolumeImage(String volumeImageId) throws ConnectorException;

}
