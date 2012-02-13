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
import org.ow2.sirocco.cloudmanager.api.spec.VmImageInfo;
import org.ow2.sirocco.cloudmanager.api.spec.VmImageInfos;

import com.beust.jcommander.Parameter;

public class ListImagesClient extends Client {
    @Parameter(names = "-project", description = "project id")
    private String projectId;

    public ListImagesClient() {
        this.commandName = "sirocco-image-list";
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        VmImageInfos images;
        if (this.projectId == null) {
            images = proxy.listImages();
        } else {
            images = proxy.listImages(this.projectId);
        }
        System.out.format("%-13s %-12s %-20s %-10s %-13s\n", "Image Id", "Project Id", "Name", "Visibility", "Description");
        for (VmImageInfo image : images.getImage()) {
            System.out.format("%-13s %-12s %-20.20s %-10s %-13s\n", image.getImageId(), image.getProjectId() == null ? "-"
                : image.getProjectId(), image.getName(), image.getVisibility(), image.getDescription());
        }

    }

    public static void main(final String[] args) {
        new ListImagesClient().run(args);
    }

}
