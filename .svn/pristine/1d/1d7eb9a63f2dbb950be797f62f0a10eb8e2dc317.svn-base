package com.fable.hamal.ftp.manager;

import java.io.File;
import java.util.List;

import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;
/**
 * 文件方式用户管理类
 * @author 邱爽
 *
 */
public class PropertyFtpdManager  {
	
	private static PropertiesUserManager userManager;
	static{
		if (userManager == null) {
			String userfile = System.getProperty("user.dir")+File.separator+"src"+File.separator+"main/resources/users.properties";
			File file = new File(userfile);
			SaltedPasswordEncryptor salterpassword = new SaltedPasswordEncryptor();
			salterpassword.encrypt("clear");
			userManager = new PropertiesUserManager(salterpassword,file,"admin");
		}
	}

	
	
}
