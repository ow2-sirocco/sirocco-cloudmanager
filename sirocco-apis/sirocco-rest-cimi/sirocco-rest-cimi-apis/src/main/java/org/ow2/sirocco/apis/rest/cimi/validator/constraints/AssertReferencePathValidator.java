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
package org.ow2.sirocco.apis.rest.cimi.validator.constraints;

import javax.validation.ConstraintValidatorContext;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiResource;
import org.ow2.sirocco.apis.rest.cimi.validator.CimiContextValidatorAbstract;

/**
 * Implementation of {@link AssertReferencePath} validator.
 **/
public class AssertReferencePathValidator extends CimiContextValidatorAbstract<AssertReferencePath, Object> {

    @Override
    public void initialize(final AssertReferencePath annotation) {
        // Nothing to do
    }

    /**
     * Validate a valid reference.
     * <p>
     * {@inheritDoc}
     * </p>
     * 
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
     *      javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext ctx) {
        boolean valid = true;
        if (value != null) {
            CimiResource resource = (CimiResource) value;
            String href = resource.getHref();
            if (href != null) {
                String hrefNoId = this.getCimiContext().makeHrefBase(resource);
                if (false == href.startsWith(hrefNoId)) {
                    valid = false;
                } else if (false == this.getCimiContext().mustHaveIdInReference(resource.getClass())) {
                    if (false == href.equals(hrefNoId)) {
                        valid = false;
                    }
                } else {
                    int index = href.lastIndexOf('/');
                    String sub = href.substring(0, index + 1);
                    if ((false == sub.equals(hrefNoId)) || (href.length() == hrefNoId.length())) {
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }
}