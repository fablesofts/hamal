/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.ftp.listener;

import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.usermanager.impl.BaseUser;

/**
 * 
 * @author xieruidong 2014年5月17日 下午3:53:07
 */
public interface ReplicationService {

	public void process(BaseUser user, FtpFile file);
	public void uncompress(String zipball);
}
