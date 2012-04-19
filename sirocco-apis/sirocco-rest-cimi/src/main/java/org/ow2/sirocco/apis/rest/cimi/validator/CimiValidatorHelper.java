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

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;

import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.request.CimiRequest;
import org.ow2.sirocco.apis.rest.cimi.request.CimiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CimiValidatorHelper {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(CimiValidatorHelper.class);

    /** Factory */
    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    /** Singleton */
    private static final CimiValidatorHelper SINGLETON = new CimiValidatorHelper();

    /**
     * Private constructor to protect the singleton.
     */
    private CimiValidatorHelper() {
        super();
    }

    /**
     * Get the singleton instance.
     * 
     * @return The singleton instance
     */
    public static CimiValidatorHelper getInstance() {
        return CimiValidatorHelper.SINGLETON;
    }

    /**
     * Validate a bean.
     * 
     * @param <T> The type of bean
     * @param beanToValidate Bean to validate
     * @return True if the bean is valid
     */
    public <T> boolean validate(final T beanToValidate) {
        return this.validate(null, null, beanToValidate);
    }

    /**
     * Validate a bean.
     * 
     * @param <T> The type of bean
     * @param beanToValidate Bean to validate
     * @return True if the bean is valid
     */
    public <T> boolean validate(final CimiRequest request, final CimiResponse response, final T beanToValidate) {
        CimiValidatorHelper.LOGGER.debug("Validation of {}", beanToValidate.getClass().getName());
        return this.checkViolations(response, this.getValidator(request).validate(beanToValidate));
    }

    /**
     * Validate a bean with a filter.
     * 
     * @param <T> Type of bean to validate
     * @param beanToValidate Bean to validate
     * @param filterClass Filter class
     * @return True if the bean is valid
     */
    public <T> boolean validate(final T beanToValidate, final Class<?> filterClass) {
        return this.validate(null, null, beanToValidate, filterClass);
    }

    /**
     * Validate a bean with a filter.
     * 
     * @param <T> Type of bean to validate
     * @param beanToValidate Bean to validate
     * @param filterClass Filter class
     * @return True if the bean is valid
     */
    public <T> boolean validate(final CimiRequest request, final CimiResponse response, final T beanToValidate,
        final Class<?> filterClass) {
        CimiValidatorHelper.LOGGER.debug("Validation of {} with {}", beanToValidate.getClass().getName(),
            filterClass.getSimpleName());
        return this.checkViolations(response, this.getValidator(request).validate(beanToValidate, filterClass));
    }

    /**
     * Validate a bean with a array of filters.
     * 
     * @param <T> Type of bean to validate
     * @param beanToValidate Bean to validate
     * @param filters Group to validate
     * @return True if the bean is valid
     */
    public <T> boolean validate(final T beanToValidate, final Class<?>... filters) {
        return this.validate(null, null, beanToValidate, filters);
    }

    /**
     * Validate a bean with a array of filters.
     * 
     * @param <T> Type of bean to validate
     * @param beanToValidate Bean to validate
     * @param filters Group to validate
     * @return True if the bean is valid
     */
    public <T> boolean validate(final CimiRequest request, final CimiResponse response, final T beanToValidate,
        final Class<?>... filters) {
        if (CimiValidatorHelper.LOGGER.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < filters.length; i++) {
                if (i > 0) {
                    sb.append(',').append(' ');
                }
                sb.append(filters[i].getSimpleName());
            }
            CimiValidatorHelper.LOGGER.debug("Validation of {} with {}", beanToValidate.getClass().getName(), sb);
        }
        return this.checkViolations(response, this.getValidator(request).validate(beanToValidate, filters));
    }

    private Validator getValidator(final CimiRequest request) {
        Validator validator = null;
        CimiContext context = null;
        if (null != request) {
            context = request.getContext();
        }
        if (null == context) {
            validator = CimiValidatorHelper.factory.getValidator();
        } else {
            ValidatorContext validatorContext = CimiValidatorHelper.factory.usingContext();
            validatorContext.constraintValidatorFactory(new CimiConstraintValidatorFactoryImpl(context));
            validator = validatorContext.getValidator();
        }
        return validator;
    }

    private <T> boolean checkViolations(final CimiResponse response, final Set<ConstraintViolation<T>> violations) {
        boolean valid = true;
        if (violations.size() > 0) {
            valid = false;
            if ((null != response) || (CimiValidatorHelper.LOGGER.isDebugEnabled())) {
                StringBuilder sb = new StringBuilder();
                for (ConstraintViolation<T> constraintViolation : violations) {
                    sb.append("Validation error: ").append(constraintViolation.getMessage());
                    sb.append(", Bean: ").append(constraintViolation.getRootBeanClass().getName());
                    sb.append(", Property: ").append(constraintViolation.getPropertyPath());
                    // sb.append(", Value: ").append(constraintViolation.getInvalidValue());
                    sb.append('\n');
                }
                if (null != response) {
                    response.setErrorMessage(sb.toString());
                }
                if (CimiValidatorHelper.LOGGER.isDebugEnabled()) {
                    CimiValidatorHelper.LOGGER.debug(sb.toString());
                }
            }
        }
        return valid;
    }

}