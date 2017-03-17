/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.allocation.assist;

import com.fable.hamal.shuttle.allocation.enums.StageType;
import com.fable.hamal.shuttle.allocation.event.AllocateEventData;

/**
 * 
 * @author xieruidong 2013年12月23日 下午2:01:36
 */
public class ProcessStage {

	private StageType type;
	private AllocateEventData eventData;
	
	public ProcessStage(StageType type, AllocateEventData eventData) {
		this.type = type;
		this.eventData = eventData;
	}
	
	public ProcessStage() {
		
	}
	
	public StageType getType() {
		return type;
	}
	public void setType(StageType type) {
		this.type = type;
	}
	public AllocateEventData getEventData() {
		return eventData;
	}
	public void setEventData(AllocateEventData eventData) {
		this.eventData = eventData;
	}
	
}
