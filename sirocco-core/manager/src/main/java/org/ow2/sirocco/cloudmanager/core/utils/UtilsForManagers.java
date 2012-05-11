package org.ow2.sirocco.cloudmanager.core.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.hibernate.proxy.HibernateProxy;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;

public class UtilsForManagers {
    
    
    public static Object fillObject(Object obj, Map<String, Object> updatedAttributes)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, IntrospectionException,
            NoSuchFieldException, InvocationTargetException {

        for (Map.Entry<String, Object> attr : updatedAttributes.entrySet()) {
            invokeSetter(obj, attr.getKey(), attr.getValue());
        }

        return obj;

    }

    private static Object invokeSetter(Object targetObj,
            String attrName, Object attrValue) throws IntrospectionException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {

        BeanInfo info = Introspector.getBeanInfo(targetObj.getClass());
        for (PropertyDescriptor pd : info.getPropertyDescriptors())
            if (attrName.equals(pd.getName()))
                return pd.getWriteMethod().invoke(targetObj, attrValue);
        throw new NoSuchFieldException(targetObj.getClass() + " has no field "
                + attrName);
    }
    
    public static Object getEntityThroughProxy(Object o)
    {
        if (o instanceof HibernateProxy)
        {
            HibernateProxy oProxy=(HibernateProxy) o;
            o=(Object) oProxy.getHibernateLazyInitializer().getImplementation();
        }
        return o;
        
    }


}
