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
public class MysqlDialect extends AbstractDbDialect implements DbDialect {

	public MysqlDialect(JdbcTemplate jdbcTemplate, LobHandler lobHandler) {
        super(jdbcTemplate, lobHandler);
    }

    public MysqlDialect(JdbcTemplate jdbcTemplate, LobHandler lobHandler, String name, int majorVersion,
                        int minorVersion) {
        super(jdbcTemplate, lobHandler, name, majorVersion, minorVersion);
    }

    public boolean isCharSpacePadded() {
        return false;
    }

    public boolean isCharSpaceTrimmed() {
        return true;
    }

    public boolean isEmptyStringNulled() {
        return false;
    }

    public boolean isSupportMergeSql() {
        return true;
    }

    public String getDefaultSchema() {
        return null;
    }

    public String getDefaultCatalog() {
        return (String) jdbcTemplate.queryForObject("select database()", String.class);
    }
}
