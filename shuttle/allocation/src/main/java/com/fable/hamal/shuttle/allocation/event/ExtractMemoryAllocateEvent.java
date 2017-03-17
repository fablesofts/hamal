/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.allocation.event;

import com.fable.hamal.shuttle.allocation.assist.ProcessStage;
import com.fable.hamal.shuttle.allocation.enums.StageType;
import com.fable.hamal.shuttle.allocation.manager.StageManager;
import com.fable.hamal.shuttle.allocation.manager.factory.StageManagerFactory;

/**
 * 
 * @author xieruidong 2013年11月4日 下午3:24:39
 */
public class ExtractMemoryAllocateEvent implements ExtractAllocateEvent {

	@Override
	public AllocateEventData await(Long pumpId) throws InterruptedException {
		
		StageManager stageManager = StageManagerFactory.getInstance(pumpId, StageManager.class);
		Long processId = stageManager.prepareProcess(StageType.EXTRACT);
		ProcessStage ps = stageManager.getProcessData(processId);
		AllocateEventData allocateEventData = null == ps ?null : ps.getEventData();
		if (null == allocateEventData) {
			allocateEventData = new AllocateEventData();
			allocateEventData.setPumpId(pumpId);
			allocateEventData.setProcessId(processId);
		}
		return allocateEventData;
	}

	@Override
	public void awise(AllocateEventData allocateEventData) {
		Long pumpId = allocateEventData.getPumpId();
		StageManager stageManager = StageManagerFactory.getInstance(pumpId, StageManager.class);
		stageManager.awise(StageType.EXTRACT, allocateEventData);
	}
	
	@Override
	public void endup(Long pumpId) {
		StageManager stageManager = StageManagerFactory.getInstance(pumpId, StageManager.class);
		stageManager.endup(StageType.EXTRACT);
	}

}
