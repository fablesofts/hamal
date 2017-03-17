/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.core.config.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.fable.hamal.manager.core.config.service.ScheduleConfigService;
import com.fable.hamal.shuttle.common.model.config.ScheduleConfig;

/**
 * 
 * @author xieruidong 2013年11月21日 下午5:55:55
 */
public class ScheduleConfigServiceImpl extends BaseServiceImpl implements ScheduleConfigService {

	public List<ScheduleConfig> getAllScheduleConfigs() {
		
		return this.getJdbcTemplate().query("SELECT ID,TASK_ID,CRONTAB_EXPRESSION FROM DSP_SCHEDULE_CONFIG", new RowMapper<ScheduleConfig>() {

			public ScheduleConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
				ScheduleConfig sc = new ScheduleConfig();
				sc.setId(rs.getLong("ID"));
				sc.setTaskId(rs.getLong("TASK_ID"));
				sc.setCrontabExpression(rs.getString("CRONTAB_EXPRESSION"));
				return sc;
			}
		});
	}
	
	public List<ScheduleConfig> getScheduleConfigByTaskId(Long taskId) {
		return this.getJdbcTemplate().query("SELECT ID,TASK_ID,CRONTAB_EXPRESSION FROM DSP_SCHEDULE_CONFIG WHERE TASK_ID = " + taskId, new RowMapper<ScheduleConfig>() {

			public ScheduleConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
				ScheduleConfig sc = new ScheduleConfig();
				sc.setId(rs.getLong("ID"));
				sc.setTaskId(rs.getLong("TASK_ID"));
				sc.setCrontabExpression(rs.getString("CRONTAB_EXPRESSION"));
				return sc;
			}
		});
	}
}
