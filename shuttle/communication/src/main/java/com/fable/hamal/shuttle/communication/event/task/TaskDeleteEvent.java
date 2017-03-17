/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.event.task;

import com.fable.hamal.shuttle.communication.event.Event;

/**
 * 
 * @author xieruidong 2013年11月22日 上午11:48:33
 */
public class TaskDeleteEvent extends Event {

	private static final long serialVersionUID = -1929017581439473037L;

	public TaskDeleteEvent() {
		super(TaskActionEventType.DELETE);
	}
}
