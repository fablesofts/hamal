package com.fable.hamal.ftp.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.fable.hamal.ftp.config.ConfigInfo;

public class FtpHelper {
	//日志
	private static Logger logger = Logger.getLogger(FtpHelper.class);
	//客户端操作
	public FTPClient ftpClient = new FTPClient();
	
	public boolean connect(String hostname, int port, String user,String password) {
		logger.info("begin connect");
		logger.info("hostname:" +hostname+ " port:"+port+" user" +user +" password:"+password);
		//连接服务器
		try {
			ftpClient.connect(hostname,port);
			ftpClient.setControlEncoding("UTF-8");
			//设置客户端操作系统类型，为windows,其实就是"WINDOWS"
			FTPClientConfig conf = new FTPClientConfig(ConfigInfo.getSystem());
			//设置服务器端语言 中文“zh”
			conf.setServerLanguageCode("zh");
			//判断服务器返回值，验证是否已经连接上
			if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				//验证用户名，密码
				if(ftpClient.login(user, password)) {
					logger.info("已经连接到ftp.....");
					return true;
				}
				logger.error("连接ftp的用户名或密码错误。。。。。。");
				//取消连接
				disconnect();
			}
		} catch (SocketException e) {
			logger.error("连接不上ftp.....",e);
		} catch (IOException e) {
			logger.error("出现 io异常.....",e);
		}
		return false;
	}
	/**
	 * 关闭FTP连接
	 */
	public void disconnect() {
		logger.info("进入FTP连接关闭连接方法...");
		// 判断客户端是否连接上FTP
		if (ftpClient.isConnected()) {
			// 如果连接上FTP，关闭FTP连接
			try {
				logger.info("关闭ftp连接......");
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error("关闭ftp连接出现异常......", e);
				// e.printStackTrace();
			}
		}
	}
	/**
	 * 查询当前工作空间下的所有ftp文件包括了目录
	 * @return 文件数组
	 */
	public FTPFile[] getFilesList() {
		logger.info("进入查询ftp所有文件方法");
		try {
			FTPFile[]ftpFiles = ftpClient.listFiles();
			int num = 0;
			for (FTPFile ftpFile : ftpFiles) {
				if(!ftpFile.isFile()) {
					continue;
				}
				num++;
			}
			logger.info("进入查询上文件个数。。。。"+num);
			logger.info("进入查询ftp所有文件方法结束.....");
			return ftpFiles;
		} catch (IOException e) {
			logger.error("查询ftp上文件失败。。。。",e);
			return null;
		}
	}
	/**
	 * 变更工作目录
	 * 
	 * @param remoteDir 变更到的工作目录
	 */
	public boolean changeDir(String remoteDir) {
		try {
			logger.info("变更工作目录为：" + remoteDir);
			ftpClient.changeWorkingDirectory(new String(remoteDir
					.getBytes("UTF-8"), "iso8859-1"));
			return true;
		} catch (IOException e) {
			logger.error("变更工作目录为" + remoteDir + "失败", e);
			return false;
		}
	}
	/**
	 * 变更工作目录到父目录
	 * @return
	 */
	public boolean changeToParentDir() {
		try {
			logger.info("变更工作目录到父目录");
			return ftpClient.changeToParentDirectory();
		} catch (IOException e) {
			logger.error("变更工作目录到父目录出错", e);
			return false;
		}
	}
	


	public Boolean downloadonefile(String remote, String local) {
		//System.out.println(ftpClient.isConnected());
		logger.info("开始下载.....");
		logger.info("远程文件：" + remote + " 本地文件存放路径：" + local);

		// 设置被动模式
		ftpClient.enterLocalPassiveMode();
		// 设置以二进制方式传输
		try {
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			logger.error("设置以二进制传输模式失败...", e);
		}

		// 检查FTP上是否存在文件
		FTPFile[] files = null;
		try {
			files = ftpClient.listFiles(new String(remote
					.getBytes("UTF-8"), "iso8859-1"));
			logger.info(files==null?"不存在":"存在"+files.length);
			logger.info("搜索出来文件名为：");
			for(FTPFile file:files){
				logger.info(file.getName());
			}
		} catch (IOException e) {
			logger.error("检查远程文件是否存在失败....", e);
		}
		if (files == null || files.length ==0) {
			logger.error("远程文件不存在");
			return false;
		}

		long ftp_file_size = files[0].getSize();
		logger.info("远程文件的大小:" + ftp_file_size);
		File local_file = new File(local);
		InputStream in = null;
		OutputStream out = null;
		
		//判断本地文件是否存在，如果存在判断是否需要断点续传
		if (local_file.exists()) {
			logger.info("本地文件存在，判断是否需要续传.....");
			long local_file_size = local_file.length();
			logger.info("本地文件大小：" + local_file_size);

			// 判断本地文件大小是否大于远程文件大小
			if (local_file_size >= ftp_file_size) {
				logger.info("本地文件大于等于远程文件，不需要续传");
				return true;
			}

			// 进行断点续传
			logger.info("开始断点续传.....");
			ftpClient.setRestartOffset(local_file_size);
			try {
				
				//根据文件名字得到输入留
				in = ftpClient.retrieveFileStream(new String(remote
						.getBytes("UTF-8"), "iso8859-1"));
				//建立输出流，设置成续传
				out = new FileOutputStream(local_file, true);
				byte[] b = new byte[1024];
				//已下载的大小
				long dowland_size = local_file_size;
				int flag = 0;
				long count;
				if (((ftp_file_size - dowland_size) % b.length) == 0) {
					count = ((ftp_file_size - dowland_size) / b.length);
				} else {
					count = ((ftp_file_size - dowland_size) / b.length) + 1;
				}

				while (true) {
					int num = in.read(b);
					//System.out.println(num);
					if (num == -1)
						break;
					out.write(b, 0, num);
					dowland_size += num;
					flag++;
					//打印下载进度
					if (flag % 1000 == 0) {
						logger.info("下载进度为:"
								+ (dowland_size * 100 / ftp_file_size) + "%");
					}
				}
				if (count == flag) {
					logger.info("下载进度为:100%");
				}
				in.close();
				out.close();
			} catch (UnsupportedEncodingException e) {
				logger.error("字符转换失败", e);
				return false;
			} catch (FileNotFoundException e) {
				logger.error("未找到文件", e);
				return false;		
			} catch (IOException e) {
				logger.error("出现io异常，请检查网络", e);
				return false;
			}

		} else {

			logger.info("本地文件不存在，此文件为新文件，开始下载.....");
			byte[] b = new byte[1024];

			try {
				//得到输入输出流
				in = ftpClient.retrieveFileStream(new String(remote
						.getBytes("UTF-8"), "iso8859-1"));
				out = new FileOutputStream(local);
				
				//已下载的大小
				long dowland_size = 0;
				int flag = 0;
				long count;
				if ((ftp_file_size % b.length) == 0) {
					count = (ftp_file_size / b.length);
				} else {
					count = (ftp_file_size / b.length) + 1;
				}
				while (true) {
					int num = in.read(b);
					if (num == -1)
						break;
					out.write(b, 0, num);
					dowland_size += num;
					flag++;
					//打印下载进度
					if (flag % 1000 == 0) {
						logger.info("下载进度为:"
								+ (dowland_size * 100 / ftp_file_size) + "%");
					}
					// ftp_file_size
				}
				if (count == flag) {
					logger.info("下载进度为:100%");
				}
				//关闭输入输出流
				in.close();
				out.close();
			} catch (UnsupportedEncodingException e) {
				logger.error("字符转换失败", e);
				return false;		
			} catch (IOException e) {
				logger.error("出现io异常请检查网络", e);
				return false;		
			}
		}
		return true;
	}
}
