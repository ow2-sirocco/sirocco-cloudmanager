package org.ow2.sirocco.cloudmanager.core.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class utilForManagers {
    
    
    public static Object fillObject(Object o, Map<String, Object> updatedAttributes)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, IntrospectionException,
            NoSuchFieldException, InvocationTargetException {

        for (Map.Entry<String, Object> attr : updatedAttributes.entrySet()) {
            invokeSetter(o.getClass(), o, attr.getKey(), attr.getValue());
        }

        return o;

    }

    private static Method invokeSetter(Class targetClass, Object targetObj,
            String attrName, Object attrValue) throws IntrospectionException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {

        BeanInfo info = Introspector.getBeanInfo(targetClass);
        for (PropertyDescriptor pd : info.getPropertyDescriptors())
            if (attrName.equals(pd.getName()))
                pd.getWriteMethod().invoke(targetObj, attrValue);
        throw new NoSuchFieldException(targetClass + " has no field "
                + attrName);
    }

}
