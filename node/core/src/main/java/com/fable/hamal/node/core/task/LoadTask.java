/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.node.core.load.Loader;
import com.fable.hamal.node.core.load.factory.LoaderFactory;
import com.fable.hamal.shuttle.allocation.event.AllocateEventData;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;

/**
 * Load任务
 * @author xieruidong 2013年11月4日 下午4:00:22
 */
public class LoadTask extends Task {
	
	private static final Logger logger = LoggerFactory.getLogger(LoadTask.class);
	private LoaderFactory loaderFactory;
	private List<Loader> loaders;
	private Pump pump;
	
	public LoadTask() {}
	
	public LoadTask(Pump pump) {
		this.pump = pump;
	}
	
	public LoadTask(Pump pump, ThreadGroup tg) {
		this.pump = pump;
	}
	
	@Override
	public void run() {
		Thread.currentThread().setName("LoadTask--Thread");
		if (logger.isInfoEnabled()) {
			logger.info("Pump's(pumpid:{}) LoadTask is working!", pump.getId());
		}
		while (running) {
			try {
				if (null == loaders || 0 == loaders.size()) {
					loaders = loaderFactory.createLoaders(pump);
				}
				if (logger.isInfoEnabled()) {
					logger.info("Pump's(pumpid:{}) before EventData", pump.getId());
				}
				AllocateEventData allocateEventData = allocateEventService.await(pump.getId(),allocateEventService.loadAllocateEvent());
				if (logger.isDebugEnabled()) {
					logger.debug("======================LoadTask: the process id is: {}", allocateEventData.getProcessId());
				}
				//接收来自S-E-T-L中T的终止信号
				if (Long.MAX_VALUE == allocateEventData.getProcessId().longValue()) {
					allocateEventService.destoryStageManager(pump.getId());
					running = false;
					break;
				}
				for (Loader loader : loaders) {
					Runner runner = new Runner(allocateEventData, loader);
					executorService.submit(runner);
				}
				
				
			} catch (InterruptedException ie) {
				logger.error("LoadTask InterruptedException, The message is:({})", ie.getMessage());
			}
		}
	}
	
	public void setLoaders(List<Loader> loaders) {
		this.loaders = loaders;
	}

	public LoaderFactory getLoaderFactory() {
		return loaderFactory;
	}

	public void setLoaderFactory(LoaderFactory loaderFactory) {
		this.loaderFactory = loaderFactory;
	}

	class Runner extends Thread {
		
		private AllocateEventData allocateEventData;
		private Loader loader;
		
		public Runner(AllocateEventData allocateEventData) {
			this.allocateEventData = allocateEventData;
		}
		
		public Runner(AllocateEventData allocateEventData, Loader loader) {
			this.allocateEventData = allocateEventData;
			this.loader = loader;
		}
		
		@Override
		public void run() {
			loader.start();
			loader.load(dataPipe.get(allocateEventData.getProcessId()));
			allocateEventService.awise(allocateEventData, allocateEventService.loadAllocateEvent());
		}
	}

	public List<Loader> getLoaders() {
		return loaders;
	}

	public void setLoader(List<Loader> loaders) {
		this.loaders = loaders;
	}

	public Pump getPump() {
		return pump;
	}

	public void setPump(Pump pump) {
		this.pump = pump;
	}
}
