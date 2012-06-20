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

package org.ow2.sirocco.apis.rest.cimi.tools;

import javax.naming.Context;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.ow2.jonas.security.auth.callback.NoInputCallbackHandler;

public class EJBClient {
    private static final String INITIAL_CONTEXT_FACTORY = "org.ow2.carol.jndi.spi.MultiOrbInitialContextFactory";

    public static void connect(final String endpoint, final String login, final String password) throws Exception {
        // Obtain a CallbackHandler
        CallbackHandler handler = new NoInputCallbackHandler(login, password);

        // Set properties for Initial Context : JAAS and EJB
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, EJBClient.INITIAL_CONTEXT_FACTORY);
        System.setProperty(Context.PROVIDER_URL, endpoint);

        // Obtain a LoginContext
        LoginContext lc = null;
        try {
            lc = new LoginContext("jaasclient", handler);
        } catch (LoginException le) {
            System.err.println("Cannot create LoginContext: " + le.getMessage());
            throw le;
        } catch (SecurityException se) {
            System.err.println("Cannot create LoginContext: " + se.getMessage());
            throw se;
        }
        // Login
        try {
            lc.login();
        } catch (LoginException le) {
            System.err.println("Authentication failed : " + le.getMessage());
            throw le;
        }
    }

}
