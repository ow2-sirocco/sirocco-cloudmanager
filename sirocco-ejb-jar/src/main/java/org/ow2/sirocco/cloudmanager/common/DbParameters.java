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

package org.ow2.sirocco.cloudmanager.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.DbParametersVO;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.service.api.ICloudSettings;

public final class DbParameters {

    private static Logger log = Logger.getLogger(DbParameters.class.getName());

    /**
     * Singleton class used to initialize database settings of application
     */
    private static DbParameters instance;

    public static DbParameters getInstance() {
        if (null == DbParameters.instance) {
            DbParameters.instance = new DbParameters();
        }
        return DbParameters.instance;
    }

    private static String adminMail;

    public String VM_EXPIRED_MAIL1;

    public String VM_EXPIRED_MAIL2;

    public String SERVICE_NAME;

    public String SERVICE_URL;

    public String VM_CREATED;

    public String VM_ROOT_PWD;

    public String LDAP_SERVER_IP;

    public String LDAP_BASENAME;

    public String MAIL_HOST;

    public String MAIL_PORT;

    public String VNC_PROXY_URL;

    public String HOST_SSH_KEY_FILE;

    public int DEFAULT_DISK_QUOTA_IN_MB;

    public int DEFAULT_RAM_QUOTA_IN_MB;

    public int DEFAULT_CPU_QUOTA;

    public int DEFAULT_VM_QUOTA;

    public int PURGE_STATS_MONTH;

    public int STATS_COLLECT_INTERVAL_MS;

    public String TARGET_AUDIENCE;

    public boolean AUTO_CREATE_ACCOUNT;

    public boolean SEND_EMAIL_UPON_VM_CREATION;

    public boolean USE_LDAP_SERVER_FOR_AUTHENTIFICATION;

    public int VM_REFRESH_PERIOD_SECONDS;

    public int BOOKING_PERIOD_EXPRESSED_IN_TIME_UNIT;

    private DbParameters() {
        try {
            InitialContext context = new InitialContext();
            ICloudSettings cloudSettings = (ICloudSettings) context.lookup(ICloudSettings.EJB_JNDI_NAME);

            DbParameters.setAdminMail(cloudSettings.getValue("ADMIN_MAIL"));
            this.VM_EXPIRED_MAIL1 = cloudSettings.getValue("VM_EXPIRED_MAIL1");
            this.VM_EXPIRED_MAIL2 = cloudSettings.getValue("VM_EXPIRED_MAIL2");
            this.SERVICE_NAME = cloudSettings.getValue("SERVICE_NAME");
            this.SERVICE_URL = cloudSettings.getValue("SERVICE_URL");
            this.VM_CREATED = cloudSettings.getValue("VM_CREATED");
            this.VM_ROOT_PWD = cloudSettings.getValue("VM_ROOT_PWD");
            this.LDAP_SERVER_IP = cloudSettings.getValue("LDAP_SERVER_IP");
            this.LDAP_BASENAME = cloudSettings.getValue("LDAP_BASENAME");
            this.MAIL_HOST = cloudSettings.getValue("MAIL_HOST");
            this.MAIL_PORT = cloudSettings.getValue("MAIL_PORT");
            this.VNC_PROXY_URL = cloudSettings.getValue("VNC_PROXY_URL");
            this.HOST_SSH_KEY_FILE = cloudSettings.getValue("HOST_SSH_KEY_FILE");
            this.DEFAULT_DISK_QUOTA_IN_MB = Integer.parseInt(cloudSettings.getValue("DEFAULT_DISK_QUOTA_IN_MB"));
            this.DEFAULT_RAM_QUOTA_IN_MB = Integer.parseInt(cloudSettings.getValue("DEFAULT_RAM_QUOTA_IN_MB"));
            this.DEFAULT_CPU_QUOTA = Integer.parseInt(cloudSettings.getValue("DEFAULT_CPU_QUOTA"));
            this.DEFAULT_VM_QUOTA = Integer.parseInt(cloudSettings.getValue("DEFAULT_VM_QUOTA"));
            this.PURGE_STATS_MONTH = Integer.parseInt(cloudSettings.getValue("PURGE_STATS_MONTH"));
            this.STATS_COLLECT_INTERVAL_MS = Integer.parseInt(cloudSettings.getValue("STATS_COLLECT_INTERVAL_MS"));
            this.TARGET_AUDIENCE = cloudSettings.getValue("TARGET_AUDIENCE");
            this.BOOKING_PERIOD_EXPRESSED_IN_TIME_UNIT = Integer.parseInt(cloudSettings
                .getValue("BOOKING_PERIOD_EXPRESSED_IN_TIME_UNIT"));
            this.AUTO_CREATE_ACCOUNT = Boolean.parseBoolean(cloudSettings.getValue("AUTO_CREATE_ACCOUNT", "false"));
            this.USE_LDAP_SERVER_FOR_AUTHENTIFICATION = Boolean.parseBoolean(cloudSettings.getValue(
                "USE_LDAP_SERVER_FOR_AUTHENTIFICATION", "false"));
            this.SEND_EMAIL_UPON_VM_CREATION = Boolean.parseBoolean(cloudSettings.getValue("SEND_EMAIL_UPON_VM_CREATION",
                "false"));
            this.VM_REFRESH_PERIOD_SECONDS = Integer.parseInt(cloudSettings.getValue("VM_REFRESH_PERIOD_SECONDS", "60"));

        } catch (CloudProviderException e) {
            DbParameters.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
        } catch (NamingException e) {
            DbParameters.log.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
        }
    }

    public static String getAdminMail() {
        return DbParameters.adminMail;
    }

    public static void setAdminMail(final String adminMail) {
        DbParameters.adminMail = adminMail;
    }

    public DbParametersVO toValueObject() {
        DbParametersVO dbParam = new DbParametersVO();
        dbParam.setTargetAudience(DbParameters.getInstance().TARGET_AUDIENCE);
        return dbParam;
    }

}
