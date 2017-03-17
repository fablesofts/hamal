package com.fable.hamal.shuttle.common.model.config;

import java.io.Serializable;



/**
 * FTP路径映射表
 * DTO
 * @author WUH.
 * 
 */

public class FtpMapping implements Serializable  {

	private static final long serialVersionUID = 5038646986481494413L;

	private Long id;

	
	private Character userFlag;
	/**
	 * 内交换用户名
	 */
	private String innerUserName;
	/**
	 * 外交换用户名
	 */
	private String outerUserName;
	/**
	 * 内交换路径
	 */
	private String innerAddress;
	/**
	 * 外交换路径
	 */
	private String outerAddress;

	public Character getUserFlag() {
		return userFlag;
	}

	public void setUserFlag(Character userFlag) {
		this.userFlag = userFlag;
	}

	public String getInnerUserName() {
		return innerUserName;
	}

	public void setInnerUserName(String innerUserName) {
		this.innerUserName = innerUserName;
	}

	public String getOuterUserName() {
		return outerUserName;
	}

	public void setOuterUserName(String outerUserName) {
		this.outerUserName = outerUserName;
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

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

	
	
}
