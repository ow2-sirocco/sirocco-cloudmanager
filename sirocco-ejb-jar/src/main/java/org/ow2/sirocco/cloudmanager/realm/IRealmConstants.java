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

package org.ow2.sirocco.cloudmanager.realm;

import org.ow2.sirocco.cloudmanager.common.DbParameters;

public interface IRealmConstants {

    // ldap server constants
    String LDAP_SERVER_IP = DbParameters.getInstance().LDAP_SERVER_IP;

    String LDAP_BASENAME = DbParameters.getInstance().LDAP_BASENAME;

    // shared constants
    String COMMON_TITLE_BASE = "SecurityFilter Example Application: ";

    String VALID_USERNAME = "username";

    String VALID_PASSWORD = "password";

    String VALID_USERNAME2 = "username2";

    String VALID_PASSWORD2 = "password2";

    String VALID_ROLE = "inthisrole";

    // home page constants
    String HOME_TITLE = IRealmConstants.COMMON_TITLE_BASE + "Home";

    String HOME_FORM_ID = "homeForm";

    String HOME_POST_FIELD = "postMe";

    // login form constants
    String LOGIN_TITLE = IRealmConstants.COMMON_TITLE_BASE + "Login Page";

    String LOGIN_FORM_ID = "loginForm";

    String LOGIN_FORM_ACTION = "j_security_check";

    String LOGIN_USERNAME_FIELD = "j_username";

    String LOGIN_PASSWORD_FIELD = "j_password";

    String LOGIN_REMEMBERME_FIELD = "j_rememberme";

    // secure page constants
    String SECURE_TITLE = IRealmConstants.COMMON_TITLE_BASE + "Secure Page";

    String SECURE_POSTED_VALUE_FORM = "postedValueForm";

    String SECURE_POSTED_VALUE_FIELD = "postedValue";

    String SECURE_LAST_POSTED_VALUE_FIELD = "lastPostedValue";

    // logout page constants
    String LOGOUT_TITLE = IRealmConstants.COMMON_TITLE_BASE + "Logout";

}
