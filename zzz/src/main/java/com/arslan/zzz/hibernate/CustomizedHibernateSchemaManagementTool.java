package com.arslan.zzz.hibernate;

import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.internal.DefaultSchemaFilterProvider;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaFilterProvider;
import org.hibernate.tool.schema.spi.SchemaValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CustomizedHibernateSchemaManagementTool extends HibernateSchemaManagementTool implements  HibernatePropertiesCustomizer {

    @Value("${multitenant.schemas}")
    private List<String> schemas;

    @Override
    public SchemaValidator getSchemaValidator(Map<String, Object> options) {
        return new CustomSchemaValidator(this,getSchemaFilterProvider( options ).getValidateFilter(),schemas);
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.SCHEMA_MANAGEMENT_TOOL,this);
    }

    private SchemaFilterProvider getSchemaFilterProvider(Map<String,Object> options) {
        final Object configuredOption = (options == null)
                ? null
                : options.get( AvailableSettings.HBM2DDL_FILTER_PROVIDER );
        return getServiceRegistry().requireService( StrategySelector.class ).resolveDefaultableStrategy(
                SchemaFilterProvider.class,
                configuredOption,
                DefaultSchemaFilterProvider.INSTANCE
        );
    }
}
