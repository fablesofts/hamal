/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.connection;

/**
 * 
 * @author xieruidong 2013年11月1日 上午10:36:34
 */
public class ConnectionParameter {
	
	/**target ip*/
	private String address;
	
	private int port;
	
	private String service = "eventService";
	
	public ConnectionParameter(String address, int port, String service) {
		this.address = address;
		this.port = port;
		this.service = service;
	}
	
	/**rmi//:{0}:{1}/{2}*/
	public ConnectionParameter(String full) {
		String[] pre = full.split("\\//");
		String[] service = pre[1].split("\\/");
		String[] target = service[0].split(":");
		this.address = target[0];
		this.port = Integer.valueOf(target[1]);
		this.service = service[1];
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
}