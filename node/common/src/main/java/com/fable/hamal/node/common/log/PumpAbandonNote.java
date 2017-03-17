/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 
 * @author xieruidong 2013年12月17日 下午5:10:31
 */
public class PumpAbandonNote {
	private final static Logger logger = LoggerFactory.getLogger(PumpAbandonNote.class);
	
	public static void log() {
		logger.info("");
	}
	
	public static void log(Long pumpid, String data) {
		String taskName = "d:/log/中国交换/";
		String subTaskName = "江苏/";
		String date = "2013-12-17/";
		String sourceTable = "test";
		String targetTable = "xatest";
		StringBuffer tasknotepath = new StringBuffer(); 
		tasknotepath.append(taskName).append(subTaskName).append(date)
		.append(sourceTable).append("---").append(targetTable);
		MDC.put("tasknotepath", tasknotepath.toString());
		logger.info("==================");
	}
	
	public static void main(String[] args) {
		log(null, null);
	}
}
