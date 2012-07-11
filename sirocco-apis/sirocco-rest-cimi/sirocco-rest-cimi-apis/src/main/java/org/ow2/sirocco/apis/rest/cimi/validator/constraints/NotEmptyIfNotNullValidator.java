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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.ow2.sirocco.apis.rest.cimi.domain.collection.CimiCollection;

/**
 * Implementation of {@link NotEmptyIfNotNullValidator} validator.
 **/
public class NotEmptyIfNotNullValidator implements ConstraintValidator<NotEmptyIfNotNull, Object> {

    @Override
    public void initialize(final NotEmptyIfNotNull annotation) {
        // Nothing to do
    }

    /**
     * Validate the value.
     * <p>
     * Valid if
     * <ul>
     * <li>String : false == value.trim().isEmpty</li>
     * <li>Array : value.length > 0 and all items not null</li>
     * <li>Collection : value.size() > 0 and all items not null</li>
     * <li>Map : value.size() > 0 and all items not null</li>
     * <li>CimiCollection : value.size() > 0 and all items not null</li>
     * </ul>
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
            Class<?> klass = value.getClass();
            if (true == klass.isAssignableFrom(String.class)) {
                if (true == value.toString().trim().isEmpty()) {
                    valid = false;
                }
            } else if (true == klass.isArray()) {
                if (0 == Array.getLength(value)) {
                    valid = false;
                } else {
                    boolean allNotNull = true;
                    for (int i = 0; i < Array.getLength(value); i++) {
                        allNotNull = allNotNull && (null != Array.get(value, i));
                    }
                    valid = allNotNull;
                }
            } else if (true == Collection.class.isAssignableFrom(klass)) {
                if (0 == ((Collection<?>) value).size()) {
                    valid = false;
                } else {
                    boolean allNotNull = true;
                    for (Object obj : ((Collection<?>) value)) {
                        allNotNull = allNotNull && (null != obj);
                    }
                    valid = allNotNull;
                }
            } else if (true == Map.class.isAssignableFrom(klass)) {
                if (0 == ((Map<?, ?>) value).size()) {
                    valid = false;
                } else {
                    boolean allNotNull = true;
                    for (Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                        allNotNull = allNotNull && (null != entry.getKey()) && (null != entry.getValue());
                    }
                    valid = allNotNull;
                }
            } else if (true == CimiCollection.class.isAssignableFrom(klass)) {
                Collection<?> valueCollect = ((CimiCollection<?>) value).getCollection();
                if (null == valueCollect) {
                    valid = false;
                } else {
                    if (0 == valueCollect.size()) {
                        valid = false;
                    } else {
                        boolean allNotNull = true;
                        for (Object obj : valueCollect) {
                            allNotNull = allNotNull && (null != obj);
                        }
                        valid = allNotNull;
                    }
                }
            }
        }
        return valid;
    }

}