/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.server;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

import org.springframework.remoting.rmi.RmiServiceExporter;

import com.fable.hamal.shuttle.communication.exception.CommunicationException;
import com.fable.hamal.shuttle.communication.service.AbstractService;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

/**
 * 
 * @author xieruidong 2013年10月31日 上午11:52:07
 */
public class RmiEndpoint implements Endpoint {
	
	private String host;
	private int port = 1099;
	private boolean alwaysRegister = false;
	private Registry registry;
	
	private Map<String, RmiServiceExporter> exporters = null;
	
	private List<AbstractService> services= null;
	
	public RmiEndpoint() {
		exporters = new MapMaker().makeComputingMap(new Function<String, RmiServiceExporter>() {

            public RmiServiceExporter apply(String serviceName) {
                return new RmiServiceExporter();
            }
        });
	}

	public void initialize() {
		for (AbstractService service : services) {
			RmiServiceExporter exporter= exporters.get(service.getServiceName());
			
			exporter.setServiceName(service.getServiceName());
			exporter.setService(service);
			exporter.setServiceInterface(service.getServiceInterface());
			exporter.setAlwaysCreateRegistry(service.isAlwaysCreateRegister());
			exporter.setRegistryHost(host);
			if (null != registry) {
				exporter.setRegistry(registry);
			} else {
				exporter.setRegistryPort(port);
			}
			
			try {
	            exporter.afterPropertiesSet();
	        } catch (RemoteException e) {
	            throw new CommunicationException("Rmi_Create_Error", e);
	        }
		}
	}
	
	public void destroy() {
		
	}
	
	public int out() {
		return exporters.size();
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isAlwaysRegister() {
		return alwaysRegister;
	}

	public void setAlwaysRegister(boolean alwaysRegister) {
		this.alwaysRegister = alwaysRegister;
	}

	public List<AbstractService> getServices() {
		return services;
	}

	public void setServices(List<AbstractService> services) {
		this.services = services;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
}
