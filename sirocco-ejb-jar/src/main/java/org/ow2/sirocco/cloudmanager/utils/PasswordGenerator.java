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

package org.ow2.sirocco.cloudmanager.utils;

public final class PasswordGenerator {

    private PasswordGenerator() {

    }

    /** Minimum length for a decent password */
    public static final int MIN_LENGTH = 10;

    /** The random number generator. */
    private static java.util.Random r = new java.util.Random();

    /*
     * Set of characters that is valid. Must be printable, memorable, and "won't
     * break HTML" (i.e., not ' <', '>', '&', '=', ...). or break shell commands
     * (i.e., not ' <', '>', '$', '!', ...). I, L and O are good to leave out,
     * as are numeric zero and one.
     */
    private static char[] goodChar = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u',
        'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
        'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '@',};

    /* Generate a Password object with a random password. */
    public static String getNext() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < PasswordGenerator.MIN_LENGTH; i++) {
            sb.append(PasswordGenerator.goodChar[PasswordGenerator.r.nextInt(PasswordGenerator.goodChar.length)]);
        }
        return sb.toString();
    }

}
