/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.ftp.client;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author xieruidong 2014年5月16日 下午3:06:55
 */
public class FTPConnectionPool {

	private final static Logger logger = LoggerFactory.getLogger(FTPConnectionPool.class);
	private volatile GenericKeyedObjectPool pool	= null;
	private FTPConnectionFactory factory = null;
	private int	maxActive = 10;
	
	public FTPConnectionPool() {
		factory = new FTPConnectionFactory();
		initial();
	}
	
	public void initial() {
		// 创建链接池对象
        pool = new GenericKeyedObjectPool();
        pool.setMaxActive(maxActive);
        pool.setMaxIdle(maxActive);
        pool.setMinIdle(0);
        pool.setMaxWait(60 * 1000); // 60s
        pool.setTestOnBorrow(false);
        pool.setTestOnReturn(false);
        pool.setTimeBetweenEvictionRunsMillis(10 * 1000);
        pool.setNumTestsPerEvictionRun(maxActive * 2);
        pool.setMinEvictableIdleTimeMillis(30 * 60 * 1000);
        pool.setTestWhileIdle(true);
        pool.setFactory(new FTPConnectionPoolableFactory(factory)); // 设置连接池管理对象
	}
	
	public void destory() {
        try {
            pool.close();
        } catch (Exception e) {
        	logger.error("Close ftp connection failed:---{}", e.getMessage());
            throw new RuntimeException("Close ftp connection failed", e);
        }
    }
	
	public void getConnection(FTPConnectionParameter parameter) {
		try {
			pool.borrowObject(parameter);
		} catch (Exception e) {
			throw new RuntimeException("Create FTPConnection failed" + e.getMessage());
		}
	}
	
	public void releaseConnection() {
		//do nothing!
	}
	
	//--------------------------------------setter / getter------------------------------
	public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }
}
