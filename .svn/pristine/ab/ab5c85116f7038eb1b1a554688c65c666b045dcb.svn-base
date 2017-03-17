/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.pipe;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;

import com.fable.hamal.shuttle.common.data.envelope.BatchData;
import com.google.common.collect.MapMaker;

/**
 * 
 * @author xieruidong 2013年11月6日 上午11:49:59
 */
public class DataPipe implements InitializingBean {

	protected Long	timeout = 60 * 1000L;
	private Map<Long, BatchData> cache;
	
	public void put(Long key, BatchData data) {
		cache.put(key, data);
	}
	
	public BatchData get(Long key) {
		return cache.get(key);
	}

	public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
	
	@Override
	public void afterPropertiesSet() throws Exception {
		cache = new MapMaker().expireAfterWrite(timeout, TimeUnit.MILLISECONDS).softValues().makeMap();
	}
}
