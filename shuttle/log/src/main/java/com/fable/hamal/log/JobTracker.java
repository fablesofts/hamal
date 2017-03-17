/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.log;

import java.sql.Timestamp;
import java.util.Calendar;

import com.fable.hamal.log.event.SysLogDetailEvent;
import com.fable.hamal.log.event.TrackerTaskEndEvent;
import com.fable.hamal.log.event.TrackerTaskStartEvent;
import com.fable.hamal.log.model.SysLog;
import com.fable.hamal.log.model.SysLogDetail;
import com.fable.hamal.shuttle.common.data.envelope.FileDataBatch;
import com.fable.hamal.shuttle.common.data.envelope.RowDataBatch;
import com.fable.hamal.shuttle.communication.client.Communication;
import com.fable.hamal.shuttle.communication.event.Event;

/**
 * node用来跟踪作业运行信息
 * @author xieruidong 2014年5月13日 下午4:51:55
 */
public class JobTracker {
	public static void main(String[] args) {
		System.out.println(System.getProperty("java.rmi.server.hostname"));
	}
	private Communication communication;
	private RmiUrlService rmiUrlService;
	
	/**任务开始*/
	public void start(Long taskId, String taskName, String taskSerial) {
		SysLog log = new SysLog();
		log.setTaskId(taskId);
		log.setTaskName(taskName);
		log.setTaskSerial(taskSerial);
		log.setStartTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		Event event = new TrackerTaskStartEvent(log);
		communication.call(rmiUrlService.getManagerRmiUrl(), event);
	}
	
	/**任务结束*/
	public void end(Long taskId, String taskName, String taskSerial) {
		SysLog log = new SysLog();
		log.setTaskId(taskId);
		log.setTaskName(taskName);
		log.setTaskSerial(taskSerial);
		log.setEndTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		Event event = new TrackerTaskEndEvent(log);
		communication.call(rmiUrlService.getManagerRmiUrl(), event);
	}
	
	public void detail(Long taskId, String serial, String type, String detail, String result) {
		SysLogDetail log = new SysLogDetail();
		log.setTaskId(taskId);
		log.setTaskSerial(serial);
		log.setOperationType(type);
		log.setOperationDetail(detail);
		log.setOperationResults(result);
		Event event = new SysLogDetailEvent(log);
		communication.call(rmiUrlService.getManagerRmiUrl(), event);
	}
	
	public void row(Long taskId, String serial, RowDataBatch rdb) {
		
	}
	
	public void row(Long taskId, String serial, FileDataBatch fdb) {
		
	}
	
	
	public void insert() {
		
	}
	
	public void update() {
		
	}
	
	public void insertOrUpdate() {
		
	}

	public Communication getCommunication() {
		return communication;
	}

	public void setCommunication(Communication communication) {
		this.communication = communication;
	}

	public RmiUrlService getRmiUrlService() {
		return rmiUrlService;
	}

	public void setRmiUrlService(RmiUrlService rmiUrlService) {
		this.rmiUrlService = rmiUrlService;
	}
}
