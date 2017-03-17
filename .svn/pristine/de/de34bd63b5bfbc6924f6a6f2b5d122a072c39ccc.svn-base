package com.fable.hamal.ftp.start;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.impl.DefaultFtpServer;

import com.fable.hamal.ftp.manager.FtpUserManager;
import com.fable.hamal.ftp.manager.FtpdManager;
import com.fable.hamal.ftp.util.HamalContextHelper;

public class Main {

	public static void main(String[] args) {
		FtpServer ftpd = HamalContextHelper.getBean("ftpd");
		FtpdManager.initial();
		UserManager um = ((DefaultFtpServer)ftpd).getUserManager();
		FtpUserManager.setUserManager(um);
		FtpUserManager.initial(um);
	}
}
