/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.ftp.client;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

/**
 * 
 * @author xieruidong 2014年5月16日 下午3:26:29
 */
public class FTPConnectionFactory {

	/**ftp控制字符集--默认为UTF-8*/
	private String controlEncoding = "UTF-8";
	/**数据传输超时时间-默认为1小时*/
	private int dataTimeout = 3600 * 1000;
	/**打开一个Socket默认超时时间10秒*/
	private int defaultTimeout = 10 * 1000;
	/**文件传输类型--默认为二进制文件*/
	private int fileType = 2;
	
	public FTPClient createFTPConnection(FTPConnectionParameter parameter) {
		FTPClient connection = new FTPClient();
		connection.setControlEncoding(controlEncoding); //设置字符集  
		connection.setDataTimeout(dataTimeout);
		connection.setDefaultTimeout(defaultTimeout);
		try {
			connection.connect(parameter.getHost(), parameter.getPort());
			connection.login(parameter.getUsername(), parameter.getPassword());
			connection.pasv();
			connection.setFileType(fileType);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		connection.enterLocalPassiveMode();
		return connection;
	}
	
	public void releaseFTPConnection(FTPClient connection) {
		
	}
	
	public void setControlEncoding(String controlEncoding) {
		this.controlEncoding = controlEncoding;
	}
	public void setDataTimeout(int dataTimeout) {
		this.dataTimeout = dataTimeout;
	}
	public void setDefaultTimeout(int defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}
	public void setFileType(int fileType) {
		this.fileType = fileType;
	}
}
