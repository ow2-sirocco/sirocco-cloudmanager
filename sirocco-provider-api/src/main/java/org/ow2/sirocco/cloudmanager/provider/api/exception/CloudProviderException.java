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
 * Base class of the exception classes that can be raised by the CloudAdmin
 * interface. This exception may be thrown in case of a runtime error that is
 * not covered by the derived exceptions.
 * 
 * @see org.ow2.sirocco.cloudmanager.service.IMachineManager
 */
public class CloudProviderException extends Exception {
    private static final long serialVersionUID = 4102349022960352481L;

    /**
     * Constructs a CloudAdminException with null as its error detail message.
     */
    public CloudProviderException() {
    }

    /**
     * Constructs a CloudAdminException with the specified detailed message
     * 
     * @param message the detail message
     */
    public CloudProviderException(String message) {
        super(message);
    }

    /**
     * Constructs a CloudAdminException with the specified cause.
     * 
     * @param cause the cause
     */
    public CloudProviderException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a CloudAdminException with the specified detail message and
     * cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public CloudProviderException(String message, Throwable cause) {
        super(message, cause);
    }

}
