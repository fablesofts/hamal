package com.fable.hamal.ftp.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fable.hamal.ftp.util.GZIPUtil;
/**
 * FTP客户端类
 * @author 邱爽
 *
 */
public class ContinueFtp1 {

	private static final Logger log = LoggerFactory.getLogger(ContinueFtp1.class);

	// 枚举类下载相关：DownloadStatus代码
	public enum DownloadStatus {
		Remote_File_Noexist, // 远程文件不存在
		Local_Bigger_Remote, // 本地文件大于远程文件
		Download_From_Break_Success, // 断点下载文件成功
		Download_From_Break_Failed, // 断点下载文件失败
		Download_New_Success, // 全新下载文件成功
		Download_New_Failed, // 全新下载文件失败
	}
	//上传相关
	public enum UploadStatus {
		Create_Directory_Fail, // 远程服务器相应目录创建失败
		Create_Directory_Success, // 远程服务器创建目录成功
		Upload_New_File_Success, // 上传新文件成功
		Upload_New_File_Failed, // 上传新文件失败
		File_Exits, // 文件已经存在
		Remote_Bigger_Local, // 远程文件大于本地文件
		Upload_From_Break_Success, // 断点续传成功
		Upload_From_Break_Failed, // 断点续传失败
		Delete_Remote_Faild; // 删除远程文件失败
	}

	private FTPClient ftpClient = null;
	private String ftpUrl, username, pwd,homepath,ftpPort, local, remote;
	/** 本地字符编码 */
	public static String LOCAL_CHARSET = "GBK";
	 
	// FTP协议里面，规定文件名编码为iso-8859-1
	public static String SERVER_CHARSET = "ISO-8859-1";
	//超时时间为30秒
	private int clientTimeout =1000*60;	
	 
	public FTPClient getFtpClient() {
		return ftpClient;
	}
	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}
	public String getFtpURL() {
		return ftpUrl;
	}
	public void setFtpURL(String ftpURL) {
		this.ftpUrl = ftpURL;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getHomepath() {
		return homepath;
	}
	public void setHomepath(String homepath) {
		this.homepath = homepath;
	}
	public String getFtpport() {
		return ftpPort;
	}
	public void setFtpport(String ftpport) {
		this.ftpPort = ftpport;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getRemote() {
		return remote;
	}
	public void setRemote(String remote) {
		this.remote = remote;
	}
	public ContinueFtp1() {
		super();
	}
	
	public ContinueFtp1(String ftpURL, String username,
			String pwd, String ftpport, String file1, String file2,String homepath) {
		super();
		this.ftpUrl = ftpURL;
		this.username = username;
		this.pwd = pwd;
		this.ftpPort = ftpport;
		this.local = file1;
		this.remote = file2;
		this.homepath=homepath;
	}
	public ContinueFtp1 (String ftpURL, String username,
			String pwd, String ftpport){
		this.ftpUrl = ftpURL;
		this.username = username;
		this.pwd = pwd;
		this.ftpPort = ftpport;
	}
	/**
	 * 判断某工作目录下是否存在某文件夹
	 * @param fileName 某文件名
	 * @param workingDirctory
	 * @return
	 */
	public boolean isExistsDirectory (String fileName,String workingDirctory) {
		boolean isExists = false;
		try {
			changeDirectory(fileName);
			FTPFile[] filenames = ftpClient.listFiles(fileName);
			for (int i =0; i<filenames.length; i++) {
				if(fileName.equals(filenames[i].getName())) {
					isExists = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isExists;
	}
	/**
	 * 删除文件夹
	 * @param pathname
	 * @return
	 * @throws RuntimeException 
	 */
	public boolean removeAll(String pathname) throws RuntimeException {
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
						ftpClient.removeDirectory(pathname);
					}
					if (f.isFile()) {
						this.deleteFile(pathname + "/" + f.getName());
					}
				}
			} else{
				ftpClient.removeDirectory(pathname);
			}
			flag = true;
		  } catch (IOException e) {
			   throw new RuntimeException("delete directory fail,",e);
		  } finally{
			  try {
				ftpClient.removeDirectory(pathname);
			} catch (IOException e) {
				 throw new RuntimeException("delete directory fail,",e);
			}
		  }
		  return flag;
	}
	/**
	 * 列出其下所有的文件
	 * @param pathname
	 * @param b
	 * @return
	 * @throws RuntimeException 
	 */
	private FTPFile[] listFiles(String pathname, boolean b) throws RuntimeException {
		try {
			return ftpClient.listFiles(pathname);
		} catch (IOException e) {
			throw new RuntimeException("server list files fail,",e);
		}
	}
	/**
	 * 递归删除指定文件夹及其下属文件
	 * @throws IOException 
	 * @throws RuntimeException 
	 */
	public boolean deleteFileDirectory(String pathName) throws IOException, RuntimeException {
		ftpClient.setControlEncoding("UTF-8"); //设置字符集  
		try {
			changeDirectory(pathName);
			FTPFile[] files = ftpClient.listFiles();
			for (FTPFile f :files) {
				if(f.isDirectory()) {
					this.deleteFileDirectory(pathName);
					deleteFileDirectory(f.getName());
				}
				if(f.isFile()) {
					ftpClient.deleteFile(f.getName());
				}
				ftpClient.changeToParentDirectory();
				ftpClient.removeDirectory(pathName);
			}
			return true;
		} catch (Exception e) {
			throw new RuntimeException("delete file directory fail ",e);
		} finally {
			disconnect();
		}
	}
	/**
	 * 在FTP远程机，移动文件
	 * @param srcPath	源数据
	 * @param srcFileName	源文件名
	 * @param targetPath	目标路路径
	 * @param targetFileName	目标文件名
	 * @throws IOException 
	 */
	public void moveFile(String srcPath,String srcFileName,String targetPath,String targetFileName) throws IOException {
		ftpClient.changeWorkingDirectory(srcPath);
		if (!targetPath.endsWith("/")) {
			targetPath = targetPath + "/";
		}
		try {
			ftpClient.rename(srcPath + "/"+ srcFileName,targetPath + targetFileName);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	 * 列出文件夹下所有文件名
	 * @param remotePath
	 * @param autoClose
	 * @return
	 * @throws IOException
	 * @throws RuntimeException 
	 */
	public String[]	 listNames(String remotePath, boolean autoClose) throws IOException, RuntimeException {
		try {
			String[] listNames =ftpClient.listNames("/"+remotePath);
			return listNames;
		} catch (Exception e) {
			throw new RuntimeException("list names fail ",e);
		} finally {
			if(autoClose) {
				disconnect();
			}
		}
	}
	/**
	 * 重命名
	 * @param oldName
	 * @param newName
	 * @throws RuntimeException 
	 */
	public void rename(String oldName,String newName) throws RuntimeException {
		try {
			ftpClient.rename(oldName,newName);
		} catch (IOException e) {
			throw new RuntimeException("rename file fail",e);
		}
	}
	/**
	 * 连接到FTP服务器
	 * 
	 * @param hostname
	 *            主机名
	 * @param port
	 *            端口
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return 是否连接成功
	 * @throws Exception 
	 * @throws IOException
	 */
	private boolean connect(String hostname, int port, String username,
			String password) throws Exception{
		try {
			ftpClient = new FTPClient();
			ftpClient.configure(getFTPClientConfig());
			ftpClient.connect(hostname, port);
			ftpClient.login(username, password);
			//设置文件类型，默认是ASCII
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			//设置被动模式
			ftpClient.enterLocalPassiveMode();
			ftpClient.setDataTimeout(clientTimeout);
			//
			ftpClient.setControlEncoding("UTF-8");
			//响应信息
			int replyCode = ftpClient.getReplyCode();
			if ((!FTPReply.isPositiveCompletion(replyCode))) {
				//关闭FTP连接
				closeFTPClient();
				//释放空间
				ftpClient = null;
				throw new Exception("![Server:" + hostname + "、"  
			            + "User:" + username + "、" + "Password:" + password);  
			} else {
				return true;
			}
		} catch (Exception e) {
			log.error("login error , the detail:{}",e.getMessage());
			ftpClient.disconnect();
			ftpClient = null;
			throw e;
		} 
	}

	/**
	 * 配置FTP连接参数
	 * @return
	 */
	public FTPClientConfig getFTPClientConfig() {
		String systemKey = FTPClientConfig.SYST_NT;  
		String serverLanguageCode = "zh";  
		FTPClientConfig conf = new FTPClientConfig(systemKey);  
		conf.setServerLanguageCode(serverLanguageCode);  
		conf.setDefaultDateFormatStr("yyyy-MM-dd");  
		return conf;  
	}
    /** 
     * 关闭FTP连接 
     *  
     * @param ftp 
     * @throws Exception 
     */  
    public void closeFTPClient(FTPClient ftp) throws Exception {  
        try {  
            if (ftp.isConnected())  
                ftp.disconnect();  
        } catch (Exception e) {  
            throw new Exception("close connection error");  
        }  
    }  
  
    /** 
     * 关闭FTP连接 
     *  
     * @throws Exception 
     */  
    public void closeFTPClient() throws Exception {  
        this.closeFTPClient(this.ftpClient);  
    }  
	  /** 

     * 从FTP服务器上下载文件 

     * @param remote 远程文件路径 

     * @param local 本地文件路径 

     * @return 是否成功 

     * @throws IOException 

     */   

    public boolean download(String remote,String local) throws IOException{   

        ftpClient.enterLocalPassiveMode();   

        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);   

        boolean result;   

        File f = new File(local);   

        FTPFile[] files = ftpClient.listFiles(remote);   

        if(files.length != 1){   

            System.out.println("远程文件不唯一");   

            return false;   

        }   

        long lRemoteSize = files[0].getSize();   
        //如果该文件存在，则将会覆盖原文件，条件是大小要大于原文件
        if(f.exists()){   

            OutputStream out = new FileOutputStream(f,true);   

            System.out.println("本地文件大小为:"+f.length());   

            if(f.length() >= lRemoteSize){   
            	log.error("local file bigger than remote file,download stop");
                return false;   

            }   

            ftpClient.setRestartOffset(f.length());   

            result = ftpClient.retrieveFile(remote, out);   

            out.close();   

        }else {   

            OutputStream out = new FileOutputStream(f);   

            result = ftpClient.retrieveFile(remote, out);   

            out.close();   

        }   

        return result;   

    }  
    /**
     * 在指定目录下创建文件夹 	
     * @param pathname	要创建的文件夹名称
     * @param workingDirectory 指定的目录
     * @param autoClose 创建后是否自动关闭连接
     * @return
     * @throws RuntimeException 
     * @throws IOException 
     */
    public boolean mkdir(String pathname, String workingDirectory, boolean autoClose) throws RuntimeException, IOException {
    	//下到指定的目录下
    	try {
			this.ftpClient.changeWorkingDirectory(workingDirectory);
			return this.ftpClient.makeDirectory(pathname);
		} catch (IOException e) {
			throw new RuntimeException("make directory fail",e);
		} finally {
			if (autoClose) {
				disconnect();
			}
		}
    }
    /**
     * 改变工作目录
     * @param remoteFoldPath 工作目录名称
     * @throws Exception
     */
    public void changeDirectory(String remoteFoldPath) throws Exception {
    	if(remoteFoldPath != null) {
    		boolean flag = ftpClient.changeWorkingDirectory(remoteFoldPath);
    		if (!flag) {
    			ftpClient.makeDirectory(remoteFoldPath);
    			ftpClient.changeWorkingDirectory(remoteFoldPath);
    		}
    	}
    }
    /**
     * 批量上传
     * @param files
     * @throws RuntimeException 
     */
    public void ftp(ArrayList<File> files) throws RuntimeException {
        try {
            this.connect(this.ftpUrl,Integer.valueOf(this.ftpPort), username,this.pwd);
    } catch (Exception e) {
        throw new RuntimeException("Unexpected exception while transfering order files via FTP", e);
    } 
    finally {
        if (this.ftpClient.isConnected()) {
            try {
            	this.ftpClient.disconnect();
            } catch (IOException ioe) {
            	throw new RuntimeException("server disconnect error", ioe);
            }
        }
    }
    
    }
	/**
	 * 创建远程目录
	 * @param remote
	 * @return
	 * @throws RuntimeException 
	 */
	public UploadStatus createDirecroty(String remote) throws RuntimeException {
		UploadStatus status = UploadStatus.Create_Directory_Success;
		try {
			String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
			if (!directory.equalsIgnoreCase("/")
					&& !ftpClient.changeWorkingDirectory(new String(directory
							.getBytes(),"ISO-8859-1"))) {
				// 如果远程目录不存在，则递归创建远程服务器目录
				int start = 0;
				int end = 0;
				if (directory.startsWith("/")) {
					start = 1;
				} else {
					start = 0;
				}
				end = directory.indexOf("/", start);
				while (true) {
					String subDirectory = new String(remote.substring(start,
							end).getBytes("GBK"), "UTF-8");
					if (!ftpClient.changeWorkingDirectory(subDirectory)) {
						if (ftpClient.makeDirectory(subDirectory)) {
							ftpClient.changeWorkingDirectory(subDirectory);
						} else {
							return UploadStatus.Create_Directory_Fail;
						}
					}

					start = end + 1;
					end = directory.indexOf("/", start);

					if (end <= start) {
						break;
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("create ftp file fail", e);
		}
		return status;
	}
	/**
	 * 获取当前路径
	 * •PWD - print working directory
	 * 
	 * @return
	 * @throws RuntimeException 
	 */
	public String getWorkingDirectory() throws RuntimeException {
		try {
			return ftpClient.printWorkingDirectory();
		} catch (IOException e) {
			throw new RuntimeException("get working directory fail", e);
		} 
	}
	/**
	 * 删除文件		
	 * @param fileName
	 * @throws RuntimeException 
	 */
	public void deleteFile(String fileName) throws RuntimeException {
		try {
			boolean exist = ftpClient.deleteFile(fileName);
			if (exist) {
				log.info("File :" + fileName+ " deleted");
			} else{
				//log.error("File :" + fileName + "doesn't exist");
			}
		} catch (FTPConnectionClosedException e) {
			throw new RuntimeException("the server closing", e);
		} catch (IOException e) {
			throw new RuntimeException("the server closing", e);
		}
		
	}
	 /** 

     * 上传文件到FTP服务器，支持断点续传 

     * @param local 本地文件名称，绝对路径 

     * @param remote 远程文件路径，
     * 使用/home/directory1/subdirectory/file.ext 按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构 

     * @return 上传结果 

     * @throws IOException 

     */   

    public UploadStatus upload(String remote,String local) throws IOException{   

        //设置PassiveMode传输   

        ftpClient.enterLocalPassiveMode();   

        //设置以二进制流的方式传输   

        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);   

        UploadStatus result;   

        //对远程目录的处理   

        String remoteFileName = remote;   

        if(remote.contains("/")){   

            remoteFileName = remote.substring(remote.lastIndexOf("/")+1);   //得到纯文件名，例如bb.txt

            String directory = remote.substring(0,remote.lastIndexOf("/")+1);   

            if(!directory.equalsIgnoreCase("/")&&!ftpClient.changeWorkingDirectory(directory)){   

                //如果远程目录不存在，则递归创建远程服务器目录   

                int start=0;   

                int end = 0;   

                if(directory.startsWith("/")){   

                    start = 1;   

                }else{   

                    start = 0;   

                }   

                end = directory.indexOf("/",start);   

                while(true){   

                    String subDirectory = remote.substring(start,end);   

                    if(!ftpClient.changeWorkingDirectory(subDirectory)){   

                        if(ftpClient.makeDirectory(subDirectory)){   

                            ftpClient.changeWorkingDirectory(subDirectory);   

                        }else {   

                            log.error("create directory error");

                            return UploadStatus.Create_Directory_Fail;   

                        }   

                    }   

                    start = end + 1;   

                    end = directory.indexOf("/",start);   

                    //检查所有目录是否创建完毕   

                    if(end <= start){   

                        break;   

                    }   

                }   

            }   

        }   
          
        //检查远程是否存在文件   

        FTPFile[] files = ftpClient.listFiles(remoteFileName);   

        if(files.length == 1){   

            long remoteSize = files[0].getSize();   

            File f = new File(local);   

            long localSize = f.length();   

            if(remoteSize==localSize){   

                return UploadStatus.File_Exits;   

            }else if(remoteSize > localSize){   

                return UploadStatus.Remote_Bigger_Local;   

            }   

              
            //当文件数量多于1个时，采用断点上传
            //尝试移动文件内读取指针,实现断点续传   

            InputStream is = new FileInputStream(f);   

            if(is.skip(remoteSize)==remoteSize){   

                ftpClient.setRestartOffset(remoteSize);   

                if(ftpClient.storeFile(remote, is)){   

                    return UploadStatus.Upload_From_Break_Success;   

                }   

            }   

              

            //如果断点续传没有成功，则删除服务器上文件，重新上传   

            if(!ftpClient.deleteFile(remoteFileName)){   

                return UploadStatus.Delete_Remote_Faild;   

            }   

            is = new FileInputStream(f);   

            if(ftpClient.storeFile(remote, is)){       

                result = UploadStatus.Upload_New_File_Success;   

            }else{   

                result = UploadStatus.Upload_New_File_Failed;   

            }   

            is.close();   

        }else {   

            InputStream is = new FileInputStream(local);   

            if(ftpClient.storeFile(remoteFileName, is)){   

                result = UploadStatus.Upload_New_File_Success;   

            }else{   

                result = UploadStatus.Upload_New_File_Failed;   

            }   

            is.close();   

        }   

        return result;   

    }   


    
    
    
	/** */
	/**
	 * 断开与远程服务器的连接
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		if (ftpClient.isConnected()) {
			ftpClient.disconnect();
		}
	}

	/**
	 * 批量删除
	 * @param delFile
	 * @return
	 * @throws Exception 
	 */
	public boolean delete(String[] delFile,boolean autoClose) throws Exception {
		boolean flag = false;
		if (this.connect(this.ftpUrl, Integer.parseInt(ftpPort), username,pwd)) {
			System.out.println(ftpClient.printWorkingDirectory());
			try {
//				if (File.separator.equals(ftpClient.printWorkingDirectory()))
				for (String s : delFile) {
					boolean delFlg = ftpClient.deleteFile(s);
//					if (ftpClient.completePendingCommand()) {
						MessageFormat format = new MessageFormat("delete file {0} ,is success :{1}");
						log.info(format.format(new String[]{s,String.valueOf(delFlg)}));
						//continue;
//					}
				}
				flag = true;
			} catch (Exception e) {
				throw new RuntimeException("Couldn't delete file from server.",e);
			} finally {
				if(autoClose) {
					disconnect();
				}
			}
		} else{
			log.error("can not connet to server");
		}
		return flag;
	}

	/**
	 * 上传文件
	 * @param remoteFile
	 * @param localFile
	 * @param ftpClient
	 * @param remoteSize
	 * @return
	 * @throws RuntimeException 
	 */
	public UploadStatus up(String remoteFile, File localFile,
			FTPClient ftpClient, long remoteSize) throws RuntimeException {
		UploadStatus status = null;
		try {
			RandomAccessFile raf = new RandomAccessFile(localFile,"r");
			OutputStream out = ftpClient.appendFileStream(new String(remoteFile
					.getBytes("ISO-8859-1"), "GBK"));

			// 断点续传
			if (remoteSize > 0) {
				ftpClient.setRestartOffset(remoteSize);
				raf.seek(remoteSize);
			}
			byte[] bytes = new byte[1024];
			int c;
			while ((c = raf.read(bytes)) != -1) {
				out.write(bytes, 0, c);
			}
			out.flush();
			raf.close();
			out.close();
			boolean result = ftpClient.completePendingCommand();
			if (remoteSize > 0) {
				status = result ? UploadStatus.Upload_From_Break_Success
						: UploadStatus.Upload_From_Break_Failed;
			} else {
				status = result ? UploadStatus.Upload_New_File_Success
						: UploadStatus.Upload_New_File_Failed;
			}
		} catch (Exception e) {
			throw new RuntimeException("upload file error", e);
		}
		return status;
	}
	
	public void test() {
		try {//先做一个连接
			System.out.println(this.connect(ftpUrl, new java.lang.Integer(ftpPort), username, pwd));
			//采用这种方法，可以新建路径和文件夹，但是对于中文的文件上传仍旧不行
//			for (int i=0;i<3;i++) {
//				this.remote = System.currentTimeMillis()+".txt";
//				this.upload(remote,local);
//			}
//			this.moveFile("/innerqiu", "testupload.txt", "/outerqiu", "aa.txt");
//			this.mkdir("aaa",getWorkingDirectory(),true);
			this.delete(new String[]{"/innerqiu/复件 (10) test.txt","/innerqiu/复件 (10) 复件 test.txt","/innerqiu/复件 (100) test.txt"},true);
		} catch (Exception e) {
			log.error("file upload exception");
			throw new RuntimeException("file upload exception", e);
		}
	}
}