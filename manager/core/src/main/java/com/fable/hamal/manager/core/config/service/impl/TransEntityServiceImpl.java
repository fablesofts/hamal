/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.core.config.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.fable.hamal.manager.core.config.service.TransEntityService;
import com.fable.hamal.shuttle.common.model.config.TransEntity;

/**
 * 
 * @author xieruidong 2013年11月21日 下午5:56:20
 */
public class TransEntityServiceImpl extends BaseServiceImpl implements TransEntityService {

	public List<TransEntity> getAllTransEntities() {
		
		String sql = "SELECT ID,DATA_SOURCE_ID,TYPE,TABLE_NAME,LOCATION FROM DSP_TRANS_ENTITY";
		return getTransEntities(sql);
	}
	
	public List<TransEntity> getTransEntitiesByTaskId(Long taskId) {
		StringBuffer sql = new StringBuffer("SELECT ID,DATA_SOURCE_ID,TYPE,TABLE_NAME,LOCATION FROM DSP_TRANS_ENTITY WHERE ID IN(SELECT TARGET_ID FROM DSP_PIPELINE WHERE TASK_ID = ");
		sql.append(taskId).append(" UNION ALL SELECT SOURCE_ID FROM DSP_PIPELINE WHERE TASK_ID=").append(taskId).append(")");
		return getTransEntities(sql.toString());
	}
	
	private List<TransEntity> getTransEntities(String sql) {
		return this.getJdbcTemplate().query(sql, new RowMapper<TransEntity>() {

			public TransEntity mapRow(ResultSet rs, int rowNum)	throws SQLException {
				TransEntity te = new TransEntity();
				te.setId(rs.getLong("ID"));
				te.setDataSourceId(rs.getLong("DATA_SOURCE_ID"));
				te.setLocation(rs.getString("LOCATION"));
				te.setTableName(rs.getString("TABLE_NAME"));
				te.setType(rs.getString("TYPE"));
				return te;
			}
		});
	}
}
