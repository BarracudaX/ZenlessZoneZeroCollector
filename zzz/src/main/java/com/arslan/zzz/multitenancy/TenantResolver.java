package com.arslan.zzz.multitenancy;

public interface TenantResolver<T> {

    String resolve(T t);

}
