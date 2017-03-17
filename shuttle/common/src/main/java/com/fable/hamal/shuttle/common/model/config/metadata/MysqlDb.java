/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.config.metadata;

/**
 * 
 * @author xieruidong 2013年11月5日 上午11:17:38
 */
public class MysqlDb extends Db {

	private String defaultEngine = "innodb";
	
	private String defaultCharactor = "utf8";
	
	
	public String getDefaultEngine() {
		return defaultEngine;
	}
	
	public void setDefaultEngine(String defaultEngine) {
		this.defaultEngine = defaultEngine;
	}
	
	public String getDefaultCharactor() {
		return defaultCharactor;
	}

	public void setDefaultCharactor(String defaultCharactor) {
		this.defaultCharactor = defaultCharactor;
	}
}
