/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.schedule.job;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.fable.hamal.manager.common.utils.DateUtil;
import com.fable.hamal.manager.common.utils.constants.ScheduleConstants;
import com.fable.hamal.shuttle.common.utils.rmi.RmiUtil;
import com.fable.hamal.shuttle.common.utils.spring.HamalPropertyConfigurer;
import com.fable.hamal.shuttle.communication.client.Communication;
import com.fable.hamal.shuttle.communication.client.DefaultCommunication;
import com.fable.hamal.shuttle.communication.connection.RMIConnectionFactory;
import com.fable.hamal.shuttle.communication.event.Event;
import com.fable.hamal.shuttle.communication.event.task.TaskRunEvent;

/**
 * 任务(调度的任务)
 * 
 * @modify 修改logger为final static、修改日志内容、添加RMI执行通知 by xieruidong。
 * @author 汪朝
 * @author rdongxie
 */
public class ScheduleJob implements Job {

	private final static Logger logger = LoggerFactory
			.getLogger(ScheduleJob.class);
	private static final String NODES_RMI_ADDRESS = "nodes.communication.address";
	private static final String INIT_SERIAL = "100001";
	private static final String COLON = ":";

	public ScheduleJob() {

	}

	/** 业务处理方法 */
	public void execute(JobExecutionContext jobexecutioncontext) throws JobExecutionException {
		JobDataMap map = jobexecutioncontext.getJobDetail().getJobDataMap();
		com.fable.hamal.shuttle.common.model.envelope.data.Job job = 
				(com.fable.hamal.shuttle.common.model.envelope.data.Job) map.get(ScheduleConstants.JOB_INFO_KEY);
		Communication communication = null;
		Long jobId = job.getId();
		try {
			ApplicationContext context = (ApplicationContext)jobexecutioncontext.getScheduler().getContext().get("context");
			JdbcTemplate jdbcTemplate = (JdbcTemplate)context.getBean("jdbcTemplate");
			communication = (Communication)context.getBean("communication");
			//作业运行信息检查
			StringBuffer sqlStatus = new StringBuffer("SELECT COUNT(1) FROM JOB_RUN_INFO where TASK_ID=");
			sqlStatus.append(job.getId()).append(" AND CURRENT_STATUS='1'");
			int running = jdbcTemplate.queryForInt(sqlStatus.toString());
			if (0 != running) {
				logger.error("作业(taskid:{},name:{})正在运行中，不能同时运行多个作业。", jobId, job.getName());
				return;
			}
			List<String> serials = jdbcTemplate.query("SELECT TASK_SERIAL from TASK_RUN_SERIAL WHERE TASK_ID="+job.getId(), new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString("TASK_SERIAL");
				}
			});
			
			String serial = null;
			String today = DateUtil.getDate();
			if (null == serials || 0 == serials.size()) {
				serial = today + COLON + INIT_SERIAL;
			} else {
				serial = serials.get(0);
				String[] temp = serial.split(COLON);
				if (today.equals(temp[0])) {
					Integer ret = (Integer.valueOf(temp[1]) + 1);
					serial = temp[0] + COLON + ret.toString();
				} else {
					serial = today + COLON + INIT_SERIAL;
				}
			}
			job.setSerial(serial);
		} catch (SchedulerException e) {
			logger.error("内部错误---获取Spring上下文错误");
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("任务(taskid:{},name:{})调度开始", job.getId(), job.getName());
		}
		//通知node节点执行作业
		Event event =  new TaskRunEvent();
		event.setData(job);
		communication.call(RmiUtil.getRmiUrl(HamalPropertyConfigurer.getHamalProperty(NODES_RMI_ADDRESS)), event);
		
		if (logger.isInfoEnabled()) {
			logger.info("任务(taskid:{},name:{})调度完成，准备执行...", job.getId(), job.getName());
		}
	}
}