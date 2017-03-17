/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.fable.hamal.node.core.pipe.PipeKey;
import com.fable.hamal.node.core.select.Selector;
import com.fable.hamal.node.core.select.factory.SelectorFactory;
import com.fable.hamal.shuttle.allocation.event.AllocateEventData;
import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.model.config.Pipeline;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;

/**
 * Select阶段任务
 * @author xieruidong 2013年11月4日 下午3:59:57
 */
public class SelectTask extends Task {
	
	private static final Logger logger = LoggerFactory.getLogger(SelectTask.class);
	private Pump pump;
	private SelectorFactory selectorFactory;
	private Selector selector = null;
	/**记录所有子线程执行的结果--停止S-E-T-L流程中S必须等待所有selector执行完成*/
	private List<Future<?>> futures = new ArrayList<Future<?>>();
	
	public SelectTask(Pump pump) {
		this.pump = pump;
	}
	
	public SelectTask(Pump pump, ThreadGroup tg) {
		this.pump = pump;
	}
	
	public SelectTask() {}

	@Override
	public void run() {
		String currentName = Thread.currentThread().getName();
		Thread.currentThread().setName("SelectTask--Thread");
		while(running) {
			try {
				Assert.notNull(pump);
				if (null == selector) {
					selector = selectorFactory.createSelector(pump);
				}
				if (!selector.isStart()) {
					selector.setPump(pump);
					selector.start();
				}
				BatchData data = selector.select();
				//销毁整个流程，单独线程处理？stagemanger的销毁，所有上下文的销毁，目前pumpId随机所以暂时不用销毁
				//启动单独的结束线程方案，临时方案：标志位通知
				//selector查询数据为空，S通知E-T-L流程没有后续数据。
				if (null == data) {
					running = false;
					waitSelector();
					allocateEventService.endup(pump.getId(), allocateEventService.selectAllocateEvent());
					break;
				}
				AllocateEventData allocateEventData = allocateEventService.await(pump.getId(), allocateEventService.selectAllocateEvent());
				PipeKey key = new PipeKey();
				key.setProcessId(allocateEventData.getProcessId());
				allocateEventData.setKey(key);
				if (logger.isInfoEnabled()) {
					logger.info("SelectTask: the process id is{}", allocateEventData.getProcessId());
				}
				//loader通知selector不需要进一步查询，L通知S-E-T流程不要进行下次数据处理
				if (Long.MIN_VALUE == allocateEventData.getProcessId().longValue()) {
					waitSelector();
					running = false;
					break;
				}
				Runner runner = new Runner(allocateEventData, data);
				Future<?> future = executorService.submit(runner);
				futures.add(future);
			} catch (InterruptedException ie) {
				//DO Something:http://www.ibm.com/developerworks/cn/java/j-jtp05236.html
				logger.error("SelectTask InterruptedException");
			} finally {
				Thread.currentThread().setName(currentName);
			}
		}
	}
	
	/**等待所有子线程执行完成*/
	private void waitSelector() throws InterruptedException {
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
	
	/**内部线程类*/
	class Runner extends Thread {
		
		private AllocateEventData allocateEventData;
		private BatchData data;
		
		public Runner(AllocateEventData allocateEventData) {
			this.allocateEventData = allocateEventData;
		}
		
		public Runner(AllocateEventData allocateEventData, BatchData data) {
			this.allocateEventData = allocateEventData;
			this.data = data;
		}
		
		@Override
		public void run() {
			dataPipe.put(allocateEventData.getProcessId(), data);
			fullDataPipe.put((PipeKey)allocateEventData.getKey(), data);
			allocateEventService.awise(allocateEventData, allocateEventService.selectAllocateEvent());
		}
	}

	//-----------------------------------------------------setter&&getter----------------------------------------------
	public Pump getPump() {
		return pump;
	}

	public void setPump(Pump pump) {
		this.pump = pump;
	}

	public Selector getSelector() {
		return selector;
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	public SelectorFactory getSelectorFactory() {
		return selectorFactory;
	}

	public void setSelectorFactory(SelectorFactory selectorFactory) {
		this.selectorFactory = selectorFactory;
	}
}
