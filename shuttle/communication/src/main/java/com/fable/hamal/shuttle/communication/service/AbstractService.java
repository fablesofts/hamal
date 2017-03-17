/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.service;

/**
 * 
 * @author xieruidong 2013年10月31日 下午3:09:38
 */
public abstract class AbstractService implements Service {

	protected String serviceName 				= null;
	protected Class<?> serviceInterface 			= null;
	protected boolean alwaysCreateRegister 	= false;
	
	
	public String getServiceName() {
		return serviceName;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public Class<?> getServiceInterface() {
		return serviceInterface;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	public boolean isAlwaysCreateRegister() {
		return alwaysCreateRegister;
	}
	
	public void setAlwaysCreateRegister(boolean alwaysCreateRegister) {
		this.alwaysCreateRegister = alwaysCreateRegister;
	}
}
