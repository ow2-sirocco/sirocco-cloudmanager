package org.ow2.sirocco.cloudmanager.api.server;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;

@Interceptor
@ResourceInterceptorBinding
public class IdentityInterceptor implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    IdentityContext identityContext;

    @AroundInvoke
    public Object retrieveUserNameAndTenantId(final InvocationContext ctx) throws Exception {
        if (ctx.getTarget() instanceof ResourceBase) {
            ResourceBase resourceBase = (ResourceBase) ctx.getTarget();
            IdentityContext idCtx = resourceBase.getIdentityContext();
            this.identityContext.setUserName(idCtx.getUserName());
            this.identityContext.setTenantId(idCtx.getTenantId());
        }
        return ctx.proceed();
    }

}
