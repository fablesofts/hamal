/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.core.config.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.fable.hamal.manager.core.config.service.TaskService;
import com.fable.hamal.shuttle.common.model.config.Task;

/**
 * 
 * @author xieruidong 2013年11月21日 下午4:33:34
 */
public class TaskServiceImpl extends BaseServiceImpl implements TaskService {

	public List<Task> getAllTasks() {
		return this.getJdbcTemplate().query("SELECT ID,NAME,NEED_RESOURCE,DELETE_SOURCEDATA,REBUILD_TRIGGER,SYNCHROTYPE,ASSOCIATION FROM DSP_TASK", new RowMapper<Task>() {

			public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
				Task task = new Task();
				task.setId(rs.getLong("ID"));
				task.setName(rs.getString("NAME"));
                task.setNeedResource(rs.getInt("NEED_RESOURCE"));
                task.setDeleteSourcedata(rs.getString("DELETE_SOURCEDATA"));
                task.setRebuildTrigger(rs.getString("REBUILD_TRIGGER"));
                task.setSynchroType(rs.getString("SYNCHROTYPE"));
                task.setAssociation(rs.getString("ASSOCIATION"));
				return task;
			}
		});
	}
	
	public Task getTask(Long taskId) {
		return this.getJdbcTemplate().queryForObject("SELECT ID,NAME,NEED_RESOURCE,DELETE_SOURCEDATA,REBUILD_TRIGGER,SYNCHROTYPE,ASSOCIATION FROM DSP_TASK WHERE ID=" + taskId, new RowMapper<Task>() {
			@Override
			public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
				Task task = new Task();
				task.setId(rs.getLong("ID"));
				task.setName(rs.getString("NAME"));
                task.setNeedResource(rs.getInt("NEED_RESOURCE"));
                task.setDeleteSourcedata(rs.getString("DELETE_SOURCEDATA"));
                task.setRebuildTrigger(rs.getString("REBUILD_TRIGGER"));
                task.setSynchroType(rs.getString("SYNCHROTYPE"));
                task.setAssociation(rs.getString("ASSOCIATION"));
				return task;
			}
		});
	}
}
