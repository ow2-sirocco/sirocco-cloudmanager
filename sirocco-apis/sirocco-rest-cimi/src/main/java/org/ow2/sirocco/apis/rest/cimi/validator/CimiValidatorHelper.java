package org.ow2.sirocco.apis.rest.cimi.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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
        boolean valid = true;
        Validator validator = CimiValidatorHelper.factory.getValidator();

        CimiValidatorHelper.LOGGER.debug("Validation of {}", beanToValidate.getClass().getName());

        Set<ConstraintViolation<T>> violations = validator.validate(beanToValidate);
        if (violations.size() > 0) {
            valid = false;
            if (CimiValidatorHelper.LOGGER.isInfoEnabled()) {
                for (ConstraintViolation<T> constraintViolation : violations) {
                    CimiValidatorHelper.LOGGER.info("Validation error: {}, Bean: {}, Property: {}, Value: {}", new Object[] {
                        constraintViolation.getMessage(), constraintViolation.getRootBeanClass().getSimpleName(),
                        constraintViolation.getPropertyPath(), constraintViolation.getInvalidValue()});
                }
            }
        }
        return valid;
    }

    /**
     * /** Validate a bean with a filter.
     * 
     * @param <T> Type of bean to validate
     * @param beanToValidate Bean to validate
     * @param filterClass Filter class
     * @return True if the bean is valid
     */
    public <T> boolean validate(final T beanToValidate, final Class<?> filterClass) {
        boolean valid = true;
        Validator validator = CimiValidatorHelper.factory.getValidator();

        CimiValidatorHelper.LOGGER.debug("Validation of {} with {}", beanToValidate.getClass().getName(),
            filterClass.getSimpleName());
        Set<ConstraintViolation<T>> violations = validator.validate(beanToValidate, filterClass);
        if (violations.size() > 0) {
            valid = false;
            if (CimiValidatorHelper.LOGGER.isInfoEnabled()) {
                for (ConstraintViolation<T> constraintViolation : violations) {
                    CimiValidatorHelper.LOGGER.info("Validation error: {}, Bean: {}, Property: {}, Value: {}", new Object[] {
                        constraintViolation.getMessage(), constraintViolation.getRootBeanClass().getSimpleName(),
                        constraintViolation.getPropertyPath(), constraintViolation.getInvalidValue()});
                }
            }
        }
        return valid;
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
        boolean valid = true;
        Validator validator = CimiValidatorHelper.factory.getValidator();

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
        Set<ConstraintViolation<T>> violations = validator.validate(beanToValidate, filters);
        if (violations.size() > 0) {
            valid = false;
            if (CimiValidatorHelper.LOGGER.isInfoEnabled()) {
                for (ConstraintViolation<T> constraintViolation : violations) {
                    CimiValidatorHelper.LOGGER.info("Validation error: {}, Bean: {}, Property: {}, Value: {}", new Object[] {
                        constraintViolation.getMessage(), constraintViolation.getRootBeanClass().getSimpleName(),
                        constraintViolation.getPropertyPath(), constraintViolation.getInvalidValue()});
                }
            }
        }
        return valid;
    }

}