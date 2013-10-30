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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.ow2.sirocco.cloudmanager.core.api.IConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class SiroccoStartup {
    private static Logger logger = LoggerFactory.getLogger(SiroccoStartup.class.getName());

    @EJB
    private IConfigManager configManager;

    @PostConstruct
    void init() {
        String proxyHost = this.configManager.getConfigParameter(IConfigManager.HTTP_PROXY_HOST);
        if (proxyHost != null) {
            System.setProperty("http.proxyHost", proxyHost);
            SiroccoStartup.logger.info("Set http.proxyHost to " + proxyHost);
        }
        String proxyPort = this.configManager.getConfigParameter(IConfigManager.HTTP_PROXY_PORT);
        if (proxyPort != null) {
            System.setProperty("http.proxyPort", proxyPort);
            SiroccoStartup.logger.info("Set http.proxyPort to " + proxyPort);
        }
    }

}
