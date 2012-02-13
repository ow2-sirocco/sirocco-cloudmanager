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

package org.ow2.sirocco.cloudmanager.api.impl;

public class Base64Coder {

    /**
     * Mapping table from 6-bit nibbles to Base64 characters.
     */
    private static char[] map1 = new char[64];

    static {
        int i = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            Base64Coder.map1[i++] = c;
        }

        for (char c = 'a'; c <= 'z'; c++) {
            Base64Coder.map1[i++] = c;
        }

        for (char c = '0'; c <= '9'; c++) {
            Base64Coder.map1[i++] = c;
        }

        Base64Coder.map1[i++] = '+';
        Base64Coder.map1[i++] = '/';
    }

    /**
     * Mapping table from Base64 characters to 6-bit nibbles.
     */
    private static byte[] map2 = new byte[128];

    static {
        for (int i = 0; i < Base64Coder.map2.length; i++) {
            Base64Coder.map2[i] = -1;
        }

        for (int i = 0; i < 64; i++) {
            Base64Coder.map2[Base64Coder.map1[i]] = (byte) i;
        }
    }

    /**
     * The <b>decode (char[] in)</b> method decodes an array of Base64-encoded
     * characters. No blanks or line breaks are allowed within the
     * Base64-encoded data.
     * 
     * @param in A character array containing the Base64-encoded data.
     * @return An array containing the decoded data bytes.
     * @throws IllegalArgumentException if the input is not valid Base64-encoded
     *         data.
     */
    private static byte[] decode(final char[] in) {
        int iLen = in.length;
        if (iLen % 4 != 0) {
            throw new IllegalArgumentException("Length of Base64 encoded input " + "string is not a multiple of 4.");
        }

        while (iLen > 0 && in[iLen - 1] == '=') {
            iLen--;
        }

        int oLen = (iLen * 3) / 4;
        byte[] out = new byte[oLen];
        int ip = 0;
        int op = 0;
        while (ip < iLen) {
            int i0 = in[ip++];
            int i1 = in[ip++];
            int i2 = ip < iLen ? in[ip++] : 'A';
            int i3 = ip < iLen ? in[ip++] : 'A';
            if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127) {
                throw new IllegalArgumentException("Illegal character in " + "Base64 encoded data.");
            }
            int b0 = Base64Coder.map2[i0];
            int b1 = Base64Coder.map2[i1];
            int b2 = Base64Coder.map2[i2];
            int b3 = Base64Coder.map2[i3];
            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0) {
                throw new IllegalArgumentException("Illegal character in Base64 " + "encoded data.");
            }
            int o0 = (b0 << 2) | (b1 >>> 4);
            int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
            int o2 = ((b2 & 3) << 6) | b3;
            out[op++] = (byte) o0;

            if (op < oLen) {
                out[op++] = (byte) o1;
            }

            if (op < oLen) {
                out[op++] = (byte) o2;
            }
        }
        return out;
    }

    /**
     * The <b>decode (String s)</b> method decodes a Base64-encoded string.
     * 
     * @param s The Base64 String to be decoded.
     * @return A String containing the decoded data.
     * @throws IllegalArgumentException if the input is not valid Base64-encoded
     *         data.
     */
    public static String decode(final String s) {
        return new String(Base64Coder.decode(s.toCharArray()));
    }

    private Base64Coder() {
    }
}
