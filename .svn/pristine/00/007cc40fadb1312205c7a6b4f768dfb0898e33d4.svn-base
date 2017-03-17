/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.pipe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fable.hamal.shuttle.common.data.envelope.BatchData;

/**
 * BatchData数据存储容器
 * @author xieruidong 2013年12月16日 下午2:10:24
 */
public class FullDataPipe {

	private Map<PipeKey, BatchData> cache = new ConcurrentHashMap<PipeKey, BatchData>();
	
	public void put(PipeKey key, BatchData data) {
		cache.put(key, data);
	}
	
	public BatchData get(PipeKey key) {
		return cache.get(key);
	}
}
