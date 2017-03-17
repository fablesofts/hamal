/*
 * Copyright (C) 2013-2033 Fable Limited.
 */

package com.fable.hamal.shuttle.common.model.config;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {

	private static final long serialVersionUID = -8420076139179121501L;
	
	private Long id;

	private String name;
	
	private int needResource;
	
	private int createUser;
	
	private Date createTime;
	
	private Date updateTime;
	
	private boolean delFlag;
	
	private String description;
	/**
	 * 是否包含关联关系 (0:否,1：是)
	 */
	private String association;
	
	/**
	 * 同步类型，(0:全量 ；1:增量,2:日志)
	 */
	private String synchroType;
	
    /**
     * 0:不删除源数据
     * 1：删除源数据
     */
	private String deleteSourcedata;

    /**
     * 0:不重建触发器
     * 1：重建触发器
     */
	private String rebuildTrigger;
	
	//--------------------------getter/setter-------------------------------------------------
	
    public Long getId() {
		return id;
	}

	
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	
	public int getCreateUser() {
		return createUser;
	}

	
	public void setCreateUser(int createUser) {
		this.createUser = createUser;
	}

	
	public Date getCreateTime() {
		return createTime;
	}

	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	
	public Date getUpdateTime() {
		return updateTime;
	}

	
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	
	public boolean isDelFlag() {
		return delFlag;
	}

	
	public void setDelFlag(boolean delFlag) {
		this.delFlag = delFlag;
	}

	
	public String getDescription() {
		return description;
	}

	
	public void setDescription(String description) {
		this.description = description;
	}

	
    
    public int getNeedResource() {
        return needResource;
    }


    
    public void setNeedResource(int needResource) {
        this.needResource = needResource;
    }


    
    public String getSynchroType() {
        return synchroType;
    }


    
    public void setSynchroType(String synchroType) {
        this.synchroType = synchroType;
    }


    
    public String getDeleteSourcedata() {
        return deleteSourcedata;
    }


    
    public void setDeleteSourcedata(String deleteSourcedata) {
        this.deleteSourcedata = deleteSourcedata;
    }


    
    public String getRebuildTrigger() {
        return rebuildTrigger;
    }


    
    public void setRebuildTrigger(String rebuildTrigger) {
        this.rebuildTrigger = rebuildTrigger;
    }


    public String getAssociation() {
        return association;
    }


    
    public void setAssociation(String association) {
        this.association = association;
    }



}
