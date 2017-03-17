package com.fable.hamal.shuttle.common.model.config;
import java.io.Serializable;
/**
 * FTP 用户管理DTO
 * @author 邱爽
 *
 */
public class FtpUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6017171567545524243L;

	private String ftpUsername;
	private String ftpPassword;
	private String webFlag;
	private String homeDirectory;
	
	
	public String getFtpUsername() {
		return ftpUsername;
	}
	
	public void setFtpUsername(String ftpUsername) {
		this.ftpUsername = ftpUsername;
	}
	
	public String getFtpPassword() {
		return ftpPassword;
	}
	
	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}
	
	public String getHomeDirectory() {
		return homeDirectory;
	}
	
	public void setHomeDirectory(String homeDirectory) {
		this.homeDirectory = homeDirectory; 
	}
    
    public String getWebFlag() {
        return webFlag;
    }
    
    public void setWebFlag(String webFlag) {
        this.webFlag = webFlag;
    }
	
	
}
