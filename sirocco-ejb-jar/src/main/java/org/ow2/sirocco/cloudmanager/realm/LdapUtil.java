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

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

public final class LdapUtil {

    private static Logger log = Logger.getLogger(LdapUtil.class.getName());

    private Hashtable<String, String> ldapEnv = new Hashtable<String, String>();

    private String serverIP = IRealmConstants.LDAP_SERVER_IP;

    private String baseName = IRealmConstants.LDAP_BASENAME;

    private LdapUtil() {
        this.ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        this.ldapEnv.put(Context.PROVIDER_URL, "ldap://" + this.serverIP + ":389");
        this.ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        this.ldapEnv.put(Context.SECURITY_CREDENTIALS, "");
    }

    private static LdapUtil instance;

    public static LdapUtil getInstance() {
        if (null == LdapUtil.instance) {
            LdapUtil.instance = new LdapUtil();
        }
        return LdapUtil.instance;
    }

    public String getDN(final String username) {

        try {
            this.ldapEnv.put(Context.SECURITY_CREDENTIALS, "");
            DirContext ctx = new InitialDirContext(this.ldapEnv);

            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String filter = "(sAMAccountName=" + username + ")";

            @SuppressWarnings("rawtypes")
            NamingEnumeration results = ctx.search(this.baseName, filter, constraints);

            if (results != null && results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                String dn = searchResult.getName() + "," + this.baseName;
                LdapUtil.log.info("dn = " + dn);
                return dn;
            } else {
                LdapUtil.log.info("results = " + results);
                return null;
            }
        } catch (Exception e) {
            LdapUtil.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            LdapUtil.log.info(" fetch error: " + e);
            return null;
        }
    }

    public String getName(final String username) {

        try {
            this.ldapEnv.put(Context.SECURITY_CREDENTIALS, "");
            DirContext ctx = new InitialDirContext(this.ldapEnv);

            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String filter = "(sAMAccountName=" + username + ")";

            @SuppressWarnings("rawtypes")
            NamingEnumeration results = ctx.search(this.baseName, filter, constraints);

            if (results != null && results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                final int beginIndex = 3;
                return searchResult.getName().substring(beginIndex);
            } else {
                LdapUtil.log.info("results = " + results);
                return null;
            }
        } catch (Exception e) {
            LdapUtil.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            LdapUtil.log.info(" fetch error: " + e);
            return null;
        }
    }

    public String getEmail(final String username) {

        try {
            this.ldapEnv.put(Context.SECURITY_CREDENTIALS, "");
            DirContext ctx = new InitialDirContext(this.ldapEnv);

            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String filter = "(sAMAccountName=" + username + ")";

            @SuppressWarnings("rawtypes")
            NamingEnumeration results = ctx.search(this.baseName, filter, constraints);

            if (results != null && results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                Attributes at = searchResult.getAttributes();
                return (String) at.get("mail").get(0);
            } else {
                LdapUtil.log.info("results = " + results);
                return null;
            }
        } catch (Exception e) {
            LdapUtil.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            LdapUtil.log.info(" fetch error: " + e);
            return null;
        }
    }

    public String getFirstName(final String username) {

        try {
            this.ldapEnv.put(Context.SECURITY_CREDENTIALS, "");
            DirContext ctx = new InitialDirContext(this.ldapEnv);

            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String filter = "(sAMAccountName=" + username + ")";

            @SuppressWarnings("rawtypes")
            NamingEnumeration results = ctx.search(this.baseName, filter, constraints);

            if (results != null && results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                Attributes at = searchResult.getAttributes();
                return (String) at.get("givenName").get(0);
            } else {
                LdapUtil.log.info("results = " + results);
                return null;
            }
        } catch (Exception e) {
            LdapUtil.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            LdapUtil.log.info(" fetch error: " + e);
            return null;
        }
    }

    public String getLastName(final String username) {

        try {
            this.ldapEnv.put(Context.SECURITY_CREDENTIALS, "");
            DirContext ctx = new InitialDirContext(this.ldapEnv);

            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String filter = "(sAMAccountName=" + username + ")";

            @SuppressWarnings("rawtypes")
            NamingEnumeration results = ctx.search(this.baseName, filter, constraints);

            if (results != null && results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                Attributes at = searchResult.getAttributes();
                return (String) at.get("SN").get(0);
            } else {
                LdapUtil.log.info("results = " + results);
                return null;
            }
        } catch (Exception e) {
            LdapUtil.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            LdapUtil.log.info(" fetch error: " + e);
            return null;
        }
    }

    public boolean validate(final String username, final String password) {
        try {
            if (null == username || username.length() < 1) {
                return false;
            }

            if (null == password || password.length() < 1) {
                return false;
            }

            this.ldapEnv.put(Context.SECURITY_PRINCIPAL, this.getDN(username));
            this.ldapEnv.put(Context.SECURITY_CREDENTIALS, password);

            @SuppressWarnings("unused")
            InitialLdapContext initCtx = new InitialLdapContext(this.ldapEnv, null);

            return true;
        } catch (Exception e) {
            LdapUtil.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            LdapUtil.log.info(" fetch error: " + e);
            return false;
        }
    }

}
