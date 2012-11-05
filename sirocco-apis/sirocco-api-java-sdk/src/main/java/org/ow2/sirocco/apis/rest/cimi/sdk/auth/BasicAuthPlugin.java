/**
 *
 * SIROCCO
 * Copyright (C) 2012 France Telecom
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
package org.ow2.sirocco.apis.rest.cimi.sdk.auth;

import java.util.Collections;
import java.util.Map;

import org.ow2.sirocco.apis.rest.cimi.sdk.AuthPlugin;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiException;

import com.sun.jersey.core.util.Base64;

public class BasicAuthPlugin implements AuthPlugin {

    @Override
    public Map<String, String> authenticate(final String user, final String password) throws CimiException {
        StringBuilder sbToEncode = new StringBuilder();
        sbToEncode.append(user).append(':').append(password);
        StringBuilder sb = new StringBuilder();
        sb.append("Basic ").append(new String(Base64.encode(sbToEncode.toString())));
        return Collections.singletonMap("Authorization", sb.toString());
    }

}
