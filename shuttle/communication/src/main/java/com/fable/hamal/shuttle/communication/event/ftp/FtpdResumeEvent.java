/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.event.ftp;

import com.fable.hamal.shuttle.communication.event.Event;

/**
 * 
 * @author xieruidong 2014年2月19日 下午4:46:27
 */
public class FtpdResumeEvent extends Event{

	private static final long serialVersionUID = 1L;
	
	public FtpdResumeEvent () {
		super(FtpdEventType.RESUME);
	}
}
