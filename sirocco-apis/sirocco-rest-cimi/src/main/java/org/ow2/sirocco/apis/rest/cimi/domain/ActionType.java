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
package org.ow2.sirocco.apis.rest.cimi.domain;

/**
 * Enumeration of actions used.
 */
public enum ActionType {

    START("start"), STOP("stop"), RESTART("restart"), PAUSE("pause"), SUSPEND("suspend"), CAPTURE("capture");

    /** The action pathname. */
    String pathname;

    /** URI action. */
    public static final String BASE_PATH = "http://www.dmtf.org/cimi/action/";

    /** Constructor. */
    private ActionType(final String pathname) {
        this.pathname = pathname;
    }

    /**
     * Get the action pathname.
     * 
     * @return The pathname
     */
    public String getPathname() {
        return this.pathname;
    }

    /**
     * Get the complete action path.
     * 
     * @return The path
     */
    public String getPath() {
        return ActionType.BASE_PATH + this.pathname;
    }

    /**
     * Find the action type with a given action string.
     * <p>
     * The string to find can be a string enum or a complete actio path or a
     * pathname.
     * </p>
     * 
     * @param toFind The action string to find
     * @return The action type or null if not found
     */
    public static ActionType findValueOf(final String toFind) {
        ActionType action = null;
        for (ActionType value : ActionType.values()) {
            if (toFind.equals(value.toString())) {
                action = value;
                break;
            }
            if (toFind.equals(value.getPath())) {
                action = value;
                break;
            }
            if (toFind.equals(value.getPathname())) {
                action = value;
                break;
            }
        }
        return action;
    }

    /**
     * Find the action type with a given action path.
     * 
     * @param toFind The action path to find
     * @return The action type or null if not found
     */
    public static ActionType findPath(final String toFind) {
        ActionType action = null;
        for (ActionType value : ActionType.values()) {
            if (toFind.equals(value.getPath())) {
                action = value;
                break;
            }
        }
        return action;
    }
}
