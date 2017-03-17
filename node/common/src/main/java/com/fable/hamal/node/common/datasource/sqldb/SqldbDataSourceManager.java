/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.common.datasource.sqldb;

import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.fable.hamal.node.common.datasource.DataSourceManage;
import com.fable.hamal.shuttle.common.data.media.DataCarrierBelongType;
import com.fable.hamal.shuttle.common.data.media.DataCarrierSource;
import com.fable.hamal.shuttle.common.data.media.DataCarrierType;
import com.fable.hamal.shuttle.common.data.media.source.DbCarrierSource;
import com.fable.hamal.shuttle.common.model.envelope.Pump;

/**
 * 
 * @author xieruidong 2013年11月12日 下午3:46:04
 */
public class SqldbDataSourceManager implements DataSourceManage {

	private static final Logger logger = LoggerFactory.getLogger(SqldbDataSourceManager.class);
	@SuppressWarnings("unchecked")
	public DataSource getDataSource(Long pumpId, DataCarrierSource carrierSource) {
		
		if (DataCarrierBelongType.SQLDB.equals(carrierSource.getType().getBelongType())) {
			DbCarrierSource source = (DbCarrierSource)carrierSource;
			return createDataSource(source.getUrl(), source.getUsername(), source.getPassword(), 
					source.getDriver(), source.getType(), source.getEncode());
		}
		return null;
	}
	
	private DataSource createDataSource(String url, String username, String password, String driverClassName,
            DataCarrierType dataCarrierType, String encoding) {
		Properties props = new Properties();
		DriverManagerDataSource dmds = new DriverManagerDataSource();
		dmds.setDriverClassName(driverClassName);
		dmds.setPassword(password);
		dmds.setUsername(username);
		dmds.setUrl(url);
		if (DataCarrierType.ORACLE.equals(dataCarrierType)) {
			props.setProperty("restrictGetTables", "true");
		} else if (DataCarrierType.MYSQL.equals(dataCarrierType)) {
			props.setProperty("useServerPrepStmts", "false");
			props.setProperty("rewriteBatchedStatements", "true");
			props.setProperty("characterEncoding", encoding);
		}
		dmds.setConnectionProperties(props);
		return dmds;
	}

	public <T> T getDataSource(Pump pump, DataCarrierSource carrierSource) {
		return null;
	}

	public void destroy(Long pumpId) {
		
	}

	public void destroy(Long pumpId, DataCarrierSource carrierSource) {
		
	}

	public void destroy(Pump pump, DataCarrierSource carrierSource) {

	}
}
