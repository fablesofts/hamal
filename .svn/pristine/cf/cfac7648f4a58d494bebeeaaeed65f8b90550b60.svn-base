/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.schedule.service.impl;

import java.text.ParseException;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.fable.hamal.manager.common.utils.constants.ScheduleConstants;
import com.fable.hamal.manager.schedule.job.ScheduleJob;
import com.fable.hamal.manager.schedule.service.JobScheduleService;
import com.fable.hamal.shuttle.common.model.envelope.data.Job;

/**
 * 任务调度服务实现类--singleton--目前假设只有一个trigger
 * @author xieruidong
 */
public class JobScheduleServiceImpl implements JobScheduleService {

	private final static Logger logger = LoggerFactory.getLogger(JobScheduleServiceImpl.class);
	private Scheduler scheduler;
	
	@Override
	public void addJob(Job job) throws SchedulerException, ParseException {
		Assert.notNull(job);
		String name = job.getId().toString();
		String jobName = name;
		String triggerName = name + ScheduleConstants.TRIGGER_SUBFIX;

		try {
			JobDetail jobTemp = scheduler.getJobDetail(jobName, ScheduleConstants.JOB_GROUP_DEFAULT);
			if (null != jobTemp) {
				updateJob(job);
				return;
			}
			
			JobDetail jobDetail = new JobDetail(jobName, ScheduleConstants.JOB_GROUP_DEFAULT, ScheduleJob.class);
			jobDetail.getJobDataMap().put(ScheduleConstants.JOB_INFO_KEY, job);
			CronTrigger cronTrigger = new CronTrigger(triggerName,ScheduleConstants.TRIGGER_GROUP_DEFAULT);
			cronTrigger.setCronExpression(job.getCrontabExpression());
			scheduler.scheduleJob(jobDetail, cronTrigger);
			if (!scheduler.isShutdown()) {
				scheduler.start();
			}
		} catch (SchedulerException e) {
			logger.error("任务(taskid:{})调度出错", name);
			throw e;
		} catch (ParseException e) {
			logger.error("作业(taskid:{})触发规则设置出错,对应的CronExpression:{}", name, job.getCrontabExpression());
			throw e;
		}
	}

	@Override
	public void updateJob(Job job) throws SchedulerException, ParseException {
		Assert.notNull(job);
		String jobName = job.getId().toString();
		String triggerName = jobName + ScheduleConstants.TRIGGER_SUBFIX;
		String crontabExpression = job.getCrontabExpression();
		Assert.notNull(crontabExpression);
		
		try {
			JobDetail jobDetail = scheduler.getJobDetail(jobName, ScheduleConstants.JOB_GROUP_DEFAULT);
			if (null != jobDetail) {
				jobDetail.getJobDataMap().put(ScheduleConstants.JOB_INFO_KEY, job);
			} else {
				addJob(job);
				return;
			}
			
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerName, ScheduleConstants.TRIGGER_GROUP_DEFAULT);
			if (!trigger.getCronExpression().equals(crontabExpression)) {
				trigger.setCronExpression(crontabExpression);
				scheduler.rescheduleJob(triggerName, ScheduleConstants.JOB_GROUP_DEFAULT, trigger);
			}
		} catch (SchedulerException e) {
			logger.error("任务(taskid:{})调度出错", jobName);
			throw e;
		} catch (ParseException e) {
			logger.error("作业(taskid:{})触发规则设置出错,对应的CronExpression:{}", jobName, job.getCrontabExpression());
			throw e;
		}
	}

	@Override
	public void deleteJob(Job job) throws SchedulerException {
		Assert.notNull(job);
		String jobName = job.getId().toString();
		String triggerName = jobName + ScheduleConstants.TRIGGER_SUBFIX;
		
		try {
			scheduler.pauseTrigger(triggerName, ScheduleConstants.TRIGGER_GROUP_DEFAULT);
			scheduler.unscheduleJob(triggerName, ScheduleConstants.TRIGGER_GROUP_DEFAULT);
			scheduler.deleteJob(jobName, ScheduleConstants.JOB_GROUP_DEFAULT);
		} catch (SchedulerException e) {
			logger.error("任务(taskid:{})调度出错", jobName);
			throw e;
		}
	}

	@Override
	public void pauseJob(Job job) throws SchedulerException {
		Assert.notNull(job);
		String jobName = job.getId().toString();
		String triggerName = jobName + ScheduleConstants.TRIGGER_SUBFIX;
		
		try {
			scheduler.pauseTrigger(triggerName, ScheduleConstants.TRIGGER_GROUP_DEFAULT);
		} catch (SchedulerException e) {
			logger.error("任务(taskid:{})调度出错", jobName);
			throw e;
		}
	}

	@Override
	public void resumeJob(Job job) throws SchedulerException {
		Assert.notNull(job);
		String jobName = job.getId().toString();
		String triggerName = jobName + ScheduleConstants.TRIGGER_SUBFIX;
		try {
			scheduler.resumeTrigger(triggerName, ScheduleConstants.TRIGGER_GROUP_DEFAULT);
		} catch (SchedulerException e) {
			logger.error("任务(taskid:{})调度出错", jobName);
			throw e;
		}
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
}
