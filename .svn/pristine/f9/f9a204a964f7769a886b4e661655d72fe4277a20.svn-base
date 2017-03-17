/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.ftp.listener;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.ftp.config.ConfigurationConstants;
import com.fable.hamal.ftp.config.dao.FtpMappingDao;
import com.fable.hamal.ftp.config.dao.FtpUserDao;
import com.fable.hamal.ftp.manager.Server;
import com.fable.hamal.ftp.util.HamalContextHelper;
import com.fable.hamal.ftp.util.HamalPropertyConfigurer;

/**
 * scope:prototype
 * @author xieruidong 2014年5月16日 下午7:08:30
 */
public class ReplicationListener extends DefaultFtplet {

	private final static Logger logger = LoggerFactory.getLogger(ReplicationListener.class);
	private final static String SLAVE_IP_NAME = "outerftp.server.ip";

	private ReplicationService replicate;
	private String tarballPrefix;
	
	public ReplicationListener() {
		tarballPrefix = HamalPropertyConfigurer.getHamalProperty(ConfigurationConstants.FTP_SERVER_TARBALL_NAME);
	}
	
	@Override
	public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
		String clientIp = session.getAttribute("clientIp").toString();
		String slaveIp = HamalPropertyConfigurer.getHamalProperty(SLAVE_IP_NAME);
		BaseUser currentUser = (BaseUser) session.getUser();
		String username = currentUser.getName();
		String fileName = request.getArgument();
		if (fileName == null) {
			return FtpletResult.SKIP;
		}
		
		if (clientIp.equals(slaveIp) && !fileName.startsWith(tarballPrefix)) {
			return super.onUploadEnd(session, request);
		}
		
		FtpFile file = null;
		try {
			file = session.getFileSystemView().getFile(fileName);
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				e.printStackTrace();
			}
			logger.error("Exception getting file object,{}", e.getMessage());
		}
		
		if (clientIp.equals(slaveIp) && fileName.startsWith(tarballPrefix) && username.equals(Server.reservedUser)) {
			//解压缩
			String fullPath = currentUser.getHomeDirectory() + file.getAbsolutePath();
			replicate.uncompress(fullPath.replaceAll("/", File.separator));
		}
		replicate.process(currentUser, file);
		
		return super.onUploadEnd(session, request);
	}
	
	@Override
	public FtpletResult onLogin(FtpSession session, FtpRequest request) throws FtpException, IOException {
		InetSocketAddress clientAddress = session.getClientAddress();
		if (logger.isInfoEnabled()) {
			logger.info("客户端信息：{}--{}",clientAddress.getHostName(),String.valueOf(clientAddress.getPort()));
		}
		session.setAttribute("clientIp", session.getClientAddress().getAddress().getHostAddress());
		return super.onLogin(session, request);
	}
	
	@Override
	public FtpletResult onDisconnect(FtpSession session) throws FtpException, IOException {
		return null;
	}

	public ReplicationService getReplicate() {
		return replicate;
	}

	public void setReplicate(ReplicationService replicate) {
		this.replicate = replicate;
	}
}
