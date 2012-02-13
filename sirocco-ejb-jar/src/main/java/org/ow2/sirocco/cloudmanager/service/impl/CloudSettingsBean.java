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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.service.impl;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudSettings;
import org.ow2.sirocco.cloudmanager.provider.api.exception.NoInitializationParamException;
import org.ow2.sirocco.cloudmanager.service.api.ICloudSettings;

@Stateless(name = ICloudSettings.EJB_JNDI_NAME, mappedName = ICloudSettings.EJB_JNDI_NAME)
@Local(ICloudSettings.class)
public class CloudSettingsBean implements ICloudSettings {

    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(CloudSettingsBean.class.getName());

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    public String getValue(final String name) throws NoInitializationParamException {
        Query query = this.em.createQuery("SELECT s.value FROM CloudSettings s WHERE s.name=:name").setParameter("name", name);
        @SuppressWarnings("unchecked")
        List<String> list = query.getResultList();
        if (list.isEmpty()) {
            throw new NoInitializationParamException("Parameter named: " + name
                + " hasn't been found in DB. This is a required parameter.");
        } else {
            return list.get(0);
        }
    }

    @Override
    public String getValue(final String key, final String defaultValue) {
        try {
            return this.getValue(key);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public void addValue(final String name, final String value) {
        CloudSettings cs = new CloudSettings(name, value);
        this.em.persist(cs);
        this.em.flush();
    }

}
