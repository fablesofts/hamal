/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.task;

import com.fable.hamal.shuttle.allocation.event.AllocateEventData;

/**
 * 
 * @author xieruidong 2013年11月1日 下午3:30:16
 */
public class PrepareTask extends Task {

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
