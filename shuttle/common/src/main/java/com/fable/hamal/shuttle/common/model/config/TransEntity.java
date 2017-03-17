/*
 * Copyright (C) 2013-2033 Fable Limited.
 */

package com.fable.hamal.shuttle.common.model.config;

import java.io.Serializable;

public class TransEntity implements Serializable {

	private static final long serialVersionUID = -137563174587097543L;

	private Long id;
	private Long dataSourceId;
	private String type;
	private String tableName;
	private String location;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDataSourceId() {
		return dataSourceId;
	}
	public void setDataSourceId(Long dataSourceId) {
		this.dataSourceId = dataSourceId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}
