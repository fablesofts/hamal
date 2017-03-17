package com.fable.hamal.ftp.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.log4j.Logger;
/**
 * 
 * @author Administrator
 *
 */
public class FTPClientTemplate {
	protected final Logger log = Logger.getLogger(getClass());
	private ThreadLocal<FTPClient> ftpClientThreadLocal = new ThreadLocal<FTPClient>();
	
	private String host;
	private int port;
	private String username;
	private String password;
	
	private boolean binaryTransfer = true;
	private boolean passiveMode = true;
	private String encoding = "UTF-8";
	//超时时间为30秒
	private int clientTimeout =1000*60;	
	public ThreadLocal<FTPClient> getFtpClientThreadLocal() {
		return ftpClientThreadLocal;
	}
	public void setFtpClientThreadLocal(ThreadLocal<FTPClient> ftpClientThreadLocal) {
		this.ftpClientThreadLocal = ftpClientThreadLocal;
	}
	public FTPClientTemplate(){}
	public FTPClientTemplate(String host, int port, String username,
			String password) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	
	public FTPClientTemplate(String host, int port, String username) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isBinaryTransfer() {
		return binaryTransfer;
	}
	public void setBinaryTransfer(boolean binaryTransfer) {
		this.binaryTransfer = binaryTransfer;
	}
	public boolean isPassiveMode() {
		return passiveMode;
	}
	public void setPassiveMode(boolean passiveMode) {
		this.passiveMode = passiveMode;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public int getClientTimeout() {
		return clientTimeout;
	}
	public void setClientTimeout(int clientTimeout) {
		this.clientTimeout = clientTimeout;
	}
	/**
	 * 返回FTPClient实例
	 * @return
	 * @throws FtpException 
	 */
	public FTPClient getFTPClient() throws FtpException {
		if (ftpClientThreadLocal.get() !=null && ftpClientThreadLocal.get().isConnected()) {
			return ftpClientThreadLocal.get();
		} else {
			FTPClient ftpClient = new FTPClient();
			ftpClient.setControlEncoding(encoding);	//设置字符集
			connect(ftpClient);	//连接到ftp服务器
			//设置passive模式
			if(passiveMode) {
				ftpClient.enterLocalPassiveMode();
			}
			setFileType(ftpClient);	//设置文件传输类型
			try {
				ftpClient.setSoTimeout(clientTimeout);
			} catch (SocketException e) {
				throw new FtpException("Set timeout error",e);
			}
			ftpClientThreadLocal.set(ftpClient);
			return ftpClient;
		}
	}
	/**
	 * 设置文件传输类型
	 * @param ftpClient
	 * @throws FtpException 
	 */
	private void setFileType(FTPClient ftpClient) throws FtpException {
		try {
			if(binaryTransfer) {
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			} else {
				ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
			}
		} catch (IOException e) {
			throw new FtpException("Could not set file type",e);
		}
	}
	/**
	 * 连接到ftp服务器
	 * @param ftpClient
	 * @throws FtpException 
	 */
	public boolean connect(FTPClient ftpClient) throws FtpException {
		try {
			ftpClient.connect(host, port);
			//连接后检测返回是否成功
			int reply = ftpClient.getReplyCode();
			if (FTPReply.isPositiveCompletion(reply)) {
				//登录到服务器
				if (ftpClient.login(username, password)) {
					setFileType(ftpClient);
					return true;
				}
			} else {
				ftpClient.disconnect();
				throw new FtpException("FTP server refused connection");
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();	//断开连接
				} catch (IOException e1) {
					throw new FtpException("could not connect to server", e);
				}	
			}
			throw new FtpException("could not connect to server", e);
		}
		return false;
	}
	/**
	 * 断开ftp连接
	 * @throws FtpException 
	 */
	public void disconnect() throws FtpException {
		FTPClient ftpClient = getFTPClient();
		try {
			ftpClient.logout();
			if (ftpClient.isConnected()) {
				ftpClient.disconnect();
				ftpClient = null;
			}
		} catch (IOException e) {
			throw new FtpException("could not connect to server",e);
		}
	}
	/**
	 * 在ftp
	 * @param pathname
	 * @return
	 * @throws FtpException 
	 */
	public boolean mkdir(String pathname) throws FtpException {
		return mkdir(pathname,null);
	}
	/**
	 * 在ftp服务器端创建目录
	 * @param pathname
	 * @param object
	 * @return
	 * @throws FtpException 
	 */
	public boolean mkdir(String pathname, String workingDirectory) throws FtpException {
		return mkdir(pathname,workingDirectory,true);
	}
	/**
	 * 在ftp服务器端创建目录
	 * @param pathname
	 * @param workingDirectory
	 * @param b
	 * @return
	 * @throws FtpException 
	 */
	public boolean mkdir(String pathname, String workingDirectory, boolean autoClose) throws FtpException {
		try {
			getFTPClient().changeWorkingDirectory(workingDirectory);
			return getFTPClient().makeDirectory(pathname);
		} catch (IOException e) {
			throw new FtpException("Could not mkdir",e);
		} finally {
			if (autoClose) {
				disconnect();	//断开连接
			}
		}
	}
	/**
	 * 上传一个本地文件到远程指定文件
	 * @param remoteAbsoluteFile 
	 * @param localAbsoluteFile
	 * @return
	 * @throws FtpException 
	 */
	public boolean put(String remoteAbsoluteFile,String localAbsoluteFile) throws FtpException {
		return put(remoteAbsoluteFile,localAbsoluteFile,false);
	}
	/**
	 * 上传远程文件并控制是否自动关闭
	 * @param remoteAbsoluteFile
	 * @param localAbsoluteFile
	 * @param autoClos
	 * @return
	 * @throws FtpException 
	 */
	private boolean put(String remoteAbsoluteFile, String localAbsoluteFile,
			boolean autoClos) throws FtpException {
		InputStream input = null;
		try {
			input = new FileInputStream(localAbsoluteFile);
			getFTPClient().storeFile(remoteAbsoluteFile,input);
			log.debug("put "+localAbsoluteFile);
		} catch (FileNotFoundException e) {
			throw new FtpException("local file not found",e);
		} catch (IOException e) {
			throw new FtpException("counld not put file to server",e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				throw new FtpException("counld not put file to server",e); 
			}
			if (autoClos) {
				disconnect();	//断开连接
			}
		}
		return false;
	}
	/**
	 * 下载远程文件到本地的指定文件
	 * @param remoteAbsoluteFile
	 * @param localAbsoluteFile
	 * @return
	 * @throws FtpException 
	 */
	public boolean get(String remoteAbsoluteFile, String localAbsoluteFile) throws FtpException {
		return get(remoteAbsoluteFile,localAbsoluteFile,true);
	}
	/**
	 * 下载远程文件到本地并控制是否自动关闭
	 * @param remoteAbsoluteFile
	 * @param localAbsoluteFile
	 * @param b
	 * @return
	 * @throws FtpException 
	 */
	public boolean get(String remoteAbsoluteFile, String localAbsoluteFile,
			boolean autoClose) throws FtpException {
		OutputStream output = null;
		try {
			output = new FileOutputStream(localAbsoluteFile);
			return get(remoteAbsoluteFile,output,autoClose);
		} catch (FileNotFoundException e) {
			throw new FtpException("local file not found",e);
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				throw new FtpException("Couldn't close FileOutputStream",e);
			}
		}
	}
	/**
	 * 
	 * @param remoteAbsoluteFile
	 * @param output
	 * @param autoClose
	 * @return
	 * @throws FtpException 
	 */
	public boolean get(String remoteAbsoluteFile, OutputStream output,
			boolean autoClose) throws FtpException {
		try {
			FTPClient ftpClient = getFTPClient();
			//处理传输
			return ftpClient.retrieveFile(remoteAbsoluteFile,output);
		} catch (IOException e) {
			throw new FtpException("Couldn't close FileOutputStream",e);
		} finally {
			if (autoClose) {
				disconnect();	//关闭连接
			}
		}
	}
	/**
	 * 从ftp服务器上删除一个文件
	 * @param delFile
	 * @return
	 * @throws FtpException 
	 */
	public boolean delete(String delFile) throws FtpException {
		return delete(delFile,true);
	}
	/**
	 * 从ftp服务器上删除一个文件
	 * @param delFile
	 * @param autoClose
	 * @return
	 * @throws FtpException 
	 */
	private boolean delete(String delFile, boolean autoClose) throws FtpException {
		try {
			getFTPClient().deleteFile(delFile);
			return true;
		} catch (IOException e) {
			throw new FtpException("Couldn't delete file from server.", e); 
		} finally {
			if (autoClose) {
				disconnect();	//关闭连接
			}
		}
	}
	/**
	 * 批量删除
	 * @param delFile
	 * @return
	 * @throws FtpException 
	 */
	public boolean delete(String[] delFile) throws FtpException {
		return delete(delFile,true);
	}
	/**
	 * 批量删除，是否关闭当前连接
	 * @param delFile
	 * @param autoClose
	 * @return
	 * @throws FtpException 
	 */
	private boolean delete(String[] delFiles, boolean autoClose) throws FtpException {
		try {
			FTPClient ftpClient = getFTPClient();
			for (String s : delFiles) {
				ftpClient.deleteFile(s);
			}
			return true;
		} catch (IOException e) {
			throw new FtpException("Couldn't delete file from server",e);
		} finally {
			if (autoClose) {
				disconnect();
			}
		}
	}
	/**
	 * 列出远程默认目录下所有的文件
	 * @return
	 * @throws FtpException 
	 */
	public String[] listNames() throws FtpException {
		return listNames(null,true);
	}
	/**
	 * 
	 * @param object
	 * @param b
	 * @return
	 * @throws FtpException 
	 */
	private String[] listNames(String remotePath, boolean autoClose) throws FtpException {
		try {
			String[] listNames = getFTPClient().listNames(remotePath);
			return listNames;
		} catch (IOException e) {
			throw new FtpException("list remote files exception",e);
		} finally {
			if (autoClose) {
				disconnect();
			}
		}
	}
	/**
	 * 列出其下所有的文件
	 * @param pathname
	 * @param b
	 * @return
	 * @throws FtpException 
	 */
	private FTPFile[] listFiles(String pathname, boolean b) throws FtpException {
		try {
			return getFTPClient().listFiles(pathname);
		} catch (Exception e) {
			throw new FtpException("list remote files exception",e);
		} 
	}
	/**
	 * 删除文件夹及其子文件
	 * @param pathname
	 * @return
	 * @throws FtpException
	 */
	public boolean removeAll(String pathname) throws FtpException {
		boolean flag = false;
		  try {
			   FTPFile[] files = this.listFiles(pathname, false);
			   if (files.length>0) {
				for (FTPFile f : files) {
					String str = new String(f.getName().getBytes("GBK"),
							"UTF-8");
					f.setName(str);
					if (f.isDirectory()) {
						this.removeAll(pathname + "/" + f.getName());
						getFTPClient().removeDirectory(pathname);
					}
					if (f.isFile()) {
						delete(pathname + "/" + f.getName(),false);
					}
				}
			} else{
				getFTPClient().removeDirectory(pathname);
			}
			flag = true;
		  } catch (IOException e) {
			   e.printStackTrace();
			   flag = false;
		  } finally{
			  try {
				getFTPClient().removeDirectory(pathname);
			} catch (IOException e) {
				e.printStackTrace();
			}
		  }
		  return flag;
	}
	/**
	 * 获取当前工作目录
	 * @return
	 * @throws FtpException 
	 * @throws IOException 
	 */
	public String getWorkingDirectory() throws IOException, FtpException {
		return getFTPClient().printWorkingDirectory();
	}
	
}
