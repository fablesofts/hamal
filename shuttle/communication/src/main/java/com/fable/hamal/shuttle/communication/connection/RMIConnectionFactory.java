/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.connection;

import java.text.MessageFormat;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import com.fable.hamal.shuttle.communication.service.EventService;

/**
 * 
 * @author xieruidong 2013年11月1日 上午10:01:31
 */
public class RMIConnectionFactory implements ConnectionFactory {
	
	private final String RMI_SERVICE_URL = "rmi://{0}:{1}/{2}";
	
	static {
		//设置连接超时时间为1分钟
		System.setProperty("sun.rmi.transport.connectTimeout", "60000");
	}

	public Connection create(ConnectionParameter parameter) {
		
		if (parameter == null) {
            throw new IllegalArgumentException("parameter is null!");
        }

        String serviceUrl = MessageFormat.format(RMI_SERVICE_URL, parameter.getAddress(), String.valueOf(parameter.getPort()), parameter.getService());
        
        RmiProxyFactoryBean proxy = new RmiProxyFactoryBean();
        proxy.setServiceUrl(serviceUrl);
        proxy.setServiceInterface(EventService.class);
        proxy.afterPropertiesSet();
        return new RmiConnection((EventService) proxy.getObject(), parameter);// 创建链接
	}

	public void close() {
		
	}
}
