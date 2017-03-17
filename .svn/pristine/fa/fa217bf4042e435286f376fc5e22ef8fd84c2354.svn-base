package com.fable.hamal.shuttle.communication.event.ftp;


import com.fable.hamal.shuttle.common.model.config.FtpUser;
import com.fable.hamal.shuttle.communication.event.Event;

public class UserSaveEvent extends Event {

	private static final long serialVersionUID = -1522489574764013858L;
	
	public UserSaveEvent(FtpUser ftpUser) {
		super(UserEventType.SAVE);
		setData(ftpUser);
	}
}
