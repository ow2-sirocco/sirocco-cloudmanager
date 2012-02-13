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

package org.ow2.sirocco.cloudmanager.common.jndilocator;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public final class ServiceLocator {
    /**
     * Singleton class allowing to locate beans through JNDI. It also implements
     * a cache where all the already used objects are put after a lookup in the
     * JNDI. The object is looked up in the cache and if it's not there, in the
     * JNDi.
     * 
     * @throws ServiceLocatorException If there's a problem finding the bean.
     * @throws ParseException If there's a problem parsing times.
     * @throws IOException On configuration file failure.
     */

    /**
     * Logger.
     */
    // private static Logger logger =
    // Logger.getLogger(ServiceLocator.class.getName());

    /* InitialContext is stored here */
    private Context initialContext;

    /* Keep cache of distant objects already retrieved */
    private Map<String, Object> cache;

    /* Define singleton */
    private static final ServiceLocator INSTANCE = new ServiceLocator();

    public static ServiceLocator getInstance() {
        return ServiceLocator.INSTANCE;
    }

    /**
     * Private constructor of ServiceLocator
     * 
     * @throws ServiceLocatorException
     */
    private ServiceLocator() throws ServiceLocatorException {
        try {
            this.initialContext = this.getInitialContext();
            this.cache = new HashMap<String, Object>();
        } catch (Exception e) {
            throw new ServiceLocatorException(e);
        }
    }

    /**
     * Get initial context - Carol JNDI
     * 
     * @return Context
     * @throws NamingException , ServiceLocatorException
     */
    public Context getInitialContext() throws ServiceLocatorException {

        try {
            return new InitialContext();
        } catch (NamingException ne) {
            throw new ServiceLocatorException(ne);
        }
    }

    /**
     * Get initial context - Carol JNDI
     * 
     * @param url Accepts a string that represents the Context.PROVIDER_URL
     *        parameter.
     * @return Context
     * @throws NamingException , ServiceLocatorException
     */
    public Context getInitialContext(final String url) throws ServiceLocatorException {

        try {
            // Get an InitialContext
            Properties h = new Properties();
            h.put(Context.INITIAL_CONTEXT_FACTORY, "org.objectweb.carol.jndi.spi.MultiOrbInitialContextFactory");
            h.put(Context.PROVIDER_URL, url);
            return new InitialContext(h);
        } catch (NamingException ne) {
            throw new ServiceLocatorException(ne);
        }
    }

    /**
     * Gets the remote object, adds it to the cache and returns it.
     * 
     * @param jndiName
     * @return Object
     * @throws ServiceLocatorException
     */
    public synchronized Object getRemoteObject(final String jndiName) throws ServiceLocatorException {

        Object remoteObject = this.cache.get(jndiName);
        if (remoteObject == null) {
            try {
                remoteObject = this.initialContext.lookup(jndiName);
                this.cache.put(jndiName, remoteObject);
            } catch (Exception e) {
                throw new ServiceLocatorException(e);
            }
        }
        return remoteObject;
    }

}
