/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.core.config.service;

import java.util.List;

import com.fable.hamal.shuttle.common.model.config.TransEntity;

/**
 * 
 * @author xieruidong 2013年11月21日 下午5:51:20
 */
public interface TransEntityService {
	
	public List<TransEntity> getAllTransEntities();
	public List<TransEntity> getTransEntitiesByTaskId(Long taskId);
}
