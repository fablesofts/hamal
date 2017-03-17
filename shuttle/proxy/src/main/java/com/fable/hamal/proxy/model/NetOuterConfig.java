/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.proxy.model;

/**
 * 
 * @author xieruidong 2014年4月14日 下午4:13:00
 */
public class NetOuterConfig {

	private Long id;
	private String hostname;
	private String toGapIp;
	private String toOuterNetIp;
	private Integer innerProxyPort;
	private Integer outerProxyPort;
	private Integer servicePort;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getToGapIp() {
		return toGapIp;
	}
	public void setToGapIp(String toGapIp) {
		this.toGapIp = toGapIp;
	}
	public String getToOuterNetIp() {
		return toOuterNetIp;
	}
	public void setToOuterNetIp(String toOuterNetIp) {
		this.toOuterNetIp = toOuterNetIp;
	}
	public Integer getInnerProxyPort() {
		return innerProxyPort;
	}
	public void setInnerProxyPort(Integer innerProxyPort) {
		this.innerProxyPort = innerProxyPort;
	}
	public Integer getOuterProxyPort() {
		return outerProxyPort;
	}
	public void setOuterProxyPort(Integer outerProxyPort) {
		this.outerProxyPort = outerProxyPort;
	}
	public Integer getServicePort() {
		return servicePort;
	}
	public void setServicePort(Integer servicePort) {
		this.servicePort = servicePort;
	}
}
