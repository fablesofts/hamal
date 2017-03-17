/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.ftp.manager;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.FtpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.ftp.util.HamalContextHelper;
import com.fable.hamal.shuttle.communication.event.Event;
import com.fable.hamal.shuttle.communication.event.EventHandler;
import com.fable.hamal.shuttle.communication.event.EventRegisterCenter;
import com.fable.hamal.shuttle.communication.event.ftp.FtpdEventType;

/**
 * 
 * @author xieruidong 2014年2月18日 下午3:56:50
 */
public class FtpdManager {

	private final static Logger logger = LoggerFactory.getLogger(FtpdManager.class);
	private final static FtpServer ftpd = HamalContextHelper.getBean("ftpd");
	private final static String STATUS_STARTED = "0";
	private final static String STATUS_STOPED = "1";
	private final static String STATUS_SUSPENDED = "2";
	
	static {
		EventRegisterCenter.regist(FtpdEventType.START, new EventHandler() {

			@Override
			public Object handle(Event event) {
				start();
				return true;
			}
		});
		EventRegisterCenter.regist(FtpdEventType.RESUME, new EventHandler() {

			@Override
			public Object handle(Event event) {
				resume();
				return true;
			}
		});
		EventRegisterCenter.regist(FtpdEventType.STOP, new EventHandler() {

			@Override
			public Object handle(Event event) {
				stop();
				return true;
			}
		});
		EventRegisterCenter.regist(FtpdEventType.SUSPEND, new EventHandler() {

			@Override
			public Object handle(Event event) {
				suspend();
				return true;
			}
		});
		
		EventRegisterCenter.regist(FtpdEventType.STATUS, new EventHandler() {

			@Override
			public Object handle(Event event) {
				if (ftpd.isStopped()) {
					return STATUS_STOPED;
				}
				if (ftpd.isSuspended()) {
					return STATUS_SUSPENDED;
				}
				return STATUS_STARTED;
			}
		});
		
	}
	
	public static void initial() {
		//do something init
	}
	
	public static void start() {
		try {
			ftpd.start();
		} catch (FtpException e) {
			logger.error("ftp服务启动异常，异常信息：{}",e.getMessage());
		}
	}
	
	public static void stop() {
		try {
			ftpd.stop();
		} catch (Exception e) {
			logger.error("ftp服务器停止异常，异常信息：{}",e.getMessage());
		}
	}
	
	public static void suspend() {
		try {
			ftpd.suspend();
		} catch (Exception e) {
			logger.error("ftp服务器暂停异常，异常信息：{}",e.getMessage());
		}
	}
	
	public static void resume() {
		try {
			ftpd.resume();
		} catch (Exception e) {
			logger.error("ftp服务器恢复异常，异常信息为:{}",e.getMessage());
		}
	}
}
