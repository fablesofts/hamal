/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.log.event;

import com.fable.hamal.log.model.SysLogDetail;
import com.fable.hamal.shuttle.communication.event.Event;

/**
 * 
 * @author xieruidong 2014年5月22日 下午3:33:57
 */
public class SysLogDetailEvent extends Event {

	private static final long serialVersionUID = 4882896943111030314L;

	public SysLogDetailEvent(SysLogDetail log) {
		super(TrackerEventTypes.SYS_LOG_DETAIL);
		setData(log);
	}
}
