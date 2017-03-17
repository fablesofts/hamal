/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.proxy.util;

import java.util.Map;

import com.fable.hamal.shuttle.common.utils.spring.HamalPropertyConfigurer;
import com.fable.hamal.shuttle.communication.client.Communication;
import com.fable.hamal.shuttle.communication.event.rate.JobRateEvent;

/**
 * 流量统计工具类
 * @author xieruidong 2014年5月22日 下午7:48:39
 */
public class Statistics extends Thread {

	private Map<String, Long> rate;
	private final static Long DEFAULT_TIMEOUT=  3000L;
	public final static String MANAGERS_COMMUNICATION_ADDRESS = "managers.communication.address";
	private Communication communication;
	
	public Statistics() {
		
	}
	
	public Statistics(Map<String, Long> rate) {
		this.rate = rate;
	}
	
	@Override
	public void run() {
		if (!this.rate.isEmpty()) {
			//1.清理已经完成的作业的流量统计信息
			
			//2.发送给manager
			JobRateEvent event = new JobRateEvent(this.rate);
			communication.call(HamalPropertyConfigurer.getHamalProperty(MANAGERS_COMMUNICATION_ADDRESS), event);
		}
		try {
			sleep(DEFAULT_TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Communication getCommunication() {
		return communication;
	}

	public void setCommunication(Communication communication) {
		this.communication = communication;
	}
}
