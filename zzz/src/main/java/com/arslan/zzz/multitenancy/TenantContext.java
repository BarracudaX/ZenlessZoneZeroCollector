package com.arslan.zzz.multitenancy;

public final class TenantContext {

    private static final ThreadLocal<String> tenantID = new InheritableThreadLocal<>();

    private TenantContext(){

    }

    public static void setTenantID(String id){
        tenantID.set(id);
    }

    public static String getTenantID(){
        return tenantID.get();
    }

    public static void clear(){
        tenantID.remove();
    }

}
