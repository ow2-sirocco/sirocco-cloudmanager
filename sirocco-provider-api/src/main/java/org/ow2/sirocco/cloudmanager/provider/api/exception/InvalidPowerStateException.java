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

package org.ow2.sirocco.cloudmanager.provider.api.exception;

/**
 * Signals that an operation has been attempted on a virtual machine which is
 * not allowed given the current power state of the VM, for example attempting
 * to stop a stopped virtual machine.
 */
public class InvalidPowerStateException extends CloudProviderException {
    private static final long serialVersionUID = 1303861300912898238L;

    /**
     * Constructs a InvalidPowerStateException with null as its error detail
     * message.
     */
    public InvalidPowerStateException() {
    }

    /**
     * Constructs a InvalidPowerStateException with a detail message
     * 
     * @param message the detail message
     */
    public InvalidPowerStateException(String message) {
        super(message);
    }

}
