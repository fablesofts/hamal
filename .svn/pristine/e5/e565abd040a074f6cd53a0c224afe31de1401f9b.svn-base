/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.envelope.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author xieruidong 2013年11月22日 下午3:31:44
 */
public class Job implements Serializable {

	private static final long serialVersionUID = -4738838600833186858L;

	private Long id;
	
	private String name;
	
	private String crontabExpression;
	
	private List<Pump> pumps = new ArrayList<Pump>();

	/**作业运行批次，以天为单位*/
	private String serial;
	
	public Job() {
		
	}
	
	public Job(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCrontabExpression() {
		return crontabExpression;
	}

	public void setCrontabExpression(String crontabExpression) {
		this.crontabExpression = crontabExpression;
	}

	public List<Pump> getPumps() {
		return pumps;
	}

	public void setPumps(List<Pump> pumps) {
		this.pumps = pumps;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}
}
