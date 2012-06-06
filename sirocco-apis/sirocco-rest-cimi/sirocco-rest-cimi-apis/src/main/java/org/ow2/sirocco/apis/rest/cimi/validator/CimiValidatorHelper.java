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

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiResource;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.apis.rest.cimi.utils.ReflectionHelper;
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
     * Validate a bean to create.
     * 
     * @param <T> The type of bean
     * @param beanToValidate Bean to validate
     * @return True if the bean is valid
     * @throws Exception In case of reflection error
     */
    public <T> boolean validateToCreate(final CimiContext context, final T beanToValidate) throws Exception {
        boolean valid = false;
        Class<?> group = GroupCreateByValue.class;
        if (true == CimiResource.class.isAssignableFrom(beanToValidate.getClass())) {
            if (true == ((CimiResource) beanToValidate).hasReference()) {
                group = GroupCreateByRefOrByValue.class;
            }
        }
        valid = CimiValidatorHelper.getInstance().validate(context, beanToValidate, group);
        if (true == valid) {
            valid = this.validateToWrite(context, beanToValidate);
            if (true == valid) {
                Set<Field> fields = ReflectionHelper.getInstance().findAnnotationInFields(beanToValidate.getClass(),
                    ValidChild.class);
                Map<Field, Object> props = ReflectionHelper.getInstance().getProperties(fields, beanToValidate, true);
                for (Object obj : props.values()) {
                    valid = this.validateToCreate(context, obj);
                    if (false == valid) {
                        break;
                    }
                }
            }
        }
        return valid;
    }

    /**
     * Validate a bean to create.
     * 
     * @param <T> The type of bean
     * @param beanToValidate Bean to validate
     * @return True if the bean is valid
     * @throws Exception In case of reflection error
     */
    public <T> boolean validateToWrite(final T beanToValidate) throws Exception {
        boolean valid = false;
        valid = CimiValidatorHelper.getInstance().validate(null, beanToValidate, GroupWrite.class);
        if (true == valid) {
            Set<Field> fields = ReflectionHelper.getInstance().findAnnotationInFields(beanToValidate.getClass(),
                ValidChild.class);
            Map<Field, Object> props = ReflectionHelper.getInstance().getProperties(fields, beanToValidate, true);
            for (Object obj : props.values()) {
                valid = this.validateToWrite(null, obj);
                if (false == valid) {
                    break;
                }
            }
        }
        return valid;
    }

    /**
     * Validate a bean to create.
     * 
     * @param <T> The type of bean
     * @param request
     * @param response
     * @param beanToValidate Bean to validate
     * @return True if the bean is valid
     * @throws Exception In case of reflection error
     */
    public <T> boolean validateToWrite(final CimiContext context, final T beanToValidate) throws Exception {
        boolean valid = false;
        valid = CimiValidatorHelper.getInstance().validate(context, beanToValidate, Default.class, GroupWrite.class);
        if (true == valid) {
            Set<Field> fields = ReflectionHelper.getInstance().findAnnotationInFields(beanToValidate.getClass(),
                ValidChild.class);
            Map<Field, Object> props = ReflectionHelper.getInstance().getProperties(fields, beanToValidate, true);
            for (Object obj : props.values()) {
                valid = this.validateToWrite(context, obj);
                if (false == valid) {
                    break;
                }
            }
        }
        return valid;
    }

    // FIXME in comment?
    // /**
    // * Validate a bean.
    // *
    // * @param <T> The type of bean
    // * @param beanToValidate Bean to validate
    // * @return True if the bean is valid
    // */
    // public <T> boolean validate(final T beanToValidate) {
    // return this.validate(null, beanToValidate);
    // }

    // FIXME in comment?
    /**
     * Validate a bean with a array of filters.
     * 
     * @param <T> Type of bean to validate
     * @param beanToValidate Bean to validate
     * @param filters Group to validate
     * @return True if the bean is valid
     */
    public <T> boolean validate(final T beanToValidate, final Class<?>... filters) {
        return this.validate(null, beanToValidate, filters);
    }

    /**
     * Validate a bean with a array of filters.
     * 
     * @param <T> Type of bean to validate
     * @param beanToValidate Bean to validate
     * @param filters Group to validate
     * @return True if the bean is valid
     */
    public <T> boolean validate(final CimiContext context, final T beanToValidate, final Class<?>... filters) {
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
        return this.checkViolations(context, this.getValidator(context).validate(beanToValidate, filters));
    }

    private Validator getValidator(final CimiContext context) {
        Validator validator = null;
        if (null == context) {
            validator = CimiValidatorHelper.factory.getValidator();
        } else {
            ValidatorContext validatorContext = CimiValidatorHelper.factory.usingContext();
            validatorContext.constraintValidatorFactory(new CimiConstraintValidatorFactoryImpl(context));
            validator = validatorContext.getValidator();
        }
        return validator;
    }

    private <T> boolean checkViolations(final CimiContext context, final Set<ConstraintViolation<T>> violations) {
        boolean valid = true;
        if (violations.size() > 0) {
            valid = false;
            if ((null != context) || (CimiValidatorHelper.LOGGER.isDebugEnabled())) {
                StringBuilder sb = new StringBuilder();
                for (ConstraintViolation<T> constraintViolation : violations) {
                    sb.append("Validation error: ").append(constraintViolation.getMessage());
                    sb.append(", Bean: ").append(constraintViolation.getRootBeanClass().getName());
                    sb.append(", Property: ").append(constraintViolation.getPropertyPath());
                    // sb.append(", Value: ").append(constraintViolation.getInvalidValue());
                    sb.append('\n');
                }
                if (null != context) {
                    context.getResponse().setErrorMessage(sb.toString());
                }
                if (CimiValidatorHelper.LOGGER.isDebugEnabled()) {
                    CimiValidatorHelper.LOGGER.debug(sb.toString());
                }
            }
        }
        return valid;
    }

}