/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.connection;

import com.fable.hamal.shuttle.communication.event.Event;
import com.fable.hamal.shuttle.communication.server.RmiEndpoint;
import com.fable.hamal.shuttle.communication.service.AbstractService;
import com.fable.hamal.shuttle.communication.service.EventService;

/**
 * 
 * @author xieruidong 2013年11月1日 上午10:01:45
 */
public class RmiConnection implements Connection {
	
	private EventService service;
	private ConnectionParameter parameter;

	
	public RmiConnection(EventService service, ConnectionParameter parameter) {
		this.parameter = parameter;
		this.service = service;
	}
	
	public RmiConnection(EventService service) {
		this.service = service;
	}

	public Object call(Event event) {
		return service.accept(event);
	}
}
