/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.controller;

import java.text.ParseException;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.manager.common.cache.JobConfigCache;
import com.fable.hamal.manager.core.job.JobService;
import com.fable.hamal.manager.schedule.service.JobScheduleService;
import com.fable.hamal.shuttle.common.model.envelope.data.Job;
import com.fable.hamal.shuttle.common.utils.rmi.RmiUtil;
import com.fable.hamal.shuttle.common.utils.spring.HamalPropertyConfigurer;
import com.fable.hamal.shuttle.communication.client.Communication;
import com.fable.hamal.shuttle.communication.event.Event;
import com.fable.hamal.shuttle.communication.event.EventHandler;
import com.fable.hamal.shuttle.communication.event.EventRegisterCenter;
import com.fable.hamal.shuttle.communication.event.task.TaskActionEventType;
import com.fable.hamal.shuttle.communication.event.task.TaskAddEvent;
import com.fable.hamal.shuttle.communication.event.task.TaskDeleteEvent;
import com.fable.hamal.shuttle.communication.event.task.TaskRunEvent;
import com.fable.hamal.shuttle.communication.event.task.TaskUpdateEvent;

/**
 * 
 * @author xieruidong 2013年11月21日 下午2:33:35
 */
public class ManagerController {

	private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);
	private JobService jobService;
	private Communication communication;
	private static final String NODES_RMI_ADDRESS ="nodes.communication.address";
	private JobScheduleService jobScheduleService;
	
	public ManagerController() {
		EventRegisterCenter.regist(TaskActionEventType.RUN, new EventHandler() {
			public Object handle(Event event) {
				if (event instanceof TaskRunEvent) {
					runJob((Long)event.getData());
				}
				return true;
			}
		});
		EventRegisterCenter.regist(TaskActionEventType.ADD, new EventHandler() {
			public Object handle(Event event) {
				if (event instanceof TaskAddEvent) {
					addJob((Long)event.getData());
				}
				return true;
			}
		});
		EventRegisterCenter.regist(TaskActionEventType.UPDATE, new EventHandler() {
			public Object handle(Event event) {
				if (event instanceof TaskUpdateEvent) {
					updateJob((Long)event.getData());
				}
				return true;
			}
		});
		EventRegisterCenter.regist(TaskActionEventType.DELETE, new EventHandler() {
			public Object handle(Event event) {
				if (event instanceof TaskDeleteEvent) {
					deleteJob((Long)event.getData());
				}
				return true;
			}
		});
	}
	
	public void start() {
		if (logger.isInfoEnabled()) {
			logger.info("Hamal manager is starting~~~");
		}
		jobService.start();
		alwaysRun();
	}
	
	/**增加作业到调度信息中*/
	public void addJob(Long jobId) {
		Job job = JobConfigCache.get(jobId);
		try {
			jobScheduleService.addJob(job);
		} catch (SchedulerException e) {
			logger.error("新增作业调度---调度异常：{}", e.getMessage());
		} catch (ParseException e) {
			logger.error("新增作业调度---调度规则转换异常：{}", e.getMessage());
		}
	}
	
	/**删除作业配置信息*/
	public void deleteJob(Long jobId) {
		Job job = JobConfigCache.get(jobId);
		try {
			jobScheduleService.deleteJob(job);
		} catch (SchedulerException e) {
			logger.error("删除作业调度---调度异常：{}", e.getMessage());
		}
	}
	/**更新作业配置信息*/
	public void updateJob(Long jobId) {
		Job job = JobConfigCache.get(jobId);
		try {
			jobScheduleService.updateJob(job);
		} catch (SchedulerException e) {
			logger.error("更新作业调度---调度异常：{}", e.getMessage());
		} catch (ParseException e) {
			logger.error("更新作业调度---调度规则转换异常：{}", e.getMessage());
		}
	}
	
	/**运行一次*/
	public void run() {
		Job job = JobConfigCache.getOne();
		Event event =  new TaskRunEvent();
		event.setData(job);
		communication.call(RmiUtil.getRmiUrl(HamalPropertyConfigurer.getHamalProperty(NODES_RMI_ADDRESS)), event);
	}
	
	public void alwaysRun() {
		Job job = JobConfigCache.getOne();
		job.setCrontabExpression("0/20 * * ? * * *");
		
		try {
			jobScheduleService.addJob(job);
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void onListener(Job job) {
		
	}
	
	public void runJob(Long jobId) {
		System.out.println(jobId);
		Job job = jobService.getJob(jobId);
		Event event =  new TaskRunEvent();
		event.setData(job);
		communication.call(RmiUtil.getRmiUrl(HamalPropertyConfigurer.getHamalProperty(NODES_RMI_ADDRESS)), event);
	}
	
	//---------------------------------------------setter&&getter------------------------------------------------------
	public JobService getJobService() {
		return jobService;
	}
	
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}
	
	public Communication getCommunication() {
		return communication;
	}
	
	public void setCommunication(Communication communication) {
		this.communication = communication;
	}

	public JobScheduleService getJobScheduleService() {
		return jobScheduleService;
	}

	public void setJobScheduleService(JobScheduleService jobScheduleService) {
		this.jobScheduleService = jobScheduleService;
	}
}
