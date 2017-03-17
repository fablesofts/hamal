//package com.fable.hamal.ftp.manager;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.fable.hamal.ftp.config.dao.FtpMappingDao;
//import com.fable.hamal.ftp.constant.Constants;
//import com.fable.hamal.ftp.dto.FtpMapping;
//import com.fable.hamal.ftp.util.HamalPropertyConfigurer;
///**
// * 查询FTP映射信息加载类
// * @author 邱爽
// *
// */
//public class FtpMappingLoader {
//	private final static Logger logger = LoggerFactory.getLogger(FtpMappingLoader.class);
//	
//	/**
//	 * 内交换映射信息
//	 */
//	public static final Map<String,FtpMapping> innerFtpMap = new HashMap<String, FtpMapping>();
//	
//	/**
//	 * 外交换映射信息
//	 */
//	public static final Map<String,FtpMapping> outerFtpMap = new HashMap<String, FtpMapping>();
//	
//	/**
//	 * 数据库连接DAO
//	 */
//	private static FtpMappingDao ftpMappingDao;
//	
//	private static final String FTPSERVERFLAG = "ftp.server.flag";
//	
//	public FtpMappingLoader() {
//		
//	}
//	/**
//	 * 查询所有映射信息
//	 */
//	public static void listAllMappings(String flag) {
//		//根据是内，外交换的标识来确定
//		List<FtpMapping> mappings= ftpMappingDao.findMappingByFlag(flag);
//		for (int i =0; i<mappings.size(); i++){
//			FtpMapping ftpMapping = mappings.get(i);	//获取每一个映射对象
//			if(Constants.innerToOuter.equals(flag)){
//				//由内交换到外
//				innerFtpMap.put(ftpMapping.getInnerUserName(),ftpMapping);
//			}else if(Constants.outerToInner.equals(flag)){
//				//由外交换到内
//				outerFtpMap.put(ftpMapping.getOuterUserName(),ftpMapping);
//			}
//		}
//		
//	}
//	/**
//	 * 读取当前服务是否是内、外交换的标识
//	 * @return 1.由内交换到外 0 由外交换到内
//	 */
//	public String getUserFlag() {
//		return HamalPropertyConfigurer.getHamalProperty(FTPSERVERFLAG);
//	}
//	public static void main(String[] args) {
//		FtpMappingLoader ftpMappingLoader = new FtpMappingLoader();
//		System.out.println(ftpMappingLoader.getUserFlag());
//	}
//	public static FtpMappingDao getFtpMappingDao() {
//		return ftpMappingDao;
//	}
//
//
//	public static void setFtpMappingDao(FtpMappingDao ftpMappingDao) {
//		FtpMappingLoader.ftpMappingDao = ftpMappingDao;
//	}
//
//
//
//}
