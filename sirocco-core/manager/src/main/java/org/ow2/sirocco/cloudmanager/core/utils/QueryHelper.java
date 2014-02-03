/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
 * Contact: sirocco@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *
 */
package org.ow2.sirocco.cloudmanager.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.beanutils.PropertyUtils;
import org.ow2.sirocco.cloudmanager.core.api.QueryParams;
import org.ow2.sirocco.cloudmanager.core.api.QueryResult;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudCollectionItem;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.Identifiable;
import org.ow2.sirocco.cloudmanager.model.cimi.Resource;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.ICloudProviderResource;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.IMultiCloudResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryHelper {
    private static Logger logger = LoggerFactory.getLogger(QueryHelper.class.getName());

    public static class QueryParamsBuilder {
        private String entityType;

        private Class<?> clazz;

        private Integer tenantId;

        private Integer first;

        private Integer last;

        private String marker;

        private Integer limit;

        private String providerUuid;

        private String locationUuid;

        private List<String> filters;

        private List<String> attributes;

        private Enum<?> stateToIgnore = null;

        private boolean filterEmbbededTemplate;

        private String containerType;

        private String containerAttributeName;

        private String containerId;

        private boolean returnPublicEntities = false;

        private QueryParamsBuilder(final String entityType, final Class<?> clazz) {
            this.entityType = entityType;
            this.clazz = clazz;
        }

        public static QueryParamsBuilder builder(final String entityType, final Class<?> clazz) {
            return new QueryParamsBuilder(entityType, clazz);
        }

        public QueryParamsBuilder tenantId(final Integer tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public QueryParamsBuilder first(final int first) {
            this.first = first;
            if (first == -1) {
                this.first = null;
            }
            return this;
        }

        public QueryParamsBuilder containerType(final String containerType) {
            this.containerType = containerType;
            return this;
        }

        public QueryParamsBuilder containerAttributeName(final String containerAttributeName) {
            this.containerAttributeName = containerAttributeName;
            return this;
        }

        public QueryParamsBuilder containerId(final String containerId) {
            this.containerId = containerId;
            return this;
        }

        public QueryParamsBuilder filterByProvider(final String providerUuid) {
            this.providerUuid = providerUuid;
            return this;
        }

        public QueryParamsBuilder filterByLocation(final String locationUuid) {
            this.locationUuid = locationUuid;
            return this;
        }

        public QueryParamsBuilder filter(final List<String> filters) {
            this.filters = filters;
            return this;
        }

        public QueryParamsBuilder attributes(final List<String> attributes) {
            this.attributes = attributes;
            return this;
        }

        public QueryParamsBuilder stateToIgnore(final Enum<?> stateToIgnore) {
            this.stateToIgnore = stateToIgnore;
            return this;
        }

        public QueryParamsBuilder filterEmbbededTemplate() {
            this.filterEmbbededTemplate = true;
            return this;
        }

        public QueryParamsBuilder returnPublicEntities() {
            this.returnPublicEntities = true;
            return this;
        }

        public QueryParamsBuilder last(final int last) {
            this.last = last;
            if (last == -1) {
                this.last = null;
            }
            return this;
        }

        public QueryParamsBuilder params(final QueryParams queryParams) {
            if (queryParams != null) {
                this.first = queryParams.getFirst();
                this.last = queryParams.getLast();
                this.filters = queryParams.getFilters();
                this.attributes = queryParams.getAttributes();
                this.marker = queryParams.getMarker();
                this.limit = queryParams.getLimit();
                this.providerUuid = queryParams.getProviderUuid();
                this.locationUuid = queryParams.getLocationUuid();
            }
            return this;
        }

        public String getEntityType() {
            return this.entityType;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public Integer getTenantId() {
            return this.tenantId;
        }

        public Integer getFirst() {
            return this.first;
        }

        public Integer getLast() {
            return this.last;
        }

        public String getProviderUuid() {
            return this.providerUuid;
        }

        public String getLocationUuid() {
            return this.locationUuid;
        }

        public List<String> getFilters() {
            return this.filters;
        }

        public List<String> getAttributes() {
            return this.attributes;
        }

        public Enum<?> getStateToIgnore() {
            return this.stateToIgnore;
        }

        public String getMarker() {
            return this.marker;
        }

        public Integer getLimit() {
            return this.limit;
        }

        public boolean isFilterEmbbededTemplate() {
            return this.filterEmbbededTemplate;
        }

        public String getContainerType() {
            return this.containerType;
        }

        public String getContainerAttributeName() {
            return this.containerAttributeName;
        }

        public String getContainerId() {
            return this.containerId;
        }

        public boolean isReturnPublicEntities() {
            return this.returnPublicEntities;
        }

    }

    /**
     * code factoring for getXXX (getMachines, getVolumes,etc)
     * 
     * @param entityType
     * @param em
     * @param tenantId optional, filter request to given tenant
     * @param verifyDeletedState if the query should ignore deleted entities.<br>
     *        Must be set to false if an entity doesn't have a state field
     * @return
     */
    @SuppressWarnings({"rawtypes"})
    public static List getEntityList(final String entityType, final EntityManager em, final Integer tenantId,
        final Enum stateToIgnore, final boolean returnPublicEntities) {
        String tenantQuery = "", stateQuery = "";

        if (tenantId != null) {
            if (!returnPublicEntities) {
                tenantQuery = " v.tenant.id=:tenantId ";
            } else {
                tenantQuery = " (v.tenant.id=:tenantId OR v.visibility = org.ow2.sirocco.cloudmanager.model.cimi.extension.Visibility.PUBLIC) ";
            }
        }
        if (stateToIgnore != null) {
            if (tenantQuery.length() > 0) {
                stateQuery = " AND ";
            }
            stateQuery = stateQuery + " v.state<>" + stateToIgnore.getClass().getName() + "." + stateToIgnore.name() + " ";
        }
        return em.createQuery("SELECT v FROM " + entityType + " v WHERE " + tenantQuery + stateQuery + " ORDER BY v.id DESC")
            .setParameter("tenantId", tenantId).getResultList();

    }

    public static <E extends Identifiable> QueryResult<E> getEntityList(final EntityManager em, final QueryParamsBuilder params)
        throws InvalidRequestException {
        StringBuffer whereClauseSB = new StringBuffer();
        if (params.getTenantId() != null) {
            if (!params.isReturnPublicEntities()) {
                whereClauseSB.append(" v.tenant.id=:tenantId ");
            } else {
                whereClauseSB
                    .append("( v.tenant.id=:tenantId OR v.visibility = org.ow2.sirocco.cloudmanager.model.cimi.extension.Visibility.PUBLIC) ");
            }
        }
        if (params.getStateToIgnore() != null) {
            if (whereClauseSB.length() > 0) {
                whereClauseSB.append(" AND ");
            }
            whereClauseSB.append(" v.state<>" + params.getStateToIgnore().getClass().getName() + "."
                + params.getStateToIgnore().name() + " ");
        }
        if (params.isFilterEmbbededTemplate()) {
            if (whereClauseSB.length() > 0) {
                whereClauseSB.append(" AND ");
            }
            whereClauseSB.append(" v.isEmbeddedInSystemTemplate=false ");
        }
        if (params.getFilters() != null) {
            String filterClause;
            try {
                filterClause = QueryHelper.generateFilterClause(params.getFilters(), "v", params.getClazz().getName()
                    + "$State.");
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
        if (params.getProviderUuid() != null) {
            if (whereClauseSB.length() > 0) {
                whereClauseSB.append(" AND ");
            }
            whereClauseSB.append(" v.cloudProviderAccount.uuid='" + params.getProviderUuid() + "' ");
        }
        if (params.getLocationUuid() != null) {
            if (whereClauseSB.length() > 0) {
                whereClauseSB.append(" AND ");
            }
            whereClauseSB.append(" v.location.uuid='" + params.getLocationUuid() + "' ");
        }

        String whereClause = whereClauseSB.toString();

        try {
            int count = ((Number) em.createQuery("SELECT COUNT(v) FROM " + params.getEntityType() + " v WHERE " + whereClause)
                .setParameter("tenantId", params.getTenantId()).getSingleResult()).intValue();

            if (params.getMarker() != null) {
                try {
                    Resource resourceAtMarker = (Resource) em
                        .createQuery("SELECT r FROM " + params.getEntityType() + " r WHERE r.uuid=:uuid")
                        .setParameter("uuid", params.getMarker()).getSingleResult();
                    if (whereClauseSB.length() > 0) {
                        whereClauseSB.append(" AND ");
                    }
                    whereClauseSB.append(" v.id>" + resourceAtMarker.getId() + " ");
                } catch (NoResultException e) {
                    throw new InvalidRequestException("Invalid marker " + params.getMarker());
                }
            }
            whereClause = whereClauseSB.toString();

            Query query = em.createQuery(
                "SELECT v FROM " + params.getEntityType() + " v  WHERE " + whereClause + " ORDER BY v.id DESC").setParameter(
                "tenantId", params.getTenantId());
            if (params.getLimit() != null) {
                query.setMaxResults(params.getLimit());
            } else {
                if (params.getFirst() != null) {
                    query.setFirstResult(params.getFirst());
                }
                if (params.getLast() != null) {
                    if (params.getFirst() != null) {
                        query.setMaxResults(params.getLast() - params.getFirst() + 1);
                    } else {
                        query.setMaxResults(params.getLast() + 1);
                    }
                }
            }
            List<E> queryResult = query.getResultList();
            if (params.getAttributes() != null && params.getAttributes().size() != 0) {
                List<E> items = new ArrayList<E>();
                for (E from : queryResult) {
                    E resource = (E) params.getClazz().newInstance();
                    for (int i = 0; i < params.getAttributes().size(); i++) {
                        try {
                            PropertyUtils.setSimpleProperty(resource, params.getAttributes().get(i),
                                PropertyUtils.getSimpleProperty(from, params.getAttributes().get(i)));
                        } catch (NoSuchMethodException e) {
                            // ignore wrong attribute name
                        }
                    }
                    resource.setUuid(from.getUuid());
                    if (resource instanceof ICloudProviderResource) {
                        ICloudProviderResource fromResource = (ICloudProviderResource) from;
                        ICloudProviderResource toResource = (ICloudProviderResource) resource;
                        toResource.setLocation(fromResource.getLocation());
                        toResource.setProviderAssignedId(fromResource.getProviderAssignedId());
                        toResource.setCloudProviderAccount(fromResource.getCloudProviderAccount());
                    } else if (resource instanceof IMultiCloudResource) {
                        IMultiCloudResource fromResource = (IMultiCloudResource) from;
                        IMultiCloudResource toResource = (IMultiCloudResource) resource;
                        toResource.setProviderMappings(fromResource.getProviderMappings());
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
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    public static <E> QueryResult<E> getCollectionItemList(final EntityManager em, final QueryParamsBuilder params)
        throws InvalidRequestException {
        StringBuffer whereClauseSB = new StringBuffer();
        if (params.getTenantId() != null) {
            whereClauseSB.append(" v.tenant.id=:tenantId ");
        }
        if (params.getStateToIgnore() != null) {
            if (whereClauseSB.length() > 0) {
                whereClauseSB.append(" AND ");
            }
            whereClauseSB.append(" vv.state<>" + params.getStateToIgnore().getClass().getName() + "."
                + params.getStateToIgnore().name() + " ");
        }
        if (whereClauseSB.length() > 0) {
            whereClauseSB.append(" AND ");
        }
        whereClauseSB.append("v.uuid=:cid ");
        if (params.getFilters() != null) {
            String filterClause;
            try {
                filterClause = QueryHelper.generateFilterClause(params.getFilters(), "vv", params.getClazz().getName()
                    + "$State.");
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
        String queryExpression = "SELECT COUNT(vv) FROM " + params.getEntityType() + " vv, " + params.getContainerType()
            + " v WHERE vv MEMBER OF v." + params.getContainerAttributeName() + " AND " + whereClause;
        try {
            int count = ((Number) em.createQuery(queryExpression).setParameter("cid", params.getContainerId())
                .setParameter("tenantId", params.getTenantId()).getSingleResult()).intValue();
            queryExpression = "SELECT vv FROM " + params.getEntityType() + " vv, " + params.getContainerType()
                + " v WHERE vv MEMBER OF v." + params.getContainerAttributeName() + " AND " + whereClause + " ORDER BY vv.id";
            Query query = em.createQuery(queryExpression).setParameter("cid", params.getContainerId())
                .setParameter("tenantId", params.getTenantId());

            if (params.getFirst() != null) {
                query.setFirstResult(params.getFirst());
            }
            if (params.getLast() != null) {
                if (params.getFirst() != null) {
                    query.setMaxResults(params.getLast() - params.getFirst() + 1);
                } else {
                    query.setMaxResults(params.getLast() + 1);
                }
            }
            List<E> queryResult = query.getResultList();
            if (params.getAttributes() != null && params.getAttributes().size() != 0) {
                List<E> items = new ArrayList<E>();
                for (E from : queryResult) {
                    E resource = (E) params.getClazz().newInstance();
                    for (int i = 0; i < params.getAttributes().size(); i++) {
                        try {
                            PropertyUtils.setSimpleProperty(resource, params.getAttributes().get(i),
                                PropertyUtils.getSimpleProperty(from, params.getAttributes().get(i)));
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
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    private static String generateFilterClause(final List<String> filters, final String variableName,
        final String stateClassPrefix) throws ParseException {
        StringBuffer jpqlFilterClause = new StringBuffer();
        if (filters != null) {
            for (String filter : filters) {
                FilterExpressionParser parser = new FilterExpressionParser(filter, variableName, stateClassPrefix);
                parser.parse();
                if (jpqlFilterClause.length() > 0) {
                    jpqlFilterClause.append(" AND ");
                }
                jpqlFilterClause.append(parser.getQuery());
            }
        }
        return jpqlFilterClause.toString();
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
            .createQuery("SELECT v FROM CloudCollectionItem v  WHERE v.uuid=:idd").setParameter("idd", entityId)
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
    public static CloudResource getCloudResourceById(final EntityManager em, final int resourceId)
        throws CloudProviderException {
        CloudResource obj = (CloudResource) em.createQuery("SELECT v FROM CloudResource v WHERE v.id=:idd")
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
    public static CloudCollectionItem getCloudCollectionFromCloudResource(final EntityManager em, final CloudResource ce)
        throws CloudProviderException {
        CloudCollectionItem obj = (CloudCollectionItem) em
            .createQuery("SELECT v FROM CloudCollectionItem v WHERE v.resource.id=:resourceId")
            .setParameter("resourceId", ce.getId()).getSingleResult();
        if (obj == null) {
            throw new CloudProviderException("bad id given");
        }
        return obj;
    }

    public static CloudResource getResourceFromProviderId(final EntityManager em, final String providerAsynchId)
        throws CloudProviderException {
        CloudResource obj = (CloudResource) em.createQuery("SELECT v FROM CloudResource v WHERE v.providerAssignedId=:provid")
            .setParameter("provid", providerAsynchId).getSingleResult();
        if (obj == null) {
            throw new CloudProviderException("bad id given");
        }
        return obj;
    }
}
