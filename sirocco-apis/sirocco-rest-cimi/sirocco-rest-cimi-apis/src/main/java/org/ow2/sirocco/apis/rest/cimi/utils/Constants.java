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
package org.ow2.sirocco.apis.rest.cimi.utils;

public abstract class Constants {

    /** Number Version CIMI. */
    public static final String VERSION_DMTF_CIMI = "1.0";

    /** Parameter name in Query String : select. */
    public static final String PARAM_CIMI_SELECT = "$select";

    /** Parameter name in Query String : expand. */
    public static final String PARAM_CIMI_EXPAND = "$expand";

    /** Parameter name in Query String : first. */
    public static final String PARAM_CIMI_FIRST = "$first";

    /** Parameter name in Query String : last. */
    public static final String PARAM_CIMI_LAST = "$last";

    /** Parameter name in Query String : filter. */
    public static final String PARAM_CIMI_FILTER = "$filter";

    /** Parameter name in Header : CIMI-Specification-Version. */
    public static final String HEADER_CIMI_VERSION = "CIMI-Specification-Version";

    /** Parameter name in Header : CIMI-Job-URI. */
    public static final String HEADER_CIMI_JOB_URI = "CIMI-Job-URI";

    /** Parameter name in Header : Location. */
    public static final String HEADER_LOCATION = "Location";

    /** Parameter name in Header : SIROCCO-INFO-TEST-ID. */
    public static final String HEADER_SIROCCO_INFO_TEST_ID = "SIROCCO-INFO-TEST-ID";

}
