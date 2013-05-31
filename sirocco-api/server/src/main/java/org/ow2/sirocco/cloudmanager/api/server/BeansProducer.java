package org.ow2.sirocco.cloudmanager.api.server;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;

public class BeansProducer {

    @Produces
    ICloudProviderManager getCloudProviderManager() {
        try {
            Context context = new InitialContext();
            return (ICloudProviderManager) context
                .lookup("java:global/org.ow2.sirocco.cloudmanager.sirocco-cloudmanager-core-manager_0.6.0.SNAPSHOT/CloudProviderManager!org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Produces
    ITenantManager getTenantManager() {
        try {
            Context context = new InitialContext();
            return (ITenantManager) context
                .lookup("java:global/org.ow2.sirocco.cloudmanager.sirocco-cloudmanager-core-manager_0.6.0.SNAPSHOT/TenantManager!org.ow2.sirocco.cloudmanager.core.api.ITenantManager");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Produces
    IUserManager getUserManager() {
        try {
            Context context = new InitialContext();
            return (IUserManager) context
                .lookup("java:global/org.ow2.sirocco.cloudmanager.sirocco-cloudmanager-core-manager_0.6.0.SNAPSHOT/UserManager!org.ow2.sirocco.cloudmanager.core.api.IUserManager");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Produces
    @RequestScoped
    IdentityContext getIdentityContext() {
        return new IdentityContext();
    }
}
