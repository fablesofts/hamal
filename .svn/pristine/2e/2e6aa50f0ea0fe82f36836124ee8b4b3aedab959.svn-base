/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.common.dialect.sqldb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fable.hamal.node.common.datasource.DataSourceManage;
import com.fable.hamal.shuttle.common.data.media.source.DbCarrierSource;

/**
 * 暂时没有实现基于pump的dialect缓存
 * @author xieruidong 2013年11月12日 下午4:16:24
 */
public class SqldbDialectFactory implements DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(SqldbDialectFactory.class);
	private DataSourceManage dataSourceManage;

	private DbDialectGenerator dbDialectGenerator;
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DbDialect getDbDialect(final Long pumpId, final DbCarrierSource carrierSource) {
		DataSource dataSource = dataSourceManage.getDataSource(pumpId, carrierSource);
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		DbDialect dialect = (DbDialect)jdbcTemplate.execute(new ConnectionCallback() {

		
		public Object doInConnection(Connection con) throws SQLException, DataAccessException {
				 DatabaseMetaData meta = con.getMetaData();
                 String databaseName = meta.getDatabaseProductName();
                 int databaseMajorVersion = meta.getDatabaseMajorVersion();
                 int databaseMinorVersion = meta.getDatabaseMinorVersion();
                 DbDialect dialect = dbDialectGenerator.generate(jdbcTemplate, databaseName,
                                                                 databaseMajorVersion, databaseMinorVersion, carrierSource.getType());
				return dialect;
			}
			
		});
		
		return dialect;
	}
	
	public void destroy() throws Exception {
		
	}
	
	//--------------------------------------------------------------setter---------------------------------------------
	public void setDataSourceService(DataSourceManage dataSourceService) {
        this.dataSourceManage = dataSourceService;
    }

    public void setDbDialectGenerator(DbDialectGenerator dbDialectGenerator) {
        this.dbDialectGenerator = dbDialectGenerator;
    }

	public DataSourceManage getDataSourceManage() {
		return dataSourceManage;
	}

	public void setDataSourceManage(DataSourceManage dataSourceManage) {
		this.dataSourceManage = dataSourceManage;
	}

	public DbDialectGenerator getDbDialectGenerator() {
		return dbDialectGenerator;
	}
}
