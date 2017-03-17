/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.proxy.model;

/**
 * 
 * @author xieruidong 2014年4月14日 下午4:13:19
 */
public class NetGapConfig {

	private Long id;
	private String gapName;
	private String innerIp;
	private String outerIp;
	private Integer innerProxyPort;
	private Integer outProxyPort;
	private Integer servicePort;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGapName() {
		return gapName;
	}
	public void setGapName(String gapName) {
		this.gapName = gapName;
	}
	public String getInnerIp() {
		return innerIp;
	}
	public void setInnerIp(String innerIp) {
		this.innerIp = innerIp;
	}
	public String getOuterIp() {
		return outerIp;
	}
	public void setOuterIp(String outerIp) {
		this.outerIp = outerIp;
	}
	public Integer getInnerProxyPort() {
		return innerProxyPort;
	}
	public void setInnerProxyPort(Integer innerProxyPort) {
		this.innerProxyPort = innerProxyPort;
	}
	public Integer getOutProxyPort() {
		return outProxyPort;
	}
	public void setOutProxyPort(Integer outProxyPort) {
		this.outProxyPort = outProxyPort;
	}
	public Integer getServicePort() {
		return servicePort;
	}
	public void setServicePort(Integer servicePort) {
		this.servicePort = servicePort;
	}
}
