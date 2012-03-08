package org.ow2.sirocco.apis.rest.cimi.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CimiValidatorAbstract {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(CimiValidatorAbstract.class);

    /** Factory */
    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    /**
     * Validate a bean.
     * @param <T> The type of bean
     * @param beanToValidate The bean
     * @return True if valid
     */
    protected <T> boolean doValidate(T beanToValidate) {
        boolean valid = true;
        Validator validator = CimiValidatorAbstract.factory.getValidator();

        LOGGER.debug("Validation of {}", beanToValidate.getClass().getName());

        Set<ConstraintViolation<T>> violations = validator.validate(beanToValidate);
        if (violations.size() > 0) {
            valid = false;
            if (LOGGER.isInfoEnabled()) {
                for (ConstraintViolation<T> constraintViolation : violations) {
                    LOGGER.info("Validation error: {}, Bean: {}, Property: {}, Value: {}", new Object[] {
                            constraintViolation.getMessage(), constraintViolation.getRootBeanClass().getSimpleName(),
                            constraintViolation.getPropertyPath(), constraintViolation.getInvalidValue()});
                }
            }
        }
        return valid;
    }

    protected <T> boolean doValidate(T beanToValidate, Class<?> partialClass) {
        boolean valid = true;
        Validator validator = CimiValidatorAbstract.factory.getValidator();

        LOGGER.debug("Validation of {} with {}", beanToValidate.getClass().getName(), partialClass.getSimpleName());
        Set<ConstraintViolation<T>> violations = validator.validate(beanToValidate, partialClass);
        if (violations.size() > 0) {
            valid = false;
            if (LOGGER.isInfoEnabled()) {
                for (ConstraintViolation<T> constraintViolation : violations) {
                    LOGGER.info("Validation error: {}, Bean: {}, Property: {}, Value: {}", new Object[] {
                            constraintViolation.getMessage(), constraintViolation.getRootBeanClass().getSimpleName(),
                            constraintViolation.getPropertyPath(), constraintViolation.getInvalidValue()});
                }
            }
        }
        return valid;
    }

    protected <T> boolean doValidate(T beanToValidate, Class<?>... groups) {
        boolean valid = true;
        Validator validator = CimiValidatorAbstract.factory.getValidator();

        if (LOGGER.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < groups.length; i++) {
                if (i > 0) {
                    sb.append(',').append(' ');
                }
                sb.append(groups[i].getSimpleName());
            }
            LOGGER.debug("Validation of {} with {}", beanToValidate.getClass().getName(), sb);
        }
        Set<ConstraintViolation<T>> violations = validator.validate(beanToValidate, groups);
        if (violations.size() > 0) {
            valid = false;
            if (LOGGER.isInfoEnabled()) {
                for (ConstraintViolation<T> constraintViolation : violations) {
                    LOGGER.info("Validation error: {}, Bean: {}, Property: {}, Value: {}", new Object[] {
                            constraintViolation.getMessage(), constraintViolation.getRootBeanClass().getSimpleName(),
                            constraintViolation.getPropertyPath(), constraintViolation.getInvalidValue()});
                }
            }
        }
        return valid;
    }

}