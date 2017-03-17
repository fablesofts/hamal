/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author xieruidong 2014年4月2日 下午4:44:19
 */
public class TaskSerial {

	/**key为任务ID,值为:流水号,流水号格式为：2014-04-02-0000000001。{"taskId":"sequnce"}*/
	private final static Map<Long, String> serial = new ConcurrentHashMap<Long, String>();
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String getSerial(Long taskId) {
		String sequnce = serial.get(taskId);
		if (null == sequnce) {
			String value = sdf.format(Calendar.getInstance().getTime()) + Calendar.getInstance().getTimeInMillis();
			serial.put(taskId, value);
		} else {
			
		}
		return null;
	}
}
