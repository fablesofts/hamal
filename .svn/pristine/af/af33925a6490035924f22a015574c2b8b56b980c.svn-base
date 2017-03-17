package com.fable.hamal.ftp.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.ftp.config.dao.FtpMappingDao;
import com.fable.hamal.ftp.config.dao.FtpUserDao;
import com.fable.hamal.ftp.util.HamalContextHelper;
import com.fable.hamal.shuttle.common.model.config.FtpUser;
import com.fable.hamal.shuttle.communication.event.Event;
import com.fable.hamal.shuttle.communication.event.EventHandler;
import com.fable.hamal.shuttle.communication.event.EventRegisterCenter;
import com.fable.hamal.shuttle.communication.event.ftp.UserEventType;
/**
 * 管理FTP用户类
 * @author 邱爽
 *
 */
public class FtpUserManager {
	
	private final static Logger logger = LoggerFactory.getLogger(FtpUserManager.class);
	private static org.apache.ftpserver.ftplet.UserManager userManager; 
	/**
	 * 查询用户相关信息类
	 */
	private static FtpUserDao ftpUserDao = HamalContextHelper.getBean("ftpUserDao");
	private static FtpMappingDao ftpMappingDao = HamalContextHelper.getBean("ftpMappingDao");
	static {
		EventRegisterCenter.regist(UserEventType.SAVE, new EventHandler() {
			
			@Override
			public Object handle(Event event) {
				return addFtpUser((FtpUser) event.getData());
			}
		});
		EventRegisterCenter.regist(UserEventType.ISADMIN, new EventHandler() {
			
			@Override
			public Object handle(Event event) {
				return isAdmin((String) event.getData());
			}
		});
		EventRegisterCenter.regist(UserEventType.EDIT, new EventHandler() {
			
			@Override
			public Object handle(Event event) {
				return editFtpUser((FtpUser) event.getData());
			}
		});
		EventRegisterCenter.regist(UserEventType.DELETE, new EventHandler() {
			
			@Override
			public Object handle(Event event) {
				return deleteFtpUser((String) event.getData());
			}
		});
		EventRegisterCenter.regist(UserEventType.UNIQUEQUERY, new EventHandler() {
			
			@Override
			public Object handle(Event event) {
				return getFtpUserByName((String) event.getData());
			}
		});
		EventRegisterCenter.regist(UserEventType.LIST, new EventHandler() {
			
			@Override
			public Object handle(Event event) {
				return listFtpUser();
			}
		});
		EventRegisterCenter.regist(UserEventType.DOESEXIST, new EventHandler() {
			
			@Override
			public Object handle(Event event) {
				return doesExist((String)event.getData());
			}
		});
		EventRegisterCenter.regist(UserEventType.UPPWD, new EventHandler() {
			
			@Override
			public Object handle(Event event) {
				return updatePwd((FtpUser) event.getData());
			}
		});
	}
	
	public static org.apache.ftpserver.ftplet.UserManager getUserManager() {
		return userManager;
	}
	public static void initial(UserManager um) {
		userManager = um;
	}
	public static void setUserManager(org.apache.ftpserver.ftplet.UserManager userManager) {
		FtpUserManager.userManager = userManager;
	}
	/**
	 * 新增FTP用户
	 * @param ftpUser
	 * @return
	 */
	public static boolean addFtpUser(FtpUser ftpUser) {
		try {
			BaseUser user = new BaseUser();
			user.setName(ftpUser.getFtpUsername());
			user.setPassword(ftpUser.getFtpPassword());
			user.setHomeDirectory(ftpUser.getHomeDirectory());
			user.setEnabled(true);
			user.setMaxIdleTime(600000);
			List<Authority> alist = new ArrayList<Authority>();
			Authority a = new WritePermission();// 写权限
			Authority readAuthority = new TransferRatePermission(6000,6000);
			alist.add(a);
			alist.add(readAuthority);
			user.setAuthorities(alist);
			userManager.save(user);
			return true;
		} catch (FtpException e) {
			logger.error("新增ftp用户时出现异常，异常信息：{}",e.getMessage());
			return false;
		}
	}
	/**
	 * 判断该用户是否存在
	 * @param userName
	 * @return
	 */
	public static boolean doesExist(String userName) {
		boolean flag=false;
		try {
			flag = userManager.doesExist(userName);
		} catch (FtpException e) {
			logger.error("判断用户是否存在时出现异常，异常信息：{}",e.getMessage());
		}
		return flag;
	}
	/**
	 * 修改密码
	 * @param user
	 * @return
	 */
	public static boolean updatePwd(FtpUser user) {
		boolean flag=false;
		try {
			FtpUser dto= (FtpUser)getFtpUserByName(user.getFtpUsername());
			deleteFtpUser(user.getFtpUsername());
			BaseUser baseUser = new BaseUser();
			baseUser.setName(user.getFtpUsername());
			baseUser.setPassword(user.getFtpPassword());
			baseUser.setHomeDirectory(dto.getHomeDirectory());
			baseUser.setEnabled(true);
			baseUser.setMaxIdleTime(600000);
			List<Authority> alist = new ArrayList<Authority>();
			Authority a = new WritePermission();// 写权限
			Authority readAuthority = new TransferRatePermission(6000,6000);
			alist.add(a);
			alist.add(readAuthority);
			baseUser.setAuthorities(alist);
			userManager.save(baseUser);
			flag = true;
		} catch (Exception e) {
			logger.error("修改FTP用户密码时出现异常，异常信息：",e.getMessage());
			flag = false;
		}
		return flag;
	}
	/**
	 * 编辑FTP用户
	 * @param user
	 * @return
	 */
	public static boolean editFtpUser(FtpUser user) {
		try {
			//先删除
			String pwd = ftpUserDao.getPwdByUserName(user.getFtpUsername());
			pwd = pwd.substring(1, pwd.length()-1);
			userManager.delete(user.getFtpUsername());
			BaseUser baseUser = new BaseUser();
			baseUser.setName(user.getFtpUsername());
			baseUser.setPassword(pwd);
			baseUser.setHomeDirectory(user.getHomeDirectory());
			baseUser.setEnabled(true);
			baseUser.setMaxIdleTime(600000);
			List<Authority> alist = new ArrayList<Authority>();
			Authority a = new WritePermission();// 写权限
			alist.add(a);
			baseUser.setAuthorities(alist);
			//再新增
			userManager.save(baseUser);
			return true;
		} catch (FtpException e) {
			logger.error("修改ftp用户时出现异常，异常信息：{}",e.getMessage());
			return false;
		}
	}
	/**
	 * 是否是管理员
	 * @param userName
	 * @return
	 */
	public static boolean isAdmin(String userName) {
		boolean flag = false;
		try {
			flag = userManager.isAdmin(userName);
			//true是，false否
//			userManager.doesExist(username)
		} catch (FtpException e) {
			logger.error("判断该用户是否是管理员时出现异常，异常信息为：{}",e.getMessage());
		}
		return flag;
	}
	/**
	 * 删除FTP用户
	 * @param userName
	 * @return
	 */
	public static boolean deleteFtpUser(String userName) {
		boolean flag = false;
		try {
			if(!ftpMappingDao.existsMapping(userName)) {
				userManager.delete(userName);
				flag = true;
			}else {
				logger.error("该用户已存在映射关系，无法删除");
			}
		} catch (FtpException e) {
			logger.error("删除ftp用户时出现异常，异常信息：{}",e.getMessage());
		}
		return flag;
	}
	/**
	 * 查询FTP用户
	 * @return
	 */
	public static List<FtpUser> listFtpUser() {
		try {
			String[] names = userManager.getAllUserNames();
			List<FtpUser>users = new ArrayList<FtpUser>();
			for (int i = 0 ;i<names.length;i++){
				String homepath =  ftpUserDao.getPathByUser(names[i]);
				homepath = homepath.substring(1, homepath.length()-1);
				FtpUser ftpUser = new FtpUser();
				ftpUser.setFtpUsername(names[i]);
				ftpUser.setHomeDirectory(homepath);
				users.add(ftpUser);
			}
			return users;
			
		} catch (FtpException e) {
			logger.error("查询ftp用户详细信息时时出现异常，异常信息：{}",e.getMessage());
			return null;
		}
	}
	/**
	 * 通过用户名唯一查询
	 * @param userName
	 * @return
	 */
	public static FtpUser getFtpUserByName(String userName) {
		try {
			BaseUser baseUser= (BaseUser) userManager.getUserByName(userName);
			FtpUser user = new FtpUser();
			user.setFtpUsername(baseUser.getName());
			user.setHomeDirectory(baseUser.getHomeDirectory());
			user.setFtpPassword(baseUser.getPassword());
			return user;
		} catch (FtpException e) {
			logger.error("查询ftp用户详细信息时时出现异常，异常信息：{}",e.getMessage());
			return null;
		}
	}
}
