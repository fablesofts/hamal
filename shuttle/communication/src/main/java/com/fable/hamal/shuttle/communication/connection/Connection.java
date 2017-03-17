/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.connection;

import com.fable.hamal.shuttle.communication.event.Event;

/**
 * 
 * @author xieruidong 2013年10月31日 上午10:17:02
 */
public interface Connection {

	public Object call(Event event);
	
}
