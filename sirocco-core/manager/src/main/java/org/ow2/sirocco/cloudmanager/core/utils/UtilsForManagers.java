package org.ow2.sirocco.cloudmanager.core.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.ejb.EJBContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilsForManagers {
    private static Logger logger = LoggerFactory.getLogger(UtilsForManagers.class.getName());

    /**
     * This generic method fills a bean with a map of attribute names and
     * attribute values
     * 
     * @param obj The bean to update
     * @param updatedAttributes The map owning attribute names and their
     *        respective values
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws IntrospectionException
     * @throws NoSuchFieldException
     * @throws InvocationTargetException
     */
    public static Object fillObject(final Object obj, final Map<String, Object> updatedAttributes)
        throws InstantiationException, IllegalAccessException, IllegalArgumentException, IntrospectionException,
        NoSuchFieldException, InvocationTargetException {

        for (Map.Entry<String, Object> attr : updatedAttributes.entrySet()) {
            UtilsForManagers.invokeSetter(obj, attr.getKey(), attr.getValue());
        }

        return obj;

    }

    /**
     * This generic method calls a bean setter, given a bean and an attribute
     * name. <br>
     * It highly relies on reflection
     * 
     * @param targetObj the bean to update
     * @param attrName the name of the attribute to be updated
     * @param attrValue the value used to update the attribute
     * @return
     * @throws IntrospectionException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static Object invokeSetter(final Object targetObj, final String attrName, final Object attrValue)
        throws IntrospectionException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException,
        InvocationTargetException {

        BeanInfo info = Introspector.getBeanInfo(targetObj.getClass());
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (attrName.equals(pd.getName())) {
                return pd.getWriteMethod().invoke(targetObj, attrValue);
            }
        }
        throw new NoSuchFieldException(targetObj.getClass() + " has no field " + attrName);
    }

    public static Object getEntityThroughProxy(Object o) {
        if (o instanceof HibernateProxy) {
            HibernateProxy oProxy = (HibernateProxy) o;
            o = oProxy.getHibernateLazyInitializer().getImplementation();
        }
        return o;

    }

    /**
     * Emits a message to set a listener on the connector task tied to the given
     * Job <br>
     * The main goal is to ensure that the job listener is triggered after
     * commit, or never triggered if the transaction is rollbacked
     * 
     * @param payload the related Job
     * @param ctx
     * @throws Exception
     */
    public static void emitJobListenerMessage(final Serializable payload, final EJBContext ctx) throws Exception {
        UtilsForManagers.emitJMSMessage(payload, ctx, "jms/JobEmission", 0, 0);
    }

    /**
     * emits an JMS message to a queue, <b>inside a JTA transaction</b>
     * 
     * @param payload the message body
     * @param ctx to send the message inside the ctx transaction
     * @param queueName
     * @throws Exception
     */

    public static void emitJMSMessage(final Serializable payload, final EJBContext ignored, final String queueName,
        final long delayMillis, final long deliveriesCounter) throws Exception {
        InitialContext ctx = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) ctx.lookup("jms/QueueConnectionFactory");
        Queue queue = (Queue) ctx.lookup(queueName);
        Connection conn = cf.createConnection();

        Session sess = conn.createSession(true, Session.AUTO_ACKNOWLEDGE);

        MessageProducer mp = sess.createProducer(queue);

        ObjectMessage msg = sess.createObjectMessage();
        if (delayMillis > 0) {
            msg.setLongProperty("scheduleDate", System.currentTimeMillis() + delayMillis);
        }
        msg.setLongProperty("deliveriesCounter", deliveriesCounter);

        msg.setObject(payload);
        mp.send(msg);

        sess.close();
        conn.close();
    }

    public static <E> E fillResourceAttributes(final E from, final List<String> attributes) {
        E resource = null;
        try {
            resource = (E) from.getClass().newInstance();
        } catch (InstantiationException e) {
            UtilsForManagers.logger.error("", e);
        } catch (IllegalAccessException e) {
            UtilsForManagers.logger.error("", e);
        }
        for (int i = 0; i < attributes.size(); i++) {
            try {
                PropertyUtils.setSimpleProperty(resource, attributes.get(i),
                    PropertyUtils.getSimpleProperty(from, attributes.get(i)));
            } catch (NoSuchMethodException e) {
                // ignore wrong attribute name
            } catch (IllegalAccessException e) {
                UtilsForManagers.logger.info("", e);
            } catch (InvocationTargetException e) {
                UtilsForManagers.logger.info("", e);
            }
        }
        return resource;
    }

}
