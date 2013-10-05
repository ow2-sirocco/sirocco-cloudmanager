/**
 *
 * SIROCCO
 * Copyright (C) 2013 Orange Labs
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
 *
 */
package org.ow2.sirocco.cloudmanager.connector.api;

/**
 * The InvalidRequestException should be thrown to indicate that a connector
 * could not process a request because it was invalid in some way
 */
public class InvalidRequestException extends ConnectorException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a InvalidRequestException with null as its error detail
     * message.
     */
    public InvalidRequestException() {
    }

    /**
     * Constructs a InvalidRequestException with the specified detailed message
     * 
     * @param message the detail message
     */
    public InvalidRequestException(final String message) {
        super(message);
    }

    /**
     * Constructs a InvalidRequestException with the specified cause.
     * 
     * @param cause the cause
     */
    public InvalidRequestException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a InvalidRequestException with the specified detail message
     * and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public InvalidRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
