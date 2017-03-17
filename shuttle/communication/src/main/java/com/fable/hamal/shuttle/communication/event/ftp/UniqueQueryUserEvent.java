package com.fable.hamal.shuttle.communication.event.ftp;

import com.fable.hamal.shuttle.communication.event.Event;

public class UniqueQueryUserEvent extends Event{

	private static final long serialVersionUID = 1264507540454995583L;
	public UniqueQueryUserEvent(String userName) {
		super(UserEventType.UNIQUEQUERY);
		setData(userName);
	}
}
