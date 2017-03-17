/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shutte.allocation.event.service;

import com.fable.hamal.shuttle.allocation.event.AllocateEvent;
import com.fable.hamal.shuttle.allocation.event.AllocateEventData;
import com.fable.hamal.shuttle.allocation.event.ExtractAllocateEvent;
import com.fable.hamal.shuttle.allocation.event.LoadAllocateEvent;
import com.fable.hamal.shuttle.allocation.event.PrepareAllocateEvent;
import com.fable.hamal.shuttle.allocation.event.SelectAllocateEvent;
import com.fable.hamal.shuttle.allocation.event.TransformAllocateEvent;

/**
 * 
 * @author xieruidong 2013年11月4日 下午2:02:16
 */
public interface AllocateEventService {

	public PrepareAllocateEvent prepareAllocateEvent();
	
	public SelectAllocateEvent selectAllocateEvent();
	
	public ExtractAllocateEvent extractAllocateEvent();
	
	public TransformAllocateEvent transformAllocateEvent();
	
	public LoadAllocateEvent loadAllocateEvent();
	
	public AllocateEventData await(Long pumpId, AllocateEvent event) throws InterruptedException;
	
	public void awise(AllocateEventData allocateEventData, AllocateEvent event);
	
	public void endup(Long pumpId, AllocateEvent event);
	
	public void destoryStageManager(Long pumpId);
}
