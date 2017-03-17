/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.allocation.event;

import com.fable.hamal.shuttle.allocation.enums.StageType;
import com.fable.hamal.shuttle.allocation.manager.StageManager;
import com.fable.hamal.shuttle.allocation.manager.factory.StageManagerFactory;

/**
 * 
 * @author xieruidong 2013年11月4日 下午3:27:21
 */
public class LoadMemoryAllocateEvent implements LoadAllocateEvent{

	public AllocateEventData await(Long pumpId) throws InterruptedException {
		
		StageManager stageManager = StageManagerFactory.getInstance(pumpId, StageManager.class);
		AllocateEventData allocateEventData = new AllocateEventData();
		allocateEventData.setPumpId(pumpId);
		long processId = stageManager.prepareProcess(StageType.LOAD);
		allocateEventData.setProcessId(processId);
		return allocateEventData;
	}

	public void awise(AllocateEventData allocateEventData) {
		StageManager stageManager = StageManagerFactory.getInstance(allocateEventData.getPumpId(), StageManager.class);
		stageManager.awise(StageType.LOAD, allocateEventData);
	}

	/* (non-Javadoc)
	 * @see com.fable.hamal.shuttle.allocation.event.AllocateEvent#endup(com.fable.hamal.shuttle.allocation.event.AllocateEventData)
	 */
	@Override
	public void endup(Long pumpId) {
		// TODO Auto-generated method stub
		
	}
}
