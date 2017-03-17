package com.fable.hamal.ftp.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.fable.hamal.ftp.dto.FtpFile;
/**
* 
* 文件操作工具类
* 
* @author houly
* 
*/
public class FileUtil {
	// 日志
	private static Logger logger = Logger.getLogger(FileUtil.class);

//	public static List<FtpFile> traversalDir(File dir, List<FtpFile> list) {
//	//	logger.info("开始进入FileUtil中的traversalDir方法......");
//		// 判断参数
//		if (dir == null) {
//			logger.error("参数为空.....");
//			return null;
//		}
//		// 判断复制目录是否是目录
//		if (!dir.isDirectory()) {
//			logger.error("开始目录设置错误....");
//			return null;
//		}
//		// 列出该目录下的所有文件
//		File[] files = dir.listFiles();
//		if (list == null)
//			list = new ArrayList<FtpFile>();
//		for (int i = 0; i < files.length; i++) {
//			File file = files[i];
//			// 递归调用......
//			if (file.isDirectory()) {
//				traversalDir(file,list);
//			} else {
//				list.add(change(file,new File(ReadProperties.readProperties("rrs.properties", "ftpimagepath"))));
//			}
//		}
//		
//		
//		
//	//	logger.info("开始进入FileUtil中的traversalDir方法结束......");
//		return list;
//	}

	public static FtpFile change(File file,File dir){

		if(file.isDirectory()){
			logger.error("目标文件是目录");
			return null;
		}
		
		if(dir==null ||  dir.isFile()){
			logger.error("目录参数出错.....");
			return null;
		}
		
		String path = dir.getAbsolutePath();
		
		String filePath = file.getAbsolutePath();
		
		int index_begin = filePath.indexOf(path);
		
		if(index_begin == -1 ){
			return null;
		}
		index_begin = path.length()-1;
		FtpFile ftpFile = new FtpFile();
		ftpFile.setFileName(file.getName());
		while(true){
			
			int index_end = filePath.indexOf("\\", index_begin+1);
			if(index_end == index_begin+1 ){
				ftpFile.getList().add("/");
			}
			else if(index_end == -1){
				break;
			}
			else{
				ftpFile.getList().add(filePath.substring(index_begin+1, index_end));
			}
			index_begin = index_end;
			
		}
//		logger.info("内容");
//		logger.info(ftpFile.getList());
		return ftpFile;
	}
}