/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.event.ftp;

import com.fable.hamal.shuttle.communication.event.EventType;

/**
 * 
 * @author xieruidong 2014年2月19日 下午4:19:37
 */
public enum UserEventType implements EventType{
	SAVE,EDIT,DELETE,LIST,UNIQUEQUERY,ISADMIN,DOESEXIST,UPPWD
}
