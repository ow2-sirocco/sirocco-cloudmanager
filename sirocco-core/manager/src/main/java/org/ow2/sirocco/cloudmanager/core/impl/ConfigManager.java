/**
 *
 * SIROCCO
 * Copyright (C) 2013 Orange
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
 */
package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.core.api.IConfigManager;
import org.ow2.sirocco.cloudmanager.model.utils.SiroccoConfiguration;

@Stateless
@Local(IConfigManager.class)
public class ConfigManager implements IConfigManager {
    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    private SiroccoConfiguration getConfig() {
        @SuppressWarnings("unchecked")
        List<SiroccoConfiguration> l = this.em.createQuery("SELECT c FROM SiroccoConfiguration c").getResultList();
        if (l.size() > 0) {
            return l.get(0);
        }
        SiroccoConfiguration config = new SiroccoConfiguration();
        this.em.persist(config);
        return config;
    }

    @Override
    public void setConfigParameter(final String key, final String value) {
        SiroccoConfiguration config = this.getConfig();
        switch (key) {
        case IConfigManager.HTTP_PROXY_HOST:
            config.setHttpProxyHost(value);
            System.setProperty("http.proxyHost", value);
            break;
        case IConfigManager.HTTP_PROXY_PORT:
            config.setHttpProxyPort(value);
            System.setProperty("http.proxyPort", value);
            break;
        }
    }

    @Override
    public Map<String, String> getConfigParameters() {
        SiroccoConfiguration config = this.getConfig();
        HashMap<String, String> result = new HashMap<>();
        result.put(IConfigManager.HTTP_PROXY_HOST, config.getHttpProxyHost());
        result.put(IConfigManager.HTTP_PROXY_PORT, config.getHttpProxyPort());
        return result;
    }

    @Override
    public String getConfigParameter(final String key) {
        SiroccoConfiguration config = this.getConfig();
        switch (key) {
        case IConfigManager.HTTP_PROXY_HOST:
            return config.getHttpProxyHost();
        case IConfigManager.HTTP_PROXY_PORT:
            return config.getHttpProxyPort();
        default:
            return null;
        }
    }

}
