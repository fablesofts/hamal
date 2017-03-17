/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.config.metadata;

import java.util.LinkedList;
import java.util.List;

import com.fable.hamal.shuttle.common.model.envelope.data.Source;
import com.fable.hamal.shuttle.common.model.envelope.data.Target;

/**
 * Table元数据
 * @author xieruidong 2013年11月4日 上午11:19:49
 */
public class Table implements Source, Target {
	
	private static final long serialVersionUID = -253329830094552664L;

	private static final String FACTORY_PREFIX_DEFAULT = "sqlTable";
	
	private String factoryPrefix = null;
	
	private Db db;
	
	private String tableName = "";
	
	private List<Column> columns = new LinkedList<Column>();
	
	
	//-------------------------------setter/getter-----------------------
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public List<Column> getColumns() {
		return columns;
	}
	
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public Db getDb() {
		return db;
	}

	public void setDb(Db db) {
		this.db = db;
	}

	public void setFactoryPrefix(String factoryPrefix) {
		this.factoryPrefix = factoryPrefix;
	}

	public String getFactoryPrefix() {
		return (null == factoryPrefix || "".equals(factoryPrefix)) ? FACTORY_PREFIX_DEFAULT : factoryPrefix;
	}
}
