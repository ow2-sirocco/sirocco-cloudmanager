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
package org.ow2.sirocco.apis.rest.cimi.validator;

import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;

public class CimiValidatorHeader extends CimiValidatorAbstract implements CimiValidator {

    /** Singleton */
    private static final CimiValidatorHeader SINGLETON = new CimiValidatorHeader();

    /**
     * Private constructor to protect the singleton.
     */
    private CimiValidatorHeader() {
        super();
    }

    /**
     * Get the singleton instance.
     * @return The singleton instance
     */
    public static CimiValidatorHeader getInstance() {
        return SINGLETON;
    }

    /**
     * {@inheritDoc}
     * @see org.ow2.sirocco.apis.rest.cimi.validator.CimiValidator#validate(org.ow2.sirocco.apis.rest.cimi.request.CimiRequest,
     *      org.ow2.sirocco.apis.rest.cimi.request.CimiResponse)
     */
    @Override
    public boolean validate(CimiRequest request, CimiResponse response) {
        return doValidate(request.getHeader());
    }

}
