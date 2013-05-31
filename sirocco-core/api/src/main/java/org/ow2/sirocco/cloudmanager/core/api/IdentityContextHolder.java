package org.ow2.sirocco.cloudmanager.core.api;

public class IdentityContextHolder {
    private static final ThreadLocal<IdentityContext> THREAD_WITH_CONTEXT = new ThreadLocal<IdentityContext>();

    private IdentityContextHolder() {
    }

    public static void set(final String tenantId, final String userName) {
        if (IdentityContextHolder.THREAD_WITH_CONTEXT.get() == null) {
            IdentityContextHolder.THREAD_WITH_CONTEXT.set(new IdentityContext());
        }
        IdentityContextHolder.THREAD_WITH_CONTEXT.get().setTenantId(tenantId);
        IdentityContextHolder.THREAD_WITH_CONTEXT.get().setUserName(userName);
    }

    public static IdentityContext get() {
        return IdentityContextHolder.THREAD_WITH_CONTEXT.get();
    }
}