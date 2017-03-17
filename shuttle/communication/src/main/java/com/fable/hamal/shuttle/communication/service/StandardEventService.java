/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.service;

/**
 * 
 * @author xieruidong 2013年10月31日 下午3:30:27
 */
public class StandardEventService extends AbstractEventService {

	public StandardEventService() {
		this.alwaysCreateRegister = true;
		this.serviceInterface = EventService.class;
		this.serviceName = "eventService";
	}
}
