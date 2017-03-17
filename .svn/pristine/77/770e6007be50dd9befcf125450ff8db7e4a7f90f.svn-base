/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.common.dialect.sqldb;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.LobHandler;

import com.fable.hamal.shuttle.common.data.media.DataCarrierType;
import com.fable.hamal.shuttle.common.utils.constant.DbType;

/**
 * 
 * @author xieruidong 2013年11月13日 下午3:33:45
 */
public class DbDialectGenerator {

    protected static final String ORACLE = "oracle";
    protected static final String MYSQL = "mysql";
    protected static final String MSSQL = "Microsoft";
    protected static final String DAMENG = "dameng";
    protected static final String TDDL_GROUP  = "TGroupDatabase";
    protected static final String TDDL_CLIENT = "TDDL";

    protected LobHandler          defaultLobHandler;
    protected LobHandler          oracleLobHandler;

    protected DbDialect generate(JdbcTemplate jdbcTemplate, String databaseName, int databaseMajorVersion,
                                 int databaseMinorVersion, DataCarrierType dataCarrierType) {
        DbDialect dialect = null;
        if (StringUtils.startsWithIgnoreCase(databaseName, ORACLE)) {
            dialect = new OracleDialect(jdbcTemplate, oracleLobHandler, databaseName, databaseMajorVersion, databaseMinorVersion);
        } else if (StringUtils.startsWithIgnoreCase(databaseName, MYSQL)) {
            dialect = new MysqlDialect(jdbcTemplate, defaultLobHandler, databaseName, databaseMajorVersion, databaseMinorVersion);
        } else if (StringUtils.startsWithIgnoreCase(databaseName, MSSQL)) {
        	dialect = new MssqlDialect(jdbcTemplate, defaultLobHandler, databaseName, databaseMajorVersion, databaseMinorVersion);
        } else if (DbType.DAMENG6.equals(dataCarrierType.getShortName())) {
        	dialect = new Dameng6Dialect(jdbcTemplate, defaultLobHandler, databaseName, databaseMajorVersion, databaseMinorVersion);
        }else if (DbType.DAMENG7.equals(dataCarrierType.getShortName())) {
        	dialect = new Dameng7Dialect(jdbcTemplate, defaultLobHandler, databaseName, databaseMajorVersion, databaseMinorVersion);
        } else if (StringUtils.startsWithIgnoreCase(databaseName, TDDL_GROUP)) {
            throw new RuntimeException(databaseName + " type is not support!");
        } else if (StringUtils.startsWithIgnoreCase(databaseName, TDDL_CLIENT)) {
            throw new RuntimeException(databaseName + " type is not support!");
        }
        return dialect;
    }

    //----------------------------------------------------------setter-------------------------------------------------
    public void setDefaultLobHandler(LobHandler defaultLobHandler) {
        this.defaultLobHandler = defaultLobHandler;
    }

    public void setOracleLobHandler(LobHandler oracleLobHandler) {
        this.oracleLobHandler = oracleLobHandler;
    }
}
