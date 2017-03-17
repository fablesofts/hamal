/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.core.config.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.fable.hamal.manager.core.config.service.PipelineService;
import com.fable.hamal.shuttle.common.model.config.Pipeline;

/**
 * 
 * @author xieruidong 2013年11月21日 下午5:37:57
 */
public class PipelineServiceImpl extends BaseServiceImpl implements PipelineService {

	/**获取所有pipelines*/
	public List<Pipeline> getAllPipelines() {

		String sql = "SELECT ID,TASK_ID,SOURCE_ID,TARGET_ID FROM DSP_PIPELINE";
		return getPipelines(sql);
	}
	
	/**获取taskId对应的pipelines*/
	public List<Pipeline> getPipelinesByTaskId(Long taskId) {
		String sql = "SELECT ID,TASK_ID,SOURCE_ID,TARGET_ID FROM DSP_PIPELINE WHERE TASK_ID = " + taskId;
		return getPipelines(sql);
	}
	
	/**获取pipelines*/
	private List<Pipeline> getPipelines(String sql) {
		return this.getJdbcTemplate().query(sql, new RowMapper<Pipeline>() {

			public Pipeline mapRow(ResultSet rs, int rowNum) throws SQLException {
				Pipeline pipeline = new Pipeline();
				pipeline.setId(rs.getLong("ID"));
				pipeline.setTaskId(rs.getLong("TASK_ID"));
				pipeline.setSourceId(rs.getLong("SOURCE_ID"));
				pipeline.setTargetId(rs.getLong("TARGET_ID"));
				return pipeline;
			}
		});
	}
}
