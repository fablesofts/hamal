/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.proxy.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * 
 * @author xieruidong 2014年4月8日 下午4:01:56
 */
public class Configure {

	public final static String DISTRIBUTE_INNER_TRANSFER_IN_PORT = "distribute.inner.transfer.in.port";
	public final static String DISTRIBUTE_INNER_TRANSFER_OUT_PORT = "distribute.inner.transfer.out.port";
	public final static String DISTRIBUTE_INNER_APP_IN = "distribute.inner.app.in";
	public final static String DISTRIBUTE_INNER_APP_OUT = "distribute.inner.app.out";
	public final static String DISTRIBUTE_OUTER_TRANSFER_IN_PORT = "distribute.outer.transfer.in.port";
	public final static String DISTRIBUTE_OUTER_APP_IN = "distribute.outer.app.in";
	public final static String DISTRIBUTE_OUTER_APP_OUT = "distribute.outer.app.out";
	
	public final static String JDBC_URL = "jdbc.url";
	public final static String JDBC_DRIVER = "jdbc.driverClassName";
	public final static String JDBC_USER = "jdbc.username";
	public final static String JDBC_PASSWORD = "jdbc.password";
	
	private Properties props = new Properties();
	private static Configure config = null;
	
	private Configure() {
		InputStream in1 = Configure.class.getClassLoader().getResourceAsStream("hamal.proxy.properties");
		InputStream in2 = Configure.class.getClassLoader().getResourceAsStream("jdbc.properties");
		
		try {
			if (null != in1) {
				props.load(in1);
			}
			props.load(in2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static String get(String key) {
		if (null == config) {
			config = new Configure();
		}
		return (String)config.props.get(key);
	}
	
	//ip:port
	public static String getConfigDatabaseInfo() {
		
		String url = get(JDBC_URL);
		String result = null;
		int start = 0;
		int end = 0;
		if (url.startsWith("jdbc:oracle")) {
			start = url.indexOf("@") + 1;
			end = url.lastIndexOf(":");
		} else if (url.startsWith("jdbc:mysql")) {
			start = url.indexOf("//") + 2;
			end = url.lastIndexOf("/");
		} else if (url.startsWith("jdbc:sqlserver")) {
			start = url.indexOf("//") + 2;
			end = url.lastIndexOf(";");
		} else {
			return null;
		}
		result = url.substring(start, end);
		return result;
	}
}
