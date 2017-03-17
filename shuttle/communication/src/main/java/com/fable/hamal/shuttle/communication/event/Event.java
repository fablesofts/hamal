/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.event;

import java.io.Serializable;

/**
 * 
 * @author xieruidong 2013年10月31日 下午4:08:54
 */
public abstract class Event implements Serializable {

	private static final long serialVersionUID = 4272286858948190367L;
	protected EventType type = UnkownEvenType.unkonwn;
	protected Object data;
	
	public Event() {
		
	}
	
	public Event(EventType type) {
		this.type = type;
	}

	
	//--------------------------------getter&&setter------------------------------
	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
