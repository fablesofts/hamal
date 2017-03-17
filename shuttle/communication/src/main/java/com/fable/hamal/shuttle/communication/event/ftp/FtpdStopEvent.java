/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.event.ftp;

import com.fable.hamal.shuttle.communication.event.Event;

/**
 * 
 * @author xieruidong 2014年2月19日 下午4:44:56
 */
public class FtpdStopEvent extends Event{
	private static final long serialVersionUID = -4961760570065808846L;
	public FtpdStopEvent () {
		super(FtpdEventType.STOP);
	}
}
