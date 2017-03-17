package com.fable.hamal.shuttle.communication.event.ftp;

import com.fable.hamal.shuttle.communication.event.Event;

public class UserIsAdminEvent extends Event{

	private static final long serialVersionUID = -2646020845213750955L;
	public UserIsAdminEvent (String userName) {
		super(UserEventType.ISADMIN);
		setData(userName);
	}
}
