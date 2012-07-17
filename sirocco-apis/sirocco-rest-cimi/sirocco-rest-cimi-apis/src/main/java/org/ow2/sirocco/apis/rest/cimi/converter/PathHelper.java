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

import java.util.regex.Pattern;

import org.ow2.sirocco.apis.rest.cimi.domain.ExchangeType;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

/**
 * Utility class for CIMI paths.
 */
public class PathHelper {

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
     * Make a CIMI URI.
     * 
     * @param suffixKey The key to use like suffix
     * @return The CIMI URI made
     */
    public static String makeCimiURI(final String suffixKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantsPath.CIMI_XML_NAMESPACE).append('/');
        if (null != suffixKey) {
            sb.append(suffixKey);
        }
        return sb.toString();
    }

    /**
     * Extract the suffix key of a CIMI URI.
     * 
     * @param uri A complete CIMI URI
     * @return The suffix key or null
     */
    public static String extractSuffixKeyOfCimiURI(final String cimiURI) {
        String key = null;
        if (null != cimiURI) {
            if (true == cimiURI.startsWith(ConstantsPath.CIMI_XML_NAMESPACE + '/')) {
                key = cimiURI.substring(ConstantsPath.CIMI_XML_NAMESPACE.length() + 1);
            }
        }
        return key;
    }

    /**
     * Extract the ID service of the HREF.
     * 
     * @param href The HREF
     * @return The ID service
     */
    public static String extractIdString(final String href) {
        String id = null;
        if (null != href) {
            int posId = href.lastIndexOf('/');
            id = href.substring(posId + 1);
        }
        return id;
    }

    /**
     * Extract the ID service of the HREF.
     * 
     * @param href The HREF
     * @return The ID service
     */
    public static Integer extractId(final String href) {
        return Integer.valueOf(PathHelper.extractIdString(href));
    }

    /**
     * Extract the pathname with a complete path.
     * 
     * @param path The complete path
     * @return The pathname
     */
    public static String extractPathname(final String path) {
        String pathname = null;
        int index = path.lastIndexOf('/');
        if (index > -1) {
            pathname = path.substring(0, index);
        } else {
            pathname = path;
        }
        return pathname;
    }

    /**
     * Find the ExchangeType with the path of the REST request.
     * 
     * @param baseUri The base URI of the current REST server
     * @param path The path of the REST request
     * @return The ExchangeType found or null
     */
    public static ExchangeType findExchangeType(final String baseUri, final String path) {
        ExchangeType typeFound = null;
        String regex;
        Pattern pattern;
        for (ExchangeType type : ExchangeType.values()) {
            regex = type.makeHrefPattern(baseUri);
            pattern = Pattern.compile(regex);
            if (true == pattern.matcher(path).matches()) {
                typeFound = type;
                break;
            }
        }
        return typeFound;
    }
}
