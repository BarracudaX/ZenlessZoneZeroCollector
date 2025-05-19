package com.arslan.zzz.web;

import com.arslan.zzz.multitenancy.TenantContext;
import com.arslan.zzz.multitenancy.TenantResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Component
public class TenantHandlerInterceptor implements HandlerInterceptor {

    private final TenantResolver<HttpServletRequest> resolver;

    public TenantHandlerInterceptor(TenantResolver<HttpServletRequest> resolver) {
        this.resolver = resolver;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var tenantID = resolver.resolve(request);

        TenantContext.setTenantID(tenantID);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        clear();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        clear();
    }

    private void clear(){
        TenantContext.clear();
    }


}
