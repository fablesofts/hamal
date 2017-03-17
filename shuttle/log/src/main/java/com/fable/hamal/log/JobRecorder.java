/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.fable.hamal.log.constants.JobCurrentStatus;
import com.fable.hamal.log.event.TrackerEventTypes;
import com.fable.hamal.log.model.SysLog;
import com.fable.hamal.log.model.SysLogDetail;
import com.fable.hamal.shuttle.communication.event.Event;
import com.fable.hamal.shuttle.communication.event.EventHandler;
import com.fable.hamal.shuttle.communication.event.EventRegisterCenter;
import com.fable.hamal.shuttle.communication.event.rate.JobRateEventType;

/**
 * manager用来记录作业运行信息
 * @author xieruidong 2014年5月21日 下午7:20:24
 */
public class JobRecorder {

	private JdbcTemplate jdbcTemplate;
	
	public JobRecorder() {
		/**作业新建*/
		EventRegisterCenter.regist(TrackerEventTypes.TRACKER_TASK_START, new EventHandler() {
			@Override
			public Object handle(Event event) {
				SysLog log = (SysLog)event.getData();
				handleStart(log);
				System.out.println(log.getTaskName());
				return true;
			}
		});
		
		EventRegisterCenter.regist(TrackerEventTypes.TRACKER_TASK_END, new EventHandler() {

			@Override
			public Object handle(Event event) {
				SysLog log = (SysLog)event.getData();
				handleEnd(log);
				System.out.println(log.getTaskName());
				return true;
			}
		});
		
		EventRegisterCenter.regist(TrackerEventTypes.SYS_LOG_DETAIL, new EventHandler() {

			@Override
			public Object handle(Event event) {
				SysLogDetail log = (SysLogDetail)event.getData();
				System.out.println(log.getTaskSerial());
				return true;
			}
		});
		
		EventRegisterCenter.regist(JobRateEventType.JOB_RATE, new EventHandler() {

			@Override
			public Object handle(Event event) {
				Map<String, Long> rate = (ConcurrentHashMap<String, Long>)event.getData();
				recordRates(rate);
				return true;
			}
		});
	}

	/**记录作业开始，上锁*/
	public void handleStart(final SysLog log) {
		//1.更新作业运行信息表
		String infoSql = "insert into JOB_RUN_INFO(TASK_ID,BATCH,CURRENT_STATUS,START_TIME) values(?,?,?,?)";
		Object[] infoArgs = {log.getTaskId(), log.getTaskSerial(), JobCurrentStatus.RUNNING, log.getStartTime()};
		jdbcTemplate.update(infoSql,infoArgs);
		//2.记录日志
		String logSql = "insert into SYS_LOG (TASK_ID,TASKNAME,TASK_SERIAL,OPERATION_RESULTS,START_TIME) " +
		        "values(?,?,?,?,?)";
//		Object[] args = {log.getTaskId(), log.getTaskSerial(), log.getOperationResults(), log.getStartTime()};
		jdbcTemplate.update(logSql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, log.getTaskId());
				ps.setString(2, log.getTaskName());
				ps.setString(3, log.getTaskSerial());
				ps.setString(4, log.getOperationResults());
				ps.setTimestamp(5, log.getStartTime());
			}
		});
//		jdbcTemplate.update(logSql, args);
	}
	
	/**记录作业结束*/
	public void handleEnd(SysLog log) {
		//1.更新作业运行信息
		String infosql = "update JOB_RUN_INFO set CURRENT_STATUS=?, FINISH_TIME= ? where TASK_ID=? and BATCH=? and PIPELINE_ID=0";
		Object[] infoArgs = {JobCurrentStatus.FINISHED, log.getEndTime(), log.getTaskId(), log.getTaskSerial()};
		jdbcTemplate.update(infosql, infoArgs);
		//2.更新日志信息
		String logSql = "update SYS_LOG set END_TIME=?, OPERATION_RESULTS=? where TASK_ID=? and TASK_SERIAL=?";
		Object[] logArgs = {log.getEndTime(), log.getOperationResults(), log.getTaskId(), log.getTaskSerial()};
		jdbcTemplate.update(logSql, logArgs);
		//3.插入作业序列号
		String judgeSql = "select count(1) from TASK_RUN_SERIAL WHERE TASK_ID=?";
		int count = jdbcTemplate.queryForInt(judgeSql, log.getTaskId());
		String serialSql = null;
		if (0 == count) {
			serialSql = "insert into TASK_RUN_SERIAL(TASK_SERIAL, TASK_ID) values(?,?)";
		} else {
			serialSql = "update TASK_RUN_SERIAL set TASK_SERIAL=? where TASK_ID=?";
		}
		jdbcTemplate.update(serialSql, new Object[]{log.getTaskSerial(), log.getTaskId()});
	}
	
	/**记录流量*/
	public void recordRates(Map<String, Long> rates) {
		if (null != rates && !rates.isEmpty() ) {
			String sqlInfo = "update JOB_RUN_INFO set SELECT_RATE=?, LOAD_RATE=? where TASK_ID=? and BATCH=? and PIPELINE_ID=0";
			String sqlLog = "update SYS_LOG set SELECT_COUNT=?, LOAD_COUNT=? where TASK_ID=? and TASK_SERIAL=?";
			Map<String, Rate> temp = new HashMap<String, Rate>();
			final List<Rate> rateList = new ArrayList<Rate>();
			
			Iterator<String> iter = rates.keySet().iterator();
			while(iter.hasNext()) {
				String key = iter.next();
				String[] ids = key.split("_");
				String tempKey = ids[0]+ids[1];
				Rate rate = temp.get(tempKey);
				if (null == rate) {
					rate = new Rate();
					temp.put(tempKey, rate);
				}
				rate.setTaskId(new Long(ids[0]));
				rate.setTaskSeria(ids[1]);
				if ("selector".equals(ids[2])) {
					rate.setSelectRate(rates.get(key));
				} else if ("loader".equals(ids[2])) {
					rate.setLoadRate(rates.get(key));
				}
				rateList.add(rate);
			}
			
			updateRate(sqlInfo, rateList);
			updateRate(sqlLog, rateList);
		}
	}
	
	public void updateRate(String sql, final List<Rate> rates) {
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Rate rate = rates.get(i);
				ps.setLong(1, rate.getSelectRate());
				ps.setLong(2, rate.getLoadRate());
				ps.setLong(3, rate.getTaskId());
				ps.setString(4, rate.getTaskSeria());
			}

			@Override
			public int getBatchSize() {
				return rates.size();
			}
		});
	}
	
	/**用来流量统计*/
	class Rate {
		private Long taskId;
		private String taskSeria;
		private Long selectRate;
		private Long loadRate;
		
		public Long getTaskId() {
			return taskId;
		}
		public void setTaskId(Long taskId) {
			this.taskId = taskId;
		}
		public String getTaskSeria() {
			return taskSeria;
		}
		public void setTaskSeria(String taskSeria) {
			this.taskSeria = taskSeria;
		}
		public Long getSelectRate() {
			return selectRate;
		}
		public void setSelectRate(Long selectRate) {
			this.selectRate = selectRate;
		}
		public Long getLoadRate() {
			return loadRate;
		}
		public void setLoadRate(Long loadRate) {
			this.loadRate = loadRate;
		}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
