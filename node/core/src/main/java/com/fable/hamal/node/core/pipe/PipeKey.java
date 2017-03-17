/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.node.core.pipe;

import java.io.Serializable;

import com.fable.hamal.shuttle.allocation.enums.DataType;

/**
 * 
 * @author xieruidong 2013年12月16日 下午2:10:36
 */
public class PipeKey implements Serializable {

	private static final long serialVersionUID = -5389413780091004321L;
	private DataType dataType;
	private Long processId;
	
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public Long getProcessId() {
		return processId;
	}
	public void setProcessId(Long processId) {
		this.processId = processId;
	}
}
