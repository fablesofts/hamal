/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.cache.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fable.hamal.shuttle.common.model.envelope.data.Pump;


/**
 * Pump 配置缓存
 * @author xieruidong 2013年11月13日 上午10:29:15
 */
public class PumpConfigCache {

	private static Map<Long, Pump> cache = new ConcurrentHashMap<Long, Pump>();
	
	public static void put(Long pumpId, Pump pump) {
		cache.put(pumpId, pump);
	}
	
	public static Pump get(Long pumpId) {
		return cache.get(pumpId);
	}
}
