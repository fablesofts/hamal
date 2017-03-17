/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.fable.hamal.log.JobTracker;
import com.fable.hamal.node.common.cache.config.JobConfigCache;
import com.fable.hamal.node.common.cache.config.PumpConfigCache;
import com.fable.hamal.node.core.task.ExtractTask;
import com.fable.hamal.node.core.task.LoadTask;
import com.fable.hamal.node.core.task.SelectTask;
import com.fable.hamal.node.core.task.Task;
import com.fable.hamal.shuttle.common.model.envelope.data.Job;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;
import com.fable.hamal.shuttle.common.utils.metadata.sqldb.DbDriverClass;
import com.fable.hamal.shuttle.communication.event.Event;
import com.fable.hamal.shuttle.communication.event.EventHandler;
import com.fable.hamal.shuttle.communication.event.EventRegisterCenter;
import com.fable.hamal.shuttle.communication.event.task.TaskActionEventType;

/**
 * 
 * @author xieruidong 2013年11月1日 下午3:28:52
 */
public class TaskController {
	
	private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
	
	static {
		try {
			Class.forName(DbDriverClass.ORACLE_DEFAULT, true, TaskController.class.getClassLoader());
			Class.forName(DbDriverClass.MSSQL_DEFAULT, true, TaskController.class.getClassLoader());
			Class.forName(DbDriverClass.MYSQL_DEFAULT, true, TaskController.class.getClassLoader());
			Class.forName(DbDriverClass.DAMENG7_DEFAULT, true, TaskController.class.getClassLoader());
			Class.forName(DbDriverClass.DAMENG6_DEFAULT, true, TaskController.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			logger.warn("Can not find the class {}" + e.getMessage());
		}
	}
	
	private JobTracker jobTracker;
	
	public TaskController() {
		EventRegisterCenter.regist(TaskActionEventType.RUN, new EventHandler() {

			public Object handle(Event event) {
				Assert.notNull(event.getData());
				if (!(event.getData() instanceof Job)) {
					logger.error("Event's data type must be {}", Job.class.getName());
					throw new java.lang.IllegalArgumentException("Event's data type must be Job");
				}
				Job job = (Job)event.getData();
				if (logger.isDebugEnabled()) {
					logger.debug("Accept the command of run the Task(id:{},name:{})", job.getId(), job.getName());
				}
				startJob(job);
				return true;
			}
		});
	}
	
	public void startJob(Job job) {
		Assert.notNull(job);
		if (logger.isInfoEnabled()) {
			logger.info("Task(id:{},name:{}) is going to start!", job.getId(), job.getName());
		}
		Long jobId = job.getId();
		String jobName = job.getName();
		String jobSerial = job.getSerial();
		Job cacheJob = JobConfigCache.get(jobId);
		if (null == cacheJob || cacheJob.equals(job)) {
			JobConfigCache.put(jobId, job);
		}
		
		//记录作业运行信息
		jobTracker.start(jobId, jobName, jobSerial);
		String groupName = new StringBuffer(String.valueOf(jobId)).append("--").append(jobName).toString();
		ThreadGroup tg = new ThreadGroup(groupName);
		
		for (Pump pump : job.getPumps()) {
			Long pumpId = pump.getId();
			Pump cachePump = PumpConfigCache.get(pumpId);
			if (null == cachePump || !cachePump.equals(pump)) {
				PumpConfigCache.put(pumpId, pump);
			}
			pump.setSerial(job.getSerial());
			startPump(pump, tg);
		}
		
		while(tg.activeCount() > 0) {
			try {
				System.out.println("**************=========&&&&&&&&&&&" + tg.activeCount());
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		jobTracker.end(jobId, jobName, jobSerial);
	}
	
	public void start() {
		if (logger.isInfoEnabled()) {
			logger.info("Hamal Node has started! She is waiting the job's command!");
		}
		
		//check
	}

	public void startPump(Pump pump, ThreadGroup tg) {
		if (logger.isDebugEnabled()) {
			logger.debug("the pump(id:{},name:{}) is going to start");
		}
		Task selectTask = new SelectTask(pump, tg);
		Task loadTask = new LoadTask(pump, tg);
		Task extractTask = new ExtractTask(pump, tg);
//		Task transformTask = new TransformTask();
		HamalContextHelper.autowire(selectTask);
		HamalContextHelper.autowire(loadTask);
		HamalContextHelper.autowire(extractTask);

		selectTask.start();
		extractTask.start();
//		transformTask.start();
		loadTask.start();
	}

	//---------------------------------------setter&&getter----------------------------------------
	public JobTracker getJobTracker() {
		return jobTracker;
	}

	public void setJobTracker(JobTracker jobTracker) {
		this.jobTracker = jobTracker;
	}
}
