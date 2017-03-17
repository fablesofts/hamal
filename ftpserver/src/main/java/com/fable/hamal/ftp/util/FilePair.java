/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.ftp.util;

/**
 * 
 * @author xieruidong 2014年5月19日 下午5:06:56
 */
public class FilePair {

	private String source;
	private String target;
	
	public FilePair() {
		
	}
	
	public FilePair(String source, String target) {
		this.source = source;
		this.target = target;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
