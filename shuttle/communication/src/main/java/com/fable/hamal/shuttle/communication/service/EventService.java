/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.service;

import com.fable.hamal.shuttle.communication.event.Event;

/**
 * 
 * @author xieruidong 2013年10月31日 上午10:02:12
 */
public interface EventService extends Service{
	
	Object accept(Event event);
}
