/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.allocation.event;

/**
 * 
 * @author xieruidong 2013年11月4日 下午3:28:34
 */
public class TransformMemoryAllocateEvent extends AbstractAllocateEvent implements TransformAllocateEvent {

	/* (non-Javadoc)
	 * @see com.fable.hamal.shuttle.allocation.event.AllocateEvent#await(long)
	 */
	public AllocateEventData await(Long pumpId) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.fable.hamal.shuttle.allocation.event.AllocateEvent#awise(com.fable.hamal.shuttle.allocation.event.AllocateEventData)
	 */
	public void awise(AllocateEventData allocateEventData) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fable.hamal.shuttle.allocation.event.AllocateEvent#endup(com.fable.hamal.shuttle.allocation.event.AllocateEventData)
	 */
	@Override
	public void endup(Long pumpId) {
		// TODO Auto-generated method stub
		
	}


}
