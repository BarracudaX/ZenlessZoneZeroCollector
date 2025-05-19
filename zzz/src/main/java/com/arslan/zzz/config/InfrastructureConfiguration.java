package com.arslan.zzz.config;

import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.MultiTenantSpringLiquibase;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.orm.jpa.*;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.ManagedClassNameFilter;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypesScanner;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({HibernateProperties.class,JpaProperties.class,LiquibaseProperties.class})
public class InfrastructureConfiguration {


    @Value("${multitenant.schemas}")
    private List<String> schemas;

    private final JpaProperties jpaProperties;

    private final HibernateProperties hibernateProperties;

    private final DataSource dataSource;

    private final List<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers;


    public InfrastructureConfiguration(JpaProperties jpaProperties, HibernateProperties hibernateProperties, DataSource dataSource, List<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers) {
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
        this.dataSource = dataSource;
        this.hibernatePropertiesCustomizers = hibernatePropertiesCustomizers;
    }

    @Bean
    public MultiTenantSpringLiquibase multiTenantSpringLiquibase(DataSource dataSource, LiquibaseProperties properties){
        var liquibase = new MultiTenantSpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(properties.getChangeLog());
        liquibase.setClearCheckSums(properties.isClearChecksums());
        liquibase.setSchemas(schemas);

        return liquibase;
    }

    @Bean
    public PlatformTransactionManager transactionManager(){
        return new JpaTransactionManager();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(this.jpaProperties.isShowSql());
        if (this.jpaProperties.getDatabase() != null) {
            adapter.setDatabase(this.jpaProperties.getDatabase());
        }
        if (this.jpaProperties.getDatabasePlatform() != null) {
            adapter.setDatabasePlatform(this.jpaProperties.getDatabasePlatform());
        }
        adapter.setGenerateDdl(this.jpaProperties.isGenerateDdl());
        return adapter;
    }

    @DependsOn("multiTenantSpringLiquibase")
    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaVendorAdapter jpaVendorAdapter,
                                                                   ObjectProvider<PersistenceUnitManager> persistenceUnitManager,
                                                                   ObjectProvider<EntityManagerFactoryBuilderCustomizer> customizers) {
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(jpaVendorAdapter,
                this::buildJpaProperties, persistenceUnitManager.getIfAvailable());
        customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder factoryBuilder,
                                                                       PersistenceManagedTypes persistenceManagedTypes) {
        return factoryBuilder.dataSource(this.dataSource)
                .managedTypes(persistenceManagedTypes)
                .mappingResources(getMappingResources())
                .jta(false)
                .build();
    }

    @Bean
    static PersistenceManagedTypes persistenceManagedTypes(BeanFactory beanFactory, ResourceLoader resourceLoader,
                                                           ObjectProvider<ManagedClassNameFilter> managedClassNameFilter) {
        String[] packagesToScan = getPackagesToScan(beanFactory);
        return new PersistenceManagedTypesScanner(resourceLoader, managedClassNameFilter.getIfAvailable())
                .scan(packagesToScan);
    }

    private static String[] getPackagesToScan(BeanFactory beanFactory) {
        List<String> packages = EntityScanPackages.get(beanFactory).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(beanFactory)) {
            packages = AutoConfigurationPackages.get(beanFactory);
        }
        return StringUtils.toStringArray(packages);
    }

    private String[] getMappingResources() {
        List<String> mappingResources = this.jpaProperties.getMappingResources();
        return (!ObjectUtils.isEmpty(mappingResources) ? StringUtils.toStringArray(mappingResources) : null);
    }



    private Map<String, Object> getVendorProperties(DataSource dataSource) {
        return new LinkedHashMap<>(
                this.hibernateProperties.determineHibernateProperties(
                        jpaProperties.getProperties(), new HibernateSettings().hibernatePropertiesCustomizers(this.hibernatePropertiesCustomizers))
        );
    }

    private Map<String, ?> buildJpaProperties(DataSource dataSource) {
        Map<String, Object> properties = new HashMap<>(this.jpaProperties.getProperties());
        Map<String, Object> vendorProperties = getVendorProperties(dataSource);
        properties.putAll(vendorProperties);
        return properties;
    }
}
