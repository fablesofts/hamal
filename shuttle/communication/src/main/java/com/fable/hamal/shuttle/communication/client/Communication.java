/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.client;

import com.fable.hamal.shuttle.communication.event.Event;

/**
 * 
 * @author xieruidong 2013年11月1日 上午11:04:29
 */
public interface Communication {

	/**
	 * 
	 * @param address example:127.0.0.1:1099/eventService
	 * @param event
	 * @return
	 */
	Object call(final String address, final Event event);
}
