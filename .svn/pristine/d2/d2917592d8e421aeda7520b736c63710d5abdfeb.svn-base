/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.schedule.service;

import java.text.ParseException;

import org.quartz.SchedulerException;

import com.fable.hamal.shuttle.common.model.envelope.data.Job;

/**
 * 任务服务接口
 * @Modify 2013-12-03 10:58，修改类名：JobService->JobScheduleService by xieruidong
 * @Modify 修改所有方法返回值为void by xieruidong
 * @author 汪朝
 * @author xieruidong
 */
public interface JobScheduleService {

	/**
	 * 添加一个任务
	 * 
	 * @param <code>{@link Job}</code>
	 * @return
	 * @throws SchedulerException 
	 * @throws ParseException 
	 */
	void addJob(Job job) throws SchedulerException, ParseException;

	/**
	 * 修改一个任务
	 * 
	 * @param <code>{@link Job}</code>
	 * @return
	 * @throws SchedulerException 
	 * @throws ParseException 
	 */
	void updateJob(Job job) throws SchedulerException, ParseException;

	/**
	 * 删除一个任务
	 * 
	 * @param <code>{@link Job}</code>
	 * @return
	 * @throws SchedulerException 
	 */
	void deleteJob(Job job) throws SchedulerException;

	/**
	 * 停止一个任务
	 * 
	 * @param <code>{@link Job}</code>
	 * @return
	 * @throws SchedulerException 
	 */
	void pauseJob(Job job) throws SchedulerException;

	/**
	 * 重启一个任务
	 * 
	 * @param <code>{@link Job}</code>
	 * @return
	 * @throws SchedulerException 
	 */
	void resumeJob(Job job) throws SchedulerException;

}
