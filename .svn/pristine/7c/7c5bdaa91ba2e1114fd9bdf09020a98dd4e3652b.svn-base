/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.common.dialect.sqldb;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.LobHandler;

/**
 * 
 * @author xieruidong 2013年11月11日 下午2:35:05
 */
public class Dameng7Dialect extends AbstractDbDialect {

	public Dameng7Dialect(JdbcTemplate jdbcTemplate, LobHandler lobHandler) {
        super(jdbcTemplate, lobHandler);
    }

    public Dameng7Dialect(JdbcTemplate jdbcTemplate, LobHandler lobHandler, String name, int majorVersion,
                         int minorVersion) {
        super(jdbcTemplate, lobHandler, name, majorVersion, minorVersion);
    }

    public boolean isCharSpacePadded() {
        return true;
    }

    public boolean isCharSpaceTrimmed() {
        return false;
    }

    public boolean isEmptyStringNulled() {
        return true;
    }

    public boolean storesUpperCaseNamesInCatalog() {
        return true;
    }

    public boolean isSupportMergeSql() {
        return true;
    }

    public String getDefaultCatalog() {
        return null;
    }

    public String getDefaultSchema() {
        return (String) jdbcTemplate.queryForObject("SELECT sys_context('USERENV', 'CURRENT_SCHEMA') FROM dual",
                                                    String.class);
    }
}
