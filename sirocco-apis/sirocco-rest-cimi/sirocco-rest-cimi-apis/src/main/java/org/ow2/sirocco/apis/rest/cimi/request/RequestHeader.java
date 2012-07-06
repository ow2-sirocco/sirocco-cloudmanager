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
package org.ow2.sirocco.apis.rest.cimi.request;

import java.io.Serializable;

import org.ow2.sirocco.apis.rest.cimi.validator.constraints.AssertVersion;

public class RequestHeader implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    private CimiSelect cimiSelect;

    private CimiExpand cimiExpand;

    @AssertVersion
    private String version;

    private String siroccoInfoTestId;

    /**
     * @return the cimiSelect
     */
    public CimiSelect getCimiSelect() {
        return this.cimiSelect;
    }

    /**
     * @param cimiSelect the cimiSelect to set
     */
    public void setCimiSelect(final CimiSelect cimiSelect) {
        this.cimiSelect = cimiSelect;
    }

    /**
     * @return the cimiExpand
     */
    public CimiExpand getCimiExpand() {
        return this.cimiExpand;
    }

    /**
     * @param cimiExpand the cimiExpand to set
     */
    public void setCimiExpand(final CimiExpand cimiExpand) {
        this.cimiExpand = cimiExpand;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    /**
     * ID used in test.
     * 
     * @return the siroccoInfoTestId
     */
    public String getSiroccoInfoTestId() {
        return this.siroccoInfoTestId;
    }

    /**
     * ID used in test.
     * 
     * @return the siroccoInfoTestId
     */
    public Integer getIntegerSiroccoInfoTestId() {
        Integer ret = null;
        try {
            ret = Integer.valueOf(this.siroccoInfoTestId);
        } catch (Exception e) {
            // Nothing to do
        }
        return ret;
    }

    /**
     * ID used in test.
     * 
     * @param siroccoInfoTestId the siroccoInfoTestId to set
     */
    public void setSiroccoInfoTestId(final String siroccoInfoTest) {
        this.siroccoInfoTestId = siroccoInfoTest;
    }
}
