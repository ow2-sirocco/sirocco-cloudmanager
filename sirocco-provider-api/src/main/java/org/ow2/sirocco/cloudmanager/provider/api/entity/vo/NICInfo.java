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

package org.ow2.sirocco.cloudmanager.provider.api.entity.vo;

import java.io.Serializable;

/**
 * Data object representing a network interface
 */
@SuppressWarnings("serial")
public class NICInfo implements Serializable {

    private String macAddress;

    private String deviceName;

    /**
     * Returns the MAC address of the network interface
     * 
     * @return the MAC address of the network interface
     */
    public String getMacAddress() {
        return this.macAddress;
    }

    /**
     * Sets the MAC address of the network interface
     * 
     * @param macAddress the MAC address of the network interface
     */
    public void setMacAddress(final String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * Returns the device name of the network interface
     * 
     * @return the device name of the network interface
     */
    public String getDeviceName() {
        return this.deviceName;
    }

    /**
     * Sets the device name of the network interface
     * 
     * @param deviceName the device name of the network interface
     */
    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }
}
