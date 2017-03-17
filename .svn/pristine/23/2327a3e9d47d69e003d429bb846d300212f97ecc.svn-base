/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.ftp.listener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import com.fable.hamal.ftp.client.FTPConnectionParameter;
import com.fable.hamal.ftp.client.FTPConnectionPool;
import com.fable.hamal.ftp.config.MasterSlaveMapping;
import com.fable.hamal.ftp.dto.FtpMapping;
import com.fable.hamal.ftp.manager.Server;
import com.fable.hamal.ftp.manager.Slave;
import com.fable.hamal.ftp.util.FileHolder;
import com.fable.hamal.ftp.util.FilePair;
import com.fable.hamal.ftp.util.GZIPUtil;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

/**
 * 1.用户的home目录不以“/”结尾
 * @author xieruidong 2014年5月17日 下午3:53:38
 */
public class ReplicationServiceImpl implements ReplicationService {
	
	public final static String SLASH = "/";
	private Map<String, FileHolder> files;
	private int critical = 2;
	private FTPConnectionPool pool;
	private MasterSlaveMapping slave;
	
	public ReplicationServiceImpl(){
		files = new MapMaker().makeComputingMap(new Function<String, FileHolder>(){
			@Override
			public FileHolder apply(String input) {
				return new FileHolder();
			}
		});
	}
	@Override
	public void process(BaseUser user, FtpFile file) {
		if (0 == critical) {
			return;
		}
		String username = user.getName();
		FileHolder holder = files.get(username);
		String home = user.getHomeDirectory();
		
		if (holder.getSize() >= critical) {
			synchronized(this) {
				if (holder.getSize() >= critical) {//双重校验,不可以去掉
					String tarball = getTarballName(username);
					Set<FilePair> pairs = files.get(username).getNoSync();
					GZIPUtil.compress(pairs, tarball);
//					clean(username, home, pairs);
					pairs.clear();
					return;
				}
			}
		}
		//file.getAbsolutePath()是ftp的路径以'/'开头，相对于home的相对路径
		BaseUser slaveUser = slave.users.get(username);
		if (null != slaveUser) {
			FilePair pair = getFilePair(username, home, file.getAbsolutePath(), slaveUser.getHomeDirectory());
			holder.getNoSync().add(pair);
		}
	}
	
	public void uncompress(String zipball) {
		GZIPUtil.decompressZip(zipball, "./");
	}
	
	public synchronized void upload(FTPConnectionParameter parameter) {
		pool.getConnection(parameter);
	}

	/**把上传文件移动到临时文件夹中*/
	public void clean(String username, String home, Set<FilePair> files) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		StringBuffer dst = new StringBuffer(Server.tempDirectory);
		if (!Server.tempDirectory.endsWith(File.separator)) {
			dst.append(File.separator);
		}
		dst.append(username).append(File.separator).append(sdf1.format(date));
		try {
			Iterator<FilePair> iter = files.iterator();
			while (iter.hasNext()) {
				String source = iter.next().getSource();
				String target = dst.append(getRelativepath(home, source)).append(getFilename(source)).toString();
				FileUtils.moveFile(new File(source), new File(target));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**获取相对于userhome的相对路径*/
	public String getRelativepath(String home, String file) {
		return file.substring(home.length());
	}
	
	/**获取文件名称，以文件分割符开始*/
	public String getFilename(String file) {
		int index = file.lastIndexOf(File.separator);
		return file.substring(index);
	}
	
	/**获取这批数据的压缩包名称*/
	public String getTarballName(String username) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String dateStr=sdf.format(date);
		StringBuffer sb = new StringBuffer(MasterSlaveMapping.reservedUserHome);
		sb.append(File.separator).append(Server.tarball);
		
		if (Server.inner) {
			sb.append("_inner_").append(dateStr).append(".zip");
		} else {
			sb.append("_outer_").append(dateStr).append(".zip");
		}
		return sb.toString().replaceAll(SLASH, File.separator);
	}
	
	/**FtpFile获取的文件，文件分割符始终为'/'需要进行转换*/
	public FilePair getFilePair(String username, String home, String fullname, String slaveHome) {
		List<FtpMapping> fms = slave.fileMapping.get(username);
		int lastSlash = fullname.lastIndexOf("/");
		int firstSlash = fullname.indexOf("/");
		String filename = fullname.substring(lastSlash + 1);
		StringBuffer sb = new StringBuffer(home);
		
		int length = Server.inner ? Server.outerRootPath.length() : Server.innerRootPath.length(); 
		String relative = slaveHome.substring(length);
		if (relative.startsWith(File.separator)) {
			relative = relative.substring(1);
		}
		StringBuffer target = new StringBuffer(relative);
		
		if (!home.endsWith(File.separator)) {
			sb.append(File.separator);
		}
		if (0 == lastSlash) {
			sb.append(filename);
			return new FilePair(sb.toString(), filename);
		}
		
		
		/**以‘/’开头*/
		String folder = fullname.substring(firstSlash, lastSlash);
		String[] sourceTemp = folder.split("/");
		for (int i=0; i < sourceTemp.length; i++) {
			sb.append(sourceTemp[i]).append(File.separator);
		}
		sb.append(filename);
		for (FtpMapping fm : fms) {
			if (fm.getMasterAddress().equals(folder)) {
				String[] targetTemp = fm.getSlaveAddress().split("/");
				for (int i=0; i < targetTemp.length; i++) {
					target.append(targetTemp[i]).append(File.separator);
				}
				target.append(filename);
				return new FilePair(sb.toString(), target.toString());
			}
		}
		target.append(File.separator).append(filename);
		return new FilePair(sb.toString(), target.toString());
	}
	
	//-----------------------------------------------setter&&getter------------------------------------------
	public int getCritical() {
		return critical;
	}

	public void setCritical(int critical) {
		this.critical = critical;
	}

	public FTPConnectionPool getPool() {
		return pool;
	}

	public void setPool(FTPConnectionPool pool) {
		this.pool = pool;
	}

	public MasterSlaveMapping getSlave() {
		return slave;
	}

	public void setSlave(MasterSlaveMapping slave) {
		this.slave = slave;
	}
}
