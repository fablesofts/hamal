/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.event.rate;

import java.util.Map;

import com.fable.hamal.shuttle.communication.event.Event;

/**
 * 
 * @author xieruidong 2014年5月22日 下午8:24:00
 */
public class JobRateEvent extends Event {

	private static final long serialVersionUID = -9112771386931267298L;

	public JobRateEvent(Map<String, Long> rate) {
		super(JobRateEventType.JOB_RATE);
		setData(rate);
	}
}
