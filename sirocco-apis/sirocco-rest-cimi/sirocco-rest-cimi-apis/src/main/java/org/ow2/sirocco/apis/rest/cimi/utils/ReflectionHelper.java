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
package org.ow2.sirocco.apis.rest.cimi.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReflectionHelper {

    /** Singleton */
    private static final ReflectionHelper SINGLETON = new ReflectionHelper();

    /**
     * Private constructor to protect the singleton.
     */
    private ReflectionHelper() {
        super();
    }

    /**
     * Get the singleton instance.
     * 
     * @return The singleton instance
     */
    public static ReflectionHelper getInstance() {
        return ReflectionHelper.SINGLETON;
    }

    public Set<Field> findAnnotationInFields(final Class<?> klass, final Class<?> annotationToFind) {
        Set<Field> associates = new HashSet<Field>();
        Field[] fields;
        Annotation[] annotations;
        fields = klass.getDeclaredFields();
        for (Field field : fields) {
            annotations = field.getAnnotations();
            if (null != annotations) {
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == annotationToFind) {
                        associates.add(field);
                        break;
                    }
                }
            }
        }
        return associates;
    }

    public Map<Field, Object> getProperties(final Set<Field> fields, final Object instance, final boolean onlyNotNull)
        throws IllegalAccessException {
        Map<Field, Object> props = new HashMap<Field, Object>();
        Object obj = null;

        for (Field field : fields) {
            field.setAccessible(true);
            obj = field.get(instance);
            if (true == onlyNotNull) {
                if (null != obj) {
                    props.put(field, obj);
                }
            } else {
                props.put(field, obj);
            }
        }
        return props;
    }

}