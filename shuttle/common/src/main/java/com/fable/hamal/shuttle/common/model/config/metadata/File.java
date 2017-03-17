/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.shuttle.common.model.config.metadata;

import com.fable.hamal.shuttle.common.model.envelope.data.Source;
import com.fable.hamal.shuttle.common.model.envelope.data.Target;

/**
 * 
 * @author xieruidong 2013年11月4日 上午11:22:15
 */
public class File implements Source, Target {

	private String filename;
	private long size;
	private String atime;
	private String ctime;
	private String path;
	private Ftp ftp;
	private boolean isDirectory;
	
	private static final String FACTORY_PREFIX_DEFAULT = "ftpPath";
	
	private String factoryPrefix = null;
	
	//--------------------------------getter/setter---------------------------
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public long getSize() {
		return size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}
	
	public String getAtime() {
		return atime;
	}
	
	public void setAtime(String atime) {
		this.atime = atime;
	}
	
	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Ftp getFtp() {
		return ftp;
	}

	public void setFtp(Ftp ftp) {
		this.ftp = ftp;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

    
    public void setFactoryPrefix(String factoryPrefix) {
        this.factoryPrefix = factoryPrefix;
    }

    public String getFactoryPrefix() {
        return (null == factoryPrefix || "".equals(factoryPrefix)) ? FACTORY_PREFIX_DEFAULT : factoryPrefix;
    }
}
