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

import org.ow2.sirocco.apis.rest.cimi.validator.constraints.AssertVersion;

public class RequestHeader {

    private CimiSelect cimiSelect;

    private String baseUri;

    private String path;

    @AssertVersion
    private String version;

    private String siroccoInfoTestId;

    private String siroccoInfoTestExpand;

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

    public void setBaseUri(final String baseUri) {
        this.baseUri = baseUri;
    }

    public String getBaseUri() {
        return this.baseUri;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
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

    /**
     * Expand used in test.
     * 
     * @return the siroccoInfoTestExpand
     */
    public String getSiroccoInfoTestExpand() {
        return this.siroccoInfoTestExpand;
    }

    /**
     * Expand used in test.
     * 
     * @param siroccoInfoTestExpand the siroccoInfoTestExpand to set
     */
    public void setSiroccoInfoTestExpand(final String siroccoInfoTestExpand) {
        this.siroccoInfoTestExpand = siroccoInfoTestExpand;
    }

    /**
     * Expand used in test.
     * 
     * @return the siroccoInfoTestExpand
     */
    public Boolean getBooleanSiroccoInfoTestExpand() {
        Boolean ret = null;
        if (null != this.siroccoInfoTestExpand) {
            try {
                ret = Boolean.valueOf(this.siroccoInfoTestExpand);
            } catch (Exception e) {
                // Nothing to do
            }
        }
        return ret;
    }
}
