/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.common.cache.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fable.hamal.shuttle.common.model.envelope.data.Job;

/**
 * 作业配置缓存
 * @author xieruidong 2013年11月13日 上午10:38:36
 */
public class JobConfigCache {

	private static Map<Long, Job> cache = new ConcurrentHashMap<Long, Job>();
	
	public static void put(Long jobId, Job job) {
		cache.put(jobId, job);
	}
	
	public static Job get(Long jobId) {
		return cache.get(jobId);
	}
}
