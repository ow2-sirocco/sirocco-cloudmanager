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
 *  $Id: ListVMClient.java 36 2011-06-20 14:38:41Z dangtran $
 *
 */

package org.ow2.sirocco.cloudmanager.api.tools;

import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;
import org.ow2.sirocco.cloudmanager.api.spec.VolumeInfo;
import org.ow2.sirocco.cloudmanager.api.spec.VolumeInfos;

import com.beust.jcommander.Parameter;

public class ListVolumesClient extends Client {
    @Parameter(names = "-project", description = "project id")
    private String projectId;

    public ListVolumesClient() {
        this.commandName = "sirocco-volumes-list";
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        VolumeInfos volumes;
        if (this.projectId == null) {
            volumes = proxy.listVolumes();
        } else {
            volumes = proxy.listVolumes(this.projectId);
        }
        System.out.format("%-13s %-12s %-12s %-12.12s %-12s %-13s\n", "Volume Id", "Status", "Project Id", "Name",
            "Capacity(MB)", "Description");
        if (volumes != null && volumes.getVolume() != null) {
            for (VolumeInfo vol : volumes.getVolume()) {
                System.out.format("%-13s %-12s %-12s %-12.12s %-12d %-13s\n", vol.getId(), vol.getStatus(),
                    vol.getProjectId() == null ? "-" : vol.getProjectId(), vol.getName(), vol.getCapacityInMB(),
                    vol.getDescription());
            }
        }

    }

    public static void main(final String[] args) {
        new ListVolumesClient().run(args);
    }

}
