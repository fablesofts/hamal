/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.core.config.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.fable.hamal.manager.core.config.service.EtlStrategyService;
import com.fable.hamal.shuttle.common.model.config.EtlStrategy;

/**
 * 
 * @author xieruidong 2013年11月21日 下午5:55:22
 */
public class EtlStrategyServiceImpl extends BaseServiceImpl implements EtlStrategyService {

	/**获取所有etlStrategy*/
	public List<EtlStrategy> getAllEtlStrategies() {

		String sql = "SELECT ID,PIPELINE_ID,FROM_TABLE,TO_TABLE,CONTENT_FILTER FROM DSP_ETL_STRATEGY";
		return getEtlStrategies(sql);
	}
	
	/**获取pipeline对应的etlstrategies*/
	public List<EtlStrategy> getEtlStratgiesBypipelineId(Long pipelineId) {
		String sql = "SELECT ID,PIPELINE_ID,FROM_TABLE,TO_TABLE,CONTENT_FILTER FROM DSP_ETL_STRATEGY WHERE PIPELINE_ID = " + pipelineId;
		return getEtlStrategies(sql);
	}
	
	/**获取task对应的所有etl策略*/
	public List<EtlStrategy> getEtlStratgiesBytaskId(Long taskId) {
		String sql = "SELECT ID,PIPELINE_ID,FROM_TABLE,TO_TABLE,CONTENT_FILTER FROM DSP_ETL_STRATEGY WHERE PIPELINE_ID in(select id from dsp_pipeline where TASK_ID= " +taskId+")";
		return getEtlStrategies(sql);
	}
	
	/**获取pipelines*/
	private List<EtlStrategy> getEtlStrategies(String sql) {
		return this.getJdbcTemplate().query(sql, new RowMapper<EtlStrategy>() {

			public EtlStrategy mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlStrategy etlStrategy = new EtlStrategy();
				etlStrategy.setId(rs.getLong("ID"));
				etlStrategy.setPipelieId(rs.getLong("PIPELINE_ID"));
				etlStrategy.setFromTable(rs.getString("FROM_TABLE"));
				etlStrategy.setToTable(rs.getString("TO_TABLE"));
				etlStrategy.setContentFilter(rs.getString("CONTENT_FILTER"));
				
				return etlStrategy;
			}
		});
	}
}
