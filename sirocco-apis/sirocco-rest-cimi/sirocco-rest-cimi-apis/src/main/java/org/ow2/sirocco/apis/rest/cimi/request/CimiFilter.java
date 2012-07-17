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

import java.util.List;

/**
 * Utility class to manage CIMI Expand expression in the QueryString.
 */
public class CimiFilter extends CimiStringParams {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public CimiFilter() {
        super();
    }

    /**
     * Set constructor.
     * 
     * @param initialValue The initial value
     */
    public CimiFilter(final List<String> initialValues) {
        super(initialValues);
    }

    /**
     * Set constructor.
     * 
     * @param initialValues The initial values
     */
    public CimiFilter(final String initialValue) {
        super(initialValue);
    }

    /**
     * Set constructor.
     * 
     * @param initialValues The initial values
     */
    public CimiFilter(final String[] initialValues) {
        super(initialValues);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Split by comma and remove duplicate.
     * </p>
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiParam#prepare()
     */
    @Override
    protected void prepare() {
        this.prepareToSplit();
    }

}
