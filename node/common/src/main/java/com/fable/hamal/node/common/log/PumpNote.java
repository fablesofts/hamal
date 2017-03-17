/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.common.log;

import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.fable.hamal.shuttle.common.model.envelope.data.Pump;

/**
 * 作业运行信息统计
 * @author xieruidong 2013年12月17日 下午4:15:36
 */
public class PumpNote {
	
	/**过滤掉的数据*/
	public static void abandon(Pump pump, BatchData batchData) {
		PumpAbandonNote.log();
	}
	
	/**全部数据--行级日志*/
	public static void raw() {
		
	}
}
