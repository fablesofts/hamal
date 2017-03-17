/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.task;

import com.fable.hamal.shuttle.allocation.event.AllocateEventData;
import com.fable.hamal.shuttle.common.model.envelope.Pump;

/**
 * 
 * @author xieruidong 2013年11月4日 下午3:59:57
 */
public class TransformTask extends Task {
	
	public TransformTask() {
		
	}
	
	public TransformTask(Pump pump) {
		
	}

	@Override
	public void run() {

	}
	
	class Runner extends Thread {
		
		private AllocateEventData allocateEventData;
		
		public Runner(AllocateEventData allocateEventData) {
			this.allocateEventData = allocateEventData;
		}
		
		@Override
		public void run() {
			allocateEventData.hashCode();
		}
	}
}
