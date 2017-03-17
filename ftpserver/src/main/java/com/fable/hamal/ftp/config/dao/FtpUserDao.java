package com.fable.hamal.ftp.config.dao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.fable.hamal.ftp.dto.FtpMapping;
/**
 * 查询用户密码，外交换使用明文登录
 * @modify ruidong.xie
 * @author qius
 *
 */
public class FtpUserDao extends JdbcDaoSupport {
	
	public List<BaseUser> getAllUser() {
		String sql = "SELECT userid,userpassword,homedirectory FROM ftp_user";
		return getJdbcTemplate().query(sql,new RowMapper<BaseUser>() {

			@Override
			public BaseUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				BaseUser user = new BaseUser();
				user.setName(rs.getString("userid"));
				user.setPassword(rs.getString("userpassword"));
				user.setHomeDirectory(rs.getString("homedirectory"));
				return user;
			}
		});
	}
	/**
	 * 根据用户名查询密码
	 * @param userName 用户名
	 * @return
	 */
	public String getPwdByUserName(String userName) {
		String sql = "SELECT userpassword FROM ftp_user WHERE userid=?";
		Object[]obj = new Object[]{userName};
		return super.getJdbcTemplate().queryForList(sql,obj,String.class).toString();
	}
	/**
	 * 根据用户名查询当前工作目录
	 * @param name
	 * @return
	 */
	public String getPathByUser(String name) {
		String sql = "SELECT homedirectory FROM ftp_user WHERE userid=?";
		Object[]obj = new Object[]{name};
		return super.getJdbcTemplate().queryForList(sql,obj,String.class).toString();
	}
}
