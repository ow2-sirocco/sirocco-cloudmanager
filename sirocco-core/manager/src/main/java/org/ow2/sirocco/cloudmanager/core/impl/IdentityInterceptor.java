package org.ow2.sirocco.cloudmanager.core.impl;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContextHolder;

@Interceptor
@IdentityInterceptorBinding
public class IdentityInterceptor implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    IdentityContext identityContext;

    @Resource
    SessionContext sessionContext;

    @AroundInvoke
    public Object retrieveUserNameAndTenantId(final InvocationContext ctx) throws Exception {

        if (this.sessionContext.getCallerPrincipal().getName().equals("ANONYMOUS")) {
            this.identityContext.setUserName("ANONYMOUS");
        } else {
            IdentityContext idCtx = IdentityContextHolder.get();
            if (idCtx != null) {
                this.identityContext.setUserName(idCtx.getUserName());
                this.identityContext.setTenantId(idCtx.getTenantId());
            }
        }

        return ctx.proceed();
    }

}
