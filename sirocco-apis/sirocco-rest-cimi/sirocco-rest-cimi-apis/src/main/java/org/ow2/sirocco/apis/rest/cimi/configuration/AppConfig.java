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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.configuration;


public class AppConfig {

    /** Singleton */
    private static final AppConfig SINGLETON = new AppConfig();

    /** Current configuration. */
    private Config config;

    /**
     * Private constructor to protect the singleton.
     */
    private AppConfig() {
        this.config = null;
    }

    /**
     * Initialize with a specific config.
     * 
     * @param config The config
     */
    public static void initialize(final Config config) {
        synchronized (AppConfig.SINGLETON) {
            AppConfig.SINGLETON.setConfig(config);
        }
    }

    /**
     * Get the singleton instance.
     * 
     * @return The singleton instance
     */
    public static AppConfig getInstance() {
        synchronized (AppConfig.SINGLETON) {
            if (null == AppConfig.SINGLETON.config) {
                AppConfig.SINGLETON.buildDefaultConfig();
            }
        }
        return AppConfig.SINGLETON;
    }

    /**
     * Get the current configuration.
     * 
     * @return The config
     */
    public Config getConfig() {
        return this.config;
    }

    /**
     * Set the current configuration.
     * 
     * @param config The config to set
     */
    protected void setConfig(final Config config) {
        this.config = config;
    }

    /**
     * Build default configuration.
     */
    protected void buildDefaultConfig() {
        ConfigFactory factory = new ConfigFactory();
        this.setConfig(factory.getConfig());
    }

}