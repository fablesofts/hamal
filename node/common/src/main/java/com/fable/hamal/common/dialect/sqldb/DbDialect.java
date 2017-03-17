/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.common.dialect.sqldb;

import org.apache.ddlutils.model.Table;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 数据方言接口
 * @author xieruidong 2013年11月13日 上午10:58:22
 */
public interface DbDialect {

	public String getName();
    public String getVersion();
    public int getMajorVersion();
    public int getMinorVersion();
    public String getDefaultSchema();
    public String getDefaultCatalog();
    public boolean isCharSpacePadded();
    public boolean isCharSpaceTrimmed();
    public boolean isEmptyStringNulled();
    public boolean isSupportMergeSql();
    public LobHandler getLobHandler();
    public JdbcTemplate getJdbcTemplate();
    public TransactionTemplate getTransactionTemplate();
    public Table findTable(String schema, String table);
    public Table findTable(String schema, String table, boolean useCache);
    public void reloadTable(String schema, String table);
    public void destory();
}
