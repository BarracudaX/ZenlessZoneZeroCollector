package com.arslan.zzz.hibernate;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;
import org.hibernate.tool.schema.internal.AbstractSchemaValidator;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.spi.ContributableMatcher;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaFilter;
import org.hibernate.tool.schema.spi.SchemaValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class CustomSchemaValidator extends AbstractSchemaValidator {

    private final List<String> schemas = new ArrayList<>();

    public CustomSchemaValidator(HibernateSchemaManagementTool tool, SchemaFilter validateFilter, List<String> schemas) {
        super(tool, validateFilter);
        this.schemas.addAll(schemas);
    }

    @Override
    protected void validateTables(Metadata metadata, DatabaseInformation databaseInformation, ExecutionOptions options, ContributableMatcher contributableInclusionFilter, Dialect dialect, Namespace namespace) {
        for ( Table table : namespace.getTables() ) {
            if ( schemaFilter.includeTable( table )
                    && table.isPhysicalTable()
                    && contributableInclusionFilter.matches( table ) ) {
                for(var schema : schemas){
                    final TableInformation tableInformation = databaseInformation.getTableInformation(new Namespace.Name(null, Identifier.toIdentifier(schema)),table.getNameIdentifier());
                    validateTable( table, tableInformation, metadata, options, dialect );
                }
            }
        }
    }
}
