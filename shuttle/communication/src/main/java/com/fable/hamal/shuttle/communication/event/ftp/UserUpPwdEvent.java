package com.fable.hamal.shuttle.communication.event.ftp;

import com.fable.hamal.shuttle.common.model.config.FtpUser;
import com.fable.hamal.shuttle.communication.event.Event;

public class UserUpPwdEvent extends Event{

	
	private static final long serialVersionUID = 1053356198388500999L;

	public UserUpPwdEvent (FtpUser ftpUser) {
		super(UserEventType.UPPWD);
		setData(ftpUser);
	}
}
