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
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class of references: HREF.
 */
public class HrefHelper {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(HrefHelper.class);

    /**
     * Make a HREF.
     * @param urlBase URL base of REST server
     * @param urlConstant A constant business
     * @return The HREF made
     */
    public static String makeHref(String urlBase, String urlConstant) {
        StringBuilder sb = new StringBuilder();
        sb.append(urlBase).append(urlConstant);
        return sb.toString();
    }

    /**
     * Make a HREF.
     * @param urlBase URL base of REST server
     * @param urlConstant A constant business
     * @param id Service ID
     * @return The HREF made
     */
    public static String makeHref(String urlBase, String urlConstant, Integer id) {
        StringBuilder sb = new StringBuilder();
        sb.append(urlBase).append(urlConstant).append('/').append(id);
        return sb.toString();
    }

    /**
     * Make a HREF.
     * @param urlBase URL base of REST server
     * @param urlConstant A constant business
     * @param id Service ID
     * @return The HREF made
     */
    public static String makeHref(String urlBase, String urlConstant, String id) {
        StringBuilder sb = new StringBuilder();
        sb.append(urlBase).append(urlConstant).append('/').append(id);
        return sb.toString();
    }

    /**
     * Extract the ID service of the HREF.
     * @param href The HREF
     * @return The ID service
     */
    public static Integer extractId(String href) throws Exception {
        Integer id = null;
        try {
            int posId = href.lastIndexOf('/');
            id = Integer.valueOf(href.substring(posId + 1));
        } catch (Exception e) {
            LOGGER.error("Error ID conversion with HREF : {}", href, e);
            throw e;
        }

        return id;
    }
}
