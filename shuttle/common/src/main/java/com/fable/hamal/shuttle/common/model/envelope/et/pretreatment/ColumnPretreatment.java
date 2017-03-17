/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.envelope.et.pretreatment;

import java.io.Serializable;

/**
 * 
 * @author xieruidong 2013年12月25日 下午3:00:15
 */
public class ColumnPretreatment implements Serializable {

	private static final long serialVersionUID = -3299567473343028953L;
	private String name;
	private int index;
	private int type;
	
	public ColumnPretreatment() {
		
	}
	
	public ColumnPretreatment(String name) {
		this.name = name;
	}
	
	public ColumnPretreatment(String name, int index, int type) {
		this.name = name;
		this.index = index;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
