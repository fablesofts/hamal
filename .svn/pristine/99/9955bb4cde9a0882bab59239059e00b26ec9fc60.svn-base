/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.a.example;

import com.fable.hamal.shuttle.communication.event.Event;
import com.fable.hamal.shuttle.communication.event.EventHandler;
import com.fable.hamal.shuttle.communication.event.EventRegisterCenter;
import com.fable.hamal.shuttle.communication.event.EventType;

/**
 * 
 * @author xieruidong 2013年11月8日 上午10:20:25
 */
public class HelloEventHandler implements EventHandler {

	public void init() {
		EventRegisterCenter.regist(HelloEventType.CHINESE, this);
		EventRegisterCenter.regist(HelloEventType.ENGLISH, this);
	}

	public Object handle(Event event) {
		EventType type = event.getType();
		if (HelloEventType.CHINESE.equals(type)) {
			return "您好！";
		}
		if (HelloEventType.ENGLISH.equals(type)) {
			return "Hello！";
		}
		return "";
	}
}
