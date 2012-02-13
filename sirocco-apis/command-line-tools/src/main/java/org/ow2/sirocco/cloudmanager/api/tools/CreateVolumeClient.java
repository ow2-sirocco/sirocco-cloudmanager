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
import org.ow2.sirocco.cloudmanager.api.spec.VolumeInfo;
import org.ow2.sirocco.cloudmanager.api.spec.VolumeSpec;

import com.beust.jcommander.Parameter;

public class CreateVolumeClient extends Client {
    @Parameter(names = "-project", description = "project id", required = true)
    private String projectId;

    @Parameter(names = "-name", description = "Volume name", required = true)
    private String volName;

    @Parameter(names = "-desc", description = "Volume description", required = true)
    private String volDesc;

    @Parameter(names = "-size", description = "Volume size(MB)", required = true)
    private long volSizeMB;

    @Parameter(names = "-accountId", description = "account id", required = true)
    private String cloudProviderAccountId;

    @Parameter(names = "-location", description = "Location", required = false)
    private String location;

    public CreateVolumeClient() {
        this.commandName = "sirocco-create-volume";
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        VolumeSpec spec = new VolumeSpec();
        spec.setName(this.volName);
        spec.setDescription(this.volDesc);
        spec.setCapacityInMB(this.volSizeMB);
        spec.setProjectId(this.projectId);
        spec.setCloudProviderAccountId(this.cloudProviderAccountId);
        spec.setLocation(this.location);

        VolumeInfo vol = proxy.createVolume(spec);
        System.out.format("%-13s %-12s %-12s %-12.12s %-12s %-13s\n", "Volume Id", "Status", "Project Id", "Name",
            "Capacity(MB)", "Description");
        System.out.format("%-13s %-12s %-12s %-12.12s %-12d %-13s\n", vol.getId(), vol.getStatus(),
            vol.getProjectId() == null ? "-" : vol.getProjectId(), vol.getName(), vol.getCapacityInMB(), vol.getDescription());
    }

    public static void main(final String[] args) {
        new CreateVolumeClient().run(args);
    }

}
