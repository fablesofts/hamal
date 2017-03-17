/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.load;

import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.model.envelope.data.Pair;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;

/**
 * 
 * @author xieruidong 2013年11月5日 下午4:47:42
 */
public interface Loader {
	public final static String BAR_LINE = "-";
	public final static String LOADER = "loader";
	public void start();
	
	public void load(final BatchData data);
	
	public void setPump(Pump pump);
	
	public void setPair(Pair pair);
	
//	public boolean isStart();
}
