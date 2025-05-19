package com.arslan.zzz.multitenancy;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class HttpRequestTenantResolver implements TenantResolver<HttpServletRequest> {

    public static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    public String resolve(HttpServletRequest request) {
        return request.getHeader(TENANT_HEADER);
    }
}
