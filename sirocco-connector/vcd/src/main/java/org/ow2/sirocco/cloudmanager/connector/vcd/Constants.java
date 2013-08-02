/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
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
 */

package org.ow2.sirocco.cloudmanager.connector.vcd;

public final class Constants {

    public static final int RASD_RESOURCETYPE_CPU = 3;

    public static final int RASD_RESOURCETYPE_RAM = 4;

    public static final int RASD_RESOURCETYPE_DISK_CONTROLER = 6;

    public static final int RASD_RESOURCETYPE_NETWORK_ADAPTER = 10;

    public static final int RASD_RESOURCETYPE_DISK_DEVICE = 17;

    public static final String RASD_ALLOCATION_UNIT_KILOBYTE = "byte * 2^10";

    public static final String RASD_ALLOCATION_UNIT_MEGABYTE = "byte * 2^20";

    // Network (tmp)
    public static final String VAPP_NETWORK_NAME = "VappNtwk";

    // public static final String PARENT_NETWORK_NAME = "DirectNet";
}
