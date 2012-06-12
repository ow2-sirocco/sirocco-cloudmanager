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
package org.ow2.sirocco.apis.rest.cimi.manager;

import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.validator.CimiValidatorHelper;

/**
 * Abstract class for manage CREATE request.
 */
public abstract class CimiManagerCreateAbstract extends CimiManagerAbstract {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#validate(org.ow2.sirocco.apis.rest.cimi.request.CimiContext)
     */
    @Override
    protected boolean validate(final CimiContext context) throws Exception {
        boolean valid = CimiValidatorHelper.getInstance().validate(context, context.getRequest().getHeader());
        if (valid) {
            if (null == context.getRequest().getCimiData()) {
                valid = false;
            } else {
                valid = CimiValidatorHelper.getInstance().validateToCreate(context, context.getRequest().getCimiData());
            }
        }
        return valid;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#convertToDataService(org.ow2.sirocco.apis.rest.cimi.request.CimiContext)
     */
    @Override
    protected Object convertToDataService(final CimiContext context) throws Exception {
        return context.convertToService(context.getRequest().getCimiData());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.manager.CimiManagerAbstract#convertToResponse(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      java.lang.Object)
     */
    @Override
    protected void convertToResponse(final CimiContext context, final Object dataService) throws Exception {
        // Nothing to do
    }

}