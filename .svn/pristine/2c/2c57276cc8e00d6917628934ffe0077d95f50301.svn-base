package com.fable.hamal.ftp.dto;

import com.fable.hamal.ftp.manager.Server;

/**
 * @author xieruidong
 */
public class FtpMapping {
	
	private Long id;
	private String userFlag;
	private String innerUsername;
	private String outerUsername;
	private String innerAddress;
	private String outerAddress;
	
	public String getSlaveUser() {
		return Server.inner ? outerUsername : innerUsername;
	}
	
	public String getMasterAddress() {
		return Server.inner ? innerAddress : outerAddress;
	}
	
	public String getSlaveAddress() {
		return Server.inner ? outerAddress : innerAddress;
	}
	
	//--------------------------------------setter&&getter-----------------------------------------
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUserFlag() {
		return userFlag;
	}
	
	public void setUserFlag(String userFlag) {
		this.userFlag = userFlag;
	}
	
	public String getInnerUsername() {
		return innerUsername;
	}

	public void setInnerUsername(String innerUsername) {
		this.innerUsername = innerUsername;
	}

	public String getOuterUsername() {
		return outerUsername;
	}

	public void setOuterUsername(String outerUsername) {
		this.outerUsername = outerUsername;
	}

	public String getInnerAddress() {
		return innerAddress;
	}
	
	public void setInnerAddress(String innerAddress) {
		this.innerAddress = innerAddress;
	}
	
	public String getOuterAddress() {
		return outerAddress;
	}
	
	public void setOuterAddress(String outerAddress) {
		this.outerAddress = outerAddress;
	}
}
