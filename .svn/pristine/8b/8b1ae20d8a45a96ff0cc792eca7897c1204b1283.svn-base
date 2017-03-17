/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.allocation.event;

import com.fable.hamal.shuttle.allocation.enums.StageType;
import com.fable.hamal.shuttle.allocation.manager.StageManager;
import com.fable.hamal.shuttle.allocation.manager.factory.StageManagerFactory;

/**
 * 
 * @author xieruidong 2013年11月4日 下午3:12:17
 */
public class SelectMemoryAllocateEvent implements SelectAllocateEvent {

	public AllocateEventData await(Long pumpId) throws InterruptedException {
		StageManager stageManager = StageManagerFactory.getInstance(pumpId, StageManager.class);
		AllocateEventData allocateEventData = new AllocateEventData();
		allocateEventData.setPumpId(pumpId);
		long processId = stageManager.prepareProcess(StageType.SELECT);
		allocateEventData.setProcessId(processId);
		return allocateEventData;
	}

	public void awise(AllocateEventData allocateEventData) {
		Long pumpId = allocateEventData.getPumpId();
		StageManager stageManager = StageManagerFactory.getInstance(pumpId, StageManager.class);
		stageManager.awise(StageType.SELECT, allocateEventData);
	}
	
	public void endup(Long pumpId) {
		StageManager stageManager = StageManagerFactory.getInstance(pumpId, StageManager.class);
		stageManager.endup(StageType.SELECT);
	}
}
