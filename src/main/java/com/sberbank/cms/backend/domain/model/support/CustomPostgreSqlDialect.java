package com.sberbank.cms.backend.domain.model.support;

import org.hibernate.dialect.PostgreSQL94Dialect;

import java.sql.Types;

@SuppressWarnings("unused")
public class CustomPostgreSqlDialect extends PostgreSQL94Dialect {
    public CustomPostgreSqlDialect() {
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }
}