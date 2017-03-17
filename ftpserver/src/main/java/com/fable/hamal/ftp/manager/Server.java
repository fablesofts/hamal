/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.ftp.manager;

import com.fable.hamal.ftp.config.ConfigurationConstants;
import com.fable.hamal.ftp.util.HamalPropertyConfigurer;

/**
 * 
 * @author xieruidong 2014年5月19日 下午5:20:33
 */
public class Server {

	public static boolean inner = ConfigurationConstants.INNERTOOUTER.equals(HamalPropertyConfigurer.getHamalProperty(ConfigurationConstants.FTP_SERVER_FLAG));
	public static String tarball = HamalPropertyConfigurer.getHamalProperty(ConfigurationConstants.FTP_SERVER_TARBALL_NAME);
	public static String tempDirectory = HamalPropertyConfigurer.getHamalProperty(ConfigurationConstants.FTP_SERVER_TEMP_DIRECTORY);
	public static String innerRootPath = HamalPropertyConfigurer.getHamalProperty(ConfigurationConstants.FTP_SERVER_INNER_ROOT_PATH);
	public static String outerRootPath = HamalPropertyConfigurer.getHamalProperty(ConfigurationConstants.FTP_SERVER_OUTER_ROOT_PATH);
	public static String reservedUser = HamalPropertyConfigurer.getHamalProperty(ConfigurationConstants.FTP_SERVER_RESERVED_USER);
}
