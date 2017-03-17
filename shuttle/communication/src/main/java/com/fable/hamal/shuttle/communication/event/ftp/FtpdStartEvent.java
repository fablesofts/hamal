/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.event.ftp;


import com.fable.hamal.shuttle.communication.event.Event;

/**
 * 
 * @author xieruidong 2014年2月19日 下午4:18:00
 */
public class FtpdStartEvent extends Event {

	private static final long serialVersionUID = -5424278920083091269L;
	
	public FtpdStartEvent() {
		super(FtpdEventType.START);
	}
}
