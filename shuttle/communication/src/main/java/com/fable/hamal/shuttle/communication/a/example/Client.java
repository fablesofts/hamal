/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.a.example;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fable.hamal.shuttle.communication.client.Communication;
import com.fable.hamal.shuttle.communication.event.task.TaskRunEvent;

/**
 * 
 * @author xieruidong 2013年11月8日 上午10:37:51
 */
public class Client {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/hamal.shuttle.communication-client.xml");
		Communication communication = (Communication)context.getBean("client");
		TaskRunEvent rte = new TaskRunEvent();
		rte.setData(10100L);
		//String chinese = (String)
		communication.call("rmi://192.168.0.64:1099/eventService", rte);
//		String english = (String)communication.call("rmi://rmihost:1099/eventService", new EnglishHelloEvent());
		
//		System.out.println(chinese);
//		System.out.println(english);
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
