/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.fable.hamal.node.core.extract.chain.manager.ChainManager;
import com.fable.hamal.node.core.pipe.PipeKey;
import com.fable.hamal.shuttle.allocation.event.AllocateEventData;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;

/**
 * 
 * @author xieruidong 2013年11月4日 下午4:00:09
 */
public class ExtractTask extends Task {
	
	private static final Logger logger = LoggerFactory.getLogger(ExtractTask.class);
	private List<Future<?>> futures = new ArrayList<Future<?>>();
//	private static final Long TIME_SLEEP = 100L;//ms
	private ChainManager chainManager;
	private Pump pump;
	
	public ExtractTask() {
		
	}
	
	public ExtractTask(Pump pump) {
		this.pump = pump;
	}
	
	public ExtractTask(Pump pump, ThreadGroup tg) {
		this.pump = pump;
	}

	@Override
	public void run() {
		
		Long pumpId = pump.getId();
		String currentName = Thread.currentThread().getName();
		Thread.currentThread().setName("ExtractTask---Thread");
		if (logger.isDebugEnabled()) {
			logger.debug("==========================Pump's({})ExtractTask", pumpId);
		}
		
		while (running) {
			try {
				if (logger.isInfoEnabled()) {
					logger.info("Pump's(pumpid:{}) before EventData", pump.getId());
				}
				AllocateEventData allocateEventData = allocateEventService.await(pumpId, allocateEventService.extractAllocateEvent());
				Long processId = allocateEventData.getProcessId();
				if (logger.isDebugEnabled()) {
					logger.debug("==========================Pump's({})ExtractTask and processId({})", pumpId, processId);
				}
				if (Long.MAX_VALUE == processId.longValue()) {
					running = false;
					waitExtracter();
					allocateEventService.endup(pumpId, allocateEventService.extractAllocateEvent());
					break;
				}
				Runner runner = new Runner(allocateEventData);
				futures.add(executorService.submit(runner));
			} catch (InterruptedException e) {
				logger.error("Thread(name: {}) was Interrupted", Thread.currentThread().getName());
				e.printStackTrace();
			} finally {
				Thread.currentThread().setName(currentName);
			}
		}
	}
	
	public ChainManager getChainManager() {
		return chainManager;
	}

	public void setChainManager(ChainManager chainManager) {
		this.chainManager = chainManager;
	}
	
	public Pump getPump() {
		return pump;
	}

	public void setPump(Pump pump) {
		this.pump = pump;
	}

	class Runner extends Thread {
		
		private AllocateEventData allocateEventData;
		
		public Runner(AllocateEventData allocateEventData) {
			this.allocateEventData = allocateEventData;
		}
		
		@Override
		public void run() {
			Assert.notNull(chainManager);
			PipeKey key = (PipeKey)allocateEventData.getKey();
			chainManager.process(pump.getId(), key, fullDataPipe.get(key));
			allocateEventService.awise(allocateEventData, allocateEventService.extractAllocateEvent());
		}
	}
	
	/**等待所有子线程执行完成*/
	private void waitExtracter() throws InterruptedException {
		int length = futures.size();
		while (true) {
			int count = 0;
			for (Future<?> future : futures) {
				if (future.isDone()) {
					count++;
				}
			}
			if (length == count) {
				break;
			}
			Thread.sleep(100);
		}
	}
	
	class Caller implements Callable<BatchData> {

		public BatchData call() throws Exception {
			return null;
		}
	}
	
}
