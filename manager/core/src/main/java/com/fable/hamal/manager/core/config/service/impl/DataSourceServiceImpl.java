/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.core.config.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.fable.hamal.manager.core.config.service.DataSourceService;
import com.fable.hamal.shuttle.common.model.config.DataSource;

/**
 * 
 * @author xieruidong 2013年11月21日 下午5:54:44
 */
public class DataSourceServiceImpl extends BaseServiceImpl implements DataSourceService {

	public List<DataSource> getAllDataSources() {

		String sql = "SELECT ID,NAME,SERVER_IP,SOURCE_TYPE,USERNAME,PASSWORD,PORT,DEVICE_TYPE,DB_NAME,DB_TYPE,CONNECT_URL FROM DSP_DATA_SOURCE";
		return getDataSources(sql);
	}
	
	public List<DataSource> getDataSourcesByTaskId(Long taskId) {
		StringBuffer sql = new StringBuffer("SELECT ID,NAME,SERVER_IP,SOURCE_TYPE,USERNAME,PASSWORD,PORT,DEVICE_TYPE,DB_NAME,DB_TYPE,CONNECT_URL FROM DSP_DATA_SOURCE WHERE ID IN(");
		sql.append("SELECT DATA_SOURCE_ID FROM DSP_TRANS_ENTITY ").append("WHERE ID IN(SELECT TARGET_ID FROM DSP_PIPELINE WHERE TASK_ID=");
		sql.append(taskId).append(" UNION ALL SELECT SOURCE_ID FROM DSP_PIPELINE WHERE TASK_ID=").append(taskId).append("))");
		return getDataSources(sql.toString());
	}
	
	private List<DataSource> getDataSources(String sql) {
		return this.getJdbcTemplate().query(sql, new RowMapper<DataSource>() {

			public DataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataSource ds = new DataSource();
				ds.setId(rs.getLong("ID"));
				ds.setName(rs.getString("NAME"));
				ds.setServerIp(rs.getString("SERVER_IP"));
				ds.setSourceType(rs.getString("SOURCE_TYPE"));
				ds.setUsername(rs.getString("USERNAME"));
				ds.setPassword(rs.getString("PASSWORD"));
				ds.setPort(rs.getInt("PORT"));
				ds.setDeviceType(rs.getString("DEVICE_TYPE"));
				ds.setDbName(rs.getString("DB_NAME"));
				ds.setDbType(rs.getString("DB_TYPE"));
				ds.setConnectUrl(rs.getString("CONNECT_URL"));
				return ds;
			}
		});
	}
}
