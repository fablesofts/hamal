/*
 * Copyright (C) 2013-2033 Fable Limited.
 */

package com.fable.hamal.shuttle.common.model.config;

import java.io.Serializable;

public class Pipeline implements Serializable {

	private static final long serialVersionUID = -6393819925469806054L;
	
	private Long id;
	private Long taskId;
	private Long sourceId;
	private Long targetId;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public Long getSourceId() {
		return sourceId;
	}
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	public Long getTargetId() {
		return targetId;
	}
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
}
