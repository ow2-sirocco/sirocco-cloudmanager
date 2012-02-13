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

import java.util.HashMap;
import java.util.List;

import org.ow2.sirocco.cloudmanager.api.spec.ServerSpec;
import org.ow2.sirocco.cloudmanager.api.spec.UserAPI;
import org.ow2.sirocco.cloudmanager.api.spec.VmInfo;

import com.beust.jcommander.Parameter;

public class CreateVMClient extends Client {
    @Parameter(names = "-project", description = "project id", required = true)
    private String projectId;

    @Parameter(names = "-accountId", description = "account id", required = false)
    private String cloudProviderAccountId;

    @Parameter(names = "-name", description = "VM name", required = true)
    private String vmName;

    @Parameter(names = "-image", description = "VM imageId", required = true)
    private Integer vmImageId;

    @Parameter(names = "-size", description = "VM size", required = true)
    private String vmSize;

    @Parameter(names = {"-userdata", "-ud"}, arity = 2, description = "User data: key value")
    public List<String> osData;

    @Parameter(names = {"-reservationId"}, description = "Reservation id (optional)", required = false)
    public String reservationId;

    public CreateVMClient() {
        this.commandName = "sirocco-create-vm";
    }

    @Override
    protected Object getOptions() {
        return this;
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        ServerSpec spec = new ServerSpec();
        spec.setName(this.vmName);
        spec.setImageId(this.vmImageId);
        spec.setSize(this.vmSize);
        spec.setProjectId(this.projectId);
        spec.setCloudProviderAccountId(this.cloudProviderAccountId);
        HashMap<String, String> userData = new HashMap<String, String>();
        if (this.osData != null) {
            for (int i = 0; i < this.osData.size(); i += 2) {
                userData.put(this.osData.get(i), this.osData.get(i + 1));
            }
        } // no "else" needed.

        spec.setUserData(userData);

        spec.setReservationId(this.reservationId);

        VmInfo vm = proxy.createServer(spec);
        System.out.format("%-12s %-13s %-8s %-13s %-16s %-10s %-10s\n", "VM name", "id", "size", "imageId", "private IP",
            "project", "status");
        System.out.format("%-12s %-13s %-8s %-13s %-16s %-10s %-10s\n", vm.getName(), vm.getInstanceId(), vm.getSize(),
            vm.getImageId(), vm.getPrivateIp(), vm.getProjectId(), vm.getStatus());
    }

    public static void main(final String[] args) {
        new CreateVMClient().run(args);
    }

}
