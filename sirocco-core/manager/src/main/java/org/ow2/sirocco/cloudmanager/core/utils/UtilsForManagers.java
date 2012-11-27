package org.ow2.sirocco.cloudmanager.core.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJBContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.util.FilterExpressionParser;
import org.ow2.sirocco.cloudmanager.core.util.ParseException;
import org.ow2.sirocco.cloudmanager.core.util.TokenMgrError;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudCollectionItem;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;

public class UtilsForManagers {
    private static Logger logger = Logger.getLogger(UtilsForManagers.class.getName());

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
        UtilsForManagers.emitJMSMessage(payload, ctx, "JobEmission", 0, 0);
    }

    public static void emitJobCompletionMessage(final Serializable payload, final EJBContext ctx, final long delayMillis,
        final long deliveriesCounter) throws Exception {
        UtilsForManagers.emitJMSMessage(payload, ctx, "JobCompletion", delayMillis, deliveriesCounter);
    }

    /**
     * emits an JMS message to a queue, <b>inside a JTA transaction</b>
     * 
     * @param payload the message body
     * @param ctx to send the message inside the ctx transaction
     * @param queueName
     * @throws Exception
     */
    public static void emitJMSMessage(final Serializable payload, final EJBContext ctx, final String queueName,
        final long delayMillis, final long deliveriesCounter) throws Exception {
        ConnectionFactory cf = (ConnectionFactory) ctx.lookup("QCF");
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
    public static List getEntityList(final String entityType, final EntityManager em, final String username,
        final boolean verifyDeletedState) {
        String userQuery = "", stateQuery = "";

        if (!(("".equals(username) || username == null))) {
            userQuery = " v.user.username=:username ";
        }
        if (verifyDeletedState) {
            if (userQuery.length() > 0) {
                stateQuery = " AND ";
            }
            stateQuery = stateQuery + " v.state<>'DELETED' ";
        }
        return em.createQuery("FROM " + entityType + " v WHERE " + userQuery + stateQuery + " ORDER BY v.id")
            .setParameter("username", username).getResultList();

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
    public static List getEntityList(final String entityType, final EntityManager em, final String username) {
        return UtilsForManagers.getEntityList(entityType, em, username, true);

    }

    private static String generateFilterClause(final List<String> filters, final String variableName) throws ParseException {
        StringBuffer jpqlFilterClause = new StringBuffer();
        if (filters != null) {
            for (String filter : filters) {
                FilterExpressionParser parser = new FilterExpressionParser(filter, variableName);
                parser.parse();
                if (jpqlFilterClause.length() > 0) {
                    jpqlFilterClause.append(" AND ");
                }
                jpqlFilterClause.append(parser.getQuery());
            }
        }
        return jpqlFilterClause.toString();
    }

    public static <E> QueryResult<E> getEntityList(final String entityType, final Class<E> clazz, final EntityManager em,
        final String username, final int first, final int last, final List<String> filters, final List<String> attributes,
        final boolean verifyDeletedState) throws InvalidRequestException {
        StringBuffer whereClauseSB = new StringBuffer();
        if (username != null) {
            whereClauseSB.append(" v.user.username=:username ");
        }
        if (verifyDeletedState) {
            if (whereClauseSB.length() > 0) {
                whereClauseSB.append(" AND ");
            }
            whereClauseSB.append(" v.state<>'DELETED' ");
        }
        if (filters != null) {
            String filterClause;
            try {
                filterClause = UtilsForManagers.generateFilterClause(filters, "v");
            } catch (ParseException ex) {
                throw new InvalidRequestException("Parsing error in filter expression " + ex.getMessage());
            } catch (TokenMgrError ex) {
                throw new InvalidRequestException(ex.getMessage());
            }
            if (!filterClause.isEmpty()) {
                if (whereClauseSB.length() > 0) {
                    whereClauseSB.append(" AND ");
                }
                whereClauseSB.append(filterClause);
            }
        }
        String whereClause = whereClauseSB.toString();

        try {
            int count = ((Number) em.createQuery("SELECT COUNT(v) FROM " + entityType + " v WHERE " + whereClause)
                .setParameter("username", username).getSingleResult()).intValue();
            Query query = em.createQuery("FROM " + entityType + " v  WHERE " + whereClause + " ORDER BY v.id").setParameter(
                "username", username);
            if (first != -1) {
                query.setFirstResult(first);
            }
            if (last != -1) {
                if (first != -1) {
                    query.setMaxResults(last - first + 1);
                } else {
                    query.setMaxResults(last + 1);
                }
            }
            List<E> queryResult = query.getResultList();
            if (attributes != null && attributes.size() != 0) {
                List<E> items = new ArrayList<E>();
                for (E from : queryResult) {
                    E resource = clazz.newInstance();
                    for (int i = 0; i < attributes.size(); i++) {
                        try {
                            PropertyUtils.setSimpleProperty(resource, attributes.get(i),
                                PropertyUtils.getSimpleProperty(from, attributes.get(i)));
                        } catch (NoSuchMethodException e) {
                            // ignore wrong attribute name
                        }
                    }
                    items.add(resource);
                }
                return new QueryResult<E>(count, items);
            } else {
                return new QueryResult<E>(count, queryResult);
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            throw new InvalidRequestException(ex.getMessage());
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            throw new InvalidRequestException(ex.getMessage());
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            throw new InvalidRequestException(ex.getMessage());
        } catch (InvocationTargetException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    public static <E> E fillResourceAttributes(final E from, final List<String> attributes) {
        E resource = null;
        try {
            resource = (E) from.getClass().newInstance();
        } catch (InstantiationException e) {
            UtilsForManagers.logger.fatal("", e);
        } catch (IllegalAccessException e) {
            UtilsForManagers.logger.fatal("", e);
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

    public static <E> QueryResult<E> getCollectionItemList(final String entityType, final Class<E> clazz,
        final EntityManager em, final String username, final int first, final int last, final List<String> filters,
        final List<String> attributes, final boolean verifyDeletedState, final String containerType,
        final String containerAttributeName, final String containerId) throws InvalidRequestException {
        StringBuffer whereClauseSB = new StringBuffer();
        if (username != null) {
            whereClauseSB.append(" v.user.username=:username ");
        }
        if (verifyDeletedState) {
            if (whereClauseSB.length() > 0) {
                whereClauseSB.append(" AND ");
            }
            whereClauseSB.append(" vv.state<>'DELETED' ");
        }
        if (whereClauseSB.length() > 0) {
            whereClauseSB.append(" AND ");
        }
        whereClauseSB.append("v.id=:cid ");
        if (filters != null) {
            String filterClause;
            try {
                filterClause = UtilsForManagers.generateFilterClause(filters, "vv");
            } catch (ParseException ex) {
                throw new InvalidRequestException("Parsing error in filter expression " + ex.getMessage());
            } catch (TokenMgrError ex) {
                throw new InvalidRequestException(ex.getMessage());
            }
            if (!filterClause.isEmpty()) {
                if (whereClauseSB.length() > 0) {
                    whereClauseSB.append(" AND ");
                }
                whereClauseSB.append(filterClause);
            }
        }

        String whereClause = whereClauseSB.toString();
        String queryExpression = "SELECT COUNT(vv) FROM " + entityType + " vv, " + containerType + " v WHERE vv MEMBER OF v."
            + containerAttributeName + " AND " + whereClause;
        try {
            int count = ((Number) em.createQuery(queryExpression).setParameter("cid", Integer.valueOf(containerId))
                .setParameter("username", username).getSingleResult()).intValue();
            queryExpression = "SELECT vv FROM " + entityType + " vv, " + containerType + " v WHERE vv MEMBER OF v."
                + containerAttributeName + " AND " + whereClause + " ORDER BY vv.id";
            Query query = em.createQuery(queryExpression).setParameter("cid", Integer.valueOf(containerId))
                .setParameter("username", username);

            if (first != -1) {
                query.setFirstResult(first);
            }
            if (last != -1) {
                if (first != -1) {
                    query.setMaxResults(last - first + 1);
                } else {
                    query.setMaxResults(last + 1);
                }
            }
            List<E> queryResult = query.getResultList();
            if (attributes != null && attributes.size() != 0) {
                List<E> items = new ArrayList<E>();
                for (E from : queryResult) {
                    E resource = clazz.newInstance();
                    for (int i = 0; i < attributes.size(); i++) {
                        try {
                            PropertyUtils.setSimpleProperty(resource, attributes.get(i),
                                PropertyUtils.getSimpleProperty(from, attributes.get(i)));
                        } catch (NoSuchMethodException e) {
                            // ignore wrong attribute name
                        }
                    }
                    items.add(resource);
                }
                return new QueryResult<E>(count, items);
            } else {
                return new QueryResult<E>(count, queryResult);
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            throw new InvalidRequestException(ex.getMessage());
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            throw new InvalidRequestException(ex.getMessage());
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            throw new InvalidRequestException(ex.getMessage());
        } catch (InvocationTargetException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    /**
     * gets a cloudCollection from an Id
     * 
     * @param em
     * @param entityId
     * @return
     * @throws CloudProviderException
     */
    public static CloudCollectionItem getCloudCollectionById(final EntityManager em, final String entityId)
        throws CloudProviderException {
        CloudCollectionItem obj = (CloudCollectionItem) em
            .createQuery("FROM " + CloudCollectionItem.class.getName() + " WHERE v.id=:idd").setParameter("idd", entityId)
            .getSingleResult();
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
        CloudResource obj = (CloudResource) em.createQuery("FROM " + CloudResource.class.getName() + " v WHERE v.id=:idd")
            .setParameter("idd", new Integer(resourceId)).getSingleResult();
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
    public static CloudCollectionItem getCloudCollectionFromCloudResource(final EntityManager em, final CloudResource ce)
        throws CloudProviderException {
        CloudCollectionItem obj = (CloudCollectionItem) em
            .createQuery("FROM " + CloudCollectionItem.class.getName() + " v WHERE v.resource.id=:resourceId")
            .setParameter("resourceId", ce.getId()).getSingleResult();
        if (obj == null) {
            throw new CloudProviderException("bad id given");
        }
        return obj;
    }

    public static CloudResource getResourceFromProviderId(final EntityManager em, final String providerAsynchId)
        throws CloudProviderException {
        CloudResource obj = (CloudResource) em
            .createQuery("FROM " + CloudResource.class.getName() + " v WHERE v.providerAssignedId=:provid")
            .setParameter("provid", providerAsynchId).getSingleResult();
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
