/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.envelope;

import com.fable.hamal.shuttle.common.model.config.metadata.File;

/**
 * 
 * @author xieruidong 2013年11月5日 上午11:00:06
 */
public class File2FilePump {
	
	private File source;
	
	private File target;
	
	public File getSource() {
		return source;
	}
	
	public void setSource(File source) {
		this.source = source;
	}
	
	public File getTarget() {
		return target;
	}
	
	public void setTarget(File target) {
		this.target = target;
	}
}
