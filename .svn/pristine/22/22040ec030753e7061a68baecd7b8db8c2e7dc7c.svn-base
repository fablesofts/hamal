package com.fable.hamal.ftp.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.fable.hamal.ftp.config.ConfigurationConstants;
import com.fable.hamal.ftp.dto.FtpMapping;
import com.fable.hamal.ftp.util.HamalPropertyConfigurer;

/**
 * @author xieruidong
 */
public class FtpMappingDao extends JdbcDaoSupport {
	
	public Object getPwdByUserName(String userName) {
		String sql = "SELECT userpassword FROM  ftp_user WHERE userid=?";
		Object[]obj = new Object[]{userName};
		return super.getJdbcTemplate().queryForList(sql,obj,String.class);

	}

	public List<FtpMapping> getAllMappings() {
		String sql = "SELECT * FROM DSP_FTP_MAPPING WHERE USER_FLAG = ?";
		Object[]obj = new Object[]{HamalPropertyConfigurer.getHamalProperty(ConfigurationConstants.FTP_SERVER_FLAG)};
		return getJdbcTemplate().query(sql,new RowMapper<FtpMapping>() {
			@Override
			public FtpMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
				FtpMapping ftpMapping = new FtpMapping();
				ftpMapping.setId(rs.getLong("ID"));
				ftpMapping.setInnerAddress(rs.getString("INNER_ADDRESS"));
				ftpMapping.setOuterAddress(rs.getString("OUTER_ADDRESS"));
				ftpMapping.setInnerUsername(rs.getString("INNER_USERNAME"));
				ftpMapping.setOuterUsername(rs.getString("OUTER_USERNAME"));
				ftpMapping.setUserFlag(rs.getString("USER_FLAG"));
				return ftpMapping;
			}
		},obj);
	}
	
//	public String getInnerAddressByUser(String userName) {
//		FtpMapping ftpMapping;
//		String ftpMappingAddress =null;
//		if(Constants.innerToOuter.equals(flag)) {
//			//由内向外
//			 ftpMapping = FtpMappingLoader.innerFtpMap.get(userName);
//			 ftpMappingAddress = ftpMapping.getOuterAddress();
//		}else if(Constants.outerToInner.equals(flag)) {
//			//由外到内
//			 ftpMapping = FtpMappingLoader.outerFtpMap.get(userName);
//			 ftpMappingAddress= ftpMapping.getInnerAddress();
//		}
//		return ftpMappingAddress;
//	}
	/**
	 * 查询是否存在映射关系
	 * @param userName
	 * @return
	 */
	public boolean existsMapping (String userName) {
		boolean flag = false;
		String sql = "SELECT COUNT(*) FROM dsp_ftp_mapping WHERE INNER_USERNAME =? OR OUTER_USERNAME =?";
		int count =  super.getJdbcTemplate().queryForObject(sql,Integer.class,new Object[]{userName,userName});
		if (count>0) {
			flag = true;
		}else{
			flag = false;
		}
		return flag;
	}
//	
//	/**
//	 * 查询映射目录
//	 * @param userName
//	 * @param flag
//	 * @return
//	 */
//	public String getAddressByUser(String userName) {
//		String correspondingFlag ="1".equals(this.flag) ? "0":"1";
//		String sql = "";
//		if("0".equals(correspondingFlag)) {
//			sql = "SELECT OUTER_ADDRESS FROM DSP_FTP_MAPPING WHERE OUTER_USERNAME= ? AND USER_FLAG=?";
//		}else if("1".equals(correspondingFlag)) {
//			sql = "SELECT INNER_ADDRESS FROM DSP_FTP_MAPPING WHERE INNER_USERNAME = ? AND USER_FLAG=?";
//		}
//		Object[]obj = new Object[]{userName,this.flag};
//		return super.getJdbcTemplate().queryForList(sql,obj,String.class).toString();
//	}
//
//	/**
//	 * 初始化对应登录用户
//	 * @param userName
//	 * @return
//	 */
//	public FtpHelper initClientInfo(String userName) {
//		FtpMapping ftpMapping;
//		String correspondingUserName = null;	//对应的用户名
//		FtpHelper continueFtp = new FtpHelper();
//		if(Constants.innerToOuter.equals(flag)) {
//			//由内向外
//			 ftpMapping = FtpMappingLoader.innerFtpMap.get(userName);
//			 //得到外交换用户名
//			 correspondingUserName = ftpMapping.getOuterUserName();
//		}else if(Constants.outerToInner.equals(flag)) {
//			 ftpMapping = FtpMappingLoader.outerFtpMap.get(userName);
//			 correspondingUserName = ftpMapping.getInnerUserName();
//		}
//		FtpUser ftpUser = FtpUserManager.getFtpUserByName(correspondingUserName);
//		continueFtp.setPort(Integer.valueOf(port));
//		continueFtp.setHostname(ftpUrl);
//		continueFtp.setHomepath(ftpUser.getHomeDirectory());
//		continueFtp.setUsername(correspondingUserName);
//		return continueFtp;
//	}
//	/**
//	 * 初始化当前登录用户客户端
//	 * @param userName 当前登录用户
//	 * @return
//	 */
//	 public FtpHelper initCurrentClient(String userName) {
//		 FtpHelper continueFtp = new FtpHelper();
//		 continueFtp.setPort(Integer.valueOf(currentPort));
//		 continueFtp.setHostname(currentFtpUrl);
//		 continueFtp.setUsername(userName);
//		 continueFtp.setHomepath("/");
//		 return continueFtp;
//	 }
}
