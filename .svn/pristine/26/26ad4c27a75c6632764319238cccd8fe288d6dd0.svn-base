/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.communication.client;

import org.springframework.util.Assert;

import com.fable.hamal.shuttle.communication.connection.Connection;
import com.fable.hamal.shuttle.communication.connection.ConnectionFactory;
import com.fable.hamal.shuttle.communication.connection.ConnectionParameter;
import com.fable.hamal.shuttle.communication.event.Event;

/**
 * 
 * @author xieruidong 2013年11月7日 上午11:10:50
 */
public class DefaultCommunication implements Communication {

	private ConnectionFactory factory;
	
	public Object call(String address, Event event) {
		Assert.notNull(address);
		Assert.notNull(factory);
		Connection connection = factory.create(new ConnectionParameter(address));
		return connection.call(event);
	}
	
	public Object calls(String[] addresses, Event event) {
		
		return null;
	}

	public ConnectionFactory getFactory() {
		return factory;
	}

	public void setFactory(ConnectionFactory factory) {
		this.factory = factory;
	}
}
