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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.hibernate.validator.util.ReflectionHelper;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;

/**
 *
 */
public class CimiConstraintValidatorFactoryImpl implements ConstraintValidatorFactory {
    private CimiContext context;

    /**
     * Constructor.
     * 
     * @param context The current context
     */
    public CimiConstraintValidatorFactoryImpl(final CimiContext context) {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.validation.ConstraintValidatorFactory#getInstance(java.lang.Class)
     */
    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(final Class<T> key) {
        T instance = null;

        instance = ReflectionHelper.newInstance(key, "ConstraintValidator");

        if (true == CimiContextValidator.class.isAssignableFrom(key)) {
            CimiContextValidator validator = (CimiContextValidator) instance;
            validator.setCimiContext(this.context);
        }
        return instance;
    }

}
