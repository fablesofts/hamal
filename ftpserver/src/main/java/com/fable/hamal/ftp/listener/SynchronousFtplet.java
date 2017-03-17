package com.fable.hamal.ftp.listener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import com.fable.hamal.ftp.config.dao.FtpMappingDao;
import com.fable.hamal.ftp.config.dao.FtpUserDao;
import com.fable.hamal.ftp.util.GZIPUtil;
import com.fable.hamal.ftp.util.HamalPropertyConfigurer;
import com.fable.hamal.ftp.util.PathReader;
/**
 * 自定义文件监听事件
 * @author 邱爽
 *
 */
public class SynchronousFtplet extends DefaultFtplet {
//	private static Logger logger = LoggerFactory.getLogger(SynchronousFtplet.class);
//	
//	/**当前登录的用户*/
//	private BaseUser currentLoginUser;
//	/**内交换目录*/
//	private String correspondingAddress;
//	/**外交换目录*/
//	private String currentFtpAddress;
//	/**是否是根目录*/
//	private boolean isDirectory;
//	/**判断外交换上是否有临时文件夹*/
//	private boolean hasTempDirectory;
//	/**是否允许删除*/
//	private boolean enableDelete = false;
//	/**数据库查询类*/
//	private FtpMappingDao ftpMappingDao;
//	/**用户查询类 */
//	private FtpUserDao ftpUserDao;
//	/**连接外交换的FTP客户端类*/
//	private FtpHelper correspondingClient;
//	private FtpHelper currentLoginClient;
//	/**要打包的文件名 */
//	private String tarFileName;
//	/**远程文件名数组:用于删除*/
//	private String[] filenames;
//	/**本地文件名数组：用于打包*/
//	private String[] localFileNames;
//	/**文件打包数量*/
//	private Integer compressCount;
//	/**迭代计数 */
//	private Integer compressIndex = 0;
//	/**临时文件夹名*/
//	private String tempPath;
//	private String compressPath; 
//	/**字符编码集*/
//	private String encoding;
//
//	public SynchronousFtplet() {
//		//文件打包数量
//		compressCount =Integer.valueOf(HamalPropertyConfigurer.getHamalProperty("ftp.server.compressCount"));
//		compressPath = HamalPropertyConfigurer.getHamalProperty("ftp.compress");
//		tempPath = HamalPropertyConfigurer.getHamalProperty("ftp.temp");
//		encoding = System.getProperty("file.encoding");
//		filenames = new String[compressCount];
//		localFileNames = new String[compressCount];
//	}
//
//	/**监听上传事件*/
//	@Override
//	public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
//		if (enableDelete) {
//			deleteFiles();
//			//删除后传包
//			filenames = new String[compressCount];
//			enableDelete = false;
//		}
//		//如果和对应客户端url相同，则跳过
//		String clientIp = session.getAttribute("clientIp").toString();
//		if (clientIp.equals(correspondingClient.getHostname())) {
//			return FtpletResult.SKIP;
//		}
//		String fileName = request.getArgument();
//		if (fileName == null) {
//			return FtpletResult.SKIP;
//		}
//		
//		FtpFile file = null;
//		try {
//			file = session.getFileSystemView().getFile(fileName);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("Exception getting file object,{}", e.getMessage());
//		}
//		String absoluteFile = file.getAbsolutePath();
//		int lastSlash = absoluteFile.lastIndexOf("/");
//		String currentWdr = lastSlash == 0 ? "/" : absoluteFile.substring(1, lastSlash);
//		InputStream inputStream = null;
//		//用户根目录(apache ftpserver数据库配置的用户根目录是操作系统的目录，不是相对应ftp的"/")目录
//		String userHomeDirectory = null;
//		String remotePath = null;
//		try {
//			userHomeDirectory = ftpUserDao.getPathByUser(currentLoginUser.getName());
//			//数据库查询出来的
//			userHomeDirectory = userHomeDirectory.substring(1, userHomeDirectory.length() -1);
//			StringBuffer temp = new StringBuffer(userHomeDirectory);
//			temp.append(File.separator).append(currentWdr).append(File.separator).append(fileName);
//			String localFileName = temp.toString();
//			localFileName = new String(localFileName.getBytes(encoding), "UTF-8");
//			fileName = fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
//			String encoding = System.getProperty("file.encoding");  
//			fileName = new String(fileName.getBytes(encoding),"UTF-8");  
//			remotePath = ftpMappingDao.getAddressByUser(correspondingClient.getUsername());	
//			remotePath = remotePath.substring(1, remotePath.length()-1);
//			correspondingClient.setHomepath(File.separator);
//			//将文件添加到集合中
//			filenames[compressIndex] = absoluteFile;
//			localFileNames[compressIndex] = localFileName;
//			if (compressIndex <compressCount) {
//				compressIndex ++;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		if(compressIndex == compressCount) {
//			try {
//				String name = compressFile(userHomeDirectory,tarFileName);
//				String tarName = name.substring(name.lastIndexOf(File.separator),name.length());
//				String remote = remotePath + tarName;
////				correspondingClient.upload(remote,name);
//			} catch (Exception e) {
//				e.printStackTrace();
//				logger.error("compress files error,the detail{}",e.getMessage());
//			}
//			
//			compressIndex = 0;
//			localFileNames = new String[compressCount];
//			enableDelete = true;
//		}
//		return super.onUploadEnd(session, request);
//	}
//	
//	
//	/**删除原来文件*/
//	private void deleteFiles() throws IOException {
//		try {
//			currentLoginClient.delete(filenames,false);
//		} catch (IOException e) {
//			throw new IOException(e);
//		} catch (Exception e) {
//			if (logger.isDebugEnabled()) {
//				e.printStackTrace();
//			}
//			e.printStackTrace();
//			logger.error("delete source files error,the detail:{}",e.getMessage());
//		}
//	}
//
//	/**
//	 * 打包
//	 * @param workingDirectory 打包文件所在的根目录
//	 * @param tarFileName 打包文件开头的名称
//	 */
//	private String compressFile(String workingDirectory,String tarFileName) {
//		//打包的目标文件位置，位于用户根目录-compress
//		StringBuffer sbtarget = new StringBuffer();
//		sbtarget.append(workingDirectory);
//		sbtarget.append(File.separator + compressPath);
//		sbtarget.append(File.separator + tarFileName+System.currentTimeMillis() + ".zip");
//		try {
//			GZIPUtil.compress(localFileNames, sbtarget.toString());
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		return sbtarget.toString();
//	}
//
//	@Override
//	public void init(FtpletContext ftpletContext) throws FtpException {
//		logger.info("run server success");
//	}
// 
//	//客户端连接后通知事件。是FTPServer第一个触发的事件。如果返回SKIP,IP的约束检查,connection limit检查就不会触发，并且服务器不会发送欢迎语句
//	@Override
//	public FtpletResult onConnect(FtpSession session) throws FtpException, IOException {
//		//在这里区分
//		return super.onConnect(session);
//	}
//	
//	//客户端连接断开后触发的事件，是服务器最后触发的事件。不论返回什么，客户端连接都会关闭
//	@Override
//	public FtpletResult onDisconnect(FtpSession session) throws FtpException,
//			IOException {
//		SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
//		String a1=dateformat1.format(new Date());
//		logger.info("session closing ,{}",a1);
//		return super.onDisconnect(session);
//	}
//
//	@Override
//	public FtpletResult onLogin(FtpSession session, FtpRequest request)
//			throws FtpException, IOException {
//		try {
//			InetSocketAddress clientAddress = session.getClientAddress();
//			MessageFormat format = new MessageFormat("clienthostname:{0},clientport:{1}");
//			logger.info(format.format(new String [] {clientAddress.getHostName(),String.valueOf(clientAddress.getPort())}));
//			InetAddress clientIp = clientAddress.getAddress();
//			session.setAttribute("clientIp",clientIp.getHostAddress());
//			//得到当前用户所在的文件夹
//			currentLoginUser = (BaseUser) session.getUser();
//			//查询当前用户对应的内角换目录
//			correspondingAddress = ftpMappingDao.getInnerAddressByUser(currentLoginUser.getName());
//			//初始化外交换客户端
//			if (this.correspondingClient == null) {
//				//初始化对应的内交换用户
//				correspondingClient = ftpMappingDao.initClientInfo(currentLoginUser.getName());
//				String pwd = ftpUserDao.getPwdByUserName(correspondingClient.getUsername());
//				correspondingClient.setPwd(pwd.substring(1, pwd.length()-1));
//			}
//			//初始化当前登录客户端
//			if (this.currentLoginClient == null) {
//				//初始化当前登录用户的客户端
//				currentLoginClient = ftpMappingDao.initCurrentClient(currentLoginUser.getName());
//				String currentPwd = ftpUserDao.getPwdByUserName(currentLoginClient.getUsername());
//				currentLoginClient.setPwd(currentPwd.substring(1, currentPwd.length()-1));
//				//创建打包文件夹
////				if(!currentLoginClient.isExistsDirectory(compressPath, "/")) {
////				currentLoginClient.mkdir(compressPath,currentLoginClient.getWorkingDirectory(),false);
////				}
//				
//			}
//			//在对应外交换上建立临时文件夹temp:
////			if (!hasTempDirectory) {
////					FtpFile workingDir= session.getFileSystemView().getWorkingDirectory();
////					String workingDirectory = workingDir.getAbsolutePath();
////					if (!correspondingClient.isExistsDirectory(tempPath, "/")) {
////						correspondingClient.mkdir(tempPath,workingDirectory,false);
////						hasTempDirectory = true;
////					}
////			}
//			//
//			if (tarFileName == null) {
//				tarFileName = HamalPropertyConfigurer.getHamalProperty("ftp.server.tarFile");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("init ftp client info error, the detail{}",e.getMessage());
//		}
//		return super.onLogin(session, request);
//	}
//	
//
////	/**
////	 * 删除文件开始
////	 */
////	@Override
////	public FtpletResult onDeleteStart(FtpSession session, FtpRequest request)
////			throws FtpException, IOException {
////		//1.建立外网连接
////		String fileName = request.getArgument();
////		if (fileName == null) {
////			return FtpletResult.SKIP;
////		}
////		FtpFile file = null;
////		try {
////			file = session.getFileSystemView().getFile(fileName);
////			isDirectory = file.isDirectory();	//是文件夹，就不监听onDeleteEnd
////		} catch (Exception e) {
////			logger.error("Exception getting file object:{}", e);
////		}
////		
////		return super.onDeleteStart(session, request);
////	}
//	
//	/**
//	 * 
//	 * 监听文件删除事件
//	 */
////	@Override
////	public FtpletResult onDeleteEnd(FtpSession session, FtpRequest request)
////			throws FtpException, IOException {
////		boolean isConnect=false;
////		String fileName = request.getArgument();
////		if(isDirectory){
////			//是文件夹，删除
////			try {
////				correspondingClient.removeAll(fileName);
////			} catch (Exception e) {
////				e.printStackTrace();
////			}
////			return FtpletResult.SKIP;
////		}else{
////			//2。得到文件名
////			if (fileName == null) {
////				return FtpletResult.SKIP;
////			}
////			isConnect = isConnect();
////			if(isConnect) {
////				//删除
////				correspondingClient.deleteFile(fileName);
////			}
////		}
////		return super.onDeleteEnd(session, request);
////	}
//
//	/**
//	 * 监听新文件夹的创建
//	 */
////	@Override
////	public FtpletResult onMkdirEnd(FtpSession session, FtpRequest request)
////			throws FtpException, IOException {
////		//得到新文件夹名字
////		String newFilePath =request.getArgument();
////			//在当前工作目录下建立该文件夹
////			String currentPath = correspondingClient.getWorkingDirectory();
////			correspondingClient.mkdir(newFilePath, currentPath, true);
////		return FtpletResult.SKIP;
////	}
//
//	/**
//	 * 监听删除文件夹事件:递归将相应目录下的文件及文件夹删除
//	 */
////	@Override
////	public FtpletResult onRmdirEnd(FtpSession session, FtpRequest request)
////			throws FtpException, IOException {
////		String remoteFile = request.getArgument();
////		try {
////			correspondingClient.removeAll(remoteFile);
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////		return super.onRmdirEnd(session, request);
////	}
//	
//	public SynchronousFtplet(FtpMappingDao ftpMappingDao) {
//		this.ftpMappingDao = ftpMappingDao;
//	}
//	
//	public FtpMappingDao getFtpMappingDao() {
//		return ftpMappingDao;
//	}
//
//	public void setFtpMappingDao(FtpMappingDao ftpMappingDao) {
//		this.ftpMappingDao = ftpMappingDao;
//	}
//	
//	public FtpUserDao getFtpUserDao() {
//		return ftpUserDao;
//	}
//
//	public void setFtpUserDao(FtpUserDao ftpUserDao) {
//		this.ftpUserDao = ftpUserDao;
//	}
}