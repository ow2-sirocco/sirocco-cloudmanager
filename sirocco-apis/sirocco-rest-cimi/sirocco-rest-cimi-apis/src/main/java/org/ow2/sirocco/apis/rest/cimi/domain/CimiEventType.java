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

package org.ow2.sirocco.apis.rest.cimi.domain;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

@XmlRootElement(name = "content")
@XmlType(propOrder = {"resName", "operation", "resource", "resType", "code", "state", "previous", "change", "detail",
    "initiator"})
@JsonPropertyOrder({"resName", "operation", "resource", "resType", "code", "state", "previous", "change", "detail", "initiator"})
@JsonSerialize(include = Inclusion.NON_NULL)
public class CimiEventType implements CimiData {
    private static final long serialVersionUID = 1L;

    /** All event type */
    private String resName;

    /** All event type */
    private String resType;

    /** All event type */
    private TargetResource resource;

    /** All event type */
    private String detail;

    /** All Access event type */
    private String operation;

    /** All Access event type */
    private String initiator;

    /** All Alarm event type */
    private String code;

    /** All Model event type */
    private String change;

    /** All State event type */
    private String state;

    /** All State event type */
    private String previous;

    /**
     * Enum for different types of events.
     */
    public enum EventType {
        /** */
        ACCESS("access"),
        /** */
        ALARM("alarm"),
        /** */
        MODEL("model"),
        /** */
        STATE("state");

        /** The event pathname. */
        String pathname;

        /** URI event. */
        public static final String BASE_PATH = ConstantsPath.CIMI_XML_NAMESPACE + "/event/";

        /** Constructor. */
        private EventType(final String pathname) {
            this.pathname = pathname;
        }

        /**
         * Get the event pathname.
         * 
         * @return The pathname
         */
        public String getPathname() {
            return this.pathname;
        }

        /**
         * Get the complete event path.
         * 
         * @return The path
         */
        public String getPath() {
            return EventType.BASE_PATH + this.pathname;
        }

        /**
         * Find the event type with a given event string.
         * <p>
         * The string to find can be a string enum or a complete event path or a
         * pathname.
         * </p>
         * 
         * @param toFind The event string to find
         * @return The event type or null if not found
         */
        public static EventType findValueOf(final String toFind) {
            EventType event = null;
            for (EventType value : EventType.values()) {
                if (toFind.equals(value.toString())) {
                    event = value;
                    break;
                }
                if (toFind.equals(value.getPath())) {
                    event = value;
                    break;
                }
                if (toFind.equals(value.getPathname())) {
                    event = value;
                    break;
                }
            }
            return event;
        }

        /**
         * Find the event type with a given event path.
         * 
         * @param toFind The event path to find
         * @return The event type or null if not found
         */
        public static EventType findWithPath(final String toFind) {
            EventType event = null;
            for (EventType value : EventType.values()) {
                if (toFind.equals(value.getPath())) {
                    event = value;
                    break;
                }
            }
            return event;
        }
    }

    public String getResName() {
        return this.resName;
    }

    public void setResName(final String resName) {
        this.resName = resName;
    }

    public TargetResource getResource() {
        return this.resource;
    }

    public void setResource(final TargetResource resource) {
        this.resource = resource;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(final String detail) {
        this.detail = detail;
    }

    public String getResType() {
        return this.resType;
    }

    public void setResType(final String resType) {
        this.resType = resType;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(final String operation) {
        this.operation = operation;
    }

    public String getInitiator() {
        return this.initiator;
    }

    public void setInitiator(final String initiator) {
        this.initiator = initiator;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getChange() {
        return this.change;
    }

    public void setChange(final String change) {
        this.change = change;
    }

    public String getState() {
        return this.state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getPrevious() {
        return this.previous;
    }

    public void setPrevious(final String previous) {
        this.previous = previous;
    }

    @XmlTransient
    @JsonIgnore
    public EventType getEventType() {
        return EventType.ACCESS;
    }

}
