/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.service;

import com.fable.hamal.shuttle.communication.event.Event;
import com.fable.hamal.shuttle.communication.event.EventHandler;
import com.fable.hamal.shuttle.communication.event.EventRegisterCenter;

/**
 * 
 * @author xieruidong 2013年10月31日 上午11:47:52
 */
public abstract class AbstractEventService extends AbstractService implements EventService {
	
	public Object accept(Event event) {

		EventHandler handler = EventRegisterCenter.getHandler(event.getType());
		return handler.handle(event);
	}
}
