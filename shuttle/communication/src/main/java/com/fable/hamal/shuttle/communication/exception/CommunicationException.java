/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * 通讯运行时异常
 * @author xieruidong 2013年10月31日 上午10:11:15
 */
public class CommunicationException extends NestedRuntimeException {

	private static final long serialVersionUID = -559500032114580202L;

	public CommunicationException(String msg) {
		super(msg);
	}
	
	public CommunicationException(String msg, Throwable e) {
		super(msg, e);
	}
}
