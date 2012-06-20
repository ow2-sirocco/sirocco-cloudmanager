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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Implementation of {@link AssertVersion} validator.
 **/
public class AssertVersionValidator implements ConstraintValidator<AssertVersion, Object> {

    private String version;

    @Override
    public void initialize(final AssertVersion annotation) {
        this.version = annotation.version();

    }

    /**
     * Validate the value version.
     * <ul>
     * <li>None Version is valid</li>
     * <li>Same version is valid</li>
     * </ul> {@inheritDoc}
     * 
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
     *      javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext ctx) {
        boolean valid = true;
        if (value != null) {
            if (false == this.version.equals(value)) {
                valid = false;
            }
        }
        return valid;
    }
}