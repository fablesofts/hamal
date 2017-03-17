/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.allocation.manager;


import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;

import com.fable.hamal.shuttle.allocation.assist.ProcessQueue;
import com.fable.hamal.shuttle.allocation.assist.ProcessStage;
import com.fable.hamal.shuttle.allocation.enums.StageType;
import com.fable.hamal.shuttle.allocation.event.AllocateEventData;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
/**
 * 
 * @author xieruidong 2013年11月4日 上午9:50:04
 */
public class StageManager {

	private Long pumpId;
	private AtomicLong atomicProcessId = new AtomicLong(0);
	private Map<StageType, ProcessQueue<Long>> stages;
	private Map<Long, ProcessStage> stageData;
	/**S->E->T->L结束信号*/
	private final static Long TERMINAL_ONCE_PUMP = Long.MAX_VALUE; 
	
	public StageManager(Long pumpId) {
		this.pumpId = pumpId;
		stages = new MapMaker().makeComputingMap(new Function<StageType, ProcessQueue<Long>>() {
            public ProcessQueue<Long> apply(StageType input) {
                int size = 100;
                return new ProcessQueue<Long>(size);
            }
        });
		stageData = new MapMaker().makeMap();
	}
	
	public StageManager() {
		
	}
	
	public Long prepareProcess(StageType type) throws InterruptedException {
		
		if (type.equals(StageType.SELECT) && !stages.containsKey(type)) {
			initSelect();
		}

		Long processId = stages.get(type).take();
		return processId;
	}
	
	/**初始化selector查询次数：1*/
	private synchronized void initSelect() {
		ProcessQueue<Long> queue = stages.get(StageType.SELECT);
		queue.offer(atomicProcessId.incrementAndGet());
	}
	
	/**通知S-E-T-L流程继续处理*/
	public boolean awise(StageType stage, AllocateEventData allocateEventData) {
		boolean result = false;
		Long process = allocateEventData.getProcessId();
        switch (stage) {
            case SELECT:
            	ProcessQueue<Long> queue = stages.get(StageType.EXTRACT);
            	stageData.put(process, new ProcessStage(stage, allocateEventData));
            	queue.offer(process);
                result = true;
                break;
            case EXTRACT:
        		stageData.put(process, new ProcessStage(stage, allocateEventData));
        		stages.get(StageType.LOAD).offer(process);
                result = true;
            	break;
            case LOAD:
            	stageData.remove(process);
            	stages.get(StageType.SELECT).offer(atomicProcessId.incrementAndGet());
                result = true;
                break;
            default:
                break;
        }
        return result;
	}
	
	/**结束一次任务执行*/
	public boolean endup(StageType stage) {
		boolean result = false;
        switch (stage) {
            case SELECT:
            	stages.get(StageType.EXTRACT).offer(TERMINAL_ONCE_PUMP);
                result = true;
                break;
            case EXTRACT:
            	stages.get(StageType.LOAD).offer(TERMINAL_ONCE_PUMP);
            	result = true;
            	break;
            case LOAD:
                stages.get(StageType.SELECT).offer(TERMINAL_ONCE_PUMP);
                result = true;
                break;
            default:
                break;
        }
        return result;
	}
	
	public ProcessStage getProcessData(Long processId) {
		return stageData.get(processId);
	}

	public Long getPumpId() {
		return pumpId;
	}

	public void setPumpId(Long pumpId) {
		this.pumpId = pumpId;
	}
}
