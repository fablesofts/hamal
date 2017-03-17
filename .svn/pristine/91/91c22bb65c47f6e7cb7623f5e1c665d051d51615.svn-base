/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.ftp.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ftpserver.usermanager.impl.BaseUser;

import com.fable.hamal.ftp.config.dao.FtpMappingDao;
import com.fable.hamal.ftp.config.dao.FtpUserDao;
import com.fable.hamal.ftp.dto.FtpMapping;
import com.fable.hamal.ftp.manager.Server;
import com.fable.hamal.ftp.util.HamalPropertyConfigurer;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

/**
 * 保存对应的信息
 * @author xieruidong 2014年5月17日 下午4:15:39
 */
public class MasterSlaveMapping {
	
	public static BaseUser reservedUser = null; 
	public static String reservedUserHome = null;
	/**映射关系*/
	public Map<String, List<FtpMapping>> fileMapping;
	/**slave上的用户信息 用enable字段标识,0表示slave用户*/
	public Map<String, BaseUser> users = new ConcurrentHashMap<String, BaseUser>();
	
	/**数据库查询类*/
	private FtpMappingDao ftpMappingDao;
	/**用户查询类 */
	private FtpUserDao ftpUserDao;
	
	
	public MasterSlaveMapping() {
		fileMapping = new MapMaker().makeComputingMap(new Function<String, List<FtpMapping>>() {

			@Override
			public List<FtpMapping> apply(String input) {
				return new ArrayList<FtpMapping>();
			}
		});
	}
	
	public void init() {
		
		Map<String, BaseUser> temp = new ConcurrentHashMap<String, BaseUser>();
		List<BaseUser> userList = ftpUserDao.getAllUser();
		for (BaseUser user : userList) {
			String username = user.getName();
			System.out.println("*******************************************************************************");
			System.out.println("*******************************************************************************");
			System.out.println("*******************************************************************************");
			System.out.println("*******************************************************************************");
			System.out.println(username + ":::" + Server.reservedUser);
			System.out.println("*******************************************************************************");
			System.out.println("*******************************************************************************");
			System.out.println("*******************************************************************************");
			if (Server.reservedUser.equals(username)) {
				reservedUser = user;
				reservedUserHome = user.getHomeDirectory();
			}
			temp.put(username, user);
		}
		
		boolean inner = ConfigurationConstants.INNERTOOUTER.equals(HamalPropertyConfigurer.getHamalProperty(ConfigurationConstants.FTP_SERVER_FLAG));
		List<FtpMapping> list= ftpMappingDao.getAllMappings();
		for (int i=0; i< list.size(); i++) {
			FtpMapping ft = list.get(i);
			String username = inner ? ft.getInnerUsername() : ft.getOuterUsername();
			String slaveUsername = inner ? ft.getOuterUsername() : ft.getInnerUsername();
			List<FtpMapping> ftpMappings = fileMapping.get(username);
			fileMapping.put(username, ftpMappings);
			if (null == users.get(username)) {
				users.put(username, temp.get(slaveUsername));
			}
			ftpMappings.add(ft);
		}
		
		
	}
	
	//-------------------------------------------------getter&&setter--------------------------------------------------
	public FtpMappingDao getFtpMappingDao() {
		return ftpMappingDao;
	}

	public void setFtpMappingDao(FtpMappingDao ftpMappingDao) {
		this.ftpMappingDao = ftpMappingDao;
	}
	
	public FtpUserDao getFtpUserDao() {
		return ftpUserDao;
	}

	public void setFtpUserDao(FtpUserDao ftpUserDao) {
		this.ftpUserDao = ftpUserDao;
	}
}
