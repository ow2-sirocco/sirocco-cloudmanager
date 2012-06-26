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
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;

import org.hibernate.proxy.HibernateProxy;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;

public class UtilsForManagers {

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
    public static Object fillObject(Object obj, Map<String, Object> updatedAttributes) throws InstantiationException,
        IllegalAccessException, IllegalArgumentException, IntrospectionException, NoSuchFieldException,
        InvocationTargetException {

        for (Map.Entry<String, Object> attr : updatedAttributes.entrySet()) {
            invokeSetter(obj, attr.getKey(), attr.getValue());
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
    private static Object invokeSetter(Object targetObj, String attrName, Object attrValue) throws IntrospectionException,
        NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        BeanInfo info = Introspector.getBeanInfo(targetObj.getClass());
        for (PropertyDescriptor pd : info.getPropertyDescriptors())
            if (attrName.equals(pd.getName()))
                return pd.getWriteMethod().invoke(targetObj, attrValue);
        throw new NoSuchFieldException(targetObj.getClass() + " has no field " + attrName);
    }

    public static Object getEntityThroughProxy(Object o) {
        if (o instanceof HibernateProxy) {
            HibernateProxy oProxy = (HibernateProxy) o;
            o = (Object) oProxy.getHibernateLazyInitializer().getImplementation();
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
    public static void emitJobListenerMessage(final Serializable payload, EJBContext ctx) throws Exception {
        emitJMSMessage(payload, ctx, "JobEmission");
    }

    /**
     * emits an JMS message to a queue, <b>inside a JTA transaction</b>
     * 
     * @param payload the message body
     * @param ctx to send the message inside the ctx transaction
     * @param queueName
     * @throws Exception
     */
    public static void emitJMSMessage(final Serializable payload, EJBContext ctx, String queueName) throws Exception {
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

    /**
     * code factoring for getXXX (getMachines, getVolumes,etc)
     * 
     * @param entityType
     * @param em
     * @param username optionnal, filter request to given user
     * @param verifyDeletedState if the query should ignore deleted entities.<br>
     *        Must be set to false if an entity doesn't have a state field
     * @return
     */
    @SuppressWarnings({"rawtypes"})
    public static List getEntityList(String entityType, EntityManager em, String username, boolean verifyDeletedState) {
        String userQuery = "", stateQuery = "";

        if (!(("".equals(username) || username == null))) {
            userQuery = " v.user.username=:username ";
        }
        if (verifyDeletedState) {
            stateQuery = " v.state<>'DELETED' ";
        }
        return em
            .createQuery(
                "FROM " + entityType + " v WHERE " + userQuery + (userQuery.equals("") ? "" : " AND ") + stateQuery
                    + " ORDER BY v.id").setParameter("username", username).getResultList();

    }

    /**
     * same as full getEntityList, but automatically sets verifyDeletedState to
     * true
     * 
     * @param entityType
     * @param em
     * @param username
     * @return
     */
    @SuppressWarnings({"rawtypes"})
    public static List getEntityList(String entityType, EntityManager em, String username) {
        return getEntityList(entityType, em, username, true);

    }

    /**
     * gets a cloudCollection from an Id
     * 
     * @param em
     * @param entityId
     * @return
     * @throws CloudProviderException
     */
    public static CloudCollection getCloudCollectionById(final EntityManager em, final String entityId)
        throws CloudProviderException {
        CloudCollection obj = (CloudCollection) em
            .createQuery("FROM " + CloudCollection.class.getName() + " WHERE v.id=:idd ORDER BY v.id")
            .setParameter("idd", entityId).getSingleResult();
        if (obj == null) {
            throw new CloudProviderException("bad id given");
        }
        return obj;
    }

    /**
     * gets a cloudResource from an id
     * 
     * @param em
     * @param resourceId
     * @return
     * @throws CloudProviderException
     */
    public static CloudResource getCloudResourceById(final EntityManager em, final String resourceId)
        throws CloudProviderException {
        CloudResource obj = (CloudResource) em
            .createQuery("FROM " + CloudResource.class.getName() + " WHERE v.id=:idd ORDER BY v.id")
            .setParameter("idd", resourceId).getSingleResult();
        if (obj == null) {
            throw new CloudProviderException("bad id given");
        }
        return obj;
    }

    /**
     * gets a cloudCollection linked to a cloudResource
     * 
     * @param em
     * @param ce
     * @return
     * @throws CloudProviderException
     */
    public static CloudCollection getCloudCollectionFromCloudResource(final EntityManager em, CloudResource ce)
        throws CloudProviderException {
        CloudCollection obj = (CloudCollection) em
            .createQuery("FROM " + CloudCollection.class.getName() + " WHERE v.resource.id=:resourceId ORDER BY v.id")
            .setParameter("resourceId", ce.getId().toString()).getSingleResult();
        if (obj == null) {
            throw new CloudProviderException("bad id given");
        }
        return obj;
    }

    /*
     * public static List<CloudCollection>
     * getCloudCollectionsFromParentResource(final EntityManager em, String
     * parentResourceId, String collectionType) throws CloudProviderException {
     * @SuppressWarnings("unchecked") List<CloudCollection> objs =
     * (List<CloudCollection>) em .createQuery("FROM " + collectionType +
     * " WHERE v.resource=:resource ORDER BY v.id") .setParameter("resource",
     * ce.getId().toString()); return objs; }
     */

}
