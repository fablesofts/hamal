/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.event.task;

import com.fable.hamal.shuttle.communication.event.Event;

/**
 * 
 * @author xieruidong 2013年11月22日 上午11:22:47
 */
public class TaskAddEvent extends Event {

	private static final long serialVersionUID = 6140198454597321439L;

	public TaskAddEvent() {
		super(TaskActionEventType.ADD);
	}
}
