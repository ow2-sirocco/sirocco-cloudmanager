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

/**
 * Utility class for references: HREF.
 */
public class HrefHelper {

    /**
     * Make a HREF.
     * 
     * @param urlBase URL base of REST server
     * @param urlConstant A constant business
     * @return The HREF made
     */
    public static String makeHref(final String urlBase, final String urlConstant) {
        return HrefHelper.makeHref(urlBase, urlConstant, (String) null);

    }

    /**
     * Make a HREF.
     * 
     * @param urlBase URL base of REST server
     * @param urlConstant A constant business
     * @param id Service ID
     * @return The HREF made
     */
    public static String makeHref(final String urlBase, final String urlConstant, final Integer id) {
        String href;
        if (null != id) {
            href = HrefHelper.makeHref(urlBase, urlConstant, id.toString());
        } else {
            href = HrefHelper.makeHref(urlBase, urlConstant, (String) null);
        }
        return href;
    }

    /**
     * Make a HREF.
     * 
     * @param urlBase URL base of REST server
     * @param urlConstant A constant business
     * @param id Service ID
     * @return The HREF made
     */
    public static String makeHref(final String urlBase, final String urlConstant, final String id) {
        StringBuilder sb = new StringBuilder();
        sb.append(urlBase).append(urlConstant);
        if (null != id) {
            sb.append('/').append(id);
        }
        return sb.toString();
    }

    /**
     * Extract the ID service of the HREF.
     * 
     * @param href The HREF
     * @return The ID service
     */
    public static String extractIdString(final String href) {
        String id = null;
        int posId = href.lastIndexOf('/');
        id = href.substring(posId + 1);
        return id;
    }

    /**
     * Extract the ID service of the HREF.
     * 
     * @param href The HREF
     * @return The ID service
     */
    public static Integer extractId(final String href) {
        return Integer.valueOf(HrefHelper.extractIdString(href));
    }

}
