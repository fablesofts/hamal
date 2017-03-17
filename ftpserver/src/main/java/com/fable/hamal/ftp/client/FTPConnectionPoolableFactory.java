/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.ftp.client;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool.KeyedPoolableObjectFactory;

/**
 * 
 * @author xieruidong 2014年5月16日 下午3:03:07
 */
public class FTPConnectionPoolableFactory implements KeyedPoolableObjectFactory {

	private FTPConnectionFactory factory;
	
	public FTPConnectionPoolableFactory(FTPConnectionFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public Object makeObject(Object key) throws Exception {
		if (key instanceof FTPConnectionParameter) {
			return factory.createFTPConnection((FTPConnectionParameter)key);
		} else {
            throw new IllegalArgumentException("key object is not FTPConnectionParameter!");
        }
	}

	@Override
	public void destroyObject(Object key, Object obj) throws Exception {
		if (obj instanceof FTPClient) {
			factory.releaseFTPConnection((FTPClient)obj);
		} else {
            throw new IllegalArgumentException("obj is not instance of FTPClient");
        }
	}

	@Override
	public boolean validateObject(Object key, Object obj) {
		if (obj instanceof FTPClient) {
			FTPClient client = (FTPClient)obj;
			return client.isConnected();
		}
		return false;
	}

	@Override
	public void activateObject(Object key, Object obj) throws Exception {
		
	}

	@Override
	public void passivateObject(Object key, Object obj) throws Exception {
		
	}
}
