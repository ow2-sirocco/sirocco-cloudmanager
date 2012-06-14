package org.ow2.sirocco.cloudmanager.core.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.ejb.EJBContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import org.hibernate.proxy.HibernateProxy;

public class UtilsForManagers {

    public static Object fillObject(Object obj,
            Map<String, Object> updatedAttributes)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, IntrospectionException,
            NoSuchFieldException, InvocationTargetException {

        for (Map.Entry<String, Object> attr : updatedAttributes.entrySet()) {
            invokeSetter(obj, attr.getKey(), attr.getValue());
        }

        return obj;

    }

    private static Object invokeSetter(Object targetObj, String attrName,
            Object attrValue) throws IntrospectionException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {

        BeanInfo info = Introspector.getBeanInfo(targetObj.getClass());
        for (PropertyDescriptor pd : info.getPropertyDescriptors())
            if (attrName.equals(pd.getName()))
                return pd.getWriteMethod().invoke(targetObj, attrValue);
        throw new NoSuchFieldException(targetObj.getClass() + " has no field "
                + attrName);
    }

    public static Object getEntityThroughProxy(Object o) {
        if (o instanceof HibernateProxy) {
            HibernateProxy oProxy = (HibernateProxy) o;
            o = (Object) oProxy.getHibernateLazyInitializer()
                    .getImplementation();
        }
        return o;

    }

    public static void emitJobListenerMessage(final Serializable payload,
            EJBContext ctx) throws Exception {
        emitJMSMessage(payload, ctx, "JobEmission");
    }

    public static void emitJMSMessage(final Serializable payload,
            EJBContext ctx, String queueName) throws Exception {
        ConnectionFactory cf = (ConnectionFactory) ctx.lookup("QCF");
        Queue queue = (Queue) ctx.lookup(queueName);
        Connection conn = cf.createConnection();

        Session sess = conn.createSession(true, Session.AUTO_ACKNOWLEDGE);

        MessageProducer mp = sess.createProducer((Destination) queue);

        ObjectMessage msg = sess.createObjectMessage();
        msg.setObject(payload);
        mp.send(msg);

        sess.close();
        conn.close();
    }

}
