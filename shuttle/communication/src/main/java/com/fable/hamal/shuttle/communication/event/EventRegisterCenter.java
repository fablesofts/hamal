/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事件注册中心
 * @author xieruidong 2013年10月31日 下午4:08:35
 */
public class EventRegisterCenter {
	
	 private static final Logger logger = LoggerFactory.getLogger(EventRegisterCenter.class);
	 private static Map<EventType, EventHandler> center  = new ConcurrentHashMap<EventType, EventHandler>();

	 public static void regist(EventType eventType, EventHandler handler) {
		 if (center.containsKey(eventType)) {
			 if (logger.isWarnEnabled()) {
				 logger.warn("Communication Event's EventHandler" + eventType + "has been register!");
			 }
		 }
	     center.put(eventType, handler);
	 }

	 public static void unregist(EventType eventType) {
	        center.remove(eventType);
	 }

    public static EventHandler getHandler(EventType eventType) {
        return center.get(eventType);
    }
}
