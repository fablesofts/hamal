/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Context一些默认实现
 * @author xieruidong 2013年12月13日 上午11:01:12
 */
public abstract class AbstractContext implements Context {

	@SuppressWarnings("rawtypes")
	private Map initParameters = new ConcurrentHashMap();
	@SuppressWarnings("rawtypes")
	private Map attributes = new ConcurrentHashMap();
	
	@SuppressWarnings("unchecked")
	@Override
	public void setInitParameter(Object key, Object value) {
		initParameters.put(key, value);
	}
	
	@Override
	public Object getInitParameter(Object key) {
		return initParameters.get(key);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setAttribute(Object key, Object value) {
		attributes.put(key, value);
	}
	
	@Override
	public Object getAttribute(Object key) {
		return attributes.get(key);
	}
}
