/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.node.core.pipe.DataPipe;
import com.fable.hamal.node.core.pipe.FullDataPipe;
import com.fable.hamal.shutte.allocation.event.service.AllocateEventService;

/**
 * 
 * @author xieruidong 2013年11月1日 下午3:25:16
 */
public abstract class Task extends Thread {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected volatile boolean running = true;
	protected AllocateEventService allocateEventService;
	protected ExecutorService executorService = Executors.newFixedThreadPool(4);
	protected DataPipe dataPipe;
	protected FullDataPipe fullDataPipe;
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public AllocateEventService getAllocateEventService() {
		return allocateEventService;
	}

	public void setAllocateEventService(AllocateEventService allocateEventService) {
		this.allocateEventService = allocateEventService;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
	public DataPipe getDataPipe() {
		return dataPipe;
	}
	
	public void setDataPipe(DataPipe dataPipe) {
		this.dataPipe = dataPipe;
	}

	public Logger getLogger() {
		return logger;
	}

	public FullDataPipe getFullDataPipe() {
		return fullDataPipe;
	}

	public void setFullDataPipe(FullDataPipe fullDataPipe) {
		this.fullDataPipe = fullDataPipe;
	}
}
