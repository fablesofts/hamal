/*
 * Copyright (C) 2013-2033 Fable Limited.
 */
package com.fable.hamal.manager.core.config.service;

import java.util.List;

import com.fable.hamal.shuttle.common.model.config.ScheduleConfig;

/**
 * 
 * @author xieruidong 2013年11月21日 下午5:53:02
 */
public interface ScheduleConfigService {

	public List<ScheduleConfig> getAllScheduleConfigs();
	public List<ScheduleConfig> getScheduleConfigByTaskId(Long taskId);
}
