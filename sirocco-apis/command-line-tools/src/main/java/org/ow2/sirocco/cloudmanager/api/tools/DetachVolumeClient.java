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

package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;
import org.ow2.sirocco.cloudmanager.api.spec.VolumeAttachmentSpec;

import com.beust.jcommander.Parameter;

public class DetachVolumeClient extends Client {
    @Parameter(names = "-vmId", description = "VM Id", required = true)
    private String vmId;

    @Parameter(names = "-volumeId", description = "Volume Id", required = true)
    private String volumeId;

    public DetachVolumeClient() {
        this.commandName = "sirocco-detach-volume";
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        VolumeAttachmentSpec spec = new VolumeAttachmentSpec();
        spec.setVmId(this.vmId);
        proxy.detachVolume(this.volumeId, spec);
    }

    public static void main(final String[] args) {
        new DetachVolumeClient().run(args);
    }

}
