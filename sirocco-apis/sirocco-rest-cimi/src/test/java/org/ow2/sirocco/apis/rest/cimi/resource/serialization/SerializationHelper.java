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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id:  $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.resource.serialization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SerializationHelper {

    /**
     * Get a ressource in classpath and convert it as Reader.
     * 
     * @param classLocation This class allows to find the package which is
     *        located the ressoure
     * @param resourceName The name of ressource
     * @return The Reader for the ressource
     * @throws IOException In case of IO error
     */
    public static Reader getResourceAsReader(final Class<?> classLocation, final String resourceName) throws IOException {
        InputStream in = classLocation.getResourceAsStream(resourceName);
        if (null == in) {
            throw new IOException("Resource not found : " + resourceName);
        }
        return new InputStreamReader(in);
    }

    /**
     * Get a ressource in classpath and convert it as string with the default
     * encoding.
     * 
     * @param classLocation This class allows to find the package which is
     *        located the ressoure
     * @param resourceName The name of ressource
     * @return The string with the content of ressource
     * @throws IOException In case of IO error
     */
    public static String getResourceAsString(final Class<?> classLocation, final String resourceName) throws IOException {
        return SerializationHelper.getResourceAsString(classLocation, resourceName, null);
    }

    /**
     * Get a ressource in classpath and convert it as string.
     * 
     * @param classLocation This class allows to find the package which is
     *        located the ressoure
     * @param resourceName The name of ressource
     * @param encoding The encoding name. If null the default encoding is used
     * @return The string with the content of ressource
     * @throws IOException In case of IO error
     */
    public static String getResourceAsString(final Class<?> classLocation, final String resourceName, final String encoding)
        throws IOException {
        InputStream in = classLocation.getResourceAsStream(resourceName);
        if (null == in) {
            throw new IOException("Resource not found : " + resourceName);
        }
        return SerializationHelper.convertStreamToString(classLocation.getResourceAsStream(resourceName), encoding);
    }

    /**
     * Convert a stream in string.
     * 
     * @param inStream The input stream
     * @param encoding The encoding name. If null the default encoding is used
     * @return The string with the content of stream
     * @throws IOException In case of IO error
     */
    public static String convertStreamToString(final InputStream inStream, final String encoding) throws IOException {
        InputStreamReader inReader;
        if (null == encoding) {
            inReader = new InputStreamReader(inStream);
        } else {
            inReader = new InputStreamReader(inStream, encoding);
        }
        BufferedReader reader = new BufferedReader(inReader);
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            line = reader.readLine();
            if (null != line) {
                sb.append(line);
                while ((line = reader.readLine()) != null) {
                    sb.append("\n");
                    sb.append(line);
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                throw e;
            }
        }
        return sb.toString();
    }

    /**
     * Class who extends ToStringStyle to manage the recursive depth with
     * ToStringBuilder.
     */
    public static class RecursiveToStringStyle extends ToStringStyle {

        private static final long serialVersionUID = 1L;

        private static final int INFINITE_DEPTH = -1;

        /**
         * Setting {@link #maxDepth} to 0 will have the same effect as using
         * original {@link #ToStringStyle}: it will print all 1st level values
         * without traversing into them. Setting to 1 will traverse up to 2nd
         * level and so on.
         */
        private int maxDepth;

        private int depth;

        public RecursiveToStringStyle() {
            this(RecursiveToStringStyle.INFINITE_DEPTH);
        }

        public RecursiveToStringStyle(final int maxDepth) {
            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);

            this.maxDepth = maxDepth;
        }

        @Override
        protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object value) {
            if (value.getClass().getName().startsWith("java.lang.")
                || (this.maxDepth != RecursiveToStringStyle.INFINITE_DEPTH && this.depth >= this.maxDepth)) {
                buffer.append(value);
            } else {
                this.depth++;
                buffer.append(ReflectionToStringBuilder.toString(value, this));
                this.depth--;
            }
        }

        // another helpful method
        @Override
        protected void appendDetail(final StringBuffer buffer, final String fieldName, final Collection<?> coll) {
            this.depth++;
            buffer.append(ReflectionToStringBuilder.toString(coll.toArray(), this, true, true));
            this.depth--;
        }
    }
}
