package com.fable.hamal.shuttle.communication.event.ftp;

import com.fable.hamal.shuttle.common.model.config.FtpUser;
import com.fable.hamal.shuttle.communication.event.Event;

public class UserEditEvent extends Event{

	private static final long serialVersionUID = -1669501169714293577L;
	
	public UserEditEvent (FtpUser ftpUser) {
		super(UserEventType.EDIT);
		setData(ftpUser);
	}
}
