/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.envelope.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author xieruidong 2013年11月4日 下午1:49:41
 */
public class Pump implements Serializable{

	private static final long serialVersionUID = -3619391079694624127L;

	/**生成的UUID*/
	protected Long id;
	/**任务ID和taskId相同*/
	protected Long jobId;
	/**子任务ID*/
	protected Long pipelineId;
	/**流水号*/
	protected String serial;
	
	protected Source source;
	
	protected List<Pair> pairs = new ArrayList<Pair>();

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public List<Pair> getPairs() {
		return pairs;
	}

	public void setPairs(List<Pair> pairs) {
		this.pairs = pairs;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public Long getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(Long pipelineId) {
		this.pipelineId = pipelineId;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}
}