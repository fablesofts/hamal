/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.utils.rmi;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author xieruidong 2013年11月29日 下午3:26:01
 */
public class RmiUtil {

	private static final String PROTOCOL = "rmi://";
	private static final String SLASH = "/";
	private static final String SERVICE = "eventService";
	private static final String SEMICOLON = ";";
	
	/**example:url->localhost:1099*/
	public static String getRmiUrl(String url) {
		StringBuffer sb = new StringBuffer();
		sb.append(PROTOCOL).append(url).append(SLASH).append(SERVICE);
		return sb.toString();
	}
	
	/**example:url->localhost:1099;192.168.1.100:1099*/
	public static String[] getRmiUrls(String urls) {
		List<String> result = new ArrayList<String>();
		String[] tmp = urls.split(SEMICOLON);
		for (String url : tmp) {
			result.add(getRmiUrl(url));
		}
		return result.toArray(tmp);
	}
	
	public static void main(String[] args) {
		String[] tmp = getRmiUrls("localhost:1099;192.168.1.100:1099");
		for (String str : tmp) {
			System.out.println(str);
		}
	}
}
