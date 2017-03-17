package com.fable.hamal.shuttle.communication.event.ftp;

import com.fable.hamal.shuttle.communication.event.Event;

public class FtpStatusEvent extends Event{
	
	private static final long serialVersionUID = 6819571794595716969L;
	
	public FtpStatusEvent () {
		super(FtpdEventType.STATUS);
	}
}
