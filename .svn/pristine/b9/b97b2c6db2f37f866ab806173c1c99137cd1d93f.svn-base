/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.proxy.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 
 * @author xieruidong 2014年4月14日 下午5:17:51
 */
public class MachineInfo {

	private static String hostname;
	private static String ip;
	
	static {
		InetAddress ia;
		try {
			ia = InetAddress.getLocalHost();
			hostname = ia.getHostName();  
	        ip= ia.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}   
	}
	
	public static String getHostname() {
		return hostname;
	}
	
	public static String getLocalIp() {
		return ip;
	}
}
