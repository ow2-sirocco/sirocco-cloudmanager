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
import org.ow2.sirocco.cloudmanager.api.spec.VmInfo;
import org.ow2.sirocco.cloudmanager.api.spec.VmInfos;

import com.beust.jcommander.Parameter;

public class ListVMClient extends Client {
    @Parameter(names = "-vmId", description = "VM vmId")
    private String vmId;

    public ListVMClient() {
        this.commandName = "sirocco-vm-list";
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        VmInfos userVMs;
        if (this.vmId == null) {
            userVMs = proxy.listServers();
            System.out.format("%-12s %-13s %-8s %-13s %-16s %-10s %-10s\n", "VM name", "vmId", "size", "imageId", "private IP",
                "project", "status");
            if (userVMs != null && userVMs.getServer() != null) {
                for (VmInfo vm : userVMs.getServer()) {
                    System.out.format("%-12s %-13s %-8s %-13s %-16s %-10s %-10s\n", vm.getName(), vm.getInstanceId(),
                        vm.getSize(), vm.getImageId(), vm.getPrivateIp(), vm.getProjectId(), vm.getStatus());
                }
            }
        } else {
            VmInfo vm = proxy.listServers(this.vmId);
            System.out.format("%-12s %-13s %-8s %-13s %-16s %-10s %-10s\n", "VM name", "vmId", "size", "imageId", "private IP",
                "project", "status");
            if (vm != null) {
                System.out.format("%-12s %-13s %-8s %-13s %-16s %-10s %-10s\n", vm.getName(), vm.getInstanceId(), vm.getSize(),
                    vm.getImageId(), vm.getPrivateIp(), vm.getProjectId(), vm.getStatus());
            }
        }

    }

    @Override
    protected Object getOptions() {
        return this;
    }

    public static void main(final String[] args) {
        new ListVMClient().run(args);
    }

}
