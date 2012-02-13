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
import org.ow2.sirocco.cloudmanager.api.spec.VmSize;
import org.ow2.sirocco.cloudmanager.api.spec.VmSizes;

public class ListVMSizesClient extends Client {
    public ListVMSizesClient() {
        this.commandName = "sirocco-size-list";
    }

    @Override
    protected Object getOptions() {
        return null;
    }

    @Override
    protected void operation(final UserAPI proxy) throws Exception {
        VmSizes sizes = proxy.listVmSizes();
        System.out.format("%-10s %-6s %-12s\n", "Name", "#CPU", "Memory(MB)");
        for (VmSize size : sizes.getSize()) {
            System.out.format("%-10s %-6d %-12d\n", size.getName(), size.getNumCpu(), size.getMemorySizeMB());
        }
    }

    public static void main(final String[] args) {
        new ListVMSizesClient().run(args);
    }

}
