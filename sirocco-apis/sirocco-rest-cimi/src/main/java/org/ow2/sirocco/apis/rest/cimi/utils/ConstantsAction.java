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
 * $Id: ConstantsAction.java 101 2012-03-05 08:26:32Z antonma $
 *
 */
package org.ow2.sirocco.apis.rest.cimi.utils;

public abstract class ConstantsAction {

    /**
     * base of uri action
     */
    public static final String BASE_URI_ACTIONS = "http://www.dmtf.org/cimi/action/";

    /** */
    public static final String ACTION_START = BASE_URI_ACTIONS + "start";

    /** */
    public static final String ACTION_STOP = BASE_URI_ACTIONS + "stop";

    /** */
    public static final String ACTION_DELETE = BASE_URI_ACTIONS + "delete";

    /** */
    public static final String ACTION_RESTART = BASE_URI_ACTIONS + "restart";

    /** */
    public static final String ACTION_PAUSE = BASE_URI_ACTIONS + "pause";

    /** */
    public static final String ACTION_SUSPEND = BASE_URI_ACTIONS + "suspend";

    /** */
    public static final String ACTION_CAPTURE = BASE_URI_ACTIONS + "capture";
}
