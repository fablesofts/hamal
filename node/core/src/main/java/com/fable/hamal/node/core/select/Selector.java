/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.select;

import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;

/**
 * 
 * @author xieruidong 2013年11月5日 下午4:45:05
 */
public interface Selector {
	public final static String BAR_LINE = "-";
	public final static String SELECTOR = "selector";
	public void start();
	
	public boolean isStart();

	public BatchData select();
	
	public void stop();
	
	public void rollback();
	
	public void setPump(Pump pump);
}
