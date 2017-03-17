/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.ftp.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author xieruidong 2014年5月19日 下午2:42:20
 */
public class FileHolder {

	public int size;
	public Set<FilePair> noSync = new HashSet<FilePair>();
	public Set<FilePair> sync = new HashSet<FilePair>();
	
	public synchronized int getSize() {
		return noSync.size();
	}
	
	public synchronized void setSize(int size) {
		this.size = noSync.size();
	}
	
	public Set<FilePair> getNoSync() {
		return noSync;
	}
	
	public void setNoSync(Set<FilePair> noSync) {
		this.noSync = noSync;
	}
	
	public Set<FilePair> getSync() {
		return sync;
	}
	
	public void setSync(Set<FilePair> sync) {
		this.sync = sync;
	}
}
